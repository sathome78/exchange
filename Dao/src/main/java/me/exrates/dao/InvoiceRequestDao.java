package me.exrates.dao;

import me.exrates.model.ClientBank;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.User;
import me.exrates.model.dto.InvoiceRequestFlatForReportDto;
import me.exrates.model.dto.InvoiceUserDto;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by ogolv on 26.07.2016.
 */
public interface InvoiceRequestDao {

  void create(InvoiceRequest invoiceRequest, User user);

  void delete(InvoiceRequest invoiceRequest);

  void updateAcceptanceStatus(InvoiceRequest invoiceRequest);

  Optional<InvoiceRequest> findById(int id);

  Integer getStatusById(int id);

  Optional<InvoiceRequest> findByIdAndBlock(int id);

  List<InvoiceRequest> findByStatus(List<Integer> invoiceRequestStatusIdList);

  Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer intervalMinutes, List<Integer> invoiceRequestStatusIdList);

  void setNewStatusByDateIntervalAndStatus(LocalDateTime boundDate, Integer intervalMinutes, Integer newInvoiceRequestStatusId, List<Integer> invoiceRequestStatusIdList);

  List<InvoiceUserDto> findInvoicesListByStatusChangedAtDate(Integer invoiceRequestStatusId, LocalDateTime dateWhenChanged);

  List<InvoiceRequest> findAll();

  List<InvoiceRequest> findAllForUser(String email);

  List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId);

  List<ClientBank> findClientBanksForCurrency(Integer currencyId);

  InvoiceBank findBankById(Integer bankId);

  void updateConfirmationInfo(InvoiceRequest invoiceRequest);

  void updateReceiptScan(Integer invoiceId, String receiptScanPath);

  void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceRequestStatusEnum invoiceRequestStatus);

  List<InvoiceRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);
}
