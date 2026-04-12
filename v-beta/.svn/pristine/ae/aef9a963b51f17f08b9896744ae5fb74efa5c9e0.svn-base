package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_HolidayGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_HolidayGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_HolidayGoodsQuery extends SPosBasicService<DCP_HolidayGoodsQueryReq, DCP_HolidayGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_HolidayGoodsQueryReq req) throws Exception {
        
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request节点不存在！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_HolidayGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_HolidayGoodsQueryReq>(){};
    }
    
    @Override
    protected DCP_HolidayGoodsQueryRes getResponseType() {
        return new DCP_HolidayGoodsQueryRes();
    }
    
    @Override
    protected DCP_HolidayGoodsQueryRes processJson(DCP_HolidayGoodsQueryReq req) throws Exception {
        DCP_HolidayGoodsQueryRes res = this.getResponse();
        String sql = null;
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        
        res.setDatas(new ArrayList<>());
        sql = this.getQuerySql(req);
        
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            Map<String,Boolean> condition = new HashMap<String,Boolean>();
            condition.put("BILLNO",true);
            List<Map<String,Object>> getHeader = MapDistinct.getMap(getQData,condition);
            for (Map<String,Object> oneData : getHeader)
            {
                DCP_HolidayGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
                oneLv1.setGoodsList(new ArrayList<>());
                String billNo = oneData.get("BILLNO").toString();
                String status = oneData.getOrDefault("STATUS","").toString();
                String billDate = oneData.getOrDefault("BILLDATE","").toString();//yyyy-mm-dd hh:mm:ss
                if(billDate!=null&&billDate.length()==19)
                {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(billDate);
                    billDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                }
                
                //1-未开始：STATUS=-1 或 BEGINDATE>系统日期
                // execStatus=1 时为'N',否则='Y';
                String canGoodsSync = "Y";//是否可操作 商品同步Y/N
                
                String beginDate = oneData.getOrDefault("BEGINDATE","").toString();//yyyy-mm-dd hh:mm:ss
                if(beginDate!=null&&beginDate.length()==19)
                {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beginDate);
                    beginDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                    Date date2 = new Date();
                    //STATUS=-1 或 BEGINDATE>系统日期
                    if (date1.after(date2))
                    {
                        canGoodsSync = "N";
                    }
                    
                }
                
                String endDate = oneData.getOrDefault("ENDDATE","").toString();//yyyy-mm-dd hh:mm:ss
                if(endDate!=null&&endDate.length()==19)
                {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
                    endDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                }
                
                if (status.equals("-1"))
                {
                    //STATUS=-1 或 BEGINDATE>系统日期
                    canGoodsSync = "N";
                }
                
                oneLv1.setBillNo(billNo);
                oneLv1.setBillDate(billDate);
                oneLv1.setBeginDate(beginDate);
                oneLv1.setEndDate(endDate);
                oneLv1.setStatus(status);
                oneLv1.setCanGoodsSync(canGoodsSync);
                oneLv1.setCompanyId(oneData.getOrDefault("COMPANYID","").toString());
                oneLv1.setEmployeeId(oneData.getOrDefault("EMPLOYEEID","").toString());
                oneLv1.setEmployeeName(oneData.getOrDefault("OP_NAME","").toString());
                oneLv1.setRemark(oneData.getOrDefault("REMARK","").toString());
                oneLv1.setTitle(oneData.getOrDefault("TITLE","").toString());
                oneLv1.setGoodsSync(oneData.getOrDefault("GOODSSYNC","").toString());
                oneLv1.setRedisUpdateSuccess(oneData.getOrDefault("REDISUPDATESUCCESS","").toString());
                
                for (Map<String,Object> oneData2 : getQData)
                {
                    DCP_HolidayGoodsQueryRes.level2Elm oneLv2 = res.new level2Elm();
                    String billNo_detail = oneData2.getOrDefault("BILLNO","").toString();
                    String pluNo = oneData2.getOrDefault("PLUNO","").toString();
                    if (billNo_detail.isEmpty()||pluNo.isEmpty())
                    {
                        continue;
                    }
                    if (!billNo_detail.equals(billNo))
                    {
                        continue;
                    }
                    
                    String sortId_str = oneData2.getOrDefault("SORTID","0").toString();
                    int sortId = 0;
                    try
                    {
                        sortId = Integer.parseInt(sortId_str);
                    }
                    catch (Exception e)
                    {
                    
                    }
                    
                    String pluName = oneData2.getOrDefault("PLU_NAME","").toString();
                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setSortId(sortId);
                    oneLv1.getGoodsList().add(oneLv2);
                }
                
                res.getDatas().add(oneLv1);
                
            }
            
        }
        
        
        
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_HolidayGoodsQueryReq req) throws Exception {
        String eId = req.geteId();
        String billNo = req.getRequest().getBillNo();
        String status = req.getRequest().getStatus();
        String execStatus = req.getRequest().getExecStatus();
        String ketTxt = req.getRequest().getKeyTxt();
        String langType = req.getLangType();
        if (Check.Null(langType))
        {
            langType="zh_CN";
        }
        
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        
        int startRow = (pageNumber-1) * pageSize;
        int endRow = startRow + pageSize;
        
        StringBuffer sqlBuffer = new StringBuffer("");
        sqlBuffer.append("");
        
        sqlBuffer.append(" with p as (");
        sqlBuffer.append(" select * from (");
        sqlBuffer.append(" select  count(*) over() num,rownum rn,a.* from dcp_holidaygoods a "
                + " where a.eid='"+eId+"' ");
        if (!Check.Null(billNo))
        {
            sqlBuffer.append(" and a.billno='"+billNo+"' ");
        }
        if (!Check.Null(ketTxt))
        {
            sqlBuffer.append(" and (a.billno like '%%"+ketTxt+"%%' or a.title like '%%"+ketTxt+"%%') ");
        }
        if (!Check.Null(status))
        {
            sqlBuffer.append(" and a.status='"+status+"' ");
        }
        if (!Check.Null(execStatus))
        {
            if ("1".equals(execStatus))
            {
                //1-未开始：STATUS=-1 或 BEGINDATE>系统日期
                sqlBuffer.append(" and (a.status='-1' or trunc(a.begindate)>trunc(sysdate))");
            }
            else if ("2".equals(execStatus))
            {
                //2-活动中：STATUS=100 且 BEGINDATE<=系统日期 且 ENDDATE>=系统日期
                sqlBuffer.append(" and a.status='100' and trunc(a.begindate)<=trunc(sysdate) and trunc(a.enddate)<=trunc(sysdate) ");
            }
            else if ("3".equals(execStatus))
            {
                //3-已结束：STATUS=0 或 STATUS=100 且 ENDDATE<系统日期
                sqlBuffer.append(" and (a.status='0' or (a.status='100' and trunc(a.enddate)<trunc(sysdate) ) )");
            }
            else
            {
            
            }
        }
        sqlBuffer.append(" order by billno ");
        sqlBuffer.append(" ) where rn>"+startRow+" and rn<="+endRow);
        sqlBuffer.append(" )");
        sqlBuffer.append(" select p.*,B.PLUNO,B.SORTID,BL.PLU_NAME,PL.OP_NAME from p ");
        sqlBuffer.append(" left join dcp_holidaygoods_detail b on b.eid=p.eid and b.billno=p.billno ");
        sqlBuffer.append(" left join dcp_goods_lang bl on b.eid=bl.eid and b.pluno=bl.pluno and bl.lang_type='"+langType+"' ");
        sqlBuffer.append(" left join platform_staffs_lang pl on pl.eid=p.eid and pl.opno=p.employeeid and pl.lang_type='"+langType+"' ");
        sqlBuffer.append(" order by rn, SORTID ");
        String sql = sqlBuffer.toString();
        return sql;
    }
}
