<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<div class="col-md-12 content">
<%--ref stat--%>
<div class="form-group" style="text-align: left">
    <H6><loc:message code="admin.referralAccruals"/></H6>
    <c:forEach var="item" items="${userRefBonuses}">
        <span>${item.amount} ${item.currencyName} </span><br>
    </c:forEach>
</div>
<br>
<%--search filters--%>
<div class="form-group filters" hidden>
    <form id="ref_download_form" class="form_auto_height" method="get">
        <br>
        <div class="input-block-wrapper">
            <div class="col-md-3 ">
                 <label class="input-block-wrapper__label">
                    <loc:message code="ordersearch.date"/>
                </label>
            </div>
            <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="ref_download_start" class="filter_input" type="text" name="dateFrom">
                <input id="ref_download_end" class="filter_input" type="text" name="dateTo">
            </div>
        </div>
        <br>
        <div class="input-block-wrapper">
            <div class="col-md-3 ">
                <label for="refSearch" class="input-block-wrapper__label">
                    <loc:message code="login.email"/>
                </label>
            </div>
            <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="refSearch" class="filter_input" type="text" name="email" placeholder='E-mail'>
            </div>
        </div>
        <br>
        <div class="input-block-wrapper">
            <div class="col-md-3 ">
                <label class="input-block-wrapper__label">
                    <loc:message code="admin.currencyLimits.name"/>
                </label>
            </div>
            <div class="col-md-9 input-block-wrapper__input-wrapper">
                <ul class="checkbox-grid">
                    <c:forEach items="${allCurrencies}" var="currency">
                        <li><input type="checkbox" class="currency_check" checked name="currencyIds" value="${currency.id}"><span>${currency.name}</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <br>

    </form>
    <button id="refSearchButton" class="blue-box"><loc:message code="currency.search"/></button>
</div>


<%--buttons group--%>
<div class="form-group" style="text-align: left">
    <button id="refExtFilter" class="blue-box"><loc:message code="admin.user.transactions.extendedFilter"/></button>
    <button id="refSearchClearButton" class="blue-box"><loc:message code="admin.reset"/></button>
    <button id="refDownloadButton" class="blue-box"><loc:message code="admin.user.transactions.downloadHistory"/></button>
</div>

<%--ref table--%>
<div hidden id="level-outer"><loc:message code="admin.referralLevel" />: <span id="level"></span></div>
<div id="ref" class="referral-table">
    <div class="table-wrp">
        <div class="table-body-wrp">
            <table class="table">
                <th class="user_name"><loc:message code="login.email"/></th>
                <th class="user-bonus"><loc:message code="btcWallet.history.amount"/></th>
                <th class="user-bonus"><loc:message code="admin.referrals"/></th>
            </table >
            <div class="wrap-rows reffil_${user.id}">
                <%--port for templates--%>
            </div>
        </div>
    </div>
</div>
<ul id="pagination-demo" class="pagination-sm"></ul>
</div>

<%--referrals template --%>
<script id="refTemplate" type="text/html">
    <div class="row_aff" id="reff_{%= refId %}" style="color:#333333; padding-left:10px;">
        <div class="reffil_">
            <div class="column-left">{%= email %}
                {%if firstRefLevelCount>0 %}
                    <span id="span_{%= id %}" class="fa-stack ref-Show" style="cursor:pointer; font-size: 10px;"  onclick="ShowHide({%= refId %})" >
                        <i class="fa fa-info fa-stack-1x"></i><i class="fa fa-circle-thin fa-stack-2x"></i>
                    </span>
                {%/if%}
            </div>
            <div class="ref_center column-center">
                {%if refProfitFromUser == 0  %}
                     0
                {%else%}
                {%each(i, dto) referralProfitDtoList %}
                      <span>{%= dto.amount %} {%= dto.currencyName %} </span><br>
                {%/each%}
                {%/if%}
            </div>
            <div class="ref_center column-right">
                <span class="badge badge-clear">{%= firstRefLevelCount %}</span>
            </div>
        </div>
        <div hidden="" class="reffil_{%= refId %}">
            <div id="test" class="list-view">
                <ul></ul>
            </div>
        </div>
    </div>
</script>
