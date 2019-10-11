package me.exrates.service.syndex;

import java.util.List;
import java.util.Set;

public interface SyndexFrontDataService {

    List<SyndexClient.Country> getCountryList();

    Set<SyndexClient.Currency> getCurrencyList();

    Set<SyndexClient.PaymentSystem> getPaymentSystemList(String countryCode);
}
