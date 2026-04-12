package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayGoodsDeleteReq;
import com.dsc.spos.json.cust.res.DCP_HolidayGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public  class DCP_HolidayGoodsDelete extends SPosAdvanceService<DCP_HolidayGoodsDeleteReq, DCP_HolidayGoodsDeleteRes> {
    @Override
    protected void processDUID(DCP_HolidayGoodsDeleteReq req, DCP_HolidayGoodsDeleteRes res) throws Exception {

        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        StringBuffer goodsSyncBuffer = new StringBuffer();

        if (!CheckIsExist(eId,billNo,goodsSyncBuffer))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")不存在,无法删除！");
        }
        String GOODSSYNC = goodsSyncBuffer.toString();

        if (GOODSSYNC.equals("Y"))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")商品同步状态("+GOODSSYNC+")等于Y,不可删除！");
        }

        DelBean db1 = new DelBean("DCP_HOLIDAYGOODS");
        db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_HOLIDAYGOODS_DETAIL");
        db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HolidayGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HolidayGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HolidayGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HolidayGoodsDeleteReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改


        if(Check.Null(billNo))
        {
            errMsg.append("活动编号billNo不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        return isFail;

    }

    @Override
    protected TypeToken<DCP_HolidayGoodsDeleteReq> getRequestType() {
        return new TypeToken<DCP_HolidayGoodsDeleteReq>(){};
    }

    @Override
    protected DCP_HolidayGoodsDeleteRes getResponseType() {
        return new DCP_HolidayGoodsDeleteRes();
    }

    private boolean CheckIsExist (String eId,String billNo,StringBuffer goodsSync) throws  Exception
    {
        String sql = "select * from dcp_holidaygoods where EID='"+eId+"' and BILLNO='"+billNo+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
            goodsSync.append(getQData.get(0).getOrDefault("GOODSSYNC","").toString());
            return  true;
        }
        return  false;
    }

    private boolean CheckDateFormat(String dateStr) throws  Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date d1 = simpleDateFormat.parse(dateStr);
            if (d1!=null)
            {
                return true;
            }
        }
        catch (Exception e)
        {

        }
        return  false;
    }
}
