package framework.model.impl.http;

import framework.model.BasePresentBodyRequest;
import framework.model.HttpMethodBlock;
import framework.model.Response;

public class Patch extends BasePresentBodyRequest implements HttpMethodBlock, Response {

    public Patch() {
        super();
    }

    @Override
    public String getName() {
        return "PATCH";
    }
}
