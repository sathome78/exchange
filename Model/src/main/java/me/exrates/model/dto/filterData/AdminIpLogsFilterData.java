package me.exrates.model.dto.filterData;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static me.exrates.model.dto.filterData.FilterDataItem.DATE_FORMAT;
import static me.exrates.model.dto.filterData.FilterDataItem.LIKE_FORMAT_MIDDLE;

@Getter
@Setter
@ToString
public class AdminIpLogsFilterData extends TableFilterData {

    private String ip;
    private String event;
    private String email;
    private String dateFrom;
    private String dateTo;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("ip", "IP_Log.ip LIKE", ip, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("event", "IP_Log.event =", event),
                new FilterDataItem("date_from", "IP_Log.date >=", dateFrom, DATE_FORMAT),
                new FilterDataItem("date_to", "IP_Log.date <=", dateTo, DATE_FORMAT),
                new FilterDataItem("email", "USER.email =", email)
        };
        populateFilterItemsNonEmpty(items);
    }
}
