package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProcessTaskDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskDetailQueryRes;
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

public class DCP_ProcessTaskDetailQuery  extends SPosBasicService<DCP_ProcessTaskDetailQueryReq, DCP_ProcessTaskDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessTaskDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskDetailQueryReq>(){};
    }

    @Override
    protected DCP_ProcessTaskDetailQueryRes getResponseType() {
        return new DCP_ProcessTaskDetailQueryRes();
    }

    @Override
    protected DCP_ProcessTaskDetailQueryRes processJson(DCP_ProcessTaskDetailQueryReq req) throws Exception {
        String sql;
        //查询条件
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String processTaskNo = req.getRequest().getProcessTaskNo();

        //查询资料
        DCP_ProcessTaskDetailQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());

                StringBuffer sqlbuf = new StringBuffer();
                sqlbuf.append(""
                        + " select A.PROCESSTASKNO,A.bDate,A.memo,A.pDate,A.pTemplateNO,A.status,A.createBy,A.create_date AS createDate,"
                        + " A.create_time AS createTime, "
                        + " A.tot_pqty AS totPqty,A.tot_amt AS totAmt,A.TOT_DISTRIAMT,A.tot_cqty AS totCqty,b.item,b.pluNO,b.pqty,  "
                        + " b.punit,b.baseunit,b.unit_ratio AS unitRatio,b.price,b.amt,b.DISTRIAMT,bul.udlength as baseunitudlength, "
                        + " D.plu_name AS pluName,E.uname AS punitName,E1.uname AS baseUnitName,f.op_name AS createByName,"
                        + " b.featureNo,fn.featurename,b.baseqty,"
                        + " B.MUL_QTY as mulQty,"
                        + " J.PTEMPLATE_NAME AS pTemplateName, "
                        + " a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , a.modifyby , "
                        + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                        + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                        + " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
                        + " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime ,  "
                        + "	f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName , "
                        + " f4.op_name as submitByName ,f5.op_name as accountByName ,"
                        + " image.listimage , gul.spec, a.update_time , a.process_status,a.WAREHOUSE,k1.warehouse_name as WAREHOUSENAME , "
                        + " a.MATERIALWAREHOUSE,k2.warehouse_name as MATERIALWAREHOUSENAME , "
                        + " b.DISTRIPRICE , h.udlength AS  PUNIT_UDLENGTH,a.PROCESSPLANNO,a.PROCESSPLANNAME,a.TASK0NO,a.DTNO, " +
                        " dt1.dtname,dt1.begin_time,dt1.end_time,b.PSTOCKIN_QTY,a.LOAD_DOCTYPE loadDocType,a.LOAD_DOCNO as loaddocno,a.ofno,a.otype,a.isRefundOrder,a.employeeid,a.departid," +
                        "  a.mainTaskNo,a.prodtype,i.name as employeename,j.departname,b.endTime,b.saleTime,b.scrapQty,b.minQty,b.dispType,b.semiWoType,b.oddValue," +
                        " b.remainType,b.bomNo,b.versionNum,b.beginDate,b.endDate,b.dispatchQty,b.dispatchStatus,b.mainTaskNo,b.mainItem"
                        + " from DCP_PROCESSTASK A "
                        + " INNER JOIN DCP_PROCESSTASK_DETAIL b ON A .PROCESSTASKNO = b.PROCESSTASKNO AND A .organizationno = b.organizationno  and a.EID=b.EID AND A .SHOPID = b.SHOPID and A.BDATE=b.BDATE "
                        + " INNER JOIN DCP_GOODS c ON b.PLUNO = c.PLUNO AND b.EID = c.EID "
                        + " LEFT JOIN DCP_GOODS_LANG D ON b.PLUNO = D .PLUNO AND b.EID = D.EID AND D .LANG_TYPE = '"+langType+"' "
                        + " LEFT JOIN DCP_UNIT_LANG E ON b.PUNIT = E.UNIT AND b.EID = E.EID AND E.LANG_TYPE ='"+langType+"' "
                        + " LEFT JOIN DCP_UNIT_LANG E1 ON b.baseUNIT = E1.UNIT AND b.EID = E1.EID AND E1.LANG_TYPE ='"+langType+"' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f ON A .EID = f.EID AND A.createby = f.opno and f.lang_type='"+langType+"' "
                        //2018-11-20 新增以下几行， 用于查modifyByName 等字段
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON A .EID = f1.EID AND A.modifyBy = f1.opno AND F1.LANG_TYPE  = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON A .EID = f2.EID AND A.cancelby = f2.opno AND F2.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f3 ON A .EID = f3.EID AND A.confirmby = f3.opno AND F3.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f4 ON A .EID = f4.EID AND A.submitby = f4.opno AND F4.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN PLATFORM_STAFFS_LANG f5 ON A .EID = f5.EID AND A.accountby = f5.opno AND F5.LANG_TYPE = '" + langType + "' "
                        + " LEFT JOIN DCP_PTEMPLATE J ON A.EID=J.EID AND A.PTEMPLATENO=J.PTEMPLATENO  AND j.doc_type = 2  "
                        + " left join DCP_WAREHOUSE_lang k1 on a.EID=k1.EID and a.SHOPID=k1.organizationno and a.WAREHOUSE=k1.warehouse and k1.lang_type='"+langType +"' "
                        + " left join DCP_WAREHOUSE_lang k2 on a.EID=k2.EID and a.SHOPID=k2.organizationno and a.MATERIALWAREHOUSE=k2.warehouse and k2.lang_type='"+langType +"' "
                        + " LEFT JOIN DCP_UNIT H ON b.PUNIT = H.UNIT AND b.EID = H.EID "
                        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                        + " left join dcp_unit bul on b.eid=bul.eid and b.baseunit=bul.unit"
                        + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                        + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                        + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' " +
                        " left join DCP_DINNERTIME dt1 on a.eid=dt1.eid and a.organizationno=dt1.shopid and a.dtno=dt1.dtno " +
                        " left join dcp_employee i on i.eid=a.eid and i.employeeno=a.employeeid" +
                        " left join dcp_department_lang j on j.eid=a.eid and j.departno=a.departid and j.lang_type='"+req.getLangType()+"' "
                );

                //2018-11-09 添加 日期查询条件
                sqlbuf.append(" WHERE a.processTaskno='"+processTaskNo+"'  ");
                sqlbuf.append(" and a.SHOPID='"+shopId+"'");
                sqlbuf.append(" and a.EID='"+eId+"'");

                sql=sqlbuf.toString();

                List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
                if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                    // 拼接返回图片路径  by jinzma 20210705
                    String isHttps= PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                    String httpStr=isHttps.equals("1")?"https://":"http://";
                    String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                    if (domainName.endsWith("/")) {
                        domainName = httpStr + domainName + "resource/image/";
                    }else{
                        domainName = httpStr + domainName + "/resource/image/";
                    }

                    //单头主键字段
                    Map<String, Boolean> condition = new HashMap<>(); //查询条件
                    condition.put("PROCESSTASKNO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQDataDetail, condition);

                    for (Map<String, Object> oneData : getQHeader)
                    {
                        DCP_ProcessTaskDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();
                        oneLv1.setDatas(new ArrayList<>());

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

                        oneLv1.setLoadDocType(oneData.get("LOADDOCTYPE").toString());
                        oneLv1.setLoadDocNo(oneData.get("LOADDOCNO").toString());
                        oneLv1.setOfNo(oneData.get("OFNO").toString());
                        oneLv1.setOType(oneData.get("OTYPE").toString());
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

                        for (Map<String, Object> oneData2 : getQDataDetail)
                        {
                            //过滤属于此单头的明细
                            if(processTaskNO.equals(oneData2.get("PROCESSTASKNO")))
                            {
                                DCP_ProcessTaskDetailQueryRes.level2Elm oneLv2 = res.new level2Elm();
                                String item = oneData2.get("ITEM").toString();
                                String pluNO = oneData2.get("PLUNO").toString();
                                String pluName = oneData2.get("PLUNAME").toString();
                                String punit = oneData2.get("PUNIT").toString();
                                String punitName = oneData2.get("PUNITNAME").toString();
                                String pqty = oneData2.get("PQTY").toString();
                                String price = oneData2.get("PRICE").toString();
                                String amt = oneData2.get("AMT").toString();
                                String mulQty = oneData2.get("MULQTY").toString();
                                String baseUnit = oneData2.get("BASEUNIT").toString();
                                String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                                String unitRatio = oneData2.get("UNITRATIO").toString();
                                String listImage = oneData2.get("LISTIMAGE").toString();
                                if (!Check.Null(listImage)){
                                    listImage = domainName+listImage;
                                }
                                String spec = oneData2.get("SPEC").toString();
                                String distriPrice = oneData2.get("DISTRIPRICE").toString();
                                String distriAmt = oneData2.get("DISTRIAMT").toString();
                                String punitUDLength = oneData2.get("PUNIT_UDLENGTH").toString();
                                String baseQty = oneData2.get("BASEQTY").toString();
                                String featureNo = oneData2.get("FEATURENO").toString();
                                String featureName = oneData2.get("FEATURENAME").toString();

                                //单身赋值
                                oneLv2.setItem(item);
                                oneLv2.setPluNo(pluNO);
                                oneLv2.setPluName(pluName);
                                oneLv2.setPunit(punit);
                                oneLv2.setPunitName(punitName);
                                oneLv2.setPqty(pqty);
                                oneLv2.setPrice(price);
                                oneLv2.setAmt(amt);
                                oneLv2.setMulQty(mulQty);
                                oneLv2.setBaseUnit(baseUnit);
                                oneLv2.setBaseUnitName(baseUnitName);
                                oneLv2.setUnitRatio(unitRatio);
                                oneLv2.setListImage(listImage);
                                oneLv2.setSpec(spec);
                                oneLv2.setDistriPrice(distriPrice);
                                oneLv2.setDistriAmt(distriAmt);
                                oneLv2.setPunitUdLength(punitUDLength);
                                oneLv2.setBaseQty(baseQty);
                                oneLv2.setFeatureNo(featureNo);
                                oneLv2.setFeatureName(featureName);

                                //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                                oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
                                oneLv2.setPStockInQty(oneData2.get("PSTOCKIN_QTY").toString());


                                oneLv2.setSaleTime(oneData2.get("SALETIME").toString());
                                oneLv2.setScrapQty(oneData2.get("SCRAPQTY").toString());
                                oneLv2.setMinQty(oneData2.get("MINQTY").toString());
                                oneLv2.setDispType(oneData2.get("DISPTYPE").toString());
                                oneLv2.setSemiWoType(oneData2.get("SEMIWOTYPE").toString());
                                oneLv2.setOddValue(oneData2.get("ODDVALUE").toString());
                                oneLv2.setRemainType(oneData2.get("REMAINTYPE").toString());
                                oneLv2.setBomNo(oneData2.get("BOMNO").toString());
                                oneLv2.setVersionNum(oneData2.get("VERSIONNUM").toString());
                                oneLv2.setBeginDate(oneData2.get("BEGINDATE").toString());
                                oneLv2.setEndDate(oneData2.get("ENDDATE").toString());
                                oneLv2.setDispatchQty(oneData2.get("DISPATCHQTY").toString());
                                oneLv2.setDispatchStatus(oneData2.get("DISPATCHSTATUS").toString());
                                oneLv2.setMainTaskNo(oneData2.get("MAINTASKNO").toString());
                                oneLv2.setMainItem(oneData2.get("MAINITEM").toString());

                                //添加单身
                                oneLv1.getDatas().add(oneLv2);
                            }
                        }
                        //添加单头
                        res.getDatas().add(oneLv1);
                    }

            }

            return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ProcessTaskDetailQueryReq req) throws Exception {
        return null;
    }


}
