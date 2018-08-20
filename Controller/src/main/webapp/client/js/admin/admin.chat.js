/**
 * Created by Asset on 12.07.2018.
 */

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