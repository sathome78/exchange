$(function () {
    const $datetimepickerStart = $('#datetimepicker_start');
    const $datetimepickerEnd = $('#datetimepicker_end');
    const $datepickerBalances = $('#datepicker-balances');
    const $timepickerMailing = $('#timepicker_mailtime');
    const $emailsTable = $('#report-emails-table');
    const $addEmailModal = $('#add-email-modal');
    const $balancesTable = $('#total-balances-table');
    const $balancesSliceStatisticTable = $('#balances-slice-statistic-table');

    const datetimeFormat = 'YYYY-MM-DD HH:mm';
    const dateFormat = 'YYYY-MM-DD';
    const timeFormat = 'HH:mm';

    var emailsDataTable;
    var emailsUrlGet = '/2a8fy7b07dxe44/generalStats/mail/emails';

    var balancesDataTable;
    var balancesUrl = '/2a8fy7b07dxe44/generalStats/groupTotalBalances';
    var balancesSliceStatisticUrl = '/2a8fy7b07dxe44/generalStats/balancesSliceStatistic';

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
        formatDate: dateFormat,
        formatTime: timeFormat,
        lang: 'ru',
        defaultDate: moment().subtract(1, 'days').toDate(),
        defaultTime: '00:00'
    });
    $($datetimepickerEnd).datetimepicker({
        format: datetimeFormat,
        formatDate: dateFormat,
        formatTime: timeFormat,
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($datepickerBalances).datetimepicker({
        format: datetimeFormat,
        formatDate: dateFormat,
        formatTime: timeFormat,
        changeMonth: true,
        changeYear: true,
        pickTime: false,
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($timepickerMailing).datetimepicker({
        datepicker: false,
        format: timeFormat,
        formatTime: timeFormat,
        lang: 'ru',
        defaultTime: '00:00'
    });

    $($datetimepickerEnd).val(moment($($datetimepickerEnd).datetimepicker('getValue')).format(datetimeFormat));
    $($datetimepickerStart).val(moment($($datetimepickerStart).datetimepicker('getValue')).format(datetimeFormat));
    $($datepickerBalances).val(moment($($datepickerBalances).datetimepicker('getValue')).format(datetimeFormat));
    $($timepickerMailing).val('00:00');
    refreshUsersNumber();
    refreshMailingTime();
    refreshMailingStatus();


    $('#refresh-users').click(refreshUsersNumber);
    $('#download-currencies-report').click(getCurrenciesTurnover);
    $('#download-currency-pairs-report').click(getCurrencyPairsTurnover);
    $('#download-currency-pairs-comissions').click(getCurrencyPairsComissions);
    $('#download-input-output-summary-with-commissions').click(getInputOutputSummaryWithCommissions);
    $('#mailing-status-indicator').find('i').click(updateMailingStatus);
    $('#mail-time-submit').click(updateMailingTime);
    $('#download-total-balances').click(getTotalBalancesForRoles);
    $('#download-user-activities').click(getUserActivities);
    $('#download-user-total-commision').click(getUserTotalCommission);
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
                    text: '<i class="fa fa-plus" aria-hidden="true"></i>',
                    action: function (e, dt, node, config) {
                        $($addEmailModal).modal();
                    }
                }
            ]
        });
    }

    if ($.fn.dataTable.isDataTable('#total-balances-table')) {
        balancesDataTable = $($balancesTable).DataTable();
        balancesDataTable.ajax.reload();
    } else {
        var options = {
            "ajax": {
                "url": balancesUrl,
                "dataSrc": ""
            },
            "paging": false,
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            dom: "<'row pull-left' B>t",
            "order": [],
            "columns": [
                {
                    data: 'curId'
                },
                {
                    data: 'currency'
                },
                {
                    data: 'rateToUSD',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    data: 'totalReal'
                }
            ],
            buttons: [{
                extend: 'csv',
                text: 'CSV',
                fieldSeparator: ';',
                bom: true,
                charset: 'UTF8'
            }]
        };

        $($balancesTable).find('th').filter(function (index) {
            return index > 3
        }).map(function () {
            return $.trim($(this).text());
        }).get().forEach(function (item) {
            options['columns'].push({
                data: 'balances.' + item,
                "render": function (data, type, row) {
                    if (type === 'display') {
                        return numbroWithCommas(data);
                    }
                    return data;
                }
            });
        });
        balancesDataTable = $($balancesTable).DataTable(options);


    }

    if ($.fn.dataTable.isDataTable('#balances-slice-statistic-table')) {
        balancesExternalWalletsDataTable = $($balancesSliceStatisticTable).DataTable();
        balancesExternalWalletsDataTable.ajax.reload();
    } else {
        var options = {
            "ajax": {
                "url": balancesSliceStatisticUrl,
                "dataSrc": ""
            },
            "paging": false,
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            dom: "<'row pull-left' B>t",
            "order": [],
            "columns": [
                {
                    "data": 'currencyId'
                },
                {
                    "data": 'currencyName'
                },
                {
                    "data": 'usdRate',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'btcRate',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalWalletBalance',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalWalletBalanceUSD',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalWalletBalanceBTC',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalExratesBalance',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalExratesBalanceUSD',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'totalExratesBalanceBTC',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'deviation',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'deviationUSD',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": 'deviationBTC',
                    "render": function (data, type, row) {
                        if (type === 'display') {
                            return numbroWithCommas(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "lastUpdatedDate"
                }

            ],
            buttons: [{
                extend: 'csv',
                text: 'CSV',
                fieldSeparator: ';',
                bom: true,
                charset: 'UTF8'
            }]
        };

        // $($balancesSliceStatisticTable).find('th').filter(function (index) {
        //     return index > 7
        // }).map(function () {
        //     return $.trim($(this).text());
        // }).get().forEach(function (item) {
        //     options['columns'].push({
        //         data: 'balances.' + item
        //     });
        // });

        balancesExternalWalletsDataTable = $($balancesSliceStatisticTable).DataTable(options);

    }
});

function getArchiveBalances() {
    var url = '/2a8fy7b07dxe44/generalStats/archiveBalancesReports/' + $('#datepicker-balances').val().replace(' ', '_');

    if ($.fn.dataTable.isDataTable('#archive-balances-table')) {
        $('#archive-balances-table').DataTable().ajax.url(url).load();
    } else {
        $('#archive-balances-table').DataTable({
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
                    "data": "id",
                    "visible": false
                },
                {
                    "data": "file_name",
                    "render": function (data, type, full, meta) {
                        return '<a href="javascript:void(0)" onclick="getContentToDownload(' + full.id + ')">' + data + '</a>';
                    }
                }
            ]
        });
    }
}

