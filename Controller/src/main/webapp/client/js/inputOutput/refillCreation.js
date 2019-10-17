/**
 * Created by ValkSam on 10.04.2017.
 */
/**
 * Created by ValkSam on 10.04.2017.
 */

$(function refillCreation() {
    const $container = $("#merchants-input-center");
    const operationType = $container.find("#operationType").html();
    const $refillParamsDialog = $container.find('#dialog-refill-creation');
    const $refillDetailedParamsDialog = $container.find('#dialog-refill-detailed-params-enter');
    const $loadingDialog = $container.find('#loading-process-modal');
    const $amountHolder = $container.find("#sum");
    const notifications = new NotificationsClass();
    const inputOutput = new InputOutputClass();
    const urlForRefillCreate = "/refill/request/create";
    const modalTemplate = $container.find('.paymentInfo p');
    const numberFormat = '0,0.00[0000000]';
    const phrases = {
        "bankNotSelected": $container.find("#bank-not-selected").html(),
        "enterOtherBankPhrase": $container.find("#enter-other-bank-phrase").html()
    };

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var merchantIsSimpleInvoice;
    var merchantIsCrypto;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var commissionMerchantPercent;
    var commissionMerchantAmount;
    var totalAmount;
    var bankDataList;
    var merchantWarningList;
    var childMerchant;

    $container.find(".start-refill").on('click', function () {
        startRefill(this);
    });

    $('#merchants-input-center').on('click', '#address-copy', function (event) {
        selectAndCopyText($(event.currentTarget).siblings("#address-to-pay"));
    });

    $('#merchants-input-center').on('click', '#add-address-copy', function (event) {
        selectAndCopyText($(event.currentTarget).siblings("#add-address-to-pay"));
    });

    function startRefill(button) {
        currency = $(button).data("currency-id");
        currencyName = $(button).data("currency-name");
        merchant = $(button).data("merchant-id");
        merchantName = $(button).data("merchant-name");
        merchantMinSum = $(button).data("merchant-min-sum");
        merchantImageId = $(button).data("merchant-image-id");
        merchantIsSimpleInvoice = $(button).data("process_type") == "INVOICE";
        merchantIsCrypto = $(button).data("process_type") == "CRYPTO";
        amount = parseFloat($amountHolder.val());
        childMerchant = $(button).data("merchant-child-merchant");
        if (merchantIsCrypto || checkAmount()) {
            fillInterkassaInputCommission();
            fillModalWindow();
            showRefillDialog();
        }
    }

    function fillInterkassaInputCommission() {
        $.ajax({
            type: "GET",
            url: "/getMerchantInputCommissionNotification?merchant_id=" + merchant + "&currency_id=" + currency + "&child_merchant=" + childMerchant,
            success: function (data) {
                $('#merchant-warnings').text(data['message']);
            },
            error: function (data) {
                alert('Something happened wrong: ' + data.statusText);
            }
        });
    }

    function fillModalWindow() {
        if (amount) {
            getCommission(function (response) {
                var templateVariables = {
                    amount: '__amount',
                    currency: '__currency',
                    merchant: '__merchant',
                    percent: '__percent'
                };
                var newHTMLElements = [];
                modalTemplate.slice().each(function (index, val) {
                    var elementId = $(val).attr('id');
                    var elementIdString = elementId ? ' id="' + elementId + '"' : '';
                    newHTMLElements[index] = '<p' + elementIdString + '>' + $(val).html() + '</p>';
                });

                newHTMLElements[0] = newHTMLElements[0]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + amount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                    .replace(templateVariables.merchant, "<span class='modal-merchant'>" + merchantName + "</span>");
                newHTMLElements[1] = newHTMLElements[1]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionMerchantAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                    .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionMerchantPercent + "</span>");
                newHTMLElements[2] = newHTMLElements[2]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                    .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionPercent + "</span>");
                newHTMLElements[3] = newHTMLElements[3]
                    .replace(templateVariables.amount, "<span class='modal-amount'>" + totalAmount + "</span>")
                    .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
                var newHTML = '';

                $.each(newHTMLElements, function (index) {
                    newHTML += newHTMLElements[index];
                });
                $('.paymentInfo').html(newHTML);
                if (merchantIsCrypto || merchantIsSimpleInvoice) {
                    $('#payment-info-commission-merchant').hide();
                }
                $('.merchantError').hide();
                $("#amount-info-wrapper").show();
            });
        } else {
            $("#amount-info-wrapper").hide();
        }
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showRefillDialog(message) {
        if (merchantIsSimpleInvoice) {
            $refillParamsDialog.find("#merchant-commission-warning").hide();
            performRefill();
        } else if (merchantIsCrypto) {
            performRefill();
        } else {
            $refillParamsDialog.find("#merchant-warnings").empty();

            getMerchantWarnings(function () {
                $refillParamsDialog.find("#merchant-warnings").append('<br/>');
                merchantWarningList.forEach(function (item) {
                    $refillParamsDialog.find("#merchant-warnings").append(item + '<br/>');
                })
            });
            $refillParamsDialog.find("#merchant-commission-warning").show();
            $refillParamsDialog.find('#request-money-operation-btns-wrapper').show();
            $refillParamsDialog.find('#destination-input-wrapper').show();
            $refillParamsDialog.find('#response-money-operation-btns-wrapper').hide();
            $refillParamsDialog.find('#message').hide();
            $refillParamsDialog.find('#message').html(message ? message : '');
            $refillParamsDialog.find('#payment-qr').html('');
            $refillParamsDialog.find("#continue-btn").off('click').on('click', function () {

                $("#warning-remporary-validity-refill-request-merchant").modal({
                    backdrop: 'static'
                });

                window.open("about:blank", "newwin");
                if (!checkRefillParamsEnter()) {
                    return;
                }
                $refillParamsDialog.one('hidden.bs.modal', function () {
                    performRefill();
                });
                $refillParamsDialog.modal("hide");
            });
            $refillParamsDialog.modal();
        }
    }

    function checkRefillParamsEnter() {
        return true;
    }

    function performRefill() {
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            merchantImage: merchantImageId,
            childMerchant: childMerchant,
            operationType: operationType
        };
        if (merchantIsSimpleInvoice) {
            var refillDetailedParamsDialogResult = false;
            $refillDetailedParamsDialog.find("#invoiceSubmit").off("click").one("click", function () {
                refillDetailedParamsDialogResult = true;
                $refillDetailedParamsDialog.modal("hide");
            });
            $refillDetailedParamsDialog.one("hidden.bs.modal", function () {
                if (refillDetailedParamsDialogResult) {
                    data.recipientBankId = $refillDetailedParamsDialog.find("#bank-id").html();
                    data.recipientBankCode = $refillDetailedParamsDialog.find("#bank-code").html();
                    data.recipientBankName = $refillDetailedParamsDialog.find("#bank-name").html();
                    data.recipient = $refillDetailedParamsDialog.find("#bank-recipient").html();
                    data.address = $refillDetailedParamsDialog.find("#bank-account").html();
                    data.userFullName = $refillDetailedParamsDialog.find("#user-full-name").val();
                    data.remark = $refillDetailedParamsDialog.find("#remark").val();
                    sendRequest(data);
                }
            });
            showRefillDetailDialog();
        } else {
            data.generateNewAddress = true;
            sendRequest(data);
        }
    }

    function sendRequest(data) {
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForRefillCreate,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data)
            }).success(function (result) {
                console.log(result);
                console.log("Send refill requests data currency is " + data.currency);
                dataLayer.push({'event':'Refill','eventCategory':'Refill_coin','EventLabel':""+data.currency+""});
                console.log("Send refill requests succesfully");
                if (!result || !result['redirectionUrl']) {
                    var qrElement = result['params']['qr'];
                    var qrTag = qrElement ? qrElement : '';
                    showRefillDialogAfterCreation(result['params']['message'], qrTag, result['requestId']);
                    notifications.getNotifications();
                } else {
                    if (!result['method']) {
                        window.location = result['redirectionUrl'];
                    } else {
                        redirectByPost(
                            result['redirectionUrl'],
                            result);
                    }
                }
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function redirectByPost(url, params) {
        var formFields = '';
        var method = params["method"];
        $.each(params["params"], function (key, value) {
            formFields += '<input type="hidden" name="' + key + '" value="' + value + '">';
        });
        var $form = $('<form id=temp-form-for-redirection target="newwin" action=' + url + ' method=' + params["method"] + '>' + formFields + '</form>');
        $("body").append($form);
        $form.submit();
        $("#temp-form-for-redirection").remove();
    }

    function showRefillDetailDialog() {
        resetForm();
        $refillDetailedParamsDialog.find("#amount").html(numbro(amount).format(numberFormat));
        $refillDetailedParamsDialog.find("#commission-percent").html(commissionPercent);
        $refillDetailedParamsDialog.find("#commission-amount").html(numbro(commissionAmount).format(numberFormat));
        $refillDetailedParamsDialog.find("#total-amount").html(numbro(totalAmount).format(numberFormat));
        $refillDetailedParamsDialog.find(".currency").html(currencyName);
        getBankDataList(function () {
            var $bankSelect = $refillDetailedParamsDialog.find("#bank-data-list");
            $bankSelect.empty();
            var $bankItem = $("<option> </option>");
            $bankItem.val(-1);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.attr("data-bank-account", "");
            $bankItem.attr("data-bank-recipient", "");
            $bankItem.attr("data-bank-details", "");
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
                $bankItem.attr("data-bank-details", bank.bankDetails);
                $bankItem.html(bank.name);
                $bankSelect.append($bankItem.clone());
            });
        });
        $refillDetailedParamsDialog.modal({
            backdrop: 'static'
        });
    }

    function showRefillDialogAfterCreation(message, qrTag, requestId) {
        if (merchantIsSimpleInvoice) {
            $refillParamsDialog.find("#simple-invoice-btns-wrapper").show();
        } else {
            $refillParamsDialog.find("#simple-invoice-btns-wrapper").hide();
        }
        $refillParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $refillParamsDialog.find('#destination-input-wrapper').hide();
        $refillParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $refillParamsDialog.find('#message').show();
        $refillParamsDialog.find('#message').html(message ? message : '');
        $refillParamsDialog.find('#payment-qr').attr("src", qrTag ? 'https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=' + qrTag : '');

        $('#request-confirm-btn').click(function () {
            $refillParamsDialog.modal('hide');
            inputOutput.getRequestDataAndShowConfirmDialog(requestId, function () {
                window.location.reload(true);
            });
        });
        $('#request-revoke-btn').click(function () {
            $refillParamsDialog.modal('hide');
            inputOutput.revokeRefillRequest(requestId, function () {
                window.location.reload(true);
            });
        });
        $('#dialog-refill-creation-close').one("click", function () {
            window.location.reload(true);
        });

        $refillParamsDialog.modal();
    }

    function getCommission(callback) {
        $.ajax({
            url: '/refill/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currency, "merchant": merchant}
        }).success(function (response) {
            amount = response['amount'];
            commissionPercent = response['companyCommissionRate'];
            commissionAmount = response['companyCommissionAmount'];
            commissionMerchantAmount = response['merchantCommissionAmount'];
            commissionMerchantPercent = response['merchantCommissionRate'];
            totalAmount = response['resultAmount'];
            var additional = response['addition'];
            $refillDetailedParamsDialog.find("#additional").html(additional);
            if (additional != 0) {
                $refillDetailedParamsDialog.find("#additional-wrapper").show();
            } else {
                $refillDetailedParamsDialog.find("#additional-wrapper").hide();
            }
            if (callback) {
                callback();
            }
        });
    }

    function getBankDataList(callback) {
        $.ajax({
            url: '/refill/banks',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"currencyId": currency}
        }).success(function (response) {
            bankDataList = response;
            if (callback) {
                callback();
            }
        });
    }

    function getMerchantWarnings(callback) {
        $.ajax({
            url: '/merchants/warnings',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"merchant": merchant, "type": operationType}
        }).success(function (response) {
            merchantWarningList = response;
            if (callback) {
                callback();
            }
        });
    }
});

window.dataLayer = window.dataLayer || [];
function gtag(){dataLayer.push(arguments);}
gtag('js', new Date());

gtag('config', 'UA-75711135-1');