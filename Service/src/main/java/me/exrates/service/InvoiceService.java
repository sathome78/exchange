package me.exrates.service;

import me.exrates.model.ClientBank;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.exception.invoice.IllegalInvoiceStatusException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


public interface InvoiceService {

  Transaction createPaymentInvoice(InvoiceData invoiceData);

  void acceptInvoiceAndProvideTransaction(Integer invoiceId, String acceptanceUserEmail) throws Exception;

  void declineInvoice(Integer invoiceId, Integer transactionId, String acceptanceUserEmail) throws Exception;

  Integer clearExpiredInvoices(Integer intervalMinutes) throws Exception;

  List<InvoiceRequest> findAllInvoiceRequests();

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  InvoiceBank findBankById(Integer bankId);

  List<ClientBank> findClientBanksForCurrency(Integer currencyId);


  Optional<InvoiceRequest> findRequestById(Integer transactionId);

  Integer getInvoiceRequestStatusByInvoiceId(Integer invoiceId);

  Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId);

  List<InvoiceRequest> findAllByStatus(List<Integer> invoiceRequestStatusIdList);

  List<InvoiceRequest> findAllRequestsForUser(String userEmail);

  void userActionOnInvoice(
      InvoiceConfirmData invoiceConfirmData,
      InvoiceActionTypeEnum userActionOnInvoiceEnum, Locale locale) throws IllegalInvoiceStatusException, InvoiceNotFoundException;

  void updateReceiptScan(Integer invoiceId, String receiptScanPath);
}
