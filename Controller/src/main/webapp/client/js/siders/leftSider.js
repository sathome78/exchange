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

    this.getStatisticsForMyWallets = function (refreshIfNeeded) {
        if (!windowIsActive) {
            clearTimeout(timeOutIdForStatisticsForMyWallets);
            timeOutIdForStatisticsForMyWallets = setTimeout(function () {
                that.getStatisticsForMyWallets(true);
            }, refreshIntervalForStatisticsForMyWallets);
            return;
        }
        $mvFilter = $('#my-wallets-filter');
        if($mvFilter.val() === undefined || $mvFilter.val().length > 0 ) {
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
        if ($row.length) {
            $row.replaceWith(tmpl($tmpl, data));
        } else {
            $currencyTable.append(tmpl($tmpl, data));
        }
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
        $('#exclude-zero-statbalances').click(function(e) {
            excludeZero();
            if (e.target.checked) {
                localStorage.setItem('statWalletsCheckbox', true);
            } else {
                localStorage.setItem('statWalletsCheckbox', false);
            }
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

    function excludeZero() {
        var exclZeroes = $('#exclude-zero-statbalances').prop('checked');
        // Declare variables
        var input, filter, table, tr, td1, tdIn1, td2, description, i, activeBalance;
        input = document.getElementById("my-wallets-filter");
        if (input == null) {
            return;
        }
        filter = input.value.toUpperCase();
        table = document.getElementById("mywallets_table");
        tr = table.getElementsByTagName("tr");

        // Loop through all table rows, and hide those who don't match the search query
        for (i = 0; i < tr.length; i++) {
            td1 = tr[i].getElementsByTagName("td")[0];
            td2 = tr[i].getElementsByTagName("td")[1];
            if (td1 || td2 || tdIn1) {
                activeBalance =  parseFloat(td2.innerText) || 0;
                if (td1.innerText.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                    if (exclZeroes && activeBalance === 0.0) {
                        tr[i].style.display = "none";
                    }
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
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

function setMyWalletsFilter() {
    var exclZeroes = $('#exclude-zero-statbalances').prop('checked');
    // Declare variables
    var input, filter, table, tr, td1, td2,  i, activeBalance;
    input = document.getElementById("my-wallets-filter");
    filter = input.value.toUpperCase();
    table = document.getElementById("mywallets_table");
    tr = table.getElementsByTagName("tr");

    // Loop through all table rows, and hide those who don't match the search query
    for (i = 0; i < tr.length; i++) {
        td1 = tr[i].getElementsByTagName("td")[0];
        td2 = tr[i].getElementsByTagName("td")[1];
        if (td1 || td2 ) {
            activeBalance =  parseFloat(td2.innerText) || 0;
            if (td1.innerText.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
                if (exclZeroes && activeBalance === 0.0) {
                    tr[i].style.display = "none";
                }
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}
