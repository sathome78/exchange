var currentEmail;
var $withdrawalPage;
var $withdrawalTable;
var withdrawalDataTable;
var withdrawRequestsBaseUrl;
var tableViewType;
var filterParams;

$(function () {

    $('#withdrawal-request-filter_button').on('click', function (e) {
        var element = document.getElementById("withdrawal-request-filter");
        if (element.style.display === "none") {
            element.style.display = "block";
            document.getElementById("withdrawal-statistic").style.display = "none";
        } else {
            element.style.display = "none";
        }
    });

    $('#withdrawal-statistic_button').on('click', function (e) {
        var element = document.getElementById("withdrawal-statistic");
        if (element.style.display === "none") {
            element.style.display = "block";
            document.getElementById("withdrawal-request-filter").style.display = "none";
        } else {
            element.style.display = "none";
        }
    });

    $('#filter_statistic_button').on('click', function (e) {
        var data = "startDate=" + $("#filter_statistic-datetimepicker_start").val() + ' 00:00:00' +
            '&' + "endDate=" + $("#filter_statistic-datetimepicker_end").val() + ' 23:59:59';

        $.ajax({
            url: '/2a8fy7b07dxe44/withdraw/statistic',
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            type: 'POST',
            data: data,
            success: function (data) {
                $("#manual_withdrawals").val(data[0]);
                $("#auto_withdrawals").val(data[1]);
            }
        });
    });


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

    $('#filter_statistic-datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD',
        formatDate: 'YYYY-MM-DD',
        lang: 'ru',
        defaultDate: new Date(),
        timepicker: false,
    });

    $('#filter_statistic-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD',
        formatDate: 'YYYY-MM-DD',
        lang: 'ru',
        defaultDate: new Date(),
        timepicker: false,
    });

    $('#filter-dateProcessing-datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#filter-dateProcessing-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

    $withdrawalPage = $('#withdraw-requests-admin');
    $withdrawalTable = $('#withdrawalTable');
    tableViewType = "FOR_WORK";
    filterParams = '';
    withdrawRequestsBaseUrl = '/2a8fy7b07dxe44/withdrawRequests?viewType=';
    $('#withdraw-requests-manual').addClass('active');


    $('#withdraw-requests-new').click(function () {
        changeTableViewType(this, "FOR_WORK")
    });
    $('#withdraw-requests-manual').click(function () {
        changeTableViewType(this, "FOR_MANUAL")
    });
    $('#withdraw-requests-confirm').click(function () {
        changeTableViewType(this, "FOR_CONFIRM")
    });
    $('#withdraw-requests-auto').click(function () {
        changeTableViewType(this, "AUTO_PROCESSING")
    });
    $('#withdraw-requests-accepted').click(function () {
        changeTableViewType(this, "POSTED")
    });
    $('#withdraw-requests-declined').click(function () {
        changeTableViewType(this, "DECLINED")
    });
    $('#withdraw-requests-checking').click(function () {
        changeTableViewType(this, "FOR_CHECKING")
    });
    $('#withdraw-requests-All').click(function () {
        changeTableViewType(this, "ALL")
    });

    function changeTableViewType($elem, newStatus) {
        tableViewType = newStatus;
        $withdrawalPage.find('.myorders__button').removeClass('active');
        $($elem).addClass('active');
        updateWithdrawalTable();
    }

    updateWithdrawalTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#withdrawal-request-search-form').serialize();
        updateWithdrawalTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#withdrawal-request-search-form')[0].reset();
        filterParams = '';
        updateWithdrawalTable();
    });

    $($withdrawalTable).on('click', 'button[data-source=WITHDRAW].post_holded_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var txHash = $(this).closest("tr").find(".txHashClass").val();
        if (txHash == '') {
            alert('Empty hash!');
            throw new Error("Empty hash!");

        }
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/post?id=' + id + '&txHash=' + txHash,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });

    $($withdrawalTable).on('click', 'button[data-source=WITHDRAW].take_to_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/take?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });

    $($withdrawalTable).on('click', 'button[data-source=WITHDRAW].return_from_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/return?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });


    $($withdrawalTable).on('click', 'button[data-source=WITHDRAW].decline_holded_button, button[data-source=WITHDRAW].decline_button', function (e) {
        e.stopPropagation();
        var id = $(this).data("id");
        var $modal = $("#note-before-decline-modal");
        var email = $(this).closest("tr").find("a[data-userEmail]").data("useremail");
        $.ajax({
            url: '/2a8fy7b07dxe44/phrases/withdraw_decline?email=' + email,
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
                        url: '/2a8fy7b07dxe44/withdraw/decline?id=' + id + '&comment=' + comment,
                        async: false,
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        complete: function () {
                            updateWithdrawalTable();
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

    $($withdrawalTable).on('click', 'button[data-source=WITHDRAW].confirm_admin_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/confirm?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });

    $($withdrawalTable).on('click', 'input.copyable', function (e) {
        selectAndCopyInputValue(this);
    });
});

function getRowId($elem) {
    var rowData = retrieveRowDataForElement($elem);
    return rowData.transaction.id;
}

function viewRequestInfo($elem) {
    var rowData = retrieveRowDataForElement($elem);
    var $modal = $withdrawalPage.find('#withdraw-info-modal');
    fillModal($modal, rowData);
    $modal.modal();
}

function retrieveRowDataForElement($elem) {
    var $row = $($elem).parents('tr');
    return withdrawalDataTable.row($row).data();
}

function fillModal($modal, rowData) {
    $modal.find('#info-transactionId').text(rowData.id);
    var currencyName = rowData.currencyName;
    $modal.find('#info-currency').text(currencyName);
    $modal.find('#info-amount').text(rowData.netAmount);
    $modal.find('#info-commissionAmount').text(rowData.commissionAmount);
    var amountToWithdraw = rowData.netAmountCorrectedForMerchantCommission;
    $modal.find('#info-ammountToWithdraw').text(amountToWithdraw);
    $modal.find('#info-status').text(rowData.status);
    $modal.find('#info-status-date').text(rowData.statusModificationDate);
    var recipientBank = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankCode = rowData.recipientBankCode ? rowData.recipientBankCode : '';
    $modal.find('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
    var wallet = rowData.wallet;
    $modal.find('#info-wallet').text(wallet);
    $modal.find('#info-destination-tag').text(rowData.destinationTag);
    var userFullName = rowData.userFullName ? rowData.userFullName : '';
    $modal.find('#info-userFullName').text(rowData.userFullName);
    $modal.find('#info-remark').find('textarea').html(rowData.remark);

    var data = {
        ticker: currencyName,
        wallet: wallet,
        amount: amountToWithdraw
    };
    $.ajax('/getQrCode', {
        data: data,
        type: 'GET',
        contentType: 'application/json'
    }).success(function (response) {
        $('#qr-copy').empty();

        var url = response.url;
        var image = response.image;
        if (image.length === 0) {
            $('#qr-code-id').hide();
            $('#qr-url-id').hide();
        } else {
            $('#qr-code-id')
                .attr('src', `data:image/png;base64,${image}`)
                .show();
            $('#qr-url-id')
                .val(url)
                .show();
        }
    }).fail(function (response) {
        alert('Have no possibility to generate QR code');
    });
}

function copyText() {
    var copyText = document.getElementById("qr-url-id");
    copyText.select();
    document.execCommand("copy");

    $('#qr-copy')
        .empty()
        .append('<label style="color: #0a8415;">Copied</label>')
        .show();
}


function updateWithdrawalTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = withdrawRequestsBaseUrl + tableViewType + filter;
    if ($.fn.dataTable.isDataTable('#withdrawalTable')) {
        withdrawalDataTable = $withdrawalTable.DataTable();
        withdrawalDataTable.ajax.url(url).load();
    } else {
        withdrawalDataTable = $withdrawalTable.DataTable({
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
                    "name": "WITHDRAW_REQUEST.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "dateCreation",
                    "name": "WITHDRAW_REQUEST.date_creation",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "name": "USER.email",
                    "render": function (data, type, row) {
                        return '<a data-userEmail="' + row.userEmail + '" href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
                    }
                },
                {
                    "data": "netAmount",
                    "name": "WITHDRAW_REQUEST.amount",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[00000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "netAmountCorrectedForMerchantCommission",
                    "name": "WITHDRAW_REQUEST.amount",
                    "render": function (data, type, row) {
                        if (!data) return "";

                        if (type === 'display') {
                            return '<input class="form-control copyable" value="' + numbro(data).format('0.00[00000000]') + '">';
                        }
                        return data;
                    }
                },
                {
                    "data": "currencyName",
                    "name": "CUR.name"
                },

                {
                    "data": "commissionAmount",
                    "name": "WITHDRAW_REQUEST.commission",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[00000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "merchantName",
                    "name": "MER.name",
                    "render": function (data, type, row) {
                        var merchantName = data;
                        var imageName = row.merchantImageName;

                        if (imageName && merchantName !== imageName) {
                            return merchantName + '-' + imageName;
                        }
                        return merchantName;
                    }
                },
                {
                    "data": "wallet",
                    "name": "WITHDRAW_REQUEST.wallet",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return '<input class="form-control copyable" value="' + data + '">';
                        }
                        return data;
                    }
                },
                {
                    "data": "txHash",
                    "name": "WITHDRAW_REQUEST.transaction_hash",
                    "type": "readonly",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            var hash = data == null ? '' : data;
                            var isHashReadOnly = tableViewType == "FOR_MANUAL" || tableViewType == "FOR_WORK" || tableViewType == "FOR_CHECKING" ? '<input ' : '<input readonly';
                            return isHashReadOnly + ' class="form-control txHashClass" value="' + hash + '">';
                        }
                        return data;
                    }
                }, {
                    "data": "destinationTag",
                    "name": "WITHDRAW_REQUEST.destination_tag",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            var tag = data == null ? '' : data;
                            return '<input class="form-control copyable" value="' + tag + '">';
                        }
                        return data;
                    }
                },
                {
                    "data": "adminHolderEmail",
                    "name": "WITHDRAW_REQUEST.admin_holder_id",
                    "render": function (data, type, row) {
                        if (data && (row.isEndStatus || !row.authorizedUserIsHolder)) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + row.adminHolderId + '">' + data + '</a>';
                        } else {
                            return tableViewType == "ALL" ? row.status : getButtonsSet(row.id, row.sourceType, row.merchantName,
                                row.buttons, "withdrawalTable");
                        }
                    },
                    "className": "text-center"
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']],
            dom: "<'download-btn col-md-12'B>lftip",
            buttons: [
                {
                    text: 'CSV',
                    action: function (e, dt, node, config) {
                        var filter = filterParams.length > 0 ? '&' + filterParams : '';
                        var url = '/2a8fy7b07dxe44/withdrawRequests/report?viewType=' + tableViewType + filter;
                        $.get(url, function (data) {
                            saveToDisk(data, 'withdrawals.csv');
                        })
                    }
                }
            ]
        });
    }
}
