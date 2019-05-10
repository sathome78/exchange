package framework.model.impl.http;

import framework.model.BasePresentBodyRequest;

public class Post extends BasePresentBodyRequest {

    public Post() {
        super();
    }

    @Override
    public String getName() {
        return "POST";
    }

}
