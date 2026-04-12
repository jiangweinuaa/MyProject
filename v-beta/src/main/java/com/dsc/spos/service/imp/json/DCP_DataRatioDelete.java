package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DataRatioDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DataRatioDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_DataRatioDelete extends SPosAdvanceService<DCP_DataRatioDeleteReq, DCP_DataRatioDeleteRes> {
    @Override
    protected void processDUID(DCP_DataRatioDeleteReq req, DCP_DataRatioDeleteRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String d_year = req.getRequest().getD_year();
        List<DataProcessBean> DPBList = new ArrayList<>();//先删后插，插入的列表放在删除列表之后
        for (DCP_DataRatioDeleteReq.LevelDataShop par : req.getRequest().getShopList()) {
            String shopId = par.getShopId();
            DelBean db1 = new DelBean("DCP_DATARATIO");
            db1.addCondition("D_YEAR", new DataValue(d_year, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }


        this.doExecuteDataToDB();
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DataRatioDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DataRatioDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DataRatioDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DataRatioDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getD_year()))
        {
            errCt++;
            errMsg.append("d_year年份不能为空值，");
            isFail = true;
        }


        if (req.getRequest().getShopList()==null||req.getRequest().getShopList().isEmpty())
        {
            errCt++;
            errMsg.append("shopList门店列表不能为空");
            isFail = true;
        }
        else
        {
            for (DCP_DataRatioDeleteReq.LevelDataShop par : req.getRequest().getShopList())
            {
                if (Check.Null(par.getShopId()))
                {
                    errCt++;
                    errMsg.append("shopId门店编码不能为空值，");
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
    protected TypeToken<DCP_DataRatioDeleteReq> getRequestType() {
        return new TypeToken<DCP_DataRatioDeleteReq>(){};
    }

    @Override
    protected DCP_DataRatioDeleteRes getResponseType() {
        return new DCP_DataRatioDeleteRes();
    }
}
