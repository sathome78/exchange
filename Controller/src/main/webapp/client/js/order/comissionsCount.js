var dataTable;

$(document).ready(function () {

    $('#commission_withdraw_submit').on('click', function (e) {
        e.preventDefault();
        var formData = $('#commission_withdraw_form').serialize();
        $.ajax({
            url: '/2a8fy7b07dxe44/withdrawCommission/submit',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: formData,
            success: function (e) {
                successNoty('Success');
                updateDataTable();
            },
            error: function (e) {
            }
        })
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

    var date = new Date();
    date.setMonth(date.getMonth() - 1);
    var date2 = new Date();
    date2.setDate(date.getDate() - 7);
    date2.setMonth(date.getMonth());

    $('#datetimepicker_start').datetimepicker({
        format: "YYYY-MM-DD_HH-mm-ss",
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH-mm-ss',
        lang: 'en',
        value: date2,
        defaultDate: date2,
        defaultTime: '00:00'
    });

    $('#datetimepicker_end').datetimepicker({
        format: "YYYY-MM-DD_HH-mm-ss",
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH-mm-ss',
        lang: 'en',
        value: date,
        defaultDate: date,
        defaultTime: '00:00'
    });

    updateDataTable();

    $('#submit-info__search').on('click', function (e) {
        e.preventDefault();
        updateDataTable();
    });

});


function updateDataTable() {
    var $table = $('#comissions-count-table');
    var dateParams = $('#dates-form-info').serialize();
    var url = '/2a8fy7b07dxe44/comission_count/get?' + dateParams;
    if ($.fn.dataTable.isDataTable('#comissions-count-table')) {
        dataTable = $($table).DataTable();
        dataTable.ajax.url(url).load();
    } else {
        dataTable = $($table).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "bFilter": false,
            "paging": false,
            "order": [
                [
                    6,
                    "desc"
                ]
            ],
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "refillComission"
                },
                {
                    "data": "withdrawComission"
                },
                {
                    "data": "transferComission"
                },
                {
                    "data": "tradeComission"
                },
                {
                    "data": "referralPayments"
                },
                {
                    "data": "total"
                },
                {
                    "data": "totalWithdrawed"
                },
                {
                    "data": "commissionBalance"
                }
            ]
        });
    }
}
