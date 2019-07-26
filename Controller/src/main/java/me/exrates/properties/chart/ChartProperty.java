package me.exrates.properties.chart;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.json.Json;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ChartProperty {

    private static final String EXRATES = "EXRATES";

    public static String get() {
        return Json.createObjectBuilder()
                .add("supports_search", true)
                .add("supports_group_request", false)
                .add("supports_marks", false)
                .add("supports_timescale_marks", false)
                .add("supports_time", true)
                .add("exchanges", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("value", StringUtils.EMPTY)
                                .add("name", "All Exchanges")
                                .add("desc", StringUtils.EMPTY)
                                .build())
                        .add(Json.createObjectBuilder()
                                .add("value", EXRATES)
                                .add("name", EXRATES)
                                .add("desc", EXRATES)
                                .build())
                        .build())
                .add("symbols_types", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("value", StringUtils.EMPTY)
                                .add("name", "All types")
                                .build()))
                .add("supported_resolutions", Json.createArrayBuilder()
                        .add("5")
                        .add("15")
                        .add("30")
                        .add("60")
                        .add("360")
                        .add("D")
                        .build())
                .build()
                .toString();
    }
}