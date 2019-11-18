/**
 * Created by ValkSam on 10.04.2017.
 */
/**
 * Created by ValkSam on 10.04.2017.
 */
var currencyName;
$(function withdrawCreation() {
    const $container = $("#merchants-output-center");
    const operationType = $container.find("#operationType").html();
    const $withdrawParamsDialog = $container.find('#dialog-withdraw-creation');
    const $withdrawDetailedParamsDialog = $container.find('#dialog-withdraw-detailed-params-enter');
    const $loadingDialog = $container.find('#loading-process-modal');
    const $walletAddressDialog = $container.find('#walletAddressModal');
    const $amountHolder = $container.find("#sum");
    const $destinationHolder = $withdrawParamsDialog.find("#walletUid");
    const $destinationTagHolder = $withdrawParamsDialog.find("#address-tag");
    const urlForWithdrawCreate = "/withdraw/request/create";
    const urlForPin = "/withdraw/request/pin";
    const modalTemplate = $container.find('.paymentInfo p');
    const numberFormat = '0,0.00[0000000]';
    const phrases = {
        "bankNotSelected": $container.find("#bank-not-selected").html(),
        "enterOtherBankPhrase": $container.find("#enter-other-bank-phrase").html()
    };
    const $pinDialogModal = $container.find('#pin_modal');
    const $pinDialogText = $pinDialogModal.find('#pin_text');
    const $pinWrong = $pinDialogModal.find('#pin_wrong');
    const $pinSendButton = $container.find("#check-pin-button");
    const $pinInput = $('#pin_code');
    var $continueButton = $('#continue-btn');

    var currency;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var destination;
    var destinationTag;
    var merchantIsSimpleInvoice;
    var additionalFieldNeeded;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var commissionMerchantPercent;
    var commissionMerchantAmount;
    var totalAmount;
    var totalCommissionAmount;
    var bankDataList;
    var additionalFieldName;
    var specComissionCount;
    var comissionDependsOnDestinationTag;
    const cyrillicPattern = /[\u0400-\u04FF]/;

    $container.find(".start-withdraw").on('click', function () {
        startWithdraw(this);
    });


    $destinationHolder.on('input', function (e) {
        checkAddrInput()
    });

    function checkAddrInput() {
        if(checkWithdrawParamsEnter($destinationHolder.val())) {
            $continueButton.prop('disabled', false);
        } else {
            $continueButton.prop('disabled', true);
        }
    }

    function startWithdraw(button) {
        currency = $(button).data("currency-id");
        currencyName = $(button).data("currency-name");
        merchant = $(button).data("merchant-id");
        merchantName = $(button).data("merchant-name");
        merchantMinSum = $(button).data("merchant-min-sum");
        merchantImageId = $(button).data("merchant-image-id");
        merchantIsSimpleInvoice = $(button).data("process_type")=="INVOICE";
        additionalFieldNeeded = $(button).data("additional-field-needed");
        additionalFieldName = $(button).data("additional-field-name");
        amount = parseFloat($amountHolder.val());
        $withdrawParamsDialog.find('#destination-input-wrapper').show();
        $destinationHolder.val('');
        $destinationTagHolder.val('');
        specComissionCount = $(button).data("spec-merchan-comission");
        comissionDependsOnDestinationTag = $(button).data("comission-depends-on-destination-tag");
        console.log(comissionDependsOnDestinationTag);
        if (comissionDependsOnDestinationTag) {
            $destinationTagHolder.on('input', function (e) {
                fillModalWindow()
            });
        }
        if (checkAmount()) {
            fillModalWindow();
            showWithdrawDialog();
        }
        checkAddrInput()
    }

    function fillModalWindow() {

        getCommission(function (response) {
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
                .replace(templateVariables.amount, "<span class='modal-amount'>" + amount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.merchant, "<span class='modal-merchant'>" + merchantName + "</span>");
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionPercent + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionMerchantAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionMerchantPercent + "</span>");
            newHTMLElements[3] = newHTMLElements[3]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + totalAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
            newHTMLElements[4] = newHTMLElements[4]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + totalCommissionAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.paymentInfo').html(newHTML);
            $('.merchantError').hide();
        });
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showWithdrawDialog(message) {
        if (additionalFieldNeeded) {
            $withdrawParamsDialog.find("[for=address-tag]").show();
            $withdrawParamsDialog.find("#address-tag").show();
            $('#additional_field_name').text(additionalFieldName);
        } else {
            $withdrawParamsDialog.find("[for=address-tag]").hide();
            $withdrawParamsDialog.find("#address-tag").hide();
        }
        if (merchantIsSimpleInvoice) {
            $withdrawParamsDialog.find("#merchant-commission-warning").hide();
            performWithdraw();
        } else {
            $withdrawParamsDialog.find("#merchant-commission-warning").hide();
            $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').show();
            $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').hide();
            $withdrawParamsDialog.find('#message').hide();
            $withdrawParamsDialog.find('#message').html(message ? message : '');
            /**/
            $withdrawParamsDialog.find("#continue-btn").off('click').on('click', function () {
                destination = $destinationHolder.val();
                destinationTag = $destinationTagHolder.val();
                if (!checkWithdrawParamsEnter(destination)) {
                    return;
                }
                $.ajax({
                    url: '/withdraw/check?wallet=' + destination + '&merchant=' + merchant,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                    type: 'GET'
                }).success(function (result) {
                    if (result) {
                        $withdrawParamsDialog.one('hidden.bs.modal', function () {
                            performWithdraw();
                        });
                        $withdrawParamsDialog.modal("hide");
                    } else {
                        $walletAddressDialog.modal({
                            backdrop: 'static'
                        });
                        $withdrawParamsDialog.modal("hide");
                    }
                });
            });
            /**/
            $withdrawParamsDialog.modal();
        }
    }

    /*function showFinPassModal() {
        $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
            e.preventDefault();
            var finPassword = $finPasswordDialog.find("#finpassword").val();
            $finPasswordDialog.one("hidden.bs.modal", function () {
                performWithdraw(finPassword);
            });
            $finPasswordDialog.modal("hide");
        });
        $finPasswordDialog.modal({
            backdrop: 'static'
        });
    }*/

    function performWithdraw() {
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            destination: destination,
            destinationTag: destinationTag,
            merchantImage: merchantImageId,
            operationType: operationType,
        };
        if (merchantIsSimpleInvoice) {
            var withdrawDetailedParamsDialogResult = false;
            $withdrawDetailedParamsDialog.find("#invoiceSubmit").off("click").one("click", function () {
                withdrawDetailedParamsDialogResult = true;
                $withdrawDetailedParamsDialog.modal("hide");
            });
            $withdrawDetailedParamsDialog.one("hidden.bs.modal", function () {
                if (withdrawDetailedParamsDialogResult) {
                    data.recipientBankCode = $withdrawDetailedParamsDialog.find("#bank-code").val();
                    var bankId = $withdrawDetailedParamsDialog.find('#bank-data-list').val();
                    data.recipientBankName = bankId == 0 ?
                        $withdrawDetailedParamsDialog.find("#other-bank").val() :
                        $withdrawDetailedParamsDialog.find('#bank-data-list').find('option:selected').text();
                    data.destination = $withdrawDetailedParamsDialog.find("#user-account").val();
                    data.userFullName = $withdrawDetailedParamsDialog.find("#user-full-name").val();
                    data.remark = $withdrawDetailedParamsDialog.find("#remark").val();
                    sendRequest(data);
                }
            });
            showWithdrawDetailDialog();
        } else {
            sendRequest(data);
        }
    }

    function sendRequest(data) {
        $pinWrong.hide();
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForWithdrawCreate,
                async: true,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data)
            }).success(function (result, textStatus, xhr) {
                if (xhr.status == 202 && result.cause == 'PinCodeCheckNeedException') {
                    $pinDialogModal.modal();
                    $pinDialogText.text(result.detail);
                } else {
                    withdrawSuccess(result)
                }
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function withdrawSuccess(result) {
        if (!result || !result['redirectionUrl']) {
            showWithdrawDialogAfterCreation(result['message']);
        } else {
            window.location = result['redirectionUrl'];
        }
    }

    $pinInput.on('input', function (e) {
        checkPinInput()
    });

    function checkPinInput() {
        var value = $pinInput.val();
        if (value.length > 2 && value.length < 15 ) {
            $pinSendButton.prop('disabled', false);
        } else {
            $pinSendButton.prop('disabled', true);
        }
    }



    $pinSendButton.on('click', function () {
        sendPin($pinInput.val());
    });

    function sendPin(pin) {
        $pinWrong.hide();
        $.ajax({
            url: urlForPin + '?pin=' + pin,
            async: true,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json'
        }).success(function (result, textStatus, xhr) {
            if (xhr.status === 200) {
                $pinDialogModal.modal("hide");
                withdrawSuccess(result)
            } else {
                $pinWrong.show();
                $pinDialogText.text(result.message);
                if (result.needToSendPin) {
                    successNoty(result.message)
                }
            }
        }).error(function (result) {

        }).complete(function () {
            $pinInput.val("");
            $pinSendButton.prop('disabled', true);
        });
    }

    function showWithdrawDetailDialog() {
        resetForm();
        $withdrawDetailedParamsDialog.find("#amount").html(numbro(amount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#commission-percent").html(commissionPercent);
        $withdrawDetailedParamsDialog.find("#commission-amount").html(numbro(commissionAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#merchant-commission-amount").html(numbro(commissionMerchantAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#total-amount").html(numbro(totalAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#total-commission-amount").html(numbro(totalCommissionAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find(".currency").html(currencyName);
        getBankDataList(function () {
            var $bankSelect = $withdrawDetailedParamsDialog.find("#bank-data-list");
            $bankSelect.empty();
            var $bankItem = $("<option> </option>");
            $bankItem.val(-1);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.html(phrases.bankNotSelected);
            $bankSelect.append($bankItem.clone());
            /**/
            bankDataList.forEach(function (bank) {
                $bankItem.val(bank.id);
                $bankItem.attr("data-bank-id", bank.id);
                $bankItem.attr("data-bank-code", bank.code);
                $bankItem.attr("data-bank-name", bank.name);
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
        $withdrawDetailedParamsDialog.modal({
            backdrop: 'static'
        });
    }

    function showWithdrawDialogAfterCreation(message) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#destination-input-wrapper').hide();
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $withdrawParamsDialog.find('#message').show();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.modal();
    }

    function checkWithdrawParamsEnter(localDestination) {
        return merchantIsSimpleInvoice || (localDestination.length > 3 && localDestination.length < 1000
            && !cyrillicPattern.test(localDestination) && !/\s/g.test(localDestination));
    }

    function getCommission(callback) {
        $.ajax({
            url: '/withdraw/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currency, "merchant": merchant, "memo" : $destinationTagHolder.val()}
        }).success(function (response) {
            amount = response['amount'];
            commissionPercent = response['companyCommissionRate'];
            commissionAmount = response['companyCommissionAmount'];
            commissionMerchantAmount = response['merchantCommissionAmount'];
            commissionMerchantPercent = response['merchantCommissionRate'];
            totalAmount = response['resultAmount'];
            totalCommissionAmount = response['totalCommissionAmount'];
            var additional = response['addition'];
            $withdrawDetailedParamsDialog.find("#additional").html(additional);
            if (additional != 0) {
                $withdrawDetailedParamsDialog.find("#additional-wrapper").show();
            } else {
                $withdrawDetailedParamsDialog.find("#additional-wrapper").hide();
            }
            if (callback) {
                callback();
            }
        });
    }

    function getBankDataList(callback) {
        $.ajax({
            url: '/withdraw/banks',
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

});

function toTransfer () {
    transferUrl="/merchants/transfer?currency=";
    transferUrl+=currencyName;
    window.open(transferUrl);
};

