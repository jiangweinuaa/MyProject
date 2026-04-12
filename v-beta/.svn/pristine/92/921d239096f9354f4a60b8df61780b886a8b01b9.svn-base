package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_PinPeiDetailReq;
import com.dsc.spos.json.cust.res.DCP_PinPeiDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiDetail 服务说明：拼胚详情
 *
 * @author jinzma
 * @since 2020-07-13
 */
public class DCP_PinPeiDetail extends SPosBasicService<DCP_PinPeiDetailReq, DCP_PinPeiDetailRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_PinPeiDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String pinPeiNo = req.getRequest().getPinPeiNo();
        if (Check.Null(pinPeiNo)) {
            errMsg.append("单据编号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_PinPeiDetailReq> getRequestType() {
        return new TypeToken<DCP_PinPeiDetailReq>() {};
    }
    
    @Override
    protected DCP_PinPeiDetailRes getResponseType() {
        return new DCP_PinPeiDetailRes();
    }
    
    @Override
    protected DCP_PinPeiDetailRes processJson(DCP_PinPeiDetailReq req) throws Exception {
        DCP_PinPeiDetailRes res = this.getResponse();
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            DCP_PinPeiDetailRes.level1Elm datas = res.new level1Elm();
            datas.setStockInPluList(new ArrayList<>());
            datas.setStockOutPluList(new ArrayList<>());
            
            if (getQData != null && !getQData.isEmpty()) {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                //单头资料取第一笔
                String createOpId=getQData.get(0).get("CREATEOPID").toString();
                String createOpName=getQData.get(0).get("CREATEOPNAME").toString();
                String createTime=getQData.get(0).get("CREATE_TIME").toString();  ///数据库转字符串另取字段名
                String lastModiOpId=getQData.get(0).get("LASTMODIOPID").toString();
                String lastModiOpName=getQData.get(0).get("LASTMODIOPNAME").toString();
                String lastModiTime=getQData.get(0).get("LASTMODI_TIME").toString();  ///数据库转字符串另取字段名
                String memo=getQData.get(0).get("MEMO").toString();
                String pinPeiNo=getQData.get(0).get("PINPEINO").toString();
                String bDate=getQData.get(0).get("BDATE").toString();
                String stockInNo=getQData.get(0).get("STOCKINNO").toString();
                String stockOutNo=getQData.get(0).get("STOCKOUTNO").toString();
                String status=getQData.get(0).get("STATUS").toString();
                String totPqty=getQData.get(0).get("TOT_PQTY").toString();
                String totCqty=getQData.get(0).get("TOT_CQTY").toString();
                String totAmt=getQData.get(0).get("TOT_AMT").toString();
                String totDistriAmt=getQData.get(0).get("TOT_DISTRIAMT").toString();
                String stockInWearehouse=getQData.get(0).get("STOCKINWEAREHOUSE").toString();
                String stockInWearehouseName=getQData.get(0).get("STOCKINWEAREHOUSENAME").toString();
                String stockOutWearehouse=getQData.get(0).get("STOCKOUTWEAREHOUSE").toString();
                String stockOutWearehouseName=getQData.get(0).get("STOCKOUTWEAREHOUSENAME").toString();
                String stockInBsNo=getQData.get(0).get("STOCKINBSNO").toString();
                String stockInBsName=getQData.get(0).get("STOCKINBSNAME").toString();
                String stockOutBsNo=getQData.get(0).get("STOCKOUTBSNO").toString();
                String stockOutBsName=getQData.get(0).get("STOCKOUTBSNAME").toString();
                
                
                datas.setCreateOpId(createOpId);
                datas.setCreateOpName(createOpName);
                datas.setCreateTime(createTime);
                datas.setLastModiOpId(lastModiOpId);
                datas.setLastModiOpName(lastModiOpName);
                datas.setLastModiTime(lastModiTime);
                datas.setMemo(memo);
                datas.setPinPeiNo(pinPeiNo);
                datas.setbDate(bDate);
                datas.setStockInNo(stockInNo);
                datas.setStockOutNo(stockOutNo);
                datas.setStatus(status);
                datas.setTotPqty(totPqty);
                datas.setTotCqty(totCqty);
                datas.setTotAmt(totAmt);
                datas.setTotDistriAmt(totDistriAmt);
                datas.setStockInWearehouse(stockInWearehouse);
                datas.setStockInWearehouseName(stockInWearehouseName);
                datas.setStockOutWearehouse(stockOutWearehouse);
                datas.setStockOutWearehouseName(stockOutWearehouseName);
                datas.setStockInBsNo(stockInBsNo);
                datas.setStockInBsName(stockInBsName);
                datas.setStockOutBsNo(stockOutBsNo);
                datas.setStockOutBsName(stockOutBsName);
                
                //入库商品列表
                Map<String, Object> conditionValues = new HashMap<>(); //查詢條件
                conditionValues.put("STOCKTYPE", "STOCKIN");
                //调用过滤函数
                List<Map<String, Object>> getQStockIn=MapDistinct.getWhereMap(getQData, conditionValues, true);
                datas.setStockInPluList(new ArrayList<>());
                for (Map<String, Object> oneData : getQStockIn) {
                    DCP_PinPeiDetailRes.level2Elm oneLv2 = res.new level2Elm();
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String featureNo = oneData.get("FEATURENO").toString();
                    String featureName = oneData.get("FEATURENAME").toString();
                    String listImage =oneData.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName+listImage;
                    }
                    String spec = oneData.get("SPEC").toString();
                    String punit = oneData.get("PUNIT").toString();
                    String punitName = oneData.get("PUNITNAME").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String unitRatio = oneData.get("UNIT_RATIO").toString();
                    String pqty = oneData.get("PQTY").toString();
                    String baseQty = oneData.get("BASEQTY").toString();
                    String batchNo = oneData.get("BATCH_NO").toString();
                    String prodDate = oneData.get("PROD_DATE").toString();
                    String price = oneData.get("PRICE").toString();
                    String distriPrice = oneData.get("DISTRIPRICE").toString();
                    String amt = oneData.get("AMT").toString();
                    String distriAmt = oneData.get("DISTRIAMT").toString();
                    String punitUdLength = oneData.get("UDLENGTH").toString();
                    String pluMemo = oneData.get("PLU_MEMO").toString();
                    
                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setFeatureNo(featureNo);
                    oneLv2.setFeatureName(featureName);
                    oneLv2.setListImage(listImage);
                    oneLv2.setSpec(spec);
                    oneLv2.setPunit(punit);
                    oneLv2.setPunitName(punitName);
                    oneLv2.setBaseUnit(baseUnit);
                    oneLv2.setBaseUnitName(baseUnitName);
                    oneLv2.setUnitRatio(unitRatio);
                    oneLv2.setPqty(pqty);
                    oneLv2.setBaseQty(baseQty);
                    oneLv2.setBatchNo(batchNo);
                    oneLv2.setProdDate(prodDate);
                    oneLv2.setPrice(price);
                    oneLv2.setDistriPrice(distriPrice);
                    oneLv2.setAmt(amt);
                    oneLv2.setDistriAmt(distriAmt);
                    oneLv2.setPunitUdLength(punitUdLength);
                    oneLv2.setPluMemo(pluMemo);
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                    oneLv2.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    
                    datas.getStockInPluList().add(oneLv2);
                }
                
                
                //出库商品列表
                conditionValues.clear();
                conditionValues.put("STOCKTYPE", "STOCKOUT");
                //调用过滤函数
                List<Map<String, Object>> getQStockOut=MapDistinct.getWhereMap(getQData, conditionValues, true);
                datas.setStockOutPluList(new ArrayList<>());
                for (Map<String, Object> oneData : getQStockOut) {
                    DCP_PinPeiDetailRes.level2Elm oneLv2 = res.new level2Elm();
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String featureNo = oneData.get("FEATURENO").toString();
                    String featureName = oneData.get("FEATURENAME").toString();
                    String listImage =oneData.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName+listImage;
                    }
                    String spec = oneData.get("SPEC").toString();
                    String punit = oneData.get("PUNIT").toString();
                    String punitName = oneData.get("PUNITNAME").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String unitRatio = oneData.get("UNIT_RATIO").toString();
                    String pqty = oneData.get("PQTY").toString();
                    String baseQty = oneData.get("BASEQTY").toString();
                    String batchNo = oneData.get("BATCH_NO").toString();
                    String prodDate = oneData.get("PROD_DATE").toString();
                    String price = oneData.get("PRICE").toString();
                    String distriPrice = oneData.get("DISTRIPRICE").toString();
                    String amt = oneData.get("AMT").toString();
                    String distriAmt = oneData.get("DISTRIAMT").toString();
                    String punitUdLength = oneData.get("UDLENGTH").toString();
                    String pluMemo = oneData.get("PLU_MEMO").toString();
                    
                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setFeatureNo(featureNo);
                    oneLv2.setFeatureName(featureName);
                    oneLv2.setListImage(listImage);
                    oneLv2.setSpec(spec);
                    oneLv2.setPunit(punit);
                    oneLv2.setPunitName(punitName);
                    oneLv2.setBaseUnit(baseUnit);
                    oneLv2.setBaseUnitName(baseUnitName);
                    oneLv2.setUnitRatio(unitRatio);
                    oneLv2.setPqty(pqty);
                    oneLv2.setBaseQty(baseQty);
                    oneLv2.setBatchNo(batchNo);
                    oneLv2.setProdDate(prodDate);
                    oneLv2.setPrice(price);
                    oneLv2.setDistriPrice(distriPrice);
                    oneLv2.setAmt(amt);
                    oneLv2.setDistriAmt(distriAmt);
                    oneLv2.setPunitUdLength(punitUdLength);
                    oneLv2.setPluMemo(pluMemo);
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                    oneLv2.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    
                    datas.getStockOutPluList().add(oneLv2);
                }
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"未查询到明细资料，请重新查询！");
            }
            
            res.setDatas(datas);
            
            return res;
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_PinPeiDetailReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String pinPeiNo = req.getRequest().getPinPeiNo();
        String langType = req.getLangType();
        sqlbuf.append(" "
                + " select a.*,"
                + " b.item,b.pluno,b.featureno,b.punit,b.baseunit,b.unit_ratio,b.pqty,b.baseqty,b.batch_no,b.prod_date,"
                + " b.price,b.distriprice,b.amt,b.distriamt,b.plu_memo,b.stocktype,"
                + " fn.featurename,gul.spec,image.listimage,c.plu_name,u.udlength,"
                + " u1.uname as punitname,u2.uname as baseunitname,"
                + " w1.warehouse_name as stockinwearehousename,w2.warehouse_name as stockoutwearehousename,"
                + " r1.reason_name as stockinbsname,r2.reason_name as stockoutbsname,"
                + " to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as create_time,"
                + " to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastmodi_time,"
                + " bul.udlength as baseunitudlength"
                + " from dcp_pinpei a"
                + " inner join dcp_pinpei_detail b on a.eid=b.eid and a.shopid=b.shopid and a.pinpeino=b.pinpeino"
                + " left join dcp_goods_lang c on b.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+langType+"'"
                + " inner join dcp_goods d on b.eid=d.eid and b.pluno=d.pluno and d.status='100'"
                + " left join dcp_unit u on u.eid=b.eid and u.unit=b.punit"
                //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                + " left join dcp_unit bul on b.eid=bul.eid and b.baseunit=bul.unit"
                + " left join dcp_unit_lang u1 on u1.eid=b.eid and u1.unit=b.punit and u1.lang_type='"+langType+"'"
                + " left join dcp_unit_lang u2 on u2.eid=b.eid and u2.unit=b.baseunit and u2.lang_type='"+langType+"'"
                + " left join dcp_warehouse_lang w1 on w1.eid=a.eid and w1.organizationno='"+shopId+"' and w1.warehouse=a.stockinwearehouse and w1.lang_type='"+langType+"'"
                + " left join dcp_warehouse_lang w2 on w2.eid=a.eid and w2.organizationno='"+shopId+"' and w2.warehouse=a.stockoutwearehouse and w2.lang_type='"+langType+"'"
                + " left join dcp_reason_lang r1 on r1.eid=a.eid and r1.bstype='14' and r1.bsno=a.stockinbsno and r1.lang_type='"+langType+"'"
                + " left join dcp_reason_lang r2 on r2.eid=a.eid and r2.bstype='15' and r2.bsno=a.stockoutbsno and r2.lang_type='"+langType+"'"
                + " left join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+langType+"'"
                + " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.pinpeino='"+pinPeiNo+"'"
                + " order by b.item"
                + " ");
        
        return sqlbuf.toString();
    }
    
}
