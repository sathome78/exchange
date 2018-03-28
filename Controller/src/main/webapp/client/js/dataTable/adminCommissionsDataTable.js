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
        var merchantId = rowData.merchantId;
        var currencyId = rowData.currencyId;
        var merchantName = rowData.merchantName;
        var currencyName = rowData.currencyName;
        var transferCommissionValue = parseFloat(rowData.transferCommission);
        var minFixedOutputCommission = parseFloat(rowData.minFixedCommission);
        $($editTransferForm).find('input[name="merchantId"]').val(merchantId);
        $($editTransferForm).find('input[name="currencyId"]').val(currencyId);
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

    $($merchantCommissionTable).on('click', 'tr', function () {
        var rowData = merchantsCommissionsDataTable.row(this).data();
        var merchantId = rowData.merchantId;
        var currencyId = rowData.currencyId;
        var merchantName = rowData.merchantName;
        var currencyName = rowData.currencyName;
        var inputCommissionValue = parseFloat(rowData.inputCommission);
        var outputCommissionValue = parseFloat(rowData.outputCommission);
        var minFixedOutputCommission = parseFloat(rowData.minFixedCommission);
        $($merchantCommissionForm).find('input[name="merchantId"]').val(merchantId);
        $($merchantCommissionForm).find('input[name="currencyId"]').val(currencyId);
        $('#merchantName').val(merchantName);
        $('#currencyName').val(currencyName);
        $($merchantCommissionForm).find('input[name="inputValue"]').val(inputCommissionValue);
        $($merchantCommissionForm).find('input[name="outputValue"]').val(outputCommissionValue);
        $($merchantCommissionForm).find('input[name="minFixedAmount"]').val(minFixedOutputCommission);
        $('#editMerchantCommissionModal').modal();
    });

    $($merchantCommissionTable).on('click', 'i', function (e) {
        var event = e || window.event;
        event.stopPropagation();
        var row = $(this).parents('tr');
        var rowData = merchantsCommissionsDataTable.row(row).data();
        var merchantId = rowData.merchantId;
        var currencyId = rowData.currencyId;
        var subtractFromWithdraw = !rowData.isMerchantCommissionSubtractedForWithdraw;
        toggleSubtractMerchantCommissionForWithdraw(merchantId, currencyId, subtractFromWithdraw)
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
                    "data": "inputCommission"
                },
                {
                    "data": "outputCommission",
                    "render": function (data, type, row) {
                        if (row.merchantFixedComission === true) {
                            return data + ' ' + row.currencyName;
                        } else {
                            return data + ' %';
                        }
                    }
                },
                {
                    "data": "minFixedCommission"
                },
                {
                    "data": "isMerchantCommissionSubtractedForWithdraw",
                    "render": function (data) {
                        return '<span>'.concat(data ? '<i class="fa fa-check green text-1_5"></i>' : '<i class="fa fa-close red text-1_5"></i>')
                            .concat('</span>');
                    },
                    "className": "text-center"
                }
            ]
        });
    }
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

function toggleSubtractMerchantCommissionForWithdraw(merchantId, currencyId, subtractFromWithdraw) {
    var formData = new FormData();
    formData.append("merchantId", merchantId);
    formData.append("currencyId", currencyId);
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
