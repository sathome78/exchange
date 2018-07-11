$(document).ready(function () {

    var $enabled = $('#enabled');
    var $minutes = $('#minutes');
    var $length = $('#length');
    var $sendButton = $('#update_updates');

    checkFields();

    $('.ch_field').on('input', function (e) {
        console.log("input");
        checkFields()
    });

    function checkFields() {
        if ($enabled.text() == 'true') {
            $sendButton.prop("disabled", false)
        } else if (checkMinutesField() && checkLengthField()) {
            $sendButton.prop("disabled", false)
        } else {
            $sendButton.prop("disabled", true)
        }
    }

    function checkMinutesField() {
        var value = $minutes.val();
        console.log("min " + value);
        return isInteger(value) && value < 1440;
    }

    function checkLengthField() {
        var value = $length.val();
        console.log("length " + value);
        return isInteger(value) && value < 1440;
    }

    function isInteger(x) {
        var isInt = parseInt(x, 10) == x;
        console.log(isInt);
        return isInt;
    }
});

$(function () {
    $('#editorSystemMessageForUser').on('click', callEditorSystemMessageForUser);
    $('#editorSystemMessageForUser').on('click', getInfoWhenCallModalViewForEditingSysMessage);

    $('#languageVarSysMess').on('select, change', getInfoWhenCallModalViewForEditingSysMessage);

    initTinyMce();

    $('#system-alert-message-update').on('click', sendUpdateSystemMessageToUser);
});

/**
 * Call modal view for editing system message for users
 */
function callEditorSystemMessageForUser() {
    $('#alert-sys-mess-update-modal').modal();
}

/**
 * Method for getting info from db for alert system message for user with concrete lagnuage
 */
function getInfoWhenCallModalViewForEditingSysMessage() {
    var language = $('#languageVarSysMess').val();
    $.ajax({
        url: '/2a8fy7b07dxe44/alerts/sysmessage?language=' + language,
        type: 'GET',
        success: function (data) {
            if (!data) {
                return;
            } else {
                $('#titleSysMess').val(data.title);
                tinymce.activeEditor.setContent(data.text);
                $('#languageVarSysMess').val(data.language);
            }
        },
        error: function (err) {
            console.log(err);
        }
    });
}

/**
 * Method for update alert system message for users in db
 */
function sendUpdateSystemMessageToUser() {
    var title = $('#titleSysMess').val();
    var text = tinymce.activeEditor.getContent();
    var language = $('#languageVarSysMess').val();

    $.ajax({
        url: '/2a8fy7b07dxe44/alerts/sysmessage/update',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: {
            "title":title,
            "text":text,
            "language":language
        },
        success: function () {
            successNoty('System message for users update successfully.');
            $("#alert-sys-mess-update-modal").modal('hide');
        },
        error: function (err) {
            console.log(err);
        }
    });
}

/**
 * Init texteditor
 */
function initTinyMce() {
    var language = $('#language').text().trim().toLowerCase();
    if (language === 'cn') {
        language = 'zh_CN';
    } else if (language === 'in') {
        language = 'id';
    }
    var languageUrl = language === 'en' ? '' : '/client/js/tinymce/langs/' + language +'.js';

    tinymce.init({
        selector:'#tinymce-sys-editor',
        height: 500,
        theme: 'modern',
        plugins: [
            'advlist autolink lists link charmap print preview hr anchor pagebreak',
            'searchreplace wordcount visualblocks visualchars code fullscreen',
            'insertdatetime nonbreaking table contextmenu directionality',
            'emoticons template paste textcolor colorpicker textpattern'
        ],
        toolbar1: 'undo redo | bold italic | fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | ' +
        'bullist numlist outdent indent',
        toolbar2: 'print preview | forecolor backcolor emoticons | link | code',
        image_advtab: true,
        language_url: languageUrl,
        language: language,
        relative_urls: false,
        font_formats : "Andale Mono=andale mono,times;"+
        "Arial=arial,helvetica,sans-serif;"+
        "Arial Black=arial black,avant garde;"+
        "Book Antiqua=book antiqua,palatino;"+
        "Comic Sans MS=comic sans ms,sans-serif;"+
        "Courier New=courier new,courier;"+
        "Georgia=georgia,palatino;"+
        "Helvetica=helvetica;"+
        "Impact=impact,chicago;"+
        "Symbol=symbol;"+
        "Tahoma=tahoma,arial,helvetica,sans-serif;"+
        "Terminal=terminal,monaco;"+
        "Times New Roman=times new roman,times;"+
        "Trebuchet MS=trebuchet ms,geneva;"+
        "Verdana=verdana,geneva;"+
        "Webdings=webdings;"+
        "Wingdings=wingdings,zapf dingbats",
        fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
        templates: [
            { title: 'Test template 1', content: 'Test 1' },
            { title: 'Test template 2', content: 'Test 2' }
        ],
        content_css: [
            '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
            '//www.tinymce.com/css/codepen.min.css'
        ]
    });
    $(document).on('focusin', function(e) {
        if ($(e.target).closest(".mce-window, .moxman-window").length) {
            e.stopImmediatePropagation();
        }
    });
}