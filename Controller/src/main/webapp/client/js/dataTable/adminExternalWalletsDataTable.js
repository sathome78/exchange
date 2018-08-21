/**
 * Created by OLEG on 23.09.2016.
 */
var externalWalletsDataTable;
var specMinUsdDataTable;
$(document).ready(function () {

    var $externalWalletsTable = $('#external-wallets-table');
    var $editExternalWalletsForm = $('#edit-external-wallets-form');
    var $specMinUsdTable = $('#spec-min-usd');
    var $commissionForm = $('#edit-specSum-form');

    updateSpecMinUsdTable();
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

    $($specMinUsdTable).on('click', 'tr', function () {
        var rowData = specMinUsdDataTable.row(this).data();
        var paramName = rowData.paramName;
        var specSumForUsd = parseFloat(rowData.paramValue);
        $($commissionForm).find('input[name="paramName"]').val(paramName);
        $($commissionForm).find('input[name="specSumForUsd"]').val(specSumForUsd);
        $('#editMinSumModal').modal();
    });


    $('#submitNewBalance').click(function(e) {
        e.preventDefault();
        submitNewBalance()
    });

    $('#submitSpecSum').click(function(e) {
        e.preventDefault();
        submitSpecSum()
    });

});

function updateSpecMinUsdTable() {
    var $specMinUsdTable = $('#spec-min-usd');
    var url = '/2a8fy7b07dxe44/externalWallets/minusd';
    if ($.fn.dataTable.isDataTable('#spec-min-usd')) {
        specMinUsdDataTable = $($specMinUsdTable).DataTable();
        specMinUsdDataTable.ajax.url(url).load();
    } else {
        specMinUsdDataTable = $($specMinUsdTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "paging": false,
            "bSort": false,
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columnDefs": [ {
                "targets": [ 0, 1],
                "orderable": false
            } ],
            "searching": false,
            "columns": [
                {
                    "data":"paramName",
                    "render": function (data, type, row) {
                           if(data == 'min_sum_usd'){
                               return '<label class="table-button-block__button">' + minValueUsd + '</label>'
                           } else if (data == 'min_commission_usd'){
                               return '<label class="table-button-block__button">' + minCommissionUsd + '</label>'
                           }

                    }
                },
                {
                    "data": "paramValue"
                }
            ]
        });
    }
}

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

function submitSpecSum() {
    var formData =  $('#edit-specSum-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/externalWallets/editMinSum',
        type: 'POST',
        data: formData,
        success: function () {
            updateSpecMinUsdTable();

            $('#editMinSumModal').modal('hide');
        },
        error: function (error) {
            $('#editMinSumModal').modal('hide');
            console.log(error);
        }
    });
}
