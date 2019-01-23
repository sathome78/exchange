/**
 * Created by Valk on 12.05.2016.
 */

var currentOrderId;

function getOrderDetailedInfo(orderId, enableActions) {

    currentOrderId = orderId;
    $.ajax({
        url: '/2a8fy7b07dxe44/orderinfo?id=' + orderId,
        type: 'GET',
        success: function (resp_data) {
            var data = resp_data.orderInfo;
            $("#id").find('span').html(data.id);
            $("#dateCreation").find('span').html(data.dateCreation);
            $("#dateAcception").find('span').html(data.dateAcception ? data.dateAcception : '-');
            $("#currencyPairName").find('span').html(data.currencyPairName);
            $("#orderStatusName").find('span').html(data.orderStatusName.toUpperCase());
            $("#orderTypeName").find('span').html(data.orderTypeName);
            $("#exrate").find('span').html(data.exrate);
            $("#amountBase").find('span').html(data.amountBase + ' ' + data.currencyBaseName);
            $("#amountConvert").find('span').html(data.amountConvert + ' ' + data.currencyConvertName);
            $("#orderCreatorEmail").find('span').html(data.orderCreatorEmail);
            $("#orderAcceptorEmail").find('span').html(data.orderAcceptorEmail?data.orderAcceptorEmail:'-');
            $("#transactionCount").find('span').html(data.transactionCount);
            $("#companyCommission").find('span').html(data.companyCommission?data.companyCommission + ' ' + data.currencyConvertName:'-');
            /**/
            if (data.orderStatusName.toUpperCase() === 'OPENED') {
                $("#notification").find('span').html(resp_data.notification);
            }

            var statusUpperCase = data.orderStatusName.toUpperCase();
            if (statusUpperCase === 'DELETED' || statusUpperCase === 'SPLIT_CLOSED' || !enableActions) {
                $("#delete-order-info__delete").toggle(false);
            } else {
                $("#delete-order-info__delete").toggle(true);

            }

            if (data.orderStatusName.toUpperCase() === 'OPENED' && enableActions && resp_data.acceptable) {
                $("#delete-order-info__accept").toggle(true);

            } else {
                $("#delete-order-info__accept").toggle(false);
            }

            if (data.source) {
                $('#orderSource').toggle(true);
                $('#orderSource').find('div').html('<button class="btn btn-sm btn-default" ' +
                    'onclick="getOrderDetailedInfo(' + data.source + ', true)">' + data.source + '</button>')
            } else {
                $('#orderSource').toggle(false);
            }

            if (data.children && data.children.length > 0) {
                $('#orderChildren').find('div').empty();
                data.children.forEach(function (item) {
                    $('#orderChildren').find('div').append('<button class="btn btn-sm btn-default" ' +
                        'onclick="getOrderDetailedInfo(' + item + ', true)">' + item + '</button>')
                });
                $('#orderChildren').toggle(true);
            } else {
                $('#orderChildren').toggle(false);
            }

            /**/
            $('#order-delete-modal').modal();
        }
    });
}

function getTransferDetailedInfo(orderId) {
    $.ajax({
        url: '/2a8fy7b07dxe44/transferInfo?id=' + orderId,
        type: 'GET',
        success: function (data) {
            $("#info-date").html(data.creationDate);
            $("#info-currency").html(data.currencyName);
            $("#info-amount").html(data.amount);
            $("#info-userFrom").html("<a href='mailto:" +  data.userFromEmail + "'>" + data.userFromEmail + "</a>");
            $("#info-userTo").html("<a href='mailto:" + data.userToEmail + "'>" + data.userToEmail + "</a>");
            $("#info-commissionAmount").html(data.comission);
            $('#user_transfer_info_modal').modal();
        }
    });
}

