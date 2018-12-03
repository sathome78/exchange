package me.exrates.model.dto.filterData;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static me.exrates.model.dto.filterData.FilterDataItem.DATE_FORMAT;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DatesFilterData extends TableFilterData  {

    private String startDate;
    private String endDate;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("date_from", "date_creation >=", startDate, DATE_FORMAT),
                new FilterDataItem("date_to", "date_creation <=", endDate, DATE_FORMAT),
        };
        populateFilterItemsNonEmpty(items);
    }
}
