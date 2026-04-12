package com.dsc.spos.xml.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;


/**
 * 分析 xml 的工具
 * @author Xavier
 *
 */
public class ParseXml {
	
		
	public ParseXml() 
	{
		
	}
		
	private void setCdataParse(Marshaller m) throws PropertyException {
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        m.setProperty(CharacterEscapeHandler.class.getName(),
                new CharacterEscapeHandler() {
                    @Override
                    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer writer) throws IOException {
                        writer.write(ch, start, length);
                    }
                });
	}
	
	
	/**
	 * 將 bean 轉成 xml.
	 * @param bean
	 * @return
	 * @throws JAXBException
	 */
	public String beanToXml(Object bean) throws Exception {
		JAXBContext context = JAXBContext.newInstance(bean.getClass());
        Marshaller m = context.createMarshaller();
        this.setCdataParse(m);
        StringWriter sw = new StringWriter();
        m.marshal(bean, sw);
        return sw.toString();
//        return sw.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">"); //若該套件 jaxb-impl-2.1.13.jar 不能使用, 則用最笨的方式, 來處理
	}
	
	/**
	 * 將 xml 轉成 bean.
	 * @param <T>
	 * @param xml
	 * @param bean
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public <T> T xmlToBean(String xml, Class<T> bean) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance(bean);//(bean.getClass());
			Unmarshaller um = context.createUnmarshaller();
			StringReader sr = new StringReader(xml);
			T unmarshal = (T)um.unmarshal(sr);
			return unmarshal;
		} catch(JAXBException e) {
			
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T xmlToBean(URL xmlFilePath, Class<T> bean) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance(bean);//(bean.getClass());
			Unmarshaller um = context.createUnmarshaller();
			T unmarshal = (T)um.unmarshal(xmlFilePath);
			return unmarshal;
		} catch(JAXBException e) {
			
			throw e;
		}
	}
	
	/**
	 * 混合 xml, properties
	 * @param xmlFilePath
	 * @param propertiesFilePath
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public <T> T xmlToBean(URL xmlFilePath, URL propertiesFilePath, Class<T> bean) throws Exception {
		try {
			//取得 xml.
			String xml = getStringFromInputStream(xmlFilePath.openStream());
			
			//取得設定檔
			Properties props = new Properties();
			props.load(propertiesFilePath.openStream());

			String regex = "\\$\\{(.+?)\\}";  
	        Pattern pattern = Pattern.compile(regex);  
	        Matcher matcher = pattern.matcher(xml);
	        StringBuffer sb = new StringBuffer();
	        while(matcher.find()) {
	        	String key = matcher.group(1);
	        	String value = props.getProperty(key);
	        	matcher.appendReplacement(sb, value);
	        }
	        matcher.appendTail(sb);
			xml = sb.toString();
			
			return this.xmlToBean(xml, bean);
		} catch(Exception e) {
			
			throw e;
		}
	}
	
	/**
	 * 讀取檔案內容
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private String getStringFromInputStream(InputStream is) throws Exception {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is,"utf-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 將 filed 的資料, 轉成 map.
	 * @param data
	 * @return
	 */
//	public Map<String, String> fieldToMap(List<Field> data) {
//		Map<String, String> m = new HashMap<String, String>();
//		for (Field f : data) {
//			m.put(f.getName(), f.getValue());
//		}
//		return m;
//	}

}
