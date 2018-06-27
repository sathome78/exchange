/**
 * Created by Valk on 05.06.2016.
 */

function LeftSiderClass() {
    if (LeftSiderClass.__instance) {
        return LeftSiderClass.__instance;
    } else if (this === window) {
        return new LeftSiderClass(currentCurrencyPair);
    }
    LeftSiderClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForStatisticsForMyWallets;
    var refreshIntervalForStatisticsForMyWallets = 30000 * REFRESH_INTERVAL_MULTIPLIER;
    var timeOutIdForStatisticsForAllCurrencies;
    var refreshIntervalForStatisticsForAllCurrencies = 10000 * REFRESH_INTERVAL_MULTIPLIER;
    var $currencyTable = $('#currency_table').find('tbody');
    /**/
    var showLog = false;

    var onWalletStatisticRefresh;

    this.getStatisticsForMyWallets = function (refreshIfNeeded) {
        if (!windowIsActive) {
            clearTimeout(timeOutIdForStatisticsForMyWallets);
            timeOutIdForStatisticsForMyWallets = setTimeout(function () {
                that.getStatisticsForMyWallets(true);
            }, refreshIntervalForStatisticsForMyWallets);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getStatisticsForMyWallets');
        }
        var $mywalletsTable = $('#mywallets_table').find('tbody');
        var url = '/dashboard/myWalletsStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.mapWallets.length == 0 || data.mapWallets[0].needRefresh) {
                    var $tmpl = $('#mywallets_table_row').html().replace(/@/g, '%');
                    clearTable($mywalletsTable);
                    data.mapWallets.forEach(function (e) {
                        $mywalletsTable.append(tmpl($tmpl, e));
                    });
                    $('#total-sum-usd').html(numbro(data.sumTotalUSD).format('0.00'));
                    blink($mywalletsTable);
                    if (onWalletStatisticRefresh) {
                        onWalletStatisticRefresh();
                    }
                }
                clearTimeout(timeOutIdForStatisticsForMyWallets);
                timeOutIdForStatisticsForMyWallets = setTimeout(function () {
                    that.getStatisticsForMyWallets(true);
                }, refreshIntervalForStatisticsForMyWallets);
            }
        });
    };

    that.updateStatisticsForAllCurrencies = function (data) {
        var $tmpl = $('#currency_table_row').html().replace(/@/g, '%');
        clearTable($currencyTable);
        data.forEach(function (e) {
            $currencyTable.append(tmpl($tmpl, e));
        });
        blink($('#currency_table'));
        setPairFilter();
    };

    that.updateStatisticsForCurrency = function (data) {
        var $tmpl = $('#currency_table_row').html().replace(/@/g, '%');
        var sel = 'stat_' + data.currencyPairName;
        var $row = $(document.getElementById(sel));
        $row.replaceWith(tmpl($tmpl, data));
        blink($row);
        setPairFilter();
    };


    this.setOnWalletsRefresh = function(refreshCallback) {
        onWalletStatisticRefresh = refreshCallback;
    };

    /*===========================================================*/
    (function init() {
        clearTimeout(timeOutIdForStatisticsForAllCurrencies);
        $.ajax({
            url: '/dashboard/firstentry',
            type: 'GET',
            success: function () {
              /*  that.getStatisticsForAllCurrencies();*/
            }
        });
        that.getStatisticsForMyWallets();
        $('#refferal-generate').on('click', generateReferral);
        $('#refferal-copy').on('click', function () {
            selectAndCopyText($('#refferal-reference'));
        });
        $('#pair-filter').on('keyup', function (e) {
            setPairFilter();
        });
        generateReferral();
    })();

    function setPairFilter() {
        var str = $('#pair-filter').val().toUpperCase();
        $('#currency_table').find('td:first-child').each(function (idx) {
            var pair = $(this).text();
            if (!pair || pair.indexOf(str) != -1) {
                $(this).parent().removeClass('hidden');
            } else {
                $(this).parent().addClass('hidden');
            }
        })
    }

    function generateReferral() {
        $.ajax('/generateReferral', {
            method: 'get'
        }).done(function (e) {
            $('#refferal-reference').html(e['referral']);
        });
        blink($('#refferal-reference'));
    }
}
