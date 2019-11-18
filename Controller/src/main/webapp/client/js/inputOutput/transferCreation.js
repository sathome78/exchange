/**
 * Created by ValkSam
 */

$(function transferCreation() {
    const $container = $("#merchants-transfer-center");
    const operationType = $container.find("#operationType").html();
    const $transferParamsDialog = $container.find('#dialog-transfer-creation');
    const $loadingDialog = $container.find('#loading-process-modal');
    const $amountHolder = $container.find("#sum");
    const $recipientHolder = $transferParamsDialog.find("#recipient");
    const urlForTransferCreate = "/transfer/request/create";
    const modalTemplate = $container.find('.transferInfo p');
    const urlForPin = "/transfer/request/pin";
    const $pinDialogModal = $container.find('#pin_modal');
    const $pinDialogText = $pinDialogModal.find('#pin_text');
    const $pinWrong = $pinDialogModal.find('#pin_wrong');
    const $pinSendButton = $container.find("#check-pin-button");
    const $pinInput = $('#pin_code');

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var recipient;
    var recipientUserIsNeeded;
    var amount;
    var commissionAmount;
    var commissionMerchantPercent;
    var commissionMerchantAmount;
    var totalAmount;
    var isVoucher;

    $container.find(".start-transfer").on('click', function () {
        startTransfer(this);
    });

    function startTransfer(button) {
        currency = $(button).data("currency-id");
        currencyName = $(button).data("currency-name");
        merchant = $(button).data("merchant-id");
        merchantName = $(button).data("merchant-name");
        merchantMinSum = $(button).data("min-sum");
        merchantImageId = $(button).data("merchant-image-id");
        isVoucher = $(button).data("process_type").startsWith("VOUCHER");
        recipientUserIsNeeded = $(button).data("recipient-user-needed");
        amount = parseFloat($amountHolder.val());
        if (checkAmount()) {
            fillModalWindow();
            showTransferDialog();
        }
    }

    function fillModalWindow() {
        getCommission(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                percent: '__percent',
                transferType: '__transferType'
            };
            var newHTMLElements = [];
            modalTemplate.slice().each(function (index, val) {
                newHTMLElements[index] = '<p>' + $(val).html() + '</p>';
            });
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.transferType, "<span class='modal-amount'>" + merchantName + "</span>")
                .replace(templateVariables.amount, "<span class='modal-amount'>" + amount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionMerchantAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionMerchantPercent + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + totalAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.transferInfo').html(newHTML);
        });
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showTransferDialog(message) {
        if (recipientUserIsNeeded) {
            $transferParamsDialog.find("#recipient-input-wrapper").show();
        } else {
            $transferParamsDialog.find("#recipient-input-wrapper").hide();
        }
        $transferParamsDialog.find('#request-money-operation-btns-wrapper').show();
        $transferParamsDialog.find('#response-money-operation-btns-wrapper').hide();
        $transferParamsDialog.find('#message').hide();
        $transferParamsDialog.find('#hash').hide();
        $transferParamsDialog.find('#message').html(message ? message : '');
        /**/
        $transferParamsDialog.find("#continue-btn").off('click').on('click', function () {
            recipient = $recipientHolder.val();
            if (!checkTransferParamsEnter(recipient)) {
                return;
            }

            $transferParamsDialog.one('hidden.bs.modal', function () {
                checkReception();
            });
            $transferParamsDialog.modal("hide");
        });
        /**/
        $transferParamsDialog.modal();
    }

    /*function showFinPassModal() {
     $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
     e.preventDefault();
     var finPassword = $finPasswordDialog.find("#finpassword").val();
     $finPasswordDialog.one("hidden.bs.modal", function () {
     performTransfer(finPassword);
     });
     $finPasswordDialog.modal("hide");
     });
     $finPasswordDialog.modal({
     backdrop: 'static'
     });
     }*/

    function checkReception() {
        recipient = recipientUserIsNeeded ? recipient : '';
        $.ajax({
            url: '/transfer/request/checking?recipient='+ recipient,
            async: true,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json'
        }).success(function () {
            performTransfer();
        }).error(function () {
            $transferParamsDialog.modal("hide");
        });
    }

    function performTransfer() {
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            recipient: recipientUserIsNeeded ? recipient : '',
            operationType: operationType
        };
        sendRequest(data);
    }

    function sendRequest(data) {
        $pinWrong.hide();
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForTransferCreate,
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
                    transferSuccess(result)
                }
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function transferSuccess(result) {
        showTransferDialogAfterCreation(result['message'], result['hash']);
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
                transferSuccess(result)
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

    function showTransferDialogAfterCreation(message, hash) {
        $transferParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $transferParamsDialog.find('#recipient-input-wrapper').hide();
        $transferParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $transferParamsDialog.find('#message').show();
        $transferParamsDialog.find('#message').html(message ? message : '');
        if(hash) {
            $transferParamsDialog.find('#hash').show();
            $transferParamsDialog.find('#hash_field').text(hash);
        }
        $transferParamsDialog.modal();
    }

    function checkTransferParamsEnter(value) {
        return !recipientUserIsNeeded ||
            /^\D+[\w\d\-_]+/.test(value);

    }

    function getCommission(callback) {
        $.ajax({
            url: '/transfer/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currency, "merchant": merchant}
        }).success(function (response) {
            amount = response['amount'];
            commissionAmount = response['companyCommissionAmount'];
            commissionMerchantAmount = response['merchantCommissionAmount'];
            commissionMerchantPercent = response['merchantCommissionRate'];
            totalAmount = response['resultAmount'];
            if (callback) {
                callback();
            }
        });
    }

});
