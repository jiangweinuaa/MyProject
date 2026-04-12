package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockInNewQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockInNewQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockInNewQuery extends SPosBasicService<DCP_StockInNewQueryReq, DCP_StockInNewQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockInNewQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockInNewQueryReq.levelElm request = req.getRequest();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String docType= request.getDocType();

        if (Check.Null(beginDate)) {
            errMsg.append("开始日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(endDate)) {
            errMsg.append("截止日期不可为空值, ");
            isFail = true;
        }

        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockInNewQueryReq> getRequestType() {
        return new TypeToken<DCP_StockInNewQueryReq>(){};
    }

    @Override
    protected DCP_StockInNewQueryRes getResponseType() {
        return new DCP_StockInNewQueryRes();
    }

    @Override
    protected DCP_StockInNewQueryRes processJson(DCP_StockInNewQueryReq req) throws Exception {
        //取得 SQL
        String sql = null;
        DCP_StockInNewQueryReq.levelElm request = req.getRequest();
        DCP_StockInNewQueryRes res = this.getResponse();

        if(Check.Null(String.valueOf(req.getPageSize()))){
            req.setPageSize(10);
        }
        if(Check.Null(String.valueOf(req.getPageNumber()))){
            req.setPageNumber(1);
        }

        int totalRecords;		//总笔数
        int totalPages;

        //单头
        sql = this.getDnoSql(req);				//查询明细数据

        List<Map<String, Object>> getQData = this.doQueryData(sql,null);

        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            res.setDatas(new ArrayList<>());
            for (Map<String, Object> oneData : getQData) {
                DCP_StockInNewQueryRes.level1Elm oneLv1 = res.new level1Elm();

                // 取得第一層資料庫搜尋結果
                String shop1 = oneData.get("SHOPID").toString();
                String stockInNO = oneData.get("STOCKINNO").toString();
                String processERPNo = oneData.get("PROCESSERPNO").toString();
                String bDate = oneData.get("BDATE").toString();
                String memo = oneData.get("MEMO").toString();
                String bsNO = oneData.get("BSNO").toString();
                String bsName = oneData.get("BSNAME").toString();
                String status1 = oneData.get("STATUS").toString();
                String docType1 = oneData.get("DOCTYPE").toString();
                String transferShop=oneData.get("TRANSFERSHOP").toString();
                String transferShopName=oneData.get("TRANSFERSHOPNAME").toString();
                String oType = oneData.get("OTYPE").toString();
                String ofNO = oneData.get("OFNO").toString();
                String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                String loadDocType = oneData.get("LOADDOCTYPE").toString();
                String loadDocNO = oneData.get("LOADDOCNO").toString();
                String createByName = oneData.get("CREATEBYNAME").toString();
                String diffStatus = oneData.get("DIFFSTATUS").toString();
                String differenceNO = oneData.get("DIFFERENCENO").toString();
                String createBy = oneData.get("CREATEBY").toString();
                String createDate = oneData.get("CREATEDATE").toString();
                String createTime = oneData.get("CREATETIME").toString();
                String submitBy = oneData.get("SUBMITBY").toString();
                String submitDate = oneData.get("SUBMITDATE").toString();
                String submitTime = oneData.get("SUBMITTIME").toString();
                String submitByName = oneData.get("SUBMITBYNAME").toString();
                String cancelBy = oneData.get("CANCELBY").toString();
                String cancelDate = oneData.get("CANCELDATE").toString();
                String cancelTime = oneData.get("CANCELTIME").toString();
                String cancelByName = oneData.get("CANCELBYNAME").toString();
                String confirmBy = oneData.get("CONFIRMBY").toString();
                String confirmDate = oneData.get("CONFIRMDATE").toString();
                String confirmTime = oneData.get("CONFIRMTIME").toString();
                String confirmByName = oneData.get("CONFIRMBYNAME").toString();
                String modifyBy = oneData.get("MODIFYBY").toString();
                String modifyDate = oneData.get("MODIFYDATE").toString();
                String modifyTime = oneData.get("MODIFYTIME").toString();
                String modifyByName = oneData.get("MODIFYNAME").toString();
                String accountBy = oneData.get("ACCOUNTBY").toString();
                String accountDate = oneData.get("ACCOUNTDATE").toString();
                String accountTime = oneData.get("ACCOUNTTIME").toString();
                String accountByName = oneData.get("ACCOUNTBYNAME").toString();
                String totPqty = oneData.get("TOTPQTY").toString();
                String totCqty = oneData.get("TOTCQTY").toString();
                String warehouse     = oneData.get("WAREHOUSE").toString();
                String warehouseName = oneData.get("WAREHOUSENAME").toString();
                String receiptDate   = oneData.get("RECEIPTDATE").toString();
                String deliveryNO = oneData.get("DELIVERY_NO").toString();
                String totAmt = oneData.get("TOTAMT").toString();
                String totDistriAmt = oneData.get("TOTDISTRIAMT").toString();
                String packingNo = oneData.get("PACKINGNO").toString();
                String receiving_rdate = "";//oneData.get("RECEIVING_RDATE").toString();
                String stockinno_origin = oneData.get("STOCKINNO_ORIGIN").toString();
                String stockinno_refund = oneData.get("STOCKINNO_REFUND").toString();
                String invwarehouse = oneData.get("INVWAREHOUSE").toString();
                String invwarehousename = oneData.get("INVWAREHOUSENAME").toString();
                String employeeid = oneData.get("EMPLOYEEID").toString();
                String employeename = oneData.get("EMPLOYEENAME").toString();
                String departid = oneData.get("DEPARTID").toString();
                String departname = oneData.get("DEPARTNAME").toString();
                String is_location = oneData.get("ISLOCATION").toString();
                String reason = oneData.get("REASON").toString();
                String ootype = oneData.get("OOTYPE").toString();
                String oofno = oneData.get("OOFNO").toString();
                String transferwarehouse = oneData.get("TRANSFERWAREHOUSE").toString();
                String transferwarehousename = oneData.get("TRANSFERWAREHOUSENAME").toString();

                // 處理調整回傳值
                oneLv1.setShopId(shop1);
                oneLv1.setStockInNo(stockInNO);
                oneLv1.setProcessERPNo(processERPNo);
                oneLv1.setBDate(bDate);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status1);
                oneLv1.setDocType(docType1);
                oneLv1.setBsNo(bsNO);
                oneLv1.setBsName(bsName);
                oneLv1.setTransferShop(transferShop);
                oneLv1.setTransferShopName(transferShopName);
                oneLv1.setOType(oType);
                oneLv1.setOfNo(ofNO);
                oneLv1.setPTemplateNo(pTemplateNO);
                oneLv1.setPTemplateName(pTemplateName);
                oneLv1.setLoadDocType(loadDocType);
                oneLv1.setLoadDocNo(loadDocNO);
                oneLv1.setCreateByName(createByName);
                oneLv1.setCreateBy(createBy);
                oneLv1.setCreateDate(createDate);
                oneLv1.setCreateTime(createTime);
                oneLv1.setCancelBy(cancelBy);
                oneLv1.setCancelByName(cancelByName);
                oneLv1.setCancelDate(cancelDate);
                oneLv1.setCancelTime(cancelTime);
                oneLv1.setConfirmBy(confirmBy);
                oneLv1.setConfirmByName(confirmByName);
                oneLv1.setConfirmDate(confirmDate);
                oneLv1.setConfirmTime(confirmTime);
                oneLv1.setAccountBy(accountBy);
                oneLv1.setAccountByName(accountByName);
                oneLv1.setAccountDate(accountDate);
                oneLv1.setAccountTime(accountTime);
                oneLv1.setSubmitBy(submitBy);
                oneLv1.setSubmitByName(submitByName);
                oneLv1.setSubmitDate(submitDate);
                oneLv1.setSubmitTime(submitTime);
                oneLv1.setModifyBy(modifyBy);
                oneLv1.setModifyByName(modifyByName);
                oneLv1.setModifyDate(modifyDate);
                oneLv1.setModifyTime(modifyTime);
                oneLv1.setDiffStatus(diffStatus);
                oneLv1.setDifferenceNo(differenceNO);
                oneLv1.setTotPqty(totPqty);
                oneLv1.setTotAmt(totAmt);
                oneLv1.setTotDistriAmt(totDistriAmt);
                oneLv1.setTotCqty(totCqty);
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);

                oneLv1.setInvWarehouse(invwarehouse);
                oneLv1.setInvWarehouseName(invwarehousename);
                oneLv1.setEmployeeId(employeeid);
                oneLv1.setEmployeeName(employeename);
                oneLv1.setDepartId(departid);
                oneLv1.setDepartName(departname);
                oneLv1.setReason(reason);
                oneLv1.setOoType(ootype);
                oneLv1.setOofNo(oofno);
                oneLv1.setIsLocation(is_location);
                oneLv1.setTransferWarehouse(transferwarehouse);
                oneLv1.setTransferWarehouseName(transferwarehousename);

                if (Check.Null(receiving_rdate)){
                    receiving_rdate = oneData.get("RECEIPTDATE").toString();
                }
                oneLv1.setRDate(receiving_rdate);

                oneLv1.setReceiptDate(receiptDate);
                oneLv1.setDeliveryNo(deliveryNO);
                oneLv1.setPackingNo(packingNo);


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
                oneLv1.setStockInNo_origin(stockinno_origin);
                oneLv1.setStockInNo_refund(stockinno_refund);
                oneLv1.setDeliveryBy(oneData.get("DELIVERYBY").toString());
                oneLv1.setDeliveryName(oneData.get("DELIVERYNAME").toString());
                oneLv1.setDeliveryTel(oneData.get("DELIVERYTEL").toString());
                oneLv1.setCorp(oneData.get("CORP").toString());
                oneLv1.setDeliveryCorp(oneData.get("DELIVERYCORP").toString());

                res.getDatas().add(oneLv1);
            }
        } else {
            res.setDatas(new ArrayList<DCP_StockInNewQueryRes.level1Elm>());
            totalRecords = 0;
            totalPages = 0;
        }



        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        //調整查出來的資料
    }

    protected String getDnoSql(DCP_StockInNewQueryReq req) throws Exception {
        DCP_StockInNewQueryReq.levelElm request = req.getRequest();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String docType = request.getDocType();

        String beginDate =request.getBeginDate();
        String endDate =request.getEndDate();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String dateType = request.getDateType();  /// 日期类型  bDate：单据日期（默认）,receiptDate：预计到货日 BY JZMA 20201118

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料


        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");

        sqlbuf.append("" +
                " select case when a.status=0 then 0 else 2 end  as rw,a.shopId,a.stockInNo,a.PROCESS_ERP_NO as processERPNo,a.bdate,a.memo," +
                " a.status,a.doc_type as doctype,a.bsno,b.REASON_NAME  as bsName,a.TRANSFER_SHOP AS transfershop,c.org_name as transfershopname," +
                " a.oType,a.ofNo,a.pTemplateNo,d.PTEMPLATE_NAME as PTEMPLATENAME,a.createby,a.create_date as createdate,a.create_time as createtime,e1.op_name as createbyname," +
                " a.submitby,a.submit_date as submitdate,a.submit_time as submittime,e2.op_name as submitbyname,a.modifyby,a.modify_date as modifydate,a.modify_time as modifytime,e3.op_name as modifyname," +
                " a.confirmby,a.confirm_date as confirmdate,a.confirm_time as confirmtime,e4.op_name as confirmbyname,a.cancelby ,a.cancel_date as canceldate,a.cancel_time as canceltime,e5.op_name as cancelbyname," +
                " a.accountby,a.account_date as accountdate,a.account_time as accounttime,e6.op_name as accountbyname ," +
                " a.LOAD_DOCNO as loaddocno,a.LOAD_DOCtype as loaddoctype,a.tot_pqty as totpqty,a.tot_cqty as totcqty,a.tot_amt totamt,a.warehouse,f.warehouse_name as warehousename," +
                " a.receiptdate AS rdate,a.receiptDate,a.update_time,a.process_status,a.TOT_DISTRIAMT as TOTDISTRIAMT,a.packingNo,a.stockInNo_origin,a.stockInNo_refund,cast('' as nvarchar2(40)) as DELIVERY_NO,a.deliveryby,g.opname as deliveryname,g.phone as deliverytel,a.invWarehouse,f1.warehouse_name as invWarehousename," +
                " a.ootype,a.oofno,a.bsno as reason,a.employeeid,e7.name as employeename,a.departid,h.departname,a.transfer_warehouse as transferwarehouse ,f2.warehouse_name as transferwarehousename,f0.islocation as islocation," +
                " i.differenceno,i.status as diffstatus,a.corp,a.deliverycorp  " +
                " from dcp_stockin a " +
                " left join DCP_REASON_LANG b ON A.EID = b.EID AND A.BSNO=b.BSNO and b.lang_type='"+langType+"' " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.TRANSFER_SHOP and c.lang_type='"+langType+"'"+
                " left join DCP_ptemplate d on a.EID=d.EID and a.ptemplateno=d.ptemplateno and d.doc_type='0' "+
                " left join platform_staffs_lang e1 on a.EID=e1.EID and a.createby=e1.opno and e1.lang_type='"+req.getLangType()+"' "+
                " left join platform_staffs_lang e2 on a.EID=e2.EID and a.submitby=e2.opno and e2.lang_type='"+req.getLangType()+"'  "+
                " left join platform_staffs_lang e3 on a.EID=e3.EID and a.modifyby=e3.opno and e3.lang_type='"+req.getLangType()+"'  "+
                " left join platform_staffs_lang e4 on a.EID=e4.EID and a.confirmby=e4.opno  and e4.lang_type='"+req.getLangType()+"' "+
                " left join platform_staffs_lang e5 on a.EID=e5.EID and a.cancelby=e5.opno and e5.lang_type='"+req.getLangType()+"' "+
                " left join platform_staffs_lang e6 on a.EID=e6.EID and a.accountby=e6.opno and e6.lang_type='"+req.getLangType()+"'  " +
                " left join dcp_employee e7 on a.EID=e7.EID and a.employeeid=e7.employeeno  " +
                " left join dcp_warehouse f0 on f0.eid=a.eid and f0.warehouse=a.warehouse "+
                " left join dcp_warehouse_lang f on f.eid=a.eid and f.warehouse=a.warehouse and f.lang_type='"+langType+"'"+
                " left join dcp_warehouse_lang f1 on f1.eid=a.eid and f1.warehouse=a.invWarehouse and f1.lang_type='"+langType+"'"+
                " left join dcp_warehouse_lang f2 on f2.eid=a.eid and f2.warehouse=a.transfer_Warehouse and f2.lang_type='"+langType+"'"+

                " left join dcp_deliveryman g on a.eid=g.eid and a.deliveryby=g.opno"+
                " left join DCP_DEPARTMENT_LANG h on h.eid=a.eid and h.departno=a.departid and h.lang_type='"+langType+"'"+
                " LEFT JOIN DCP_DIFFERENCE I ON A.EID=I.EID  AND A.STOCKINNO=I.OFNO AND A.SHOPID=I.SHOPID  "+

                " where a.eid='"+eId+"' and a.organizationno='"+ req.getOrganizationNO()+"' " +
                " and a.doc_type='"+docType+"'" +
                " ");



        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND a.status = '"+status+"' ");
        }

        if(!Check.Null(dateType)){
            if(dateType.equals("bDate")){
                sqlbuf.append(" and a.bdate between "+beginDate+" and "+endDate+" ");
            }else{
                sqlbuf.append(" and a.receiptdate between "+beginDate+" and "+endDate+" ");
            }
        }


        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" AND (a.TOT_AMT LIKE '%%"+keyTxt+"%%' "
                    + " OR a.TOT_PQTY LIKE '%%"+keyTxt+"%%' "
                    + " OR a.STOCKINNO LIKE '%%"+keyTxt+"%%' "
                    + " OR a.MEMO LIKE '%%"+keyTxt+"%%' "
                    + " OR a.LOAD_DOCNO LIKE '%%"+keyTxt+"%%'  "
                    + " OR a.PROCESS_ERP_NO like '%%"+ keyTxt +"%%' "
                    + " OR a.TRANSFER_SHOP LIKE '%%"+keyTxt+"%%' "
                    + " OR c.ORG_NAME LIKE '%%"+keyTxt+"%%' "
                    + " OR a.PACKINGNO LIKE '%%"+keyTxt+"%%' "
                    + " )");
        }
        sqlbuf.append(" order by a.STOCKINNO desc ");


        sqlbuf.append(" "
                + " ) ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");


        sql = sqlbuf.toString();

        return sql;
    }

    @Override
    protected String getQuerySql(DCP_StockInNewQueryReq req) throws Exception {
        return null;
    }




}
