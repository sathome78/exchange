<%--
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%----%>
<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>

<%----%>
<div id="right-sider" class="cols-md-2">
    <%--CHAT TODO REMOVE TO SEPARATE jsp--%>

    <div>
        <h4 class="h4_green"><loc:message code="dashboard.onlinechat"/></h4>

        <div class="chat-locales">
            <a href="javascript:void(0)" onclick="changeChatLocale('en')">EN</a>
            <a href="javascript:void(0)" onclick="changeChatLocale('ru')">RU</a>
            <a href="javascript:void(0)" onclick="changeChatLocale('cn')">CN</a>
        </div>
    </div>

    <hr class="under_h4">
    <div id="chat" class="chat">
    </div>
    <sec:authorize access="isAuthenticated()">
        <form id="new_mess" method="POST">
            <input type="text" name="body" class="message_text"
                   placeholder='<loc:message code="dashboard.onlinechatenter"/>'>
            <input type="hidden" name="lang" value="EN"/>
            <button class="send_button" type="submit"><loc:message code="dashboard.onlinechatsend"/></button>

        </form>
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
