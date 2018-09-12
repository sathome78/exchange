package me.exrates.dao;

import me.exrates.model.EDCAccount;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Deprecated
public interface EDCAccountDao {

    EDCAccount findByTransactionId(int id);

    void deleteByTransactionId(int id);

    void create(EDCAccount edcAccount);

    void setAccountIdByTransactionId(int transactionId, String accountId);

    List<EDCAccount> getAccountsWithoutId();

    List<EDCAccount> getUnusedAccounts();

    void setAccountUsed(int transactionId);

}
