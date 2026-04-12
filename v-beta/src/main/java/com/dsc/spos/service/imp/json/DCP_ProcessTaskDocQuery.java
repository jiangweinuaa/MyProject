package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProcessTaskDocQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskDocQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ProcessTaskDocQuery extends SPosBasicService<DCP_ProcessTaskDocQueryReq, DCP_ProcessTaskDocQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskDocQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProcessTaskDocQueryReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskDocQueryReq>(){};
    }

    @Override
    protected DCP_ProcessTaskDocQueryRes getResponseType() {
        return new DCP_ProcessTaskDocQueryRes();
    }

    @Override
    protected DCP_ProcessTaskDocQueryRes processJson(DCP_ProcessTaskDocQueryReq req) throws Exception {
        String sql;
        //查询条件
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();

        String departId = req.getRequest().getDepartId();

        //查询资料
        DCP_ProcessTaskDocQueryRes res = this.getResponse();
        try {
            //给分页字段赋值
            sql = this.getQuerySql_Count(req);	//查询总笔数
            List<Map<String, Object>> getQData_Count = this.doQueryData(sql,null);
            int totalRecords;	//总笔数
            int totalPages;		//总页数
            res.setDatas(new ArrayList<>());
            if (getQData_Count != null && !getQData_Count.isEmpty()) {
                String num = getQData_Count.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //计算起始位置
                int pageSize = req.getPageSize();
                int startRow = (req.getPageNumber() - 1) * pageSize;

                StringBuffer sqlbuf = new StringBuffer();
                sqlbuf.append(""
                        + " select A.PROCESSTASKNO,A.bDate,A.memo,A.pDate,A.pTemplateNO,A.status,A.createBy,A.create_date AS createDate,"
                        + " A.create_time AS createTime, "
                        + " A.tot_pqty AS totPqty,A.tot_amt AS totAmt,A.TOT_DISTRIAMT,A.tot_cqty AS totCqty,  "
                        + " f.op_name AS createByName,"
                        + " J.PTEMPLATE_NAME AS pTemplateName, "
                        + " a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , a.modifyby , "
                        + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                        + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                        + " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
                        + " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime ,  "
                        + "	f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName , "
                        + " f4.op_name as submitByName ,f5.op_name as accountByName ,"
                        + "  a.update_time , a.process_status,a.WAREHOUSE,k1.warehouse_name as WAREHOUSENAME , "
                        + " a.MATERIALWAREHOUSE,k2.warehouse_name as MATERIALWAREHOUSENAME , "
                        + " a.PROCESSPLANNO,a.PROCESSPLANNAME,a.TASK0NO,a.DTNO, " +
                        " dt1.dtname,dt1.begin_time,dt1.end_time,a.otype,a.ofno,a.employeeid,l.name as employeename,a.prodtype,a.maintaskno,a.departid,m.departname,a.isRefundOrder  "
                        + " from DCP_PROCESSTASK A "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f ON A .EID = f.EID AND A.createby = f.opno and f.lang_type='"+langType+"' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON A .EID = f1.EID AND A.modifyBy = f1.opno AND F1.LANG_TYPE  = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON A .EID = f2.EID AND A.cancelby = f2.opno AND F2.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f3 ON A .EID = f3.EID AND A.confirmby = f3.opno AND F3.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f4 ON A .EID = f4.EID AND A.submitby = f4.opno AND F4.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f5 ON A .EID = f5.EID AND A.accountby = f5.opno AND F5.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN DCP_PTEMPLATE J ON A.EID=J.EID AND A.PTEMPLATENO=J.PTEMPLATENO  AND j.doc_type = 2  "
                        + " left join DCP_WAREHOUSE_lang k1 on a.EID=k1.EID and a.SHOPID=k1.organizationno and a.WAREHOUSE=k1.warehouse and k1.lang_type='"+langType +"' "
                        + " left join DCP_WAREHOUSE_lang k2 on a.EID=k2.EID and a.SHOPID=k2.organizationno and a.MATERIALWAREHOUSE=k2.warehouse and k2.lang_type='"+langType +"' "
                        + " left join DCP_DINNERTIME dt1 on a.eid=dt1.eid and a.organizationno=dt1.shopid and a.dtno=dt1.dtno " +
                        " left join dcp_employee l on l.eid=a.eid and l.employeeno=a.employeeid " +
                        " left join dcp_department_lang m on m.eid=a.eid and m.departno=a.departid and m.lang_type='"+req.getLangType()+"' "
                );

                //2018-11-09 添加 日期查询条件
                sqlbuf.append(" WHERE a.BDATE between "+beginDate +" and "+endDate+"  ");
                sqlbuf.append(" and a.SHOPID='"+shopId+"'");
                sqlbuf.append(" and a.EID='"+eId+"'");



                sqlbuf.append(" AND A .PROCESSTASKNO IN ( "
                        + "select PROCESSTASKNO from ( "
                        + "select PROCESSTASKNO,ROWNUM rn from DCP_PROCESSTASK "
                );

                //2018-11-09 添加 日期查询条件
                sqlbuf.append(" WHERE BDATE between "+beginDate +" and "+endDate+" ");

                if(status != null && status.length() > 0)
                {
                    sqlbuf.append(" and STATUS='"+status+"'");
                }
                if(keyTxt != null && keyTxt.length() > 0)
                {
                    sqlbuf.append(" and (TOT_DISTRIAMT LIKE '%%"+keyTxt+"%%' OR TOT_AMT LIKE '%%"+keyTxt+"%%' OR TOT_PQTY LIKE '%%"+keyTxt+"%%' OR PROCESSTASKNO LIKE '%%"+keyTxt+"%%' OR MEMO LIKE '%%"+keyTxt+"%%')");
                }
                sqlbuf.append(" and SHOPID='"+shopId+"'");
                sqlbuf.append(" and EID='"+eId+"'");
                if(Check.NotNull(req.getRequest().getDepartId())){
                    sqlbuf.append(" and DEPARTID='"+req.getRequest().getDepartId()+"' ");
                }
                sqlbuf.append(" ORDER BY status ASC,bdate DESC,PROCESSTASKNO DESC) ");
                sqlbuf.append(" where rn>" + startRow + " AND rn <= " + (startRow+pageSize) + "");
                sqlbuf.append(" ) ORDER BY status ASC,bdate DESC,processtaskNo DESC   ");
                sql=sqlbuf.toString();

                List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
                if (getQDataDetail != null && !getQDataDetail.isEmpty()) {


                    //单头主键字段
                    Map<String, Boolean> condition = new HashMap<>(); //查询条件
                    condition.put("PROCESSTASKNO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQDataDetail, condition);

                    for (Map<String, Object> oneData : getQHeader)
                    {
                        DCP_ProcessTaskDocQueryRes.level1Elm oneLv1 = res.new level1Elm();

                        //取出第一层
                        String processTaskNO = oneData.get("PROCESSTASKNO").toString();
                        String bDate = oneData.get("BDATE").toString();
                        String memo = oneData.get("MEMO").toString();
                        String a_status = oneData.get("STATUS").toString();
                        String createByName = oneData.get("CREATEBYNAME").toString();
                        String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                        String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                        String pDate = oneData.get("PDATE").toString();
                        String warehouse = oneData.get("WAREHOUSE").toString();
                        String warehouseName = oneData.get("WAREHOUSENAME").toString();
                        String materialWarehouseNO = oneData.get("MATERIALWAREHOUSE").toString();
                        String materialWarehouseName = oneData.get("MATERIALWAREHOUSENAME").toString();
                        String createBy = oneData.get("CREATEBY").toString();
                        String createDate = oneData.get("CREATEDATE").toString();
                        String createTime = oneData.get("CREATETIME").toString();
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
                        String totPqty = oneData.get("TOTPQTY").toString();
                        String totAmt = oneData.get("TOTAMT").toString();
                        String totCqty = oneData.get("TOTCQTY").toString();
                        String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();


                        oneLv1.setOType(oneData.get("OTYPE").toString());
                        oneLv1.setOfNo(oneData.get("OFNO").toString());
                        oneLv1.setIsRefundOrder(oneData.get("ISREFUNDORDER").toString());
                        oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                        oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                        oneLv1.setDepartId(oneData.get("DEPARTID").toString());
                        oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
                        oneLv1.setProdType(oneData.get("PRODTYPE").toString());
                        oneLv1.setMainTaskNo(oneData.get("MAINTASKNO").toString());

                        //设置响应
                        oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
                        oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                        oneLv1.setTotPqty(totPqty);
                        oneLv1.setTotAmt(totAmt);
                        oneLv1.setTotCqty(totCqty);
                        oneLv1.setTotDistriAmt(totDistriAmt);
                        oneLv1.setCreateBy(createBy);
                        oneLv1.setCreateDate(createDate);
                        oneLv1.setCreateTime(createTime);
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
                        oneLv1.setProcessTaskNo(processTaskNO);
                        oneLv1.setBDate(bDate);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(a_status);
                        oneLv1.setCreateByName(createByName);
                        oneLv1.setPTemplateName(pTemplateName);
                        oneLv1.setPTemplateNo(pTemplateNO);
                        oneLv1.setPDate(pDate);
                        oneLv1.setWarehouse(warehouse);
                        oneLv1.setWarehouseName(warehouseName);
                        oneLv1.setMaterialWarehouseNo(materialWarehouseNO);
                        oneLv1.setMaterialWarehouseName(materialWarehouseName);
                        oneLv1.setProcessPlanNo(oneData.get("PROCESSPLANNO").toString());
                        oneLv1.setTask0No(oneData.get("TASK0NO").toString());
                        oneLv1.setDtNo(oneData.get("DTNO").toString());
                        oneLv1.setDtName(oneData.get("DTNAME").toString());
                        oneLv1.setDtBeginTime(oneData.get("BEGIN_TIME").toString());
                        oneLv1.setDtEndTime(oneData.get("END_TIME").toString());

                        res.getDatas().add(oneLv1);
                    }
                } else {
                    totalRecords = 0;
                    totalPages = 0;
                }
            } else {
                totalRecords = 0;
                totalPages = 0;
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ProcessTaskDocQueryReq req) throws Exception {
        return null;
    }

    private String getQuerySql_Count(DCP_ProcessTaskDocQueryReq req) {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        String beginDate = req.getRequest().getBeginDate();
        String endDate =req.getRequest().getEndDate();

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append( ""
                + " select count(*) as num from DCP_PROCESSTASK "
                + " WHERE BDATE between "+beginDate +" and "+endDate+" "
        );

        if (status != null && status.length() > 0)
        {
            sqlbuf.append(" AND STATUS='"+status+"'");
        }
        if(Check.NotNull(req.getRequest().getDepartId())){
            sqlbuf.append(" AND DEPARTID='"+req.getRequest().getDepartId()+"' ");
        }
        if (keyTxt != null && keyTxt.length() > 0)
        {
            sqlbuf.append(" AND (TOT_AMT LIKE '%%"+keyTxt+"%%' OR TOT_PQTY LIKE '%%"+keyTxt+"%%' "
                    + " OR PROCESSTASKNO LIKE '%%"+keyTxt+"%%' OR MEMO LIKE '%%"+keyTxt+"%%')");
        }
        sqlbuf.append(" and SHOPID='"+shopId+"'");
        sqlbuf.append(" and EID='"+eId+"'");

        return sqlbuf.toString();
    }


}
