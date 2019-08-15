

function ChartAmchartsClass2(currencyPair) {

    if (ChartAmchartsClass2.__instance) {
        return ChartAmchartsClass2.__instance;
    } else if (this === window) {
        return new ChartAmchartsClass2();
    }
    ChartAmchartsClass2.__instance = this;

    var that = this;

    this.chartType = null;
    this.currencyPair = currencyPair;

    var isChartReady = false;

    var timeFrames;

    var datafeed;

    var stockChart;

    this.isReady = function () {
        return isChartReady;
    };


    this.switchCurrencyPair = function (pairName) {
        /*var currencyPairName = $('.currency-pair-selector__menu-item.active').prop('id');*/

        stockChart.setSymbol(pairName, function () {

            stockChart.setSymbol(pairName, function () {

                stockChart.resetData()
            })
        })
    };


    function initChartWidget(currencyPair) {
        var host = location.protocol + '//' + location.host + '/dashboard';
        datafeed = new Datafeeds.UDFCompatibleDatafeed(host, 20000);
        var lang = $("#language").text().toLowerCase().trim();

        var widget = window.tvWidget = new TradingView.widget({
            // debug: true, // uncomment this line to see Library errors and warnings in the console
            allow_symbol_change: true,
            autosize: true,
            symbol: currencyPair,
            timezone: 'UTC',
            interval: '30',
            container_id: "amcharts-stock_chart_div",
            //	BEWARE: no trailing slash is expected in feed URL
            datafeed: datafeed,
            library_path: "/client/js/lib/charting_library/",
            locale: lang || "en" ,
            //	Regression Trend-related functionality is not implemented yet, so it's hidden for a while
            disabled_features: ["header_symbol_search", "cl_feed_return_all_data"],
            charts_storage_api_version: "1.2",
            time_frames: [
                {text: "8m", resolution: "D"},
                {text: "2m", resolution: "D"},
                {text: "7d", resolution: "60"},
                {text: "5d", resolution: "30"},
                {text: "3d", resolution: "30"}
            ]

        });

        widget.onChartReady(function () {
            stockChart = widget.activeChart();
        });

    }

    /*==========================================*/

    (function init() {
        $.get('/stockChart/timeFrames', function (data) {
            timeFrames = data;
            TradingView.onready(function () {
            })
            initChartWidget(currencyPair);
        })
    })();

}