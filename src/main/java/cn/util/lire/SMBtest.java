/**
 * 
 */
package cn.util.lire;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import cn.services.StaticClient;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * @author chenen
 *
 */
public class SMBtest {

	/**
	 * @param args 测试 java 链接NAS共享文件夹
	 * @throws IOException 
	 */
	private Logger log=Logger.getLogger(this.getClass());
	public static void main(String[] args) throws IOException {
		
//		String url = "smb://[NAS server-IP or hostname]/file-or-directory-path";
//	    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("[company network domain]", "user", "password");
//	    SmbFile dir = new SmbFile(url, auth);
//	    for (SmbFile f : dir.listFiles())
//	    {
//	        System.out.println(f.getName());
//	    }
		System.setProperty("jcifs.smb.client.dfs.disabled", "true");
		 SmbFile f = null;
	
		   try {
		    f = new SmbFile("smb://admin:nic123321@192.168.2.249/photo/dpimage/11宝山/临江公园/BS_LinjiangPark_2011.06.28 097.jpg");
		   
		
	}catch(Exception e)
	{
	}

		     try {
				if (f!=null&&f.exists()&&f.getPath().indexOf(" ")<0) {
					//f.createNewFile();
					//f.copyTo(new SmbFile("smb://admin:nic123321@192.168.2.249/share1/docup/很好"));
					SmbFile[] fs=f.listFiles();
				    //f.delete();
					long flagtime=Calendar.getInstance().getTimeInMillis();
					 HttpClient httpClient=new DefaultHttpClient();
					for(SmbFile sf:fs)
					{
						//Thumbnailator.createThumbnail(arg0, arg1, arg2, arg3, arg4);;
						
						if(sf.getName().endsWith(".jpg")||sf.getName().endsWith(".JPG"))
						{
							 String str=sf.getPath().substring(sf.getPath().indexOf("dpimage")); 
							 flagtime=Calendar.getInstance().getTimeInMillis();
							
							
							 SmbFile f1 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/small/"+str);
							 SmbFile f2 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/middle/"+str);
							 SmbFile f3 = new SmbFile("smb://admin:nic123321@192.168.2.249/web/www/big/"+str);
							
							 if(f1.exists()&&f2.exists()&&f3.exists())
							 {
								 continue;
							 }
							    HttpGet httpget = new HttpGet("http://192.168.2.249/www/"+str);
							  
					            HttpResponse response = httpClient.execute(httpget);
					            HttpEntity entity = response.getEntity();
					            InputStream is = entity.getContent();
					            BufferedImage bi = ImageIO.read(is); 
					            createSbmFromUrl(str,"small",f1,bi);
					            createSbmFromUrl(str,"middle",f2,bi);
					            createSbmFromUrl(str,"big",f3,bi);
					            is.close();
						}
					}
				    }
				else
				{
					String str=f.getPath().substring(f.getPath().indexOf("dpimage"));
					 SmbFile f1 = new SmbFile("smb://admin:nic123321@192.168.2.249/photo/"+str);
					   InputStream is = f1.getInputStream();
			            BufferedImage bi = ImageIO.read(is); 
					 HttpGet httpget = new HttpGet("http://192.168.2.249/www/"+str);
					  HttpClient httpClient=new DefaultHttpClient();
						
			            HttpResponse response = httpClient.execute(httpget);
			            HttpEntity entity = response.getEntity();
//			            InputStream is = f1.getInputStream();
//			            BufferedImage bi = ImageIO.read(is); 
			            createSbmFromUrl(str,"small",f1,bi);
				}
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		     System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
	}
	
	public static void createSbmFromUrl(String path,String type,SmbFile f1,BufferedImage bi)
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
						.watermark(Positions.CENTER, StaticClient.getWaterMake(), 0.7f)
						.height(caculator(bi.getWidth(),bi.getHeight(),type))
						.outputFormat("jpg")
						.toOutputStream(ops);
				 }
				 else
				 {
				 Thumbnails.of(bi)
					.allowOverwrite(true)
					.watermark(Positions.CENTER, StaticClient.getWaterMake(), 0.7f)
					.width(caculator(bi.getWidth(),bi.getHeight(),type))
					.outputFormat("jpg")
					.toOutputStream(ops);
				 }
				 ops.close();
			 
		} catch (MalformedURLException e) {
			//log.info(e.getMessage());
		}
		 catch (SmbException e) {
			// log.info(e.getMessage());
		}catch (IOException e) {
			// log.info(e.getMessage());
		}
	} 
	public static int caculator(int width,int height,String type)
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
