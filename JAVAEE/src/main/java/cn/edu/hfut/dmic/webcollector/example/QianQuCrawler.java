/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.example;

import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawler.Crawler;
import cn.edu.hfut.dmic.webcollector.fetcher.Executor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BerkeleyDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 本教程演示了WebCollector 2.20的新特性:
 * 1)MetaData:
 * MetaData是每个爬取任务的附加信息,灵活应用MetaData可以大大简化爬虫的设计.
 * 例如Post请求往往需要包含参数，而传统爬虫单纯使用URL来保存参数的方法不适合复杂的POST请求.
 * 一些爬取任务希望获取遍历树的深度信息，这也可以通过MetaData轻松实现，可参见教程DemoDepthCrawler
 * <p>
 * 2)RamCrawler:
 * RamCrawler不需要依赖文件系统或数据库，适合一次性的爬取任务.
 * 如果希望编写长期任务，请使用BreadthCrawler.
 * <p>
 * 本教程实现了一个爬取Bing搜索前n页结果的爬虫，爬虫的结果直接输出到标准输出流
 * 如果希望将爬取结果输出到ArrayList等数据结构中，在类中定义一个ArrayList的成员变量，
 * 输出时将结果插入ArrayList即可，这里需要注意的是爬虫是多线程的，而ArrayList不是线程
 * 安全的，因此在执行插入操作时，可使用synchronized(this){ //插入操作}的方式上锁保证安全。
 * <p>
 * 本教程中对Bing搜索的解析规则可能会随Bing搜索的改版而失效
 *
 * @author hu
 */

@Component public class QianQuCrawler extends RamCrawler
{

	private final File downloadDir;
//	private AtomicInteger imageId;
String attionWei="和美女一姐打嘴炮、探讨不一样姿势。微信添加朋友→公众号→搜“姿势情报局一姐”（已认证），太纯洁的，不要来！";
String attionWei1="(千趣网实习美女小编微信公号——“广告系小师妹”，已认证，想看小师妹搜集的杜蕾斯趣味广告合集?想和小师妹拉家常打嘴炮、想要小师妹的私照?抓紧关注，欢迎骚扰!)";
	public interface CallBack
	{
		void callBack(Article article, ArticleData articleData);
	}
	long waitLoadBaseTime = 3000;
	int waitLoadRandomTime = 3000;
	Random random = new Random(System.currentTimeMillis());

	static CallBack callBack;
	HttpServletRequest request;

	public void setCallBack(CallBack callBack)
	{
		this.callBack = callBack;
	}

	public QianQuCrawler(String crawlPath, boolean autoParse, String keyword, HttpServletRequest request)
	{
	//	/uploads/image/article/big/20160630/1467257043818023932.jpg
		this.request=request;
		downloadDir = new File(request.getServletContext().getRealPath("/")+"uploads/image/article/");
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
//		computeImageId();
	}

	@Override public void visit(final Page page, CrawlDatums next)
	{

		String pageType = page.meta("pageType");
		if (pageType.equals("Image"))
		{
			Article article = new Article();
			article.setTitle(page.meta("Title"));

			//    article.setLink(page.meta("link"));
			article.setDescription(page.meta("Description"));

			ArticleData articleData = new ArticleData();
			articleData.setContent(page.meta("content"));
			//如果是图片，直接下载
			String contentType = page.getResponse().getContentType();
			String extensionName=contentType.split("/")[1];
			String imageFileName=System.currentTimeMillis()+"."+extensionName;
			File imageFile=new File(downloadDir,imageFileName);
			try {
				FileUtils.writeFile(imageFile, page.getContent());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			article.setImage("/uploads/image/article/"+imageFileName);
			if (callBack != null)
			{
				callBack.callBack(article, articleData);
			}
		}else if (pageType.equals("ImageDetail"))
	{

		//如果是图片，直接下载
		String path=	page.meta("imagePath").replace("\\","/");
		File		filepath = new File(request.getRealPath("/")+path);
//		if(!filepath.exists()){
//			filepath.mkdirs();
//		}
		if(!filepath.getParentFile().exists()) {
			//如果目标文件所在的目录不存在，则创建父目录
			filepath.getParentFile().mkdirs();
		}
		try {
			filepath.createNewFile();
			FileUtils.writeFile(filepath, page.getContent());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}
		else if (pageType.equals("outlink"))
		{
			String html = page.getHtml();
			String content = html.substring(html.indexOf("<div class=\"contentText\">"), html.indexOf("<div id=\"SOHUCS\" >"));
			if (content.contains(attionWei))
			{
				content=	content.replace(attionWei,"");
			}
			if (content.contains(attionWei1))
			{
				content=	content.replace(attionWei1,"");
			}
			CrawlDatum crawlDatum = new CrawlDatum(page.meta("Image")).meta("Title", page.meta("Title")).meta("Description", page.meta("Description")).meta("pageType", "Image").meta("content",content);
			next.add(crawlDatum);
			ArrayList<String> attrs = page.getAttrs("p img[src]", "abs:src");
			ArrayList<String> attrss = page.getAttrs("p img[src]", "src");
			for (int i=0;i<attrs.size();i++ )
			{
				crawlDatum=new CrawlDatum(attrs.get(i));
				crawlDatum.meta("pageType","ImageDetail").meta("imagePath",attrss.get(i));
				next.add(crawlDatum);
			}




		}
	}
//	public void computeImageId(){
//		int maxId=-1;
//		for(File imageFile:downloadDir.listFiles()){
//			String fileName=imageFile.getName();
//			String idStr=fileName.split("\\.")[0];
//			int id=Integer.valueOf(idStr);
//			if(id>maxId){
//				maxId=id;
//			}
//		}
//		imageId=new AtomicInteger(maxId);
//	}
	public void startS()
	{
			String url = null;
			try
			{
				url = createBingUrl("最美", 1);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			Executor executor = new Executor()
			{
				@Override public void execute(CrawlDatum page, CrawlDatums next) throws Exception
				{
					String pageType = page.meta("pageType");

					HtmlUnitDriver driver = new HtmlUnitDriver();
					driver.setJavascriptEnabled(true);
					driver.get(page.getUrl());
					WebElement element = driver.findElementByCssSelector("div#mainContent");
					List<WebElement> elementss = element.findElements(By.cssSelector("div.article"));

					for (WebElement element0 : elementss)
					{
						WebElement webElementa = element0.findElement(By.cssSelector("img"));
						WebElement webElementb = element0.findElement(By.cssSelector("h3 a"));
						WebElement webElementc = element0.findElement(By.cssSelector("p"));
						WebElement webElementd = element0.findElement(By.cssSelector("a"));
						String href = webElementa.getAttribute("src");
						String link = webElementd.getAttribute("href");
						CrawlDatum crawlDatum = new CrawlDatum(link).meta("Title", webElementb.getText()).meta("Image", href).meta("Description", webElementc.getText()).meta("pageType", "outlink").meta("link", link);
						QianQuCrawler crawler = new QianQuCrawler("bcrawlerd", true, link, request);
						crawler.addSeed(crawlDatum);
						crawler.setThreads(30);
						crawler.start(1);

					}
					for(int i=2; i<14; i++) {
						//滚动加载下一页
						driver.findElement(By.cssSelector("a.page-link.next")).click();
						//等待页面动态加载完毕
						Thread.sleep(waitLoadBaseTime+random.nextInt(waitLoadRandomTime));
						WebElement element2 = driver.findElementByCssSelector("div#mainContent");
						List<WebElement> elementss2 = element2.findElements(By.cssSelector("div.article"));

						for (WebElement element0 : elementss2)
						{
							WebElement webElementa = element0.findElement(By.cssSelector("img"));
							WebElement webElementb = element0.findElement(By.cssSelector("h3 a"));
							WebElement webElementc = element0.findElement(By.cssSelector("p"));
							WebElement webElementd = element0.findElement(By.cssSelector("a"));
							String href = webElementa.getAttribute("src");
							String link = webElementd.getAttribute("href");
							CrawlDatum crawlDatum = new CrawlDatum(link).meta("Title", webElementb.getText()).meta("Image", href).meta("Description", webElementc.getText()).meta("pageType", "outlink").meta("link", link);
							QianQuCrawler crawler = new QianQuCrawler("bcrawlerd", true, link, request);
							crawler.addSeed(crawlDatum);
							crawler.setThreads(30);
							crawler.start(5);

						}
						Thread.sleep(waitLoadBaseTime+random.nextInt(waitLoadRandomTime));
					}
				}
			};
			//创建一个基于伯克利DB的DBManager
			DBManager manager = new BerkeleyDBManager("acrawlerd");
			//创建一个Crawler需要有DBManager和Executor
			Crawler crawler = new Crawler(manager, executor);
			crawler.setThreads(30);
			crawler.addSeed(url);
			try
			{
				crawler.start(1);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

	}

//	public static void main(String[] args) throws Exception
//	{
//		QianQuCrawler crawler = new QianQuCrawler("depth_crawlerd", true, "", request);
//		crawler.startS();
//	}

	/**
	 * 根据关键词和页号拼接Bing搜索对应的URL
	 *
	 * @param keyword 关键词
	 * @param pageNum 页号
	 * @return 对应的URL
	 * @throws Exception 异常
	 */
	public static String createBingUrl(String keyword, int pageNum) throws Exception
	{
		keyword = URLEncoder.encode(keyword, "utf-8");
		return String.format("http://www.qianqu.cc/index/search?q=%s&page=%s", keyword, pageNum+"");
	}

}
