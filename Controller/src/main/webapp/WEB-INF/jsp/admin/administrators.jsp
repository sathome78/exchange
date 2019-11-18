<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 13:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <title><loc:message code="admin.admins"/></title>
    <%@include file='links_scripts.jsp' %>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>

        <sec:authorize access="<%=AdminController.adminAnyAuthority%>">

            <div id="panel2" class="col-md-8 col-md-offset-1 admin-container">
                <div class="col-md-12 text-center">
                <h4>
                    <b><loc:message code="admin.listOfAdmins"/></b>
                </h4>
                </div>

                <div class="admin-add-functions-container clearfix">
                    <div id="admin-add-functions">
                        <button onclick="javascript:window.location.href='/2a8fy7b07dxe44/addUser';"
                                class="admin-add-functions__item blue-box"><loc:message code="admin.addUser"/></button>

                    </div>
                </div>
                <table id="adminsTable" class="admin-table table table-hover table-bordered table-striped">
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
        </sec:authorize>
    </div>
</main>




<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
