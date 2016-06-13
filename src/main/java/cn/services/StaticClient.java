/**
 * 
 */
package cn.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import cn.util.SpringContext;

/**
 * @author chenen
 *
 */
public class StaticClient {
	 private StaticClient() {}  
	    private static DbService dbService=null;
	    private static Client client=null;  
	    private static  GlobalDocumentBuilder gdb=null;
	    private static BufferedImage bi=null;
	    private static BufferedImage bib=null;
	    private static IndexReader ir=null;
	    //静态工厂方法   
	    public static Client getClient() {  
	         if (client == null) {    
	        	 try {
	     			client= TransportClient.builder().build()
	     			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
	     		} catch (UnknownHostException e) {
	     			// TODO Auto-generated catch block
	     			e.printStackTrace();
	     		}
	         }    
	        return client;  
	    }  
	    
	    public static DbService getDbService() {  
	         if (dbService == null) {    
	        dbService=(DbService)SpringContext.getBean("dbservice");
	         }    
	        return dbService;  
	    } 
	    
	    public static IndexReader getIndexReader() {  
	         if (ir == null) {    
	        	 try {
					ir=DirectoryReader.open(FSDirectory.open(Paths.get("/usr/local/cn/lireindex")));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
	         }    
	        return ir;  
	    } 
	    
	   
	    
	    public static GlobalDocumentBuilder getGDB()
	    {
	    	if(gdb==null)
	    	{
	    		 // Creating a CEDD document builder and indexing all files.
	    		gdb = new GlobalDocumentBuilder(CEDD.class);
			      // and here we add those features we want to extract in a single run:
	    		gdb.addExtractor(FCTH.class);
	    		gdb.addExtractor(AutoColorCorrelogram.class);
	    	}
	    	 return gdb;
	    }
	    
	    public static BufferedImage getWaterMake()
	    {
	    	if(bi==null)
	    	{
	    		try {
					bi=ImageIO.read(new File("/usr/local/cn/watermark/DP.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	return bi;
	    	
	    }
	    public static BufferedImage getWaterMakeB()
	    {
	    	if(bib==null)
	    	{
	    		try {
	    			bib=ImageIO.read(new File("/usr/local/cn/watermark/DPB.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	return bib;
	    	
	    }

}