function deleteOrderByAdmin(order_id) {
    $('#order-delete-modal').one('hidden.bs.modal', function (e) {
        /*placed in close callback because we must give time for #order-delete-modal to restore parameters of <body>
         * otherwise we get the shift of the window to the left every time when open and then close #order-delete-modal--ok
         */
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/orderdelete?id=' + order_id,
                type: 'POST',
                success: function (data) {
                    $('#order-delete-modal--result-info').find('.delete-order-info__item').toggle(false);
                    if (data === -1) {
                        $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
                    } else {
                        $('#order-delete-modal--result-info').find('.success').toggle(true);
                        $("#order-delete-modal--result-info").find('.success').find('span').html(data);
                        updateOrderTable();
                    }
                    $('#order-delete-modal--result-info').modal();
                },
                error: function(jqXHR){
                    if (jqXHR.status != 403) {
                        $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
                        $('#order-delete-modal--result-info').modal();
                    }

                }
            }
        );
    });
    $('#order-delete-modal').modal('hide');
}

function acceptOrderByAdmin(order_id) {
    $('#order-delete-modal').one('hidden.bs.modal', function (e) {
        /*placed in close callback because we must give time for #order-delete-modal to restore parameters of <body>
         * otherwise we get the shift of the window to the left every time when open and then close #order-delete-modal--ok
         */
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/order/accept?id=' + order_id,
                type: 'POST',
                success: function (data) {
                    successNoty(data['result'], 'successOrder');
                    updateOrderTable()
                }
            }
        );
    })
    $('#order-delete-modal').modal('hide');
}

function searchAndDeleteOrderByAdmin() {
    $('#order-delete-modal--search').modal();
}

function validateErrorForm() {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    var creatorEmail = $('#creatorEmail').val();
    var acceptorEmail = $('#acceptorEmail').val();

    if (creatorEmail.length > 0) {
        match = creatorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== creatorEmail) {
            $('.input-block-wrapper__error-wrapper[for=creatorEmail]').toggle(true);
            isError = true;
        }
    }
    if (acceptorEmail.length > 0) {
        match = acceptorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== acceptorEmail) {
            $('.input-block-wrapper__error-wrapper[for=acceptorEmail]').toggle(true);
            isError = true;
        }
    }

    return isError;
}

function acceptSelected(orderIds, orderDataTable) {
    var data = 'orderIds=' + orderIds.join(',');
    if (confirm($('#promptAcceptLoc').text() + orderIds.join(', ') + '?')) {
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/order/acceptMany',
                type: 'POST',
                data: data,
                success: function (/*data*/) {
                    /*successNoty(data['result'], 'successOrder');*/
                    updateOrderTable();
                    orderDataTable.button(2).enable(false);
                    orderDataTable.button(3).enable(false);
                }
            }
        );
    }
}

function deleteSelected(orderIds, orderDataTable) {
    var data = 'orderIds=' + orderIds.join(',');
    if (confirm($('#promptDeleteLoc').text() + orderIds.join(', ') + '?')) {
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/order/deleteMany',
                type: 'POST',
                data: data,
                success: function (/*data*/) {
                    /*successNoty(data['result'], 'successOrder');*/
                    updateOrderTable();
                    orderDataTable.button(2).enable(false);
                    orderDataTable.button(3).enable(false);
                }
            }
        );
    }
}

