package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Valk on 19.04.16.
 */
public class OrderWideListDto {
    private int id;
    private int userId;
    private OperationType operationType;
    private BigDecimal exExchangeRate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private int comissionId;
    private BigDecimal commissionFixedAmount;
    private int userAcceptorId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAcception;
    private OrderStatus status;
    /**/
    private String currencyPairName;
    private String statusString;

    /*constructors*/

    public OrderWideListDto() {
    }

    public OrderWideListDto(ExOrder exOrder) {
        this.id = exOrder.getId();
        this.userId = exOrder.getUserId();
        this.operationType = exOrder.getOperationType();
        this.exExchangeRate = exOrder.getExRate();
        this.amountBase = exOrder.getAmountBase();
        this.amountConvert = exOrder.getAmountConvert();
        this.comissionId = exOrder.getComissionId();
        this.commissionFixedAmount = exOrder.getCommissionFixedAmount();
        this.userAcceptorId = exOrder.getUserAcceptorId();
        this.dateCreation = exOrder.getDateCreation();
        this.dateAcception = exOrder.getDateAcception();
        this.status = exOrder.getStatus();
    }

    /**/
    /*getters setters*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getExExchangeRate() {
        return exExchangeRate;
    }

    public void setExExchangeRate(BigDecimal exExchangeRate) {
        this.exExchangeRate = exExchangeRate;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(BigDecimal amountBase) {
        this.amountBase = amountBase;
    }

    public BigDecimal getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(BigDecimal amountConvert) {
        this.amountConvert = amountConvert;
    }

    public int getComissionId() {
        return comissionId;
    }

    public void setComissionId(int comissionId) {
        this.comissionId = comissionId;
    }

    public BigDecimal getCommissionFixedAmount() {
        return commissionFixedAmount;
    }

    public void setCommissionFixedAmount(BigDecimal commissionFixedAmount) {
        this.commissionFixedAmount = commissionFixedAmount;
    }

    public int getUserAcceptorId() {
        return userAcceptorId;
    }

    public void setUserAcceptorId(int userAcceptorId) {
        this.userAcceptorId = userAcceptorId;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateAcception() {
        return dateAcception;
    }

    public void setDateAcception(LocalDateTime dateAcception) {
        this.dateAcception = dateAcception;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }
}
