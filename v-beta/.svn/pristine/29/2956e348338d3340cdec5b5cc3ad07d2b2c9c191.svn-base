package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapDeleteReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapDeleteReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_AbnormalGoodsMapDeleteRes;
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

public class DCP_AbnormalGoodsMapDelete extends SPosAdvanceService<DCP_AbnormalGoodsMapDeleteReq, DCP_AbnormalGoodsMapDeleteRes> {
    @Override
    protected void processDUID(DCP_AbnormalGoodsMapDeleteReq req, DCP_AbnormalGoodsMapDeleteRes res) throws Exception {

        String eId = req.geteId();//不取传入的吧，万一传错了呢
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

       for (DCP_AbnormalGoodsMapDeleteReq.level1Elm par : req.getRequest().getDatas())
       {
           DelBean db1 = new DelBean("DCP_ABNORMALGOOD_MAPPING");
           db1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
           db1.addCondition("GOODNAME",new DataValue(par.getGoodName(),Types.VARCHAR));
           db1.addCondition("LOADDOCTYPE",new DataValue(par.getLoadDocType(),Types.VARCHAR));
           db1.addCondition("CHANNELID",new DataValue(par.getChannelId(),Types.VARCHAR));

           this.addProcessData(new DataProcessBean(db1)); // 新增單頭
       }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AbnormalGoodsMapDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AbnormalGoodsMapDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AbnormalGoodsMapDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AbnormalGoodsMapDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelRequest request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(request.getDatas()==null||request.getDatas().isEmpty())
        {
            errMsg.append("删除列表不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_AbnormalGoodsMapDeleteReq.level1Elm par : request.getDatas())
        {
            if (Check.Null(par.geteId())) {
                errMsg.append("企业ID不能为空值,");
                isFail = true;
            }
            if (Check.Null(par.getGoodName())) {
                errMsg.append("商品名称不能为空值,");
                isFail = true;
            }
            if (Check.Null(par.getLoadDocType())) {
                errMsg.append("渠道类型不能为空值,");
                isFail = true;
            }
            if (Check.Null(par.getChannelId())) {
                errMsg.append("渠道编码不能为空值,");
                isFail = true;
            }
        }



        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AbnormalGoodsMapDeleteReq> getRequestType() {
        return new TypeToken<DCP_AbnormalGoodsMapDeleteReq>(){};
    }

    @Override
    protected DCP_AbnormalGoodsMapDeleteRes getResponseType() {
        return new DCP_AbnormalGoodsMapDeleteRes();
    }
}
