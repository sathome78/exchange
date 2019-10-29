package me.exrates.service.freecoins;

import me.exrates.model.dto.freecoins.AdminGiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.ReceiveResultDto;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

public interface FreecoinsService {

    void updateGiveawayStatuses();

    GiveawayResultDto processGiveaway(String currencyName, BigDecimal amount, BigDecimal partialAmount, boolean isSingle,
                                      Integer timeRange, String creatorEmail);

    boolean processRevokeGiveaway(int giveawayId, boolean revokeToUser);

    List<GiveawayResultDto> getAllGiveaways();

    Pair<Boolean, ReceiveResultDto> processReceive(int giveawayId, String receiverEmail);

    List<ReceiveResultDto> getAllReceives(String receiverEmail);

    List<AdminGiveawayResultDto> getAllGiveawaysForAdmin();
}
