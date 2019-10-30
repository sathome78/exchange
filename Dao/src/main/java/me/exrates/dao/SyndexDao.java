package me.exrates.dao;

import me.exrates.model.dto.SyndexOrderDto;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

public interface SyndexDao {
    void saveOrder(SyndexOrderDto orderDto);

    void updateStatus(int refillRequestId, int newStatus);

    void updatePaymentDetailsAndEndDate(int refillRequestId, String details, LocalDateTime endPaymentTime);

    void updateSyndexId(int refillRequestId, long sybexId);

    void setConfirmed(int refillRequestId);

    List<SyndexOrderDto> getAllorders(@Nullable List<Integer> statuses, @Nullable Integer userId);

    SyndexOrderDto getById(int id, Integer userId);

    SyndexOrderDto getBySyndexId(long id);

    SyndexOrderDto getByIdForUpdate(int id, int userId);

    SyndexOrderDto getBySyndexIdForUpdate(long id);

    void openDispute(int id, String text, int statusId);
}
