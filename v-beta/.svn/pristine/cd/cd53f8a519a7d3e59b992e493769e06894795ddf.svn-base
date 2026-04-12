package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_LStockOutDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_LStockOutDetailQuery extends SPosBasicService<DCP_LStockOutDetailQueryReq, DCP_LStockOutDetailQueryRes>
{
    @Override
    protected boolean isVerifyFail(DCP_LStockOutDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_LStockOutDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_LStockOutDetailQueryReq>(){};
    }

    @Override
    protected DCP_LStockOutDetailQueryRes getResponseType() {
        return new DCP_LStockOutDetailQueryRes();
    }

    @Override
    protected DCP_LStockOutDetailQueryRes processJson(DCP_LStockOutDetailQueryReq req) throws Exception {

        //查询条件
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String lStockOutNo = req.getRequest().getLStockOutNo();
            //查询资料
        DCP_LStockOutDetailQueryRes res = this.getResponse();

            //给分页字段赋值
        String sql ="";
        res.setDatas(new ArrayList<>());
        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                        + " SELECT processERPNO,lStockOutNO,bDate,memo,status,totPqty,TOT_DISTRIAMT,LSTOCKOUTNO_ORIGIN,LSTOCKOUTNO_REFUND, "
                        + " totAmt,totCqty,item,pluNO,pqty,rqty,baseqty,punit,baseunit,unitRatio,price,amt,distriAmt,FEATURENO,FEATURENAME, "
                        + " pluName,punitName,baseunitName,bsNO,bsName,A_Warehouse,A_WarehouseName,B_Warehouse,B_WarehouseName,"
                        + " PROCESS_STATUS,UPDATE_TIME, "
                        + " LISTIMAGE,SPEC,"
                        + " CREATEBY,CREATEDATE,CREATETIME,CREATENAME,"
                        + " MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME,"
                        + " SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME,"
                        + " CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME,"
                        + " CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME,"
                        + " ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME, PTEMPLATENO, PTEMPLATENAME,  "
                        + " BATCH_NO,PROD_DATE,DISTRIPRICE,ISBATCH,PUNIT_UDLENGTH,FILENAME,baseunitudlength,PQTY_REFUND,ITEM_ORIGIN,PQTY_ORIGIN,FEEOBJECTTYPE,FEEOBJECTID,FEEOBJECTNAME,FEE,BFEENO,EMPLOYEEID,EMPLOYEENAME,DEPARTID,DEPARTNAME,FEENAME,STOCKQTY,LOCATION,EXPDATE,locationname from ( "
                        + " SELECT a.process_erp_no as processERPNO, A.lstockoutno,A.bDate,A.memo,A.status, "
                        + " A.tot_pqty AS totPqty,A.tot_amt AS totAmt,A.tot_cqty AS totCqty,A.TOT_DISTRIAMT,A.LSTOCKOUTNO_ORIGIN,A.LSTOCKOUTNO_REFUND,b.item,b.pluNO,"
                        + " b.pqty,nvl(b.rqty,0) as rqty,b.baseqty, "
                        + " b.punit,b.baseunit,b.unit_ratio AS unitRatio,b.price,b.amt,b.distriAmt,image.listimage, "
                        + " D.plu_name AS pluName,gul.spec,E.uname AS punitName,F.uname AS baseunitName,b.bsno,I.REASON_NAME as bsName, "
                        + " A.Warehouse as A_Warehouse,W1.Warehouse_Name as A_WarehouseName,b.warehouse as B_Warehouse,"
                        + " W2.Warehouse_Name as B_WarehouseName,A.PROCESS_STATUS,A.UPDATE_TIME, "
                        + " A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,e3.op_NAME as CREATENAME,"
                        + " A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,e4.op_name as MODIFYNAME,"
                        + " A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,e5.op_name as SUBMITNAME,"
                        + " A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,e6.op_name as CONFIRMNAME,"
                        + " A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,e7.op_name as CANCELNAME,"
                        + " A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,e2.op_name as ACCOUNTNAME, "
                        + " A.PTEMPLATENO , p.PTEMPLATE_NAME as PTEMPLATENAME,b.featureno,fn.featurename,  "
                        + " B.BATCH_NO,B.PROD_DATE,B.DISTRIPRICE,C.ISBATCH,HH.UDLENGTH AS PUNIT_UDLENGTH,IMAGE.FILENAME,"
                        + " bul.udlength as baseunitudlength,b.PQTY_REFUND,b.ITEM_ORIGIN,b.PQTY_ORIGIN,b.location,b.expdate,a.feeObjectType,a.feeObjectId,a.fee,a.BFEENO,a.employeeId,a.departId, "
                        + " e1.name as employeeName,d1.departname,case when a.feeobjecttype='1' then o1.departname else b1.sname end as feeobjectname,fee.fee_name as feename,bsd.qty as stockqty,j.locationname  "
                        + " FROM DCP_LSTOCKOUT A "
                        + " INNER JOIN DCP_LSTOCKOUT_DETAIL b ON A.lstockoutno = b.lstockoutno AND A.organizationno = b.organizationno and A.EID = b.EID AND A .SHOPID = b.SHOPID and A.BDATE=b.BDATE "
                        + " LEFT JOIN DCP_LSTOCKOUT_IMAGE IMAGE ON A.EID=IMAGE.EID AND A.SHOPID=IMAGE.SHOPID AND A.LSTOCKOUTNO=IMAGE.LSTOCKOUTNO"
                        + " LEFT JOIN DCP_PTEMPLATE p ON A.EID=p.EID AND A.PTEMPLATENO=p.PTEMPLATENO AND p.DOC_TYPE='4' "
                        + " LEFT JOIN DCP_GOODS c ON b.PLUNO = c.PLUNO AND b.EID = c.EID "
                        + " LEFT JOIN DCP_GOODS_LANG D ON b.PLUNO = D.PLUNO AND b.EID = D.EID AND D.LANG_TYPE = '"+langType+"' "
                        + " LEFT JOIN DCP_UNIT_LANG E ON b.PUNIT = E.UNIT AND b.EID = E.EID AND E.LANG_TYPE ='"+langType+"' "
                        + " LEFT JOIN DCP_UNIT_LANG F ON b.baseUNIT = F.UNIT AND b.EID = F.EID AND F.LANG_TYPE ='"+langType+"' "
                        + " LEFT JOIN DCP_REASON_LANG I ON B.EID = I.EID AND B.BSNO = I.BSNO and I.lang_type='"+langType+"' and I.bstype='1' "
                        + " LEFT JOIN DCP_WAREHOUSE_LANG W1 ON A.EID = W1.EID AND A.organizationno=W1.organizationno AND A.WAREHOUSE=W1.WAREHOUSE AND W1.LANG_TYPE='"+langType+"' "
                        + " LEFT JOIN DCP_WAREHOUSE_LANG W2 ON B.EID = W2.EID AND B.organizationno=W2.organizationno AND B.WAREHOUSE=W2.WAREHOUSE AND W2.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b0 ON A.EID=b0.EID AND A.CREATEBY=b0.OPNO AND b0.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b1 ON A.EID=b1.EID AND A.modifyby=b1.OPNO AND b1.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b2 ON A.EID=b2.EID AND A.SubmitBy=b2.OPNO AND b2.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b3 ON A.EID=b3.EID AND A.ConfirmBy=b3.OPNO AND b3.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b4 ON A.EID=b4.EID AND A.CancelBy=b4.OPNO AND b4.LANG_TYPE='"+langType+"' "
                        //+ " LEFT JOIN PLATFORM_STAFFS_LANG b5 ON A.EID=b5.EID AND A.AccountBy=b5.OPNO AND b5.LANG_TYPE='"+langType+"' "
                        + " LEFT JOIN DCP_UNIT HH ON HH.EID = b.EID AND HH.unit = b.punit "
                        + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                        + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                        + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                        + " left join dcp_unit bul on a.eid=bul.eid and b.baseunit=bul.unit "
                        + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid "
                        + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"' "
                        + " left join DCP_DEPARTMENT_LANG o1 on o1.eid=a.eid and o1.departno=a.feeObjectId and o1.lang_type='"+langType+"' "
                        + " left join DCP_BIZPARTNER b1 on b1.eid=a.eid and b1.BIZPARTNERNO=a.feeobjectid "
                        + " left join DCP_FEE_LANG fee on fee.eid=a.eid and fee.fee=a.fee and fee.lang_type='"+langType+"' "

                        + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.OPNO=a.ACCOUNTBY and e2.lang_type='"+req.getLangType()+"' "
                        + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.OPNO=a.CREATEBY and e3.lang_type='"+req.getLangType()+"' "
                        + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.OPNO=a.MODIFYBY and e4.lang_type='"+req.getLangType()+"' "
                        + " left join PLATFORM_STAFFS_LANG e5 on e5.eid=a.eid and e5.OPNO=a.SUBMITBY and e5.lang_type='"+req.getLangType()+"' "
                        + " left join PLATFORM_STAFFS_LANG e6 on e6.eid=a.eid and e6.OPNO=a.CONFIRMBY and e6.lang_type='"+req.getLangType()+"' "
                        + " left join PLATFORM_STAFFS_LANG e7 on e7.eid=a.eid and e7.OPNO=a.CANCELBY and e7.lang_type='"+req.getLangType()+"' "
                        + " left join MES_BATCH_STOCK_DETAIL bsd on bsd.eid=a.eid and bsd.organizationno=a.organizationno and bsd.pluno=b.pluno and bsd.featureno=b.featureno " +
                          " and bsd.warehouse=b.warehouse and bsd.baseunit=b.baseunit and bsd.batchno=b.batch_no and bsd.location=b.location " +
                "left join dcp_location j on j.eid=a.eid and j.location=b.location and j.warehouse=b.warehouse and j.ORGANIZATIONNO=a.organizationno "

        );

        sqlbuf.append(" WHERE a.LSTOCKOUTNO='"+lStockOutNo+"'");
        sqlbuf.append(" and a.SHOPID='"+shopId+"'");
        sqlbuf.append(" and a.EID='"+eId+"'");





                sqlbuf.append(" ) ");
                sql = sqlbuf.toString();

                List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
                if (getQDataDetail != null && !getQDataDetail.isEmpty()){

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
                    condition.put("LSTOCKOUTNO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

                    //图片主键字段
                    condition.clear();
                    condition.put("LSTOCKOUTNO", true);
                    condition.put("FILENAME", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQFileList = MapDistinct.getMap(getQDataDetail, condition);

                    //图片主键字段
                    condition.clear();
                    condition.put("LSTOCKOUTNO", true);
                    condition.put("ITEM", true);
                    //调用过滤函数
                    getQDataDetail = MapDistinct.getMap(getQDataDetail, condition);

                    for (Map<String, Object> oneData : getQHeader) {
                        DCP_LStockOutDetailQueryRes.level1Elm oneLv1 = new DCP_LStockOutDetailQueryRes().new level1Elm();
                        oneLv1.setDatas(new ArrayList<>());
                        oneLv1.setFileList(new ArrayList<>());
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
                        oneLv1.setLStockOutNo(lStockOutNO);
                        oneLv1.setBDate(bDate);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(a_status);
                        oneLv1.setWarehouse(warehouse);
                        oneLv1.setWarehouseName(warehouseName);
                        oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                        oneLv1.setPTemplateNo(pTemplateNO);
                        oneLv1.setPTemplateName(pTemplateName);
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
                        oneLv1.setLStockoutNo_origin(lstockoutno_origin);
                        oneLv1.setLStockoutNo_refund(lstockoutno_refund);
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

                        //feeObjectType,feeObjectId,feeObjectName, fee,feeName,feeBillNo,employeeId,employeeName, departId,departName


                        for (Map<String, Object> oneData2 : getQDataDetail) {
                            //过滤属于此单头的明细
                            if(lStockOutNO.equals(oneData2.get("LSTOCKOUTNO"))) {
                                DCP_LStockOutDetailQueryRes.level2Elm oneLv2 = new DCP_LStockOutDetailQueryRes().new level2Elm();
                                String item = oneData2.get("ITEM").toString();
                                String pluNO = oneData2.get("PLUNO").toString();
                                String pluName = oneData2.get("PLUNAME").toString();
                                String punit = oneData2.get("PUNIT").toString();
                                String punitName = oneData2.get("PUNITNAME").toString();
                                String pqty = oneData2.get("PQTY").toString();
                                String rqty =  oneData2.get("RQTY").toString();
                                String price = oneData2.get("PRICE").toString();
                                String amt = oneData2.get("AMT").toString();
                                String bsNO = oneData2.get("BSNO").toString();
                                String bsName = oneData2.get("BSNAME").toString();
                                String unitRatio = oneData2.get("UNITRATIO").toString();
                                String baseUnit = oneData2.get("BASEUNIT").toString();
                                String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                                String warehouse2 = oneData2.get("B_WAREHOUSE").toString();
                                String warehouseName2 = oneData2.get("B_WAREHOUSENAME").toString();
                                String spec = oneData2.get("SPEC").toString();
                                String stockQty = oneData2.get("STOCKQTY").toString();
                                String listImage = oneData2.get("LISTIMAGE").toString();
                                if (!Check.Null(listImage)){
                                    listImage = domainName + listImage;
                                }
                                String batchNO = oneData2.get("BATCH_NO").toString();
                                String isBatch = oneData2.get("ISBATCH").toString();
                                String prodDate = oneData2.get("PROD_DATE").toString();
                                String distriPrice = oneData2.get("DISTRIPRICE").toString();
                                String distriAmt = oneData2.get("DISTRIAMT").toString();
                                String punitUDLength = oneData2.get("PUNIT_UDLENGTH").toString();
                                String baseQty  = oneData2.get("BASEQTY").toString();
                                String featureNo = oneData2.get("FEATURENO").toString();
                                String featureName = oneData2.get("FEATURENAME").toString();
                                String pqty_refund = oneData2.get("PQTY_REFUND").toString();
                                String item_origin = oneData2.get("ITEM_ORIGIN").toString();
                                String pqty_origin = oneData2.get("PQTY_ORIGIN").toString();
                                String location = oneData2.get("LOCATION").toString();
                                String expDate = oneData2.get("EXPDATE").toString();
                                String locationName = oneData2.get("LOCATIONNAME").toString();

                                //单身赋值
                                oneLv2.setItem(item);
                                oneLv2.setPluNo(pluNO);
                                oneLv2.setPluName(pluName);
                                oneLv2.setPunit(punit);
                                oneLv2.setPunitName(punitName);
                                oneLv2.setPqty(pqty);
                                oneLv2.setRqty(rqty);
                                oneLv2.setPrice(price);
                                oneLv2.setAmt(amt);
                                oneLv2.setBsNo(bsNO);
                                oneLv2.setBsName(bsName);
                                oneLv2.setBaseUnit(baseUnit);
                                oneLv2.setBaseUnitName(baseUnitName);
                                oneLv2.setUnitRatio(unitRatio);
                                oneLv2.setWarehouse(warehouse2);
                                oneLv2.setWarehouseName(warehouseName2);
                                oneLv2.setSpec(spec);
                                oneLv2.setListImage(listImage);
                                oneLv2.setBatchNo(batchNO);
                                oneLv2.setIsBatch(isBatch);
                                oneLv2.setDistriPrice(distriPrice);
                                oneLv2.setProdDate(prodDate);
                                oneLv2.setDistriAmt(distriAmt);
                                oneLv2.setPunitUdLength(punitUDLength);
                                oneLv2.setFeatureNo(featureNo);
                                oneLv2.setFeatureName(featureName);
                                oneLv2.setBaseQty(baseQty);
                                oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
                                oneLv2.setPqty_refund(pqty_refund);
                                oneLv2.setItem_origin(item_origin);
                                oneLv2.setPqty_origin(pqty_origin);
                                oneLv2.setExpDate(expDate);
                                oneLv2.setLocation(location);
                                oneLv2.setStockQty(stockQty);
                                oneLv2.setLocationName(locationName);

                                //添加单身
                                oneLv1.getDatas().add(oneLv2);
                            }
                        }

                        //添加报损图片
                        for (Map<String, Object> oneFileList : getQFileList) {
                            if(lStockOutNO.equals(oneFileList.get("LSTOCKOUTNO").toString())) {
                                String fileName = oneFileList.get("FILENAME").toString();
                                if (!Check.Null(fileName)) {
                                    DCP_LStockOutDetailQueryRes.level2ElmFileList oneLv2 = res.new level2ElmFileList();
                                    oneLv2.setFileName(fileName);
                                    oneLv1.getFileList().add(oneLv2);
                                }
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
    protected String getQuerySql(DCP_LStockOutDetailQueryReq req) throws Exception {
        return null;
    }


}
