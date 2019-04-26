package mock;

import org.imagination.comparator.Comparator;
import service.api.pokitdok.AbstractPokitDokAPI;
import service.api.pokitdok.PokitDokAPI;

import java.util.HashMap;
import java.util.Map;

import static sun.plugin2.util.PojoUtil.toJson;

public class MockPokitDokAPIImpl extends AbstractPokitDokAPI implements PokitDokAPI {

    private Map<String, String> cache = new HashMap<>();

    @Override
    protected String getMps(Map<String, String> q) throws Exception {
        return findByQuery(q);
    }

    @Override
    protected String getProviders(Map<String, String> q) throws Exception {
        return findByQuery(q);
    }

    @Override
    protected String getEligibility(Map<String, Object> q) throws Exception {
        return findByQuery(q);
    }

    @Override
    protected String getClaimStatus(Map<String, Object> q) throws Exception {
        return findByQuery(q);
    }

    @Override
    protected String getTradingPartners(Map<String, String> q) throws Exception {
        return findByQuery(q);
    }

    private String findByQuery(Object query) {
        String request = toJson(query);

        logger.info("POKITDOK STUB: Looking for " + request);

        for (Map.Entry<String, String> each : cache.entrySet()) {
            if (matches(each.getKey(), request)) {
                return each.getValue();
            }
        }

        String message = "POKITDOK STUB: Unable to find object for request " + request;
        logger.error(message);
        throw new UnknownRequestToStubException(message);
    }

    private boolean matches(String expected, String actual) {
        try {
            Comparator.java().strict().compare(expected, actual);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public void put(String query, String content) {
        this.cache.put(query, content);
    }

    public static class UnknownRequestToStubException extends RuntimeException {

        public UnknownRequestToStubException(String msg) {
            super(msg);
        }
    }
}
