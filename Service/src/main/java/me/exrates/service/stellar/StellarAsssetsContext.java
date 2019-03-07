package me.exrates.service.stellar;

import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.stellar.sdk.Asset;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 04.04.2018.
 */
@Component
@Conditional(MonolitConditional.class)
public class StellarAsssetsContext {

    @Autowired
    private Map<String, StellarAsset> assetsMap;

    private Map<String, StellarAsset> byAssetNameMap = new HashMap<>();
    private Map<Asset, StellarAsset> byAssetObjectMap = new HashMap<>();

    @PostConstruct
    private void init() {
        assetsMap.forEach((k,v)-> {
          byAssetNameMap.put(v.getAssetName(), v);
          byAssetObjectMap.put(v.getAsset(), v);
        });
    }

    StellarAsset getStellarAssetByName(String assetName) {
        return byAssetNameMap.get(assetName);
    }

    StellarAsset getStellarAssetByAssetObject(Asset asset) {
        return byAssetObjectMap.get(asset);
    }

}
