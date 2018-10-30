package me.exrates.model.dto;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EthAccCredentials {

    private int id;
    private String publicKey;
    private String privateKey;
    private String url;
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EthAccCredentials that = (EthAccCredentials) o;
        return id == that.id &&
                Objects.equal(publicKey, that.publicKey) &&
                Objects.equal(privateKey, that.privateKey) &&
                Objects.equal(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, publicKey, privateKey, url);
    }
}
