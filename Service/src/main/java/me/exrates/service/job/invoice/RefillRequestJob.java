package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.BitcoinService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by ValkSam
 */
@Service
@Log4j2
public class RefillRequestJob {

  @Autowired
  RefillService refillService;

  @Autowired
  private MerchantService merchantService;
  @Autowired
  private MerchantServiceContext serviceContext;

  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  private void refillExpiredClean() throws Exception {
    log.debug("\nstart expired refill cleaning ... ");
    Integer expireCount = refillService.clearExpiredInvoices();
    log.debug("\n... end expired refill cleaning. Mark as expired: " + expireCount);
  }

  /**
   * Method for check unprocessed transactions for coins in merchantNames array.
   * Because blocknotify doesn't work correctly (!!! need to check node config and node config properties !!!)
   * During the check processBtcPayment is executed, which create refill request and refill user wallet.
   */
  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  public void refillCheckPaymentsForCoins() {
      String[] merchantNames = new String[]{"QRK", "LBTC", "LPC", "XFC", "DDX"};

      for (String coin : merchantNames) {
          getBitcoinServiceByMerchantName(coin).scanForUnprocessedTransactions(null);
      }
  }

  private BitcoinService getBitcoinServiceByMerchantName(String merchantName) {
    String serviceBeanName = merchantService.findByName(merchantName).getServiceBeanName();
    IMerchantService merchantService = serviceContext.getMerchantService(serviceBeanName);
    if (merchantService == null || !(merchantService instanceof BitcoinService)) {
      throw new RuntimeException(serviceBeanName);
    }
    return (BitcoinService) merchantService;
  }

}



