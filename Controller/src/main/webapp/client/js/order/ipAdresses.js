
function search() {
    var data = $('#ip_search_form').serialize();
    var searchUrl = '/2a8fy7b07dxe44/ip/ip_log?' + data;
    var orderDataTable;
    if ($.fn.dataTable.isDataTable('#ip-info-table')) {
        orderDataTable = $('#ip-info-table').DataTable();
        orderDataTable.ajax.url(searchUrl).load();
    } else {
        orderDataTable = $('#ip-info-table').DataTable({
            "serverSide": true,
            "ajax": {
                "url": searchUrl,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "bFilter": false,
            "columns": [
                {
                    "data": "id",
                    "name": "IP_Log.id"
                },
                {
                    "data": "email",
                    "name": "USER.email"
                },
                {
                    "data": "ip",
                    "name": "IP_Log.ip"
                },
                {
                    "data": "event",
                    "name": "IP_Log.event"
                },
                {
                    "data": "dateTime",
                    "name": "IP_Log.date",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0] + '<br/>' + data.split(' ')[1];
                        }
                        return data;
                    }
                },
                {
                    "data": "url",
                    "name": "IP_Log.url"
                }
            ],
            "order": [
                [0, 'desc']
            ]
        });
    }

}
$(function () {

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

    $('#ip_search').on('click', search);

    $('#ip_reset').on('click', function () {
        $('#ip_search_form')[0].reset();
        search();
    });

    if ($('#ip_search_form').size() > 0) {
        search();
    }
});
