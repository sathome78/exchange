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

var $imageUpload = $('#imageUpload');

$(function () {
    $('#editorSystemMessageForUser').on('click', callEditorSystemMessageForUser);
    initTinyMce();

    function callEditorSystemMessageForUser() {
        $('#news-add-modal').modal();
    }

    function initTinyMce() {
        var language = $('#language').text().trim().toLowerCase();
        if (language === 'cn') {
            language = 'zh_CN';
        } else if (language === 'in') {
            language = 'id';
        }
        var languageUrl = language === 'en' ? '' : '/client/js/tinymce/langs/' + language +'.js';

        tinymce.init({
            selector:'#tinymce',
            height: 500,
            theme: 'modern',
            plugins: [
                'advlist autolink lists link image charmap print preview hr anchor pagebreak',
                'searchreplace wordcount visualblocks visualchars code fullscreen',
                'insertdatetime media nonbreaking table contextmenu directionality',
                'emoticons template paste textcolor colorpicker textpattern imagetools'
            ],
            toolbar1: 'insertfile undo redo | bold italic | fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | ' +
            'bullist numlist outdent indent',
            toolbar2: 'print preview | forecolor backcolor emoticons | link image media | code',
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
            file_browser_callback: function(field_name, url, type, win) {
                $($imageUpload).one('change', function () {
                    submitImage(function (data) {
                        if (data.location) {
                            $('#' + field_name).val(data.location);
                        }
                        $($imageUpload).val('');
                    })
                });
                $($imageUpload).trigger('click');
            },
            file_browser_callback_types: 'image',
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

    function submitImage(successCallback) {
        var data = new FormData($('#imageUploadForm')[0]);
        data.append("newsId", $('#newsIdEd').val());
        $.ajax('/news/uploadImage', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: "POST",
            contentType: false,
            processData: false,
            data: data,
            success: function (data) {
                successCallback(data);
            }
        });
    }
});



