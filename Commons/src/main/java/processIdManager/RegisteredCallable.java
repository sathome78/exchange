package processIdManager;

import java.util.Optional;
import java.util.concurrent.Callable;

public abstract class RegisteredCallable<V>  implements Callable<V> {
    private final Optional<String> linkThreadID;

    public RegisteredCallable() {
        this.linkThreadID = ProcessIDManager.getProcessIdFromCurrentThread();
    }

    abstract V callImpl() throws Exception;

    @Override
    public final V call() throws Exception {
        ProcessIDManager.registerNewThreadForParentProcessId(getClass(), linkThreadID);
        try {
            return callImpl();
        } finally {
            ProcessIDManager.unregisterProcessId(getClass());
        }
    }
}
