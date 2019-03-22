/**
 * Created by Valk on 27.06.2016.
 */

function MyStatementsClass(currentCurrencyPair, cpData) {
    if (MyStatementsClass.__instance) {
        return MyStatementsClass.__instance;
    } else if (this === window) {
        return new MyStatementsClass(currentCurrencyPair, cpData);
    }
    MyStatementsClass.__instance = this;
    /**/
    var that = this;
    var timeOutIdForMyStatementsData;
    var refreshIntervalForMyStatementsData = (1) * 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $mystatementsContainer = $('#mystatements');
    var tableId = "mystatement-table";
    var mystatementsCurrencyPairSelector;
    var tablePageSize = 19;
    /**/
    this.walletId = null;
    /**/
    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(currentCurrencyPair);
    }

    this.syncCurrencyPairSelector = function () {
        mystatementsCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowMyStatementsData(refreshIfNeeded);
    };


    this.getAndShowMyStatementsData = function (refreshIfNeeded, page, direction) {
        if ($mystatementsContainer.hasClass('hidden') || !windowIsActive) {
            if (refreshIntervalForMyStatementsData) {
                clearTimeout(timeOutIdForMyStatementsData);
                timeOutIdForMyStatementsData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyStatementsData);
            }
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyStatementsData');
        }
        var $mystatementsTable = $('#' + tableId).find('tbody');
        var url = '/dashboard/myStatementData/' + tableId + '/'+that.walletId +
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
                    var $tmpl = $('#mystatement-table_row').html().replace(/@/g, '%');
                    clearTable($mystatementsTable);
                    data.forEach(function (e) {
                        $mystatementsTable.append(tmpl($tmpl, e));
                    });
                    blink($mystatementsTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.mystatement-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.mystatement-table__page').text());
                    $('.mystatement-table__page').text(++p);
                }
                if (refreshIntervalForMyStatementsData) {
                    clearTimeout(timeOutIdForMyStatementsData);
                    timeOutIdForMyStatementsData = setTimeout(function () {
                        that.updateAndShowAll(true);
                    }, refreshIntervalForMyStatementsData);
                }
            }
        });
    };

    /*=====================================================*/
    (function init(currentCurrencyPair, cpData) {
        if(currentCurrencyPair) {
            mystatementsCurrencyPairSelector = new CurrencyPairSelectorClass('mystatement-currency-pair-selector', currentCurrencyPair, cpData);
            mystatementsCurrencyPairSelector.init(onCurrencyPairChange, 'ALL');
        }
        /**/
        syncTableParams(tableId, tablePageSize, function (data) {
            //NOP
        });
        /**/
        $('#balance-page').on('click', '.wallet-mystatement-button', function (e) {
            e.preventDefault();

            var row_index = $(this).closest("tr").index() - 2;

            that.walletId = $('.mywallet-item-id')[row_index].innerText;
            if (!e.ctrlKey) {
                showPage('mystatement');
                that.updateAndShowAll();
            } else {
                window.open('/dashboard?startupPage=mystatement', '_blank');
                return false;
            }
        });
        /**/
        $('#mystatement').on('click', '.source-type-button', function (e) {
            e.preventDefault();
            var sourceTypeId = $(this).parent().data('sourcetypeid');
            var sourceId = $(this).parent().data('sourceid');
            switch (sourceTypeId) {
                case 'ORDER': {
                    $.ajax({
                        url: '/order/orderinfo?id=' + sourceId,
                        type: 'GET',
                        success: function (data) {
                            $("#order-info-id").val(data.id);
                            $("#order-info-dateCreation").val(data.dateCreation);
                            $("#order-info-dateAcception").val(data.dateAcception ? data.dateAcception : '-');
                            $("#order-info-currencyPairName").val(data.currencyPairName);
                            $("#order-info-orderStatusName").val(data.orderStatusName.toUpperCase());
                            $("#order-info-orderTypeName").val(data.orderTypeName);
                            $("#order-info-exrate").val(data.exrate+ ' ' + data.currencyConvertName);
                            $("#order-info-amountBase").val(data.amountBase + ' ' + data.currencyBaseName);
                            $("#order-info-amountConvert").val(data.amountConvert + ' ' + data.currencyConvertName);
                            $("#order-info-orderCreatorEmail").val(data.orderCreatorEmail);
                            $("#order-info-orderAcceptorEmail").val(data.orderAcceptorEmail?data.orderAcceptorEmail:'-');
                            $("#order-info-transactionCount").val(data.transactionCount);
                            $("#order-info-companyCommission").val(data.companyCommission?data.companyCommission + ' ' + data.currencyConvertName:'-');
                            /**/
                            $('#order-info-modal').modal();
                        }
                    });
                    break;
                }
            }
        });
        /**/
        $('.mystatement-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowMyStatementsData(true, null, 'BACKWARD');
        });
        $('.mystatement-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowMyStatementsData(true, null, 'FORWARD');
        });
    })(currentCurrencyPair, cpData);
}
