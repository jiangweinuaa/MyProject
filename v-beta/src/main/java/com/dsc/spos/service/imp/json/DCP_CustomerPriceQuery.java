package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPriceQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustomerPriceQuery extends SPosBasicService<DCP_CustomerPriceQueryReq, DCP_CustomerPriceQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String customerNo = req.getRequest().getCustomerNo();

        if (Check.Null(customerNo)) {
            errCt++;
            errMsg.append("客户编码不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_CustomerPriceQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CustomerPriceQueryReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CustomerPriceQueryRes();
    }

    @Override
    protected DCP_CustomerPriceQueryRes processJson(DCP_CustomerPriceQueryReq req) throws Exception {

        // TODO Auto-generated method stub
        String sql = null;
        DCP_CustomerPriceQueryRes res = null;
        int totalRecords = 0; //总笔数
        int totalPages = 0;

        res = this.getResponse();
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        res.setDatas(new ArrayList<>());

        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            String num = getQDataDetail.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getQDataDetail) {
                DCP_CustomerPriceQueryRes.Datas oneLv1 = res.new Datas();
                oneLv1.setCustomerNo(oneData.get("CUSTOMERNO").toString());
                oneLv1.setBDate(oneData.get("LASTMODITIME").toString());
                oneLv1.setPluNo(oneData.get("PLUNO").toString());
                oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                oneLv1.setItem(oneData.get("ITEM").toString());
                oneLv1.setUnit(oneData.get("UNIT").toString());
                oneLv1.setUnitName(oneData.get("UNAME").toString());
                oneLv1.setPrice(oneData.get("PRICE").toString());
                oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                oneLv1.setIsDiscount(oneData.get("ISDISCOUNT").toString());
                oneLv1.setIsProm(oneData.get("ISPROM").toString());
                oneLv1.setSdRetailPrice(oneData.get("SDRETAILPRICE").toString());
                oneLv1.setDiscRate(oneData.get("DISCRATE").toString());
                oneLv1.setCustomerType(StringUtils.toString(oneData.get("CUSTOMERTYPE"), ""));
                oneLv1.setCategory(StringUtils.toString(oneData.get("CATEGORY"), ""));
                oneLv1.setCategoryName(StringUtils.toString(oneData.get("CATEGORY_NAME"), ""));
                oneLv1.setOrgNo(StringUtils.toString(oneData.get("ORGANIZATIONNO"), ""));
                oneLv1.setOItem(StringUtils.toString(oneData.get("OITEM"), ""));
                oneLv1.setOfNo(StringUtils.toString(oneData.get("OFNO"), ""));
                oneLv1.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                oneLv1.setOrgName(StringUtils.toString(oneData.get("ORG_NAME"), ""));
                oneLv1.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                oneLv1.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                oneLv1.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));

                res.getDatas().add(oneLv1);
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CustomerPriceQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        String customerNo = req.getRequest().getCustomerNo();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = " SELECT DISTINCT EID,CUSTOMERNO, CATEGORYID, DISCRATE " +
                "      FROM (SELECT a.EID,d.ID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                "            FROM DCP_CUSTOMER_CATE_DISC a " +
                "                     INNER JOIN DCP_CUSTGROUP b " +
                "                                ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                "                     INNER JOIN DCP_CUSTGROUP_DETAIL c " +
                "                                on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '1'" +
                "                     INNER JOIN DCP_TAGTYPE_DETAIL d" +
                "                                ON d.EID = c.EID and TAGNO = c.ATTRID and d.TAGGROUPTYPE = 'CUST'" +
                "            UNION all " +
                "            SELECT a.EID,c.ATTRID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                "            FROM DCP_CUSTOMER_CATE_DISC a " +
                "                     INNER JOIN DCP_CUSTGROUP b " +
                "                                ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                "                     INNER JOIN DCP_CUSTGROUP_DETAIL c" +
                "                                on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '2' " +
                "            UNION all " +
                "            SELECT a.EID,CUSTOMERNO, CATEGORYID, DISCRATE " +
                "            FROM DCP_CUSTOMER_CATE_DISC a " +
                "            WHERE a.CUSTOMERTYPE = '2') a ";

        StringBuilder sqlbuf = new StringBuilder();
        sqlbuf.append("select * from (");

        sqlbuf.append(" select count(*) over() num, rownum rn," +
                        " a.*,b.plu_name,ul1.uname " +
                        " ,ol1.ORG_NAME,dp0.DEPARTNAME,ee5.NAME as EMPLOYEENAME  " +
                        " ,c.CATEGORY,cl1.CATEGORY_NAME,c.PRICE * NVL(uc.UNIT_RATIO,1) as SDRETAILPRICE " +
                        " ,nvl(cd1.DISCRATE,temp.DISCRATE) DISCRATE " +
                        " from dcp_customer_price a ")
                .append(" left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='").append(langType).append("' ")
                .append(" left join dcp_unit_lang ul1 on a.eid=ul1.eid and a.unit=ul1.unit and ul1.lang_type='").append(langType).append("' ")
                .append(" LEFT JOIN DCP_GOODS c ON c.eid=b.eid and c.PLUNO=b.PLUNO ")
                .append(" left join DCP_CUSTOMER_CATE_DISC cd1 on cd1.eid=a.eid and cd1.CUSTOMERNO=a.CUSTOMERNO and cd1.CUSTOMERTYPE=a.CUSTOMERTYPE and cd1.CATEGORYID=c.CATEGORY")
                .append(" LEFT JOIN (").append(sql).append(" ) temp on temp.eid=a.eid and temp.CUSTOMERNO=a.CUSTOMERNO and temp.CATEGORYID=c.CATEGORY   ")
//                .append(" left join DCP_CUSTPRICEADJUST_DETAIL add1 on a.EID=add1.EID and a.OFNO=add1.BILLNO and a.OITEM=add1.ITEM  ")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid =c.eid and cl1.CATEGORY=c.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid =a.eid and a.ORGANIZATIONNO=ol1.ORGANIZATIONNO and ol1.lang_type='").append(langType).append("' ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee5 ON ee5.EID=a.EID and ee5.EMPLOYEENO=a.EMPLOYEEID")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp0 on dp0.EID=a.EID AND dp0.DEPARTNO=a.DEPARTID AND dp0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNITCONVERT uc on uc.eid = a.eid and uc.OUNIT=c.SUNIT and uc.UNIT=a.UNIT  ")
        ;
        sqlbuf.append(" where A.EID='").append(eId).append("' ");
        if (keyTxt != null && !keyTxt.isEmpty()) {
            sqlbuf.append(" and (A.PLUNO like '%%").append(keyTxt).append("%%' or B.PLU_NAME like '%%").append(keyTxt).append("%%')");
        }
        if (customerNo != null && !customerNo.isEmpty()) {
            sqlbuf.append(" and a.customerno='" + customerNo + "' ");
        }
        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));

        sql = sqlbuf.toString();
        return sql;

    }

}
