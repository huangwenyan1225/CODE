<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${category.name}</title>
    <meta name="decorator" content="cms_default_${site.theme}"/>
    <meta name="description" content="${category.description}"/>
    <meta name="keywords" content="${category.keywords}"/>
   <%-- <link href="${ctxStatic}/bootstrap/2.3.1/css_${not empty cookie.theme.value ? cookie.theme.value : 'cerulean'}/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
    <!--[if lte IE 6]>
    <link href="${ctxStatic}/bootstrap/bsie/css/bootstrap-ie6.min.css" type="text/css" rel="stylesheet"/>
    <script src="${ctxStatic}/bootstrap/bsie/js/bootstrap-ie.min.js" type="text/javascript"></script><![endif]-->
    <link href="${ctxStatic}/common/jeesite.min.css" type="text/css" rel="stylesheet"/>
    <link href="${ctxStaticTheme}/style.css" type="text/css" rel="stylesheet"/>
    <script src="${ctxStaticTheme}/script.js" type="text/javascript"></script>--%>
</head>
<body>
<div class="row">
    <div class="span2">
        <h4>栏目列表</h4>
        <ol>
            <cms:frontCategoryList categoryList="${categoryList}"/>
        </ol>
        <h4>推荐阅读</h4>
        <ol>
            <cms:frontArticleHitsTop category="${category}"/>
        </ol>
    </div>
    <div class="span10">
        <ul class="breadcrumb">
            <cms:frontCurrentPosition category="${category}"/>
        </ul>
    </div>
    <div class="span10">


        <c:if test="${category.module eq 'article'}">
            <ul><c:forEach items="${page.list}" var="article">
                <li>
                    <div class="media">
                        <a class="pull-left" href="${article.url}">
                            <img class="media-object" src="${article.image}"
                            >
                        </a>
                        <div class="media-body">
                            <h4 class="media-heading"><a href="${article.url}" target="_blank" style="color:${article.color}">${fns:abbr(article.title,96)}</a></h4>
                            <p>${article.description}</p>
                        </div>
                    </div>

                <li>
                        <%--
                                        <li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy.MM.dd"/></span><a href="${article.url}" style="color:${article.color}">${fns:abbr(article.title,96)}</a></li>
                        --%>
            </c:forEach></ul>
            <div class="pagination">${page}</div>
            <script type="text/javascript">
                function page(n, s) {
                    location = "${ctx}/list-${category.id}${urlSuffix}?pageNo=" + n + "&pageSize=" + s;
                }
            </script>
        </c:if>
        <c:if test="${category.module eq 'link'}">
            <ul><c:forEach items="${page.list}" var="link">
                <li><a href="${link.href}" target="_blank" style="color:${link.color}"><c:out value="${link.title}"/></a></li>
            </c:forEach></ul>
        </c:if>
    </div>
</div>
</body>
</html>