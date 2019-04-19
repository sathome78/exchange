var ordersSellDataTable;
var ordersBuyDataTable;
var stopOrdersDataTable;

$(function () {
    var myordersStatusForShow = 'Closed';

    $('#orders-tables-init').click(update);


    function update() {
        updateOrdersSellTableClosed();
        updateOrdersBuyTableClosed();
        updateStopOrdersTableClosed();
    }

    $('#currency-pair-selector').change('click', function () {
        update();
    });

    $('#myorders-button-deal').click(function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Closed';
        update();
    });
    $('#myorders-button-opened').click(function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Opened';
        update();
    });
    $('#myorders-button-cancelled').click(function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Cancelled';
        update();
    });

    function updateOrdersSellTableClosed() {
        var ordersSellTable = $('#ordersSellTable');
        var ordersUrl = '/2a8fy7b07dxe44/orders';
        if ($.fn.dataTable.isDataTable(ordersSellTable)) {
            ordersSellDataTable = $(ordersSellTable).DataTable();
            ordersSellDataTable.ajax.url(ordersUrl).load();
        } else {
            var userId = $("#user-id").val();
            var currencyPairId = $('#currency-pair-selector').val();

            ordersSellDataTable = $(ordersSellTable).DataTable({
                "ajax": {
                    "url": ordersUrl,
                    "type": "GET",
                    "data": function (d) {
                        d.id = userId;
                        d.tableType = 'ordersSell' + myordersStatusForShow;
                        d.currencyPairId = currencyPairId;
                    },
                    "dataSrc": "data"
                },
                "processing": true,
                "serverSide": true,
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
                        "render": function (data, type, row) {
                            if (myordersStatusForShow === 'Closed') {
                                return data;
                            } else {
                                return row.dateStatusModification;
                            }
                        }
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "destroy": true
            });
        }
    }

    function updateOrdersBuyTableClosed() {
        var ordersBuyTable = $('#ordersBuyTable');
        var ordersUrl = '/2a8fy7b07dxe44/orders';

        if ($.fn.dataTable.isDataTable(ordersBuyTable)) {
            ordersBuyDataTable = $(ordersBuyTable).DataTable();
            ordersBuyDataTable.ajax.url(ordersUrl).load();
        } else {
            var userId = $("#user-id").val();
            var currencyPairId = $('#currency-pair-selector').val();

            ordersBuyDataTable = $(ordersBuyTable).DataTable({
                "ajax": {
                    "url": ordersUrl,
                    "type": "GET",
                    "data": function (d) {
                        d.id = userId;
                        d.tableType = 'ordersBuy' + myordersStatusForShow;
                        d.currencyPairId = currencyPairId;
                    },
                    "dataSrc": "data"
                },
                "processing": true,
                "serverSide": true,
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
                        "render": function (data, type, row) {
                            if (myordersStatusForShow === 'Closed') {
                                return data;
                            } else {
                                return row.dateStatusModification;
                            }
                        }
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "destroy": true
            });
        }
    }

    function updateStopOrdersTableClosed() {
        var stopOrdersTable = $('#stopOrdersTable');
        var ordersUrl = '/2a8fy7b07dxe44/orders';

        if ($.fn.dataTable.isDataTable(stopOrdersTable)) {
            stopOrdersDataTable = $(stopOrdersTable).DataTable();
            stopOrdersDataTable.ajax.url(ordersUrl).load();
        } else {
            var userId = $("#user-id").val();
            var currencyPairId = $('#currency-pair-selector').val();

            stopOrdersDataTable = $(stopOrdersTable).DataTable({
                "ajax": {
                    "url": ordersUrl,
                    "type": "GET",
                    "data": function (d) {
                        d.id = userId;
                        d.tableType = 'stopOrders' + myordersStatusForShow;
                        d.currencyPairId = currencyPairId;
                    },
                    "dataSrc": "data"
                },
                "processing": true,
                "serverSide": true,
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
                        "data": "operationType"
                    },
                    {
                        "data": "amountBase"
                    },
                    {
                        "data": "exExchangeRate"
                    },
                    {
                        "data": "stopRate"
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
                        "data": "dateStatusModification",
                        "render": function (data, type, row) {
                            if (myordersStatusForShow === 'Closed' || myordersStatusForShow === 'Cancelled') {
                                return data;
                            } else {
                                return "";
                            }
                        }
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "destroy": true
            });
        }
    }
});

