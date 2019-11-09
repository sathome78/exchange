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
    <title><loc:message code="admin.users"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/2faSettings.js'/>"></script>
</head>

<body id="main-admin">

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>

        <div class="col-sm-9 content">

            <%--контейнер форм ролей пользователей--%>
            <div class=" col-md-8 col-md-offset-2 tab-content admin-container">
                <%--форма Пользователи--%>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <div id="panel1" class="tab-pane active">
                        <div class="text-center">
                        <h4>
                            <b><loc:message code="admin.listOfUsers"/></b>
                        </h4>
                        </div>
                        <table id="usersTable" class="admin-table table table-hover table-bordered table-striped"
                               style="width: 100%;">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.user"/></th>
                                <th><loc:message code="admin.email"/></th>
                                <th><loc:message code="admin.registrationDate"/></th>
                                <th><loc:message code="admin.role"/></th>
                                <th><loc:message code="admin.status"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <br>
                    <div id="post_url" hidden>${post_url}</div>
                    <section id="2fa-options">
                        <h4 class="h4_green">
                            <loc:message code="message.2fa.title"/>
                        </h4>
                        <h4 class="under_h4_margin"></h4>
                        <div class="container">
                            <div class="row">
                                <div class="col-sm-6 content">
                                    <form method="post" id="2faSettings_form">
                                        <input type="hidden" name="_csrf" value="62217712-a688-4689-9f52-e8f47b288e0b">
                                        <label for="login2fa"><loc:message code="admin.2falogin.allSite"/></label>
                                        <input type="checkbox" id="login2fa" name="enable_2fa"
                                               <c:if test="${enable_2fa}">checked</c:if>>
                                        <div id="result" hidden="" style="display: block;"></div>
                                        <button id="submitSessionOptionsButton" type="submit" class="blue-box">
                                            <loc:message code="button.update"/></button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </section>
                </sec:authorize>

            </div>
        </div>
    </div>
    <hr>

</main>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

