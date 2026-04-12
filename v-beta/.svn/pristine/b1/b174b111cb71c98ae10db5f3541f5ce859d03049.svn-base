package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PStockOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_PStockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_PStockOutQuery extends SPosBasicService<DCP_PStockOutQueryReq, DCP_PStockOutQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PStockOutQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();

        if(Check.Null(beginDate)){
            errMsg.append("开始日期不可为空值, ");
            isFail = true;
        }

        if(Check.Null(endDate)){
            errMsg.append("截止日期不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_PStockOutQueryReq> getRequestType() {
        return new TypeToken<DCP_PStockOutQueryReq>(){};
    }

    @Override
    protected DCP_PStockOutQueryRes getResponseType() {
        return new DCP_PStockOutQueryRes();
    }

    @Override
    protected DCP_PStockOutQueryRes processJson(DCP_PStockOutQueryReq req) throws Exception {
        String docType = req.getRequest().getDocType(); //0-完工入库  1-组合单   2-拆解单   3-转换合并   4-合并转换
        //try {
        int totalRecords = 0;	//总笔数
        int totalPages = 0;		//总页数
        //查询资料
        DCP_PStockOutQueryRes res = this.getResponse();
        //单头单身查询
        String sql = this.getQuerySql(req);				//查询完工入库单头和商品明细、原料子表   BY JZMA 20190730
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());
        if (getQData != null && !getQData.isEmpty()) {

            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            // 有資料，取得詳細內容
            //单头主键字段
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("DNO", true);

            //调用过滤函数
            List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQData, condition);
            for (Map<String, Object> oneData : getQHeader) {
                DCP_PStockOutQueryRes.level1Elm oneLv1 = res.new level1Elm();

                // 取得第一層資料庫搜尋結果
                String doNO = oneData.get("DNO").toString();
                String processERPNo = oneData.get("PROCESSERPNO").toString();
                String pStockInNO = oneData.get("PSTOCKINNO").toString();
                String bDate = oneData.get("BDATE").toString();
                String memo = oneData.get("MEMO").toString();
                String status2 = oneData.get("STATUS").toString();
                String oType = oneData.get("OTYPE").toString();
                String ofNO = oneData.get("OFNO").toString();
                String loadDocType = oneData.get("LOADDOCTYPE").toString();
                String loadDocNO = oneData.get("LOADDOCNO").toString();
                String createBy = oneData.get("CREATEBY").toString();
                String createDate = oneData.get("CREATEDATE").toString();
                String createTime = oneData.get("CREATETIME").toString();
                String createByName = oneData.get("CREATEBYNAME").toString();

                String confirmBy = oneData.get("CONFIRMBY").toString();
                String confirmDate = oneData.get("CONFIRMDATE").toString();
                String confirmTime = oneData.get("CONFIRMTIME").toString();
                String confirmByName = oneData.get("CONFIRMBYNAME").toString();

                String accountBy = oneData.get("ACCOUNTBY").toString();
                String accountDate = oneData.get("ACCOUNTDATE").toString();
                String accountTime = oneData.get("ACCOUNTTIME").toString();
                String accountByName = oneData.get("ACCOUNTBYNAME").toString();

                String cancelBy = oneData.get("CANCELBY").toString();
                String cancelDate = oneData.get("CANCELDATE").toString();
                String cancelTime = oneData.get("CANCELTIME").toString();
                String cancelByName = oneData.get("CANCELBYNAME").toString();

                String modifyBy = oneData.get("MODIFYBY").toString();
                String modifyDate = oneData.get("MODIFYDATE").toString();
                String modifyTime = oneData.get("MODIFYTIME").toString();
                String modifyByName = oneData.get("MODIFYBYNAME").toString();

                String submitBy = oneData.get("SUBMITBY").toString();
                String submitDate = oneData.get("SUBMITDATE").toString();
                String submitTime = oneData.get("SUBMITTIME").toString();
                String submitByName = oneData.get("SUBMITBYNAME").toString();

                String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                String warehouse = oneData.get("WAREHOUSE").toString();
                String warehouseName = oneData.get("WAREHOUSENAME").toString();


                String docType2 = "";
                if(oneData.get("DOC_TYPE")!=null){
                    docType2 = oneData.get("DOC_TYPE").toString();
                }

                String totPqty = oneData.get("TOTPQTY").toString();
                String totAmt = oneData.get("TOTAMT").toString();
                String totCqty = oneData.get("TOTCQTY").toString();
                String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();

                String refundStatus = oneData.get("REFUNDSTATUS").toString();
                String pStockInNO_refund = oneData.get("PSTOCKINNO_REFUND").toString();
                String pStockInNO_origin = oneData.get("PSTOCKINNO_ORIGIN").toString();

                oneLv1.setRefundStatus(refundStatus);
                oneLv1.setPStockInNo_refund(pStockInNO_refund);
                oneLv1.setPStockInNo_origin(pStockInNO_origin);

                // 處理調整回傳值；
                oneLv1.setPStockInNo(pStockInNO);
                oneLv1.setProcessERPNo(processERPNo);
                oneLv1.setBDate(bDate);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status2);
                oneLv1.setOType(oType);
                oneLv1.setOfNo(ofNO);
                oneLv1.setLoadDocType(loadDocType);
                oneLv1.setLoadDocNo(loadDocNO);
                oneLv1.setPTemplateNo(pTemplateNO);
                oneLv1.setPTemplateName(pTemplateName);
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);

                //oneLv1.setMaterialWarehouseNo(materialWarehouseNO);
                //oneLv1.setMaterialWarehouseName(materialWarehouseName);

                oneLv1.setCreateBy(createBy);
                oneLv1.setCreateDate(createDate);
                oneLv1.setCreateTime(createTime);
                oneLv1.setCreateByName(createByName);

                oneLv1.setAccountBy(accountBy);
                oneLv1.setAccountDate(accountDate);
                oneLv1.setAccountTime(accountTime);
                oneLv1.setAccountByName(accountByName);

                oneLv1.setConfirmBy(confirmBy);
                oneLv1.setConfirmDate(confirmDate);
                oneLv1.setConfirmTime(confirmTime);
                oneLv1.setConfirmByName(confirmByName);


                oneLv1.setCancelBy(cancelBy);
                oneLv1.setCancelDate(cancelDate);
                oneLv1.setCancelTime(cancelTime);
                oneLv1.setCancelByName(cancelByName);

                oneLv1.setModifyBy(modifyBy);
                oneLv1.setModifyDate(modifyDate);
                oneLv1.setModifyTime(modifyTime);
                oneLv1.setModifyByName(modifyByName);

                oneLv1.setSubmitBy(submitBy);
                oneLv1.setSubmitDate(submitDate);
                oneLv1.setSubmitTime(submitTime);
                oneLv1.setSubmitByName(submitByName);

                oneLv1.setDocType(docType2);

                BigDecimal totPqty_b = new BigDecimal("0");
                if (!Check.Null(totPqty)) {
                    totPqty_b = new BigDecimal(totPqty);
                }

                oneLv1.setTotPqty(totPqty_b.toPlainString());
                oneLv1.setTotAmt(totAmt);
                oneLv1.setTotCqty(totCqty);
                oneLv1.setTotDistriAmt(totDistriAmt);
                oneLv1.setProcessPlanNo(oneData.get("PROCESSPLANNO").toString());
                oneLv1.setTask0No(oneData.get("TASK0NO").toString());
                oneLv1.setDtNo(oneData.get("DTNO").toString());
                oneLv1.setDtName(oneData.get("DTNAME").toString());
                oneLv1.setDtBeginTime(oneData.get("BEGIN_TIME").toString());
                oneLv1.setDtEndTime(oneData.get("END_TIME").toString());

                oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                oneLv1.setDepartId(oneData.get("DEPARTID").toString());
                oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
                oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());


                String UPDATE_TIME;
                SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty()) {
                    UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
                } else {
                    UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
                }
                oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
                oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());

                //				String sql2 = this.getQuerySql_MaterialDetail(req,pStockInNO );				//查询完工入库原料明细
                //				String[] conditionValues2 = {}; //查詢條件
                //				List<Map<String, Object>> getQData2 = this.doQueryData(sql2,conditionValues2);

                res.getDatas().add(oneLv1);
            }
        }


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
    protected String getQuerySql(DCP_PStockOutQueryReq req) throws Exception {

        StringBuffer sqlbuf = new StringBuffer();
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();

        String docType = req.getRequest().getDocType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);

        sqlbuf.append(" "
                + " with p1 as ("
                + " select num,dno from ("
                + " select count(*) over () as num,row_number() over (order by rw asc,status asc,bdate desc,dno desc) as rn,a.*"
                + " from ( " );

        sqlbuf.append(" "
                + " select 2 as rw,pstockinno as dno,a.status,to_number(bdate) as bdate,a.eid,a.shopid from dcp_pstockin a"
                + " left join dcp_ptemplate c on a.eid=c.eid and a.ptemplateno=c.ptemplateno and c.doc_type='2'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.account_date between "+beginDate +" and "+endDate+" and a.doc_type='"+docType+"'");
        if (status != null && status.length()!=0) {
            sqlbuf.append(" and a.status = '"+ status +"'  ");
        }
        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" "
                    + " and (a.tot_amt like '%%"+keyTxt+"%%' or a.tot_pqty like '%%"+keyTxt+"%%'"
                    + " or a.pstockinno like '%%"+keyTxt+"%%'  or a.ptemplateno like '%%"+keyTxt+"%%' or c.ptemplate_name like '%%"+keyTxt+"%%' "
                    + " or a.process_erp_no like '%%"+keyTxt+"%%' )");
        }
        sqlbuf.append(" )a");
        sqlbuf.append(" ) where rn >" + startRow + " and rn <= "+(startRow+pageSize)+")");

        //with as p1表
        sqlbuf.append(""
                + " select p1.num,a.*,w1.warehouse_name as warehousename,"
                + " j.ptemplate_name as ptemplatename,"
                + " f.op_name as createbyname,"
                + " f1.op_name as modifybyname,f2.op_name as cancelbyname,f3.op_name as confirmbyname,f4.op_name as submitbyname,"
                + " f5.op_name as accountbyname,e1.name as employeename,d1.DEPARTNAME as departname,d2.DEPARTNAME as CREATEDEPTNAME "

                + " from ("
                + " ");
        //docType 传值0的时候需要加上这些数据
        //docType 传值1或2的时候不需要加工任务的查询,也就是select 1 as rw......

        sqlbuf.append("SELECT 2 as rw,A.Refundstatus,"
                + " A.Pstockinno_Refund,A.Pstockinno_Origin,A.EID,A.ORGANIZATIONNO,a.pstockinno as dno,  a.process_erp_NO AS processERPNO,"
                + " A. PSTOCKINNO,A.SHOPID as SHOPID,");


        sqlbuf.append(" to_number(a.bDate) bdate,A.memo,A.status, A.oType  "
                + " ,A.ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy "
                + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,a.pTemplateNO "
                + " ,A.WAREHOUSE "
                + " ,N'' materialWarehouse,a.doc_Type "
                + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime, a.modify_Date  AS modifyDate,"
                + " a.modify_time AS modifyTime,a.modifyby , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                + " a.accountby, nvl(a.account_date,'"+sDate+"') AS accountDate, a.account_time AS accountTime , "
                + " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime "
                + " ,a.UPDATE_TIME,a.PROCESS_STATUS, "
                + " A.PROCESSPLANNO,A.TASK0NO,A.DTNO, "
                + " dt1.dtname,dt1.begin_time,dt1.end_time,a.employeeid,a.departid,a.CREATEDEPTID  "
                + " FROM DCP_PSTOCKIN A  "
                + " left join DCP_DINNERTIME dt1 on A.eid=dt1.eid and A.organizationno=dt1.shopid and A.dtno=dt1.dtno "
                + " "
        );

        sqlbuf.append(" WHERE a.account_date between "+beginDate +" and "+endDate+" ");

        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND A.status = '"+ status +"'  ");
        }

        if (!Check.Null(docType)) {
            sqlbuf.append(" AND A.doc_Type = '"+ docType +"'  " );
        }

        sqlbuf.append(" AND a.SHOPID='"+ shopId +"' ");
        sqlbuf.append(" AND a.EID='"+ eId +"'");
        sqlbuf.append(" ) a");
        sqlbuf.append(" inner join p1 on p1.dno=a.dno ");

        sqlbuf.append(""
                + " LEFT JOIN platform_staffs_lang f ON A .EID = f.EID AND A .createby = f.opno  and f.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno  and f1.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f2 ON A .EID = f2.EID AND A .cancelby = f2.opno  and f2.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f3 ON A .EID = f3.EID AND A .confirmby = f3.opno  and f3.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f4 ON A .EID = f4.EID AND A .submitby = f4.opno  and f4.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f5 ON A .EID = f5.EID AND A .accountby = f5.opno and f5.lang_type='"+req.getLangType()+"'  "
                + " LEFT JOIN dcp_warehouse_lang w1  ON A .EID = w1.EID AND A .warehouse = w1.warehouse AND w1.LANG_TYPE = '" + langType + "' "
                + " left join dcp_employee e1 on e1.employeeno=a.employeeid and e1.eid=a.eid  "
                + " left join dcp_department_lang d1 on d1.departno=a.departid and d1.eid=a.eid and d1.lang_type='"+langType+"'"
                + " left join dcp_department_lang d2 on d2.departno=a.CREATEDEPTID and d2.eid=a.eid and d2.lang_type='"+langType+"'"

                + " LEFT JOIN DCP_pTEMPLATE J ON A.EID=J.EID AND A.PTEMPLATENO=J.pTEMPLATENO and J.DOC_TYPE='2' and j.status='100' "
        );

        sqlbuf.append(" order by rw asc,a.status asc,a.bdate desc,a.dno desc ");

        return sqlbuf.toString();

    }

}

