package me.exrates.aspect;

/*
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import persistence.dao.jdbc.AbstractIDBaseDAO;
import persistence.dao.interfaces.Log3DAO;
import persistence.json.JsonTransformer;
import persistence.model.ID;
import persistence.model.Log3;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Aspect
@Component
public class DAOAspects {

    private static final Logger LOGGER = LoggerFactory.getLogger(DAOAspects.class);

    public static final ThreadContext CONTEXT = new ThreadContext();

    private Log3DAO log3DAO;
    private JsonTransformer jsonTransformer;

    @Around("!execution(* persistence.dao.jdbc.Log1DAOImpl.*(..)) && " +
            "!execution(* persistence.dao.jdbc.Log2DAOImpl.*(..)) && " +
            "!execution(* persistence.dao.jdbc.Log3DAOImpl.*(..)) && " +
            "execution(* persistence.dao.jdbc.AbstractIDBaseDAO.*(..)) && @annotation(applyProfiling)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp, ApplyProfiling applyProfiling) throws Throwable {
        ThreadContext.ThreadHolder ctx = ThreadContext.get();

        Object[] args = pjp.getArgs();
        AbstractIDBaseDAO<? extends ID> e = (AbstractIDBaseDAO<?>) pjp.getTarget();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String method = signature.getMethod().getName();

        Integer modifiedId;
        boolean isInsert = "insert".equals(method);
        if (isInsert || "update".equals(method) || "delete".equals(method)) {
            modifiedId = ((ID) args[0]).getId();
        } else if ("deleteById".equals(method)) {
            modifiedId = (Integer) args[0];
        } else {
            throw new IllegalArgumentException("Unable to collect required information for modified record id");
        }
        modifiedId = (modifiedId == 0) ? null : modifiedId;

        long start = System.currentTimeMillis();

        String daoRequest = toJson(toKeyValueMap(e, args[0]));

        try {
            Object result = pjp.proceed();
            Object forStoring = isInsert ? ((ID) result).getId() : result;

            addLog(new Log3(ctx.requestId, ctx.userId, e.getTable(), method, modifiedId,
                    daoRequest, toJson(forStoring),
                    getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            addLog(new Log3(ctx.requestId, ctx.userId, e.getTable(), method, modifiedId,
                    daoRequest, StringUtils.EMPTY,
                    getExecutionTime(start), ExceptionUtils.getStackTrace(ex)));

            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toKeyValueMap(AbstractIDBaseDAO e, Object arg) {
        if (arg instanceof Integer) {
            return Collections.singletonMap("id", arg);
        } else {
            return e.asMap((ID) arg);
        }
    }

    private long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }

    private void addLog(Log3 log3) {
        try {
            log3DAO.insert(log3);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to insert new log to the database (Aspect)", e);
            throw e;
        }
    }

    private String toJson(Object o) {
        try {
            return o == null ? "NULL" : jsonTransformer.toJson(o);
        } catch (IOException e) {
            LOGGER.error("Unable to serialize model " + o.getClass() + " to json", e);
            return "(Unable to serialize model " + o.getClass() + " to json). Printint as string [" + o + "]";
        }
    }

    @Required
    public void setLog3DAO(Log3DAO log3DAO) {
        this.log3DAO = log3DAO;
    }

    @Required
    public void setJsonTransformer(JsonTransformer jsonTransformer) {
        this.jsonTransformer = jsonTransformer;
    }

}*/
