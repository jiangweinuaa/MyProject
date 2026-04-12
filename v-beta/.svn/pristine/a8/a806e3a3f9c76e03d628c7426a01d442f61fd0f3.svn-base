package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PStockInDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PStockInDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PStockInDetailQuery extends SPosBasicService<DCP_PStockInDetailQueryReq, DCP_PStockInDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PStockInDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_PStockInDetailQueryReq.levelElm request = req.getRequest();
        String pStockInNo = request.getPStockInNo();
        String taskNo = request.getTaskNo();
        if(Check.Null(pStockInNo)&&Check.Null(taskNo)){
            isFail=true;
            errMsg.append("pStockInNo和taskNo不能同时为空!");
        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_PStockInDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PStockInDetailQueryReq>(){};
    }

    @Override
    protected DCP_PStockInDetailQueryRes getResponseType() {
        return new DCP_PStockInDetailQueryRes();
    }

    @Override
    protected DCP_PStockInDetailQueryRes processJson(DCP_PStockInDetailQueryReq req) throws Exception {
        //2018-08-07添加docType;
        String docType = req.getRequest().getDocType(); //0-完工入库  1-组合单   2-拆解单   3-转换合并   4-合并转换

            //查询资料
            DCP_PStockInDetailQueryRes res = this.getResponse();
            //单头单身查询
            String sql =Check.Null(req.getRequest().getPStockInNo())?this.getTaskQuerySql(req): this.getQuerySql(req);//查询完工入库单头和商品明细、原料子表   BY JZMA 20190730
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);

            String batchSql = this.getBatchSql(req);
            List<Map<String, Object>> getBatchData = this.doQueryData(batchSql,null);

            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {


                List<Map<String, Object>> plus = new ArrayList<>();
                Map<String, Boolean> condition1 = new HashMap<>(); //查詢條件
                condition1.put("MATERIAL_PLUNO", true);
                List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition1);
                for (Map<String, Object> onePlu :getQPlu ) {
                    Map<String, Object> plu = new HashMap<>();
                    plu.put("PLUNO", onePlu.get("M_MATERIAL_PLUNO").toString());
                    plu.put("PUNIT", onePlu.get("M_MATERIAL_PUNIT").toString());
                    plu.put("BASEUNIT", onePlu.get("M_MATERIAL_BASEUNIT").toString());
                    plu.put("UNITRATIO", onePlu.get("M_MATERIAL_UNIT_RATIO").toString());
                    plus.add(plu);
                }
                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,req.geteId(),req.getBELFIRM(), req.getOrganizationNO(),plus,req.getBELFIRM());


                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                // 有資料，取得詳細內容
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                condition.put("DNO", true);



                //调用过滤函数
                List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQData, condition);

                for (Map<String, Object> oneData : getQHeader) {
                    DCP_PStockInDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();

                    List<String> pluNos=new ArrayList<>();
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



                    //单头原料仓只有加工任务会给值，当单头原料仓为空，取子料对应的原料仓
                    String materialWarehouseNO = oneData.get("MATERIALWAREHOUSE").toString();
                    String materialWarehouseName = oneData.get("MATERIALWAREHOUSENAME").toString();
                    String materialIsLocation=oneData.get("MATERIAL_ISLOCATION").toString();
                    if(Check.Null(materialWarehouseNO)) {
                        materialWarehouseNO = oneData.get("M_MATERIALWAREHOUSE").toString();
                        materialWarehouseName = oneData.get("M_MATERIALWAREHOUSENAME").toString();
                    }
                    if(Check.Null(materialIsLocation)){
                        materialIsLocation=oneData.get("M_MATERIAL_ISLOCATION").toString();
                    }


                    //2018-08-07添加docType
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

                    oneLv1.setMaterialWarehouseNo(materialWarehouseNO);
                    oneLv1.setMaterialWarehouseName(materialWarehouseName);
                    oneLv1.setMaterial_IsLocation(materialIsLocation);

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

                    //2018-08-08添加docType
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
                    oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
                    oneLv1.setCreateDateTime(PosPub.combineDateTime(oneLv1.getCreateDate(), oneLv1.getCreateTime()));
                    oneLv1.setAccountDateTime(PosPub.combineDateTime(oneLv1.getAccountDate(), oneLv1.getAccountTime()));
                    oneLv1.setConfirmDateTime(PosPub.combineDateTime(oneLv1.getConfirmDate(), oneLv1.getConfirmTime()));
                    oneLv1.setCancelDateTime(PosPub.combineDateTime(oneLv1.getCancelDate(), oneLv1.getCancelTime()));
                    oneLv1.setModifyDateTime(PosPub.combineDateTime(oneLv1.getModifyDate(), oneLv1.getModifyTime()));
                    oneLv1.setSubmitDateTime(PosPub.combineDateTime(oneLv1.getSubmitDate(), oneLv1.getSubmitTime()));
                    oneLv1.setProdType(oneData.get("PRODTYPE").toString());
                    oneLv1.setOOfNo(oneData.get("OOFNO").toString());
                    oneLv1.setOOType(oneData.get("OOTYPE").toString());

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

                    //单身主键字段
                    Map<String, Object> condition2 = new HashMap<>(); //查詢條件
                    condition2.put("DNO", doNO);

                    //调用过滤函数
                    List<Map<String, Object>> getDetailDatas=MapDistinct.getWhereMap(getQData, condition2, true);

                    //过滤商品重复，一个商品对应多个原料
                    condition = new HashMap<>(); //查詢條件
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    condition.put("BATCH_NO", true);
                    condition.put("PUNIT", true);
                    condition.put("BSNO", true);
                    condition.put("ITEM", true);
                    getDetailDatas=MapDistinct.getMap(getDetailDatas, condition);

                    oneLv1.setDatas(new ArrayList<>());
                    for (Map<String, Object> oneDataDetail : getDetailDatas) {
                        DCP_PStockInDetailQueryRes.level2Elm oneLv2 = res.new level2Elm();
                        oneLv2.setMaterial(new ArrayList<>());

                        String item = oneDataDetail.get("ITEM").toString();
                        String oItem = oneDataDetail.get("OITEM").toString();
                        String pluNO = oneDataDetail.get("PLUNO").toString();
                        String pluName = oneDataDetail.get("PLUNAME").toString();
                        String punit = oneDataDetail.get("PUNIT").toString();
                        String punitName = oneDataDetail.get("PUNITNAME").toString();
                        String baseUnit = oneDataDetail.get("BASEUNIT").toString();
                        String baseUnitName = oneDataDetail.get("BASEUNITNAME").toString();
                        String unitRatio = oneDataDetail.get("UNITRATIO").toString();
                        String pqty = oneDataDetail.get("PQTY").toString();
                        String price = oneDataDetail.get("PRICE").toString();
                        String amt = oneDataDetail.get("AMT").toString();
                        String distriAmt = oneDataDetail.get("DISTRIAMT").toString();
                        String mulQty = oneDataDetail.get("MULQTY").toString();
                        String listImage = oneDataDetail.get("LISTIMAGE").toString();
                        if (!Check.Null(listImage)){
                            listImage = domainName+listImage;
                        }
                        String spec = oneDataDetail.get("SPEC").toString();
                        String pStockInQty = "";
                        if(docType.equals("0")) {
                            pStockInQty = oneDataDetail.get("PSTOCKINQTY").toString();
                        }
                        String bsNO = oneDataDetail.get("BSNO").toString();
                        String bsName = oneDataDetail.get("BSNAME").toString();
                        String taskQty = oneDataDetail.get("TASKQTY").toString();
                        if (!PosPub.isNumeric(taskQty)) {
                            taskQty="0";
                        }
                        String scrapQty = oneDataDetail.get("SCRAPQTY").toString();

                        String batchNO = oneDataDetail.get("BATCH_NO").toString();
                        String isBatch = oneDataDetail.get("ISBATCH").toString();
                        String prodDate = oneDataDetail.get("PROD_DATE").toString();
                        String distriPrice = oneDataDetail.get("DISTRIPRICE").toString();
                        String pqty_origin = oneDataDetail.get("PQTY_ORIGIN").toString();
                        String pqty_refund = oneDataDetail.get("PQTY_REFUND").toString();
                        if(pqty_origin==null||pqty_origin.trim().isEmpty()) {
                            pqty_origin = "0";
                        }
                        if(pqty_refund==null||pqty_refund.trim().isEmpty()) {
                            pqty_refund = "0";
                        }
                        String scrapQty_origin = oneDataDetail.get("SCRAP_QTY_ORIGIN").toString();
                        String scrapQty_refund = oneDataDetail.get("SCRAP_QTY_REFUND").toString();
                        if(scrapQty_origin==null||scrapQty_origin.trim().isEmpty()) {
                            scrapQty_origin = "0";
                        }
                        if(scrapQty_refund==null||scrapQty_refund.trim().isEmpty()) {
                            scrapQty_refund = "0";
                        }

                        String punitUDLength = oneDataDetail.get("PUNIT_UDLENGTH").toString();
                        String featureNo = oneDataDetail.get("FEATURENO").toString();
                        String featureName = oneDataDetail.get("FEATURENAME").toString();
                        String baseQty  = oneDataDetail.get("BASEQTY").toString();

                        //单身赋值
                        oneLv2.setItem(item);
                        oneLv2.setOItem(oItem);
                        oneLv2.setPluNo(pluNO);
                        oneLv2.setPluName(pluName);
                        oneLv2.setPunit(punit);
                        oneLv2.setPunitName(punitName);
                        oneLv2.setBaseUnit(baseUnit);
                        oneLv2.setBaseUnitName(baseUnitName);
                        oneLv2.setBaseUnitUdLength(oneDataDetail.get("BASEUNITUDLENGTH").toString());
                        oneLv2.setUnitRatio(unitRatio);
                        oneLv2.setPqty(pqty);
                        oneLv2.setPrice(price);
                        oneLv2.setAmt(amt);
                        oneLv2.setDistriAmt(distriAmt);
                        oneLv2.setMulQty(mulQty);
                        oneLv2.setBsNo(bsNO);
                        oneLv2.setBsName(bsName);
                        oneLv2.setTaskQty(taskQty);
                        oneLv2.setListImage(listImage);
                        oneLv2.setSpec(spec);
                        oneLv2.setBatchNo(batchNO);
                        oneLv2.setIsBatch(isBatch);
                        oneLv2.setProdDate(prodDate);
                        oneLv2.setDistriPrice(distriPrice);
                        oneLv2.setPunitUdLength(punitUDLength);
                        if(docType.equals("0")) {
                            oneLv2.setPStockInQty(pStockInQty);
                        }
                        oneLv2.setScrapQty(scrapQty);
                        oneLv2.setWarehouse(warehouse);
                        oneLv2.setWarehouseName(warehouseName);
                        oneLv2.setScrapQty_origin(scrapQty_origin);
                        oneLv2.setScrapQty_refund(scrapQty_refund);
                        oneLv2.setPqty_origin(pqty_origin);
                        oneLv2.setPqty_refund(pqty_refund);
                        oneLv2.setFeatureNo(featureNo);
                        oneLv2.setFeatureName(featureName);
                        oneLv2.setBaseQty(baseQty);
                        oneLv2.setExpDate(oneDataDetail.get("EXPDATE").toString());
                        oneLv2.setMemo(oneDataDetail.get("DETAIL_MEMO").toString());
                        oneLv2.setLocation(oneDataDetail.get("LOCATION").toString());
                        oneLv2.setLocationName(oneDataDetail.get("LOCATIONNAME").toString());
                        oneLv2.setIsLocation(oneDataDetail.get("D_ISLOCATION").toString());
                        oneLv2.setShelfLife(oneDataDetail.get("SHELFLIFE").toString());
                        oneLv2.setDispType(oneDataDetail.get("DISPTYPE").toString());
                        oneLv2.setOOItem(oneDataDetail.get("OOITEM").toString());
                        oneLv2.setMinQty(oneDataDetail.get("MINQTY").toString());
                        oneLv2.setProdType(oneDataDetail.get("PRODTYPED").toString());
                        oneLv2.setBomNo(oneDataDetail.get("BOMNO").toString());
                        oneLv2.setVersionNum(oneDataDetail.get("VERSIONNUM").toString());
                        Map<String, Object> condition3 = new HashMap<>(); //查詢條件
                        condition3.put("DNO", doNO);
                        if(Check.NotNull(req.getRequest().getPStockInNo())) {
                            condition3.put("M_MITEM", oneLv2.getItem());
                        }
                        condition3.put("PLUNO", pluNO);
                        condition3.put("PUNIT", punit);
                        condition3.put("BSNO", bsNO);
                        condition3.put("FEATURENO", featureNo);
                        condition3.put("BATCH_NO", batchNO);
                        //调用过滤函数
                        List<Map<String, Object>> getMaterialDatas=MapDistinct.getWhereMap(getQData, condition3, true);

                        for (Map<String, Object> oneMaterialData : getMaterialDatas) {
                            DCP_PStockInDetailQueryRes.level3Elm oneLv3 = res.new level3Elm();
                            //String mDNO = oneMaterialData.get("DNO").toString();
                            String mItem = oneMaterialData.get("M_MITEM").toString();
                            String material_item = oneMaterialData.get("M_MATERIAL_ITEM").toString();
                            //String material_warehouse = oneMaterialData.get("M_MATERIAL_WAREHOUSE").toString();  前面已经取值
                            //String material_warehouseName = oneMaterialData.get("M_MATERIAL_WAREHOUSENAME").toString(); 前面已经取值
                            String material_pluNO = oneMaterialData.get("M_MATERIAL_PLUNO").toString();
                            String material_pluName = oneMaterialData.get("M_MATERIAL_PLUNAME").toString();
                            String material_punit = oneMaterialData.get("M_MATERIAL_PUNIT").toString();
                            String material_punitName = oneMaterialData.get("M_MATERIAL_PUNITNAME").toString();
                            String mlistImage = oneMaterialData.get("M_MATERIAL_LISTIMAGE").toString();
                            if (!Check.Null(mlistImage)){
                                mlistImage = domainName + mlistImage;
                            }
                            String mspec = oneMaterialData.get("M_MATERIAL_SPEC").toString();
                            String material_baseUnit = oneMaterialData.get("M_MATERIAL_BASEUNIT").toString();
                            String material_baseUnitName = oneMaterialData.get("M_MATERIAL_BASEUNITNAME").toString();
                            String material_unitRatio = oneMaterialData.get("M_MATERIAL_UNIT_RATIO").toString();
                            String m_material_pqty = oneMaterialData.get("M_MATERIAL_PQTY").toString();
                            String material_price = oneMaterialData.get("M_MATERIAL_PRICE").toString();
                            String material_amt = oneMaterialData.get("M_MATERIAL_AMT").toString();

                            //原料用量设置默认值为0
                            String rawMaterialBaseQty = oneMaterialData.get("M_MATERIAL_RAWMATERIALBASEQTY").toString();
                            //基础用量设置默认值为0
                            String finalProdBaseQty = oneMaterialData.get("M_MATERIAL_FINALPRODBASEQTY").toString();

                            String material_batchNO = oneMaterialData.get("M_MATERIAL_BATCH_NO").toString();
                            String material_isBatch = oneMaterialData.get("M_MATERIAL_ISBATCH").toString();
                            String material_prodDate = oneMaterialData.get("M_MATERIAL_PROD_DATE").toString();
                            String material_distriPrice = oneMaterialData.get("M_MATERIAL_DISTRIPRICE").toString();
                            String material_distriAmt = oneMaterialData.get("M_MATERIAL_DISTRIAMT").toString();
                            String material_punitUDLength = oneMaterialData.get("M_MATERIAL_PUNIT_UDLENGTH").toString();
                            String material_isBuckle=oneMaterialData.get("M_ISBUCKLE").toString();
                            if (Check.Null(material_isBuckle) || !material_isBuckle.equals("N")) {
                                material_isBuckle="Y";
                            }
                            String material_featureNo =oneMaterialData.get("M_MATERIAL_FEATURENO").toString();
                            String material_featureName =oneMaterialData.get("M_MATERIAL_FEATURENAME").toString();
                            String material_baseQty =oneMaterialData.get("M_MATERIAL_BASEQTY").toString();

                            String material_location = oneMaterialData.get("M_MATERIAL_LOCATION").toString();
                            String material_locationName = oneMaterialData.get("M_MATERIAL_LOCATIONNAME").toString();
                            String material_expDate = oneMaterialData.get("M_MATERIAL_EXPDATE").toString();
                            String material_isLocation = oneMaterialData.get("M_MATERIAL_ISLOCATION").toString();

                            if(!pluNos.contains(material_pluNO)){
                                pluNos.add(material_pluNO);
                            }

                            //material_pqty
                            BigDecimal material_pqty = new BigDecimal(m_material_pqty);
                            if(Check.Null(req.getRequest().getPStockInNo())&& Check.NotNull(req.getRequest().getTaskNo())){
                                BigDecimal lossRate = new BigDecimal(oneMaterialData.get("LOSS_RATE").toString());
                                BigDecimal batchQty = new BigDecimal(oneMaterialData.get("BATCHQTY").toString());
                                BigDecimal pQty = new BigDecimal(oneMaterialData.get("PQTY").toString());
                                BigDecimal mQty = new BigDecimal(oneMaterialData.get("M_QTY").toString());
                                material_pqty = pQty.multiply(material_pqty).multiply(BigDecimal.ONE.add(lossRate.multiply(new BigDecimal("0.01")))).divide((mQty.multiply(batchQty)), Integer.valueOf(material_punitUDLength), BigDecimal.ROUND_HALF_UP);
                                finalProdBaseQty=batchQty.toString();
                                //material_pqty=(DCP_PROCESSTASK_DETAIL.PQTY×DCP_BOM_MATERIAL.MATERIAL_QTY×(1+DCP_BOM_MATERIAL.LOSS_RATE×0.01))/(DCP_BOM_MATERIAL.QTY×DCP_BOM.BATCHQTY)，按material_punitUdLength保留小数位数
                                //material_finalProdBaseQty=DCP_BOM.BATCHQTY
                                //material_rawMaterialBaseQty=(DCP_BOM_MATERIAL.MATERIAL_QTY×(1+DCP_BOM_MATERIAL.LOSS_RATE×0.01))/DCP_BOM_MATERIAL.QTY，按material_punitUdLength保留小数位数
                                rawMaterialBaseQty=(new BigDecimal(m_material_pqty).multiply(BigDecimal.ONE.add(lossRate.multiply(new BigDecimal("0.01"))))).divide(mQty, Integer.valueOf(material_punitUDLength), BigDecimal.ROUND_HALF_UP).toString();
                            }

                            oneLv3.setMItem(mItem);
                            oneLv3.setMaterial_item(material_item);
                            oneLv3.setMaterial_warehouse(materialWarehouseNO);
                            oneLv3.setMaterial_warehouseName(materialWarehouseName);
                            oneLv3.setMaterial_pluNo(material_pluNO);
                            oneLv3.setMaterial_pluName(material_pluName);
                            oneLv3.setMaterial_punit(material_punit);
                            oneLv3.setMaterial_punitName(material_punitName);
                            oneLv3.setMaterial_baseUnit(material_baseUnit);
                            oneLv3.setMaterial_baseUnitName(material_baseUnitName);
                            oneLv3.setMaterial_baseUnitUdLength(oneMaterialData.get("M_MATERIAL_BASEUNITUDLENGTH").toString());
                            oneLv3.setMaterial_unitRatio(material_unitRatio);
                            oneLv3.setMaterial_pqty(material_pqty.toString());
                            oneLv3.setMaterial_price(material_price);
                            oneLv3.setMaterial_amt(material_amt);
                            oneLv3.setMaterial_distriAmt(material_distriAmt);
                            oneLv3.setMaterial_finalProdBaseQty(finalProdBaseQty);
                            oneLv3.setMaterial_rawMaterialBaseQty(rawMaterialBaseQty);
                            oneLv3.setMaterial_listImage(mlistImage);
                            oneLv3.setMaterial_spec(mspec);
                            oneLv3.setMaterial_batchNo(material_batchNO);
                            oneLv3.setMaterial_isBatch(material_isBatch);
                            oneLv3.setMaterial_prodDate(material_prodDate);
                            oneLv3.setMaterial_distriPrice(material_distriPrice);
                            oneLv3.setIsBuckle(material_isBuckle);
                            oneLv3.setMaterial_punitUdLength(material_punitUDLength);
                            oneLv3.setMaterial_featureNo(material_featureNo);
                            oneLv3.setMaterial_featureName(material_featureName);
                            oneLv3.setMaterial_baseQty(material_baseQty);
                            oneLv3.setMaterial_location(material_location);
                            oneLv3.setMaterial_locationName(material_locationName);
                            oneLv3.setMaterial_expDate(material_expDate);
                            oneLv3.setMaterial_isLocation(material_isLocation);
                            oneLv3.setCostRate(oneMaterialData.get("COSTRATE").toString());

                            if("0".equals(docType)){
                                String materialPrice="0";
                                String materialDistriPrice="0";
                                Map<String, Object> condiV= new HashMap<>();
                                condiV.put("PLUNO",oneLv3.getMaterial_pluNo());
                                condiV.put("PUNIT",oneLv3.getMaterial_punit());
                                List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                                if(priceList!=null && priceList.size()>0 ) {
                                    materialPrice=priceList.get(0).get("PRICE").toString();
                                    materialDistriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                                }

                                oneLv3.setMaterial_price(materialPrice);
                                oneLv3.setMaterial_distriPrice(materialDistriPrice);
                                BigDecimal materialPqty = new BigDecimal(oneLv3.getMaterial_pqty());
                                BigDecimal materialAmt = materialPqty.multiply(new BigDecimal(oneLv3.getMaterial_price()));
                                BigDecimal materialDistriAmt = materialPqty.multiply(new BigDecimal(oneLv3.getMaterial_distriPrice()));
                                oneLv3.setMaterial_amt(materialAmt.toString());
                                oneLv3.setMaterial_distriAmt(materialDistriAmt.toString());
                            }


                            oneLv3.setMaterialBatchList(new ArrayList<>());

                            BigDecimal totBatchQty=new BigDecimal("0");
                            List<Map<String, Object>> batchData = getBatchData.stream().filter(x -> x.get("OITEM").toString().equals(oneLv3.getMaterial_item())).collect(Collectors.toList());
                            for (Map<String, Object> oneBatchData : batchData){
                                DCP_PStockInDetailQueryRes.MaterialBatchList materialBatchList = res.new MaterialBatchList();
                                materialBatchList.setItem(oneBatchData.get("ITEM").toString());
                                materialBatchList.setOItem(oneBatchData.get("OITEM").toString());
                                materialBatchList.setLocation(oneBatchData.get("LOCATION").toString());
                                materialBatchList.setLocationName(oneBatchData.get("LOCATIONNAME").toString());
                                materialBatchList.setBatchNo(oneBatchData.get("BATCHNO").toString());
                                materialBatchList.setProdDate(oneBatchData.get("PRODDATE").toString());
                                materialBatchList.setExpDate(oneBatchData.get("EXPDATE").toString());
                                materialBatchList.setPUnit(oneBatchData.get("PUNIT").toString());
                                materialBatchList.setPUnitName(oneBatchData.get("PUNITNAME").toString());
                                materialBatchList.setPQty(oneBatchData.get("PQTY").toString());
                                materialBatchList.setBaseUnit(oneBatchData.get("BASEUNIT").toString());
                                materialBatchList.setBaseUnitName(oneBatchData.get("BASEUNITNAME").toString());
                                materialBatchList.setBaseQty(oneBatchData.get("BASEQTY").toString());
                                totBatchQty.add(new BigDecimal(materialBatchList.getBaseQty()));
                                oneLv3.getMaterialBatchList().add(materialBatchList);

                            }

                            oneLv3.setMaterial_totBatchQty(totBatchQty.toString());

                            //给商品添加 明细
                            oneLv2.getMaterial().add(oneLv3);
                        }

                        //添加单身
                        oneLv1.getDatas().add(oneLv2);
                    }

                    res.getDatas().add(oneLv1);
                    StringBuffer sJoinno=new StringBuffer("");
                    for (String pluNo:pluNos){
                        sJoinno.append(pluNo+",");
                    }

                    Map<String, String> mapPluNo=new HashMap<String, String>();
                    mapPluNo.put("PLUNO", sJoinno.toString());
                    MyCommon cm=new MyCommon();
                    String withasSql_mono=cm.getFormatSourceMultiColWith(mapPluNo);

                    if (withasSql_mono.equals(""))
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                    }

                    String stockSql="with p as ("+withasSql_mono+") "+
                            " select a.pluno,a.warehouse,sum(a.qty) qty,a.featureno " +
                            " from DCP_STOCK a " +
                            " inner join p on p.pluno=a.pluno "+
                            " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                            " group by a.pluno,a.warehouse,a.featureno "
                            ;
                    List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

                    String batchStockSql="with p as ("+withasSql_mono+") "+
                            " select a.pluno,a.warehouse,a.location,a.batchno,a.qty,a.featureno " +
                            " from MES_BATCH_STOCK_DETAIL a " +
                            " inner join p on p.pluno=a.pluno "+
                            " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                            ;
                    List<Map<String, Object>> getBatchStockData=this.doQueryData(batchStockSql, null);

                    for (DCP_PStockInDetailQueryRes.level2Elm l2:oneLv1.getDatas()){
                        for (DCP_PStockInDetailQueryRes.level3Elm l3:l2.getMaterial()){
                            String material_pluNo = l3.getMaterial_pluNo();
                            String material_featureNo = l3.getMaterial_featureNo();
                            String material_warehouse = l3.getMaterial_warehouse();
                            List<Map<String, Object>> collect = getStockData.stream().filter(x -> x.get("PLUNO").toString().equals(material_pluNo) &&
                                    x.get("FEATURENO").toString().equals(material_featureNo) &&
                                    x.get("WAREHOUSE").toString().equals(material_warehouse)).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(collect)){
                                l3.setMaterial_stockQty(collect.get(0).get("QTY").toString());
                            }

                            for (DCP_PStockInDetailQueryRes.MaterialBatchList batchList:l3.getMaterialBatchList()){
                                List<Map<String, Object>> collectBatch = getBatchStockData.stream().filter(x -> x.get("PLUNO").toString().equals(material_pluNo) &&
                                        x.get("FEATURENO").toString().equals(material_featureNo) &&
                                        x.get("WAREHOUSE").toString().equals(material_warehouse)&&
                                        x.get("LOCATION").toString().equals(batchList.getLocation())&&
                                                x.get("BATCHNO").toString().equals(batchList.getBatchNo())).collect(Collectors.toList());
                                if(CollUtil.isNotEmpty(collectBatch)){
                                    batchList.setStockQty(collectBatch.get(0).get("QTY").toString());
                                }
                            }
                        }
                    }


                }

                if(Check.NotNull(req.getRequest().getTaskNo())){
                    String taskSql="select a.*,b.warehouse_name as warehousename,c.warehouse_name as materialwarehousename" +
                            " from DCP_PROCESSTASK a " +
                            " left join dcp_warehouse_lang b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and b.lang_type='"+req.getLangType()+"' " +
                            " left join dcp_warehouse_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.warehouse=a.materialwarehouse and c.lang_type='"+req.getLangType()+"' " +
                            " where a.eid='"+req.geteId()+"' and a.processtaskno='"+req.getRequest().getTaskNo()+"' ";
                    List<Map<String, Object>> taskData=this.doQueryData(taskSql, null);
                    if(taskData.size()>0){
                        String warehouse=taskData.get(0).get("WAREHOUSE").toString();
                        String warehouseName = taskData.get(0).get("WAREHOUSENAME").toString();
                        String materialWarehouse = taskData.get(0).get("MATERIALWAREHOUSE").toString();
                        String materialwarehousename = taskData.get(0).get("MATERIALWAREHOUSENAME").toString();
                        res.getDatas().forEach(x->{
                            x.setWarehouse(warehouse);
                            x.setWarehouseName(warehouseName);
                            x.setMaterialWarehouseNo(materialWarehouse);
                            x.setMaterialWarehouseName(materialwarehousename);
                            x.getDatas().forEach(y->{
                                y.setWarehouse( warehouse);
                                y.setWarehouseName(warehouseName);
                                y.getMaterial().forEach(z->{
                                    z.setMaterial_warehouse(materialWarehouse);
                                    z.setMaterial_warehouseName(materialwarehousename);
                                });
                            });

                        });

                        if(Check.Null(materialWarehouse)||Check.Null(warehouse)){
                            String taskdSql="select a.*,b.warehouse,b.warehouse_name as warehousename,c.warehouse_name as materialwarehousename,c.warehouse as materialwarehouse from ( " +
                                    " select a.eid,a.organizationno,a.processtaskno, a.item,nvl(d.INWGROUPNO,e.INWGROUPNO) as INWGROUPNO,f.material_pluno,nvl(g.KWGROUPNO,f.KWGROUPNO) as KWGROUPNO " +
                                    " from DCP_PROCESSTASK_detail a " +
                                    " left join dcp_bom d on a.eid=d.eid and a.bomno=d.bomno and a.VERSIONNUM=d.VERSIONNUM " +
                                    " left join dcp_bom_v e on e.eid=a.eid and e.bomno=a.bomno and e.versionnum=a.versionnum " +
                                    " left join dcp_bom_material f on f.eid=a.eid and f.bomno=a.bomno " +
                                    " left join dcp_bom_material_v g on g.eid=a.eid and g.bomno=a.bomno and g.versionnum=a.versionnum " +
                                    " ) a " +
                                    " left join MES_WAREHOUSE_GROUP_DETAIL d on d.eid=a.eid and a.INWGROUPNO=d.WGROUPNO and d.organizationno='"+req.getOrganizationNO()+"' " +
                                    " left join MES_WAREHOUSE_GROUP_DETAIL e on e.eid=a.eid and e.WGROUPNO=a.KWGROUPNO and e.organizationno='"+req.getOrganizationNO()+"' " +
                                    " left join dcp_warehouse_lang b on a.eid=b.eid and a.organizationno=b.organizationno and d.warehouse=b.warehouse and b.lang_type='"+req.getLangType()+"' " +
                                    " left join dcp_warehouse_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.warehouse=e.warehouse and c.lang_type='"+req.getLangType()+"' " +
                                    " where a.eid='"+req.geteId()+"' and a.processtaskno='"+req.getRequest().getTaskNo()+"' ";
                            List<Map<String, Object>> taskDetailData=this.doQueryData(taskdSql, null);

                            if(taskDetailData.size()>0){
                                res.getDatas().forEach(x-> {
                                    x.getDatas().forEach(y -> {
                                        if(Check.Null(y.getWarehouse())){
                                            List<Map<String, Object>> warehouseList = taskDetailData.stream().filter(m -> m.get("ITEM").toString().equals(y.getItem())).collect(Collectors.toList());
                                            if(warehouseList.size()>0) {
                                                y.setWarehouse(warehouseList.get(0).get("WAREHOUSE").toString());
                                                y.setWarehouseName(warehouseList.get(0).get("WAREHOUSENAME").toString());
                                            }
                                        }
                                        if(Check.Null(materialWarehouse)) {
                                            y.getMaterial().forEach(z -> {

                                                List<Map<String, Object>> materialList = taskDetailData.stream().filter(m -> m.get("ITEM").toString().equals(y.getItem()) && m.get("MATERIAL_PLUNO").toString().equals(z.getMaterial_pluNo())).collect(Collectors.toList());
                                                if (materialList.size() > 0) {
                                                    z.setMaterial_warehouse(materialList.get(0).get("MATERIALWAREHOUSE").toString());
                                                    z.setMaterial_warehouseName(materialList.get(0).get("MATERIALWAREHOUSENAME").toString());
                                                }
                                            });
                                        }
                                    });
                                });
                            }
                        }
                    }

                }

            }



            return res;

    }


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PStockInDetailQueryReq req) throws Exception {

        StringBuffer sqlbuf = new StringBuffer();
        String pStockInNo = req.getRequest().getPStockInNo();
        String docType = req.getRequest().getDocType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        sqlbuf.append(""
                + " select a.*,"
                + " j.ptemplate_name as ptemplatename,h.reason_name as bsname,taw.warehouse_name as warehousename,"
                + " d.plu_name as pluname,e.uname as punitname,e1.uname as baseunitname,f.op_name as createbyname,"
                + " f1.op_name as modifybyname,f2.op_name as cancelbyname,f3.op_name as confirmbyname,f4.op_name as submitbyname,"
                + " f5.op_name as accountbyname,image.listimage,gul.spec,c.isbatch,b.mitem as m_mitem ,b.item as m_material_item,"
                + " b1.warehouse_name as materialwarehousename,b.warehouse as m_materialwarehouse,b2.warehouse_name as m_materialwarehousename,"
                + " b.pluno as m_material_pluno,b3.plu_name as m_material_pluname,b.punit as m_material_punit,b4.uname as m_material_punitname,"
                + " b5.uname as m_material_baseunitname,b.pqty as m_material_pqty,b.price as m_material_price,b.amt as m_material_amt,"
                + " b.distriamt as m_material_distriamt, b.finalprodbaseqty as m_material_finalprodbaseqty,"
                + " b.rawmaterialbaseqty as m_material_rawmaterialbaseqty,mimage.listimage as m_material_listimage,mgul.spec as m_material_spec,"
                + " b.batch_no as m_material_batch_no , b.prod_date as m_material_prod_date,"
                + " b.distriprice as m_material_distriprice,b2.isbatch as m_material_isbatch,b.baseunit as m_material_baseunit,"
                + " b.unit_ratio as m_material_unit_ratio,b.baseqty as m_material_baseqty,"
                + " h1.udlength as punit_udlength,bul1.udlength as baseunitudlength, "
                + " h2.udlength as m_material_punit_udlength,bul2.udlength as m_material_baseunitudlength,"
                + " b.isbuckle as m_isbuckle,b.featureno as m_material_featureno,"
                + " mfn.featurename as m_material_featurename,fn.featurename, "
                + " e1.name as employeename,d1.departname,d2.departname as createdeptname,TAW1.islocation,l1.locationname,w1.islocation as d_islocation,w2.islocation as m_material_islocation,  "
                + " b.location as m_material_location,b.expdate as m_material_expdate,l2.locationname as m_material_locationname,w3.islocation as material_islocation,c.SHELFLIFE,b.costrate "
                + " from ("
                + " ");
        //docType 传值0的时候需要加上这些数据
        //docType 传值1或2的时候不需要加工任务的查询,也就是select 1 as rw......
        //if( docType.equals("0")) {
        //    sqlbuf.append( " SELECT 1 as rw,0 as pqty_origin, 0 as pqty_refund,0 as SCRAP_QTY_ORIGIN, 0 as SCRAP_QTY_REFUND,"
          //          + " CAST('' AS nvarchar2(1)) AS Refundstatus,"
          //          + " CAST('' AS nvarchar2(30)) AS Pstockinno_Refund,CAST('' AS nvarchar2(30)) AS Pstockinno_Origin,A.EID,A.ORGANIZATIONNO,"
          //          + " a.processtaskno as dno,"
           //         + " cast('' as nvarchar2(30)) as processERPNO ,"
           //         + " cast('' as nvarchar2(30)) as PSTOCKINNO,A.SHOPID,0 as pStockInQty,a.bdate,A.memo,cast(A.status as int) as status,cast('' as nvarchar2(1)) as oType "
           //         + " ,A.PROCESSTASKNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO, A.CREATEBY as createBy "
           //         + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,a.ptemplateNO "
           //         + " ,b.item,0 as oitem,b.pluNO,b.featureno,b.pUnit,cast('' as nvarchar2(40)) as pluBarcode "
           //         + " ,b.baseunit,b.baseqty,b.unit_ratio as unitRatio,b.price,b.amt,b.DISTRIAMT,b.pqty as taskQty,0 as scrapQty,0 as pqty, "
            //        + " cast('' as nvarchar2(10)) as bsNO,b.mul_qty as mulQty "
            //        + " ,a.WAREHOUSE, CAST('' AS nvarchar2(30)) AS d_warehouse "
            //        + " ,a.MATERIALWAREHOUSE"
           //         + " ,N'2' as Doc_type "
           //         + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime, a.modify_Date  AS modifyDate ,  "
           //         + " a.modify_time AS modifyTime,a.modifyby , "
          //          + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
           //         + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
           //         + " a.accountby, nvl(a.account_date,'"+sDate+"') AS accountDate, a.account_time AS accountTime , "
           //         + " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime "
           //         + " ,a.UPDATE_TIME,a.PROCESS_STATUS,"
           //         + " N'' as batch_no , N'' as prod_date,b.distriPrice,N'' as detail_memo,A.PROCESSPLANNO,A.TASK0NO,A.DTNO,dt1.dtname,dt1.begin_time,dt1.end_time, CAST('' AS nvarchar2(30)) as employeeid, CAST('' AS nvarchar2(30)) as departid, CAST('' AS nvarchar2(30)) as CREATEDEPTID, CAST('' AS nvarchar2(30)) as location, CAST('' AS nvarchar2(30)) as expdate,a.prodtype, CAST('' AS nvarchar2(30)) as oofno, CAST('' AS nvarchar2(30)) as ootype,CAST('' AS nvarchar2(30)) as disptype ,0 as ooitem,0 as minqty,CAST('' AS nvarchar2(30)) as prodtype,CAST('' AS nvarchar2(30)) as bomno,CAST('' AS nvarchar2(30)) as versionnum  "
           //         + " FROM DCP_PROCESSTASK A "
            //        + " INNER JOIN DCP_PROCESSTASK_DETAIL b ON A.EID=b.EID AND A.SHOPID=b.SHOPID AND A.PROCESSTASKNO=b.PROCESSTASKNO and A.BDATE=b.BDATE "
             //       + " left join DCP_DINNERTIME dt1 on A.eid = dt1.eid and A.organizationno = dt1.shopid and A.dtno = dt1.dtno "
             //       + " where a.eid='"+ eId +"' and a.shopid='"+ shopId +"' and a.pdate = '"+ sDate +"' and a.processtaskno='"+req.getRequest().getTaskNo()+"'"
           // );

          //  sqlbuf.append(" union all ");
       // }
        sqlbuf.append("SELECT 2 as rw,b.pqty_origin, b.pqty_refund,b.SCRAP_QTY_ORIGIN, b.SCRAP_QTY_REFUND,A.Refundstatus,"
                + " A.Pstockinno_Refund,A.Pstockinno_Origin,A.EID,A.ORGANIZATIONNO,a.pstockinno as dno,  a.process_erp_NO AS processERPNO,"
                + " A. PSTOCKINNO,A.SHOPID as SHOPID,");

        if(docType.equals("0")){
            sqlbuf.append( "nvl(i.pstockin_qty,0) as pStockInQty,");
        }

        sqlbuf.append(" to_number(a.bDate) bdate,A.memo,A.status, A.oType  "
                + " ,A.ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy "
                + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,a.pTemplateNO "
                + " ,b.item,b.oItem,b.pluNO,b.featureno,b.pUnit,cast('' as nvarchar2(40)) as pluBarcode "
                + " ,b.baseunit,b.baseqty,b.unit_Ratio unitRatio,b.price,b.amt,b.DISTRIAMT,"
                + " b.task_Qty as taskQty,b.scrap_Qty as scrapQty,b.pqty,b.bsNO,b.mul_qty as mulQty "
                + " ,A.WAREHOUSE,b.warehouse as d_warehouse "
                + " ,N'' materialWarehouse,a.doc_Type "
                + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime, a.modify_Date  AS modifyDate,"
                + " a.modify_time AS modifyTime,a.modifyby , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                + " a.accountby, nvl(a.account_date,'"+sDate+"') AS accountDate, a.account_time AS accountTime , "
                + " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime "
                + " ,a.UPDATE_TIME,a.PROCESS_STATUS, "
                + " b.batch_no ,b.prod_date,b.distriPrice,b.memo as detail_memo,A.PROCESSPLANNO,A.TASK0NO,A.DTNO, "
                + " dt1.dtname,dt1.begin_time,dt1.end_time,a.employeeid,a.departid,a.CREATEDEPTID,b.location,b.expdate,a.prodtype,a.oofno,a.ootype,b.disptype,b.ooitem,b.MINQTY,b.prodtype as prodtyped,b.bomno,b.versionnum  "
                + " FROM DCP_PSTOCKIN A  "
                + " inner join DCP_PSTOCKIN_detail b  on a.EID=b.EID and a.SHOPID=b.SHOPID and a.pstockinno=b.pstockinno and a.ACCOUNT_DATE=b.ACCOUNT_DATE "
                + " left join DCP_DINNERTIME dt1 on A.eid=dt1.eid and A.organizationno=dt1.shopid and A.dtno=dt1.dtno "
                + " "
        );

        if(docType.equals("0")){
            sqlbuf.append( " LEFT JOIN DCP_PROCESSTASK_detail i on a.ofno=i.processtaskno and a.EID=i.EID "
                    + "and a.SHOPID=i.SHOPID and b.pluno=i.pluno  ");
        }

        sqlbuf.append(" WHERE 1=1  and a.pstockinno='"+pStockInNo+"'");

        if (!Check.Null(docType)) {
            sqlbuf.append(" AND A.doc_Type = '"+ docType +"'  " );
        }

        sqlbuf.append(" AND a.SHOPID='"+ shopId +"' ");
        sqlbuf.append(" AND a.EID='"+ eId +"'");
        sqlbuf.append(" ) a");


        sqlbuf.append(""
                + " LEFT JOIN DCP_PSTOCKIN_MATERIAL B ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.dno=b.pstockinno and a.item = b.mitem and a.accountDate=b.ACCOUNT_DATE "
                + " left join DCP_WAREHOUSE_lang b1 on b1.EID=a.EID and b1.organizationno=a.SHOPID and  b1.warehouse=a.MATERIALWAREHOUSE and b1.lang_type='"+langType+"' "
                + " left join DCP_WAREHOUSE_lang b2 on b2.EID=b.EID and b2.organizationno=b.SHOPID and  b2.warehouse=b.WAREHOUSE and b2.lang_type='"+langType+"'  "
                + " left join DCP_WAREHOUSE w1 on w1.EID=a.EID and w1.organizationno=a.SHOPID and  w1.warehouse=a.d_warehouse  "
                + " left join DCP_WAREHOUSE w2 on w2.EID=b.EID and w2.organizationno=b.SHOPID and  w2.warehouse=b.WAREHOUSE   "
                + " left join DCP_WAREHOUSE w3 on w3.EID=a.EID and w3.organizationno=a.SHOPID and  w3.warehouse=a.MATERIALWAREHOUSE   "
                + " left join DCP_GOODS b2 on b2.EID=b.EID and b2.pluno=b.pluno "
                + " left join DCP_GOODS_lang b3 on b3.EID=b.EID and b3.pluno=b.pluno and b3.lang_type='"+langType+"' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
                + " left join DCP_GOODS_FEATURE_LANG mfn on b.eid=mfn.eid and b.pluno=mfn.pluno and b.featureno=mfn.featureno  and mfn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG mgul on b.eid=mgul.eid and b.pluno=mgul.pluno and b.punit=mgul.ounit and mgul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage mimage on mimage.eid=b.eid and mimage.pluno=b.pluno and mimage.apptype='ALL' "
                + " left join DCP_UNIT_lang b4 on b4.EID=b.EID and b4.unit=b.punit and b4.lang_type='"+langType+"'  "
                + " left join DCP_UNIT_lang b5 on b5.EID=b.EID and b5.unit=b.baseunit and b5.lang_type='"+langType+"'  "
                + " INNER JOIN DCP_GOODS c ON A.PLUNO = c.PLUNO AND A.EID = c.EID and c.status='100' "
                + " LEFT JOIN DCP_GOODS_LANG D ON A.PLUNO = D .PLUNO AND A.EID = D .EID AND D.LANG_TYPE ='"+ langType +"'  "
                + " LEFT JOIN DCP_UNIT_LANG E ON A.PUNIT = E.UNIT AND A.EID = E.EID AND E .LANG_TYPE ='"+ langType +"'  "
                + " LEFT JOIN DCP_UNIT_LANG E1 ON A.baseUNIT = E1.UNIT AND A.EID = E1.EID AND E1.LANG_TYPE ='"+ langType +"'  "
                + " LEFT JOIN platform_staffs_lang f ON A .EID = f.EID AND A .createby = f.opno and f.lang_type='"+langType+"'  "
                + " LEFT JOIN platform_staffs_lang f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno  and f1.lang_type='"+langType+"' "
                + " LEFT JOIN platform_staffs_lang f2 ON A .EID = f2.EID AND A .cancelby = f2.opno  and f2.lang_type='"+langType+"'"
                + " LEFT JOIN platform_staffs_lang f3 ON A .EID = f3.EID AND A .confirmby = f3.opno and f3.lang_type='"+langType+"' "
                + " LEFT JOIN platform_staffs_lang f4 ON A .EID = f4.EID AND A .submitby = f4.opno and f4.lang_type='"+langType+"' "
                + " LEFT JOIN platform_staffs_lang f5 ON A .EID = f5.EID AND A .accountby = f5.opno and f5.lang_type='"+langType+"'  "
                + " LEFT JOIN DCP_pTEMPLATE J ON A.EID=J.EID AND A.PTEMPLATENO=J.pTEMPLATENO and J.DOC_TYPE='2' and j.status='100' "
                + " LEFT JOIN DCP_REASON_LANG h on a.EID=h.EID and a.bsno=h.bsno and h.lang_Type ='"+ langType +"' and h.bstype='3' "
                + " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND A.WAREHOUSE=TAW.WAREHOUSE AND TAW.LANG_TYPE='"+ langType +"' "

                + " LEFT JOIN DCP_WAREHOUSE TAW1 ON A.EID=TAW1.EID AND A.ORGANIZATIONNO=TAW1.ORGANIZATIONNO AND A.WAREHOUSE=TAW1.WAREHOUSE "

                + " LEFT JOIN DCP_UNIT H1 ON A.PUNIT = H1.UNIT AND A.EID = H1.EID "
                + " LEFT JOIN DCP_UNIT H2 ON B.PUNIT = H2.UNIT AND B.EID = H2.EID "
                + " left join dcp_unit bul1 on a.eid=bul1.eid and a.baseunit=bul1.unit"
                + " left join dcp_unit bul2 on b.eid=bul2.eid and b.baseunit=bul2.unit"
                + " left join dcp_employee e1 on e1.employeeno=a.employeeid and e1.eid=a.eid  "
                + " left join dcp_department_lang d1 on d1.departno=a.departid and d1.eid=a.eid and d1.lang_type='"+langType+"'"
                + " left join dcp_department_lang d2 on d2.departno=a.CREATEDEPTID and d2.eid=a.eid and d2.lang_type='"+langType+"'"
                + " LEFT JOIN DCP_LOCATION l1 on l1.eid=a.eid and l1.organizationno=a.organizationno and l1.location=a.location "
                + " LEFT JOIN DCP_LOCATION l2 on l2.eid=a.eid and l2.organizationno=a.organizationno and l2.location=b.location "
        );

        sqlbuf.append(" order by rw asc,a.status asc,a.bdate desc,a.dno desc,a.pluNo,m_material_pluno,a.item,m_material_item asc ");

        return sqlbuf.toString();

    }

    private String getTaskQuerySql(DCP_PStockInDetailQueryReq req) throws Exception{

        StringBuffer sqlbuf = new StringBuffer();
        String docType = req.getRequest().getDocType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        sqlbuf.append(""
                + " select a.*,'"+docType+"' as DOC_TYPE,"
                + " j.ptemplate_name as ptemplatename,h.reason_name as bsname,taw.warehouse_name as warehousename,"
                + " d.plu_name as pluname,e.uname as punitname,e1.uname as baseunitname,'' as createbyname,"
                + " '' as modifybyname,'' as cancelbyname,'' as confirmbyname,'' as submitbyname,"
                + " '' as accountbyname,image.listimage,gul.spec,c.isbatch,"

                + " '' as m_mitem ,'' as m_material_item,"
                + " '' as materialwarehousename,'' m_materialwarehouse,'' as m_materialwarehousename,"
                + " b.MATERIAL_PLUNO as m_material_pluno,b3.plu_name as m_material_pluname,b.MATERIAL_UNIT as m_material_punit,b4.uname as m_material_punitname,"
                + " b5.uname as m_material_baseunitname,b.MATERIAL_QTY as m_material_pqty,0 as m_material_price,0 as m_material_amt,"
                + " 0 as m_material_distriamt, 0 as m_material_finalprodbaseqty,"
                + " 0 as m_material_rawmaterialbaseqty,mimage.listimage as m_material_listimage,mgul.spec as m_material_spec,"
                + " '' as m_material_batch_no , '' as m_material_prod_date,"
                + " 0 as m_material_distriprice,b2.isbatch as m_material_isbatch,b2.baseunit as m_material_baseunit,"
                + " mgul1.unitratio as m_material_unit_ratio,0 as m_material_baseqty,"
                + " h1.udlength as punit_udlength,bul1.udlength as baseunitudlength, "
                + " h2.udlength as m_material_punit_udlength,bul2.udlength as m_material_baseunitudlength,"
                + " b.isbuckle as m_isbuckle,'' as m_material_featureno,"
                + " '' as m_material_featurename,fn.featurename, "
                + " e1.name as employeename,d1.departname,d2.departname as createdeptname,TAW1.islocation,l1.locationname,w1.islocation as d_islocation,'' as m_material_islocation,  "
                + " '' as m_material_location,'' as m_material_expdate,'' as m_material_locationname,w3.islocation as material_islocation,c.SHELFLIFE,b.loss_rate,db.batchqty,b.qty as m_qty,b.costrate  "
                + " from ("
                + " ");
        {
            sqlbuf.append(" SELECT 1 as rw,0 as pqty_origin, 0 as pqty_refund,0 as SCRAP_QTY_ORIGIN, 0 as SCRAP_QTY_REFUND,"
                    + " CAST('' AS nvarchar2(1)) AS Refundstatus,"
                    + " CAST('' AS nvarchar2(30)) AS Pstockinno_Refund,CAST('' AS nvarchar2(30)) AS Pstockinno_Origin,A.EID,A.ORGANIZATIONNO,"
                    + " a.processtaskno as dno,"
                    + " cast('' as nvarchar2(30)) as processERPNO ,"
                    + " cast('' as nvarchar2(30)) as PSTOCKINNO,A.SHOPID,nvl(b.pstockin_qty,0) as pStockInQty,'"+sDate+"' as bdate,A.memo,cast(A.status as int) as status,cast('' as nvarchar2(1)) as oType "
                    + " ,A.PROCESSTASKNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO, A.CREATEBY as createBy "
                    + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,a.ptemplateNO "
                    + " ,b.item,b.item as oitem,b.pluNO,b.featureno,b.pUnit,cast('' as nvarchar2(40)) as pluBarcode "
                    + " ,b.baseunit,b.baseqty,b.unit_ratio as unitRatio,b.price,b.amt,b.DISTRIAMT,b.pqty as taskQty,0 as scrapQty,b.pqty as pqty, "
                    + " cast('' as nvarchar2(10)) as bsNO,b.mul_qty as mulQty "
                    + " ,a.WAREHOUSE "
                    + " ,a.MATERIALWAREHOUSE"
                    + " ,N'2' as Doc_type "
                    + " ,N'' as createDate,N'' as createTime,N'' as modifyDate ,  "
                    + " N'' as modifyTime,N'' as modifyby , "
                    + "  N'' as confirmBy,N'' as confirmDate , N'' as ConfirmTime , "
                    + " N'' as cancelBy , N'' as cancelDate , N'' as  cancelTime  ,"
                    + " N'' as accountby, N'' as accountDate, N'' as accountTime , "
                    + " N'' as submitby , N'' as submitDate, N'' as submitTime "
                    + " ,N'' as UPDATE_TIME,a.PROCESS_STATUS,"
                    + " N'' as batch_no , N'' as prod_date,b.distriPrice,N'' as detail_memo,A.PROCESSPLANNO,A.TASK0NO,A.DTNO,dt1.dtname,dt1.begin_time,dt1.end_time,'"+req.getEmployeeNo()+"' as employeeid,a.departid,'' as CREATEDEPTID,'' as location,'' as expdate,a.prodtype" +
                    ",'"+req.getRequest().getTaskNo()+"' as oofno,'0' as ootype, b.disptype,b.item as ooitem,b.MINQTY,b.bomno as bomno,N'' as prodtyped,b.versionnum as versionnum   "
                    + " FROM DCP_PROCESSTASK A "
                    + " INNER JOIN DCP_PROCESSTASK_DETAIL b ON A.EID=b.EID AND A.SHOPID=b.SHOPID AND A.PROCESSTASKNO=b.PROCESSTASKNO and A.BDATE=b.BDATE "
                    + " left join DCP_DINNERTIME dt1 on A.eid = dt1.eid and A.organizationno = dt1.shopid and A.dtno = dt1.dtno "
                    + " where a.eid='" + eId + "' and a.shopid='" + shopId + "'  and a.processtaskno='" + req.getRequest().getTaskNo() + "'"  //and a.pdate = '" + sDate + "'
            );
        }
            sqlbuf.append(" ) a");



        sqlbuf.append(" left join DCP_BOM db on db.eid=a.eid and a.pluno=db.pluno  and db.bomno=a.bomno "
                + " LEFT JOIN DCP_BOM_MATERIAL B ON a.EID=b.EID and b.bomno=db.bomno "
                + " left join DCP_WAREHOUSE_lang b1 on b1.EID=a.EID and b1.organizationno=a.SHOPID and  b1.warehouse=a.MATERIALWAREHOUSE and b1.lang_type='"+langType+"' "
                //+ " left join DCP_WAREHOUSE_lang b2 on b2.EID=b.EID and b2.organizationno=b.SHOPID and  b2.warehouse=b.WAREHOUSE and b2.lang_type='"+langType+"'  "
                + " left join DCP_WAREHOUSE w1 on w1.EID=a.EID and w1.organizationno=a.SHOPID and  w1.warehouse=a.WAREHOUSE  "
                //+ " left join DCP_WAREHOUSE w2 on w2.EID=b.EID and w2.organizationno=b.SHOPID and  w2.warehouse=b.WAREHOUSE   "
                + " left join DCP_WAREHOUSE w3 on w3.EID=b.EID and w3.organizationno=a.organizationno and  w3.warehouse=a.MATERIALWAREHOUSE   "
                + " left join DCP_GOODS b2 on b2.EID=b.EID and b2.pluno=b.MATERIAL_PLUNO "
                + " left join DCP_GOODS_lang b3 on b3.EID=b.EID and b3.pluno=b.MATERIAL_PLUNO and b3.lang_type='"+langType+"' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
                //+ " left join DCP_GOODS_FEATURE_LANG mfn on b.eid=mfn.eid and b.pluno=mfn.pluno and b.featureno=mfn.featureno  and mfn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG mgul on b.eid=mgul.eid and b.MATERIAL_PLUNO=mgul.pluno and b.MATERIAL_UNIT=mgul.ounit and mgul.lang_type='"+langType+"'"
                + " left join DCP_GOODS_UNIT mgul1 on b.eid=mgul1.eid and b.MATERIAL_PLUNO=mgul1.pluno and b.MATERIAL_UNIT=mgul1.ounit "
                + " left join dcp_goodsimage mimage on mimage.eid=b.eid and mimage.pluno=b.MATERIAL_PLUNO and mimage.apptype='ALL' "
                + " left join DCP_UNIT_lang b4 on b4.EID=b.EID and b4.unit=b.MATERIAL_UNIT and b4.lang_type='"+langType+"'  "
                + " left join DCP_UNIT_lang b5 on b5.EID=b.EID and b5.unit=b2.baseunit and b5.lang_type='"+langType+"'  "
                + " INNER JOIN DCP_GOODS c ON A.PLUNO = c.PLUNO AND A.EID = c.EID and c.status='100' "
                + " LEFT JOIN DCP_GOODS_LANG D ON A.PLUNO = D .PLUNO AND A.EID = D .EID AND D.LANG_TYPE ='"+ langType +"'  "
                + " LEFT JOIN DCP_UNIT_LANG E ON A.PUNIT = E.UNIT AND A.EID = E.EID AND E .LANG_TYPE ='"+ langType +"'  "
                + " LEFT JOIN DCP_UNIT_LANG E1 ON A.baseUNIT = E1.UNIT AND A.EID = E1.EID AND E1.LANG_TYPE ='"+ langType +"'  "
                //+ " LEFT JOIN dcp_employee f ON A .EID = f.EID AND A .createby = f.employeeno  "
                //+ " LEFT JOIN dcp_employee f1 ON A .EID = f1.EID AND A .modifyBy = f1.employeeno  "
                //+ " LEFT JOIN dcp_employee f2 ON A .EID = f2.EID AND A .cancelby = f2.employeeno  "
                //+ " LEFT JOIN dcp_employee f3 ON A .EID = f3.EID AND A .confirmby = f3.employeeno  "
                //+ " LEFT JOIN dcp_employee f4 ON A .EID = f4.EID AND A .submitby = f4.employeeno  "
                //+ " LEFT JOIN dcp_employee f5 ON A .EID = f5.EID AND A .accountby = f5.employeeno  "
                + " LEFT JOIN DCP_pTEMPLATE J ON A.EID=J.EID AND A.PTEMPLATENO=J.pTEMPLATENO and J.DOC_TYPE='2' and j.status='100' "
                + " LEFT JOIN DCP_REASON_LANG h on a.EID=h.EID and a.bsno=h.bsno and h.lang_Type ='"+ langType +"' and h.bstype='3' "
                + " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND A.WAREHOUSE=TAW.WAREHOUSE AND TAW.LANG_TYPE='"+ langType +"' "
                + " LEFT JOIN DCP_WAREHOUSE TAW1 ON A.EID=TAW1.EID AND A.ORGANIZATIONNO=TAW1.ORGANIZATIONNO AND A.WAREHOUSE=TAW1.WAREHOUSE "
                + " LEFT JOIN DCP_UNIT H1 ON A.PUNIT = H1.UNIT AND A.EID = H1.EID "
                + " LEFT JOIN DCP_UNIT H2 ON B.material_unit = H2.UNIT AND B.EID = H2.EID "
                + " left join dcp_unit bul1 on a.eid=bul1.eid and a.baseunit=bul1.unit"
                + " left join dcp_unit bul2 on b.eid=bul2.eid and b2.baseunit=bul2.unit"
                + " left join dcp_employee e1 on e1.employeeno=a.employeeid and e1.eid=a.eid  "
                + " left join dcp_department_lang d1 on d1.departno=a.departid and d1.eid=a.eid and d1.lang_type='"+langType+"'"
                + " left join dcp_department_lang d2 on d2.departno=a.CREATEDEPTID and d2.eid=a.eid and d2.lang_type='"+langType+"'"
                + " LEFT JOIN DCP_LOCATION l1 on l1.eid=a.eid and l1.organizationno=a.organizationno and l1.location=a.location "
                + " LEFT JOIN DCP_LOCATION l2 on l2.eid=a.eid and l2.organizationno=a.organizationno and l2.location='' "
        );
        sqlbuf.append(" order by rw asc,a.status asc,a.bdate desc,a.dno desc,a.pluNo,m_material_pluno,a.item,m_material_item asc ");

        return sqlbuf.toString();
    }

    private String getBatchSql(DCP_PStockInDetailQueryReq req) throws Exception{
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" select a.*,b.locationname,u1.uname as punitname,u2.uname as baseunitname " +
                " from DCP_PSTOCKOUT_BATCH a " +
                " left join dcp_location b on a.eid=b.eid and a.location=b.location " +
                //" LEFT JOIN DCP_PSTOCKIN_MATERIAL c on a.eid=c.eid and a.organizationno=c.organizationno and a.mitem=c.item and a.pstockinno=c.pstockinno " +
                " left join dcp_unit_lang u1 on u1.eid=a.eid and u1.unit=a.punit and u1.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang u2 on u2.eid=a.eid and u2.unit=a.baseunit and u2.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.PSTOCKINNO ='"+req.getRequest().getPStockInNo()+"'");

        return sqlbuf.toString();
    }

}
