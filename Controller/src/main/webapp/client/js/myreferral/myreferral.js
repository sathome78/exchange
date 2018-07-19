/**
 * Created by Valk on 27.06.2016.
 */

function MyReferralClass(currentCurrencyPair, cpData) {
    if (MyReferralClass.__instance) {
        return MyReferralClass.__instance;
    } else if (this === window) {
        return new MyReferralClass(currentCurrencyPair, cpData);
    }
    MyReferralClass.__instance = this;
    /**/
    var that = this;
    var timeOutIdForMyReferralData;
    var refreshIntervalForMyReferralData = 30000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $myreferralContainer = $('#myreferral');
    var tableId = "myreferral-table";
    var myreferralCurrencyPairSelector;
    var tablePageSize = 5;

    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(currentCurrencyPair);
    }

    this.syncCurrencyPairSelector = function () {
        if (myreferralCurrencyPairSelector) {
            myreferralCurrencyPairSelector.syncState();
        }
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowMyReferralData(refreshIfNeeded);
    };


    this.getAndShowMyReferralData = function (refreshIfNeeded, page, direction) {
        if ($myreferralContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyReferralData);
            timeOutIdForMyReferralData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForMyReferralData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyReferralData');
        }
        var $myreferralTable = $('#' + tableId).find('tbody');
        var url = '/dashboard/myReferralData/' + tableId + '' +
            '?page=' + (page ? page : '') +
            '&direction=' + (direction ? direction : '') +
            '&refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#myreferral-table_row').html().replace(/@/g, '%');
                    $myreferralTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $myreferralTable.append(tmpl($tmpl, e));
                    });
                    blink($myreferralTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.myreferral-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.myreferral-table__page').text());
                    $('.myreferral-table__page').text(++p);
                }
                clearTimeout(timeOutIdForMyReferralData);
                timeOutIdForMyReferralData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyReferralData);
            }
        });
    };

    /*=====================================================*/
    (function init(currentCurrencyPair, cpData) {
        if (currentCurrencyPair) {
            myreferralCurrencyPairSelector = new CurrencyPairSelectorClass('myreferral-currency-pair-selector', currentCurrencyPair, cpData);
            myreferralCurrencyPairSelector.init(onCurrencyPairChange);
        }
        /**/
        syncTableParams(tableId, tablePageSize, function (data) {
            /*that.getAndShowMyReferralData();*/
        });
        /**/
        $('.myreferral-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowMyReferralData(true, null, 'BACKWARD');
        });
        $('.myreferral-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowMyReferralData(true, null, 'FORWARD');
        });
    })(currentCurrencyPair, cpData);
}
