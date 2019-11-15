<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 30.08.2016
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title><loc:message code="admin.sessionControl"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminSessionDataTable.js'/>"></script>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 content admin-container">
            <div class="text-center">
                <button class="btn btn-lg btn-default pull-right" style="margin-bottom: 10px" onclick="refreshTable()">
                    <span class="glyphicon glyphicon-refresh"></span>
                </button>
                <h4><loc:message code="admin.sessionControl"/></h4>
            </div>
            <br/>
                     <table id="user_sessions">
                        <thead>
                        <tr>
                            <th><loc:message code="admin.user"/></th>
                            <th><loc:message code="admin.email"/></th>
                            <th><loc:message code="admin.role"/></th>
                            <th><loc:message code="admin.sessionControl.sessionId"/></th>
                            <th><loc:message code="admin.sessionControl.action"/></th>
                        </tr>
                        </thead>
                    </table>
        </div>
    </div>
    <hr/>
</main>
<div id="expire-session-modal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.sessionControl.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <div class="text-center">
                    <h5><loc:message code="admin.sessionControl.prompt"/></h5>
                </div>
                <div>
                    <strong><loc:message code="admin.user"/></strong><span style="margin-left: 10px" id="session-owner"></span>
                </div>
                <div>
                    <strong><loc:message code="admin.sessionControl.sessionId"/></strong><span style="margin-left: 10px" id="session-id"></span>
                </div>
            </div>
            <div class="modal-footer">
                <div>
                    <button id="expire-session-submit" >
                        <loc:message code="admin.sessionControl.submit"/></button>
                    <button data-dismiss="modal">
                        <loc:message code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

