package me.exrates.service.stellar;

import lombok.Data;
import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

/**
 * Created by Maks on 04.04.2018.
 */
@Data
public class StellarAsset {

    private String currencyName;
    private String merchantName;
    private String assetName;
    private String emmitentAccount;
    private KeyPair issuer;
    private Asset asset;

    public StellarAsset(String currencyName, String merchantName, String assetName, String emmitentAccount) {
        this.currencyName = currencyName;
        this.merchantName = merchantName;
        this.assetName = assetName;
        this.emmitentAccount = emmitentAccount;
        issuer = KeyPair.fromAccountId(emmitentAccount);
        asset = Asset.createNonNativeAsset(assetName, issuer);
    }
}
