package me.exrates.service.freecoins;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.FreecoinsRepository;
import me.exrates.model.dto.freecoins.AdminGiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayStatus;
import me.exrates.model.dto.freecoins.ReceiveResultDto;
import me.exrates.service.WalletService;
import me.exrates.service.exception.FreecoinsException;
import me.exrates.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@EnableScheduling
@Transactional
@Service
public class FreecoinsServiceImpl implements FreecoinsService {

    private final FreecoinsRepository freecoinsRepository;
    private final WalletService walletService;

    @Autowired
    public FreecoinsServiceImpl(FreecoinsRepository freecoinsRepository,
                                WalletService walletService) {
        this.freecoinsRepository = freecoinsRepository;
        this.walletService = walletService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 5 * 60 * 1_000)
    @Override
    public void updateGiveawayStatuses() {
        freecoinsRepository.updateStatuses();

        log.info("Process of update claims statuses was finished");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GiveawayResultDto processGiveaway(String currencyName, BigDecimal amount, BigDecimal partialAmount,
                                             boolean isSingle, Integer timeRange, String creatorEmail) {
        GiveawayStatus status = GiveawayStatus.CREATED;
        if (!walletService.performFreecoinsGiveawayProcess(currencyName, amount, creatorEmail)) {
            status = GiveawayStatus.FAILED;
        }
        final GiveawayResultDto giveawayResultDto = GiveawayResultDto.builder()
                .currencyName(currencyName)
                .amount(amount)
                .partialAmount(partialAmount)
                .totalQuantity((int) (amount.doubleValue() / partialAmount.doubleValue()))
                .isSingle(isSingle)
                .timeRange(timeRange)
                .status(status)
                .creatorEmail(creatorEmail)
                .build();

        int id = freecoinsRepository.saveClaim(giveawayResultDto);
        if (id == 0) {
            throw new FreecoinsException("Free coins giveaway process have not been saved");
        }
        giveawayResultDto.setId(id);

        return giveawayResultDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean processRevokeGiveaway(int giveawayId, boolean revokeToUser) {
        GiveawayResultDto giveawayResultDto = freecoinsRepository.getClaim(giveawayId);
        if (giveawayResultDto.getTotalQuantity() == 0) {
            throw new FreecoinsException("Free coins have been ran out, revoke process have been stopped");
        }
        if (revokeToUser) {
            final BigDecimal revokeAmount = giveawayResultDto.getPartialAmount().multiply(BigDecimal.valueOf(giveawayResultDto.getTotalQuantity()));

            if (!walletService.performFreecoinsGiveawayRevokeProcess(
                    giveawayResultDto.getCurrencyName(),
                    revokeAmount,
                    giveawayResultDto.getCreatorEmail())) {
                throw new FreecoinsException("Free coins giveaway revoke process was failed");
            }
        }
        return freecoinsRepository.updateStatus(giveawayId, GiveawayStatus.REVOKED);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiveawayResultDto> getAllGiveaways() {
        return freecoinsRepository.getAllCreatedClaims();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReceiveResultDto processReceive(int giveawayId, String receiverEmail) {
        GiveawayResultDto giveawayResultDto = freecoinsRepository.getClaim(giveawayId);
        if (giveawayResultDto.getTotalQuantity() == 0) {
            throw new FreecoinsException("Free coins have been ran out");
        }
        ReceiveResultDto receiveResultDto = freecoinsRepository.getProcess(giveawayId, receiverEmail);
        if (Objects.nonNull(receiveResultDto) && receiveResultDto.isReceived()) {
            throw new FreecoinsException("Free coins receive process was failed: receiving for this giveaway not allowed for you any more");
        }

        if (!walletService.performFreecoinsReceiveProcess(
                giveawayResultDto.getCurrencyName(),
                giveawayResultDto.getPartialAmount(),
                receiverEmail)) {
            throw new FreecoinsException("Free coins receive process was failed");
        }

        final boolean received = giveawayResultDto.isSingle();
        final LocalDateTime lastReceived = giveawayResultDto.isSingle() ? null : LocalDateTime.now();

        if (Objects.isNull(receiveResultDto)) {
            receiveResultDto = ReceiveResultDto.builder()
                    .giveawayId(giveawayId)
                    .receiverEmail(receiverEmail)
                    .received(received)
                    .lastReceived(lastReceived)
                    .build();

            int id = freecoinsRepository.saveProcess(receiveResultDto);
            if (id == 0) {
                throw new FreecoinsException("Free coins receive process have not been saved");
            }
            receiveResultDto.setId(id);
        } else {
            receiveResultDto.setReceived(received);
            receiveResultDto.setLastReceived(lastReceived);

            boolean updated = freecoinsRepository.updateProcess(receiveResultDto);
            if (!updated) {
                throw new FreecoinsException("Free coins receive process have not been updated");
            }
        }
        boolean updated = freecoinsRepository.updateTotalQuantity(giveawayId);
        if (!updated) {
            throw new FreecoinsException("Free coins claim total quantity have not been updated");
        }
        return receiveResultDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReceiveResultDto> getAllReceives(String receiverEmail) {
        return freecoinsRepository.getAllUserProcess(receiverEmail);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AdminGiveawayResultDto> getAllGiveawaysForAdmin() {
        List<GiveawayResultDto> allClaims = freecoinsRepository.getAllClaims();

        if (CollectionUtil.isEmpty(allClaims)) {
            return Collections.emptyList();
        }
        return allClaims.stream()
                .map(claim -> {
                    AdminGiveawayResultDto adminClaim = new AdminGiveawayResultDto(claim);
                    adminClaim.setUniqueAcceptors(freecoinsRepository.getUniqueAcceptorsByClaimId(claim.getId()));

                    return adminClaim;
                })
                .sorted(Comparator.comparing(AdminGiveawayResultDto::getDate).reversed())
                .collect(Collectors.toList());
    }
}