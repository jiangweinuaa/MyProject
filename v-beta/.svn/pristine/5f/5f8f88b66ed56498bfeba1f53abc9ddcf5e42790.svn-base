package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_StockAdjustDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustDetailQueryRes;
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

public class DCP_StockAdjustDetailQuery extends SPosBasicService<DCP_StockAdjustDetailQueryReq, DCP_StockAdjustDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockAdjustDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        String adjustNo = req.getRequest().getAdjustNo();
        String docType = req.getRequest().getDocType();
        if(Check.Null(adjustNo)) {
            errMsg.append("调整单号不可为空值,");
            isFail=true;
        }
        if(Check.Null(docType)) {
            errMsg.append("单据类型不可为空值,");
            isFail=true;
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockAdjustDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_StockAdjustDetailQueryReq>(){};
    }

    @Override
    protected DCP_StockAdjustDetailQueryRes getResponseType() {
        return new DCP_StockAdjustDetailQueryRes();
    }

    @Override
    protected DCP_StockAdjustDetailQueryRes processJson(DCP_StockAdjustDetailQueryReq req) throws Exception {

        //查詢資料
        DCP_StockAdjustDetailQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        //单头总数
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            // 拼接返回图片路径  by jinzma 20210705
            String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
            String httpStr=isHttps.equals("1")?"https://":"http://";
            String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
            if (domainName.endsWith("/")) {
                domainName = httpStr + domainName + "resource/image/";
            }else{
                domainName = httpStr + domainName + "/resource/image/";
            }

            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("DCP_ADJUST_ADJUSTNO", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQDataDetail, condition);
            for (Map<String, Object> oneData : getQHeader) {
                DCP_StockAdjustDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();
                oneLv1.setDatas(new ArrayList<DCP_StockAdjustDetailQueryRes.level2Elm>());
                // 取出第一层
                String adjustNO = oneData.get("DCP_ADJUST_ADJUSTNO").toString();
                String bDate = oneData.get("DCP_ADJUST_BDATE").toString();
                String memo = oneData.get("DCP_ADJUST_MEMO").toString();
                String status = oneData.get("DCP_ADJUST_STATUS").toString();
                String docType = oneData.get("DCP_ADJUST_DOC_TYPE").toString();
                String oType = oneData.get("DCP_ADJUST_OTYPE").toString();
                String ofNO = oneData.get("DCP_ADJUST_OFNO").toString();
                String loadDocType = oneData.get("DCP_ADJUST_LOAD_DOCTYPE").toString();
                String loadDocNO = oneData.get("DCP_ADJUST_LOAD_DOCNO").toString();
                String warehouse = oneData.get("WAREHOUSE_MAIN").toString();
                String warehouseName = oneData.get("WAREHOUSE_MAIN_NAME").toString();
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


                //设置响应
                oneLv1.setAdjustNo(adjustNO);
                oneLv1.setBDate(bDate);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status);
                oneLv1.setDocType(docType);
                oneLv1.setOType(oType);
                oneLv1.setOfNo(ofNO);
                oneLv1.setLoadDocType(loadDocType);
                oneLv1.setLoadDocNo(loadDocNO);
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);
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

                oneLv1.setCreateDateTime(PosPub.combineDateTime(oneLv1.getCreateDate(), oneLv1.getCreateTime()));
                oneLv1.setAccountDateTime(PosPub.combineDateTime(oneLv1.getAccountDate(), oneLv1.getAccountTime()));
                oneLv1.setConfirmDateTime(PosPub.combineDateTime(oneLv1.getConfirmDate(), oneLv1.getConfirmTime()));
                oneLv1.setCancelDateTime(PosPub.combineDateTime(oneLv1.getCancelDate(), oneLv1.getCancelTime()));
                oneLv1.setModifyDateTime(PosPub.combineDateTime(oneLv1.getModifyDate(), oneLv1.getModifyTime()));
                oneLv1.setSubmitDateTime(PosPub.combineDateTime(oneLv1.getSubmitDate(), oneLv1.getSubmitTime()));


                oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                oneLv1.setDepartId(oneData.get("DEPARTID").toString());
                oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());

                oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());

                if(CollUtil.isNotEmpty(getQDataDetail)) {

                    String pluStr=getQDataDetail.stream().map(x->x.get("DCP_ADJUST_DETAIL_PLUNO").toString()).collect(Collectors.joining(","))+",";
                    String warehouseStr=getQDataDetail.stream().map(x->x.get("WAREHOUSE_DETAIL").toString()).collect(Collectors.joining(","))+",";
                    String featureStr=getQDataDetail.stream().map(x->x.get("FEATURENO").toString()).collect(Collectors.joining(","))+",";
                    String batchStr=getQDataDetail.stream().map(x->x.get("BATCHNO").toString()).collect(Collectors.joining(","))+",";
                    String locationStr=getQDataDetail.stream().map(x->x.get("LOCATION").toString()).collect(Collectors.joining(","))+",";
                    Map<String, String> mapOrder=new HashMap<String, String>();
                    mapOrder.put("PLUNO", pluStr.toString());
                    mapOrder.put("WAREHOUSE", warehouseStr.toString());
                    mapOrder.put("FEATURENO", featureStr.toString());
                    //mapOrder.put("BATCHNO", batchStr.toString());
                    //mapOrder.put("LOCATION", locationStr.toString());

                    MyCommon cm=new MyCommon();
                    String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

                    String batchSql=" with p as ("+withasSql_mono+") " +
                            " " +
                            " select a.pluno,a.featureno,a.batchno,a.location,a.warehouse,sum(a.qty-nvl(a.lockqty,0)) as qty " +
                            " from MES_BATCH_STOCK_DETAIL a " +
                            " inner join p on p.pluno=a.pluno and p.featureno=a.featureno and p.warehouse=a.warehouse " +
                            " where a.eid='"+req.geteId()+"' " +
                            " and a.organizationno='"+req.getOrganizationNO()+"'" +
                            " group by a.pluno,a.featureno,a.batchno,a.location,a.warehouse " +
                            "   ";
                    List<Map<String, Object>> getQDataBatch=this.doQueryData(batchSql, null);

                    for (Map<String, Object> oneData2 : getQDataDetail) {
                        //过滤属于此单头的明细
                        if (adjustNO.equals(oneData2.get("DCP_ADJUST_ADJUSTNO"))) {
                            DCP_StockAdjustDetailQueryRes.level2Elm oneLv2 = res.new level2Elm();
                            String item = oneData2.get("DCP_ADJUST_DETAIL_ITEM").toString();
                            String oItem = oneData2.get("DCP_ADJUST_DETAIL_OITEM").toString();
                            String pluNO = oneData2.get("DCP_ADJUST_DETAIL_PLUNO").toString();
                            String pluName = oneData2.get("DCP_GOODS_PLUNAME").toString();
                            String spec = oneData2.get("SPEC").toString();//20181120 添加商品规格
                            String listImage = oneData2.get("LISTIMAGE").toString();//20181120 添加 商品图片
                            if (!Check.Null(listImage)) {
                                listImage = domainName + listImage;
                            }
                            String punit = oneData2.get("DCP_ADJUST_DETAIL_PUNIT").toString();
                            String punitName = oneData2.get("DCP_UNIT_LANG_PUNITNAME").toString();
                            String pqty = oneData2.get("DCP_ADJUST_DETAIL_PQTY").toString();
                            String price = oneData2.get("DCP_ADJUST_DETAIL_PRICE").toString();
                            String distriPrice = oneData2.get("DISTRIPRICE").toString();
                            String amt = oneData2.get("DCP_ADJUST_DETAIL_AMT").toString();
                            String distriAmt = oneData2.get("DISTRIAMT").toString();
                            String pluBarcode = oneData2.get("DCP_ADJUST_DETAIL_PLU_BARCODE").toString();
                            String warehouse_detail = oneData2.get("WAREHOUSE_DETAIL").toString();
                            String featureNo = oneData2.get("FEATURENO").toString();
                            String featureName = oneData2.get("FEATURENAME").toString();
                            String baseQty = oneData2.get("BASEQTY").toString();
                            String baseUnit = oneData2.get("BASEUNIT").toString();
                            String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                            String category = oneData2.get("CATEGORY").toString();
                            String categoryName = oneData2.get("CATEGORYNAME").toString();
                            String location = oneData2.get("LOCATION").toString();
                            String locationName = oneData2.get("LOCATIONNAME").toString();
                            String expDate = oneData2.get("EXPDATE").toString();
                            String unitRatio = oneData2.get("UNIT_RATIO").toString();
                            String batchNo = oneData2.get("BATCHNO").toString();
                            String prodDate = oneData2.get("PRODDATE").toString();
                            String memoDetail = oneData2.get("DETAILMEMO").toString();


                            oneLv2.setItem(item);
                            oneLv2.setOItem(oItem);
                            oneLv2.setPluNo(pluNO);
                            oneLv2.setPluName(pluName);
                            oneLv2.setPunit(punit);
                            oneLv2.setPunitName(punitName);
                            oneLv2.setPqty(pqty);
                            oneLv2.setPrice(price);
                            oneLv2.setDistriPrice(distriPrice);
                            oneLv2.setAmt(amt);
                            oneLv2.setDistriAmt(distriAmt);
                            oneLv2.setPluBarcode(pluBarcode);
                            oneLv2.setWarehouse(warehouse_detail);
                            oneLv2.setSpec(spec);
                            oneLv2.setListImage(listImage);
                            oneLv2.setFeatureNo(featureNo);
                            oneLv2.setFeatureName(featureName);
                            oneLv2.setBaseQty(baseQty);
                            oneLv2.setBaseUnit(baseUnit);
                            oneLv2.setBaseUnitName(baseUnitName);

                            oneLv2.setUnitRatio(unitRatio);
                            oneLv2.setBatchNo(batchNo);
                            oneLv2.setProdDate(prodDate);
                            oneLv2.setCategory(category);
                            oneLv2.setCategoryName(categoryName);
                            oneLv2.setLocation(location);
                            oneLv2.setLocationName(locationName);
                            oneLv2.setExpDate(expDate);
                            oneLv2.setMemo(memoDetail);
                            oneLv2.setIsBatch(oneData2.get("ISBATCH").toString());
                            oneLv2.setStockQty("0");

                            if(CollUtil.isNotEmpty(getQDataBatch)){
                                List<Map<String, Object>> collect = getQDataBatch.stream().filter(x -> x.get("PLUNO").toString().equals(pluNO) &&
                                        x.get("FEATURENO").toString().equals(featureNo)
                                        && x.get("WAREHOUSE").toString().equals(warehouse_detail)

                                ).collect(Collectors.toList());

                                if(Check.NotNull(oneLv2.getBatchNo())){
                                    collect=collect.stream().filter(x -> x.get("BATCHNO").toString().equals(batchNo)).collect(Collectors.toList());
                                }
                                if(Check.NotNull(oneLv2.getLocation())){
                                    collect=collect.stream().filter(x -> x.get("LOCATION").toString().equals(location)).collect(Collectors.toList());
                                }


                                if(CollUtil.isNotEmpty(collect)){
                                    BigDecimal qty = new BigDecimal(collect.get(0).get("QTY").toString());
                                    Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao, req.geteId(), pluNO, oneLv2.getBaseUnit(), oneLv2.getPunit(), qty.toString());
                                    String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                    oneLv2.setStockQty(afterDecimal);
                                }

                            }

                            //添加
                            oneLv1.getDatas().add(oneLv2);
                        }
                    }
                }
                //添加
                res.getDatas().add(oneLv1);
            }

        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_StockAdjustDetailQueryReq req) throws Exception {
        String sql=null;
        String adjustNo = req.getRequest().getAdjustNo();
        String docType = req.getRequest().getDocType();
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        String langType=req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append(""
                + " SELECT a.adjustno as DCP_ADJUST_ADJUSTNO,a.bdate as DCP_ADJUST_BDATE,a.memo as DCP_ADJUST_Memo,"
                + " a.status as DCP_ADJUST_Status,"
                + " a.doc_type as DCP_ADJUST_Doc_Type,a.otype as DCP_ADJUST_Otype,a.ofno as DCP_ADJUST_Ofno,"
                + " a.load_doctype as DCP_ADJUST_Load_Doctype,"
                + " a.load_docno as DCP_ADJUST_Load_Docno,b.item as DCP_ADJUST_DETAIL_Item,b.oitem as DCP_ADJUST_DETAIL_Oitem,"
                + " b.pluno as DCP_ADJUST_DETAIL_Pluno,b.punit as DCP_ADJUST_DETAIL_Punit,b.pqty as DCP_ADJUST_DETAIL_Pqty,"
                + " b.price as DCP_ADJUST_DETAIL_Price,b.DISTRIPRICE,b.amt as DCP_ADJUST_DETAIL_Amt,b.DISTRIAMT,"
                + " b.featureNo,fn.featurename,b.baseQty,b.baseUnit,e1.uname as baseUnitName,"
                + " b.plu_barcode as DCP_ADJUST_DETAIL_Plu_Barcode,"
                + " image.listimage, D.plu_name as DCP_GOODS_PLUNAME,gul.spec,e.uname as DCP_UNIT_LANG_punitName,"
                + " A.WAREHOUSE AS WAREHOUSE_MAIN,TAW.WAREHOUSE_NAME AS WAREHOUSE_MAIN_NAME,B.WAREHOUSE AS WAREHOUSE_DETAIL, "
                + " A.TOT_PQTY AS TOTPQTY,A.TOT_CQTY as TOTCQTY,A.TOT_AMT as TOTAMT,A.TOT_DISTRIAMT, "
                + " A.UPDATE_TIME as UPDATE_TIME,A.PROCESS_STATUS as PROCESS_STATUS,"
                + " A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,b0.op_name as CREATENAME,"
                + " A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,b1.op_name as MODIFYNAME,"
                + " A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,b2.op_name as SUBMITNAME,"
                + " A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,b3.op_name as CONFIRMNAME,"
                + " A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,b4.op_name as CANCELNAME,"
                + " A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,b5.op_name as ACCOUNTNAME," +
                "b.category,g.category_name as categoryname ,b.location,h.locationname,b.expdate,B.BATCH_NO AS BATCHNO,B.PROD_DATE AS PRODDATE,b.unit_ratio, "
                + " a.employeeid,a.departid,e0.name as employeename,d0.departname ,b.memo as detailmemo,d1.isbatch,taw1.ISLOCATION "
                + " FROM DCP_ADJUST a "
                + " INNER JOIN DCP_ADJUST_DETAIL b ON a.ADJUSTNO=b.ADJUSTNO and a.organizationno=b.organizationno "
                + " and a.SHOPID=b.SHOPID "
                + " left JOIN DCP_GOODS_LANG  d ON b.PLUNO=d.PLUNO AND  b.EID=d.EID and d.LANG_TYPE='"+langType+"'"
                + " left JOIN DCP_GOODS  d1 ON b.PLUNO=d1.PLUNO AND  b.EID=d1.EID "

                + " left JOIN DCP_UNIT_LANG e ON b.punit =e.UNIT AND b.EID=e.EID AND e.LANG_TYPE='"+langType+"' "
                + " left JOIN DCP_UNIT_LANG e1 ON b.baseunit =e1.UNIT AND b.EID=e1.EID AND e1.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND "
                + " A.WAREHOUSE=TAW.WAREHOUSE AND  TAW.LANG_TYPE ='"+langType+"' "
                + " LEFT JOIN DCP_WAREHOUSE TAW1 ON A.EID=TAW1.EID AND A.ORGANIZATIONNO=TAW1.ORGANIZATIONNO AND "
                + " A.WAREHOUSE=TAW1.WAREHOUSE  "
                + " LEFT JOIN platform_staffs_lang b0 ON A.EID=b0.EID AND A.CREATEBY=b0.opno  and b0.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang b1 ON A.EID=b1.EID AND A.modifyby=b1.opno  and b1.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang b2 ON A.EID=b2.EID AND A.SubmitBy=b2.opno  and b2.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang b3 ON A.EID=b3.EID AND A.ConfirmBy=b3.opno  and b3.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang b4 ON A.EID=b4.EID AND A.CancelBy=b4.opno  and b4.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang b5 ON A.EID=b5.EID AND A.AccountBy=b5.opno and b5.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                +" left join DCP_CATEGORY_LANG g on g.category=b.category and g.eid=b.eid and g.lang_type='"+req.getLangType()+"'" +
                " left join DCP_LOCATION h on h.location=b.location and h.eid=a.eid and h.organizationno=b.organizationno and h.warehouse=b.warehouse "
                + " LEFT JOIN dcp_employee e0 ON A.EID=e0.EID AND A.employeeid=e0.employeeno  "
                + " left join DCP_DEPARTMENT_LANG d0 on d0.eid=a.eid and d0.departno=a.departid and d0.lang_type='"+req.getLangType()+"'"

                + " WHERE "
                + " a.SHOPID='"+req.getShopId()+"' "
                + " AND a.EID='"+req.geteId()+"' " +
                " and a.adjustno='"+adjustNo+"' and a.DOC_TYPE='"+docType+"' " +
                " order by b.item  ");


        sql=sqlbuf.toString();
        return sql;
    }

}
