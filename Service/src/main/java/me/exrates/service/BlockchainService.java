package me.exrates.service;

import me.exrates.model.BTCTransaction;
import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;

import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService {

    Optional<BlockchainPayment> createPaymentInvoice(CreditsOperation creditsOperation);

    BlockchainPayment findByInvoiceId(int invoiceId);

    void persistBlockchainTransaction(BlockchainPayment payment, BTCTransaction btcTransaction);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);
}