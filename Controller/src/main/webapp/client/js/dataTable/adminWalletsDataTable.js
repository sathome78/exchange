var walletsDataTable;

$.fn.dataTable.ext.search.push(
    function( settings, data, dataIndex ) {
        if (settings.nTable.id === 'walletsTable') {
            var excludeZeroes = $('#exclude-zero-balances').prop('checked');
            var activeBalance = parseFloat(data[1]) || 0;
            var reservedBalance = parseFloat(data[8]) || 0;

            if (excludeZeroes && activeBalance === 0.0 && reservedBalance === 0.0) {
                return false;
            }
            return true;
        } else {
            return true;
        }


    }
);


$(function () {
    $('#exclude-zero-balances').prop('checked', true);
    $('#walletsTable').hide();
    $('#wallets-table-init').click(function () {
        loadWalletsTable(false);
    });

    $('#wallets-table-balances-only').click(function () {
        loadWalletsTable(true);
    });


    function loadWalletsTable(onlyBalances) {
        var id = $("#user-id").val();
        var url = '/2a8fy7b07dxe44/wallets?id=' + id + '&onlyBalances=' + onlyBalances;
        if ($.fn.dataTable.isDataTable('#walletsTable')) {
            walletsDataTable = $('#walletsTable').DataTable();
            walletsDataTable.ajax.url(url).load()
        } else {
            if ($('#walletsExtendedInfoRequired').text() === 'true') {
                walletsDataTable = $('#walletsTable').DataTable({
                    "paging": false,
                    "order": [],
                    "bLengthChange": false,
                    "bPaginate": false,
                    "bInfo": false,
                    "ajax": {
                        "url": url,
                        "dataSrc": ""
                    },
                    "info": true,
                    "columns": [
                        {
                            "data": "name"
                        },
                        {
                            "data": "totalBalance",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "activeBalance",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "reservedBalance",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "reserveOrders",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "reserveWithdraw",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "totalInput",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "totalSell",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "totalBuy",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "totalOutput",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        }
                    ],
                    dom: "t"
                });
            } else {
                walletsDataTable = $('#walletsTable').DataTable({
                    "paging": false,
                    "order": [],
                    "bLengthChange": false,
                    "bPaginate": false,
                    "bInfo": false,
                    "info": true,
                    "ajax": {
                        "url": '/2a8fy7b07dxe44/wallets?id=' + id,
                        "dataSrc": ""
                    },
                    "columns": [
                        {
                            "data": "name"
                        },
                        {
                            "data": "activeBalance",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        },
                        {
                            "data": "reservedBalance",
                            "render": function (data, type, row) {
                                return formatDecimalValue(data);
                            }
                        }
                    ],
                    dom: "t"
                });
            }
            $('#walletsTable').show();


        }
    }


    $('#walletsTable').on('click', 'tbody tr', function () {
        var currentRow = walletsDataTable.row( this );
        var currentData = currentRow.data();
        window.location = "/2a8fy7b07dxe44/userStatements/" + currentData.id;
    });
    $('#exclude-zero-balances').change(function() {
        walletsDataTable.draw();
    });
});

function formatDecimalValue(val) {
    return numbro(val).format('0.00[0000000]')
}