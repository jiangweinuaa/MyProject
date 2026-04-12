package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_POrderCustomerQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderCustomerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_POrderCustomerQuery extends SPosBasicService<DCP_POrderCustomerQueryReq, DCP_POrderCustomerQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderCustomerQueryReq req) throws Exception {
        if (req.getRequest()==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (Check.Null(req.getRequest().getTemplateNo()))
        {
            errMsg.append("节点templateNo不可为空值，");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_POrderCustomerQueryReq> getRequestType() {
        return new TypeToken<DCP_POrderCustomerQueryReq>(){};
    }

    @Override
    protected DCP_POrderCustomerQueryRes getResponseType() {
        return new DCP_POrderCustomerQueryRes();
    }

    @Override
    protected DCP_POrderCustomerQueryRes processJson(DCP_POrderCustomerQueryReq req) throws Exception {
        DCP_POrderCustomerQueryRes res = this.getResponse();
        DCP_POrderCustomerQueryRes.level1Elm datas = res.new level1Elm();
        datas.setDataList(new ArrayList<>());
        int totalRecords = 0;
        int totalPages = 0;
        boolean isNeedPage = true;

        if (req.getPageSize()==0)
        {
            isNeedPage = false;
        }
        String sql = this.getQuerySql(req);

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty())
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            if (isNeedPage)
            {
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            }

            for (Map<String, Object> oneData : getQData)
            {
                DCP_POrderCustomerQueryRes.level2Elm oneLv2 = res.new level2Elm();
                oneLv2.setCustomerNo(oneData.get("CUSTOMERNO").toString());
                oneLv2.setCustomerName(oneData.get("CUSTOMER_NAME").toString());
                datas.getDataList().add(oneLv2);
            }

        }
        res.setDatas(datas);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_POrderCustomerQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        boolean isNeedPage = true;

        if (req.getPageSize()==0)
        {
            isNeedPage = false;
        }

        String eId = req.geteId();
        String templateNo = req.getRequest().getTemplateNo();
        StringBuffer sqlbuf=new StringBuffer("");
        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over() num,row_number() over (order by a.CUSTOMERNO) rn,");
        sqlbuf.append(" a.CUSTOMERNO,b.CUSTOMER_NAME ");
        sqlbuf.append(" from DCP_TEMCUSTOMER a ");
        sqlbuf.append(" left join DCP_CUSTOMER b on  a.EID=b.EID and a.CUSTOMERNO=b.CUSTOMERNO ");
        sqlbuf.append(" where a.EID='"+eId+"' and a.TEMPLATENO='"+templateNo+"'");
        sqlbuf.append(" )");
        if (isNeedPage)
        {
            //計算起啟位置
            int startRow=(pageNumber-1) * pageSize;
            int endRow = startRow+pageSize;
            sqlbuf.append(" where rn>" + startRow + " and rn <="+endRow );
        }

        return sqlbuf.toString();
    }
}
