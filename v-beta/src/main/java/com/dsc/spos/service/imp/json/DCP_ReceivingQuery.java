package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReceivingQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReceivingQuery extends SPosBasicService<DCP_ReceivingQueryReq, DCP_ReceivingQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReceivingQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReceivingQueryReq> getRequestType() {
        return new TypeToken<DCP_ReceivingQueryReq>() {

        };
    }

    @Override
    protected DCP_ReceivingQueryRes getResponseType() {
        return new DCP_ReceivingQueryRes();
    }

    @Override
    protected DCP_ReceivingQueryRes processJson(DCP_ReceivingQueryReq req) throws Exception {
        DCP_ReceivingQueryRes res = this.getResponse();

        int totalRecords = 0;        //总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> queryData = this.doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(queryData)) {

            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            List<String> receivingnos = queryData.stream().map(x -> x.get("RECEIVINGNO").toString()).distinct().collect(Collectors.toList());
            MyCommon cm = new MyCommon();
            StringBuffer sJoinReceivingNo = new StringBuffer("");
            for (String receiveNo : receivingnos) {
                sJoinReceivingNo.append(receiveNo + ",");
            }
            Map<String, String> mapReceivingNo = new HashMap<String, String>();
            mapReceivingNo.put("RECEIVINGNO", sJoinReceivingNo.toString());

            String withasSql_receiving = "";
            withasSql_receiving = cm.getFormatSourceMultiColWith(mapReceivingNo);
            mapReceivingNo = null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("with p AS ( " + withasSql_receiving + ") " +
                    " select * from dcp_receiving_detail a " +
                    " inner join p on p.RECEIVINGNO=a.RECEIVINGNO" +
                    " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "' ");
            List<Map<String, Object>> queryDetailData = this.doQueryData(stringBuilder.toString(), null);

            for (Map<String, Object> map : queryData) {
                List<Map<String, Object>> collect = queryDetailData.stream().filter(x -> x.get("RECEIVINGNO").toString().equals(map.get("RECEIVINGNO").toString())).collect(Collectors.toList());
                BigDecimal totCqty;
                BigDecimal totPqty = new BigDecimal(0);
                BigDecimal totDistriAmt = new BigDecimal(0);
                BigDecimal totReceiptCqty = new BigDecimal(0);
                BigDecimal totReceiptPqty = new BigDecimal(0);

                List<String> plunos = collect.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());
                totCqty = new BigDecimal(plunos.size());
                for (Map<String, Object> co : collect) {
                    String poqty = co.get("POQTY").toString();//申请
                    String pqty = co.get("PQTY").toString();
                    String stockin_qty = co.get("STOCKIN_QTY").toString();//已收
                    String distriamt = Check.Null(co.get("DISTRIAMT").toString())?"0":co.get("DISTRIAMT").toString();
                    BigDecimal poqtybs = new BigDecimal(poqty.length() <= 0 ? "0" : poqty);
                    BigDecimal pqtybs = new BigDecimal(pqty.length() <= 0 ? "0" : pqty);
                    BigDecimal skqtybs = new BigDecimal(stockin_qty.length() <= 0 ? "0" : stockin_qty);

                    totPqty = totPqty.add(pqtybs);
                    totReceiptPqty = totReceiptPqty.add(skqtybs);
                    totDistriAmt = totDistriAmt.add(new BigDecimal(distriamt));

                    if (poqtybs.compareTo(skqtybs) <= 0) {
                        totReceiptCqty = totReceiptCqty.add(new BigDecimal("1"));
                    }

                }

                DCP_ReceivingQueryRes.Data data = res.new Data();
                data.setStatus(map.get("STATUS").toString());
                data.setOrgNo(map.get("ORGANIZATIONNO").toString());
                data.setOrgName(map.get("ORGNAME").toString());
                data.setReceivingNo(map.get("RECEIVINGNO").toString());
                data.setReceiptOrgNo(map.get("RECEIPTORGNO").toString());
                data.setReceiptOrgName(map.get("RECEIPTORGNAME").toString());
                data.setBDate(map.get("BDATE").toString());
                data.setSupplierNo(map.get("SUPPLIER").toString());
                data.setSupplierName(map.get("SUPPLIERNAME").toString());
                data.setReceiptDate(map.get("RECEIPTDATE").toString());
                data.setLoadDocNo(StringUtils.toString(map.get("LOAD_DOCNO"), ""));
                data.setLoadDocType(StringUtils.toString(map.get("LOAD_DOCTYPE"), ""));
                data.setPurType(StringUtils.toString(map.get("PURTYPE"), ""));
                data.setExpireDate(map.get("EXPIREDATE").toString());
                data.setWareHouse(map.get("WAREHOUSE").toString());
                data.setWareHouseName(map.get("WAREHOUSENAME").toString());
//                data.setTotCqty(totCqty.toString());
//                data.setTotPqty(totPqty.toString());
                data.setTotCqty(map.get("TOT_CQTY").toString());
                data.setTotPqty(map.get("TOT_PQTY").toString());
                data.setTotDistriAmt(map.get("TOT_DISTRIAMT").toString());
                data.setTotAmt(map.get("TOT_AMT").toString());

                data.setTotReceiptCqty(StringUtils.toString(map.get("TOT_RECEIPTCQTY"),"0"));
                data.setTotReceiptPqty(StringUtils.toString(map.get("TOT_RECEIPTPQTY"),"0"));
                data.setTotReceiptDistriAmt(StringUtils.toString(map.get("TOT_RECEIPTDISTRIAMT"),"0"));
                data.setTotReceiptAmt(StringUtils.toString(map.get("TOT_RECEIPTAMT"),"0"));

                data.setEmployeeID(map.get("EMPLOYEEID").toString());
                data.setEmployeeName(map.get("EMPLOYEENAME").toString());
                data.setDepartID(map.get("DEPARTID").toString());
                data.setDepartName(map.get("DEPARTNAME").toString());
                data.setCreateBy(map.get("CREATEBY").toString());
                data.setCreateByName(map.get("CREATEBYNAME").toString());
                data.setCreateDate(map.get("CREATE_DATE").toString());
                data.setCreateTime(map.get("CREATE_TIME").toString());
                data.setModifyBy(map.get("MODIFYBY").toString());
                data.setModifyByName(map.get("MODIFYBYNAME").toString());
                data.setModifyDate(map.get("MODIFY_DATE").toString());
                data.setModifyTime(map.get("MODIFY_TIME").toString());
                data.setConfirmBy(map.get("CONFIRMBY").toString());
                data.setConfirmByName(map.get("CONFIRMBYNAME").toString());
                data.setConfirmDate(map.get("CONFIRM_DATE").toString());
                data.setConfirmTime(map.get("CONFIRM_TIME").toString());
                data.setCancelBy(map.get("CANCELBY").toString());
                data.setCancelByName(map.get("CANCELBYNAME").toString());
                data.setCancelDate(map.get("CANCEL_DATE").toString());
                data.setCancelTime(map.get("CANCEL_TIME").toString());
                data.setOwnOpID(map.get("OWNOPID").toString());
                data.setOwnOpName(map.get("OWNOPNAME").toString());
                data.setOwnDeptID(map.get("OWNDEPTID").toString());
                data.setOwnDeptName(map.get("OWNDEPTNAME").toString());

                data.setReceiptAddress(map.get("RECEIPTADDRESS").toString());
                data.setCloseOpId(map.get("CLOSEOPID").toString());
                data.setCloseOpName(map.get("CLOSEOPNAME").toString());
                data.setCloseTime(map.get("CLOSETIMES").toString());

                data.setOfNo(map.get("OFNO").toString());
                data.setDocType(map.get("DOC_TYPE").toString());
                data.setOType(map.get("OTYPE").toString());
                data.setCustomer(StringUtils.toString(map.get("CUSTOMER"), ""));
                data.setCustomerName(StringUtils.toString(map.get("CUSTOMERNAME"), ""));
                data.setMemo(StringUtils.toString(map.get("MEMO"), ""));
                data.setDeliveryNo(StringUtils.toString(map.get("DELIVERY_NO"), ""));
                data.setIsLocation(map.get("ISLOCATION").toString());
                data.setTransferShop(map.get("TRANSFERSHOP").toString());
                data.setTransferShopName(StringUtils.toString(map.get("TRANSFERSHOPNAME"), ""));
                data.setPackingNo(map.get("PACKINGNO").toString());
                data.setPTemplateNo(map.get("PTEMPLATENO").toString());
                data.setPTemplateName(map.get("PTEMPLATENAME").toString());

                res.getDatas().add(data);
            }
        }
        res.setSuccess(true);
        res.setServiceStatus("000");
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
    protected String getQuerySql(DCP_ReceivingQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        querySql.append(" select * from (");

        querySql.append(
                " SELECT COUNT(DISTINCT RECEIVINGNO ) OVER() NUM ,dense_rank() over(ORDER BY BDATE DESC,RECEIVINGNO DESC) rn, " +
                        " temp.* FROM(" +
                        "   SELECT a.*,c.org_name as orgname,d.org_name as receiptOrgName,e.sname as suppliername,f.warehouse_name as warehousename," +
                        "   em2.name as employeename,dd2.DEPARTNAME as departName,em1.op_name as createbyname,em6.op_name as modifybyname," +
                        "   em3.op_name as confirmbyname,em4.op_name as cancelbyname, " +
                        "   em0.op_name as ownopname,dd0.DEPARTNAME as ownDeptName,b.EXPIREDATE,em7.op_name as closeopname,  " +
                        "   g.sname CUSTOMERNAME,h.ISLOCATION,b.PURTYPE," +
                        "   a.TRANSFER_SHOP as transfershop,i.org_name as transfershopname," +
                        "  sb.TOT_RECEIPTCQTY,sb.TOT_RECEIPTPQTY,sb.TOT_RECEIPTDISTRIAMT,sb.TOT_RECEIPTAMT,j.PTEMPLATE_NAME as ptemplatename,to_char(a.closetime,'yyyy-MM-dd HH:mm:ss') as closetimes " +
                        "   FROM DCP_RECEIVING a" +
                        "   left join ( " +
                        "       SELECT SHOPID,RECEIVINGNO,ORGANIZATIONNO,EID, " +
                        "       COUNT(CASE WHEN POQTY-STOCKIN_QTY <= 0 THEN 1 END) AS TOT_RECEIPTCQTY, " +
                        "       SUM(NVL(STOCKIN_QTY,0)) TOT_RECEIPTPQTY, " +
                        "       SUM(CASE WHEN POQTY-STOCKIN_QTY <= 0 THEN DISTRIAMT END) as TOT_RECEIPTDISTRIAMT, " +
                        "       SUM(PRICE * STOCKIN_QTY ) as TOT_RECEIPTAMT " +
                        "       FROM DCP_RECEIVING_DETAIL " +
                        "       GROUP BY SHOPID,RECEIVINGNO,ORGANIZATIONNO,EID) sb on sb.eid=a.eid and sb.SHOPID=a.SHOPID and a.RECEIVINGNO=sb.RECEIVINGNO and a.ORGANIZATIONNO=sb.ORGANIZATIONNO " +
                        "   LEFT JOIN DCP_PURORDER b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO  and a.ofNo=b.purorderno " +
                        "   left join dcp_org_lang c on c.eid=a.eid and c.ORGANIZATIONNO=a.ORGANIZATIONNO and c.lang_type='" + req.getLangType() + "'   " +
                        "   left join dcp_org_lang d on d.eid=a.eid and d.ORGANIZATIONNO=a.RECEIPTORGNO and d.lang_type='" + req.getLangType() + "'   " +
                        "   left join DCP_BIZPARTNER e on e.eid=a.eid and e.BIZPARTNERNO=a.supplier " +
                        "   left join dcp_warehouse_lang f on f.eid=a.eid and f.warehouse=a.warehouse and f.lang_type='" + req.getLangType() + "' " +
                        "   left join DCP_BIZPARTNER g on g.eid=a.eid and g.BIZPARTNERNO=a.CUSTOMER " +
                        "   left join dcp_warehouse h on h.eid=a.eid and h.warehouse=a.warehouse " +
                        "   left join platform_staffs_lang em0 on em0.eid=a.eid and em0.opno=a.OWNOPID and em0.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em1 on em1.eid=a.eid and em1.opno=a.CREATEBY and em1.lang_type='"+req.getLangType()+"' " +
                        "   left join DCP_employee em2 on em2.eid=a.eid and em2.employeeno=a.EMPLOYEEID " +
                        "   left join platform_staffs_lang em3 on em3.eid=a.eid and em3.opno=a.CONFIRMBY and em3.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em4 on em4.eid=a.eid and em4.opno=a.CANCELBY and em4.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em6 on em6.eid=a.eid and em6.opno=a.MODIFYBY and em6.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em7 on em7.eid=a.eid and em7.opno=a.CLOSEOPID and em7.lang_type='"+req.getLangType()+"' " +
                        "   left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.OWNDEPTID and dd0.lang_type='" + req.getLangType() + "'  " +
                        "   left join dcp_org_lang i on i.eid=a.eid and i.organizationno=a.TRANSFER_SHOP and i.lang_type='"+req.getLangType()+"'" +
                        "   left join dcp_department_lang dd2 on dd2.eid=a.eid and dd2.departno=a.DEPARTID and dd2.lang_type='" + req.getLangType() + "' " +
                        "   left join DCP_PTEMPLATE j on j.eid=a.eid and j.ptemplateno=a.ptemplateno " +
                        "   where a.eid='" + req.geteId() + "' "

        );

        if (CollectionUtils.isNotEmpty(req.getRequest().getDocType())) {
            querySql.append(" and ( 1=2 ");
            for (String s : req.getRequest().getDocType()) {
                querySql.append(" or a.DOC_TYPE='").append(s).append("'");
            }
            querySql.append(")");
        }

        if (StringUtils.isEmpty(req.getRequest().getGetType())||"0".equals(req.getRequest().getGetType())) {
            querySql.append(" and a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");
        } else {
            querySql.append(" and a.RECEIPTORGNO='").append(req.getOrganizationNO()).append("'");
        }

        if (!Check.Null(req.getRequest().getStatus())) {
            querySql.append("and a.status='" + req.getRequest().getStatus() + "' ");
        }
        if (!Check.Null(req.getRequest().getSupplierNo())) {
            querySql.append("and a.supplier='" + req.getRequest().getSupplierNo() + "' ");
        }
        if (!Check.Null(req.getRequest().getDateType())) {
            String dateType = req.getRequest().getDateType();
            if ("bDate".equals(dateType)) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and a.bdate>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and a.bdate<='" + req.getRequest().getEndDate() + "' ");
                }
            }
            if ("receiptDate".equals(dateType)) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and a.receiptDate>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and a.receiptDate<='" + req.getRequest().getEndDate() + "' ");
                }
            }

        }

        if (!Check.Null(req.getRequest().getKeyTxt())) {
            querySql.append("and (a.receivingno like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or a.supplier like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or e.sname like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or a.ofno like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " ) ");
        }

        querySql.append(") temp  ");

        querySql.append(" ORDER BY BDATE DESC,RECEIVINGNO DESC "
                + " ) where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + "  "
                + " ");


        return querySql.toString();
    }
}
