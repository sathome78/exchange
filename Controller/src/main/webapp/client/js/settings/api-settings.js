

$(function () {

    var $tokensTable = $('#api-tokens-table');
    var tokenDataTable;
    var tokensUrlBase = '/settings/token';
    updateTokensTable();

    $($tokensTable).find('tbody').on('click', 'tr i.fa-trash', function () {
        var $row = $(this).parents('tr');
        deleteToken($row);
    });

    $($tokensTable).find('tbody').on('click', 'tr span.allowTrade i', function () {
        updateAllowTrade(this);
    });


    function deleteToken($row) {
        var rowData =  tokenDataTable.row($row).data();
        var alias = rowData['alias'];
        var promptLocText = $('#prompt-del-loc').text().replace('__alias__', alias);
        if (confirm(promptLocText)) {
            var tokenId = rowData['id'];
            var postData = {
                tokenId: tokenId
            };
            $.ajax(tokensUrlBase + '/delete', {
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                data: postData,
                success: function () {
                    updateTokensTable()
                }
            })
        }
    }

    function updateAllowTrade($elem) {
        var $row = $($elem).parents('tr');
        var rowData =  tokenDataTable.row($row).data();
        var alias = rowData['alias'];
        var promptLocText = $('#prompt-perm-loc').text().replace('__alias__', alias);
        if (confirm(promptLocText)) {
            var tokenId = rowData['id'];
            var postData = {
                tokenId: tokenId,
                allowTrade: $($elem).hasClass('red')
            };
            $.ajax(tokensUrlBase + '/allowTrade', {
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                data: postData,
                success: function () {
                    updateTokensTable()
                }
            })
        }

    }

    function updateTokensTable() {
        var tokenFindAllUrl = tokensUrlBase + '/findAll';
        if ( $.fn.dataTable.isDataTable( '#api-tokens-table' ) ) {
            tokenDataTable = $($tokensTable).DataTable();
            tokenDataTable.ajax.url(tokenFindAllUrl).load();
        } else {
            tokenDataTable = $($tokensTable).DataTable({
                "ajax": {
                    "url": tokenFindAllUrl,
                    "dataSrc": ""
                },
                "paging": true,
                "info": true,
                "columns": [
                    {
                        "data": "alias"
                    },
                    {
                        "data": "publicKey"
                    },
                    {
                        "data": "allowTrade",
                        "render": function (data) {
                            return '<span class="allowTrade">'.concat(data ? '<i style="cursor: pointer" class="fa fa-unlock green" aria-hidden="true"></i>' :
                                '<i style="cursor: pointer" class="fa fa-lock red" aria-hidden="true"></i>')
                                .concat('</span>');
                        },
                        "className": "text-center"
                    },
                    {
                        "data": "generationDate"
                    },
                    {
                        "data": null,
                        "render": function () {
                            return "<div style='display: inline-block; min-width: 70px'>" +
                                "<i style='cursor: pointer' class='fa fa-trash red'></i>" +
                                "</div>" ;
                        },
                        "orderable": false,
                        "className": "text-center"
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
    }
});