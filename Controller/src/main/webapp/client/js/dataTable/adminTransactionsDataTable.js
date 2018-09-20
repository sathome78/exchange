var transactionsDataTable;

$(function () {


    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });
    var date = new Date();
    date.setMonth(date.getMonth()-1);
    console.log(date);
    $('#datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru',
        value:date,
        defaultDate: date,
        defaultTime: '00:00'
    });

    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru',
        value:new Date(),
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

    $('#transactionsTable').hide();
    $('#transactionsTable').on('click', 'tbody .transactionlist-order-detail-button', function () {
        var row = transactionsDataTable.row($(this).parents('tr'));
        getSourceTypeDetailedInfo($(this).data("sourcetype"), $(this).data("sourceid"));
    });

    $('#transactions-table-init').click(function () {
        loadTransactionsDataTable();
    });



    function getSourceTypeDetailedInfo(sourceType, sourceId) {
        if (sourceType === "USER_TRANSFER") {
            getTransferDetailedInfo(sourceId);
        } else if (sourceType === "ORDER") {
            getOrderDetailedInfo(sourceId, false);
        } else if (sourceType === "WITHDRAW") {
            getWithdrawDetailedInfo(sourceId);
        } else if (sourceType === "REFILL") {
            getRefillDetailedInfo(sourceId);
        } else if (sourceType === "STOP_ORDER") {
            getStopOrderDetailedInfo(null, sourceId, false)
        }
    }

    function getTransferDetailedInfo(orderId) {
        $.ajax({
            url: '/2a8fy7b07dxe44/transfer/request/info?id=' + orderId,
            type: 'GET',
            success: function (data) {
                $("#info-merchantType").html(data.merchantName);
                $("#info-status").html(data.status);
                $("#info-date").html(data.dateCreation);
                $("#info-date-modif").html(data.statusModificationDate);
                $("#info-currency").html(data.currencyName);
                $("#info-amount").html(numbro(data.amount).format('0.00[000000]'));
                $("#info-userFrom").html("<a href='mailto:" + data.creatorEmail + "'>" + data.creatorEmail + "</a>");
                $("#info-userTo").html("<a href='mailto:" + data.recipientEmail + "'>" + data.recipientEmail + "</a>");
                $("#info-commissionAmount").html(numbro(data.commissionAmount).format('0.00[000000]'));
                $("#info-hash").html(data.hash);
                $('#user_transfer_info_modal').modal();
            }
        });
    }

    function getWithdrawDetailedInfo(id) {
        $.ajax({
            url: '/2a8fy7b07dxe44/withdraw/info?id=' + id,
            type: 'GET',
            success: function (data) {
                var $modal = $('#withdraw-info-modal');
                $modal.find('#info-currency').text(data.currencyName);
                $modal.find('#info-amount').text(data.amount);
                $modal.find('#info-commissionAmount').text(data.commissionAmount);
                var recipientBank = data.recipientBankName ? data.recipientBankName : '';
                var recipientBankCode = data.recipientBankCode ? data.recipientBankCode : '';
                var userFullName = data.userFullName ? data.userFullName : '';
                $modal.find('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
                $modal.find('#info-status').text(data.status);
                $modal.find('#info-status-date').text(data.statusModificationDate);
                $modal.find('#info-wallet').text(data.wallet);
                $modal.find('#info-destination-tag').text(data.destinationTag);
                $modal.find('#info-userFullName').text(data.userFullName);
                $modal.find('#info-remark').find('textarea').html(data.remark);
                $modal.modal();
            }
        });
    }

    function getRefillDetailedInfo(id) {
        $.ajax({
            url: '/2a8fy7b07dxe44/refill/info?id=' + id,
            type: 'GET',
            success: function (rowData) {
                var $modal = $('#refill-info-modal');
                $modal.find('#info-currency').text(rowData.currencyName);
                $modal.find('#info-amount').text(rowData.amount);
                $modal.find('#info-receivedAmount').text(rowData.receivedAmount);
                $modal.find('#info-commissionAmount').text(rowData.commissionAmount);
                $modal.find('#info-enrolledAmount').text(rowData.enrolledAmount);
                $modal.find('#info-status').text(rowData.status);
                $modal.find('#info-status-date').text(rowData.statusModificationDate);
                $modal.find('#info-confirmations').text(rowData.confirmations);
                var recipientBankName = rowData.recipientBankName ? rowData.recipientBankName : '';
                var recipientBankAccount = rowData.recipientBankAccount ? '</br>'+rowData.recipientBankAccount : '';
                var recipientBankRecipient = rowData.recipientBankRecipient ? '</br>'+rowData.recipientBankRecipient : '';
                $modal.find('#info-bankRecipient').html(recipientBankName + recipientBankAccount + recipientBankRecipient);
                var payerBankCode = rowData.payerBankCode ? rowData.payerBankCode : '';
                var payerBankName = rowData.payerBankName ? '</br>'+rowData.payerBankName : '';
                var payerBankAccount = rowData.payerBankAccount ? '</br>'+rowData.payerBankAccount : '';
                var userFullName = rowData.userFullName ? '</br>'+rowData.userFullName : '';
                var payerDataString = payerBankCode+payerBankName+payerBankAccount+userFullName;
                $modal.find('#info-payer-data').html(payerDataString);
                $modal.find('#info-address').text(rowData.address);
                $modal.find('#info-merchant-transaction-id').text(rowData.merchantTransactionId);
                $modal.find('#info-remark').find('textarea').html(rowData.remark);
                $modal.modal();
            }
        });
    }

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        loadTransactionsDataTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#transaction-search-form')[0].reset();
        loadTransactionsDataTable();

    });

    $('#download_trans_history').click(function () {
        var formParams = $('#transaction-search-form').serialize();
        var dateParams = $('#transaction-search-datetime-form').serialize();
        var params = "id="+$("#user-id").val() + '&' + dateParams +'&' + formParams;
        uploadUserTransactionsReport(params);
    });

    $('#transactions_change_date').on('click', function (e) {
        e.preventDefault();
        loadTransactionsDataTable();
    });

    function loadTransactionsDataTable() {
        var formParams = $('#transaction-search-form').serialize();
        var dateParams = $('#transaction-search-datetime-form').serialize();
        var url = '/2a8fy7b07dxe44/transactions?id=' + $("#user-id").val() + '&' + dateParams +'&' + formParams;
        if ($.fn.dataTable.isDataTable('#transactionsTable')) {
            transactionsDataTable = $('#transactionsTable').DataTable();
            transactionsDataTable.ajax.url(url).load();
        } else {
            transactionsDataTable = $('#transactionsTable').DataTable({
                "serverSide": true,
                "deferRender": true,
                "ajax": {
                    "url": url,
                    "dataSrc": "data"
                },
                "paging": true,
                "info": true,
                "bFilter": false,
                "order": [[0, "desc"]],
                "columns": [
                    {
                        "data": "datetime",
                        "name": "TRANSACTION.datetime",
                        "render": function (data, type, row) {
                            if (type == 'display') {
                                return data.split(' ')[0];
                            }
                            return data;
                        }
                    },
                    {
                        "data": "datetime",
                        "render": function (data, type, row) {
                            if (type == 'display') {
                                return data.split(' ')[1];
                            }
                            return data;
                        },
                        "orderable": false
                    },
                    {
                        "data": "operationType",
                        "name": "TRANSACTION.operation_type_id",
                        "orderable": false
                    },
                    {
                        "data": "status",
                        "name": "TRANSACTION.provided",
                        "orderable": false
                    },
                    {
                        "data": "currency",
                        "name": "TRANSACTION.currency_id",
                        "orderable": false

                    },
                    {
                        "data": "amount",
                        "name": "TRANSACTION.amount",
                        "render": function (data, type, row) {
                            if (type == 'display') {
                                return numbro(data).format('0.00[000000]');
                            }
                            return data;
                        }
                    },
                    {
                        "data": "commissionAmount",
                        "name": "TRANSACTION.commission_amount",
                        "render": function (data, type, row) {
                            if (type == 'display') {
                                return numbro(data).format('0.00[000000]');
                            }
                            return data;
                        }
                    },
                    {
                        "data": "merchant.description",
                        "name": "MERCHANT.description",
                        "orderable": false
                    },
                    {
                        "data": "sourceId",
                        "name": "TRANSACTION.source_id",
                        "render": function (data, type, row) {
                            if (data) {
                                return '<button class="transactionlist-order-detail-button"' +
                                    ' data-sourceType=' + row.sourceType +
                                    ' data-sourceId=' + data + '>' +
                                    data +
                                    '</button>';
                            } else {
                                return '';
                            }
                        }
                    }
                ],
                "searchDelay": 1000
            });


            $('#transactionsTable').show();

        }
    }

    function reloadTable() {
        var formParams = $('#transaction-search-form').serialize();
        var url = '/2a8fy7b07dxe44/transactions?id=' + $("#user-id").val() + '&' + formParams;
        transactionsDataTable.ajax.url(url).load();
    }


});

