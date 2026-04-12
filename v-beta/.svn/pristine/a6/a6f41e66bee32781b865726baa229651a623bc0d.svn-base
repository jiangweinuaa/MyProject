package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CurrencyQueryReq;
import com.dsc.spos.json.cust.res.DCP_CurrencyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;



public class DCP_CurrencyQuery extends SPosBasicService<DCP_CurrencyQueryReq, DCP_CurrencyQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CurrencyQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_CurrencyQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CurrencyQueryReq>(){};
    }

    @Override
    protected DCP_CurrencyQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CurrencyQueryRes();
    }

    @Override
    protected DCP_CurrencyQueryRes processJson(DCP_CurrencyQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        DCP_CurrencyQueryRes res = null;
        res = this.getResponse();
        String langType_cur = req.getLangType();
        int totalRecords = 0; //总笔数
        int totalPages = 0;

        sql = this.getQuerySql(req);
        List<Map<String, Object>> getDatas = this.doQueryData(sql, null);

        if(!getDatas.isEmpty() && getDatas != null ){
            res.setDatas(new ArrayList<DCP_CurrencyQueryRes.level1Elm>());
            String num = getDatas.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("CURRENCY", true);
            condition.put("NATION", true);
            //调用过滤函数
            List<Map<String, Object>> headDatas=MapDistinct.getMap(getDatas, condition);

            for (Map<String, Object> oneData : headDatas) {

                DCP_CurrencyQueryRes.level1Elm lev1 = res.new level1Elm();
                String nation = oneData.get("NATION").toString();
                //String taxName = oneData.get("TAXNAME").toString();
                String currency = oneData.get("CURRENCY").toString();
                String minvalue = oneData.get("MINVALUE").toString();
                String symbol = oneData.get("SYMBOL").toString();
                String priceDigit = oneData.get("PRICEDIGIT").toString();
                String amountDigit = oneData.get("AMOUNTDIGIT").toString();
                String costPriceDigit = oneData.get("COSTPRICEDIGIT").toString();
                String costAmountDigit = oneData.get("COSTAMOUNTDIGIT").toString();
                String status = oneData.get("STATUS").toString();
                String createopid = oneData.get("CREATEOPID").toString();
                String createopname = oneData.get("CREATEOPNAME").toString();
                String create_datetime = oneData.get("CREATETIME").toString();

                String creatorDeptID = oneData.get("CREATEDEPTID").toString();
                String createdeptname = oneData.get("CREATEDEPTNAME").toString();

                String lastmodifyopname = oneData.get("LASTMODIOPNAME").toString();
                String lastmodifytime = oneData.get("LASTMODITIME").toString();
                String lastmodifyopid = oneData.get("LASTMODIOPID").toString();




                lev1.setNation(nation);
                lev1.setCurrency(currency);
                lev1.setMinValue(minvalue);
                lev1.setSymbol(symbol);
                lev1.setPriceDigit(Integer.parseInt(priceDigit));
                lev1.setAmountDigit(Integer.parseInt(amountDigit));
                lev1.setCostPriceDigit(Integer.parseInt(costPriceDigit));
                lev1.setCostAmountDigit(Integer.parseInt(costAmountDigit));

                lev1.setStatus(status);
                lev1.setCreatorID(createopid);
                lev1.setCreatorName(createopname);
                lev1.setCreatorDeptID(creatorDeptID);
                lev1.setCreatorDeptName(createdeptname);
                lev1.setCreate_datetime(create_datetime);
                lev1.setLastmodifyID(lastmodifyopid);
                lev1.setLastmodifyName(lastmodifyopname);
                lev1.setLastmodify_datetime(lastmodifytime);


                lev1.setCurrencyName_lang(new ArrayList<DCP_CurrencyQueryRes.level2Elm>());
                for (Map<String, Object> langDatas : getDatas)
                {
                    //过滤属于此单头的明细
                    if(nation.equals(langDatas.get("NATION")) == false||currency.equals(langDatas.get("CURRENCY"))==false)
                        continue;

                    DCP_CurrencyQueryRes.level2Elm lev2 = res.new level2Elm();
                    String langType = langDatas.get("LANGTYPE").toString();
                    String lTaxName = langDatas.get("CURRENCY_NAME").toString();
                    if(langType.equals(langType_cur))
                    {
                        lev1.setCurrencyName(lTaxName);
                    }

                    lev2.setLangType(langType);
                    lev2.setName(lTaxName);
                    lev1.getCurrencyName_lang().add(lev2);
                }

                res.getDatas().add(lev1);
            }

        }
        else
        {
            res.setDatas(new ArrayList<DCP_CurrencyQueryRes.level1Elm>());
        }
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CurrencyQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();


        String keyTxt = null;
        String status = null;
        if(req.getRequest()!=null)
        {
            keyTxt = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
        }

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = ((pageNumber - 1) * pageSize);
        startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" SELECT * FROM ("
                + " SELECT  COUNT(DISTINCT a.CURRENCY ) OVER() NUM ,dense_rank() over(ORDER BY a.NATION,a.CURRENCY) rn, a. *  FROM "
                + " ( SELECT a.NATION ,a.CURRENCY ,a.STATUS, a.MINVALUE, a.SYMBOL , a.PRICEDIGIT, a.AMOUNTDIGIT, b.lang_Type AS langType,b.name as CURRENCY_NAME ,"
                + "   a.EID,a.COSTPRICEDIGIT,a.COSTAMOUNTDIGIT,a.CREATEOPID,a.CREATEDEPTID,to_char(a.CREATETIME,'yyyy-mm-dd HH:mm:ss') CREATETIME ,a.LASTMODIOPID,to_char(a.LASTMODITIME,'yyyy-mm-dd HH:mm:ss') LASTMODITIME,d.departname as CREATEDEPTNAME,e1.name as CREATEOPNAME,e2.name as LASTMODIOPNAME  FROM DCP_CURRENCY a   "
                + " LEFT JOIN DCP_CURRENCY_lang b ON a.EID = b.EID AND a.NATION = b.NATION AND a.CURRENCY=b.CURRENCY " +
                " left join dcp_employee e1 on e1.employeeno=a.createopid and a.eid=e1.eid  " +
                " left join dcp_employee e2 on e2.employeeno=a.lastmodiopid and a.eid=e2.eid  " +
                " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.createdeptid and d.lang_type='"+req.getLangType()+" '  "+
                ")   a "
                + " WHERE  EID = '"+eId+"'  " );

        if (keyTxt != null && keyTxt.length() > 0)
        {
            sqlbuf.append(" AND ( CURRENCY like '%%"+keyTxt+"%%' or CURRENCY_NAME like '%%"+keyTxt+"%%' or NATION like '%%"+keyTxt+"%%'   ) ");
        }
        if (status != null && status.length() > 0)
        {
            sqlbuf.append(" AND status = '"+status+"' ");
        }

        sqlbuf.append( " ) WHERE rn > " + startRow + " AND rn <= " + (startRow+pageSize) + "  order by CURRENCY ");
        sql= sqlbuf.toString();
        return sql;
    }

}
