/**
 * Created by Valk on 05.05.2016.
 */

var currentId;
var currentRole;

function uploadUserWallets(role) {
    currentRole = role;
    currentId = 'upload-users-wallets';
    showDialog({
        currencyPicker: false,
        currencyPairPicker: false,
        directionPicker: false,
        includeEmptyChecker: true
    });
}

function uploadUserWalletsInOut(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-inout';
    showDialog({
        currencyPicker: false,
        currencyPairPicker: false,
        directionPicker: false,
        includeEmptyChecker: false
    });
}

function uploadUserWalletsOrders(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-orders';
    showDialog({
        currencyPicker: false,
        currencyPairPicker: false,
        directionPicker: false,
        includeEmptyChecker: false
    });
}

function uploadUserWalletsOrdersByCurrencyPairs(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-orders-by-currency-pairs';
    showDialog({
        currencyPicker: false,
        currencyPairPicker: false,
        directionPicker: false,
        includeEmptyChecker: false
    });
}

function uploadInputOutputSummaryReport(role) {
    currentRole = role;
    currentId = 'downloadInputOutputSummaryReport';
    showDialog({
        currencyPairPicker: false,
        includeEmptyChecker: false
    });
}

function uploadUserTransactionsReport(paramsString) {
    currentId = 'upload-users-transactions';
    makeReportByParams(paramsString);
}

function uploadUserIps(role) {
    currentRole = role;
    currentId = 'upload-users-ips';
    makeReportByParams('role=' + role)
}


function showDialog(params) {
    params.currencyPicker = (params.currencyPicker || params.currencyPicker == undefined) ? "block" : "none";
    params.currencyPairPicker = (params.currencyPairPicker || params.currencyPairPicker == undefined) ? "block" : "none";
    params.directionPicker = (params.directionPicker || params.directionPicker == undefined) ? "block" : "none";
    params.includeEmptyChecker = (params.includeEmptyChecker || params.includeEmptyChecker == undefined) ? "block" : "none";
    var $dialog = $('#report-dialog-currency-date-direction-dialog');
    $dialog.find("#currencyPicker").css("display", params.currencyPicker);
    $dialog.find("#currencyPairPicker").css("display", params.currencyPairPicker);
    $dialog.find("#directionPicker").css("display", params.directionPicker);
    $dialog.find("#includeEmptyChecker").css("display", params.includeEmptyChecker);
    $dialog.modal();
}

function makeReportWithPeriodDialog() {
    var $dialog = $('#report-dialog-currency-date-direction-dialog');
    const $loadingDialog = $('#loading-process-modal');
    var $form = $dialog.find('form');
    if (!isDatesValid($form)) {
        return;
    }

    $dialog.one('hidden.bs.modal', function (e) {
        var data = "startDate=" + $form.find("#start-date").val() + ' 00:00:00' +
            '&' + "endDate=" + $form.find("#end-date").val() + ' 23:59:59' +
            '&' + "currencyList=" + $form.find("#currencies").val() +
            '&' + "currencyPairList=" + $form.find("#currencyPairs").val() +
            '&' + "direction=" + $form.find("#direction").val() +
            '&' + "includeEmpty=" + $form.find("#includeEmpty").val() +
            "&role=" + currentRole;
        $loadingDialog.modal({
            backdrop: 'static'
        });

        //wolper 23.04.18
        //start and end dates form the dialog form
        var startDate=$form.find("#start-date").val();
        var endDate=$form.find("#end-date").val();
        var reprtName="downloadUsersWalletsSummaryInOut.csv";

        if (currentId == 'downloadInputOutputSummaryReport') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/InputOutputSummary',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    },
                }
            );
        } else if (currentId == 'upload-users-wallets-inout') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/UsersWalletsSummaryInOut',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data.list, extendsReportName(reprtName, startDate, endDate));
                        saveToDisk(data.summary, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    },
                }
            );
        } else if (currentId == 'upload-users-wallets') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/usersWalletsSummary',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    },
                }
            );
        } else if (currentId == 'upload-users-wallets-orders') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/userSummaryOrders',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    },
                }
            );
        } else if (currentId == 'upload-users-wallets-orders-by-currency-pairs') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/userSummaryOrdersByCurrencyPairs',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    },
                }
            );
        }  else if (currentId == 'upload-users-transactions') {
            //wolper 23.04.18
            // ??? isn't this branch unreachable
            $.ajax({
                    url: '/2a8fy7b07dxe44/report/downloadTransactions',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        //wolper 23.04.18
                        saveToDisk(data, extendsReportName(reprtName, startDate, endDate));
                    },
                    complete: function () {
                        $loadingDialog.modal("hide");
                    }
                }
            );
        }

    });
    $dialog.modal('hide');
}

