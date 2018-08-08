package me.exrates.model.dto;

import com.google.gson.JsonObject;
import lombok.Data;
import org.json.JSONObject;

@Data
public class TronNewAddressDto {

    private String address;
    private String privateKey;

    public static TronNewAddressDto fromGetAddressMethod(String response) {
        JSONObject json = new JSONObject(response);
        TronNewAddressDto dto = new TronNewAddressDto();
        dto.setAddress(json.getString("address"));
        dto.setPrivateKey(json.getString("privateKey"));
        return dto;
    }
}
