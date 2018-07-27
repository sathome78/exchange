/**
 * Created by Valk on 05.06.2016.
 */

function RightSiderClass() {
    var currentTime;

    if (RightSiderClass.__instance) {
        return RightSiderClass.__instance;
    } else if (this === window) {
        return new RightSiderClass(currentCurrencyPair);
    }
    RightSiderClass.__instance = this;
    /**/
    var that = this;
    var rightSiderId = "right-sider";
    /**/
    this.newsList = null;
    var $newsLoadingImg = $('#new-list-container').find('.loading');
    if ($newsLoadingImg.length == 0 || $newsLoadingImg.hasClass('hidden')) {
        $newsLoadingImg = null;
    }
    /*===========================================================*/
    (function init() {
        that.newsList = new NewsClass($newsLoadingImg);
        $.get('/utcOffset', function (data) {
            currentTime = moment().utcOffset(data);

                    setInterval(function () {
                $('#current-datetime').text(currentTime.format('YYYY-MM-DD HH:mm:ss'));
                currentTime.add(1, 's');
            }, 1000)
        });
    })();




}
