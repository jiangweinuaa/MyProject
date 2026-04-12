package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_LineQueryReq;
import com.dsc.spos.json.cust.res.DCP_LineQueryRes;
import com.dsc.spos.json.cust.res.DCP_LineQueryRes.Datas;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.Map;
import org.apache.cxf.common.util.CollectionUtils;

/*
 * 服務函數：DCP_LineQuery
 * 服务说明：获取路线服务
 * @author jinzma
 * @since  2023-12-22
 */
public class DCP_LineQuery extends SPosBasicService<DCP_LineQueryReq, DCP_LineQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_LineQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_LineQueryReq> getRequestType() {
        return new TypeToken<DCP_LineQueryReq>(){};
    }

    @Override
    protected DCP_LineQueryRes getResponseType() {
        return new DCP_LineQueryRes();
    }

    @Override
    protected DCP_LineQueryRes processJson(DCP_LineQueryReq req) throws Exception {

        try{
            DCP_LineQueryRes res = this.getResponse();
            res.setDatas(new ArrayList<>());

            //int totalRecords = 0;								//总笔数
            //int totalPages = 0;									//总页数

            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)){
                //算總頁數
                //String num = getQData.get(0).get("NUM").toString();
                //totalRecords=Integer.parseInt(num);
                //totalPages = totalRecords / req.getPageSize();
                //totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> map:getQData){
                    Datas datas = res.new Datas();
                    datas.setLineNo(map.get("LINENO").toString());
                    datas.setLineName(map.get("LINENAME").toString());

                    res.getDatas().add(datas);
                }
            }


            //res.setPageNumber(req.getPageNumber());
            //res.setPageSize(req.getPageSize());
            //res.setTotalRecords(totalRecords);
            //res.setTotalPages(totalPages);

            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_LineQueryReq req) throws Exception {
        String eId = req.geteId();
        //分页处理
        //int pageSize=req.getPageSize();
        //int startRow=(req.getPageNumber()-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        /*sqlbuf.append(" select num,lineno,linename from ("
                + " select count(*)over() num ,row_number() over(order by a.lineno) as rn,a.*"
                + " from dcp_linemessage a"
                + " where a.eid='"+eId+"'"
                + " )"
                + " where rn>'"+startRow+"' and rn<="+(startRow+pageSize));*/

        sqlbuf.append(" select a.* from dcp_linemessage a where a.eid='"+eId+"' order by a.lineno ");


        return sqlbuf.toString();

    }
}
