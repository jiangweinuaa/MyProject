package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutEntryCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutEntryCreateReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryCreateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryCreateRes.*;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_StockOutEntryCreateReq
 * 服务说明：退货录入创建
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryCreate extends SPosAdvanceService<DCP_StockOutEntryCreateReq, DCP_StockOutEntryCreateRes> {
    @Override
    protected void processDUID(DCP_StockOutEntryCreateReq req,DCP_StockOutEntryCreateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String createBy = req.getOpNO();
            String createByName = req.getOpName();
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            
            levelElm request = req.getRequest();
            String sql = " select stockoutentryid from dcp_stockout_entry where eid='"+eId+"' and stockoutentryid='"+request.getStockOutEntryId()+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退货录入单ID已存在 ");
            }
            //获取单号
            String stockOutEntryNo = getDocNo(req,eId,shopId,sDate);
            level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setPluList(new ArrayList<>());
            oneLv1.setStockOutEntryNo(stockOutEntryNo);
            
            String[] columns = {
                    "EID","SHOPID","STOCKOUTENTRYID","STOCKOUTENTRYNO","BDATE","DELIVERY_NO","BSNO","WAREHOUSE","STATUS",
                    "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT",
                    "CREATEBY","CREATEBYNAME","CREATETIME","MEMO",
            };
            
            // 新增DCP_STOCKOUT_ENTRY_DETAIL
            List<DCP_StockOutEntryCreateReq.level1Elm> datas = request.getDatas();
            if (!CollectionUtils.isEmpty(datas)) {
                String[] detailColumns = {
                        "EID","SHOPID","STOCKOUTENTRYNO","ITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                        "PRICE","DISTRIPRICE","AMT","DISTRIAMT","BASEQTY","BASEUNIT","PQTY","PUNIT","UNIT_RATIO",
                        "RECEIPTORG","BSNO","MEMO","STOCKQTY",
                };
                
                //收货组织查询
                {
                    MyCommon mc = new MyCommon();
                    Map<String, String> map = new HashMap<>();
                    String sJoinPlu = "";
                    for (DCP_StockOutEntryCreateReq.level1Elm par : datas) {
                        //【ID1037013】【罗森3.0】退货录入找不到商品   by jinzma 20231101
                        //sJoinPlu += par.getPluNo().toLowerCase() + ",";
                        sJoinPlu += par.getPluNo() + ",";
                    }
                    map.put("PLUNO", sJoinPlu);
                    String withPlu = mc.getFormatSourceMultiColWith(map);
                    
                    //模板发货机构查询
                    sql = " select b.pluno,max(a.receipt_org) keep(dense_rank last order by a.ptemplateno) as receipt_org"
                            + " from dcp_ptemplate a"
                            + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                            + " left  join dcp_ptemplate_shop c on a.eid=c.eid and a.ptemplateno=c.ptemplateno and a.doc_type=c.doc_type and c.shopid='" + shopId + "'"
                            + " inner join (" + withPlu + ")plu on b.pluno=plu.pluno"
                            + " where a.eid='" + eId + "' and a.doc_type='0' and a.receipt_org is not null and a.status='100' "
                            + " and (a.shoptype='1' or (a.shoptype='2' and c.shopid='" + shopId + "'))"
                            + " group by b.pluno"
                            + " ";
                    List<Map<String, Object>> getReceiptOrgA = this.doQueryData(sql, null);
                    
                    //收货通知单发货机构查询
                    sql = " select b.pluno,max(a.transfer_shop) keep(dense_rank last order by a.receivingno) as receipt_org "
                            + " from dcp_receiving a"
                            + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
                            + " inner join (" + withPlu + ")plu on b.pluno=plu.pluno"
                            + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.transfer_shop is not null and a.doc_type='0' "
                            + " group by b.pluno";
                    List<Map<String, Object>> getReceiptOrgB = this.doQueryData(sql, null);
                    List<Map<String, Object>> getReceiptOrg;
                    for (DCP_StockOutEntryCreateReq.level1Elm par : datas) {
                        //取要货模板
                        getReceiptOrg = getReceiptOrgA.stream().filter(p -> p.get("PLUNO").equals(par.getPluNo())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(getReceiptOrg)) {
                            boolean isExist = false;
                            for (Map<String, Object> oneReceiptOrg:getReceiptOrg){
                                if (!Check.Null(oneReceiptOrg.get("RECEIPT_ORG").toString())){
                                    par.setReceiptOrg(oneReceiptOrg.get("RECEIPT_ORG").toString());
                                    isExist = true;
                                    break;
                                }
                            }
                            if (isExist){
                                continue;
                            }
                        }
                        //取配送收货单
                        getReceiptOrg = getReceiptOrgB.stream().filter(p -> p.get("PLUNO").equals(par.getPluNo())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(getReceiptOrg)) {
                            boolean isExist = false;
                            for (Map<String, Object> oneReceiptOrg:getReceiptOrg){
                                if (!Check.Null(oneReceiptOrg.get("RECEIPT_ORG").toString())){
                                    par.setReceiptOrg(oneReceiptOrg.get("RECEIPT_ORG").toString());
                                    isExist = true;
                                    break;
                                }
                            }
                            if (isExist){
                                continue;
                            }
                        }
                        
                        //找不到返回给前端
                        level2Elm oneLv2 = res.new level2Elm();
                        oneLv2.setPluNo(par.getPluNo());
                        oneLv2.setPluName(par.getPluName());
                        oneLv1.getPluList().add(oneLv2);
                    }
                    
                    if (!CollectionUtils.isEmpty(oneLv1.getPluList())){
                        oneLv1.setStockOutEntryNo("");
                        res.setDatas(oneLv1);
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                        return ;
                    }
                }
                
                for (DCP_StockOutEntryCreateReq.level1Elm par : datas) {
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(stockOutEntryNo, Types.VARCHAR),
                            new DataValue(par.getItem(), Types.VARCHAR),
                            new DataValue(par.getPluNo(), Types.VARCHAR),
                            new DataValue(par.getFeatureNo(), Types.VARCHAR),
                            new DataValue(par.getBatchNo(), Types.VARCHAR),
                            new DataValue(par.getProdDate(), Types.VARCHAR),
                            new DataValue(par.getPrice(), Types.VARCHAR),
                            new DataValue(par.getDistriPrice(), Types.VARCHAR),
                            new DataValue(par.getAmt(), Types.VARCHAR),
                            new DataValue(par.getDistriAmt(), Types.VARCHAR),
                            new DataValue(par.getBaseQty(), Types.VARCHAR),
                            new DataValue(par.getBaseUnit(), Types.VARCHAR),
                            new DataValue(par.getPqty(), Types.VARCHAR),
                            new DataValue(par.getPunit(), Types.VARCHAR),
                            new DataValue(par.getUnitRatio(), Types.VARCHAR),
                            new DataValue(par.getReceiptOrg(), Types.VARCHAR),
                            new DataValue(par.getBsNo(), Types.VARCHAR),
                            new DataValue(par.getPluMemo(), Types.VARCHAR),
                            new DataValue(par.getStockQty(), Types.VARCHAR),
                    };
                    InsBean ib = new InsBean("DCP_STOCKOUT_ENTRY_DETAIL", detailColumns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }
            
            // 新增DCP_STOCKOUT_ENTRY
            DataValue[]	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(request.getStockOutEntryId(), Types.VARCHAR),
                    new DataValue(stockOutEntryNo, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(request.getDeliveryNo(), Types.VARCHAR),
                    new DataValue(request.getBsNo(), Types.VARCHAR),
                    new DataValue(request.getWarehouse(), Types.VARCHAR),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(request.getTotPqty(), Types.VARCHAR),
                    new DataValue(request.getTotCqty(), Types.VARCHAR),
                    new DataValue(request.getTotAmt(), Types.VARCHAR),
                    new DataValue(request.getTotDistriAmt(), Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(createByName, Types.VARCHAR),
                    new DataValue(createTime, Types.DATE),
                    new DataValue(request.getMemo(), Types.VARCHAR),
            };
            InsBean ib = new InsBean("DCP_STOCKOUT_ENTRY", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
            
            
            this.doExecuteDataToDB();
            
            res.setDatas(oneLv1);
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutEntryCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutEntryCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutEntryCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_StockOutEntryCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        if (request==null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else{
            if (Check.Null(request.getStockOutEntryId())){
                errMsg.append("退货录入单ID不可为空值, ");
                isFail = true;
            }
            if (Check.Null(request.getWarehouse())){
                errMsg.append("仓库不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(request.getTotPqty())){
                errMsg.append("数量合计必须为数值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(request.getTotCqty())){
                errMsg.append("品种合计必须为数值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(request.getTotAmt())){
                errMsg.append("零售价合计必须为数值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(request.getTotDistriAmt())){
                errMsg.append("进货价合计必须为数值, ");
                isFail = true;
            }
            
            List<DCP_StockOutEntryCreateReq.level1Elm> datas = request.getDatas();
            if (!CollectionUtils.isEmpty(datas)){
                for (DCP_StockOutEntryCreateReq.level1Elm par : datas){
                    if (Check.Null(par.getItem())){
                        errMsg.append("项次不可为空值, ");
                        isFail = true;
                    }
                    if (Check.Null(par.getPluNo())){
                        errMsg.append("商品编号不可为空值, ");
                        isFail = true;
                    }
                    if (Check.Null(par.getFeatureNo())){
                        errMsg.append("商品特征码不可为空值, ");
                        isFail = true;
                    }
                    
                    if (Check.Null(par.getPunit())){
                        errMsg.append("退货单位不可为空值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getPqty())){
                        errMsg.append("退货数量必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getUnitRatio())){
                        errMsg.append("单位转换率必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getPrice())){
                        errMsg.append("零售价必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getDistriPrice())){
                        errMsg.append("进货价必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getAmt())){
                        errMsg.append("零售金额必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getDistriAmt())){
                        errMsg.append("进货金额必须为数值, ");
                        isFail = true;
                    }
                    if (Check.Null(par.getBaseUnit())){
                        errMsg.append("基准单位不可为空值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getBaseQty())){
                        errMsg.append("基准数量必须为数值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumericType(par.getStockQty())){
                        errMsg.append("库存数量必须为数值, ");
                        isFail = true;
                    }
                    
                    if (isFail){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_StockOutEntryCreateReq> getRequestType() {
        return new TypeToken<DCP_StockOutEntryCreateReq>(){};
    }
    
    @Override
    protected DCP_StockOutEntryCreateRes getResponseType() {
        return new DCP_StockOutEntryCreateRes();
    }
    
    
    private String getDocNo(DCP_StockOutEntryCreateReq req,String eId,String shopId,String sDate) throws Exception {
        String stockOutEntryNo= "THLR" + sDate;
        
        String sql=" select max(stockoutentryno) as stockoutentryno from dcp_stockout_entry "
                + " where eid='"+eId+"' and shopid='"+shopId+"'  and stockoutentryno like '%" + stockOutEntryNo + "%'  ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        stockOutEntryNo = getQData.get(0).get("STOCKOUTENTRYNO").toString();
        if (Check.Null(stockOutEntryNo)) {
            stockOutEntryNo = "THLR" + sDate + "00001";
        } else {
            long i = Long.parseLong(stockOutEntryNo.substring(4)) + 1;
            stockOutEntryNo = "THLR" + i ;
        }
        
        return stockOutEntryNo;
    }
    
    
    
}
