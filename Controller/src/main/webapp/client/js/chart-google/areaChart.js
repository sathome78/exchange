/*$(function () {
    google.charts.setOnLoadCallback(drawChart);
});*/

function ChartAreaClass() {
    var that = this;

    var areaChartAreaHeight = 130+60+15; /*candleChartAreaHeight + barChartAreaHeight + .candle-description__item.height*/

    this.draw = function () {
        var url = '/dashboard/chartArray/area';
        waiterSwitch(true);
        $.get(url, function (arrayResult) {
            var backDealInterval = arrayResult[0][0]; //BackDealInterval is here
            setActivePeriodSwitcherButton(backDealInterval);
            /**/
            var Combined = new Array();
            for (var i = 1; i < arrayResult.length; i++) {
                /*skip first row - BackDealInterval is there*/
                Combined[i - 1] = [arrayResult[i][0], arrayResult[i][1],
                    /*returns html for tip*/
                    getAreaHtml(
                        /*data for tip:*/
                        arrayResult[i][0], arrayResult[i][1], arrayResult[i][2],
                        /*titles for tip data:*/
                        arrayResult[i][3], arrayResult[i][4], arrayResult[i][5]
                    )];
            }
            if (Combined.length == 0) {
                Combined[0] = ['', 0, getAreaHtml()];
            }

            options = {
                height: areaChartAreaHeight,
                chartArea: {
                    left: 50,
                    right: 30,
                    top: 10,
                    bottom: 20,
                    backgroundColor: {
                        fillOpacity: 0.25,
                        fill: '#FFF'
                    }
                },
                backgroundColor: {
                    fillOpacity: 0,
                    fill: '#FFF'
                },
                vAxis: {
                    gridlines: {},
                    baselineColor: '#525F74',
                    textStyle: {
                        color: '#525F74',
                        fontSize: 10
                    },
                    format: '##0.###'
                },
                hAxis: {
                    textStyle: {
                        color: '#525F74',
                        opacity: 0,
                        fontSize: 10
                    },
                    gridlines: {
                        color: 'transparent'
                    },
                    baselineColor: '#525F74'
                },
                tooltip: {isHtml: true},
                crosshair: {trigger: 'both'},
                series: {0: {type: "area", color: '#525F74'}},
                legend: "none"
            };
            try {
                if (!google.visualization) {
                    return;
                }
                var chart = new google.visualization.ComboChart(document.getElementById("area-chart_div"));
                var dataTable = new google.visualization.DataTable();
                dataTable.addColumn('string', '');
                dataTable.addColumn('number', '');
                dataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});
                dataTable.addRows(Combined);
                chart.draw(dataTable, options);
            } catch (e) {
                console.log(e);
            }
            waiterSwitch(false);
        });
    }

    function getAreaHtml(date, rate, value, dateTitle, rateTitle, valueTitle) {
        var html;
        if (!date) {
            html = '<div class="area-chart-tip">' +
            '<p>' + '<span>' + $('#noData').text() + '</span>' + '</p>' +
            '</div>';
        } else {
            html = '<div class="area-chart-tip">' +
            '<p class="areaChartTip__date">' + '<span>' + dateTitle + '</span>' + ': ' + date + '</p>' +
            '<p class="areaChartTip__rate">' + '<span>' + rateTitle + '</span>' + ': ' + rate + '</p>' +
            '<p class="areaChartTip__rate">' + '<span>' + valueTitle + '</span>' + ': ' + value + '</p>' +
            '</div>';
        }
        return html;
    }


    function waiterSwitch(state) {
        $('#graphic-wait').toggle(state);
    }

    function setActivePeriodSwitcherButton(backDealInterval) {
        var id = backDealInterval.intervalValue + backDealInterval.intervalType.toLowerCase();
        $('.period-menu__item').removeClass('active');
        $('#' + id).addClass('active');
    }
}