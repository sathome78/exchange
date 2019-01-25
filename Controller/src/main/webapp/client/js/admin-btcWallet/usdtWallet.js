

var txHistoryDataTable;
var blockedAddressesTable;
var urlBase;

$(function () {
    var $copyAddressButton = $('#copy-address');
    $copyAddressButton.hide();
    urlBase = '/2a8fy7b07dxe44/omniWallet/';

    updateTxHistoryTable();
    updateBlockedAddressesTable();

    $('#generate-address').click(function () {
        retrieveAddress();
    });
    $($copyAddressButton).click(function () {
        selectAndCopyText($('#refill-address'))
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
});


function updateTxHistoryTable() {
    var $txHistoryTable = $('#txHistory');
    var viewMessage = $('#viewMessage').text();
    var url = urlBase + 'getUsdtTransactions';

    if ($.fn.dataTable.isDataTable('#txHistory')) {
        txHistoryDataTable = $($txHistoryTable).DataTable();
        txHistoryDataTable.ajax.url(url).load();
    } else {
        txHistoryDataTable = $($txHistoryTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "columns": [
                {
                    "data": "blocktime",
                    "render": function (data) {
                        return new Date(data*1000).toLocaleString();
                    },
                    "className": "text-center"
                },
                {
                    "data": "txid",
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
                    "data": "sendingaddress",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "referenceaddress",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "blockhash",
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
                    "data": "propertyid"
                },
                {
                    "data": "fee"
                },
                {
                    "data": "confirmations"
                },
                {
                    "data": "",
                    "render": function () {
                        return '<button class="btn btn-sm btn-info" onclick="viewTransactionData(this)">' + viewMessage + '</button>'
                    }
                },
                {
                    "data": "txid",
                    "visible": false
                },
                {
                    "data": "referenceaddress",
                    "visible": false
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
                        columns: [0, 1, 2, 3, 4, 5, 6]
                    }

                }
            ]
        });
    }
}

function updateBlockedAddressesTable() {
    var $blockedAddressesTable = $('#blockedAdresses');
    var url = urlBase + 'getBlockedAddersses';

    if ($.fn.dataTable.isDataTable('#blockedAdresses')) {
        blockedAddressesTable = $($blockedAddressesTable).DataTable();
        blockedAddressesTable.ajax.url(url).load();
    } else {
        blockedAddressesTable = $($blockedAddressesTable).DataTable({
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "columns": [
                    {
                        "data": "address",
                        "render": function (data) {
                            var inputValue = data ? data : '';
                            return '<input readonly value="' + inputValue + '" style="width: 330px" ' +
                                'class="form-control input-block-wrapper__input">';
                        }
                    },
                    {
                        "data": "userEmail"
                    }

                ],
                "order": [[
                    0,
                    "desc"
                ]]
            });
        }
}


function viewTransactionData($elem) {
    var $row = $($elem).parents('tr');
    var rowData = txHistoryDataTable.row($row).data();
    var url = '/2a8fy7b07dxe44/bitcoinWallet/USDT/transaction/details?currency=' + $('#currencyName').text() +
        '&address=' + rowData.referenceaddress +
        '&hash=' + rowData.txid;
    $.get(url, function (data) {
        if (data['result']) {
            var result = data['result'];
            $('#btcTxInfoTable').show();
            $('#no-address').hide();
            hideRowIfAbsent('#info-id', result.id);
            hideRowIfAbsent('#info-dateCreation', result.dateCreation);
            hideRowIfAbsent('#info-status-date', result.dateModification);
            hideRowIfAbsent('#info-status', result.status);
            hideRowIfAbsent('#info-user', result.userEmail);
            if (!result.id) {
                $('#create-refill').show();
                var $form = $('#createRefillForm');
                $($form).find('input[name="txId"]').val(rowData.txid);
                $($form).find('input[name="address"]').val(rowData.referenceaddress);
            } else {
                $('#create-refill').hide();
            }
        } else {
            $('#btcTxInfoTable').hide();
            $('#create-refill').hide();
            $('#no-address').show();
        }
        $('#btc-tx-info-modal').modal();
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







