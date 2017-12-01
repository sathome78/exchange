$(function () {
    const $datetimepickerStart = $('#datetimepicker_start');
    const $datetimepickerEnd = $('#datetimepicker_end');
    const $timepickerMailing = $('#timepicker_mailtime');
    const $emailsTable = $('#report-emails-table');
    const $addEmailModal = $('#add-email-modal');

    const datetimeFormat = 'YYYY-MM-DD HH:mm';
    const timeFormat = 'HH:mm';

    var emailsDataTable;
    var emailsUrlGet = '/2a8fy7b07dxe44/generalStats/mail/emails';



    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $($datetimepickerStart).datetimepicker({
        format: datetimeFormat,
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($datetimepickerEnd).datetimepicker({
        format: datetimeFormat,
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($timepickerMailing).datetimepicker({
        datepicker:false,
        format: timeFormat,
        formatTime: timeFormat,
        lang:'ru',
        defaultTime: '00:00',
        onSelectTime: function () {
            updateMailingTime()
        }
    });

    $($datetimepickerEnd).val(moment($($datetimepickerEnd).datetimepicker('getValue')).format(datetimeFormat));
    $($datetimepickerStart).val(moment($($datetimepickerEnd).datetimepicker('getValue')).subtract(1, 'days').format(datetimeFormat));
    $($timepickerMailing).val('00:00');
    refreshUsersNumber();
    refreshMailingTime();
    refreshMailingStatus();



    $('#refresh-users').click(refreshUsersNumber);
    $('#download-currencies-report').click(getCurrenciesTurnover);
    $('#download-currency-pairs-report').click(getCurrencyPairsTurnover);
    $('#mailing-status-indicator').find('i').click(updateMailingStatus);
    $($addEmailModal).on('click', '#submit-email', function () {
        addSubscriberEmail(emailsDataTable);
    });
    $($emailsTable).on('click', 'i.fa-close', function () {
        const data = emailsDataTable.row($(this).parents('tr')).data();
        if (data.length > 0) {
            const email = data[0];
            deleteSubscriberEmail(email, emailsDataTable)
        }
    });

    if ($.fn.dataTable.isDataTable('#report-emails-table')) {
        emailsDataTable = $($emailsTable).DataTable();
        emailsDataTable.ajax.url(emailsUrlGet).load();
    } else {
        emailsDataTable = $($emailsTable).DataTable({
            "ajax": {
                "url": emailsUrlGet,
                "dataSrc": ""
            },
            dom: "<'row pull-right' B>t",
            "order": [],
            "columns": [
                {
                    data: 0
                },
                {
                    data: null,
                    render: function (data, type, row) {
                        return '<span class="delete-email"><i class="fa fa-close red"></i></span>'
                    },
                    sortable: false
                }
            ],
            buttons: [
                {
                    text: /*$('#acceptSelectedButtonLoc').text()*/'<i class="fa fa-plus" aria-hidden="true"></i>',
                    action: function (e, dt, node, config) {
                        $($addEmailModal).modal();
                    }
                }
            ]
        });
    }

});

function refreshUsersNumber() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/newUsers?' + getTimeParams();
    $.get(fullUrl, function (data) {
        $('#new-users-quantity').text(data)
    })
}

function getCurrencyPairsTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyPairTurnover?' + getTimeParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, 'currencyPairs.csv')
    })
}

function getCurrenciesTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyTurnover?' + getTimeParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, 'currencies.csv')
    })

}

function getTimeParams() {
    return 'startTime=' +
        $('#datetimepicker_start').val().replace(' ', '_') + '&endTime=' +
        $('#datetimepicker_end').val().replace(' ', '_');
}

function refreshMailingTime() {
    $.get('/2a8fy7b07dxe44/generalStats/mail/time', function (data) {
        $('#timepicker_mailtime').val(data)
    })
}

function refreshMailingStatus() {
    $.get('/2a8fy7b07dxe44/generalStats/mail/status', function (data) {
        var $indicator = $('#mailing-status-indicator').find('i');

        if (data) {
            $($indicator).removeClass('fa-close red');
            $($indicator).addClass('fa-check green');
        } else {
            $($indicator).removeClass('fa-check green');
            $($indicator).addClass('fa-close red');
        }

    })
}


function updateMailingTime() {
    var data = {
        newTime: $('#timepicker_mailtime').val()
    };

    $.ajax('/2a8fy7b07dxe44/generalStats/mail/time/update', {
        data: data,
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        }
    }).done(function () {
        refreshMailingTime();
    })
}

function updateMailingStatus() {
    var $indicator = $('#mailing-status-indicator').find('i');
    $($indicator).toggleClass('fa-close red');
    $($indicator).toggleClass('fa-check green');

    var data = {
        newStatus: $($indicator).hasClass('fa-check green')
    };

    $.ajax('/2a8fy7b07dxe44/generalStats/mail/status/update', {
        data: data,
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        }
    }).done(function () {
        refreshMailingStatus();
    })
}

function addSubscriberEmail(datatable) {
    var data = $('#add-email-form').serialize();
    $.ajax('/2a8fy7b07dxe44/generalStats/mail/emails/add', {
        data: data,
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        }
    }).done(function () {
        datatable.ajax.reload(null, false);
        $('#add-email-modal').modal('hide')
    })
}

function deleteSubscriberEmail(email, datatable) {
    var data = {
        email: email
    };
    $.ajax('/2a8fy7b07dxe44/generalStats/mail/emails/delete', {
        data: data,
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        }
    }).done(function () {
        datatable.ajax.reload(null, false);
    })
}


