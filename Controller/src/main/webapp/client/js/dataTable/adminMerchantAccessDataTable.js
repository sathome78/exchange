/**
 * Created by Oleg and ValkSam
 */
var merchantAccessTable;
const TIME_UNIT_SECS = "second(s)";
const TIME_UNIT_MINUTES = "minute(s)";
const TIME_UNIT_HOURS = "hours(s)";

var pairsRestrictions = [];
var arrSize = 0;

$(document).ready(function () {

    pairsRestrictions = $('#pairs_restrictions').text().split(',');
    arrSize = pairsRestrictions.length;
    console.log('log ' + pairsRestrictions + ' ' + arrSize);

    var $merchantAccessTable = $('#merchant-options-table');
    merchantAccessTable = $($merchantAccessTable).DataTable({
        "ajax": {
            "url": "/2a8fy7b07dxe44/merchantAccess/data",
            "dataSrc": "",
            traditional: true,
            data: {processTypes: ["CRYPTO, MERCHANT, INVOICE"]}
        },
        "bFilter": true,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false,
        "columns": [
            {
                "data": "merchantName",
            },
            {
                "data": "currencyName",
            },
            {
                "data": "isRefillBlocked",
                "render": function (data) {
                    return '<span data-operationtype="INPUT">'.concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                },
            },
            {
                "data": "isWithdrawBlocked",
                "render": function (data) {
                    return '<span data-operationtype="OUTPUT">'.concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                },
            },
            {
                "data": "withdrawAutoEnabled",
                "render": function (data) {
                    return '<input style="cursor: pointer" readonly class="auto-enabled" type="checkbox"' + (data ? 'checked' : '') + '/>';
                },
            },
            {
                "data": "withdrawAutoDelaySeconds",
                "render": function (data) {
                    var timeData = autoConvertSecondsToTimeUnit(data);
                    return '<span style="white-space: nowrap" class="auto-delay">' + timeData.time + " " + timeData.unit + '</span>';
                }
            },
            {
                "data": "withdrawAutoThresholdAmount",
                "render": function (data) {
                    return '<span style="white-space: nowrap" class="auto-threshold">' + (data > 0 ? data : "ALWAYS") + '</span>';
                }
            },
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-merchantid", data.merchantId);
            $(row).attr("data-currencyid", data.currencyId);
            if (!data.withdrawAutoEnabled) {
                $(row).find(".auto-delay").attr("hidden", true);
                $(row).find(".auto-threshold").attr("hidden", true);
            }
        }
    });

    var $transferAccessTable = $('#transfer-options-table');
    transferAccessTable = $($transferAccessTable).DataTable({
        "ajax": {
            "url": "/2a8fy7b07dxe44/merchantAccess/data",
            "dataSrc": "",
            traditional: true,
            data: {processTypes: ["TRANSFER"]}
        },
        "bFilter": true,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false,
        "columns": [
            {
                "data": "merchantName",
            },
            {
                "data": "currencyName",
            },
            {
                "data": "isTransferBlocked",
                "render": function (data) {
                    return '<span data-operationtype="USER_TRANSFER">'.concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                },
            },
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-merchantid", data.merchantId);
            $(row).attr("data-currencyid", data.currencyId);
        }
    });

    var $currencyVisibilityTable = $('#currency-visibility-options-table');
    currencyVisibilityTable = $($currencyVisibilityTable).DataTable({
        "ajax": {"url": "/2a8fy7b07dxe44/merchantAccess/getCurrency", "dataSrc": ""},
        "paging": false,
        "columns": [
            {
                "data": "id",
                "visible": false
            },
            {
                "data": "name"
            },
            {
                "data": "description"
            },
            {
                "data": "hidden",
                "render": function (data) {
                    return '<span>'.concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                }
            }
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-currencyid", data.id);
        }
    });

    var $currencyPairsVisibilityTable = $('#currency-pairs-visibility-options-table');
    currencyPairsVisibilityTable = $($currencyPairsVisibilityTable).DataTable({
        "ajax": {"url": "/2a8fy7b07dxe44/merchantAccess/getCurrencyPairs", "dataSrc": ""},
        "paging": false,
        "columns": [
            {
                "data": "id",
                "visible": false
            },
            {
                "data": "name"
            },
            {
                "data": "hidden",
                "render": function (data) {
                    return '<span>'.concat(data ? '<i class="visibility fa fa-lock red" aria-hidden="true"></i>' : '<i class="visibility fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                }
            },
            {
                "data": "tradeRestriction",
                "render": function (data) {
                    var wrapper = $('<div></div>');
                    for (var i = 0; i < arrSize; i++) {
                        var currentRestr = pairsRestrictions[i];
                        if (data !== null && data.indexOf(currentRestr) > -1) {
                            $("<div/>").attr('id','new').appendTo('body');
                            wrapper.append('<span>' + currentRestr + ' <i data-restriction=' + currentRestr + ' class="restriction fa fa-lock red" aria-hidden="true"></i></span>');
                        } else {
                            wrapper.append('<span>' + currentRestr + ' <i data-restriction=' + currentRestr + ' class="restriction fa fa-unlock" aria-hidden="true"></i></span>');
                        }

                    }
                    return wrapper.html();
                }
            },
            {
                "data": "topMarketVolume",
                "name": "topMarketVolume",
                "render": function (data, type, row) {
                    if (type === 'display') {
                        var tag = data == null ? '' : data;
                        return '<input class="form-control copyable topMarketVolume" value="' + tag + '">';
                    }
                    return data;
                }
            },
            {
                "name": "WITHDRAW_REQUEST.admin_holder_id",
                "render": function (data, type, row) {

                    return '<button data-id="' + row.id + '" class="action-button btn confirm_admin_button">Update</button>&nbsp;';
                },
                "className": "text-center"
            }
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-currencypairid", data.id);
        }
    });

    var $currencyPairsMarketVolumes = $('#currency-pair-market-volumes');
    currencyPairsMarketVolumes = $($currencyPairsMarketVolumes).DataTable({
        "ajax": {"url": "/2a8fy7b07dxe44/merchantAccess/getMarketVolumes", "dataSrc": ""},
        "paging": false,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "marketVolume",
                "name": "marketVolume",
                "render": function (data, type, row) {
                    if (type === 'display') {
                        var tag = data == null ? '' : data;
                        return '<input class="form-control copyable marketVolume" value="' + tag + '">';
                    }
                    return data;
                }
            },
            {
                "name": "WITHDRAW_REQUEST.admin_holder_id",
                "render": function (data, type, row) {

                    return '<button data-name="' + row.name + '" class="action-button btn confirm_admin_button">Update</button>&nbsp;';
                },
                "className": "text-center"
            }
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-currencypairid", data.name);
        }
    });

    var $currencyPairsAccessToDirectLinkTable = $('#currency-pairs-access-to-direct-links-options-table');
    currencyPairsAccessToDirectLinkTable = $($currencyPairsAccessToDirectLinkTable).DataTable({
        "ajax": { "url": "/2a8fy7b07dxe44/merchantAccess/getCurrencyPairs", "dataSrc": ""},
        "paging": false,
        "columns": [
            {
                "data": "id",
                "visible": false
            },
            {
                "data": "name"
            },
            {
                "data": "permittedLink",
                "render": function (data) {
                    return '<span>'.concat(data ? '<i class="fa fa-unlock" aria-hidden="true"></i>' : '<i class="fa fa-lock red" aria-hidden="true"></i>')
                        .concat('</span>');
                }
            }
        ],
        "createdRow": function (row, data, index) {
            $(row).attr("data-currencypairid", data.id);
        }
    });

    $($currencyPairsMarketVolumes).on('click', 'button[data-name]', function (e) {
        e.preventDefault();
        var marketVolume = $(this).closest("tr").find(".marketVolume").val();
        var name = $(this).data("name");
        $.ajax({
            url: '/2a8fy7b07dxe44/merchantAccess/currencyPairs/market/post?name=' + name + '&volume=' + marketVolume,
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            type: 'POST',
            complete: function () {
            }
        });
    });

    $($currencyPairsVisibilityTable).on('click', 'button[data-id]', function (e) {
        e.preventDefault();
        var topMarketVolume = $(this).closest("tr").find(".topMarketVolume").val();
        var id = $(this).data("id");
        $.ajax({
            url: '/2a8fy7b07dxe44/merchantAccess/currencyPairs/post?id=' + id + '&volume=' + topMarketVolume,
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            type: 'POST',
            complete: function () {
            }
        });
    });

    $merchantAccessTable.find('tbody').on('click', 'i', function () {
        var operationType = $(this).parent().data('operationtype');
        var merchantId = $(this).parents('tr').data('merchantid');
        var currencyId = $(this).parents('tr').data('currencyid');
        if (confirm($('#prompt-toggle-block').html())) {
            toggleBlock(merchantId, currencyId, operationType, this);
        }
    });

    $transferAccessTable.find('tbody').on('click', 'i', function () {
        var operationType = $(this).parent().data('operationtype');
        var merchantId = $(this).parents('tr').data('merchantid');
        var currencyId = $(this).parents('tr').data('currencyid');
        if (confirm($('#prompt-toggle-block').html())) {
            toggleBlock(merchantId, currencyId, operationType, this);
        }
    });

    $currencyVisibilityTable.find('tbody').on('click', 'i', function () {
        var currencyId = $(this).parents('tr').data('currencyid');
        if (confirm($('#prompt-toggle-block').html())) {
            changeVisibilityForCurrency(currencyId, this);
        }
    });

    $currencyPairsVisibilityTable.find('tbody').on('click', '.visibility', function () {
        var currencyPairId = $(this).parents('tr').data('currencypairid');
        if (confirm($('#prompt-toggle-block').html())) {
            changeVisibilityForCurrencyPair(currencyPairId, this);
        }
    });

    /*trade restriction*/
    $currencyPairsVisibilityTable.find('tbody').on('click', '.restriction', function () {
        var currencyPairId = $(this).parents('tr').data('currencypairid');
        var restriction = $(this).data('restriction');
        var isEnabled = $(this).hasClass( "fa-lock" );
        if (confirm($('#prompt-toggle-block').html())) {
            if (isEnabled) {
                changeTradeRestrictionForCurrencyPair(currencyPairId, restriction, this, 'DELETE');
            } else {
                changeTradeRestrictionForCurrencyPair(currencyPairId, restriction, this, 'POST');
            }
        }
    });

    $currencyPairsAccessToDirectLinkTable.find('tbody').on('click', 'i', function () {
        var currencyPairId = $(this).parents('tr').data('currencypairid');
        if (confirm($('#prompt-toggle-block').html())) {
            changeAccessToDirectLinkForCurrencyPair(currencyPairId, this);
        }
    });

    $merchantAccessTable.find('tbody').on('click', '.auto-enabled', function (event) {
        event.preventDefault();
        var $row = $(this).parents('tr');
        var rowData = merchantAccessTable.row($row).data();
        showAutoWithdrawParamsDialog(rowData);
    });

    $('#block-all-input').click(function () {
        setBlockForAll('INPUT', true);
    });
    $('#block-all-output').click(function () {
        setBlockForAll('OUTPUT', true);
    });
    $('#unblock-all-input').click(function () {
        setBlockForAll('INPUT', false);
    });
    $('#unblock-all-output').click(function () {
        setBlockForAll('OUTPUT', false);
    });

});


