
var ieoDataTable;

$(function () {

    $.ajaxSetup({
        headers:
            { 'X-CSRF-TOKEN': $('input[name="_csrf"]').attr('value') }
    });

    loadIeoTable();

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#start_date_create').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru',
        value: new Date(),
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

    $('#end_date_create').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });


    $('#start_date_upd').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });

    $('#end_date_upd').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });


    $('#ieoTable').on('click', 'tbody tr', function () {
        var row = ieoDataTable.row( this );
        var currentData = row.data();
        showUpdate(currentData);
    });

    $('#ieo_create').click(function () {
        $('#currencyToPairWith').val('BTC');
        $('#create_ieo').show();
    });

    $('#ieo_create_close').click(function () {
        /*clear data*/
        $('#create_ieo_form').find("input, textarea").val("");
        $('#create_ieo').hide();
    });

    $('#ieo_update_send').click(function () {
        sendUpdateIeo($('#id_upd').val());
    });

    $('#ieo_update_close').click(function () {
        /*clear data*/
        $('#update_ieo-form').find("input, textarea").val("");
        $('#update_ieo').hide();
    });

    $('#ieo_create_send').click(function () {
        sendCreateIeo()
    });

    $('#ieo_revert_send').click(function () {
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo/revert/" + $('#id_upd').val(),
            contentType: "application/json; charset=utf-8",
            success: function(data) {
                console.log(data);
                successNoty("Warning! Ieo reverted");
                loadIeoTable();
            },
            error: function() {
                /*todo show error window*/
            }
        });
    });

    function showUpdate(data) {
        $('#id_upd').val(data.id);
        $('#currencyName').val(data.currencyName);
        $('#description').val(data.currencyDescription);
        /*$('#makerEmail').val(data.makerEmail);*/
        $('#status').val(data.status);
        $('#rate').val(data.rate);
        $('#amount').val(data.amount);
        $('#available_balance').val(data.availableBalance);
        $('#minAmount').val(data.minAmount);
        $('#maxAmountPerUser').val(data.maxAmountPerUser);
        $('#maxAmountPerClaim').val(data.maxAmountPerClaim);
        $('#start_date_upd').val(data.startDate);
        $('#end_date_upd').val(data.endDate);
        $('#createdAt').val(data.createdAt);
        $('#createdBy').val(data.createdBy);
        $('#version').val(data.version);
        $('#update_ieo').show();
    }


    function sendCreateIeo() {
        var formData = JSON.stringify($("#create_ieo_form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $("#ieo_create_send").attr("disabled", true);
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo",
            data: formData,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                $('#ieo_create_send').attr("disabled", false);
                successNoty("Ieo created!");
                loadIeoTable();
                $('#create_ieo_form').find("input, textarea").val("");
                $('#create_ieo').hide();
            },
            error: function(msg) {
                $('#ieo_create_send').attr("disabled", false);
                loadIeoTable();
            }
        });
    }

    function sendUpdateIeo(id) {
        var datastring = JSON.stringify($("#update_ieo-form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $.ajax({
            type: "PUT",
            url: "/2a8fy7b07dxe44/ieo/" + id,
            data: datastring,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                successNoty("Ieo updated!");
                loadIeoTable();
                $('#update_ieo-form').find("input, textarea").val("");
                $('#update_ieo').hide();
            },
            error: function(errMsg) {
                errorNoty(errMsg);
                loadIeoTable();
            }
        })
    }




    function loadIeoTable() {
        var url = '/2a8fy7b07dxe44/ieo/all';
        if ($.fn.dataTable.isDataTable('#ieoTable')) {
            ieoDataTable = $('#ieoTable').DataTable();
            ieoDataTable.ajax.url(url).load()
        } else {
            ieoDataTable = $('#ieoTable').DataTable({
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "deferRender": true,
                "paging": true,
                "info": true,
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "columns": [

                    {
                        "data": "currencyDescription"
                    },
                    {
                        "data": "currencyName"
                    },
                    {
                        "data": "status"
                    },
                    {
                        "data": "contributors"
                    },
                    {
                        "data": "priceString"
                    },
                    {
                        "data": "availableBalance"
                    },
                    {
                        "data": "startDate"
                    },
                    {
                        "data": "endDate"
                    },
                    {
                        "data": "amount"
                    },
                    {
                        "data": "minAmount"
                    },
                    {
                        "data": "maxAmountPerClaim"
                    },
                    {
                        "data": "maxAmountPerUser"
                    },
                    {
                        "data": "id",
                        "visible": false
                    },
                    {
                        "data": "createdAt",
                        "visible": false
                    },
                    {
                        "data": "rate",
                        "visible": false
                    },
                    {
                        "data": "createdBy",
                        "visible": false
                    },
                    {
                        "data": "version",
                        "visible": false
                    }


                ],
                "destroy" : true
            });
        }
    }
});