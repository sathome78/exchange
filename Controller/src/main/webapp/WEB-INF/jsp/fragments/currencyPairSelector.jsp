<%--&lt;%&ndash;--%>
  <%--User: Valk--%>
<%--&ndash;%&gt;--%>

<%--<c:if test="${showAllPairsEnabled == true}">--%>
    <%--<i class="currency-pair-selector__menu-item all-pairs-item">--%>
        <%--<a href="#"><loc:message code="currency.allpairs"/></a>--%>
    <%--</i>--%>
<%--</c:if>--%>

<%--<script type="text/template" class="selectors_template">--%>

        <%--<@for(var i=0; i<keys.length ; i++){@>--%>
            <%--<div class="dropdown" style="display: inline">--%>
                <%--<button id="<@=keys[i]@>" class="currency-pair-selector__button  blue-box <@=keys[i]@>" type="button" data-toggle="dropdown"--%>
                        <%--aria-haspopup="true" aria-expanded="false" data-text="<@=data[i][0].marketName@>">--%>
                    <%--<@=keys[i]@><span class="caret"></span></button>--%>

                <%--<ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="<@=keys[i]@>">--%>

                    <%--<@for(var j=0; j<data[i].length; j++){@>--%>
                       <%--<li id="<@=data[i][j].name@>" class="currency-pair-selector__menu-item--%>
                       <%--<@=data[i][j].name===currentCurrencyPair ? 'active':''@>" data-market="<@=keys[i]@>">--%>
                            <%--<a href="#"><@=data[i][j].name@></a>--%>
                        <%--</li>--%>
                       <%--<@}@>--%>
                <%--</ul>--%>
            <%--</div>--%>
            <%--<@}@>--%>

<%--</script>--%>

<%--&lt;%&ndash;<script type="text/template" class="currency-pair-selector_row">--%>
    <%--<ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="dLabel">--%>
        <%--<c:if test="${showAllPairsEnabled == true}">--%>
            <%--<li class="currency-pair-selector__menu-item all-pairs-item">--%>
                <%--<a href="#">btc markets</a>--%>
            <%--</li>--%>
        <%--</c:if>--%>
        <%--<@for(var i=0; i--%>
        <%--<data.length--%>
                <%--; i++){@>--%>
            <%--<li class="currency-pair-selector__menu-item <@=data[i].name===currentCurrencyPair ? 'active':''@>">--%>
                <%--<a href="#"><@=data[i].name@></a>--%>
            <%--</li>--%>
            <%--<@}@>--%>
    <%--</ul>--%>
<%--</script>&ndash;%&gt;--%>

<%--&lt;%&ndash;<button id="dLabel1" class='currency-pair-selector__button blue-box' type="button" data-toggle="dropdown"--%>
        <%--aria-haspopup="true"--%>
        <%--aria-expanded="false">--%>
    <%--btc markets--%>
    <%--<span class="caret"></span>--%>
<%--</button>--%>

<%--<script type="text/template" class="currency-pair-selector_row">--%>
    <%--<ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="dLabel1">--%>
        <%--<c:if test="${showAllPairsEnabled == true}">--%>
            <%--<li class="currency-pair-selector__menu-item all-pairs-item">--%>
                <%--<a href="#">btc markets</a>--%>
            <%--</li>--%>
        <%--</c:if>--%>
        <%--<@for(var i=0; i--%>
        <%--<data.length--%>
                <%--; i++){@>--%>
            <%--<li class="currency-pair-selector__menu-item <@=data[i]===currentCurrencyPair ? 'active':''@>">--%>
                <%--<a href="#"><@=data[i]@></a>--%>
            <%--</li>--%>
            <%--<@}@>--%>
    <%--</ul>--%>
<%--</script>--%>

<%--<button id="dLabel2" class='blue-box' type="button" data-toggle="dropdown"--%>
        <%--aria-haspopup="true"--%>
        <%--aria-expanded="false">--%>
    <%--usd markets--%>
    <%--<span class="caret"></span>--%>
<%--</button>--%>

<%--<script type="text/template" class="currency-pair-selector_row">--%>
    <%--<ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="dLabel2">--%>
        <%--<c:if test="${showAllPairsEnabled == true}">--%>
            <%--<li class="currency-pair-selector__menu-item all-pairs-item">--%>
                <%--<a href="#">fiat markets</a>--%>
            <%--</li>--%>
        <%--</c:if>--%>
        <%--<@for(var i=0; i--%>
        <%--<data.length--%>
                <%--; i++){@>--%>
            <%--<li class="currency-pair-selector__menu-item <@=data[i]===currentCurrencyPair ? 'active':''@>">--%>
                <%--<a href="#"><@=data[i]@></a>--%>
            <%--</li>--%>
            <%--<@}@>--%>
    <%--</ul>--%>
<%--</script>--%>

<%--<button id="dLabel2" class='blue-box' type="button" data-toggle="dropdown"--%>
        <%--aria-haspopup="true"--%>
        <%--aria-expanded="false">--%>
    <%--fiat markets--%>
    <%--<span class="caret"></span>--%>
<%--</button>--%>

<%--<script type="text/template" class="currency-pair-selector_row">--%>
    <%--<ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="dLabel2">--%>
        <%--<c:if test="${showAllPairsEnabled == true}">--%>
            <%--<li class="currency-pair-selector__menu-item all-pairs-item">--%>
                <%--<a href="#">fiat markets</a>--%>
            <%--</li>--%>
        <%--</c:if>--%>
        <%--<@for(var i=0; i--%>
        <%--<data.length--%>
                <%--; i++){@>--%>
            <%--<li class="currency-pair-selector__menu-item <@=data[i]===currentCurrencyPair ? 'active':''@>">--%>
                <%--<a href="#"><@=data[i]@></a>--%>
            <%--</li>--%>
            <%--<@}@>--%>
    <%--</ul>--%>
<%--</script>&ndash;%&gt;--%>


