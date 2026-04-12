package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.json.cust.req.DCP_FaPiaoTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_FaPiaoTemplateDetail
 * 服务说明：发票模板详情
 *
 * @author wangzyc
 * @since 2021-1-27
 */
public class DCP_FaPiaoTemplateDetail extends SPosBasicService<DCP_FaPiaoTemplateDetailReq, DCP_FaPiaoTemplateDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_FaPiaoTemplateDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (Check.Null(req.getRequest().getTemplateId())) {
            errMsg.append("模板编码不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoTemplateDetailReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoTemplateDetailReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoTemplateDetailRes getResponseType() {
        return new DCP_FaPiaoTemplateDetailRes();
    }

    @Override
    protected DCP_FaPiaoTemplateDetailRes processJson(DCP_FaPiaoTemplateDetailReq req) throws Exception {
        DCP_FaPiaoTemplateDetailRes res = null;
        res = this.getResponse();
        String templateId = req.getRequest().getTemplateId();
        String sql = null;

        // 根据模板ID 取出模板相关的信息
        sql = this.getQuerySql(req);
        List<Map<String, Object>> templateDetails = this.doQueryData(sql, null);

        Map<String, Object> templateDetail = templateDetails.get(0);
        String platformType = templateDetail.get("PLATFORMTYPE").toString(); // 发票平台类型
        String strparams = templateDetail.get("PARAMS").toString(); // 参数明细

        // 根据发票 平台类型查询出参数信息
        sql = this.getParams(platformType);
        List<Map<String, Object>> paramList = this.doQueryData(sql, null);
        List<DCP_FaPiaoTemplateDetailRes.level3Elm> list = JSON.parseArray(strparams, DCP_FaPiaoTemplateDetailRes.level3Elm.class);

        List<DCP_FaPiaoTemplateDetailRes.level3Elm> params = new ArrayList<>();
        if (!CollectionUtils.isEmpty(paramList)) {
            // 参数编码和名称取自集合paramList，参数值优先取自集合list，取不到返回空串【兼容后续新增或删除参数项】
            for (Map<String, Object> param : paramList) {
                // 从解析后的List 中取value
                String value ="";
                for (DCP_FaPiaoTemplateDetailRes.level3Elm level3Elm : list) {
                        if (param.get("PARAM").toString().equals(level3Elm.getParam())) {
                            value = level3Elm.getValue();
                        }
                }

                DCP_FaPiaoTemplateDetailRes.level3Elm level3Elm = new DCP_FaPiaoTemplateDetailRes.level3Elm().toBuilder()
                        .param(param.get("PARAM").toString())
                        .name(param.get("NAME").toString())
                        .value(value)
                        .memo(param.get("MEMO").toString())
                        .groupId(param.get("GROUPID").toString())
                        .sortId(param.get("SORTID").toString())
                        .build();
                params.add(level3Elm);
            }

        }

        // 根据模板ID  获取门店
        sql = this.getShops(req);
        List<Map<String, Object>> shopList = this.doQueryData(sql, null);
        ArrayList<DCP_FaPiaoTemplateDetailRes.level2Elm> shops = new ArrayList<>();
        if (!CollectionUtils.isEmpty(shopList)) {
            for (Map<String, Object> shop : shopList) {
                DCP_FaPiaoTemplateDetailRes.level2Elm level2Elm = new DCP_FaPiaoTemplateDetailRes.level2Elm().toBuilder()
                        .shopId(shop.get("SHOPID").toString())
                        .shopName(shop.get("SHOPNAME").toString())
                        .build();
                shops.add(level2Elm);
            }
        }

        DCP_FaPiaoTemplateDetailRes.level1ELm level1ELm = new DCP_FaPiaoTemplateDetailRes.level1ELm().toBuilder()
                .templateId(templateDetail.get("TEMPLATEID").toString())
                .templateName(templateDetail.get("TEMPLATENAME").toString())
                .platformType(platformType)
                .platformName(templateDetail.get("PLATFORMNAME").toString())
                .templateType(templateDetail.get("TEMPLATETYPE").toString())
                .memo(templateDetail.get("MEMO").toString())
                .status(templateDetail.get("STATUS").toString())
                .params(params)
                .shopList(shops)
                .isDeleteShop(templateDetail.get("ISDELETESHOP").toString())
                .build();

        res.setDatas(level1ELm);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_FaPiaoTemplateDetailReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT a.TEMPLATEID, a.TEMPLATENAME, a.PLATFORMTYPE, b.PLATFORMNAME, a.TEMPLATETYPE , a.MEMO, a.STATUS, a.PARAMS,a.ISDELETESHOP" +
                " FROM DCP_FAPIAO_TEMPLATE a " +
                " LEFT JOIN DCP_FAPIAO_PLATFORM b ON a.PLATFORMTYPE = b.PLATFORMTYPE " +
                " WHERE a.TEMPLATEID = '" + req.getRequest().getTemplateId() + "' AND a.eid = '" + req.geteId() + "'");
        return sqlbuf.toString();
    }

    /**
     * 根据发票平台类型查询所有参数
     *
     * @param platformType
     * @return
     */
    private String getParams(String platformType) {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.PLATFORMTYPE, a.PARAM, b.NAME, b.MEMO,b.groupId,b.sortId" +
                " FROM DCP_FAPIAO_PLATFORM_PARAM a " +
                " LEFT JOIN DCP_FAPIAO_PARAM b ON a.PARAM = b.PARAM " +
                " WHERE a.PLATFORMTYPE = '" + platformType + "' ORDER BY b.groupId,b.sortId");
        return sqlbuf.toString();
    }

    /**
     * 根据模板ID 查询所有门店
     *
     * @param req
     * @return
     */
    private String getShops(DCP_FaPiaoTemplateDetailReq req) {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT  b.SHOPID, d.ORG_NAME AS shopName " +
                " FROM DCP_FAPIAO_TEMPLATE a " +
                " RIGHT JOIN DCP_FAPIAO_TEMPLATE_SHOP b ON a.eid = b.eid AND a.TEMPLATEID = b.TEMPLATEID " +
                " LEFT JOIN DCP_ORG_LANG d ON a.EID = d.EID AND b.SHOPID = d.ORGANIZATIONNO AND LANG_TYPE = 'zh_CN'" +
                " WHERE a.TEMPLATEID = '" + req.getRequest().getTemplateId() + "' and a.eid = '" + req.geteId() + "'");
        return sqlbuf.toString();
    }
}
