package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoTemplateCreateReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;


import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_FaPiaoTemplateCreate
 * 服务说明：发票模板添加
 *
 * @author wangzyc
 * @since 2021-1-27
 */
public class DCP_FaPiaoTemplateCreate extends SPosAdvanceService<DCP_FaPiaoTemplateCreateReq, DCP_FaPiaoTemplateCreateRes> {
    @Override
    protected void processDUID(DCP_FaPiaoTemplateCreateReq req, DCP_FaPiaoTemplateCreateRes res) throws Exception {
        DCP_FaPiaoTemplateCreateReq.level1Elm request = req.getRequest();
        String templateType = request.getTemplateType(); // 模板类型 通用模板:1  专用模板:2
        String isDeleteShop = request.getIsDeleteShop(); // 是否删除门店：Y/N，当前门店存在于系统中时，前端确认删除后传Y
        List<DCP_FaPiaoTemplateCreateReq.level2Elm> shopList = request.getShopList();
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
            // 模板类型为专用
            // 检核特殊模板时门店是否重复设置：isDeleteShop=N时，一个门店只能有一个特殊模板 ，返回code=‘001’
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

        if (!isFail) {

            // 检核通过 isDeleteShop = Y 删除其他模板中 适用门店=当前门店模板的数据
            if (isDeleteShop.equals("Y")) {
                for (DCP_FaPiaoTemplateCreateReq.level2Elm level2Elm : shopList) {
                    DelBean db1 = new DelBean("DCP_FAPIAO_TEMPLATE_SHOP");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(level2Elm.getShopId(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));
                }
            }

            // 1.insert DCP_FAPIAO_TEMPLATE 发票模板
            String[] columns_DCP_FAPIAO_TEMPLATE =
                    {
                            "EID", "TEMPLATEID", "TEMPLATENAME", "PLATFORMTYPE",
                            "TEMPLATETYPE", "PARAMS", "STATUS", "MEMO", "CREATEOPID",
                            "CREATEOPNAME", "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME","ISDELETESHOP"
                    };
            DataValue[] insValueDCP_FAPIAO_TEMPLATE = null;
            String opNO = req.getOpNO(); // 创建/修改人
            String opName = req.getOpName(); // 创建/修改人名称
            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 创建/修改人时间
            String params = JSON.toJSON(request.getParams()).toString();

            insValueDCP_FAPIAO_TEMPLATE = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(request.getTemplateId(), Types.VARCHAR),
                    new DataValue(request.getTemplateName(), Types.VARCHAR),
                    new DataValue(request.getPlatformType(), Types.VARCHAR),
                    new DataValue(request.getTemplateType(), Types.VARCHAR),
                    new DataValue(params, Types.VARCHAR),
                    new DataValue(request.getStatus(), Types.VARCHAR),
                    new DataValue(request.getMemo(), Types.VARCHAR),
                    new DataValue(opNO, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(opNO, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(request.getIsDeleteShop(), Types.VARCHAR)
            };
            InsBean ib1 = new InsBean("DCP_FAPIAO_TEMPLATE", columns_DCP_FAPIAO_TEMPLATE);
            ib1.addValues(insValueDCP_FAPIAO_TEMPLATE);
            this.addProcessData(new DataProcessBean(ib1));

            // 2.insert DCP_FAPIAO_TEMPLATE_SHOP 发票模板适用门店
            if (!CollectionUtils.isEmpty(shopList)) {

                String[] columns_DCP_FAPIAO_TEMPLATE_SHOP =
                        {
                                "EID", "TEMPLATEID", "SHOPID", "LASTMODIOPNAME", "LASTMODITIME"
                        };
                DataValue[] insValueDCP_FAPIAO_TEMPLATE_SHOP = null;
                for (DCP_FaPiaoTemplateCreateReq.level2Elm level2Elm : shopList) {
                    insValueDCP_FAPIAO_TEMPLATE_SHOP = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(request.getTemplateId(), Types.VARCHAR),
                            new DataValue(level2Elm.getShopId(), Types.VARCHAR),
                            new DataValue(req.getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };
                    InsBean ib2 = new InsBean("DCP_FAPIAO_TEMPLATE_SHOP", columns_DCP_FAPIAO_TEMPLATE_SHOP);
                    ib2.addValues(insValueDCP_FAPIAO_TEMPLATE_SHOP);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoTemplateCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoTemplateCreateReq.level1Elm request = req.getRequest();

        if(Check.Null(request.getTemplateId())){
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

        List<DCP_FaPiaoTemplateCreateReq.level3Elm> params = request.getParams();
        if (!CollectionUtils.isEmpty(params)) {
            for (DCP_FaPiaoTemplateCreateReq.level3Elm param : params) {
                if (Check.Null(param.getParam())) {
                    errMsg.append("参数编码不能为空值 ");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("参数详情不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getIsDeleteShop())) {
            errMsg.append("是否删除门店不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoTemplateCreateReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoTemplateCreateReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoTemplateCreateRes getResponseType() {
        return new DCP_FaPiaoTemplateCreateRes();
    }

    /**
     * 检验通用模板 只能存在一个
     *
     * @param req
     * @return
     */
    private boolean isTemplateType(DCP_FaPiaoTemplateCreateReq req) throws Exception {
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
    private String isDeleteShop(DCP_FaPiaoTemplateCreateReq req) {
        DCP_FaPiaoTemplateCreateReq.level1Elm request = req.getRequest();
        List<DCP_FaPiaoTemplateCreateReq.level2Elm> shopList = request.getShopList();

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select a.TEMPLATEID,b.SHOPID,c.ORG_NAME from DCP_FAPIAO_TEMPLATE a LEFT JOIN DCP_FAPIAO_TEMPLATE_SHOP b on a.eid = b.eid and a.TEMPLATEID = b.TEMPLATEID\n" +
                " LEFT JOIN DCP_ORG_LANG c ON a.EID  = c.EID AND b.SHOPID  = c.ORGANIZATIONNO AND c.STATUS = 100 AND c.LANG_TYPE  = 'zh_CN' " +
                "where a.eid = '"+req.geteId()+"'");
        if (!CollectionUtils.isEmpty(shopList)) {
            sqlbuf.append(" AND b.SHOPID IN (");
            for (DCP_FaPiaoTemplateCreateReq.level2Elm level2Elm : shopList) {
                sqlbuf.append("'" + level2Elm.getShopId() + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(" )");
        }
        return sqlbuf.toString();
    }


}
