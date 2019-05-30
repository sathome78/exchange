package processIdManager;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;


public class ProcessIDManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessIDManager.class);

    private static final String PROCESS_ID = "process-id";
    public static final String X_REQUEST_ID_HEADER = "X-Request-ID";

    /*
      Usage:
      ProcessIDManager.registerNewProcessForRequest(getClass(),HttpServletRequest request);
      try {
          // do your work
      } finally {
          ProcessIDManager.unregisterProcessId(getClass());
      }
   */
    public static void registerNewProcessForRequest(Class<?> cls, HttpServletRequest request) {
        logRegistrationByClass(cls);
        String parentProcessId = request.getHeader(X_REQUEST_ID_HEADER);
        String newProcessId = generateId(parentProcessId);
        System.out.println("new processid " + newProcessId);
        setProcessId(newProcessId);
        System.out.println("id setted");
    }

    public static String getCurrentOrRegisterNewProcess(Class<?> cls) {
        return getProcessIdFromCurrentThread()
                .orElseGet(() -> {
                    registerNewThreadForParentProcessId(cls, Optional.empty());
                    return getProcessIdFromCurrentThread().orElse(StringUtils.EMPTY);
                });
    }

    public static void registerNewProcessForRequest(Class<?> cls, HttpServletRequest request, ProcessCallback pc) {
        registerNewProcessForRequest(cls, request);
        try {
            pc.withRegisteredProcess();
        } finally {
            unregisterProcessId(cls);
        }
    }

    /*
       Usage:
       ProcessIDManager.registerNewThreadForParentProcessId(getClass(),Optional<String> parentProcessId);
       try {
           // do your work
       } finally {
           ProcessIDManager.unregisterProcessId(getClass());
       }
    */
    public static void registerNewThreadForParentProcessId(Class<?> cls, Optional<String> parentProcessId) {
        logRegistrationByClass(cls);
        String newProcessId = generateId(parentProcessId.orElse(null));
        setProcessId(newProcessId);
    }

    public static void registerNewThreadForParentProcessId(Class<?> cls, Optional<String> parentProcessId, ProcessCallback pc) {
        registerNewThreadForParentProcessId(cls, parentProcessId);
        try {
            pc.withRegisteredProcess();
        } finally {
            unregisterProcessId(cls);
        }
    }

    /*
        Usage:
        ProcessIDManager.registerCronJobProcessId(getClass());
        try {
            // do your work
        } finally {
            ProcessIDManager.unregisterProcessId(getClass());
        }
     */
    public static void registerCronJobProcessId(Class<?> cls) {
        logRegistrationByClass(cls);
        String id = "cron-job-" + cls.getSimpleName() + "-" + UUID.randomUUID();
        setProcessId(id);
    }

    public static void registerCronJobProcessId(Class<?> cls, ProcessCallback pc) {
        registerCronJobProcessId(cls);
        try {
            pc.withRegisteredProcess();
        } finally {
            unregisterProcessId(cls);
        }
    }

    public static void unregisterProcessId(Class<?> cls) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Class " + cls.getSimpleName() + " unregistered process id " + MDC.get(PROCESS_ID));
        }
        System.out.println(Thread.currentThread().getName());
        MDC.remove(PROCESS_ID);
    }

    public static Optional<String> getProcessIdFromCurrentThread() {
        Optional<String> id = Optional.ofNullable(MDC.get(PROCESS_ID));
        return id;
    }

    private static void logRegistrationByClass(Class<?> cls) {
        if (false && LOGGER.isInfoEnabled()) {
            LOGGER.info("Class " + cls.getSimpleName() + " started registration of new process id");
        }
    }

    private static void setProcessId(String id) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Before registered process id " + id);
        }

        String old = MDC.get(PROCESS_ID);

        if (old != null) {
            throw new RuntimeException("Unable to register new process id " + id +
                    " because it has conflict with existing process id " + old +
                    ".Please ensure that you have called unregisterProcessId method for current thread");
        }
        System.out.println(Thread.currentThread().getName());
        MDC.put(PROCESS_ID, id);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("New process id registered");
        }
    }

    private static String generateId(String parentProcessId) {
        String childProcess = UUID.randomUUID().toString();
        return parentProcessId == null ? childProcess : parentProcessId + ">" + childProcess;
    }

    public interface ProcessCallback {
        void withRegisteredProcess();
    }
}