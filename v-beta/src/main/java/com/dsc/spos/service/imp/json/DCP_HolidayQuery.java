package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_HolidayQueryReq;
import com.dsc.spos.json.cust.res.DCP_HolidayQueryRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 门店休息日查询
 * @author: wangzyc
 * @create: 2021-07-27
 */
public class DCP_HolidayQuery extends SPosBasicService<DCP_HolidayQueryReq, DCP_HolidayQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_HolidayQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_HolidayQueryReq.level1Elm request = req.getRequest();

        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空值 ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_HolidayQueryReq> getRequestType() {
        return new TypeToken<DCP_HolidayQueryReq>() {
        };
    }

    @Override
    protected DCP_HolidayQueryRes getResponseType() {
        return new DCP_HolidayQueryRes();
    }

    @Override
    protected DCP_HolidayQueryRes processJson(DCP_HolidayQueryReq req) throws Exception {
        DCP_HolidayQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<DCP_HolidayQueryRes.level1Elm>());
        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(datas)){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String num = datas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> data : datas) {
                    String item = data.get("ITEM").toString();
                    String beginDate = data.get("BEGINDATE").toString();
                    String endDate = data.get("ENDDATE").toString();
                    String memo = data.get("MEMO").toString();

                    DCP_HolidayQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setItem(item);
                    lv1.setBeginDate(df.format(df.parse(beginDate)));
                    lv1.setEndDate(df.format(df.parse(endDate)));
                    lv1.setMemo(memo);

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
    protected String getQuerySql(DCP_HolidayQueryReq req) throws Exception {
        String sql = "";

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ( SELECT count(item) OVER () AS num, DENSE_RANK() OVER (ORDER BY item) AS rn ,ITEM,BEGINDATE,ENDDATE,MEMO " +
                " from DCP_RESERVEHOLIDAY where eid = '" + req.geteId() + "' and SHOPID = '" + req.getRequest().getShopId() + "' ORDER BY item");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
