var freecoinsDataTable;

$(function () {

    $.ajaxSetup({
        headers:
            {'X-CSRF-TOKEN': $('input[name="_csrf"]').attr('value')}
    });

    loadFreecoinsTable();
});

function loadFreecoinsTable() {
    var url = '/2a8fy7b07dxe44/free-coins/giveaway/all';
    if ($.fn.dataTable.isDataTable('#freecoinsTable')) {
        freecoinsDataTable = $('#freecoinsTable').DataTable();
        freecoinsDataTable.ajax.url(url).load()
    } else {
        freecoinsDataTable = $('#freecoinsTable').DataTable({
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
                    "data": "date"
                },
                {
                    "data": "currency"
                },
                {
                    "data": "total_amount",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "period"
                },
                {
                    "data": "prize_amount",
                    "render": function (data, type) {
                        if (type === 'display') {
                            return numbro(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "quantity"
                },
                {
                    "data": "quantity_left"
                },
                {
                    "data": "unique_acceptors"
                },
                {
                    "data": "creator"
                },
                {
                    "data": "status"
                },
                {
                    "data": "revoke",
                    "render": function (data, type, row) {
                        var status = row.status;
                        var giveawayId = row.id;

                        if (status === 'CREATED') {
                            return '<button class="btn btn-sm btn-info" onclick="revoke(' + giveawayId + ', ' + false + ')">To us</button>'
                                + '<button class="btn btn-sm btn-warning" onclick="revoke(' + giveawayId + ', ' + true + ')">To creator</button>';
                        } else {
                            return '';
                        }
                    }
                }
            ],
            "destroy": true
        });
    }
}

function revoke(giveawayId, toUser) {
    $.ajax({
        url: '/2a8fy7b07dxe44/free-coins/revoke',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "giveaway_id": giveawayId,
            "revoke_to_user": toUser
        },
        success: function () {
            loadFreecoinsTable();
        },
        error: function (err) {
            console.log(err);
        }
    });
}