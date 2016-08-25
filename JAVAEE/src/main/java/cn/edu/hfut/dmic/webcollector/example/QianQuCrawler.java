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
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

@Component public class QianQuCrawler extends BreadthCrawler
{

	private final File downloadDir;
	private AtomicInteger imageId;

	public interface CallBack
	{
		void callBack(Article article, ArticleData articleData);
	}

	static CallBack callBack;

	public void setCallBack(CallBack callBack)
	{
		this.callBack = callBack;
	}

	public QianQuCrawler(String crawlPath, boolean autoParse, String keyword)
	{
		super(crawlPath, autoParse);
		downloadDir = new File("/file/image/");
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		computeImageId();
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
			String imageFileName=imageId.incrementAndGet()+"."+extensionName;
			File imageFile=new File(downloadDir,imageFileName);
			try {
				FileUtils.writeFile(imageFile, page.getContent());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		String path=	imageFile.getPath().replace("\\","/");
			article.setImage(path);
			if (callBack != null)
			{
				callBack.callBack(article, articleData);
			}
		}
		else if (pageType.equals("outlink"))
		{
			String html = page.getHtml();
			String content = html.substring(html.indexOf("<div class=\"contentText\">"), html.indexOf("<div id=\"SOHUCS\" >"));
			CrawlDatum crawlDatum = new CrawlDatum(page.meta("Image")).meta("Title", page.meta("Title")).meta("Description", page.meta("Description")).meta("pageType", "Image").meta("content",content);
			next.add(crawlDatum);


		}
	}
	public void computeImageId(){
		int maxId=-1;
		for(File imageFile:downloadDir.listFiles()){
			String fileName=imageFile.getName();
			String idStr=fileName.split("\\.")[0];
			int id=Integer.valueOf(idStr);
			if(id>maxId){
				maxId=id;
			}
		}
		imageId=new AtomicInteger(maxId);
	}
	public void startS()
	{
		for (int pageNum = 1; pageNum <= 5; pageNum++)
		{
			String url = null;
			try
			{
				url = createBingUrl("最美", pageNum);
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
						QianQuCrawler crawler = new QianQuCrawler("depth_crawlerd", true, link);
						crawler.addSeed(crawlDatum);
						//crawler.setResumable(true);
						crawler.start(10);

					}
				}
			};
			//创建一个基于伯克利DB的DBManager
			DBManager manager = new BerkeleyDBManager("crawl");
			//创建一个Crawler需要有DBManager和Executor
			Crawler crawler = new Crawler(manager, executor);
			//crawler.setResumable(true);
			crawler.addSeed(url);
			try
			{
				crawler.start(10);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		QianQuCrawler crawler = new QianQuCrawler("depth_crawlerd", true, "");
		crawler.startS();
	}

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
		return String.format("http://www.qianqu.cc/index/search?q=%s&page=%s", keyword, pageNum);
	}

}
