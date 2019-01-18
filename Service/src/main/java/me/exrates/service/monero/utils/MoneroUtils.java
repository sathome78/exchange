package me.exrates.service.monero.utils;


import me.exrates.service.monero.MoneroAddress;
import me.exrates.service.monero.MoneroIntegratedAddress;
import wallet.MoneroException;
import wallet.MoneroWallet;

import java.util.ArrayList;
import java.util.List;

public class MoneroUtils {
    private static final int STANDARD_ADDRESS_LENGTH = 95;
    private static final int STANDARD_ADDRESS_SUMOKOIN_LENGTH = 99;
    private static final int PAYMENT_ID_LENGTH = 16;
    private static final int INTEGRATED_ADDRESS_LENGTH = 106;
    private static final int INTEGRATED_ADDRESS_SUMOKOIN_LENGTH = 110;
    private static final int MNEMONIC_SEED_NUM_WORDS = 25;
    private static final int VIEW_KEY_LENGTH = 64;
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final List<Character> CHARS = new ArrayList();

    public MoneroUtils() {
    }

    public static boolean isValidStandardAddress(String standardAddress) {
        try {
            validateStandardAddress(standardAddress);
            return true;
        } catch (MoneroException var2) {
            return false;
        }
    }

    public static boolean isValidIntegratedAddress(String integratedAddress) {
        try {
            validateIntegratedAddress(integratedAddress);
            return true;
        } catch (MoneroException var2) {
            return false;
        }
    }

    public static boolean isValidPaymentId(String paymentId) {
        try {
            validatePaymentId(paymentId);
            return true;
        } catch (MoneroException var2) {
            return false;
        }
    }

    public static void validateStandardAddress(String standardAddress) {
//        if (standardAddress == null) {
//            throw new MoneroException("Standard address is null");
//        } else if (!standardAddress.startsWith("4") && !standardAddress.startsWith("9") && !standardAddress.startsWith("A") && !standardAddress.startsWith("S") && !standardAddress.startsWith("h")  && !standardAddress.startsWith("i")) {
//            throw new MoneroException("Standard address does not start with 4, 9 or A");
//        } else {
//            validateBase58(standardAddress);
//            if (standardAddress.length() != 95 && standardAddress.length() != 99 && standardAddress.length() != 97) {
//                throw new MoneroException("Standard address is " + standardAddress.length() + " characters but must be " + 95 + " or " + 99 + " or " + 97);
//            }
//        }
    }

    public static void validatePaymentId(String paymentId) {
        if (paymentId == null) {
            throw new MoneroException("Payment id is null");
        } else {
            validateHex(paymentId);
            if (paymentId.length() != 16) {
                throw new MoneroException("Payment id is " + paymentId.length() + " characters but must be " + 16);
            }
        }
    }

    public static void validateIntegratedAddress(String integratedAddress) {
        if (integratedAddress == null) {
            throw new MoneroException("Integrated address is null");
        } else if (integratedAddress.length() != 106 && integratedAddress.length() != 110) {
            throw new MoneroException("Integrated address is " + integratedAddress.length() + " characters but must be " + 106 + " or " + 110);
        }
    }

    public static void validateIntegratedAddress(String standardAddress, String paymentId, String integratedAddress) {
        validateStandardAddress(standardAddress);
        if (paymentId != null) {
            validatePaymentId(paymentId);
        }

        validateIntegratedAddress(integratedAddress);
    }

    public static void validateAddress(MoneroAddress address) {
        if (address instanceof MoneroIntegratedAddress) {
            MoneroIntegratedAddress integratedAddress = (MoneroIntegratedAddress)address;
            validateIntegratedAddress(integratedAddress.getStandardAddress(), integratedAddress.getPaymentId(), integratedAddress.getIntegratedAddress());
        } else {
            validateStandardAddress(address.getStandardAddress());
        }

    }

    public static void validateMnemonicSeed(String mnemonicSeed) {
        if (mnemonicSeed == null) {
            throw new MoneroException("Mnemonic seed is null");
        } else {
            String[] words = mnemonicSeed.split(" ");
            if (words.length != 25) {
                throw new MoneroException("Mnemonic seed is " + words.length + " words but must be " + 25);
            }
        }
    }

    public static void validateViewKey(String viewKey) {
        if (viewKey == null) {
            throw new MoneroException("View key is null");
        } else if (viewKey.length() != 64) {
            throw new MoneroException("View key is " + viewKey.length() + " characters but must be " + 64);
        }
    }
//
//    public static MoneroAddress toAddress(String address, MoneroWallet wallet) {
//        if (isValidStandardAddress(address)) {
//            return new MoneroAddress(address);
//        } else if (isValidIntegratedAddress(address)) {
//            return wallet.splitIntegratedAddress(address);
//        } else {
//            throw new MoneroException("Address is neither standard nor integrated: " + address);
//        }
//    }

    private static void validateHex(String str) {
        str = "8c8e616596edafb6";
        if (!str.matches("^([0-9A-Fa-f]{2})+$")) {
            throw new MoneroException("Invalid hex: " + str);
        }
    }

    private static void validateBase58(String standardAddress) {
        char[] var1 = standardAddress.toCharArray();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            char c = var1[var3];
            if (!CHARS.contains(c)) {
                throw new MoneroException("Invalid Base58 " + standardAddress);
            }
        }

    }

    static {
        char[] var0 = ALPHABET;
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            char c = var0[var2];
            CHARS.add(c);
        }

    }
}
