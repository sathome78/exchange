var currentEmail;
var $withdrawalPage;
var $voucherTable;
var withdrawalDataTable;
var transferRequestsBaseUrl;
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


    $withdrawalPage = $('#withdraw-requests-admin');
    $voucherTable = $('#voucherTable');
    filterParams = '';
    transferRequestsBaseUrl = '/2a8fy7b07dxe44/transfer/requests?';
    $('#withdraw-requests-manual').addClass('active');


    updateVoucherTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#withdrawal-request-search-form').serialize();
        updateVoucherTable();

        /* Hide error message for transaction id ("#errorValueForTransactionId")
        on vouchers page (vouchers.jsp) - extended filter. */
        $('#errorValueForTransactionId').hide();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#withdrawal-request-search-form')[0].reset();
        filterParams = '';
        updateVoucherTable();

        /* Hide error message for transaction id ("#errorValueForTransactionId")
        on vouchers page (vouchers.jsp) - extended filter. */
        $('#errorValueForTransactionId').hide();
    });

    $('#voucherTable').on('click', 'button[data-source=USER_TRANSFER].revoke_admin_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/transfer/request/revoke?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                complete: function () {
                    updateVoucherTable();
                }
            });
        });
        $modal.modal();
    });

});

function getRowId($elem) {
    var rowData = retrieveRowDataForElement($elem);
    return rowData.transaction.id;
}

function viewRequestInfo($elem) {
    var rowData = retrieveRowDataForElement($elem);
    var $modal = $withdrawalPage.find('#user_transfer_info_modal');
    fillModal($modal, rowData);
    $modal.modal();
}

function retrieveRowDataForElement($elem) {
    var $row = $($elem).parents('tr');
    return withdrawalDataTable.row($row).data();
}

function fillModal($modal, data) {
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
}

function updateVoucherTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = transferRequestsBaseUrl + filter;
    if ($.fn.dataTable.isDataTable('#voucherTable')) {
        withdrawalDataTable = $voucherTable.DataTable();
        withdrawalDataTable.ajax.url(url).load();
    } else {
        withdrawalDataTable = $voucherTable.DataTable({
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
                    "name": "TRANSFER_REQUEST.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "dateCreation",
                    "name": "TRANSFER_REQUEST.date_creation",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "name": "email",
                    "render": function (data, type, row) {
                        return '<a data-userEmail="' + row.creatorEmail + '" href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.creatorEmail + '</a>'
                    }
                },
                {
                    "data": "netAmount",
                    "name": "TRANSFER_REQUEST.amount"
                },
                {
                    "data": "currencyName",
                    "name": "currency"
                },

                {
                    "data": "commissionAmount",
                    "name": "TRANSFER_REQUEST.commission"
                },
                {
                    "data": "merchantName",
                    "name": "merchant_name",
                    "render": function (data, type, row) {
                        var merchantName = data;
                        var merchantImageName = '';
                        if (row.merchantImage && row.merchantImage.image_name != merchantName) {
                            merchantImageName = ' ' + row.merchantImage.image_name;
                        }
                        return merchantName + merchantImageName;
                    }
                },
                {
                    "data": "status",
                    "name": "TRANSFER_REQUEST.status_id"
                },
                {
                    "data": "recipientEmail",
                    "name": "recipient_email"
                },
                {
                    "data": "",
                    "name": "",
                    "render": function (data, type, row) {
                        if (data && row.isEndStatus) {
                            return '';
                        } else {
                            return getButtonsSet(row.id, row.sourceType, row.merchantName,
                                    row.buttons, "voucherTable");
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

/**
 * The method for validation transaction hash. Add min and max value for filter-id for transaction id in admin panel on vouchers page
 (min = 0, max = maxValueOfInteger).
 */
$(function () {

    var minValueOfTransactionId = 0;

    //Max value of integer in Java
    var maxValueOfTransactionId = 2147483647;

    //Max length can be maxValueOfTransactionId.toString().length (but in this case need use: length-1;
    var maxCountOfSymbols = maxValueOfTransactionId.toString().length-1;

    $("#filter-id").prop('min', minValueOfTransactionId);
    $("#filter-id").prop('max', maxValueOfTransactionId);
    $("#filter-id").prop('maxlength', maxCountOfSymbols);

    $("#filter-id").on('keyup keypress blur change', function(e) {
        if (!(e.which>47 && e.which<58)) return false;
        if(this.value.charAt(0)=="0") return false;
    });

    $('#filter-id').bind('input', function(){
        var commentText = this.value.length;
        if (commentText > maxCountOfSymbols) {
            this.value = this.value.substr(0, maxCountOfSymbols);
        }

        if (this.value > maxValueOfTransactionId){
            this.value = maxValueOfTransactionId;
            $('#errorValueForTransactionId').show();
        }
        else{
            $('#errorValueForTransactionId').hide();
        }
    });
});