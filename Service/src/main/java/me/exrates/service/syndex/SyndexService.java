package me.exrates.service.syndex;

import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface SyndexService extends IRefillable, IWithdrawable {


    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return true;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return true;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return true;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return true;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    @Override
    default Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not supported");
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default boolean isValidDestinationAddress(String address) {
        return false;
    }

    List<SyndexOrderDto> getAllPendingPayments(List<Integer> statuses, Integer userId);

    SyndexOrderDto getOrderInfo(int orderId, String email);

    void cancelOrder(int id, String email);

    void openDispute(SyndexClient.DisputeData data, String email);

    void onCallbackEvent(SyndexClient.OrderInfo order);

    void confirmOrder(Integer id, String email);

    void checkOrder(long syndexOrderId);
}
