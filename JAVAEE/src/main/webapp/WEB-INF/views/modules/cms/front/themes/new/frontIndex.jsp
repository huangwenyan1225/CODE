<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>首页</title>
    <meta name="decorator" content="cms_default_${site.theme}"/>
    <meta name="description" content="JeeSite ${site.description}"/>
    <meta name="keywords" content="JeeSite ${site.keywords}"/>
</head>
<body>
<%--<div class="hero-unit" style="padding-bottom:35px;margin:10px 0;">
    <c:set var="article" value="${fnc:getArticle('2')}"/>
    <h1>${fns:abbr(article.title,28)}</h1>
    <p></p>
    <p>${fns:abbr(fns:replaceHtml(article.articleData.content),260)}</p>
    <p><a href="${article.url}" class="btn btn-primary btn-large">&nbsp;&nbsp;&nbsp;查看详情 &raquo;&nbsp;&nbsp;&nbsp;</a></p>
</div>--%>
<div id="myCarousel" class="carousel slide" style="padding-bottom:15px;margin:10px 0;">
    <!-- 轮播（Carousel）指标 -->

    <ol class="carousel-indicators">
        <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
        <li data-target="#myCarousel" data-slide-to="1"></li>
        <li data-target="#myCarousel" data-slide-to="2"></li>
        <li data-target="#myCarousel" data-slide-to="3"></li>
    </ol>
    <!-- 轮播（Carousel）项目 -->
    <div class="carousel-inner center">
        <c:forEach items="${fnc:getArticleList(site.id, 2, 4, '')}" var="article" varStatus="status">
            <a class="item ${status.index eq 0? "active":""}" href="${article.url}">
                <img src="${article.image}" class="img-responsive "    style="width: 70%;margin:0 auto;">
                <div class="carousel-caption">"${article.title}"</div>
            </a>
        </c:forEach>
        <!-- 轮播（Carousel）导航 -->
        <a class="carousel-control left" href="#myCarousel"
           data-slide="prev">&lsaquo;
        </a>
        <a class="carousel-control right" href="#myCarousel"
           data-slide="next">&rsaquo;
        </a>
    </div>

</div>

<div class="row">
    <div class="col-xs-6 col-sm-3 col-md-6 col-lg-6">
        <h4>
            <small><a href="${ctx}/list-2${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>
            今日最美
        </h4>
        <ul class="media-list">
            <c:forEach items="${fnc:getArticleList(site.id, 2, 10, '')}" var="article">
                <li class="media pull-left">
                    <a class="pull-left" href="${article.url}">
                        <img class="media-object" src="${article.image}" alt="媒体对象">
                    </a>
                    <div class="media-body ">
                        <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                        <p> ${article.description}</p>
                    </div>
                </li>
                <%--
                            <li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy.MM.dd"/></span><a href="${article.url}" style="color:${article.color}">${fns:abbr(article.title,28)}</a></li>
                --%>
            </c:forEach></ul>
    </div>
    <%--<div class="col-xs-6 col-sm-3 col-md-6 col-lg-6">
        <h4>
            <small><a href="${ctx}/list-6${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>
            今日最萌
        </h4>
        <ul><c:forEach items="${fnc:getArticleList(site.id, 6, 8, '')}" var="article">
            <li class="media pull-left">
                <a class="pull-left" href="${article.url}">
                    <img class="media-object img-responsive" src="${article.image}" alt="媒体对象">
                </a>
                <div class="media-body ">
                    <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                    <p> ${article.description}</p>
                </div>
            </li>
&lt;%&ndash;
            <li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy.MM.dd"/></span><a href="${article.url}" style="color:${article.color}">${fns:abbr(article.title,28)}</a></li>
&ndash;%&gt;
        </c:forEach></ul>
    </div>--%>
    <div class="col-xs-6 col-sm-3 col-md-6 col-lg-6">
        <h4>
            <small><a href="${ctx}/list-10${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>
            今日最萌
        </h4>
        <ul class="media-list"><c:forEach items="${fnc:getArticleList(site.id, 10, 10, '')}" var="article">
            <li class="media pull-left">
                <a class="pull-left" href="${article.url}">
                    <img class="media-object img-responsive" src="${article.image}" alt="媒体对象">
                </a>
                <div class="media-body ">
                    <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                    <p> ${article.description}</p>
                </div>
            </li>
        </c:forEach></ul>
    </div>
    <div class="col-xs-6 col-sm-3 col-md-6 col-lg-6">
        <h4>
            <small><a href="${ctx}/list-10${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>
            今日最动听
        </h4>
        <ul class="media-list"><c:forEach items="${fnc:getArticleList(site.id, 10, 10, '')}" var="article">
            <li class="media pull-left">
                <a class="pull-left" href="${article.url}">
                    <img class="media-object img-responsive" src="${article.image}" alt="媒体对象">
                </a>
                <div class="media-body ">
                    <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                    <p> ${article.description}</p>
                </div>
            </li>
        </c:forEach></ul>
    </div>
    <div class="col-xs-6 col-sm-3 col-md-6 col-lg-6">
        <h4>
            <small><a href="${ctx}/list-10${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>
            今日最热门
        </h4>
        <ul class="media-list"><c:forEach items="${fnc:getArticleList(site.id, 10, 10, '')}" var="article">
            <li class="media pull-left">
                <a class="pull-left" href="${article.url}">
                    <img class="media-object img-responsive" src="${article.image}" alt="媒体对象">
                </a>
                <div class="media-body ">
                    <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                    <p> ${article.description}</p>
                </div>
            </li>
        </c:forEach></ul>
    </div>
</div>
<script>
    $(function () {
        $("#myCarousel").carousel('cycle');
    });

</script>
</body>


</html>