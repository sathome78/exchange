package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 27.02.2018.
 */
@Component
public class NemMosaicStrategy {

    @Autowired
    Map<String, XemMosaicService> mosaicMap;

    Map<MosaicIdDto, XemMosaicService> mosaicIdMap = new HashMap<>();
    Map<String, XemMosaicService> byMerchantNameMap = new HashMap<>();

    @PostConstruct
    private void init() {
        mosaicMap.forEach((k,v)-> {
            mosaicIdMap.put(v.getMosaicId(), v);
            byMerchantNameMap.put(v.getMerchantName(), v);
        });
    }

    XemMosaicService getByIdDto(MosaicIdDto mosaicIdDto) {
        return mosaicIdMap.get(mosaicIdDto);
    }

    XemMosaicService getByMerchantName(String merchantName) {
        return byMerchantNameMap.get(merchantName);
    }
}
