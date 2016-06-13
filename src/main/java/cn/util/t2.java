/**
 * 
 */
package cn.util;

import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.utils.FileUtils;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author chenen
 *
 */
public class t2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	String lirePath="F:\\Lire-0.9.5\\demo\\";
	String indexPath=lirePath+"index";
	String picsPath=lirePath+"pics";
	String reco=lirePath+"test.jpg";
	//存储Index
		
//	  boolean passed = false;
//      if (args.length > 0) {
//          File f = new File(args[0]);
//          System.out.println("Indexing images in " + args[0]);
//          if (f.exists() && f.isDirectory()) passed = true;
//      }
//      if (!passed) {
//          System.out.println("No directory given as first argument.");
//          System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
//          System.exit(1);
//      }
      // Getting all images from a directory and its sub directories.
      ArrayList<String> images = FileUtils.getAllImages(new File(picsPath), true);

      // Creating a CEDD document builder and indexing all files.
      GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);
      // and here we add those features we want to extract in a single run:
      globalDocumentBuilder.addExtractor(FCTH.class);
      globalDocumentBuilder.addExtractor(AutoColorCorrelogram.class);
      // Creating an Lucene IndexWriter
      IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
      IndexWriter iw = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), conf);
      // Iterating through images building the low level features
      for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
          String imageFilePath = it.next();
          System.out.println("Indexing " + imageFilePath);
          try {
              BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
              Document document = globalDocumentBuilder.createDocument(img, imageFilePath);
              
             
              iw.addDocument(document);
              //Query queries=null;
              Term tt=new Term("ImageIdentifier",imageFilePath);
              iw.deleteDocuments(tt);
          } catch (Exception e) {
              System.err.println("Error reading image or indexing it.");
              e.printStackTrace();
          }
      }
      // closing the IndexWriter
      iw.close();
      System.out.println("Finished indexing.");
		

	
	 
	    }
}
