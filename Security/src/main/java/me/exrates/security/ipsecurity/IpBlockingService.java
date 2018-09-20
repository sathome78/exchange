package me.exrates.security.ipsecurity;



public interface IpBlockingService {

    void checkIp(String ip, IpTypesOfChecking typeOfChecking);

    void failureProcessing(String ipAddress, IpTypesOfChecking typeOfChecking);

    void successfulProcessing(String ipAddress, IpTypesOfChecking typeOfChecking);
}
