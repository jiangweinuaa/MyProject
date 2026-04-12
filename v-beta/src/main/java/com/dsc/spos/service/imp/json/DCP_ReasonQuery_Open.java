package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReasonQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReasonQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 原因查询
 * @author: wangzyc
 * @create: 2022-03-08
 */
public class DCP_ReasonQuery_Open extends SPosBasicService<DCP_ReasonQuery_OpenReq, DCP_ReasonQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReasonQuery_OpenReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReasonQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReasonQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReasonQuery_OpenRes getResponseType() {
        return new DCP_ReasonQuery_OpenRes();
    }

    @Override
    protected DCP_ReasonQuery_OpenRes processJson(DCP_ReasonQuery_OpenReq req) throws Exception {
        DCP_ReasonQuery_OpenRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("BSTYPE", true);
            // 调用过滤函数
            List<Map<String, Object>> getBsType = MapDistinct.getMap(queryDatas, condition);

            if(!CollectionUtils.isEmpty(queryDatas)){
                for (Map<String, Object> bsTypeMap : getBsType) {
                    String bstype = bsTypeMap.get("BSTYPE").toString();

                    DCP_ReasonQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setReasonType(bstype);
                    lv1.setReasonList(new ArrayList<>());
                    for (Map<String, Object> queryData : queryDatas) {
                        String bstype1 = queryData.get("BSTYPE").toString();
                        if(!bstype1.equals(bstype)){
                            continue;
                        }
                        String bsNo = queryData.get("BSNO").toString();
                        String bsName = queryData.get("BSNAME").toString();
                        DCP_ReasonQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                        lv2.setReasonId(bsNo);
                        lv2.setReasonName(bsName);
                        lv1.getReasonList().add(lv2);
                    }
                    res.getDatas().add(lv1);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ReasonQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String reasonType = req.getRequest().getReasonType();
        sqlbuf.append("SELECT BSTYPE,BSNO,BSNAME FROM DCP_REASON WHERE eid = '" + req.geteId() + "' AND STATUS = '100' ");
        if (!Check.Null(reasonType)) {
            sqlbuf.append(" and BSTYPE = '" + reasonType + "'");
        }
        sqlbuf.append(" order by TO_NUMBER( bstype),bsno");
        sql = sqlbuf.toString();
        return sql;
    }
}
