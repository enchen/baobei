/**
 * 
 */
package cn.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import cn.services.ProcessThread;

/**
 * @author chenen
 *
 */
public class Choice {

	/**
	 * @param args
	 */
	 private static int tt=0;
	public static void main(String[] args) {
		Date day=StringToDate("yyyy-MM-dd","2016-01-21");
		  Calendar cal = Calendar.getInstance();
	         cal.setTime(day);
		System.out.println(cal.get(Calendar.DAY_OF_WEEK));
		System.out.println(cal.getTime().toLocaleString());
//		System.out.println("2016-01-05".substring(0,"2016-01-05".lastIndexOf('-')+1)+cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		System.out.println(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		int order=cal.get(Calendar.DAY_OF_WEEK)-2;
   	     cal.add(Calendar.DAY_OF_MONTH, -order);
		String fromdate=DateUtil.DateToStr("yyyy-MM-dd",cal.getTime());
		 cal.add(Calendar.DAY_OF_MONTH, 6);
		String todate=DateUtil.DateToStr("yyyy-MM-dd",cal.getTime());
		System.out.println(fromdate+"  "+todate);
		
	}
	
	  static void showAllFiles(File dir) throws Exception{
		  Date day=StringToDate("yyyy-MM-dd","2015-11-03");
		  Calendar cal = Calendar.getInstance();
	         cal.setTime(day);
		cal.getActualMaximum(Calendar.DAY_OF_WEEK);
	}
	  public static Date StringToDate(String format,String date){
			Date d=null;
			try{
				d=new SimpleDateFormat(format).parse(date);
			}catch(Exception e){
				d=null;
			}
			return d;
		}

}
