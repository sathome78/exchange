package me.exrates.service.nem;

import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MosaicIdDto;
import org.nem.core.model.mosaic.MosaicFeeInformation;
import org.nem.core.model.mosaic.MosaicFeeInformationLookup;
import org.nem.core.model.mosaic.MosaicId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(MonolitConditional.class)
public class MosaicFeeInformationLookupImpl implements MosaicFeeInformationLookup {

    @Autowired
    private NemMosaicStrategy mosaicStrategy;


    @Override
    public MosaicFeeInformation findById(MosaicId mosaicId) {
        MosaicIdDto mosaicIdDto = new MosaicIdDto(mosaicId.getNamespaceId().toString(), mosaicId.getName());
        XemMosaicService mosaicService = mosaicStrategy.getByIdDto(mosaicIdDto);
        return new MosaicFeeInformation(mosaicService.getSupply(), mosaicService.getDivisibility());
    }
}
