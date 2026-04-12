package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_StockInQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockInQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服務函數：StockInGet
 *    說明：收货单查询
 * 服务说明：收货单查询
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_StockInQuery extends SPosBasicService<DCP_StockInQueryReq, DCP_StockInQueryRes> {
    Logger logger = LogManager.getLogger(DCP_StockInQuery.class.getName());
    @Override
    protected boolean isVerifyFail(DCP_StockInQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
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
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockInQueryReq> getRequestType() {
        return new TypeToken<DCP_StockInQueryReq>(){};
    }

    @Override
    protected DCP_StockInQueryRes getResponseType() {
        return new DCP_StockInQueryRes();
    }

    @Override
    protected DCP_StockInQueryRes processJson(DCP_StockInQueryReq req) throws Exception {
        //取得 SQL
        String sql = null;
        levelElm request = req.getRequest();
        //查詢條件
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String docType = request.getDocType();
        String status = request.getStatus();
        if (status == null) {
            status = "6";
        }

        try {
            //查詢資料
            DCP_StockInQueryRes res = this.getResponse();

            //给分页字段赋值
            sql = this.getQuerySql_Count(req);
            logger.info("配送收货SQL:"+sql);
            //查询总笔数
            String[] conditionValues_Count = {langType, langType, status,docType, shopId, eId, langType, langType, docType, shopId, eId }; //查詢條件
            List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
            int totalRecords;								//总笔数
            int totalPages;
            //指定页码的单据信息
            String withasSql_dno="";

            //总页数
            if (getQData_Count != null && !getQData_Count.isEmpty()) {
                Map<String, Object> oneData_Count = getQData_Count.get(0);
                String num = oneData_Count.get("NUM").toString();
                totalRecords=Integer.parseInt(num);

                //算總頁數
                if(req.getPageSize()==0) {
                    req.setPageSize(20);
                }
                if(req.getPageNumber()==0) {
                    req.setPageNumber(1);
                }

                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //計算起啟位置
                int startRow = ((req.getPageNumber() - 1) * req.getPageSize());

                //根据页码取记录
                StringBuffer sJoinDno=new StringBuffer();

                //for (int i = startRow; i < getQData_Count.size(); i++)
                for (int i = 0; i < getQData_Count.size(); i++) {
                    if (i<(startRow+req.getPageSize())) {
                        sJoinDno.append(getQData_Count.get(i).get("DNO").toString()+",");
                    }
                }

                Map<String, String> map=new HashMap<String, String>();
                map.put("DNO", sJoinDno.toString());

                MyCommon cm=new MyCommon();
                withasSql_dno=cm.getFormatSourceMultiColWith(map);

                map=null;
                cm=null;
                sJoinDno.setLength(0);
                sJoinDno=null;

            } else {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);



            //有单
            if (!withasSql_dno.equals("")) {

                //计算起始位置
                int pageNumber = req.getPageNumber();
                int pageSize = req.getPageSize();
                int startRow = ((pageNumber - 1) * pageSize);
                startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
                startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

                //处理==绑定变量SQL的写法
                List<DataValue> lstDV=new ArrayList<>();

                //单头
                sql = this.getDnoSql(req, withasSql_dno,lstDV);				//查询明细数据
                logger.info("配送收货单头SQL:"+sql);
                List<Map<String, Object>> getQData1 = this.executeQuerySQL_BindSQL(sql,lstDV);

                if (getQData1 != null && !getQData1.isEmpty()) {
                    // 拼接返回图片路径  by jinzma 20210705
                    String isHttps= PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                    String httpStr=isHttps.equals("1")?"https://":"http://";
                    String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                    if (domainName.endsWith("/")) {
                        domainName = httpStr + domainName + "resource/image/";
                    }else{
                        domainName = httpStr + domainName + "/resource/image/";
                    }
                    // 有資料，取得詳細內容
                    //单头主键字段
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                    condition.put("DNO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData1, condition);

                    res.setDatas(new ArrayList<DCP_StockInQueryRes.level1Elm>());
                    for (Map<String, Object> oneData : getQHeader) {
                        DCP_StockInQueryRes.level1Elm oneLv1 = res.new level1Elm();
                        oneLv1.setDatas(new ArrayList<DCP_StockInQueryRes.level2Elm>());

                        // 取得第一層資料庫搜尋結果
                        String shop1 = oneData.get("SHOPID").toString();
                        String stockInNO = oneData.get("STOCKINNO").toString();
                        String processERPNo = oneData.get("PROCESSERPNO").toString();
                        String dNO = oneData.get("DNO").toString();
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
                        String pTemplateName = "";
                        if(!docType.equals("4")){
                            pTemplateName = oneData.get("PTEMPLATE_NAME").toString();
                        }
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
                        String modifyByName = oneData.get("MODIFYBYNAME").toString();
                        String accountBy = oneData.get("ACCOUNTBY").toString();
                        String accountDate = oneData.get("ACCOUNTDATE").toString();
                        String accountTime = oneData.get("ACCOUNTTIME").toString();
                        String accountByName = oneData.get("ACCOUNTBYNAME").toString();
                        String totPqty = oneData.get("TOTPQTY").toString();
                        String totCqty = oneData.get("TOTCQTY").toString();
                        String warehouse     = oneData.get("MASTER_WAREHOUSE").toString();
                        String warehouseName = oneData.get("MASTER_WAREHOUSENAME").toString();
                        String receiptDate   = oneData.get("RECEIPTDATE").toString();
                        String deliveryNO = oneData.get("DELIVERY_NO").toString();
                        String totAmt = oneData.get("TOTAMT").toString();
                        String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                        String packingNo = oneData.get("PACKINGNO").toString();
                        String receiving_rdate = oneData.get("RECEIVING_RDATE").toString();
                        String stockinno_origin = oneData.get("STOCKINNO_ORIGIN").toString();
                        String stockinno_refund = oneData.get("STOCKINNO_REFUND").toString();


                        for (Map<String, Object> oneData2 : getQData1) {
                            //过滤属于此单头的明细
                            if(!dNO.equals(oneData2.get("DNO")))
                                continue;
                            DCP_StockInQueryRes.level2Elm oneLv2 = res.new level2Elm();
                            String item = oneData2.get("ITEM").toString();
                            String oType2 = oneData2.get("OTYPE").toString();
                            String ofNO2 = oneData2.get("OFNO").toString();
                            String oItem = oneData2.get("OITEM").toString();
                            String ooType = oneData2.get("OOTYPE").toString();
                            String oofNO = oneData2.get("OOFNO").toString();
                            String ooItem = oneData2.get("OOITEM").toString();
                            int i_item;
                            int i_oItem;
                            if (item == null || item.length()==0 ){
                                i_item=0;
                            } else {
                                float f= Float.parseFloat(item);
                                i_item=(int) Math.floor(f);
                            }

                            if (oItem == null || oItem.length()==0 ){
                                i_oItem=0;
                            } else {
                                float f= Float.parseFloat(oItem);
                                i_oItem=(int) Math.floor(f);
                            }
                            String pluNO = oneData2.get("PLUNO").toString();
                            String pluMemo = oneData2.get("PLUMEMO").toString();
                            String pluName = oneData2.get("PLUNAME").toString();
                            String spec = oneData2.get("SPEC").toString();
                            String punit = oneData2.get("PUNIT").toString();
                            String punitName = oneData2.get("PUNITNAME").toString();
                            String pqty = oneData2.get("PQTY").toString();
                            float f_pqty;
                            if (pqty == null || pqty.length()==0 ){
                                f_pqty=0;
                            } else 	f_pqty =  Float.parseFloat(pqty);

                            String receivingqty = oneData2.get("RECEIVINGQTY").toString();
                            float f_receivingqty;
                            if (receivingqty == null || receivingqty.length()==0 ){
                                f_receivingqty=0;
                            } else 	f_receivingqty =  Float.parseFloat(receivingqty);
                            String poQty = oneData2.get("POQTY").toString();
                            float f_poQty;
                            if(poQty == null || poQty.length()==0){
                                f_poQty=0;
                            }else f_poQty = Float.parseFloat(poQty);

                            String diffReqQty = oneData2.get("DIFFREQQTY").toString();
                            float f_diffReqQty;
                            if(diffReqQty == null || diffReqQty.length()==0){
                                f_diffReqQty=0;
                            }else f_diffReqQty = Float.parseFloat(diffReqQty);

                            String diffQty = oneData2.get("DIFFQTY").toString();
                            float f_diffQty;
                            if(diffQty == null || diffQty.length()==0){
                                f_diffQty=0;
                            }else f_diffQty = Float.parseFloat(diffQty);

                            String price = oneData2.get("PRICE").toString();
                            float f_price;
                            if (price == null || price.length()==0 ){
                                f_price=0;
                            } else 	f_price =  Float.parseFloat(price);

                            String amt = oneData2.get("AMT").toString();
                            if (amt == null || amt.length()==0 ){
                                amt="0";
                            }
                            String distriAmt = oneData2.get("DISTRIAMT").toString();
                            if (Check.Null(distriAmt)) distriAmt="0";

                            String pluBarcode = oneData2.get("PLUBARCODE").toString();
                            String unitRatio = oneData2.get("UNITRATIO").toString();
                            String detail_warehouse = oneData2.get("DETAIL_WAREHOUSE").toString();
                            String RETWQTY = oneData2.get("RETWQTY").toString()==null||oneData2.get("RETWQTY").toString().isEmpty()?"0":oneData2.get("RETWQTY").toString();
                            //换算成已退货的包装单位和包装数量
                            float fRETWQTY="0".equals(unitRatio)?0:(new BigDecimal(RETWQTY)).divide(new BigDecimal(unitRatio), RoundingMode.HALF_UP).floatValue();
                            String procRate = oneData2.get("PROCRATE").toString()==null||oneData2.get("PROCRATE").toString().isEmpty()?"0":oneData2.get("PROCRATE").toString();
                            String batchNO = oneData2.get("BATCH_NO").toString();
                            String isBatch = oneData2.get("ISBATCH").toString();

                            // 【ID1017769】【大连大万3.0】总部发货后，门店没有收到  by jinzma 20210517 isBatch为空抛异常，未关联到dcp_goods
                            if (Check.Null(isBatch)){
                                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"ofNO来源单号:"+ofNO+" 商品:"+pluNO+" 在商品资料档中不存在,请确认"+"\r\n");
                                isBatch="N";   //以上处理是避免查询走不下去，先给N，在DCP_StockInProcess服务里面加管控
                            }

                            String prodDate = oneData2.get("PROD_DATE").toString();
                            String distriPrice = oneData2.get("DISTRIPRICE").toString();

                            String punitUDLength = "2";//默认2
                            String punitUDLength_db = oneData2.get("PUNIT_UDLENGTH").toString();

                            try {
                                int punitUDLength_i = Integer.parseInt(punitUDLength_db);
                                punitUDLength = punitUDLength_i+"";
                            } catch (Exception ignored) {
                            }

                            String baseQty = oneData2.get("BASEQTY").toString();
                            String baseUnit = oneData2.get("BASEUNIT").toString();
                            String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                            String featureName = oneData2.get("FEATURENAME").toString();
                            String featureNo = oneData2.get("FEATURENO").toString();
                            String listImage = oneData2.get("LISTIMAGE").toString();
                            if (!Check.Null(listImage)){
                                listImage = domainName+listImage;
                            }

                            String detailPackingNo = oneData2.get("DETAILPACKINGNO").toString();

                            String item_origin = oneData2.get("ITEM_ORIGIN").toString();
                            String pqty_origin = oneData2.get("PQTY_ORIGIN").toString();
                            String pqty_refund = oneData2.get("PQTY_REFUND").toString();
                            String transfer_batchno = oneData2.get("TRANSFER_BATCHNO").toString();

                            // 處理調整回傳值；
                            oneLv2.setItem(i_item);
                            oneLv2.setoType(oType2);
                            oneLv2.setOfNo(ofNO2);
                            oneLv2.setoItem(i_oItem);
                            oneLv2.setOoType(ooType);
                            oneLv2.setOofNo(oofNO);
                            oneLv2.setOoItem(ooItem);
                            oneLv2.setPluNo(pluNO);
                            oneLv2.setPluMemo(pluMemo);
                            oneLv2.setPluName(pluName);
                            oneLv2.setSpec(spec);
                            oneLv2.setPunit(punit);
                            oneLv2.setPunitName(punitName);
                            oneLv2.setPqty(f_pqty);
                            oneLv2.setPoQty(f_poQty);
                            oneLv2.setDiffReqQty(f_diffReqQty);
                            oneLv2.setDiffQty(f_diffQty);
                            oneLv2.setPrice(f_price);
                            oneLv2.setAmt(amt);
                            oneLv2.setDistriAmt(distriAmt);
                            oneLv2.setReceivingQty(f_receivingqty);
                            oneLv2.setPluBarcode(pluBarcode);
                            oneLv2.setUnitRatio(unitRatio);
                            oneLv2.setWarehouse(detail_warehouse);
                            oneLv2.setRoutunit(punit);
                            oneLv2.setRoutqty(fRETWQTY);
                            oneLv2.setRoutunitName(punitName);
                            oneLv2.setProcRate(procRate);
                            oneLv2.setBatchNo(batchNO);
                            oneLv2.setIsBatch(isBatch);
                            oneLv2.setProdDate(prodDate);
                            oneLv2.setDistriPrice(distriPrice);
                            oneLv2.setPunitUdLength(punitUDLength);
                            oneLv2.setBaseQty(baseQty);
                            oneLv2.setBaseUnit(baseUnit);
                            oneLv2.setBaseUnitName(baseUnitName);
                            oneLv2.setFeatureNo(featureNo);
                            oneLv2.setFeatureName(featureName);
                            oneLv2.setListImage(listImage);
                            oneLv2.setPackingNo(detailPackingNo);
                            //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221109
                            oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
                            oneLv2.setItem_origin(item_origin);
                            oneLv2.setPqty_origin(pqty_origin);
                            oneLv2.setPqty_refund(pqty_refund);
                            oneLv2.setTransferBatchNo(transfer_batchno);

                            oneLv1.getDatas().add(oneLv2);

                        }

                        // 處理調整回傳值
                        oneLv1.setShopId(shop1);
                        oneLv1.setStockInNo(stockInNO);
                        oneLv1.setProcessERPNo(processERPNo);
                        oneLv1.setbDate(bDate);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(status1);
                        oneLv1.setDocType(docType1);
                        oneLv1.setBsNo(bsNO);
                        oneLv1.setBsName(bsName);
                        oneLv1.setTransferShop(transferShop);
                        oneLv1.setTransferShopName(transferShopName);
                        oneLv1.setoType(oType);
                        oneLv1.setOfNo(ofNO);
                        oneLv1.setpTemplateNo(pTemplateNO);
                        oneLv1.setpTemplateName(pTemplateName);
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

                        //【ID1024504】【戴氏3.0】3.0门店管理，采购入库，不显示需求日期 by jinzma 20220316 今天小区隔离做核酸
                        //此处优先取ERP下传接口中的需求日期
                        if (Check.Null(receiving_rdate)){
                            receiving_rdate = oneData.get("RDATE").toString();
                        }
                        oneLv1.setrDate(receiving_rdate);

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

                        //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                        // 之前易成用的是（易成用的要货发货单功能）--服务端
                        oneLv1.setDeliveryBy(oneData.get("DELIVERYBY").toString());
                        oneLv1.setDeliveryName(oneData.get("DELIVERYNAME").toString());
                        oneLv1.setDeliveryTel(oneData.get("DELIVERYTEL").toString());

                        oneLv1.setCorp(oneData.get("CORP").toString());
                        oneLv1.setDeliveryCorp(oneData.get("DELIVERYCORP").toString());

                        res.getDatas().add(oneLv1);
                    }
                } else {
                    res.setDatas(new ArrayList<DCP_StockInQueryRes.level1Elm>());
                }

            } else {
                res.setDatas(new ArrayList<DCP_StockInQueryRes.level1Elm>());
            }

            return res;

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        //調整查出來的資料
    }

    protected String getDnoSql(DCP_StockInQueryReq req,String withasSql_dno,List<DataValue> lstDV) throws Exception {
        levelElm request = req.getRequest();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String docType = request.getDocType();

        //2018-11-09 yyy 添加beginDate 和 endDate
        String beginDate =request.getBeginDate();
        String endDate =request.getEndDate();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String dateType = request.getDateType();  /// 日期类型  bDate：单据日期（默认）,receiptDate：预计到货日 BY JZMA 20201118

        //处理==绑定变量SQL的写法
        if (lstDV == null) {
            lstDV=new ArrayList<>();
        }
        DataValue dv=null;

        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append("select EE.rw rw,EE.dNO,EE.processERPNO,EE.stockInNO,EE.STOCKINNO_ORIGIN,EE.STOCKINNO_REFUND,EE.bsNO,EE.bsName,EE.pTemplateNO pTemplateNO,"
                + " PTEMPLATE_NAME,EE.SHOPID SHOPID,"
                + " to_number(EE.bDate) as bDate,EE.memo memo,EE.status status,EE.docType,EE.oType,EE.ofNO,EE.loadDocType,EE.loadDocNO,"
                + " EE.createBy createBy,transferShop,totPqty,totCqty,"
                + " totAmt,ee.TOT_DISTRIAMT,EE.DELIVERY_NO,EE.PACKINGNO,item,pluMemo,oItem,pluNO,receivingqty,punit,poQty,pluBarcode,"
                + " baseunit,baseqty,unitRatio,"
                + " price,amt,DISTRIAMT,diffQty,"
                + " diffReqQty,pqty,realQty,spec,"
                + " pluName,punitName,createByName  ,transferShopName,diffStatus,pTemplateName,differenceNO,RETWQTY, "
                + " BASEUNITNAME,ooType,oofNO,ooItem,ITEM_ORIGIN,PQTY_ORIGIN,PQTY_REFUND,master_warehouse,master_warehouseName,detail_warehouse,"
                + " EE.PROCESS_STATUS,EE.UPDATE_TIME,"
                + " REITEM,PROCRATE, "
                + " EE.createDate, EE.createTime, "
                + " EE.ModifyBy , EE.modifyDate ,  EE.modifyTime  , "
                + " EE.confirmBy, EE.confirmDate , EE.ConfirmTime, "
                + " EE.cancelBy , EE.cancelDate , EE.cancelTime , "
                + " EE.accountBY ,EE.accountDate, EE.accountTime ,"
                + " EE.submitBy, EE.submitDate , EE.submitTime , "
                + " EE.modifyByName ,EE.cancelByName , EE.ConfirmByName ,  "
                + " EE.submitByName,  EE.accountByName,  "
                + " EE.BATCH_NO,EE.PROD_DATE,EE.DISTRIPRICE,EE.ISBATCH,EE.RECEIPTDATE,punit_UDLENGTH,rdate,"
                + " FEATURENO,FEATURENAME,LISTIMAGE,SPEC,detailpackingno,receiving_rdate,baseunitudlength,"
                + " ee.deliveryby,ee.deliveryname,ee.deliverytel,ee.TRANSFER_BATCHNO,ee.corp,ee.deliverycorp  "
                + " from ( "
                + " select rw,a.EID,a.organizationNO,a.bsNO,L.REASON_NAME as bsName,a.dno,a.processERPNO,a.stockInNO,a.STOCKINNO_ORIGIN,a.STOCKINNO_REFUND,"
                + " a.ptemplateno,a.SHOPID,to_number(a.bDate) as bdate,"
                + " a.memo,a.status, a.docType,a.oType  "
                + " ,a.ofno,a.loadDocType,a.loadDocNO,a.createBy,a.transferShop "
                + " ,a.totPqty,a.totCqty,a.totAmt,a.TOT_DISTRIAMT,a.DELIVERY_NO,a.PACKINGNO "
                + " ,a.item,a.oItem,a.pluMemo,a.pluNO,a.receivingqty,a.punit,a.poQty,a.pluBarcode "
                + " ,a.baseunit,a.baseqty, a.unitRatio,a.price,a.amt,a.DISTRIAMT,a.diffQty,a.diffReqQty,a.pqty,(a.pqty+a.diffqty) as realqty "
                + " ,d.plu_name as pluName,e.uname as punitName,f.op_name as createByName "
                + " ,h.SHOPNAME as transferShopName,i.status as diffStatus,k.ptemplate_Name as ptemplatename,i.differenceno"
                + " ,ulang.uname as BASEUNITNAME,ooType,oofNO,ooItem,ITEM_ORIGIN,PQTY_ORIGIN,PQTY_REFUND,a.master_warehouse,"
                + " DCP_WAREHOUSE_lang.warehouse_name as master_warehouseName,"
                + " a.detail_warehouse,RETWQTY,A.PROCESS_STATUS,A.UPDATE_TIME,REITEM,a.PROCRATE "
                + " ,A.createDate, A.createTime,  "
                + " a.ModifyBy , a.modifyDate ,  a.modifyTime  , "
                + " a.confirmBy, a.confirmDate , a.ConfirmTime,   "
                + " a.cancelBy , a.cancelDate , a.cancelTime , "
                + " a.accountBY ,a.accountDate, a.accountTime ,"
                + " a.submitBy, a.submitDate , a.submitTime , "
                + " f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName ,  "
                + " f4.op_name as submitByName,  f5.op_name as accountByName, "
                + " a.BATCH_NO,a.PROD_DATE,a.DISTRIPRICE,c.ISBATCH,a.RECEIPTDATE, U.UDLENGTH AS punit_UDLENGTH,"
                + " k.PTEMPLATE_NAME AS PTEMPLATE_NAME,rdate,a.FEATURENO,fn.FEATURENAME,image.LISTIMAGE,gul.spec,a.detailpackingno,"
                + " receiving_rdate,bul.udlength as baseunitudlength,a.deliveryby,a.deliveryname,a.deliverytel,a.TRANSFER_BATCHNO,a.corp,a.deliverycorp "
                + " from ( "
                + " SELECT 1 as rw,A.EID,A.ORGANIZATIONNO,cast('' as nvarchar2(10)) as bsNO,a.receivingno as dno,"
                + " cast('' as nvarchar2(30)) as processERPNo ,cast('' as nvarchar2(30)) as stockInNO,cast('' as nvarchar2(30)) as STOCKINNO_ORIGIN,cast('' as nvarchar2(30)) as STOCKINNO_REFUND,A.PTEMPLATENO,A.SHOPID,"
                + " to_number(A.bDate) as bdate,A.memo,A.status,"
                + " A.DOC_TYPE as docType,A.DOC_TYPE as oType "
                + " ,A.ReceivingNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO, A.CREATEBY as createBy,"
                + " A.TRANSFER_SHOP as transferShop "
                + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,a.DELIVERY_NO,a.PACKINGNO "
                + " ,b.item,b.oItem,b.plu_Memo as pluMemo,b.pluNO,b.PQTY AS receivingqty,b.pUnit,b.poQty,b.plu_barcode as pluBarcode "
                + " ,b.baseunit,b.baseqty,b.unit_ratio as unitRatio,b.price,b.amt,b.DISTRIAMT,0 as diffQty,0 as diffReqQty,0 as pqty"
                + " ,cast('' as nvarchar2(1)) as ooType,p.porderno as oofNO"
                + " ,cast(0 as Integer) as ooItem,cast('0' as nvarchar2(30)) as ITEM_ORIGIN,cast('0' as nvarchar2(30)) as PQTY_ORIGIN,cast('0' as nvarchar2(30)) as PQTY_REFUND,b.baseunit as unit,a.warehouse as master_warehouse,b.warehouse as detail_warehouse,0 as RETWQTY,"
                + " Translate('N' USING NCHAR_CS) PROCESS_STATUS,A.UPDATE_TIME,B.ITEM REITEM,B.PROC_RATE as PROCRATE "
                + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,  "
                + " a.ModifyBy , a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime,   "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime , "
                + " a.accountBY ,a.account_date AS accountDate, a.account_time AS accountTime ,"
                + " a.submitBy, a.submit_date AS submitDate , a.submit_time AS submitTime,  "
                + " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,a.RECEIPTDATE,p.rdate,b.FEATURENO,b.packingno as detailpackingno,"
                + " a.rdate as receiving_rdate,a.deliveryby,dm.opname as deliveryname,dm.phone as deliverytel,cast('' as nvarchar2(30)) as TRANSFER_BATCHNO,cast('' as nvarchar2(30)) as corp,cast('' as nvarchar2(30)) as deliverycorp "
                + " FROM DCP_RECEIVING A "
                + " INNER JOIN DCP_RECEIVING_DETAIL b ON A.EID=b.EID AND A.SHOPID=b.SHOPID AND A.RECEIVINGNO=b.RECEIVINGNO "  //and A.BDATE=b.BDATE
                + " LEFT JOIN DCP_porder p on  b.OFNO=p.PORDERNO AND b.EID=p.EID AND b.SHOPID=p.SHOPID "
                + " LEFT join DCP_ptemplate t on p.ptemplateno=t.ptemplateno and p.EID=t.EID and t.doc_type='0' "
                + " left join dcp_deliveryman dm on a.eid=dm.eid and a.deliveryby=dm.opno"
                + " where a.doc_type=? and a.SHOPID=? and a.EID=? ");

        //?问号参数赋值处理
        dv=new DataValue(docType, Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(shopId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);

        //【ID1018832】【货郎】门店管理-配送收货，待收货的单据不收查询时间范围约束，，没收货就始终显示始终显示
        //预计到货日如果为空就查出来，如果有值就判断截止日期 by jinzma 20210630
        /*if (!Check.Null(dateType) && dateType.equals("receiptDate")) {
            sqlbuf.append(" and (a.receiptdate between ? and ? or a.receiptdate is null) ");
		}else {
			///红艳要求这样改的，原本通知单未收货的都会显示，现在拿掉这段注释  BY JZMA 20201120
			//sqlbuf.append( " and (a.RECEIPTDATE <= '" +endDate +"' or a.RECEIPTDATE IS NULL) ");
            sqlbuf.append(" and a.bdate between ? and ? ");
		}*/
        //?问号参数赋值处理
        //dv=new DataValue(beginDate,Types.VARCHAR);
        //lstDV.add(dv);

        //非调拨的单据才需要判断到货日期 by jinzma 20220401
        if (!docType.equals("1")) {
            if (!Check.Null(dateType) && dateType.equals("receiptDate")) {
                //预计到货日查询
                sqlbuf.append(" and (a.receiptdate <= ? or a.receiptdate IS NULL) ");
                //?问号参数赋值处理
                dv=new DataValue(endDate,Types.VARCHAR);
                lstDV.add(dv);
            }
        }


        sqlbuf.append(""
                + " AND A.RECEIVINGNO NOT IN (SELECT NVL(OFNO,' ') FROM DCP_STOCKIN WHERE doc_type=? and SHOPID=? "
                + " and EID=? ) "
        );
        //?问号参数赋值处理
        dv=new DataValue(docType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(shopId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);

        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND A.status = ? ");

            //?问号参数赋值处理
            dv=new DataValue(status,Types.VARCHAR);
            lstDV.add(dv);
        } else {
            sqlbuf.append(" AND a.status <> '8' AND a.status <> '7' ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }

        sqlbuf.append(" union all ");
        sqlbuf.append(" SELECT  case when a.status=0 then 0 else 2 end  as rw,b.EID,b.ORGANIZATIONNO,a.bsNO,a.stockinno as dno , "
                + " a.process_ERP_NO as processERPNo, A.stockInNO,A.STOCKINNO_ORIGIN,A.STOCKINNO_REFUND,a.PTEMPLATENO,b.SHOPID,to_number(a.bDate) as bdate,A.memo,"
                + " A.status,b.DOC_TYPE as docType,A.oType "
                + " ,A.ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy,A.TRANSFER_SHOP as transferShop "
                + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,cast('' as nvarchar2(40)) as DELIVERY_NO,a.PACKINGNO "
                + " ,b.item,b.oItem,b.pluMemo,b.pluNO,b.receivingqty,b.pUnit,b.poQty,cast('' as nvarchar2(40)) as pluBarcode "
                + " ,b.baseunit,b.baseqty,b.unitRatio,b.price,b.amt,b.DISTRIAMT,b.diffQty,b.diffReqQty,b.pqty"
                + " ,b.ooType,b.oofNO,b.ooItem,b.ITEM_ORIGIN,b.PQTY_ORIGIN,b.PQTY_REFUND,b.baseunit as unit,a.warehouse as master_warehouse,b.warehouse as detail_warehouse,RETWQTY, "
                + " A.PROCESS_STATUS,A.UPDATE_TIME,B.OITEM REITEM ,0 as PROCRATE"
                + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime ,"
                + " a.ModifyBy , a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime,   "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime , "
                + " a.accountBY ,a.account_date AS accountDate, a.account_time AS accountTime ,"
                + " a.submitBy, a.submit_date AS submitDate , a.submit_time AS submitTime,"
                + " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,a.RECEIPTDATE,b.rdate,b.FEATURENO,b.detailpackingno,"
                + " g.rdate as receiving_rdate,a.deliveryby,dm.opname as deliveryname,dm.phone as deliverytel,b.TRANSFER_BATCHNO,a.corp,a.deliverycorp   "
                + " FROM DCP_STOCKIN A "
                + " left join dcp_receiving g on a.eid=g.eid and a.shopid=g.shopid and a.ofno=g.receivingno "
                + " INNER JOIN "
                //这里不能合并商品否则会缺商品
                + " (select doc_type,stockInNO,SHOPID,EID,organizationno,item,oItem,pluMemo,pluno,punit,baseunit,unitRatio,diffReqQty,diffQty, "
                + " pqty,amt,DISTRIAMT,price,poQty,baseqty,receivingQty,oType,ofNO,ooType,oofNO,ooItem,ITEM_ORIGIN,PQTY_ORIGIN,PQTY_REFUND,warehouse,RETWQTY,BATCH_NO,PROD_DATE,"
                + " DISTRIPRICE,rdate,FEATURENO,detailpackingno,TRANSFER_BATCHNO from  "
                + " ( "
                + " select b.doc_type,a.stockInNO,b.ptemplateno,a.SHOPID,a.EID,a.organizationno,a.item,a.oitem,a.plu_memo as pluMemo,a.pluNO,a.punit,"
                + " 0 as diffReqQty,0 as diffQty,a.pqty,a.amt,a.DISTRIAMT,a.price,a.poQty,a.baseunit,a.baseqty,a.unit_ratio as unitRatio,"
                + " receiving_qty as receivingqty,a.oType,a.ofNO,a.ooType,a.oofNO,a.ooItem,ITEM_ORIGIN,PQTY_ORIGIN,PQTY_REFUND ,a.warehouse,RETWQTY,a.BATCH_NO,a.PROD_DATE,a.DISTRIPRICE,"
                + " b.bdate as RECEIPTDATE,p.rdate,a.FEATURENO,a.packingno as detailpackingno,a.TRANSFER_BATCHNO  "
                + " from DCP_stockin_detail a left join DCP_porder P on  a.oofno=P.PORDERNO AND a.EID=P.EID AND a.SHOPID=P.SHOPID "
                + " inner join DCP_stockin b on a.EID=b.EID and a.stockinno=b.stockinno and a.organizationno=b.organizationno and a.BDATE=b.BDATE "
                + " where b.doc_type=? and b.SHOPID=? and b.EID=? "
        );

        //?问号参数赋值处理
        dv=new DataValue(docType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(shopId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);

        //2018-11-09 yyy 添加日期查询条件
        if (!Check.Null(dateType)&& dateType.equals("receiptDate")) {
            sqlbuf.append(" and b.receiptdate between ? and ? ");
        } else {
            sqlbuf.append(" and b.bdate between ? and ? ");
        }

        //?问号参数赋值处理
        dv=new DataValue(beginDate,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(endDate,Types.VARCHAR);
        lstDV.add(dv);


        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND b.status = ?  ");

            //?问号参数赋值处理
            dv=new DataValue(status,Types.VARCHAR);
            lstDV.add(dv);
        } else {
            sqlbuf.append(" AND b.status <> '8'  "); //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }

        ////以下查询浪费效能，差异调整单类型==4是调拨差异，所以作废  BY JZMA 20201118
		/*	sqlbuf.append(" union all "
				+ " select cast('0' as nvarchar2(1))  as doc_type,c.ofNO as stockInNO,cast('' as nvarchar2(40)) as PTEMPLATENO,b.SHOPID,b.EID,"
				+ " b.organizationno,0 as item,0 as oitem,"
				+ " cast('' as nvarchar2(10))  AS pluMemo,a.pluNO,a.punit,0 as diffReqQty,a.pqty as diffQty,0 as pqty,0 as amt,a.DISTRIAMT,"
				+ " a.price,0 as poQty,"
				+ " a.baseunit,0 as baseqty,a.unit_ratio as unitRatio,0 as receivingqty,"
				+ " cast('' as nvarchar2(1)) as otype,cast('' as nvarchar2(40)) as ofno,cast('' as nvarchar2(1)) as ooType,"
				+ " cast('' as nvarchar2(40)) as oofNO,cast(0 as Integer) as ooItem , a.warehouse,0 as RETWQTY, "
				+ " a.BATCH_NO,a.PROD_DATE,a.DISTRIPRICE,b.bdate as RECEIPTDATE,N'' as rdate,a.FEATURENO "
				+ " from DCP_adjust_detail a "
				+ " inner join DCP_adjust b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.adjustno=b.adjustno "
				+ " inner join DCP_difference c on b.EID=c.EID and b.SHOPID=c.SHOPID and b.ofno=c.differenceno  "
				+ " where a.SHOPID='" + shopId + "' and a.EID='" + eId + "' and c.doc_type='4'" );*/

        sqlbuf.append(" ) ) b on a.stockinno=b.stockinno and a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO ");
        sqlbuf.append(" left join dcp_deliveryman dm on a.eid=dm.eid and a.deliveryby=dm.opno");
        sqlbuf.append(" where a.doc_type=? and  a.SHOPID=? and a.EID=? ");


        //?问号参数赋值处理
        dv=new DataValue(docType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(shopId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);

        //2018-11-09 yyy 添加日期查询条件
        if (!Check.Null(dateType)&& dateType.equals("receiptDate")) {
            sqlbuf.append(" and a.receiptdate between ? and ? ");
        } else {
            sqlbuf.append(" and a.bdate between ? and ? ");
        }

        //?问号参数赋值处理
        dv=new DataValue(beginDate,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(endDate,Types.VARCHAR);
        lstDV.add(dv);

        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND A.status = ?  ");

            //?问号参数赋值处理
            dv=new DataValue(status,Types.VARCHAR);
            lstDV.add(dv);
        } else {
            sqlbuf.append(" AND a.status <> '8'  ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }

        sqlbuf.append(" ) a ");

        // 【ID1017769】【大连大万3.0】总部发货后，门店没有收到  by jinzma 20210517 inner-->left ,避免单身商品丢失
        sqlbuf.append(" LEFT JOIN DCP_GOODS c ON A.PLUNO = c.PLUNO AND A.EID = c.EID ");
        sqlbuf.append(" "
                + " LEFT JOIN DCP_GOODS_LANG D ON A.PLUNO = D .PLUNO AND A.EID = D .EID AND D .LANG_TYPE = ? "
                + " LEFT JOIN DCP_UNIT_LANG E ON A.PUNIT = E.UNIT AND A.EID = E.EID AND E.LANG_TYPE = ?  "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f ON A .EID = f.EID AND A .createby = f.opno AND F.LANG_TYPE = ? "
                //2018-11-20 新增以下几行， 用于查modifyByName 等字段
                + " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno AND F1.LANG_TYPE  = ? "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON A .EID = f2.EID AND A .cancelby = f2.opno AND F2.LANG_TYPE = ? "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f3 ON A .EID = f3.EID AND A .confirmby = f3.opno AND F3.LANG_TYPE = ? "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f4 ON A .EID = f4.EID AND A .submitby = f4.opno AND F4.LANG_TYPE = ? "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f5 ON A .EID = f5.EID AND A .accountby = f5.opno AND F5.LANG_TYPE = ? "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno "
                + " and fn.lang_type=? "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type=? "
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "

        );
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);


        if (keyTxt != null && keyTxt.length() > 0 && docType.equals("1")) {
            sqlbuf.append(" INNER JOIN ");
        }else {
            sqlbuf.append(" LEFT JOIN ");
        }
        sqlbuf.append(""
                + "("
                + "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
                + "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE=? "
                + "WHERE A.EID=?  AND A.status='100' "
                + ") H ON A.EID=H.EID AND A.TRANSFERSHOP=H.SHOPID AND H.LANG_TYPE = ?  "
                + " LEFT JOIN DCP_DIFFERENCE I ON A.EID=I.EID  AND A.STOCKINNO=I.OFNO AND A.SHOPID=I.SHOPID  "
                + " left join DCP_ptemplate k on a.EID=k.EID and a.ptemplateno=k.ptemplateno and k.doc_type='0' "
                + " left join DCP_REASON_LANG L ON A.EID = L.EID AND A.BSNO=L.BSNO and L.lang_type=? "
                + " AND  L.BSTYPE='5' "
        );

        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);

        //docType(0:配送收货deliveryReceive 1:调拨入库stockIn 3:其他入库otherIn)
        if(docType.equals("3")){
            sqlbuf.append(" and L.bstype = '5' ");
        }

        sqlbuf.append(" "
                + " LEFT JOIN dcp_unit U ON A.EID = U.EID  AND A.unit = U.unit"
                + " left join dcp_unit bul on a.eid=bul.eid and a.baseunit=bul.unit"
                + " left join dcp_unit_lang Ulang on A.EID = Ulang.EID AND  A.unit=Ulang.unit and Ulang.Lang_Type=?  "
                + " left join DCP_WAREHOUSE_lang  on a.EID=DCP_WAREHOUSE_lang.EID and a.organizationno=DCP_WAREHOUSE_lang.organizationno  and a.master_warehouse=DCP_WAREHOUSE_lang.warehouse "
                + " and DCP_WAREHOUSE_lang.lang_type=? "
                +" ) EE inner join (" +withasSql_dno+ " ) P1 ON EE.DNO=P1.DNO "
                + " ORDER BY rw ASC, status ASC, bdate DESC, stockinno DESC, DNO DESC, ITEM ASC ");


        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);

        sql = sqlbuf.toString();

        return sql;
    }

    protected String getQuerySql_Count(DCP_StockInQueryReq req) throws Exception {

        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        levelElm request = req.getRequest();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String docType = request.getDocType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String dateType = request.getDateType();  /// 日期类型  bDate：单据日期（默认）,receiptDate：预计到货日 BY JZMA 20201118


        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        if(docType.equals("4")) {
            sqlbuf.append(""
                    + "SELECT num,rw, dno, status, to_number(bdate) as bdate, stockinno "
                    + " from ("
                    + " SELECT count(*) over() as num,2 AS rw,rownum rn, stockinno AS dno, a.status, to_number(bdate) as bdate, stockinno "
                    + " FROM DCP_STOCKIN A "
                    + " where A.doc_type='" + docType + "' and A.SHOPID='" + shopId + "' and A.EID='" + eId + "'"
            );

            if(status != null) {
                sqlbuf.append("  AND A.status = '"+status+"' ");
            } else {
                sqlbuf.append(" AND A.status <> '8'  ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
            }

            if (!Check.Null(dateType)&& dateType.equals("receiptDate")) {
                sqlbuf.append(" and A.receiptdate between "+beginDate+" and "+endDate+" ");
            } else {
                sqlbuf.append(" and A.bdate between "+beginDate+" and "+endDate+" ");
            }


            if (keyTxt != null && keyTxt.length()!=0) {
                sqlbuf.append(""
                        + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                        + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                        + "OR  A.STOCKINNO like '%%"+ keyTxt +"%%'    "
                        + "OR  A.MEMO like '%%"+ keyTxt +"%%'  "
                        + "OR  A.PROCESS_ERP_NO like '%%"+ keyTxt +"%%' "
                        + "OR  A.TRANSFER_SHOP LIKE '%%"+keyTxt+"%%' "
                        + "OR  A.PACKINGNO LIKE '%%"+keyTxt+"%%' "
                        + " )");
            }

            sqlbuf.append(" ORDER BY status ASC, bdate DESC, stockinno DESC  ) TBL "
                    + " WHERE rn > " + startRow + " AND rn <= " + (startRow+pageSize)
                    + " ");
        } else {
            sqlbuf.append(""
                    + "SELECT num,rw, dno, status, to_number(bdate) as bdate, stockinno "
                    + " from ("
                    + " SELECT count(*) over() as num,rw,rownum rn, dno, status, to_number(bdate) as bdate, stockinno  from ("
                    + " SELECT rw,dno,status,to_number(bdate) as bdate,stockinno FROM  ( "
                    + " select 1 as rw,RECEIVINGNO as dno,DCP_receiving.status,to_number(bdate) as bdate,cast('' as nvarchar2(30)) as stockInNO "
                    + " FROM DCP_receiving ");
            if (keyTxt != null && keyTxt.length() > 0 && docType.equals("1")) {
                sqlbuf.append(" INNER  JOIN  DCP_ORG ORG ON DCP_receiving.EID= ORG.EID AND DCP_receiving.TRANSFER_SHOP=ORG.ORGANIZATIONNO and ORG.STATUS='100' "
                        + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON ORG.EID=ORG_LANG.EID AND ORG.ORGANIZATIONNO=ORG_LANG.ORGANIZATIONNO "
                        + " AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                );
            } else {
                sqlbuf.append(" LEFT  JOIN  DCP_ORG ORG ON DCP_receiving.EID= ORG.EID AND DCP_receiving.TRANSFER_SHOP=ORG.ORGANIZATIONNO AND ORG.STATUS='100' "
                        + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON ORG.EID=ORG_LANG.EID AND ORG.ORGANIZATIONNO=ORG_LANG.ORGANIZATIONNO "
                        + " AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                );
            }
            sqlbuf.append(""
                    + " where DCP_receiving.doc_type='" + docType + "' and DCP_receiving.SHOPID='" + shopId + "' and DCP_receiving.EID='" + eId + "' " );

            //【ID1018832】【货郎】门店管理-配送收货，待收货的单据不收查询时间范围约束，，没收货就始终显示始终显示
            //预计到货日如果为空就查出来，如果有值就判断截止日期 by jinzma 20210630
			/*if (!Check.Null(dateType) && dateType.equals("receiptDate"))
			{
				sqlbuf.append(" and (dcp_receiving.receiptdate between "+beginDate+" and "+endDate+" or dcp_receiving.receiptdate is null) ");
			}
			else {
				///红艳要求这样改的，原本通知单未收货的都会显示，现在拿掉这段注释  BY JZMA 20201120
				//sqlbuf.append( " and (DCP_receiving.RECEIPTDATE <= '" +endDate +"' or DCP_receiving.RECEIPTDATE IS NULL) ");
				sqlbuf.append(" and dcp_receiving.bdate between "+beginDate+" and "+endDate+" ");
			}*/


            //非调拨的单据才需要判断到货日期 by jinzma 20220401
            if (!docType.equals("1")) {
                if (!Check.Null(dateType) && dateType.equals("receiptDate")) {
                    //预计到货日查询
                    sqlbuf.append(" and (dcp_receiving.receiptdate <= '" + endDate + "' or dcp_receiving.receiptdate IS NULL) ");
                }
            }

            sqlbuf.append(" AND DCP_receiving.RECEIVINGNO NOT IN (SELECT NVL(OFNO,' ')  FROM DCP_STOCKIN WHERE doc_type='"+docType+"' and SHOPID='"+shopId+"' and EID='"+eId+"')  ");
            if (status != null && status.length()!=0){
                sqlbuf.append(" AND DCP_receiving.status = '"+ status +"'  ");
            } else {
                sqlbuf.append(" AND DCP_receiving.status <> '8' AND DCP_receiving.status <> '7' ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废 7-已完成)
            }

            if (keyTxt != null && keyTxt.length() > 0) {
                sqlbuf.append(" AND (DCP_receiving.TOT_AMT LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_receiving.TOT_PQTY LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_receiving.RECEIVINGNO LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_receiving.MEMO LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_receiving.LOAD_DOCNO LIKE '%%"+keyTxt+"%%'  "
                        + " OR DCP_receiving.TRANSFER_SHOP LIKE '%%"+keyTxt+"%%' "
                        + " OR ORG_LANG.ORG_NAME LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_receiving.PACKINGNO LIKE '%%"+keyTxt+"%%' "
                        + " )");
            }

            sqlbuf.append(" union all "
                    + "   select case when DCP_stockin.status=0 then 0 else 2 end as rw,stockinno as dno,DCP_stockin.status,to_number(bdate) as bdate,stockinno from DCP_stockin " 	);
            if (keyTxt != null && keyTxt.length() > 0 && docType.equals("1")) {
                sqlbuf.append(" INNER  JOIN  DCP_ORG ORG ON DCP_stockin.EID= ORG.EID AND DCP_stockin.TRANSFER_SHOP=ORG.ORGANIZATIONNO "
                        + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON ORG.EID=ORG_LANG.EID AND ORG.ORGANIZATIONNO=ORG_LANG.ORGANIZATIONNO "
                        + " AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                );
            }else {
                sqlbuf.append(" LEFT  JOIN  DCP_ORG ORG ON DCP_stockin.EID= ORG.EID AND DCP_stockin.TRANSFER_SHOP=ORG.ORGANIZATIONNO "
                        + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON ORG.EID=ORG_LANG.EID AND ORG.ORGANIZATIONNO=ORG_LANG.ORGANIZATIONNO "
                        + " AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                );
            }

            sqlbuf.append(""
                    + " where DCP_stockin.doc_type='" + docType + "' and DCP_stockin.SHOPID='" + shopId + "' and DCP_stockin.EID='" + eId + "'"
            );



            if (!Check.Null(dateType)&& dateType.equals("receiptDate")) {
                sqlbuf.append(" and dcp_stockin.receiptdate between "+beginDate+" and "+endDate+" ");
            } else {
                sqlbuf.append(" and dcp_stockin.bdate between "+beginDate+" and "+endDate+" ");
            }


            if (status != null && status.length()!=0){
                sqlbuf.append(" AND DCP_stockin.status = '"+ status +"'  ");
            } else {
                sqlbuf.append(" AND DCP_stockin.status <> '8' "); //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
            }

            if (keyTxt != null && keyTxt.length() > 0) {
                sqlbuf.append(" AND (DCP_stockin.TOT_AMT LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_stockin.TOT_PQTY LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_stockin.STOCKINNO LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_stockin.MEMO LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_stockin.LOAD_DOCNO LIKE '%%"+keyTxt+"%%'  "
                        + " OR DCP_stockin.PROCESS_ERP_NO like '%%"+ keyTxt +"%%' "
                        + " OR DCP_stockin.TRANSFER_SHOP LIKE '%%"+keyTxt+"%%' "
                        + " OR ORG_LANG.ORG_NAME LIKE '%%"+keyTxt+"%%' "
                        + " OR DCP_stockin.PACKINGNO LIKE '%%"+keyTxt+"%%' "
                        + " )");
            }
            sqlbuf.append(" ) "
                    + "ORDER BY rw ASC, status ASC, bdate DESC, stockinno DESC ))"
                    + " WHERE rn > " + startRow + " AND rn <= " + (startRow+pageSize)
                    + " ");
        }

        sql = sqlbuf.toString();
        return sql;

    }

    @Override
    protected String getQuerySql(DCP_StockInQueryReq req) throws Exception {
        return null;
    }




}