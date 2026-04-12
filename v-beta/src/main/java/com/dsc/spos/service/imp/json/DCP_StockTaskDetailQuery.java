package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_StockTaskDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_StockTaskDetailQuery extends SPosBasicService<DCP_StockTaskDetailQueryReq, DCP_StockTaskDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockTaskDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockTaskDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_StockTaskDetailQueryReq>(){};
    }

    @Override
    protected DCP_StockTaskDetailQueryRes getResponseType() {
        return new DCP_StockTaskDetailQueryRes();
    }

    @Override
    protected DCP_StockTaskDetailQueryRes processJson(DCP_StockTaskDetailQueryReq req) throws Exception {
        //查詢條件
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String langType = req.getLangType();
        String stockTaskNo = req.getRequest().getStockTaskNo();

            DCP_StockTaskDetailQueryRes res = null;
            res = this.getResponse();
            StringBuffer sb = new StringBuffer(" ");
            sb.append( " select  A.*,B.WAREHOUSE_NAME,C.STOCKTAKENO,d.PTEMPLATE_NAME as ptemplatename," +
                    " e1.name as employeename ,d1.departname as departname,e2.name as createbyname,e3.name as modifyname,e4.name as submitbyname,e5.name as confirmbyname,e6.name as cancelbyname,e7.name as accountbyname from DCP_STOCKTASK A ");
            sb.append( " left JOIN DCP_WAREHOUSE_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.WAREHOUSE=B.WAREHOUSE and B.LANG_TYPE='"+ langType +"' ");
            sb.append( " LEFT JOIN DCP_STOCKTAKE C ON A.EID=C.EID AND A.ORGANIZATIONNO=C.ORGANIZATIONNO AND A.STOCKTASKNO=C.OFNO");
            sb.append(" left join DCP_PTEMPLATE d on d.eid=a.eid and d.ptemplateno=a.ptemplateno ");
            sb.append(" left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid ");
            sb.append(" left join DCP_DEPARTMENT_LANG d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"' ");
            sb.append(" left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.createby ");
            sb.append(" left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.modifyby ");
            sb.append(" left join dcp_employee e4 on e4.eid=a.eid and e4.employeeno=a.submitby ");
            sb.append(" left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno=a.confirmby ");
            sb.append(" left join dcp_employee e6 on e6.eid=a.eid and e6.employeeno=a.cancelby ");
            sb.append(" left join dcp_employee e7 on e7.eid=a.eid and e7.employeeno=a.accountby ");

            sb.append( " where A.EID='"+ eId +"' AND A.ORGANIZATIONNO='"+ organizationNO +"' and a.stockTaskNo='"+stockTaskNo+"' ");

            List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);

            if (getQData != null && !getQData.isEmpty())
            { // 有資料，取得詳細內容
                res.setDatas(new ArrayList<DCP_StockTaskDetailQueryRes.level1Elm>());
                for (Map<String, Object> oneData : getQData)
                {
                    DCP_StockTaskDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();

                    // 取得第一層資料庫搜尋結果
                    String shopId = oneData.get("SHOPID").toString();
                    String stockTaskNO = oneData.get("STOCKTASKNO").toString();
                    String stockTaskId = oneData.get("STOCKTASKID").toString();
                    String sDate = oneData.get("SDATE").toString();
                    String pTemplateNo = oneData.get("PTEMPLATENO").toString();
                    String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                    String bDate = oneData.get("BDATE").toString();
                    String memo = oneData.get("MEMO").toString();
                    String status2 = oneData.get("STATUS").toString();
                    String docType = oneData.get("DOC_TYPE").toString();
                    String isBTake = oneData.get("IS_BTAKE").toString();
                    String loadDocType = oneData.get("LOAD_DOCTYPE").toString();
                    String loadDocNO = oneData.get("LOAD_DOCNO").toString();
                    String warehouse = oneData.get("WAREHOUSE").toString();
                    String warehouseName = oneData.get("WAREHOUSE_NAME").toString();
                    String taskWay = oneData.get("TASKWAY").toString();
                    String notGoodsMode = oneData.get("NOTGOODSMODE").toString();
                    String stockTakeNO = oneData.get("STOCKTAKENO").toString();
                    String submitStatus = oneData.get("SUBMITSTATUS").toString();

                    String createBy =oneData.get("CREATEBY").toString();
                    String createByName =oneData.get("CREATEBYNAME").toString() ;
                    String createDate =oneData.get("CREATE_DATE").toString();
                    String createTime =oneData.get("CREATE_TIME").toString();
                    String modifyBy =oneData.get("MODIFYBY").toString();
                    String modifyByName =oneData.get("MODIFYNAME").toString() ;
                    String modifyDate =oneData.get("MODIFY_DATE").toString();
                    String modifyTime =oneData.get("MODIFY_TIME").toString();
                    String submitBy =oneData.get("SUBMITBY").toString();
                    String submitByName =oneData.get("SUBMITBYNAME").toString() ;
                    String submitDate =oneData.get("SUBMIT_DATE").toString();
                    String submitTime =oneData.get("SUBMIT_TIME").toString();
                    String confirmBy =oneData.get("CONFIRMBY").toString();
                    String confirmByName =oneData.get("CONFIRMBYNAME").toString() ;
                    String confirmDate =oneData.get("CONFIRM_DATE").toString();
                    String confirmTime =oneData.get("CONFIRM_TIME").toString();
                    String cancelBy =oneData.get("CANCELBY").toString();
                    String cancelByName =oneData.get("CANCELBYNAME").toString() ;
                    String cancelDate =oneData.get("CANCEL_DATE").toString();
                    String cancelTime =oneData.get("CANCEL_TIME").toString();
                    String accountBy =oneData.get("ACCOUNTBY").toString();
                    String accountByName =oneData.get("ACCOUNTBYNAME").toString();
                    String accountDate =oneData.get("ACCOUNT_DATE").toString();
                    String accountTime =oneData.get("ACCOUNT_TIME").toString();
                    String is_adjust_stock = oneData.get("IS_ADJUST_STOCK").toString();

                    String createType = oneData.get("CREATETYPE").toString();
                    String totSubTaskQty = oneData.get("TOTSUBTASKQTY").toString();
                    String totCqty = oneData.get("TOTCQTY").toString();
                    String warehouseType = oneData.get("WAREHOUSETYPE").toString();
                    String employeeId = oneData.get("EMPLOYEEID").toString();
                    String employeeName = oneData.get("EMPLOYEENAME").toString();
                    String departId = oneData.get("DEPARTID").toString();
                    String departName = oneData.get("DEPARTNAME").toString();

                    // 處理調整回傳值；
                    //oneLv1.setShopId(shopId);
                    oneLv1.setStockTaskNo(stockTaskNO);
                    oneLv1.setBDate(bDate);
                    oneLv1.setMemo(memo);
                    oneLv1.setStatus(status2);
                    oneLv1.setDocType(docType);
                    oneLv1.setIsBTake(isBTake);
                    //oneLv1.setLoadDocType(loadDocType);
                    //oneLv1.setLoadDocNo(loadDocNO);
                    //oneLv1.setWarehouse(warehouse);
                    //oneLv1.setWarehouseName(warehouseName);
                    oneLv1.setTaskWay(taskWay);
                    oneLv1.setNotGoodsMode(notGoodsMode);
                    oneLv1.setIsAdjustStock(is_adjust_stock);
                    //oneLv1.setStockTakeNo(stockTakeNO);
                    //oneLv1.setSubmitStatus(submitStatus);

                    oneLv1.setCreateBy(createBy);
                    oneLv1.setCreateByName(createByName);
                    oneLv1.setCreateDate(createDate);
                    oneLv1.setCreateTime(createTime);
                    oneLv1.setModifyBy(modifyBy);
                    oneLv1.setModifyByName(modifyByName);
                    oneLv1.setModifyDate(modifyDate);
                    oneLv1.setModifyTime(modifyTime);
                    //oneLv1.setSubmitBy(submitBy);
                    //oneLv1.setSubmitByName(submitByName);
                    //oneLv1.setSubmitDate(submitDate);
                    //oneLv1.setSubmitTime(submitTime);
                    oneLv1.setConfirmBy(confirmBy);
                    oneLv1.setConfirmByName(confirmByName);
                    oneLv1.setConfirmDate(confirmDate);
                    oneLv1.setConfirmTime(confirmTime);
                    oneLv1.setCancelBy(cancelBy);
                    oneLv1.setCancelByName(cancelByName);
                    oneLv1.setCancelDate(cancelDate);
                    oneLv1.setCancelTime(cancelTime);
                    //oneLv1.setacc(accountBy);
                    //oneLv1.setAccountByName(accountByName);
                    //oneLv1.setAccountDate(accountDate);
                    //oneLv1.setAccountTime(accountTime);

                    oneLv1.setPTemplateNo(pTemplateNo);
                    oneLv1.setPTemplateName(pTemplateName);
                    oneLv1.setSDate(sDate);
                    oneLv1.setStockTaskID(stockTaskId);
                    oneLv1.setCreateType(createType);
                    oneLv1.setTotCqty(totCqty);
                    oneLv1.setTotSubTaskQty(totSubTaskQty);
                    oneLv1.setWarehouseType(warehouseType);
                    oneLv1.setEmployeeId(employeeId);
                    oneLv1.setEmployeeName(employeeName);
                    oneLv1.setDepartId(departId);
                    oneLv1.setDepartName(departName);

                    oneLv1.setDetail(new ArrayList<>());
                    oneLv1.setOrgList(new ArrayList<>());

                    String detailSql="select a.item,a.pluno,b.plu_name as pluname,c.featureno,c.featurename," +
                            " a.category,cl.category_name as categoryname,a.sdprice,a.punit,u1.uname as punitname,a.baseunit,u2.uname as baseunitname,a.unit_ratio as unitratio,gu.spec  from DCP_STOCKTASK_DETAIL a " +
                            " left join DCP_GOODS_LANG b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"' " +
                            " left join DCP_GOODS_FEATURE_LANG c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='"+langType+"' " +
                            " left join dcp_unit_lang u1 on u1.eid=a.eid and u1.unit=a.punit and u1.lang_type='"+langType+"' " +
                            " left join dcp_unit_lang u2 on u2.eid=a.eid and u2.unit=a.baseunit and u2.lang_type='"+langType+"' " +
                            " left join dcp_goods_unit_lang gu on gu.eid=a.eid and gu.pluno=a.pluno and gu.ounit=a.punit and gu.lang_type='"+langType+"' " +
                            " left join DCP_CATEGORY_LANG cl on cl.eid=a.eid and cl.category=a.category and cl.lang_type='"+langType+"' "+
                            " where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNO+"' ";
                    List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
                    if(CollUtil.isNotEmpty(detailList)){
                        for(Map<String, Object> oneDetail:detailList){
                            DCP_StockTaskDetailQueryRes.Detail detail = res.new Detail();

                            detail.setItem(oneDetail.get("ITEM").toString());
                            detail.setPluNo(oneDetail.get("PLUNO").toString());
                            detail.setPluName(oneDetail.get("PLUNAME").toString());
                            detail.setFeatureNo(oneDetail.get("FEATURENO").toString());
                            detail.setFeatureName(oneDetail.get("FEATURENAME").toString());
                            detail.setPUnit(oneDetail.get("PUNIT").toString());
                            detail.setPUnitName(oneDetail.get("PUNITNAME").toString());
                            detail.setBaseUnit(oneDetail.get("BASEUNIT").toString());
                            detail.setBaseUnitName(oneDetail.get("BASEUNITNAME").toString());
                            detail.setUnitRatio(oneDetail.get("UNITRATIO").toString());
                            detail.setSpec(oneDetail.get("SPEC").toString());
                            detail.setSdPrice(oneDetail.get("SDPRICE").toString());
                            detail.setCategory(oneDetail.get("CATEGORY").toString());
                            detail.setCategoryName(oneDetail.get("CATEGORYNAME").toString());
                            oneLv1.getDetail().add(detail);

                        }
                    }

                    String orgSql="select a.organizationno,b.org_name as organizationname,a.warehouse,c.warehouse_name as warehousename from DCP_STOCKTASK_ORG a" +
                            " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+langType+"'" +
                            " left join dcp_warehouse_lang c on c.eid=a.eid and c.warehouse=a.warehouse and c.lang_type='"+langType+"'" +
                            " where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNO+"' ";

                    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                    if(CollUtil.isNotEmpty(orgList)){
                        for(Map<String, Object> oneOrg:orgList){
                            DCP_StockTaskDetailQueryRes.OrgList org = res.new OrgList();
                            org.setOrganizationNo(oneOrg.get("ORGANIZATIONNO").toString());
                            org.setOrganizationName(oneOrg.get("ORGANIZATIONNAME").toString());
                            org.setWarehouse(oneOrg.get("WAREHOUSE").toString());
                            org.setWarehouseName(oneOrg.get("WAREHOUSENAME").toString());
                            oneLv1.getOrgList().add(org);
                        }
                    }

                    res.getDatas().add(oneLv1);
                }
            }
            else
            {
                res.setDatas(new ArrayList<DCP_StockTaskDetailQueryRes.level1Elm>());
            }

            return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_StockTaskDetailQueryReq req) throws Exception {
        return null;
    }

}
