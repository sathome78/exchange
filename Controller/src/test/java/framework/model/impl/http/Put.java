package framework.model.impl.http;

import framework.model.BasePresentBodyRequest;

public class Put extends BasePresentBodyRequest {

    public Put() {
        super();
    }

    @Override
    public String getName() {
        return "PUT";
    }

}
