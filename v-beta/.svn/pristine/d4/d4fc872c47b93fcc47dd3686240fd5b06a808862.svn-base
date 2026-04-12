package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PriceAdjustDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PriceAdjustDetailQuery extends SPosBasicService<DCP_PriceAdjustDetailQueryReq, DCP_PriceAdjustDetailQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustDetailQueryReq req) throws Exception {
        StringBuilder errMsg = new StringBuilder("");


        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustDetailQueryReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustDetailQueryRes getResponseType() {
        return new DCP_PriceAdjustDetailQueryRes();
    }

    @Override
    protected DCP_PriceAdjustDetailQueryRes processJson(DCP_PriceAdjustDetailQueryReq req) throws Exception {
        DCP_PriceAdjustDetailQueryRes res = this.getResponse();
        String sql = this.getQuerySql(req);
        String[] conditionValues1 = {}; //查詢條件
        int totalRecords;                //总笔数
        int totalPages;
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
        res.setDatas(new ArrayList<>());
        //单头主键字段
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件

        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            totalRecords = Integer.parseInt(getQDataDetail.get(0).get("NUM").toString());
            //算總頁數
            totalPages = 1;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            Map<String, Object> master = getQDataDetail.get(0);

            DCP_PriceAdjustDetailQueryRes.Datas data = res.new Datas();

            data.setBillNo(StringUtils.toString(master.get("BILLNO"), ""));
            data.setBDate(DateFormatUtils.getDate(StringUtils.toString(master.get("BDATE"), "")));
            data.setBType(StringUtils.toString(master.get("BTYPE"), ""));
            data.setOrgNo(StringUtils.toString(master.get("ORGANIZATIONNO"), ""));
            data.setOrgName(StringUtils.toString(master.get("ORGANIZATIONNAME"), ""));

            data.setEffectiveDate(StringUtils.toString(master.get("EFFECTIVEDATE"), ""));
            data.setInvalidDate(StringUtils.toString(master.get("INVALIDDATE"), ""));
            data.setIsUpdateSdPrice(StringUtils.toString(master.get("ISUPDATESDPRICE"), ""));
            data.setIsUpdate_DefpurPrice(StringUtils.toString(master.get("UPDATE_DEFPURPRICE"), ""));

            data.setTemplateNo(StringUtils.toString(master.get("TEMPLATENO"), ""));
            data.setTemplateName(StringUtils.toString(master.get("TEMPLATENAME"), ""));

            data.setPurTemplateNo(StringUtils.toString(master.get("TEMPLATENO"), ""));
            data.setPurTemplateName(StringUtils.toString(master.get("TEMPLATENAME"), ""));

            data.setEmployeeID(StringUtils.toString(master.get("EMPLOYEEID"), ""));
            data.setEmployeeName(StringUtils.toString(master.get("EMPLOYEENAME"), ""));
            data.setDepartID(StringUtils.toString(master.get("DEPARTID"), ""));
            data.setDepartName(StringUtils.toString(master.get("DEPARTNAME"), ""));

            data.setSupplier(StringUtils.toString(master.get("SUPPLIER"), ""));
            data.setSupplierName(StringUtils.toString(master.get("SUPPLIERNAME"), ""));


            data.setMemo(StringUtils.toString(master.get("MEMO"), ""));
            data.setStatus(StringUtils.toString(master.get("STATUS"), ""));

            data.setCancelBy(StringUtils.toString(master.get("CANCELBY"), ""));
            data.setConfirmBy(StringUtils.toString(master.get("CANCELBY"), ""));

            data.setCreatorID(StringUtils.toString(master.get("CREATEOPID"), ""));
            data.setCreatorName(StringUtils.toString(master.get("CREATEOPNAME"), ""));

            data.setOwnerID(StringUtils.toString(master.get("OWNOPID"), ""));
            data.setOwnerName(StringUtils.toString(master.get("OWNOPNAME"), ""));
            data.setConfirmByName(StringUtils.toString(master.get("CONFIRMNAME"), ""));
            data.setCancelByName(StringUtils.toString(master.get("CANCELNAME"), ""));
            data.setLastmodifyName(StringUtils.toString(master.get("LASTMODIOPNAME"), ""));

            data.setOwnDeptID(StringUtils.toString(master.get("OWNDEPTID"), ""));
            data.setCreatorDeptID(StringUtils.toString(master.get("CREATEDEPTID"), ""));
            data.setCreatorDeptName(StringUtils.toString(master.get("CREATEDEPTNAME"), ""));
            data.setOwnDeptName(StringUtils.toString(master.get("OWNDEPTNAME"), ""));

            data.setCreate_datetime(StringUtils.toString(master.get("CREATETIME"), ""));
            data.setCancel_datetime(StringUtils.toString(master.get("CANCELTIME"), ""));
            data.setConfirm_datetime(StringUtils.toString(master.get("CONFIRMTIME"), ""));
            data.setLastmodify_datetime(StringUtils.toString(master.get("LASTMODITIME"), ""));

            data.setDetail(new ArrayList<>());

            //调用过滤函数
            List<Map<String, Object>> priceDataFind =  this.doQueryData(getQueryPriceSql(req), conditionValues1);

            for (Map<String, Object> oneData : getQDataDetail) {
                DCP_PriceAdjustDetailQueryRes.Detail detail = res.new Detail();
                detail.setPriceList(new ArrayList<>());
                detail.setOpriceList(new ArrayList<>());

                detail.setItem(StringUtils.toString(oneData.get("ITEM"), ""));
                detail.setCategory(StringUtils.toString(oneData.get("CATEGORY"), ""));
                detail.setCategoryName(StringUtils.toString(oneData.get("CATEGORY_NAME"), ""));
                detail.setPluNo(StringUtils.toString(oneData.get("PLUNO"), ""));
                detail.setPluName(StringUtils.toString(oneData.get("PLUNAME"), ""));
                detail.setPluBarcode(StringUtils.toString(oneData.get("PLUBARCODE"), ""));
                detail.setSpec(StringUtils.toString(oneData.get("SPEC"), ""));

                detail.setPriceUnit(StringUtils.toString(oneData.get("PRICEUNIT"), ""));
                detail.setPriceUnitName(StringUtils.toString(oneData.get("PRICEUNITNAME"), ""));

                detail.setNpurPriceType(StringUtils.toString(oneData.get("PURPRICETYPE"), ""));
                detail.setNpurPrice(StringUtils.toString(oneData.get("PURPRICE"), ""));

                detail.setOpurPriceType(StringUtils.toString(oneData.get("OPURPRICETYPE"), ""));
                detail.setOpurPrice(StringUtils.toString(oneData.get("OPURPRICE"), ""));

                detail.setPrice(StringUtils.toString(oneData.get("PRICE"), ""));
                detail.setOPrice(StringUtils.toString(oneData.get("OPRICE"), ""));
                detail.setMinPrice(StringUtils.toString(oneData.get("MINPRICE"), ""));
                detail.setSdPrice(StringUtils.toString(oneData.get("SDPRICE"), ""));
                detail.setIsDiscount(StringUtils.toString(oneData.get("ISDISCOUNT"), ""));
                detail.setIsProm(StringUtils.toString(oneData.get("ISPROM"), ""));

                Map<String, Object> condDetailImage=new HashMap<>();
                condDetailImage.put("ITEM", detail.getItem());
                List<Map<String, Object>> getPriceMap = MapDistinct.getWhereMap(priceDataFind,condDetailImage,true);
                if (null!=getPriceMap && !getPriceMap.isEmpty()){
                    for (Map<String, Object> price: getPriceMap ){
                        DCP_PriceAdjustDetailQueryRes.OpriceList oPrice = res.new OpriceList();
                        DCP_PriceAdjustDetailQueryRes.PriceList nPrice = res.new PriceList();

                        oPrice.setItem2(StringUtils.toString(price.get("ITEM2"),""));
                        nPrice.setItem2(StringUtils.toString(price.get("ITEM2"),""));

                        oPrice.setOpurPrice(StringUtils.toString(price.get("OPURPRICE"),""));
                        nPrice.setNpurPrice(StringUtils.toString(price.get("PURPRICE"),""));

                        oPrice.setObeginQty(StringUtils.toString(price.get("OBEGINQTY"),""));
                        nPrice.setNbeginQty(StringUtils.toString(price.get("BEGINQTY"),""));

                        oPrice.setOendQty(StringUtils.toString(price.get("OENDQTY"),""));
                        nPrice.setNendQty(StringUtils.toString(price.get("ENDQTY"),""));

                        detail.getPriceList().add(nPrice);
                        detail.getOpriceList().add(oPrice);

                    }
                }

                data.getDetail().add(detail);

            }

            res.getDatas().add(data);
        }

        return res;
    }

    private String getQueryPriceSql(DCP_PriceAdjustDetailQueryReq req) throws Exception  {

        String querySql = " SELECT * FROM DCP_PRICEADJUST_PRICE WHERE EID='%s' AND BILLNO='%s' ";
        querySql =String.format(querySql,req.geteId(),req.getRequest().getBillNo());
        return querySql;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PriceAdjustDetailQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();

        querySql.append(
                " SELECT * FROM ( " +
                        " SELECT COUNT(DISTINCT BILLNO ) OVER() NUM ,dense_rank() over(ORDER BY BILLNO) rn," +
                        " temp.* FROM( " +
                        "   SELECT " +
                        "   a.EID,a.ORGANIZATIONNO,a.BTYPE,a.BILLNO,a.BILL_ID,a.BDATE,a.SUPPLIER," +
                        "   a.UPDATE_DEFPURPRICE,a.EMPLOYEEID,a.DEPARTID,a.MEMO, " +
                        "   a.STATUS,a.OWNOPID,a.OWNDEPTID,a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME, " +
                        "   a.LASTMODIOPID,a.LASTMODITIME,a.CONFIRMBY,a.CONFIRMTIME,a.CANCELBY,a.CANCELTIME, " +
                        "   c.NAME AS CONFIRMNAME,d.NAME CANCELNAME,j.ORG_NAME ORGANIZATIONNAME, " +
                        "   e.NAME AS OWNOPNAME,f.NAME AS CREATEOPNAME,g.NAME AS LASTMODIOPNAME,k.SNAME AS SUPPLIERNAME, " +
                        "   h.DEPARTNAME AS CREATEDEPTNAME,i.DEPARTNAME AS OWNDEPTNAME," +
                        "   a.EFFECTIVEDATE,a.TEMPLATENO,b.ITEM,b.PRICEUNIT,b.PLUNO,b.PLUBARCODE,b.PURPRICETYPE," +
                        "   b.PURPRICE,b.PRICE,b.OPURPRICETYPE,b.OPURPRICE,b.OPRICE," +
                        "   m.CATEGORY,n.CATEGORY_NAME,o.UNAME AS PRICEUNITNAME,l.PLU_NAME as PLUNAME, " +
                        "   m.SPEC,p.DEPARTNAME AS DEPARTNAME,q.NAME EMPLOYEENAME, " +
                        "   CASE WHEN a.BTYPE='1' THEN t1.NAME ELSE t2.TEMPLATENAME END TEMPLATENAME," +
                        "   a.INVALIDDATE,a.ISUPDATESDPRICE,b.MINPRICE,b.SDPRICE,b.ISDISCOUNT,b.ISPROM " +
                        "   FROM DCP_PRICEADJUST a " +
                        "   LEFT JOIN DCP_PRICEADJUST_DETAIL b ON b.EID=a.EID AND b.BILLNO=a.BILLNO " +
                        "   LEFT JOIN DCP_PURCHASETEMPLATE_LANG t1 ON a.EID=t1.EID and a.TEMPLATENO=t1.PURTEMPLATENO and a.BTYPE='1'  AND t1.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_SALEPRICETEMPLATE_LANG t2 ON a.EID=t2.EID and a.TEMPLATENO=t2.TEMPLATEID and a.BTYPE='2'  AND t2.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_EMPLOYEE c ON c.EID=a.EID and c.EMPLOYEENO=a.CONFIRMBY " +
                        "   LEFT JOIN DCP_EMPLOYEE d ON d.EID=a.EID and d.EMPLOYEENO=a.CANCELBY " +
                        "   LEFT JOIN DCP_EMPLOYEE e ON e.EID=a.EID and e.EMPLOYEENO=a.OWNOPID " +
                        "   LEFT JOIN DCP_EMPLOYEE f ON f.EID=a.EID and f.EMPLOYEENO=a.CREATEOPID " +
                        "   LEFT JOIN DCP_EMPLOYEE g ON g.EID=a.EID and g.EMPLOYEENO=a.LASTMODIOPID " +
                        "   LEFT JOIN DCP_EMPLOYEE q ON q.EID=a.EID and q.EMPLOYEENO=a.EMPLOYEEID " +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG h on h.EID=a.EID AND h.DEPARTNO=a.CREATEDEPTID AND h.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG i on i.EID=a.EID AND i.DEPARTNO=a.OWNDEPTID AND i.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG p on p.EID=a.EID AND p.DEPARTNO=a.DEPARTID AND p.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + req.getLangType() + "'" +
//                        "   LEFT JOIN DCP_SUPPLIER_LANG k on k.EID=a.EID AND k.SUPPLIER=a.SUPPLIER AND k.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_BIZPARTNER k on k.EID=a.EID AND k.BIZPARTNERNO=a.SUPPLIER " +
                        "   LEFT JOIN DCP_GOODS_LANG l ON b.EID=l.EID and b.PLUNO=l.PLUNO AND l.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_GOODS m ON a.EID=m.EID and b.PLUNO=m.PLUNO " +
                        "   LEFT JOIN DCP_CATEGORY_LANG n on n.EID=m.EID AND n.CATEGORY=m.CATEGORY AND n.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_UNIT_LANG o on o.EID=a.EID AND o.UNIT=b.PRICEUNIT AND o.LANG_TYPE='" + req.getLangType() + "'" +
                        "  WHERE a.EID='" + req.geteId() + "' "
        );

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo()))
            querySql.append(" AND a.BILLNO ='")
                    .append(req.getRequest().getBillNo())
                    .append("'");

        querySql.append(") temp ");
        querySql.append(" )  a  ORDER BY BILLNO,ITEM " );

        return querySql.toString();
    }
}
