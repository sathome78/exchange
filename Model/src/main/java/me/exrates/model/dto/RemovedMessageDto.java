package me.exrates.model.dto;

public class RemovedMessageDto {
    private Long id;
    private final boolean isRemoved = true;

    public RemovedMessageDto() {
    }

    public RemovedMessageDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRemoved() {
        return isRemoved;
    }
}
