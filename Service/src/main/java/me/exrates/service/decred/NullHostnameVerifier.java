package me.exrates.service.decred;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class NullHostnameVerifier implements HostnameVerifier {

    /**
     * Method to verify the connected host.
     *
     * @param hostname
     * @param session
     * @return <true>/<false>
     */
    @Override
    public boolean verify(String hostname, SSLSession session) {
        //http://www.jroller.com/hasant/entry/no_subject_alternative_names_matching
        return true;
        //return hostname.equals("192.168.90.49");
    }

}
