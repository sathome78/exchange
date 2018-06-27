

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

    $('#myorders-button-deal').addClass('active');
    /**/
    $('#myorders-button-opened').on('click', function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Opened';
        update();
    });
    $('#myorders-button-cancelled').on('click', function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Cancelled';
        update();
    });
    $('#myorders-button-deal').on('click', function () {
        $('.myorders__button').removeClass('active');
        $(this).addClass('active');
        myordersStatusForShow = 'Closed';
        update();
    });

    function updateOrdersSellTableClosed() {
        if ($.fn.dataTable.isDataTable('#ordersSellTable')) {
            ordersSellTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            ordersSellTable = $('#ordersSellTable').DataTable({
                "ajax": {
                    "url": '/2a8fy7b07dxe44/orders',
                    "type": "GET",
                    "data": function(d){
                        d.id = id;
                        d.tableType = 'ordersSell' + myordersStatusForShow;
                        d.currencyPairId = $('#currency-pair-selector').val();
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
                            if (myordersStatusForShow == 'Closed') {
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

    function updateOrdersBuyTableClosed() {
        if ($.fn.dataTable.isDataTable('#ordersBuyTable')) {
            ordersBuyTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            ordersBuyTable = $('#ordersBuyTable').DataTable({
                "ajax": {
                    "url": '/2a8fy7b07dxe44/orders',
                    "type": "GET",
                    "data": function(d){
                        d.id = id;
                        d.tableType = 'ordersBuy' + myordersStatusForShow;
                        d.currencyPairId = $('#currency-pair-selector').val();
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
                            if (myordersStatusForShow == 'Closed') {
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

    function updateStopOrdersTableClosed() {
        if ($.fn.dataTable.isDataTable('#stopOrdersTable')) {
            stopOrdersTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            stopOrdersTable = $('#stopOrdersTable').DataTable({
                "ajax": {
                    "url": '/2a8fy7b07dxe44/orders',
                    "type": "GET",
                    "data": function(d){
                        d.id = id;
                        d.tableType = 'stopOrders' + myordersStatusForShow;
                        d.currencyPairId = $('#currency-pair-selector').val();
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
                        "render": function (data, type, row){
                            if (myordersStatusForShow == 'Closed' || myordersStatusForShow == 'Cancelled') {
                                return data;
                            }else {
                                return "";
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

});

