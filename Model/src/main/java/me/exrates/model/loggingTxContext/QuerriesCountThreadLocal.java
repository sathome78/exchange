package me.exrates.model.loggingTxContext;
/** * this class acts as a container to our thread local variables. * @author vsundar * */

public class QuerriesCountThreadLocal {

    private static final ThreadLocal<Context> userThreadLocal = new ThreadLocal<>();


    public static Integer inc() {
        Context context = get();
        return context == null ? null : context.incrementAndGet();
    }

    private static void unset() {userThreadLocal.remove();}

    public static void init() {userThreadLocal.set(new Context(0));}

    private static Context get() {return userThreadLocal.get();}

    public static Integer getCountAndUnsetVarialbe() {
        Context context = get();
        Integer count = context == null ? null : context.getQuerriesCount();
        unset();
        return count;
    }
}
