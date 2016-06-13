/**
 * 
 */
package cn.services;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.coobird.thumbnailator.Thumbnails;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.elasticsearch.client.Client;

import cn.beans.EPicture;
import cn.beans.Picture;

import com.google.gson.Gson;

/**
 * @author chenen
 *
 */
public class PictureServiceBoy {
	private Logger log=Logger.getLogger(this.getClass());
	
	public void picMainProcecorNas(SmbFile f) throws ClientProtocolException, UnsupportedOperationException, IOException//图片处理主函数
	{
	
		String path=f.getPath();
		path=path.substring(path.indexOf("dpimage")+8);
		if(path.lastIndexOf('.')<0)
		{
			return;//判断是否为可出来图片格式
		}
		String suffix=path.substring(path.lastIndexOf('.')).toLowerCase();
		if(suffix.equals(".jpg")||suffix.equals(".tif")||suffix.equals(".tiff"))
		{
			BufferedImage bi=pic2Nas(f);
			if(bi==null)
			{
				return;
			}
			
			
		}
		
	}
	public void picMainProcecor(SmbFile f) throws ClientProtocolException, UnsupportedOperationException, IOException//图片处理主函数
	{
	
		String path=f.getPath();
		path=path.substring(path.indexOf("dpimage")+8);
		if(path.lastIndexOf('.')<0)
		{
			return;//判断是否为可出来图片格式
		}
		String suffix=path.substring(path.lastIndexOf('.')).toLowerCase();
		if(suffix.equals(".jpg")||suffix.equals(".tif")||suffix.equals(".tiff"))
		{
			BufferedImage bi=pic2Nas(f);
			if(bi==null)
			{
				return;
			}
			pic2Lire(bi,path);
			EPicture ep=new EPicture();
			ep.setPicpath(path);
			ep.setHeight(bi.getHeight()+"");
			ep.setWidth(bi.getWidth()+"");
			String picname=path.substring(path.lastIndexOf('/')+1,path.lastIndexOf('.'));
			if(picname.getBytes().length>picname.length())
			{
			ep.setDescrible(picname);
			}
			String fid=pic2Elastic(ep);
			
			Picture p=new Picture();
			p.setDescrible(ep.getDescrible());
			p.setHeight(bi.getHeight());
			p.setPicpath(ep.getPicpath());
			p.setPid(fid);
			p.setWidth(bi.getWidth());
			
			pic2Db(p);
			
		}
		
	}
	private BufferedImage pic2Nas(SmbFile sf) throws ClientProtocolException, IOException, UnsupportedOperationException 
	{
		BufferedImage bi=null;
		  try {
				if (sf!=null&&sf.exists()) {
					 HttpClient httpClient=new DefaultHttpClient();
							 String str=sf.getPath().substring(sf.getPath().indexOf("dpimage")); 
							
							 SmbFile f1 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/small/"+str);
							 SmbFile f2 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/middle/"+str);
							 SmbFile f3 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/big/"+str);
							
							 if(f1.exists()&&f2.exists()&&f3.exists())
							 {
								if(f1.length()==0||f2.length()==0||f3.length()==0)
								{
									log.info("删除："+str);
									f1.delete();
									f2.delete();
									f3.delete();
								}
								else
								{
								
								 return null;
								}
							 }
							    HttpGet httpget=null;
							    httpget= new HttpGet("http://192.168.2.249/www/"+str);
							
					            HttpResponse response = httpClient.execute(httpget);
					            HttpEntity entity = response.getEntity();
					            InputStream is=null;
								try {
									is = entity.getContent();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					            
								try {
									bi = ImageIO.read(is);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
					            createSbmFromUrl(str,"small",f1,bi);
					            createSbmFromUrl(str,"middle",f2,bi);
					            createSbmFromUrl(str,"big",f3,bi);
					            is.close();
						
					
				    }
			} catch (SmbException e) {
				bi=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				bi=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		  return bi;
	}
	private void pic2Lire(BufferedImage bi,String path)
	{
		String indexPath="/usr/local/cn/lireindex";

	      // Creating a CEDD document builder and indexing all files.
	      GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);
	      // and here we add those features we want to extract in a single run:
	      globalDocumentBuilder.addExtractor(FCTH.class);
	      globalDocumentBuilder.addExtractor(AutoColorCorrelogram.class);
	      // Creating an Lucene IndexWriter
	      IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
	      IndexWriter iw=null;
		try {
			iw = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), conf);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(iw==null)
		{
			return;
		}
	      // Iterating through images building the low level features

	          try {
	              Document document = globalDocumentBuilder.createDocument(bi, path);
	              iw.addDocument(document);
	              //Query queries=null;
	             // Term tt=new Term("ImageIdentifier",imageFilePath);
	              //iw.deleteDocuments(tt);
	          } catch (Exception e) {
	              System.err.println("Error reading image or indexing it.");
	              e.printStackTrace();
	          }
	      try {
			iw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String pic2Elastic(EPicture ep)
	{
		Gson jsonConverter=new Gson();
		Client client=StaticClient.getClient();
		 String fileid=getId();
			
			client.prepareIndex("dp", "picture")
					.setId(fileid)
			        .setSource(jsonConverter.toJson(ep))
			        .get();
			return fileid;
	}
	private void pic2Db(Picture p)
	{
		DbService ds=StaticClient.getDbService();
		ds.insertPic(p);
	}
	
	private String  getId()
	{
		String newid = System
		.currentTimeMillis()
		+ "_"
		+ (int) (Math.random() * 100);
		return newid;
	}
	
	private void createSbmFromUrl(String path,String type,SmbFile f1,BufferedImage bi)
	{
		try {
			if(f1.exists())
			{
				return;//二次验证
			}
		} catch (SmbException e1) {
		}
		//文件夹与文件
		 String url0="smb://admin:nic123321@192.168.2.249/web/www/"+type+"/"+path.substring(0,path.lastIndexOf('/'));
		 //String url1="smb://admin:nic123321@192.168.2.249/web/www/"+type+"/"+path;
		 SmbFile f0;
		try {
			f0 = new SmbFile(url0);
			if(!f0.isDirectory())//判断目录是否存在
			 {
			 f0.mkdirs();
			 }
				 f1.createNewFile();
				 OutputStream ops= new  BufferedOutputStream(f1.getOutputStream());
				 if(bi.getWidth()>bi.getHeight())
				 {
					 Thumbnails.of(bi)
						.allowOverwrite(true)
						.height(caculator(bi.getWidth(),bi.getHeight(),type))
						.outputFormat("jpg")
						.toOutputStream(ops);
				 }
				 else
				 {
				 Thumbnails.of(bi)
					.allowOverwrite(true)
					.width(caculator(bi.getWidth(),bi.getHeight(),type))
					.outputFormat("jpg")
					.toOutputStream(ops);
				 }
				 ops.close();
			 
		} catch (MalformedURLException e) {
			log.info(e.getMessage());
		}
		 catch (SmbException e) {
			 log.info(e.getMessage());
		}catch (IOException e) {
			log.info(e.getMessage());
		}
	} 
	private int caculator(int width,int height,String type)
	{
		if(width>height)
		{
			if(type.equals("small"))
			{
				return 200;
			}
			if(type.equals("middle"))
			{
				return 400;
			}
			else
			{
				return 800;
			}
		}
		else
		{
			if(type.equals("small"))
			{
				return 200;
			}
			if(type.equals("middle"))
			{
				return 400;
			}
			else
			{
				return 800;
			}
		}
	}

}
