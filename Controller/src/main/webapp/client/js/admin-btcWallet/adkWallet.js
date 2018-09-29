

var txHistoryDataTable;
var urlBase;
$(function () {
    const $loadingDialog = $('#loading-process-modal');
    var $passwordModal = $('#password-modal');
    var $paymentConfirmModal = $('#payment-confirm-modal');
    var merchantName = $('#merchantName').text();
    var $copyAddressButton = $('#copy-address');
    $copyAddressButton.hide();
    urlBase = '/2a8fy7b07dxe44/bitcoinWallet/ADK/';


    updateTxHistoryTable();
    /*retrieveFee();*/
    checkSendBtcFormFields();
   /*refreshSubtractFeeStatus();*/

    $('#addPayment').click(function (e) {
        e.preventDefault();
        var $newPaymentDiv = $('#payment_0').clone(true);
        var currentNum = $('.btcWalletPayment').size();
        $newPaymentDiv.attr('id', 'payment_' + currentNum);
        $($newPaymentDiv).find('input[name="amount"]').attr('id', 'amount_' + currentNum).val('');
        $($newPaymentDiv).find('input[name="address"]').attr('id', 'address_' + currentNum).val('');
        var $removeButton = $('#rm-button-template').find('.rm-button-container').clone();
        $($newPaymentDiv).find('.rm-button-placeholder').append($removeButton);
        $('#payments').append($newPaymentDiv);
        checkSendBtcFormFields();
    });

    $('#send-btc-form').on('click', '.remove-payment', function (e) {
        e.preventDefault();
        $(this).parents('.btcWalletPayment').remove();
        checkSendBtcFormFields();
    });

    $('#payments').on('input', '.input-amount, .input-address', function () {
        checkSendBtcFormFields();
    });

    $('#input-fee-actual').on('input', function () {
        $('#tx-fee-form').find('input[name="fee"]').val($(this).val())
    });

    $('#submit-btc').click(function () {
        $($passwordModal).modal();
    });

    $('#generate-address').click(function () {
        retrieveAddress();
    });
    $($copyAddressButton).click(function () {
        selectAndCopyText($('#refill-address'))
    });

    $('#reset-btc').click(function () {
        resetForm();
    });


    $('#submit-wallet-pass').click(function () {
        $.ajax(urlBase + 'unlock', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $('#password-form').serialize(),
            success: function () {
                var rawTxEnabled = $('#enable-raw-tx').text() === 'true';
                $($passwordModal).modal('hide');
                if (rawTxEnabled) {
                    prepareRawTx();
                } else {
                    fillConfirmModal();
                    $($paymentConfirmModal).modal();
                }
            }
        })
    });


    $('#confirm-btc-submit').click(function () {
        var confirmButton = this;
        $(confirmButton).prop('disabled', true);
        var data = serializeBtcPayments();
        $loadingDialog.modal({
            backdrop: 'static'
        });
        $.ajax(urlBase + 'sendToMany', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify(data),
            success: function (data) {
                $loadingDialog.modal('hide');
                $($paymentConfirmModal).modal('hide');
                resetForm();
                $('#current-btc-balance').text(data['newBalance']);
                var $btcSendResultModal = $('#btc-send-result-modal');
                var $btcResultInfoTable = $($btcSendResultModal).find('#btcResultInfoTable').find('tbody');

                var $tmpl = $('#results-table_row').html().replace(/@/g, '%');
                clearTable($btcResultInfoTable);
                data['results'].forEach(function (e) {
                    $btcResultInfoTable.append(tmpl($tmpl, e));
                });
                updateTxHistoryTable();
                $($btcSendResultModal).modal();

            },
            complete: function () {
                $loadingDialog.modal('hide');
                $(confirmButton).prop('disabled', false);
            }
        })
    });


    $('#create-refill').click(function () {
        var formData = $('#createRefillForm').serialize();
        formData.admin = 'true';
        $.ajax({
            url: urlBase + 'transaction/create',
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
    /*$('#subtract-fee-from-amount').on('click', 'i', setSubtractFeeStatus);*/

    $('#submit-raw-tx').click(function () {
        var confirmButton = this;
        $(confirmButton).prop('disabled', true);
        $.ajax(urlBase + 'sendRawTx', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            success: function (data) {
                resetForm();
                $('#current-btc-balance').text(data['newBalance']);
                var $btcSendResultModal = $('#btc-send-result-modal');
                var $btcResultInfoTable = $($btcSendResultModal).find('#btcResultInfoTable').find('tbody');

                var $tmpl = $('#results-table_row').html().replace(/@/g, '%');
                clearTable($btcResultInfoTable);
                data['results'].forEach(function (e) {
                    $btcResultInfoTable.append(tmpl($tmpl, e));
                });
                updateTxHistoryTable();
                $($btcSendResultModal).modal();

            },
            complete: function () {
                $('#btc-prepare-raw-modal').modal('hide');
                $loadingDialog.modal('hide');
                $(confirmButton).prop('disabled', false);
            }
        })
    });

    $('#change-fee-raw').click(function () {
        updateTxFee($('#fee-rate-raw').val()).complete(prepareRawTx);
    });



    $('#submit-check-payments-btn').click(function () {
        $('#btc-check-payments-modal').modal('hide');
        $('#submit-check-payments-btn').prop('disabled', true);
        var url = urlBase + 'checkPayments';
        var data = {
            blockhash: $('#start-block-hash').val()
        };
        $loadingDialog.modal({
            backdrop: 'static'
        });
        $.ajax(url, {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: data,
            complete: function () {
                $loadingDialog.modal('hide');
                $('#submit-check-payments-btn').prop('disabled', false);
            }
        })
    });


});

function checkSendBtcFormFields() {
    var isFormValid = true;
    $('.btcWalletPayment').each(function () {
        var amount =  $(this).find('input[name="amount"]').val();
        var address = $(this).find('input[name="address"]').val();
        if (amount.length === 0 || address.length === 0) {
            $('#submit-btc').prop('disabled', true);
            isFormValid = false;
            return false;
        }
    });
    if (isFormValid) {
        $('#submit-btc').prop('disabled', false);
    }
}

function resetForm() {
    var $payments = $('#payments');
    var $initialPayment = $('#payment_0');
    $($payments).empty();
    $payments.append($initialPayment);
    $('#send-btc-form')[0].reset();
   /* retrieveFee();*/
    checkSendBtcFormFields();
}

function fillConfirmModal() {
    var templateVariables = {
        amount: '__amount__',
        address: '__address__'
    };
    var promptMessage = $('#confirmBtcMessage').text();
    var addresses = '';
    var totalAmount = 0;
    $('.btcWalletPayment').each(function () {
        if (addresses.length === 0) {
            addresses = addresses + $(this).find('input[name="address"]').val();
        } else {
            addresses = addresses + ', ' + $(this).find('input[name="address"]').val();
        }
        totalAmount = totalAmount + parseFloat($(this).find('input[name="amount"]').val())
    });
    promptMessage = promptMessage.replace(templateVariables.amount, totalAmount)
        .replace(templateVariables.address, addresses);
    $('#btc-confirm-prompt').text(promptMessage);

}




function updateTxHistoryTable() {
    var $txHistoryTable = $('#txHistory');
    var viewMessage = $('#viewMessage').text();
    var url = urlBase + 'transactions';

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
                    "data": "time",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "txId",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "category",
                    "render": function (data) {
                        var dataClass;
                        if (data === 'receive') {
                            dataClass = 'class="green"';
                        } else if (data === 'send') {
                            dataClass = 'class="red"';
                        } else {
                            dataClass = '';
                        }
                        return '<span '+ dataClass + '><strong>' + data + '</strong></span>'
                    }
                },
                {
                    "data": "address",
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
                    "data": "txId",
                    "visible": false
                },
                {
                    "data": "address",
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
                    bom:true,
                    charset: 'UTF8',
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6]
                    }

                }
            ]
        });}
}


