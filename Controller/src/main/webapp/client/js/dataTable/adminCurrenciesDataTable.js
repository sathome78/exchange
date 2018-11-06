/**
 * Created by OLEG on 23.09.2016.
 */
var currencyLimitDataTable;
var currencyPairLimitDataTable;
$(document).ready(function () {

    var $currencyLimitsTable = $('#currency-limits-table');
    var $currencyPairLimitsTable = $('#currency-pair-limits-table');
    var $editCurrencyLimitForm = $('#edit-currency-limit-form');
    var $editCurrencyPairLimitForm = $('#edit-currency-pair-limit-form');


    $('#roleName, #operationType').change(updateCurrencyLimitsDataTable);
    $('#roleName-pair, #orderType').change(updateCurrencyPairLimitsDataTable);
    updateCurrencyLimitsDataTable();
    updateCurrencyPairLimitsDataTable();
    $($currencyLimitsTable).find('tbody').on('click', 'tr', function () {
        var rowData = currencyLimitDataTable.row(this).data();
        var currencyId = rowData.currency.id;
        var currencyName = rowData.currency.name;
        var currentMinLimit = rowData.minSum;
        var currentMaxDailyRequest = rowData.maxDailyRequest;
        var operationType = $('#operationType').val();
        var userRole = $('#roleName').val();
        $($editCurrencyLimitForm).find('input[name="currencyId"]').val(currencyId);
        $('#currency-name').val(currencyName);
        $($editCurrencyLimitForm).find('input[name="operationType"]').val(operationType);
        $($editCurrencyLimitForm).find('input[name="roleName"]').val(userRole);
        $($editCurrencyLimitForm).find('input[name="minAmount"]').val(currentMinLimit);
        $($editCurrencyLimitForm).find('input[name="maxDailyRequest"]').val(currentMaxDailyRequest);
        $('#editLimitModal').modal();
    });

    $($currencyPairLimitsTable).find('tbody').on('click', 'tr', function () {
        var rowData = currencyPairLimitDataTable.row(this).data();
        var currencyId = rowData.currencyPairId;
        var currencyPairName = rowData.currencyPairName;
        var currentMinRate = rowData.minRate;
        var currentMaxRate = rowData.maxRate;
        var currentMinAmount = rowData.minAmount;
        var currentMaxAmount = rowData.maxAmount;
        var orderType = $('#orderType').val();
        var userRole = $('#roleName-pair').val();
        $($editCurrencyPairLimitForm).find('input[name="currencyPairId"]').val(currencyId);
        $('#currency-pair-name').val(currencyPairName);
        $($editCurrencyPairLimitForm).find('input[name="orderType"]').val(orderType);
        $($editCurrencyPairLimitForm).find('input[name="roleName"]').val(userRole);
        $($editCurrencyPairLimitForm).find('input[name="minRate"]').val(currentMinRate);
        $($editCurrencyPairLimitForm).find('input[name="maxRate"]').val(currentMaxRate);
        $($editCurrencyPairLimitForm).find('input[name="minAmount"]').val(currentMinAmount);
        $($editCurrencyPairLimitForm).find('input[name="maxAmount"]').val(currentMaxAmount);
        $('#editPairLimitModal').modal();
    });





    $('#submitNewLimit').click(function(e) {
        e.preventDefault();
        submitNewLimit()
    });

    $('#submitNewPairLimit').click(function(e) {
        e.preventDefault();
        submitNewPairLimit();
    });





});

function updateCurrencyLimitsDataTable() {
    var $currencyLimitsTable = $('#currency-limits-table');
    var userRole = $('#roleName').val();
    var operationType = $('#operationType').val();
    var urlBase = '/2a8fy7b07dxe44/editCurrencyLimits/retrieve';
    var currencyLimitUrl = urlBase + '?roleName=' + userRole + '&operationType=' + operationType;
    if ($.fn.dataTable.isDataTable('#currency-limits-table')) {
        currencyLimitDataTable = $($currencyLimitsTable).DataTable();
        currencyLimitDataTable.ajax.url(currencyLimitUrl).load();
    } else {
        currencyLimitDataTable = $($currencyLimitsTable).DataTable({
            "ajax": {
                "url": currencyLimitUrl,
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
                    "data":"currency.id"
                },
                {
                    "data": "currency.name"
                },
                {
                    "data": "currencyUsdRate",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "minSum",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "minSumUsdRate",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "maxDailyRequest"
                }
            ]
        });
    }
}

function updateCurrencyPairLimitsDataTable() {
    var $currencyPairLimitsTable = $('#currency-pair-limits-table');
    var userRole = $('#roleName-pair').val();
    var orderType = $('#orderType').val();
    var urlBase = '/2a8fy7b07dxe44/editCurrencyLimits/pairs/retrieve';
    var currencyPairLimitUrl = urlBase + '?roleName=' + userRole + '&orderType=' + orderType;
    if ($.fn.dataTable.isDataTable('#currency-pair-limits-table')) {
        currencyPairLimitDataTable = $($currencyPairLimitsTable).DataTable();
        currencyPairLimitDataTable.ajax.url(currencyPairLimitUrl).load();
    } else {
        currencyPairLimitDataTable = $($currencyPairLimitsTable).DataTable({
            "ajax": {
                "url": currencyPairLimitUrl,
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
                    "data":"currencyPairId",
                    "visible": false
                },
                {
                    "data": "currencyPairName"
                },
                {
                    "data": "minRate",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.[0000000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "maxRate",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.[0000000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "minAmount",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.[0000000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "maxAmount",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbro(data).format('0.[0000000000]');
                        }
                        return data;
                    }
                }
            ]
        });
    }
}


function submitNewLimit() {
    var formData =  $('#edit-currency-limit-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/editCurrencyLimits/submit',
        type: 'POST',
        data: formData,
        success: function () {
            updateCurrencyLimitsDataTable();
            $('#editLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editLimitModal').modal('hide');
            console.log(error);
        }
    });
}

function submitNewPairLimit() {
    var formData =  $('#edit-currency-pair-limit-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/editCurrencyLimits/pairs/submit',
        type: 'POST',
        data: formData,
        success: function () {
            updateCurrencyPairLimitsDataTable();
            $('#editPairLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editPairLimitModal').modal('hide');
            console.log(error);
        }
    });
}