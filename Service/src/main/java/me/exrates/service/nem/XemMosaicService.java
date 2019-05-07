package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;
import org.nem.core.model.mosaic.MosaicId;
import org.nem.core.model.primitive.Quantity;
import org.nem.core.model.primitive.Supply;

public interface XemMosaicService {

    MosaicIdDto getMosaicId();

    String getMerchantName();

    String getCurrencyName();

    long getDecimals();

    Quantity getLevyFee();

    MosaicId mosaicId();

    Supply getSupply();

    int getDivisibility();
}
