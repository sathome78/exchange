package me.exrates.service.syndex;

import java.util.List;

public interface SyndexFrontDataService {

    List<SyndexClient.Country> getCountryList();

    List<SyndexClient.Currency> getCurrencyList();

    List<SyndexClient.PaymentSystemWrapper> getPaymentSystemList(String countryCode);
}
