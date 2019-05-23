package me.exrates.service.usdx.model.enums;

public enum UsdxApiRequestStatus {

    SUCCESS("success"), FAIL("fail"), ERROR("error");

    UsdxApiRequestStatus(String name){
        this.name = name;
    }

    private String name;

    public String getName(){
        return name;
    }
}
