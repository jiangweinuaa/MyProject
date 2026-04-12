package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_FaPiaoTemplateUpdate
 * 服务说明：发票模板修改
 *
 * @author wangzyc
 * @since 2021-1-27
 */
public class DCP_FaPiaoTemplateUpdate extends SPosAdvanceService<DCP_FaPiaoTemplateUpdateReq, DCP_FaPiaoTemplateUpdateRes> {
    @Override
    protected void processDUID(DCP_FaPiaoTemplateUpdateReq req, DCP_FaPiaoTemplateUpdateRes res) throws Exception {
        DCP_FaPiaoTemplateUpdateReq.level1Elm request = req.getRequest();
        String templateType = request.getTemplateType(); // 模板类型 通用模板:1  专用模板:2
        String isDeleteShop = request.getIsDeleteShop();
        List<DCP_FaPiaoTemplateUpdateReq.level2Elm> shopList = request.getShopList();
        String eId = req.geteId();

        boolean isFail = false;

        // 判断传入的是 通用模板:1 OR 专用模板:2
        if (templateType.equals("1")) {
            // 模板类型为通用
            // 检验通用模板： 只能存在一个通用模板
            if (!isTemplateType(req)) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 同一企业只能存在一个通用模板！");
                isFail = true;
            }
        } else if (templateType.equals("2")) {
            // 检核特殊模板时门店是否重复设置：isDeleteShop=N时，一个门店只能有一个特殊模板 ，返回code=‘001’
            if (!Check.Null(isDeleteShop)) {
                if (isDeleteShop.equals("N")) {
                    String deleteShopSql = isDeleteShop(req);
                    List<Map<String, Object>> maps = this.doQueryData(deleteShopSql, null);

                    if (!CollectionUtils.isEmpty(maps)) {
                        StringBuffer sqlbuf = new StringBuffer("门店：");
                        for (Map<String, Object> map : maps) {
                            sqlbuf.append(""+map.get("ORG_NAME")+"("+map.get("SHOPID")+") ,");
                        }
                        sqlbuf.deleteCharAt(sqlbuf.length() - 1);

                        res.setSuccess(false);
                        res.setServiceStatus("001");
                        res.setServiceDescription("服务执行失败: 一个门店只能有一个特殊模板," + sqlbuf.toString() + "已有特殊模板！");
                        isFail = true;
                    }
                }
            }
        }

