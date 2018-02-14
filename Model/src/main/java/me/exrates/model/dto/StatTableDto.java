package me.exrates.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Maks on 14.02.2018.
 */
@Data
public class StatTableDto<T> {

    private int page;
    private int pageSize;
    private boolean hasNextPage;
    private int currentSize;
    private List<T> data;


    public StatTableDto(int page, int pageSize, boolean hasNextPage, int currentSize, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.hasNextPage = hasNextPage;
        this.currentSize = currentSize;
        this.data = data;
    }
}
