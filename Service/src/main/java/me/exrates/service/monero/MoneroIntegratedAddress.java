package me.exrates.service.monero;


import me.exrates.service.monero.utils.MoneroUtils;

public class MoneroIntegratedAddress extends MoneroAddress {
    private String paymentId;
    private String integratedAddress;

    public MoneroIntegratedAddress(String standardAddress, String paymentId, String integratedAddress) {
        super(standardAddress);
        MoneroUtils.validatePaymentId(paymentId);
        MoneroUtils.validateIntegratedAddress(integratedAddress);
        this.paymentId = paymentId;
        this.integratedAddress = integratedAddress;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public String getIntegratedAddress() {
        return this.integratedAddress;
    }

    public String toString() {
        return this.integratedAddress;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.integratedAddress == null ? 0 : this.integratedAddress.hashCode());
        result = 31 * result + (this.paymentId == null ? 0 : this.paymentId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            MoneroIntegratedAddress other = (MoneroIntegratedAddress)obj;
            if (this.integratedAddress == null) {
                if (other.integratedAddress != null) {
                    return false;
                }
            } else if (!this.integratedAddress.equals(other.integratedAddress)) {
                return false;
            }

            if (this.paymentId == null) {
                if (other.paymentId != null) {
                    return false;
                }
            } else if (!this.paymentId.equals(other.paymentId)) {
                return false;
            }

            return true;
        }
    }
}