function showAutoWithdrawParamsDialog(rowData) {
    var $form = $("#withdraw-auto-params-form");
    var currentTimeUnit;
    $form.find('input[name="merchantId"]').val(rowData.merchantId);
    $form.find('input[name="currencyId"]').val(rowData.currencyId);
    $form.find('input[name="withdrawAutoEnabled"]').prop("checked", rowData.withdrawAutoEnabled);
    $form.find('input[name="withdrawAutoThresholdAmount"]').val(rowData.withdrawAutoThresholdAmount);
    $form.find("#submitAutoWithdrawParams").off("click").one("click", submitAutoWithdrawParams);
    var $timeUnitSelect = $form.find("#timeUnit");
    $timeUnitSelect.find("option").remove();
    $timeUnitSelect.append("<option value=" + TIME_UNIT_SECS + ">" + TIME_UNIT_SECS + "</option>");
    $timeUnitSelect.append("<option value=" + TIME_UNIT_MINUTES + ">" + TIME_UNIT_MINUTES + "</option>");
    $timeUnitSelect.append("<option value=" + TIME_UNIT_HOURS + ">" + TIME_UNIT_HOURS + "</option>");
    var timeData = autoConvertSecondsToTimeUnit(rowData.withdrawAutoDelaySeconds);
    var $timeUnitOption = $timeUnitSelect.find("option[value='" + timeData.unit + "']");
    $timeUnitSelect.val($timeUnitOption.val());
    var $withdrawAutoDelay = $form.find('input[name="withdrawAutoDelaySeconds"]');
    $withdrawAutoDelay.val(timeData.time);

    $timeUnitSelect.off("focusin").on("focusin", function () {
        currentTimeUnit = $(this).val();
    });

    $timeUnitSelect.off("change").on("change", function () {
        var currentTime = $withdrawAutoDelay.val();
        var newTimeUnit = $timeUnitSelect.val();
        var newTime = convertToTimeUnit(currentTime, currentTimeUnit, newTimeUnit);
        $withdrawAutoDelay.val(newTime);
        $withdrawAutoDelay.focus();
    });

    $("#withdraw-auto-params").modal();
}

