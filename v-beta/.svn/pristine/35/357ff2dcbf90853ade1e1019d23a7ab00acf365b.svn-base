package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateGoodsUpdateRes;
import com.dsc.spos.model.Template_POS_GoodsChannelPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_SalePriceTemplateGoodsUpdate extends SPosAdvanceService<DCP_SalePriceTemplateGoodsUpdateReq, DCP_SalePriceTemplateGoodsUpdateRes> {

    @Override
    protected void processDUID(DCP_SalePriceTemplateGoodsUpdateReq req, DCP_SalePriceTemplateGoodsUpdateRes res) throws Exception {

        //同步缓存
        List<Template_POS_GoodsChannelPriceRedisUpdate> templateList = new ArrayList<>();
        //
        Template_POS_GoodsChannelPriceRedisUpdate pos_goodsChannelPriceRedisUpdate = new Template_POS_GoodsChannelPriceRedisUpdate();
        pos_goodsChannelPriceRedisUpdate.setTemplateId(req.getRequest().getTemplateId());
        pos_goodsChannelPriceRedisUpdate.setPluList(new ArrayList<>());
        templateList.add(pos_goodsChannelPriceRedisUpdate);

        String eId = req.geteId();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String templateId = req.getRequest().getTemplateId();
        List<DCP_SalePriceTemplateGoodsUpdateReq.PluList> pluList = req.getRequest().getPluList();

        String sql = "select templateid from dcp_salepricetemplate_price where eid='" + eId + "' and templateid='" + templateId + "' ";
        List<Map<String, Object>> getData = this.doQueryData(sql, null);
        if (getData == null || getData.isEmpty()) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("此模板编码不存在！");
            return;
        }


        if (pluList.size() == 0) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("商品信息列表必须有值！");
            return;
        }

        for (DCP_SalePriceTemplateGoodsUpdateReq.PluList template : pluList) {

            //同步缓存
            Template_POS_GoodsChannelPriceRedisUpdate.plu plu = pos_goodsChannelPriceRedisUpdate.new plu();
            plu.setItem(template.getItem());
            plu.setPluNo(template.getPluNo());

            UptBean ub1 = new UptBean("dcp_salepricetemplate_price");
//            ub1.addUpdateValue("UNIT", new DataValue(template.getUnit(),Types.VARCHAR));
            ub1.addUpdateValue("FEATURENO", new DataValue(template.getFeatureNo(), Types.VARCHAR));
            ub1.addUpdateValue("PRICE", new DataValue(template.getPrice(), Types.DECIMAL));
            ub1.addUpdateValue("MINPRICE", new DataValue(template.getMinPrice(), Types.DECIMAL));
            ub1.addUpdateValue("ISDISCOUNT", new DataValue(template.getIsDiscount(), Types.VARCHAR));
            ub1.addUpdateValue("ISPROM", new DataValue(template.getIsProm(), Types.VARCHAR));
            ub1.addUpdateValue("BEGINDATE", new DataValue(template.getBeginDate(), Types.DATE));
            ub1.addUpdateValue("ENDDATE", new DataValue(template.getEndDate(), Types.DATE));
            ub1.addUpdateValue("STATUS", new DataValue(template.getStatus(), Types.INTEGER));
            ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

            ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            ub1.addCondition("ITEM", new DataValue(template.getItem(), Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(template.getPluNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }

        this.doExecuteDataToDB();


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


        //同步缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
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


        PosPub.POS_GoodsChannelPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey, templateList);


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateGoodsUpdateReq req) throws Exception {
        boolean isFail = false;


        return isFail;
    }

    @Override
    protected TypeToken<DCP_SalePriceTemplateGoodsUpdateReq> getRequestType() {
        return new TypeToken<DCP_SalePriceTemplateGoodsUpdateReq>() {
        };
    }

    @Override
    protected DCP_SalePriceTemplateGoodsUpdateRes getResponseType() {
        return new DCP_SalePriceTemplateGoodsUpdateRes();
    }


}
