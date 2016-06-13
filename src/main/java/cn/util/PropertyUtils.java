package cn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 说明:此类主要用于读写 属性文件<br>
 * 1.有两个构造方法 默认无惨构造选择读取'paths.properties'
 * 2.带参的构造方法 参数为访问的 属性文件路径
 * @author manager
 * @version  2010.12.22 created
 */
public class PropertyUtils {
	private static Logger log = Logger.getLogger(PropertyUtils.class.getName());

	private Properties G_properties = null;

	private Object propertiesLock = new Object();

	private String path = null;

	/**
	 * 默认属性文件(weixin.properties)
	 */
	public PropertyUtils() {
		this.path=this.getClass().getResource("/").getPath()+"paths.properties";
	}
//阿里配置文件
	public PropertyUtils(String propertiesname,String type) {
		this.path=this.getClass().getResource("/").getPath()+propertiesname+".properties";
	}
	/**
	 * 指定属性文件名称
	 * @param filepath  属性文件路径
	 */
	public PropertyUtils(String filepath) {
		this.path = filepath;
	}

	/**
	 * 获取属性值 默认为null
	 * @param key  键
	 * @return
	 */
	public String getProp(String key) {
		return getProp(key, null);
	}

	public  HashMap<String , String> getKeyAndValues()
	{
		if (G_properties == null) {
			synchronized (propertiesLock) {
				if (G_properties == null) {
					if (loadProps() == false) {
						return null;
					}
				}
			}
		}
		HashMap<String , String> hm=new HashMap<String , String>();
		 Iterator<Entry<Object, Object>> it = G_properties.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Entry<Object, Object> entry = it.next();  
	            Object key = entry.getKey();  
	            Object value = entry.getValue();  
	            hm.put(key.toString(), value.toString());
	        }  
	        return hm;
	}
	/**
	 * 获取属性值
	 * @param key   键
	 * @param defaultValue   默认值
	 * @return
	 */
	public String getProp(String key, String defaultValue) {
		if (G_properties == null) {
			synchronized (propertiesLock) {
				if (G_properties == null) {
					if (loadProps() == false) {
						return null;
					}
				}
			}
		}
		String property = G_properties.getProperty(key);
		if (property == null) {
			return defaultValue;
		} else {
			return property.trim();
		}
	}

	/**
	 * 设置属性值
	 * <br> 同一时间,只允许一个线程访问该方法
	 * @param name
	 * @param value
	 */
	public void setProp(String name, String value) {
		synchronized (propertiesLock) {
			if (G_properties == null) {
				loadProps();
			}
			G_properties.setProperty(name, value);
			saveProps();
		}
	}

	/**
	 *　加载属性文件
	 */
	private boolean loadProps() {
		boolean m_success = true;
		if (G_properties != null) {
			G_properties.clear();
		} else {
			G_properties = new Properties();
		}
		InputStream in = null;
		try {
			File file = new File(this.path);
			in = openInputStream(file);
			if (path.endsWith(".xml")) {
				G_properties.loadFromXML(in);
			}
			if (path.endsWith(".properties")) {
				G_properties.load(in);
			}
		} catch (Exception e) {
			log.error("读取" + path + "失败! \n 异常信息:", e);
			m_success = false;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return m_success;
	}

	/**
	 *　保存属性文件.
	 */
	public void saveProps() {
		log.info("Save   properties   to:   " + this.path);
		OutputStream out = null;
		try {
			File file = new File(this.path);
			out = openOutputStream(file);
			if (path.endsWith(".xml")) {
				G_properties.storeToXML(out, this.path);
			}
			if (path.endsWith(".properties")) {
				G_properties.store(out, this.path);
			}
		} catch (Exception ioe) {
			log.error("文件写入错误 " + path + "　．确认文件是否存在,或文件是否只读", ioe);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	private FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
		return new FileInputStream(file);
	}

	private FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file);
	}
}