function submitAutoWithdrawParams(event) {
    event.preventDefault();
    var $form = $("#withdraw-auto-params-form");
    var data = {};
    $.each($form.serializeArray(), function () {
        data[this.name] = this.value;
    });
    var currentTime = data["withdrawAutoDelaySeconds"];
    var currentTimeUnit = $form.find("#timeUnit").val();
    var seconds = convertToSeconds(currentTime, currentTimeUnit);
    var timeData = autoConvertSecondsToTimeUnit(seconds);
    seconds = convertToSeconds(timeData.time, timeData.unit);
    data["withdrawAutoDelaySeconds"] = seconds ? seconds : 0;
    var url = '/2a8fy7b07dxe44/merchantAccess/autoWithdrawParams';
    $.ajax({
        url: url,
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        contentType: "application/json",
        type: 'POST',
        data: JSON.stringify(data),
        success: function () {
            merchantAccessTable.ajax.reload();
        },
        complete: function (error) {
            $("#withdraw-auto-params").modal('hide');
        }
    });
}

function autoConvertSecondsToTimeUnit(seconds) {
    if (seconds <= 0) {
        return {
            time: "no delay",
            unit: "",
        }
    }
    if (seconds > 60 * 60 - 1) {
        return {
            time: Math.round(seconds / (60 * 60)),
            unit: TIME_UNIT_HOURS,
        }
    }
    if (seconds > 59) {
        return {
            time: Math.round(seconds / 60),
            unit: TIME_UNIT_MINUTES,
        }
    }
    return {
        time: seconds,
        unit: TIME_UNIT_SECS,
    }
}

