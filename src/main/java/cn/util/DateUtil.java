package cn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	/**
	 * 按格式将时间转换成字符
	 * @param format {yyyy-MM-dd,yyyyMMdd,yyyy-MM-dd hh:ss...}
	 * @param date
	 * @return
	 */
	public static String DateToStr(String format,Date date){
		String s="";
		try{
			s=new SimpleDateFormat(format).format(date);
		}catch(Exception e){
			s="";
		}
		return s;
	}
	
	/**
	 * 按格式将字符转换成时间
	 * @param format
	 * @param date
	 * @return
	 */
	public static Date StringToDate(String format,String date){
		Date d=null;
		try{
			d=new SimpleDateFormat(format).parse(date);
		}catch(Exception e){
			d=null;
		}
		return d;
	}
	//根据时间字符串获取其两段的周，月时间起止点
	public static String[] date2both(String datestr,String type)
	{
		Date day=StringToDate("yyyy-MM-dd",datestr);
		  Calendar cal = Calendar.getInstance();
	         cal.setTime(day);
	         String [] both=new String[2];
	         if(type.endsWith("month"))
	         {
	        	 both[0]=datestr.substring(0,datestr.lastIndexOf('-')+1)+(cal.getActualMinimum(Calendar.DAY_OF_MONTH)<10?"0"+cal.getActualMinimum(Calendar.DAY_OF_MONTH):cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	        	 both[1]=datestr.substring(0,datestr.lastIndexOf('-')+1)+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	         }
	         else if (type.endsWith("week"))
	         {
	        	 int order=cal.get(Calendar.DAY_OF_WEEK)-2;
	        	 cal.add(Calendar.DAY_OF_MONTH, -order);
	     		String fromdate=DateUtil.DateToStr("yyyy-MM-dd",cal.getTime());
	     		 cal.add(Calendar.DAY_OF_MONTH, 6);
	     		String todate=DateUtil.DateToStr("yyyy-MM-dd",cal.getTime());
	        	 both[0]=fromdate;
	        	 both[1]=todate;
	         }
	         else if (type.endsWith("day"))
	         {
	        	 both[0]=datestr;
	        	 both[1]=datestr;
	         }
		
		return both;
	}
	
	public static String DateToWeekDay(Date date){
		
		 String[] weekDays = {"SUN","MON", "TUE", "WED", "THU", "FRI", "SAT"};
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);

         int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
         if (w < 0)
             w = 0;

        return weekDays[w];
	}
	
	public static String DateToWeekDayCN(Date date){
		
		 String[] weekDays = {"周日","周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

       return weekDays[w];
	}
	public static String getTimeMillis() 
	{
		return System.currentTimeMillis()+""; 
	}
}
