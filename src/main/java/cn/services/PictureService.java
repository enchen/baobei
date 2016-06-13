/**
 * 
 */
package cn.services;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
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

import com.google.gson.Gson;

import cn.beans.EPicture;
import cn.beans.Picture;
import cn.util.PropertyUtils;

/**
 * @author chenen
 *
 */

/**
 * @author chenen
 *
 */
public class PictureService {
	
	private Logger log=Logger.getLogger(this.getClass());
	private List<String> picDirList=new ArrayList<String>();
	
	//获取文件列表
	public  List<String> getTestPics()
	{
		List<String> picDirList=new ArrayList<String>();
		SmbFile f=null;
		try {
			f = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/small/dpimage/01浦东/05临港新城/01中国航海博物馆/");
			SmbFile[] fs=f.listFiles();
			for(SmbFile sf:fs)
			{
				if(!sf.getPath().contains(".jpg."))
				{
				picDirList.add(sf.getPath().substring(sf.getPath().indexOf("dpimage")));
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return picDirList;
	}
	
	//图片处理
	public void transPictures()
	{
		 SmbFile root = null;
			
		   try {
			   root = new SmbFile(new PropertyUtils().getProp("picfileroot"));
			   if(root.exists())
			   {
				   //生成索引
				   getPicIndex(root);
			   }
			   if(picIndex.size()>0)
			   {
				   log.info("开始循环处理picIndex,其大小为："+picIndex.size());
				   for(int i=0;i<picIndex.size();i++)
				   {
					   if(!picIndex.get(i).getPath().equals("smb://admin:nic123321@192.168.2.249/photo/dpimage/招投标/红砖文化旅游资源数据库/博物馆旅游资源数据库/05长宁-东华大学纺织服饰博物馆/DP-DHDXFZFS-8.jpg"))
					   {
						  continue; 
					   }
					   
					   try
					   {
						  
						   picMainProcecorNas(picIndex.get(i));
						   
					   }catch(Exception e)
					   {
						   
						   log.error("处理异常："+picIndex.get(i).getPath()+e.getMessage());
						   picIndex.set(i, null);
					   }
					   picIndex.set(i, null);
				   }
			   }
		   
		
	}catch(Exception e)
	{
		 log.info("处理错误："+e.getMessage());
	}
		 log.info("完成："+picIndex.size());
	}
	public void intoTree()
	{
		try {
			SmbFile	root = new SmbFile(new PropertyUtils().getProp("treefileroot"));
			showAllSmbFiles(root);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	//遍历NAS图片存储
	private void showAllSmbFiles(SmbFile sb) 
	{
		try {
		SmbFile[] smbfs=sb.listFiles();
		for(int i=0;i<smbfs.length;i++)
		{
			if(smbfs[i].isDirectory())
			{
				intoTree(smbfs[i]);
				showAllSmbFiles(smbfs[i]);
			}
			else
			{
				
				
				
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Tree插入
	private void intoTree(SmbFile s)
	{
		String path=s.getPath().substring(0,s.getPath().length()-1);
		String pn=path.substring(path.indexOf("dpimage"),path.lastIndexOf('/')+1);
		String n=path.substring(path.lastIndexOf('/')+1);
         StaticClient.getDbService().insertTree(pn, n);
		
	}
	//处理主函数
	public void picMainProcecorNas(SmbFile f) throws ClientProtocolException, UnsupportedOperationException, IOException//图片处理主函数
	{
	
		String path=f.getPath();
		path=path.substring(path.indexOf("dpimage")+8);
		//if(path.lastIndexOf('.')<0||path.indexOf(" ")<0)
		if(path.lastIndexOf('.')<0)
		{
			return;//判断是否为可出来图片格式
		}
		String suffix=path.substring(path.lastIndexOf('.')).toLowerCase();
		if(suffix.equals(".jpg")||suffix.equals(".tif")||suffix.equals(".tiff"))
		{
			//图片处理存储
			BufferedImage bi=pic2Nas(f);	
			if(bi==null)
			{
				return;
			}
			//相似图索引
			pic2Lire(bi,path);
			//描述索引
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
			//数据库索引
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
//		if(sf!=null&&sf.exists()&&sf.getPath().indexOf(" ")>0)
//		{
//			return;
//		}
		BufferedImage bi=null;
		 InputStream is=null;
		  try {
				if (sf!=null&&sf.exists()) { //正常图片使用
				//if (sf!=null&&sf.exists()&&sf.getPath().indexOf(" ")>0) {   //空格图片使用
					 HttpClient httpClient=new DefaultHttpClient();
							 String str=sf.getPath().substring(sf.getPath().indexOf("dpimage")); 
							
							 //检测是否存在
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
							 //正常部分
//							    HttpGet httpget=null;
//							    httpget= new HttpGet("http://192.168.2.249/www/"+str);
//					            HttpResponse response = httpClient.execute(httpget);
//					            
//					            HttpEntity entity = response.getEntity();
					        
					           
								try {
									is = sf.getInputStream();
								} catch (Exception e) {
									is.close();
									is=null;
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					            
								try {
									bi = ImageIO.read(is);
								} catch (Exception e) {
									is.close();
									bi=null;
									is=null;
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
					            createSbmFromUrl(str,"small",f1,bi);
					            createSbmFromUrl(str,"middle",f2,bi);
					            createSbmFromUrl(str,"big",f3,bi);
					            is.close();
						
					
				    }
			} catch (SmbException e) {
				is.close();
				bi=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				is.close();
				bi=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		  is.close();
		  return bi;
	}
	
	private boolean deleteBrokenFile(String str)
	{
		boolean hasunbroken=false;
		try{
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
				hasunbroken=true;
			 //都有就不咋地了
			}
		 }
		}catch(Exception e)
		{
			log.info("deleteBrokenFile异常："+str);
		}
		return hasunbroken;
	}
	private void pic2Lire(BufferedImage bi,String path)
	{
		String indexPath="/usr/local/cn/lireindex";

	      // Creating a CEDD document builder and indexing all files.
	      GlobalDocumentBuilder globalDocumentBuilder = StaticClient.getGDB();
	   
	      // Creating an Lucene IndexWriter
	      IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
	      IndexWriter iw=null;
		try {
			iw = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), conf);
		} catch (IOException e1) {
		
			e1.printStackTrace();
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
	      try {
			iw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//存储图片描述
	public String pic2Elastic(EPicture ep)
	{
		Gson jsonConverter=new Gson();
		Client client=StaticClient.getClient();
		 String fileid=getId();
			
			client.prepareIndex("dp2", "picture")
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
		 OutputStream ops=null;
		try {
			f0 = new SmbFile(url0);
			if(!f0.isDirectory())//判断目录是否存在
			 {
			 f0.mkdirs();
			 }
				 f1.createNewFile();
				 ops= new  BufferedOutputStream(f1.getOutputStream());
				 if(!type.equals("small"))
				 {
					 BufferedImage waterbi=null;
					 if(type.equals("middle"))
					 {
						 waterbi=StaticClient.getWaterMake();
					 }
					 else
					 {
						 waterbi=StaticClient.getWaterMakeB();
					 }
				 if(bi.getWidth()>bi.getHeight())
				 {
					 Thumbnails.of(bi)
						.allowOverwrite(true)
						.watermark(waterbi, 0.1f)
						.height(caculator(bi.getWidth(),bi.getHeight(),type))
						.outputFormat("jpg")
						.toOutputStream(ops);
				 }
				 else
				 {
				 Thumbnails.of(bi)
					.allowOverwrite(true)
					.watermark(waterbi, 0.1f)
					.width(caculator(bi.getWidth(),bi.getHeight(),type))
					.outputFormat("jpg")
					.toOutputStream(ops);
				 }
				 }
				 else
				 {
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
				 }
				 ops.close();
			 
		} catch (MalformedURLException e) {
			 try {
				ops.close();
			} catch (IOException e1) {
				log.info("关闭ops异常");
			}
			log.info(e.getMessage());
		}
		 catch (SmbException e) {
			 try {
				ops.close();
			} catch (IOException e1) {
				log.info("关闭ops异常");
			}
			 log.info(e.getMessage());
		}catch (IOException e) {
			 try {
				ops.close();
			} catch (IOException e1) {
				log.info("关闭ops异常");
			}
			log.info(e.getMessage());
			
		}
		try {
			ops.close();
		} catch (IOException e) {
			log.info("关闭ops异常");
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
	private List<SmbFile> picIndex=new ArrayList<SmbFile>();//图片列表
	private void  getPicIndex(SmbFile sb)
	{
		try {
			SmbFile[] smbfs=sb.listFiles();
			for(int i=0;i<smbfs.length;i++)
			{
				if(smbfs[i].isDirectory())
				{
					getPicIndex(smbfs[i]);
				}
				else
				{
					
						String suffix=smbfs[i].getPath().substring(smbfs[i].getPath().lastIndexOf('.'));
						if(suffix.toLowerCase().equals(".jpg"))
						{
							picIndex.add(smbfs[i]);
							//System.out.println(picIndex.size());
						}
						if(suffix.toLowerCase().equals(".tif"))
						{
							picIndex.add(smbfs[i]);
							//System.out.println(picIndex.size());
						}
						if(suffix.toLowerCase().equals(".tiff"))
						{
							picIndex.add(smbfs[i]);
							//System.out.println(picIndex.size());
						}
					
				}
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	}
	
	//提取未处理图片文件目录
	private void showAllFiles(File dir) throws Exception{
		  File[] fs = dir.listFiles();
		  for(int i=0; i<fs.length; i++){
		  // System.out.println(fs[i].getAbsolutePath());
		   if(fs[i].isDirectory())
		   {
			   if(havePic(fs[i]))
			   {
				   System.out.println(fs[i].getAbsolutePath());
				   if(StaticClient.getDbService().countPicDir(fs[i].getAbsolutePath())<1)
				   {picDirList.add(fs[i].getAbsolutePath().substring(3));}
				   
				   showAllFiles(fs[i]);
			   }
			   else
			   {
				   showAllFiles(fs[i]);
			   }
			   
		   }
		  
		  }
	}
	
	//检查子目录是否存在图片格式为
	private boolean havePic(File filedir)
	{
		boolean havepic=false;
		File[] subfs = filedir.listFiles();
		for(File file:subfs)
		{
			if(file.isFile()&&file.getName().lastIndexOf('.')>0)
			{
				String suffix=file.getName().substring(file.getName().lastIndexOf('.'));
				if(suffix.toLowerCase().equals(".jpg")||suffix.toLowerCase().equals(".tif")||suffix.toLowerCase().equals(".tiff"))
				{
					return true;
				}
			}
		}
		return havepic;
		
	}
	/** *//**文件重命名 
	    * @param path 文件目录 
	    * @param oldname  原来的文件名 
	    * @param newname 新文件名 
	    */ 
	       public void renameFile(String path,String oldname,String newname){ 
	        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
	            File oldfile=new File(path+"/"+oldname); 
	            File newfile=new File(path+"/"+newname); 
	            if(!oldfile.exists()){
	                return;//重命名文件不存在
	            }
	            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	            {
	                System.out.println(newname+"已经存在！"); 
	            }
	            else{ 
	                oldfile.renameTo(newfile); 
	            } 
	        }else{
	           // System.out.println("新文件名和旧文件名相同...");
	        }
	    }

}
