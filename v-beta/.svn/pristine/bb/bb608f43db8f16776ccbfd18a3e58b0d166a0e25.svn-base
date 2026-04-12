package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TaxGroupDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TaxGroupDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_TaxGroupDetailQuery extends SPosBasicService<DCP_TaxGroupDetailQueryReq, DCP_TaxGroupDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_TaxGroupDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TaxGroupDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_TaxGroupDetailQueryReq>() {};
    }

    @Override
    protected DCP_TaxGroupDetailQueryRes getResponseType() {
        return new DCP_TaxGroupDetailQueryRes();
    }

    @Override
    protected DCP_TaxGroupDetailQueryRes processJson(DCP_TaxGroupDetailQueryReq req) throws Exception {
        DCP_TaxGroupDetailQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
//            totalPages = totalRecords / req.getPageSize();
//            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
//            res.setTotalPages(totalPages);

            //单头主键字段
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("EID", true);
            condition.put("TAXGROUPNO", true);

            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getData, condition);

            for (Map<String, Object> oneData : getQHeader) {
                DCP_TaxGroupDetailQueryRes.Datas datas = res.new Datas();
                String taxGroupNo = String.valueOf(oneData.get("TAXGROUPNO"));

                datas.setTaxGroupNo(oneData.get("TAXGROUPNO").toString());
                datas.setTaxGroupName(StringUtils.toString(oneData.get("TAXGROUPNAME"), ""));
                datas.setTaxCode(oneData.get("TAXCODE").toString());
                datas.setTaxName(StringUtils.toString(oneData.get("TAXNAME"), ""));
                datas.setTaxRate(StringUtils.toString(oneData.get("TAXRATE"), ""));
                datas.setInclTax(StringUtils.toString(oneData.get("INCLTAX"), ""));
                datas.setGoodsQty(StringUtils.toString(oneData.get("QTY"), ""));
                datas.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                datas.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                datas.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                datas.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                datas.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                datas.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                datas.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                datas.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                datas.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                datas.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));
                datas.setTaxGroupNo(oneData.get("TAXGROUPNO").toString());
                datas.setTaxGroupName(StringUtils.toString(oneData.get("TAXGROUPNAME"), ""));
                datas.setTaxCode(oneData.get("TAXCODE").toString());
                datas.setTaxName(StringUtils.toString(oneData.get("TAXNAME"), ""));
                datas.setTaxRate(StringUtils.toString(oneData.get("TAXRATE"), ""));
                datas.setInclTax(StringUtils.toString(oneData.get("INCLTAX"), ""));

                datas.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                datas.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                datas.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                datas.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                datas.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                datas.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                datas.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                datas.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                datas.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                datas.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));

                datas.setGoodsList(new ArrayList<>());
                for (Map<String, Object> oneData2 : getData) {
                    //过滤属于此单头的明细
                    if (!taxGroupNo.equals(oneData2.get("TAXGROUPNO"))) {
                        continue;
                    }
                    DCP_TaxGroupDetailQueryRes.GoodsList goodsList = res.new GoodsList();

                    goodsList.setAttrId(oneData2.get("ATTRID").toString());
                    goodsList.setAttrType(oneData2.get("ATTRTYPE").toString());
                    goodsList.setStatus(oneData2.get("DETAILSTATUS").toString());
                    goodsList.setAttrName(oneData2.get("ATTRNAME").toString());

                    if ("1".equals(oneData2.get("ATTRTYPE").toString())) {
                        goodsList.setGoodsQty(oneData2.get("CQTY").toString());
                    }else {
                        goodsList.setGoodsQty("1");
                    }

                    datas.getGoodsList().add(goodsList);
                }

                res.getDatas().add(datas);
            }

        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
//        res.setTotalRecords(totalRecords);
//        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_TaxGroupDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ");

        sb.append("SELECT row_number() OVER (ORDER BY a.TAXGROUPNO,attrType, attrId ASC) AS RN,COUNT(*) OVER ( ) NUM,NVL(B.QTY,0) QTY,NVL(cs.CQTY,0) CQTY,A.*,D.TAXNAME,C.TAXRATE,C.INCLTAX," +
                " em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME," +
                " d.ATTRTYPE,d.ATTRID,d.STATUS as DETAILSTATUS," +
                " CASE WHEN D.ATTRTYPE='1' THEN F.CATEGORY_NAME ELSE E.PLU_NAME END ATTRNAME " +
                " FROM DCP_TAXGROUP A " +
                " INNER JOIN DCP_TAXGROUP_DETAIL D ON A.EID=D.EID AND A.TAXGROUPNO=D.TAXGROUPNO  " +
                " LEFT JOIN (" +
                "  SELECT A.EID,A.TAXGROUPNO,COUNT(DISTINCT ATTRID) QTY" +
                "  FROM(" +
                "   SELECT A.EID,a.TAXGROUPNO,b.ATTRTYPE,CASE WHEN b.ATTRTYPE='1' THEN c.PLUNO ELSE b.ATTRID END ATTRID " +
                "   FROM DCP_TAXGROUP a" +
                "   INNER JOIN DCP_TAXGROUP_DETAIL b on a.EID=b.EID and A.TAXGROUPNO=b.TAXGROUPNO" +
                "   LEFT JOIN DCP_GOODS c ON b.EID=c.EID and c.CATEGORY=b.ATTRID AND b.ATTRTYPE='1' and c.STATUS='100' " +
                "   LEFT JOIN DCP_GOODS d ON b.EID=d.EID and b.ATTRID=d.PLUNO AND b.ATTRTYPE='2' and d.STATUS='100' " +
//                "  WHERE c.STATUS='100' and d.STATUS='100'" +
                "  WHERE  c.PLUNO is not  null or d.PLUNO is not null " +
                "  ) A GROUP BY A.EID,TAXGROUPNO" +
                ") B ON A.EID=B.EID AND A.TAXGROUPNO=B.TAXGROUPNO " +
                " LEFT JOIN DCP_TAXCATEGORY C ON C.EID=A.EID AND C.TAXCODE=A.TAXCODE " +
                " LEFT JOIN DCP_TAXCATEGORY_LANG d ON d.EID=A.EID AND d.TAXCODE=A.TAXCODE and d.LANG_TYPE='" +req.getLangType()+"'"+
                " LEFT JOIN DCP_GOODS_LANG E ON E.EID=d.EID and D.ATTRID=E.PLUNO AND D.ATTRTYPE='2' AND E.lang_type='" + req.getLangType() + "'" +
                " LEFT JOIN DCP_CATEGORY_LANG F ON F.EID=d.EID and D.ATTRID=F.CATEGORY AND D.ATTRTYPE='1' AND F.lang_type='" + req.getLangType() + "'" +
                " LEFT JOIN (SELECT EID,CATEGORY, COUNT(1) CQTY " +
                "  FROM DCP_GOODS " +
                "  WHERE  STATUS = '100' " +
                "  GROUP BY EID,CATEGORY ) cs on cs.eid=d.eid and cs.CATEGORY=d.ATTRID and d.ATTRTYPE='1' " +
                " LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID" +
                " LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID" +
                " LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='" + req.getLangType() + "'" +
                " WHERE 1=1");

        if (StringUtils.isNotEmpty(req.getRequest().getTaxGroupNo())) {
            sb.append(" AND A.TAXGROUPNO='").append(req.getRequest().getTaxGroupNo()).append("'");
        }


        sb.append("  )  a "
//                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY TAXGROUPNO,attrType, attrId ASC");


        return sb.toString();
    }
}
