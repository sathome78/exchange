<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Exrates</title>

    <link href="/client/img/favicon.ico" rel="shortcut icon" type="image/x-icon" />

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <%@include file="../tools/google_head.jsp"%>

    <link href='/client/css/roboto-font-400_700_300.css' rel='stylesheet' type='text/css'>

    <script src="/client/js/jquery_1.11.3.min.js" type="text/javascript"></script>
    <link href="/client/css/bootstrap.min.css" rel="stylesheet">
    <link href="/client/css/style.css" rel="stylesheet">


    <!-- New design -->
    <link rel="stylesheet" href="/client/assets/css/main.min.css">
    <link rel="stylesheet" href="/client/assets/css/libs.min.css">
</head>
<body>
<%@include file="../tools/google_body.jsp"%>
<style>
    body,
    html {
        height: 100%;
        background-color: #fff;
    }

    .page-wrap {
        position: relative;
        z-index: 2;
        display: -webkit-flex;
        display: -ms-flex;
        display: flex;
        -webkit-flex-direction: column;
        -ms-flex-direction: column;
        flex-direction: column;
        -ms-align-items: center;
        align-items: center;
        height: 100%;
    }

    .logo {
        position: relative;
        z-index: 2;
        margin: 36px 0 36px;

    }

    .bg {
        width: 100%;
        height: 100%;
        /* background-color: #237BEF; */
        background-image: url('/client/img/light-bg.svg');
        -webkit-background-size: contain;
        background-size: contain;
        background-repeat: no-repeat;
        position: absolute;
        z-index: 1;
    }
</style>
<!-- START-->
<div class="bg"></div>
<div class="page-wrap">
    <img src="/client/img/logo-works.svg" alt="" class="logo">
    <h1 style="font-size: 36px;line-height: 42px;margin-bottom: 8px; color:#fff;">Maintenance work in progress </h1>
    <span style="font-size: 24px;line-height: 28px;margin-bottom: 36px; color:#fff;">Please try again later</span>
    <span style="visibility: hidden; height: 0;">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Eius quis pariatur perferendis. Et labore exercitationem cumque voluptate cupiditate, libero voluptates iste explicabo, ad natus eos iusto dolorum provident itaque perferendis, repudiandae quam. Illo quia quis sed facilis optio, at provident animi deserunt nulla nesciunt! Harum autem similique commodi aliquam vitae.</span>
    <a class="twitter-timeline" data-width="768" data-height="1000" data-theme="light" href="https://twitter.com/Exrates_Me?ref_src=twsrc%5Etfw">Tweets by Exrates_Me</a>
    <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
</div>
<!-- END -->
</body>
</html>
