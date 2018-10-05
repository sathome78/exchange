$(function () {
    $('#download-chats').on('click', function (e) {
        const fullUrl = '/2a8fy7b07dxe44/chat/allHistory?lang='+chatLanguage;
        $.get(fullUrl, function (data) {
            saveToDisk(data, 'chat_'+chatLanguage+'.csv');
        })
    });
    function saveToDisk(data, filename) {
        var link = document.createElement('a');
        link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
        link.download = filename;
        var e = document.createEvent('MouseEvents');
        e.initEvent('click', true, true);
        link.dispatchEvent(e);
    }
});

function addDeleteButton($elems) {
    var deletionButton = '<button class="btn btn-sm btn-danger pull-right" onclick="deleteMessage.call(this, event)">' +
        '<span class="glyphicon glyphicon-remove"></span></button>';
    $($elems).prepend(deletionButton);
}

function deleteMessage(event) {
    var $chat_message = $(this).parent();


    var message = {
        id: parseInt($chat_message.find('.message_id').text()),
        userId: parseInt($chat_message.find('.user_id').text()),
        body: $chat_message.find('.message_body').text(),
        nickname: $chat_message.find('.nickname').text(),
        lang: $('#new_mess').find('input[name="lang"]').val()
    };
    $.ajax('/2a8fy7b07dxe44/chat/deleteMessage', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'POST',
        data: message,
        dataType: "text"
    }).done(function () {
        //
    }).fail(function(e){
        console.log(e)
    })
}

$('#chatLangButtons').on('historyLoaded', function () {
    addDeleteButton($('.chat_message'));
});

$('#chat').on('newMessage', 'div.chat_message', function () {
    addDeleteButton($(this));
});
