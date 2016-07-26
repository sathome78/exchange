package me.exrates.model.dto.onlineTableDto;

import me.exrates.model.dto.onlineTableDto.OnlineTableDto;

import java.time.LocalDate;

/**
 * Created by Valk on 27.05.2016.
 */
public class NewsDto extends OnlineTableDto {
    private Integer id;
    private String title;
    private String brief;
    private String resource;
    private String variant;
    private String ref;

    public NewsDto() {
        this.needRefresh = true;
    }

    public NewsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}
