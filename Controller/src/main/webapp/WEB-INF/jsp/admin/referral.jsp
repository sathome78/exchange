<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 13:58
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
    <title><loc:message code="admin.referral"/></title>
    <%@include file='links_scripts.jsp' %>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-4 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.referral"/></h4></div>

            <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                <div id="panel4 row" class="tab-pane">
                    <div class="col-sm-6 text-center">
                        <h5>
                            <loc:message code="admin.referralLevels"/>
                        </h5>
                        <table class="table ref-lvl-table">
                            <thead>
                            <tr>
                                <th>Level</th>
                                <th>Percent</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${referralLevels}" var="level">
                                <tr class="table-row" data-percent="${level.percent}" data-id="${level.id}" data-level="${level.level}" data-toggle="modal" data-target="#refModal">
                                    <td>
                                            ${level.level}
                                    </td>
                                    <td id="_${level.level}">
                                        <span class="lvl-percent">${level.percent}</span>%
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-sm-6">
                        <h5>
                            <loc:message code="admin.refCommonRoot"/>
                            <c:choose>
                                <c:when test="${commonRefRoot != null}">
                                    <span data-id="${commonRefRoot.id}" id="ref-root-info">(${commonRefRoot.email})</span>
                                </c:when>
                                <c:otherwise>
                                    <span id="current-ref-root">(<loc:message code="admin.refAbsentCommonRoot"/>)</span>
                                </c:otherwise>
                            </c:choose>
                        </h5>
                        <form id="edit-cmn-ref-root">
                            <select class="admin-form-input" name="ref-root">
                                <c:forEach items="${admins}" var="admin">
                                    <option value="${admin.id}">${admin.email}</option>
                                </c:forEach>
                            </select>
                            <button class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                        </form>
                    </div>
                </div>
            </sec:authorize>

        </div>
    </div>
</main>

<div id="refModal" class="modal modal-small fade edit-ref-lvl-modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.referralLevelEdit"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-ref-lvl-form">
                    <input class="" type="hidden" name="level">
                    <input type="hidden" name="id">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.referralLevel"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="" class="input-block-wrapper__input lvl-id" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.referralPercent"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="percent" class="input-block-wrapper__input" type="text">
                        </div>
                    </div>
                    <button class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
