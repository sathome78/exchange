package me.exrates.service.logs;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import processIdManager.ProcessIDManager;

import java.util.Optional;
import java.util.function.Supplier;

import static me.exrates.service.logs.LoggingUtils.getAuthenticatedUser;
import static me.exrates.service.logs.LoggingUtils.getExecutionTime;

@Log4j2(topic = "Service_layer_log")
public class Logger {


    public static Object logAndExecute(Supplier supp, String methodName, Class clazz, String args){
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        ProcessIDManager
                .getProcessIdFromCurrentThread()
                .orElseGet(() -> {
                    ProcessIDManager.registerNewThreadForParentProcessId(clazz, Optional.empty());
                    return StringUtils.EMPTY;
                });
        try {
            Object result = supp.get();
            log.debug(new MethodsLog(methodName, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(methodName, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(clazz);
        }
    }
}
