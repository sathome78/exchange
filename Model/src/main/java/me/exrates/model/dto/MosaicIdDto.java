package me.exrates.model.dto;

import lombok.Data;

@Data
public class MosaicIdDto {

    private String namespaceId;
    private String name;

    public MosaicIdDto(String namespaceId, String name) {
        this.namespaceId = namespaceId;
        this.name = name;
    }
}
