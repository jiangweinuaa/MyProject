package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.duandian.duandianService;
import com.dsc.spos.thirdpart.duandian.marketingVirgoChangeReq;
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
public class DuanDianOMSPointSync extends InitJob {
    static boolean bRun=false;//标记此服务是否正在执行中
    public DuanDianOMSPointSync()
    {

    }
    public String doExe() throws Exception
    {
        String sReturnInfo = "";
        String jobName = DuanDianOMSPointSync.class.getSimpleName();
        if (bRun){
            HelpTools.writelog_fileName("***************DuanDianOMSPointSync 正在执行中,本次调用取消****************",jobName);
            return null;
        }
        bRun=true;//
        HelpTools.writelog_fileName("***************DuanDianOMSPointSync START****************",jobName);
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
                String sdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//注意格式
                //查询积分异动单，没有同步的tooms为N
                syncNewOrder(eId,sdate,setMap);
                //查询积分异动单，同步失败的tooms为E
                syncFailOrder(eId,sdate,setMap);

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

                HelpTools.writelog_fileName("******DuanDianOMSPointSync报错信息" + e.getMessage() +"\r\n" + errors.getBuffer().toString()+ "******",jobName);

                pw=null;
                errors=null;
            }
            catch (IOException e1) {
                HelpTools.writelog_fileName("******DuanDianOMSPointSync报错信息" + e.getMessage() + "******",jobName);
            }

        }
        finally
        {
            bRun = false;
        }
        HelpTools.writelog_fileName("***************DuanDianOMSPointSync END****************",jobName);
        return sReturnInfo;
    }

    /**
     * 积分异动单(没有上传过的)
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncNewOrder(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSPointSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【积分异动单】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            String beginDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            //beginDate = "2022-01-01";
            String sqlCon = "'CRM014','CRM016','CRM017'";//这些不走，走的实时调用
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select A.ID,A.BILLTYPE,A.CARDNO,A.DIRECT,A.CHANGEVALUE,A.DESCRIPTION,row_number()over (order by A.CREATETIME desc ) rn from CRM_CARDACCOUNTCHANGE  A");
            sqlbuf.append(" where  A.ACCOUNTTYPE=3  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.BILLTYPE not in ("+sqlCon+")");
            sqlbuf.append(" and A.BILLDATE<=TO_DATE('"+endDate+"','yyyy-mm-dd')");
            sqlbuf.append(" and A.BILLDATE>=TO_DATE('"+beginDate+"','yyyy-mm-dd')");
            sqlbuf.append(" and A.TOOMS='N' ");//未上传的
            sqlbuf.append(") where rn<=2000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【积分异动单】查询sql="+sql,jobName);
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
                        String id = map.get("ID").toString();
                        String cardNo = map.get("CARDNO").toString();
                        String direct = map.get("DIRECT").toString();
                        String changeValue = map.get("CHANGEVALUE").toString();
                        String description = map.getOrDefault("DESCRIPTION","").toString();
                        sql_item = " SELECT A.MEMBERID,A.CARDNO "
                                + " FROM crm_card A  "
                                + " where  A.EID='"+eId+"' and A.CARDNO='"+cardNo+"' order by LASTMODITIME desc";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        String TOOMS = "";//Y已上传，E上传失败，F没有会员的
                        String memberId = getQDataDetail.get(0).getOrDefault("MEMBERID","").toString();
                        if (memberId.isEmpty())
                        {
                            TOOMS = "F";
                        }
                        else
                        {
                            marketingVirgoChangeReq reqDataDTO = new marketingVirgoChangeReq();
                            reqDataDTO.setBizNo(id);
                            reqDataDTO.setMemberNo(memberId);
                            if ("1".equals(direct))
                            {
                                reqDataDTO.setChangeType("UP");
                            }
                            else if ("-1".equals(direct))
                            {
                                reqDataDTO.setChangeType("DOWN");
                            }
                            else
                            {

                            }
                            double changePoint = 0;
                            try
                            {
                                changePoint = Double.parseDouble(changeValue);
                            }
                            catch ( Exception e)
                            {

                            }
                            reqDataDTO.setChangePoint(changePoint);
                            reqDataDTO.setReason(description);
                            HelpTools.writelog_fileName("循环同步【积分异动单】调用接口开始,异动单号ID="+id+",会员ID="+memberId,jobName);
                            String res = ddService.syncMarketingPoint(setMap,reqDataDTO);
                            HelpTools.writelog_fileName("循环同步【积分异动单】调用接口结束,返回res:"+res+",异动单号ID="+id+",会员ID="+memberId,jobName);
                            if (res==null||res.isEmpty())
                            {
                                continue;
                            }
                            JSONObject resJson = new JSONObject(res);
                            String success = resJson.optString("success");
                            String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败
                            if ("true".equalsIgnoreCase(success))
                            {
                                TOOMS = "Y";
                            }
                            else
                            {
                                TOOMS = "E";
                            }
                        }

                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("CRM_CARDACCOUNTCHANGE");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("ID", new DataValue(id, Types.VARCHAR));

                        ubecOrder.addUpdateValue("TOOMS", new DataValue(TOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
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
                HelpTools.writelog_fileName("同步【积分异动单】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【积分异动单】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【积分异动单】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

    /**
     * 积分异动单(上传失败的)
     * @param eId
     * @param sDate
     * @param setMap
     * @throws Exception
     */
    private void syncFailOrder(String eId,String sDate,Map<String,Object> setMap) throws Exception
    {
        String jobName = DuanDianOMSPointSync.class.getSimpleName();
        try
        {
            HelpTools.writelog_fileName("同步【积分异动单】【处理失败的重传】开始执行,EID="+eId,jobName);
            if (sDate==null||sDate.isEmpty())
            {
                sDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
            String endDate = sDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            //beginDate = "2022-01-01";
            String sqlCon = "'CRM014','CRM016','CRM017'";//这些不走，走的实时调用
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select A.ID,A.BILLTYPE,A.CARDNO,A.DIRECT,A.CHANGEVALUE,A.DESCRIPTION,row_number()over (order by A.CREATETIME desc ) rn from CRM_CARDACCOUNTCHANGE  A");
            sqlbuf.append(" where  A.ACCOUNTTYPE=3  ");
            sqlbuf.append(" and A.EID='"+eId+"'");
            sqlbuf.append(" and A.BILLTYPE not in ("+sqlCon+")");
            sqlbuf.append(" and A.BILLDATE<=TO_DATE('"+endDate+"','yyyy-mm-dd')");
            sqlbuf.append(" and A.BILLDATE>=TO_DATE('"+beginDate+"','yyyy-mm-dd')");
            sqlbuf.append(" and A.TOOMS='E' ");//上传失败
            sqlbuf.append(") where rn<=2000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("同步【积分异动单】【处理失败的重传】查询sql="+sql,jobName);
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
                        String id = map.get("ID").toString();
                        String cardNo = map.get("CARDNO").toString();
                        String direct = map.get("DIRECT").toString();
                        String changeValue = map.get("CHANGEVALUE").toString();
                        String description = map.getOrDefault("DESCRIPTION","").toString();
                        sql_item = " SELECT A.MEMBERID,A.CARDNO "
                                + " FROM crm_card A  "
                                + " where  A.EID='"+eId+"' and A.CARDNO='"+cardNo+"' order by LASTMODITIME desc";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        String TOOMS = "";//Y已上传，E上传失败，F没有会员的
                        String memberId = getQDataDetail.get(0).getOrDefault("MEMBERID","").toString();
                        if (memberId.isEmpty())
                        {
                            TOOMS = "F";
                        }
                        else
                        {
                            marketingVirgoChangeReq reqDataDTO = new marketingVirgoChangeReq();
                            reqDataDTO.setBizNo(id);
                            reqDataDTO.setMemberNo(memberId);
                            if ("1".equals(direct))
                            {
                                reqDataDTO.setChangeType("UP");
                            }
                            else if ("-1".equals(direct))
                            {
                                reqDataDTO.setChangeType("DOWN");
                            }
                            else
                            {

                            }
                            double changePoint = 0;
                            try
                            {
                                changePoint = Double.parseDouble(changeValue);
                            }
                            catch ( Exception e)
                            {

                            }
                            reqDataDTO.setChangePoint(changePoint);
                            reqDataDTO.setReason(description);
                            HelpTools.writelog_fileName("循环同步【积分异动单】【处理失败的重传】调用接口开始,异动单号ID="+id+",会员ID="+memberId,jobName);
                            String res = ddService.syncMarketingPoint(setMap,reqDataDTO);
                            HelpTools.writelog_fileName("循环同步【积分异动单】【处理失败的重传】调用接口结束,返回res:"+res+",异动单号ID="+id+",会员ID="+memberId,jobName);
                            if (res==null||res.isEmpty())
                            {
                                continue;
                            }
                            JSONObject resJson = new JSONObject(res);
                            String success = resJson.optString("success");
                            String STATUSTOOMS = "";//1已上传，2单据成功，3单据处理失败
                            if ("true".equalsIgnoreCase(success))
                            {
                                TOOMS = "Y";
                            }
                            else
                            {
                                TOOMS = "E";
                            }
                        }

                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("CRM_CARDACCOUNTCHANGE");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("ID", new DataValue(id, Types.VARCHAR));

                        ubecOrder.addUpdateValue("TOOMS", new DataValue(TOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
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
                HelpTools.writelog_fileName("同步【积分异动单】【处理失败的重传】查询返回无数据！",jobName);
            }
            HelpTools.writelog_fileName("同步【积分异动单】【处理失败的重传】执行结束,EID="+eId,jobName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("同步【积分异动单】【处理失败的重传】执行异常:"+e.getMessage()+",EID="+eId,jobName);
        }
    }

}
