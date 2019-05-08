
var $withdrawalPage;
var $addressesTable;
var addressesDataTable;
var transferRequestsBaseUrl;
var filterParams;

$(function () {


    $withdrawalPage = $('#addresses-admin');
    $addressesTable = $('#addressesTable');
    filterParams = '';
    transferRequestsBaseUrl = '/2a8fy7b07dxe44/refillAddresses/table?';
    $('#withdraw-requests-manual').addClass('active');


    updateAddressesTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#addresses-search-form').serialize();
        updateAddressesTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#addresses-search-form')[0].reset();
        filterParams = '';
        updateAddressesTable();
    });

    $($addressesTable).on('click', 'input.copyable', function (e) {
        selectAndCopyInputValue(this);
    });

});




function updateAddressesTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = transferRequestsBaseUrl + filter;
    if ($.fn.dataTable.isDataTable('#addressesTable')) {
        addressesDataTable = $addressesTable.DataTable();
        addressesDataTable.ajax.url(url).load();
    } else {
        addressesDataTable = $addressesTable.DataTable({
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
                    "data": "userEmail",
                    "name": "USER.email"
                },
                {
                    "data": "currencyName",
                    "name": "CU.name"
                },
                {
                    "data": "address",
                    "name": "RRA.address",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            var address = data == null ? '' : data;
                            return '<input class="form-control copyable" value="' + address + '">';
                        }
                        return data;
                    }
                },
                {
                    "data": "addressFieldName",
                    "name": "CU.name"
                },
                {
                    "data": "generationDate",
                    "name": "RRA.date_generation",
                    "className": "text-center"
                },
                {
                    "data": "needTransfer",
                    "name": "RRA.need_transfer",
                    "className": "text-center",
                    "render": function (data, type, row) {
                        var userId = row.userId;
                        var currencyId = row.currencyId;
                        var merchantId = row.merchantId;
                        var address = row.address;

                        var checkbox;
                        if (data) {
                            checkbox = '<input id="chkbox" type="checkbox" name="chkbox" ' +
                                'onchange="setNeedTransfer(this, \'' + userId + '\', \'' + currencyId + '\', \'' + merchantId + '\', \'' + address + '\')" checked />';
                        } else {
                            checkbox = '<input id="chkbox" type="checkbox" name="chkbox" ' +
                                'onchange="setNeedTransfer(this, \'' + userId + '\', \'' + currencyId + '\', \'' + merchantId + '\', \'' + address + '\')" />';
                        }
                        return checkbox;
                    }
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']]
        });
    }
}

function setNeedTransfer(elem, userId, currencyId, merchantId, address) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/refillAddresses/set-need-transfer',
        type: 'POST',
        data: {
            "userId": userId,
            "currencyId": currencyId,
            "merchantId": merchantId,
            "address": address,
            "needTransfer": elem.checked
        },
        success: function () {
            updateAddressesTable();
        },
        error: function (error) {
            console.log(error);
        }
    });
}