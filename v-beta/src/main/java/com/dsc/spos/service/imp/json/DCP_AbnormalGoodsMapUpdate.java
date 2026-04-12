package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapUpdateReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapUpdateReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_AbnormalGoodsMapUpdateRes;
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

public class DCP_AbnormalGoodsMapUpdate extends SPosAdvanceService<DCP_AbnormalGoodsMapUpdateReq, DCP_AbnormalGoodsMapUpdateRes> {
    @Override
    protected void processDUID(DCP_AbnormalGoodsMapUpdateReq req, DCP_AbnormalGoodsMapUpdateRes res) throws Exception {

        String eId = req.geteId();//不取传入的吧，万一传错了呢
        String goodName = req.getRequest().getGoodName();
        String barcode = req.getRequest().getBarcode();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = "select * from DCP_ABNORMALGOOD_MAPPING where EID='"+eId+"' and GOODNAME='"+goodName+"' "
                  + " and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData==null||getQData.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商品映射数据不存在！");
        }

        UptBean up1 = new UptBean("DCP_ABNORMALGOOD_MAPPING");
        up1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
        up1.addCondition("GOODNAME",new DataValue(goodName,Types.VARCHAR));
        up1.addCondition("LOADDOCTYPE",new DataValue(loadDocType,Types.VARCHAR));
        up1.addCondition("CHANNELID",new DataValue(channelId,Types.VARCHAR));

        up1.addUpdateValue("GOODBARCODE",new DataValue(barcode,Types.VARCHAR));
        up1.addUpdateValue("MAP_DATE",new DataValue(lastmoditime,Types.DATE));
        this.addProcessData(new DataProcessBean(up1)); // 新增單頭

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AbnormalGoodsMapUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AbnormalGoodsMapUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AbnormalGoodsMapUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AbnormalGoodsMapUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelRequest request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId())) {
            errMsg.append("企业ID不能为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getBarcode())) {
            errMsg.append("映射商品条码为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getGoodName())) {
            errMsg.append("商品名称不能为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getLoadDocType())) {
            errMsg.append("渠道类型不能为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getChannelId())) {
            errMsg.append("渠道编码不能为空值,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AbnormalGoodsMapUpdateReq> getRequestType() {
        return new TypeToken<DCP_AbnormalGoodsMapUpdateReq>(){};
    }

    @Override
    protected DCP_AbnormalGoodsMapUpdateRes getResponseType() {
        return new DCP_AbnormalGoodsMapUpdateRes();
    }
}
