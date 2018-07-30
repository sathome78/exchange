

var notificatorsDataTable;

$(document).ready(function () {
    updateNodesDataTable();
});


function updateNodesDataTable() {
    var $notificatorsTable = $('#nodes-table');
    var url = '/2a8fy7b07dxe44/nodes_state_control/getNodesInfo';
    if ($.fn.dataTable.isDataTable('#nodes-table')) {
        notificatorsDataTable = $($notificatorsTable).DataTable();
        notificatorsDataTable.ajax.url(url).load();
    } else {
        notificatorsDataTable = $($notificatorsTable).DataTable({
            "ajax": {
                "url": url,
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
                    "data": "nodeName"
                },
                {
                    "data": "isNodeWork",
                    "render": function (data, type, row) {
                        return data;
                    }
                },

                {
                    "data": "isNodeWorkCorrect",
                    "render": function (data, type, row) {
                        return data;
                    }
                },
                {
                    "data": "lastPollingTime"
                }
            ]
        });
    }
}