package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardWidget {

    @JsonIgnore
    private int userId;

    @JsonProperty(value = "type", defaultValue = "", index = 0)
    private String type;
    @JsonProperty(value = "position_x", defaultValue = "0", index = 1)
    private int positionX;
    @JsonProperty(value = "position_y", defaultValue = "0", index = 2)
    private int positionY;
    @JsonProperty(value = "position_w", defaultValue = "0", index = 3)
    private int positionW;
    @JsonProperty(value = "position_h", defaultValue = "0", index = 4)
    private int positionH;
    @JsonProperty(value = "dragAndDrop", defaultValue = "false", index = 5)
    private boolean dragAndDrop;
    @JsonProperty(value = "resizable", defaultValue = "false", index = 6)
    private boolean resizable;
    @JsonProperty(value = "hidden", defaultValue = "false", index = 7)
    private boolean hidden;
}
