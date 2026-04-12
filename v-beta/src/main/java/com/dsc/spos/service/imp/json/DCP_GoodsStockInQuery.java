package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GoodsStockInQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStockInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * @apiNote 商品配送收货查询
 * @since 2021-04-20
 * @author jinzma
 */
public class DCP_GoodsStockInQuery extends SPosBasicService<DCP_GoodsStockInQueryReq, DCP_GoodsStockInQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_GoodsStockInQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_GoodsStockInQueryReq.levelElm request = req.getRequest();
        if (Check.Null(request.getDateType())) {
            errMsg.append("日期类型不可为空值 ");
            isFail = true;
        }
        if (Check.Null(request.getBeginDate())) {
            errMsg.append("开始日期不可为空值 ");
            isFail = true;
        }
        if (Check.Null(request.getEndDate())) {
            errMsg.append("结束日期不可为空值 ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GoodsStockInQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsStockInQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsStockInQueryRes getResponseType() {
        return new DCP_GoodsStockInQueryRes();
    }
    
    @Override
    protected DCP_GoodsStockInQueryRes processJson(DCP_GoodsStockInQueryReq req) throws Exception {
        DCP_GoodsStockInQueryRes res = this.getResponse();
        DCP_GoodsStockInQueryRes.levelElm datas = res.new levelElm();
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            datas.setPluList(new ArrayList<DCP_GoodsStockInQueryRes.level1Elm>());
            if (getQData != null && !getQData.isEmpty()) {
                // 拼接返回图片路径
                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";
                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData) {
                    DCP_GoodsStockInQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    
                    String listImage = oneData.get("LISTIMAGE").toString();
                    if(!Check.Null(listImage)){
                        if (DomainName.endsWith("/")) {
                            listImage = httpStr+DomainName+"resource/image/" +listImage;
                        }
                        else{
                            listImage = httpStr+DomainName+"/resource/image/" +listImage;
                        }
                    }
                    String punitUdLength = oneData.get("PUNITUDLENGTH").toString();
                    if (Check.Null(punitUdLength)){
                        punitUdLength="0";
                    }
                    
                    //已完成：(收货通知单状态为7) 或 (收货通知单状态为6且商品已转入到入库单)的所有数据 by jinzma 20210508
                    String receivingStatus = oneData.get("RECEIVINGSTATUS").toString();
                    String status = oneData.get("STATUS").toString();
                    String stockinNo = oneData.get("STOCKINNO").toString();
                    if (Check.Null(status)){
                        if (!receivingStatus.equals("6") || !Check.Null(stockinNo)){
                            status="100";
                        }else{
                            status="0";
                        }
                    }
                    
                    oneLv1.setItem(oneData.get("ITEM").toString());
                    oneLv1.setbDate(oneData.get("BDATE").toString());
                    oneLv1.setReceivingNo(oneData.get("RECEIVINGNO").toString());
                    oneLv1.setLoad_docNo(oneData.get("LOAD_DOCNO").toString());
                    oneLv1.setPackingNo(oneData.get("PACKINGNO").toString());
                    oneLv1.setStockInNo(oneData.get("STOCKINNO").toString());
                    oneLv1.setStockInStatus(oneData.get("STOCKINSTATUS").toString());
                    oneLv1.setPluNo(oneData.get("PLUNO").toString());
                    oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                    oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                    oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
                    oneLv1.setSpec(oneData.get("SPEC").toString());
                    oneLv1.setListImage(listImage);
                    oneLv1.setPunit(oneData.get("PUNIT").toString());
                    oneLv1.setPunitName(oneData.get("PUNITNAME").toString());
                    oneLv1.setBaseUnit(oneData.get("BASEUNIT").toString());
                    oneLv1.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
                    oneLv1.setPunitUdLength(punitUdLength);
                    oneLv1.setUnitRatio(oneData.get("UNIT_RATIO").toString());
                    oneLv1.setPrice(oneData.get("PRICE").toString());
                    oneLv1.setDistriPrice(oneData.get("DISTRIPRICE").toString());
                    oneLv1.setPqty(oneData.get("PQTY").toString());
                    oneLv1.setPoQty(oneData.get("POQTY").toString());
                    oneLv1.setStockInQty(oneData.get("STOCKIN_QTY").toString());
                    oneLv1.setBaseQty(oneData.get("BASEQTY").toString());
                    oneLv1.setStatus(status);
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221109
                    oneLv1.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    datas.getPluList().add(oneLv1);
                }
                
            }else{
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setDatas(datas);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsStockInQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopid = req.getShopId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String dateType = req.getRequest().getDateType();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        sqlbuf.append(" select * from (");
        if (dateType.equals("bDate")){
            sqlbuf.append(" select count(*) over() num,row_number() over (order by a.bdate desc,a.receivingno desc, b.item) rn,");
        }else{
            sqlbuf.append(" select count(*) over() num,row_number() over (order by a.receiptdate desc,a.receivingno desc, b.item) rn,");
        }
        sqlbuf.append(""
                + " a.eid,a.shopid,a.bdate,a.receivingno,a.load_docno,a.status as receivingstatus,"
                + " b.item,b.packingno,b.pluno,b.featureno,b.punit,b.baseunit,b.unit_ratio,"
                + " b.price,b.distriprice,b.pqty,b.poqty,b.stockin_qty,b.baseqty,b.status,"
                + " c.stockinno,c.status as stockinstatus,d.plu_name,fn.featurename,gul.spec,image.listimage,"
                + " u1.uname as punitname,u2.uname as baseUnitName,e.udlength as punitUdLength,"
                + " bul.udlength as baseunitudlength"
                + " from dcp_receiving a"
                + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
                + " left  join dcp_stockin c on a.eid=c.eid and a.shopid=c.shopid and c.ofno=a.receivingno and c.doc_type='0'"
                + " left  join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and d.lang_type='"+langType+"'"
                + " left  join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='"+langType+"'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left  join dcp_goodsimage image on a.eid=image.eid and b.pluno=image.pluno and image.apptype='ALL'"
                + " left  join dcp_unit_lang u1 on a.eid=u1.eid and b.punit=u1.unit and u1.lang_type='"+langType+"'"
                + " left  join dcp_unit_lang u2 on a.eid=u2.eid and b.baseunit=u2.unit and u2.lang_type='"+langType+"'"
                + " left  join dcp_unit e on a.eid=e.eid and b.punit=e.unit"
                + " left  join dcp_unit bul on a.eid=bul.eid and b.baseunit=bul.unit"
        );
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" left join ("
                    + " select pluno from dcp_goods_barcode where eid='"+eId+"' and status='100' and plubarcode='"+keyTxt+"'"
                    + " group by pluno) barcode on barcode.pluno=b.pluno");
        }
        sqlbuf.append(" "
                + " where a.eid='"+eId+"' and a.shopid='"+shopid+"' and a.doc_type='0' "
                + " ");
        if (dateType.equals("bDate")){
            sqlbuf.append("and a.bdate>='"+beginDate+"' and a.bdate<='"+endDate+"'");
        }else{
            sqlbuf.append("and a.receiptdate>='"+beginDate+"' and a.receiptdate<='"+endDate+"'");
        }

       /* 查询逻辑：
        全部：收货通知单状态为6或7的所有数据
        待收货：收货通知单状态为6 且 商品的收货状态为0或空的所有数据
        已完成：收货通知单状态为6或7  且 商品的收货状态为100的所有数据
        收货通知单状态为7  且 商品的状态为空的所有数据*/
        if (Check.Null(status)){
            sqlbuf.append(" and (a.status='6' or a.status='7')");
        }else if (status.equals("0")){
            sqlbuf.append(" and a.status='6' and (b.status='0' or b.status is null) ");
        }else {
            sqlbuf.append(" and (a.status='7' or (a.status='6' and b.status='100'))");
        }
        
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and (b.packingno like '%"+keyTxt+"%' or a.load_docno like '%"+keyTxt+"%' "
                    + " or barcode.pluno is not null"
                    + " or c.stockinno like '%"+keyTxt+"%' or b.pluno like '%"+keyTxt+"%' or d.plu_name like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" )");
        sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
        
        return sqlbuf.toString();
    }
    
    
}
