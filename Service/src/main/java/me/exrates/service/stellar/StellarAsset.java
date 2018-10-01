package me.exrates.service.stellar;

import lombok.Data;
import me.exrates.service.nodes_control.NodeStateControl;
import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

/**
 * Created by Maks on 04.04.2018.
 */
@Data
public class StellarAsset implements NodeStateControl {

    private String currencyName;
    private String merchantName;
    private String assetName;
    private String emmitentAccount;
    private KeyPair issuer;
    private Asset asset;
    private StellarService stellarService;

    public StellarAsset(String currencyName, String merchantName, String assetName, String emmitentAccount, StellarService stellarService) {
        this.stellarService = stellarService;
        this.currencyName = currencyName;
        this.merchantName = merchantName;
        this.assetName = assetName;
        this.emmitentAccount = emmitentAccount;
        issuer = KeyPair.fromAccountId(emmitentAccount);
        asset = Asset.createNonNativeAsset(assetName, issuer);
    }

    @Override
    public boolean isNodeWorkCorrect() {
        return false;
    }

    @Override
    public String getBalance() {
        return stellarService.getBalance(asset);
    }
}