function makeReportByParams(params) {
    if (currentId == 'upload-users-transactions') {
        $.ajax({
                url: '/2a8fy7b07dxe44/report/downloadTransactions'+"?"+params,
                type: 'GET',
                success: function (data) {
                    //wolper 23.04.18
                    var reprtName="downloadUsersWalletsSummaryInOut.csv";
                    saveToDisk(data, extendsReportName(reprtName));
                }
            }
        );
    } else if (currentId == 'upload-users-ips') {
        $.ajax({
                url: '/2a8fy7b07dxe44/report/downloadUserIpInfo' +"?"+params,
                type: 'GET',
                success: function (data) {
                    //wolper 23.04.18
                    var reprtName="downloadUsersWalletsSummaryInOut.csv";
                    saveToDisk(data, extendsReportName(reprtName));
                }
            }
        );
    }
}

function saveToDisk(data, filename) {
    //wolper 23.04.18
    //argument name changed to filename from 'name'
    //var filename = name ? name : "downloadUsersWalletsSummaryInOut_" + currentRole + ".csv";

    var link = document.createElement('a');
    link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
    link.download = filename;
    var e = document.createEvent('MouseEvents');
    e.initEvent('click', true, true);
    link.dispatchEvent(e);
}

function isDatesValid($form) {
    var $startDateErrorWrapper = $form.find('.input-block-wrapper__error-wrapper[for=start-date]');
    var $endDateErrorWrapper = $form.find('.input-block-wrapper__error-wrapper[for=end-date]');
    $startDateErrorWrapper.toggle(false);
    $endDateErrorWrapper.toggle(false);
    var $startDatePiker = $form.find('#start-date');
    var $endDatePiker = $form.find('#end-date');
    var isError = false;
    if (!$startDatePiker.val().match(/\d{4}\-\d{2}\-\d{2}/)) {
        $startDateErrorWrapper.toggle(true);
        isError = true;
    }
    if (!$endDatePiker.val().match(/\d{4}\-\d{2}\-\d{2}/)) {
        $endDateErrorWrapper.toggle(true);
        isError = true;
    }
    return !isError;
}


// wolper 23.04.18
// an adapter which extends the name of report file with requested dates
// (but without time, time is excluded from picker value)
// name is a filename to extend
// start, end - optional arguments for reporting date interval
function extendsReportName(name, start, end){
    var baseName    = name.slice(0,-4);
    var dateNow     = new Date().toLocaleDateString();
    var dateTimeNow = new Date().toUTCString();

    switch (name){
        case 'totalBalances.csv':
            return baseName+'_as_of-'+dateTimeNow+ '.csv';

        case 'inputOutputSummaryWithCommissions.csv':
        case 'currencyPairsComissions.csv':
        case 'currencyPairs.csv':
        case 'currencies.csv':
            return baseName+'_from-'+start+'_to-'+end+'.csv';

        case 'downloadUsersWalletsSummaryInOut.csv':
            var role = currentRole?"_"+currentRole:"";
            if (start && end) return baseName+'_from-'+start+"_to-"+end+"_"+currentRole + ".csv";
            else return baseName+'_as_of-'+dateNow+ role + ".csv";

        default: return name;
    }
}


