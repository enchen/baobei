/**
 * 
 */
package cn.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.springframework.beans.factory.annotation.Autowired;

import cn.beans.ArticleIndex;
import cn.beans.Pass7;
import cn.beans.User;
import cn.dao.BaseDaoI;
import cn.services.DbService;
import cn.services.ElasticServices;
import cn.services.RecordThread;
import cn.services.SecurityService;
import cn.services.StaticClient;
import cn.util.DateUtil;

/**
 * @author chenen
 *
 */
public class SearchAction extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7846807720364508719L;
	private Logger log=Logger.getLogger(this.getClass());
	private BaseDaoI<ArticleIndex> articleDao;
	private String queryinput;
	private String token;
	//关键字查询
	@Action("resultsearch")
	public void refresh()
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
		//操作记录线程
		Thread t1=new Thread(new RecordThread(user.getUsername(),"query",queryinput));
		t1.start();
		try {
			outPrintJson(ElasticServices.queryByMatch(queryinput));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String passday;
	@Action("pass7")
	public void downloadRecord() throws IOException
	{
		String[] both=DateUtil.date2both(passday, passtype);
		outPrintJson(StaticClient.getDbService().getStatistics(both[0],both[1]));
		
	}
	//图片抄袭
	@Action("picpass7")
	public void picdownloadRecord() throws IOException
	{
		String[] both=DateUtil.date2both(passday, passtype);
		outPrintJson(StaticClient.getDbService().picgetStatistics(both[0],both[1]));
		
	}
	private String passtype;
	private String counttype;
	@Action("pass7area")
	public void pass7Area() throws IOException
	{
		String[] both=DateUtil.date2both(passday, passtype);
		outPrintJson(StaticClient.getDbService().getPerStatistics(counttype, both[0], both[1]));
		
	}
	//图片抄袭
	@Action("picpass7area")
	public void picpass7Area() throws IOException
	{
		String[] both=DateUtil.date2both(passday, passtype);
		outPrintJson(StaticClient.getDbService().picgetPerStatistics(counttype, both[0], both[1]));
		
	}
	//全文
	private String inputpath;
	@Action("resultdetails")
	public void resultdetails()
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
			
			outPrintJson(articleDao.find("from ArticleIndex where filename=? and path=? order by fileid", queryinput,inputpath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread t1=new Thread(new RecordThread(user.getUsername(),"all",queryinput));
		t1.start();
	}
	private String markpath;
	private String markname;
	private String markreason;
	//好像是举报
	@Action("makemark")
	public void makemark()
	{
		try {
			
			articleDao.executeSql("insert INTO mark2clear(markpath,markname,markreason,marktime) values(?,?,?,?)", markpath,markname,markreason,Calendar.getInstance().getTime().toLocaleString());
			outPrint("0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Action("newpass")
	public void newpass()
	{
	
		try {
			outPrint(StaticClient.getDbService().updatepass(username, userpass, newpass)+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String newpass;
	private String username;
	private String userpass;
	@Action("login")
	public void login()
	{
		User user=StaticClient.getDbService().checkUser(username, userpass);
		if(user==null)
		{
			try {
				outPrint("0");
			} catch (IOException e) {
				
			}	
		}
		else
		{
			String newtoken=SecurityService.putUser(user);
			try {
				outPrint(newtoken);
			} catch (IOException e) {
			}
		}
	}
	@Action("islogin")
	public void islogin()
	{
		if(SecurityService.getUser(token)==null)
		{//未登录
			try {
				outPrint("0");
			} catch (IOException e) {
			}
		}
		else
		{//已登陆
			try {
				outPrint("1");
			} catch (IOException e) {
			}
		}
	}
	public String getQueryinput() {
		return queryinput;
	}
	public void setQueryinput(String queryinput) {
		this.queryinput = queryinput;
	}

	public BaseDaoI<ArticleIndex> getArticleDao() {
		return articleDao;
	}
	@Autowired
	public void setArticleDao(BaseDaoI<ArticleIndex> articleDao) {
		this.articleDao = articleDao;
	}
	public String getInputpath() {
		return inputpath;
	}
	public void setInputpath(String inputpath) {
		this.inputpath = inputpath;
	}
	public String getMarkpath() {
		return markpath;
	}
	public void setMarkpath(String markpath) {
		this.markpath = markpath;
	}
	public String getMarkname() {
		return markname;
	}
	public void setMarkname(String markname) {
		this.markname = markname;
	}
	public String getMarkreason() {
		return markreason;
	}
	public void setMarkreason(String markreason) {
		this.markreason = markreason;
	}
	public String getPassday() {
		return passday;
	}
	public void setPassday(String passday) {
		this.passday = passday;
	}
	public String getPasstype() {
		return passtype;
	}
	public void setPasstype(String passtype) {
		this.passtype = passtype;
	}
	public String getCounttype() {
		return counttype;
	}
	public void setCounttype(String counttype) {
		this.counttype = counttype;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getNewpass() {
		return newpass;
	}
	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}


}
