package com.secretchat.tools.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件类
 * @author chec
 *
 */
public class ReadPropsUtils {
	private static Map<String,Object> PropertiesPool=new HashMap<String,Object>();
	private static Logger logger = LoggerFactory.getLogger(ReadPropsUtils.class);
	@SuppressWarnings("unused")
	private volatile ReadPropsUtils instance = null;
	private Properties p = null;
	
	public ReadPropsUtils(){
		this("sys.properties");
	}

	public ReadPropsUtils(String configName) {
		String configPath = configName;//ConfigFileUtil.getPath(PropertyConfig.class,configName);
		if (configPath != null) {
			if(ReadPropsUtils.PropertiesPool.containsKey(configName)){
				p = (Properties)ReadPropsUtils.PropertiesPool.get(configName);
			}else{
				try {
					p = new Properties();
					InputStream input = getClass().getClassLoader().getResourceAsStream(configName);
					p.load(input);
					ReadPropsUtils.PropertiesPool.put(configName,p);
				} catch (FileNotFoundException e) {
					logger.error("",e);
				} catch (IOException e) {
					logger.error("",e);
				}		
			}
		} else {
			logger.error("没有发现配置文件:"+configName);
		}
	}
	
	/**
	 * 根据配置文件中的键，返回其字符串类型的值
	 * @param key the key
	 * @return the value
	 */
	public  String getValue(String key) {
		String value = p.getProperty(key);
		try {
			value=new String(value.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return value;
	}
	
	/**
	 * 根据配置文件中的键，返回其整数类型的值，如果不能转化为整数，返回0.
	 * @param key the key
	 * @return the int
	 */
	public  int getInt(String key) {
		String str = getValue(key);
		int valueInt = 0;
		if (str != null) {
			try {
				valueInt = Integer.parseInt(str);
			} catch (Exception e) {
				logger.error(e+"");
			}
		}
		logger.debug(key + "->" + valueInt);
		return valueInt;
	}
	
	public  void traceInfo(String key) {
		logger.info(key + "->" + p.getProperty(key));
	}

	public Properties getProperties() {
		return p;
	}
	
}
