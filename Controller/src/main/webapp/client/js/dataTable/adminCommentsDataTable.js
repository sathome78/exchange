var commentsDataTable;

$(function () {
    $('#commentsTable').hide();
    $('#comments-table-init').click(update);

    function update() {
        $('#checkMessage').hide();
        if ($.fn.dataTable.isDataTable('#commentsTable')) {
            commentsDataTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            commentsDataTable = $('#commentsTable').DataTable({
                "ajax": {
                    "url": '/2a8fy7b07dxe44/comments?id=' + id,
                    "dataSrc": ""
                },
                "paging": true,
                "info": true,

                "columns": [

                    {
                        "data": "creationTime"
                    },
                    {
                        "data": "creator.email"
                    },
                    {
                        "data": "comment",
                        "render": function (data, type, row){
                            var text = data.replace(/(<([^>]+)>)/ig,"");
                            if (text.length > commentLengthNoShorter || text.split('\n').length > 2) {
                                var shortData = text.substr(0, commentLengthNoShorter) + " ... ";
                                if(text.split('\n').length > 2){
                                    shortData = shortData.substring(0, shortData.indexOf("\n", shortData.indexOf("\n",  shortData.indexOf("\n")+1))) + " ... ";
                                }
                                return '<textarea id="textOfComment'+row.id+'"style="width: 100%; height: 100%; resize:none; overflow: auto; text-align: justify;" readonly>'+ shortData +'</textarea>'
                                    +'<button id="buttonShowFullComment'+row.id+'"onclick="showFullComment(this)" style="float: right;">Expand</button>'
                                    +'<button id="buttonHideFullComment'+row.id+'"onclick="hideFullComment(this)" style="float: right;" hidden>Hide</button>';
                            }
                            else{
                                return '<textarea id="textOfComment'+row.id+'"style="width: 100%; height: 100%; resize: none; overflow: hidden; text-align: justify;" readonly>'+ text +'</textarea>'
                            }
                            return "";
                        }
                    },
                    {
                        "data": "messageSent",
                        "render": function (data, type, row){
                            if (data) {
                                return '<input type="image" src="/client/img/email.png" style="width: 20px; height: 20px"/> ';
                            } else if (row.editable) {
                                return '<input type="image" src="/client/img/edit_icon_32.png" onclick="editUserComment(this)" style="width: 20px; height: 20px"/>' ;
                            }
                            return "";
                        }
                    },
                    {
                        "data": "id"
                        ,
                        "visible": false
                    }

                ],
                "order": [
                    [
                        0,
                        "desc"
                    ]
                ]
            });
        }
        $('#commentsTable').show();
    }

    $('#comments-button').on('click', function () {
        $("#commentText").val("");

        preparingToStartModelWindowWithComment();
        $("#myModal").modal();
    });

    function sendAddComment(newComment, email, sendMessage) {
        $.ajax({
            url: '/2a8fy7b07dxe44/addComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
                update();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }

    function sendEditComment(commentId, newComment, email, sendMessage) {
        $.ajax({
            url: '/2a8fy7b07dxe44/editUserComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "commentId": commentId,
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
                $('#commentId').val("");
                update();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }

    $('#createCommentConfirm').on('click', function () {
        var commentId = $('#commentId').val();
        var newComment = document.getElementById("commentText").value;
        var email = $("input[name='email']").val();
        var sendMessage = document.getElementById("sendMessageCheckbox").checked;

        if (sendMessage){
            if (!confirm($('#prompt_send_message_rqst').html() + " " + email + "?")) {
                return;
            }
        }

        if (commentId == null || commentId =="") {
            sendAddComment(newComment, email, sendMessage);
        } else {
            sendEditComment(commentId, newComment, email, sendMessage)
        }
        $("#myModal").modal('hide');
    });

    $('#createCommentCancel').on('click', function () {
        document.getElementById("commentText").value = "";
    });

    $('#sendMessageCheckbox').on('click', function () {
        document.getElementById("sendMessageCheckbox").checked ? $('#checkMessage').show() : $('#checkMessage').hide();
    });

});

/**
 * Method for calculate content height of textarea (comment).
 * @param ta
 * @param scanAmount
 * @returns {number | *}
 */
var calculateContentHeight = function(ta, scanAmount) {
    var origHeight = ta.style.height,
        height = ta.offsetHeight,
        scrollHeight = ta.scrollHeight,
        overflow = ta.style.overflow;
    // only bother if the ta is bigger than content
    if ( height >= scrollHeight ) {
        // check that our browser supports changing dimension
        // calculations mid-way through a function call...
        ta.style.height = (height + scanAmount) + 'px';
        // because the scrollbar can cause calculation problems
        ta.style.overflow = 'hidden';
        // by checking that scrollHeight has updated
        if ( scrollHeight < ta.scrollHeight ) {
            // now try and scan the ta's height downwards
            // until scrollHeight becomes larger than height
            while (ta.offsetHeight >= ta.scrollHeight) {
                ta.style.height = (height -= scanAmount)+'px';
            }
            // be more specific to get the exact height
            while (ta.offsetHeight < ta.scrollHeight) {
                ta.style.height = (height++)+'px';
            }
            // reset the ta back to it's original height
            ta.style.height = origHeight;
            // put the overflow back
            ta.style.overflow = overflow;
            return height;
        }
    } else {
        return scrollHeight;
    }
};

/**
 * Method for calculate and set height for comments textarea. Uses in 'showFullComment' action.
 * @param commentId
 */
function calculateAndSetHeightForCommentTextArea(commentId) {
    var ta = document.getElementById("textOfComment"+commentId);
    var style = (window.getComputedStyle) ? window.getComputedStyle(ta) : ta.currentStyle;

    // This will get the line-height only if it is set in the css, otherwise it's "normal"
    var taLineHeight = parseInt(style.lineHeight, 8);

    // Get the scroll height of the textarea
    var taHeight = calculateContentHeight(ta, taLineHeight);

    // calculate the number of lines
    var numberOfLines = Math.ceil(taHeight / taLineHeight);

    var heightTextAreaFullComment = numberOfLines*taLineHeight+"px";
    $("#textOfComment"+commentId).css("height", heightTextAreaFullComment);
};

/**
 * Long comments will be shortened to this length for correct display.
 * @type {number}
 */
var commentLengthNoShorter = 60;

/**
 * Method for button 'Expand' (for long comments by user). Show full comment.
 * @param elem
 */
function showFullComment(elem) {
    var row = $(elem).parents('tr');
    var rowData = commentsDataTable.row(row).data();
    var textArea = "#textOfComment"+rowData.id;
    var buttonShowFullComment = "#buttonShowFullComment"+rowData.id;
    var buttonHideFullComment = "#buttonHideFullComment"+rowData.id;

    $(buttonShowFullComment).prop("hidden", true);
    $(buttonHideFullComment).prop("hidden", false);

    $(textArea).css("resize", "vertical");
    $(textArea).html(rowData.comment);

    calculateAndSetHeightForCommentTextArea(rowData.id);
}

/**
 * Method for button 'Hide' in admin panel (for long comments by user). Hide full comment (make substring of comment to {commentLengthNoShorter}).
 * @param elem
 */
function hideFullComment(elem) {
    var row = $(elem).parents('tr');
    var rowData = commentsDataTable.row(row).data();
    var textArea = "#textOfComment"+rowData.id;
    var buttonShowFullComment = "#buttonShowFullComment"+rowData.id;
    var buttonHideFullComment = "#buttonHideFullComment"+rowData.id;

    $(buttonShowFullComment).prop("hidden", false);
    $(buttonHideFullComment).prop("hidden", true);

    $(textArea).css("resize", "none");

    $(textArea).html(rowData.comment.substring(0,commentLengthNoShorter)+" ... ");
    if(rowData.comment.split('\n').length > 2){
        $(textArea).html(rowData.comment.substring(0, rowData.comment.indexOf("\n", rowData.comment.indexOf("\n",  rowData.comment.indexOf("\n")+1)))+" ... ");
    }

    $(textArea).css("height", "100%");
}

function editUserComment(elem) {
    var row = $(elem).parents('tr');
    var rowData = commentsDataTable.row(row).data();
    console.log(rowData.comment);
    $("#commentId").val(rowData.id);
    $("#commentText").val(rowData.comment);

    preparingToStartModelWindowWithCommentForEditing();
    $("#myModal").modal();

}

function deleteUserComment(e) {

    if (confirm($('#prompt_delete_user_comment_rqst').html())) {
        var element = $(this);
        var row = $(this).closest('tr');
        var data = $('#commentsTable').dataTable().fnGetData(row);

        $.ajax({
            url: '/2a8fy7b07dxe44/deleteUserComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "commentId": data.id
            },
            success: function (data) {
                // update();
                commentsDataTable.ajax.reload();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }
}

/**
* Max count of symbols in comment on user for admin
 */
var maxCountOfSymbols = 400;

/**
* Preparing to start a modal window with a comment
*/
function preparingToStartModelWindowWithComment(){
    $("#checkLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").prop('maxlength', maxCountOfSymbols);

    $("#sendMessageCheckbox").prop('checked', false);
    $('#checkMessage').hide();

    $("#createCommentConfirm").prop('disabled', true);
}

/**
* Preparing to start a modal window with a comment.
* For editing comment.
*/
function preparingToStartModelWindowWithCommentForEditing(){
    var presentValueOfLengthComment = $("#commentText").val().length;
    var remainingCharactersCurrentValue = maxCountOfSymbols - presentValueOfLengthComment;

    $("#checkLengthComment").html(remainingCharactersCurrentValue);
    $("#checkMaxLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").prop('maxlength', maxCountOfSymbols);

    $("#sendMessageCheckbox").prop('checked', false);
    $('#checkMessage').hide();

    $("#createCommentConfirm").prop('disabled', true);
}

/**
* The method for working with creating comments (adding a counter, the maximum length of comments)
* Added a restriction that the comment can not be empty or only with spaces.
*/
$(function(){
    $('#commentText').bind('input', function(){
        var commentText = this.value.length;
        var pattern = /^[\s]+$/;

        //Block button 'confirm' when textarea contains only spaces or length of comment = 0.
        commentText == 0 || pattern.test(this.value) ? $("#createCommentConfirm").prop('disabled', true) : $("#createCommentConfirm").prop('disabled', false);

        if (commentText > maxCountOfSymbols) {
            this.value = this.value.substr(0, maxCountOfSymbols);
        }
        var counter = (maxCountOfSymbols - commentText);

        counter <= 0 ? $("#checkLengthComment").html('0') : $("#checkLengthComment").html(counter);
    });
});
