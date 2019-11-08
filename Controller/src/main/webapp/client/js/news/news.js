/**
 * Created by Valk on 20.06.2016.
 */
function NewsClass($loadingImg) {
    if (NewsClass.__instance) {
        return NewsClass.__instance;
    } else if (this === window) {
        return new NewsClass(currentCurrencyPair);
    }
    NewsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var tableNewsId = "news-table";
    var $imageUpload = $('#imageUpload');
    var $clearFormDialog = $('#clearFormDialog');
    /**/
    var timeOutIdForStatisticsForNews;
    var refreshIntervalForStatisticsForNews = 60000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;

    this.$loadingImg = $loadingImg;

    this.getNewsList = function (refreshIfNeeded) {
        if (!windowIsActive) {
            clearTimeout(timeOutIdForStatisticsForNews);
            timeOutIdForStatisticsForNews = setTimeout(function () {
                that.getNewsList(true);
            }, refreshIntervalForStatisticsForNews);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getNewsList');
        }
        var $newsTable = $('#' + tableNewsId);
        // var url = '/dashboard/news/' + tableNewsId + '?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        var url = '/dashboard/newsTwitter';
        if (that.$loadingImg) that.$loadingImg.removeClass('hidden');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#news_table_row').html().replace(/@/g, '%');
                    $newsTable.find('.news_item').remove();
                    data.forEach(function (e) {
                        var $newItem = $(tmpl($tmpl, e));
                        $newsTable.append($newItem);
                    });
                    blink($newsTable);
                }

                clearTimeout(timeOutIdForStatisticsForNews);
                timeOutIdForStatisticsForNews = setTimeout(function () {
                    that.getNewsList(true);
                }, refreshIntervalForStatisticsForNews);
            },
            complete: function(){
                if (that.$loadingImg) that.$loadingImg.addClass('hidden');
            }
        });
    };

    this.addNews =function() {
        var newNews = true;
        $('#newsIdEd').val(null);
        $('#variantEd').val($('#language').text().trim().toLowerCase());
        clearAddNewsForm();
        $(".news-add-info__date").css({'height': '2rem'});
        $("#newsId").val(null);
        $('#news-add-modal').modal();
    };

    this.addNewsVariant = function(newsId, resource) {
        $(".news-add-info__date").css({'overflow': 'hidden', 'height': '0'});
        $("#newsId").val(newsId);
        $("#resource").val(resource);
        $('#news-add-modal').modal();
    };

    this.deleteNewsVariant = function(newsId, resource, variant) {
        $("#delete-newsId").val(newsId);
        $("#delete-newsVariant").val(variant);
        $("#delete-resource").val(resource);
        $('#news-delete-modal').modal();
    };

    this.saveNewsVariant = function(newNews) {
        var isError = false;
        $('.input-block-wrapper__error-wrapper').toggle(false);
        if (newNews && (!$('#newsDate').val() || !$('#newsDate').val().trim().match(/\d{4}\-\d{2}\-\d{2}/))) {
            $('.input-block-wrapper__error-wrapper[for=newsDate]').toggle(true);
            isError = true;
        }
        if (isError) {
            return;
        }
        $('#news-add-modal').one('hidden.bs.modal', function (e) {
            var data = new FormData($('#news-add-info__form')[0]);
            $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/news/addNewsVariant',
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                enctype: 'multipart/form-data',
                type: 'POST',
                success: function (data) {
                    that.getNewsList();
                    successNoty(data.result);
                }
            });
        });
        $('#news-add-modal').modal('hide');
    };

    this.removeNewsVariant = function(newNews) {
        $('#news-delete-modal').one('hidden.bs.modal', function (e) {
            var data = $('#news-delete-info__form').serialize();
            $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/news/deleteNews',
                data: data,
                type: 'POST',
                success: function (data) {
                    that.getNewsList();
                    successNoty(data.result);
                }
            });
        });
        $('#news-delete-modal').modal('hide');
    };

    /*===========================================================*/
    (function init() {

        syncTableParams(tableNewsId, -1, function (data) {
            that.getNewsList();
            if (that.$loadingImg) that.$loadingImg.addClass('hidden');
        });
        $('#all_news_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true

        });
        $('#archive_news_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true

        });

        if ($('#showAllNews').length) {
            $('#showAllNews').click(function () {
                var $allNewsTable = $('#all_news_table');
                clearTable($allNewsTable);
                $.ajax('/news/findAllNewsVariants', {
                    type: 'GET',
                    success: function (data) {
                        if (!data) return;
                        var $tmpl = $('#all_news_table_row').html().replace(/@/g, '%');
                        data.forEach(function (e) {
                            var $newItem = $(tmpl($tmpl, e));
                            $allNewsTable.append($newItem);
                        });
                    }
                });
                $('#news-list-modal').modal();
            });
        }

        $('#newsArchive').click(function () {
            var url = '/dashboard/news/' + tableNewsId;
            var $arcNewsTable = $('#archive_news_table');
            clearTable($arcNewsTable);
            $.ajax(url, {
                type: 'GET',
                success: function (data) {
                    if (!data) return;
                    var $tmpl = $('#archive_news_table_row').html().replace(/@/g, '%');
                    data.forEach(function (e) {
                        var $newItem = $(tmpl($tmpl, e));
                        $arcNewsTable.append($newItem);
                    });
                }
            });
            $('#news-archive-modal').modal();
        });

        $('#add-news-button').on('click', that.addNews);
        $('#variantEd').val($('#language').text().trim().toLowerCase());
        $('#variantEd').change(function () {
            if ($('#newsIdEd').val()) {
                retrieveNewsWithContent($('#newsIdEd').val())
            }
        });

        $($clearFormDialog).dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            position: {
                my: "center top",
                at: "center top",
                of:  $('#editor')
            },
            buttons: {
                "Yes": function () {
                    clearAddNewsForm();
                    $(this).dialog("close");
                },
                "No": function () {
                    $('#titleEd').prop('readonly', false);
                    $('#briefEd').prop('readonly', false);
                    $(this).dialog("close");
                }
            }
        });
        initTinyMce();
    })();

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

     this.submitNews = function(newNews) {
        if ($('#news-add-modal').find('li').has('a[href="#editor"]').hasClass('active')) {
            submitNewsFromEditor();
        } else {
           that.saveNewsVariant(newNews);
        }
    };

    this.updateVariant = function (newsId, resource) {
        var newNews = false;
        if ($('#news-add-modal').find('li').has('a[href="#editor"]').hasClass('active')) {
            updateNewsInEditor(newsId)
        } else {
            that.addNewsVariant(newsId, resource);
        }
    };


    function submitNewsFromEditor() {
        var text = tinymce.activeEditor.getContent();
        var container = {
            id: $('#newsIdEd').val(),
            title: $('#titleEd').val(),
            brief: $('#briefEd').val(),
            content: text,
            date: $('#dateEd').val(),
            resource: $('#resourceEd').val(),
            newsVariant: $('#variantEd').val()
        };
        $.ajax("/news/addNewsFromEditor", {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(container),
            success: function (data) {
                $('#newsIdEd').val(data.id);
                $('#dateEd').val(data.date);
                $('#variantEd').val(data.newsVariant);
                $('#resourceEd').val(data.resource);
                $('#titleEd').prop('readonly', true);
                $('#briefEd').prop('readonly', true);
                successNoty('News saved successfully');
            }
        })
    }

    function updateNewsInEditor(newsId) {
        retrieveNewsWithContent(newsId);
        $('#news-add-modal').modal();
    }

    function retrieveNewsWithContent(newsId) {
        $.ajax("/news/getNewsVariant?newsId=" + newsId + "&language=" + $('#variantEd').val(), {
            type: "GET",
            success: function (data) {
                if (!data || data.length === 0) {
                    $($clearFormDialog).dialog('open');
                } else {
                    $('#newsIdEd').val(data.id);
                    $('#dateEd').val(data.date);
                    $('#variantEd').val(data.newsVariant);
                    $('#resourceEd').val(data.resource);
                    $('#titleEd').val(data.title);
                    $('#briefEd').val(data.brief);
                    $('#titleEd').prop('readonly', true);
                    $('#briefEd').prop('readonly', true);
                    tinymce.activeEditor.setContent(data.content);
                }

            }
        });
    }

    function clearAddNewsForm() {
        $('#dateEd').val('');
        $('#resourceEd').val('');
        $('#titleEd').val('');
        $('#briefEd').val('');
        $('#titleEd').prop('readonly', false);
        $('#briefEd').prop('readonly', false);
        tinymce.activeEditor.setContent('');
    }




}