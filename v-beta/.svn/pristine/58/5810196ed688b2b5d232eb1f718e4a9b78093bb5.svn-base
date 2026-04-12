package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DataRatioUpdateByShopReq;
import com.dsc.spos.json.cust.res.DCP_DataRatioUpdateByShopRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_DataRatioUpdateByShop extends SPosAdvanceService<DCP_DataRatioUpdateByShopReq, DCP_DataRatioUpdateByShopRes> {
    @Override
    protected void processDUID(DCP_DataRatioUpdateByShopReq req, DCP_DataRatioUpdateByShopRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String shopId = req.getRequest().getShopId();
        String d_year = req.getRequest().getD_year();
        List<DCP_DataRatioUpdateByShopReq.LevelDataRatio> dataRatioList = req.getRequest().getDataRatioList();
        DelBean db1 = new DelBean("DCP_DATARATIO");
        db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        db1.addCondition("D_YEAR", new DataValue(d_year, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        String[] columnsName = {"EID","SHOPID","D_YEAR","D_MONTH","DATARATIO","CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

        for (DCP_DataRatioUpdateByShopReq.LevelDataRatio par : dataRatioList)
        {

            DataValue[] columnsVal = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(d_year, Types.VARCHAR),
                    new DataValue(par.getD_month(), Types.VARCHAR),
                    new DataValue(par.getDataRatio(), Types.VARCHAR),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_DATARATIO", columnsName);
            ib1.addValues(columnsVal);
            this.addProcessData(new DataProcessBean(ib1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DataRatioUpdateByShopReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DataRatioUpdateByShopReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DataRatioUpdateByShopReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DataRatioUpdateByShopReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getShopId()))
        {
            errCt++;
            errMsg.append("shopId门店编码不能为空值，");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getD_year()))
        {
            errCt++;
            errMsg.append("d_year年份不能为空值，");
            isFail = true;
        }
        List<DCP_DataRatioUpdateByShopReq.LevelDataRatio> dataRatioList = req.getRequest().getDataRatioList();
        if (dataRatioList==null||dataRatioList.isEmpty())
        {
            errCt++;
            errMsg.append("dataRatioList数据比例列表不能为空，");
            isFail = true;
        }
        else
        {
            for (DCP_DataRatioUpdateByShopReq.LevelDataRatio par : dataRatioList)
            {
                if (Check.Null(par.getD_month()))
                {
                    errCt++;
                    errMsg.append("d_month月份不能为空值，");
                    isFail = true;
                }
                if (Check.Null(par.getDataRatio()))
                {
                    errCt++;
                    errMsg.append("dataRatio比例不能为空值，");
                    isFail = true;
                }
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DataRatioUpdateByShopReq> getRequestType() {
        return new TypeToken<DCP_DataRatioUpdateByShopReq>(){};
    }

    @Override
    protected DCP_DataRatioUpdateByShopRes getResponseType() {
        return new DCP_DataRatioUpdateByShopRes();
    }
}
