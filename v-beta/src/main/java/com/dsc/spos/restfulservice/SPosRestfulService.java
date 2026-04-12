package com.dsc.spos.restfulservice;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.config.SPosConfig.Value;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TransferJsonUtils;
import com.dsc.spos.thirdpart.duandian.duandianCallBackService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.ISVWeComUtils;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackService;
import com.dsc.spos.thirdpart.youzan.response.YouZanNotifyBasicRes;
import com.dsc.spos.thirdpart.youzan.util.YouZanUtils;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.dsc.spos.utils.ec.Shopee;
import com.dsc.spos.utils.invoice.InvoiceService;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.dsc.spos.utils.wxcp.WxCpServiceUtils;
import com.dsc.spos.waimai.*;
import com.dsc.spos.waimai.kdniao.kdnQGService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.activation.DataHandler;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/")
public class SPosRestfulService {
    Logger logger = LogManager.getLogger(SPosRestfulService.class.getName());
    private MySpringContext springContext;

    // 做成单例 ,避免Gson内存泄漏
    private static final Gson gson = new Gson();

    public void setSpringContext(MySpringContext springContext) {
        this.springContext = springContext;
    }

    /**
     * 这个是直接访问资源类就可以测试到的
     *
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "Hello jersey , Welcome to the POS Department!";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHelloPOST() {
        return "Hello jersey , Welcome to the POS Department!";
    }

    /**
     * 服务健康检查
     * http://localhost:8086/DCP/services/health
     * @param request
     * @return
     * @throws Exception
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception
    {
        //设置允许跨域访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        response.setCharacterEncoding("UTF-8");

        //打标记
        boolean healthOk=false;
        String url="";
        //1、pos服务
        url=StaticInfo.POS_INNER_URL;
        if(!Check.Null(url))
        {
            String res=HttpSend.SendNCRRestfulStatus(url,"pos服务-健康检查");
            if (!Check.Null(res))
            {
                healthOk=true;
            }
            if (!healthOk)
            {
                return Response.status(500).entity("{\"ERROR_URL\":\"错误地址："+url+"\"}").build();//返回错误状态
            }
            healthOk=false;
        }
        //2、促销服务
        url=StaticInfo.PROM_INNER_URL;
        if(!Check.Null(url))
        {
            String res=HttpSend.SendNCRRestfulStatus(url,"prom服务-健康检查");
            if (!Check.Null(res))
            {
                healthOk=true;
            }
            if (!healthOk)
            {
                return Response.status(500).entity("{\"ERROR_URL\":\"错误地址："+url+"\"}").build();//返回错误状态
            }

            healthOk=false;
        }
        //3、CRM服务
        url=StaticInfo.CRM_INNER_URL;
        url=url.replace("/member", "");
        if(!Check.Null(url))
        {
            String res=HttpSend.SendNCRRestfulStatus(url,"crm服务-健康检查");
            if (!Check.Null(res))
            {
                healthOk=true;
            }
            if (!healthOk)
            {
                return Response.status(500).entity("{\"ERROR_URL\":\"错误地址："+url+"\"}").build();//返回错误状态
            }

            healthOk=false;
        }
        //4、PAY服务
        url=StaticInfo.PAY_INNER_URL;
        url=url.replace("/pay", "");
        if(!Check.Null(url))
        {
            String res=HttpSend.SendNCRRestfulStatus(url,"crm服务-健康检查");
            if (!Check.Null(res))
            {
                healthOk=true;
            }
            if (!healthOk)
            {
                return Response.status(500).entity("{\"ERROR_URL\":\"错误地址："+url+"\"}").build();//返回错误状态
            }
        }
        return Response.status(200).entity("{\"INNER_URL\":\"服务OK\"}").build();//200就是OK
    }

    /**
     * 分区SQL字段提取
     * http://localhost:8086/DCP/services/partition
     * @return
     * @throws Exception
     */
    @GET
    @Path("/partition")
    @Produces(MediaType.TEXT_PLAIN)
    public String partition(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception
    {
        //设置允许跨域访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        response.setCharacterEncoding("UTF-8");

        StaticInfo.dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        //
        LocalDate localDate = LocalDate.now();


        String res="";

        StringBuffer stringBuffer=new StringBuffer();

        stringBuffer.append("分区SQL提取结果：\r\n\r\n");

        //查找需要分区的表结构信息
        List<Map<String, Object>> list_partition=StaticInfo.dao.executeQuerySQL("SELECT AA.TABLE_NAME,\n" +
                "       AA.COLUMN_NAME,\n" +
                "       AA.NULLABLE,\n" +
                "       AA.DATA_TYPE,\n" +
                "       AA.DATA_LENGTH,\n" +
                "       AA.CHAR_LENGTH,\n" +
                "       AA.DATA_PRECISION,\n" +
                "       AA.DATA_SCALE,\n" +
                "       AA.DATA_DEFAULT,\n" +
                "       BB.ISKEY\n" +
                "  FROM USER_TAB_COLUMNS AA\n" +
                "  left join (SELECT A.TABLE_NAME, A.COLUMN_NAME, 'Y' as iskey\n" +
                "               FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B\n" +
                "              WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME\n" +
                "                AND B.CONSTRAINT_TYPE = 'P') BB on AA.TABLE_NAME =BB.TABLE_NAME\n" +
                "                                               and AA.COLUMN_NAME =BB.COLUMN_NAME \n" +
                "WHERE AA.TABLE_NAME='DCP_STOCK_DETAIL_STATIC' " +//表名

                "OR AA.TABLE_NAME='DCP_STOCK_DAY_STATIC' " +//表名

                "OR AA.TABLE_NAME='DCP_PSTOCKIN' " +//表名
                "OR AA.TABLE_NAME='DCP_PSTOCKIN_DETAIL' " +//表名
                "OR AA.TABLE_NAME='DCP_PSTOCKIN_MATERIAL' " +//表名

                "OR AA.TABLE_NAME='DCP_PORDER' " +//表名
                "OR AA.TABLE_NAME='DCP_PORDER_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_RECEIVING' " +//表名
                "OR AA.TABLE_NAME='DCP_RECEIVING_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_STOCKIN' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKIN_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_PROCESSTASK' " +//表名
                "OR AA.TABLE_NAME='DCP_PROCESSTASK_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_SSTOCKIN' " +//表名
                "OR AA.TABLE_NAME='DCP_SSTOCKIN_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_STOCKOUT' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKOUT_DETAIL' " +//表名

                "OR AA.TABLE_NAME='DCP_LSTOCKOUT' " +//表名
                "OR AA.TABLE_NAME='DCP_LSTOCKOUT_DETAIL' " +//表名
                "OR AA.TABLE_NAME='DCP_LSTOCKOUT_IMAGE' " +//表名

                "OR AA.TABLE_NAME='DCP_STOCKTAKE' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKTAKE_DETAIL' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKTAKE_DETAIL_UNIT' " +//表名

                "OR AA.TABLE_NAME='DCP_STOCKTASK' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKTASK_LIST' " +//表名
                "OR AA.TABLE_NAME='DCP_STOCKTASK_RANGE' " +//表名

                "OR AA.TABLE_NAME='DCP_ADJUST' " +//表名
                "OR AA.TABLE_NAME='DCP_ADJUST_DETAIL' " +//表名

                "ORDER BY AA.TABLE_NAME,AA.COLUMN_ID ",null);





        //
        if (list_partition != null && list_partition.size()>0)
        {
            List<Map<String, Object>> tables=list_partition.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(p -> (String) p.get("TABLE_NAME")))),
                    ArrayList::new));

            //字段列表，不带分区字段，因为分区字段在复制的时候要赋值
            StringBuffer sb_same_column=new StringBuffer();

            for (Map<String, Object> table : tables)
            {
                sb_same_column.setLength(0);

                String tablename=table.get("TABLE_NAME").toString();
                List<Map<String, Object>> list_DCP_STOCK_DETAIL_STATIC=list_partition.stream().filter(p->p.get("TABLE_NAME").toString().equals(tablename)).collect(Collectors.toList());

                //1、创建表分区
                stringBuffer.append(tablename+" 1、创建表分区：\r\n");



                if (list_DCP_STOCK_DETAIL_STATIC != null && list_DCP_STOCK_DETAIL_STATIC.size()>0)
                {
                    stringBuffer.append("create table "+tablename+"_OLD \r\n");
                    stringBuffer.append("( \r\n");
                    for (int i = 0; i < list_DCP_STOCK_DETAIL_STATIC.size(); i++)
                    {
                        Map<String, Object> map=list_DCP_STOCK_DETAIL_STATIC.get(i);

                        String COLUMN_NAME=map.get("COLUMN_NAME").toString();
                        String NULLABLE=map.get("NULLABLE").toString();
                        String DATA_TYPE=map.get("DATA_TYPE").toString();
                        String DATA_LENGTH=map.get("DATA_LENGTH").toString();
                        String CHAR_LENGTH=map.get("CHAR_LENGTH").toString();
                        String DATA_PRECISION=map.get("DATA_PRECISION").toString();
                        String DATA_SCALE=map.get("DATA_SCALE").toString();
                        String DATA_DEFAULT=map.get("DATA_DEFAULT").toString();

                        sb_same_column.append(COLUMN_NAME+" ,\r\n");

                        //跳过分区字段,后面会默认加上次字段，避免测试环境有此字段
                        if (COLUMN_NAME.equals("PARTITION_DATE"))
                        {
                            continue;
                        }

                        if (DATA_TYPE.equals("NUMBER") && DATA_SCALE.equals("0"))
                        {
                            //INTEGER
                            DATA_TYPE="INTEGER ";
                        }

                        //字段名+空格+类型+空格+默认值+,
                        //TOT_AMT_CUSTPAYREAL_SALE  NUMBER(23,8) default 0 not null,

                        stringBuffer.append(COLUMN_NAME +" "+ DATA_TYPE );
                        if (DATA_TYPE.equals("NVARCHAR2") || DATA_TYPE.equals("VARCHAR2"))
                        {
                            stringBuffer.append("("+CHAR_LENGTH+") ");
                        }
                        if (DATA_TYPE.equals("NUMBER") && !Check.Null(DATA_SCALE))
                        {
                            stringBuffer.append("("+DATA_PRECISION+","+DATA_SCALE+") ");
                        }
                        if (DATA_TYPE.equals("RAW"))
                        {
                            stringBuffer.append("("+DATA_LENGTH+") ");
                        }

                        if (DATA_DEFAULT.equals("")==false && DATA_DEFAULT.equals("null")==false&&DATA_DEFAULT.equals("null\n")==false&&DATA_DEFAULT.equals("null\r\n")==false)
                        {
                            stringBuffer.append(" default "+DATA_DEFAULT);
                        }

                        if (NULLABLE.equals("N"))
                        {
                            stringBuffer.append(" not null ");
                        }
                        stringBuffer.append(",\r\n");
                    }
                    stringBuffer.append("PARTITION_DATE NUMBER default to_number(to_char(SYSDATE,'yyyyMMdd')) \r\n");//加上这个分区字段
                    stringBuffer.append(") \r\n");

                    stringBuffer.append("PARTITION BY RANGE (PARTITION_DATE) INTERVAL (100)\r\n");

                    switch (tablename)
                    {
                        case "DCP_STOCK_DETAIL_STATIC":
                            stringBuffer.append("SUBPARTITION BY HASH (BILLNO)\r\n");
                            break;
                        case "DCP_STOCK_DAY_STATIC":
                            stringBuffer.append("SUBPARTITION BY HASH (PLUNO)\r\n");
                            break;
                        case "DCP_PSTOCKIN":
                            stringBuffer.append("SUBPARTITION BY HASH (PSTOCKINNO)\r\n");
                            break;
                        case "DCP_PSTOCKIN_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (PSTOCKINNO)\r\n");
                            break;
                        case "DCP_PSTOCKIN_MATERIAL":
                            stringBuffer.append("SUBPARTITION BY HASH (PSTOCKINNO)\r\n");
                            break;
                        case "DCP_PORDER":
                            stringBuffer.append("SUBPARTITION BY HASH (PORDERNO)\r\n");
                            break;
                        case "DCP_PORDER_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (PORDERNO)\r\n");
                            break;
                        case "DCP_RECEIVING":
                            stringBuffer.append("SUBPARTITION BY HASH (RECEIVINGNO)\r\n");
                            break;
                        case "DCP_RECEIVING_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (RECEIVINGNO)\r\n");
                            break;
                        case "DCP_STOCKIN":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKINNO)\r\n");
                            break;
                        case "DCP_STOCKIN_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKINNO)\r\n");
                            break;
                        case "DCP_PROCESSTASK":
                            stringBuffer.append("SUBPARTITION BY HASH (PROCESSTASKNO)\r\n");
                            break;
                        case "DCP_PROCESSTASK_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (PROCESSTASKNO)\r\n");
                            break;
                        case "DCP_SSTOCKIN":
                            stringBuffer.append("SUBPARTITION BY HASH (SSTOCKINNO)\r\n");
                            break;
                        case "DCP_SSTOCKIN_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (SSTOCKINNO)\r\n");
                            break;
                        case "DCP_STOCKOUT":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKOUTNO)\r\n");
                            break;
                        case "DCP_STOCKOUT_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKOUTNO)\r\n");
                            break;
                        case "DCP_LSTOCKOUT":
                            stringBuffer.append("SUBPARTITION BY HASH (LSTOCKOUTNO)\r\n");
                            break;
                        case "DCP_LSTOCKOUT_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (LSTOCKOUTNO)\r\n");
                            break;
                        case "DCP_LSTOCKOUT_IMAGE":
                            stringBuffer.append("SUBPARTITION BY HASH (LSTOCKOUTNO)\r\n");
                            break;
                        case "DCP_STOCKTAKE":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTAKENO)\r\n");
                            break;
                        case "DCP_STOCKTAKE_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTAKENO)\r\n");
                            break;
                        case "DCP_STOCKTAKE_DETAIL_UNIT":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTAKENO)\r\n");
                            break;
                        case "DCP_STOCKTASK":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTASKNO)\r\n");
                            break;
                        case "DCP_STOCKTASK_LIST":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTASKNO)\r\n");
                            break;
                        case "DCP_STOCKTASK_RANGE":
                            stringBuffer.append("SUBPARTITION BY HASH (STOCKTASKNO)\r\n");
                            break;
                        case "DCP_ADJUST":
                            stringBuffer.append("SUBPARTITION BY HASH (ADJUSTNO)\r\n");
                            break;
                        case "DCP_ADJUST_DETAIL":
                            stringBuffer.append("SUBPARTITION BY HASH (ADJUSTNO)\r\n");
                            break;
                        default:
                            break;
                    }
                    stringBuffer.append("SUBPARTITIONS 16\r\n");
                    stringBuffer.append("(\r\n");
                    stringBuffer.append("PARTITION PART_01 VALUES LESS THAN("+localDate.getYear()+"0101)\r\n");//
                    stringBuffer.append(");\r\n\r\n");
                }



                //2、复制数据
                stringBuffer.append("\r\n\r\n");
                stringBuffer.append(tablename+" 2、复制数据：\r\n");
                if (list_DCP_STOCK_DETAIL_STATIC != null && list_DCP_STOCK_DETAIL_STATIC.size()>0)
                {
                    stringBuffer.append("alter table "+tablename+"_OLD nologging;\r\n");
                    stringBuffer.append("insert /*+ append parallel(p,8) */  into "+tablename+"_OLD p (\r\n");

                    //所有字段名拼接
                    stringBuffer.append(sb_same_column.toString());
                    //拼接分区字段
                    stringBuffer.append("PARTITION_DATE\r\n");

                    stringBuffer.append(") \r\n");
                    stringBuffer.append("SELECT /*+ parallel(n,6) */  \r\n");
                    stringBuffer.append(sb_same_column.toString());
                    //分区字段赋值
                    stringBuffer.append("to_number(to_char(SYSDATE,'yyyyMMdd')) PARTITION_DATE\r\n");
                    stringBuffer.append("FROM "+tablename+" n  \r\n\r\n\r\n");

                }

            }

            sb_same_column=null;
        }


        res=stringBuffer.toString();
        stringBuffer.setLength(0);
        stringBuffer=null;

        return res;
    }

    /**
     * 数据中心：外部请求调用云中台数据
     *
     * @param json
     * @param request
     * @return
     */
    @POST
    @Path("/invoke1")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String invoke1(@FormParam("json") String json, @Context HttpServletRequest request) {
        Object object = request.getSession().getServletContext().getAttribute("curUserNum");
        if (object == null) {
            // System.out.println("curUserNum");
        }
        object = null;

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = df.format(new Date());
        logger.info("\r\n开始时间： " + startTime + " invoke1:" + json + "\r\n");
        DispatchService ds = DispatchService.getInstance();
        String resXml = ds.callService(json, dao);
        String endTime = df.format(new Date());
        logger.info("\r\n结束时间： " + endTime + "");

        //
        ds = null;
        json = null;
        request = null;

        return resXml;
    }

    @POST
    @Path("/invoke")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String invoke(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        Object object = request.getSession().getServletContext().getAttribute("curUserNum");
        if (object == null) {
            // System.out.println("curUserNum");
        }

        String serviceId = "";
        
        //设置允许跨域访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

        object = null;

        StaticInfo.dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        StaticInfo.dao_pos2 = (DsmDAO) this.springContext.getContext().getBean("Pos2Dao");

        StaticInfo.dao_crm2 = (DsmDAO) this.springContext.getContext().getBean("Crm2Dao");


        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader.close();
            insr.close();
            reader = null;
            insr = null;

        } catch (Exception e) {
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + e.getMessage()
                    + "\"}";

            return errJson;
        }
        String json = jb.toString();

        String description = ""; //签名校验结果
        com.alibaba.fastjson.JSONObject newReqJson = new com.alibaba.fastjson.JSONObject(true);

        try {
            /**
             * 外部接口通过invoke 进来的时候
             */
            //兼容以前json=的方式
            if (json.startsWith("json=")) //URL方式需要解码,Body方式不需要解码
            {
                json=json.substring(5);
                json = URLDecoder.decode(json, "UTF-8");
            }

            com.alibaba.fastjson.JSONObject reqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);

            serviceId = request.getHeader("serviceId");
            if(!Check.Null(serviceId)){

                String pageSize = "0";
                String pageNumber = "0";

                if(reqJson.containsKey("pageSize") && !Check.Null(reqJson.get("pageSize").toString())){
                    pageSize = reqJson.get("pageSize").toString();
                }

                if(reqJson.containsKey("pageNumber") && !Check.Null(reqJson.get("pageNumber").toString())){
                    pageNumber = reqJson.get("pageNumber").toString();
                }

                // 3.0 外部接口 header 中的sign ，serviceId等参数，   需要兼容。
                String apiUserCode = request.getHeader("apiUserCode");
                String sign = request.getHeader("sign");
                String langType = request.getHeader("langType");
                String requestId = request.getHeader("requestId");
                String timestamp = request.getHeader("timestamp");
                String version = request.getHeader("version");
                String plantType = request.getHeader("plantType");


                String token = request.getHeader("token"); //token 内外部都用的接口，可能会传token进来
                com.alibaba.fastjson.JSONObject signJson = new com.alibaba.fastjson.JSONObject();
                signJson.put("key", apiUserCode);
                signJson.put("sign", sign);
                String signJsonStr = signJson.toString();
                // 开始校验外部接口 ************
                String bodyServiceId = reqJson.getString("serviceId");//这一步是看body中有没有serviceId

                /**
                 * 如果header 中的serviceId 不为空， 并且 请求 body 中没有serviceId ， 说明这是3.0 新规范的接口，需要做兼容
                 * 为什么要判断两个：  有的前端对中台接口不熟悉，  header 中有serviceId ，body 中也设置了serviceId 。
                 */
                if(!Check.Null(serviceId) && Check.Null(bodyServiceId)){
                    newReqJson.put("serviceId", serviceId);
                    newReqJson.put("request", reqJson);
                    newReqJson.put("sign", sign);

                    newReqJson.put("apiUserCode", apiUserCode);
                    newReqJson.put("requestId", requestId);
                    newReqJson.put("timestamp", timestamp);
                    newReqJson.put("version", version);
                    newReqJson.put("plantType", plantType);

                    newReqJson.put("pageSize", pageSize);
                    newReqJson.put("pageNumber", pageNumber);
                    newReqJson.put("token", token);
                    newReqJson.put("signJson", signJson);
                    newReqJson.put("langType", langType);
                    json = newReqJson.toString();

                }
                // 3.0 新外部接口 兼容结束
            }

            else if( ((String) reqJson.get("serviceId")).endsWith("_Open")){ //老版外部接口中， 需要对sign 节点进行处理

            		serviceId = (String) reqJson.get("serviceId");
                boolean hh = reqJson.containsKey("token");

                if(!reqJson.containsKey("token")){
                    // @Modi by zhangcj on 20201231 处理空指针问题
                    String signJsonStr = "";
                    if (reqJson.get("sign") != null) {
                        signJsonStr = reqJson.get("sign").toString();
                    }
                    if(Check.Null(signJsonStr)){
                        reqJson.put("signJson", "");
                        reqJson.put("sign", "");
                        reqJson.put("apiUserCode","");
                    }
                    else{
                        com.alibaba.fastjson.JSONObject oldSignJson = com.alibaba.fastjson.JSONObject.parseObject(signJsonStr, Feature.OrderedField);
                        String sign = oldSignJson.get("sign").toString();
                        String key = oldSignJson.get("key").toString();

                        reqJson.put("signJson", oldSignJson);
                        reqJson.put("sign", sign);
                        reqJson.put("apiUserCode", key);
                    }

                    json = reqJson.toString();
                }

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block

            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" +description +  e.getMessage()
                    + "\"}";
            return errJson;
        }

        jb.setLength(0);
        jb = null;

        /*
         * //兼容以前json=的方式 if (json.startsWith("json=")) {
         * json=json.substring(5); }
         */

        // 【ID1021045】【嘉华3.0】日志很大 减少日志输出 by jinzma 20210924
        //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"invoke:" + json + "\r\n");
        
        //判断是否应该调老的DCP3.0的服务
        if	(Check.Null(serviceId))
        {
        	com.alibaba.fastjson.JSONObject reqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);
        	serviceId = (String)reqJson.get("serviceId");
        }
        //v3x兼容下划线后首字母小写接口
        serviceId = StringUtils.toUnderScoreFirstUpperCase(serviceId);
        String resXml ="";
        String sql="select * from DCP_SERVICE_DISPATCH where SERVICE_NAME='"+serviceId+"' AND STATUS = 100";
        List<Map<String, Object>> ServiceList=StaticInfo.dao.executeQuerySQL(sql, null);
        if(!ServiceList.isEmpty())
        {
        	//IBP4.0的服务
          DispatchService ds = DispatchService.getInstance();
          resXml = ds.callService(json, StaticInfo.dao);
          ds = null;
          json = null;
          request = null;
        }
        else
        {
        	//老的DCP3.0服务
        	//暂时认为装在8040端口上，如果不是，则要另外做配置读配置
        	String url = "http://localhost:8040/DCP/services/invoke";
        	String sqlconfig="select * from DCP_SERVICE_DISPATCH_CONFIG where SERVICE_PROJECT='DCP3.0'";
        	List<Map<String, Object>> ServiceConfig=StaticInfo.dao.executeQuerySQL(sqlconfig, null);
        	if(!ServiceConfig.isEmpty())
        	{
        		url = ServiceConfig.get(0).get("SERVICE_URL").toString();
        	}
        	resXml = HttpSend.Sendcom(json, url);
        }

        //

        return resXml;
    }

    /**
     * ERP调用门店管理 不走中台
     *
     * @param json
     * @return
     */
    @POST
    @Path("/invoke2")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String invoke2(@RequestBody String json) {
        DsmDAO dao;
        String service_name = "";
        String service_key = "";
        String erpJson = json; // 保存ERP调用中台的原始JSON
        String nrcJson = "";
        MyCommon myCom = new MyCommon(); //// 保存ERP调用中台的WS记录

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        DispatchService ds = DispatchService.getInstance();
        logger.debug("\r\ninvoke2:" + json + "\r\n");

        // 这部分是为了兼容T100代码,把系统资讯加入到Body
        int idx = json.indexOf("std_data");
        json = json.substring(0, idx - 1) + "\"payload\": {" + json.substring(idx - 1, json.length()) + "}";

        // 插入json轉換器
        try {

            Map<String, Object> jsonMap = gson.fromJson(json, Map.class);
            Map<String, Object> serviceMap = (Map<String, Object>) jsonMap.get("service");
            service_key = jsonMap.get("key").toString();
            service_name = serviceMap.get("name").toString();
            // System.out.println(serviceMap.get("name").toString());
            PosPub.writelog_SaleCreate(serviceMap.get("name").toString());
            PosPub.writelog_SaleCreate("请求服务Key：" + service_key + " 开始执行:");

            TransferJsonUtils tfjs = new TransferJsonUtils();

            if (serviceMap.get("name").toString().equals("pos.receiving.create")) {
                Map<String, Object> replaceMap = setReplaceField_receiving();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.counting.create")) {
                Map<String, Object> replaceMap = setReplaceField_counting();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.return.update")) {
                Map<String, Object> replaceMap = setReplaceField_return();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.day.end.check")) {
                Map<String, Object> replaceMap = setReplaceField_dayend();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.adjust.create")) {
                Map<String, Object> replaceMap = setReplaceField_AdjustCreate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.up.error.log.get")) {
                Map<String, Object> replaceMap = setReplaceField_posErrorGet();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.sale.create")) {
                json = tfjs.transferWGSJsonMsg(json);
            }

            if (serviceMap.get("name").toString().equals("pos.sale.ccbparam")) {
                json = tfjs.transferCCBJsonMsg(json);
            }

            if (serviceMap.get("name").toString().equals("pos.scrap.update")) {
                Map<String, Object> replaceMap = setReplaceField_scrap();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.transfer.create")) {
                Map<String, Object> replaceMap = setReplaceField_TransferCreate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.reject.create")) {
                Map<String, Object> replaceMap = setReplaceField_RejectCreate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.undo.create")) {
                Map<String, Object> replaceMap = setReplaceField_UndoCreate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.orderstatus.update")) {
                Map<String, Object> replaceMap = setReplaceField_OrderStatusUpdate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.requisition.update")) {
                Map<String, Object> replaceMap = setReplaceField_RequisitionUpdate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.payin.check")) {
                Map<String, Object> replaceMap = setReplaceField_payInCheck();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.etl.retrans")) {
                Map<String, Object> replaceMap = setReplaceField_etlRetrans();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.fee.update")) {
                Map<String, Object> replaceMap = setReplaceField_feeUpdate();
                json = tfjs.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.requisition.create")) {
                Map<String, Object> replaceMap = setReplaceField_requisitionCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }

            if (serviceMap.get("name").toString().equals("pos.requisition.ecsflg")) {
                Map<String, Object> replaceMap = setReplaceField_requisitionEcsflg();
                json = tfjs.transferPosJson(json, replaceMap);
            }

            if (serviceMap.get("name").toString().equals("pos.mesMo.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_MoCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockTake.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ErpStockTakeAdd();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSortData.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SortDataAdd();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesReceiving.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ReceivingAdd();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSalesOrder.Create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SalesOrderCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockOutApplication.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_StockOutApplicationCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSalesReturn.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SalesReturnCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesComposeDis.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ComposeDisCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesPurchaseReturn.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_PurchaseReturnCreate();
                json = tfjs.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockDetail.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_StockDetailAdd();
                json = tfjs.transferPosJson(json, replaceMap);
            }

            tfjs = null;
            // System.out.println(json);
            serviceMap = null;
            jsonMap = null;

        } catch (Exception e) {
            /////// 保存ERP调用中台的WS记录
            myCom.erpWSSave(service_name, erpJson, nrcJson, e.getMessage(), e.getMessage());
            myCom = null;
            return e.getMessage();
        }
        nrcJson = json;
        Date cal_start = Calendar.getInstance().getTime();// 获得当前时间
        String resXml = ds.callService(json, dao);
        String resJson = resXml;
        Date cal_end = Calendar.getInstance().getTime();
        long diff_ss = cal_end.getTime() - cal_start.getTime();

        // 插入jsonMsg轉換器
        try {
            TransferJsonUtils tfju = new TransferJsonUtils();
            resXml = tfju.transferT100JsonMsg(resXml);
            tfju = null;

            PosPub.writelog_SaleCreate("执行完成！");
            PosPub.writelog_SaleCreate("服务返回：" + resXml);
            PosPub.writelog_SaleCreate("整个服务执行总耗时：" + diff_ss);

            @SuppressWarnings("unchecked")
            Map<String, Object> jsonResMap = gson.fromJson(resXml, Map.class);

            // payload
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) jsonResMap.get("payload");

            // std_data
            @SuppressWarnings("unchecked")
            Map<String, Object> std_data = (Map<String, Object>) payload.get("std_data");

            jsonResMap.remove("payload");
            jsonResMap.put("std_data", std_data);

            resXml = gson.toJson(jsonResMap);

            std_data = null;
            payload = null;
            jsonResMap = null;

        } catch (Exception e) {
            /////// 保存ERP调用中台的WS记录
            myCom.erpWSSave(service_name, erpJson, nrcJson, e.getMessage(), e.getMessage());
            myCom = null;
            return e.getMessage();
        }

        /////// 保存ERP调用中台的WS记录
        myCom.erpWSSave(service_name, erpJson, nrcJson, resJson, "");
        myCom = null;

        ds = null;
        json = null;

        return resXml;
    }

    /**
     * ERP调用门店管理 走中台
     * T100 现在也调这个地址，奇怪，不用带invoke3,到/DCP/services就行
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/{uri:([\\w/])*}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void invoke3(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        StringBuffer jb = new StringBuffer();
        String line = null;
        MyCommon myCom = new MyCommon(); //// 保存ERP调用中台的WS记录
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader = null;
            insr = null;
        } catch (Exception e) {
            bSuccess = false;
            sErrorMSG = e.getMessage();// 赋值
            response.setHeader("digi-srvver", "1.0");// 服务的版本
            response.setHeader("digi-srvcode", "100");// 服务返回状态码 000：正常流程执行成功
            // 100：服务未执行成功
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + sErrorMSG
                    + "\"}";
            TransferJsonUtils tfju = new TransferJsonUtils();
            errJson = tfju.transferT100JsonMsg(errJson);
            tfju = null;
            response.getOutputStream().write(errJson.getBytes("UTF-8"));
            logger.error(sErrorMSG);

            //// 保存ERP调用中台的WS记录
            myCom.erpWSSave("", "ERP JSON解析失败，无法获取", "", errJson, errJson);
            myCom = null;
            return;
        }
        String json = jb.toString();
        String erpJson = json;
        logger.info("\r\ninvoke3收到ERP请求转换前Reqeust：" + json);

        String key = request.getHeader("digi-reqid");
        if (Check.Null(key)) {
            key = "";
        }
        String type = "sync";
        String host = request.getHeader("digi-host");
        if (Check.Null(host)) {
            host = "";
        }
        String datakey = request.getHeader("digi-datakey");
        if (Check.Null(datakey)) {
            datakey = "";
        }
        String service = request.getHeader("digi-service");
        if (Check.Null(service) || service.isEmpty()) {
            bSuccess = false;
            response.setHeader("digi-srvver", "1.0");// 服务的版本
            response.setHeader("digi-srvcode", "100");// 服务返回状态码 000：正常流程执行成功
            // 100：服务未执行成功
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\""
                    + "请求服务名:digi-service不可为空" + "\"}";
            TransferJsonUtils tfju = new TransferJsonUtils();
            errJson = tfju.transferT100JsonMsg(errJson);
            tfju = null;
            response.getOutputStream().write(errJson.getBytes("UTF-8"));
            //// 保存ERP调用中台的WS记录
            myCom.erpWSSave("", erpJson, "", "", errJson);
            myCom = null;
            return;
        }

        // 这部分是为了兼容T100代码,把系统资讯加入到Body
        String sHeaders = "\"key\":\"" + key + "\"," + "\"type\":\"" + type + "\"," + "\"host\":" + host + ","
                + "\"service\":" + service + "," + "\"datakey\":" + datakey + ",";
        json = "{" + sHeaders + "\"payload\":" + json + "}";
        //
        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        DispatchService ds = DispatchService.getInstance();
        // 插入json轉換器
        String serviceName = "";
        String nrcJson = "";
        try {
            Map<String, Object> serviceMap = gson.fromJson(request.getHeader("digi-service"), Map.class);
            TransferJsonUtils tfju = new TransferJsonUtils();
            serviceName = serviceMap.get("name").toString();
            if (serviceMap.get("name").toString().equals("pos.receiving.create")) {
                Map<String, Object> replaceMap = setReplaceField_receiving();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.counting.create")) {
                Map<String, Object> replaceMap = setReplaceField_counting();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.return.update")) {
                Map<String, Object> replaceMap = setReplaceField_return();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.day.end.check")) {
                Map<String, Object> replaceMap = setReplaceField_dayend();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.adjust.create")) {
                Map<String, Object> replaceMap = setReplaceField_AdjustCreate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.up.error.log.get")) {
                Map<String, Object> replaceMap = setReplaceField_posErrorGet();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.transfer.create")) {
                Map<String, Object> replaceMap = setReplaceField_TransferCreate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.scrap.update")) {
                Map<String, Object> replaceMap = setReplaceField_scrap();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.orgorder.update")) {
                Map<String, Object> replaceMap = setReplaceField_orgorder();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.reject.create")) {
                Map<String, Object> replaceMap = setReplaceField_RejectCreate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.undo.create")) {
                Map<String, Object> replaceMap = setReplaceField_UndoCreate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.orderstatus.update")) {
                Map<String, Object> replaceMap = setReplaceField_OrderStatusUpdate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.requisition.update")) {
                Map<String, Object> replaceMap = setReplaceField_RequisitionUpdate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.payin.check")) {
                Map<String, Object> replaceMap = setReplaceField_payInCheck();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }
            if (serviceMap.get("name").toString().equals("pos.etl.retrans")) {
                Map<String, Object> replaceMap = setReplaceField_etlRetrans();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.fee.update")) {
                Map<String, Object> replaceMap = setReplaceField_feeUpdate();
                json = tfju.transferPosJson(json, replaceMap);
                replaceMap = null;
            }

            if (serviceMap.get("name").toString().equals("pos.requisition.create")) {
                Map<String, Object> replaceMap = setReplaceField_requisitionCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }

            if (serviceMap.get("name").toString().equals("pos.requisition.ecsflg")) {
                Map<String, Object> replaceMap = setReplaceField_requisitionEcsflg();
                json = tfju.transferPosJson(json, replaceMap);
            }

            if (serviceMap.get("name").toString().equals("pos.mesMo.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_MoCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockTake.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ErpStockTakeAdd();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSortData.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SortDataAdd();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesReceiving.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ReceivingAdd();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSalesOrder.Create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SalesOrderCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockOutApplication.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_StockOutApplicationCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesSalesReturn.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_SalesReturnCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesComposeDis.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_ComposeDisCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesPurchaseReturn.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_PurchaseReturnCreate();
                json = tfju.transferPosJson(json, replaceMap);
            }
            if (serviceMap.get("name").toString().equals("pos.mesStockDetail.create"))
            {
                Map<String, Object> replaceMap = setReplaceField_MES_StockDetailAdd();
                json = tfju.transferPosJson(json, replaceMap);
            }


            tfju = null;
            //
            serviceMap = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            bSuccess = false;
            sErrorMSG = e.getMessage();// 赋值
            /*
             * response.setHeader("digi-code", "-1");//互动代码
             * response.setHeader("digi-message",
             * PosPub.encodeBASE64(sErrorMSG));//互动信息，以base64编码
             * response.setHeader("digi-reqid", PosPub.getGUID(true));//请求ID
             */
            response.setHeader("digi-srvver", "1.0");// 服务的版本
            response.setHeader("digi-srvcode", "100");// 服务返回状态码 000：正常流程执行成功
            // 100：服务未执行成功
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + sErrorMSG
                    + "\"}";

            TransferJsonUtils tfju = new TransferJsonUtils();
            errJson = tfju.transferT100JsonMsg(errJson);
            tfju = null;
            response.getOutputStream().write(errJson.getBytes("UTF-8"));
            logger.error(sErrorMSG);

            /////// 保存ERP调用中台的WS记录
            myCom.erpWSSave(serviceName, erpJson, "", errJson, sErrorMSG);
            myCom = null;
            return;
        }
        nrcJson = json;
        String resXml = ds.callService(json, dao);
        String resJson = resXml;
        Map<String, Object> jsonMap = gson.fromJson(resXml, Map.class);

        // 插入jsonMsg轉換器
        try {
            TransferJsonUtils tfju = new TransferJsonUtils();
            resXml = tfju.transferT100JsonMsg(resXml);
            tfju = null;

            // 这里是中台规格需要将payload、srvcode、srvver标签删除
            Map<String, Object> jsonResMap = gson.fromJson(resXml, Map.class);
            jsonResMap.remove("srvcode");
            jsonResMap.remove("srvver");
            resXml = gson.toJson(jsonResMap);
            resXml = resXml.substring(resXml.indexOf("\"payload\":") + 10);
            resXml = resXml.substring(0, resXml.length() - 1);

            // System.out.println("Response中台：" +resXml);
            jsonResMap = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            bSuccess = false;
            sErrorMSG = e.getMessage();// 赋值
            /*
             * response.setHeader("digi-code", "-1");//互动代码
             * response.setHeader("digi-message",
             * PosPub.encodeBASE64(sErrorMSG));//互动信息，以base64编码
             * response.setHeader("digi-reqid", PosPub.getGUID(true));//请求ID
             */
            response.setHeader("digi-srvver", "1.0");// 服务的版本
            response.setHeader("digi-srvcode", "100");// 服务返回状态码 000：正常流程执行成功
            // 100：服务未执行成功
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + sErrorMSG
                    + "\"}";

            TransferJsonUtils tfju = new TransferJsonUtils();
            errJson = tfju.transferT100JsonMsg(errJson);
            tfju = null;

            response.getOutputStream().write(errJson.getBytes("UTF-8"));
            logger.error(sErrorMSG);

            /////// 保存ERP调用中台的WS记录
            myCom.erpWSSave(serviceName, erpJson, nrcJson, errJson, errJson);
            myCom = null;
            return;
        }
        // return resXml;
        /*
         * if (((boolean)jsonMap.get("success")==false)) {
         * response.setHeader("digi-code", "-1");//互动代码 } else {
         * response.setHeader("digi-code", "0");//互动代码 }
         * response.setHeader("digi-message",
         * PosPub.encodeBASE64(jsonMap.get("serviceDescription").toString()));//
         * 互动信息，以base64编码 response.setHeader("digi-reqid",
         * PosPub.getGUID(true));//请求ID
         */
        response.setHeader("digi-srvver", "1.0");// 服务的版本
        response.setHeader("digi-srvcode", jsonMap.get("serviceStatus").toString());// 服务返回状态码
        // 000：正常流程执行成功
        // 100：服务未执行成功
        response.getOutputStream().write(resXml.getBytes("UTF-8"));

        /////// 保存ERP调用中台的WS记录
        myCom.erpWSSave(serviceName, erpJson, nrcJson, resJson, "");
        myCom = null;

        resXml = null;
        jsonMap = null;

        ds = null;
        jb = null;
        json = null;
        request = null;
    }


    @POST
    @Path("/excel_upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA,MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String excel_upload(@Multipart("upload_file") Attachment attachment,@Context HttpServletRequest request,@Context HttpServletResponse response)
            throws IOException {

        //设置允许跨域访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");


        //StaticInfo.dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");
        //StaticInfo.dao_pos2 = (DsmDAO) this.springContext.getContext().getBean("Pos2Dao");
        //StaticInfo.dao_crm2 = (DsmDAO) this.springContext.getContext().getBean("Crm2Dao");

        String filePath="";
        try {
            DataHandler dh = attachment.getDataHandler();
            String contentType = attachment.getContentType().toString();

            if (attachment.getContentType().toString().equals("text/plain")) {

            } else {
                String path = System.getProperty("catalina.home") + "\\webapps\\excel\\";
                File file = new File(path);
                // 如果文件夹不存在则创建
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdir();
                }
                file = null;

                String tempStr = new String(dh.getName().getBytes("ISO-8859-1"), "UTF-8");

                //InputStream inputStream = dh.getInputStream();
                //EasyExcel.read(inputStream, UploadData.class, new UploadDataListener(uploadDAO)).sheet().doRead();

                //ExcelReader reader = ExcelUtil.getReader(inputStream);
                //List<List<Object>> ExcelList = reader.read();
                filePath=path + tempStr;
                File excelfile=new File(filePath);
                if(excelfile.exists()){
                    String newFileName=tempStr.substring(0,tempStr.lastIndexOf("."))+"_"+System.currentTimeMillis()+"."+tempStr.substring(tempStr.lastIndexOf(".")+1);
                    filePath=path + newFileName;
                }
                writeToFile(dh.getInputStream(), filePath);

                //
                tempStr = null;
                path = null;

            }

            dh = null;
        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);
            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());

            pw = null;
            errors = null;

            return errors.toString();

        }

        //String json = serviceBody;
        //String description = ""; //签名校验结果
        //com.alibaba.fastjson.JSONObject newReqJson = new com.alibaba.fastjson.JSONObject(true);

        //try {
            /**
             * 外部接口通过invoke 进来的时候
             */
            //兼容以前json=的方式
            //if (json.startsWith("json=")) //URL方式需要解码,Body方式不需要解码
            //{
            //    json=json.substring(5);
            //    json = URLDecoder.decode(json, "UTF-8");
            //}

            //com.alibaba.fastjson.JSONObject reqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);


         //   }

      //  } catch (Exception e) {


      //      String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" +description +  e.getMessage()
         //           + "\"}";
        //    return errJson;
       // }

       // DispatchService ds = DispatchService.getInstance();
       // String resXml = ds.callService(json, StaticInfo.dao);

        //
       // ds = null;
      //  json = null;
        attachment = null;

       // return resXml;

        return filePath;

    }


    /**
     * 双屏客显文件
     *
     * @param attachments
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/dualplay_upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public Response dualplay_upload(@Multipart("upload_file") List<Attachment> attachments, HttpServletRequest request)
            throws IOException {
        try {
            if (attachments.size() > 0) {
                // System.out.println("ok");
            }

            for (Attachment attach : attachments) {
                DataHandler dh = attach.getDataHandler();
                //// System.out.println(attach.getContentType().toString());

                if (attach.getContentType().toString().equals("text/plain")) {
                    // System.out.println(new
                    // String(dh.getName().getBytes("ISO-8859-1"), "UTF-8"));
                    // System.out.println(writeToString(dh.getInputStream()));
                } else {
                    String path = System.getProperty("catalina.home") + "\\webapps\\dualplay\\";
                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdir();
                    }
                    file = null;

                    String tempStr = new String(dh.getName().getBytes("ISO-8859-1"), "UTF-8");

                    writeToFile(dh.getInputStream(), path + tempStr);

                    //
                    tempStr = null;
                    path = null;

                }

                dh = null;

            }

        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);
            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());

            pw = null;
            errors = null;

            return Response.ok().entity("{\"status\":\"error\"}").build();

        }

        //
        attachments = null;
        request = null;

        return Response.ok().entity("{\"status\":\"done\"}").build();
    }

    /**
     * 商品图片
     *
     * @param attachments
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/goodsimages_upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response goodsimages_upload(@Multipart("upload_file") List<Attachment> attachments,
                                       HttpServletRequest request) throws IOException {
        try {
            if (attachments.size() > 0) {
                //// System.out.println("ok");
            }

            for (Attachment attach : attachments) {
                DataHandler dh = attach.getDataHandler();
                //// System.out.println(attach.getContentType().toString());

                if (attach.getContentType().toString().equals("text/plain")) {
                    // System.out.println(new
                    // String(dh.getName().getBytes("ISO-8859-1"), "UTF-8"));
                    // System.out.println(writeToString(dh.getInputStream()));
                } else {
                    String path = System.getProperty("catalina.home") + "\\webapps\\goodsimages\\";
                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdir();
                    }
                    file = null;

                    String tempStr = new String(dh.getName().getBytes("ISO-8859-1"), "UTF-8");
                    writeToFile(dh.getInputStream(), path + tempStr);

                    tempStr = null;
                    path = null;

                }

                dh = null;
            }

            attachments = null;
            request = null;

        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());
            pw = null;
            errors = null;

            return Response.ok().entity("{\"status\":\"error\"}").build();
        }

        return Response.ok().entity("{\"status\":\"done\"}").build();
    }

    /**
     * 商品报损图片
     * @param attachments
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/baosunimages_upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response baosunimages_upload(@Multipart("upload_file") List<Attachment> attachments,
                                        HttpServletRequest request) throws IOException {
        String fileName = "";
        try {
            if (attachments.size() > 0) {
                //// System.out.println("ok");
            }

            for (Attachment attach : attachments) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                DataHandler dh = attach.getDataHandler();
                //// System.out.println(attach.getContentType().toString());

                if (attach.getContentType().toString().equals("text/plain")) {
                    // System.out.println(new
                    // String(dh.getName().getBytes("ISO-8859-1"), "UTF-8"));
                    // System.out.println(writeToString(dh.getInputStream()));
                } else {
                    String path = System.getProperty("catalina.home") + "\\webapps\\baosunimages\\";
                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdir();
                    }
                    file = null;
                    String name = dh.getName();
                    int i = name.lastIndexOf(".");
                    String strName = name.substring((i + 1), name.length());
                    fileName =  uuid+"."+strName;
                    String tempStr = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                    writeToFile(dh.getInputStream(), path + tempStr);

                    tempStr = null;
                    path = null;

                }

                dh = null;
            }

            attachments = null;
            request = null;

        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());
            pw = null;
            errors = null;

            return Response.ok().entity("{\"status\":\"error\"}").build();
        }
        return Response.ok().entity("{\"status\":\"done\",\"fileName\":\""+fileName+"\"}").build();
    }

    /**
     * 商品报损图片
     * @param attachments
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/tuihuoimages_upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response tuihuoimages_upload(@Multipart("upload_file") List<Attachment> attachments,
                                        HttpServletRequest request) throws IOException {
        String fileName = "";
        try {
            if (attachments.size() > 0) {
                //// System.out.println("ok");
            }

            for (Attachment attach : attachments) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                DataHandler dh = attach.getDataHandler();
                //// System.out.println(attach.getContentType().toString());

                if (attach.getContentType().toString().equals("text/plain")) {
                    // System.out.println(new
                    // String(dh.getName().getBytes("ISO-8859-1"), "UTF-8"));
                    // System.out.println(writeToString(dh.getInputStream()));
                } else {
                    String path = System.getProperty("catalina.home") + "\\webapps\\tuihuoimages\\";
                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdir();
                    }
                    file = null;
                    String name = dh.getName();
                    int i = name.lastIndexOf(".");
                    String strName = name.substring((i + 1), name.length());
                    fileName =  uuid+"."+strName;
                    String tempStr = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                    writeToFile(dh.getInputStream(), path + tempStr);

                    tempStr = null;
                    path = null;

                }

                dh = null;
            }

            attachments = null;
            request = null;

        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());
            pw = null;
            errors = null;

            return Response.ok().entity("{\"status\":\"error\"}").build();
        }
        return Response.ok().entity("{\"status\":\"done\",\"fileName\":\""+fileName+"\"}").build();
    }

    /**
     * 云中台图片上传
     * @param attachments
     * @param uploadType 双屏客显：dualplay 商品图片：goodsimages 报损出库：lstockout 采购退货：sstockout
     * @param MediaResourcePath 参数：媒体资源存储目录
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/images_upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response images_upload(@Multipart("upload_file") List<Attachment> attachments,
                                  @Multipart("upload_type") String uploadType,
                                  @Multipart("MediaResourcePath") String MediaResourcePath,
                                  HttpServletRequest request) throws IOException {
        String fileName ="";
        try {
            if (Check.Null(uploadType) || Check.Null(MediaResourcePath)){
                return Response.ok().entity("{\"status\":\"error\",\"description\":\"upload_type或MediaResourcePath不能为空\"}").build();
            }
            for (Attachment attach : attachments) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                DataHandler dh = attach.getDataHandler();
                if (!attach.getContentType().toString().equals("text/plain")) {
                    String path = MediaResourcePath.replaceAll("\\\\", "\\\\\\\\");  //把"\"替换为"\\"
                    path = MediaResourcePath+"\\"+uploadType.toLowerCase()+"\\";        //目录名处理成小写
                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdir();
                    }
                    file = null;
                    String name = dh.getName();
                    int i = name.lastIndexOf(".");
                    String strName = name.substring((i + 1), name.length());
                    fileName =  uuid+"."+strName;
                    String tempStr = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                    writeToFile(dh.getInputStream(), path + tempStr);

                    tempStr = null;
                    path = null;
                }
                dh = null;
                attachments = null;
                request = null;

            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            return Response.ok().entity("{\"status\":\"error\"}").build();
        }
        return Response.ok().entity("{\"status\":\"done\",\"fileName\":\""+fileName+"\"}").build();
    }






    private void writeToFile(InputStream ins, String path) {
        try {

            OutputStream out = new FileOutputStream(new File(path));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = ins.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String writeToString(InputStream ins) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int i = -1;
        while ((i = ins.read(b)) != -1) {
            out.write(b, 0, i);
        }
        ins.close();
        return new String(out.toByteArray(), "UTF-8");
    }

    /**
     * 电商EXCEL文件导入
     *
     * @param attachments
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("/ec_upload/{ectype}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response ec_upload(@Multipart("upload_file") List<Attachment> attachments, HttpServletRequest request,
                              @PathParam("ectype") String ectype) throws IOException {
        try {
            // pchome的地址是多一次的pchome@@@allDay

            if (attachments.size() > 0) {
                // System.out.println("ok");
            }

            for (Attachment attach : attachments) {
                DataHandler dh = attach.getDataHandler();
                //// System.out.println(attach.getContentType().toString());

                if (attach.getContentType().toString().equals("text/plain")) {
                    // System.out.println(new
                    // String(dh.getName().getBytes("ISO-8859-1"), "UTF-8"));
                    // System.out.println(writeToString(dh.getInputStream()));
                } else {
                    String path = System.getProperty("catalina.home") + "\\webapps\\ec\\";

                    // pchome@@@allDay/
                    if (ectype.contains("@@@")) {
                        String[] splitStr = ectype.split("@@@");

                        int iPos = splitStr[1].indexOf("/");

                        String sLastFolderName = "";

                        if (iPos > 0) {
                            sLastFolderName = splitStr[1].substring(0, iPos);
                        } else {
                            sLastFolderName = splitStr[1];
                        }
                        path = path + splitStr[0] + "\\import\\" + sLastFolderName + "\\";
                    } else {
                        // 根据电商代码来保存到不同路径下
                        path = path + ectype + "\\import\\";
                    }

                    File file = new File(path);
                    // 如果文件夹不存在则创建
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdirs();
                    }
                    file = null;

                    String tempStr = new String(dh.getName().getBytes("ISO-8859-1"), "UTF-8");
                    writeToFile(dh.getInputStream(), path + tempStr);

                    tempStr = null;
                    path = null;

                }

                dh = null;

            }

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error(errors.toString());
            pw = null;
            errors = null;

            return Response.ok().entity("{\"status\":\"error\"}").build();
        }

        //
        attachments = null;
        request = null;

        return Response.ok().entity("{\"status\":\"done\"}").build();
    }

    /**
     * waimai
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/MT")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMT(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {
            sErrorMSG = e.getMessage();// 赋值

            /*
             * response.setHeader("digi-code", "-1");//互动代码
             * response.setHeader("digi-message",
             * PosPub.encodeBASE64(sErrorMSG));//互动信息，以base64编码
             * response.setHeader("digi-reqid", PosPub.getGUID(true));//请求ID
             */
            /*
             * response.setHeader("digi-srvver", "1.0");//服务的版本
             * response.setHeader("digi-srvcode", "100");//服务返回状态码 000：正常流程执行成功
             * 100：服务未执行成功 String errJson=
             * "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\""
             * +sErrorMSG+"\"}";
             * errJson=TransferJsonUtils.transferT100JsonMsg(errJson);
             * response.getOutputStream().write(errJson.getBytes("UTF-8"));
             */
            sResponseMSG = "{\"data\":\"ok\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;
        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMMTService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            json = null;
            swm = null;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        if (dao != null) {
            dao.closeDAO();
        }
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    // 为了在浏览器上打开显示（浏览器都是Get方式发送）
    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/MT")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMT_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    // 为了在浏览器上打开显示（浏览器都是Get方式发送）
    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/MTRefund")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTRefund_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"data\":\"ok\"}";
        String jb = "";
        String line = null;
        JSONObject obj = new JSONObject();
        try {
            String order_id = request.getParameter("order_id");
            String notify_type = request.getParameter("notify_type");
            String reason = request.getParameter("reason");
            String res_type = request.getParameter("res_type");
            String is_appeal = request.getParameter("is_appeal");
            // String pictures = request.getParameter("pictures");

            String food = request.getParameter("food");// 推送部分退款信息 才有
            String money = request.getParameter("money");// 推送部分退款信息 才有

            if (order_id == null) {
                response.setContentType("application/json");
                response.setHeader("Content-Type", "application/json");
                response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
                return;
            }

            obj.put("order_id", order_id);
            obj.put("notify_type", notify_type);
            obj.put("reason", reason);
            obj.put("res_type", res_type);
            obj.put("is_appeal", is_appeal);
            obj.put("food", food);
            obj.put("money", money);

            jb = obj.toString();

        } catch (Exception e) {

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMMTService("3");
            swm.setDao(dao);

            String json = jb;

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            obj = null;
            sResponseMSG = null;
            request = null;

            return;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/MTCancel")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTCancel_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"data\":\"ok\"}";
        String jb = "";
        String line = null;
        JSONObject obj = new JSONObject();
        try {
            String order_id = request.getParameter("order_id");
            String reason_code = request.getParameter("reason_code");
            String reason = request.getParameter("reason");
            String deal_op_type = request.getParameter("deal_op_type");
            if (order_id == null) {
                response.setContentType("application/json");
                response.setHeader("Content-Type", "application/json");
                response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
                return;
            }
            obj.put("order_id", order_id);
            obj.put("reason_code", reason_code);
            obj.put("reason", reason);
            obj.put("deal_op_type", deal_op_type);

            jb = obj.toString();

        } catch (Exception e) {

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMMTService("2");
            swm.setDao(dao);

            String json = jb;

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            obj = null;
            request = null;

            return;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/MTShipping")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTShipping_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";

        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 美团订单配送状态回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/MTShipping")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTShipping(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMMTShippingService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"ok\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTOther(@Context HttpServletRequest request, @Context HttpServletResponse response,
                              @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTOther_Get(@Context HttpServletRequest request, @Context HttpServletResponse response,
                                  @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (mtType.equals("MTBatchPullPhoneNumber"))// 调用下接口拉取下
        {
            String app_poi_code = request.getQueryString();
            if (app_poi_code == null || app_poi_code.isEmpty()) {
                app_poi_code = "123456";
            }
            try {
                StringBuilder errorMessage = new StringBuilder();
                String res = WMMTOrderProcess.orderBatchPullPhoneNumber(app_poi_code, 0, 50, errorMessage);

                //
                errorMessage = null;
                res = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("confirm"))// 调用商家确认订单
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMMTOrderProcess.orderConfirm(orderID, errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("cancel"))// 调用商家取消订单
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMMTOrderProcess.orderCancel(orderID, "", "", errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("agree"))// 调用订单确认退款请求
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMMTOrderProcess.orderRefundAgree(orderID, "", errorMessage);

                errorMessage = null;
            } catch (Exception e) {

            }
        } else if (mtType.equals("reject"))// 调用驳回订单退款申请
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMMTOrderProcess.orderRefundReject(orderID, "", errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        }

        else if (mtType.equals("mappingshop"))// 同步门店到redis
        {
            String reqStr = request.getQueryString();

            if (reqStr != null && reqStr.trim().isEmpty() == false) {

                try {
                    String loadDocType = "";
                    String isJBP = "";
                    int index1 = reqStr.indexOf("_");
                    if (index1 > 0) {
                        loadDocType = reqStr.substring(0, index1);
                        isJBP = reqStr.substring(index1 + 1, reqStr.length());
                    }
                    WMMappingShop_DBToRedis dbToRedis = new WMMappingShop_DBToRedis();
                    dbToRedis.SetMappingShopDBToRedis(loadDocType, isJBP);

                    dbToRedis = null;
                } catch (Exception e) {

                }

            }

        }
        else if (mtType.equals("kdn"))
        {
            String orderNo = request.getQueryString();
            if (orderNo!=null)
            {
                kdnQGService kdnQGService = new kdnQGService(true);
                Map<String, Object> setMap = new HashMap<>();
                Map<String, Object> orderMap = new HashMap<>();
                Map<String, Object> SendShopInfoMap = new HashMap<>();
                String sqlOutSaleset="select * from dcp_order where orderno='"+orderNo+"'";
                List<Map<String, Object>> getOrderList=StaticInfo.dao.executeQuerySQL(sqlOutSaleset, null);
                orderMap = getOrderList.get(0);
                String shop = orderMap.getOrDefault("SHIPPINGSHOP","").toString();
                sqlOutSaleset="select * from DCP_ORG where ORGANIZATIONNO='"+shop+"'";

                List<Map<String, Object>> getShopList=StaticInfo.dao.executeQuerySQL(sqlOutSaleset, null);
                SendShopInfoMap = getShopList.get(0);

                sqlOutSaleset = "select * from dcp_outsaleset where deliverytype='KDN' order by lastmoditime desc ";
                List<Map<String, Object>> getQData_OutSaleset=StaticInfo.dao.executeQuerySQL(sqlOutSaleset, null);
                setMap = getQData_OutSaleset.get(0);

                StringBuffer error = new StringBuffer();
                //UUID.randomUUID()
                String res = kdnQGService.checkDeliveryRange(setMap,orderMap,SendShopInfoMap,error);
                res = kdnQGService.kdnOrderCreate(orderNo,setMap,orderMap,SendShopInfoMap,null,error);

            }
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 微商城接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/MicroMarket/Order")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void MicroMarketOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"success\":true,\"serviceStatus\":\"000\",\"serviceDescription\":\"服务执行成功\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + sErrorMSG + "\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new MicroMarketService();
            swm.setDao(dao);

            String json = jb.toString();

            String res_json = swm.execute(json); // 获取下返回值

            // 兼容下，防止更新部分
            if (res_json != null && res_json.isEmpty() == false) {
                sResponseMSG = res_json;
            }

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            sResponseMSG = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + e.getMessage()
                    + "\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }

    /**
     * 微商城接受推送消息 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/MicroMarket/Order")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void MicroMarketOrder_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;
    }

    /**
     * 美团聚宝盆接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBP")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBP(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"OK\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJBPService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"OK\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        // response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

    }

    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/JBP")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBP_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 聚宝盆订单配送状态回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBPShipping")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPShipping(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"OK\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJBPShippingService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"OK\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        // response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

    }

    @GET
    @Path("/Waimai/JBPShipping")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiShipping_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;
    }

    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBPToken")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPToken(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"success\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJBPTokenService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/JBPToken")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPToken_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"success\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBPTokenReleaseBinding")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPTokenReleaseBinding(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"success\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJBPTokenReleaseBindingService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            json = null;
            swm = null;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        if (dao != null) {
            dao.closeDAO();
        }
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP解绑后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/JBPTokenReleaseBinding")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPTokenReleaseBinding_Get(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response) throws Exception {
        String sResponseMSG = "{\"data\":\"success\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 饿了么接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/ELM")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELM(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"message\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {

            SWaimaiBasicService swm = new WMELMService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"message\":\"ok\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/ELM")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELM_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"message\":\"ok\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;
    }


    /**
     * 抖音外卖接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/douyin")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiDouyin(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"message\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        String msgId = "";
        //msg-id,x-bytedance-logid,x-douyin-signature
        /*try {
           Enumeration<String>  headerNames = request.getHeaderNames();
           String headerStr = "";
           if (headerNames!=null)
           {
               while (headerNames.hasMoreElements())
               {
                   headerStr =headerStr+headerNames.nextElement()+",";
               }
           }
           HelpTools.writelog_waimai("【抖音外卖消息header】"+ headerStr);
        }
        catch (Exception e)
        {

        }*/

        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;
            msgId = request.getHeader("msg-id");

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {

            SWaimaiBasicService swm = new WMDYService(msgId);
            swm.setDao(dao);

            String json = jb.toString();

            String res_json = swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"message\":\"ok\"}";
            // 兼容下，防止更新部分
            if (res_json != null && res_json.isEmpty() == false) {
                sResponseMSG = res_json;
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 抖音外卖用户申请退款消息(当用户发起退款申请，且需要商家审核时，需要通过此接口发消息给第三方系统)
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/douyinSPI")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiDouyinSPI(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"message\":\"ok\"}";
        JSONObject resObj = new JSONObject();
        JSONObject resultObj = new JSONObject();
        resultObj.put("result",1);//1 处理中 2 审核通过 3 审核拒绝
        resultObj.put("error_code",0);
        resultObj.put("description","处理中");
        resObj.put("data",resultObj);
        sResponseMSG = resObj.toString();

        /*try {
            Enumeration<String>  headerNames = request.getHeaderNames();
            String headerStr = "";
            if (headerNames!=null)
            {
                while (headerNames.hasMoreElements())
                {
                    headerStr =headerStr+headerNames.nextElement()+",";
                }
            }
            HelpTools.writelog_waimai("【抖音外卖消息header】"+ headerStr);
        }
        catch (Exception e)
        {

        }*/

        StringBuffer jb = new StringBuffer();
        String line = null;
        String logId = "";
        try {

            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader = null;
            insr = null;
            logId = request.getHeader("x-bytedance-logid");

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            //sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {

            SWaimaiBasicService swm = new WMDYSPIService(logId);
            swm.setDao(dao);

            String json = jb.toString();

            String res_json = swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            //sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * /djsw/token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/token")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswToken(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJTokenService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            json = null;
            swm = null;

        } catch (Exception e) {
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * /djsw/token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/djsw/token")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswToken_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * /djsw/newOrder
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/newOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswnewOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");// 1-订单消息
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * /djsw/newOrder
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/djsw/newOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswnewOrder_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 拣货完成消息 /djsw/pickFinishOrder
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/pickFinishOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswpickFinishOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";



        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 订单开始配送消息 /djsw/deliveryOrder
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/deliveryOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswdeliveryOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;
        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            swm = null;
            jb = null;
            sResponseMSG = null;
            json = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 订单妥投消息 /djsw/finishOrder
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/finishOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswfinishOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;
        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;
        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 订单锁定消息 拣货完成未开始配送前，到家风控取消订单时，推送订单锁定消息；客户拒收货物后，推送订单锁定消息。
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/lockOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswlockOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;
        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 用户取消消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/userCancelOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswuserCancelOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 用户取消申请消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/applyCancelOrder")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswapplyCancelOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;
        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;
        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            swm = null;
            sResponseMSG = null;
            jb = null;
            json = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 订单应结消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/orderAccounting")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djsworderAccounting(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try
        {
            SWaimaiBasicService swm = new WMJDDJService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 订单运单状态消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/pushDeliveryStatus")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswpushDeliveryStatus(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try
        {
            SWaimaiBasicService swm = new WMJDDJService("2");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 订单调整消息 /djsw/orderAdjust
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/orderAdjust")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djsworderAdjust(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 订单金额拆分完成消息 /djsw/endOrderFinance
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/endOrderFinance")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djswendOrderFinance(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 订单等待出库消息 /djsw/orderWaitOutStore
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/djsw/orderWaitOutStore")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void djsworderWaitOutStore(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"code\":\"0\",\"msg\":\"success\",\"data\":\"\"}";

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 隐私降级号
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Order/BatchPullPhoneNumber")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void BatchPullPhoneNumber(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;
    }

    /**
     * order/BatchPullPhoneNumber
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Order/BatchPullPhoneNumber")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void OrderGet_BatchPullPhoneNumber(@Context HttpServletRequest request,
                                              @Context HttpServletResponse response) throws Exception {
        /*
         * Integer pageIndex = 1; Integer pageSize = 100; String orderId =
         * request.getQueryString(); StringBuilder errorMeassge = new
         * StringBuilder(); String nResult = ""; String errorstr = "";
         * if(orderId == null) { orderId ="1"; }
         *
         * try { pageIndex = Integer.parseInt(orderId);
         *
         * } catch (Exception e) { pageIndex = 1; }
         *
         *
         * nResult = WMMTOrderProcess.orderBatchPullPhoneNumber("", pageIndex,
         * pageSize, errorMeassge); errorstr = errorMeassge.toString();
         *
         * String sResponseMSG =""; if(nResult !=null &&nResult.length()>0) {
         * sResponseMSG = nResult; } else { sResponseMSG = errorstr; }
         */
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 舞像接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/WUXIANG/Order")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WUXIANGOrder(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":0,\"message\":\"成功\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":-1,\"message\":\"失败\",\"result\":\"" + sErrorMSG + "\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            // 这里需要判断一下head的method 如果为return 需要走订单退订的方法

            SWaimaiBasicService swm;
            if (request.getHeader("method").equals("sync")) {
                swm = new WuXiangService();
            } else {
                swm = new WuXiangReturnService();
            }
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            sResponseMSG = "{\"code\":-1,\"message\":\"失败\",\"result\":\"" + e.getMessage() + "\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 物流回调接口 POST台湾绿界物流状态通知/WULIU/greenworld
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    // @Path("/WULIU")
    @Path("/WULIU/{WLTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WULIU(@Context HttpServletRequest request, @Context HttpServletResponse response,
                      @PathParam("WLTYPE") String wultype) throws Exception
    // public void WULIU(@Context HttpServletRequest request,@Context
    // HttpServletResponse response ) throws Exception
    {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":0,\"message\":\"成功\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        String dianwoda_type = "";// 点我达回调的消息类型，是URL参数里面，
        if (wultype.equals("DIANWODA")) {
            try {
                dianwoda_type = request.getParameter("type").toString();

            } catch (Exception e) {

            }
        }



        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":-1,\"message\":\"失败\",\"result\":\"" + sErrorMSG + "\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        if (wultype.equals("YTO"))
        {
            JSONObject resobject = new JSONObject();
            resobject.put("logisticProviderID", "YTO");
            resobject.put("txLogisticID", "1111111");
            resobject.put("success", true);
            sResponseMSG = resobject.toString();
        }

        try {
            // 这里需要判断一下head的method 如果为return 需要走订单退订的方法

            SWaimaiBasicService swm = new WULIUCallBackService(wultype);

            swm.setDao(dao);

            String json = jb.toString();
            if (wultype.equals("DIANWODA"))// 在body添加一个消息类型节点，否则不知道是那种消息
            {
                try {
                    JSONObject obj = new JSONObject(json);
                    obj.put("type", dianwoda_type);
                    json = obj.toString();
                } catch (Exception e) {

                }

            }


            sResponseMSG = swm.execute(json);




            json = null;
            swm = null;
        } catch (Exception e) {
            //sResponseMSG = "{\"code\":-1,\"message\":\"失败\",\"result\":\"" + e.getMessage() + "\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 闪送用的是GET方式回调的，所以要单独添加一个 物流回调接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/WULIU/{WLTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WULIU_get(@Context HttpServletRequest request, @Context HttpServletResponse response,
                          @PathParam("WLTYPE") String wultype) throws Exception {
        String sResponseMSG = "{\"code\":0,\"message\":\"成功\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        StringBuffer jb = new StringBuffer();
        String line = null;
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 闪送用的是GET方式回调的，所以要单独添加一个 物流回调接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/WULIUGET")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WULIUGET(@Context HttpServletRequest request, @Context HttpServletResponse response,
                         @QueryParam("orderno") String orderno, @QueryParam("issorderno") String issorderno,
                         @QueryParam("statuscode") String statuscode) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"code\":0,\"message\":\"成功\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":-1,\"message\":\"失败\",\"result\":\"" + sErrorMSG + "\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);
        
       /* DsmDAO dao;
        
        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");
        
        ytoService ytoS = new ytoService();
        
        Map<String, Object> setMap = new HashMap<String, Object>();
        setMap.put("APPSECRET", "u2Z1F7Fh");
        setMap.put("APPSIGNKEY", "K21000119");
        setMap.put("SHOPCODE", "oDZKFb");
        
        Map<String, Object> orderMap = new HashMap<String, Object>();
        orderMap.put("ORDERNO", System.currentTimeMillis());
        orderMap.put("SHIPPINGSHOP", "test01");
        orderMap.put("SHIPPINGSHOPNAME", "测试门店");
        orderMap.put("CONTMAN", "陶日平");
        orderMap.put("PROVINCE", "上海市");
        orderMap.put("CITY", "上海市");
        orderMap.put("COUNTY", "");
        orderMap.put("ADDRESS", "上海市场中路3328号");
        orderMap.put("CONTTEL", "18717912278");
        orderMap.put("DELMEMO", "易碎品");
        orderMap.put("LOADDOCTYPE", "WECHAT");
        orderMap.put("SHIPDATE", "20210724");
        orderMap.put("SHIPSTARTTIME", "163000");
        orderMap.put("LATITUDE", 31.9299180000);
        orderMap.put("LONGITUDE", 118.8201960000);
        
        Map<String, Object> SendShopInfoMap = new HashMap<String, Object>();
        SendShopInfoMap.put("PROVINCE", "上海市");
        SendShopInfoMap.put("CITY", "上海市");
        SendShopInfoMap.put("COUNTY", "");
        SendShopInfoMap.put("ADDRESS", "华亭路328号");
        SendShopInfoMap.put("PHONE", "18600006666");
        
        Map<String, Object> orderDetailMap = new HashMap<String, Object>();
        orderDetailMap.put("PLUNAME", "美国产AK47");
        orderDetailMap.put("PLUBARCODE", "sku01");
        orderDetailMap.put("PLUNO", "spu01");
        orderDetailMap.put("SUNITNAME", "把");
        
        Map<String, Object> orderDetailMap2 = new HashMap<String, Object>();
        orderDetailMap2.put("PLUNAME", "美国产M4A1");
        orderDetailMap2.put("PLUBARCODE", "sku02");
        orderDetailMap2.put("PLUNO", "spu02");
        orderDetailMap2.put("SUNITNAME", "把");
        
        List<Map<String, Object>> detailList = new ArrayList<Map<String,Object>>();
        detailList.add(orderDetailMap);
        detailList.add(orderDetailMap2);
        
        
        ytoS.generateKOrderCreate(setMap, orderMap, SendShopInfoMap,detailList);
        ytoS.generateKOrderCancel(setMap, orderMap);
        
        DadaService dada = new DadaService();
        setMap.put("APPSECRET", "1cf4d0cafb99b43699e57570746d62c7");
        setMap.put("APPSIGNKEY", "dada6f5846daffe8416");
        setMap.put("SHOPCODE", "73753");
        dada.addOrder(setMap, orderMap, detailList, "http://retaildev.digiwin.com.cn/dcpService_3.0/DCP/services", "2");
        */
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 7-11超商C2C更新門市通知
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("greenworldUpdateShop")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void greenworldUpdateShop(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sErrorMSG = "";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

            // System.out.println(jb);

            DsmDAO dao;

            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");


            // 排序字段處理
            Map<String, Object> maps = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

            String content = "";
            String CheckMacValue = "";

            String MerchantID = "";//
            String StoreType = "";// 01:取件門市更新 02：退件門市管寧割席
            String Status = "";// 01：門市關轉店 02：門市舊店號更新(同樣一間門市，但 是更換店號)
            // 03：退件門市為原寄件門市，但無寄件 門市資料 04：取(退)件門市臨時關轉店
            String StoreID = "";// 門店號
            String AllPayLogisticsID = "";//

            String[] splitPostData = jb.toString().split("&");
            for (int i = 0; i < splitPostData.length; i++) {
                String nodes = splitPostData[i];
                // 转义
                nodes = nodes.replaceAll("%(?![0-9a-fA-F]{2})", "%25"); // %
                nodes = nodes.replaceAll("\\+", "%2B"); // +
                nodes = URLDecoder.decode(nodes, "UTF-8");
                // System.out.println(nodes);

                String[] splitTemp = nodes.split("=");
                if (splitTemp.length > 1) {
                    if (splitTemp[0].equals("CheckMacValue") == false) {
                        // 记录
                        maps.put(splitTemp[0], splitTemp[1]);

                        if (splitTemp[0].equals("MerchantID")) {
                            MerchantID = splitTemp[1];
                        } else if (splitTemp[0].equals("StoreType")) {
                            StoreType = splitTemp[1];
                        } else if (splitTemp[0].equals("Status")) {
                            Status = splitTemp[1];
                        } else if (splitTemp[0].equals("StoreID")) {
                            StoreID = splitTemp[1];
                        } else if (splitTemp[0].equals("AllPayLogisticsID")) {
                            AllPayLogisticsID = splitTemp[1];
                        }
                    } else {
                        CheckMacValue = splitTemp[1];
                    }
                }

            }

            Iterator it = maps.entrySet().iterator();
            while (it.hasNext()) {
                // entry的输出结果如key0=value0等
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();

                // System.out.println(key +"=" + value);

                content = content + '&' + key + "=" + value;
            }

            //
            String HashKey = "";
            String HashIV = "";
            String apiUrl = "";

            if (MerchantID.equals("") == false)// 根据商店代号查询绿界物流的基本资料
            {
                String sql = "select * from OC_logistics  t where t.greenworld_merchantid='" + MerchantID
                        + "' or t.greenworld_merchantid_btoc='" + MerchantID + "' and status='100' ";
                List<Map<String, Object>> getQDataGW = dao.executeQuerySQL(sql, null);

                if (getQDataGW != null && getQDataGW.isEmpty() == false) {
                    for (Map<String, Object> map : getQDataGW) {
                        apiUrl = map.get("API_URL").toString();
                        String myMerchantID = map.get("GREENWORLD_MERCHANTID").toString();
                        if (myMerchantID.equals(MerchantID)) {
                            HashKey = map.get("GREENWORLD_HASHKEY").toString();
                            HashIV = map.get("GREENWORLD_HASHIV").toString();
                        } else {
                            HashKey = map.get("GREENWORLD_HASHKEY_BTOC").toString();
                            HashIV = map.get("GREENWORLD_HASHIV_BTOC").toString();
                        }

                        // 轉碼
                        String urlEncode = URLEncoder
                                .encode("HashKey=" + HashKey + content + "&HashIV=" + HashIV, "UTF-8").toLowerCase();
                        ;
                        // .Net url encoded string
                        urlEncode = urlEncode.replaceAll("%21", "\\!").replaceAll("%28", "\\(").replaceAll("%29",
                                "\\)");
                        // System.out.println(urlEncode);

                        // 产生檢查碼
                        String myCheckMacValue = hash(urlEncode.getBytes(), "MD5");
                        // System.out.println(myCheckMacValue);

                        // 核验检查码
                        if (myCheckMacValue.equals(CheckMacValue) == false) {
                            logger.error(
                                    "\r\n7-11超商C2C更新門市通知greenworldUpdateShop推送信息，檢查碼錯誤\r\n" + jb.toString() + "\r\n");

                            response.getOutputStream().write("|ErrorMessage".getBytes("UTF-8"));
                            return;
                        } else {
                            String sqlorder = "select * from OC_order  where greenworld_logisticsid='"
                                    + AllPayLogisticsID + "' ";
                            List<Map<String, Object>> getQData = dao.executeQuerySQL(sqlorder, null);

                            if (getQData != null && getQData.isEmpty() == false) {
                                String orderNO = getQData.get(0).get("ORDERNO").toString();
                                String eId = getQData.get(0).get("EID").toString();
                                String inShop = getQData.get(0).get("SHOPID").toString();
                                String deliveryNo = getQData.get(0).get("DELIVERYNO").toString();
                                String validno = getQData.get(0).get("GREENWORLD_VALIDNO").toString();
                                String loaddoctype = getQData.get(0).get("LOAD_DOCTYPE").toString();

                                String memo = "";

                                String statusDes = "";
                                if (Status.equals("01")) {
                                    statusDes = "門市關轉";
                                } else if (Status.equals("02")) {
                                    statusDes = "舊門店號更新";
                                } else if (Status.equals("03")) {
                                    statusDes = "無原寄件門市資料";
                                } else if (Status.equals("04")) {
                                    statusDes = "門市臨時關轉";
                                }

                                if (StoreType.equals("01"))// 取件門市更新
                                {
                                    memo = "取件門市更新為：" + StoreID + "<br/>原因：" + statusDes;
                                } else // 退件門市更新
                                {
                                    memo = "退件門市更新為：" + StoreID + "<br/>原因：" + statusDes;
                                }

                                // 訂單日誌時間
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                                String orderStatusLogTimes = dfDatetime.format(calendar.getTime());

                                // 寫一筆狀態日誌

                                // 列表SQL
                                List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                                // 接單日誌
                                String[] columnsORDER_STATUSLOG = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO",
                                        "LOAD_DOCTYPE", "STATUSTYPE", "STATUSTYPENAME", "STATUS", "STATUSNAME",
                                        "NEED_NOTIFY", "NOTIFY_STATUS", "NEED_CALLBACK", "CALLBACK_STATUS", "OPNO",
                                        "OPNAME", "UPDATE_TIME", "MEMO", "STATUS" };

                                // 接單日誌
                                DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR), new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                        new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                        new DataValue(orderNO, Types.VARCHAR), //
                                        new DataValue(loaddoctype, Types.VARCHAR), // 電商平台
                                        new DataValue("2", Types.VARCHAR), // 状态类型
                                        // //
                                        // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                        new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
                                        new DataValue("7", Types.VARCHAR), // 状态
                                        // 0需调度
                                        // 1.订单开立
                                        // 2.已接单
                                        // 3.已拒单
                                        // 4.生产接单
                                        // 5.生产拒单
                                        // 6.完工入库
                                        // 7.内部调拨
                                        // 8.待提货
                                        // 9.待配送
                                        // 10.已发货
                                        // 11.已完成
                                        // 12.已退单
                                        // 13.电商已点货
                                        // 14开始制作
                                        new DataValue("内部调拨", Types.VARCHAR), // 状态名称
                                        new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                        new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                        new DataValue(loaddoctype, Types.VARCHAR), // 操作員編碼
                                        new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                        new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                        new DataValue("配送狀態-->" + memo, Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                        new DataValue("100", Types.VARCHAR) };
                                InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                                ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                lstData.add(new DataProcessBean(ibOrderStatusLog));
                                ibOrderStatusLog = null;

                                dao.useTransactionProcessData(lstData);

                                // 調用接口
                                GreenWorld greenWorld = new GreenWorld();
                                String resBody = greenWorld.C2C_Print711UpdateStoreInfo(apiUrl, MerchantID, HashKey,
                                        HashIV, AllPayLogisticsID, deliveryNo, validno, StoreType, StoreID);

                                greenWorld = null;

                                if (resBody.equals("1|OK") == false) {
                                    logger.error("\r\n7-11超商C2C更新門市通知greenworldUpdateShop推送信息，調用更新門市接口失敗:\r\n" + resBody
                                            + "," + jb.toString() + "\r\n");

                                    response.getOutputStream().write("|ErrorMessage".getBytes("UTF-8"));
                                    return;
                                }

                                lstData = null;
                                getQData = null;
                            } else {
                                logger.error("\r\n7-11超商C2C更新門市通知greenworldUpdateShop推送信息，訂單資料找不到\r\n" + jb.toString()
                                        + "\r\n");

                                response.getOutputStream().write("|ErrorMessage".getBytes("UTF-8"));
                                return;
                            }

                        }

                        //
                        maps = null;
                        jb = null;
                        request = null;

                    }

                } else {
                    logger.error(
                            "\r\n7-11超商C2C更新門市通知greenworldUpdateShop推送信息，找不到綠界物流基本資料\r\n" + jb.toString() + "\r\n");

                    response.getOutputStream().write("|ErrorMessage".getBytes("UTF-8"));
                    return;
                }

                getQDataGW = null;

            }

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            response.getOutputStream().write(sErrorMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write("1|OK".getBytes("UTF-8"));

    }

    /**
     * 中台产品信息注册
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/syncProd")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void syncProd(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

        try {
            request.setCharacterEncoding("UTF-8");

            JsonObject tSrvJsonObject = new JsonParser().parse(request.getHeader("digi-service")).getAsJsonObject();
            String service = tSrvJsonObject.get("name").getAsString();
            String callProd = tSrvJsonObject.get("prod").getAsString();// 呼叫指定产品

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            String hostProd = "";
            String hostIP = "";
            String hostID = "";

            // 查找ERP产品节点
            List<ProdInterface> listProd = StaticInfo.psc.getT100Interface().getProdInterface();
            for (int i = 0; i < listProd.size(); i++) {
                if (callProd.equals(listProd.get(i).getHostProd().getValue())) {
                    hostProd = listProd.get(i).getHostProd().getValue();
                    hostIP = listProd.get(i).getHostIP().getValue();
                    hostID = listProd.get(i).getHostID().getValue();
                    break;
                }
            }

            JsonObject tHostObject = new JsonObject();
            tHostObject.addProperty("prod", hostProd);
            tHostObject.addProperty("ver", "3.0");
            tHostObject.addProperty("ip", hostIP);
            tHostObject.addProperty("id", hostID);

            // 取得產品註冊訊息
            if (service.equals("getProdRegInfo")) {
                JsonObject tJsonObject = new JsonObject();

                // 可选参数
                tHostObject.addProperty("timezone", "+8");
                // tHostObject.addProperty("wsdl", "http://" + hostIP +
                // ":63082/TestSrv?wsdl");
                tHostObject.addProperty("resturl", "http://" + hostIP + ":8080/services/jaxrs/sposService/");
                tHostObject.addProperty("retrytimes", "1");
                tHostObject.addProperty("retryinterval", "500");

                tJsonObject.add("host", tHostObject);

                // System.out.println(tJsonObject.toString());

                response.setHeader("digi-srvcode", "000");
                response.setHeader("digi-action", "reg");// 服务返回状态码 000：正常流程执行成功
                // 100：服务未执行成功

                response.getOutputStream().write(tJsonObject.toString().getBytes("UTF-8"));

                tJsonObject = null;
                tHostObject = null;
                listProd = null;
                request = null;

                return;
            }

            // 取得服務註冊訊息
            if (service.equals("getSrvRegInfo")) {
                JsonObject tJsonObject = new JsonObject();

                JsonObject tService = new JsonObject();

                JsonArray tServiceNames = new JsonArray();

                JsonArray tSrvconcs = new JsonArray();// 各服务同时连线数

                List<Value> lstOPS = StaticInfo.psc.getServiceInterface().getOPSService();
                for (int i = 0; i < lstOPS.size(); i++) {
                    tServiceNames.add(new JsonParser().parse(lstOPS.get(i).getValue()));

                    tSrvconcs.add(new JsonParser().parse("\"" + lstOPS.get(i).getValue() + ";1000\""));
                }

                tService.add("srvname", tServiceNames);

                tService.add("srvconc", tSrvconcs);

                tJsonObject.add("host", tHostObject);
                tJsonObject.add("service", tService);

                // System.out.println(tJsonObject.toString());

                response.setHeader("digi-srvcode", "000");
                response.setHeader("digi-action", "reg");// 服务返回状态码 000：正常流程执行成功
                // 100：服务未执行成功

                response.getOutputStream().write(tJsonObject.toString().getBytes("UTF-8"));

                //
                tJsonObject = null;
                tService = null;
                tSrvconcs = null;
                tServiceNames = null;
                tJsonObject = null;
                tHostObject = null;
                listProd = null;
                request = null;

                return;
            }

            /*
             * 目前还不能做，中台还不完善 //接收整合同步資訊(这个主要是为了更新产品信息的,防止手动更改后，对方不知道) if
             * (service.equals("doSyncProcess")) { jb = new StringBuffer();
             * BufferedReader reader =new BufferedReader(new
             * InputStreamReader((ServletInputStream)request.getInputStream(),
             * "UTF-8")); while ((line = reader.readLine()) != null)
             * jb.append(line);
             *
             * JSONObject jsonres = new JSONObject(jb.toString());
             *
             * //中台cross 如果本地配置的CROSS地址与此不同需更新 JSONObject
             * json_Cross=jsonres.getJSONObject("cross"); String
             * cross_resturl=json_Cross.getString("resturl");
             *
             * //产品信息 JSONArray json_ERP=jsonres.getJSONArray("prod");
             *
             * Document doc=new SAXReader().read(new
             * File(this.getClass().getResource("/config/PlugInService.xml").
             * getPath()));
             *
             * Element root =
             * doc.getRootElement().element("T100Transferm").element(
             * "ServiceProd"); //System.out.println("\r\n" +
             * root.attribute("value"));
             *
             * for (int i = 0; i < json_ERP.length(); i++) { JSONObject jsonTemp
             * = new JSONObject(json_ERP.get(i).toString()); String
             * opsProd=jsonTemp.getString("name"); //ERP产品
             * if(opsProd.equals("T100")) {
             *
             * }
             *
             * //门店产品 if(opsProd.equals("POSSERVER")) {
             *
             * } } }
             */

        } catch (Exception e) {
            String sErrorMSG = e.getMessage();// 赋值

            // System.out.println(sErrorMSG);

            response.setHeader("digi-srvcode", "100");// 服务返回状态码 000：正常流程执行成功
            // 100：服务未执行成功
            String errJson = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"" + sErrorMSG
                    + "\"}";

            TransferJsonUtils tfju = new TransferJsonUtils();
            errJson = tfju.transferT100JsonMsg(errJson);
            tfju = null;

            response.getOutputStream().write(errJson.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 电商推送接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/ecpush/{ECTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void ecpush(@Context HttpServletRequest request, @Context HttpServletResponse response,
                       @PathParam("ECTYPE") String ectype) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";

        StringBuffer jb = new StringBuffer();
        String line = null;

        try {
            request.setCharacterEncoding("UTF-8");

            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

            // 虾皮网
            if (ectype.toLowerCase().equals("shopee")) {
                logger.info("\r\n虾皮推送信息=" + jb.toString() + "\r\n");

                String Host = request.getHeader("Host");
                String Authorization = request.getHeader("Authorization");
                // 这里可以做验证

                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");

                // {"code":0,"data":{"verify_info":"This is a Verification
                // message.Please respond in the certain format."}}
                JSONObject jsonres = new JSONObject(jb.toString());

                if (jsonres.has("code")) {
                    int code = jsonres.getInt("code");

                    // 接單日誌
                    String[] columnsORDER_STATUSLOG = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO",
                            "LOAD_DOCTYPE", "STATUSTYPE", "STATUSTYPENAME", "STATUS", "STATUSNAME", "NEED_NOTIFY",
                            "NOTIFY_STATUS", "NEED_CALLBACK", "CALLBACK_STATUS", "OPNO", "OPNAME", "UPDATE_TIME",
                            "MEMO", "STATUS" };

                    // 0:测试验证
                    if (code == 0) {
                        // http status code empty body
                        response.setStatus(202);
                        response.flushBuffer();
                    }
                    // 4:接收托运单号
                    else if (code == 4)
                    {
                        JSONObject dataInf = jsonres.getJSONObject("data");
                        String ordersn = dataInf.getString("ordersn");
                        String trackingno = dataInf.getString("trackingno");

                        DsmDAO dao = null;

                        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

                        String sql = "select EID,SHOPID from OC_ORDER where ORDERNO='" + ordersn + "' ";
                        List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);

                        String eId = "";
                        String inShop = "";
                        if (getQData != null && getQData.isEmpty() == false)
                        {
                            eId = getQData.get(0).get("EID").toString();
                            inShop = getQData.get(0).get("SHOPID").toString();

                            // 訂單日誌時間
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                            String orderStatusLogTimes = dfDatetime.format(calendar.getTime());

                            // 列表SQL
                            List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                            // 更新发货表
                            UptBean ubec = new UptBean("DCP_shipment");
                            ubec.addCondition("EC_ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                            ubec.addCondition("ECPLATFORMNO", new DataValue("shopee", Types.VARCHAR));

                            ubec.addUpdateValue("EXPRESSNO", new DataValue(trackingno, Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubec));
                            ubec = null;

                            // 更新订单表
                            ubec = new UptBean("OC_ORDER");
                            ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                            ubec.addCondition("LOAD_DOCTYPE", new DataValue("shopee", Types.VARCHAR));

                            ubec.addUpdateValue("DELIVERYNO", new DataValue(trackingno, Types.VARCHAR));
                            ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue("0", Types.VARCHAR));// -1预下单
                            // 0
                            // 已下单
                            // 1
                            // 接单，2=取件，3=签收，4=物流取消或异常
                            // 5=手动撤销
                            // 6
                            // 到店
                            // 7重下单

                            lstData.add(new DataProcessBean(ubec));
                            ubec = null;

                            // 接單日誌
                            DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                    new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                    new DataValue(ordersn, Types.VARCHAR), //
                                    new DataValue("shopee", Types.VARCHAR), // 電商平台
                                    new DataValue("2", Types.VARCHAR), // 状态类型
                                    // //
                                    // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                    new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
                                    new DataValue("9", Types.VARCHAR), // 状态
                                    // 0需调度
                                    // 1.订单开立
                                    // 2.已接单
                                    // 3.已拒单
                                    // 4.生产接单
                                    // 5.生产拒单
                                    // 6.完工入库
                                    // 7.内部调拨
                                    // 8.待提货
                                    // 9.待配送
                                    // 10.已发货
                                    // 11.已完成
                                    // 12.已退单
                                    // 13.电商已点货
                                    // 14开始制作
                                    new DataValue("待配送", Types.VARCHAR), // 状态名称
                                    new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                    new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                    new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                    new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                    new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                    new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                    new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                    new DataValue("配送狀態-->待配送", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                    new DataValue("100", Types.VARCHAR) };
                            InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                            ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                            lstData.add(new DataProcessBean(ibOrderStatusLog));
                            ibOrderStatusLog = null;

                            dao.useTransactionProcessData(lstData);

                            lstData = null;

                        }

                        dao = null;

                        getQData = null;

                        response.setStatus(202);
                        response.flushBuffer();
                    }
                    // 订单状态推送
                    else if (code == 3) {
                        JSONObject dataInf = jsonres.getJSONObject("data");
                        String ordersn = dataInf.getString("ordersn");
                        // 订单状态 IN_CANCEL买家申请取消，等待卖家同意或拒绝 CANCELLED 已取消
                        // COMPLETED 已完成 TO_RETURN 买家申请退货 SHIPPED 發貨接口成功後
                        String sStatus = dataInf.getString("status");

                        DsmDAO dao = null;

                        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

                        String sql = "select EID,SHOPID from OC_ORDER where ORDERNO='" + ordersn + "' ";
                        List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);

                        String eId = "";
                        String inShop = "";
                        if (getQData != null && getQData.isEmpty() == false) {
                            eId = getQData.get(0).get("EID").toString();
                            inShop = getQData.get(0).get("SHOPID").toString();

                            // 訂單日誌時間
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                            String orderStatusLogTimes = dfDatetime.format(calendar.getTime());

                            // 列表SQL
                            List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                            // 订单状态 IN_CANCEL买家申请取消，等待卖家同意或拒绝 CANCELLED 已取消
                            // COMPLETED 已完成 TO_RETURN 买家申请退货 SHIPPED 已發貨
                            if (sStatus.equals("COMPLETED")) {
                                // 更新发货表
                                UptBean ubec = new UptBean("DCP_shipment");
                                ubec.addCondition("EC_ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("ECPLATFORMNO", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));// 1:新建
                                // 2：已发货
                                // 3：退货
                                // 4：换货
                                // 5：已安排物流取件
                                // 6：已完成
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 更新订单表
                                ubec = new UptBean("OC_ORDER");
                                ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("LOAD_DOCTYPE", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
                                // 1.订单开立
                                // 2.已接单
                                // 3.已拒单
                                // 4.生产接单
                                // 5.生产拒单
                                // 6.完工入库
                                // 7.内部调拨
                                // 8.待提货
                                // 9.待配送
                                // 10.已发货
                                // 11.已完成
                                // 12.已退单
                                // 13.电商已点货
                                // 14开始制作
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 接單日誌
                                DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                        new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                        new DataValue(ordersn, Types.VARCHAR), //
                                        new DataValue("shopee", Types.VARCHAR), // 電商平台
                                        new DataValue("1", Types.VARCHAR), // 状态类型
                                        // //
                                        // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                        new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
                                        new DataValue("1", Types.VARCHAR), // 状态
                                        // 0需调度
                                        // 1.订单开立
                                        // 2.已接单
                                        // 3.已拒单
                                        // 4.生产接单
                                        // 5.生产拒单
                                        // 6.完工入库
                                        // 7.内部调拨
                                        // 8.待提货
                                        // 9.待配送
                                        // 10.已发货
                                        // 11.已完成
                                        // 12.已退单
                                        // 13.电商已点货
                                        // 14开始制作
                                        new DataValue("已完成", Types.VARCHAR), // 状态名称
                                        new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                        new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                        new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                        new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                        new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                        new DataValue("訂單状态-->已完成", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                        new DataValue("100", Types.VARCHAR) };
                                InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                                ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                lstData.add(new DataProcessBean(ibOrderStatusLog));
                                ibOrderStatusLog = null;

                                dao.useTransactionProcessData(lstData);

                                dao = null;

                                response.setStatus(202);
                                response.flushBuffer();
                            } else if (sStatus.equals("SHIPPED"))// SHIPPED 已發貨
                            {
                                // 更新发货表
                                UptBean ubec = new UptBean("DCP_shipment");
                                ubec.addCondition("EC_ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("ECPLATFORMNO", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("STATUS", new DataValue(2, Types.INTEGER));// 1:新建
                                // 2：已发货
                                // 3：退货
                                // 4：换货
                                // 5：已安排物流取件
                                // 6：已完成
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 更新订单表
                                ubec = new UptBean("OC_ORDER");
                                ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("LOAD_DOCTYPE", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));// 0需调度
                                // 1.订单开立
                                // 2.已接单
                                // 3.已拒单
                                // 4.生产接单
                                // 5.生产拒单
                                // 6.完工入库
                                // 7.内部调拨
                                // 8.待提货
                                // 9.待配送
                                // 10.已发货
                                // 11.已完成
                                // 12.已退单
                                // 13.电商已点货
                                // 14开始制作
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 接單日誌
                                DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                        new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                        new DataValue(ordersn, Types.VARCHAR), //
                                        new DataValue("shopee", Types.VARCHAR), // 電商平台
                                        new DataValue("1", Types.VARCHAR), // 状态类型
                                        // //
                                        // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                        new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
                                        new DataValue("10", Types.VARCHAR), // 状态
                                        // 0需调度
                                        // 1.订单开立
                                        // 2.已接单
                                        // 3.已拒单
                                        // 4.生产接单
                                        // 5.生产拒单
                                        // 6.完工入库
                                        // 7.内部调拨
                                        // 8.待提货
                                        // 9.待配送
                                        // 10.已发货
                                        // 11.已完成
                                        // 12.已退单
                                        // 13.电商已点货
                                        // 14开始制作
                                        new DataValue("已發貨", Types.VARCHAR), // 状态名称
                                        new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                        new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                        new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                        new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                        new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                        new DataValue("訂單状态-->已發貨", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                        new DataValue("100", Types.VARCHAR) };
                                InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                                ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                lstData.add(new DataProcessBean(ibOrderStatusLog));
                                ibOrderStatusLog = null;

                                dao.useTransactionProcessData(lstData);

                                dao = null;

                                response.setStatus(202);
                                response.flushBuffer();
                            } else if (sStatus.equals("IN_CANCEL")) // IN_CANCEL买家申请取消，等待卖家同意或拒绝
                            {
                                // 更新订单表
                                UptBean ubec = new UptBean("OC_ORDER");
                                ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("LOAD_DOCTYPE", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("REFUNDSTATUS", new DataValue("11", Types.VARCHAR));// 1.未申请
                                // 2.用户申请退单
                                // 3.拒绝退单
                                // 4.客服仲裁中
                                // 5.退单失败
                                // 6.退单成功
                                // 7.用户申请部分退款
                                // 8.拒绝部分退款
                                // 9
                                // 部分退款失败
                                // 10.部分退款成功
                                // 11.用户申请取消订单
                                // 12.取消订单失败
                                // 13.取消订单成功
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 接單日誌
                                DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                        new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                        new DataValue(ordersn, Types.VARCHAR), //
                                        new DataValue("shopee", Types.VARCHAR), // 電商平台
                                        new DataValue("1", Types.VARCHAR), // 状态类型
                                        // //
                                        // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                        new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
                                        new DataValue("0", Types.VARCHAR), // 状态
                                        // 0需调度
                                        // 1.订单开立
                                        // 2.已接单
                                        // 3.已拒单
                                        // 4.生产接单
                                        // 5.生产拒单
                                        // 6.完工入库
                                        // 7.内部调拨
                                        // 8.待提货
                                        // 9.待配送
                                        // 10.已发货
                                        // 11.已完成
                                        // 12.已退单
                                        // 13.电商已点货
                                        // 14开始制作
                                        new DataValue("買家申請取消訂單", Types.VARCHAR), // 状态名称
                                        new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                        new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                        new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                        new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                        new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                        new DataValue("訂單狀態-->買家申請取消訂單", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                        new DataValue("100", Types.VARCHAR) };
                                InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                                ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                lstData.add(new DataProcessBean(ibOrderStatusLog));
                                ibOrderStatusLog = null;

                                dao.useTransactionProcessData(lstData);

                                dao = null;

                                response.setStatus(202);
                                response.flushBuffer();
                            } else if (sStatus.equals("CANCELLED"))// CANCELLED
                            // 已取消
                            {
                                // 更新订单表
                                UptBean ubec = new UptBean("OC_ORDER");
                                ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                ubec.addCondition("LOAD_DOCTYPE", new DataValue("shopee", Types.VARCHAR));

                                ubec.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));// 0需调度
                                // 1.订单开立
                                // 2.已接单
                                // 3.已拒单
                                // 4.生产接单
                                // 5.生产拒单
                                // 6.完工入库
                                // 7.内部调拨
                                // 8.待提货
                                // 9.待配送
                                // 10.已发货
                                // 11.已完成
                                // 12.已退单
                                // 13.电商已点货
                                // 14开始制作
                                ubec.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));// 1.未申请
                                // 2.用户申请退单
                                // 3.拒绝退单
                                // 4.客服仲裁中
                                // 5.退单失败
                                // 6.退单成功
                                // 7.用户申请部分退款
                                // 8.拒绝部分退款
                                // 9
                                // 部分退款失败
                                // 10.部分退款成功
                                // 11.用户申请取消订单
                                // 12.取消订单失败
                                // 13.取消订单成功
                                lstData.add(new DataProcessBean(ubec));
                                ubec = null;

                                // 接單日誌
                                DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                        new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                        new DataValue(ordersn, Types.VARCHAR), //
                                        new DataValue("shopee", Types.VARCHAR), // 電商平台
                                        new DataValue("1", Types.VARCHAR), // 状态类型
                                        // //
                                        // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                        new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
                                        new DataValue("12", Types.VARCHAR), // 状态
                                        // 0需调度
                                        // 1.订单开立
                                        // 2.已接单
                                        // 3.已拒单
                                        // 4.生产接单
                                        // 5.生产拒单
                                        // 6.完工入库
                                        // 7.内部调拨
                                        // 8.待提货
                                        // 9.待配送
                                        // 10.已发货
                                        // 11.已完成
                                        // 12.已退单
                                        // 13.电商已点货
                                        // 14开始制作
                                        new DataValue("訂單已取消", Types.VARCHAR), // 状态名称
                                        new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                        new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                        new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                        new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                        new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                        new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                        new DataValue("訂單狀態-->訂單已取消", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                        new DataValue("100", Types.VARCHAR) };
                                InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
                                ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                lstData.add(new DataProcessBean(ibOrderStatusLog));
                                ibOrderStatusLog = null;

                                dao.useTransactionProcessData(lstData);

                                dao = null;

                                response.setStatus(202);
                                response.flushBuffer();
                            } else if (sStatus.equals("TO_RETURN")) // TO_RETURN
                            // 买家申请退货
                            {
                                String sqlECOMMERCE = "select * from OC_ECOMMERCE where ECPLATFORMNO='shopee' and status='100' ";
                                List<Map<String, Object>> getQECOMMERCE = dao.executeQuerySQL(sqlECOMMERCE, null);
                                if (getQECOMMERCE != null && getQECOMMERCE.isEmpty() == false) {
                                    for (Map<String, Object> map : getQECOMMERCE) {
                                        String apiUrl = map.get("API_URL").toString();
                                        int partner_id = Integer.parseInt(map.get("PARTNER_ID").toString());
                                        int shop_id = Integer.parseInt(map.get("STORE_ID").toString());
                                        String partner_key = map.get("PARTNER_KEY").toString();

                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DAY_OF_MONTH, -14);// 固定减14天,最多才半个月
                                        long create_time_from = c.getTime().getTime() / 1000;

                                        long lEndTimestamp = System.currentTimeMillis() / 1000;

                                        Shopee shopee = new Shopee();
                                        String resBody = shopee.GetReturnList(apiUrl, partner_id, partner_key, shop_id,
                                                0, create_time_from, lEndTimestamp);

                                        JSONObject jsonReturnres = new JSONObject(resBody);

                                        shopee = null;

                                        if (jsonReturnres.has("error"))// 错误
                                        {
                                            String errorno = jsonReturnres.getString("error");
                                            String errormsg = jsonReturnres.getString("msg");

                                            logger.error("\r\n虾皮推送卖家退货申请，GetReturnList接口调用失败:" + resBody);

                                            continue;
                                        } else {
                                            boolean more = jsonReturnres.getBoolean("more");// 是否还有更多页
                                            String request_id = jsonReturnres.getString("request_id");// 错误追踪ID

                                            boolean bFindOrdersn = false;

                                            // 订单单头列表Object []
                                            JSONArray returns = jsonReturnres.getJSONArray("returns");
                                            for (int i = 0; i < returns.length(); i++) {
                                                String myordersn = returns.getJSONObject(i).getString("ordersn");// 订单号

                                                // 定位订单信息
                                                if (myordersn.equals(ordersn)) {
                                                    bFindOrdersn = true;

                                                    long returnsn = returns.getJSONObject(i).getLong("returnsn");// 退货编号
                                                    String reason = returns.getJSONObject(i).getString("reason");// 原因码
                                                    String text_reason = returns.getJSONObject(i)
                                                            .getString("text_reason");// 原因
                                                    double refund_amount = returns.getJSONObject(i)
                                                            .getDouble("refund_amount");// 退款金额

                                                    JSONObject user = returns.getJSONObject(i).getJSONObject("user");
                                                    String email = user.get("email").toString();
                                                    String username = user.get("username").toString();

                                                    StringBuffer imageUrl = new StringBuffer("");
                                                    JSONArray images = returns.getJSONObject(i).getJSONArray("images");
                                                    for (int a = 0; a < images.length(); a++) {
                                                        if (a == images.length() - 1) {
                                                            imageUrl.append(images.getString(a));
                                                        } else {
                                                            imageUrl.append(images.getString(a) + "@@@");
                                                        }
                                                    }

                                                    long update_time = returns.getJSONObject(i).getLong("update_time");// 更新时间
                                                    // System.out.println(ordersn);

                                                    // 更新发货表
                                                    UptBean ubec = new UptBean("DCP_shipment");
                                                    ubec.addCondition("EC_ORDERNO",
                                                            new DataValue(ordersn, Types.VARCHAR));
                                                    ubec.addCondition("ECPLATFORMNO",
                                                            new DataValue("shopee", Types.VARCHAR));

                                                    ubec.addUpdateValue("STATUS", new DataValue(3, Types.INTEGER));// 1:新建
                                                    // 2：已发货
                                                    // 3：退货
                                                    // 4：换货
                                                    // 5：已安排物流取件
                                                    // 6：已完成
                                                    lstData.add(new DataProcessBean(ubec));
                                                    ubec = null;

                                                    // 更新订单表
                                                    ubec = new UptBean("DCP_ORDER");
                                                    ubec.addCondition("ORDERNO", new DataValue(ordersn, Types.VARCHAR));
                                                    ubec.addCondition("LOAD_DOCTYPE",
                                                            new DataValue("shopee", Types.VARCHAR));

                                                    ubec.addUpdateValue("REFUNDSTATUS",
                                                            new DataValue("2", Types.VARCHAR));// 1.未申请
                                                    // 2.用户申请退单
                                                    // 3.拒绝退单
                                                    // 4.客服仲裁中
                                                    // 5.退单失败
                                                    // 6.退单成功
                                                    // 7.用户申请部分退款
                                                    // 8.拒绝部分退款
                                                    // 9
                                                    // 部分退款失败
                                                    // 10.部分退款成功
                                                    // 11.用户申请取消订单
                                                    // 12.取消订单失败
                                                    // 13.取消订单成功
                                                    ubec.addUpdateValue("REFUNDREASONNO",
                                                            new DataValue(reason, Types.VARCHAR));
                                                    ubec.addUpdateValue("REFUNDREASON",
                                                            new DataValue(text_reason, Types.VARCHAR));
                                                    ubec.addUpdateValue("RETURNSN",
                                                            new DataValue(returnsn, Types.VARCHAR));
                                                    ubec.addUpdateValue("RETURNUSERNAME",
                                                            new DataValue(username, Types.VARCHAR));
                                                    ubec.addUpdateValue("RETURNEMAIL",
                                                            new DataValue(email, Types.VARCHAR));
                                                    ubec.addUpdateValue("RETURNIMAGEURL",
                                                            new DataValue(imageUrl.toString(), Types.VARCHAR));
                                                    ubec.addUpdateValue("REFUNDAMT",
                                                            new DataValue(refund_amount, Types.FLOAT));
                                                    lstData.add(new DataProcessBean(ubec));
                                                    ubec = null;

                                                    // 接單日誌
                                                    DataValue[] insValueOrderStatus_LOG = new DataValue[] {
                                                            new DataValue(eId, Types.VARCHAR),
                                                            new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
                                                            new DataValue(inShop, Types.VARCHAR), // 映射后的门店
                                                            new DataValue(ordersn, Types.VARCHAR), //
                                                            new DataValue("shopee", Types.VARCHAR), // 電商平台
                                                            new DataValue("1", Types.VARCHAR), // 状态类型
                                                            // //
                                                            // 1-订单状态，2-配送状态，3-退单状态，4-其他
                                                            new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
                                                            new DataValue("12", Types.VARCHAR), // 状态
                                                            // 0需调度
                                                            // 1.订单开立
                                                            // 2.已接单
                                                            // 3.已拒单
                                                            // 4.生产接单
                                                            // 5.生产拒单
                                                            // 6.完工入库
                                                            // 7.内部调拨
                                                            // 8.待提货
                                                            // 9.待配送
                                                            // 10.已发货
                                                            // 11.已完成
                                                            // 12.已退单
                                                            // 13.电商已点货
                                                            // 14开始制作
                                                            new DataValue("買家申請退貨", Types.VARCHAR), // 状态名称
                                                            new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
                                                            new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
                                                            new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
                                                            new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
                                                            new DataValue("shopee", Types.VARCHAR), // 操作員編碼
                                                            new DataValue("管理員", Types.VARCHAR), // 操作員名稱
                                                            new DataValue(orderStatusLogTimes, Types.VARCHAR), // yyyyMMddHHmmssSSS
                                                            new DataValue("訂單狀態-->買家申請退貨", Types.VARCHAR), // 類型名稱+"-->"+狀態名稱
                                                            new DataValue("100", Types.VARCHAR) };
                                                    InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG",
                                                            columnsORDER_STATUSLOG);
                                                    ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
                                                    lstData.add(new DataProcessBean(ibOrderStatusLog));
                                                    ibOrderStatusLog = null;

                                                    dao.useTransactionProcessData(lstData);

                                                    dao = null;

                                                    response.setStatus(202);
                                                    response.flushBuffer();

                                                    break;
                                                }
                                            }

                                            if (bFindOrdersn == false) {
                                                getQECOMMERCE = null;
                                                logger.error("\r\n虾皮推送卖家退货申请，找不到退货订单！" + ordersn);
                                                continue;
                                            }

                                        }
                                    }

                                } else {
                                    getQECOMMERCE = null;
                                    logger.error("\r\n虾皮推送卖家退货申请，虾皮基本资料未设置！");
                                    return;
                                }

                                getQECOMMERCE = null;

                            }

                            lstData = null;
                        }
                        getQData = null;

                    }

                }

            } else if (ectype.toLowerCase().equals("deliveroo"))// Deliveroo外卖
            {
                logger.info("\r\ndeliveroo推送信息=" + jb.toString() + "\r\n");

                DsmDAO dao = null;

                dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");


                SWaimaiBasicService swm = new WMHHSService();
                swm.setDao(dao);

                String json = jb.toString();

                swm.execute(json);

                if (dao != null) {
                    dao.closeDAO();
                }

                // http status code empty body
                response.setStatus(202);
                response.flushBuffer();

                //
                json = null;
                swm = null;
                jb = null;
                request = null;

                return;

            }
            else if (ectype.toLowerCase().equals("sy"))// 商有云管家
            {
                logger.info("\r\nshangyou商有云管家推送信息=" + jb.toString() + "\r\n");

                DsmDAO dao = null;

                dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");


                SWaimaiBasicService swm = new ShangyouService();
                swm.setDao(dao);

                String json = jb.toString();

                swm.execute(json);

                if (dao != null) {
                    dao.closeDAO();
                }

                JSONObject header = new JSONObject();
                header.put("errorCode", "000");
                header.put("errorMsg", "操作成功");
                String resInfoString = header.toString();
                response.getOutputStream().write(resInfoString.getBytes("UTF-8"));

                //
                json = null;
                swm = null;
                jb = null;
                request = null;

                return;
            }

        } catch (Exception e) {
            bSuccess = false;

            sErrorMSG = e.getMessage();// 赋值

            JSONObject header = new JSONObject();
            header.put("code", 500);
            header.put("message", sErrorMSG);
            String resInfoString = header.toString();

            response.getOutputStream().write(resInfoString.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * GET便利达康物流电子地图选门店ecmapget/cvs
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/ecmapget/{ECTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void ecmapget(@Context HttpServletRequest request, @Context HttpServletResponse response,
                         @PathParam("ECTYPE") String ectype) throws Exception {
        try {
            request.setCharacterEncoding("UTF-8");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            String sysDate = sdf.format(calendar.getTime());
            //
            sdf = new SimpleDateFormat("HHmmss");
            String sysTime = sdf.format(calendar.getTime());

            String distributorno = "";
            String distributorname = "";
            String tel = "";

            // 门店编码6码
            String getshopNo = "";
            // 门店名称
            String getshopName = "";
            // 地址
            String getshopAddress = "";
            // 原值回传
            String tempvar = "";
            // 订单编号
            String orderno = "";
            String eId = "";
            String shopId = "";

            String timeno = "";

            if (ectype.toLowerCase().equals("cvs")) {
                // 由 企业编码@@@门店编号
                tempvar = request.getParameter("cvstemp");

                String[] splitStr = tempvar.split("@@@");

                eId = splitStr[0];
                shopId = splitStr[1];
                timeno = splitStr[2];

                // 订单编号
                orderno = request.getParameter("cvsid");
                // 带通路商代码+门店编号
                getshopNo = request.getParameter("cvsspot").substring(1);

                distributorno = request.getParameter("cvsspot").substring(0, 1);
                distributorname = "";
                // 代码(F：全家、L：萊爾富、K：OK)
                if (distributorno.equals("F")) {
                    distributorname = "全家";
                } else if (distributorno.equals("L")) {
                    distributorname = "萊爾富";
                } else if (distributorno.equals("K")) {
                    distributorname = "OK";
                }

                // 门店名称
                getshopName = request.getParameter("name");
                // 电话
                tel = request.getParameter("tel");
                // 地址
                getshopAddress = request.getParameter("addr");

                logger.info("\r\n物流电子地图选门店信息,企业编码" + eId + ",门店编号=" + shopId + ",订单号=" + orderno + "\r\n");

            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            DsmDAO dao = null;

            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

            // 列表SQL
            List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

            String sqlExist = "select t.orderno from OC_lgmap t where t.EID='" + eId + "' and t.SHOPID='"
                    + shopId + "' and t.orderno='" + orderno + "' and t.timeno='" + timeno + "' ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sqlExist, null);
            if (getQData == null || getQData.isEmpty()) {
                // 插入明细
                String[] columnsMap = { "EID", "SHOPID", "ORDERNO", "TIMENO", "DISTRIBUTORNO", "DISTRIBUTORNAME",
                        "GETSHOPNO", "GETSHOPNAME", "TEL", "ADDRESS", "SDATE", "STIME", "STATUS" };
                DataValue[] insValueOrderDetail = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR), new DataValue(orderno, Types.VARCHAR),
                        new DataValue(timeno, Types.VARCHAR), new DataValue(distributorno, Types.VARCHAR),
                        new DataValue(distributorname, Types.VARCHAR), new DataValue(getshopNo, Types.VARCHAR),
                        new DataValue(getshopName, Types.VARCHAR), new DataValue(tel, Types.VARCHAR),
                        new DataValue(getshopAddress, Types.VARCHAR), new DataValue(sysDate, Types.VARCHAR),
                        new DataValue(sysTime, Types.VARCHAR), new DataValue("100", Types.VARCHAR), };

                InsBean ibOrderDetail = new InsBean("OC_LGMAP", columnsMap);
                ibOrderDetail.addValues(insValueOrderDetail);
                lstData.add(new DataProcessBean(ibOrderDetail));

                ibOrderDetail = null;
            } else {
                //
                UptBean ubec = new UptBean("OC_LGMAP");
                ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ubec.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
                ubec.addCondition("TIMENO", new DataValue(timeno, Types.VARCHAR));

                ubec.addUpdateValue("DISTRIBUTORNO", new DataValue(distributorno, Types.VARCHAR));
                ubec.addUpdateValue("DISTRIBUTORNAME", new DataValue(distributorname, Types.VARCHAR));
                ubec.addUpdateValue("GETSHOPNO", new DataValue(getshopNo, Types.VARCHAR));
                ubec.addUpdateValue("GETSHOPNAME", new DataValue(getshopName, Types.VARCHAR));
                ubec.addUpdateValue("TEL", new DataValue(tel, Types.VARCHAR));
                ubec.addUpdateValue("ADDRESS", new DataValue(getshopAddress, Types.VARCHAR));
                ubec.addUpdateValue("SDATE", new DataValue(sysDate, Types.VARCHAR));
                ubec.addUpdateValue("STIME", new DataValue(sysTime, Types.VARCHAR));

                lstData.add(new DataProcessBean(ubec));

                ubec = null;
            }

            //
            dao.useTransactionProcessData(lstData);

            dao = null;

            //
            lstData = null;
            getQData = null;
            request = null;

            response.setStatus(202);
            response.flushBuffer();

        } catch (Exception e) {

            JSONObject header = new JSONObject();
            header.put("code", 500);
            header.put("message", e.getMessage());
            String resInfoString = header.toString();

            response.getOutputStream().write(resInfoString.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * POST大智通物流电子地图选门店ecmappost/dzt POST綠界選門店ecmappost/greenworld
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/ecmappost/{ECTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void ecmappost(@Context HttpServletRequest request, @Context HttpServletResponse response,
                          @PathParam("ECTYPE") String ectype) throws Exception {
        try {
            request.setCharacterEncoding("UTF-8");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            String sysDate = sdf.format(calendar.getTime());
            //
            sdf = new SimpleDateFormat("HHmmss");
            String sysTime = sdf.format(calendar.getTime());

            String distributorno = "";
            String distributorname = "";
            String tel = "";

            // 门店编码6码
            String getshopNo = "";
            // 门店名称
            String getshopName = "";
            // 地址
            String getshopAddress = "";
            // 原值回传
            String tempvar = "";
            // 订单编号
            String orderno = "";
            String eId = "";
            String shopId = "";
            String timeno = "";

            // 大智通
            if (ectype.toLowerCase().equals("dzt")) {

                distributorno = "7";
                distributorname = "7-11";
                tel = "";

                // 门店编码6码
                getshopNo = request.getParameter("storeid");
                // 门店名称
                getshopName = request.getParameter("storename");
                // 地址
                getshopAddress = request.getParameter("address");
                // 由 订单号@@@企业编码@@@门店编号@@@时间戳 组成
                tempvar = request.getParameter("tempvar");

                String[] splitStr = tempvar.split("@@@");

                // 订单编号
                orderno = splitStr[0];
                eId = splitStr[1];
                shopId = splitStr[2];
                timeno = splitStr[3];

                logger.info("\r\n物流电子地图选门店信息,企业编码" + eId + ",门店编号=" + shopId + ",订单号=" + orderno + "\r\n");

            }
            // 台灣綠界
            else if (ectype.toLowerCase().equals("greenworld")) {
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));

                    while ((line = reader.readLine()) != null)
                        jb.append(line);
                } catch (Exception e) {

                    logger.error("\r\n台湾绿界选门店接收报错：" + e.getMessage() + "\r\n");
                }

                String paraString = jb.toString();

                // logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"绿界地图："+paraString);

                String[] splitPostData = paraString.split("&");

                String MerchantID = "";
                String MerchantTradeNo = "";
                String LogisticsSubType = "";
                String CVSStoreID = "";
                String CVSStoreName = "";
                String CVSAddress = "";
                String CVSTelephone = "";// 7-11无此栏位
                String CVSOutSide = "";// 0:本島 1:離島 7-11才有此栏位
                String ExtraData = "";

                for (int i = 0; i < splitPostData.length; i++) {
                    String nodes = splitPostData[i];
                    // 转义
                    nodes = nodes.replaceAll("%(?![0-9a-fA-F]{2})", "%25"); // %
                    nodes = nodes.replaceAll("\\+", "%2B"); // +
                    nodes = URLDecoder.decode(nodes, "UTF-8");
                    // System.out.println(nodes);
                    if (nodes.startsWith("MerchantID")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            MerchantID = splitTemp[1];
                        }
                    } else if (nodes.startsWith("MerchantTradeNo")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            MerchantTradeNo = splitTemp[1];
                        }
                    } else if (nodes.startsWith("LogisticsSubType")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            LogisticsSubType = splitTemp[1];
                        }
                    } else if (nodes.startsWith("CVSStoreID")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            CVSStoreID = splitTemp[1];
                        }
                    } else if (nodes.startsWith("CVSStoreName")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            CVSStoreName = splitTemp[1];
                        }
                    } else if (nodes.startsWith("CVSAddress")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            CVSAddress = splitTemp[1];
                        }
                    } else if (nodes.startsWith("CVSTelephone")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            CVSTelephone = splitTemp[1];
                        }
                    } else if (nodes.startsWith("CVSOutSide")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            CVSOutSide = splitTemp[1];
                        }
                    } else if (nodes.startsWith("ExtraData")) {
                        String[] splitTemp = nodes.split("=");
                        if (splitTemp.length > 1) {
                            ExtraData = splitTemp[1];
                        }
                    }

                }

                // System.out.println(MerchantID);
                // System.out.println(MerchantTradeNo);
                // System.out.println(LogisticsSubType);
                // System.out.println(CVSStoreID);
                // System.out.println(CVSStoreName);
                // System.out.println(CVSAddress);
                // System.out.println(CVSTelephone);
                // System.out.println(CVSOutSide);
                // System.out.println(ExtraData);

                if (LogisticsSubType.equals("FAMI") || LogisticsSubType.equals("FAMIC2C")) {
                    distributorno = "F";
                    distributorname = "全家";
                } else if (LogisticsSubType.equals("UNIMART") || LogisticsSubType.equals("UNIMARTC2C")) {
                    distributorno = "7";
                    distributorname = "7-11";
                } else if (LogisticsSubType.equals("HILIFE") || LogisticsSubType.equals("HILIFEC2C")) {
                    distributorno = "L";
                    distributorname = "莱而富";
                }

                tel = CVSTelephone;

                // 门店编码6码
                getshopNo = CVSStoreID;
                // 门店名称
                getshopName = CVSStoreName;
                // 地址
                getshopAddress = CVSAddress;
                // 由 订单号@@@企业编码@@@门店编号@@@时间戳 组成
                tempvar = ExtraData;

                String[] splitStr = tempvar.split("@@@");

                // 订单编号
                orderno = splitStr[0];
                eId = splitStr[1];
                shopId = splitStr[2];
                timeno = splitStr[3];

            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            if (ectype.toLowerCase().equals("dzt") || ectype.toLowerCase().equals("greenworld")) {
                DsmDAO dao = null;

                dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

                // 列表SQL
                List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                String sqlExist = "select t.orderno from OC_lgmap t where t.eid='" + eId + "' and t.SHOPID='"
                        + shopId + "' and t.orderno='" + orderno + "' ";
                List<Map<String, Object>> getQData = dao.executeQuerySQL(sqlExist, null);
                if (getQData == null || getQData.isEmpty()) {
                    // 插入明细
                    String[] columnsMap = { "EID", "SHOPID", "ORDERNO", "TIMENO", "DISTRIBUTORNO",
                            "DISTRIBUTORNAME", "GETSHOPNO", "GETSHOPNAME", "TEL", "ADDRESS", "SDATE", "STIME",
                            "STATUS" };
                    DataValue[] insValueOrderDetail = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR), new DataValue(orderno, Types.VARCHAR),
                            new DataValue(timeno, Types.VARCHAR), new DataValue(distributorno, Types.VARCHAR),
                            new DataValue(distributorname, Types.VARCHAR), new DataValue(getshopNo, Types.VARCHAR),
                            new DataValue(getshopName, Types.VARCHAR), new DataValue(tel, Types.VARCHAR),
                            new DataValue(getshopAddress, Types.VARCHAR), new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR), new DataValue("100", Types.VARCHAR), };

                    InsBean ibOrderDetail = new InsBean("OC_LGMAP", columnsMap);
                    ibOrderDetail.addValues(insValueOrderDetail);
                    lstData.add(new DataProcessBean(ibOrderDetail));

                    ibOrderDetail = null;
                } else {
                    //
                    UptBean ubec = new UptBean("OC_LGMAP");
                    ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubec.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

                    ubec.addUpdateValue("DISTRIBUTORNO", new DataValue(distributorno, Types.VARCHAR));
                    ubec.addUpdateValue("DISTRIBUTORNAME", new DataValue(distributorname, Types.VARCHAR));
                    ubec.addUpdateValue("GETSHOPNO", new DataValue(getshopNo, Types.VARCHAR));
                    ubec.addUpdateValue("GETSHOPNAME", new DataValue(getshopName, Types.VARCHAR));
                    ubec.addUpdateValue("TEL", new DataValue(tel, Types.VARCHAR));
                    ubec.addUpdateValue("ADDRESS", new DataValue(getshopAddress, Types.VARCHAR));
                    ubec.addUpdateValue("SDATE", new DataValue(sysDate, Types.VARCHAR));
                    ubec.addUpdateValue("STIME", new DataValue(sysTime, Types.VARCHAR));
                    ubec.addUpdateValue("TIMENO", new DataValue(timeno, Types.VARCHAR));

                    lstData.add(new DataProcessBean(ubec));

                    ubec = null;
                }

                //
                dao.useTransactionProcessData(lstData);

                dao = null;

                //
                getQData = null;
                lstData = null;

            }

            //
            request = null;

            response.setStatus(202);
            response.flushBuffer();

        } catch (Exception e) {

            logger.error(e.getMessage());
            JSONObject header = new JSONObject();
            header.put("code", 500);
            header.put("message", e.getMessage());
            String resInfoString = header.toString();

            response.getOutputStream().write(resInfoString.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * DingDing
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/dingding")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void dingding(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;
        DCP_DingCallBack ddcb = new DCP_DingCallBack();
        Map<String, String> ddMap = new HashMap<String, String>();
        com.alibaba.fastjson.JSONObject response_json = new com.alibaba.fastjson.JSONObject();
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader = null;
            insr = null;

            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

            // dingding 解析处理服务
            ddMap = ddcb.execute(request, jb.toString(), dao);
            if (!ddMap.isEmpty()) {
                response_json.putAll(ddMap);
                response.getOutputStream().write(response_json.toString().getBytes("UTF-8"));
            }
        } catch (Exception e) {
            HelpTools.writelog_fileName("\r\n 钉钉推送发生异常: " + e.getMessage() + "\r\n", "dingding");
            return;
        } finally {
            jb = null;
            line = null;
            dao = null;
            response_json = null;
            ddcb = null;
            ddMap = null;
        }
    }

    private Map<String, Object> setReplaceField_receiving() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> receivingMap = new HashMap<String, String>();
        // 单号
        receivingMap.put("docno", "receivingNO");
        // 門店
        receivingMap.put("shop_no", "shopId");
        // 備註
        receivingMap.put("remark", "remark");
        // 调拨人员
        receivingMap.put("creator", "createBy");
        // 資料來源類型
        receivingMap.put("load_doc_type", "loadDocType");
        // 資料來源單號
        receivingMap.put("load_doc_no", "loadDocNO");
        // 要货模板编号
        receivingMap.put("po_template_no", "pTemplateNO");
        // ERP采购收货入库单号
        receivingMap.put("receipt_no", "loadReceiptNO");
        // 供应商编号
        receivingMap.put("supplier_no", "supplier");
        // 预计收货日期
        receivingMap.put("receipt_date", "receipt_date");

        // 发货机构
        receivingMap.put("transfer_out_site_no", "transfer_shop");

        // 物料单号
        receivingMap.put("delivery_no", "delivery_no");
        //装箱单号
        receivingMap.put("packingNo", "packingNo");
        //需求日期
        receivingMap.put("rdate", "rDate");

        // 單身需取代的欄位
        Map<String, String> receivingDetailMap = new HashMap<String, String>();
        // 項次
        receivingDetailMap.put("seq", "item");
        // 來源項次
        receivingDetailMap.put("source_seq", "oItem");
        // 來源類型
        receivingDetailMap.put("source_type", "oType");
        // 來源單號
        receivingDetailMap.put("source_no", "ofNO");
        // 商品編號
        receivingDetailMap.put("item_no", "pluNO");
        // 商品條碼
        receivingDetailMap.put("item_barcode", "pluBarcode");
        // 產品特徵
        receivingDetailMap.put("feature_no", "featureNO");
        // 產品特徵說明
        receivingDetailMap.put("item_feature_name", "featureName");
        // 包裝單位
        receivingDetailMap.put("packing_unit", "punit");
        // 包裝數量
        receivingDetailMap.put("packing_qty", "pqty");
        // 基本单位
        receivingDetailMap.put("base_unit", "baseUnit");
        // 基本单位数量
        receivingDetailMap.put("base_qty", "baseQty");
        // 单位转化率
        receivingDetailMap.put("unit_ratio", "unitRatio");
        // 要货数量
        receivingDetailMap.put("requisition_qty", "poQty");
        // 單價
        receivingDetailMap.put("price", "price");
        // 金額
        receivingDetailMap.put("amount", "amt");
        // 进货单价
        receivingDetailMap.put("distri_price", "distriPrice");
        // 进货金额
        receivingDetailMap.put("distri_amount", "distriAmt");
        // 超交率
        receivingDetailMap.put("over_deliver_rate", "PROC_RATE");
        // 仓库
        receivingDetailMap.put("warehouse_no", "WAREHOUSE");
        // 单身明细
        receivingDetailMap.put("item_memo", "PLU_MEMO");
        // 商品批号
        receivingDetailMap.put("item_batch_no", "batchNO");
        // 生产日期
        receivingDetailMap.put("prod_date", "prodDate");
        //装箱单号
        receivingDetailMap.put("packingNo", "packingNo");
        //要货单位
        receivingDetailMap.put("requisition_unit", "poUnit");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_ReceivingCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("receiving", receivingMap);
        replaceMap.put("receiving_detail", receivingDetailMap);
        return replaceMap;
    }

    private Map<String, Object> setReplaceField_counting() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> countingMap = new HashMap<String, String>();
        // 備註
        countingMap.put("remark", "remark");
        // 資料來源類型
        countingMap.put("load_doc_type", "loadDocType");
        // 資料來源單號
        countingMap.put("load_doc_no", "loadDocNO");
        // 盘点日期
        countingMap.put("counting_date", "Bdate");
        // 操作类型
        countingMap.put("operation_type", "operation_type");
        // 盘点类型
        countingMap.put("counting_type", "docType");
        // 盘点方式 1.营业中盘点 2.闭店盘点
        countingMap.put("counting_way", "taskWay");
        // 未盘商品处理方式 1.库存变成零 2.库存不变
        countingMap.put("goods_mode", "notgoodsMode");
        // 是否盲盘
        countingMap.put("is_blind_counting", "btake");
        // 是否调整库存
        countingMap.put("is_adjust_stock", "isAdjustStock");

        // 單身需取代的欄位
        Map<String, String> countingShopMap = new HashMap<String, String>();
        // 门店
        countingShopMap.put("shop_no", "shopId");
        countingShopMap.put("warehouse_no", "warehouse");

        // 單身需取代的欄位
        Map<String, String> countingRangeMap = new HashMap<String, String>();
        // 组别
        countingRangeMap.put("group_no", "groupNO");
        // 属性类型
        countingRangeMap.put("property_type", "attrNO");
        // 属性编码
        countingRangeMap.put("property_value", "valueNO");

        // 單身需取代的欄位
        Map<String, String> countingDetailMap = new HashMap<String, String>();
        // 商品编码
        countingDetailMap.put("item_no", "pluNO");
        // 单价
        countingDetailMap.put("price", "price");
        // 仓库
        // countingDetailMap.put("warehouse_no", "WAREHOUSE");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_StockTaskCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("counting", countingMap);
        replaceMap.put("counting_shop", countingShopMap);
        replaceMap.put("counting_range", countingRangeMap);
        replaceMap.put("counting_detail", countingDetailMap);
        return replaceMap;
    }

    private Map<String, Object> setReplaceField_return() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> returnMap = new HashMap<String, String>();
        // 備註
        returnMap.put("front_no", "stockOutNO");
        // 门店
        returnMap.put("shop_no", "shopId");
        // 資料來源類型
        returnMap.put("operation_type", "opType");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_StockOutErpUpdate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("return", returnMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_dayend() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> dayendshopsMap = new HashMap<String, String>();
        //
        dayendshopsMap.put("day_end_shops", "dayEndShops");

        // 單身需取代的欄位
        Map<String, String> dayendshopsDetailpMap = new HashMap<String, String>();
        // 门店
        dayendshopsDetailpMap.put("shop_no", "shopId");
        dayendshopsDetailpMap.put("posted_date", "eDate");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_DayEndCheck");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("day_end", dayendshopsMap);
        replaceMap.put("day_end_shops", dayendshopsDetailpMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_AdjustCreate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> AdjustMap = new HashMap<String, String>();
        AdjustMap.put("shop_no", "shopId");
        AdjustMap.put("doc_type", "docType");
        AdjustMap.put("remark", "memo");
        AdjustMap.put("adjust_date", "bDate");
        AdjustMap.put("load_doc_type", "loadDocType");
        AdjustMap.put("load_doc_no", "loadDocNO");
        AdjustMap.put("source_type", "otype");
        AdjustMap.put("source_no", "ofno");
        AdjustMap.put("creator", "createBy");
        AdjustMap.put("create_date", "createDate");
        AdjustMap.put("create_time", "createTime");
        AdjustMap.put("modify_no", "modifyBy");
        AdjustMap.put("modify_date", "modifyDate");
        AdjustMap.put("modify_time", "modifyTime");
        AdjustMap.put("approve_no", "confirmBy");
        AdjustMap.put("approve_date", "confirmDate");
        AdjustMap.put("approve_time", "confirmTime");
        AdjustMap.put("posted_no", "accountBy");
        AdjustMap.put("posted_date", "accountDate");
        AdjustMap.put("posted_time", "accountTime");
        AdjustMap.put("void_no", "cancelBy");
        AdjustMap.put("void_date", "cancelDate");
        AdjustMap.put("void_time", "cancelTime");
        AdjustMap.put("difference_status", "differenceStatus");

        // 單身需取代的欄位
        Map<String, String> adjustDetailMap = new HashMap<String, String>();
        adjustDetailMap.put("seq", "item");
        adjustDetailMap.put("item_no", "pluNO");
        adjustDetailMap.put("feature_no", "featureNO");
        adjustDetailMap.put("packing_unit", "punit");
        adjustDetailMap.put("packing_qty", "pqty");
        adjustDetailMap.put("base_unit", "baseUnit");
        adjustDetailMap.put("base_qty", "baseQty");
        adjustDetailMap.put("unit_ratio", "unitRatio");
        adjustDetailMap.put("price", "price");
        adjustDetailMap.put("amount", "amt");
        adjustDetailMap.put("distri_price", "distriPrice");
        adjustDetailMap.put("distri_amount", "distriAmt");
        adjustDetailMap.put("warehouse_no", "WAREHOUSE");
        adjustDetailMap.put("item_batch_no", "batchNO");
        adjustDetailMap.put("prod_date", "prodDate");
        //adjustDetailMap.put("load_doc_seq", "oitem");
        adjustDetailMap.put("source_seq", "oitem");  /// source_seq 调整成规格一致 by jzma 20200721

        // adjustDetailMap.put("warehouse", "WAREHOUSE");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_AdjustCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("adjust", AdjustMap);
        replaceMap.put("adjust_detail", adjustDetailMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_posErrorGet() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> conditionMap = new HashMap<String, String>();
        //
        conditionMap.put("dates", "dates");
        conditionMap.put("shops", "shops");
        conditionMap.put("doc_types", "docTypes");

        // 單身需取代的欄位
        Map<String, String> datesMapDetail = new HashMap<String, String>();
        // 日期
        datesMapDetail.put("get_date", "getDate");

        // 單身需取代的欄位
        Map<String, String> shopsMapDetail = new HashMap<String, String>();
        // 门店
        shopsMapDetail.put("shop_no", "shopId");

        // 單身需取代的欄位
        Map<String, String> docTypesMapDetail = new HashMap<String, String>();
        // 类型
        docTypesMapDetail.put("doc_type", "docType");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_UpErrorLogQuery");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("get_condition", conditionMap);
        replaceMap.put("dates", datesMapDetail);
        replaceMap.put("shops", shopsMapDetail);
        replaceMap.put("doc_types", docTypesMapDetail);
        // System.out.println(replaceMap);
        return replaceMap;
    }

    private Map<String, Object> setReplaceField_TransferCreate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> transferMap = new HashMap<>();
        //【ID1030137】【3.0】中台和erp之间增加调拨通知单服务  by jinzma 20221207
        // 门店
        transferMap.put("shop_no", "shopId");
        // 调入门店
        transferMap.put("transfer_shop", "transferShop");
        // 资料转入来源类型
        transferMap.put("load_doc_type", "loadDocType");
        // 资料转入来源单号
        transferMap.put("load_doc_no", "loadDocNo");
        // 备注
        transferMap.put("remark", "memo");

        // 企业编码
        //transferMap.put("enterprise_no", "eid");
        // 调出仓库
        //transferMap.put("warehouse_no", "warehouse");
        // 调入仓库
        //transferMap.put("ingoing_warehouse_no", "transferWarehouse");
        // 建立者/调拨人员
        //transferMap.put("creator", "createBy");
        // 总数量
        //transferMap.put("tot_pqty", "totPQty");
        // 总金额
        //transferMap.put("tot_amt", "totAmt");
        // 品种数
        //transferMap.put("tot_cqty", "totCQty");

        // 單身需取代的欄位
        Map<String, String> transferDetailMap = new HashMap<>();
        // 項次
        transferDetailMap.put("seq", "item");
        // 商品編號
        transferDetailMap.put("item_no", "pluNo");
        // 產品特徵
        transferDetailMap.put("feature_no", "featureNo");
        //商品批号
        transferDetailMap.put("item_batch_no", "batchNo");
        //生产日期
        transferDetailMap.put("prod_date", "prodDate");
        //包裝單位
        transferDetailMap.put("packing_unit", "punit");
        //包裝數量
        transferDetailMap.put("packing_qty", "pqty");
        //基本单位
        transferDetailMap.put("base_unit", "baseUnit");
        //基本单位数量
        transferDetailMap.put("base_qty", "baseQty");
        //单位转化率
        transferDetailMap.put("unit_ratio", "unitRatio");
        //单价
        transferDetailMap.put("price", "price");
        //金额
        transferDetailMap.put("amount", "amt");
        //单价
        transferDetailMap.put("distri_price", "distriPrice");
        //金额
        transferDetailMap.put("distri_amount", "distriAmt");


        // 仓库
        //transferDetailMap.put("warehouse_no", "warehouse");
        // 商品條碼
        //transferDetailMap.put("item_barcode", "pluBarcode");
        // 库存单位
        //transferDetailMap.put("inventory_unit", "wunit");
        // 库存数量
        //transferDetailMap.put("inventory_qty", "wqty");
        // 单位换算比率
        //transferDetailMap.put("reference_rate", "unitRatio");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_TransferCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("transfer", transferMap);
        replaceMap.put("transfer_detail", transferDetailMap);
        return replaceMap;
    }

    private Map<String, Object> setReplaceField_scrap() {

        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> scrapMap = new HashMap<String, String>();
        // 備註
        scrapMap.put("front_no", "lstockOutNO");
        // 门店
        scrapMap.put("shop_no", "shopId");
        // 操作类型
        scrapMap.put("operation_type", "opType");

        // 單身需取代的欄位
        Map<String, String> scrapDetailMap = new HashMap<String, String>();
        // 項次
        scrapDetailMap.put("seq", "item");
        // 商品編號
        scrapDetailMap.put("item_no", "pluNO");
        // 包裝單位
        scrapDetailMap.put("packing_unit", "punit");
        // 包裝數量
        scrapDetailMap.put("packing_qty", "pqty");
        // 基准单位
        scrapDetailMap.put("base_unit", "baseUnit");
        // 基准数量
        scrapDetailMap.put("base_qty", "baseQty");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_LStockOutErpUpdate");
        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("scrap", scrapMap);
        replaceMap.put("scrap_detail", scrapDetailMap);
        return replaceMap;

    }

    private Map<String, Object> setReplaceField_orgorder() {

        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_OrgOrderUpdate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        return replaceMap;

    }

    private Map<String, Object> setReplaceField_RejectCreate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> rejectMap = new HashMap<String, String>();
        // 驳回单类型
        rejectMap.put("doc_type", "docType");

        // 單身需取代的欄位
        Map<String, String> rejectDetailMap = new HashMap<String, String>();
        // 门店编号
        rejectDetailMap.put("shop_no", "shopId");
        // 前端单号
        rejectDetailMap.put("front_no", "docNO");
        // 驳回原因
        rejectDetailMap.put("reason", "reason");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_RejectCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("reject", rejectMap);
        replaceMap.put("reject_detail", rejectDetailMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_UndoCreate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> undoMap = new HashMap<String, String>();
        // 撤销单类型
        undoMap.put("doc_type", "docType");

        // 單身需取代的欄位
        Map<String, String> undoDetailMap = new HashMap<String, String>();
        // 门店编号
        undoDetailMap.put("shop_no", "shopId");
        // ERP单号
        undoDetailMap.put("load_doc_no", "loadDocNO");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_UndoCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("undo", undoMap);
        replaceMap.put("undo_detail", undoDetailMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_OrderStatusUpdate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("orderNO", "orderNo");
        requestMap.put("o_opNO", "opNO");
        requestMap.put("o_opName", "opName");
        requestMap.put("deliveryNO", "deliveryNo");
        //requestMap.put("status", "status");
        //requestMap.put("deliveryStatus", "deliveryStatus");
        //requestMap.put("productStatus", "productStatus");
        //requestMap.put("delName", "delName");
        //requestMap.put("delTelephone", "delTelephone");
        //requestMap.put("deliveryType", "deliveryType");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_OrderStatusUpdateERP");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("request", requestMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_payInCheck() {

        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("eId", "o_eId");
        requestMap.put("shopId", "o_shopId");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_PayInCheck");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("request", requestMap);
        return replaceMap;

    }

    private Map<String, Object> setReplaceField_etlRetrans(){
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> retransMap = new HashMap<String, String>();
        // 单据类型
        retransMap.put("doc_type","docType");
        // 单号
        retransMap.put("doc_no","docNo");
        // 门店
        retransMap.put("shop_no","shopId");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_EtlRetransProcess");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("retrans", retransMap);

        return replaceMap;

    }

    private Map<String, Object> setReplaceField_feeUpdate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> feeUpdateMap = new HashMap<>();
        // 企业编码
        feeUpdateMap.put("enterprise_no","enterprise_no");
        // 门店
        feeUpdateMap.put("shop_no","shop_no");
        // 前端单号
        feeUpdateMap.put("front_no","front_no");
        // 操作类型
        feeUpdateMap.put("operation_type","operation_type");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_BFeeErpUpdate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("feeupdate", feeUpdateMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_RequisitionUpdate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<String, Object>();
        // 單頭需取代的欄位
        Map<String, String> requisitionMap = new HashMap<String, String>();
        // 前端单号
        requisitionMap.put("front_no", "pOrderno");
        // 门店编号
        requisitionMap.put("site_no", "shopId");
        requisitionMap.put("remark", "remark");
        requisitionMap.put("modify_no", "modify_no");
        requisitionMap.put("modify_datetime", "modify_datetime");
        requisitionMap.put("load_type", "load_type");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<String, String>();
        // 项次
        DetailMap.put("seq", "item");
        // 来源项次
        DetailMap.put("load_seq", "oitem");
        // 商品编号
        DetailMap.put("item_no", "pluNO");
        // 库存单位
        DetailMap.put("base_unit", "wunit");
        // 库存数量
        DetailMap.put("base_qty", "wqty");
        // 包装单位
        DetailMap.put("packing_unit", "punit");
        // 包装数量
        DetailMap.put("packing_qty", "pqty");
        //
        DetailMap.put("remark", "remark");
        DetailMap.put("unit_ratio", "unitRatio");
        DetailMap.put("feature_no", "featureNo");
        DetailMap.put("warehouse_no", "warehouseNo");



        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_RequisitionUpdate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("requisitionupdate", requisitionMap);
        replaceMap.put("requisitionupdate_detail", DetailMap);

        return replaceMap;
    }

    private Map<String, Object> setReplaceField_requisitionCreate() {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> requisitionMap = new HashMap<>();
        requisitionMap.put("shopNo", "shopId");
        requisitionMap.put("requisition_date","rDate" );
        requisitionMap.put("remark","memo");
        requisitionMap.put("load_doc_no", "loadDocNo");
        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("seq", "item");
        // 商品编号
        DetailMap.put("item_no", "pluNo");
        // 基本单位
        DetailMap.put("base_unit", "baseUnit");
        // 基本单位数量
        DetailMap.put("base_qty", "baseQty");
        // 包装单位
        DetailMap.put("packing_unit", "punit");
        // 包装数量
        DetailMap.put("packing_qty", "pqty");
        DetailMap.put("feature_no", "featureNo");
        DetailMap.put("unit_ratio", "unitRatio");
        DetailMap.put("price", "price");
        DetailMap.put("distri_price", "distriPrice");
        DetailMap.put("amount", "amt");
        DetailMap.put("distri_amount", "distriAmt");
        DetailMap.put("remark", "memo");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_POrderERPCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("requisitionCreate", requisitionMap);
        replaceMap.put("requisitionCreate_detail", DetailMap);

        return replaceMap;

    }

    private Map<String, Object> setReplaceField_requisitionEcsflg(){
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> requisitionEcsflgMap = new HashMap<>();
        requisitionEcsflgMap.put("shop_no", "shopId");
        requisitionEcsflgMap.put("load_doc_no", "loadDocNo");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "DCP_POrderERPEcsflg");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("requisitionClose", requisitionEcsflgMap);

        return replaceMap;
    }

    /**
     MES工单创建
     */
    private Map<String, Object> setReplaceField_MES_MoCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("loadDocNo", "loadDocNo");
        mes_moCreateMap.put("pGroupNo", "pGroupNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("pDate", "pDate");
        mes_moCreateMap.put("creatByNo", "creatByNo");
        mes_moCreateMap.put("creatByName", "creatByName");
        mes_moCreateMap.put("status", "status");
        mes_moCreateMap.put("memo", "memo");


        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("item", "item");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("pUnit", "pUnit");
        DetailMap.put("pQty", "pQty");
        DetailMap.put("beginDate", "beginDate");
        DetailMap.put("endDate", "endDate");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_MoCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesmo", mes_moCreateMap);
        replaceMap.put("mesmo_detail", DetailMap);

        return replaceMap;
    }

    /**
     *MES erp下发盘点单
     * @return
     */
    private Map<String, Object> setReplaceField_MES_ErpStockTakeAdd()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("warehouseNo", "warehouseNo");
        mes_moCreateMap.put("ofNo", "ofNo");
        mes_moCreateMap.put("totQty", "totQty");
        mes_moCreateMap.put("totCQty", "totCQty");
        mes_moCreateMap.put("status", "status");
        mes_moCreateMap.put("bDate", "bDate");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("oItem", "oItem");
        DetailMap.put("bDate", "bDate");
        DetailMap.put("batchNo", "batchNo");
        DetailMap.put("location", "location");
        DetailMap.put("distriPrice", "distriPrice");
        DetailMap.put("baseUnit", "baseUnit");


        // 子單身需取代的欄位
        Map<String, String> unitDetailMap = new HashMap<>();
        // 项次
        unitDetailMap.put("oItem", "oItem");
        unitDetailMap.put("pUnit", "pUnit");
        unitDetailMap.put("unitRatio", "unitRatio");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_ErpStockTakeAdd");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("messtocktake", mes_moCreateMap);
        replaceMap.put("messtocktake_detail", DetailMap);
        replaceMap.put("messtocktakeunit_detail", unitDetailMap);

        return replaceMap;
    }

    /**
     * MES 分拣底稿数据创建
     * @return
     */
    private Map<String, Object> setReplaceField_MES_SortDataAdd()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("docNo", "docNo");
        mes_moCreateMap.put("requireNo", "requireNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("rTamplateNo", "rTamplateNo");
        mes_moCreateMap.put("department", "department");
        mes_moCreateMap.put("name", "name");
        mes_moCreateMap.put("phone", "phone");
        mes_moCreateMap.put("address", "address");
        mes_moCreateMap.put("sDate", "sDate");
        mes_moCreateMap.put("sTime", "sTime");


        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("aQty", "aQty");
        DetailMap.put("transferOut", "transferOut");
        DetailMap.put("transferIn", "transferIn");
        DetailMap.put("unit", "unit");
        DetailMap.put("item", "item");
        DetailMap.put("rDate", "rDate");
        DetailMap.put("price", "price");
        DetailMap.put("amt", "amt");
        DetailMap.put("taxRate", "taxRate");
        DetailMap.put("taxAmount", "taxAmount");
        DetailMap.put("sourceType", "sourceType");
        DetailMap.put("sourceNo", "sourceNo");
        DetailMap.put("oItem", "oItem");
        DetailMap.put("oCompany", "oCompany");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_SortDataAdd");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("messortdata", mes_moCreateMap);
        replaceMap.put("messortdata_detail", DetailMap);

        return replaceMap;
    }


    /**
     *MES采购计划单创建
     * @return
     */
    private Map<String, Object> setReplaceField_MES_ReceivingAdd()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("supplierNo", "supplierNo");
        mes_moCreateMap.put("docNo", "docNo");
        mes_moCreateMap.put("totQty", "totQty");
        mes_moCreateMap.put("totCQty", "totCQty");
        mes_moCreateMap.put("opNo", "opNo");
        mes_moCreateMap.put("opName", "opName");
        mes_moCreateMap.put("department", "department");
        mes_moCreateMap.put("status", "status");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("oItem", "oItem");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("unit", "unit");
        DetailMap.put("qty", "qty");
        DetailMap.put("baseUnit", "baseUnit");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("unitRatio", "unitRatio");
        DetailMap.put("distriPrice", "distriPrice");
        DetailMap.put("distriAmount", "distriAmount");
        DetailMap.put("warehouseNo", "warehouseNo");
        DetailMap.put("rDate", "rDate");
        DetailMap.put("isGive", "isGive");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_ReceivingAdd");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesreceiving", mes_moCreateMap);
        replaceMap.put("mesreceiving_detail", DetailMap);

        return replaceMap;
    }


    /**
     MES销售订单创建
     */
    private Map<String, Object> setReplaceField_MES_SalesOrderCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("erpOrderNo", "erpOrderNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("customer", "customer");
        mes_moCreateMap.put("salesMan", "salesMan");
        mes_moCreateMap.put("salesManTel", "salesManTel");
        mes_moCreateMap.put("department", "department");
        mes_moCreateMap.put("address", "address");
        mes_moCreateMap.put("contactName", "contactName");
        mes_moCreateMap.put("contactTele", "contactTele");
        mes_moCreateMap.put("memo", "memo");
        mes_moCreateMap.put("tot_Qty", "tot_Qty");
        mes_moCreateMap.put("tot_CQty", "tot_CQty");
        mes_moCreateMap.put("createBy", "createBy");
        mes_moCreateMap.put("createByName", "createByName");
        mes_moCreateMap.put("createTime", "createTime");



        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("erpItem", "erpItem");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("pluName", "pluName");
        DetailMap.put("unit", "unit");
        DetailMap.put("pQty", "pQty");
        DetailMap.put("rDate", "rDate");
        DetailMap.put("baseUnit", "baseUnit");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("warehouseNo", "warehouseNo");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_SalesOrderCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesSalesOrder", mes_moCreateMap);
        replaceMap.put("mesSalesOrder_detail", DetailMap);

        return replaceMap;
    }


    /**
     MES出库单申请
     */
    private Map<String, Object> setReplaceField_MES_StockOutApplicationCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("erpStockOutNo", "erpStockOutNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("warehouseNo", "warehouseNo");
        mes_moCreateMap.put("memo", "memo");
        mes_moCreateMap.put("tot_Qty", "tot_Qty");
        mes_moCreateMap.put("tot_CQty", "tot_CQty");
        mes_moCreateMap.put("createBy", "createBy");
        mes_moCreateMap.put("createByName", "createByName");
        mes_moCreateMap.put("createTime", "createTime");
        mes_moCreateMap.put("mes_recipient", "mes_recipient");
        mes_moCreateMap.put("mes_department", "mes_department");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("erpItem", "erpItem");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("pluName", "pluName");
        DetailMap.put("unit", "unit");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("pQty", "pQty");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("baseUnit", "baseUnit");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_StockOutApplicationCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesStockOutApplication", mes_moCreateMap);
        replaceMap.put("mesStockOutApplication_detail", DetailMap);

        return replaceMap;
    }


    /**
     MES销退单创建
     */
    private Map<String, Object> setReplaceField_MES_SalesReturnCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("erpSaleNo", "erpSaleNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("customer", "customer");
        mes_moCreateMap.put("salesMan", "salesMan");
        mes_moCreateMap.put("salesManTel", "salesManTel");
        mes_moCreateMap.put("department", "department");
        mes_moCreateMap.put("memo", "memo");
        mes_moCreateMap.put("tot_Qty", "tot_Qty");
        mes_moCreateMap.put("tot_CQty", "tot_CQty");
        mes_moCreateMap.put("createBy", "createBy");
        mes_moCreateMap.put("createByName", "createByName");
        mes_moCreateMap.put("createTime", "createTime");


        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("erpItem", "erpItem");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("pluName", "pluName");
        DetailMap.put("unit", "unit");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("pQty", "pQty");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("baseUnit", "baseUnit");
        DetailMap.put("warehouseNo", "warehouseNo");
        DetailMap.put("batchNo", "batchNo");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_SalesReturnCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesSalesReturn", mes_moCreateMap);
        replaceMap.put("mesSalesReturn_detail", DetailMap);

        return replaceMap;
    }


    /**
     MES组合拆解单创建
     */
    private Map<String, Object> setReplaceField_MES_ComposeDisCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("erpDocNo", "erpDocNo");
        mes_moCreateMap.put("type", "type");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("pluNo", "pluNo");
        mes_moCreateMap.put("featureNo", "featureNo");
        mes_moCreateMap.put("pUnit", "pUnit");
        mes_moCreateMap.put("pQty", "pQty");
        mes_moCreateMap.put("baseUnit", "baseUnit");
        mes_moCreateMap.put("baseQty", "baseQty");
        mes_moCreateMap.put("warehouseNo", "warehouseNo");
        mes_moCreateMap.put("batchNo", "batchNo");
        mes_moCreateMap.put("createBy", "createBy");
        mes_moCreateMap.put("createByName", "createByName");
        mes_moCreateMap.put("createTime", "createTime");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("erpItem", "erpItem");
        DetailMap.put("subPluNo", "subPluNo");
        DetailMap.put("unit", "unit");
        DetailMap.put("qty", "qty");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("baseUnit", "baseUnit");
        DetailMap.put("warehouseNo", "warehouseNo");
        DetailMap.put("batchNo", "batchNo");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_ComposeDisCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesComposeDis", mes_moCreateMap);
        replaceMap.put("mesComposeDis_detail", DetailMap);

        return replaceMap;
    }


    /**
     MES采退单创建
     */
    private Map<String, Object> setReplaceField_MES_PurchaseReturnCreate()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_moCreateMap = new HashMap<>();
        mes_moCreateMap.put("eId", "eId");
        mes_moCreateMap.put("organizationNo", "organizationNo");
        mes_moCreateMap.put("erpReturnNo", "erpReturnNo");
        mes_moCreateMap.put("bDate", "bDate");
        mes_moCreateMap.put("supplier", "supplier");
        mes_moCreateMap.put("purchaser", "purchaser");
        mes_moCreateMap.put("department", "department");
        mes_moCreateMap.put("memo", "memo");
        mes_moCreateMap.put("tot_Qty", "tot_Qty");
        mes_moCreateMap.put("tot_CQty", "tot_CQty");
        mes_moCreateMap.put("createBy", "createBy");
        mes_moCreateMap.put("createByName", "createByName");
        mes_moCreateMap.put("createTime", "createTime");

        // 單身需取代的欄位
        Map<String, String> DetailMap = new HashMap<>();
        // 项次
        DetailMap.put("erpItem", "erpItem");
        DetailMap.put("pluNo", "pluNo");
        DetailMap.put("pluName", "pluName");
        DetailMap.put("pUnit", "pUnit");
        DetailMap.put("featureNo", "featureNo");
        DetailMap.put("pQty", "pQty");
        DetailMap.put("baseQty", "baseQty");
        DetailMap.put("baseUnit", "baseUnit");
        DetailMap.put("warehouseNo", "warehouseNo");
        DetailMap.put("batchNo", "batchNo");
        //新加字段start===
        DetailMap.put("sourceStockInNo", "sourceStockInNo");
        DetailMap.put("sourceStockInItem", "sourceStockInItem");

        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_PurchaseReturnCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesPurchaseReturn", mes_moCreateMap);
        replaceMap.put("mesPurchaseReturn_detail", DetailMap);

        return replaceMap;
    }

    /**
     * 库存流水新增
     * @return
     */
    private Map<String, Object> setReplaceField_MES_StockDetailAdd()
    {
        // 傳入轉換器的Map
        Map<String, Object> replaceMap = new HashMap<>();
        // 單頭需取代的欄位
        Map<String, String> mes_stockDetailCreateMap = new HashMap<>();
        mes_stockDetailCreateMap.put("eId", "eId");
        mes_stockDetailCreateMap.put("organizationNo", "organizationNo");
        mes_stockDetailCreateMap.put("billType", "billType");
        mes_stockDetailCreateMap.put("erpBillNo", "erpBillNo");
        mes_stockDetailCreateMap.put("erpItem", "erpItem");
        mes_stockDetailCreateMap.put("erpSeq", "erpSeq");
        mes_stockDetailCreateMap.put("direct", "direct");
        mes_stockDetailCreateMap.put("bDate", "bDate");
        mes_stockDetailCreateMap.put("accountDate", "accountDate");
        mes_stockDetailCreateMap.put("pluNo", "pluNo");
        mes_stockDetailCreateMap.put("featureNo", "featureNo");
        mes_stockDetailCreateMap.put("batchNo", "batchNo");
        mes_stockDetailCreateMap.put("prodDate", "prodDate");
        mes_stockDetailCreateMap.put("warehouse", "warehouse");
        mes_stockDetailCreateMap.put("location", "location");
        mes_stockDetailCreateMap.put("baseUnit", "baseUnit");
        mes_stockDetailCreateMap.put("baseQty", "baseQty");
        mes_stockDetailCreateMap.put("price", "price");
        mes_stockDetailCreateMap.put("amt", "amt");
        mes_stockDetailCreateMap.put("userId", "userId");


        // service端的POS服務名稱設定
        Map<String, String> serviceMap = new HashMap<String, String>();
        // 指定POS端服務名稱
        serviceMap.put("serviceId", "MES_StockDetailCreate");

        // 將需取代的內容傳入
        replaceMap.put("service", serviceMap);
        replaceMap.put("mesStockDetail", mes_stockDetailCreateMap);//数组多笔

        return replaceMap;
    }





    /**
     * 將 byte array 資料做 hash md5或 sha256 運算，並回傳 hex值的字串資料
     *
     * @param data
     * @param mode
     * @return string
     */
    private String hash(byte data[], String mode) {
        MessageDigest md = null;
        try {
            if (mode == "MD5") {
                md = MessageDigest.getInstance("MD5");
            } else if (mode == "SHA-256") {
                md = MessageDigest.getInstance("SHA-256");
            }
        } catch (NoSuchAlgorithmException e) {

        }

        byte[] bytes = md.digest(data);

        md = null;

        return bytesToHex(bytes);
    }

    private char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 將 byte array 資料轉換成 hex字串值
     *
     * @param bytes
     * @return string
     */
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 外部支付接口： 支付成功后，云中台单据处理
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/orderNotify")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String orderNotify(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        WeixinNotifyResponse res = new WeixinNotifyResponse();
        res.setReturn_code("FAIL");
        ESBUtils esbu = new ESBUtils();
        try {
            logger.info("\r\norderNotify 1");
            //			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            request.setCharacterEncoding("UTF-8");

            // check sign
            /*
             * String acceptPram = ESBUtils.getRequestData(request);
             * //这句坑，会出错，经过这一句后request就会被关闭，后面读取会出错
             * loger.info("orderNotify "+acceptPram); boolean pass =
             * Signature.checkIsSignValidFromResponseString(acceptPram); if
             * (!pass) {
             * loger.info("==>execute func orderNotify error, sign error.");
             * return ESBUtils.ObjectToXML2(res); }
             */

            // 将微信服务器推送来的信息转为map集合
            Map<String, String> resData = esbu.xmlToMap(request);
            // check trade status

            logger.info("\r\norderNotify请求参数："+resData.toString());

            String returnCode = resData.get("return_code");
            String resultCode = resData.get("result_code");

            boolean tradeSuccess = false;
            if (returnCode != null && returnCode.equalsIgnoreCase("SUCCESS")) {
                //				if (resultCode != null && resultCode.equalsIgnoreCase("SUCCESS")) {
                //					tradeSuccess = true;
                //				}
                tradeSuccess = true;
            }

            String out_trade_no = resData.get("out_trade_no");

            if (tradeSuccess) {

                String sql = "";
                StringBuffer sqlbuf = new StringBuffer();
                sqlbuf.append(
                        " select orderNo, Prepay_id , eid, shopId, LOAD_DOCTYPE, payAmt , CRMPAYCODE   from DCP_PREORDER where Prepay_id = '"
                                + out_trade_no + "' ");
                sql = sqlbuf.toString();

                String sErrorMSG = "";
                DsmDAO dao = null;

                dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

                List<Map<String, Object>> preOrderDatas = dao.executeQuerySQL(sql, null);

                if (preOrderDatas != null && !preOrderDatas.isEmpty()) {
                    String orderNo = preOrderDatas.get(0).get("ORDERNO").toString();
                    String eId = preOrderDatas.get(0).get("EID").toString();
                    String shopId = preOrderDatas.get(0).get("SHOPID").toString();
                    String totAmt = preOrderDatas.get(0).getOrDefault("PAYAMT", "0").toString();
                    String loadDocType = preOrderDatas.get(0).getOrDefault("LOAD_DOCTYPE", "0").toString();
                    String crmPayCode = preOrderDatas.get(0).getOrDefault("CRMPAYCODE", "").toString();
                    String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                    // 列表SQL
                    List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                    // 更新订单表
                    UptBean ubec = new UptBean("OC_ORDER");
                    ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubec.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ubec.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

                    ubec.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
                    ubec.addUpdateValue("PAYAMT", new DataValue(totAmt, Types.VARCHAR));
                    lstData.add(new DataProcessBean(ubec));
                    ubec = null;

                    UptBean ubec2 = new UptBean("DCP_PREORDER");
                    ubec2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubec2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ubec2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    ubec2.addCondition("PREPAY_ID", new DataValue(out_trade_no, Types.VARCHAR));

                    ubec2.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
                    lstData.add(new DataProcessBean(ubec2));
                    ubec2 = null;

                    //DCP_PreOrder

                    // ************* 写付款档开始 *****************

                    String payInfoSql = " select eid , SHOPID , orderNo , payCode, payCodeERP , payName , cardNO, ctType , paySerNum , "
                            + " serialNo , refNo , teriminalNo , descore, pay , extra , changed , bDate , isOrderPay , STATUS , "
                            + " load_docType , order_payCode , ISONLINEPAY , rcPay , shop_pay, load_doctype_Pay , orderpayId ,"
                            + " canInvoice, invoiceNo , isInvoice , invoiceLoadType , prepay_id  "
                            + " from DCP_PreOrder_pay where eid = '"+eId+"' and SHOPID = '"+shopId+"' "
                            + " and prepay_id = '"+out_trade_no+"' ";


                    List<Map<String, Object>> crmPayDatas = dao.executeQuerySQL(payInfoSql, null);

                    if(crmPayDatas != null && !crmPayDatas.isEmpty()){
                        // 外卖点餐支付只会有一种支付方式， 也就是一条信息。
                        // 用循环是因为开发测试的时候会有重复的数据，防止报错

                        for (Map<String, Object> map : crmPayDatas) {
                            //
                            String payCode = map.get("PAYCODE").toString() == null ? "" : map.get("PAYCODE").toString();
                            String payCodeERP = map.get("PAYCODEERP").toString()== null  ? "" : map.get("PAYCODEERP").toString();
                            String PAYNAME = map.get("PAYNAME").toString() == null ? "" : map.get("PAYNAME").toString();
                            String CARDNO = map.get("CARDNO").toString() == null ? "" : map.get("CARDNO").toString();
                            String CTTYPE = map.get("CTTYPE").toString() == null ? "" : map.get("CTTYPE").toString();
                            String SERIALNO = map.get("SERIALNO").toString()== null ? "" : map.get("SERIALNO").toString();
                            String REFNO = map.get("REFNO").toString()== null  ? "" :  map.get("REFNO").toString();
                            String TERIMINALNO = map.get("TERIMINALNO").toString()== null  ? "" : map.get("TERIMINALNO").toString();
                            String DESCORE = map.get("DESCORE").toString() == null ? "0" : map.get("DESCORE").toString() ;
                            String PAY = map.get("PAY").toString() == null ? "0" : map.get("PAY").toString();
                            String EXTRA = map.get("EXTRA").toString() == null ? "0" : map.get("EXTRA").toString();
                            String CHANGED = map.get("CHANGED").toString()== null ? "0" : map.get("CHANGED").toString();
                            String ISORDERPAY = map.get("ISORDERPAY").toString() == null ? "" : map.get("ISORDERPAY").toString();
                            String ISONLINEPAY = map.get("ISONLINEPAY").toString() == null ? "" : map.get("ISONLINEPAY").toString();
                            String ORDER_PAYCODE = map.get("ORDER_PAYCODE").toString()== null  ? "" : map.get("ORDER_PAYCODE").toString();
                            String RCPAY = map.get("RCPAY").toString() == null ? "0" : map.get("RCPAY").toString();
                            String SHOP_PAY = map.get("SHOP_PAY").toString()== null ? "" : map.get("SHOP_PAY").toString();
                            String LOAD_DOCTYPE_PAY = map.get("LOAD_DOCTYPE_PAY").toString()== null ? "" : map.get("LOAD_DOCTYPE_PAY").toString();

                            // 外卖点餐该sql 只会查一次
                            String sqlString = "select * from (select nvl(max(item), '0') + 1  as MAXITEM from OC_order_pay where eid='"
                                    + eId + "'  and load_doctype='" + loadDocType + "' and orderno='" + orderNo + "' )";

                            List<Map<String, Object>> itemList = dao.executeQuerySQL(sqlString, null);
                            int maxItem = 1;
                            if (itemList != null && !itemList.isEmpty()) {
                                try {
                                    maxItem = Integer.parseInt(itemList.get(0).get("MAXITEM").toString());

                                } catch (Exception e) {

                                }
                            }

                            String[] columns2 = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO", "ITEM", "LOAD_DOCTYPE",
                                    "PAYCODE", "PAYCODEERP", "PAYNAME", "CARDNO", "CTTYPE", "PAYSERNUM", "SERIALNO", "REFNO",
                                    "TERIMINALNO", "DESCORE", "PAY", "EXTRA", "CHANGED", "BDATE", "ISORDERPAY", "STATUS",
                                    "ISONLINEPAY", "ORDER_PAYCODE", "RCPAY", "SHOP_PAY", "LOAD_DOCTYPE_PAY", "ORDERPAYID",
                                    "PROCESS_STATUS" };
                            DataValue[] insValue2 = null;

                            insValue2 = new DataValue[] { new DataValue(eId, Types.VARCHAR), // eid
                                    new DataValue(shopId, Types.VARCHAR), // ORGANIZATIONNO  // 数据库下单门店与单头一致
                                    new DataValue(shopId, Types.VARCHAR), // SHOP
                                    new DataValue(orderNo, Types.VARCHAR), // ORDERNO
                                    new DataValue(maxItem+"", Types.INTEGER), // ITEM
                                    new DataValue(loadDocType, Types.VARCHAR), // LOAD_DOCTYPE
                                    new DataValue(payCode, Types.VARCHAR), // PAYCODE
                                    new DataValue(payCodeERP, Types.VARCHAR), // PAYCODEERP
                                    new DataValue(PAYNAME, Types.VARCHAR), // PAYNAME
                                    new DataValue(CARDNO, Types.VARCHAR), // CARDNO
                                    new DataValue(CTTYPE, Types.VARCHAR), // CTTYPE
                                    new DataValue(out_trade_no, Types.VARCHAR), // PAYSERNUM 支付订单号
                                    new DataValue(SERIALNO, Types.VARCHAR), // SERIALNO 银联卡
                                    new DataValue(REFNO, Types.VARCHAR), // REFNO
                                    new DataValue(TERIMINALNO, Types.VARCHAR), // TERIMINALNO
                                    new DataValue(DESCORE, Types.VARCHAR), // DESCORE
                                    new DataValue(PAY, Types.VARCHAR), // PAY 金额
                                    new DataValue(EXTRA, Types.VARCHAR), // EXTRA 溢收金额
                                    new DataValue(CHANGED, Types.VARCHAR), // CHANGED 找零
                                    new DataValue(bDate, Types.VARCHAR), // BDATE 收银营业日期
                                    new DataValue(ISORDERPAY, Types.VARCHAR), // ISORDERPAY 是否定金
                                    new DataValue("100", Types.VARCHAR), // STATUS
                                    new DataValue(ISONLINEPAY, Types.VARCHAR), // ISONLINEPAY 是否平台支付
                                    new DataValue(ORDER_PAYCODE, Types.VARCHAR), // ORDER_PAYCODE
                                    new DataValue(RCPAY, Types.VARCHAR), // RCPAY
                                    new DataValue(SHOP_PAY, Types.VARCHAR), // SHOP_PAY
                                    new DataValue(LOAD_DOCTYPE_PAY, Types.VARCHAR), // LOAD_DOCTYPE_PAY 付款的来源平台类型
                                    new DataValue(out_trade_no, Types.VARCHAR), // ORDERPAYID //定金补录单号
                                    new DataValue("N", Types.VARCHAR)
                            };

                            InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
                            ib2.addValues(insValue2);
                            lstData.add(new DataProcessBean(ib2));

                        }

                    }

                    // ************* 写付款档结束 *****************
                    dao.useTransactionProcessData(lstData);
                    logger.info("\r\n支付完成后回写订单成功");
                }

            }

            res.setReturn_code("SUCCESS");
            String resXml = esbu.ObjectToXML2(res);

            return resXml;

        } catch (Exception e) {
            logger.error("\r\n支付完成后回写订单异常 orderNotify Exception:" + e.toString());
            System.out.println("支付完成后回写订单异常 orderNotify Exception:" + e.toString());
            return esbu.ObjectToXML2(res);


        }

    }


    /**
     * 企业微信重定向地址(用于POS端）
     *
     * 重写向请求地址示例DCP_GetEnterpriseChatLoginStatus_Open
     *
     * http://eliutong2.digiwin.com.cn/dcpservice/DCP/wecom
     * ?params=ZUlkPTk5JmFnZW50SWQ9MTAwMDAwNiZhcHBUeXBlPVBPUyZzaG9wSWQ9MDEmbWFjaGluZUlkPW0wMQ==
     * &code=Qqx0kdsmWtUOzlYJuG_ZHHm9xcM7rdgWYAVFyHl1zuM
     * &state=78db1b9c-8740-42b1-af2a-07f806182720
     * &appid=ww6cbc67ae603a4bdf
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/wecom")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.TEXT_PLAIN + ";charset=UTF-8"})
    public String wecom(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        WxCpServiceUtils.WxCpUserRedisData redisData = new WxCpServiceUtils.WxCpUserRedisData();
        String code = request.getParameter("code");
        String params = request.getParameter("params");
        String state = request.getParameter("state");
        String appId = request.getParameter("appid");

        // 回调地址只支持一个参数，所以需要解码
        Map<String, String> parmMap = new HashMap<>();
        params = Base64.decodeStr(params, "UTF-8");

        String[] split = params.split("&");
        for (String s : split) {
            String[] v = s.split("=");
            parmMap.put(v[0], v[1]);
        }

        WxCpServiceUtils.WxCpLoginLog log = new WxCpServiceUtils.WxCpLoginLog();
        log.setEId(parmMap.get("eId"));
        log.setAppType(parmMap.get("appType"));
        log.setCorpId(appId);
        log.setAgentId(parmMap.get("agentId"));
        log.setMachineId(parmMap.get("machineId"));
        log.setShopId(parmMap.get("shopId"));
        log.setLoginTime(DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        log.setStatus("0");

        try {
            Integer agentId = Integer.parseInt(parmMap.get("agentId"));
            WxCpService wxCpService = WxCpServiceUtils.getWxCpService(agentId);
            WxCpOauth2UserInfo userInfo = wxCpService.getOauth2Service().getUserInfo(code);
            redisData.setUser(wxCpService.getUserService().getById(userInfo.getUserId()));
            if (redisData.getUser().getStatus() == 1) {
                redisData.setErrcode(0);
                redisData.setErrmsg("SUCCESS");
                log.setStatus("1");
                log.setUserId(redisData.getUser().getUserId());
                log.setMachineCode(state);
                log.setName(redisData.getUser().getName());
                return "授权成功";
            } else {
                return "授权失败：非企业成员或企业成员未激活";
            }
        } catch (Exception e) {
            redisData.setErrcode(500);
            redisData.setErrmsg(e.getMessage());
            logger.error("\r\nexec func wecom error", e);
            return "授权失败";
        } finally {
            try {
                String key = "WECOM_STATE" + ":" + state;
                RedisPosPub redis = new RedisPosPub();
                //redis.setString(key, JSON.toJSONString(redisData));
                ParseJson pj = new ParseJson();
                String value = pj.beanToJson(redisData);
                logger.error("\r\n写入Redis WECOM_STATE:" +value);
                redis.setString(key, value);
                redis.setExpire(key, (int) TimeUnit.MINUTES.toSeconds(1));
            } catch (Exception e) {
                logger.error("\r\nWrite Redis Cache Error", e);
            }

            WxCpServiceUtils.writeLoginLog(log);
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/youzanNotify")
    public String YouZanNotifygGet() {
        return "Hello , Welcome to the POS Department for youzan!";
    }

    //供有赞端调用
    @POST
    @Path("/youzanNotify")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String YouZanNotifyPost(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;
        YouZanCallBackService callBack = new YouZanCallBackService();
        YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
        YouZanUtils yz=new YouZanUtils();
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader = null;
            insr = null;

            // dao
            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

            //			res.setCode(0);
            //			res.setMsg("success");
            //解析处理
            try{
                res = callBack.execute(request, jb.toString(), dao);
            }catch(Exception e){
                String msg=ExceptionUtils.getRootCauseMessage(e);
                if(msg!=null&&msg.startsWith("Exception: ")){
                    msg=msg.substring("Exception: ".length(),msg.length());
                }
                res.setCode(-1);
                res.setMsg(msg);
            }

            return com.alibaba.fastjson.JSON.toJSONString(res);
        } catch (Exception e) {
            yz.Log("\r\n***********有赞推送发生异常:"+ExceptionUtils.getRootCauseMessage(e));
            return null;
        } finally {
            jb = null;
            line = null;
            dao = null;
        }
    }



    /**
     * 电子发票
     * @param request
     * @param response
     * @param PLATFORMTYPE 发票类型
     * @return
     * @throws Exception
     */
    @POST
    @Path("/invoice/scan/{PLATFORMTYPE}")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String invoiceScan(@Context HttpServletRequest request, @Context HttpServletResponse response,
                              @PathParam("PLATFORMTYPE") String PLATFORMTYPE) throws Exception {
        String res ="";

        StaticInfo.dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        StaticInfo.dao_pos2 = (DsmDAO) this.springContext.getContext().getBean("Pos2Dao");

        StaticInfo.dao_crm2 = (DsmDAO) this.springContext.getContext().getBean("Crm2Dao");


        StringBuffer jb = new StringBuffer();
        InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

        BufferedReader reader = new BufferedReader(insr);
        String line;
        while ((line = reader.readLine()) != null) {
            jb.append(line);
        }
        String json = jb.toString();
        InvoiceService invoiceService = new InvoiceService();

        // 发票回调日志记录 by jinzma 20220811
        HelpTools.writelog_fileName(
                "***********invoiceScan接口调用,平台类型:"+PLATFORMTYPE+",JSON请求:"+json+" *************",
                "invoiceService");

        // 诺诺发票
        if("NUONUO_V1".equals(PLATFORMTYPE)){
            String[] nnResquest = json.split("&");//
            Map<String, String> map_nnResquest = new HashMap<String, String>();
            String urlDecodeString = "";
            for (String string_mt : nnResquest) {
                try {
                    int indexofSpec = string_mt.indexOf("=");
                    String s1 = string_mt.substring(0, indexofSpec);
                    String s2 = string_mt.substring(indexofSpec + 1);
                    String s2_decode = HelpTools.getURLDecoderString(s2);

                    map_nnResquest.put(s1, s2_decode);
                    urlDecodeString +=s1+"="+s2_decode+"&";
                } catch (Exception e) {
                    continue;
                }
            }

            if(!CollectionUtils.isEmpty(map_nnResquest)){
                String operater = map_nnResquest.get("operater");
                json = new JSONObject(map_nnResquest).toString();
                // 根据单个订单号查询发票信息； OR 根据多个订单号查询发票信息
                if("queryInfoByOrderId".equals(operater)||"queryInfoByOrderList".equals(operater)){
                    res = invoiceService.invoiceQueryInfoByOrder(StaticInfo.dao,json,PLATFORMTYPE);
                }
                // 开票申请回调
                else if("invoiceApply".equals(operater)){
                    String orderno = map_nnResquest.get("orderno");
                    String taxNo = map_nnResquest.get("taxNo");
                    String isSuccess = map_nnResquest.get("isSuccess");
                    String invoiceId = map_nnResquest.get("invoiceId");
                    res = invoiceService.invoiceApply(StaticInfo.dao, json, orderno,taxNo,isSuccess,invoiceId);
                }
                // 开票申请结果回调
                else if("callback".equals(operater)){
                    res = invoiceService.invoiceCallbake(StaticInfo.dao,json,PLATFORMTYPE);
                }
            }



        }else if("RUIHONG".equals(PLATFORMTYPE)){
            res = invoiceService.invoiceCallbake(StaticInfo.dao,json,PLATFORMTYPE);
        }

        return res;
    }

    /**
     * 端点OMS查询和回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/duandianOMS/{OMSTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String duandianOMS(@Context HttpServletRequest request, @Context HttpServletResponse response,@PathParam("OMSTYPE") String omsType)
            throws Exception {
        String sResponseMSG = "{\"code\":200,\"msg\":\"响应成功\",\"success\":false}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        JSONObject resJsonObj = new JSONObject();
        resJsonObj.put("code",200);
        resJsonObj.put("msg","响应成功");
        resJsonObj.put("success",true);
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            resJsonObj.put("code",200);
            resJsonObj.put("msg",e.getMessage());
            resJsonObj.put("success",false);
            sResponseMSG = resJsonObj.toString();
            //response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return sResponseMSG;
        }

        String json = jb.toString();
        if ("queryOrder".equals(omsType))
        {
            duandianCallBackService queryOrderService = new duandianCallBackService();
            sResponseMSG = queryOrderService.queryOrder(json);
            //response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
        }
        else if ("callBackOrder".equals(omsType))
        {
            duandianCallBackService callBackService = new duandianCallBackService();
            sResponseMSG = callBackService.callBackOrder(json);
        }
        else
        {
            resJsonObj.put("code",200);
            resJsonObj.put("msg","/duandianOMS/"+omsType+"接口未实现");
            resJsonObj.put("success",false);
            sResponseMSG = resJsonObj.toString();
            //response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
        }

        return sResponseMSG;

    }

    /**
     * 端点OMS为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/duandianOMS/{OMSTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void duandianOMS_Get(@Context HttpServletRequest request, @Context HttpServletResponse response,@PathParam("OMSTYPE") String omsType)
            throws Exception {
        String sResponseMSG = "{\"code\":200,\"msg\":\"响应成功\",\"success\":true}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }


    /**
     * 美团闪购订单信息
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/MT")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMT(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {
            sErrorMSG = e.getMessage();// 赋值
            sResponseMSG = "{\"data\":\"ok\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;
        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMSGService("1");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            json = null;
            swm = null;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        if (dao != null) {
            dao.closeDAO();
        }
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    // 为了在浏览器上打开显示（浏览器都是Get方式发送）
    /**
     * waimai 为了在浏览器上打开显示（浏览器都是Get方式发送）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/shangou/MT")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMT_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 美团订单配送状态回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/MTShipping")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTShipping(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMSGShippingService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"ok\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 美团订单配送异常回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/MTShippingException")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTShippingException(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMSGShippingExceptionService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"ok\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTOther(@Context HttpServletRequest request, @Context HttpServletResponse response,
                               @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/shangou/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTOther_Get(@Context HttpServletRequest request, @Context HttpServletResponse response,
                                   @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"ok\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (mtType.equals("MTBatchPullPhoneNumber"))// 调用下接口拉取下
        {
            String app_poi_code = request.getQueryString();
            if (app_poi_code == null || app_poi_code.isEmpty()) {
                app_poi_code = "123456";
            }
            try {
                StringBuilder errorMessage = new StringBuilder();
                String res = WMSGOrderProcess.orderBatchPullPhoneNumber(app_poi_code, 0, 50, errorMessage);

                //
                errorMessage = null;
                res = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("confirm"))// 调用商家确认订单
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMSGOrderProcess.orderConfirm(orderID, errorMessage);
                WMSGOrderProcess.orderReviewAfterSales(orderID, errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("cancel"))// 调用商家取消订单
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMSGOrderProcess.orderCancel(orderID, "", "", errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        } else if (mtType.equals("agree"))// 调用订单确认退款请求
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMSGOrderProcess.orderRefundAgree(orderID, "", errorMessage);

                errorMessage = null;
            } catch (Exception e) {

            }
        } else if (mtType.equals("reject"))// 调用驳回订单退款申请
        {
            String orderID = request.getQueryString();
            if (orderID == null || orderID.isEmpty()) {
                orderID = "12345622222222";
            }

            try {
                StringBuilder errorMessage = new StringBuilder();
                WMSGOrderProcess.orderRefundReject(orderID, "", errorMessage);

                errorMessage = null;

            } catch (Exception e) {

            }
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 美团闪购订单退款信息
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/MTRefund")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTRefund (@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            //sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMSGService("3");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 美团闪购订单取消消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/shangou/MTCancel")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void shangouMTCancel (@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        String sResponseMSG = "{\"data\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            //sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }


        /*try {
            JSONObject obj = new JSONObject();
            String order_id = request.getParameter("order_id");
            String reason_code = request.getParameter("reason_code");
            String reason = request.getParameter("reason");
            String deal_op_type = request.getParameter("deal_op_type");
            if (order_id == null) {
                response.setContentType("application/json");
                response.setHeader("Content-Type", "application/json");
                response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
                return;
            }
            obj.put("order_id", order_id);
            obj.put("reason_code", reason_code);
            obj.put("reason", reason);
            obj.put("deal_op_type", deal_op_type);

            //jb = obj.toString();

        } catch (Exception e) {

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }*/

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMSGService("2");
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }

            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 饿了么外卖授权后回传的Token及商户账号信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/ELMToken")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELMToken(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"messsage\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            //sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new WMELM_ISV_TokenService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }


    /**
     *企业微信服务商回调URL验证  by jinzma 20230906
     */
    @GET
    @Path("/isv/wecom/callback")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void IsvWeComGetCallBack(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        String msg_signature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;
        com.alibaba.fastjson.JSONObject response_json = new com.alibaba.fastjson.JSONObject();
        try {
            InputStreamReader insr = new InputStreamReader(request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);
            while ((line = reader.readLine()) != null){
                jb.append(line);
            }

            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

            ISVWeComUtils isvWeComUtils = new ISVWeComUtils();
            //get: 验证回调URL
            if (Check.Null(msg_signature)||Check.Null(timestamp)||Check.Null(nonce)||Check.Null(echostr)){
                HelpTools.writelog_fileName("企业微信服务商回调参数给值异常,url: "+request.getRequestURI()+",参数: "+request.getQueryString()+" body: "+jb+" ", "isvwecom");
            }else {
                String res = isvWeComUtils.verify(dao, msg_signature, timestamp, nonce, echostr);
                response.getOutputStream().write(res.getBytes("UTF-8"));
            }

        } catch (Exception e){
            HelpTools.writelog_fileName("企业微信服务商回调发生异常: " + e.getMessage() + " ", "isvwecom");
        }
    }

    /**
     *企业微信服务商回调事件处理  by jinzma 20230128
     */
    @POST
    @Path("/isv/wecom/callback")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN,MediaType.TEXT_XML,MediaType.MULTIPART_FORM_DATA})
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8"})
    public String IsvWeComPostCallBack(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        String msg_signature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");

        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;

        try {
            InputStreamReader insr = new InputStreamReader(request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);
            while ((line = reader.readLine()) != null){
                jb.append(line);
            }

            dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

            HelpTools.writelog_fileName("企业微信服务商回调请求msg_signature: " + msg_signature + " ", "isvwecom");
            HelpTools.writelog_fileName("企业微信服务商回调请求timestamp: " + timestamp + " ", "isvwecom");
            HelpTools.writelog_fileName("企业微信服务商回调请求nonce: " + nonce + " ", "isvwecom");
            HelpTools.writelog_fileName("企业微信服务商回调请求原文: " + jb + " ", "isvwecom");

            String xml2json = XmlAndJsonConvert.xml2json(jb.toString());
            HelpTools.writelog_fileName("企业微信服务商回调请求XML: " + xml2json + " ", "isvwecom");

            ISVWeComUtils isvWeComUtils = new ISVWeComUtils();
            isvWeComUtils.callBackProcess(dao,xml2json,msg_signature,timestamp,nonce);

            //response.getOutputStream().write("success".getBytes());

        } catch (Exception e){
            HelpTools.writelog_fileName("企业微信服务商回调发生异常: " + e.getMessage() + " ", "isvwecom");
        }

        return "success";

    }



    /**
     *企业微信客户端应用回调URL验证  by jinzma 20230906
     */
    @GET
    @Path("/dcpisv/wecom/callback")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN,MediaType.TEXT_XML,MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String DcpIsvWeComGetCallBack(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;
        com.alibaba.fastjson.JSONObject response_json = new com.alibaba.fastjson.JSONObject();
        InputStreamReader insr = new InputStreamReader(request.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(insr);
        while ((line = reader.readLine()) != null){
            jb.append(line);
        }

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        //get: 验证回调URL
        String msg_signature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        String res ="";
        try {
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            res = dcpWeComUtils.dcpVerify(dao,msg_signature,timestamp,nonce,echostr);
            response.getOutputStream().write(res.getBytes("UTF-8"));
        } catch (Exception e){
            HelpTools.writelog_fileName("企业微信回调URL验证发生异常: " + e.getMessage() + " ", "dcpisvwecom");
        }
        return res;

    }

    /**
     *企业微信客户端回调事件  by jinzma 20230906
     */
    @POST
    @Path("/dcpisv/wecom/callback")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN,MediaType.TEXT_XML,MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public String DcpIsvWeComPostCallBack(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        StringBuffer jb = new StringBuffer();
        String line = "";
        DsmDAO dao;
        com.alibaba.fastjson.JSONObject response_json = new com.alibaba.fastjson.JSONObject();
        InputStreamReader insr = new InputStreamReader(request.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(insr);
        while ((line = reader.readLine()) != null){
            jb.append(line);
        }

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        String msg_signature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");


        HelpTools.writelog_fileName("企业微信回调请求msg_signature: " + msg_signature + " ", "dcpisvwecom");
        HelpTools.writelog_fileName("企业微信回调请求timestamp: " + timestamp + " ", "dcpisvwecom");
        HelpTools.writelog_fileName("企业微信回调请求nonce: " + nonce + " ", "dcpisvwecom");
        HelpTools.writelog_fileName("企业微信回调请求原文: " + jb + " ", "dcpisvwecom");

        String xml2json = XmlAndJsonConvert.xml2json(jb.toString());
        //HelpTools.writelog_fileName("企业微信回调请求XML: " + xml2json + " ", "dcpisvwecom");

        DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
        dcpWeComUtils.callBackProcess(dao,xml2json,msg_signature,timestamp,nonce);

        return "sucess";

    }

}
