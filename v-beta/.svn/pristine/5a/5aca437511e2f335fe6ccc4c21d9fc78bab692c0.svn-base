package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_MinQtyTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_N_MinQtyTemplateUpdateReq.*;
import com.dsc.spos.json.cust.res.DCP_N_MinQtyTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * 服务函数：DCP_N_MinQtyTemplateUpdate
 * 服务说明：N-起售量模板修改
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_MinQtyTemplateUpdate extends SPosAdvanceService<DCP_N_MinQtyTemplateUpdateReq, DCP_N_MinQtyTemplateUpdateRes> {
    @Override
    protected void processDUID(DCP_N_MinQtyTemplateUpdateReq req, DCP_N_MinQtyTemplateUpdateRes res) throws Exception {

        try {

            String templateId = req.getRequest().getTemplateId();
            String templateName = req.getRequest().getTemplateName();
            String memo = req.getRequest().getMemo();
            String status = req.getRequest().getStatus();
            String restrictShop = req.getRequest().getRestrictShop();
            List<Shop> shopList = req.getRequest().getShopList();
            List<Plu> pluList = req.getRequest().getPluList();

            String eId = req.geteId();
            String opNO = req.getOpNO();
            String opName = req.getOpName();

            // 获取当前时间
            String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String updateDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());


            String sql = " SELECT * FROM DCP_MINQTYTEMPLATE WHERE EID = '"+eId+"'  AND TEMPLATEID = '"+templateId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在，请重新输入");
            }


            DataValue[] insValue = null;
            // 删除适用门店
            DelBean db = new DelBean("DCP_MINQTYTEMPLATE_RANGE");
            db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db));

            if (restrictShop.equals("1"))
            {
                // 模板适用门店 DCP_MINQTYTEMPLATE_RANGE 保存资料
                for (Shop level2Elm : shopList) {
                    String[] columnsDcpMinQtyTemplateRange = { "EID", "TEMPLATEID", "TEMPLATENAME","RANGETYPE","ID", "NAME", "LASTMODITIME" };
                    insValue = null;
                    insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                            new DataValue(templateId, Types.VARCHAR),
                            new DataValue(templateName, Types.VARCHAR),
                            new DataValue(2, Types.INTEGER),
                            new DataValue(level2Elm.getId(), Types.VARCHAR),
                            new DataValue(level2Elm.getName(), Types.VARCHAR), new DataValue(createDate, Types.DATE) };
                    InsBean ib3 = new InsBean("DCP_MINQTYTEMPLATE_RANGE", columnsDcpMinQtyTemplateRange);
                    ib3.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib3));
                }
            }

            // 删除单据单身
            DelBean db1 = new DelBean("DCP_MINQTYTEMPLATE_DETAIL");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            // DCP_MINQTYTEMPLATE_DETAIL 单身 (多条) 保存资料
            String[] columnsDcpMinQtyTemplateDetail = { "EID", "TEMPLATEID", "PLUNO", "MOQ", "MAXQTY","LASTMODIOPID",
                    "LASTMODIOPNAME", "UPDATE_TIME" };
            for (Plu level3Elm : pluList) {
                insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateId, Types.VARCHAR),
                        new DataValue(level3Elm.getPluNo(), Types.VARCHAR),
                        new DataValue(level3Elm.getMinQty(), Types.VARCHAR),
                        new DataValue(level3Elm.getMaxQty(), Types.VARCHAR),
                        new DataValue(opNO, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(updateDate, Types.VARCHAR) };
                InsBean ib1 = new InsBean("DCP_MINQTYTEMPLATE_DETAIL", columnsDcpMinQtyTemplateDetail);
                ib1.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib1));
            }

            // 修改 DCP_MINQTYTEMPLATE_LANG 模板多语言信息
            UptBean ub1 = new UptBean("DCP_MINQTYTEMPLATE_LANG");
            ub1.addUpdateValue("LANG_TYPE", new DataValue(req.getLangType(), Types.VARCHAR));
            ub1.addUpdateValue("TEMPLATENAME", new DataValue(templateName, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateDate, Types.VARCHAR));
            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            // 修改单头 DCP_MINQTYTEMPLATE
            UptBean ub2 = new UptBean("DCP_MINQTYTEMPLATE");
            ub2.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
            ub2.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
            ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
            ub2.addUpdateValue("LASTMODITIME", new DataValue(createDate, Types.DATE));
            ub2.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.INTEGER));
            // condition
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));





            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }




    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_MinQtyTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_MinQtyTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_MinQtyTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_MinQtyTemplateUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        String templateId = req.getRequest().getTemplateId();
        String templateName = req.getRequest().getTemplateName();
        String status = req.getRequest().getStatus();

        if (!PosPub.isNumeric(req.getRequest().getRestrictShop())) {
            errMsg.append("restrictShop 不可为空值, ");
            isFail = true;
        }
        if (!PosPub.isNumeric(req.getRequest().getRestrictChannel())) {
            errMsg.append("restrictChannel 不可为空值, ");
            isFail = true;
        }
        if (!PosPub.isNumeric(req.getRequest().getRestrictPeriod())) {
            errMsg.append("restrictPeriod 不可为空值, ");
            isFail = true;
        }

        String restrictShop = req.getRequest().getRestrictShop();

        List<Plu> pluList = req.getRequest().getPluList();
        List<Shop> shopList = req.getRequest().getShopList();

        if (Check.Null(templateId)) {
            errMsg.append("模板编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(templateName)) {
            errMsg.append("模板名称不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(restrictShop)) {
            errMsg.append("适用门店不可为空值, ");
            isFail = true;
        }

        if (pluList != null && !pluList.isEmpty()) {
            for (Plu level3Elm : pluList) {
                String pluNo = level3Elm.getPluNo();
                String minQty = level3Elm.getMinQty();
                String maxQty = level3Elm.getMaxQty();

                if (Check.Null(pluNo)) {
                    errMsg.append("商品编码不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(minQty)) {
                    errMsg.append("起售量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(maxQty)) {
                    errMsg.append("maxQty 不可为空值, ");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }

        if (restrictShop.equals("1")) {
            if (shopList != null && !shopList.isEmpty()) {
                for (Shop level2Elm : shopList) {
                    String id = level2Elm.getId();
                    String name = level2Elm.getName();

                    if (Check.Null(id)) {
                        errMsg.append("门店门店编号不可为空值, ");
                        isFail = true;
                    }
                    if (Check.Null(name)) {
                        errMsg.append("门店名称不可为空值, ");
                        isFail = true;
                    }
                }
            } else {
                errMsg.append("适用门店列表不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_MinQtyTemplateUpdateReq> getRequestType() {
        return new TypeToken<DCP_N_MinQtyTemplateUpdateReq>(){};
    }

    @Override
    protected DCP_N_MinQtyTemplateUpdateRes getResponseType() {
        return new DCP_N_MinQtyTemplateUpdateRes();
    }
}
