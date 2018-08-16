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
