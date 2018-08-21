var $dialogConfirm;
$(function () {
    $dialogConfirm = $("#dialog-refill-confirmation-params-enter");
    $dialogConfirm.find("#bank-data-list-confirm").val(-1);
    $($dialogConfirm).find(".credits-operation-enter__item").on("change input", function (event) {
        checkAllConfirmFields();
    });
});

function resetFormConfirm() {
    [].forEach.call($dialogConfirm.find(".credits-operation-enter__item"), function (item) {
        $(item).val("");
    });
    $dialogConfirm.find("#bank-data-list").val(-1);
    checkAllConfirmFields();
}

function onSelectNewValueConfirm(select) {
    var bankId = $('#bank-data-list-confirm').val();
    var $bankInfoOption = $(select).find("option[value=" + bankId + "]");
    var $bankCode = $dialogConfirm.find("#bank-code");
    $bankCode.val($bankInfoOption.data("bank-code"));

    var $infoWrapper = $dialogConfirm.find("#credits-operation-info");
    var $otherBankWrapper = $("#other-bank-wrapper");
    if (bankId == -1) {
        $otherBankWrapper.hide();
    } else if (bankId == 0) {
        $otherBankWrapper.show();
    } else {
        $otherBankWrapper.hide();
    }

}

function checkAllConfirmFields() {
    var result = true;
    [].forEach.call($($dialogConfirm).find(".credits-operation-enter__item"), function (item) {
        result = checkConfirmField($(item)) && result;
    });
    if (result) {
        $('#invoiceSubmitConfirm').prop('disabled', false);
    } else {
        $('#invoiceSubmitConfirm').prop('disabled', true);
    }
}

function checkConfirmField($elem) {
    const DIGITS_ONLY_REGEX = /^\d+$/;
    const NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*( [a-zA-Z]([-']?[a-zA-Z]+)*)*$/;
    const BANK_NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+[.]*)*([ ,.]{0,2}[a-zA-Z\d&]([-']?[a-zA-Z\d]+)*[.]*)*$/;
    const BANK_CODE_REGEX = /^[\d]{2,5}$/;

    var result = true;
    var elemId = $elem.attr("id");
    var bankId = $('#bank-data-list-confirm').val();

    if ($elem.closest(".input-block-wrapper").css("display") == 'none') {
        result = true;
    } else if (elemId == "bank-data-list") {
        if (bankId == -1) {
            result = false;
        }
    } else if (elemId == "other-bank") {
        result = validateString($elem, BANK_NAME_REGEX, $('#bankNameError'), false);
    } else if (elemId == "user-account") {
        result = validateString($elem, DIGITS_ONLY_REGEX, $('#userAccountError'), false);
    } else if (elemId == "user-full-name") {
        result = validateString($elem, NAME_REGEX, $('#userFullNameConfirmError'), false);
    } else if (elemId == "bank-code") {
        if (bankId != 0) {
            result = true;
        } else {
            result = validateString($elem, BANK_CODE_REGEX, null, false, true);
        }
    } else if (elemId == "receipt-scan") {
        result = $elem && $elem[0].files.length > 0
    }
    return result;
}