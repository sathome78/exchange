
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.commissions"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminNodesTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">

            <div class="text-center"><h4><loc:message code="admin.nodesStateControl"/></h4></div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6">
                        <table id="nodes-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.nodeName"/></th>
                                <th><loc:message code="admin.nodeIsWork"/></th>
                                <th><loc:message code="admin.nodeIsWorkCorrect"/></th>
                                <th><loc:message code="admin.lastPollingTime "/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

            </div>



        </div>
</main>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
