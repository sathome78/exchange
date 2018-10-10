<section id="nickname-changing">
    <h4 class="h4_green">
        <loc:message code="admin.changeNicknameTitle"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">
                    <%--nickname--%>
                    <div class="input-block-wrapper clearfix">
                        <loc:message code="register.nickname" var="nickname"/>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="login" class="form-control input-block-wrapper__input"
                                   type="text" name="nickname" value="${user.nickname}" required readonly>
                        </div>
                        <div class="col-md-3">
                            <c:set var="userNickname" value = "${user.nickname}"/>
                            <c:choose>

                                <c:when test="${empty userNickname}">
                                    <input id="go_to_change_nickname" class="btn btn-warning btn-sm" value="Change" readonly>
                                </c:when>

                                <c:otherwise>
                                    <input id="go_to_change_nickname" class="btn btn-warning btn-sm" value="Change" readonly disabled>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <br/><br/><br/>
                    </div>
            </div>
        </div>
    </div>
</section>

<%--NICKNAME change form | START--%>
<a id="nickname_change_hide" data-fancybox href="#nickname_change" class="popup__bottom-link" style="display: none">Nickname change</a>

<div id="nickname_change" class="popup">
    <div class="popup__inner">
        <div class="popup__caption">Nickname change</div>

        <div class="popup__sub-caption">
            Now, we need to create a unique and correct nickname.
        </div>

        <form id="nickname_change_form" action="/settings/changeNickname/submit" class="form" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="field">
                <div class="field__label">Nickname</div>
                <input id="nickname" class="field__input" type="text" name="nickname" placeholder='<loc:message code="register.nickname"/>' required>
                <div id="nickname_exists" class='field__error' style="display:none">Nickname is already exist.</div>
                <div id="nickname_required" class='field__error' style="display:none">Nickname field is required.</div>
                <div id="nickname_start_with_digit_or_symbol" class='field__error' style="display:none">Nickname cannot start with a digit or symbol.</div>
                <div id="nickname_fail_length" class='field__error' style="display:none">Nickname cannot be less than 2 and more than 20 characters long.</div>
                <div id="nickname_contain_special_symbols" class='field__error' style="display:none">Nickname cannot contain special characters except dashes (-), underscores (_) and periods (.).</div>
                <div id="nickname_only_latin_characters" class='field__error' style="display:none">Use only Latin characters.</div>
            </div>

            <div class="field field--btn__new">
                <input id="nickname_change__submit" class="btn__new btn__new--form" type="submit" value="Change nickname" disabled>
            </div>
        </form>
    </div>
</div>
<%--NICKNAME change form | END--%>