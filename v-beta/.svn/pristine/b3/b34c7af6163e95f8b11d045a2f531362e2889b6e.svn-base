package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayGoodsSyncReq;
import com.dsc.spos.json.cust.res.DCP_HolidayGoodsSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public  class DCP_HolidayGoodsSync extends SPosAdvanceService<DCP_HolidayGoodsSyncReq, DCP_HolidayGoodsSyncRes> {
    @Override
    protected void processDUID(DCP_HolidayGoodsSyncReq req, DCP_HolidayGoodsSyncRes res) throws Exception {
        
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
        String eId = req.getRequest().geteId();
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        
        String sql = " select eid,billno,billdate,goodssync,status,"
                + " to_char(begindate,'yyyyMMDD') as begindate,to_char(enddate,'yyyyMMDD') as enddate"
                + " from dcp_holidaygoods"
                + " where eid='"+eId+"' and billno='"+billNo+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData==null || getQData.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")不存在！");
        }
        
        //商品同步状态：N-未处理 Y-已打节日标记 E-已取消节日标记
        //String goodsSync = getQData.get(0).get("GOODSSYNC").toString();
        String status = getQData.get(0).get("STATUS").toString();
        String beginDate = getQData.get(0).get("BEGINDATE").toString();  //yyyy-mm-dd hh:mm:ss
        String endDate = getQData.get(0).get("ENDDATE").toString();      //yyyy-mm-dd hh:mm:ss
        
        if (Check.Null(status) || status.equals("-1")) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")状态("+status+")未启用！");
        }
        
        String oprType = ""; //1.打上节日标记；2.取消节日标记
        //打上节日标记：if staus =100 且GOODSSYNC='N‘ 且 活动起始日期<=系统日期<=活动截止日期，
        if (status.equals("100") && PosPub.compare_date(sDate,beginDate )>=0 && PosPub.compare_date(sDate,endDate)<=0) {
            oprType ="1";
        }
        //取消节日标记：if ((staus =100 且GOODSSYNC='Y‘  且 系统日期>活动截止日期 )  或 staus =0 ）且 GOODSSYNC<>'E‘
        else if(status.equals("0") || (status.equals("100") && PosPub.compare_date(sDate,endDate)>0)) {
            oprType ="2";
        }
        
        if ("1".equals(oprType)) {
            String execSql = " update dcp_goods set ISHOLIDAY='Y',HOLIDAYBEGINDATE='"+beginDate+"',HOLIDAYENDDATE='"+endDate+"',HOLIDAYBILLNO='"+billNo+"',REDISUPDATESUCCESS='N' "
                    + " where eid='"+eId+"' and pluno in (select pluno from dcp_holidaygoods_detail where  eid='"+eId+"' and billno='"+billNo+"') ";
            ExecBean execBean = new ExecBean(execSql);
            this.addProcessData(new DataProcessBean(execBean));
            
            UptBean up1 = new UptBean("DCP_HOLIDAYGOODS");
            up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            up1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
            
            up1.addUpdateValue("GOODSSYNC", new DataValue("Y",Types.VARCHAR));
            up1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N",Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPID", new DataValue("admin" ,Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPNAME", new DataValue("admin" ,Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
            this.addProcessData(new DataProcessBean(up1));
            
        } else if ("2".equals(oprType)) {
            String execSql = " update dcp_goods set ISHOLIDAY='N',HOLIDAYBEGINDATE='',HOLIDAYENDDATE='',HOLIDAYBILLNO='"+billNo+"',REDISUPDATESUCCESS='N' "
                    + " where eid='"+eId+"' and pluno in (select pluno from dcp_holidaygoods_detail where  eid='"+eId+"' and billno='"+billNo+"') ";
            ExecBean execBean = new ExecBean(execSql);
            this.addProcessData(new DataProcessBean(execBean));
            
            UptBean up1 = new UptBean("DCP_HOLIDAYGOODS");
            up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            up1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
            
            up1.addUpdateValue("GOODSSYNC", new DataValue("E",Types.VARCHAR));
            up1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N",Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPID", new DataValue("admin",Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPNAME", new DataValue("admin",Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
            this.addProcessData(new DataProcessBean(up1));
            
        } else {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")不满足条件，无须执行");
        }
        
        this.doExecuteDataToDB();
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_HolidayGoodsSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_HolidayGoodsSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_HolidayGoodsSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_HolidayGoodsSyncReq req) throws Exception {
        
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest() == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        
        
        if(Check.Null(billNo)) {
            errMsg.append("活动编号billNo不能为空值 ");
            isFail = true;
        }
        
        if(Check.Null(req.getRequest().geteId())) {
            errMsg.append("企业编码eId不能为空值 ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_HolidayGoodsSyncReq> getRequestType() {
        return new TypeToken<DCP_HolidayGoodsSyncReq>(){};
    }
    
    @Override
    protected DCP_HolidayGoodsSyncRes getResponseType() {
        return new DCP_HolidayGoodsSyncRes();
    }
    
}
