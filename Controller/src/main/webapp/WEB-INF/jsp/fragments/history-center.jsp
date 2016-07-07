<%--
  User: Valk
--%>

<div id="myhistory" data-menuitemid="menu-myhistory" class="myhistory center-frame-container hidden">
    <%----%>
    <h4 class="h4_green"><loc:message code="history.title"/></h4>

    <div class="myhistory__button-wrapper">
        <button id="myhistory-button-orders" class="myhistory__button blue-box"><loc:message
                code="history.orders"/></button>
        <button id="myhistory-button-inputoutput" class="myhistory__button blue-box"><loc:message
                code="history.inputoutput"/></button>
        <button id="myhistory-button-referral" class="myhistory__button blue-box"><loc:message
                code="history.referral"/></button>
    </div>
    <%----%>
    <%@include file="myorders-center.jsp" %>
    <%@include file="inputoutput-center.jsp" %>
    <%@include file="myreferral-center.jsp" %>
</div>

