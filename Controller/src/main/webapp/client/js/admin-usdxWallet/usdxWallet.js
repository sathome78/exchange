var txHistoryDataTable;
var urlBase;

$(function () {
    var $passwordModal = $('#usdx-password-modal');

    urlBase = '/2a8fy7b07dxe44/usdxWallet/';

    updateUsdxTxHistoryTable();

    $('#button-reset-trans-fields').click(function () {
        resetUsdxTransactionForm();
    });

    $('#create-refill').click(function () {
        var formData = $('#createRefillForm').serialize();
        formData.admin = 'true';
        $.ajax({
            url: urlBase + 'createTransaction',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: formData,
            success: function () {
                $('#btc-tx-info-modal').modal('hide');
            }
        })
    });

    $('#button-usdx-send-trans').click(function () {
        $($passwordModal).modal();
    });

    $('#button-send-usdx-wallet-transaction-pass').click(function () {
        $.ajax({
            url: urlBase + 'sendTransaction',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                password: $('#usdx-password').val(),
                accountName: $('#accountNameUsdxWallet').val(),
                amount: $('#amountUsdxWallet').val(),
                currency: $('#currencyUsdxWallet').val(),
                memo: $('#memoUsdxWallet').val(),
                customData: $('#customDataUsdxWallet').val()
            },
            success: function () {
                resetUsdxTransactionForm();
                $($passwordModal).modal('hide');
                location.reload();
            }
        })
    });

    $('#accountNameUsdxWallet').on("change keyup", function(){
        checkUsdxTransactionForm();
    });
    $('#amountUsdxWallet').on("change keyup", function(){
        checkUsdxTransactionForm();
    });
    $('#memoUsdxWallet').on("change keyup", function(){
        checkUsdxTransactionForm();
    });
    $('#customDataUsdxWallet').on("change keyup", function(){
        checkUsdxTransactionForm();
    });
});

function resetUsdxTransactionForm() {
    $('#usdx-transaction')[0].reset();

    checkUsdxTransactionForm();
}

function checkUsdxTransactionForm() {
    var accountName = $('#accountNameUsdxWallet').val();
    var amount = $('#amountUsdxWallet').val();
    var currency = $('#currencyUsdxWallet').val();

    if (accountName && amount && currency) {
        $("#button-usdx-send-trans").prop('disabled', false);
    } else {
        $("#button-usdx-send-trans").prop('disabled', true);
    }
}

function updateUsdxTxHistoryTable() {
    var $txHistoryTable = $('#txHistory');
    var viewMessage = $('#viewMessage').text();
    var url = urlBase + 'history';

    if ($.fn.dataTable.isDataTable('#txHistory')) {
        txHistoryDataTable = $($txHistoryTable).DataTable();
        txHistoryDataTable.ajax.url(url).load();
    } else {
        txHistoryDataTable = $($txHistoryTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": "",
                "data": {
                    "fromTransferId": "",
                    "limit": 100
                }
            },
            "columns": [
                {
                    "data": "createdAt",
                    "render": function (data) {
                        return new Date(data).toLocaleString();
                    },
                    "className": "text-center"
                },
                {
                    "data": "transferId",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "type"
                },
                {
                    "data": "currency"
                },
                {
                    "data": "memo",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "customData",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "amount"
                },
                {
                    "data": "status"
                },
                {
                    "data": "",
                    "render": function () {
                        return '<button class="btn btn-sm btn-info" onclick="viewTransactionData(this)">' + viewMessage + '</button>'
                    }
                }
            ],
            dom: "<'download-btn col-md-12'B>lftip",
            "order": [[
                0,
                "desc"
            ]],
            buttons: [
                {
                    extend: 'csv',
                    text: 'CSV',
                    fieldSeparator: ';',
                    bom: true,
                    charset: 'UTF8',
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8]
                    }

                }
            ]
        });
    }
}

function viewTransactionData($elem) {
    var $row = $($elem).parents('tr');
    var rowData = txHistoryDataTable.row($row).data();
    var url = '/2a8fy7b07dxe44/bitcoinWallet/' + $('#merchantName').text() + '/transaction/details?currency=' + $('#currencyName').text() +
        '&address=' + rowData.memo +
        '&hash=' + rowData.transferId;
    $.get(url, function (data) {
        if (data['result']) {
            var result = data['result'];
            $('#usdxTxInfoTable').show();
            $('#no-address').hide();
            hideRowIfAbsent('#info-id', result.id);
            hideRowIfAbsent('#info-dateCreation', result.dateCreation);
            hideRowIfAbsent('#info-status-date', result.dateModification);
            hideRowIfAbsent('#info-status', result.status);
            hideRowIfAbsent('#info-user', result.userEmail);
            if (!result.id) {
                $('#create-refill').show();
                var $form = $('#createRefillForm');
                $($form).find('input[name="txId"]').val(rowData.transferId);
            } else {
                $('#create-refill').hide();
            }
        } else {
            $('#usdxTxInfoTable').hide();
            $('#create-refill').hide();
            $('#no-address').show();
        }
        $('#usdx-transaction-info-modal').modal();
    })
}

function hideRowIfAbsent($elem, value) {
    var $row = $($elem).parents('tr');
    if (!value) {
        $($row).hide();
    } else {
        $($elem).text(value);
        $($row).show();
    }
}







