
<div id="alerts_container">
    <div class="alert alert-technical-work" id="tech_alert" style="display: none">
        <h4 class="text-center" id="tech_alert_text"></h4>
    </div>

    <div class="alert alert-technical-work" id="sys_message_alert" style="display: none">
        <h4 class="text-center" id="sys_message_alert_title"></h4>
        <h5 class="text-center" id="sys_message_alert_text"></h5>
    </div>

    <div class="alert alert-technical-work" id="upd_alert" style="display: none; padding-bottom: 0">
        <h4 class="text-center" id="upd_alert_text"></h4>
        <br>
        <br>
        <div class="countdown countdown-container container" style="margin-left: 40%">

            <div class="clock row">
                <h6 style="margin-left: 3%"><loc:message code="message.alert.timerTimeTo"/>:</h6>
                <!-- hours -->
                <div class="clock-item clock-hours countdown-time-value col-sm-6 col-md-3">
                    <div class="wrap">
                        <div class="inner">
                            <div id="canvas_hours" class="clock-canvas"></div>
                            <div class="text">
                                <p class="val">0</p>
                                <p class="type-hours type-time"><loc:message code="message.alert.hours"/></p>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- minutes -->
                <div class="clock-item clock-minutes countdown-time-value col-sm-6 col-md-3">
                    <div class="wrap">
                        <div class="inner">
                            <div id="canvas_minutes" class="clock-canvas"></div>
                            <div class="text">
                                <p class="val">0</p>
                                <p class="type-minutes type-time"><loc:message code="message.alert.minutes"/></p>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- seconds -->
                <div class="clock-item clock-seconds countdown-time-value col-sm-6 col-md-3">
                    <div class="wrap">
                        <div class="inner">
                            <div id="canvas_seconds" class="clock-canvas"></div>
                            <div class="text">
                                <p class="val">0</p>
                                <p class="type-seconds type-time"><loc:message code="message.alert.seconds"/></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>