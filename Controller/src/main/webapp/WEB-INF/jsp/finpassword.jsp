<%--&lt;%&ndash;--%>
  <%--Created by IntelliJ IDEA.--%>
  <%--User: Valk--%>
  <%--Date: 05.04.16--%>
<%--&ndash;%&gt;--%>

<%--<script type="text/javascript" src="<c:url value='/client/js/submits/finPassCheck.js'/>"></script>--%>

<%--<div class="modal fade" id="finPassModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">--%>
    <%--<div class="modal-dialog" role="document">--%>
        <%--<div class="modal-content">--%>
            <%--<div class="modal-header">--%>
                <%--<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span--%>
                        <%--aria-hidden="true">&times;</span></button>--%>
                <%--<h4 class="modal-title" id="myModalLabel"><loc:message code="admin.finPassword"/></h4>--%>
            <%--</div>--%>
            <%--<div class="modal-body modal-content__input-block-wrapper">--%>
                <%--<div class="content modal-content__content-wrapper">--%>
                    <%--<c:url value="/checkfinpass" var="loginUrl"/>--%>
                    <%--<form id="submitFinPassForm" action="${loginUrl}" method="post" modelAttribute="user">--%>
                        <%--&lt;%&ndash;логин&ndash;%&gt;--%>
                        <%--<sec:authentication--%>
                                <%--property="principal.username" var="username"/>--%>
                        <%--<input type="text" readonly name="email" value="${username}"/>--%>
                        <%--&lt;%&ndash;пароль&ndash;%&gt;--%>
                        <%--<loc:message--%>
                                <%--code="admin.finPassword" var="finpassPlaceholder"/>--%>
                        <%--<input type="password" name="finpassword" placeholder="${finpassPlaceholder}"/>--%>
                        <%--&lt;%&ndash;csrf&ndash;%&gt;--%>
                        <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>--%>
                        <%--<br/>--%>
                        <%--&lt;%&ndash;отправить&ndash;%&gt;--%>
                        <%--<button onclick="finPassCheck()" type="button" data-dismiss="modal" class="button_enter">--%>
                            <%--<loc:message code="admin.submitfinpassword"/></button>--%>
                        <%--&lt;%&ndash;Забыли пароль?&ndash;%&gt;--%>
                        <%--<a style="display:none" class="button_forgot" href="/forgotPassword"><loc:message--%>
                                <%--code="dashboard.forgotPassword"/></a>--%>
                    <%--</form>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>