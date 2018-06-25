/**
 * Created by Valk on 27.06.2016.
 */

function InputOutputClass(currentCurrencyPair) {
    if (InputOutputClass.__instance) {
        return InputOutputClass.__instance;
    } else if (this === window) {
        return new InputOutputClass(currentCurrencyPair);
    }
    InputOutputClass.__instance = this;
    /**/
    /**/
    var that = this;
    var timeOutIdForInputOutputData;
    var refreshIntervalForInputOutputData = 30000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $inputoutputContainer = $('#inputoutput');
    const modalTemplate = $('.paymentInfo p');
    var tableId = "inputoutput-table";
    var inputoutputCurrencyPairSelector;
    var tablePageSize = 20;
    const $withdrawParamsDialog = $('#dialog-withdraw-creation');

    var unconfirmedRefillsTableSize = 15;
    var $pagination = $('#unconfirmed-pagination');
    var $unconfirmedRefillsTable = $('#unconfirmed-refills-table');
    var defaultPaginationOpts = {
        visiblePages: 5,
        first: "<<",
        prev: "<",
        next: ">",
        last: ">>",
        onPageClick: function (event, page) {
            var currency = $('#currencyName').val();
            var offset = (page - 1) * unconfirmedRefillsTableSize;
            getUnconfirmedRefills(currency, unconfirmedRefillsTableSize, offset).done(function (result) {
                if (!result || result['totalCount'] === 0) return;
                fillUnconfirmedRefillsTable($unconfirmedRefillsTable, result['data']);
            });
        }
    };


    var $unconfirmedRefillsContainer = $('#unconfirmed-refills-container');

    /**/
    const numberFormat = '0,0.00[0000000]';
    const $refillDetailedParamsDialog = $('#dialog-refill-confirmation-params-enter');
    const phrases = {
        "bankNotSelected": $("#bank-not-selected").html(),
        "enterOtherBankPhrase": $("#enter-other-bank-phrase").html()
    };

    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(currentCurrencyPair);
    }

    this.syncCurrencyPairSelector = function () {
        inputoutputCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowInputOutputData(refreshIfNeeded);
    };


    this.getAndShowInputOutputData = function (refreshIfNeeded, page, direction) {
        if ($inputoutputContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForInputOutputData);
            timeOutIdForInputOutputData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForInputOutputData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowInputOutputData');
        }
        var $inputoutputTable = $('#' + tableId).find('tbody');
        if (!$($inputoutputTable).length) return;

        var url = '/dashboard/myInputoutputData/' + tableId + '' +
            '?page=' + (page ? page : '') +
            '&direction=' + (direction ? direction : '') +
            '&refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#inputoutput-table_row').html().replace(/@/g, '%');
                    clearTable($inputoutputTable);
                    data.forEach(function (e) {
                        $inputoutputTable.append(tmpl($tmpl, e));
                    });
                    blink($inputoutputTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.inputoutput-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.inputoutput-table__page').text());
                    $('.inputoutput-table__page').text(++p);
                }
                clearTimeout(timeOutIdForInputOutputData);
                timeOutIdForInputOutputData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForInputOutputData);
            }
        });
    };

    this.updateUnconfirmedRefillsTable = function() {
        if (!$('#unconfirmed-refills-container').length) return;
        var $currencyNameInput = $('#currencyName');
        if (!$($currencyNameInput).length) {
            return;
        }
        var currency = $currencyNameInput.val();
        getUnconfirmedRefills(currency, unconfirmedRefillsTableSize, 0).done(function (result) {
            if (!result || result['totalCount'] === 0) {
                $('#unconfirmed-refills-container').hide();
            } else {
                fillUnconfirmedRefillsTable($unconfirmedRefillsTable, result['data']);
                refreshPagination(result['pagesCount']);
            }
        });
    };


    /*=====================================================*/
    (function init(currentCurrencyPair) {
        if (currentCurrencyPair) {
            inputoutputCurrencyPairSelector = new CurrencyPairSelectorClass('inputoutput-currency-pair-selector', currentCurrencyPair);
            inputoutputCurrencyPairSelector.init(onCurrencyPairChange);
        }

        /**/
        syncTableParams(tableId, tablePageSize, function (data) {
            /*that.getAndShowInputOutputData();*/
        });
        /**/
        $('.inputoutput-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowInputOutputData(true, null, 'BACKWARD');
        });
        $('.inputoutput-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowInputOutputData(true, null, 'FORWARD');
        });

        $('#inputoutput-table').on('click', 'tr[data-type=Output].in_out_row', function (e) {
            console.log('click');
            var id = $(this).data("id");
            $.ajax({
                url: '/withdraw/info?requestId=' + id,
                type: 'GET',
                success: function (data) {
                    console.log(data);
                    showWithdrawDialogAfterCreation(data)
                }
            });

        });

        function showWithdrawDialogAfterCreation(data) {
            $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').show();
            $withdrawParamsDialog.find('#message').show();
            $withdrawParamsDialog.find('#message').html(data.message ? data.message : '');
            fillModalWindow(data);
            $withdrawParamsDialog.modal();
        }

        function fillModalWindow(data) {
                var templateVariables = {
                    amount: '__amount',
                    currency: '__currency',
                    merchant: '__merchant',
                    percent: '__percent'
                };
                var newHTMLElements = [];
                modalTemplate.slice().each(function (index, val) {
                    newHTMLElements[index] = '<p>' + $(val).html() + '</p>';
                });
                newHTMLElements[0] = newHTMLElements[0]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + data.amount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + data.currencyName + "</span>")
                    .replace(templateVariables.merchant, "<span class='modal-merchant'>" + data.merchantName + "</span>");
                newHTMLElements[1] = newHTMLElements[1]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + data.comissionAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + data.currencyName + "</span>")
                    .replace(templateVariables.percent, "<span class='modal-amount'></span>");
                newHTMLElements[2] = newHTMLElements[2]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + data.merchantComissionAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + data.currencyName + "</span>")
                    .replace(templateVariables.percent, "<span class='modal-amount'></span>");
                newHTMLElements[3] = newHTMLElements[3]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + data.finalAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + data.currencyName + "</span>");
                newHTMLElements[4] = newHTMLElements[4]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + data.totalFee + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + data.currencyName + "</span>");
                var newHTML = '';
                $.each(newHTMLElements, function (index) {
                    newHTML += newHTMLElements[index];
                });
                $('.paymentInfo').html(newHTML);
                $('.merchantError').hide();
        }

        $('#inputoutput-table').on('click', 'button[data-source=WITHDRAW].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                $.ajax({
                    url: '/withdraw/request/revoke?id=' + id,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                    },
                    type: 'POST',
                    success: function () {
                        that.updateAndShowAll(false);
                    }
                });
            });
            $modal.modal();
        });

        $('#inputoutput-table').on('click', 'button[data-source=USER_TRANSFER].show_code_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#simple-info-modal");
            $.ajax({
                url: '/transfer/request/hash?id=' + id,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                success: function (data) {
                    $modal.find("#info-field").val(data);
                    $modal.modal();
                }
            });
        });

        $('#inputoutput-table').on('click', 'button[data-source=REFILL].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                revokeRefillRequest(id, function () {
                    that.updateAndShowAll(false);
                });
            });
            $modal.modal();
        });

        $('#inputoutput-table').on('click', 'button[data-source=USER_TRANSFER].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                $.ajax({
                    url: '/transfer/request/revoke?id=' + id,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                    type: 'POST',
                    success: function () {
                        that.updateAndShowAll(false);
                    }
                });
            });
            $modal.modal();
        });

        $('#inputoutput-table').on('click', 'button[data-source=REFILL].confirm_user_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            getRequestDataAndShowConfirmDialog(id, function () {
                that.updateAndShowAll(false);
            })
        });

        $($unconfirmedRefillsTable).on('click', 'button[data-source=REFILL].confirm_user_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            getRequestDataAndShowConfirmDialog(id, function () {
                that.updateUnconfirmedRefillsTable();
            })
        });

        $($unconfirmedRefillsTable).on('click', 'button[data-source=REFILL].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                revokeRefillRequest(id, function () {
                    that.updateUnconfirmedRefillsTable();
                });
            });
            $modal.modal();
        });

        if($('#unconfirmed-refills-container').length) {
            initUnconfirmedRefillHistoryTable();
        }

    })(currentCurrencyPair);

    function fillUnconfirmedRefillsTable($unconfirmedRefillsTable, data) {
        var $tmpl = $('#unconfirmed-refills-table-row').html().replace(/@/g, '%');
        clearTable($unconfirmedRefillsTable);
        data.forEach(function (e) {
            $unconfirmedRefillsTable.append(tmpl($tmpl, e));
        });
        blink($unconfirmedRefillsTable.find('td:not(:first-child)'));
    }

    function initPagination(totalPages) {
        $pagination.twbsPagination($.extend({}, defaultPaginationOpts,  {
            totalPages: totalPages
        }));
    }

    function refreshPagination(totalPages) {
        var currentPage = $pagination.twbsPagination('getCurrentPage');
        $pagination.twbsPagination('destroy');
        $pagination.twbsPagination($.extend({}, defaultPaginationOpts,  {
            startPage: currentPage,
            totalPages: totalPages,
            initiateStartPageClick: false
        }));
    }

    function initUnconfirmedRefillHistoryTable() {
        var $currencyNameInput = $('#currencyName');
        if (!$($currencyNameInput).length) {
            return;
        }
        var currency = $currencyNameInput.val();
        getUnconfirmedRefills(currency, unconfirmedRefillsTableSize, 0).done(function (result) {
            if (!result || result['totalCount'] === 0) return;
            fillUnconfirmedRefillsTable($unconfirmedRefillsTable, result['data']);
            initPagination(result['pagesCount']);
            $('#unconfirmed-refills-container').show();
        });

    }

     function getUnconfirmedRefills(currency, limit, offset) {
        var url = '/refill/unconfirmed?currency=' + currency + '&limit=' + limit + '&offset=' + offset;
        return $.ajax({
            url: url,
            type: 'GET'
        });
    }

    function getRequestDataAndShowConfirmDialog(id, confirmCallback) {
        $.ajax({
            url: '/refill/request/info?id=' + id,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'GET',
            success: function (requestData) {
                var refillDetailedParamsDialogResult= false;
                $refillDetailedParamsDialog.find("#invoiceSubmitConfirm").off("click").one("click", function () {
                    refillDetailedParamsDialogResult = true;
                    $refillDetailedParamsDialog.modal("hide");
                });
                $refillDetailedParamsDialog.one("hidden.bs.modal", function () {
                    if (refillDetailedParamsDialogResult) {
                        sendConfirm(requestData.id, confirmCallback);
                    }
                });
                showRefillDetailDialog(requestData);
            }
        });
    }

    function showRefillDetailDialog(data) {
        resetFormConfirm();
        $refillDetailedParamsDialog.find("#amount").html(numbro(data.amount).format(numberFormat));
        $refillDetailedParamsDialog.find("#bank-name").html(data.recipientBankName);
        $refillDetailedParamsDialog.find("#bank-account").html(data.recipientBankAccount);
        $refillDetailedParamsDialog.find("#bank-recipient").html(data.recipientBankRecipient);
        getBankDataList(data.currencyId, function (bankDataList) {
            var $bankSelect = $refillDetailedParamsDialog.find("#bank-data-list-confirm");
            $bankSelect.empty();
            var $bankItem = $("<option> </option>");
            $bankItem.val(-1);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.attr("data-bank-account", "");
            $bankItem.attr("data-bank-recipient", "");
            $bankItem.html(phrases.bankNotSelected);
            $bankSelect.append($bankItem.clone());
            /**/
            bankDataList.forEach(function (bank) {
                $bankItem.val(bank.id);
                $bankItem.attr("data-bank-id", bank.id);
                $bankItem.attr("data-bank-code", bank.code);
                $bankItem.attr("data-bank-name", bank.name);
                $bankItem.attr("data-bank-account", bank.accountNumber);
                $bankItem.attr("data-bank-recipient", bank.recipient);
                $bankItem.html(bank.name);
                $bankSelect.append($bankItem.clone());
            });
            /**/
            $bankItem.val(0);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.html(phrases.enterOtherBankPhrase);
            $bankSelect.append($bankItem.clone());
        });
        $($refillDetailedParamsDialog).find('#invoiceCancelConfirm').one("click", function () {
            that.updateAndShowAll(false);
            that.updateUnconfirmedRefillsTable();

        });
        $refillDetailedParamsDialog.modal({
            backdrop: 'static'
        });
    }

    function getBankDataList(currency, callback) {
        $.ajax({
            url: '/withdraw/banks',
            async: true,
            type: "get",
            contentType: "application/json",
            data: {"currencyId": currency}
        }).success(function (response) {
            if (callback) {
                callback(response);
            }
        });
    }

    function sendConfirm(id, confirmCallback) {
        var data = new FormData();
        data.append('invoiceId', id);
        data.append('payerBankName', $refillDetailedParamsDialog.find('#bank-data-list-confirm').find('option:selected').text());
        data.append('payerBankCode', $refillDetailedParamsDialog.find("#bank-code").val());
        data.append('userAccount', $refillDetailedParamsDialog.find("#user-account").val());
        data.append('userFullName', $refillDetailedParamsDialog.find("#user-full-name").val());
        data.append('remark', $refillDetailedParamsDialog.find("#remark").val());
        data.append('receiptScan', $refillDetailedParamsDialog.find("#receipt-scan")[0].files[0]);
        $.ajax({
            url: '/refill/request/confirm',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: data,
            type: 'POST',
            cache: false,
            contentType: false,
            processData: false,
            enctype: 'multipart/form-data',
            success: function () {
                if (confirmCallback) {
                    confirmCallback();
                }
            }
        });
    }

    function revokeRefillRequest(id, revokeCallback) {
        $.ajax({
            url: '/refill/request/revoke?id=' + id,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            success: function () {
                if (revokeCallback) {
                    revokeCallback();
                }
            }
        });
    }

    this.revokeRefillRequest = function (requestId, callback) {
        revokeRefillRequest(requestId, callback);
    };


    this.getRequestDataAndShowConfirmDialog = function (requestId, confirmCallback) {
        getRequestDataAndShowConfirmDialog(requestId, confirmCallback);
    }



}
