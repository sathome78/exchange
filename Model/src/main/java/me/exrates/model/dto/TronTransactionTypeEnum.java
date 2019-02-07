package me.exrates.model.dto;

public enum TronTransactionTypeEnum {

    TransferContract(1), TransferAssetContract(2);

    private int contractType;

    TronTransactionTypeEnum(int contractType) {
        this.contractType = contractType;
    }

    public int getContractType() {
        return contractType;
    }
}
