package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.duandian.duandianService;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DuanDianOMSOrderSync extends InitJob {
    static boolean bRun=false;//标记此服务是否正在执行中
    public DuanDianOMSOrderSync()
    {

    }
    public String doExe() throws Exception
    {
        String sReturnInfo = "";
        String jobName = DuanDianOMSOrderSync.class.getSimpleName();
        if (bRun){
            HelpTools.writelog_fileName("***************DuanDianOMSOrderSync 正在执行中,本次调用取消****************",jobName);
            return null;
        }
        bRun=true;//
        HelpTools.writelog_fileName("***************DuanDianOMSOrderSync START****************",jobName);
        try
        {
            String sql = " select DISTINCT A.EID from DCP_ECOMMERCE A where A.STATUS='100' AND A.LOADDOCTYPE='"+ orderLoadDocType.DUANDIANOMS+"' ";
            HelpTools.writelog_fileName("查询端点OMS渠道类型（获取EID）对应的sql:"+sql,jobName);
            List<Map<String, Object>> oneLv1 = this.doQueryData(sql, null);
            if (oneLv1==null||oneLv1.isEmpty())
            {
                HelpTools.writelog_fileName("查询端点OMS渠道类型为空，无需同步订单！"+sql,jobName);
                return sReturnInfo;
            }
            for (Map<String, Object> map : oneLv1)
            {
                String eId =  map.getOrDefault("EID","").toString();
                if (eId.trim().isEmpty())
                {
                    continue;
                }
                //获取端点OMS渠道类型
                List<Map<String, Object>> duanDianAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
                        orderLoadDocType.DUANDIANOMS);
                if (duanDianAppKeyList==null||duanDianAppKeyList.isEmpty())
                {
                    continue;
                }
                Map<String, Object> setMap = duanDianAppKeyList.get(0);
                String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                //查询正向订单，没有同步的statustooms为空
                syncNewOrder(eId,sdate,setMap);
                //查询正向订单，处理失败的statustooms为3
                syncFailOrder(eId,sdate,setMap);
                //查询逆向订单,没有同步的statustooms为空
                syncReverseOrder(eId,sdate,setMap);
                //查询逆向订单,处理失败的statustooms为3
                syncReverseOrder_fail(eId,sdate,setMap);

            }
        }
        catch (Exception e)
        {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                pw.flush();
                pw.close();
                errors.flush();
                errors.close();

                HelpTools.writelog_fileName("******DuanDianOMSOrderSync报错信息" + e.getMessage() +"\r\n" + errors.getBuffer().toString()+ "******",jobName);

                pw=null;
                errors=null;
            }
            catch (IOException e1) {
                HelpTools.writelog_fileName("******DuanDianOMSOrderSync报错信息" + e.getMessage() + "******",jobName);
            }

        }
        finally
        {
            bRun = false;
        }
        HelpTools.writelog_fileName("***************DuanDianOMSOrderSync END****************",jobName);
        return sReturnInfo;
    }

    /**
     * 正向订单(没有上传过的)
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncNewOrder(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSOrderSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【正向订单】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select  A.EID,A.SHOPID,A.SALENO,row_number()over (order by A.TRAN_TIME desc ) rn from DCP_SALE  A");
            sqlbuf.append(" where  A.TYPE=0  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.SDATE<='"+endDate+"'");
            sqlbuf.append(" and A.SDATE>='"+beginDate+"'");
            sqlbuf.append(" and A.STATUSTOOMS IS NULL ");//未上传的
            sqlbuf.append(") where rn<=1000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【正向订单】查询sql="+sql,jobName);
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                duandianService ddService = new duandianService();
                String sql_item = "";
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String saleNo = map.get("SALENO").toString();
                        String shopId = map.get("SHOPID").toString();
                        sql_item = " SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                + " FROM DCP_SALE A "
                                + " inner join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                                + " where A.STATUSTOOMS IS NULL and A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SALENO='"+saleNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        HelpTools.writelog_fileName("循环同步【正向订单】调用接口开始,单号saleNo="+saleNo,jobName);
                        String res = ddService.syncChannelOrder(setMap,getQDataDetail);
                        HelpTools.writelog_fileName("循环同步【正向订单】调用接口结束,返回res:"+res+",单号saleNo="+saleNo,jobName);
                        if (res==null||res.isEmpty())
                        {
                            continue;
                        }
                        JSONObject resJson = new JSONObject(res);
                        String success = resJson.optString("success");
                        String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败
                        if ("true".equalsIgnoreCase(success))
                        {
                            STATUSTOOMS = "1";
                        }
                        else
                        {
                            STATUSTOOMS = "3";
                        }
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("dcp_sale");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ubecOrder.addCondition("SALENO", new DataValue(saleNo, Types.VARCHAR));
                        ubecOrder.addUpdateValue("STATUSTOOMS", new DataValue(STATUSTOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
                        lstData.add(new DataProcessBean(ubecOrder));
                        StaticInfo.dao.useTransactionProcessData(lstData);
                    }
                    catch (Exception e)
                    {

                    }
                }

            }
            else
            {
                HelpTools.writelog_fileName("同步【正向订单】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【正向订单】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【正向订单】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

    /**
     * 正向订单(上传失败的)
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncFailOrder(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSOrderSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【正向订单】【处理失败的重传】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select  A.EID,A.SHOPID,A.SALENO,row_number()over (order by A.TRAN_TIME desc ) rn from DCP_SALE  A");
            sqlbuf.append(" where  A.TYPE=0  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.SDATE<='"+endDate+"'");
            sqlbuf.append(" and A.SDATE>='"+beginDate+"'");
            sqlbuf.append(" and A.STATUSTOOMS='3' ");//未上传的
            sqlbuf.append(") where rn<=1000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【正向订单】【处理失败的重传】查询sql="+sql,jobName);
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                duandianService ddService = new duandianService();
                String sql_item = "";
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String saleNo = map.get("SALENO").toString();
                        String shopId = map.get("SHOPID").toString();
                        sql_item = " SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                + " FROM DCP_SALE A "
                                + " inner join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                                + " where A.STATUSTOOMS='3' and A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SALENO='"+saleNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        HelpTools.writelog_fileName("循环同步【正向订单】【处理失败的重传】调用接口开始,单号saleNo="+saleNo,jobName);
                        String res = ddService.syncChannelOrder(setMap,getQDataDetail);
                        HelpTools.writelog_fileName("循环同步【正向订单】【处理失败的重传】调用接口结束,返回res:"+res+",单号saleNo="+saleNo,jobName);
                        if (res==null||res.isEmpty())
                        {
                            continue;
                        }
                        JSONObject resJson = new JSONObject(res);
                        String success = resJson.optString("success");

                        String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败的重传
                        if ("true".equalsIgnoreCase(success))
                        {
                            STATUSTOOMS = "1";
                        }
                        else
                        {
                            STATUSTOOMS = "3";
                        }
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("dcp_sale");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ubecOrder.addCondition("SALENO", new DataValue(saleNo, Types.VARCHAR));
                        ubecOrder.addUpdateValue("STATUSTOOMS", new DataValue(STATUSTOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
                        lstData.add(new DataProcessBean(ubecOrder));
                        StaticInfo.dao.useTransactionProcessData(lstData);
                    }
                    catch (Exception e)
                    {

                    }
                }

            }
            else
            {
                HelpTools.writelog_fileName("同步【正向订单】【处理失败的重传】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【正向订单】【处理失败的重传】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【正向订单】【处理失败的重传】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

    /**
     * 逆向订单
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncReverseOrder(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSOrderSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【逆向订单】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            StringBuffer sqlbuf = new StringBuffer("");
            String sqlCon = " select saleNo from DCP_SALE where TYPE=1 and STATUSTOOMS='2' and EID='"+eId+"'";
            sqlbuf.append("select * from ( select  A.EID,A.SHOPID,A.SALENO,row_number()over (order by A.TRAN_TIME desc ) rn from DCP_SALE  A");
            sqlbuf.append(" where  A.TYPE=1  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.SDATE<='"+endDate+"'");
            sqlbuf.append(" and A.SDATE>='"+beginDate+"'");
            sqlbuf.append(" and A.STATUSTOOMS IS NULL ");//未上传的
            sqlbuf.append(" and A.OFNO in ( "+sqlCon+")");
            sqlbuf.append(") where rn<=1000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【逆向订单】查询sql="+sql,jobName);
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                duandianService ddService = new duandianService();
                String sql_item = "";
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String saleNo = map.get("SALENO").toString();
                        String shopId = map.get("SHOPID").toString();
                        sql_item = " SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                + " FROM DCP_SALE A "
                                + " inner join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                                + " where A.STATUSTOOMS IS NULL and A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SALENO='"+saleNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        HelpTools.writelog_fileName("循环同步【逆向订单】调用接口开始,单号saleNo="+saleNo,jobName);
                        String res = ddService.syncChannelOrder(setMap,getQDataDetail);
                        HelpTools.writelog_fileName("循环同步【逆向订单】调用接口结束,返回res:"+res+",单号saleNo="+saleNo,jobName);
                        if (res==null||res.isEmpty())
                        {
                            continue;
                        }
                        JSONObject resJson = new JSONObject(res);
                        String success = resJson.optString("success");
                        String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败
                        if ("true".equalsIgnoreCase(success))
                        {
                            STATUSTOOMS = "1";
                        }
                        else
                        {
                            STATUSTOOMS = "3";
                        }
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("dcp_sale");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ubecOrder.addCondition("SALENO", new DataValue(saleNo, Types.VARCHAR));
                        ubecOrder.addUpdateValue("STATUSTOOMS", new DataValue(STATUSTOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
                        lstData.add(new DataProcessBean(ubecOrder));
                        StaticInfo.dao.useTransactionProcessData(lstData);
                    }
                    catch (Exception e)
                    {

                    }
                }

            }
            else
            {
                HelpTools.writelog_fileName("同步【逆向订单】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【逆向订单】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【逆向订单】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

    /**
     * 逆向订单(处理失败的)
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncReverseOrder_fail(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSOrderSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【逆向订单】【处理失败的重传】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            StringBuffer sqlbuf = new StringBuffer("");
            String sqlCon = " select saleNo from DCP_SALE where TYPE=1 and STATUSTOOMS='2' and EID='"+eId+"'";
            sqlbuf.append("select * from ( select  A.EID,A.SHOPID,A.SALENO,row_number()over (order by A.TRAN_TIME desc ) rn from DCP_SALE  A");
            sqlbuf.append(" where  A.TYPE=1  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.SDATE<='"+endDate+"'");
            sqlbuf.append(" and A.SDATE>='"+beginDate+"'");
            sqlbuf.append(" and A.STATUSTOOMS='3' ");//未上传的
            sqlbuf.append(" and A.OFNO in ( "+sqlCon+")");
            sqlbuf.append(") where rn<=1000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【逆向订单】【处理失败的重传】查询sql="+sql,jobName);
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                duandianService ddService = new duandianService();
                String sql_item = "";
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String saleNo = map.get("SALENO").toString();
                        String shopId = map.get("SHOPID").toString();
                        sql_item = " SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                + " FROM DCP_SALE A "
                                + " inner join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                                + " where A.STATUSTOOMS='3' and A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SALENO='"+saleNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        HelpTools.writelog_fileName("循环同步【逆向订单】【处理失败的重传】调用接口开始,单号saleNo="+saleNo,jobName);
                        String res = ddService.syncChannelOrder(setMap,getQDataDetail);
                        HelpTools.writelog_fileName("循环同步【逆向订单】【处理失败的重传】调用接口结束,返回res:"+res+",单号saleNo="+saleNo,jobName);
                        if (res==null||res.isEmpty())
                        {
                            continue;
                        }
                        JSONObject resJson = new JSONObject(res);
                        String success = resJson.optString("success");
                        String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败
                        if ("true".equalsIgnoreCase(success))
                        {
                            STATUSTOOMS = "1";
                        }
                        else
                        {
                            STATUSTOOMS = "3";
                        }
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("dcp_sale");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ubecOrder.addCondition("SALENO", new DataValue(saleNo, Types.VARCHAR));
                        ubecOrder.addUpdateValue("STATUSTOOMS", new DataValue(STATUSTOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
                        lstData.add(new DataProcessBean(ubecOrder));
                        StaticInfo.dao.useTransactionProcessData(lstData);
                    }
                    catch (Exception e)
                    {

                    }
                }

            }
            else
            {
                HelpTools.writelog_fileName("同步【逆向订单】【处理失败的重传】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【逆向订单】【处理失败的重传】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【逆向订单】【处理失败的重传】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

}
