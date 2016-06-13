/**
 * 
 */
package cn.services;



import java.util.Calendar;


import javax.servlet.http.HttpServletRequest;

import cn.util.DateUtil;
import cn.util.HttpUtil;


/**
 * @author chenen
 *
 */


//操作记录处理线程
public class RecordThread implements Runnable {
	
	private String visitor;
	private String optype;
	private String keyword;

	
	public RecordThread(String visitor,String optype,String keyword){
		this.visitor=visitor;
		this.optype=optype;
		this.keyword=keyword;
		
	
	}
	public void run(){
		
		DbService dbService=StaticClient.getDbService();
//		String mac="";
//		try {
//			mac=HttpUtil.getIpAddr(visitor);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		dbService.insertRecord(visitor, optype, keyword, DateUtil.DateToStr("yyyy-MM-dd",Calendar.getInstance().getTime()));
	
	}
	
	
}
