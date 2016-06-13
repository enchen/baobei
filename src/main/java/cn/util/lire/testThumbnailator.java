/**
 * 
 */
package cn.util.lire;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;


/**
 * @author chenen
 *
 */
public class testThumbnailator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		//Thumbnails.
		{//import com.sun.imageio.plugins.tiff.TIFFImageReaderSpi
			File f=new File("D:/Androids/13G7544.tif");
			String path=f.getAbsolutePath();
		BufferedImage bi=ImageIO.read(f);
		  OutputStream ops = new FileOutputStream(new File("D:/Androids/13G7544.tifnew.jpg")) ;
		 Thumbnails.of(f)
			.allowOverwrite(true)
			.height(3840)
			.outputFormat("jpg")
			.toOutputStream(ops);
		}catch(Exception e)
		{
			System.out.print(e.getMessage());
		}

	}

}
