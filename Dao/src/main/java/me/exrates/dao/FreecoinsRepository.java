package me.exrates.dao;

import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayStatus;
import me.exrates.model.dto.freecoins.ReceiveResultDto;

import java.util.List;

public interface FreecoinsRepository {

    int saveClaim(GiveawayResultDto giveawayResultDto);

    GiveawayResultDto getClaim(int giveawayId);

    List<GiveawayResultDto> getAllCreatedClaims();

    boolean updateTotalQuantity(int giveawayId);

    boolean updateStatus(int giveawayId, GiveawayStatus status);

    int saveProcess(ReceiveResultDto receiveResultDto);

    boolean updateProcess(ReceiveResultDto receiveResultDto);

    ReceiveResultDto getProcess(int giveawayId, String receiverEmail);

    List<ReceiveResultDto> getAllUserProcess(String receiverEmail);

    boolean updateStatuses();

    List<GiveawayResultDto> getAllClaims();

    int getUniqueAcceptorsByClaimId(int giveawayId);
}