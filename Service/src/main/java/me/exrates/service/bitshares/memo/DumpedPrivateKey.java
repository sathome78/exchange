package me.exrates.service.bitshares.memo;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import eu.bittrade.crypto.core.AddressFormatException;
import eu.bittrade.crypto.core.VersionedChecksummedBytes;
import eu.bittrade.crypto.core.WrongNetworkException;
import eu.bittrade.crypto.core.base58.Base58ChecksumProvider;

import javax.annotation.Nullable;
import java.util.Arrays;

public class DumpedPrivateKey extends VersionedChecksummedBytes {
    public static DumpedPrivateKey fromBase58(@Nullable Integer privateKeyHeader, String base58, Base58ChecksumProvider checksumProvider) {
        return new DumpedPrivateKey(privateKeyHeader, base58, checksumProvider);
    }

    DumpedPrivateKey(Integer privateKeyHeader, byte[] keyBytes, boolean compressed) {
        super(privateKeyHeader, encode(keyBytes, compressed));
    }

    private static byte[] encode(byte[] keyBytes, boolean compressed) {
        Preconditions.checkArgument(keyBytes.length == 32, "Private keys must be 32 bytes");
        if (!compressed) {
            return keyBytes;
        } else {
            byte[] bytes = new byte[33];
            System.arraycopy(keyBytes, 0, bytes, 0, 32);
            bytes[32] = 1;
            return bytes;
        }
    }

    private DumpedPrivateKey(@Nullable Integer privateKeyHeader, String encoded, Base58ChecksumProvider checksumProvider) {
        super(encoded, checksumProvider);
        if (privateKeyHeader != null && this.version != privateKeyHeader) {
            throw new WrongNetworkException(this.version, new int[]{privateKeyHeader});
        } else if (this.bytes.length != 32 && this.bytes.length != 33) {
            throw new AddressFormatException("Wrong number of bytes for a private key, not 32 or 33");
        }
    }

    public ECKey getKey() {
        return ECKey.fromPrivate(Arrays.copyOf(this.bytes, 32), this.isPubKeyCompressed());
    }

    public boolean isPubKeyCompressed() {
        return this.bytes.length == 33 && this.bytes[32] == 1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DumpedPrivateKey other = (DumpedPrivateKey)o;
            return this.version == other.version && Arrays.equals(this.bytes, other.bytes);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.version, Arrays.hashCode(this.bytes)});
    }
}
