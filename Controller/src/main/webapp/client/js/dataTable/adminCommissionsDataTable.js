/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;
var commissionsDataTable;
var merchantsCommissionsDataTable;
var transfersDataTable;

$(document).ready(function () {
    var $commissionTable = $('#commissions-table');
    var $merchantCommissionTable = $('#merchant-commissions-table');
    var $transferCommissionTable = $('#transfer-commissions-table');
    var $commissionForm = $('#edit-commission-form');
    var $editTransferForm = $('#edit-transferCommission-form');
    var $merchantCommissionForm = $('#edit-merchantCommission-form');
    var $roleNameSelect = $('#roleName');
    currentLocale = $('#language').text().trim().toLowerCase();

    $($transferCommissionTable).on('click', 'tr', function () {
        var rowData = transfersDataTable.row(this).data();
        var merchantName = rowData.merchantName;
        var currencyName = rowData.currencyName;
        var transferCommissionValue = parseFloat(rowData.transferCommission);
        var minFixedOutputCommission = parseFloat(rowData.minFixedCommission);
        $($editTransferForm).find('input[name="merchantName"]').val(merchantName);
        $($editTransferForm).find('input[name="currencyName"]').val(currencyName);
        $($editTransferForm).find('input[name="transferValue"]').val(transferCommissionValue);
        $($editTransferForm).find('input[name="minFixedAmount"]').val(minFixedOutputCommission);
        $('#editTransferCommissionModal').modal();
    });

    $($commissionTable).on('click', 'tr', function () {
        var currentRoleName = $($roleNameSelect).val();
        var rowData = commissionsDataTable.row(this).data();
        var operationType = rowData.operationType;
        var commissionValue = parseFloat(rowData.value);
        $($commissionForm).find('input[name="userRole"]').val(currentRoleName);
        $('#operationType').val(operationType);
        $($commissionForm).find('input[name="commissionValue"]').val(commissionValue);
        $('#editCommissionModal').modal();
    });

    $($merchantCommissionTable).on('click', 'tr', function (event) {
        var target = $(event.target);
        if(!target.is('input#chkbox')) {
            var rowData = merchantsCommissionsDataTable.row(this).data();
            var merchantName = rowData.merchantName;
            var currencyName = rowData.currencyName;
            var inputCommissionValue = parseFloat(rowData.inputCommission);
            var outputCommissionValue = parseFloat(rowData.outputCommission);
            var minFixedOutputCommission = parseFloat(rowData.minFixedCommission);
            var minFixedOutputCommissionUsdRate = parseFloat(rowData.minFixedCommissionUsdRate);
            var usdRate = parseFloat(rowData.currencyUsdRate);
            $('#merchantName').val(merchantName);
            $('#currencyName').val(currencyName);
            $($merchantCommissionForm).find('input[name="merchantName"]').val(merchantName);
            $($merchantCommissionForm).find('input[name="currencyName"]').val(currencyName);
            $($merchantCommissionForm).find('input[name="inputValue"]').val(inputCommissionValue);
            $($merchantCommissionForm).find('input[name="outputValue"]').val(outputCommissionValue);
            $($merchantCommissionForm).find('input[name="minFixedAmount"]').val(minFixedOutputCommission);
            $($merchantCommissionForm).find('input[name="minFixedAmountUSD"]').val(minFixedOutputCommissionUsdRate);
            $($merchantCommissionForm).find('input[name="usdRate"]').val(usdRate);
            $('#editMerchantCommissionModal').modal();
        }
    });

    $('input#minFixedAmount').keyup(function() {
        var usdRate = $('#usdRate').val();
        var amount = $(this).val() * usdRate;

        $('input#minFixedAmountUSD').val(amount);
    });

    $('input#minFixedAmountUSD').keyup(function() {
        var usdRate = $('#usdRate').val();
        var amount = $(this).val() / usdRate;

        if (amount === Infinity || isNaN(amount)) {
            $('input#minFixedAmount').val('0');
        } else {
            formatMinAmount(amount);
        }
    });

    function formatMinAmount(amount) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/2a8fy7b07dxe44/editCurrencyLimits/convert-min-sum',
            type: 'GET',
            data: {
                "minSum": amount
            },
            success: function (data) {
                $('input#minFixedAmount').val(data);
            },
            error: function (error) {
                console.log(error);
                $('input#minFixedAmount').val('0');
            }
        });
    }

    $($merchantCommissionTable).on('click', 'i', function (e) {
        var event = e || window.event;
        event.stopPropagation();
        var row = $(this).parents('tr');
        var rowData = merchantsCommissionsDataTable.row(row).data();
        var merchantName = rowData.merchantName;
        var currencyName = rowData.currencyName;
        var subtractFromWithdraw = !rowData.isMerchantCommissionSubtractedForWithdraw;
        toggleSubtractMerchantCommissionForWithdraw(merchantName, currencyName, subtractFromWithdraw)
    });




    updateCommissionsDataTable();
    updateMerchantCommissionsDataTable();
    updateTransferCommissionTable();
    $($roleNameSelect).on("change", updateCommissionsDataTable);



    $('#submitCommission').click(function(e) {
        e.preventDefault();
        submitCommission()
    });

    $('#submitTransferCommission').click(function(e) {
        e.preventDefault();
        submitTransferCommission();
    });

    $('#submitMerchantCommission').click(function(e) {
        e.preventDefault();
        submitMerchantCommission()
    });


});


