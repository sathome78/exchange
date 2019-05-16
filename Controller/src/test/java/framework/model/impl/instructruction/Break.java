package framework.model.impl.instructruction;

import framework.model.Block;

public class Break implements Block {

    private String comment;

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return null;
    }
}
