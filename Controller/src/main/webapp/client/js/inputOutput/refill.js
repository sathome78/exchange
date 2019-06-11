var currentEmail;
var $refillPage;
var $refillTable;
var refillDataTable;
var refillRequestsBaseUrl;
var tableViewType;
var filterParams;

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

    $('#filter-datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#filter-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });


    $refillPage = $('#refill-requests-admin');
    $refillTable = $('#refillTable');
    tableViewType = "FOR_WORK";
    filterParams = '';
    refillRequestsBaseUrl = '/2a8fy7b07dxe44/refillRequests?viewType=';
    $('#refill-requests-new').addClass('active');


    $('#refill-requests-new').click(function () {
        changeTableViewType(this, "FOR_WORK")
    });
    $('#refill-requests-on-pending').click(function () {
        changeTableViewType(this, "WAIT_PAYMENT")
    });
    $('#refill-requests-on-bch-exam').click(function () {
        changeTableViewType(this, "COLLECT_CONFIRMATIONS")
    });
    $('#refill-requests-accepted').click(function () {
        changeTableViewType(this, "ACCEPTED")
    });
    $('#refill-requests-declined').click(function () {
        changeTableViewType(this, "DECLINED")
    });
    $('#refill-requests-All').click(function () {
        changeTableViewType(this, "ALL")
    });
    $('#refill-requests-created-by-fact').click(function () {
        changeTableViewType(this, "CREATED_BY_FACT")
    });

    function changeTableViewType($elem, newStatus) {
        tableViewType = newStatus;
        $refillPage.find('.myorders__button').removeClass('active');
        $($elem).addClass('active');
        updateRefillTable();
    }

    updateRefillTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#refill-request-search-form').serialize();
        updateRefillTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#refill-request-search-form')[0].reset();
        filterParams = '';
        updateRefillTable();
    });

    $('#refillTable').on('click', 'button[data-source=REFILL].take_to_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/refill/take?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateRefillTable();
                }
            });
        });
        $modal.modal();
    });

    $('#refillTable').on('click', 'button[data-source=REFILL].return_from_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/refill/return?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateRefillTable();
                }
            });
        });
        $modal.modal();
    });

    $('#refillTable').on('click', 'button[data-source=REFILL].decline_holded_button', function (e) {
        e.stopPropagation();
        var id = $(this).data("id");
        var $modal = $("#note-before-decline-modal");
        var email = $(this).closest("tr").find("a[data-userEmail]").data("useremail");
        $.ajax({
            url: '/2a8fy7b07dxe44/phrases/refill_decline?email=' + email,
            type: 'GET',
            success: function (data) {
                $modal.find("#user-language").val(data["lang"]);
                $list = $modal.find("#phrase-template-list");
                $list.html("<option></option>");
                data["list"].forEach(function (e) {
                    $list.append($("<option></option>").append(e));
                });
                $modal.find("#createCommentConfirm").off("click").one("click", function (event) {
                    var $textArea = $(event.target).closest("#note-before-decline-modal").find("#commentText");
                    var comment = $textArea.val().trim();
                    if (!comment) {
                        return;
                    }
                    $modal.modal('hide');
                    $.ajax({
                        url: '/2a8fy7b07dxe44/refill/decline?id=' + id /*+ '&comment=' + comment*/,
                        async: false,
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        data: {comment: comment},
                        complete: function () {
                            updateRefillTable();
                        }
                    });
                });
                $modal.find("#createCommentCancel").off("click").one("click", function () {
                    $modal.modal('hide');
                });
                $modal.modal();
            }
        });
    });

    $('#refillTable').on('click', 'button[data-source=REFILL].accept_holded_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var initialAmount = retrieveRowDataForElement(this).amount;
        var $modal = $("#dialog-refill-accept");
        $modal.find("#initial-amount").html(initialAmount);
        $modal.find("#actual-amount").val(initialAmount);
        $modal.find("#merchant_transaction_id").val(retrieveRowDataForElement(this).merchantTransactionId);
        $modal.find("#remark").val(retrieveRowDataForElement(this).amount);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            var amount = $modal.find("#actual-amount").val();
            var remark = $modal.find("#remark").val();
            var merchantTxId = $modal.find("#merchant_transaction_id").val();
            var data = {
                "requestId": id,
                "amount": amount,
                "remark": remark,
                "merchantTxId": merchantTxId
            };
            $.ajax({
                url: '/2a8fy7b07dxe44/refill/accept',
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                complete: function () {
                    updateRefillTable();
                }
            });
        });
        checkAllFields();
        $modal.modal();
    });

    $('#create_refill_request').on('click', function () {
        $('#dialog-refill-create').modal();
    });

    $('.rc_item').on('input', function () {
        if (checkManualRefillFrom()) {
            $('#refill_create_button').prop("disabled", false)
        } else {
            $('#refill_create_button').prop("disabled", true)
        }
    });


    $('#refill_create_button').on('click', function (e) {
        if (!checkManualRefillFrom()) {
            return;
        }
        e.preventDefault();
        var $modal = $('#dialog-refill-create');
        var amount = $modal.find("#rc_amount").val();
        var merchantTxId = $modal.find("#rc_merchant_transaction_id").val();
        var email = $modal.find("#rc_email").val();
        var address = $modal.find("#rc_address").val();
        var currency = $modal.find("#rc_currency_select option:selected:selected").val();
        var data = {
            "currency": currency,
            "email": email,
            "address": address,
            "amount": amount,
            "txHash": merchantTxId
        };
        $.ajax({
            url: '/2a8fy7b07dxe44/refill/crypto_create',
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
            success: function (data) {
                $modal.modal('hide');
                clearManualRefillForm();
                successNoty(data.message);
            }
        });
    });
});

