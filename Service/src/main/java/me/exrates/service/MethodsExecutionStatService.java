package me.exrates.service;


import org.springframework.stereotype.Component;

@Component
public class MethodsExecutionStatService {


    public void onMethodCall(String methodName){

    }

    public void onMethodSuccessExecute(String methodname, long executionTime) {

    }

    public void onMethodError(String methodName, long executionTime) {

    }

    enum Layer {
        CONTROLLER, SERVICE, DAO, JDBC_CALL;
    }
}
