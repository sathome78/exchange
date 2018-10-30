var externalWalletsDataTable;
var globalTitle;

$(document).ready(function () {

    var $externalWalletsTable = $('#external-wallets-table');
    var $editExternalWalletsForm = $('#edit-external-wallets-form');

    globalTitle = document.getElementsByClassName("modal-title")[0].innerText;

    updateExternalWalletsTable();
    getSummaryInUSD();
    getSummaryInBTC();
    $($externalWalletsTable).find('tbody').on('click', 'tr', function () {
        var rowData = externalWalletsDataTable.row(this).data();
        var currencyId = rowData.currencyId;
        var currencyName = rowData.currencyName;
        var usdRate = rowData.usdRate;
        var btcRate = rowData.btcRate;
        var mainBalance = rowData.mainBalance;
        var reservedBalance = rowData.reservedBalance;

        $('.modal-title').text(globalTitle.concat(' ').concat(currencyName));
        $('#usd-rate-label').text(usdRate);
        $('#btc-rate-label').text(btcRate);
        $('#main-balance-label').text(mainBalance);
        $('#currencyIdForPopUp').text(currencyId);

        getReservedWallets(currencyId);

        $($editExternalWalletsForm).find('input[name="currencyId"]').val(currencyId);
        $($editExternalWalletsForm).find('input[name="usdRate"]').val(usdRate);
        $($editExternalWalletsForm).find('input[name="btcRate"]').val(btcRate);
        $($editExternalWalletsForm).find('input[name="mainBalance"]').val(mainBalance);
        $($editExternalWalletsForm).find('input[name="reservedBalance"]').val(reservedBalance);

        $('#editBalanceModal').modal();
    });

    $(window).on('shown.bs.modal', function () {
        $('#editBalanceModal').modal('show');
        updateReservedWallets();
    });

    $("#editBalanceModal").on('shown', function () {
        alert("I want this to appear after the modal has opened!");
    });

    $('#addNewReserdedWallet').click(function () {
        addReservedWallet($('#currencyIdForPopUp').text());
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
                    "data": "currencyId",
                },
                {
                    "data": "currencyName"
                },
                {
                    "data": "usdRate",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "btcRate",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "mainBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "reservedBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "totalBalance",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "totalBalanceUSD",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "totalBalanceBTC",
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "lastUpdatedDate"
                }
            ]
        });
    }
}

function numbroWithCommas(value) {
    return numbro(value).format('0.00[000000]').toString().replace(/\./g, ',');
}

function getSummaryInUSD() {
    $.ajax({
        type: "GET",
        url: "/2a8fy7b07dxe44/externalWallets/retrieve/summaryUSD",
        success: function (data) {
            $('#summary-in-usd').text(data);
        },
        error: function (data) {
            alert('Something happened wrong');
        }
    });
}

function getSummaryInBTC() {
    $.ajax({
        type: "GET",
        url: "/2a8fy7b07dxe44/externalWallets/retrieve/summaryBTC",
        success: function (data) {
            $('#summary-in-btc').text(data);
        },
        error: function (data) {
            alert('Something happened wrong');
        }
    });
}

function getReservedWallets(currencyId) {
    $.ajax({
        type: "GET",
        url: "/2a8fy7b07dxe44/externalWallets/retrieve/reservedWallets/" + currencyId,
        success: function (data) {
            reservedWallets = data;
        },
        error: function (data) {
            alert('Something happened wrong');
        }
    });
}

function addReservedWallet(currencyIdForPopUp) {
    $.ajax({
        url: '/2a8fy7b07dxe44/externalWallets/address/create',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "currencyId": currencyIdForPopUp
        },
        success: function () {
            updateReservedWallets();
        },
        error: function (err) {
            console.log(err);
        }
    });
}

var idReservedWalletsTable = '#reservedWallets';

