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


    public StatTableDto(int page, int pageSize, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.hasNextPage = data.size() > pageSize;
        this.data = data.subList(0, pageSize > data.size() ? data.size() : pageSize);
        this.currentSize = this.data.size();
    }
}
