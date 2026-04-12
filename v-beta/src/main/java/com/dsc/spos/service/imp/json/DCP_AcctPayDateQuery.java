package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_AcctPayDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_AcctPayDateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_AcctPayDateQuery extends SPosBasicService<DCP_AcctPayDateQueryReq, DCP_AcctPayDateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AcctPayDateQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AcctPayDateQueryReq> getRequestType() {
        return new TypeToken<DCP_AcctPayDateQueryReq>() {
        };
    }

    @Override
    protected DCP_AcctPayDateQueryRes getResponseType() {
        return new DCP_AcctPayDateQueryRes();
    }

    @Override
    protected DCP_AcctPayDateQueryRes processJson(DCP_AcctPayDateQueryReq req) throws Exception {
        DCP_AcctPayDateQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);

        if (getData != null && !getData.isEmpty()) {

            for (Map<String, Object> data : getData) {
                DCP_AcctPayDateQueryRes.Datas oneData = res.new Datas();

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setPaydateType(data.get("PAYDATE_TYPE").toString());
                oneData.setPaydateNo(data.get("PAYDATENO").toString());
                oneData.setPaydateBase(data.get("PAYDATEBASE").toString());
                oneData.setPSeasons(data.get("PSEASONS").toString());
                oneData.setPMonths(data.get("PMONTHS").toString());
                oneData.setPDays(data.get("PDAYS").toString());
                oneData.setDuedateBase(data.get("DUEDATEBASE").toString());
                oneData.setDSeasons(data.get("DSEASONS").toString());
                oneData.setDMonths(data.get("DMONTHS").toString());
                oneData.setDDays(data.get("DDAYS").toString());
                oneData.setCreatorID(data.get("CREATEOPID").toString());
                oneData.setCreatorName(data.get("CREATEOPNAME").toString());
                oneData.setCreatorDeptID(data.get("CREATEDEPTID").toString());
                oneData.setCreatorDeptName(data.get("CREATEDEPTNAME").toString());
                oneData.setCreate_datetime(data.get("CREATETIMES").toString());
                oneData.setLastmodifyID(data.get("LASTMODIOPID").toString());
                oneData.setLastmodifyName(data.get("LASTMODIOPNAME").toString());
                oneData.setLastmodify_datetime(data.get("LASTMODITIMES").toString());

                //预估收付款日期
                Date date = new Date();
                //yyyyMMdd
                if(Check.NotNull(req.getRequest().getBdate())) {
                    date = DateFormatUtils.parseDate(req.getRequest().getBdate());
                    Integer season = Integer.valueOf(oneData.getPSeasons());
                    Integer month = Integer.valueOf(oneData.getPMonths());
                    Integer day = Integer.valueOf(oneData.getPDays());
                    date = DateFormatUtils.addMonth(date, 3 * season);
                    date = DateFormatUtils.addMonth(date, month);
                    date = DateFormatUtils.addDay(date, day);
                }
                String formatDate = DateFormatUtils.format(date,"yyyyMMdd");
                oneData.setEstReceExpDay(formatDate);

                res.getDatas().add(oneData);


            }
        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AcctPayDateQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("select a.*,b.op_name as createopname,c.departname as createdeptname,d.op_name as lastmodiopname,to_char(a.CREATETIME,'yyyy-MM-dd HH:mm:ss') as CREATETIMEs,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') as LASTMODITIMEs" +
                " from DCP_PAYDATE a " +
                " left join platform_staffs_lang b on a.eid=b.eid and a.CREATEOPID=b.opno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_department_lang c on c.eid=a.eid and c.departno=a.CREATEDEPTID and c.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang d on d.eid=a.eid and d.opno=a.LASTMODIOPID " +
                " where a.eid='"+req.geteId()+"' and a.paydateno='"+req.getRequest().getPaydateNo()+"' " +
                " ");
        if(Check.NotNull(req.getRequest().getPaydateType())){
            sb.append("and a.paydatetype='"+req.getRequest().getPaydateType()+"' ");
        }
        if(Check.NotNull(req.getRequest().getStatus())){
            sb.append("and a.status='"+req.getRequest().getStatus()+"' ");
        }



        return sb.toString();
    }
}
