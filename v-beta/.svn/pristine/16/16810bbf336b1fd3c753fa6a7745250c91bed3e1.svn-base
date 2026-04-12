package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SubStockTakeDetailReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeDetailRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeDetailRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeDetailRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_SubStockTakeDetail
 * 服务说明：盘点子任务详情
 * @author jinzma
 * @since  2021-02-26
 */
public class DCP_SubStockTakeDetail extends SPosBasicService<DCP_SubStockTakeDetailReq, DCP_SubStockTakeDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SubStockTakeDetailReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeDetailReq>(){};
    }
    
    @Override
    protected DCP_SubStockTakeDetailRes getResponseType() {
        return new DCP_SubStockTakeDetailRes();
    }
    
    @Override
    protected DCP_SubStockTakeDetailRes processJson(DCP_SubStockTakeDetailReq req) throws Exception {
        DCP_SubStockTakeDetailRes res = this.getResponse();
        level1Elm datas = res.new level1Elm();
        try {
            // 拼接返回图片路径
            String isHttps = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
            String httpStr=isHttps.equals("1")?"https://":"http://";
            String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
            
            int totalRecords=0;							     	//总笔数
            int totalPages=0;									//总页数
            
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //此处做兼容，因为前端之前没有传入分页 by jinzma 20220726
                if (req.getPageSize() > 0) {
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }
                
                String stockTakeNo = getQData.get(0).get("STOCKTAKENO").toString();
                String stockType = getQData.get(0).get("STOCKTYPE").toString();
                
                datas.setShopId(getQData.get(0).get("SHOPID").toString());
                datas.setSubStockTakeNo(getQData.get(0).get("SUBSTOCKTAKENO").toString());
                datas.setbDate(getQData.get(0).get("BDATE").toString());
                datas.setStockTakeNo(stockTakeNo);
                datas.setWarehouse(getQData.get(0).get("WAREHOUSE").toString());
                datas.setWarehouseName(getQData.get(0).get("WAREHOUSE_NAME").toString());
                datas.setDocType(getQData.get(0).get("DOC_TYPE").toString());
                datas.setIsBTake(getQData.get(0).get("IS_BTAKE").toString());
                datas.setTaskWay(getQData.get(0).get("TASKWAY").toString());
                datas.setDeviceNo(getQData.get(0).get("DEVICENO").toString());
                datas.setMemo(getQData.get(0).get("MEMO").toString());
                datas.setStockType(stockType);
                datas.setStatus(getQData.get(0).get("STATUS").toString());
                datas.setImportStatus(getQData.get(0).get("IMPORTSTATUS").toString());
                datas.setCreateOpId(getQData.get(0).get("CREATEOPID").toString());
                datas.setCreateOpName(getQData.get(0).get("CREATEOPNAME").toString());
                datas.setCreateTime(getQData.get(0).get("CREATETIME").toString());
                datas.setLastModiOpId(getQData.get(0).get("LASTMODIOPID").toString());
                datas.setLastModiOpName(getQData.get(0).get("LASTMODIOPNAME").toString());
                datas.setLastModiTime(getQData.get(0).get("LASTMODITIME").toString());
                
                //盘点单位初盘数量（盘点类型为复盘时返回，同一来源盘点单的总初盘数）
                List<Map<String, Object>> getFirstPQtyQData = new ArrayList<>();
                if (!Check.Null(stockType) && stockType.equals("2")){
                    sql = " "
                            + " select b.pluno,b.featureno,sum(b.baseqty) as baseqty"
                            + " from dcp_substocktake a"
                            + " inner join dcp_substocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.substocktakeno=b.substocktakeno"
                            + " where a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and a.stocktakeno='"+stockTakeNo+"' and a.stocktype='1'"
                            + " group by b.pluno,b.featureno";
                    getFirstPQtyQData =this.doQueryData(sql, null);
                }
                
                datas.setPluList(new ArrayList<>());
                if (!Check.Null(getQData.get(0).get("ITEM").toString())) {
                    for (Map<String, Object> oneData : getQData) {
                        level2Elm pluList = res.new level2Elm();
                        String pluNo = oneData.get("PLUNO").toString();
                        String featureNo = oneData.get("FEATURENO").toString();
                        String unitRatio = oneData.get("UNIT_RATIO").toString();
                        String udlength = oneData.get("UDLENGTH").toString();
                        if (!PosPub.isNumeric(udlength)) {
                            udlength = "0";
                        }
                        
                        // 增加商品图片 BY jinzma 20210311
                        String listImage = oneData.get("LISTIMAGE").toString();
                        if (!Check.Null(listImage)) {
                            if (domainName.endsWith("/")) {
                                listImage = httpStr + domainName + "resource/image/" + listImage;
                            } else {
                                listImage = httpStr + domainName + "/resource/image/" + listImage;
                            }
                        } else {
                            listImage = "";
                        }
                        
                        //【ID1016322】【货郎3.0】多PDA盘点-优化项 BY jinzma 20210324
                        String firstPQty = "0";
                        if (!Check.Null(stockType) && stockType.equals("2")) {
                            for (Map<String, Object> firstPQtyItem : getFirstPQtyQData) {
                                String getPluNo = firstPQtyItem.get("PLUNO").toString();
                                String getFeatureNo = firstPQtyItem.get("FEATURENO").toString();
                                String getBaseQty = firstPQtyItem.get("BASEQTY").toString();
                                
                                if (pluNo.equals(getPluNo) && featureNo.equals(getFeatureNo)) {
                                    //【ID1016322】【货郎3.0】多PDA盘点-优化项 BY jinzma 20210324
                                    BigDecimal getBaseQty_b = new BigDecimal(getBaseQty);
                                    BigDecimal unitRatio_b = new BigDecimal(unitRatio);
                                    BigDecimal firstPQty_b = getBaseQty_b.divide(unitRatio_b,Integer.parseInt(udlength), RoundingMode.HALF_UP);
                                    firstPQty = firstPQty_b.toPlainString();
                                    break;
                                }
                            }
                        }
                        
                        pluList.setItem(oneData.get("ITEM").toString());
                        pluList.setPluNo(pluNo);
                        pluList.setPluName(oneData.get("PLU_NAME").toString());
                        pluList.setFeatureNo(featureNo);
                        pluList.setFeatureName(oneData.get("FEATURENAME").toString());
                        pluList.setSpec(oneData.get("SPEC").toString());
                        pluList.setPunit(oneData.get("PUNIT").toString());
                        pluList.setPunitName(oneData.get("PUNITNAME").toString());
                        pluList.setBaseUnit(oneData.get("BASEUNIT").toString());
                        pluList.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
                        pluList.setUnitRatio(unitRatio);
                        pluList.setPrice(oneData.get("PRICE").toString());
                        pluList.setPqty(oneData.get("PQTY").toString());
                        pluList.setBaseQty(oneData.get("BASEQTY").toString());
                        pluList.setFirstPQty(firstPQty);
                        pluList.setRefBaseQty(oneData.get("REF_BASEQTY").toString());
                        pluList.setListImage(listImage);
                        pluList.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                        
                        datas.getPluList().add(pluList);
                    }
                }
            }
            
            res.setDatas(datas);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_SubStockTakeDetailReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopid = req.getShopId();
        String langType = req.getLangType();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String keyTxt = req.getRequest().getKeyTxt();
        
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;
        
        //此处做兼容，因为前端之前没有传入分页 by jinzma 20220726
        if (pageSize==0){
            pageSize = 10000;
        }
        
        sqlbuf.append(""
                + " select * from ("
                //钟鸣哥，这个服务返回的商品明细给按修改时间倒序排序返回--红艳提  by jinzma 20210329
                + " select count(*) over() num,row_number() over (order by b.lastmoditime desc) rn,"
                + " a.substocktakeno,a.shopid,a.bdate,a.stocktakeno,a.deviceno,a.memo,a.stocktype,a.status,a.importstatus,"
                + " a.createopid,a.createopname,to_char(a.createtime,'yyyy-MM-dd hh24:mi:ss') as createtime,"
                + " a.lastmodiopid,a.lastmodiopname,to_char(a.lastmoditime,'yyyy-MM-dd hh24:mi:ss') as lastmoditime,"
                + " b.item,b.pluno,b.featureno,b.punit,b.baseunit,b.unit_ratio,b.price,b.pqty,b.baseqty,b.ref_baseqty,"
                + " c.doc_type,c.is_btake,c.taskway,c.warehouse,"
                + " wl.warehouse_name,gl.plu_name,gfl.featurename,gul.spec,pu1.uname as punitName,bul.uname as baseUnitName,"
                + " image.listImage,dcp_unit.udlength,buludlength.udlength as baseunitudlength"
                + " from dcp_substocktake a");
        
        //【ID1026072】【潮品3.0】移动门店盘点录入扫商品后提示来源盘点单仓库不能为空 by jinzma 20220524 商品为空时，返回单头资料
        if (Check.Null(keyTxt)){
            sqlbuf.append( " left join dcp_substocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.substocktakeno=b.substocktakeno");
        }else{
            sqlbuf.append( " "
                    + " left join ("
                    + " select a.* from dcp_substocktake_detail a"
                    + " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"'"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopid+"' and a.substocktakeno='"+subStockTakeNo+"'"
                    + " and (a.pluno like '%"+keyTxt+"%' or b.plu_name like '%"+keyTxt+"%')"
                    + " )b on a.eid=b.eid and a.shopid=b.shopid and a.substocktakeno=b.substocktakeno");
        }
        
        sqlbuf.append(""
                + " inner join dcp_stocktake c on a.eid=c.eid and a.shopid=c.shopid and a.stocktakeno=c.stocktakeno"
                + " left join dcp_warehouse_lang wl on a.eid=wl.eid and c.warehouse=wl.warehouse and wl.lang_type='"+langType+"'"
                + " left join dcp_goods_lang gl on a.eid=gl.eid and b.pluno=gl.pluno and gl.lang_type='"+langType+"'"
                + " left join dcp_goods_feature_lang gfl on a.eid=gfl.eid and b.pluno=gfl.pluno and b.featureno=gfl.featureno and gfl.lang_type='"+langType+"'"
                + " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_unit_lang pu1 on a.eid=pu1.eid and b.punit=pu1.unit and pu1.lang_type='"+langType+"'"
                + " left join dcp_unit_lang bul on a.eid=bul.eid and b.baseunit=bul.unit and bul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on a.eid=image.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " left join dcp_unit on a.eid=dcp_unit.eid and b.punit=dcp_unit.unit"
                + " left join dcp_unit buludlength on a.eid=buludlength.eid and b.baseunit=buludlength.unit"
                + " where a.eid='"+eId+"' and a.shopid='"+shopid+"' and a.substocktakeno='"+subStockTakeNo+"'"
                + " ");
        
        //【ID1026072】【潮品3.0】移动门店盘点录入扫商品后提示来源盘点单仓库不能为空 by jinzma 20220524 商品为空时，返回单头资料
        /*if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (b.pluno like '%"+keyTxt+"%' or gl.plu_name like '%"+keyTxt+"%') ");
        }*/
        
        //钟鸣哥，这个服务返回的商品明细给按修改时间倒序排序返回--红艳提  by jinzma 20210329
        //sqlbuf.append(" order by b.lastmoditime desc");
        
        //【ID1027530】盘点录入优化--服务端 by jinzma 20220725
        sqlbuf.append(") where rn>"+startRow+" and rn<="+(startRow+pageSize));
        
        return sqlbuf.toString();
    }
}
