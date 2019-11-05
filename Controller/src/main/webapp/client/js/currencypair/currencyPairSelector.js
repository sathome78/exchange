/**
 * Created by Valk on 06.06.2016.
 */

function CurrencyPairSelectorClass(currencyPairSelectorId, currentCurrencyPair, cpData) {
    var that = this;
    this.$currencyPairSelector = $('#' + currencyPairSelectorId);
    this.currentCurrencyPair = currentCurrencyPair;

    var previousValue;

    this.init = function (onChangeHandler, pairType) {
        that.$currencyPairSelector.on('click', '.currency-pair-selector__menu-item', function (e) {
            e.preventDefault();
            var $item = $(this);
            var showAllPairs;
            if (that.$currencyPairSelector.find('.currency-pair-selector__menu-item').hasClass('all-pairs-item')) {
                showAllPairs = $item.hasClass('all-pairs-item');
            } else {
                showAllPairs = null;
            }
            trading.clearOrdersCreationForm();
            trading.resetOrdersListForAccept();
            var newCurrentCurrencyPairName = showAllPairs ? null : $(this).text().trim();
            syncCurrentParams(newCurrentCurrencyPairName, showAllPairs, null, pairType, function (data) {
                $('.currency-pair-selector__menu-item.active').each(function() {
                    $(this).removeClass('active');
                });
                $item.addClass('active');
                that.currentCurrencyPair = $item.text().trim();
                setButtonTitle();
                onChangeHandler(data.currencyPair);
            });
        });
        that.getAndShowCurrencySelector();
    };

    this.syncState = function (pairType, onChangeHandler) {
        syncCurrentParams(null, null, null, pairType, function (data) {
            var $item;
            if (data.showAllPairs && that.$currencyPairSelector.find('.currency-pair-selector__menu-item').hasClass('all-pairs-item')) {
                $item = that.$currencyPairSelector.find('.currency-pair-selector__menu-item.all-pairs-item');
            } else {
                $item = that.$currencyPairSelector.find('.currency-pair-selector__menu-item:contains(' + data.currencyPair.name + ')');
            }
            $('.currency-pair-selector__menu-item.active').each(function() {
                $(this).removeClass('active');
            });
            $item.addClass('active');
            that.currentCurrencyPair = $item.text();
            setButtonTitle();
            onChangeHandler(data.currencyPair);
        });
    };

    this.getAndShowCurrencySelector = function () {
        var $currencyList = that.$currencyPairSelector;
        $currencyList.find('.currency-pair-selector__menu').remove();
        var $template = $('.selectors_template');
        var $tmpl1 = $template.html().replace(/@/g, '%');
        $currencyList.append(tmpl($tmpl1, {
                keys: Object.keys(cpData),
                data: Object.values(cpData),
                currentCurrencyPair: that.currentCurrencyPair
        }));
        setButtonTitle();
    };

    function setButtonTitle() {
        if(that.currentCurrencyPair == undefined) {
            return previousValue == undefined;
        }
        $('.currency-pair-selector__button').each(function() {
            $(this).text($(this).data('text'));
            $(this).append('<span class="caret"></span>');
        });
        var $li = $(document.getElementById(that.currentCurrencyPair.trim()));
        var market = $li.data("market");
        var newText = $li.text().trim();
        $('.' + market).each(function() {
            $(this).text(this.id);
            $(this).append('<span class="caret"></span>');
        });
        $('.' + market).text(newText).append('<span class="caret"></span>');
        var res = previousValue == newText;
        previousValue = $('.currency-pair-selector__menu-item.active').attr('id');

        return res;
    }
}
