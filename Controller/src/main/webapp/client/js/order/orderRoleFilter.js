function OrderRoleFilterClass(filteringEnabled, onChange) {
    var that = this;
    this.filteringEnabled = filteringEnabled;


    (function init() {
        var $rowFilterBox = $('#order-row-filter-box');
        if ($($rowFilterBox).length > 0) {
            $($rowFilterBox).prop( "checked", that.filteringEnabled);
            $($rowFilterBox).on('change', function () {
                that.filteringEnabled = $($rowFilterBox).prop("checked");
                syncCurrentParams(null, null, null, null, that.filteringEnabled);
                if (onChange) {
                    onChange();
                }
            });
            $($rowFilterBox).onoff();
            syncCurrentParams(null, null, null, null, that.filteringEnabled)
        }
    })();
}