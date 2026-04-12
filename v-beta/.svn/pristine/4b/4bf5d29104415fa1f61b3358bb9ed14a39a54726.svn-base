package com.dsc.spos.scheduler.job;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.config.SPosConfig.ProdInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 分销系统OMS用，将新零售商品同步到分销OMS，上次拉取时间戳保存到redis缓存
 *
 * {"startTime":"","endTime":"","pagesize":50,"pagenum":1}
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OMSGoodsSync extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(OMSGoodsSync.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public OMSGoodsSync()
    {

    }

    public OMSGoodsSync(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    public String doExe()  throws Exception
    {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********同步商品到分销OMS系统OMSGoodsSync正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-同步商品到分销OMS系统OMSGoodsSync正在执行中！";
            return sReturnInfo;
        }

        //http://124.70.146.167/restful/service/I/IOpenApiRequestService/postRequest
        String oms_url=StaticInfo.OMS_Url;
        String oms_eid=StaticInfo.OMS_Eid;
        String erp_eid=StaticInfo.OMS_ERP_Eid;
        if (Check.Null(oms_url) || Check.Null(oms_eid)|| Check.Null(erp_eid))
        {
            sReturnInfo="定时传输任务-同步商品到分销OMS系统OMSGoodsSync配置文件参数OMS_Url或OMS_Eid或OMS_ERP_Eid不能为空！";
            logger.error("\r\n*********"+sReturnInfo+"************\r\n");
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********同步商品到分销OMS系统OMSGoodsSync定时调用Start:************\r\n");

        RedisPosPub redis=new RedisPosPub();
        JSONObject jsonRedis=new JSONObject();

        try
        {
            String values=redis.getString("OMS_GoodsSync");
            //减1天
            String startTime=LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"000000000";

            //这个传输时间被他们搞坏了，不是系统时间啦
            String tran_time="";//LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

            //取最大时间
            String sqlMax="select max(TRAN_TIME) TRAN_TIME from dcp_goods where eid='"+erp_eid+"' ";
            List<Map<String, Object>> sqllistMax=this.doQueryData(sqlMax, null);
            if (sqllistMax != null && sqllistMax.size()>0)
            {
                tran_time=sqllistMax.get(0).get("TRAN_TIME").toString();
            }

            //没值，就跟开始时间一样
            if (Check.Null(tran_time))
            {
                tran_time=startTime;
            }

            String endTime="";
            int pagesize=50;
            int pagenum=0;
            int pageStart=0;
            int pageEnd=0;
            if (Check.Null(values))
            {
                endTime=tran_time;
                pagenum=0;
                pageStart=0;
                pageEnd=1;

                //第一次处理一下历史数据,那个时间可能在很久以前
                if (startTime.compareTo(tran_time)>=0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    Date Trans_date = sdf.parse(tran_time);

                    //转换传输日期
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Trans_date);
                    //第一次，传输减1天
                    calendar.add(Calendar.DATE,-1);
                    startTime=sdf.format(calendar.getTime());
                }
            }
            else
            {
                JSONObject jsonObject=new JSONObject(values);
                startTime=jsonObject.get("startTime").toString();
                endTime=jsonObject.get("endTime").toString();

                //开始时间=结束时间 说明那个时间段已经没记录了
                if (startTime.equals(endTime))
                {
                    endTime=tran_time;
                }
                pagesize=jsonObject.getInt("pagesize");
                pagenum=jsonObject.getInt("pagenum");
                pageStart=pagenum;
                pageEnd=pagenum+1;

                //开始时间=结束时间 还相等，说明没记录了，没必要执行SQL啦
                if (startTime.equals(endTime))
                {
                    sReturnInfo="定时传输任务-同步商品到分销OMS系统OMSGoodsSync没记录，时间段:"+endTime+"！";
                    logger.error("\r\n*********"+sReturnInfo+"************\r\n");
                    return sReturnInfo;
                }
            }

            //通过配置文件读取
            String langtype="zh_CN";
            List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
            if(lstProd!=null&&!lstProd.isEmpty())
            {
                langtype=lstProd.get(0).getHostLang().getValue();
            }

            //每页50笔
            String sql="select * from (select a.EID,a.PLUNO,a.WUNIT,b.PLU_NAME,c.SPEC ,rownum rn " +
                    "from DCP_goods a " +
                    "left join DCP_GOODS_LANG b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"' " +
                    "left join Dcp_Goods_Unit_Lang c on a.eid=c.eid and a.pluno=c.pluno and a.SUNIT=c.ounit and c.lang_type='"+langtype+"' " +
                    "where a.eid='"+erp_eid+"' and  a.tran_time>'"+startTime+"' and a.tran_time<='"+endTime+"') where rn>"+(pageStart*pagesize)+" and rn<="+(pageEnd*pagesize);
            List<Map<String, Object>> sqllist=this.doQueryData(sql, null);

            if (sqllist != null && sqllist.isEmpty() == false)
            {
                JSONObject head=new JSONObject();
                JSONObject param=new JSONObject();
                JSONArray requestparam=new JSONArray();
                for (Map<String, Object> map : sqllist)
                {
                    JSONObject detail=new JSONObject();
                    detail.put("cagent",oms_eid);
                    detail.put("caggoods",map.get("PLUNO").toString());
                    detail.put("caggoods_n1",map.get("PLU_NAME").toString());
                    detail.put("cagunit",map.get("WUNIT").toString());
                    detail.put("cag001",map.get("SPEC").toString());
                    requestparam.put(detail);
                }
                param.put("requestinterface","IOpenApiRequestService");
                param.put("requestmethod","syncCag2Fx");
                param.put("requestmodel","I");
                param.put("requestparam",requestparam);
                head.put("param",param);

                logger.info("\r\n*********同步商品到分销OMS系统OMSGoodsSync定时调用请求:"+head.toString()+"************\r\n");

                String resBody=HttpSend.Sendhttp("POST",head.toString(),oms_url);
                requestparam=null;
                param=null;
                head=null;

                if (Check.Null(resBody))
                {
                    logger.error("\r\n*********同步商品到分销OMS系统OMSGoodsSync 请求分销系统报错，返回为空************\r\n");
                }
                else
                {
                    JSONObject res=new JSONObject(resBody);
                    String resStatus=res.get("status").toString();
                    if (resStatus.equals("200"))
                    {
                        JSONObject response=res.getJSONObject("response");
                        String Status=response.get("Status").toString();
                        //成功
                        if (Status.equals("0"))
                        {
                            //优化一下，没有下一页了
                            if (sqllist.size() < pagesize)
                            {
                                //说明这个时间段已经没记录了
                                jsonRedis.put("startTime",endTime);
                                jsonRedis.put("endTime",endTime);
                                jsonRedis.put("pagesize",pagesize);
                                jsonRedis.put("pagenum",0);
                                redis.setString("OMS_GoodsSync",jsonRedis.toString());
                            }
                            else
                            {
                                //时间段和页码信息写进去
                                jsonRedis.put("startTime",startTime);
                                jsonRedis.put("endTime",endTime);
                                jsonRedis.put("pagesize",pagesize);
                                jsonRedis.put("pagenum",pageEnd);
                                redis.setString("OMS_GoodsSync",jsonRedis.toString());
                            }
                        }
                        else
                        {
                            String Message=response.get("Message").toString();
                            logger.error("\r\n*********同步商品到分销OMS系统OMSGoodsSync 请求分销系统报错，返回内容:"+Message+"************\r\n");
                        }
                    }
                    else
                    {
                        logger.error("\r\n*********同步商品到分销OMS系统OMSGoodsSync 请求分销系统报错，返回内容:"+resBody+"************\r\n");
                    }

                }
            }
            else
            {
                //说明这个时间段已经没记录了
                jsonRedis.put("startTime",endTime);
                jsonRedis.put("endTime",endTime);
                jsonRedis.put("pagesize",pagesize);
                jsonRedis.put("pagenum",0);
                redis.setString("OMS_GoodsSync",jsonRedis.toString());
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\n******同步商品到分销OMS系统OMSGoodsSync报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******同步商品到分销OMS系统OMSGoodsSync报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            jsonRedis=null;
            logger.info("\r\n*********同步商品到分销OMS系统OMSGoodsSync定时调用End:************\r\n");
            redis.Close();
        }

        return sReturnInfo;
    }

}



