<%--
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%----%>
<%--<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>--%>
<link href="<c:url value='/client/css/chat-lang.css'/>" rel="stylesheet">
<%----%>
<div id="right-sider" class="cols-md-2">
    <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
    <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
    <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
    <c:set var="admin_finOperatorEnum" value="<%=me.exrates.model.enums.UserRole.FIN_OPERATOR%>"/>
    <%--CHAT TODO REMOVE TO SEPARATE jsp--%>
            <div class="current-time">
                <span id="current-datetime"></span>
            </div>

        <div id="notifyBlock">
            <c:if test="${alwaysNotify2fa}">
                <h4 class="h4_green" style="color: red"><loc:message code="message.attention"/></h4>
                <hr class="under_h4">
                <p><loc:message code="message.2fa.text1"/></p>
                <button class="send_button accept_2fa"><loc:message code="message.2fa.aggree.toSettings"/></button>

            </c:if>
        </div>
    <div>
        <h4 class="h4_green"><loc:message code="dashboard.onlinechat"/></h4>

        <div id="chatLangButtons" class="chat-locales">
            <button id="bchaten" class="btna" onclick="changeChatLocale('en')">EN</button>
            <button id="bchatru" class="btna" onclick="changeChatLocale('ru')">RU</button>
            <button id="bchatcn" class="btna" onclick="changeChatLocale('cn')">CN</button>
            <button id="bchatin" class="btna" onclick="changeChatLocale('in')">IN</button>
        </div>
    </div>

    <hr class="under_h4">
    <div id="chat" class="chat">
    </div>
    <sec:authorize access="isAuthenticated()">
        <c:choose>
            <c:when test="${userStatus == 4}">
                <div class="text-center paddingtop10">
                    <span class="red"><loc:message code="dashboard.onlinechatbanned"/></span>
                    <br/>
                    <a href="<c:url value='/contacts'/>"><loc:message code="dashboard.contactsAndSupport" /> </a>
                </div>

            </c:when>
            <c:otherwise>
                <%@include file="modal/errorInfoSendMessageToChatWithoutNickname_modal.jsp" %>

                <form id="new_mess" method="POST">
                    <input type="text" name="body" class="message_text"
                           placeholder='<loc:message code="dashboard.onlinechatenter"/>' autocomplete="off">
                    <input type="hidden" name="lang" value="EN"/>
                    <button class="send_button" type="submit"><loc:message code="dashboard.onlinechatsend"/></button>

                </form>
            </c:otherwise>

        </c:choose>
    </sec:authorize>
    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
        <div class="row">
            <button id="download-chats" class="send_button">
                <loc:message code="admin.user.transactions.downloadHistory"/>
            </button>
        </div>
    </sec:authorize>

    <%--NEWS LIST--%>
    <div id="new-list-container" style="position: relative" class="clearfix">
        <%--set hidden to switch indecator--%>
        <img class="loading hidden" src="/client/img/loading-circle.gif" alt=""
             style='position: absolute;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    margin: auto;
                    z-index: 99999;'/>
        <%@include file="right-sider-news-list.jsp" %>
    </div>

</div>
<script src="<c:url value="/client/js/jquery.scrollTo.min.js"/>"></script>
<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
    <script src="<c:url value="/client/js/admin/admin.chat.js"/>"></script>
</sec:authorize>
