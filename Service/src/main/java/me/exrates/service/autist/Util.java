package me.exrates.service.autist;

import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.tukaani.xz.FinishableOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAOutputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to encapsulate common utility methods
 */
public class Util {
    public static final String TAG = "Util";
    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    public static final int LZMA = 0;
    public static final int XZ = 1;

    /**
     * AES encryption key length in bytes
     */
    public static final int KEY_LENGTH = 32;

    /**
     * Time format used across the platform
     */
    public static final String TIME_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


    /**
     * Converts an hexadecimal string to its corresponding byte[] value.
     * @param s: String with hexadecimal numbers representing a byte array.
     * @return: The actual byte array.
     */
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Converts a byte array, into a user-friendly hexadecimal string.
     * @param bytes: A byte array.
     * @return: A string with the representation of the byte array.
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Decodes an ascii string to a byte array.
     * @param data: Arbitrary ascii-encoded string.
     * @return: Array of bytes.
     */
    public static byte[] hexlify(String data){
        ByteBuffer buffer = ByteBuffer.allocate(data.length());
        for(char letter : data.toCharArray()){
            buffer.put((byte) letter);
        }
        return buffer.array();
    }

    /**
     * Utility function that compresses data using the LZMA algorithm.
     * @param inputBytes Input bytes of the data to be compressed.
     * @param which Which subclass of the FinishableOutputStream to use.
     * @return Compressed data
     * @author Henry Varona
     */
    public static byte[] compress(byte[] inputBytes, int which) {
        FinishableOutputStream out = null;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(inputBytes);
            ByteArrayOutputStream output = new ByteArrayOutputStream(2048);
            LZMA2Options options = new LZMA2Options();
            if(which == Util.LZMA) {
                out = new LZMAOutputStream(output, options, -1);
            }else if(which == Util.XZ){
                out = new XZOutputStream(output, options);
            }
            byte[] inputBuffer = new byte[inputBytes.length];
            int size;
            while ((size = input.read(inputBuffer)) != -1) {
                out.write(inputBuffer, 0, size);
            }
            out.finish();
            return output.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }



    public static byte[] revertBytes(byte[] array){
        byte[] reverted = new byte[array.length];
        for(int i = 0; i < reverted.length; i++){
            reverted[i] = array[array.length - i - 1];
        }
        return reverted;
    }

    /**
     * Function to decrypt a message with AES encryption
     * @param input data to decrypt
     * @param key key for decryption
     * @return input decrypted with AES. Null if the decrypt failed (Bad Key)
     */
    public static byte[] decryptAES(byte[] input, byte[] key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] result = md.digest(key);
            byte[] ivBytes = new byte[16];
            System.arraycopy(result, 32, ivBytes, 0, 16);
            byte[] sksBytes = new byte[32];
            System.arraycopy(result, 0, sksBytes, 0, 32);
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            cipher.init(false, new ParametersWithIV(new KeyParameter(sksBytes), ivBytes));

            byte[] pre_out = new byte[cipher.getOutputSize(input.length)];
            int proc = cipher.processBytes(input, 0, input.length, pre_out, 0);
            int proc2 = cipher.doFinal(pre_out, proc);
            byte[] out = new byte[proc+proc2]; 
            System.arraycopy(pre_out, 0, out, 0, proc+proc2);
            
            //Unpadding
            byte countByte = (byte)((byte)out[out.length-1] % 16);
            int count = countByte & 0xFF;
                       
            if ((count > 15) || (count <= 0)){
                return out;
            }
            
            byte[] temp = new byte[count];
            System.arraycopy(out, out.length - count, temp, 0, temp.length);
            byte[] temp2 = new byte[count];
            Arrays.fill(temp2, (byte) count);
            if (Arrays.equals(temp, temp2)) {
                temp = new byte[out.length - count];
                System.arraycopy(out, 0, temp, 0, out.length - count);
                return temp;
            } else {
                return out;
            }            
        } catch (NoSuchAlgorithmException | DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
