/**
 * Created by Valk on 06.06.2016.
 */

function MyOrdersClass(currentCurrencyPair, cpData) {
    if (MyOrdersClass.__instance) {
        return MyOrdersClass.__instance;
    } else if (this === window) {
        return new MyOrdersClass(currentCurrencyPair, cpData);
    }
    MyOrdersClass.__instance = this;
    /**/
    var that = this;
    var timeOutIdForMyOrdersData;
    var refreshIntervalForMyOrdersData = 30000*REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    /*    var tableSellId = "myorders-sell-table";
     var tableBuyId = "myorders-buy-table";*/
    var tableStopId = "myorders-stop-table";
    var $myordersContainer = $('#myorders');
    var myordersCurrencyPairSelector;
    var myordersStatusForShow;
    var myOrdersScope;
    /*    var tableSellPageSize = 5;
     var tableBuyPageSize = 5;*/
    var tableStopPageSize = 5;
    var fieldVisibleForOpenStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_dcrt',
        'myo_crpr',
        'myo_rate',
        'myo_totl',
        'myo_comm',
        'myo_amcm',
        'myo_delt',
        'myo_typ',
        'myo_strate'];
    var fieldVisibleForCancelledStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_crpr',
        'myo_amnt',
        'myo_rate',
        'myo_totl',
        'myo_cnsl',
        'myo_typ',
        'myo_strate'];
    var fieldVisibleForClosedStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_crpr',
        'myo_amnt',
        'myo_rate',
        'myo_totl',
        'myo_comm',
        'myo_amcm',
        'myo_deal',
        'myo_typ',
        'myo_strate'];


    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(false, 1, null);
        that.getAndShowMySellAndBuyOrdersData();
    }

    this.syncCurrencyPairSelector = function () {
        myordersCurrencyPairSelector.syncState(function (pairHasChanged) {
            if (pairHasChanged) {
                that.updateAndShowAll(false, 1, null);
                that.getAndShowMySellAndBuyOrdersData();
            }
        });
    };

    this.updateAndShowAll = function (refreshIfNeeded, page, direction) {
        /*        that.getAndShowMySellOrdersData(refreshIfNeeded, page, direction);
         that.getAndShowMyBuyOrdersData(refreshIfNeeded, page, direction);*/
        that.getAndShowMyStopOrdersData(refreshIfNeeded, page, direction);
    };

    this.getAndShowMyStopOrdersData = function (refreshIfNeeded, page, direction) {
        if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyOrdersData);
            timeOutIdForMyOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForMyOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMySellOrdersData');
        }
        var $myordersStopTable = $('#'+tableStopId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableStopId + '' +
            '?status=' + myordersStatusForShow + '' +
            '&scope=' + myOrdersScope +
            '&page=' + (page ? page : '') +
            '&direction=' + (direction ? direction : '') +
            '&baseType=STOP_LIMIT' +
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
                    $('#' + tableStopId).addClass('hidden');
                    var $tmpl = $('#myorders-stop-table_row').html().replace(/@/g, '%');
                    clearTable($myordersStopTable);
                    data.forEach(function (e) {
                        $myordersStopTable.append(tmpl($tmpl, e));
                    });
                    showFields(myordersStatusForShow, tableStopId);
                    $('#' + tableStopId).removeClass('hidden');
                    blink($myordersStopTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.myorders-stop-table__page').text(data[0].page);
                } else if (refreshIfNeeded){
                    var p = parseInt($('.myorders-stop-table__page').text());
                    $('.myorders-stop-table__page').text(++p);
                }
                clearTimeout(timeOutIdForMyOrdersData);
                timeOutIdForMyOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyOrdersData);
            }
        });
    };

    this.getAndShowMySellAndBuyOrdersData = function () {
        if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyOrdersData);
            timeOutIdForMyOrdersData = setTimeout(function () {
                that.getAndShowMySellAndBuyOrdersData();
            }, refreshIntervalForMyOrdersData);
            return;
        }
        if ($.fn.dataTable.isDataTable('#myHistoryOrdersTable')) {
            myHistoryOrdersTable.ajax.reload();
        } else {
            myHistoryOrdersTable = $('#myHistoryOrdersTable').DataTable({
                "ajax": {
                    "url": '/dashboard/myOrdersData',
                    "type": "GET",
                    "data": function(d){
                        d.tableType = myordersStatusForShow;
                        d.scope = myOrdersScope;
                    },
                    "dataSrc": ""
                },
                "deferRender": true,
                "paging": true,
                "info": true,
                "columns": [
                    {
                        "data": "id"
                    },
                    {
                        "data": "dateCreation"
                    },
                    {
                        "data": "currencyPairName",
                    },
                    {
                        "data": "operationType",
                        "render": function (data, type, row) {
                            if (data == "SELL" ) {
                                return '<p style="color: red">'+data+'</p>';

                            } else if (data == "BUY" ){
                                return '<p style="color: green">'+data+'</p>';
                            }
                            return data;
                        }
                    },
                    {
                        "data": "amountBase"
                    },
                    {
                        "data": "exExchangeRate"
                    },
                    {
                        "data": "amountConvert"
                    },
                    {
                        "data": "commissionFixedAmount"
                    },
                    {
                        "data": "amountWithCommission"
                    },
                    {
                        "data": "dateAcception",
                        "render": function (data, type, row){
                            if (myordersStatusForShow == 'CLOSED') {
                                return data;
                            }else {
                                return row.dateStatusModification;
                            }
                            return data;
                        }
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "destroy" : true
            });

        }
    }

    /*    this.getAndShowMySellOrdersData = function (refreshIfNeeded, page, direction) {
     if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
     clearTimeout(timeOutIdForMyOrdersData);
     timeOutIdForMyOrdersData = setTimeout(function () {
     that.getAndShowMySellAndBuyOrdersData();
     }, refreshIntervalForMyOrdersData);
     return;
     }
     if (showLog) {
     console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMySellOrdersData');
     }
     var $myordersSellTable = $('#'+tableSellId).find('tbody');
     var url = '/dashboard/myOrdersData/' + tableSellId + '' +
     '?type=SELL&status=' + myordersStatusForShow + '' +
     '&scope=' + myOrdersScope +
     '&page=' + (page ? page : '') +
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
     $('#' + tableSellId).addClass('hidden');
     var $tmpl = $('#myorders-sell-table_row').html().replace(/@/g, '%');
     clearTable($myordersSellTable);
     data.forEach(function (e) {
     $myordersSellTable.append(tmpl($tmpl, e));
     });
     showFields(myordersStatusForShow, tableSellId);
     $('#' + tableSellId).removeClass('hidden');
     blink($myordersSellTable.find('td:not(:first-child)'));
     }
     if (data.length > 0) {
     $('.myorders-sell-table__page').text(data[0].page);
     } else if (refreshIfNeeded){
     var p = parseInt($('.myorders-sell-table__page').text());
     $('.myorders-sell-table__page').text(++p);
     }
     clearTimeout(timeOutIdForMyOrdersData);
     timeOutIdForMyOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForMyOrdersData);
     }
     });
     };

     this.getAndShowMyBuyOrdersData = function (refreshIfNeeded, page, direction) {
     if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
     clearTimeout(timeOutIdForMyOrdersData);
     timeOutIdForMyOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForMyOrdersData);
     return;
     }
     if (showLog) {
     console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyBuyOrdersData');
     }
     var $myordersBuyTable = $('#'+tableBuyId).find('tbody');
     var url = '/dashboard/myOrdersData/' + tableBuyId + '' +
     '?type=BUY&status=' + myordersStatusForShow + '' +
     '&scope=' + myOrdersScope +
     '&page=' + (page ? page : '') +
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
     $('#' + tableBuyId).addClass('hidden');
     var $tmpl = $('#myorders-buy-table_row').html().replace(/@/g, '%');
     clearTable($myordersBuyTable);
     data.forEach(function (e) {
     $myordersBuyTable.append(tmpl($tmpl, e));
     });
     showFields(myordersStatusForShow, tableBuyId);
     $('#' + tableBuyId).removeClass('hidden');
     blink($myordersBuyTable.find('td:not(:first-child)'));
     }
     if (data.length > 0) {
     $('.myorders-buy-table__page').text(data[0].page);
     } else if (refreshIfNeeded){
     var p = parseInt($('.myorders-buy-table__page').text());
     $('.myorders-buy-table__page').text(++p);
     }
     clearTimeout(timeOutIdForMyOrdersData);
     timeOutIdForMyOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForMyOrdersData);
     }
     });
     };*/

    function showFields(myordersStatusForShow, tableId) {
        var fieldsList;
        switch (myordersStatusForShow) {
            case 'OPENED':
            {
                fieldsList = fieldVisibleForOpenStatus;
                break;
            }
            case 'CANCELLED':
            {
                fieldsList = fieldVisibleForCancelledStatus;
                break;
            }
            case 'CLOSED':
            {
                fieldsList = fieldVisibleForClosedStatus;
                break;
            }
        }
        $('#' + tableId).find('tr th').addClass('hidden');
        $('#' + tableId).find('tr td').addClass('hidden');
        fieldsList.forEach(function (e) {
            $('#' + tableId).find('.' + e).removeClass('hidden');
        })
    }
    /*=====================================================*/
    (function init (currentCurrencyPair, cpData) {
        myordersCurrencyPairSelector = new CurrencyPairSelectorClass('myorders-currency-pair-selector', currentCurrencyPair, cpData);
        myordersCurrencyPairSelector.init(onCurrencyPairChange);
        myordersStatusForShow = 'CLOSED';
        myOrdersScope = 'ALL';
        $('#myorders-button-deal').addClass('active');
        /**/
        $('#myorders-button-cancelled').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CANCELLED';
            myOrdersScope = '';
            that.updateAndShowAll(false, 1, null);
            that.getAndShowMySellAndBuyOrdersData();
        });
        $('#myorders-button-mine').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CLOSED';
            myOrdersScope = 'MINE';
            that.updateAndShowAll(false, 1, null);
            that.getAndShowMySellAndBuyOrdersData();
        });
        $('#myorders-button-accepted').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CLOSED';
            myOrdersScope = 'ACCEPTED';
            that.updateAndShowAll(false, 1, null);
            that.getAndShowMySellAndBuyOrdersData();
        });
        $('#myorders-button-deal').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CLOSED';
            myOrdersScope = 'ALL';
            that.updateAndShowAll();
            that.getAndShowMySellAndBuyOrdersData();
        });
        /*
         syncTableParams(tableSellId, tableSellPageSize, function (data) {
         /!*that.getAndShowMySellOrdersData();*!/
         });
         syncTableParams(tableBuyId, tableBuyPageSize, function (data) {
         /!*that.getAndShowMyBuyOrdersData();*!/
         });
         */
        syncTableParams(tableStopId, tableStopPageSize, function (data) {
            /*that.getAndShowMyStopOrdersData();*/
        });
        /*
         $('.myorders-sell-table__backward').on('click', function(e){
         e.preventDefault();
         that.getAndShowMySellOrdersData(true, null, 'BACKWARD');
         });
         $('.myorders-sell-table__forward').on('click', function(e){
         e.preventDefault();
         that.getAndShowMySellOrdersData(true, null, 'FORWARD');
         });
         $('.myorders-buy-table__backward').on('click', function(e){
         e.preventDefault();
         that.getAndShowMyBuyOrdersData(true, null, 'BACKWARD');
         });
         $('.myorders-buy-table__forward').on('click', function(e){
         e.preventDefault();
         that.getAndShowMyBuyOrdersData(true, null, 'FORWARD');
         });
         */
        $('.myorders-stop-table__backward').on('click', function(e){
            e.preventDefault();
            that.getAndShowMyStopOrdersData(true, null, 'BACKWARD');
        });
        $('.myorders-stop-table__forward').on('click', function(e){
            e.preventDefault();
            that.getAndShowMyStopOrdersData(true, null, 'FORWARD');
        });
    })(currentCurrencyPair, cpData);
}