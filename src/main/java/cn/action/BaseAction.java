package cn.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;


public class BaseAction extends ActionSupport {	
	private static final long serialVersionUID = 1L;
	
	public Map<String,Object> getSession(){
		return ServletActionContext.getContext().getSession();
	}
	
	public HttpServletRequest getRequest(){
		return ServletActionContext.getRequest();
	}
	
	public HttpServletResponse getResponse(){
		return ServletActionContext.getResponse();
	}

	public void outPrint(String outStr) throws IOException{
		getResponse().setContentType("text/html;charset=utf-8");
		PrintWriter writer=getResponse().getWriter();
		writer.write(outStr);
		writer.close();
		writer=null;
	}	
	public void outPrintJson(Object obj)throws IOException{
		HttpServletResponse response=getResponse();
		response.setContentType("application/json;charset=utf-8"); 
		PrintWriter writer=response.getWriter();
		Gson gson=new Gson();
		writer.write(gson.toJson(obj));
		writer.close();
		writer=null;
	}
}
