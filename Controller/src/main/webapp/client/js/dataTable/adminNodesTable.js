

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
            "responsive": true,
            "serverSide": false,
            "ajax": url,
            "stateSave": true,
            "paging": true,
            "cache": false,
            "deferRender": true,
            "columns": [
                {
                    "data": "nodeName"
                },
                {
                    "data": "nodeWork",
                    "render": function (data, type, row) {
                        return data;
                    }
                },

                {
                    "data": "nodeWorkCorrect",
                    "render": function (data, type, row) {
                        return data;
                    }
                },
                {
                    "data": "lastPollingTime",
                    "render": function (data, type, row) {
                        return data;
                    }
                }
            ]
        });
    }
}