        if (!isFail) {

            // 如果前端传入 isDeleteShop
            if (!Check.Null(isDeleteShop)) {
                    // 检核通过 isDeleteShop = Y 删除其他模板中 适用门店=当前门店模板的数据
                if (isDeleteShop.equals("Y")) {
                    for (DCP_FaPiaoTemplateUpdateReq.level2Elm level2Elm : shopList) {
                        DelBean db1 = new DelBean("DCP_FAPIAO_TEMPLATE_SHOP");
                        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db1.addCondition("SHOPID", new DataValue(level2Elm.getShopId(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));
                    }
                }
            }

             // 1.update DCP_FAPIAO_TEMPLATE 发票模板
            String opNO = req.getOpNO(); // 创建/修改人
            String opName = req.getOpName(); // 创建/修改人名称
            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 创建/修改人时间
            String params = JSON.toJSON(request.getParams()).toString();

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_FAPIAO_TEMPLATE");
            //add Value
            ub1.addUpdateValue("TEMPLATENAME", new DataValue(request.getTemplateName(), Types.VARCHAR));
            ub1.addUpdateValue("TEMPLATETYPE", new DataValue(request.getTemplateType(), Types.VARCHAR));
            ub1.addUpdateValue("PARAMS", new DataValue(params, Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
            ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            ub1.addUpdateValue("ISDELETESHOP", new DataValue(isDeleteShop, Types.VARCHAR));
            //condition
            ub1.addCondition("TEMPLATEID", new DataValue(request.getTemplateId(), Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            // 2. delete DCP_FAPIAO_TEMPLATE_SHOP  发票模板适用门店
            DelBean db2 = new DelBean("DCP_FAPIAO_TEMPLATE_SHOP");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("TEMPLATEID", new DataValue(request.getTemplateId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            // 3. insert DCP_FAPIAO_TEMPLATE_SHOP 发票模板适用门店
            if (!CollectionUtils.isEmpty(shopList)) {

                String[] columns_DCP_FAPIAO_TEMPLATE_SHOP =
                        {
                                "EID", "TEMPLATEID", "SHOPID", "LASTMODIOPNAME", "LASTMODITIME"
                        };
                DataValue[] insValueDCP_FAPIAO_TEMPLATE_SHOP = null;
                for (DCP_FaPiaoTemplateUpdateReq.level2Elm level2Elm : shopList) {
                    insValueDCP_FAPIAO_TEMPLATE_SHOP = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(request.getTemplateId(), Types.VARCHAR),
                            new DataValue(level2Elm.getShopId(), Types.VARCHAR),
                            new DataValue(req.getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };
                    InsBean ib1 = new InsBean("DCP_FAPIAO_TEMPLATE_SHOP", columns_DCP_FAPIAO_TEMPLATE_SHOP);
                    ib1.addValues(insValueDCP_FAPIAO_TEMPLATE_SHOP);
                    this.addProcessData(new DataProcessBean(ib1));
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoTemplateUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoTemplateUpdateReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getTemplateId())) {
            errMsg.append("模板编码不能为空值 ");
            isFail = true;
        }
        if (Check.Null(request.getTemplateName())) {
            errMsg.append("模板名称不能为空值 ");
            isFail = true;
        }
        if (Check.Null(request.getPlatformType())) {
            errMsg.append("发票平台不能为空值 ");
            isFail = true;
        }
        if (Check.Null(request.getTemplateType())) {
            errMsg.append("模板类型不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getStatus())) {
            errMsg.append("状态不能为空值 ");
            isFail = true;
        }

        List<DCP_FaPiaoTemplateUpdateReq.level3Elm> params = request.getParams();
        if (!CollectionUtils.isEmpty(params)) {
            for (DCP_FaPiaoTemplateUpdateReq.level3Elm param : params) {
                if (Check.Null(param.getParam())) {
                    errMsg.append("参数编码不能为空值 ");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("参数详情不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoTemplateUpdateReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoTemplateUpdateReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoTemplateUpdateRes getResponseType() {
        return new DCP_FaPiaoTemplateUpdateRes();
    }

    /**
     * 检验通用模板 只能存在一个
     *
     * @param req
     * @return
     */
    private boolean isTemplateType(DCP_FaPiaoTemplateUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select TEMPLATEID from DCP_FAPIAO_TEMPLATE where eid = '" + req.geteId() + "' and TEMPLATETYPE = '1' AND TEMPLATEID <> '"+req.getRequest().getTemplateId()+"'");

        List<Map<String, Object>> maps = this.doQueryData(sqlbuf.toString(), null);
        if (CollectionUtils.isEmpty(maps)) {
            isFail = true;
        }
        return isFail;
    }

    /**
     * 检验特殊模板时门店是否重复
     *
     * @param req
     * @return
     */
    private String isDeleteShop(DCP_FaPiaoTemplateUpdateReq req) {
        DCP_FaPiaoTemplateUpdateReq.level1Elm request = req.getRequest();
        List<DCP_FaPiaoTemplateUpdateReq.level2Elm> shopList = request.getShopList();

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select a.TEMPLATEID,b.SHOPID,c.ORG_NAME from DCP_FAPIAO_TEMPLATE a LEFT JOIN DCP_FAPIAO_TEMPLATE_SHOP b on a.eid = b.eid and a.TEMPLATEID = b.TEMPLATEID " +
                " LEFT JOIN DCP_ORG_LANG c ON a.EID  = c.EID AND b.SHOPID  = c.ORGANIZATIONNO AND c.STATUS = 100 AND c.LANG_TYPE  = 'zh_CN' " +
                "where a.eid = "+req.geteId()+" AND a.TEMPLATEID <> '"+request.getTemplateId()+"'");
        if (!CollectionUtils.isEmpty(shopList)) {
            sqlbuf.append(" AND b.SHOPID IN (");
            for (DCP_FaPiaoTemplateUpdateReq.level2Elm level2Elm : shopList) {
                sqlbuf.append("'" + level2Elm.getShopId() + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(" )");
        }
        return sqlbuf.toString();
    }
}
