package me.exrates.model.dto.merchants.omni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.exrates.model.serializer.StringToBigDecimalDeserializer;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OmniTxDto {

    private String txid;
    private String sendingaddress;
    private String referenceaddress;
    @JsonDeserialize(using = StringToBigDecimalDeserializer.class)
    private BigDecimal amount;
    private boolean ismine;
    private int confirmations;
    private String fee;
    private long blocktime;
    private boolean valid;
    private int positioninblock;
    private long version;
    @JsonProperty(value = "type_int")
    private OmniTxTypeEnum txType;
    private String type;
    private int propertyid;
    private int block;
    private String blockhash;


    /*for testing*/
    public static OmniTxDto getTestTx() {
        OmniTxDto omniTxDto = new OmniTxDto();
        omniTxDto.setTxid("b7783148585a8c04d5f0818124c1eed4481158ab650af66dafc2d5ee7d32adf7");
        omniTxDto.setSendingaddress("wgweyeueuerytuergr");
        omniTxDto.setReferenceaddress("1CbhsqKC37zhC4q6UgGAaf3k6cEwnkShMq");
        omniTxDto.setAmount(BigDecimal.valueOf(3.2));
        omniTxDto.setIsmine(true);
        omniTxDto.setConfirmations(20);
        omniTxDto.setFee("0.001");
        omniTxDto.setBlocktime(1548268591);
        omniTxDto.setValid(true);
        omniTxDto.setPositioninblock(2096);
        omniTxDto.setVersion(0);
        omniTxDto.setTxType(OmniTxTypeEnum.Simple_Send);
        omniTxDto.setType("Simple Send");
        omniTxDto.setPropertyid(31);
        omniTxDto.setBlock(559792);
        omniTxDto.setBlockhash("00000000000000000012265c466a2c95245efa10d0db765db8d6d1fce364bdf1");
        return omniTxDto;
    }

    /*for testing*/
    public static OmniTxDto getTestFrozeTx() {
        OmniTxDto omniTxDto = new OmniTxDto();
        omniTxDto.setTxid("b7783148585a8c04d5f0818124c1eed4481158ab650af66dafc2d5ee7d32adf7");
        omniTxDto.setSendingaddress("wgweyeueuerytuergr");
        omniTxDto.setReferenceaddress("1CbhsqKC37zhC4q6UgGAaf3k6cEwnkShMq");
        omniTxDto.setAmount(BigDecimal.valueOf(3.2));
        omniTxDto.setIsmine(true);
        omniTxDto.setConfirmations(20);
        omniTxDto.setFee("0.001");
        omniTxDto.setBlocktime(1548268591);
        omniTxDto.setValid(true);
        omniTxDto.setPositioninblock(2096);
        omniTxDto.setVersion(0);
        omniTxDto.setTxType(OmniTxTypeEnum.Freeze_Transaction);
        omniTxDto.setType("Freeze transaction");
        omniTxDto.setPropertyid(31);
        omniTxDto.setBlock(559792);
        omniTxDto.setBlockhash("00000000000000000012265c466a2c95245efa10d0db765db8d6d1fce364bdf1");
        return omniTxDto;
    }

}

