package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_LStockOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_LStockOutQuery extends SPosBasicService<DCP_LStockOutQueryReq, DCP_LStockOutQueryRes>
{
    @Override
    protected boolean isVerifyFail(DCP_LStockOutQueryReq req) throws Exception {
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
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_LStockOutQueryReq> getRequestType() {
        return new TypeToken<DCP_LStockOutQueryReq>(){};
    }
    
    @Override
    protected DCP_LStockOutQueryRes getResponseType() {
        return new DCP_LStockOutQueryRes();
    }
    
    @Override
    protected DCP_LStockOutQueryRes processJson(DCP_LStockOutQueryReq req) throws Exception {
        
        //查询条件
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String dateType = req.getRequest().getDateType();
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        //try {
            //查询资料
            DCP_LStockOutQueryRes res = this.getResponse();
            
            //给分页字段赋值
            String sql = this.getQuerySql_Count(req);				  												//查询总笔数
            List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            
            res.setDatas(new ArrayList<>());
            if (getQData_Count != null && !getQData_Count.isEmpty()) {
                String num = getQData_Count.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                //计算起始位置
                int pageSize = req.getPageSize();
                int startRow = ((req.getPageNumber() - 1) * pageSize);
                
                StringBuffer sqlbuf = new StringBuffer();

                sqlbuf.append(""
                        + " SELECT processERPNO,lStockOutNO,bDate,memo,status,totPqty,TOT_DISTRIAMT,LSTOCKOUTNO_ORIGIN,LSTOCKOUTNO_REFUND, "
                        + " totAmt,totCqty, "
                        + " A_Warehouse,A_WarehouseName,"
                        + " PROCESS_STATUS,UPDATE_TIME, "
                        + " CREATEBY,CREATEDATE,CREATETIME,CREATENAME,"
                        + " MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME,"
                        + " SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME,"
                        + " CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME,"
                        + " CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME,"
                        + " ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME, PTEMPLATENO, PTEMPLATENAME,FEEOBJECTTYPE,FEEOBJECTID,FEEOBJECTNAME,FEE,BFEENO,EMPLOYEEID,EMPLOYEENAME,DEPARTID,DEPARTNAME,FEENAME   "
                        + "  from ( "
                        + " SELECT a.process_erp_no as processERPNO, A.lstockoutno,A.bDate,A.memo,A.status, "
                        + " A.tot_pqty AS totPqty,A.tot_amt AS totAmt,A.tot_cqty AS totCqty,A.TOT_DISTRIAMT,A.LSTOCKOUTNO_ORIGIN,A.LSTOCKOUTNO_REFUND,"

                        + " A.Warehouse as A_Warehouse,W1.Warehouse_Name as A_WarehouseName,"
                        + " A.PROCESS_STATUS,A.UPDATE_TIME, "
                        + " A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,e3.op_NAME as CREATENAME,"
                        + " A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,e4.op_name as MODIFYNAME,"
                        + " A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,e5.op_name as SUBMITNAME,"
                        + " A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,e6.op_name as CONFIRMNAME,"
                        + " A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,e7.op_name as CANCELNAME,"
                        + " A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,e2.op_name as ACCOUNTNAME, "
                        + " A.PTEMPLATENO , p.PTEMPLATE_NAME as PTEMPLATENAME, "
                        + " a.feeObjectType,a.feeObjectId,a.fee,a.BFEENO,a.employeeId,a.departId, "
                        + " e1.name as employeeName,d1.departname,case when a.feeobjecttype='1' then d2.departname else b1.sname end as feeobjectname,fee.fee_name as feename  "
                        + " FROM DCP_LSTOCKOUT A "
                        + " LEFT JOIN DCP_PTEMPLATE p ON A.EID=p.EID AND A.PTEMPLATENO=p.PTEMPLATENO AND p.DOC_TYPE='4' "
                        + " LEFT JOIN DCP_WAREHOUSE_LANG W1 ON A.EID = W1.EID AND A.organizationno=W1.organizationno AND A.WAREHOUSE=W1.WAREHOUSE AND W1.LANG_TYPE='"+langType+"' "
                        + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid "
                        + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"' "
                        + " left join dcp_department_lang d2 on d2.eid=a.eid and d2.departno=a.feeObjectId and d2.lang_type='"+langType+"' "
                        + " left join DCP_BIZPARTNER b1 on b1.eid=a.eid and b1.BIZPARTNERNO=a.feeobjectid "
                        + " left join DCP_FEE_LANG fee on fee.eid=a.eid and fee.fee=a.fee and fee.lang_type='"+langType+"' "

                        + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.ACCOUNTBY and e2.lang_type='"+langType+"' "
                        + " left join platform_staffs_lang e3 on e3.eid=a.eid and e3.opno=a.CREATEBY and e3.lang_type='"+langType+"' "
                        + " left join platform_staffs_lang e4 on e4.eid=a.eid and e4.opno=a.MODIFYBY and e4.lang_type='"+langType+"' "
                        + " left join platform_staffs_lang e5 on e5.eid=a.eid and e5.opno=a.SUBMITBY and e5.lang_type='"+langType+"' "
                        + " left join platform_staffs_lang e6 on e6.eid=a.eid and e6.opno=a.CONFIRMBY and e6.lang_type='"+langType+"'  "
                        + " left join platform_staffs_lang e7 on e7.eid=a.eid and e7.opno=a.CANCELBY and e7.lang_type='"+langType+"' "

                );
                //2018-11-09 添加 日期查询条件
                if("bDate".equals(dateType)){
                    sqlbuf.append(" WHERE a.BDATE between "+beginDate +" and "+endDate+" ");
                }else if("accountDate".equals(dateType)){
                    sqlbuf.append(" WHERE a.ACCOUNT_DATE between "+beginDate +" and "+endDate+" ");
                }else{
                    sqlbuf.append( " where 1=1 ");
                }
                sqlbuf.append(" and a.SHOPID='"+shopId+"'");
                sqlbuf.append(" and a.EID='"+eId+"'");
                
                sqlbuf.append(" and a.LSTOCKOUTNO in ( "
                        + " SELECT lstockoutno  FROM ( "
                        + " SELECT lstockoutno,ROWNUM rn FROM ( "
                        + " select lstockoutno FROM DCP_lstockout "
                        + " WHERE BDATE between "+beginDate +" and "+endDate+" "
                );
                
                //报损单状态调整  BY JZMA 20190124
                //0.新建，1.已提交，2.待签收，4.全部签收，5.部分签收，6.全部驳回  7.终止上传
                if (status != null && status.length() > 0)
                {
                    switch (status) {
                        case "1":
                            sqlbuf.append(" AND STATUS='1'  ");
                            break;
                        case "2":
                            sqlbuf.append(" AND STATUS='2'  ");
                            break;
                        case "7":
                            sqlbuf.append(" AND STATUS='7'  ");
                            break;
                        default:
                            sqlbuf.append(" AND STATUS='" + status + "' ");
                            break;
                    }
                }
                if(keyTxt != null && keyTxt.length() > 0)
                {
                    sqlbuf.append(" and (TOT_AMT LIKE '%%"+keyTxt+"%%' OR TOT_PQTY LIKE '%%"+keyTxt+"%%' OR LSTOCKOUTNO LIKE '%%"+keyTxt+"%%' OR MEMO LIKE '%%"+keyTxt+"%%' "
                            + " or process_ERP_No like '%%"+keyTxt+"%%'  )");
                }
                sqlbuf.append(" and SHOPID='"+shopId+"'");
                sqlbuf.append(" and EID='"+eId+"'");
                sqlbuf.append(" ORDER BY status ASC,bdate DESC,lstockoutno DESC))");
                sqlbuf.append(" where rn>" + startRow + " AND rn <= " + (startRow+pageSize) );
                sqlbuf.append(" )) ORDER BY status ASC,bdate DESC,lstockoutno DESC");
                sql = sqlbuf.toString();
                
                List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
                if (getQDataDetail != null && !getQDataDetail.isEmpty()){
                    
                    //单头主键字段
                    Map<String, Boolean> condition = new HashMap<>(); //查询条件
                    condition.put("LSTOCKOUTNO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
                    
                    //图片主键字段
                    //condition.clear();
                    //condition.put("LSTOCKOUTNO", true);
                    //condition.put("FILENAME", true);
                    //调用过滤函数
                    //List<Map<String, Object>> getQFileList = MapDistinct.getMap(getQDataDetail, condition);
                    
                    //图片主键字段
                    //condition.clear();
                    //condition.put("LSTOCKOUTNO", true);
                    //condition.put("ITEM", true);
                    //调用过滤函数
                    ///getQDataDetail = MapDistinct.getMap(getQDataDetail, condition);
                    
                    for (Map<String, Object> oneData : getQHeader) {
                        DCP_LStockOutQueryRes.level1Elm oneLv1 = new DCP_LStockOutQueryRes().new level1Elm();
                        //取出第一层
                        String processERPNO = oneData.get("PROCESSERPNO").toString();
                        String lStockOutNO = oneData.get("LSTOCKOUTNO").toString();
                        String bDate = oneData.get("BDATE").toString();
                        String memo = oneData.get("MEMO").toString();
                        String a_status = oneData.get("STATUS").toString();
                        String warehouse = oneData.get("A_WAREHOUSE").toString();
                        String warehouseName = oneData.get("A_WAREHOUSENAME").toString();
                        String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                        String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                        String createBy =oneData.get("CREATEBY").toString();
                        String createByName =oneData.get("CREATENAME").toString();
                        String createDate =oneData.get("CREATEDATE").toString();
                        String createTime =oneData.get("CREATETIME").toString();
                        String modifyBy =oneData.get("MODIFYBY").toString();
                        String modifyByName =oneData.get("MODIFYNAME").toString();
                        String modifyDate =oneData.get("MODIFYDATE").toString();
                        String modifyTime =oneData.get("MODIFYTIME").toString();
                        String submitBy =oneData.get("SUBMITBY").toString();
                        String submitByName =oneData.get("SUBMITNAME").toString();
                        String submitDate =oneData.get("SUBMITDATE").toString();
                        String submitTime =oneData.get("SUBMITTIME").toString();
                        String confirmBy =oneData.get("CONFIRMBY").toString();
                        String confirmByName =oneData.get("CONFIRMNAME").toString();
                        String confirmDate =oneData.get("CONFIRMDATE").toString();
                        String confirmTime =oneData.get("CONFIRMTIME").toString();
                        String cancelBy =oneData.get("CANCELBY").toString();
                        String cancelByName =oneData.get("CANCELNAME").toString();
                        String cancelDate =oneData.get("CANCELDATE").toString();
                        String cancelTime =oneData.get("CANCELTIME").toString();
                        String accountBy =oneData.get("ACCOUNTBY").toString();
                        String accountByName =oneData.get("ACCOUNTNAME").toString();
                        String accountDate =oneData.get("ACCOUNTDATE").toString();
                        String accountTime =oneData.get("ACCOUNTTIME").toString();
                        String totPqty = oneData.get("TOTPQTY").toString();
                        String totAmt = oneData.get("TOTAMT").toString();
                        String totCqty = oneData.get("TOTCQTY").toString();
                        String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                        String lstockoutno_origin = oneData.get("LSTOCKOUTNO_ORIGIN").toString();
                        String lstockoutno_refund = oneData.get("LSTOCKOUTNO_REFUND").toString();

                        String feeObjectType = oneData.get("FEEOBJECTTYPE").toString();
                        String feeObjectId = oneData.get("FEEOBJECTID").toString();
                        String feeObjectName = oneData.get("FEEOBJECTNAME").toString();
                        String fee = oneData.get("FEE").toString();
                        String feeName = oneData.get("FEENAME").toString();
                        String feeBillNo = oneData.get("BFEENO").toString();
                        String employeeId = oneData.get("EMPLOYEEID").toString();
                        String employeeName = oneData.get("EMPLOYEENAME").toString();
                        String departId = oneData.get("DEPARTID").toString();
                        String departName = oneData.get("DEPARTNAME").toString();


                        //设置响应
                        oneLv1.setProcessERPNo(processERPNO);
                        oneLv1.setlStockOutNo(lStockOutNO);
                        oneLv1.setbDate(bDate);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(a_status);
                        oneLv1.setWarehouse(warehouse);
                        oneLv1.setWarehouseName(warehouseName);
                        oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                        oneLv1.setpTemplateNo(pTemplateNO);
                        oneLv1.setpTemplateName(pTemplateName);
                        oneLv1.setCreateBy(createBy);
                        oneLv1.setCreateByName(createByName);
                        oneLv1.setCreateDate(createDate);
                        oneLv1.setCreateTime(createTime);
                        oneLv1.setModifyBy(modifyBy);
                        oneLv1.setModifyByName(modifyByName);
                        oneLv1.setModifyDate(modifyDate);
                        oneLv1.setModifyTime(modifyTime);
                        oneLv1.setSubmitBy(submitBy);
                        oneLv1.setSubmitByName(submitByName);
                        oneLv1.setSubmitDate(submitDate);
                        oneLv1.setSubmitTime(submitTime);
                        oneLv1.setConfirmBy(confirmBy);
                        oneLv1.setConfirmByName(confirmByName);
                        oneLv1.setConfirmDate(confirmDate);
                        oneLv1.setConfirmTime(confirmTime);
                        oneLv1.setCancelBy(cancelBy);
                        oneLv1.setCancelByName(cancelByName);
                        oneLv1.setCancelDate(cancelDate);
                        oneLv1.setCancelTime(cancelTime);
                        oneLv1.setAccountBy(accountBy);
                        oneLv1.setAccountByName(accountByName);
                        oneLv1.setAccountDate(accountDate);
                        oneLv1.setAccountTime(accountTime);
                        oneLv1.setTotPqty(totPqty);
                        oneLv1.setTotAmt(totAmt);
                        oneLv1.setTotCqty(totCqty);
                        oneLv1.setTotDistriAmt(totDistriAmt);
                        oneLv1.setlStockoutNo_origin(lstockoutno_origin);
                        oneLv1.setlStockoutNo_refund(lstockoutno_refund);
                        oneLv1.setFeeObjectType(feeObjectType);
                        oneLv1.setFeeObjectId(feeObjectId);
                        oneLv1.setFeeObjectName(feeObjectName);
                        oneLv1.setFee(fee);
                        oneLv1.setFeeName(feeName);
                        oneLv1.setBeeFeeNo(feeBillNo);
                        oneLv1.setEmployeeId(employeeId);
                        oneLv1.setEmployeeName(employeeName);
                        oneLv1.setDepartId(departId);
                        oneLv1.setDepartName(departName);
                        
                        String UPDATE_TIME;
                        SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty())
                        {
                            UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
                        }
                        else
                        {
                            UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
                        }
                        oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
                        oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());


                        //添加单头
                        res.getDatas().add(oneLv1);
                    }
                }
                else
                {
                    totalRecords = 0;
                    totalPages = 0;
                }
            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
        //}
        //catch (Exception e)
        //{
         //   throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_LStockOutQueryReq req) throws Exception {
        return null;
    }
    
    private String getQuerySql_Count(DCP_LStockOutQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String dateType = req.getRequest().getDateType();

        //2018-11-09 添加 日期查询条件
        sqlbuf.append(" select COUNT(*) AS num from DCP_LSTOCKOUT a");
        //sqlbuf.append(" WHERE a.BDATE between "+beginDate +" and "+endDate+" ");

        if("bDate".equals(dateType)){
            sqlbuf.append(" WHERE a.BDATE between "+beginDate +" and "+endDate+" ");
        }else if("accountDate".equals(dateType)){
            sqlbuf.append(" WHERE a.ACCOUNT_DATE between "+beginDate +" and "+endDate+" ");
        }else{
            sqlbuf.append( " where 1=1 ");
        }

        //报损单状态调整  BY JZMA 20190124
        //0.新建，1.已提交，2.待签收，4.全部签收，5.部分签收，6.全部驳回  7.终止上传
        if (status != null && status.length() > 0)
        {
            switch (status) {
                case "1":
                    sqlbuf.append(" AND a.STATUS='2'  ");
                    break;
                case "2":
                    sqlbuf.append(" AND a.STATUS='2'  ");
                    break;
                case "7":
                    sqlbuf.append(" AND a.STATUS='2'  ");
                    break;
                default:
                    sqlbuf.append(" AND a.STATUS='" + status + "' ");
                    break;
            }
        }
        
        if (keyTxt != null && keyTxt.length() > 0)
        {
            sqlbuf.append(" AND (a.TOT_AMT LIKE '%%"+keyTxt+"%%' OR a.TOT_PQTY LIKE '%%"+keyTxt+"%%' OR a.lstockoutno LIKE '%%"+keyTxt+"%%' OR a.MEMO LIKE '%%"+keyTxt+"%%' "
                    + " or a.process_ERP_No like '%%"+keyTxt+"%%'  )");
        }
        sqlbuf.append(" and a.SHOPID='"+shopId+"'");
        sqlbuf.append(" and a.EID='"+eId+"'");
        
        return sqlbuf.toString();
    }
    
    
}
