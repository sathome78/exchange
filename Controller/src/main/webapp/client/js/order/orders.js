/**
 * Created by Valk on 06.06.2016.
 */

function OrdersClass(currentCurrencyPair, cpData) {
    if (OrdersClass.__instance) {
        return OrdersClass.__instance;
    } else if (this === window) {
        return new OrdersClass(currentCurrencyPair, cpData);
    }
    OrdersClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForOrdersData;
    var refreshIntervalForOrdersData = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    /*    var tableSellId = "orders-sell-table";
     var tableBuyId = "orders-buy-table";*/
    var stopOrdersTableId = "stop-orders-table";
    var $ordersContainer = $('#orders');
    var ordersCurrencyPairSelector;
    /*    var tableSellPageSize = 5;
     var tableBuyPageSize = 5;*/
    var tableStopPageSize = 5;
    var myordersStatusForShow = 'OPENED';

    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(false, 1, null);
        that.getAndShowMySellAndBuyOrdersData();
    }

    /**/
    this.syncCurrencyPairSelector = function () {
        ordersCurrencyPairSelector.syncState('ALL', function (pairHasChanged) {
            if (pairHasChanged) {
                that.updateAndShowAll(false, 1, null);
                that.getAndShowMySellAndBuyOrdersData();
            }
        });
    };

    this.updateAndShowAll = function (refreshIfNeeded, page, direction) {
        /*        that.getAndShowSellOrdersData(refreshIfNeeded, page, direction);
         that.getAndShowBuyOrdersData(refreshIfNeeded, page, direction);*/
        that.getAndShowStopOrdersData(refreshIfNeeded, page, direction);
    };

    this.getAndShowMySellAndBuyOrdersData = function () {
        if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForOrdersData);
            timeOutIdForOrdersData = setTimeout(function () {
                that.getAndShowMySellAndBuyOrdersData();
            }, refreshIntervalForOrdersData);
            return;
        }
        if ($.fn.dataTable.isDataTable('#myOrdersTable')) {
            myOrdersTable.ajax.reload();
        } else {
            myOrdersTable = $('#myOrdersTable').DataTable({
                "ajax": {
                    "url": '/dashboard/myOrdersData',
                    "type": "GET",
                    "data": function(d){
                        d.tableType = myordersStatusForShow;
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
                        "data": "currencyPairName"
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
                        "data": 'id',
                        "render": function (data, type, row) {
                            return '<button id="'+data+'" class="table-button-block__button btn btn-danger button_delete_order"'
                                + '>' + localDelete + '</button>';
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

    this.getAndShowStopOrdersData = function (refreshIfNeeded, page, direction) {
        if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForOrdersData);
            timeOutIdForOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowStopOrdersData');
        }
        var $stopOrdersTable = $('#' + stopOrdersTableId).find('tbody');
        var url = '/dashboard/myOrdersData/' + stopOrdersTableId + '' +
            '?status=' + myordersStatusForShow + '' +
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
                    var $tmpl = $('#stop-orders-table_row').html().replace(/@/g, '%');
                    clearTable($stopOrdersTable);
                    data.forEach(function (e) {
                        $stopOrdersTable.append(tmpl($tmpl, e));
                    });
                    blink($stopOrdersTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.stop_orders-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.stop_orders-table__page').text());
                    $('.orders-sell-table__page').text(++p);
                }
                clearTimeout(timeOutIdForOrdersData);
                timeOutIdForOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForOrdersData);
            }
        });
    };

    /*    this.getAndShowSellOrdersData = function (refreshIfNeeded, page, direction) {
     if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
     clearTimeout(timeOutIdForOrdersData);
     timeOutIdForOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForOrdersData);
     return;
     }
     if (showLog) {
     console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrdersData');
     }
     var $ordersSellTable = $('#' + tableSellId).find('tbody');
     var url = '/dashboard/myOrdersData/' + tableSellId + '' +
     '?type=SELL' +
     '&status=' + myordersStatusForShow + '' +
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
     var $tmpl = $('#orders-sell-table_row').html().replace(/@/g, '%');
     clearTable($ordersSellTable);
     data.forEach(function (e) {
     $ordersSellTable.append(tmpl($tmpl, e));
     });
     blink($ordersSellTable.find('td:not(:first-child)'));
     }
     if (data.length > 0) {
     $('.orders-sell-table__page').text(data[0].page);
     } else if (refreshIfNeeded) {
     var p = parseInt($('.orders-sell-table__page').text());
     $('.orders-sell-table__page').text(++p);
     }
     clearTimeout(timeOutIdForOrdersData);
     timeOutIdForOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForOrdersData);
     }
     });
     };

     this.getAndShowBuyOrdersData = function (refreshIfNeeded, page, direction) {
     if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
     clearTimeout(timeOutIdForOrdersData);
     timeOutIdForOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForOrdersData);
     return;
     }
     if (showLog) {
     console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrdersData');
     }
     var $ordersBuyTable = $('#' + tableBuyId).find('tbody');
     var url = '/dashboard/myOrdersData/' + tableBuyId + '' +
     '?type=BUY' +
     '&status=' + myordersStatusForShow +
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
     var $tmpl = $('#orders-buy-table_row').html().replace(/@/g, '%');
     clearTable($ordersBuyTable);
     data.forEach(function (e) {
     $ordersBuyTable.append(tmpl($tmpl, e));
     });
     blink($ordersBuyTable.find('td:not(:first-child)'));
     }
     if (data.length > 0) {
     $('.orders-buy-table__page').text(data[0].page);
     } else if (refreshIfNeeded) {
     var p = parseInt($('.orders-buy-table__page').text());
     $('.orders-buy-table__page').text(++p);
     }
     clearTimeout(timeOutIdForOrdersData);
     timeOutIdForOrdersData = setTimeout(function () {
     that.updateAndShowAll(true);
     }, refreshIntervalForOrdersData);
     }
     });
     };*/

    this.createOrder = function (onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/create/',
            type: 'POST',
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    this.acceptOrder = function (ordersList, onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/accept/',
            contentType: "application/json; charset=utf-8",
            type: 'POST',
            data: ordersList.join(' '),
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    this.deleteOrder = function (onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/delete/',
            type: 'POST',
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    /*=====================================================*/
    (function init(currentCurrencyPair, cpData) {
        ordersCurrencyPairSelector = new CurrencyPairSelectorClass('orders-currency-pair-selector', currentCurrencyPair, cpData);
        ordersCurrencyPairSelector.init(onCurrencyPairChange, 'ALL');
        /**/
        /*        syncTableParams(tableSellId, tableSellPageSize, function (data) {
         that.getAndShowSellOrdersData();
         });
         syncTableParams(tableBuyId, tableBuyPageSize, function (data) {
         that.getAndShowBuyOrdersData();
         });*/
        syncTableParams(stopOrdersTableId, tableStopPageSize, function (data) {
            that.getAndShowStopOrdersData();
        });
        /**/
        $('#orders-sell-table').on('click', '.button_delete_order', submitOrderDeleting);
        $('#orders-buy-table').on('click', '.button_delete_order', submitOrderDeleting);
        $('#myOrdersTable').on('click', '.button_delete_order', submitOrderDeleting);
        $('#stop-orders-table').on('click', '.button_delete_stop_order', submitOrderDeleting);
        $('#order-delete-confirm__submit').on('click', deletingOrder);

        /*
         $('.orders-sell-table__backward').on('click', function (e) {
         e.preventDefault();
         that.getAndShowSellOrdersData(true, null, 'BACKWARD');
         });
         $('.orders-sell-table__forward').on('click', function (e) {
         e.preventDefault();
         that.getAndShowSellOrdersData(true, null, 'FORWARD');
         });
         $('.orders-buy-table__backward').on('click', function (e) {
         e.preventDefault();
         that.getAndShowBuyOrdersData(true, null, 'BACKWARD');
         });
         $('.orders-buy-table__forward').on('click', function (e) {
         e.preventDefault();
         that.getAndShowBuyOrdersData(true, null, 'FORWARD');
         });
         */
        $('.stop_orders-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowStopOrdersData(true, null, 'BACKWARD');
        });
        $('.stop_orders-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowStopOrdersData()(true, null, 'FORWARD');
        });
    })(currentCurrencyPair, cpData);

    /*PREPARE DATA FOR MODAL DIALOG FOR DELETING ORDER ... */
    function submitOrderDeleting() {
        var baseType = $(this).data('basetype');
        var orderId = $(this).attr("id");
        var data = {orderId: orderId};
        baseType ? data.baseType = baseType : data.baseType = 1;
        showOrderDeleteDialog(data);
    }

    /*...PREPARE DATA FOR MODAL DIALOG FOR DELETING ORDER */

    /*MODAL DIALOG FOR DELETING ORDER ... */
    function showOrderDeleteDialog(data) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/submitdelete/' + data.orderId +'?baseType=' + data.baseType,
            type: 'POST',
            success: function (data) {
                $('#order-delete-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                $('#order-delete-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                $('#order-delete-confirm__modal').find('#amount').val(data.amount);
                $('#order-delete-confirm__modal').find('#exrate').val(data.exrate);
                $('#order-delete-confirm__modal').find('#total').val(data.total);
                /**/
                $('#order-delete-confirm__modal').modal();
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });
    }

    /*... MODAL DIALOG FOR DELETING ORDER*/

    /*CALL DELETING THE ORDER AND CONTROL RESULT ... */
    function deletingOrder(event) {
        event.preventDefault();
        $('#order-delete-confirm__modal').one('hidden.bs.modal', function (e) {
            that.deleteOrder(onDeleteOrderSuccess, onDeleteOrderError)
        });
        $('#order-delete-confirm__modal').modal('hide');
    }

    function onDeleteOrderSuccess(data) {
        that.updateAndShowAll();
        that.getAndShowMySellAndBuyOrdersData();
        successNoty(data.result);
    }

    function onDeleteOrderError(jqXHR, textStatus, errorThrown) {
    }

    /*... CALL DELETING THE ORDER AND CONTROL RESULT*/
}
