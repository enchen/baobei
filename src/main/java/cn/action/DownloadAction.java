/**
 * 
 */
package cn.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import cn.beans.User;
import cn.services.RecordThread;
import cn.services.SecurityService;
import cn.util.PropertyUtils;



/**
 * @author chenen
 *
 */
public class DownloadAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7418513129560138311L;
	private Logger log=Logger.getLogger(this.getClass());
	private String token;
	private InputStream imageStream;
	private String docpath;
	private String fileName;
	private String inputName;
    @Action(value="down", results = {@Result(name = "success",type="stream", params={"contentType","application/octet-stream;charset=UTF-8",
    		"contentDisposition",
      "attachment;filename=\"${fileName}\"","inputName","imageStream","bufferSize","2048"})})
    public String securityCodeImageAction ()
    {
    	User user=SecurityService.getUser(token);
		if(user==null)
		{
			try {
				outPrint("请登录");
			} catch (IOException e) {
			}
			return "";
		}
         try {
//        	 log.info("request编码方式"+getRequest().getCharacterEncoding());
        	 
        	 //log.info("收到的完整路径："+new PropertyUtils().getProp("nginxdoc")+docpath);
        	 docpath=java.net.URLDecoder.decode (docpath,"UTF-8").replaceAll("sb250", "&");
        	 fileName=Calendar.getInstance().getTimeInMillis()+docpath.substring(docpath.lastIndexOf('.'));
        	// log.info(new PropertyUtils().getProp("nginxdoc")+docpath);
			File nf=new File(new PropertyUtils().getProp("nginxdoc")+docpath);
			log.info(new PropertyUtils().getProp("nginxdoc")+docpath);
			log.info(nf.exists());
			imageStream= new FileInputStream(nf);
			Thread t1=new Thread(new RecordThread(user.getUsername(),"down",docpath));
			t1.start();
//			if(imageStream!=null)
//			{
//				log.info("存在");
//			}
		} catch (FileNotFoundException e) {
			File nf1=new File("/usr/local/cn/docover/"+docpath);
			log.info("/usr/local/cn/docover/"+docpath);
			log.info("over"+nf1.exists());
			log.info("找不到文件异常"+e.getMessage());
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			log.info("编码异常："+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
         return SUCCESS;
    }
	public InputStream getImageStream() {
		return imageStream;
	}
	public void setImageStream(InputStream imageStream) {
		
		this.imageStream = imageStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName)
	{
			this.fileName = fileName;
			
	}
	public String getDocpath() {
		return docpath;
	}
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getInputName() {
		return inputName;
	}
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}
}
