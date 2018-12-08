<%--&lt;%&ndash;--%>
  <%--User: Valk--%>
<%--&ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--&lt;%&ndash;&ndash;%&gt;--%>
<%--&lt;%&ndash;<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>&ndash;%&gt;--%>

<%--&lt;%&ndash;&ndash;%&gt;--%>
<%--<div id="right-sider" class="cols-md-2">--%>
    <%--&lt;%&ndash;CHAT TODO REMOVE TO SEPARATE jsp&ndash;%&gt;--%>
            <%--<div class="current-time">--%>
                <%--<span id="current-datetime"></span>--%>
            <%--</div>--%>

        <%--<div id="notifyBlock">--%>
            <%--<c:if test="${alwaysNotify2fa}">--%>
                <%--<h4 class="h4_green" style="color: red"><loc:message code="message.attention"/></h4>--%>
                <%--<hr class="under_h4">--%>
                <%--<p><loc:message code="message.2fa.text1"/></p>--%>
                <%--<button class="send_button accept_2fa"><loc:message code="message.2fa.aggree.toSettings"/></button>--%>

            <%--</c:if>--%>
        <%--</div>--%>
    <%--<div>--%>
        <%--<h4 class="h4_green"><loc:message code="dashboard.onlinechat"/></h4>--%>

        <%--<div class="chat-locales">--%>
            <%--<a href="javascript:void(0)" onclick="changeChatLocale('en')">EN</a>--%>
            <%--<a href="javascript:void(0)" onclick="changeChatLocale('ru')">RU</a>--%>
            <%--<a href="javascript:void(0)" onclick="changeChatLocale('cn')">CN</a>--%>
            <%--<a href="javascript:void(0)" onclick="changeChatLocale('ar')">AR</a>--%>
            <%--<a href="javascript:void(0)" onclick="changeChatLocale('in')">IN</a>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<hr class="under_h4">--%>
    <%--<div id="chat" class="chat">--%>
    <%--</div>--%>
    <%--<sec:authorize access="isAuthenticated()">--%>
        <%--<c:choose>--%>
            <%--<c:when test="${userStatus == 4}">--%>
                <%--<div class="text-center paddingtop10">--%>
                    <%--<span class="red"><loc:message code="dashboard.onlinechatbanned"/></span>--%>
                    <%--<br/>--%>
                    <%--<a href="<c:url value='/contacts'/>"><loc:message code="dashboard.contactsAndSupport" /> </a>--%>
                <%--</div>--%>

            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--<form id="new_mess" method="POST">--%>
                    <%--<input type="text" name="body" class="message_text"--%>
                           <%--placeholder='<loc:message code="dashboard.onlinechatenter"/>' autocomplete="off">--%>
                    <%--<input type="hidden" name="lang" value="EN"/>--%>
                    <%--<button class="send_button" type="submit"><loc:message code="dashboard.onlinechatsend"/></button>--%>

                <%--</form>--%>
            <%--</c:otherwise>--%>

        <%--</c:choose>--%>
    <%--</sec:authorize>--%>



    <%--&lt;%&ndash;NEWS LIST&ndash;%&gt;--%>
    <%--<div id="new-list-container" style="position: relative" class="clearfix">--%>
        <%--&lt;%&ndash;set hidden to switch indecator&ndash;%&gt;--%>
        <%--&lt;%&ndash;<img class="loading hidden" src="/client/img/loading-circle.gif" alt=""&ndash;%&gt;--%>
             <%--&lt;%&ndash;style='position: absolute;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;top: 0;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;bottom: 0;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;left: 0;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;right: 0;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;margin: auto;&ndash;%&gt;--%>
                    <%--&lt;%&ndash;z-index: 99999;'/>&ndash;%&gt;--%>
        <%--<%@include file="right-sider-news-list.jsp" %>--%>
    <%--</div>--%>

<%--</div>--%>
<%--<script src="<c:url value="/client/js/jquery.scrollTo.min.js"/>"></script>--%>
