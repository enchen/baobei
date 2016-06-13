/**
 * 
 */
package cn.services;

import java.util.HashMap;

import cn.beans.User;

/**
 * @author chenen
 *
 */
public class SecurityService {
	
	private static HashMap<String , User> login ;
	public static HashMap<String , User> getLogin(){
		if(login==null){
			login=new HashMap<String , User>();
		}
		return login;
	}
	//用户登录操作
	public static String putUser(User userName)
	{
		String token=getToken();
		getLogin().put(token, userName);
		return token;
	}
	//是否已登陆用户
	public static boolean isUser(String token)
	{
		return (getLogin().get(token)!=null);
	}
	//获取用户名
	public static User getUser(String token)
	{
		return getLogin().get(token);
	}
	//用户退出
	public static  void quitUser(String token)
	{
	   getLogin().remove(token);
	}
	//随机令牌
	public static synchronized  String getToken()
	{
		String token=java.util.UUID.randomUUID().toString().replace("-", "");
	//	String[] arr={email,String.valueOf(System.currentTimeMillis()-7*24*60*60*1000+60*60*1000)};
	//	getTokenService().put(token, arr);
		return token;
	}

}
