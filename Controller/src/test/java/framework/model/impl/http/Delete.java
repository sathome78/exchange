package framework.model.impl.http;

import framework.model.BaseAbsentBodyRequest;

public class Delete extends BaseAbsentBodyRequest {

    public Delete() {
        super();
    }

    @Override
    public String getName() {
        return "DELETE";
    }

}
