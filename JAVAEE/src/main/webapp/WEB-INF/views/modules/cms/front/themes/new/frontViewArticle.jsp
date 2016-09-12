<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${article.title} - ${category.name}</title>
    <meta name="decorator" content="cms_default_${site.theme}"/>
    <meta name="description" content="${article.description} ${category.description}"/>
    <meta name="keywords" content="${article.keywords} ${category.keywords}"/>
    <script type="text/javascript">
        $(document).ready(function () {
            if ("${category.allowComment}" == "1" && "${article.articleData.allowComment}" == "1") {
                $("#comment").show();
                page(1);
            }
        });
        function page(n, s) {
            $.get("${ctx}/comment", {
                theme: '${site.theme}', 'category.id': '${category.id}',
                contentId: '${article.id}', title: '${article.title}', pageNo: n, pageSize: s, date: new Date().getTime()
            }, function (data) {
                $("#comment").html(data);
            });
        }
    </script>
</head>
<body>
<div class="row">
    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <ul class="breadcrumb">
            <cms:frontCurrentPosition category="${category}"/>
        </ul>
    </div>
    <div class="col-xs-4 col-sm-6 col-md-10 col-lg-10">
        <div class="row">
            <div class="col-xs-4 col-sm-6 col-md-10 col-lg-10">
                <h3 style="color:#555555;font-size:20px;text-align:center;border-bottom:1px solid #ddd;padding-bottom:15px;margin:25px 0;">${article.title}</h3>
                <%--	<c:if test="${not empty article.description}"><div>摘要：${article.description}</div></c:if>--%>
                <div>${article.articleData.content}</div>
                <div style="border-top:1px solid #ddd;padding:10px;margin:25px 0;">发布者：                     &nbsp; 点击数：                &nbsp; 发布时间：                                                                              &nbsp; 更新时间：<fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
            </div>
        </div>
        <div class="row">
            <div id="comment" class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                正在加载评论...
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <h5>相关文章</h5>
                <ol><c:forEach items="${relationList}" var="relation">
                    <li style="float:left;width:230px;"><a href="${ctx}/view-${relation[0]}-${relation[1]}${urlSuffix}">${fns:abbr(relation[2],30)}</a></li>
                </c:forEach></ol>
            </div>
        </div>
    </div>
    <div class="col-xs-3 col-sm-3 col-md-2 col-lg-2">
        <h4>栏目列表</h4>
        <ol>
            <cms:frontCategoryList categoryList="${categoryList}"/>
        </ol>
        <h4>推荐阅读</h4>
        <ol>
            <cms:frontArticleHitsTop category="${category}"/>
        </ol>
    </div>


</div>
</body>
</html>