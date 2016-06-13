package cn.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext implements ApplicationContextAware {

	protected static ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		context=applicationcontext;
	}
	public static ApplicationContext getContext(){
		return context;
		//return 
	}
	
	public static Object getBean(String name){
		return context.getBean(name);
	}

}