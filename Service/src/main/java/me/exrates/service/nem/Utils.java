package me.exrates.service.nem;

import org.nem.core.crypto.KeyPair;
import org.nem.core.model.Address;
import org.nem.core.model.NetworkInfos;

public class Utils {

        /*use it to generate new account keypair*/
        public static void main(String[] args) {
            final KeyPair someKey = new KeyPair();
            System.out.println(String.format("Private key: %s", someKey.getPrivateKey()));
            System.out.println(String.format(" Public key: %s", someKey.getPublicKey()));

            final Address anAddress = Address.fromPublicKey(
                    NetworkInfos.getTestNetworkInfo().getVersion(),
                    someKey.getPublicKey());
            System.out.println(String.format("    Address: %s", anAddress));
        }
}