function removeReservedWallet(elem) {
    if (confirm("Are you sure that you want remove this reserved wallet?")) {
        var row = $(elem).parents('tr');
        var data = $(idReservedWalletsTable).dataTable().fnGetData(row);

        $.ajax({
            url: '/2a8fy7b07dxe44/externalWallets/address/delete/' + data.id,
            type: 'DELETE',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            success: function () {
                updateReservedWallets();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }
}

function saveAsAddress(elem) {
    var row = $(elem).parents('tr');
    var data = $(idReservedWalletsTable).dataTable().fnGetData(row);


    var walletAddress = "#inputWalletAddress" + data.id;

    $.ajax({
        url: '/2a8fy7b07dxe44/externalWallets/address/saveAsAddress/submit',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "id": data.id,
            "currencyId": data.currencyId,
            "walletAddress": $(walletAddress).val()
        },
        success: function () {
            updateReservedWallets();
        },
        error: function (err) {
            errorNoty(err);
            console.log(err);
        }
    });
}

function saveAsName(elem) {
    var row = $(elem).parents('tr');
    var data = $(idReservedWalletsTable).dataTable().fnGetData(row);

    var walletAddress = "#inputWalletAddress" + data.id;
    var balance = "#inputBalanceForWalletAddress" + data.id;

    $.ajax({
        url: '/2a8fy7b07dxe44/externalWallets/address/saveAsName/submit',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "id": data.id,
            "currencyId": data.currencyId,
            "walletAddress": $(walletAddress).val(),
            "reservedWalletBalance": $(balance).val()
        },
        success: function () {
            updateReservedWallets();
        },
        error: function (err) {
            console.log(err);
        }
    });
}

function saveGeneral(elem) {
    var row = $(elem).parents('tr');
    var data = $(idReservedWalletsTable).dataTable().fnGetData(row);
    var rowId = row.index() + 1;

    var nameReservedWallet = $("#labelReservedWalletBalance").text() + rowId;

    var name = "#inputNameWalletAddress" + data.id;
    var walletAddress = "#inputWalletAddress" + data.id;
    var balance = "#inputBalanceForWalletAddress" + data.id;

    var dataForRequest;
    if ($(name).val() === nameReservedWallet) {
        dataForRequest = {
            "id": data.id,
            "currencyId": data.currencyId,
            "walletAddress": $(walletAddress).val(),
            "reservedWalletBalance": $(balance).val()
        };
    } else {
        dataForRequest = {
            "id": data.id,
            "currencyId": data.currencyId,
            "name": $(name).val(),
            "walletAddress": $(walletAddress).val(),
            "reservedWalletBalance": $(balance).val()
        };
    }

    $.ajax({
        url: '/2a8fy7b07dxe44/externalWallets/address/saveAsName/submit',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: dataForRequest,
        success: function () {
            updateReservedWallets();
        },
        error: function (err) {
            console.log(err);
        }
    });
}

function changeReservedWallet(elem) {
    var row = $(elem).parents('tr');
    var data = $(idReservedWalletsTable).dataTable().fnGetData(row);

    var tdName = "td:nth-child(1)";
    var tdWalletAddress = "td:nth-child(2)";
    var tdWalletBalance = "td:nth-child(3)";

    var textNameReserverWallet = "#nameReservedWallet" + data.id;

    row.find(tdName).html('<input name="currencyId" id="inputNameWalletAddress' + data.id + '" style="width: 100%; text-align: justify;" value="' + $(textNameReserverWallet).text() + '"/>');
    row.find(tdWalletAddress).html('<input name="walletAddress" id="inputWalletAddress' + data.id + '" value="' + data.walletAddress + '" style="text-align:right;"/>' +
        '<button id="buttonSaveAsName' + data.id + '" onclick="saveGeneral(this)" style="float: left; width: 100%; height: 14px; font-size: 8px;">Save</button>');
    row.find(tdWalletBalance).html('<input name="balance" id="inputBalanceForWalletAddress' + data.id + '" value="' + data.balance + '" style="text-align:right;"/>');

}

function updateReservedWallets() {
    var currencyId = $('#currencyIdForPopUp').text();
    var urlReservedWalletsForCurrency = '/2a8fy7b07dxe44/externalWallets/retrieve/reservedWallets/' + currencyId;

    if ($.fn.dataTable.isDataTable(idReservedWalletsTable)) {
        $(idReservedWalletsTable).DataTable().ajax.url(urlReservedWalletsForCurrency).load();
    } else {
        $(idReservedWalletsTable).DataTable({
            "ajax": {
                "url": urlReservedWalletsForCurrency,
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
                    "data": "name",
                    "render": function (data, type, full, meta) {
                        var text = $("#labelReservedWalletBalance").text();
                        if (full.walletAddress != null) {

                            var htmlTextWalletAddress;
                            if (data == null) {
                                htmlTextWalletAddress = '<span id="nameReservedWallet' + full.id + '">' + text + (meta.row + 1) + '</span><br/>';
                            } else {
                                htmlTextWalletAddress = '<span id="nameReservedWallet' + full.id + '">' + data + '</span><br/>';
                            }

                            return htmlTextWalletAddress + ''
                                + '<button id="buttonChangeReservedWallet' + full.id + '" onclick="changeReservedWallet(this)" style="float: left; width: 50%; height: 14px; font-size: 8px;">Change</button>'
                                + '<button id="buttonRemoveReservedWallet' + full.id + '" onclick="removeReservedWallet(this)" style="float: right; width: 50%; height:14px; font-size: 8px;">Remove</button>';
                        } else {
                            return '<span>' + text + (meta.row + 1) + '</span>';
                        }
                    }
                },
                {
                    "data": "walletAddress",
                    "render": function (data, type, row) {
                        if (data == null) {
                            return '<input name="walletAddress" id="inputWalletAddress' + row.id + '" style="width: 100%; text-align: justify;"/>'
                                + '<button id="buttonSaveAsAddress' + row.id + '" onclick="saveAsAddress(this)" style="float: left; width: 50%; height:14px; font-size: 8px;">Save as address</button>'
                                + '<button id="buttonSaveAsName' + row.id + '" onclick="saveAsName(this)" style="float: right; width: 50%; height:14px; font-size: 8px;">Save as name</button>';
                        } else return data;
                    }
                },
                {
                    "data": "balance",
                    "render": function (data, type, row) {
                        if (row.walletAddress != null) {
                            return '<span style="float:right;">' + data + '</span>';
                        } else {
                            return '<input name="balance" id="inputBalanceForWalletAddress' + row.id + '" value="' + data + '" style="text-align:right;"/>';
                        }
                    }
                },
                {
                    "data": "id",
                    "visible": false
                }
            ]
        });
    }
}

