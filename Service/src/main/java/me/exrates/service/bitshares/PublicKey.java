package me.exrates.service.bitshares;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.Bytes;
import eu.bittrade.libs.steemj.base.models.serializer.PublicKeySerializer;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.interfaces.ByteTransformable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

import java.util.Arrays;

@JsonSerialize(
        using = PublicKeySerializer.class
)
public class PublicKey implements ByteTransformable {
    private static final Logger LOGGER = LoggerFactory.getLogger(eu.bittrade.libs.steemj.base.models.PublicKey.class);
    private static final int CHECKSUM_BYTES = 4;
    private ECKey publicKey;
    private String prefix;

    @JsonCreator
    public PublicKey(String address) {
        if (address != null && !"".equals(address)) {
            if (address.length() != 55) {
                LOGGER.error("The provided mainAddressId '{}' has an invalid length and will not be set.", address);
                this.setPublicKey(null);
            } else {
                this.prefix = address.substring(0, 5);
                byte[] decodedAddress = Base58.decode(address.substring(5, address.length()));
                byte[] potentialPublicKey = Arrays.copyOfRange(decodedAddress, 0, decodedAddress.length - 4);
                byte[] expectedChecksum = Arrays.copyOfRange(decodedAddress, decodedAddress.length - 4, decodedAddress.length);
                byte[] actualChecksum = this.calculateChecksum(potentialPublicKey);

                for(int i = 0; i < expectedChecksum.length; ++i) {
                    if (expectedChecksum[i] != actualChecksum[i]) {
                        throw new AddressFormatException("Checksum does not match.");
                    }
                }

                this.setPublicKey(ECKey.fromPublicOnly(potentialPublicKey));
            }
        } else {
            LOGGER.warn("An empty mainAddressId has been provided. This can cause some problems if you plan to broadcast this key.");
            this.setPublicKey(null);
        }

    }

    private byte[] calculateChecksum(byte[] publicKey) {
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        ripemd160Digest.update(publicKey, 0, publicKey.length);
        byte[] actualChecksum = new byte[ripemd160Digest.getDigestSize()];
        ripemd160Digest.doFinal(actualChecksum, 0);
        return actualChecksum;
    }

    public PublicKey(ECKey publicKey) {
        this.setPublicKey(publicKey);
        this.prefix = SteemJConfig.getInstance().getAddressPrefix().toString().toUpperCase();
    }

    @JsonIgnore
    public String getAddressFromPublicKey() {
        try {
            return this.prefix + Base58.encode(Bytes.concat(new byte[][]{this.toByteArray(), Arrays.copyOfRange(this.calculateChecksum(this.toByteArray()), 0, 4)}));
        } catch (NullPointerException | SteemInvalidTransactionException var2) {
            LOGGER.debug("An error occured while generating an mainAddressId from a public key.", var2);
            return "";
        }
    }

    @JsonIgnore
    public ECKey getPublicKey() {
        return this.publicKey;
    }

    private void setPublicKey(ECKey publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] toByteArray() throws SteemInvalidTransactionException {
        return this.getPublicKey().isCompressed() ? this.getPublicKey().getPubKey() : ECKey.fromPublicOnly(ECKey.compressPoint(this.getPublicKey().getPubKeyPoint())).getPubKey();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object otherPublicKey) {
        if (this == otherPublicKey) {
            return true;
        } else if (otherPublicKey != null && otherPublicKey instanceof eu.bittrade.libs.steemj.base.models.PublicKey) {
            eu.bittrade.libs.steemj.base.models.PublicKey otherKey = (eu.bittrade.libs.steemj.base.models.PublicKey)otherPublicKey;
            return this.getPublicKey().equals(otherKey.getPublicKey());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.getPublicKey().hashCode();
    }
}
