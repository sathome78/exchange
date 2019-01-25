package me.exrates.service.nem;

import lombok.ToString;
import me.exrates.model.dto.MosaicIdDto;
import org.nem.core.model.mosaic.MosaicId;
import org.nem.core.model.primitive.Quantity;
import org.nem.core.model.primitive.Supply;

/**
 * Created by Maks on 27.02.2018.
 */
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
