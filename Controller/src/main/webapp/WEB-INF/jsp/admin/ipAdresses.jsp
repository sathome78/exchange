<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="deleteorder.title"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='links_scripts.jsp'%>
    <script type="text/javascript" src="<c:url value='/client/js/order/ipAdresses.js'/>"></script>
</head>

<body>
<%@include file='../fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="row">
            <div class="col-md-6 col-md-offset-2 content admin-container">
                <div class="text-center">
                    <h4 class="modal-title"><loc:message code="admin.IPAdresses"/></h4>
                </div>
                <button data-toggle="collapse" class="blue-box" style="margin: 10px 0;" data-target="#ip-search">
                    <loc:message code="admin.user.transactions.extendedFilter"/> </button>

                <div id="ip-search" class="collapse">

                    <form id="ip_search_form" class="form_full_height_width">

                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">IP</label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input name="ip"/>
                            </div>
                        </div>

                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message code="user.event"/></label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <select class="input-block-wrapper__input admin-form-input" name="event">
                                    <option value="">ANY</option>
                                    <c:forEach items="${events}" var="event">
                                        <option value="${event}">${event.name()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">EMAIL</label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input name="email"/>
                            </div>
                        </div>


                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ordersearch.date" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="datetimepicker_start" type="text" name="dateFrom">
                                <input id="datetimepicker_end" type="text" name="dateTo">
                            </div>
                        </div>

                        <div class="delete-order-info__button-wrapper">
                            <button id="ip_search" class="delete-order-info__button blue-box"
                                    type="button"><loc:message code="ordersearch.submit"/></button>
                            <button id="ip_reset" class="delete-order-info__button blue-box"
                                    type="button"><loc:message code="admin.user.transactions.resetFilter"/></button>
                        </div>

                    </form>

                </div>
                <div class="row">
                    <table id="ip-info-table">
                        <thead>
                        <tr>
                            <th><loc:message code="orderinfo.id"/></th>
                            <th><loc:message code="login.email"/></th>
                            <th>IP</th>
                            <th><loc:message code="user.event"/></th>
                            <th><loc:message code="admin.dateTime"/></th>
                            <th>URL</th>
                        </tr>
                        </thead>
                    </table>
                </div>

            </div>
        </div>

    </div>
</main>




<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

