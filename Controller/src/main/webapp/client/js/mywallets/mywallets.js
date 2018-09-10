/**
 * Created by Valk on 04.06.2016.
 */

function MyWalletsClass() {
    if (MyWalletsClass.__instance) {
        return MyWalletsClass.__instance;
    } else if (this === window) {
        return new MyWalletsClass();
    }
    MyWalletsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForMyWalletsData;
    var refreshIntervalForMyWalletsData = 30000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $balanceContainer = $('#balance-page');
    /**/
    this.getAndShowMyWalletsData = function (refreshIfNeeded) {
        if ($balanceContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyWalletsData);
            timeOutIdForMyWalletsData = setTimeout(function () {
                that.getAndShowMyWalletsData(true);
            }, refreshIntervalForMyWalletsData);
            return;
        }
        if( $('#myInputTextField').val().length > 0 ) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyWalletsData');
        }

        var $balanceTable = $('#balance-grid').find('tbody');
        var url = '/dashboard/myWalletsData' + '?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    hideConfirmationDetailTooltip();
                    var $tmpl = $('#balance-table_row').html().replace(/@/g, '%');
                    clearTable($balanceTable);
                    data.forEach(function (e) {
                        $balanceTable.append(tmpl($tmpl, e));
                    });
                    blink($balanceTable.find('td:not(:first-child)'));
                    excludeZero();

                }
                clearTimeout(timeOutIdForMyWalletsData);
                timeOutIdForMyWalletsData = setTimeout(function () {
                    that.getAndShowMyWalletsData(true);
                }, refreshIntervalForMyWalletsData);
            }
        });
    };
    /*=====================================================*/
    (function init(cpData) {
        that.getAndShowMyWalletsData();
        $('#exclude-zero-mybalances').prop('checked', localStorage.checked == 'true');
        excludeZero();
        $('#balance-grid').on('mouseleave', function (e) {
            hideConfirmationDetailTooltip();
        });

        $('#balance-grid').on('click', '.mywallet-item-detail', function (e) {
            hideConfirmationDetailTooltip();
            var walletId = $(this).data('walletid');
            getMyWalletConfirmationDetail(walletId, $(this));
        });
        $('#exclude-zero-mybalances').click(function(e){
            excludeZero();
            if (e.target.checked) {
                localStorage.checked = true;
            } else {
                localStorage.checked = false;
            }
        });

        var getCellValue = function(tr, idx){ return tr.children[idx].innerText || tr.children[idx].textContent; }
        var comparer = function(idx, asc) { return function(a, b) { return function(v1, v2) {
            return v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2);
        }(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));
        }};

        Array.from(document.querySelectorAll('th')).forEach(function(th) { th.addEventListener('click', function() {
            var table = th.closest('table');
            Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
                .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
                .forEach(function(tr) { table.appendChild(tr) });
        })
        });
    })();

    function excludeZero() {
        var exclZeroes = $('#exclude-zero-mybalances').prop('checked');
        var input, filter, table, tr, td1, td2, td3, td4, i, activeBalance, reservedBalance;;
        input = document.getElementById("myInputTextField");
        filter = input.value.toUpperCase();
        table = document.getElementById("balance-grid");
        tr = table.getElementsByTagName("tr");

        for (i = 0; i < tr.length; i++) {
            td1 = tr[i].getElementsByTagName("td")[0];
            td2 = tr[i].getElementsByTagName("td")[1];
            td3 = tr[i].getElementsByTagName("td")[2];
            td4 = tr[i].getElementsByTagName("td")[4];
            if (td1 || td2 || td3 || td4) {
                activeBalance =  parseFloat(td3.innerText) || 0;
                reservedBalance =  parseFloat(td4.innerText) || 0;
                if ((td1.innerHTML.toUpperCase().indexOf(filter) > -1) || (td2.innerHTML.toUpperCase().indexOf(filter) > -1) ) {
                    tr[i].style.display = "";
                    if (exclZeroes && activeBalance === 0.0 && reservedBalance === 0.0) {
                        tr[i].style.display = "none";
                    }
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }

    function getMyWalletConfirmationDetail(walletId, $detailButton) {
        var url = '/dashboard/myWalletsConfirmationDetail?walletId=' + walletId;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                var $tooltip = getConfirmationDetailTooltip(data);
                $tooltip.css({
                    left: $detailButton.offset().left + parseInt($detailButton.css('width')),
                    top: $detailButton.offset().top + parseInt($detailButton.css('height'))
                });
                $('html').append($tooltip);
            }
        });
    }

    function getConfirmationDetailTooltip(data) {
        var html = '<div id="mywallet-detail-tooltip" class="mywallet-detail-tooltip"  style="position: absolute; z-index: 1">';
        data.forEach(function (e) {
            html += '<div>' + e.total + ' (' + e.stage + '/4)' + '</div>';
        });
        html += '</div>';
        return $(html)
            .on('click', function () {
                $(this).remove();
            });
    }

    function hideConfirmationDetailTooltip(){
        $('#mywallet-detail-tooltip').remove();
    }
}
function mySearchFunction() {
    var exclZeroes = $('#exclude-zero-mybalances').prop('checked');

    // Declare variables
    var input, filter, table, tr, td1, td2, td3, td4, i, activeBalance, reservedBalance;;
    input = document.getElementById("myInputTextField");
    filter = input.value.toUpperCase();
    table = document.getElementById("balance-grid");
    tr = table.getElementsByTagName("tr");

    // Loop through all table rows, and hide those who don't match the search query
    for (i = 0; i < tr.length; i++) {
        td1 = tr[i].getElementsByTagName("td")[0];
        td2 = tr[i].getElementsByTagName("td")[1];
        td3 = tr[i].getElementsByTagName("td")[2];
        td4 = tr[i].getElementsByTagName("td")[4];
        if (td1 || td2 || td3 || td4) {
            activeBalance =  parseFloat(td3.innerText) || 0;
            reservedBalance =  parseFloat(td4.innerText) || 0;
            if ((td1.innerHTML.toUpperCase().indexOf(filter) > -1) || (td2.innerHTML.toUpperCase().indexOf(filter) > -1) ) {
                tr[i].style.display = "";
                if (exclZeroes && activeBalance === 0.0 && reservedBalance === 0.0) {
                    tr[i].style.display = "none";
                }
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}
