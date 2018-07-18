/**
 * Created by Valk on 06.06.2016.
 */

function MyHistoryClass(currentCurrencyPair, cpData) {
    if (MyHistoryClass.__instance) {
        return MyHistoryClass.__instance;
    } else if (this === window) {
        return new MyHistoryClass(currentCurrencyPair, cpData);
    }
    MyHistoryClass.__instance = this;
    /**/
    var that = this;
    var $myhistoryContainer = $('#myhistory');
    var $myHistoryActivePage;
    var myOrders;
    var $myordersContainer = $('#myorders');
    var inputOutput;
    var $inputOutputContainer = $('#myinputoutput');
    var myReferral;
    var $myreferralContainer = $('#myreferral');
    var myReferralStrucure;
    var $myreferralStrucuteContainer = $('#myRefStructure');
    /**/
    function showMyHistoryPage($myHistoryActivePage) {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        $myHistoryActivePage.siblings('.center-frame-container').addClass('hidden');
        $myHistoryActivePage.removeClass('hidden');
    }

    /**/
    this.updateAndShowAll = function () {
        showMyHistoryPage($myHistoryActivePage);
      /*  that.getAndShowMyOrdersPage();
        that.getAndShowInputOutputPage();
        that.getAndShowMyReferralPage();
        that.getAndShowMyReferralStrucurePage();*/
    };

    this.getAndShowMyOrdersPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        myOrders.syncCurrencyPairSelector();
        myOrders.updateAndShowAll();
    };

    this.getAndShowMyReferralPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        myReferral.syncCurrencyPairSelector();
        myReferral.updateAndShowAll();
    };

    this.getAndShowMyReferralStrucurePage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        myReferralStrucure.syncCurrencyPairSelector();
        myReferralStrucure.updateAndShowAll();
    };


    this.getAndShowInputOutputPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        inputOutput.syncCurrencyPairSelector();
        inputOutput.updateAndShowAll();
        /**/
    };

    this.updateActiveTab = function () {
        switch ($myHistoryActivePage) {
            case $inputOutputContainer : {
                that.getAndShowInputOutputPage();
                break;
            }
            case $myordersContainer : {
                that.getAndShowMyOrdersPage();
                break;
            }
            case $myreferralContainer : {
                that.getAndShowMyReferralPage();
                break;
            }
            case $myreferralStrucuteContainer : {
                that.getAndShowMyReferralStrucurePage();
                break;
            }
        }
    };

    /*=====================================================*/
    (function init (currentCurrencyPair, cpData) {
        myOrders = new MyOrdersClass(currentCurrencyPair, cpData);
        myReferral = new MyReferralClass();
        inputOutput = new InputOutputClass();
        myReferralStrucure = new RefStructureClass();
        /**/
        $('#myhistory-button-orders').addClass('active');
        /**/
        $('#myhistory-button-orders').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $myordersContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.getAndShowMyOrdersPage();
            /*that.updateAndShowAll();*/
        });
        $('#myhistory-button-inputoutput').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $inputOutputContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.getAndShowInputOutputPage();
            /*that.updateAndShowAll();*/
        });
        $('#myhistory-button-referral').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $myreferralContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.getAndShowMyReferralPage();
            /*that.updateAndShowAll();*/
        });
        $('#myhistory-button-referral_structure').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $myreferralStrucuteContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.getAndShowMyReferralStrucurePage();
            /*that.updateAndShowAll();*/
        });
        $myHistoryActivePage = $myordersContainer;
        showMyHistoryPage($myHistoryActivePage);
       /* that.updateAndShowAll();*/
    })(currentCurrencyPair, cpData);

}
