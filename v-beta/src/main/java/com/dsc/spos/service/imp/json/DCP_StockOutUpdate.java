package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutUpdateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockOutUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;

/**
 * 服務函數：StockOutUpdateDCP
 *    說明：出货单修改保存
 * 服务说明：出货单修改保存
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_StockOutUpdate extends SPosAdvanceService<DCP_StockOutUpdateReq, DCP_StockOutUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockOutUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        List<level1Elm> jsonDatas = request.getDatas();
        
        //必传值不为空
        String stockOutNO = request.getStockOutNo();
        String bDate = request.getbDate();
        String status = request.getStatus();
        String docType = request.getDocType();
        String warehouse = request.getWarehouse();
        String transferWarehouse = request.getTransferWarehouse();
        
        //必传值可以为空
        String memo = request.getMemo();
        String oType = request.getoType();
        String ofNO = request.getOfNo();
        String loadDocType = request.getLoadDocType();
        String loadDocNO = request.getLoadDocNo();
        String deliveryNO = request.getDeliveryNo();
        String transferShop = request.getTransferShop();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();
        
        if (Check.Null(stockOutNO)) {
            errMsg.append("单据编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(bDate)) {
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(warehouse)) {
            errMsg.append("出货仓库不可为空值, ");
            isFail = true;
        }
        if (oType == null) {
            errMsg.append("来源单据类型不可为空值, ");
            isFail = true;
        }
        if (ofNO == null) {
            errMsg.append("来源单据单号不可为空值, ");
            isFail = true;
        }
        if (memo == null) {
            errMsg.append("备注不可为空值, ");
            isFail = true;
        }
        if (loadDocType == null) {
            errMsg.append("转入来源单据类型不可为空值, ");
        }
        if (loadDocNO == null) {
            errMsg.append("转入来源单据单号不可为空值, ");
        }
        if(docType.equals("1")||docType.equals("4")) {
            if (transferShop == null) {
                errMsg.append("调入门店不可为空值, ");
            }
        }
        if(docType.equals("4")) {
            if (Check.Null(transferWarehouse)) {
                errMsg.append("移入仓库不可为空值, ");
                isFail = true;
            }
        }
        if (deliveryNO == null) {
            errMsg.append("物流单号不可为空值, ");
        }
        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totDistriAmt)) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }
        if (Check.Null(totCqty)) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for (level1Elm par : jsonDatas) {
            //必传值不为空
            String item = par.getItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            
            //必传值可以为空
            String oItem = par.getoItem();
            String price = par.getPrice();
            String amt = par.getAmt();
            String warehouseD = par.getWarehouse();
            String baseUnit = par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();
            
            if (Check.Null(item)) {
                errMsg.append("商品"+pluNo+"项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(punit)) {
                errMsg.append("商品"+pluNo+"单位不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pqty)) {
                errMsg.append("商品"+pluNo+"数量不可为空值, ");
                isFail = true;
            }
            if (oItem == null) {
                errMsg.append("商品"+pluNo+"来源项次不可为空值, ");
                isFail = true;
            }
            if (price == null) {
                errMsg.append("商品"+pluNo+"单价不可为空值, ");
                isFail = true;
            }
            if (amt == null) {
                errMsg.append("商品"+pluNo+"金额不可为空值, ");
                isFail = true;
            }
            if (Check.Null(warehouseD)) {
                errMsg.append("商品"+pluNo+"拨出仓库不可为空值, ");
                isFail = true;
            }
            if (baseUnit == null) {
                errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
                isFail = true;
            }
            if (baseQty == null) {
                errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
                isFail = true;
            }
            if (unitRatio == null) {
                errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
                isFail = true;
            }

            List<DCP_StockOutUpdateReq.BatchList> batchList = par.getBatchList();
            if(CollUtil.isNotEmpty(batchList)){
                for (DCP_StockOutUpdateReq.BatchList batch : batchList){
                    if (Check.Null(batch.getItem())) {
                        errMsg.append("商品"+pluNo+"批次项次不可为空值, ");
                        isFail = true;
                    }

                    if (Check.Null(batch.getItem2())) {
                        errMsg.append("商品"+pluNo+"批次item2不可为空值, ");
                        isFail = true;
                    }

                    if (Check.Null(batch.getPUnit())) {
                        errMsg.append("商品"+pluNo+"批次单位不可为空值, ");
                        isFail = true;
                    }

                    if (Check.Null(batch.getPQty())) {
                        errMsg.append("商品"+pluNo+"批次数量不可为空值, ");
                        isFail = true;
                    }

                    if (Check.Null(batch.getUnitRatio())) {
                        errMsg.append("商品"+pluNo+"批次单位换算率不可为空值, ");
                        isFail = true;
                    }
                    if (Check.Null(batch.getBaseUnit())) {
                        errMsg.append("商品"+pluNo+"批次基准单位不可为空值, ");
                        isFail = true;
                    }
                }
            }

            List<DCP_StockOutUpdateReq.TransInLocationList> transInLocationList = par.getTransInLocationList();
            if(CollUtil.isNotEmpty(transInLocationList)) {
                for (DCP_StockOutUpdateReq.TransInLocationList transInLocation : transInLocationList) {
                    if (Check.Null(transInLocation.getItem())) {
                        errMsg.append("商品" + pluNo + "调入项次不可为空值, ");
                        isFail = true;
                    }
                    //if (Check.Null(transInLocation.getOItem())) {
                    //    errMsg.append("商品" + pluNo + "调入项次不可为空值, ");
                    //    isFail = true;
                    //}
                    //if (Check.Null(transInLocation.getTransInLocation())) {
                    //    errMsg.append("商品" + pluNo + "移入库位编号不可为空值, ");
                    //    isFail = true;
                    //}

                    if (Check.Null(transInLocation.getTransInQty())) {
                        errMsg.append("商品" + pluNo + "移入数量不可为空值, ");
                        isFail = true;
                    }
                }
            }


            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_StockOutUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockOutUpdateReq>(){};
    }
    
    @Override
    protected DCP_StockOutUpdateRes getResponseType() {
        return new DCP_StockOutUpdateRes();
    }
    
    @Override
    protected void processDUID(DCP_StockOutUpdateReq req,DCP_StockOutUpdateRes res) throws Exception {
        //try {
            levelElm request = req.getRequest();
            String shopId = req.getShopId();
            String organizationNO = req.getOrganizationNO();
            String eId = req.geteId();
            String bDate = request.getbDate();
            String memo = request.getMemo();
            String docType = request.getDocType();
            String oType = request.getoType();
            String ofNO = request.getOfNo();
            String modifyBy = req.getOpNO();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df=new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            String loadDocType = request.getLoadDocType();
            String loadDocNO = request.getLoadDocNo();
            String deliveryNO = request.getDeliveryNo();
            String transferShop = request.getTransferShop();
            String receiptOrg = request.getReceiptOrg();
            String stockOutNO =  request.getStockOutNo();
            String bsNO = Check.Null(request.getBsNo())?"":request.getBsNo();
            String warehouse = request.getWarehouse();
            String transferWarehouse = request.getTransferWarehouse();
            String totPqty = request.getTotPqty();
            String totAmt =request.getTotAmt();
            String totDistriAmt = request.getTotDistriAmt();
            String totCqty = request.getTotCqty();
            String sourceMenu = request.getSourceMenu();
            String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            String createByName = req.getOpName();
            
            //2019-12-19 增加模板编码
            String pTemplateNo = "";
            pTemplateNo = request.getpTemplateNo();
            
            
            //校验此收货单是否存在
            String sql = "select stockOutNO,Status,BDATE "
                    + " from DCP_STOCKOUT "
                    + " where OrganizationNO = '"+organizationNO+"' "
                    + " and EID = '"+eId+"' "
                    + " and SHOPID = '"+shopId+"' "
                    + " and stockOutNO = '"+stockOutNO+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            
            String keyDate=bDate;
            if (getQData != null && getQData.isEmpty() == false) {
                String status=getQData.get(0).get("STATUS").toString();
                if (status.equals("-1")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，不能修改 ");
                }
                String corp="";
                String receiptCorp="";
                String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
                String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getReceiptOrg()+"' ";
                List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
                if(CollUtil.isNotEmpty(orgList1)){
                    corp = orgList1.get(0).get("CORP").toString();
                }
                List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
                if(CollUtil.isNotEmpty(orgList2)){
                    receiptCorp = orgList2.get(0).get("CORP").toString();
                }

                if("4".equals(docType)){
                    if(Check.NotNull(request.getTransferWarehouse())){
                        String warehouseSql=" select * from dcp_warehouse where eid='"+eId+"' and organizationno='"+organizationNO+"' and warehouse='"+request.getTransferWarehouse()+"'";
                        List<Map<String, Object>> warehouseList = this.doQueryData(warehouseSql, null);
                        if(CollUtil.isNotEmpty(warehouseList)){
                            String islocation = warehouseList.get(0).get("ISLOCATION").toString();
                            if("Y".equals(islocation)){
                                List<DCP_StockOutUpdateReq.level1Elm> datas = request.getDatas();
                                for(DCP_StockOutUpdateReq.level1Elm level1Elm:datas){
                                    BigDecimal dPqty = new BigDecimal(level1Elm.getPqty());
                                    List<DCP_StockOutUpdateReq.TransInLocationList> transInLocationList = level1Elm.getTransInLocationList();
                                    BigDecimal totDqty=new BigDecimal(0);
                                    for (DCP_StockOutUpdateReq.TransInLocationList singleTrans:transInLocationList){
                                        if(Check.Null(singleTrans.getTransInLocation())){
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入库位编号不可为空值 ");
                                        }
                                        if(Check.Null(singleTrans.getTransInQty())){
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入数量不可为空值 ");
                                        }
                                        totDqty=totDqty.add(new BigDecimal(singleTrans.getTransInQty()));
                                    }
                                    if(dPqty.compareTo(totDqty)!=0){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入库位数量不等于明细数量 ");
                                    }
                                }
                            }
                            if("N".equals(islocation)){
                                request.getDatas().forEach(x->{
                                    x.setTransInLocationList(new ArrayList<>());
                                });
                            }
                        }
                    }
                    else{
                        request.getDatas().forEach(x->{
                            x.setTransInLocationList(new ArrayList<>());
                        });
                    }

                }



                if (status.equals("0")) {
                    
                    keyDate=getQData.get(0).get("BDATE").toString();
                    
                    //删除原来单身
                    DelBean db1 = new DelBean("DCP_STOCKOUT_DETAIL");
                    db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    db1 = new DelBean("DCP_STOCKOUT_DETAIL_IMAGE");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    db1 = new DelBean("DCP_STOCKOUT_BATCH");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db4 = new DelBean("DCP_STOCKOUT_DETAIL_LOCATION");
                    db4.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db4.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db4));

                    //String detailSql="select * from dcp_stockout_detail a where a.eid='"+eId+"'" +
                    //        " and a.organizationno='"+organizationNO+"' " +
                    //        " and a.stockOutNo='"+stockOutNO+"' ";
                    //List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

                    //新增單身 (多筆)
                    List<level1Elm> jsonDatas = request.getDatas();
                    int batchItem=0;
                    int locationItem=0;
                    for (level1Elm par : jsonDatas) {


                        if(Check.Null(par.getFeatureNo())){
                            par.setFeatureNo(" ");
                        }
                        if(Check.Null(par.getLocation())){
                            par.setLocation(" ");
                        }
                        if(Check.Null(par.getBatchNo())){
                            par.setBatchNo(" ");
                        }

                        int insColCt = 0;
                        String[] columnsName = {
                                "STOCKOUTNO","SHOPID","item","oItem","pluNO",
                                "punit", "pqty","BASEUNIT", "BASEQTY", "unit_Ratio", "PLU_BARCODE",
                                "price", "amt", "EID", "organizationNO", "WAREHOUSE",
                                "BSNO","PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT",
                                "BDATE","FEATURENO","STOCKQTY","PACKINGNO","OFNO","MES_LOCATION","EXPDATE",
                                "OTYPE","OOTYPE","OOFNO","OOITEM","STOCKOUTNOQTY","TRANSFER_BATCHNO"
                        };
                        
                        DataValue[] columnsVal = new DataValue[columnsName.length];
                        
                        for (int i = 0; i < columnsVal.length; i++) {
                            String keyVal = null;
                            switch (i) {
                                case 0:
                                    keyVal = stockOutNO;
                                    break;
                                case 1:
                                    keyVal = shopId;
                                    break;
                                case 2:
                                    keyVal = par.getItem(); //item
                                    break;
                                case 3:
                                    String oItem = par.getoItem();
                                    if (oItem.equals("") || oItem.length() == 0 ){
                                        oItem="0";
                                    }
                                    keyVal = oItem; //oItem
                                    break;
                                case 4:
                                    keyVal = par.getPluNo(); //pluNO
                                    break;
                                case 5:
                                    keyVal = par.getPunit(); //punit
                                    break;
                                case 6:
                                    keyVal = par.getPqty(); //pqty
                                    break;
                                case 7:
                                    keyVal = par.getBaseUnit();     //wunit
                                    break;
                                case 8:
                                    keyVal = par.getBaseQty();   //wqty
                                    break;
                                case 9:
                                    keyVal = par.getUnitRatio();  //unitRatio
                                    break;
                                case 10:
                                    keyVal = par.getPluBarcode();    //pluBarcode
                                    break;
                                case 11:
                                    keyVal = par.getPrice();    //price
                                    break;
                                case 12:
                                    keyVal = par.getAmt();    //amt
                                    break;
                                case 13:
                                    keyVal = eId;
                                    break;
                                case 14:
                                    keyVal = organizationNO;
                                    break;
                                case 15:
                                    keyVal = par.getWarehouse();
                                    break;
                                case 16:
                                    if(!Check.Null(docType) && docType.equals("3") &&
                                            !Check.Null(sourceMenu) &&
                                            (sourceMenu.equals("0") ||
                                                    sourceMenu.equals("1") ||
                                                    sourceMenu.equals("2"))  )
                                    {
                                        keyVal = bsNO;
                                    }else
                                    {
                                        keyVal = par.getBsNo();
                                    }
                                    break;
                                case 17:
                                    if( par.getPluMemo() == null){
                                        keyVal = "";
                                    }
                                    else{
                                        keyVal = par.getPluMemo();
                                    }
                                    break;
                                case 18:
                                    keyVal = par.getBatchNo();
                                    break;
                                case 19:
                                    keyVal = par.getProdDate();
                                    break;
                                case 20:
                                    keyVal = par.getDistriPrice();
                                    if (Check.Null(keyVal)) keyVal="0";
                                    break;
                                case 21:
                                    keyVal = par.getDistriAmt();
                                    if (Check.Null(keyVal)) keyVal="0";
                                    break;
                                case 22:
                                    keyVal = bDate;
                                    break;
                                case 23:
                                    keyVal = par.getFeatureNo();
                                    if (Check.Null(keyVal))
                                        keyVal=" ";
                                    break;
                                case 24:
                                    keyVal = par.getStockqty();
                                    if (PosPub.isNumericTypeMinus(keyVal)==false)
                                        keyVal="999999";
                                    break;
                                case 25:
                                    keyVal = par.getPackingNo();
                                    break;
                                case 26:
                                    keyVal = par.getOfNo();
                                    break;
                                case 27:
                                    keyVal = par.getLocation();
                                    break;
                                case 28:
                                    keyVal = par.getExpDate();
                                    break;
                                case 29:
                                    keyVal = par.getoType();
                                    break;
                                case 30:
                                    keyVal = par.getOoType();
                                    break;
                                case 31:
                                    keyVal = par.getOofNo();
                                    break;
                                case 32:
                                    keyVal = par.getOoItem();
                                    break;
                                case 33:
                                    keyVal = par.getStoctOutNoQty();
                                    if(Check.Null(keyVal)){
                                        keyVal="0";
                                    }
                                    break;
                                case 34:
                                    keyVal = par.getTransferBatchNo();
                                    break;
                                default:
                                    break;
                            }
                            
                            if (keyVal != null) {
                                insColCt++;
                                if (i == 2 || i == 3 ){
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                }else if (i == 6 || i == 8 || i == 9 || i == 11 || i == 12 || i == 20|| i == 24){
                                    columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                }else{
                                    columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                }
                            } else {
                                columnsVal[i] = null;
                            }
                        }
                        String[] columns2  = new String[insColCt];
                        DataValue[] insValue2 = new DataValue[insColCt];
                        // 依照傳入參數組譯要insert的欄位與數值；
                        insColCt = 0;
                        
                        for (int i=0;i<columnsVal.length;i++){
                            if (columnsVal[i] != null){
                                columns2[insColCt] = columnsName[i];
                                insValue2[insColCt] = columnsVal[i];
                                insColCt ++;
                                if (insColCt >= insValue2.length) break;
                            }
                        }
                        
                        InsBean ib2 = new InsBean("DCP_STOCKOUT_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                        //商品明细图片列表
                        List<DCP_StockOutUpdateReq.level2Elm> imageList = par.getImageList();
                        if (imageList!=null&&!imageList.isEmpty())
                        {
                            String[] columnsName_image = {
                                    "EID", "SHOPID","STOCKOUTNO", "item", "oItem", "IMAGE",
                                    "CREATEOPID", "CREATEOPNAME", "CREATETIME"
                            };
                            InsBean ib_image = new InsBean("DCP_STOCKOUT_DETAIL_IMAGE", columnsName_image);
                            int imageItem = 0;
                            for (DCP_StockOutUpdateReq.level2Elm imageInfo : imageList)
                            {
                                String image = imageInfo.getImage();
                                if (image==null||image.trim().isEmpty())
                                {
                                    continue;
                                }
                                imageItem++;
                                DataValue[] columnsImage = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(stockOutNO, Types.VARCHAR),
                                        new DataValue(imageItem, Types.VARCHAR),//item
                                        new DataValue(par.getItem(), Types.VARCHAR),//oItem
                                        new DataValue(image, Types.VARCHAR),
                                        new DataValue(modifyBy, Types.VARCHAR),
                                        new DataValue(createByName, Types.VARCHAR),
                                        new DataValue(lastModiTime, Types.DATE)
                                };
                                ib_image.addValues(columnsImage);
                            }

                            //防止异常没有图片的垃圾数据
                            if (!ib_image.getValues().isEmpty())
                            {
                                this.addProcessData(new DataProcessBean(ib_image));
                            }

                        }

                        List<DCP_StockOutUpdateReq.BatchList> batchList = par.getBatchList();
                        if(CollUtil.isNotEmpty(batchList)){
                            for (DCP_StockOutUpdateReq.BatchList batch : batchList){
                                batchItem++;

                                if(Check.Null(batch.getBatchNo())){
                                    batch.setBatchNo(" ");
                                }

                                ColumnDataValue batchColumns=new ColumnDataValue();
                                batchColumns.add("EID", DataValues.newString(eId));
                                batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                batchColumns.add("SHOPID", DataValues.newString(shopId));
                                batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                                batchColumns.add("ITEM", DataValues.newString(batchItem));
                                batchColumns.add("ITEM2", DataValues.newString(par.getItem()));
                                batchColumns.add("PLUNO", DataValues.newString(par.getPluNo()));
                                batchColumns.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                                batchColumns.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                                batchColumns.add("LOCATION", DataValues.newString(batch.getLocation()));
                                batchColumns.add("BATCHNO", DataValues.newString(batch.getBatchNo()));
                                batchColumns.add("PRODDATE", DataValues.newString(batch.getProdDate()));
                                batchColumns.add("EXPDATE", DataValues.newString(batch.getExpDate()));
                                batchColumns.add("PUNIT", DataValues.newString(batch.getPUnit()));
                                batchColumns.add("PQTY", DataValues.newString(batch.getPQty()));
                                batchColumns.add("BASEUNIT", DataValues.newString(batch.getBaseUnit()));
                                batchColumns.add("UNITRATIO", DataValues.newString(batch.getUnitRatio()));

                                if(Check.Null(batch.getBaseQty())){
                                    BigDecimal baseQtyDecimal = new BigDecimal(batch.getPQty()).multiply(new BigDecimal(batch.getUnitRatio()));
                                    batch.setBaseQty(baseQtyDecimal.toString());
                                }

                                batchColumns.add("BASEQTY", DataValues.newString(batch.getBaseQty()));

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibb=new InsBean("DCP_STOCKOUT_BATCH",batchColumnNames);
                                ibb.addValues(batchDataValues);
                                this.addProcessData(new DataProcessBean(ibb));
                            }
                        }
                        else{
                            batchItem++;

                            ColumnDataValue batchColumns=new ColumnDataValue();
                            batchColumns.add("EID", DataValues.newString(eId));
                            batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            batchColumns.add("SHOPID", DataValues.newString(shopId));
                            batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                            batchColumns.add("ITEM", DataValues.newString(batchItem));
                            batchColumns.add("ITEM2", DataValues.newString(par.getItem()));
                            batchColumns.add("PLUNO", DataValues.newString(par.getPluNo()));
                            batchColumns.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                            batchColumns.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                            batchColumns.add("LOCATION", DataValues.newString(par.getLocation()));
                            batchColumns.add("BATCHNO", DataValues.newString(par.getBatchNo()));
                            batchColumns.add("PRODDATE", DataValues.newString(par.getProdDate()));
                            batchColumns.add("EXPDATE", DataValues.newString(par.getExpDate()));
                            batchColumns.add("PUNIT", DataValues.newString(par.getPunit()));
                            batchColumns.add("PQTY", DataValues.newString(par.getPqty()));
                            batchColumns.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                            batchColumns.add("BASEQTY", DataValues.newString(par.getBaseQty()));
                            batchColumns.add("UNITRATIO", DataValues.newString(par.getUnitRatio()));

                            String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                            DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibb=new InsBean("DCP_STOCKOUT_BATCH",batchColumnNames);
                            ibb.addValues(batchDataValues);
                            this.addProcessData(new DataProcessBean(ibb));
                        }

                        List<DCP_StockOutUpdateReq.TransInLocationList> transInLocationList = par.getTransInLocationList();
                        if(CollUtil.isNotEmpty(transInLocationList)){

                            for (DCP_StockOutUpdateReq.TransInLocationList   transInLocation : transInLocationList){
                                locationItem++;

                                ColumnDataValue locationColumns=new ColumnDataValue();
                                locationColumns.add("EID", DataValues.newString(eId));
                                locationColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                locationColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                                locationColumns.add("ITEM", DataValues.newString(locationItem));
                                locationColumns.add("OITEM", DataValues.newString(par.getItem()));
                                locationColumns.add("LOCATION", DataValues.newString(transInLocation.getTransInLocation()));

                                BigDecimal tBaseQty = new BigDecimal(transInLocation.getTransInQty()).multiply(new BigDecimal(par.getUnitRatio()));
                                locationColumns.add("PQTY", DataValues.newString(transInLocation.getTransInQty()));
                                locationColumns.add("PUNIT", DataValues.newString(par.getPunit()));
                                locationColumns.add("BASEQTY", DataValues.newString(tBaseQty));
                                locationColumns.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                                locationColumns.add("UNIT_RATIO", DataValues.newString(par.getUnitRatio()));
                                String[] locationColumnNames = locationColumns.getColumns().toArray(new String[0]);
                                DataValue[] locationDataValues = locationColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibl=new InsBean("DCP_STOCKOUT_DETAIL_LOCATION",locationColumnNames);
                                ibl.addValues(locationDataValues);
                                this.addProcessData(new DataProcessBean(ibl));


                            }

                        }



                    }
                    //收货组织
                    if(transferShop==null || transferShop.trim().length()==0 ) {
                        transferShop=receiptOrg;
                    }
                    
                    //【ID1031972】【罗森尼娜3.0】中台退货出库单，ERP接过来之后退货组织变了  BY JINZMA 20230318 这帮ERP就是饭桶
                    if (docType.equals("0")){
                        transferShop = receiptOrg;
                    }
                    
                    
                    //更新单头
                    UptBean ub1 = new UptBean("DCP_STOCKOUT");
                    ub1.addUpdateValue("BDate", new DataValue(bDate, Types.VARCHAR));
                    if(!Check.Null(memo)) {
                        ub1.addUpdateValue("memo", new DataValue(Check.Null(memo), Types.VARCHAR));
                    }
                    if(!Check.Null(docType)) {
                        ub1.addUpdateValue("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
                    }
                    if(!Check.Null(receiptOrg)) {
                        ub1.addUpdateValue("RECEIPT_ORG", new DataValue(receiptOrg, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("oType", new DataValue(oType, Types.VARCHAR));
                    ub1.addUpdateValue("ofNO", new DataValue(ofNO, Types.VARCHAR));
                    if(!Check.Null(loadDocType)) {
                        ub1.addUpdateValue("load_DocType", new DataValue(loadDocType, Types.VARCHAR));
                    }
                    if(!Check.Null(loadDocNO)) {
                        ub1.addUpdateValue("load_DocNO", new DataValue(loadDocNO, Types.VARCHAR));
                    }
                    if(!Check.Null(deliveryNO)) {
                        ub1.addUpdateValue("delivery_NO", new DataValue(deliveryNO, Types.VARCHAR));
                    }
                    if(!Check.Null(transferShop)) {
                        ub1.addUpdateValue("transfer_Shop", new DataValue(transferShop, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("modifyBy", new DataValue(modifyBy, Types.VARCHAR));
                    ub1.addUpdateValue("modify_Date", new DataValue(modifyDate, Types.VARCHAR));
                    ub1.addUpdateValue("modify_Time", new DataValue(modifyTime, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Pqty", new DataValue(totPqty, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Amt", new DataValue(totAmt, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Cqty", new DataValue(totCqty, Types.VARCHAR));
                    ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("PTEMPLATENO", new DataValue(pTemplateNo, Types.VARCHAR));
                    ub1.addUpdateValue("memo", new DataValue(request.getMemo(), Types.VARCHAR));
                    ub1.addUpdateValue("CORP", new DataValue(corp, Types.VARCHAR));
                    ub1.addUpdateValue("RECEIPTCORP", new DataValue(receiptCorp, Types.VARCHAR));

                    if(!Check.Null(bsNO)) {
                        ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                    }
                    if(!Check.Null(warehouse)) {
                        ub1.addUpdateValue("warehouse", new DataValue(warehouse, Types.VARCHAR));
                    }
                    if(!Check.Null(transferWarehouse)) {
                        ub1.addUpdateValue("transfer_Warehouse", new DataValue(transferWarehouse, Types.VARCHAR));
                    }
                    if(!Check.Null(bsNO)) {
                        ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                    }
                    if(!Check.Null(sourceMenu)){
                        ub1.addUpdateValue("SOURCEMENU", new DataValue(sourceMenu, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    
                    //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，之前易成用的是（易成用的要货发货单功能）--服务端 by jinzma 20231215
                    ub1.addUpdateValue("DELIVERYBY", new DataValue(request.getDeliveryBy(), Types.VARCHAR));
                    ub1.addUpdateValue("EMPLOYEEID", new DataValue(request.getEmployeeId(), Types.VARCHAR));
                    ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
                    ub1.addUpdateValue("RDATE", new DataValue(request.getrDate(), Types.VARCHAR));
                    ub1.addUpdateValue("PACKINGNO", new DataValue(request.getPackingNo(), Types.VARCHAR));

                    ub1.addUpdateValue("INVWAREHOUSE", new DataValue(request.getInvWarehouse(), Types.VARCHAR));
                    ub1.addUpdateValue("ISTRANINCONFIRM", new DataValue(request.getIsTranInConfirm(), Types.VARCHAR));
                    ub1.addUpdateValue("OOTYPE", new DataValue(request.getOoType(), Types.VARCHAR));
                    ub1.addUpdateValue("OOFNO", new DataValue(request.getOofNo(), Types.VARCHAR));
                    ub1.addUpdateValue("RECEIPTDATE", new DataValue(request.getReceiptDate(), Types.VARCHAR));
                    ub1.addUpdateValue("DELIVERYDATE", new DataValue(request.getDeliveryDate(), Types.VARCHAR));


                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    
                    this.addProcessData(new DataProcessBean(ub1));

                    //配货出库编辑，根据入参【来源单号】、【来源项次】关联通知单更新行状态=4-出货中（如果通知单行状态="1-待出货“）
                    for (level1Elm par:jsonDatas){
                        String ofNo = par.getOfNo();
                        String oItem = par.getoItem();
                        if(Check.NotNull(ofNO)&&Check.NotNull(oItem)){
                            UptBean ub2 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                            ub2.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));

                            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub2.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
                            ub2.addCondition("ITEM", new DataValue(oItem, Types.VARCHAR));
                            ub2.addCondition("STATUS", new DataValue("1", Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub2));
                        }
                    }
                    
                    this.doExecuteDataToDB();
                    
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");

                    if(!corp.equals(receiptCorp)){
                        //单据类型(0退货出库 1调拨出库 3其他出库 4移仓出库 5配送出库)
                        //配货出库
                        if("5".equals(docType)) {

                            DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                            inReq.setServiceId("DCP_InterSettleDataGenerate");
                            inReq.setToken(req.getToken());
                            //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                            request1.setOrganizationNo(req.getOrganizationNO());
                            request1.setBillNo(stockOutNO);
                            request1.setSupplyOrgNo(req.getOrganizationNO());
                            request1.setReturnSupplyPrice("Y");
                            request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10003.getType());
                            request1.setDetail(new ArrayList<>());
                            for (DCP_StockOutUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                                detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
                                detail.setSourceBillNo(par.getOfNo());
                                detail.setSourceItem(par.getoItem());
                                detail.setItem(String.valueOf(par.getItem()));
                                detail.setPluNo(par.getPluNo());
                                detail.setFeatureNo(par.getFeatureNo());
                                detail.setPUnit(par.getPunit());
                                detail.setPQty(String.valueOf(par.getPqty()));
                                detail.setReceivePrice(par.getDistriPrice());
                                detail.setReceiveAmt(par.getDistriAmt());
                                detail.setSupplyPrice("");
                                detail.setSupplyAmt("");
                                request1.getDetail().add(detail);
                            }
                            inReq.setRequest(request1);
                            ParseJson pj = new ParseJson();
                            String jsontemp = pj.beanToJson(inReq);

                            DispatchService ds = DispatchService.getInstance();
                            String resXml = ds.callService(jsontemp, StaticInfo.dao);
                            DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                            });
                            if (resserver.isSuccess() == false) {
                                res.setSuccess(true);
                                res.setServiceStatus("000");
                                res.setServiceDescription("内部结算失败！");
                                return;
                                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                            }
                        }

                        if("0".equals(docType)){

                            DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                            inReq.setServiceId("DCP_InterSettleDataGenerate");
                            inReq.setToken(req.getToken());
                            //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                            request1.setOrganizationNo(req.getOrganizationNO());
                            request1.setBillNo(stockOutNO);
                            request1.setSupplyOrgNo(req.getRequest().getReceiptOrg());
                            request1.setReturnSupplyPrice("Y");
                            request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_10003.getType());
                            request1.setDetail(new ArrayList<>());
                            for (DCP_StockOutUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                                detail.setReceiveOrgNo(req.getOrganizationNO());
                                detail.setSourceBillNo(par.getOfNo());
                                detail.setSourceItem(par.getoItem());
                                detail.setItem(String.valueOf(par.getItem()));
                                detail.setPluNo(par.getPluNo());
                                detail.setFeatureNo(par.getFeatureNo());
                                detail.setPUnit(par.getPunit());
                                detail.setPQty(String.valueOf(par.getPqty()));
                                detail.setReceivePrice(par.getDistriPrice());
                                detail.setReceiveAmt(par.getDistriAmt());
                                detail.setSupplyPrice("");
                                detail.setSupplyAmt("");
                                request1.getDetail().add(detail);
                            }
                            inReq.setRequest(request1);
                            ParseJson pj = new ParseJson();
                            String jsontemp = pj.beanToJson(inReq);

                            DispatchService ds = DispatchService.getInstance();
                            String resXml = ds.callService(jsontemp, StaticInfo.dao);
                            DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                            });
                            if (resserver.isSuccess() == false) {
                                res.setSuccess(true);
                                res.setServiceStatus("000");
                                res.setServiceDescription("内部结算失败！");
                                return;
                                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                            }
                        }

                        if("1".equals(docType)){
                            DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                            inReq.setServiceId("DCP_InterSettleDataGenerate");
                            inReq.setToken(req.getToken());
                            //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                            request1.setOrganizationNo(req.getOrganizationNO());
                            request1.setBillNo(stockOutNO);
                            request1.setSupplyOrgNo("");
                            request1.setReturnSupplyPrice("Y");
                            request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10005.getType());
                            request1.setDetail(new ArrayList<>());
                            for (DCP_StockOutUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                                detail.setReceiveOrgNo(req.getOrganizationNO());
                                detail.setSourceBillNo(par.getOfNo());
                                detail.setSourceItem(par.getoItem());
                                detail.setItem(String.valueOf(par.getItem()));
                                detail.setPluNo(par.getPluNo());
                                detail.setFeatureNo(par.getFeatureNo());
                                detail.setPUnit(par.getPunit());
                                detail.setPQty(String.valueOf(par.getPqty()));
                                detail.setReceivePrice("");
                                detail.setReceiveAmt("");
                                detail.setSupplyPrice("");
                                detail.setSupplyAmt("");
                                request1.getDetail().add(detail);

                                if(!req.getOrganizationNO().equals(req.getRequest().getReceiptOrg())){
                                    detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
                                    detail.setSourceBillNo(par.getOfNo());
                                    detail.setSourceItem(par.getoItem());
                                    detail.setItem(String.valueOf(par.getItem()));
                                    detail.setPluNo(par.getPluNo());
                                    detail.setFeatureNo(par.getFeatureNo());
                                    detail.setPUnit(par.getPunit());
                                    detail.setPQty(String.valueOf(par.getPqty()));
                                    detail.setReceivePrice("");
                                    detail.setReceiveAmt("");
                                    detail.setSupplyPrice("");
                                    detail.setSupplyAmt("");
                                    request1.getDetail().add(detail);
                                }

                            }
                            inReq.setRequest(request1);
                            ParseJson pj = new ParseJson();
                            String jsontemp = pj.beanToJson(inReq);

                            DispatchService ds = DispatchService.getInstance();
                            String resXml = ds.callService(jsontemp, StaticInfo.dao);
                            DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                            });
                            if (resserver.isSuccess() == false) {
                                res.setSuccess(true);
                                res.setServiceStatus("000");
                                res.setServiceDescription("内部结算失败！");
                                return;
                                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                            }
                        }
                    }

                } else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此单已确认，不允许修改！");
                }
                
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，请重新输入！");
            }
        //}
        //catch (Exception e) {
         //   throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected String getQuerySql(DCP_StockOutUpdateReq req) throws Exception {
        return null;
    }
    
}
