package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateGoodsDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateGoodsDeleteRes;
import com.dsc.spos.model.Template_POS_GoodsChannelPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SalePriceTemplateGoodsDelete extends SPosAdvanceService<DCP_SalePriceTemplateGoodsDeleteReq, DCP_SalePriceTemplateGoodsDeleteRes> {

    @Override
    protected void processDUID(DCP_SalePriceTemplateGoodsDeleteReq req, DCP_SalePriceTemplateGoodsDeleteRes res) throws Exception {


        //同步缓存
        List<Template_POS_GoodsChannelPriceRedisUpdate> templateList = new ArrayList<>();
        //
        Template_POS_GoodsChannelPriceRedisUpdate pos_goodsChannelPriceRedisUpdate = new Template_POS_GoodsChannelPriceRedisUpdate();
        pos_goodsChannelPriceRedisUpdate.setTemplateId(req.getRequest().getTemplateId());
        pos_goodsChannelPriceRedisUpdate.setPluList(new ArrayList<>());
        templateList.add(pos_goodsChannelPriceRedisUpdate);

        StringBuilder mWhere = new StringBuilder();
        mWhere.append(" WHERE 1=1 AND EID='").append(req.geteId()).append("' AND TEMPLATEID='").append(req.getRequest().getTemplateId()).append("'");
        if (!"Y".equals(req.getRequest().getIsAllGoods())) {
            mWhere.append(" AND ITEM IN (");
            for (DCP_SalePriceTemplateGoodsDeleteReq.PluList templatePrice : req.getRequest().getPluList()) {
                mWhere.append(templatePrice.getItem()).append(",");
            }
            mWhere.deleteCharAt(mWhere.length() - 1);
            mWhere.append(")");
        }
        String sql = " SELECT * FROM dcp_salePriceTemplate_price " + mWhere;
        List<Map<String, Object>> exist = this.doQueryData(sql, null);
        if (null != exist && !exist.isEmpty()) {
            StringBuilder item = new StringBuilder();
            for (Map<String, Object> one : exist) {
                if (item.length() == 0) {
                    item.append(one.get("ITEM"));
                } else {
                    item.append(";").append(one.get("ITEM"));
                }
            }
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "序号" + item + "价格生效日<=系统日期，不可删除");
        }


        String eId = req.geteId();
        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey, eId);

        String templateId = req.getRequest().getTemplateId();
        String isAllgoods = req.getRequest().getIsAllGoods();

        if (!isAllgoods.equals("Y")) {
            List<DCP_SalePriceTemplateGoodsDeleteReq.PluList> pluList = req.getRequest().getPluList();

            for (DCP_SalePriceTemplateGoodsDeleteReq.PluList templatePrice : pluList) {

                //同步缓存
                Template_POS_GoodsChannelPriceRedisUpdate.plu plu = pos_goodsChannelPriceRedisUpdate.new plu();
                plu.setItem(templatePrice.getItem());

                DelBean db1 = new DelBean("DCP_SALEPRICETEMPLATE_PRICE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
                db1.addCondition("ITEM", new DataValue(templatePrice.getItem(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
            }

        } else {
            DelBean db1 = new DelBean("DCP_SALEPRICETEMPLATE_PRICE");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


        PosPub.POS_GoodsChannelPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey, templateList);


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateGoodsDeleteReq req) throws Exception {
        boolean isFail = false;

        return isFail;
    }

    @Override
    protected TypeToken<DCP_SalePriceTemplateGoodsDeleteReq> getRequestType() {
        return new TypeToken<DCP_SalePriceTemplateGoodsDeleteReq>() {
        };
    }

    @Override
    protected DCP_SalePriceTemplateGoodsDeleteRes getResponseType() {
        return new DCP_SalePriceTemplateGoodsDeleteRes();
    }

}
