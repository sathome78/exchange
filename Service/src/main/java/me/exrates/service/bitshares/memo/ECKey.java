package me.exrates.service.bitshares.memo;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.primitives.UnsignedBytes;
import eu.bittrade.crypto.core.CryptoUtils;
import eu.bittrade.crypto.core.NativeSecp256k1;
import eu.bittrade.crypto.core.NativeSecp256k1Util.AssertFailException;
import eu.bittrade.crypto.core.Secp256k1Context;
import eu.bittrade.crypto.core.Sha256Hash;
import eu.bittrade.crypto.core.crypto.EncryptedData;
import eu.bittrade.crypto.core.crypto.KeyCrypter;
import eu.bittrade.crypto.core.crypto.KeyCrypterException;
import eu.bittrade.crypto.core.crypto.LazyECPoint;
import eu.bittrade.crypto.core.crypto.LinuxSecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERSequenceGenerator;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.math.ec.FixedPointUtil;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;

public class ECKey {
    private static final Logger log = LoggerFactory.getLogger(ECKey.class);
    public static final Comparator<ECKey> AGE_COMPARATOR = new Comparator<ECKey>() {
        public int compare(ECKey k1, ECKey k2) {
            if (k1.creationTimeSeconds == k2.creationTimeSeconds) {
                return 0;
            } else {
                return k1.creationTimeSeconds > k2.creationTimeSeconds ? 1 : -1;
            }
        }
    };
    public static final Comparator<ECKey> PUBKEY_COMPARATOR = new Comparator<ECKey>() {
        private Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();

        public int compare(ECKey k1, ECKey k2) {
            return this.comparator.compare(k1.getPubKey(), k2.getPubKey());
        }
    };
    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    public static final ECDomainParameters CURVE;
    public static final BigInteger HALF_CURVE_ORDER;
    private static final SecureRandom secureRandom;
    protected final BigInteger priv;
    protected final LazyECPoint pub;
    protected long creationTimeSeconds;
    protected KeyCrypter keyCrypter;
    protected EncryptedData encryptedPrivateKey;
    private byte[] pubKeyHash;
    @VisibleForTesting
    public static boolean FAKE_SIGNATURES;

    public ECKey() {
        this(secureRandom);
    }

