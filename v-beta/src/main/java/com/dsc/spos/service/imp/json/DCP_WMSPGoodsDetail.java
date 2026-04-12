package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WMSPGoodsDetailReq;
import com.dsc.spos.json.cust.res.DCP_WMSPGoodsDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_WMSPGoodsDetail extends SPosBasicService<DCP_WMSPGoodsDetailReq, DCP_WMSPGoodsDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_WMSPGoodsDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_WMSPGoodsDetailReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("request节点不可为空, ");
            isFail = true;

        } else {

            if (Check.Null(request.getId())) {
                errMsg.append("商品id不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.substring(0, errMsg.length() - 2));
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WMSPGoodsDetailReq> getRequestType() {
        return new TypeToken<DCP_WMSPGoodsDetailReq>() {
        };
    }

    @Override
    protected DCP_WMSPGoodsDetailRes getResponseType() {
        return new DCP_WMSPGoodsDetailRes();
    }

    @Override
    protected DCP_WMSPGoodsDetailRes processJson(DCP_WMSPGoodsDetailReq req) throws Exception {
        DCP_WMSPGoodsDetailRes res = this.getResponseType();
        res.setDatas(res.new level1Elm());
        try {
            String sql = getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if (datas == null || datas.isEmpty()) {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功！");
                return res;
            }
            Map<String, Object> data = datas.get(0);
            DCP_WMSPGoodsDetailRes.level1Elm lv1 = res.new level1Elm();
            lv1.setSpecDatas(new ArrayList<>());
            lv1.setAttrDatas(new ArrayList<>());
            String id = data.get("ID").toString();
            String name = data.get("NAME").toString();
            String status = data.get("STATUS").toString();
            String createopid = data.get("CREATEOPID").toString();
            String createopname = data.get("CREATEOPNAME").toString();
            String createtime = data.get("CREATETIME").toString();
            String lastmodiopid = data.get("LASTMODIOPID").toString();
            String lastmodiopname = data.get("LASTMODIOPNAME").toString();
            String lastmoditime = data.get("LASTMODITIME").toString();
            lv1.setId(id);
            lv1.setName(name);
            lv1.setStatus(status);
            lv1.setCreateopid(createopid);
            lv1.setCreateopname(createopname);
            lv1.setCreatetime(createtime);
            lv1.setLastmodiopid(lastmodiopid);
            lv1.setLastmodiopname(lastmodiopname);
            lv1.setLastmoditime(lastmoditime);

            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("SPECID", true);
            //调用过滤函数
            List<Map<String, Object>> getSpecList = MapDistinct.getMap(datas, condition);
            if (!CollectionUtils.isEmpty(getSpecList)) {
                for (Map<String, Object> par : getSpecList) {
                    DCP_WMSPGoodsDetailRes.levelSpec spec = res.new levelSpec();
                    spec.setSpecId(par.get("SPECID").toString());
                    spec.setSpecName(par.get("SPECNAME").toString());
                    lv1.getSpecDatas().add(spec);
                }
            }

            //
            condition.clear();
            condition.put("ATTRNAME", true);
            condition.put("ATTRVALUE", true);
            //调用过滤函数
            List<Map<String, Object>> getAttrList = MapDistinct.getMap(datas, condition);
            if (!CollectionUtils.isEmpty(getAttrList)) {
                for (Map<String, Object> par : getAttrList) {
                    DCP_WMSPGoodsDetailRes.levelAttr attr = res.new levelAttr();
                    attr.setAttrName(par.get("ATTRNAME").toString());
                    attr.setAttrValue(par.get("ATTRVALUE").toString());
                    attr.setAttrValue_elm(par.get("ATTRVALUE_ELM").toString());
                    attr.setAttrValue_mt(par.get("ATTRVALUE_MT").toString());
                    attr.setPluBarcode(par.get("PLUBARCODE").toString());
                    lv1.getAttrDatas().add(attr);

                }
            }

            res.setDatas(lv1);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");


        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_WMSPGoodsDetailReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT A.*, B.SPECID,B.SPECNAME,C.ATTRNAME,C.ATTRVALUE,C.ATTRVALUE_ELM,C.ATTRVALUE_MT,C.PLUBARCODE " +
                " FROM DCP_WMSPGOODS A " +
                " LEFT JOIN DCP_WMSPGOODS_SPEC B ON A.EID = B.EID AND A.ID = B.ID " +
                " LEFT JOIN DCP_WMSPGOODS_ATTR C ON A.EID = C.EID AND A.ID = C.ID " +
                " WHERE A.EID = '" + req.geteId() + "' and A.ID = '" + req.getRequest().getId() + "'");

        sql = sqlbuf.toString();
        return sql;
    }
}
