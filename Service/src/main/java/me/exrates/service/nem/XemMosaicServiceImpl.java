package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;
import org.nem.core.model.mosaic.MosaicId;
import org.nem.core.model.namespace.NamespaceId;
import org.nem.core.model.primitive.Quantity;
import org.nem.core.model.primitive.Supply;

/**
 * Created by Maks on 27.02.2018.
 */
public class XemMosaicServiceImpl implements XemMosaicService {

    private String merchantName;
    private String currencyName;
    private MosaicIdDto mosaicIdDto;
    private long decimals;
    private int divisibility;
    private Supply supply;
    private MosaicId mosaicId;
    private Quantity levyFee;


    public XemMosaicServiceImpl(String merchantName, String currencyName, MosaicIdDto mosaicIdDto, long decimals,
                                int divisibility, Supply supply, long levyFee) {
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        this.mosaicIdDto = mosaicIdDto;
        this.decimals = decimals;
        this.divisibility = divisibility;
        this.supply = supply;
        this.mosaicId = new MosaicId(new NamespaceId(mosaicIdDto.getNamespaceId()), mosaicIdDto.getName());
        this.levyFee = new Quantity(levyFee);
    }

    @Override
    public Quantity getLevyFee() {
        return levyFee;
    }

    @Override
    public MosaicId mosaicId() {
        return mosaicId;
    }

    @Override
    public Supply getSupply() {
        return supply;
    }

    @Override
    public int getDivisibility() {
        return divisibility;
    }

    @Override
    public MosaicIdDto getMosaicId() {
        return mosaicIdDto;
    }

    @Override
    public String getMerchantName() {
        return merchantName;
    }

    @Override
    public String getCurrencyName() {
        return currencyName;
    }

    @Override
    public long getDecimals() {
        return decimals;
    }
}
