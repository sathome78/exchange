package me.exrates.service.kyc;

import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.KYCSettingsDao;
import me.exrates.dao.UserVerificationInfoDao;
import me.exrates.model.Email;
import me.exrates.model.User;
import me.exrates.model.UserVerificationInfo;
import me.exrates.model.constants.Constants;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.DocTypeEnum;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataKyc;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.PersonKycDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.KycResponseStatusDto;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.model.exceptions.KycException;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.service.KYCService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.kyc.http.KycHttpClient;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.util.DateUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;


@PropertySource(value = {"classpath:/kyc.properties", "classpath:/angular.properties"})
@Slf4j
@Service
public class AriadNextKycServiceImpl implements KYCService {

    private final UserService userService;
    private final SendMailService sendMailService;
    private final KycHttpClient kycHttpClient;
    private final UserVerificationInfoDao userVerificationInfoDao;
    private final KYCSettingsDao kycSettingsDao;
    private final StompMessenger stompMessenger;
    private final String emailSubject;
    private final String emailMessagePattern;

    @Value("${server-host}")
    private String host;

    @Autowired
    public AriadNextKycServiceImpl(UserService userService,
                                   SendMailService sendMailService,
                                   KycHttpClient kycHttpClient,
                                   UserVerificationInfoDao userVerificationInfoDao,
                                   KYCSettingsDao kycSettingsDao,
                                   StompMessenger stompMessenger,
                                   @Value("${shufti-pro.email.subject}") String emailSubject,
                                   @Value("${shufti-pro.email.message-pattern}") String emailMessagePattern) {
        this.userService = userService;
        this.sendMailService = sendMailService;
        this.kycHttpClient = kycHttpClient;
        this.userVerificationInfoDao = userVerificationInfoDao;
        this.kycSettingsDao = kycSettingsDao;
        this.stompMessenger = stompMessenger;
        this.emailSubject = emailSubject;
        this.emailMessagePattern = emailMessagePattern;
    }


    @Override
    public String getVerificationUrl(int stepNumber, String languageCode, String countryCode) {
        return null;
    }

    @Override
    public Pair<String, EventStatus> getVerificationStatus() {
        return null;
    }

    @Override
    public String getKycStatus(String email) {
        String status = userService.getUserKycStatusByEmail(email);
        if (status.equalsIgnoreCase("Pending")) {
            String referenceUid = userService.getKycReferenceByEmail(email);
            if (referenceUid == null) {
                log.error("Reference uid is null, cannot get status, email {}", email);
                throw new NgDashboardException("Reference uid is null", Constants.ErrorApi.QUBERA_KYC_ERROR_GET_STATUS);
            }
            KycResponseStatusDto response =
                    kycHttpClient.getCurrentKycStatus(referenceUid);
            status = response.getStatus();
        }
        return status;
    }

    @Override
    public Pair<String, EventStatus> checkResponseAndUpdateVerificationStep(String response, String s) {
        return null;
    }

    @Override
    public OnboardingResponseDto startKyCProcessing(IdentityDataRequest identityDataRequest, String email) {
        User user = userService.findByEmail(email);
        if (user.getKycStatus().equalsIgnoreCase("success")) {
            throw new KycException("Already passed KYC");
        }
        Date dateOfBirth = DateUtils.getDateFromStringForKyc(identityDataRequest.getBirthYear(), identityDataRequest.getBirthMonth(),
                identityDataRequest.getBirthDay());
        //start create applicant
        String uuid = UUID.randomUUID().toString();
        KycCountryDto countryDto = kycSettingsDao.getCountryByCode(identityDataRequest.getCountry());
        userService.updatePrivateDataAndKycReference(email, uuid, countryDto.getCountryCode(), identityDataRequest.getFirstNames()[0],
                identityDataRequest.getLastName(), dateOfBirth);
        PersonKycDto personKycDto = new PersonKycDto(Collections.singletonList(IdentityDataKyc.of(identityDataRequest)));
        CreateApplicantDto createApplicantDto = new CreateApplicantDto(uuid, personKycDto);

        ResponseCreateApplicantDto response = kycHttpClient.createApplicant(createApplicantDto);

        if (!response.getState().equalsIgnoreCase("INITIAL")) {
            throw new KycException("Error while start processing KYC, state " + response.getState()
                    + " uid " + response.getUid() + " lastReportStatus " + response.getLastReportStatus());
        }
        String docId = RandomStringUtils.random(18, true, false);

        String callBackUrl = String.format("%s/api/public/v2/kyc/webhook/%s", host, uuid);

        RequestOnBoardingDto onBoardingDto = RequestOnBoardingDto.createOfParams(callBackUrl, email, uuid, docId);
        userVerificationInfoDao.saveUserVerificationDoc(new UserVerificationInfo(user.getId(), DocTypeEnum.P, docId));
        log.info("Sending to create applicant {}", onBoardingDto);
        OnboardingResponseDto onBoarding = kycHttpClient.createOnBoarding(onBoardingDto);
        userService.updateKycStatusByEmail(user.getEmail(), "Pending");
        return onBoarding;
    }

    @Override
    public boolean updateUserVerificationInfo(User user, KycStatusResponseDto kycStatusResponseDto) {
        return userService.updateKycStatusByEmail(user.getEmail(), kycStatusResponseDto.getStatus());
    }

    @Override
    public void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto) {
        User user = userService.findByKycReferenceId(referenceId);
        updateUserVerificationInfo(user, kycStatusResponseDto);
        sendPersonalMessage(kycStatusResponseDto, user);
        sendStatusNotification(user.getEmail(), kycStatusResponseDto.getStatus());
    }

    @SuppressWarnings("Duplicates")
    private void sendStatusNotification(String userEmail, String eventStatus) {
        log.info("SEND TO EMAIL {}, STATUS {}", userEmail, eventStatus);
        Email email = Email.builder()
                .to(userEmail)
                .subject(emailSubject)
                .message(String.format(emailMessagePattern, eventStatus))
                .build();

        sendMailService.sendMailMandrill(email);

        UserNotificationType type;
        String msg = String.format(emailMessagePattern, eventStatus);
        if (eventStatus.equalsIgnoreCase("SUCCESS")
                || eventStatus.equalsIgnoreCase(EventStatus.ACCEPTED.name())) {
            type = UserNotificationType.SUCCESS;
        } else {
            type = UserNotificationType.ERROR;
        }

        final UserNotificationMessage message = new UserNotificationMessage(WsSourceTypeEnum.KYC, type, msg);

        stompMessenger.sendPersonalMessageToUser(userEmail, message);
    }

    private void sendPersonalMessage(KycStatusResponseDto kycStatusResponseDto, User user) {
        UserNotificationMessage message = UserNotificationMessage.builder()
                .notificationType(UserNotificationType.SUCCESS)
                .sourceTypeEnum(WsSourceTypeEnum.KYC)
                .text("Dear user, your current verification status is SUCCESS")
                .build();
        if (StringUtils.isNotEmpty(kycStatusResponseDto.getErrorMsg())) {
            message.setNotificationType(UserNotificationType.WARNING);
            String text = "Dear user, your verification seems to fail as " + kycStatusResponseDto.getErrorMsg();
            message.setText(text);
        }
        stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
    }
}
