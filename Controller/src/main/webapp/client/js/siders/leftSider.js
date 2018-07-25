/**
 * Created by Valk on 05.06.2016.
 */

function LeftSiderClass(type) {
    if (LeftSiderClass.__instance) {
        return LeftSiderClass.__instance;
    } else if (this === window) {
        return new LeftSiderClass(currentCurrencyPair, type);
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

    this.getStatisticsForMyWallets = function (refreshIfNeeded, thisIco) {
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
        if (!type) {
            type = 'MAIN'
        }
        var url = '/dashboard/myWalletsStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false') + '&type=' + type;
        console.log(thisIco);
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
                setMyWalletsFilter();
                excludeZero();
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
    (function init(type) {
        clearTimeout(timeOutIdForStatisticsForAllCurrencies);
        $.ajax({
            url: '/dashboard/firstentry',
            type: 'GET',
            success: function () {
              /*  that.getStatisticsForAllCurrencies();*/
            }
        });
        that.getStatisticsForMyWallets(undefined, type);
        $('#refferal-generate').on('click', generateReferral);
        $('#refferal-copy').on('click', function () {
            selectAndCopyText($('#refferal-reference'));
        });
        $('#pair-filter').on('keyup', function (e) {
            setPairFilter();
        });
        $('#my-wallets-filter').on('keyup', function (e) {
            setMyWalletsFilter();
        });
        $('#exclude-zero-statbalances').change(function() {
            excludeZero();
        });
        generateReferral();
    })(type);

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

    function setMyWalletsFilter() {
        var str = $('#my-wallets-filter').val().toUpperCase();
        $('#mywallets_table').find('td:first-child').each(function (idx) {
            var currency = $(this).text().toUpperCase();
            if (!currency || currency.indexOf(str) != -1) {
                $(this).parent().removeClass('hidden');
            } else {
                $(this).parent().addClass('hidden');
            }
        })
    }

    function excludeZero() {
        var excludeZeroes = $('#exclude-zero-statbalances').prop('checked');
        $('#mywallets_table').find('td:nth-child(2)').each(function (idx) {
            var currency = $(this).text();
            if (excludeZeroes && currency === '0.00000') {
                $(this).parent().addClass('hidden');
            } else {
                $(this).parent().removeClass('hidden');
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
