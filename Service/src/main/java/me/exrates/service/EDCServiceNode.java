package me.exrates.service;

import me.exrates.model.Transaction;

import java.io.IOException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCServiceNode {

  void submitTransactionsForProcessing(String list);

  String extractAccountId(final String account, final int invoiceId) throws IOException;

  void rescanUnusedAccounts();

  void transferToMainAccount(String accountId, Transaction tx) throws IOException, InterruptedException;

  void transferFromMainAccount(String accountName, String amount) throws IOException, InterruptedException;

  String extractBalance(final String accountId, final int invoiceId) throws IOException;
}
