<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title><loc:message code="admin.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <%@include file='links_scripts.jsp' %>

    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminWalletsDataTable.js'/>"></script>
    <%----------%>

</head>

<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <%--<div class="col-sm-6 content">--%>
            <%--форма редактирование пользователя--%>
            <div class="col-md-5 col-md-offset-2 content admin-container">
                <div class="text-center">
                    <h4>
                        <b><loc:message code="admin.addUser"/></b>
                    </h4>
                </div>
                <div class="panel-body">
                    <form:form class="form-horizontal" id="user-add-form" cssClass="form_full_height_width"
                               action="/2a8fy7b07dxe44/adduser/submit" method="post" modelAttribute="user">
                        <div>
                            <fieldset class="field-user">

                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label for="user-name" class="input-block-wrapper__label"><loc:message
                                                code="admin.login"/></label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <form:input path="nickname" class="input-block-wrapper__input admin-form-input" id="user-name" required="required"/>
                                        <form:errors path="nickname" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label for="user-email" class="input-block-wrapper__label"><loc:message
                                                code="admin.email"/></label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <form:input path="email" class="input-block-wrapper__input admin-form-input" id="user-email" required="required" />
                                        <form:errors path="email" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label for="user-password" path="password"
                                               class="input-block-wrapper__label"><loc:message
                                                code="admin.password"/></label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <form:input path="password" type="password" class="input-block-wrapper__input admin-form-input" id="user-password" required="required"/>
                                        <form:errors path="password" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label for="user-phone" class="input-block-wrapper__label"><loc:message
                                                code="admin.phone"/></label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <form:input path="phone" class="input-block-wrapper__input admin-form-input" id="user-phone" />
                                        <form:errors path="phone" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>

                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label for="user-role" class="input-block-wrapper__label"><loc:message
                                                code="admin.role"/></label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <form:select path="role" id="user-role" class="input-block-wrapper__input admin-form-input" name="user-role">
                                            <c:forEach items="${roleList}" var="role">
                                                <option value="${role}">${role}</option>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </div>

                                <div class="admin-submit-group">
                                    <div>
                                        <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                        <button class="blue-box" type="submit">${saveSubmit}</button>

                                        <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                        <button class="blue-box" type="reset"
                                                onclick="javascript:window.location='/2a8fy7b07dxe44/administrators';">${cancelSubmit}</button>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </form:form>
                </div>
            </div>
    </div>
    <hr>
</main>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

