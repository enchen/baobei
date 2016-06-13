/**
 * 
 */
package cn.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import jcifs.smb.SmbFile;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.struts2.convention.annotation.Action;
import org.springframework.beans.factory.annotation.Autowired;

import cn.beans.ArticleIndex;
import cn.beans.EPicture;
import cn.beans.User;
import cn.dao.BaseDaoI;
import cn.services.FileService;
import cn.services.ProcessThread;
import cn.services.RecordThread;
import cn.services.SecurityService;
import cn.services.StaticClient;
import cn.util.PropertyUtils;

/**
 * @author chenen
 *
 */
public class FileUpAction extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4238665285505640449L;
	private Logger log=Logger.getLogger(this.getClass());
	private BaseDaoI<ArticleIndex> atDao;
	@Action("refresh147")
	public void refresh()
	{
		log.info("intomessage");
		try {
			outPrint("action is working");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//ExecutorService pool = Executors.newFixedThreadPool(10);
	
	@Action("upload147")
	public void uploadfiles()
	{
		String encoding = System.getProperty("file.encoding");  //当前系统编码
		//File root = new File("F:\\docup");
		log.info("当前系统编码："+encoding);
		File root=null;
		//SmbFile Smbroot;//太君先歇歇
		try {
			root = new File(new String(new PropertyUtils().getProp("docup").getBytes("UTF-8"),encoding));
//			try {
//				Smbroot =new SmbFile("smb://admin:nic123321@192.168.2.249/share1/docup/");
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		if(root == null)
		{
			log.info("顶级目录获取的文件为空");
			return;
		}
		  try {
			  log.info("调用递归："+root);
			showAllFiles(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
 private void showAllFiles(File dir) throws Exception{
	 //log.info("当前递归文件："+dir.getAbsolutePath());
			  File[] fs = dir.listFiles();
			  for(int i=0; i<fs.length; i++){
			   if(fs[i].isDirectory()){
			    try{
			     showAllFiles(fs[i]);
			    }catch(Exception e)
			    {
			    	log.error(e.getMessage());
			    }
			   }
			   else
			   {
//				   for(int o=0;o<fs.length;o++)
//				   {
//				   log.info("非文件夹进入："+java.net.URLDecoder.decode(fs[o].getAbsolutePath(),"utf-8"));
//				   }
				   try{
					   //读取中文路径文件乱码
				   String filepath=java.net.URLDecoder.decode(fs[i].getAbsolutePath(),"UTF-8");
				   filepath=filepath.replaceAll("\\\\", "/");//正则中\\\\表示\
				   String docname=filepath.substring(filepath.lastIndexOf('/')+1);
				   //+6是什么意思 答 docup的结束位置
					String fpath=filepath.substring(filepath.indexOf("docup/")+6, filepath.lastIndexOf('/')+1);
                   // log.info("进入线程之前@@fpath："+fpath+"全路径："+filepath +"docname:"+docname);
//					ProcessThread pt=new ProcessThread(fpath,docname,filepath);
//					pool.execute(pt);
					FileService fse=new FileService();
					String re=fse.processFiles(fpath,docname,filepath);
					fse=null;
//				   if(re!=null)
//				   {
//					   successup+=1;
//				   }
				   }catch(Exception e)
				   {
					   log.error("fileup80@@"+fs[i].getAbsolutePath()+"@@"+e.getMessage());
				   }
			   }
			  }
		}
 
 //图片对比
 private String token;
    File filedata;
    private String filedataFileName;
	@Action("piccompare")
	public void fileupload()
	{
//		if(!filedataFileName.equals("www"))
//		return;
		User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("0000");
			} catch (IOException e) {
			}
			
		}
		BufferedImage img=null;
		 try {
			img=ImageIO.read(filedata);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 if(img==null)
		 {
			 try {
				outPrint("0");
			} catch (IOException e) {
				e.printStackTrace();
			}
			 return;
		 }
		 IndexReader ir = StaticClient.getIndexReader();
	     ImageSearcher searcher = new GenericFastImageSearcher(200, CEDD.class);
	     ImageSearchHits hits=null;
	     try {
			hits= searcher.search(img, ir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("相似图："+e.getMessage());
			return;
		}
	        // searching with a Lucene document instance ...
//	        ImageSearchHits hits = searcher.search(ir.document(0), ir);
	     List<EPicture> epl=new ArrayList<EPicture>();
	        for (int i = 0; i < hits.length(); i++) {
	            String fileName;
				try {
					fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
					EPicture ep=new EPicture();
					ep.setPicpath(fileName);
					epl.add(ep);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	           
	        }
	        Thread t1=new Thread(new RecordThread(user.getUsername(),"searchpic","looklike"));
			t1.start();
		 try {
			outPrintJson(epl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public File getFiledata() {
		return filedata;
	}
	public void setFiledata(File filedata) {
		this.filedata = filedata;
	}
	public String getFiledataFileName() {
		return filedataFileName;
	}
	public void setFiledataFileName(String filedataFileName) {
		this.filedataFileName = filedataFileName;
	}
	public BaseDaoI<ArticleIndex> getAtDao() {
		return atDao;
	}
	@Autowired
	public void setAtDao(BaseDaoI<ArticleIndex> atDao) {
		this.atDao = atDao;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
