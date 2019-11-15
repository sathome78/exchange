<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 23.09.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.candleTable.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCandleDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 admin-container">
            <div class="text-center"><h4><loc:message code="admin.candleTable.title"/></h4></div>

            <div>
                <div class="col-md-10 col-md-offset-1 text-center" style="margin-top: 20px">
                    <form id="candleFilterForm" class="form_full_height_width">
                        <div class="col-md-4">
                            <select id="currencyPair" name="currencyPair"
                                    class="input-block-wrapper__input admin-form-input">
                                <c:forEach items="${currencyPairs}" var="currencyPair">
                                    <option value="${currencyPair.id}">${currencyPair.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                               <input id="datetimepicker_start" type="text" class="input-block-wrapper__input" name="startTime">
                        </div>
                        <div class="col-md-4">
                            <select id="interval" name="interval" class="input-block-wrapper__input admin-form-input">
                                <option value="12 HOUR">12 <loc:message code="chart.hours"/> </option>
                                <option selected value="24 HOUR">24 <loc:message code="chart.hours1"/> </option>
                                <option value="7 DAY">7 <loc:message code="chart.days"/> </option>
                                <option value="1 MONTH">1 <loc:message code="chart.month"/> </option>
                                <option value="6 MONTH">6 <loc:message code="chart.months"/> </option>
                            </select>
                        </div>
                    </form>
                    <hr/>

                    <table id="candle-table">
                        <thead>
                        <tr>
                            <th><loc:message code="admin.candleTable.startPeriod"/></th>
                            <th><loc:message code="dashboard.lowestPrice"/></th>
                            <th><loc:message code="dashboard.highestPrice"/></th>
                            <th><loc:message code="dashboard.priceStart"/></th>
                            <th><loc:message code="dashboard.priceEnd"/></th>
                            <th><loc:message code="dashboard.volume"/></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <div class="col-md-6 col-md-offset-3">


                </div>


            </div>
        </div>
</main>

<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
