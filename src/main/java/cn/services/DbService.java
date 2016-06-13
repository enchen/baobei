/**
 * 
 */
package cn.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.beans.ArticleIndex;
import cn.beans.EPicture;
import cn.beans.Pass7;
import cn.beans.Pass7Area;
import cn.beans.Picture;
import cn.beans.PictureSub;
import cn.beans.Tree;
import cn.beans.User;
import cn.dao.BaseDaoI;
import cn.util.DateUtil;
import cn.util.HttpUtil;

/**
 * @author chenen
 *
 */
@Service
public class DbService {
	private Logger log=Logger.getLogger(this.getClass());
	private BaseDaoI<ArticleIndex> atDao;
	//保存索引
	@Transactional
	public  void insertFile(ArticleIndex ati)
	{
		try{
		atDao.save(ati);
		}catch(Exception e)
		{
			log.error(e.getMessage());
		}

	}
	//根据树节点获取图片目录
	@Transactional
	public  List<String> getTreePic(String pname)
	{
		List<String> pics=new ArrayList<String>();
		List<Map<String, Object>> re=atDao.findForJdbc("select picpath from pictures where rootpath=?",pname);
		for(Map<String, Object> map:re)
		{
			pics.add(map.get("picpath").toString());
		}
		return pics;
		
	}
	//删除招投标相关部分
	@Transactional
	public  List<EPicture> remoZTB()
	{
		List<EPicture> epl=new ArrayList<EPicture>();
		List<Map<String, Object>> re=atDao.findForJdbc("SELECT pid,picpath from pictures where picpath like '招投标%' ");
		
		for(Map<String, Object> map:re)
		{
			EPicture pic=new EPicture();
			pic.setDescrible(map.get("pid").toString());
			pic.setPicpath(map.get("picpath").toString());	
			epl.add(pic);
		}
		return epl;
		
	}
	//相识引擎获取更多图片细节
	@Transactional
	public  PictureSub getmorepicinfo(String pname)
	{
		
		List<Map<String, Object>> re=atDao.findForJdbc("select width,height from pictures where picpath=?",pname);
		PictureSub pic=new PictureSub();
		for(Map<String, Object> map:re)
		{
			pic.setHeight(map.get("height").toString());
			pic.setWidth(map.get("width").toString());	
		}
		return pic;
		
	}
	//返回包装对象
	@Transactional
	public  List<PictureSub> getTreePic2(String pname)
	{
		List<PictureSub> pics=new ArrayList<PictureSub>();
		List<Map<String, Object>> re=atDao.findForJdbc("select picpath,width,height from pictures where rootpath=?",pname);
		for(Map<String, Object> map:re)
		{
			PictureSub pic=new PictureSub();
			pic.setPicpath(map.get("picpath").toString());
			pic.setHeight(map.get("height").toString());
			pic.setWidth(map.get("width").toString());
			pics.add(pic);
		}
		return pics;
		
	}
	
