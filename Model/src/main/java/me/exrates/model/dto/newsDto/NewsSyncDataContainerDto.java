package me.exrates.model.dto.newsDto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class NewsSyncDataContainerDto {
  private List<NewsSyncDataDto> updatedNews = new ArrayList<>();
}


