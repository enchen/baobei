/**
 * 
 */
package cn.util;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import cn.beans.Tree;
import cn.services.PictureService;

/**
 * @author chenen
 *
 */
public class mainTest {

	/**
	 * @param args
	 */
	public static int jpg=0;
	public static int tif=0;
	public static int tiff=0;
	public static void main(String[] args) {

//		PictureService ps=new PictureService();
//		ps.transPictures();
//		String path="smb://admin:nic123321@192.168.2.249/photo/dpimage/—浦江—/01浦江游览/2011上海旅游宣传片浦江游览/HP_PJ.Sightseeing.2011-036.jpg";
//		path=path.substring(path.indexOf("dpimage")+8);
//		System.out.print(path);
		//transPictures();
		try {
			SmbFile root = new SmbFile("smb://admin:nic123321@192.168.2.249/photo/dpimage/—浦江—/01浦江游览/");
			SmbFile[] smbfs=null;
			try {
				smbfs = root.listFiles();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<smbfs.length;i++)
			{
				try {
					if(smbfs[i].isDirectory())
					{
String path=smbfs[i].getPath().substring(0,smbfs[i].getPath().length()-1);
String pn=path.substring(path.indexOf("dpimage"),path.lastIndexOf('/')+1);
String n=path.substring(path.lastIndexOf('/')+1);
System.out.println(pn);
System.out.println(n);
					}
				} catch (SmbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		    
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void neiceng(int into,List<Integer> arr)
	{
		for(int i=0;i<arr.size();i++)
		{
			if(into<arr.get(0))//进入的是最小的直接放到第一位
			{
				arr.add(0, into);
				return;
			}
			if(into>arr.get(arr.size()))//进入的是最大的直接放到最后位
			{
				arr.add(into);
				return;
			}
			if(into==arr.get(i))//处理重复
			{
				return;
			}
			if(into>arr.get(i)&&into<arr.get(i+1))//找到中间位置插入
			{
				arr.add(i+1, into);
				return;
			}
		}
	
	}
	static void transPictures()
	{
		 SmbFile root = null;
			
		   try {
			   root = new SmbFile("smb://admin:nic123321@192.168.2.249/photo/dpimage/");
			   if(root.exists())
			   {
				   showAllSmbFiles(root);
				   System.out.println(jpg+"  "+tif+"  "+tiff);
			   }
		   
		
	}catch(Exception e)
	{
	}
	}
	//遍历NAS图片存储
	static void showAllSmbFiles(SmbFile sb) 
	{
		try {
		SmbFile[] smbfs=sb.listFiles();
		for(int i=0;i<smbfs.length;i++)
		{
			if(smbfs[i].isDirectory())
			{
				showAllSmbFiles(smbfs[i]);
			}
			else
			{
				
					String suffix=smbfs[i].getPath().substring(smbfs[i].getPath().lastIndexOf('.'));
					if(suffix.toLowerCase().equals(".jpg"))
					{
						jpg+=1;
						System.out.println("jpg："+jpg);
					}
					if(suffix.toLowerCase().equals(".tif"))
					{
						tif+=1;
						System.out.println("tif："+tif);
					}
					if(suffix.toLowerCase().equals(".tiff"))
					{
						tiff+=1;
						System.out.println("tiff："+tiff);
					}
				
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
