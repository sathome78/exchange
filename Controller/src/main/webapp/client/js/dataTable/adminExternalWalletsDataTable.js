/**
 * Created by OLEG on 23.09.2016.
 */
var externalWalletsDataTable;
$(document).ready(function () {

    var $externalWalletsTable = $('#external-wallets-table');
    var $editExternalWalletsForm = $('#edit-external-wallets-form');

    updateExternalWalletsTable();
    $($externalWalletsTable).find('tbody').on('click', 'tr', function () {
        var rowData = externalWalletsDataTable.row(this).data();
        var currencyId = rowData.currencyId;
        var currencyName = rowData.currencyName;
        var rateUsdAdditional = rowData.rateUsdAdditional;
        var mainWalletBalance = rowData.mainWalletBalance;
        var reservedWalletBalance = rowData.reservedWalletBalance;
        var coldWalletBalance = rowData.coldWalletBalance;
        $($editExternalWalletsForm).find('input[name="currencyId"]').val(currencyId);
        $('#currency-name').val(currencyName);
        $($editExternalWalletsForm).find('input[name="rateUsdAdditional"]').val(rateUsdAdditional);
        $($editExternalWalletsForm).find('input[name="mainWalletBalance"]').val(mainWalletBalance);
        $($editExternalWalletsForm).find('input[name="reservedWalletBalance"]').val(reservedWalletBalance);
        $($editExternalWalletsForm).find('input[name="coldWalletBalance"]').val(coldWalletBalance);
        $('#editBalanceModal').modal();
    });


    $('#submitNewBalance').click(function(e) {
        e.preventDefault();
        submitNewBalance()
    });

});

function updateExternalWalletsTable() {
    var $externalWalletsTable = $('#external-wallets-table');
    var urlBase = '/2a8fy7b07dxe44/externalWallets/retrieve';
    var externalWalletsUrl = urlBase;
    if ($.fn.dataTable.isDataTable('#external-wallets-table')) {
        externalWalletsDataTable = $($externalWalletsTable).DataTable();
        externalWalletsDataTable.ajax.url(externalWalletsUrl).load();
    } else {
        externalWalletsDataTable = $($externalWalletsTable).DataTable({
            "ajax": {
                "url": externalWalletsUrl,
                "dataSrc": ""
            },
            "bFilter": false,
            "paging": false,
            "order": [],
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columns": [
                {
                    "data":"currencyId"
                },
                {
                    "data": "currencyName"
                },
                {
                    "data": "rateUsdAdditional",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "mainWalletBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "mainWalletBalanceUSD",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "reservedWalletBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "coldWalletBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "totalWalletsBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "totalWalletsBalanceUSD",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                }
            ]
        });
    }
}

function numbroWithCommas(value) {

    return numbro(value).format('0.00[000000]').toString().replace(/\./g, ',');
}

function submitNewBalance() {
    var formData =  $('#edit-external-wallets-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/externalWallets/submit',
        type: 'POST',
        data: formData,
        success: function () {
            updateExternalWalletsTable();
            $('#editBalanceModal').modal('hide');
        },
        error: function (error) {
            $('#editBalanceModal').modal('hide');
            console.log(error);
        }
    });
}

