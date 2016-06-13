/**
 * 
 */
package cn.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import cn.beans.Article;
import cn.beans.ArticleIndex;
import cn.beans.Person;
import cn.util.PropertyUtils;
import cn.util.SpringContext;

import com.alibaba.druid.util.StringUtils;
import com.google.gson.Gson;

/**
 * @author chenen
 *
 */
public class FileService {
	
	private Logger log=Logger.getLogger(this.getClass());
	public String processFiles(String filepath,String docname,String fullpath)
	{
		
		
		//System.out.println("@@"+filepath.substring(filepath.lastIndexOf('\\')+1));
		if(!fullpath.substring(fullpath.lastIndexOf('.')).equals(".doc")&& !fullpath.substring(fullpath.lastIndexOf('.')).equals(".docx"))
		{
			return null;
		}
		if(StaticClient.getDbService().countfile(filepath,docname)>0)
		{
			return null;
		}
		else
		{
			//File file=new File(filepath);
			processFile(filepath,docname,fullpath);
//			ExecutorService pool = Executors.newFixedThreadPool(100);
//			for(int i=0;i<appids.length;i++){
//				if(msgstr.contains("appidstand"))
//				{
//					String res="appid="+appids[i];
//					msgstr=msgstr.replace("appidstand",res);
//				}
//				System.out.print(msgstr);
//				Runnable t=new MsgSendThread(appids[i],msgstr);
//				pool.execute(t);
//			}
//			pool.shutdown();
			return filepath;
			
		}
	}
public boolean processFile(String filepath,String docname,String fullpath)
{
	//log.error("@@@@"+fullpath);
	if(docname.substring(0, 1).endsWith(".") )
	{
		return false;
	}
	File inputFile=new File(fullpath);
	boolean re=true;
	Client client=StaticClient.getClient();
	if(fullpath.substring(fullpath.lastIndexOf('.')).equals(".doc"))
	{
		Gson jsonConverter=new Gson();
		
		
		
		
		 WordExtractor wordExtractor=null;
		try {
			wordExtractor = new WordExtractor(new FileInputStream(inputFile));
		} catch (Exception e) {
			re=false;
			log.error("doc@@@@"+fullpath+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(wordExtractor!=null)
		{
		 String [] strArray =wordExtractor.getParagraphText();
	
		 for(int i=0;i<strArray.length;i++)
		 {
			// System.out.print(i+":"+strArray[i]);
			// String my_attachment=new BASE64Encoder().encode(strArray[i].getBytes());
			 Article at=new Article();
				
			    at.setPath(filepath);
				at.setFilename(docname);
				at.setContent(strArray[i]);
				at.setUpdatetime(Calendar.getInstance().getTime().toLocaleString());
			 String fileid=getId();
			
			client.prepareIndex("dp", "article")
					.setId(fileid)
			        .setSource(jsonConverter.toJson(at))
			        .get();
			//boolean created = response.isCreated();
			
				ArticleIndex ati=new ArticleIndex();
				ati.setPath(at.getPath());
				ati.setContent(at.getContent());
				ati.setFileid(fileid);
				ati.setFilename(at.getFilename());
				ati.setUpdatetime(at.getUpdatetime());
				try{
					StaticClient.getDbService().insertFile(ati);
					}catch(Exception e)
					{
						re=false;
						log.error(e.getMessage());
					}
				try {
					//FileUtils.copyFile(inputFile, new File("F:\\nginx-1.8.0\\html\\doc\\"+at.getPath()+at.getFilename()));
					FileUtils.copyFile(inputFile, new File(new PropertyUtils().getProp("nginxdoc")+at.getPath()+at.getFilename()));

				} catch (IOException e) {
					re=false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			at=null;
			ati=null;
		 }
		}
		jsonConverter=null;
		wordExtractor=null;
	}
	
	else if(fullpath.substring(fullpath.lastIndexOf('.')).equals(".docx"))
	{
		Gson jsonConverter=new Gson();
		try {
			
			XWPFDocument doc = new XWPFDocument(new FileInputStream(inputFile));
			List<XWPFParagraph> paras = doc.getParagraphs();
			for(XWPFParagraph para:paras)
			{
				//System.out.print(para.getParagraphText());
				
				    Article at=new Article();
				    at.setPath(filepath);
					at.setFilename(docname);
					at.setContent(para.getParagraphText());
					at.setUpdatetime(Calendar.getInstance().getTime().toLocaleString());
			String fileid=getId();
			client.prepareIndex("dp", "article")
						.setId(fileid)
				        .setSource(jsonConverter.toJson(at))
				        .get();
				//boolean created = response.isCreated();
				
					ArticleIndex ati=new ArticleIndex();
					ati.setContent(at.getContent());
					ati.setFileid(fileid);
					ati.setPath(at.getPath());
					ati.setFilename(at.getFilename());
					ati.setUpdatetime(at.getUpdatetime());
					try{
						StaticClient.getDbService().insertFile(ati);
					}catch(Exception e)
					{
						re=false;
						log.error(e.getMessage());
					}
					try {
						FileUtils.copyFile(inputFile, new File(new PropertyUtils().getProp("nginxdoc")+at.getPath()+at.getFilename()));
					} catch (IOException e) {
						re=false;
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					at=null;
					ati=null;
			}
			doc=null;
			paras=null;
		} catch (Exception e) {
			re=false;
			log.error("docx@@"+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	inputFile=null;
	return re;
}
private String  getId()
{
	String newid = System
	.currentTimeMillis()
	+ "_"
	+ (int) (Math.random() * 100);
	return newid;
}

}
