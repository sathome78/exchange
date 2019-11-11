package me.exrates.dao;

import me.exrates.model.dto.SyndexOrderDto;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SyndexDao {
    void saveOrder(SyndexOrderDto orderDto);

    void updateStatus(int refillRequestId, int newStatus);

    void updatePaymentDetailsAndEndDate(int refillRequestId, String details, LocalDateTime endPaymentTime);

    void updateSyndexOrder(int refillRequestId,
                           long syndexId,
                           String details,
                           LocalDateTime endPaymentTime,
                           int newStatus,
                           BigDecimal amountToRefill);

    void setConfirmed(int refillRequestId);

    List<SyndexOrderDto> getAllorders(@Nullable List<Integer> statuses, @Nullable Integer userId);

    SyndexOrderDto getById(int id, Integer userId);

    SyndexOrderDto getByIdForUpdate(int id, @Nullable Integer userId);

    SyndexOrderDto getBySyndexIdForUpdate(long id);

    void openDispute(int id, String text, int statusId);

    void updateAmountToRefill(int id, BigDecimal amountToRefill);
}