	@Transactional
	public List<String>getpicDescribles(String pname)
	{
		List<String> res=new ArrayList<String>();
		List<Map<String,Object>> re=atDao.findForJdbc("select distinct describle from picdescrible where picpath=?",pname);
		for(Map<String, Object> map:re)
		{
			
			
			res.add(map.get("describle").toString());
			
			
		}
		return res;
	}
	//插入新描述
	@Transactional
	public void insertpicDescribles(String uname,String pname,String describle,String elasid )
	{
	    String time=DateUtil.getTimeMillis();	
		atDao.executeSql("insert into  picdescrible(updater,picpath,describle,updatetime,elasticid) values(?,?,?,?,?)", uname,pname,describle,time,elasid);
		
		
	}
	@Transactional
	public User checkUser(String username,String userpass)
	{
		String sql="SELECT a.rname,b.useword,b.usepic,b.isadmin from users a,roles b where  a.urole=b.rname and a.avalible='1' and  a.uname=? and a.upass=? ";
		List<Map<String, Object>> re=atDao.findForJdbc(sql, username,userpass);
		if(re.size()<1)
		{
			return null;
		}
		User user=new User();
		for(Map<String, Object> map:re)
		{
			if(map.get("useword").toString().equals("1"))
			{
				user.setWzread(true);
			}
			if(map.get("usepic").toString().equals("1"))
			{
				user.setTpread(true);
			}
			if(map.get("isadmin").toString().equals("1"))
			{
				user.setAdmin(true);
			}
			user.setUsername(map.get("rname").toString());
			
		}
		return user;
		
	}
	//文字饼
	@Transactional
	public Pass7Area getPerStatistics(String type,String st,String et)
	{
		Pass7Area p7a=new Pass7Area();
		String sql="SELECT visitor,count(optype) from operate where optype=? and optime BETWEEN ? and ? GROUP BY visitor ORDER BY visitor";
		List<Map<String, Object>> re=atDao.findForJdbc(sql, type,st,et);
		String [] ips=new String[re.size()==0?1:re.size()];
		int [] counts=new int[re.size()==0?1:re.size()];
		if(re.size()==0)
		{
			ips[0]="0";
			counts[0]=0;
		}
		int i=0;
		for(Map<String, Object> map:re)
		{
			ips[i]=HttpUtil.transIp(map.get("visitor").toString());
			counts[i]=Integer.parseInt(map.get("count").toString());
			i++;
		}
		p7a.setCounts(counts);
		p7a.setIps(ips);
		return p7a;
	}
	//图片饼抄袭
		@Transactional
		public Pass7Area picgetPerStatistics(String type,String st,String et)
		{
			Pass7Area p7a=new Pass7Area();
			String sql="SELECT visitor,count(optype) from operate where optype=? and optime BETWEEN ? and ? GROUP BY visitor ORDER BY visitor";
			List<Map<String, Object>> re=atDao.findForJdbc(sql, type,st,et);
			String [] ips=new String[re.size()==0?1:re.size()];
			int [] counts=new int[re.size()==0?1:re.size()];
			if(re.size()==0)
			{
				ips[0]="0";
				counts[0]=0;
			}
			int i=0;
			for(Map<String, Object> map:re)
			{
				ips[i]=HttpUtil.transIp(map.get("visitor").toString());
				counts[i]=Integer.parseInt(map.get("count").toString());
				i++;
			}
			p7a.setCounts(counts);
			p7a.setIps(ips);
			return p7a;
		}
	@Transactional
	public void insertPic(Picture p)
	{
		atDao.executeSql("insert into pictures(pid,height,width,picpath,describle) values(?,?,?,?,?)", p.getPid(),p.getHeight(),p.getWidth(),p.getPicpath(),p.getDescrible());
	}
	//Tree树插入
	@Transactional
	public void insertTree(String pn,String n)
	{
		atDao.executeSql("insert into tree(pname,name) values(?,?)",pn,n);
	}
	@Transactional
	public List<String> getdir(String pname)
	{
		List<String> tree=new ArrayList<String>();
		List<Map<String,Object>> re=atDao.findForJdbc("SELECT substring(pname,9)||name pp from tree where name like '%"+pname+"%'");
		for(Map<String, Object> map:re)
		{
			
			tree.add(map.get("pp").toString());
			
		}
		return tree;
	}
	@Transactional
	public int updatepass(String username,String oldpass,String newpass)
	{
		
		int ddd=atDao.executeSql("update users set upass=? where uname=? and upass=?",newpass,username,oldpass);
		return ddd;
	}
	@Transactional
	public List<Tree> getTreeBoy(String pn)
	{
		List<Tree> boys=new ArrayList<Tree>();
		List<Map<String,Object>> re=atDao.findForJdbc("select * from tree where pname=?",pn);
		for(Map<String, Object> map:re)
		{
			Tree tree=new Tree();
			
			tree.setName(map.get("name").toString());
			tree.setPname(map.get("pname").toString()+map.get("name").toString()+"/");
			boys.add(tree);
		}
		return boys;
	}
	//文字系统统计信息
	@Transactional
	public Pass7 getStatistics(String std,String etd)
	{
		//开始日期
		//Date sdata=DateUtil.StringToDate("yyyy-MM-dd", std);
		Calendar fc=Calendar.getInstance();
		Calendar fc1=Calendar.getInstance();
		fc.setTime(DateUtil.StringToDate("yyyy-MM-dd", std));
		fc1.setTime(DateUtil.StringToDate("yyyy-MM-dd", etd));
		long l=(fc.getTimeInMillis()-fc1.getTimeInMillis())/(1000*60*60*24);
		int days=(int)l;
		Pass7 p7=new Pass7();
	
	
		//int c=atDao.countByJdbc("select count(*) from articleindex where filename=? and path=?", docname,fpath);
		List<Map<String, Object>> re=atDao.findForJdbc("SELECT optime,optype, count(optype) from (SELECT * from operate where optime BETWEEN ? and ? and optype  in('query','all','down')) aa GROUP BY optype,optime ORDER BY optime,optype"
				, std,etd);
		
		//初始化统计矩阵
		int[][] st=new int[3][Math.abs(days)+1];
		for(int i=0;i<Math.abs(days)+1;i++)
		{
			st[0][i]=0;
			st[1][i]=0;
			st[2][i]=0;
		}
		
		//初始化日期坐标
		String[] dayc=new String[Math.abs(days)+1];
		for(int i=0;i<Math.abs(days)+1;i++)
		{
			
			dayc[i]=DateUtil.DateToStr("yyyy-MM-dd",fc.getTime());
			fc.add(Calendar.DAY_OF_MONTH, 1);
		}
		//初始化类型坐标
		String[] ty=new String[]{"查询量","阅读量","下载量"};
		
		
		String dayChange="";
	
		for(Map<String, Object> map:re)
		{
			int index=0;	
			dayChange=map.get("optime").toString();
			for(int i=0;i<dayc.length;i++)//查询当前日期是第几个
			{
				if(dayChange.endsWith(dayc[i]))
				{
					index=i;
				}
			}
			if(map.get("optype").equals("query"))
			{
				st[0][index]=Integer.parseInt(map.get("count").toString());
			}
			if(map.get("optype").equals("all"))
			{
				st[1][index]=Integer.parseInt(map.get("count").toString());
			}
			if(map.get("optype").equals("down"))
			{
				st[2][index]=Integer.parseInt(map.get("count").toString());
			}
			
		}
		p7.setCounts(st);
		
		
		p7.setDays(dayc);
		
		p7.setType(ty);
		return p7;
	}
	//图片系统统计抄袭
	@Transactional
	public Pass7 picgetStatistics(String std,String etd)
	{
		//开始日期
		//Date sdata=DateUtil.StringToDate("yyyy-MM-dd", std);
		Calendar fc=Calendar.getInstance();
		Calendar fc1=Calendar.getInstance();
		fc.setTime(DateUtil.StringToDate("yyyy-MM-dd", std));
		fc1.setTime(DateUtil.StringToDate("yyyy-MM-dd", etd));
		long l=(fc.getTimeInMillis()-fc1.getTimeInMillis())/(1000*60*60*24);
		int days=(int)l;
		Pass7 p7=new Pass7();
	
	
		//int c=atDao.countByJdbc("select count(*) from articleindex where filename=? and path=?", docname,fpath);
		String sql="SELECT optime,optype, count(optype) from (SELECT * from operate where optime BETWEEN ? and ? and optype not in('query','all','down')) aa GROUP BY optype,optime ORDER BY optime,optype";
		List<Map<String, Object>> re=atDao.findForJdbc(sql, std,etd);
		
		//初始化统计矩阵
		int[][] st=new int[3][Math.abs(days)+1];
		for(int i=0;i<Math.abs(days)+1;i++)
		{
			st[0][i]=0;
			st[1][i]=0;
			st[2][i]=0;
		}
		
		//初始化日期坐标
		String[] dayc=new String[Math.abs(days)+1];
		for(int i=0;i<Math.abs(days)+1;i++)
		{
			
			dayc[i]=DateUtil.DateToStr("yyyy-MM-dd",fc.getTime());
			fc.add(Calendar.DAY_OF_MONTH, 1);
		}
		//初始化类型坐标
		String[] ty=new String[]{"描述量","原图量","查询量"};
		
		
		String dayChange="";
	
		for(Map<String, Object> map:re)
		{
			int index=0;	
			dayChange=map.get("optime").toString();
			for(int i=0;i<dayc.length;i++)//查询当前日期是第几个
			{
				if(dayChange.endsWith(dayc[i]))
				{
					index=i;
				}
			}
			if(map.get("optype").equals("despic"))
			{
				st[0][index]=Integer.parseInt(map.get("count").toString());
			}
			if(map.get("optype").equals("bigpic"))
			{
				st[1][index]=Integer.parseInt(map.get("count").toString());
			}
			if(map.get("optype").equals("searchpic"))
			{
				st[2][index]=Integer.parseInt(map.get("count").toString());
			}
			
		}
		p7.setCounts(st);
		
		
		p7.setDays(dayc);
		
		p7.setType(ty);
		return p7;
	}
	@Transactional
	public int countfile(String fpath,String docname)
	{
		
		int c=atDao.countByJdbc("select count(*) from articleindex where filename=? and path=?", docname,fpath);
		return c;
	}
	
	//检查文件目录是否已经修改完毕
	@Transactional
	public int countPicDir(String ppath)
	{
		
		int c=atDao.countByJdbc("select count(*) from picdirs where picdir=?",ppath);
		return c;
	}
	
	@Transactional
	public List<ArticleIndex> getDetails(String filename)
	{
	
		return 	atDao.find("from ArticleIndex where filename=?", filename);
	}
	
	@Transactional
	public void insertRecord(String visitor,String optype,String keyword,String optime)
	{
	
		atDao.executeSql("insert into operate(visitor,optype,keyword,optime) values(?,?,?,?)", visitor,optype,keyword,optime);
	}

	public BaseDaoI<ArticleIndex> getAtDao() {
		return atDao;
	}
	@Autowired
	public void setAtDao(BaseDaoI<ArticleIndex> atDao) {
		this.atDao = atDao;
	}

}