function convertToTimeUnit(time, timeUnitFrom, timeUnitTo) {
    var seconds = convertToSeconds(time, timeUnitFrom);
    if (timeUnitTo === TIME_UNIT_SECS) {
        return seconds;
    } else if (timeUnitTo === TIME_UNIT_MINUTES) {
        return Math.round(seconds / 60);
    } else if (timeUnitTo === TIME_UNIT_HOURS) {
        return Math.round(seconds / (60 * 60));
    }
}

function convertToSeconds(time, timeUnit) {
    if (timeUnit === TIME_UNIT_SECS) {
        return time;
    } else if (timeUnit === TIME_UNIT_MINUTES) {
        return time * 60;
    } else if (timeUnit === TIME_UNIT_HOURS) {
        return time * 60 * 60;
    }
}

function toggleBlock(merchantId, currencyId, operationType, $element) {
    var formData = new FormData();
    formData.append("merchantId", merchantId);
    formData.append("currencyId", currencyId);
    formData.append("operationType", operationType);

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/toggleBlock',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function changeVisibilityForCurrency(currencyId, $element) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/currency/visibility/update',
        type: 'POST',
        data: {
            "currencyId": currencyId
        },
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function changeVisibilityForCurrencyPair(currencyPairId, $element) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/currencyPair/visibility/update',
        type: 'POST',
        data: {
            "currencyPairId": currencyPairId
        },
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function changeTradeRestrictionForCurrencyPair(currencyPairId, restriction, $element, method) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/currencyPair/restriction?currencyPairId=' + currencyPairId + '&restriction=' + restriction,
        type: method,
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function changeAccessToDirectLinkForCurrencyPair(currencyPairId, $element) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/currencyPair/directLink/update',
        type: 'POST',
        data: {
            "currencyPairId": currencyPairId
        },
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function setBlockForAll(operationType, blockStatus) {
    if (confirm($('#prompt-toggle-block-all').html())) {
        var columnNumber = operationType === 'INPUT' ? 3 : 4;
        var formData = new FormData();
        formData.append("operationType", operationType);
        formData.append("blockStatus", blockStatus);
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/2a8fy7b07dxe44/merchantAccess/setBlockForAll',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function () {
                window.location.href = '/2a8fy7b07dxe44/merchantAccess';
                // var $targetIcons = $('#merchant-options-table').find('tr td:nth-child(' + columnNumber + ') i');
                // $($targetIcons).toggleClass('fa-lock red', blockStatus);
                // $($targetIcons).toggleClass('fa-unlock', !blockStatus);
            }
        });
    }


}

function getButton(id, sourceType, merchant, buttonDataList, tableIdFor) {
    var buttonsSet = '';
    buttonDataList.forEach(function (e) {
        if (e.tableIdListOnly.indexOf(tableIdFor) > -1
        ) {
            buttonsSet = buttonsSet +
                '<button data-id = ' + id +
                '        data-source = ' + sourceType +
                '        data-merchant= ' + merchant +
                '        style="font-size: 1.1rem;" ' +
                '        class="action-button table-button-block__button btn ' + e.buttonId + '">' +
                e.buttonTitle +
                '</button>&nbsp;';
        }
    });
    return buttonsSet;
}
