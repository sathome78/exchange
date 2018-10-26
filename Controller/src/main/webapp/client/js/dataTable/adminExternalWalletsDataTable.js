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

        getReservedWallets(currencyId);

        // $($editExternalWalletsForm).find('input[name="currencyId"]').val(currencyId);
        // // $('#currency-name').val(currencyName);
        // $($editExternalWalletsForm).find('input[name="usdRate"]').val(usdRate);
        // $($editExternalWalletsForm).find('input[name="btcRate"]').val(btcRate);
        // $($editExternalWalletsForm).find('input[name="mainBalance"]').val(mainBalance);
        // $($editExternalWalletsForm).find('input[name="reservedBalance"]').val(reservedBalance);
        // $('#editBalanceModal').modal();
    });


    // $('#submitNewBalance').click(function (e) {
    //     e.preventDefault();
    //     submitNewBalance()
    // });

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
                    "data": "currencyId"
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

// function submitNewBalance() {
//     var formData = $('#edit-external-wallets-form').serialize();
//     $.ajax({
//         headers: {
//             'X-CSRF-Token': $("input[name='_csrf']").val()
//         },
//         url: '/2a8fy7b07dxe44/externalWallets/submit',
//         type: 'POST',
//         data: formData,
//         success: function () {
//             updateExternalWalletsTable();
//             $('#editBalanceModal').modal('hide');
//         },
//         error: function (error) {
//             $('#editBalanceModal').modal('hide');
//             console.log(error);
//         }
//     });
// }

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

var id = 0;

function addFile() {
    id++;
    var html = '<input name="wallet" type="text" placeholder="Address/Name">' +
        '       <input name="balance" type="number" min="0" placeholder="Balance">' +
        '       <input name="remove" type="button" onclick="javascript:saveAsAddress(this); return false;" value="Save as address">' +
        '       <input name="remove" type="button" onclick="javascript:saveAsName(this); return false;" value="Save as name">' +
        '       <input name="remove" type="button" onclick="javascript:removeElement(this); return false;" value="Remove">';
    addElement('reserved-wallets-id', 'div', id, html);
}

function addElement(parentId, elementTag, id, html) {
    var parent = document.getElementById(parentId);
    var newElement = document.createElement(elementTag);
    newElement.setAttribute('id', 'reserve-wallet-' + id);
    newElement.innerHTML = html;

    var children = newElement.children;
    for (i = 0; i < children.length; i++) {
        var child = children[i];
        if (child.type === 'button') {
            child.id = id;
        }
    }
    parent.appendChild(newElement);
}

function removeElement(item) {
    var element = document.getElementById('reserve-wallet-' + item.id);
    element.parentNode.removeChild(element);
}

function saveAsAddress(item) {
}

function saveAsName(item) {
}