function checkManualRefillFrom() {
    var $modal = $('#dialog-refill-create');
    var amount = $modal.find("#rc_amount").val().trim();
    var email = $modal.find("#rc_email").val().trim();
    var address = $modal.find("#rc_address").val().trim();
    return address !== null && address !== '' && email !== null && email !== '' && amount !== null && amount !== '' && !isNaN(amount);

}

function clearManualRefillForm() {
    var $modal = $('#dialog-refill-create');
    $modal.find("#rc_amount").val("");
    $modal.find("#rc_merchant_transaction_id").val("");
    $modal.find("#rc_email").val("");
    $modal.find("#rc_address").val("");
}

function getRowId($elem) {
    var rowData = retrieveRowDataForElement($elem);
    return rowData.transaction.id;
}

function viewRequestInfo($elem) {
    var rowData = retrieveRowDataForElement($elem);
    var $modal = $refillPage.find('#refill-info-modal');
    fillModal($modal, rowData);
    $modal.modal();
}

function retrieveRowDataForElement($elem) {
    var $row = $($elem).parents('tr');
    return refillDataTable.row($row).data();
}

function fillModal($modal, rowData) {
    var $scan = $('#scan');
    $scan.hide();
    $modal.find('#info-currency').text(rowData.currencyName);
    $modal.find('#info-amount').text(rowData.amount);
    $modal.find('#info-receivedAmount').text(rowData.receivedAmount);
    $modal.find('#info-commissionAmount').text(rowData.commissionAmount);
    $modal.find('#info-enrolledAmount').text(rowData.enrolledAmount);
    $modal.find('#info-status').text(rowData.status);
    $modal.find('#info-status-date').text(rowData.statusModificationDate);
    $modal.find('#info-confirmations').text(rowData.confirmations);
    var recipientBankName = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankAccount = rowData.recipientBankAccount ? '</br>' + rowData.recipientBankAccount : '';
    var recipientBankRecipient = rowData.recipientBankRecipient ? '</br>' + rowData.recipientBankRecipient : '';
    $modal.find('#info-bankRecipient').html(recipientBankName + recipientBankAccount + recipientBankRecipient);
    var payerBankCode = rowData.payerBankCode ? rowData.payerBankCode : '';
    var payerBankName = rowData.payerBankName ? '</br>' + rowData.payerBankName : '';
    var payerBankAccount = rowData.payerBankAccount ? '</br>' + rowData.payerBankAccount : '';
    var userFullName = rowData.userFullName ? '</br>' + rowData.userFullName : '';
    var payerDataString = payerBankCode + payerBankName + payerBankAccount + userFullName;
    $modal.find('#info-payer-data').html(payerDataString);
    $modal.find('#info-address').text(rowData.address);
    $modal.find('#info-merchant-transaction-id').text(rowData.merchantTransactionId);
    $modal.find('#info-remark').find('textarea').html(rowData.remark);
    if (rowData.merchantName === 'Invoice') {
        var receiptImage;
        if (rowData.receiptScan) {
            receiptImage = '<a href="' + rowData.receiptScan + '" class="col-sm-4" data-toggle="lightbox"><img src="' +
                rowData.receiptScan + '" class="img-responsive"></a>';
        } else {
            receiptImage = '-';
        }
        $('#info-receipt').html(receiptImage);
        $scan.show();
    }
}


function updateRefillTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = refillRequestsBaseUrl + tableViewType + filter;
    if ($.fn.dataTable.isDataTable('#refillTable')) {
        refillDataTable = $refillTable.DataTable();
        refillDataTable.ajax.url(url).load();
    } else {
        refillDataTable = $refillTable.DataTable({
            "ajax": {
                "url": url,
                "dataSrc": "data"
            },
            "serverSide": true,
            "paging": true,
            "info": true,
            "bFilter": true,
            "columns": [
                {
                    "data": "id",
                    "name": "REFILL_REQUEST.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "dateCreation",
                    "name": "REFILL_REQUEST.date_creation",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "name": "REFILL_REQUEST.user_id",
                    "render": function (data, type, row) {
                        return '<a data-userEmail="' + row.userEmail + '" href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
                    }
                },
                {
                    "data": "amount",
                    "name": "REFILL_REQUEST.amount"
                },
                {
                    "data": "currencyName",
                    "name": "REFILL_REQUEST.currency_id"
                },
                {
                    "data": "receivedAmount",
                    "name": "REFILL_REQUEST.commission"
                },
                {
                    "data": "commissionAmount",
                    "name": "REFILL_REQUEST.commission"
                },
                {
                    "data": "enrolledAmount",
                    "name": "REFILL_REQUEST.commission"
                },
                {
                    "data": "merchantName",
                    "name": "REFILL_REQUEST.merchant_id"
                },
                {
                    "data": "status",
                    "name": "REFILL_REQUEST.status_id",
                    "render": function (data, type, row) {
                        if (data) {
                            var refillRequestId = row.id;
                            return tableViewType === "CREATED_BY_FACT" ? '<button id="changeStatus' + refillRequestId + '" onclick="changeStatusToOnPending(' + refillRequestId + ')" ' +
                                'class="action-button table-button-block__button btn" style="font-size: 1.1rem;background-color: #eff9ff">Change to \'ON_PENDING\'</button>' : row.status;
                        }
                    },
                    "className": "text-center"
                },
                {
                    "data": "adminHolderEmail",
                    "name": "REFILL_REQUEST.admin_holder_id",
                    "render": function (data, type, row) {
                        if (data && row.isEndStatus) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + row.adminHolderId + '">' + data + '</a>';
                        } else {
                            return tableViewType !== "ALL" ? getButtonsSet(row.id, row.sourceType, row.merchantName, row.buttons, "refillTable") : '';
                        }
                    },
                    "className": "text-center"
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']]
        });
    }
}

function changeStatusToOnPending(refillRequestId) {
    $.ajax({
        url: '/2a8fy7b07dxe44/refill/change-status',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "refillRequestId": refillRequestId
        },
        success: function () {
            updateRefillTable();
        },
        error: function (err) {
            console.log(err);
        }
    });
}