function updateOrderTable() {
    var isError = validateErrorForm();
    if (isError) {
        return;
    }
    var data = $('#delete-order-info__form').serialize();
    var searchUrl = '/2a8fy7b07dxe44/searchorders?' + data;
    var orderDataTable;
    if ($.fn.dataTable.isDataTable('#order-info-table')) {
        orderDataTable = $('#order-info-table').DataTable();
        orderDataTable.ajax.url(searchUrl).load();
    } else {
        orderDataTable = $('#order-info-table').DataTable({
            "serverSide": true,
            "ajax": {
                "url": searchUrl,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "bFilter": false,
            "columnDefs": [ {
                "orderable": false,
                "className": 'select-checkbox',
                "targets":   0
            } ],
            "columns": [
                {
                    "data": null,
                    "render": function (data, type, row) {
                        return "";
                    }
                },
                {
                    "data": "id",
                    "name": "EXORDERS.id"
                },
                {
                    "data": "dateCreation",
                    "name": "EXORDERS.date_creation",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0] + '<br/>' + data.split(' ')[1];
                        }
                        return data;
                    }
                },
                {
                    "data": "currencyPairName",
                    "name": "CURRENCY_PAIR.name"
                },
                {
                    "data": "orderTypeName",
                    "name": "ORDER_OPERATION.name"
                },
                {
                    "data": "exrate",
                    "name": "EXORDERS.exrate"
                },
                {
                    "data": "amountBase",
                    "name": "EXORDERS.amount_base"
                },
                {
                    "data": "orderCreatorEmail",
                    "name": "CREATOR.email"
                },
                {
                    "data": "role",
                    "name": "CREATOR.roleid"
                },
                {
                    "data": "status",
                    "name": "EXORDERS.status_id"
                }


            ],
            "select": {
                "style":    'multi+shift',
                "selector": 'td:first-child'
            },
            dom: "B<'row pull-right' l>frtip",

            buttons: [
                'selectAll',
                'selectNone',
                {
                    text: $('#acceptSelectedButtonLoc').text(),
                    action: function (e, dt, node, config) {
                        var selectedRows = orderDataTable.rows( { selected: true } );
                        var orderIds = selectedRows.data().map(function (elem) {
                            return elem.id;
                        });
                        acceptSelected(orderIds, orderDataTable)
                    },
                    enabled: false
                },
                {
                    text: $('#deleteSelectedButtonLoc').text(),
                    action: function (e, dt, node, config) {
                        var selectedRows = orderDataTable.rows( { selected: true } );
                        var orderIds = selectedRows.data().map(function (elem) {
                            return elem.id;
                        });
                        deleteSelected(orderIds, orderDataTable)

                    },
                    enabled: false
                }
            ],
            language: {
                buttons: {
                    selectAll: $('#selectAllButtonLoc').text(),
                    selectNone: $('#selectNoneButtonLoc').text()
                }
            },
            "order": [
                [1, 'desc']
            ]
        });
        orderDataTable.on( 'select', function ( e, dt, type, indexes ) {

            updateAcceptDeleteButtons(orderDataTable);


        } )
            .on( 'deselect', function ( e, dt, type, indexes ) {
                updateAcceptDeleteButtons(orderDataTable);

            } );

        $('#order-info-table').on('click', 'tr', function () {

            var currentRow = orderDataTable.row( this );

            getOrderDetailedInfo(currentRow.data().id, true);
        } );


        $('#order-info-table').toggle(true);
    }

}
function updateAcceptDeleteButtons(orderDataTable) {
    debugger;
    var rowData = orderDataTable.rows({ selected: true }).data().toArray();
    var statuses = rowData.map(function (elem) {
        return elem.status.toUpperCase();
    });
    var notEmpty = statuses.length > 0;
    var acceptable = notEmpty && statuses.every(function (t) {
        return t === 'OPENED';
    });
    var deletable = notEmpty && !statuses.some(function (t) {
        return t === 'DELETED' || t === 'SPLIT_CLOSED'
    });
    orderDataTable.button(2).enable(acceptable);
    orderDataTable.button(3).enable(deletable);
}


$(function () {
    $('#order-info-table').toggle(false);
    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#delete-order-info__search').on('click', updateOrderTable);
    $('#delete-order-info__reset').on('click', function () {
        $('#delete-order-info__form')[0].reset();
        updateOrderTable();
    });

    if ($('#delete-order-info__form').size() > 0) {
        updateOrderTable();
    }

    $("#delete-order-info__delete").on('click', function () {
        deleteOrderByAdmin(currentOrderId);
    })
    $("#delete-order-info__accept").on('click', function () {
        acceptOrderByAdmin(currentOrderId);
    });

});

function getOrders() {
    var url = '/2a8fy7b07dxe44/report/orders';
    var dataReq = $('#delete-order-info__form').serialize();

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.setRequestHeader('X-CSRF-Token', $("input[name='_csrf']").val());
    xhr.responseType = 'blob';

    xhr.onreadystatechange = function () {
        if(xhr.readyState == 4 && xhr.status == 200) {
            var blob = xhr.response;
            var header = xhr.getResponseHeader('Content-Disposition');
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = header.match(/filename="(.+)"/)[1];
            link.click();
        }
    };

    xhr.send(dataReq);

}
