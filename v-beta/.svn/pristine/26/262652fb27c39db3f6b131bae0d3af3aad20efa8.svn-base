package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsBatchMappingProcessReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsBatchMappingProcessReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_AbnormalGoodsBatchMappingProcessRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.redis.RedisUtil;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_AbnormalGoodsBatchMappingProcess extends SPosAdvanceService<DCP_AbnormalGoodsBatchMappingProcessReq, DCP_AbnormalGoodsBatchMappingProcessRes> {
    @Override
    protected void processDUID(DCP_AbnormalGoodsBatchMappingProcessReq req, DCP_AbnormalGoodsBatchMappingProcessRes res) throws Exception {

        String eId = req.geteId();//不取传入的吧，万一传错了呢
        String goodName = req.getRequest().getGoodName();
        String barcode = req.getRequest().getBarcode();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = "select * from DCP_ABNORMALGOOD_MAPPING where EID='"+eId+"' and GOODNAME='"+goodName+"' "
                  + " and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商品映射数据已存在！");
        }



        String[] columns1 = {
                "EID", "GOODNAME","GOODBARCODE","LOADDOCTYPE","CHANNELID", "APPNAME", "CHANNELIDNAME", "MAP_DATE"
        };
        DataValue[] insValue1 = null;
        insValue1 = new DataValue[]{
                new DataValue(eId, Types.VARCHAR),
                new DataValue(goodName, Types.VARCHAR),
                new DataValue(barcode, Types.VARCHAR),
                new DataValue(loadDocType, Types.VARCHAR),
                new DataValue(channelId, Types.VARCHAR),
                new DataValue(req.getRequest().getAppName(), Types.VARCHAR),
                new DataValue(req.getRequest().getChannelIdName(), Types.VARCHAR),
                new DataValue(lastmoditime, Types.DATE)

        };

        InsBean ib1 = new InsBean("DCP_ABNORMALGOOD_MAPPING", columns1);
        ib1.addValues(insValue1);
        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        //更新下订转销缓存的时间
        try
        {

            RedisPosPub redis = new RedisPosPub();
            String redisKey = orderRedisKeyInfo.redis_AbnormalOrderProcessToSaleTime;
            String orderToSaleStartTime = req.getRequest().getOrderToSaleStartTime();
            if (orderToSaleStartTime==null||orderToSaleStartTime.trim().isEmpty())
            {
                orderToSaleStartTime = "";
            }
            String orderToSaleEndTime = req.getRequest().getOrderToSaleEndTime();
            if (orderToSaleEndTime==null||orderToSaleEndTime.trim().isEmpty())
            {
                orderToSaleEndTime = "";
            }
            JSONObject obj = new JSONObject();
            obj.put("orderToSaleStartTime",orderToSaleStartTime);
            obj.put("orderToSaleEndTime",orderToSaleEndTime);
            String redisValue = obj.toString();
            redis.DeleteKey(redisKey);
            boolean nret = redis.setString(redisKey,redisValue);
            if (nret)
            {
                HelpTools.writelog_fileName("商品异常订单批量处理时，设置订转销日期,写Reids成功，rediskey="+redisKey+",redisValue="+redisValue, "AbnormalOrderBatchProcess");
            }
            else
            {
                HelpTools.writelog_fileName("商品异常订单批量处理时，设置订转销日期,写Redis失败，rediskey="+redisKey+",redisValue="+redisValue, "AbnormalOrderBatchProcess");
            }

        }
        catch (Exception e)
        {

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AbnormalGoodsBatchMappingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AbnormalGoodsBatchMappingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AbnormalGoodsBatchMappingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AbnormalGoodsBatchMappingProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelRequest request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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

        if (!Check.Null(req.getRequest().getOrderToSaleStartTime()))
        {
            try
            {
                Date date = new SimpleDateFormat("yyyyMMdd").parse(req.getRequest().getOrderToSaleStartTime());

            }
            catch (Exception e)
            {
                errMsg.append("订转销售开始日期格式不正确(正确格式:yyyyMMdd),");
                isFail = true;
            }
        }
        if (!Check.Null(req.getRequest().getOrderToSaleEndTime()))
        {
            try
            {
                Date date = new SimpleDateFormat("yyyyMMdd").parse(req.getRequest().getOrderToSaleEndTime());

            }
            catch (Exception e)
            {
                errMsg.append("订转销结束日期格式不正确(正确格式:yyyyMMdd),");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AbnormalGoodsBatchMappingProcessReq> getRequestType() {
        return new TypeToken<DCP_AbnormalGoodsBatchMappingProcessReq>(){};
    }

    @Override
    protected DCP_AbnormalGoodsBatchMappingProcessRes getResponseType() {
        return new DCP_AbnormalGoodsBatchMappingProcessRes();
    }
}
