/**
 * 
 */
package cn.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.semanticmetadata.lire.builders.DocumentBuilder;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.elasticsearch.action.delete.DeleteResponse;

import cn.beans.EPicture;
import cn.beans.User;
import cn.services.ElasticServices;
import cn.services.PictureService;
import cn.services.RecordThread;
import cn.services.SecurityService;
import cn.services.StaticClient;
import cn.util.PropertyUtils;

/**
 * @author chenen
 *
 */
public class PictureAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 195255921781397955L;
	private Logger log=Logger.getLogger(this.getClass());
	private String driver="F:\\";
	private String rootPath="F:\\Lire-0.9.5\\demo\\pics";
	//根据顶级目录获取目录列表

	@Action("testpic")
	public void testpic()
	{
		try {
			outPrintJson(new PictureService().getTestPics());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Action("tree")
	public void tree()
	{
		new PictureService().intoTree();
		
	}
	//快速获取目录
	@Action("getpicdirs")
	public void getpicdirs()
	{
		User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("0000");
			} catch (IOException e) {
			}
			return;
		}
		
		try {
			outPrintJson(StaticClient.getDbService().getdir(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Action("getelasticpic")
	public void getelasticpic()
	{
		User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("0000");
			} catch (IOException e) {
			}
			return;
		}
		Thread t1=new Thread(new RecordThread(user.getUsername(),"searchpic",pname));
		t1.start();
		try {
			outPrintJson(ElasticServices.queryPicByMatch(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//获取子节点
	private String pname;//父节点路径名称
	@Action("getboys")
	public void getboys()
	{
		try {
			outPrintJson(StaticClient.getDbService().getTreeBoy(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Action("gettreepic")
	public void gettreepic()
	{
		try {
			outPrintJson(StaticClient.getDbService().getTreePic2(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//获取图片描述列表
	@Action("getdescribles")
	public void getDescribles()
	{
		try {
			outPrintJson(StaticClient.getDbService().getpicDescribles(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String token;
	private String describle;
	private String pwidth;
	private String pheight;
	//添加图片描述
	@Action("newdescribles")
	public void newDescribles()
	{
		User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("0000");
			} catch (IOException e) {
			}
			return;
		}
		Thread t1=new Thread(new RecordThread(user.getUsername(),"despic",describle+"@8@"+pname));
		t1.start();
			//StaticClient.getDbService().insertpicDescribles(pname,describle);
			EPicture ep=new EPicture();
			ep.setDescrible(describle);
			ep.setPicpath(pname);
			ep.setHeight(pheight);
			ep.setWidth(pwidth);
			String elasid=new PictureService().pic2Elastic(ep);
			StaticClient.getDbService().insertpicDescribles(user.getUsername(),pname,describle,elasid);
	}
	//删除招投标部分内容
	@Action("removeztb")
	public void removeztb()
	{
		List<EPicture> epl=StaticClient.getDbService().remoZTB();
		log.info("开始删除，总计："+epl.size());
//		List<EPicture> epl=new ArrayList<EPicture>();
//		EPicture ep1=new EPicture();
//		EPicture ep2=new EPicture();
//		EPicture ep3=new EPicture();
//		EPicture ep4=new EPicture();
//		EPicture ep5=new EPicture();
//		EPicture ep6=new EPicture();
//		
//		ep1.setPicpath("招投标/2015hiyito应标数据库/红砖文化自有旅游资源数据库/04游在上海/上海生态休闲之旅/美兰湖/DP-MLH-5.jpg");
//		ep2.setPicpath("招投标/2015hiyito应标数据库/黄浦江沿岸旅游资源/12下游沿岸-宝山段/美兰湖/DP-MLH-5.jpg");
//		ep3.setPicpath("招投标/红砖文化旅游资源数据库/红砖文化自有旅游资源数据库/04游在上海/上海生态休闲之旅/美兰湖/DP-MLH-5.jpg");
//		ep4.setPicpath("招投标/2015招投标原图/黄浦江沿岸旅游资源/12下游沿岸-宝山段/美兰湖/DP-MLH-5.jpg");
//		ep5.setPicpath("招投标/2015招投标原图/黄浦江沿岸旅游资源/12下游沿岸-宝山段/美兰湖/DP-MLH-1.jpg");
//		ep6.setPicpath("招投标/红砖文化旅游资源数据库/红砖文化自有旅游资源数据库/03行在上海/地铁2号线之旅/上海科技馆/DP-KJG-5.jpg");
//		epl.add(ep6);
//		epl.add(ep1);
//		epl.add(ep2);
//		epl.add(ep3);
//		epl.add(ep4);
//		epl.add(ep5);
//		epl.add(ep6);
		int i=0;
		 
		for(EPicture pic:epl)
		{
			log.info(i);
			i++;
			//图像引擎删除
			
			log.info(DocumentBuilder.FIELD_NAME_IDENTIFIER+pic.getPicpath());
			  Term tt=new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,pic.getPicpath());
			try {
				IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
				 IndexWriter iw = new IndexWriter(FSDirectory.open(Paths.get("/usr/local/cn/lireindex")), conf);
				iw.deleteDocuments(tt);
				iw.close();
				
			} catch (IOException e) {
				
				log.info(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Elastic删除
		StaticClient.getClient().prepareDelete("dp2", "picture", pic.getDescrible()).get();
		}
	}
	@Action("getmoreinfo")
	public void getmoreinfo()
	{
		try {
			outPrintJson(StaticClient.getDbService().getmorepicinfo(pname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//下载图片
	@Action("downpic")
	public void securityCodeImageAction ()
	    {
		User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("0000");
			} catch (IOException e) {
			}
			
		}
		else
		{
			try {
				outPrint("ok");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	     
	        	 try {
	        		 log.info(pname);
					pname=java.net.URLDecoder.decode (pname,"UTF-8").replaceAll("sb250", "&");
					log.info(pname);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	        	
		Thread t1=new Thread(new RecordThread(user.getUsername(),"bigpic",pname));
		t1.start();
	        
	    }
		
	@Action("procpic")
	public void procpic()
	{
		PictureService ps=new PictureService();
		ps.transPictures();
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getDescrible() {
		return describle;
	}
	public void setDescrible(String describle) {
		this.describle = describle;
	}
	public String getPwidth() {
		return pwidth;
	}
	public void setPwidth(String pwidth) {
		this.pwidth = pwidth;
	}
	public String getPheight() {
		return pheight;
	}
	public void setPheight(String pheight) {
		this.pheight = pheight;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	




}
