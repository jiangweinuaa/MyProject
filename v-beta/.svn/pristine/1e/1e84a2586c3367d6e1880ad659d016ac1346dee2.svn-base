package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_StockTakeDetailReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockTakeDetailRes;
import com.dsc.spos.json.cust.res.DCP_StockTakeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_StockTakeDetail
 * 服务说明：库存盘点明细查询
 * @author jinzma
 * @since  2021-03-02
 */
public class DCP_StockTakeDetail extends SPosBasicService<DCP_StockTakeDetailReq, DCP_StockTakeDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockTakeDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String OfNO = req.getRequest().getOfNo();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        if (Check.Null(OfNO) && Check.Null(stockTakeNO))
        {
            errMsg.append("盘点单号,盘点计划单号不可同时为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockTakeDetailReq> getRequestType() {
        return new TypeToken<DCP_StockTakeDetailReq>(){};
    }
    
    @Override
    protected DCP_StockTakeDetailRes getResponseType() {
        return new DCP_StockTakeDetailRes();
    }
    
    @Override
    protected DCP_StockTakeDetailRes processJson(DCP_StockTakeDetailReq req) throws Exception {
        //查詢資料
        DCP_StockTakeDetailRes res =  this.getResponse();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        //try {
            //有盘点单号 直接查询盘点单
            //没有盘点单号，有计划单号 查询盘点计划单号
            res.setDatas(new ArrayList<>());
            if (stockTakeNO != null && stockTakeNO.length() > 0) {

                String stockTakeSql = getStockTakeSql(req);
                List<Map<String, Object>> getQData = this.doQueryData(stockTakeSql,null);
                String sql = this.getQuerySql(req);
                List<Map<String, Object>> getDetailData = this.doQueryData(sql,null);

                if(CollUtil.isNotEmpty(getQData)){
                    String isHttps=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                    String httpStr=isHttps.equals("1")?"https://":"http://";
                    String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                    if (domainName.endsWith("/")) {
                        domainName = httpStr + domainName + "resource/image/";
                    }else{
                        domainName = httpStr + domainName + "/resource/image/";
                    }
                    for (Map<String, Object> oneData : getQData) {
                        DCP_StockTakeDetailRes.StockTakeInfo oneLv1 = res.new StockTakeInfo();

                        // 取得第一層資料庫搜尋結果
                        String shop1 = oneData.get("SHOPID").toString();
                        String stocktakeNO = oneData.get("STOCKTAKENO").toString();
                        String processERPNo = oneData.get("PROCESSERPNO").toString();
                        String bDate = oneData.get("BDATE").toString();
                        String memo = oneData.get("MEMO").toString();
                        String status = oneData.get("STATUS").toString();
                        String docType = oneData.get("DOCTYPE").toString();
                        String oType = oneData.get("OTYPE").toString();
                        String ofNO = oneData.get("OFNO").toString();
                        String isBTake = oneData.get("ISBTAKE").toString();
                        String loadDocType = oneData.get("LOADDOCTYPE").toString();
                        String loadDocNO = oneData.get("LOADDOCNO").toString();
                        String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                        String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                        String plStatus = oneData.get("PLSTATUS").toString();
                        String warehouse = oneData.get("WAREHOUSE").toString();
                        String warehouseName = oneData.get("WAREHOUSE_NAME").toString();
                        String taskWay = oneData.get("TASKWAY").toString();
                        String notGoodsMode = oneData.get("NOTGOODSMODE").toString();
                        String totPqty = oneData.get("TOTPQTY").toString();
                        String totAmt = oneData.get("TOTAMT").toString();
                        String totCqty = oneData.get("TOTCQTY").toString();
                        String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                        String submitStatus = oneData.get("SUBMITSTATUS").toString();
                        String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();   //是否调整库存Y/N/X Y转库存 N转销售 X不异动
                        if (Check.Null(isAdjustStock)) {
                            isAdjustStock = "Y";
                        }
                        String createBy =oneData.get("CREATEBY").toString();
                        String createByName =oneData.get("CREATENAME").toString();
                        String createDate =oneData.get("CREATE_DATE").toString();
                        String createTime =oneData.get("CREATE_TIME").toString();
                        String modifyBy =oneData.get("MODIFYBY").toString();
                        String modifyByName =oneData.get("MODIFYNAME").toString();
                        String modifyDate =oneData.get("MODIFY_DATE").toString();
                        String modifyTime =oneData.get("MODIFY_TIME").toString();
                        String submitBy =oneData.get("SUBMITBY").toString();
                        String submitByName =oneData.get("SUBMITNAME").toString();
                        String submitDate =oneData.get("SUBMIT_DATE").toString();
                        String submitTime =oneData.get("SUBMIT_TIME").toString();
                        String confirmBy =oneData.get("CONFIRMBY").toString();
                        String confirmByName =oneData.get("CONFIRMNAME").toString();
                        String confirmDate =oneData.get("CONFIRM_DATE").toString();
                        String confirmTime =oneData.get("CONFIRM_TIME").toString();
                        String cancelBy =oneData.get("CANCELBY").toString();
                        String cancelByName =oneData.get("CANCELNAME").toString();
                        String cancelDate =oneData.get("CANCEL_DATE").toString();
                        String cancelTime =oneData.get("CANCEL_TIME").toString();
                        String accountBy =oneData.get("ACCOUNTBY").toString();
                        String accountByName =oneData.get("ACCOUNTNAME").toString();
                        String accountDate =oneData.get("ACCOUNT_DATE").toString();
                        String accountTime =oneData.get("ACCOUNT_TIME").toString();
                        String substockimport = oneData.get("SUBSTOCKIMPORT").toString();

                        String employeeid = oneData.get("EMPLOYEEID").toString();
                        String employeeName = oneData.get("EMPLOYEENAME").toString();
                        String departid = oneData.get("DEPARTID").toString();
                        String departName = oneData.get("DEPARTNAME").toString();

                        if (totPqty == null || totPqty.length()==0 ){
                            totPqty="0";
                        }
                        if (totAmt == null || totAmt.length()==0 ){
                            totAmt="0";
                        }
                        if (totCqty == null || totCqty.length()==0 ){
                            totCqty="0";
                        }
                        if (totDistriAmt == null || totDistriAmt.length()==0 ){
                            totDistriAmt="0";
                        }


                        // 處理調整回傳值；
                        oneLv1.setShopId(shop1);
                        oneLv1.setStockTakeNo(stocktakeNO);
                        oneLv1.setProcessERPNo(processERPNo);
                        oneLv1.setBDate(bDate);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(status);
                        oneLv1.setDocType(docType);
                        oneLv1.setOType(oType);
                        oneLv1.setOfNo(ofNO);
                        oneLv1.setIsBTake(isBTake);
                        oneLv1.setPTemplateNo(pTemplateNO);
                        oneLv1.setPTemplateName(pTemplateName);
                        oneLv1.setLoadDocType(loadDocType);
                        oneLv1.setLoadDocNo(loadDocNO);
                        oneLv1.setCreateByName(createByName);
                        oneLv1.setPlStatus(plStatus);
                        oneLv1.setWarehouse(warehouse);
                        oneLv1.setWarehouseName(warehouseName);
                        oneLv1.setTaskWay(taskWay);
                        oneLv1.setTotPqty(totPqty);
                        oneLv1.setTotAmt(totAmt);
                        oneLv1.setTotCqty(totCqty);
                        oneLv1.setTotDistriAmt(totDistriAmt);
                        oneLv1.setNotGoodsMode(notGoodsMode);
                        oneLv1.setSubmitStatus(submitStatus);
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
                        oneLv1.setIsAdjustStock(isAdjustStock);
                        oneLv1.setSubStockImport(substockimport);
                        oneLv1.setEmployeeId(employeeid);
                        oneLv1.setEmployeeName(employeeName);
                        oneLv1.setDepartId(departid);
                        oneLv1.setDepartName(departName);

                        oneLv1.setDatas(new ArrayList<>());
                        Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                        condition.put("ITEM", true);
                        List<Map<String, Object>> getQItem= MapDistinct.getMap(getDetailData,condition);
                        for (Map<String, Object> detailData : getQItem) {
                            DCP_StockTakeDetailRes.level1Elm detailLevel = res.new level1Elm();
                            String item = detailData.get("ITEM").toString();
                            String oItem = detailData.get("OITEM").toString();
                            String pluNO = detailData.get("PLUNO").toString();
                            String pluName = detailData.get("PLU_NAME").toString();
                            String spec = detailData.get("SPEC").toString();
                            String pqty = detailData.get("PQTY").toString();
                            String refBaseQty = detailData.get("REF_BASEQTY").toString();
                            String unitRatio = detailData.get("UNIT_RATIO").toString();
                            String punit = detailData.get("PUNIT").toString();
                            String punitName = detailData.get("PUNITNAME").toString();
                            String baseUnitName = detailData.get("BASEUNITNAME").toString();
                            String price = detailData.get("PRICE").toString();
                            String amt = detailData.get("AMT").toString();
                            String warehoused = detailData.get("WAREHOUSE").toString();
                            String memod = detailData.get("MEMO").toString();
                            String batchNO = detailData.get("BATCH_NO").toString();
                            String prodDate = detailData.get("PROD_DATE").toString();
                            String distriPrice = detailData.get("DISTRIPRICE").toString();
                            String isBatch = detailData.get("ISBATCH").toString();
                            String distriAmt = detailData.get("DISTRIAMT").toString();
                            String punitUDLength = detailData.get("PUNIT_UDLENGTH").toString();
                            String listImage  = detailData.get("LISTIMAGE").toString();
                            if (!Check.Null(listImage)){
                                listImage = domainName+listImage;
                            }
                            String baseUnit = detailData.get("BASEUNIT").toString();
                            String baseQty = detailData.get("BASEQTY").toString();
                            String featureNo = detailData.get("FEATURENO").toString();
                            String featureName = detailData.get("FEATURENAME").toString();
                            String fqty = detailData.get("FQTY").toString();
                            String fBaseQty = detailData.get("FBASEQTY").toString();
                            String rqty = detailData.get("RQTY").toString();
                            String rBaseQty = detailData.get("RBASEQTY").toString();

                            String location = detailData.get("LOCATION").toString();
                            String locationName = detailData.get("LOCATIONNAME").toString();
                            String expDate = detailData.get("EXPDATE").toString();
                            String isBatchAdd = detailData.get("ISBATCHADD").toString();
                            String shelflife = detailData.get("SHELFLIFE").toString();

                            //支持多单位盘点 by jinzma 20210720
                            detailLevel.setUnitList(new ArrayList<>());
                            for (Map<String, Object> oneDataUnit : getDetailData) {
                                String unitList_punit= oneDataUnit.get("UNITLIST_PUNIT").toString();
                                if(item.equals(oneDataUnit.get("ITEM").toString()) && !Check.Null(unitList_punit)) {
                                    DCP_StockTakeDetailRes.level2Elm oneLv2 = new DCP_StockTakeDetailRes().new level2Elm();
                                    oneLv2.setPqty(oneDataUnit.get("UNITLIST_PQTY").toString());
                                    oneLv2.setPunit(unitList_punit);
                                    oneLv2.setPunitName(oneDataUnit.get("UNITLIST_PUNITNAME").toString());
                                    oneLv2.setUnitRatio(oneDataUnit.get("UNITLIST_UNITRATIO").toString());
                                    oneLv2.setPunitUDLength(oneDataUnit.get("UNITLIST_PUNITUDLENGTH").toString());
                                    detailLevel.getUnitList().add(oneLv2);
                                }
                            }

                            // 處理調整回傳值；
                            detailLevel.setItem(item);
                            detailLevel.setoItem(oItem);
                            detailLevel.setPluNo(pluNO);
                            detailLevel.setPluName(pluName);
                            detailLevel.setSpec(spec);
                            detailLevel.setPqty(pqty);
                            detailLevel.setPunit(punit);
                            detailLevel.setPunitName(punitName);
                            detailLevel.setBaseQty(baseQty);
                            detailLevel.setBaseUnit(baseUnit);
                            detailLevel.setBaseUnitName(baseUnitName);
                            //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221104
                            detailLevel.setBaseUnitUdLength(detailData.get("BASEUNITUDLENGTH").toString());
                            detailLevel.setUnitRatio(unitRatio);
                            detailLevel.setRefBaseQty(refBaseQty);
                            detailLevel.setPrice(price);
                            detailLevel.setAmt(amt);
                            detailLevel.setWarehouse(warehoused);
                            detailLevel.setMemo(memod);
                            detailLevel.setBatchNo(batchNO);
                            detailLevel.setProdDate(prodDate);
                            detailLevel.setDistriPrice(distriPrice);
                            detailLevel.setIsBatch(isBatch);
                            detailLevel.setDistriAmt(distriAmt);
                            detailLevel.setPunitUdLength(punitUDLength);
                            detailLevel.setListImage(listImage);
                            detailLevel.setFeatureNo(featureNo);
                            detailLevel.setFeatureName(featureName);
                            detailLevel.setFqty(fqty);
                            detailLevel.setfBaseQty(fBaseQty);
                            detailLevel.setRqty(rqty);
                            detailLevel.setrBaseQty(rBaseQty);
                            detailLevel.setLocation(location);
                            detailLevel.setLocationName(locationName);
                            detailLevel.setIsBatchAdd(isBatchAdd);
                            detailLevel.setExpDate(expDate);
                            detailLevel.setShelfLife(shelflife);

                            oneLv1.getDatas().add(detailLevel);
                        }

                        res.getDatas().add(oneLv1);
                    }
                }

            }
            
            return res;
        //}catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        //}
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_StockTakeDetailReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType=req.getLangType();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        sqlbuf.append(" "
                + " select a.*,b.isbatch,c.plu_name,gul.spec,d.uname as punitname,e.uname as baseunitname,"
                + " h.udlength as punit_udlength,bu.udlength as baseunitudlength,image.listimage,fn.featurename,"
                + " u1.punit as unitlist_punit,u1.pqty as unitlist_pqty,"
                + " u2.udlength as unitlist_punitudlength,"
                + " u3.uname as unitlist_punitname,"
                + " u1.UNIT_RATIO as unitlist_unitratio,a.location,a.expdate,a.isbatchadd,i.locationname,b.SHELFLIFE "
                + " from dcp_stocktake_detail a "
                + " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno"
                + " left join dcp_goods_lang c on a.pluno=c.pluno and  a.eid=c.eid and c.lang_type='"+langType+"' "
                + " left join dcp_unit_lang d on a.punit=d.unit and a.eid=d.eid and d.lang_type='"+langType+"' "
                + " left join dcp_unit_lang e on a.baseunit=e.unit and a.eid=e.eid and e.lang_type='"+langType+"'"
                + " left join dcp_unit h on a.punit=h.unit and a.eid=h.eid"
                + " left join dcp_unit bu on a.baseunit=bu.unit and a.eid=bu.eid"
                + " left join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+langType+"' "
                + " left join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
                + " left join dcp_stocktake_detail_unit u1 on u1.eid=a.eid and u1.shopid=a.shopid and u1.stocktakeno=a.stocktakeno and u1.oitem=a.item"
                + " left join dcp_unit u2 on u2.eid=a.eid and u2.unit=u1.punit"
                + " left join dcp_unit_lang u3 on u3.eid=a.eid and u3.unit=u1.punit and u3.lang_type='"+langType+"'"
                + " left join dcp_goods_unit u4 on u4.eid=a.eid and u4.pluno=a.pluno and u4.ounit=u1.punit and u4.unit=b.baseunit"
                + " left join dcp_location i on i.eid=a.eid and i.organizationno=a.organizationno and i.location=a.location and a.warehouse=i.warehouse "
                + " where a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNO+"' and a.eid='"+eId+"' "
                //  【ID1026102】【詹记3.0】门店盘点单，单位顺序需要固定。目前詹记单位顺序显示混乱
                //  多单位查询排序结果和单据保存顺序一致   by jinzma 20220525
                //  + " order by a.item,u1.punit "
                + " order by a.item,u1.item "
        
        );
        
        return sqlbuf.toString();
    }

    protected String getStockTakeSql(DCP_StockTakeDetailReq req) throws Exception {

        StringBuffer sqlbuf = new StringBuffer();

        String stockTakeNo = req.getRequest().getStockTakeNo();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();

        sqlbuf.append(" "
                + " SELECT 1 as rw, stocktakeno as dno, D.STATUS AS plStatus, a.process_Erp_no as processERPNo, A.stocktakeNO as stocktakeNO,A.pTemplateNO,C.pTemplate_Name as pTemplateName,A.SHOPID as SHOPID,to_number(A.BDATE) as bDate,A.MEMO as memo,A.STATUS as status,A.DOC_TYPE as docType "
                + " ,A.OTYPE as otype ,A.OFNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO, "
                + "  A.createBy, A.modifyby,A.SubmitBy,A.ConfirmBy,A.CancelBy,A.AccountBy,A.Create_Time,A.Create_Date,A.Modify_Date,A.Modify_TIME, "
                + "  A.Submit_Date,A.Submit_Time,A.Confirm_Date,A.Confirm_Time,A.Cancel_Date,A.Cancel_Time,A.Account_Date,A.Account_TIME, "
                + "  b.name as createName, b1.name as modifyname,b2.name as Submitname,b3.name as Confirmname,b4.name as Cancelname, "
                + "  b5.name as Accountname,   "
                + " A.TOT_PQTY as totpqty,A.TOT_AMT as totamt,A.TOT_DISTRIAMT, A.TOT_CQTY as totcqty ,a.is_btake as isBTake,a.warehouse,  A.TASKWAY as taskway , A.NOTGOODSMODE as notgoodsmode ,taw.warehouse_name,A.PROCESS_STATUS,"
                + " A.UPDATE_TIME,Translate('1' USING NCHAR_CS) SUBMITSTATUS,A.IS_ADJUST_STOCK,A.substockimport,a.employeeid,e1.name as employeename,a.departid,d1.departname   "
                + " FROM DCP_STOCKTAKE A "
                + " LEFT JOIN dcp_employee B  ON A.EID=B.EID  AND A.CREATEBY=B.employeeno  "
                + " LEFT JOIN dcp_employee b1 ON a.EID=b1.EID AND a.modifyby=b1.employeeno  "
                + " LEFT JOIN dcp_employee b2 ON a.EID=b2.EID AND a.SubmitBy=b2.employeeno  "
                + " LEFT JOIN dcp_employee b3 ON a.EID=b3.EID AND a.ConfirmBy=b3.employeeno "
                + " LEFT JOIN dcp_employee b4 ON a.EID=b4.EID AND a.CancelBy=b4.employeeno "
                + " LEFT JOIN dcp_employee b5 ON a.EID=b5.EID AND a.AccountBy=b5.employeeno  "
                + " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND A.WAREHOUSE=TAW.WAREHOUSE AND   TAW.LANG_TYPE='"+ langType +"' "
                + " LEFT JOIN DCP_PTEMPLATE C ON A.PTEMPLATENO=C.PTEMPLATENO AND A.EID=C.EID AND C.DOC_TYPE='1' "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " LEFT JOIN DCP_ADJUST D ON A.EID=D.EID AND A.SHOPID=D.SHOPID AND A.STOCKTAKENO=D.OFNO  WHERE 1=1 ");

        sqlbuf.append(" AND  a.organizationno='"+ req.getOrganizationNO()+"' " +
                " and a.stocktakeno='"+stockTakeNo+"' ");


        sqlbuf.append(" AND a.SHOPID='"+ shopId +"' ");
        sqlbuf.append(" AND a.EID='"+ eId +"'  ");
         return sqlbuf.toString();

    }


}
