package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AdvisorProfessionalQueryReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorProfessionalQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 职称等级查询
 * @author: wangzyc
 * @create: 2021-07-14
 */
public class DCP_AdvisorProfessionalQuery extends SPosBasicService<DCP_AdvisorProfessionalQueryReq, DCP_AdvisorProfessionalQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AdvisorProfessionalQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AdvisorProfessionalQueryReq> getRequestType() {
        return new TypeToken<DCP_AdvisorProfessionalQueryReq>(){};
    }

    @Override
    protected DCP_AdvisorProfessionalQueryRes getResponseType() {
        return new DCP_AdvisorProfessionalQueryRes();
    }

    @Override
    protected DCP_AdvisorProfessionalQueryRes processJson(DCP_AdvisorProfessionalQueryReq req) throws Exception {
        DCP_AdvisorProfessionalQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<DCP_AdvisorProfessionalQueryRes.level1Elm>());

        try {
            String sql = getQuerySql(req);
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(data)){
                for (Map<String, Object> oneData : data) {
                    DCP_AdvisorProfessionalQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setProfessionalId(oneData.get("PROFESSIONALID").toString());
                    lv1.setProfessionalName(oneData.get("PROFESSIONALNAME").toString());
                    res.getDatas().add(lv1);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:"+e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AdvisorProfessionalQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_PROFESSIONALGRADE");
        sql = sqlbuf.toString();
        return sql;
    }
}
