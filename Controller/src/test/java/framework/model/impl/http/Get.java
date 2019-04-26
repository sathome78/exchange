package framework.model.impl.http;

import framework.model.BaseAbsentBodyRequest;

public class Get extends BaseAbsentBodyRequest {

    public Get() {
        super();
    }

    @Override
    public String getName() {
        return "GET";
    }


}