function updateMerchantCommissionsDataTable() {
    var $merchantCommissionTable = $('#merchant-commissions-table');
    var merchantCommissionUrl = '/2a8fy7b07dxe44/getMerchantCommissions';
    if ($.fn.dataTable.isDataTable('#merchant-commissions-table')) {
        merchantsCommissionsDataTable = $($merchantCommissionTable).DataTable();
        merchantsCommissionsDataTable.ajax.url(merchantCommissionUrl).load();
    } else {
        merchantsCommissionsDataTable = $($merchantCommissionTable).DataTable({
            "ajax": {
                "url": merchantCommissionUrl,
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
                    "data": "merchantName"
                },
                {
                    "data": "currencyName"
                },
                {
                    "data": "inputCommission",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "outputCommission",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
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
                    "data": "minFixedCommission",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "minFixedCommissionUsdRate",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "isMerchantCommissionSubtractedForWithdraw",
                    "render": function (data) {
                        return '<span>'.concat(data ? '<i class="fa fa-check green text-1_5"></i>' : '<i class="fa fa-close red text-1_5"></i>')
                            .concat('</span>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "recalculateToUsd",
                    "render": function (data, type, row) {
                        var merchantName = row.merchantName;
                        var currencyName = row.currencyName;

                        var checkbox;
                        if (data) {
                            checkbox = '<input id="chkbox" type="checkbox" name="chkbox" ' +
                                'onchange="setPropertyRecalculateLimitToUsd(this, \'' + merchantName + '\', \'' + currencyName + '\')" checked />';
                        } else {
                            checkbox = '<input id="chkbox" type="checkbox" name="chkbox" ' +
                                'onchange="setPropertyRecalculateLimitToUsd(this, \'' + merchantName + '\', \'' + currencyName + '\')"/>';
                        }
                        return checkbox;
                    }
                }
            ]
        });
    }
}

function setPropertyRecalculateLimitToUsd(elem, merchantName, currencyName) {
    $('#editMerchantCommissionModal').modal('hide');
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantCommissions/recalculate-commission-limit-to-usd',
        type: 'POST',
        data: {
            "merchantName": merchantName,
            "currencyName": currencyName,
            "recalculateToUsd": elem.checked
        },
        success: function () {
            updateMerchantCommissionsDataTable()
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function updateTransferCommissionTable() {
    var $transferCommissionTable = $('#transfer-commissions-table');
    var transfersCommissionUrl = '/2a8fy7b07dxe44/getMerchantTransferCommissions';
    if ($.fn.dataTable.isDataTable('#transfer-commissions-table')) {
        transfersDataTable = $($transferCommissionTable).DataTable();
        transfersDataTable.ajax.url(transfersCommissionUrl).load();
    } else {
        transfersDataTable = $($transferCommissionTable).DataTable({
            "ajax": {
                "url": transfersCommissionUrl,
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
                    "data": "merchantName"
                },
                {
                    "data": "currencyName"
                },
                {
                    "data": "transferCommission"
                },
                {
                    "data": "minFixedCommission"
                }
            ]
        });
    }
}

function updateCommissionsDataTable() {
    var $commissionTable = $('#commissions-table');
    var currentRoleName = $('#roleName').val();
    var commissionUrlBase = '/2a8fy7b07dxe44/getCommissionsForRole?role=';
    var commissionUrl = commissionUrlBase + currentRoleName;
    if ($.fn.dataTable.isDataTable('#commissions-table')) {
        commissionsDataTable = $($commissionTable).DataTable();
        commissionsDataTable.ajax.url(commissionUrl).load();
    } else {
        commissionsDataTable = $($commissionTable).DataTable({
            "ajax": {
                "url": commissionUrl,
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
                    "data": "operationType",
                    "render": function (data, type, row) {
                        return row.operationTypeLocalized;
                    }
                },
                {
                    "data": "value"
                }
            ]
        });
    }
}

function submitCommission() {
    var formData =  $('#edit-commission-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editCommission',
        type: 'POST',
        data: formData,
        success: function () {
            updateCommissionsDataTable();

            $('#editCommissionModal').modal('hide');
        },
        error: function (error) {
            $('#editCommissionModal').modal('hide');
            console.log(error);
        }
    });
}

function submitMerchantCommission() {
    var formData =  $('#edit-merchantCommission-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editMerchantCommission',
        type: 'POST',
        data: formData,
        success: function () {
            updateMerchantCommissionsDataTable();
            $('#editMerchantCommissionModal').modal('hide');
        },
         error: function (error) {
         $('#editCommissionModal').modal('hide');
         console.log(error);
         }
    });
}

function submitTransferCommission() {
    var formData =  $('#edit-transferCommission-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editMerchantCommission',
        type: 'POST',
        data: formData,
        success: function () {
            updateTransferCommissionTable();
            $('#editTransferCommissionModal').modal('hide');
        },
        error: function (error) {
            $('#editTransferCommissionModal').modal('hide');
            console.log(error);
        }
    });
}

function toggleSubtractMerchantCommissionForWithdraw(merchantName, currencyName, subtractFromWithdraw) {
    var formData = new FormData();
    formData.append("merchantName", merchantName);
    formData.append("currencyName", currencyName);
    formData.append("subtractMerchantCommissionForWithdraw", subtractFromWithdraw);
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editMerchantCommission/toggleSubtractWithdraw',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            updateMerchantCommissionsDataTable();
        }
    });
}
