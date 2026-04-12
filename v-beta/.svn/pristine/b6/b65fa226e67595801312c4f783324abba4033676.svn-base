package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReserveTimeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReserveTimeQueryRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-07-27
 */
public class DCP_ReserveTimeQuery extends SPosBasicService<DCP_ReserveTimeQueryReq, DCP_ReserveTimeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveTimeQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveTimeQueryReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveTimeQueryReq> getRequestType() {
        return new TypeToken<DCP_ReserveTimeQueryReq>(){};
    }

    @Override
    protected DCP_ReserveTimeQueryRes getResponseType() {
        return new DCP_ReserveTimeQueryRes();
    }

    @Override
    protected DCP_ReserveTimeQueryRes processJson(DCP_ReserveTimeQueryReq req) throws Exception {
        DCP_ReserveTimeQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<DCP_ReserveTimeQueryRes.level1Elm>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(datas)){
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");

                String num = datas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> data : datas) {
                    String item = data.get("ITEM").toString();
                    String beginTime = data.get("BEGINTIME").toString();
                    String endTime = data.get("ENDTIME").toString();
                    String status = data.get("STATUS").toString();
                    String cycle = data.get("CYCLE").toString();

                    DCP_ReserveTimeQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setItem(item);
                    lv1.setBeginTime(df.format(df.parse(beginTime)));
                    lv1.setEndTime(df.format(df.parse(endTime)));
                    lv1.setCycle(cycle);
                    lv1.setStatus(status);

                    res.getDatas().add(lv1);
                }
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
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
    protected String getQuerySql(DCP_ReserveTimeQueryReq req) throws Exception {
        String sql = "";
        String status = req.getRequest().getStatus();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT count(item) OVER () AS num, DENSE_RANK() OVER (ORDER BY item) AS rn , ITEM, CYCLE, BEGINTIME, ENDTIME, STATUS" +
                " from DCP_RESERVETIME where eid = '" + req.geteId() + "' and SHOPID = '" + req.getRequest().getShopId() + "' ");
        if(!Check.Null(status)){
            sqlbuf.append(" AND status = '"+status+"'");
        }
        sqlbuf.append(" ORDER BY BEGINTIME");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
