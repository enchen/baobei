/**
 * 
 */
package cn.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import cn.beans.Article;
import cn.beans.ArticleIndex;
import cn.util.PropertyUtils;

import com.google.gson.Gson;








/**
 * @author chenen
 *
 */
import org.apache.log4j.Logger;

//文档处理线程
public class ProcessThread implements Runnable {
	
	private String filepath;
	private String docname;
	private String fullpath;

	
	public ProcessThread(String filepath,String docname,String fullpath){
		this.filepath=filepath;
		this.docname=docname;
		this.fullpath=fullpath;
		
	
	}
	public void run(){
		
		DbService dbService=StaticClient.getDbService();
		if(StaticClient.getDbService().countfile(filepath,docname)>0)
		{
			return ;
		}
		Client client=StaticClient.getClient();
		File inputFile=new File(fullpath);
		
		
		if(fullpath.substring(fullpath.lastIndexOf('.')).equals(".doc"))
		{
			Gson jsonConverter=new Gson();
			 WordExtractor wordExtractor=null;
			try {
				wordExtractor = new WordExtractor(new FileInputStream(inputFile));
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error("doc@@文档萃取@@"+fullpath+e.getMessage());
				e.printStackTrace();
				
				if(e.getMessage().indexOf("RTF")>-1)
				{
					try {
						FileUtils.copyFile(inputFile, new File(new PropertyUtils().getProp("rtfdoc")+filepath+docname));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}
				else
				{
				return;
				}
				
			} 
			if(wordExtractor!=null)
			{
			 String [] strArray =wordExtractor.getParagraphText();
		
			 for(int i=0;i<strArray.length;i++)
			 {
				// System.out.print(i+":"+strArray[i]);
				// String my_attachment=new BASE64Encoder().encode(strArray[i].getBytes());
				 String fileid=getId();
				 Article at=new Article();
					
				    at.setPath(filepath);
					at.setFilename(docname);
					at.setContent(strArray[i]);
					at.setUpdatetime(Calendar.getInstance().getTime().toLocaleString());
				
				
				IndexResponse response = client.prepareIndex("dp", "article")
						.setId(fileid)
				        .setSource(jsonConverter.toJson(at))
				        .get();
				boolean created = response.isCreated();
				if(!created)
				{
					
				}
				else
				{
					ArticleIndex ati=new ArticleIndex();
					ati.setPath(at.getPath());
					ati.setContent(at.getContent());
					ati.setFileid(fileid);
					ati.setFilename(at.getFilename());
					ati.setUpdatetime(at.getUpdatetime());
					try{
						dbService.insertFile(ati);
						}catch(Exception e)
						{
							Logger.getLogger(this.getClass()).error("doc@@处理@@"+fullpath+e.getMessage()+"###"+ati.getContent());
							e.printStackTrace();
							return;
						}
					try {
						FileUtils.copyFile(inputFile, new File(new PropertyUtils().getProp("nginxdoc")+at.getPath()+at.getFilename()));
					} catch (IOException e) {
						Logger.getLogger(this.getClass()).error("doc@@拷贝@@"+fullpath);
						e.printStackTrace();
						return;
					}
				}
			 }
			}
		}
		
		else if(fullpath.substring(fullpath.lastIndexOf('.')).equals(".docx"))
		{
			Gson jsonConverter=new Gson();
			try {
				FileInputStream fism=new FileInputStream(inputFile);
				XWPFDocument doc = new XWPFDocument(fism);
				List<XWPFParagraph> paras = doc.getParagraphs();
				fism=null;//资源回收
				doc=null;//资源回收
				for(XWPFParagraph para:paras)
				{
					 String fileid=getId();
					//System.out.print(para.getParagraphText());
					
					    Article at=new Article();
					    at.setPath(filepath);
						at.setFilename(docname);
						at.setContent(para.getParagraphText());
						at.setUpdatetime(Calendar.getInstance().getTime().toLocaleString());
				
					IndexResponse response = client.prepareIndex("dp", "article")
							.setId(fileid)
					        .setSource(jsonConverter.toJson(at))
					        .get();
					boolean created = response.isCreated();
					if(!created)
					{
					
					}
					else
					{
						ArticleIndex ati=new ArticleIndex();
						ati.setContent(at.getContent());
						ati.setFileid(fileid);
						ati.setPath(at.getPath());
						ati.setFilename(at.getFilename());
						ati.setUpdatetime(at.getUpdatetime());
						try{
						dbService.insertFile(ati);
						
						ati=null;//资源回收
						}catch(Exception e)
						{
							Logger.getLogger(this.getClass()).error("docx@@数据库插入@@"+fullpath+e.getMessage());
							e.printStackTrace();
							return;
						}
						try {
							File toFile=new File(new PropertyUtils().getProp("nginxdoc")+at.getPath()+at.getFilename());
							FileUtils.copyFile(inputFile,toFile);
							inputFile=null;//资源回收
							toFile=null;//资源回收
						} catch (IOException e) {
							Logger.getLogger(this.getClass()).error("docx@@拷贝@@"+fullpath);
							e.printStackTrace();
							return;
						}

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error("docx@@处理@@"+fullpath);
				e.printStackTrace();
				//RTF文件转储
				if(e.getMessage().indexOf("RTF")>-1)
				{
					try {
						File rtff=new File(new PropertyUtils().getProp("rtfdoc")+filepath+docname);
						FileUtils.copyFile(inputFile, rtff);
						rtff=null;//资源回收
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}
				return;
			} 
		}
		
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
