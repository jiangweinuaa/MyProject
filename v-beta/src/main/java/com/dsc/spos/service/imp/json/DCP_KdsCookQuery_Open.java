package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_KdsCookQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsCookQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: KDS机器人查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
public class DCP_KdsCookQuery_Open extends SPosBasicService<DCP_KdsCookQuery_OpenReq, DCP_KdsCookQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_KdsCookQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsCookQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsCookQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsCookQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_KdsCookQuery_OpenRes getResponseType() {
        return new DCP_KdsCookQuery_OpenRes();
    }

    @Override
    protected DCP_KdsCookQuery_OpenRes processJson(DCP_KdsCookQuery_OpenReq req) throws Exception {
        DCP_KdsCookQuery_OpenRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(datas)){
                for (Map<String, Object> data : datas) {
                    String machineid = data.get("MACHINEID").toString();
                    String sortid = data.get("SORTID").toString();
                    String cookid = data.get("COOKID").toString();
                    String cookname = data.get("COOKNAME").toString();
                    String status = data.get("STATUS").toString();

                    DCP_KdsCookQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setMachineId(machineid);
                    lv1.setSortId(sortid);
                    lv1.setCookId(cookid);
                    lv1.setCookName(cookname);
                    lv1.setStatus(status);
                    res.getDatas().add(lv1);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_KdsCookQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        DCP_KdsCookQuery_OpenReq.level1Elm request = req.getRequest();
        String machineId = request.getMachineId();
        sqlbuf.append("SELECT * FROM DCP_KDSCOOKSET  WHERE eid= '" + req.geteId() + "' AND SHOPID = '" + request.getShopId() + "'");

        sqlbuf.append(" ORDER BY SORTID");
        sql = sqlbuf.toString();
        return sql;
    }
}
