package me.exrates.service.impl;

import me.exrates.dao.KycDao;
import me.exrates.model.Email;
import me.exrates.model.kyc.KYC;
import me.exrates.model.kyc.KycStatus;
import me.exrates.model.kyc.KycType;
import me.exrates.service.KycService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
@PropertySource({
        "classpath:/uploadfiles.properties",
        "classpath:/kyc.properties"
})
public class KycServiceImpl implements KycService {

    private static final Logger LOG = LogManager.getLogger(KycService.class);
    private static final Locale ADMIN_LOCALE = new Locale("en");

    private @Value("${upload.kycFilesDir}") String kycFilesDir;
    private @Value("${kyc.admin.email}") String kycAdminEmail;

    @Autowired
    private KycDao kycDao;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;

    @Override
    public void save(int userId, KYC kyc) {
        kyc.setKycStatus(KycStatus.IN_PROGRESS);
        try {
            kyc.getPerson().setConfirmDocumentPath(saveFile(kycFilesDir, kyc.getPerson().getConfirmDocument()));

            switch (kyc.getKycType()) {
                case INDIVIDUAL:
                    kycDao.saveIndividual(userId, kyc);
                    break;

                case LEGAL_ENTITY:
                    kyc.setCommercialRegistryPath(saveFile(kycFilesDir, kyc.getCommercialRegistry()));
                    kyc.setCompanyCharterPath(saveFile(kycFilesDir, kyc.getCompanyCharter()));
                    kycDao.saveLegalEntity(userId, kyc);
                    break;
            }
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException("Error while saving user file.");
        }
    }

    @Override
    public void update(int userId, KYC kyc) {
        KycStatus kycStatus = kycDao.getKycStatus(userId);
        if (!kycStatus.equals(KycStatus.IN_PROGRESS)) {
            throw new RuntimeException(String.format("Invalid KYC status. Unable to send. KYC status=%s; userID=%d", kycStatus, userId));
        }
        try {
            if (!kyc.getPerson().getConfirmDocument().isEmpty()) {
                kyc.getPerson().setConfirmDocumentPath(saveFile(kycFilesDir, kyc.getPerson().getConfirmDocument()));
            }
            switch (kyc.getKycType()) {
                case INDIVIDUAL:
                    kycDao.updateIndividual(userId, kyc);
                    break;

                case LEGAL_ENTITY:
                    if (!kyc.getCompanyCharter().isEmpty()) {
                        kyc.setCompanyCharterPath(saveFile(kycFilesDir, kyc.getCompanyCharter()));
                    }
                    if (!kyc.getCommercialRegistry().isEmpty()) {
                        kyc.setCommercialRegistryPath(saveFile(kycFilesDir, kyc.getCommercialRegistry()));
                    }
                    kycDao.updateLegalEntity(userId, kyc);
                    break;
            }
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException("Error while saving user file.");
        }
    }

    @Override
    public void updateByAdmin(int userId, KYC kyc) {
        switch (kyc.getKycType()) {
            case INDIVIDUAL:
                kycDao.updateIndividual(userId, kyc);
                break;

            case LEGAL_ENTITY:
                kycDao.updateLegalEntity(userId, kyc);
                break;
        }
    }

    @Override
    @Transactional
    public void sendKycForApprove(int userId) {
        KycStatus kycStatus = kycDao.getKycStatus(userId);
        if (!kycStatus.equals(KycStatus.IN_PROGRESS)) {
            throw new RuntimeException(String.format("Invalid KYC status. Unable to send. KYC status=%s; userID=%d", kycStatus, userId));
        }

        kycDao.setStatus(userId, KycStatus.NEED_CHECK, null);

        String link = "/2a8fy7b07dxe44/kyc/getKyc?userId=" + userId;
        String rootUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        Email email = new Email();
        email.setMessage(
                messageSource.getMessage("kyc.UserSendInfoForApprove", null, ADMIN_LOCALE) +
                        " <a href='" + rootUrl + link + "'>"
                        + messageSource.getMessage("admin.ref", null, ADMIN_LOCALE) + "</a>"
        );
        email.setSubject(messageSource.getMessage("kyc.UserSendInfoForApproveTitle", null, ADMIN_LOCALE));
        email.setTo(kycAdminEmail);
        sendMailService.sendMail(email);
    }

    @Override
    @Transactional
    public void approveKyc(int userId, String admin) {
        kycDao.setStatus(userId, KycStatus.APPROVED, admin);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        Email email = new Email();
        email.setMessage(messageSource.getMessage("kyc.ApproveSuccess", null, locale));
        email.setSubject(messageSource.getMessage("kyc.ApproveTitle", null, locale));
        email.setTo(userService.getEmailById(userId));
        sendMailService.sendMail(email);
    }

    @Override
    @Transactional
    public void rejectKyc(int userId, String admin) {
        kycDao.setStatus(userId, KycStatus.REJECTED, admin);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        Email email = new Email();
        email.setMessage(messageSource.getMessage("kyc.ApproveFail", null, locale));
        email.setSubject(messageSource.getMessage("kyc.ApproveTitle", null, locale));
        email.setTo(userService.getEmailById(userId));
        sendMailService.sendMail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public KYC getKyc(int userId) {
        return kycDao.getDetailed(userId);
    }

    private String saveFile(String directory, MultipartFile file) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        String name = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        Path target = Paths.get(path.toString(), name);
        Files.write(target, file.getBytes());
        return name;
    }

    private String getFileExtension(String fullName) {
        if(fullName.lastIndexOf(".") != -1 && fullName.lastIndexOf(".") != 0) {
            return fullName.substring(fullName.lastIndexOf(".") + 1 );
        } else {
            return "";
        }
    }
}
