package me.exrates.service.bitshares;

import com.google.common.primitives.Bytes;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import org.spongycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MemoDecryptor {
    public final static String TAG = "Memo";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_NONCE = "nonce";
    public static final String KEY_MESSAGE = "message";

    /**
     * Method used to decrypt memo data.
     *
     * @param privateKey: The private key of the recipient.
     * @param publicKey:  The public key of the sender.
     * @param nonce:      The nonce.
     * @param message:    The encrypted message.
     * @throws Exception
     * @return: The plaintext version of the enrcrypted message.
     */
    public static String decryptMessage(ECKey privateKey, PublicKey publicKey, BigInteger nonce, byte[] message) throws NoSuchAlgorithmException {
        String plaintext = "";
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");

            // Getting nonce bytes
            String stringNonce = nonce.toString();
            byte[] nonceBytes = Arrays.copyOfRange(Util.hexlify(stringNonce), 0, stringNonce.length());

            // Getting shared secret
            byte[] secret = publicKey.getPublicKey().getPubKeyPoint().multiply(privateKey.getPrivKey()).normalize().getXCoord().getEncoded();

            // SHA-512 of shared secret
            byte[] ss = sha512.digest(secret);

            byte[] seed = Bytes.concat(nonceBytes, Util.hexlify(Util.bytesToHex(ss)));

            // Calculating checksum
            byte[] sha256Msg = sha256.digest(message);


            // Applying decryption
            byte[] temp = Util.decryptAES(message, seed);
            byte[] checksum = Arrays.copyOfRange(temp, 0, 4);
            byte[] decrypted = Arrays.copyOfRange(temp, 4, temp.length);
            plaintext = new String(decrypted);
            byte[] checksumConfirmation = Arrays.copyOfRange(sha256.digest(decrypted), 0, 4);
            boolean checksumVerification = Arrays.equals(checksum, checksumConfirmation);
            if (!checksumVerification) {
                throw new Exception("Invalid checksum found while performing decryption");
            }
        } catch (Exception e) {
            System.out.println("NoSuchAlgotithmException. Msg:" + e.getMessage());
        }
        return plaintext;
    }


    public static String decryptBTSmemo(String privKey, String memo, String merchantName) throws NoSuchAlgorithmException {
        if (!memo.startsWith("{") || !memo.endsWith("}")) memo = new String(Base64.decode(memo));
        Any json_memo = JsonIterator.deserialize(memo);
        if (json_memo.get(KEY_NONCE).valueType().equals(ValueType.INVALID))
            return json_memo.get(KEY_MESSAGE).toString();
        BigInteger nonce = new BigInteger(json_memo.get(KEY_NONCE).toString());
        String fromKey = json_memo.get(KEY_FROM).toString();
        String toKey = json_memo.get(KEY_TO).toString();
        ECKey privateKey = GrapheneUtils.GrapheneWifToPrivateKey(privKey);
        String publicKey = GrapheneUtils.getAddressFromPublicKey(fromKey.substring(0, 3), privateKey);
        PublicKey pubKey = null;
        if (publicKey.equals(fromKey)) pubKey = new PublicKey(toKey, merchantName);
        else
            pubKey = new PublicKey(fromKey, merchantName);
        String message = json_memo.get(KEY_MESSAGE).toString();
        return decryptMessage(privateKey, pubKey, nonce, Util.hexToBytes(message));
    }
}