    public ECKey(SecureRandom secureRandom) {
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(CURVE, secureRandom);
        generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters)keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters)keypair.getPublic();
        this.priv = privParams.getD();
        this.pub = new LazyECPoint(CURVE.getCurve(), pubParams.getQ().getEncoded(true));
        this.creationTimeSeconds = CryptoUtils.currentTimeSeconds();
    }

    protected ECKey(@Nullable BigInteger priv, ECPoint pub) {
        this(priv, new LazyECPoint((ECPoint) Preconditions.checkNotNull(pub)));
    }

    protected ECKey(@Nullable BigInteger priv, LazyECPoint pub) {
        if (priv != null) {
            Preconditions.checkArgument(priv.bitLength() <= 256, "private key exceeds 32 bytes: %s bits", priv.bitLength());
            Preconditions.checkArgument(!priv.equals(BigInteger.ZERO));
            Preconditions.checkArgument(!priv.equals(BigInteger.ONE));
        }

        this.priv = priv;
        this.pub = (LazyECPoint) Preconditions.checkNotNull(pub);
    }

    public static ECPoint decompressPoint(ECPoint point) {
        return getPointWithCompression(point, false);
    }

    private static ECPoint getPointWithCompression(ECPoint point, boolean compressed) {
        if (point.isCompressed() == compressed) {
            return point;
        } else {
            point = point.normalize();
            BigInteger x = point.getAffineXCoord().toBigInteger();
            BigInteger y = point.getAffineYCoord().toBigInteger();
            return CURVE.getCurve().createPoint(x, y, compressed);
        }
    }

    public static ECKey fromPrivate(BigInteger privKey) {
        return fromPrivate(privKey, true);
    }

    public static ECKey fromPrivate(BigInteger privKey, boolean compressed) {
        ECPoint point = publicPointFromPrivate(privKey);
        return new ECKey(privKey, getPointWithCompression(point, compressed));
    }

    public static ECKey fromPrivate(byte[] privKeyBytes) {
        return fromPrivate(new BigInteger(1, privKeyBytes));
    }

    public static ECKey fromPrivate(byte[] privKeyBytes, boolean compressed) {
        return fromPrivate(new BigInteger(1, privKeyBytes), compressed);
    }

    public ECKey decompress() {
        return !this.pub.isCompressed() ? this : new ECKey(this.priv, decompressPoint(this.pub.get()));
    }

    /** @deprecated */
    @Deprecated
    public ECKey(@Nullable byte[] privKeyBytes, @Nullable byte[] pubKey) {
        this(privKeyBytes == null ? null : new BigInteger(1, privKeyBytes), pubKey);
    }

    /** @deprecated */
    @Deprecated
    public ECKey(EncryptedData encryptedPrivateKey, byte[] pubKey, KeyCrypter keyCrypter) {
        this((byte[])null, pubKey);
        this.keyCrypter = (KeyCrypter) Preconditions.checkNotNull(keyCrypter);
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    /** @deprecated */
    @Deprecated
    public ECKey(@Nullable BigInteger privKey, @Nullable byte[] pubKey, boolean compressed) {
        if (privKey == null && pubKey == null) {
            throw new IllegalArgumentException("ECKey requires at least private or public key");
        } else {
            this.priv = privKey;
            if (pubKey == null) {
                ECPoint point = publicPointFromPrivate(privKey);
                point = getPointWithCompression(point, compressed);
                this.pub = new LazyECPoint(point);
            } else {
                this.pub = new LazyECPoint(CURVE.getCurve(), pubKey);
            }

        }
    }

    /** @deprecated */
    @Deprecated
    private ECKey(@Nullable BigInteger privKey, @Nullable byte[] pubKey) {
        this(privKey, pubKey, false);
    }

    public boolean isPubKeyOnly() {
        return this.priv == null;
    }

    public static ECPoint publicPointFromPrivate(BigInteger privKey) {
        if (privKey.bitLength() > CURVE.getN().bitLength()) {
            privKey = privKey.mod(CURVE.getN());
        }

        return (new FixedPointCombMultiplier()).multiply(CURVE.getG(), privKey);
    }

    public byte[] getPubKey() {
        return this.pub.getEncoded();
    }

    public ECPoint getPubKeyPoint() {
        return this.pub.get();
    }

    public BigInteger getPrivKey() {
        if (this.priv == null) {
            throw new ECKey.MissingPrivateKeyException();
        } else {
            return this.priv;
        }
    }

    public boolean isCompressed() {
        return this.pub.isCompressed();
    }

    public ECKey.ECDSASignature sign(Sha256Hash input) throws KeyCrypterException {
        return this.sign(input, (KeyParameter)null);
    }

    public ECKey.ECDSASignature sign(Sha256Hash input, @Nullable KeyParameter aesKey) throws KeyCrypterException {
        KeyCrypter crypter = this.getKeyCrypter();
        if (crypter != null) {
            if (aesKey == null) {
                throw new ECKey.KeyIsEncryptedException();
            } else {
                return this.decrypt(aesKey).sign(input);
            }
        } else if (this.priv == null) {
            throw new ECKey.MissingPrivateKeyException();
        } else {
            return this.doSign(input, this.priv);
        }
    }

    protected ECKey.ECDSASignature doSign(Sha256Hash input, BigInteger privateKeyForSigning) {
        if (Secp256k1Context.isEnabled()) {
            try {
                byte[] signature = NativeSecp256k1.sign(input.getBytes(), CryptoUtils.bigIntegerToBytes(privateKeyForSigning, 32));
                return ECKey.ECDSASignature.decodeFromDER(signature);
            } catch (AssertFailException var6) {
                log.error("Caught AssertFailException inside secp256k1", var6);
                throw new RuntimeException(var6);
            }
        } else {
            if (FAKE_SIGNATURES) {
                Preconditions.checkNotNull(privateKeyForSigning);
            }

            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKeyForSigning, CURVE);
            signer.init(true, privKey);
            BigInteger[] components = signer.generateSignature(input.getBytes());
            return (new ECKey.ECDSASignature(components[0], components[1])).toCanonicalised();
        }
    }

    public static boolean verify(byte[] data, ECKey.ECDSASignature signature, byte[] pub) {
        if (FAKE_SIGNATURES) {
            return true;
        } else if (Secp256k1Context.isEnabled()) {
            try {
                return NativeSecp256k1.verify(data, signature.encodeToDER(), pub);
            } catch (AssertFailException var6) {
                log.error("Caught AssertFailException inside secp256k1", var6);
                return false;
            }
        } else {
            ECDSASigner signer = new ECDSASigner();
            ECPublicKeyParameters params = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(pub), CURVE);
            signer.init(false, params);

            try {
                return signer.verifySignature(data, signature.r, signature.s);
            } catch (NullPointerException var7) {
                log.error("Caught NPE inside bouncy castle", var7);
                return false;
            }
        }
    }

    public static boolean verify(byte[] data, byte[] signature, byte[] pub) {
        if (Secp256k1Context.isEnabled()) {
            try {
                return NativeSecp256k1.verify(data, signature, pub);
            } catch (AssertFailException var4) {
                log.error("Caught AssertFailException inside secp256k1", var4);
                return false;
            }
        } else {
            return verify(data, ECKey.ECDSASignature.decodeFromDER(signature), pub);
        }
    }

    public boolean verify(byte[] hash, byte[] signature) {
        return verify(hash, signature, this.getPubKey());
    }

    public boolean verify(Sha256Hash sigHash, ECKey.ECDSASignature signature) {
        return verify(sigHash.getBytes(), signature, this.getPubKey());
    }

    public byte[] getPrivKeyBytes() {
        return CryptoUtils.bigIntegerToBytes(this.getPrivKey(), 32);
    }

    public DumpedPrivateKey getPrivateKeyEncoded(Integer privateKeyHeader) {
        return new DumpedPrivateKey(privateKeyHeader, this.getPrivKeyBytes(), this.isCompressed());
    }

    public void setCreationTimeSeconds(long newCreationTimeSeconds) {
        if (newCreationTimeSeconds < 0L) {
            throw new IllegalArgumentException("Cannot set creation time to negative value: " + newCreationTimeSeconds);
        } else {
            this.creationTimeSeconds = newCreationTimeSeconds;
        }
    }

    public ECKey decrypt(KeyCrypter keyCrypter, KeyParameter aesKey) throws KeyCrypterException {
        Preconditions.checkNotNull(keyCrypter);
        if (this.keyCrypter != null && !this.keyCrypter.equals(keyCrypter)) {
            throw new KeyCrypterException("The keyCrypter being used to decrypt the key is different to the one that was used to encrypt it");
        } else {
            Preconditions.checkState(this.encryptedPrivateKey != null, "This key is not encrypted");
            byte[] unencryptedPrivateKey = keyCrypter.decrypt(this.encryptedPrivateKey, aesKey);
            ECKey key = fromPrivate(unencryptedPrivateKey);
            if (!this.isCompressed()) {
                key = key.decompress();
            }

            if (!Arrays.equals(key.getPubKey(), this.getPubKey())) {
                throw new KeyCrypterException("Provided AES key is wrong");
            } else {
                key.setCreationTimeSeconds(this.creationTimeSeconds);
                return key;
            }
        }
    }

    public ECKey decrypt(KeyParameter aesKey) throws KeyCrypterException {
        KeyCrypter crypter = this.getKeyCrypter();
        if (crypter == null) {
            throw new KeyCrypterException("No key crypter available");
        } else {
            return this.decrypt(crypter, aesKey);
        }
    }

    public boolean isEncrypted() {
        return this.keyCrypter != null && this.encryptedPrivateKey != null && this.encryptedPrivateKey.encryptedBytes.length > 0;
    }

    @Nullable
    public KeyCrypter getKeyCrypter() {
        return this.keyCrypter;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && o instanceof ECKey) {
            ECKey other = (ECKey)o;
            return Objects.equal(this.priv, other.priv) && Objects.equal(this.pub, other.pub) && Objects.equal(this.creationTimeSeconds, other.creationTimeSeconds) && Objects.equal(this.keyCrypter, other.keyCrypter) && Objects.equal(this.encryptedPrivateKey, other.encryptedPrivateKey);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.pub.hashCode();
    }

    public String toString() {
        return this.toString(false, (KeyParameter)null, (Integer)null);
    }

    public String getPrivateKeyAsHex() {
        return CryptoUtils.HEX.encode(this.getPrivKeyBytes());
    }

    public String getPublicKeyAsHex() {
        return CryptoUtils.HEX.encode(this.pub.getEncoded());
    }

    public String getPrivateKeyAsWiF(Integer privateKeyHeader) {
        return this.getPrivateKeyEncoded(privateKeyHeader).toString();
    }

    private String toString(boolean includePrivate, @Nullable KeyParameter aesKey, Integer privateKeyHeader) {
        ToStringHelper helper = MoreObjects.toStringHelper(this).omitNullValues();
        helper.add("pub HEX", this.getPublicKeyAsHex());
        if (includePrivate) {
            ECKey decryptedKey = this.isEncrypted() ? this.decrypt((KeyParameter) Preconditions.checkNotNull(aesKey)) : this;

            try {
                helper.add("priv HEX", decryptedKey.getPrivateKeyAsHex());
                helper.add("priv WIF", decryptedKey.getPrivateKeyAsWiF(privateKeyHeader));
            } catch (IllegalStateException var8) {
                ;
            } catch (Exception var9) {
                String message = var9.getMessage();
                helper.add("priv EXCEPTION", var9.getClass().getName() + (message != null ? ": " + message : ""));
            }
        }

        if (this.creationTimeSeconds > 0L) {
            helper.add("creationTimeSeconds", this.creationTimeSeconds);
        }

        helper.add("keyCrypter", this.keyCrypter);
        if (includePrivate) {
            helper.add("encryptedPrivateKey", this.encryptedPrivateKey);
        }

        helper.add("isEncrypted", this.isEncrypted());
        helper.add("isPubKeyOnly", this.isPubKeyOnly());
        return helper.toString();
    }

    static {
        if (CryptoUtils.isAndroidRuntime()) {
            new LinuxSecureRandom();
        }

        FixedPointUtil.precompute(CURVE_PARAMS.getG(), 12);
        CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
        secureRandom = new SecureRandom();
        FAKE_SIGNATURES = false;
    }

    public static class KeyIsEncryptedException extends ECKey.MissingPrivateKeyException {
        public KeyIsEncryptedException() {
        }
    }

    public static class MissingPrivateKeyException extends RuntimeException {
        public MissingPrivateKeyException() {
        }
    }

    public static class ECDSASignature {
        public final BigInteger r;
        public final BigInteger s;

        public ECDSASignature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        public boolean isCanonical() {
            return this.s.compareTo(ECKey.HALF_CURVE_ORDER) <= 0;
        }

        public ECKey.ECDSASignature toCanonicalised() {
            return !this.isCanonical() ? new ECKey.ECDSASignature(this.r, ECKey.CURVE.getN().subtract(this.s)) : this;
        }

        public byte[] encodeToDER() {
            try {
                return this.derByteStream().toByteArray();
            } catch (IOException var2) {
                throw new RuntimeException(var2);
            }
        }

        public static ECKey.ECDSASignature decodeFromDER(byte[] bytes) throws IllegalArgumentException {
            ASN1InputStream decoder = null;

            ECKey.ECDSASignature var6;
            try {
                decoder = new ASN1InputStream(bytes);
                ASN1Primitive seqObj = decoder.readObject();
                if (seqObj == null) {
                    throw new IllegalArgumentException("Reached past end of ASN.1 stream.");
                }

                if (!(seqObj instanceof DLSequence)) {
                    throw new IllegalArgumentException("Read unexpected class: " + seqObj.getClass().getName());
                }

                DLSequence seq = (DLSequence)seqObj;

                ASN1Integer r;
                ASN1Integer s;
                try {
                    r = (ASN1Integer)seq.getObjectAt(0);
                    s = (ASN1Integer)seq.getObjectAt(1);
                } catch (ClassCastException var16) {
                    throw new IllegalArgumentException(var16);
                }

                var6 = new ECKey.ECDSASignature(r.getPositiveValue(), s.getPositiveValue());
            } catch (IOException var17) {
                throw new IllegalArgumentException(var17);
            } finally {
                if (decoder != null) {
                    try {
                        decoder.close();
                    } catch (IOException var15) {
                        ;
                    }
                }

            }

            return var6;
        }

        protected ByteArrayOutputStream derByteStream() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(72);
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(this.r));
            seq.addObject(new ASN1Integer(this.s));
            seq.close();
            return bos;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                ECKey.ECDSASignature other = (ECKey.ECDSASignature)o;
                return this.r.equals(other.r) && this.s.equals(other.s);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.r, this.s});
        }
    }
}
