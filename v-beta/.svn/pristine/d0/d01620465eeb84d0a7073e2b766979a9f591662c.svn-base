package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_MinQtyTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_N_MinQtyTemplateCreateReq.*;
import com.dsc.spos.json.cust.res.DCP_N_MinQtyTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_N_MinQtyTemplateCreate
 * 服务说明：N-起售量模板新增
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_MinQtyTemplateCreate extends SPosAdvanceService<DCP_N_MinQtyTemplateCreateReq, DCP_N_MinQtyTemplateCreateRes> {
    @Override
    protected void processDUID(DCP_N_MinQtyTemplateCreateReq req, DCP_N_MinQtyTemplateCreateRes res) throws Exception {

        try {

            /*
             * 模板编号在后台按规格生成QSL+日期时分秒（例:QSL20201113143806）
             *
             */
            String templateId = "QSL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String templateName = req.getRequest().getTemplateName();
            String memo = req.getRequest().getMemo();
            String status = req.getRequest().getStatus();

            String eId = req.geteId();
            String opNO = req.getOpNO();
            String opName = req.getOpName();
            // 获取当前时间
            String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String updateDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());


            String sql = "select * from DCP_MINQTYTEMPLATE where TEMPLATEID = '"+templateId+"' ";

            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已存在，请重新输入");
            }


                // 生成模板编码
                String[] columnsDcpMinQtyTemplateDetail =
                        { "EID", "TEMPLATEID", "PLUNO", "MOQ","MAXQTY", "LASTMODIOPID","LASTMODIOPNAME", "UPDATE_TIME"};

                DataValue[] insValue = null;
                // DCP_MINQTYTEMPLATE_DETAIL 单身 (多条) 保存资料
                List<Plu> pluList = req.getRequest().getPluList();
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

                // DCP_MINQTYTEMPLATE 单头 保存资料
                List<Shop> shopList = req.getRequest().getShopList();
                String restrictShop = req.getRequest().getRestrictShop(); // 适用门店：0-所有门店1-指定门店2-排除门店
                String restrictChannel = req.getRequest().getRestrictChannel(); //适用渠道：0-所有渠道1-指定渠道2-排除渠道
                String restrictPeriod = req.getRequest().getRestrictPeriod(); //适用时段：0-所有时段1-指定时段

                String[] columnsDcpMinQtyTemplate = { "EID", "TEMPLATEID", "MEMO", "STATUS", "CREATEOPID",
                        "CREATEOPNAME", "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME",
                        "RESTRICTSHOP","RESTRICTCHANNEL","RESTRICTPERIOD" };
                insValue = null;
                insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateId, Types.VARCHAR), new DataValue(memo, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR), new DataValue(opNO, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR), new DataValue(createDate, Types.DATE),
                        new DataValue(opNO, Types.VARCHAR), new DataValue(opName, Types.VARCHAR),
                        new DataValue(createDate, Types.DATE), new DataValue(restrictShop, Types.INTEGER),
                        new DataValue(restrictChannel, Types.INTEGER),
                        new DataValue(restrictPeriod, Types.INTEGER)
                };
                InsBean ib2 = new InsBean("DCP_MINQTYTEMPLATE", columnsDcpMinQtyTemplate);
                ib2.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib2));

                // 如果指定门店适用 则添加 适用门店相关的资料
                if (restrictShop.equals("1")) {
                    // 模板适用门店 DCP_MINQTYTEMPLATE_RANGE 保存资料
                    String[] columnsDcpMinQtyTemplateRange = { "EID", "TEMPLATEID", "TEMPLATENAME","RANGETYPE","ID", "NAME", "LASTMODITIME" };
                    insValue = null;
                    for (Shop level2Elm : shopList) {
                        insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                                new DataValue(templateId, Types.VARCHAR),
                                new DataValue(templateName, Types.VARCHAR),
                                new DataValue(restrictShop, Types.INTEGER),
                                new DataValue(level2Elm.getId(), Types.VARCHAR),
                                new DataValue(level2Elm.getName(), Types.VARCHAR), new DataValue(createDate, Types.DATE) };
                        InsBean ib3 = new InsBean("DCP_MINQTYTEMPLATE_RANGE", columnsDcpMinQtyTemplateRange);
                        ib3.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib3));
                    }

                }

                // 增加 DCP_MINQTYTEMPLATE_LANG 多语言表资料
                String[] columnsDcpMinQtyTemplateLang = { "EID", "TEMPLATEID", "LANG_TYPE", "TEMPLATENAME",
                        "UPDATE_TIME" };
                insValue = null;
                insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateId, Types.VARCHAR), new DataValue(req.getLangType(), Types.VARCHAR),
                        new DataValue(templateName, Types.VARCHAR), new DataValue(updateDate, Types.VARCHAR) };
                InsBean ib4 = new InsBean("DCP_MINQTYTEMPLATE_LANG", columnsDcpMinQtyTemplateLang);
                ib4.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib4));



                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");


        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }




    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_MinQtyTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_MinQtyTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_MinQtyTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_MinQtyTemplateCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest()==null){
            errMsg.append("request 不可为空值, ");
            isFail = true;
        }else {
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

            String restrictShop = req.getRequest().getRestrictShop(); // 适用门店：0-所有门店1-指定门店2-排除门店

            List<Plu> pluList = req.getRequest().getPluList();
            List<Shop> shopList = req.getRequest().getShopList();

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

        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_MinQtyTemplateCreateReq> getRequestType() {
        return new TypeToken<DCP_N_MinQtyTemplateCreateReq>(){};
    }

    @Override
    protected DCP_N_MinQtyTemplateCreateRes getResponseType() {
        return new DCP_N_MinQtyTemplateCreateRes();
    }

}
