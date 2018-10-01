var usersDataTable;

$(function () {
    if ( $.fn.dataTable.isDataTable( '#usersTable' ) ) {
        usersDataTable = $('#usersTable').DataTable();
    } else {
        usersDataTable = $('#usersTable').DataTable({
            "ajax": {
                "url": '/2a8fy7b07dxe44/usersList',
                "dataSrc": "data"
            },
            "serverSide": true,
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "nickname",
                    "render": function (data, type, row){
                         if (type == 'display') {
                             if (!data) {
                                 return '';
                             }
                            return '<a href="/2a8fy7b07dxe44/userInfo?id='+row['id']+'">'+data+'</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "email",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return'<a href="/2a8fy7b07dxe44/userInfo?email='+row['email']+'">'+data+'</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "regdate"
                },
                {
                    "data": "role"
                },
                {
                    "data": "status"
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        });
    }
});