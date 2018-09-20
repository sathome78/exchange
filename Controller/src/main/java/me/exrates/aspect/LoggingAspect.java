package me.exrates.aspect;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 31.03.2017.
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LogManager.getLogger("exceptions_log");
    private static final Logger logExtended = LogManager.getLogger("exceptions_ext_log");
    private static final Logger withdrawLog = LogManager.getLogger("withdraw_asp_log");
    private static final Logger withdrawExtLog = LogManager.getLogger("withdraw_asp_ext_log");
    private static final Logger refillLog = LogManager.getLogger("refill_asp_log");
    private static final Logger refillExtLog = LogManager.getLogger("refill_asp_ext_log");
    private static final Logger transferLog = LogManager.getLogger("transfer_asp_log");
    private static final Logger transferExtLog = LogManager.getLogger("transfer_asp_ext_log");
    private static final Logger adminLog = LogManager.getLogger("admin_log");


    @AfterReturning(value = "execution(* * (..)) && " +
            "@annotation(me.exrates.controller.annotation.AdminLoggable)", returning = "res")
    public void logAdminExec(JoinPoint joinPoint, Object res) {
        HttpServletRequest s = (HttpServletRequest) RequestContextHolder
                .currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String method = String.join(".", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        String executor = s.getUserPrincipal() == null ? " no user " : s.getUserPrincipal().getName();
        adminLog.debug(String.format("logged admin method %s executed by user %s with args: \n%s \n%s",
                method, executor,
                s.getParameterMap().entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + " : " + Arrays.stream(entry.getValue())
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.joining(", ")),
                String.join("\n", Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull)
                        .map(Object::toString).collect(Collectors.toList()))));
        try {
            if (res != null) {
                adminLog.debug(String.format("result of execution %s by %s is %s", method, executor, res.toString()));
            }
        } catch (Throwable throwable) {
            adminLog.error("proceed joinpoint error", throwable);
        }
    }


  @AfterThrowing(pointcut = "(execution(* me.exrates.controller..*(..)) " +
          "|| execution(* me.exrates.dao..*(..))" +
          "|| execution(* me.exrates.security.service..*(..))" +
          "|| execution(* me.exrates.service.impl..*(..)) " +
          "|| execution(* me.exrates.service.stockExratesRetrieval..*(..)) " +
          "|| execution(* me.exrates.service.newsExt..*(..))) " +
          "&& !execution(* me.exrates.controller.filter.XssRequestFilter.*(..))" +
          "&& !execution(* me.exrates.controller.validator..*(..))" +
          "&& !execution(* me.exrates.security.service.UserDetailsServiceImpl.*(..)) " +
          "&& !execution(* me.exrates.security.ipsecurity.IpBlockingServiceImpl.*(..))", throwing = "ex")
  public void logException(JoinPoint joinPoint, Exception ex) {
    log.debug(String.format("error in method %s with args: \n%s",
            String.join(".", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()) ,
            String.join("\n", Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull)
                    .map(Object::toString).collect(Collectors.toList()))) );
    log.debug(String.format("exception: %s : %s ", ex.getClass().getSimpleName(), ex.getMessage()));
    log.debug("Root cause: " + ExceptionUtils.getRootCauseMessage(ex));
    logExtended.debug(ExceptionUtils.getStackTrace(ex));
  }

    @AfterThrowing(pointcut = "(execution(* me.exrates.controller.merchants.WithdrawRequestController..*(..)) " +
            "|| execution(* me.exrates.service.impl.WithdrawServiceImpl..*(..)) " +
            "|| execution(* me.exrates.service.merchantStrategy.IMerchantService.withdraw(*))) ", throwing = "ex")
    public void withdrawLogException(JoinPoint joinPoint, Exception ex) {
        int id = LocalDateTime.now().getNano();
        withdrawLog.error(String.format("id=(%s) error in method %s with args: \n%s",
                id,
                String.join(".", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()),
                String.join("\n", Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull)
                        .map(Object::toString).collect(Collectors.toList()))));
        withdrawLog.error(String.format("id=(%s) exception: %s : %s ", id, ex.getClass().getSimpleName(), ex.getMessage()));
        withdrawExtLog.error(String.format("id=(%s) stackTrace: ", id) + ExceptionUtils.getStackTrace(ex));
    }

    @AfterThrowing(pointcut = "(execution(* me.exrates.controller.merchants.RefillRequestController..*(..)) " +
            "|| execution(* me.exrates.service.impl.RefillServiceImpl..*(..)) " +
            "|| execution(* me.exrates.service.merchantStrategy.IMerchantService.refill(*))) ", throwing = "ex")
    public void refillLogException(JoinPoint joinPoint, Exception ex) {
        int id = LocalDateTime.now().getNano();
        refillLog.error(String.format("id=(%s) error in method %s with args: \n%s",
                id,
                String.join(".", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()),
                String.join("\n", Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull)
                        .map(Object::toString).collect(Collectors.toList()))));
        refillLog.error(String.format("id=(%s) exception: %s : %s ", id, ex.getClass().getSimpleName(), ex.getMessage()));
        refillExtLog.error(String.format("id=(%s) stackTrace: ", id) + ExceptionUtils.getStackTrace(ex));
    }

    @AfterThrowing(pointcut = "(execution(* me.exrates.controller.merchants.TransferRequestController..*(..)) " +
            "|| execution(* me.exrates.service.impl.TransferServiceImpl..*(..)) " +
            "|| execution(* me.exrates.service.merchantStrategy.IMerchantService.transfer(*))) ", throwing = "ex")
    public void transferLogException(JoinPoint joinPoint, Exception ex) {
        int id = LocalDateTime.now().getNano();
        transferLog.error(String.format("id=(%s) error in method %s with args: \n%s",
                id,
                String.join(".", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()),
                String.join("\n", Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull)
                        .map(Object::toString).collect(Collectors.toList()))));
        transferLog.error(String.format("id=(%s) exception: %s : %s ", id, ex.getClass().getSimpleName(), ex.getMessage()));
        transferExtLog.error(String.format("id=(%s) stackTrace: ", id) + ExceptionUtils.getStackTrace(ex));
    }

}
