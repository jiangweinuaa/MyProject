package com.dsc.spos.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dsc.spos.dao.DsmDAO;
import com.google.common.io.CharStreams;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class ESBUtils {
    static Logger logger = Logger.getLogger(ESBUtils.class);

    //xml转化成对象
    @SuppressWarnings("unchecked")
    public Map<String, String> xmlToMap(String xml)throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        SAXReader sr = new SAXReader();
        Document doc = sr.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        Element root = doc.getRootElement();
        List<Element> list = root.elements();

        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        map.put("xml",xml );
        return map;
    }

    //xml转化成对象
    @SuppressWarnings("unchecked")
    public Map<String, String> xmlToMap(HttpServletRequest req)throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        SAXReader sr = new SAXReader();
        InputStream is = req.getInputStream();

        String xml = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
        Document doc = sr.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        Element root = doc.getRootElement();
        List<Element> list = root.elements();

        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        map.put("xml",xml );
        is.close();
        return map;
    }

    //xml转化成对象
    public String getRequestData(HttpServletRequest req) throws IOException{
        InputStream is = req.getInputStream();
        String str = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
        is.close();
        return str;
    }

    private XStream xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")) {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对那些xml节点的转换增加CDATA标记   true增加  false反之
                boolean cdata = false;

                public void startNode(String name, Class clazz) {

                    if(!name.equals("xml")){
                        char[] arr = name.toCharArray();
                        if (arr[0] >= 'a' && arr[0] <= 'z') {
                            //arr[0] -= 'a' - 'A';
                            //ASCII码，大写字母和小写字符之间数值上差32
                            arr[0] = (char) ((int) arr[0] - 32);
                        }
                        name = new String(arr);
                    }
                    super.startNode(name, clazz);
                }

                @Override
                public void setValue(String text) {

                    if(text!=null && !"".equals(text)){
                        //浮点型判断
//                        Pattern patternInt = Pattern.compile("[0-9]*(\\.?)[0-9]*");
                        //整型判断
//                        Pattern patternFloat = Pattern.compile("[0-9]+");
                        //如果是整数或浮点数 就不要加[CDATA[]了
                        if(text.contains(",")){//patternInt.matcher(text).matches() || patternFloat.matcher(text).matches()
                            cdata = true;
                        }else{
                            cdata = false;
                        }
                    }
                    super.setValue(text);
                }

                protected void writeText(QuickWriter writer, String text) {

                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }

    });

    /**
     * 采用XStream将JavaBean对象转成XML
     * @param object
     * @return
     */
    public String ObjectToXML2(Object object) {
//	    XStream xStream = new XStream(new DomDriver(null,new XmlFriendlyNameCoder("_-", "_")));
        xstream.alias("xml", object.getClass());
//		xstream.alias("CouponInfo", DigiwinCouponInfo.class);
        return xstream.toXML(object).replaceAll("__", "_").replace("R","r");
    }

    /**
     * 获取guid
     *
     * @return
     */
    public static String getGuid() {
        //创建uuid
        UUID uuid = UUID.randomUUID();
        // 得到对象产生的ID
        String id = uuid.toString();
        // 转换为大写
        id = id.toUpperCase();
        // 替换 -
        id = id.replaceAll("-", "");

        return id;
    }

    /**
     * 获得20个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get20UUID() {

        UUID id = UUID.randomUUID();
        String[] idd = id.toString().toUpperCase().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3];
    }

    /**
     * (获取指定长度uuid)
     *
     * @return
     */
    public static String getUUID(int len) {
        if (0 >= len) {
            return null;
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuffer str = new StringBuffer();

        for (int i = 0; i < len; i++) {
            str.append(uuid.charAt(i));
        }

        return str.toString();
    }
    /*
     * 获取分页数据,获取后从PageQueryInfo中取
     */
    public static PageQueryInfo getPageData(String sql, String[] params, DsmDAO dao, int pageNumber, int pageSize) throws Exception {
        PageQueryInfo page = new PageQueryInfo();

      /*
      if (pageNumber <= 0 || pageSize <= 0)            //不分页
      {
          page.setDatas(dao.executeQuerySQL(sql, params, false));
          page.setTotalPages(1);
          page.setPageNumber(1);
          page.setTotalRecords(page.getDatas().size());
          page.setPageSize(0);
          return page;
      }
			*/

        String countSql = getCountSql(sql);
        String pageSql = getPageSql(sql,pageNumber,pageSize);

        List<Map<String, Object>> mapscount = dao.executeQuerySQL(countSql, params, false);
        int totalRecords = Integer.valueOf(mapscount.get(0).get("NUM").toString());
        List<Map<String, Object>> maps = dao.executeQuerySQL(pageSql, params, true);


        page.setDatas(maps);
        page.setTotalRecords(totalRecords);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);

        int totalPages = 0;
        if(pageSize <= 0) //不分页
        {
            totalPages = 1;
            page.setPageNumber(1);
            page.setPageSize(totalRecords);
        }
        else
        {
            totalPages = totalRecords / pageSize;
            int m = totalRecords % pageSize;
            if (m > 0) {
                totalPages = totalPages + 1;
            }
        }



        page.setTotalPages(totalPages);


        return page;
    }
    public static String getCountSql(String sql)
    {
        String countSql = ""
                + " SELECT COUNT(*) AS NUM "
                + " FROM ("
                + sql
                + " ) TABLE_ALLDATE";
        return countSql;
    }
    public static String getPageSql(String sql,int pageNumber, int pageSize)
    {

        if (pageNumber <= 0 || pageSize <= 0) //不分页
        {
            return sql;
        }
        int startRow = (pageNumber - 1) * pageSize+1;
        int endRow = startRow+pageSize-1;

        String pageSql = ""
                + "select * from ("
                + " SELECT rownum as NUM,ALLTABLE.* FROM"
                + " ( "
                + sql
                + " ) ALLTABLE"
                + " )"
                + " where NUM >= " + startRow + " AND NUM <= " + endRow +" ";
        return pageSql;
    }

    /*
     * 返回对象转换成JOSN字符串
     */
    public static String ObjectToJson(Object response) {
        String JsonStr = com.alibaba.fastjson.JSON.toJSONString(response);

        //ObjectMapper mapper = new ObjectMapper();
        //String JsonStr = mapper.writeValueAsString(response);


        return JsonStr;
    }

    public static void saveLog(String fileName,String logInfo){
        if(fileName!=null&&fileName.trim().length()>0){
            try{
                // 生成文件路径
                String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
                String path = System.getProperty("user.dir") + "\\log\\" + fileName + sdFormat + ".txt";
                File file = new File(path);

                String dirpath = System.getProperty("user.dir") + "\\log";
                File dirfile = new File(dirpath);
                if (!dirfile.exists()) {
                    dirfile.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
                // 前面加上时间
                String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
                String slog = stFormat + " " + logInfo + "\r\n";
                output.write(slog);
                output.close();
            }catch(Exception e) {

            }
        }else{
            logger.info(logInfo);
        }
    }

}
