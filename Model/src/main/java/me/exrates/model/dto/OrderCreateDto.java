package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;

import java.math.BigDecimal;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.util.BigDecimalProcessing.normalize;

/**
 * Created by Valk on 13.04.16.
 */

@Getter@Setter
public class OrderCreateDto {
    /*this field filled from existing order*/
    private Long orderId;
    private int userId;
    private OrderStatus status;
    /*these fields will be transferred to blank creation form */
    private CurrencyPair currencyPair;
    private int comissionForBuyId;
    private BigDecimal comissionForBuyRate;
    private int comissionForSellId;
    private BigDecimal comissionForSellRate;
    private int walletIdCurrencyBase;
    private BigDecimal currencyBaseBalance;
    private int walletIdCurrencyConvert;
    private BigDecimal currencyConvertBalance;
    //
    /*these fields will be returned from creation form after submitting*/
    /*IMPORTANT: operationType is not populated because OrderCreateDto is used for page the orders,
that consists two forms: for BUY and for SELL. After submit this field will be set because we submit concrete form: BUY or SELL.
However if we transfered to form the orders from dashboard, the fields one form (of two forms: SELL or BUY) must be filled.
To determine which of these forms to be filled, we must set field operationType
*/
    private BigDecimal stop; //stop rate for stop order
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount; //amount of base currency: base currency can be bought or sold dependending on operationType
    private OrderBaseType orderBaseType;
    //
    /*
    * these fields will be calculated after submitting the order and before final creation confirmation the order
    * (here: OrderController.submitNewOrderToSell())
    * These amounts calculated directly in java (after check the order parameters in java validator) and will be persistented in db
    * (before this step these amounts were being calculated by javascript and may be occur some difference)
    * */
    private BigDecimal spentWalletBalance;
    private BigDecimal spentAmount;
    private BigDecimal total; //calculated amount of currency conversion = amount * exchangeRate
    private int comissionId;
    private BigDecimal comission; //calculated comission amount depending on operationType and corresponding comission rate
    private BigDecimal totalWithComission; //total + comission
    private Integer sourceId;
    private Integer creatorRoleId;

    /*constructors*/

    public OrderCreateDto() {
    }

    /*service methods*/
    public OrderCreateDto calculateAmounts() {
        if (operationType == null) {
            return this;
        }
        if (operationType == OperationType.SELL) {
            this.spentWalletBalance = this.currencyBaseBalance == null ? BigDecimal.ZERO : this.currencyBaseBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForSellId;
            this.comission = doAction(this.total, this.comissionForSellRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission.negate(), ActionType.ADD);
            this.spentAmount = this.amount;
        } else if (operationType == OperationType.BUY) {
            this.spentWalletBalance = this.currencyConvertBalance == null ? BigDecimal.ZERO : this.currencyConvertBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForBuyId;
            this.comission = doAction(this.total, this.comissionForBuyRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission, ActionType.ADD);
            this.spentAmount = doAction(this.total, this.comission, ActionType.ADD);
        }
        return this;
    }

    @Override
    public String toString() {
        return "OrderCreateDto{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", status=" + status +
                ", currencyPair=" + currencyPair +
                ", comissionForBuyId=" + comissionForBuyId +
                ", comissionForBuyRate=" + comissionForBuyRate +
                ", comissionForSellId=" + comissionForSellId +
                ", comissionForSellRate=" + comissionForSellRate +
                ", walletIdCurrencyBase=" + walletIdCurrencyBase +
                ", currencyBaseBalance=" + currencyBaseBalance +
                ", walletIdCurrencyConvert=" + walletIdCurrencyConvert +
                ", currencyConvertBalance=" + currencyConvertBalance +
                ", operationType=" + operationType +
                ", exchangeRate=" + exchangeRate +
                ", spentWalletBalance=" + spentWalletBalance +
                ", spentAmount=" + spentAmount +
                ", amount=" + amount +
                ", total=" + total +
                ", comissionId=" + comissionId +
                ", comission=" + comission +
                ", totalWithComission=" + totalWithComission +
                '}';
    }

}
