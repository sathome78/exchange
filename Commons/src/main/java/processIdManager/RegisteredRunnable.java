package processIdManager;

import java.util.Optional;

public abstract class RegisteredRunnable implements Runnable {

    private final Optional<String> linkThreadID;

    public RegisteredRunnable() {
        this.linkThreadID = ProcessIDManager.getProcessIdFromCurrentThread();
    }

    public abstract void runImpl();

    @Override
    public final void run() {
        ProcessIDManager.registerNewThreadForParentProcessId(getClass(), linkThreadID, this::runImpl);
    }
}
