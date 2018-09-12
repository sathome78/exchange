package me.exrates.service;

import me.exrates.model.PendingPayment;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 17.03.2017.
 */
@Deprecated
public interface BitcoinTransactionService {
  @Transactional(readOnly = true)
  boolean existsPendingPaymentWithStatusAndAddress(InvoiceStatus beginStatus, String address);
  
  @Transactional
  RefillStatusEnum markStartConfirmationProcessing(String address, String txHash, BigDecimal factAmount) throws IllegalInvoiceAmountException;
  
  @Transactional
  void changeTransactionConfidenceForPendingPayment(
          Integer invoiceId,
          int confidenceLevel);
  
  @Transactional
  void provideBtcTransaction(Integer pendingPaymentId, String hash, BigDecimal factAmount, String acceptanceUserEmail) throws IllegalInvoiceAmountException, IllegalOperationTypeException, IllegalTransactionProvidedStatusException;
  
  List<PendingPayment> findUnconfirmedBtcPayments();
  
  List<PendingPayment> findUnpaidBtcPayments();
  
  void updatePendingPaymentHash(Integer txId, String hash);
}
