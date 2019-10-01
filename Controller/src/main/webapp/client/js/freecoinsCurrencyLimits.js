var currencyLimitDataTable;

$(document).ready(function () {
    loadCurrencyLimitsTable();

    $('#currencyLimitsTable').find('tbody').on('click', 'tr', function (event) {
        var target = $(event.target);
        if (!target.is('input#chkbox')) {
            var rowData = currencyLimitDataTable.row(this).data();
            var currencyId = rowData.currency_id;
            var currencyName = rowData.currency_name;
            var currencyMinAmount = rowData.min_amount;
            var currencyMinPartialAmount = rowData.min_partial_amount;

            $('#currency_id').val(currencyId);
            $('#currency_name').val(currencyName);
            $('#min_amount').val(numbro(currencyMinAmount).format('0.00[000000]'));
            $('#min_partial_amount').val(numbro(currencyMinPartialAmount).format('0.00[000000]'));
            $('#editLimitModal').modal();
        }
    });

    $('#submitNewLimit').click(function (e) {
        e.preventDefault();
        submitNewLimit()
    });
});

function loadCurrencyLimitsTable() {
    var url = '/2a8fy7b07dxe44/free-coins/settings';
    if ($.fn.dataTable.isDataTable('#currencyLimitsTable')) {
        currencyLimitDataTable = $('#currencyLimitsTable').DataTable();
        currencyLimitDataTable.ajax.url(url).load();
    } else {
        currencyLimitDataTable = $('#currencyLimitsTable').DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "order": [
                [
                    0,
                    "asc"
                ]
            ],
            "deferRender": true,
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "currency_id",
                },
                {
                    "data": "currency_name"
                },
                {
                    "data": "min_amount",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "min_partial_amount",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                }
            ]
        });
    }
}

function submitNewLimit() {
    var formData = $('#editCurrencyLimitForm').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/free-coins/settings/update',
        type: 'POST',
        data: formData,
        success: function () {
            loadCurrencyLimitsTable();
            $('#editLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editLimitModal').modal('hide');
            console.log(error);
        }
    });
}