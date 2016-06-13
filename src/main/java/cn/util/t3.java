/**
 * 
 */
package cn.util;

import java.io.File;

/**
 * @author chenen
 *
 */
public class t3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//寻路算法
		try {
			showAllFiles(new File("F:\\Lire-0.9.5\\demo\\pics"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void showAllFiles(File dir) throws Exception{
		  File[] fs = dir.listFiles();
		  for(int i=0; i<fs.length; i++){
		  // System.out.println(fs[i].getAbsolutePath());
		   if(fs[i].isDirectory())
		   {
			   if(havePic(fs[i]))
			   {
				   System.out.println(fs[i].getAbsolutePath());
				   showAllFiles(fs[i]);
			   }
			   else
			   {
				   showAllFiles(fs[i]);
			   }
			   
		   }
		  
		  }
	}
	
	//检查子目录是否存在图片格式为
	private static boolean havePic(File filedir)
	{
		boolean havepic=false;
		File[] subfs = filedir.listFiles();
		for(File file:subfs)
		{
			if(file.isFile()&&file.getName().lastIndexOf('.')>0)
			{
				String suffix=file.getName().substring(file.getName().lastIndexOf('.'));
				if(suffix.toLowerCase().equals(".jpg")||suffix.toLowerCase().equals(".tif")||suffix.toLowerCase().equals(".tiff"))
				{
					return true;
				}
			}
		}
		return havepic;
		
	}
}