function getContentToDownload(id) {
    var url = '/2a8fy7b07dxe44/generalStats/archiveBalancesReport/' + id;
    var req = new XMLHttpRequest();
    req.open("GET", url, true);
    req.responseType = "blob";
    req.onload = function (event) {
        var blob = req.response;
        var header = req.getResponseHeader('Content-Disposition');
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = header.match(/filename="(.+)"/)[1];
        link.click();
    };
    req.send();
}

function refreshUsersNumber() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/newUsers?' + getTimeParams();
    $.get(fullUrl, function (data) {
        $('#new-users-quantity').text(data)
    })
}

function getCurrencyPairsTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyPairTurnover?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('currencyPairs.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })
}

function getCurrencyPairsComissions() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/ordersCommissions?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('currencyPairsComissions.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })
}

function getCurrenciesTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyTurnover?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('currencies.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })

}

function getTotalBalancesForRoles() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/totalBalances?' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('totalBalances.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })
}

function getInputOutputSummaryWithCommissions() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/inputOutputSummaryWithCommissions?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('inputOutputSummaryWithCommissions.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })

}

function getUserActivities() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/userActivities?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('userActivities.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })
}

function getUserTotalCommission() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/userTotalCommission?' + getTimeParams() + '&' + getRoleParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, extendsReportName('userTotalCommission.csv', getStartDateFromPicker(), getEndDateFromPicker()))
    })
}

function getTimeParams() {
    return 'startTime=' +
        $('#datetimepicker_start').val().replace(' ', '_') + '&endTime=' +
        $('#datetimepicker_end').val().replace(' ', '_');
}

function getRoleParams() {
    return 'roles=' + $('.roleFilter').filter(function (i, elem) {
        return $(elem).prop('checked')
    }).map(function (i, elem) {
        return $(elem).attr('name')
    }).toArray().join(',')
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


function getStartDateFromPicker() {
    return getDateFromPicker($('#datetimepicker_start'))
}


function getEndDateFromPicker() {
    return getDateFromPicker($('#datetimepicker_end'))
}

function getDateFromPicker($datepicker) {
    var date = $($datepicker).datetimepicker('getValue');
    return moment(date).format('YYYY-MM-DD HH-mm');
}

function numbroWithCommas(value) {

    return numbro(value).format('0.00[000000]').toString().replace(/\./g, ',');
}