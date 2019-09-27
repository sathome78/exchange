package me.exrates.controller.interceptor;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import me.exrates.model.loggingTxContext.QuerriesCountThreadLocal;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class MDCInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        QuerriesCountThreadLocal.init();
        String processId = String.valueOf(UUID.randomUUID());
        ThreadContext.put("process.id", processId);
        ElasticApm.currentTransaction().addLabel("process.id", processId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ThreadContext.clearAll();
        Transaction transaction = ElasticApm.currentTransaction();
        Integer txCount = QuerriesCountThreadLocal.getCountAndUnsetVarialbe();
        transaction.addLabel("querries_count", txCount);
    }
}