function viewTransactionData($elem) {
    var $row = $($elem).parents('tr');
    var rowData = txHistoryDataTable.row($row).data();
    var url = urlBase + 'transaction/details?currency=' + $('#currencyName').text() +
        '&address=' + rowData.address +
        '&hash=' + rowData.txId;
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
                $($form).find('input[name="txId"]').val(rowData.txId);
                $($form).find('input[name="address"]').val(rowData.address);
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

function retrieveAddress() {
    $.get(urlBase + 'newAddress', function (data) {
        $('#refill-address').text(data);
        $('#address-qr').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + data + "'>")
        $('#copy-address').show();
    })
}

/*function refreshSubtractFeeStatus() {
    $.get(urlBase + 'getSubtractFeeStatus', function (data) {
        $('#subtract-fee-from-amount').html(data ? '<i class="fa fa-check green"></i>' :
            '<i class="fa fa-close red"></i>');
    })
}*/

/*function setSubtractFeeStatus() {
    const $subtractFee = $('#subtract-fee-from-amount').find('i');
    const subtractFeeNewValue = !$($subtractFee).hasClass('green') && $($subtractFee).hasClass('red');
    $.ajax(urlBase + 'setSubtractFee', {
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {subtractFee: subtractFeeNewValue},
        success: function () {
            refreshSubtractFeeStatus()
        }
    })
}*/

function serializeBtcPayments() {
    var data = [];
    $('.btcWalletPayment').each(function () {
        data.push({
            address: $(this).find('input[name="address"]').val(),
            amount: parseFloat($(this).find('input[name="amount"]').val())
        });
    });
    return data;
}

function prepareRawTx() {
    var data = serializeBtcPayments();
    $.ajax(urlBase + 'prepareRawTx', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        type: 'POST',
        contentType: 'application/json; charset=UTF-8',
        data: JSON.stringify(data),
        success: function (resp) {
            var $btcPrepareRawModal = $('#btc-prepare-raw-modal');
            var $paymentsList = $('#raw-tx-payments').find('ul');
            var $txHexesDiv = $('#raw-tx-hexes');
            $($paymentsList).empty();
            $($txHexesDiv).empty();
            resp['payments'].forEach(function (item) {
                $($paymentsList).append('<li>' + item['address'] + ' : ' + item['amount'] + '</li>')
            });

            $('#fee-rate-raw').val(numbro(resp['feeRate']).format('0.00[000000]'));
            $('#fee-amount-raw').text(numbro(resp['totalFeeAmount']).format('0.00[000000]'));

            if (!(($($btcPrepareRawModal).data('bs.modal') || {}).isShown)) {
                $($btcPrepareRawModal).modal();
            }
        }
    })
}

