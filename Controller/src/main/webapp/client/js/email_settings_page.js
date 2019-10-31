
var ieoDataTable;

$(function () {

    $.ajaxSetup({
        headers:
            { 'X-CSRF-TOKEN': $('input[name="_csrf"]').attr('value') }
    });

    loadEmailTable();

    $('#emailTable').on('click', 'tbody tr', function () {
        var row = ieoDataTable.row( this );
        var currentData = row.data();
        showUpdate(currentData);
    });

    $('#email_create').click(function () {
        $('#create_email').show();
    });

    $('#email_create_close').click(function () {
        /*clear data*/
        $("#update_email-form  :input:not(:checkbox):not(:button) textarea").val("");
        $('#create_email').hide();
    });

    $('#email_update_send').click(function () {
        sendUpdateEmail();
    });

    $('#email_update_close').click(function () {
        /*clear data*/
        $("#update_email-form  :input:not(:checkbox):not(:button) textarea").val("");
        $('#update_email').hide();
    });

    $('#email_create_send').click(function () {
        sendCreateEmail()
    });

    $('#email_delete_send').click(function () {
        sendDeleteEmail()
    });

    $('#ieo_approve_send').click(function () {
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo/approve/" + $('#id_upd').val(),
            contentType: "application/json; charset=utf-8",
            success: function(data) {
                console.log(data);
                successNoty("IEO successfully ended!");
                loadIeoTable();
            },
            error: function() {
            }
        });
    });

    function showUpdate(data) {
        $('#email_host').val(data.host);
        $('#email_sender').val(data.sender);
        $('#update_email').show();
    }

    function sendCreateEmail() {
        var formData = JSON.stringify($("#create_email_rule_form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $("#email_create_send").attr("disabled", true);
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/email/",
            data: formData,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                $('#email_create_send').attr("disabled", false);
                successNoty("Email rule created!");
                loadEmailTable();
                $("#create_email_form  :input:not(:checkbox):not(:button) textarea").val("");
                $('#create_email').hide();
            },
            error: function(msg) {
                $('#email_create_send').attr("disabled", false);
                loadEmailTable();
            }
        });
    }

    function sendDeleteEmail() {
        var formData = JSON.stringify($("#update_email-form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $("#email_delete_send").attr("disabled", true);
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/email/remove",
            data: formData,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                $('#email_create_send').attr("disabled", false);
                successNoty("Email rule deleted!");
                loadEmailTable();
                $("#update_email-form  :input:not(:checkbox):not(:button) textarea").val("");
                $('#update_email').hide();
            },
            error: function(msg) {
                $('#email_create_send').attr("disabled", false);
                loadEmailTable();
            }
        });
    }

    function sendUpdateEmail() {
        var datastring = JSON.stringify($("#update_email-form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $.ajax({
            type: "PUT",
            url: "/2a8fy7b07dxe44/email/",
            data: datastring,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                successNoty("Email updated!");
                loadEmailTable();
                $("#update_email-form  :input:not(:checkbox):not(:button) textarea").val("");
                $('#update_email').hide();
            },
            error: function(errMsg) {
                errorNoty(errMsg);
                loadEmailTable();
            }
        })
    }

    function loadEmailTable() {
        var url = '/2a8fy7b07dxe44/email/all';
        if ($.fn.dataTable.isDataTable('#emailTable')) {
            ieoDataTable = $('#emailTable').DataTable();
            ieoDataTable.ajax.url(url).load()
        } else {
            ieoDataTable = $('#emailTable').DataTable({
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
                        "data": "host"
                    },
                    {
                        "data": "sender"
                    }
                ],
                "destroy" : true
            });
        }
    }
});
