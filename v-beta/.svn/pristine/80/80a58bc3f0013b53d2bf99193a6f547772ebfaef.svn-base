package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_HqDishControlQueryReq;
import com.dsc.spos.json.cust.res.DCP_HqDishControlQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_HqDishControlQuery extends SPosBasicService<DCP_HqDishControlQueryReq, DCP_HqDishControlQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_HqDishControlQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        //必传值不为空
        if (req.getRequest() == null)
        {
            errMsg.append("request不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_HqDishControlQueryReq> getRequestType()
    {
        return new TypeToken<DCP_HqDishControlQueryReq>(){};
    }

    @Override
    protected DCP_HqDishControlQueryRes getResponseType()
    {
        return new DCP_HqDishControlQueryRes();
    }

    @Override
    protected DCP_HqDishControlQueryRes processJson(DCP_HqDishControlQueryReq req) throws Exception
    {
        DCP_HqDishControlQueryRes res=this.getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        int totalRecords;								//总笔数
        int totalPages;									//总页数

        res.setDatas(new ArrayList<DCP_HqDishControlQueryRes.level1Elm>());

        if (getQData != null && getQData.isEmpty() == false)
        {

            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            for (Map<String, Object> oneData : getQData)
            {
                DCP_HqDishControlQueryRes.level1Elm lv1=res.new level1Elm();

                lv1.setGoodsType(oneData.get("GOODSTYPE").toString());
                lv1.setId(oneData.get("ID").toString());
                lv1.setName(oneData.get("NAME").toString());
                lv1.setUnCall(oneData.get("UNCALL").toString());
                lv1.setUnCook(oneData.get("UNCOOK").toString());
                lv1.setUnSide(oneData.get("UNSIDE").toString());

                res.getDatas().add(lv1);
            }

        }



        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_HqDishControlQueryReq req) throws Exception
    {
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        String sql = "";

        sqlbuf.append("select * from ( " +
                              " select count(*) over() num,row_number() over (order by a.goodstype,a.id) rn,a.* from DCP_HQKDSDISHES_CONTROL a " +
                              " where a.eid='"+req.geteId()+"' ");


        if (!Check.Null(req.getRequest().getKeyTxt()))
        {
            sqlbuf.append(" and (id like '%%"+req.getRequest().getKeyTxt()+"%%' or name like '%%"+req.getRequest().getKeyTxt()+"%%') ");
        }

        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

        sql = sqlbuf.toString();
        return sql;
    }


}
