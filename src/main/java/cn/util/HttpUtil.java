package cn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {
	
	  /**  
     * 通过HttpServletRequest返回IP地址  
     * @param request HttpServletRequest  
     * @return ip String  
     * @throws Exception  
     */    
    public static String getIpAddr(HttpServletRequest request) throws Exception {    
        String ip = request.getHeader("x-forwarded-for");    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("Proxy-Client-IP");    
        }    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("WL-Proxy-Client-IP");    
        }    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_CLIENT_IP");    
        }    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");    
        }    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getRemoteAddr();    
        }    
        return ip;    
    }   
    
    public static String transIp(String ips)
    {
//    	String name="未知";
//    	switch(ips)
//    	{
//    	case "192.168.2.117":
//    		name="admin";
//    		break;
//    	case "192.168.2.210":
//    		name="付莉莉";
//    		break;
//    	case "192.168.2.127":
//    		name="熊楚天";
//    		break;
//    	case "192.168.2.199":
//    		name="胡雪迪";
//    		break;
//    	}
//    	return name;
    	return ips;
    }
      

      
}  

