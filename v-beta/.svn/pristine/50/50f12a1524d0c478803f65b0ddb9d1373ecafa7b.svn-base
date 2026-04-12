package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_ScanOrderBaseSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_ScanOrderBaseSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ScanOrderBaseSetQuery
        extends SPosBasicService<DCP_ScanOrderBaseSetQueryReq, DCP_ScanOrderBaseSetQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_ScanOrderBaseSetQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ScanOrderBaseSetQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_ScanOrderBaseSetQueryReq>() {
        };
    }
    
    @Override
    protected DCP_ScanOrderBaseSetQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_ScanOrderBaseSetQueryRes();
    }
    
    @Override
    protected DCP_ScanOrderBaseSetQueryRes processJson(DCP_ScanOrderBaseSetQueryReq req) throws Exception {
        DCP_ScanOrderBaseSetQueryRes res = null;
        res = this.getResponse();
        
        if (req.getPageNumber() == 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == 0) {
            req.setPageSize(20);
        }
        
        int totalRecords = 0;// 总笔数
        int totalPages = 0;
        
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String shopId = req.getRequest().getShopId();
        
        
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        
        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;
        
        StringBuffer sb = new StringBuffer("select * from (  "
                + " select count(DISTINCT t.ruleNo) OVER() AS NUM, dense_rank() over (order BY t.Ruleno  ) rn, "
                + " t.*, tr.rangeType, tr.Id, tr.name, tp.SORTID , tp.PAYTYPE AS tPayType, tp.PAYNAME  from dcp_scanorder_baseset t "
                + " left join DCP_SCANORDER_BASESET_RANGE tr on t.eid = tr.eid and t.ruleNo = tr.ruleNo " +
                " LEFT JOIN DCP_SCANORDER_BASESET_PAYTYPE tp ON t.EID = tp.EID AND t.RULENO = tp.RULENO "
                + " where t.eid='" + eid + "' ");
        
        if (keyTxt != null && keyTxt.length() > 0) {
            sb.append("and ( t.RULENO like '%%" + keyTxt + "%%' or t.RULENAME like '%%" + keyTxt + "%%') ");
        }
        
        if (shopId != null && shopId.length() > 0) {
            sb.append(" and ( " + " t.restrictShop = '0' " + " or (t.restrictShop = '1' and tr.id = '" + shopId + "') "
                    + " or (t.restrictShop = '2' and tr.id != '" + shopId + "')" + ") ");
        }
        sb.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize)  + "  order by rn, SORTID" );
        
        String sql = sb.toString();
        
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        
        // 单头主键字段
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
        condition.put("EID", true);
        condition.put("RULENO", true);
        
        // 调用过滤函数
        List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
        condition = null;
        
        // 单头主键字段
        Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查询条件
        condition2.put("EID", true);
        condition2.put("RULENO", true);
        condition2.put("SORTID", true);
        
        // 调用过滤函数
        List<Map<String, Object>> getPaySet = MapDistinct.getMap(getQDataDetail, condition2);
        
        // 单头主键字段
        Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); // 查询条件
        condition3.put("EID", true);
        condition3.put("RULENO", true);
        condition3.put("ID", true);
        
        // 调用过滤函数
        List<Map<String, Object>> getRange = MapDistinct.getMap(getQDataDetail, condition3);
        
        
        res.setDatas(new ArrayList<DCP_ScanOrderBaseSetQueryRes.level1Elm>());
        for (Map<String, Object> oneData : getQHeader) {
            DCP_ScanOrderBaseSetQueryRes.level1Elm oneLv1 = res.new level1Elm();
            
            String ruleNo = oneData.get("RULENO").toString();
            oneLv1.setRuleNo(ruleNo);
            oneLv1.setRuleName(oneData.get("RULENAME").toString());
            oneLv1.setScanType(oneData.get("SCANTYPE").toString());
//            oneLv1.setPackNo(oneData.get("PACKNO").toString());
//            oneLv1.setPackName(oneData.get("PACKNAME").toString());
            oneLv1.setRestrictTable(oneData.get("RESTRICTTABLE").toString());
            
            oneLv1.setTableSet(oneData.get("TABLESET").toString());
            oneLv1.setCheckType(oneData.get("CHECKTYPE").toString());
//            oneLv1.setRestrictLike(oneData.get("RESTRICTLIKE").toString());
            oneLv1.setRestrictAdvanceOrder(oneData.get("RESTRICTADVANCEORDER").toString());
            oneLv1.setRetainTime(oneData.get("RETAINTIME").toString());
            
            // 新增开关takeAway外带打包，coupon优惠券、integral积分 BY WANGZYC 20201123
            oneLv1.setTakeAway(oneData.get("TAKEAWAY").toString());
            oneLv1.setCoupon(oneData.get("CONPON").toString());
            oneLv1.setIntegral(oneData.get("INTEGRAL").toString());
            
            // 新增firstRegister点餐前会员注册 BY WANGZYC 20201130
            oneLv1.setFirstRegister(oneData.get("FIRSTREGISTER").toString());
            
            // 新增orderMemo订单备注开关 BY WANGZYC 20201214
            oneLv1.setOrderMemo(oneData.get("ORDERMEMO").toString());
            
            String search = oneData.get("SEARCH").toString();
            if(Check.Null(search)){
                search = "1";
            }
            oneLv1.setSearch(search);
            
            String recharge = oneData.get("RECHARGE").toString();
            if(Check.Null(recharge)){
                recharge = "1";
            }
            oneLv1.setRecharge(recharge);
            
            //支付成功页评价 0.禁用 1.启用，默认禁用，空为禁用 by jinzma 20230109
            String evaluation = oneData.get("EVALUATION").toString();
            if (Check.Null(evaluation)){
                evaluation="0";
            }
            oneLv1.setEvaluation(evaluation);

            String ispaycard = Convert.toStr(oneData.get("ISPAYCARD"),"0");
            if (Check.Null(ispaycard)){
                ispaycard="0";
            }
            oneLv1.setIsPayCard(ispaycard);
            
            oneLv1.setBeforOrder(oneData.get("BEFORORDER").toString());
            oneLv1.setChoosableTime(oneData.get("CHOOSABLETIME").toString());
            
            oneLv1.setIsEvaluateRemind(oneData.get("ISEVALUATEREMIND").toString());
            oneLv1.setRemindTime(oneData.get("REMINDTIME").toString());
            
            // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
            oneLv1.setIsAutoProm(oneData.get("ISAUTOPROM").toString());
            oneLv1.setIsAutoRegister(oneData.get("ISAUTOREGISTER").toString());
            String isAutoFold = oneData.get("ISAUTOFOLD").toString();
            if(Check.Null(isAutoFold)){
                isAutoFold = "1";
            }
            oneLv1.setIsAutoFold(isAutoFold);
            
            oneLv1.setQrCode(oneData.get("QRCODE").toString());
            oneLv1.setLastModiOpId( oneData.get("LASTMODIOPID").toString());
            oneLv1.setLastModiOpName( oneData.get("LASTMODIOPNAME").toString());
            oneLv1.setStatus(oneData.get("STATUS").toString());
            
            oneLv1.setCounterPay(oneData.get("COUNTERPAY").toString());
            
            oneLv1.setRecommendedDishes(oneData.get("RECOMMENDEDDISHES").toString());
            oneLv1.setDescription(oneData.get("DESCRIPTION").toString());
            oneLv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
            oneLv1.setRestrictChannel(oneData.get("RESTRICTCHANNEL").toString());
            oneLv1.setRestrictRegister(oneData.get("REGISTER").toString());
            
            //【ID1035591】扫码点餐及外卖点单加详情配置项-服务  by jinzma 20230828
            String isGoodsDetailDisplay = oneData.get("ISGOODSDETAILDISPLAY").toString();
            if(Check.Null(isGoodsDetailDisplay)){
                isGoodsDetailDisplay = "0";
            }
            oneLv1.setIsGoodsDetailDisplay(isGoodsDetailDisplay);
            
            
            oneLv1.setRangeList(new ArrayList<DCP_ScanOrderBaseSetQueryRes.RangeList>());
            oneLv1.setPaySet(new ArrayList<DCP_ScanOrderBaseSetQueryRes.PaySet>());
            for (Map<String, Object> oneData2 : getRange) {
                DCP_ScanOrderBaseSetQueryRes.RangeList oneLv2 = res.new RangeList();
                
                String Id = oneData2.get("ID").toString();
                if (ruleNo.equals(oneData2.get("RULENO").toString() ) && !Check.Null(Id)) {
                    oneLv2.setId(Id);
                    oneLv2.setName(oneData2.get("NAME").toString());
                    oneLv2.setRangeType(oneData2.get("RANGETYPE").toString());
                    oneLv1.getRangeList().add(oneLv2);
                }
                
            }
            
            for (Map<String, Object> set : getPaySet) {
                String sortid = set.get("SORTID").toString();
                DCP_ScanOrderBaseSetQueryRes.PaySet paySet = res.new PaySet();
                if(ruleNo.equals(set.get("RULENO").toString())&& !Check.Null(sortid)){
                    paySet.setSortId(sortid);
                    paySet.setPayType(set.get("TPAYTYPE").toString());
                    paySet.setPayName(set.get("PAYNAME").toString());
                    oneLv1.getPaySet().add(paySet);
                }
            }

//            // ISINVOICE 是否开发票 0否 1是
//            String isinvoice = oneData.getOrDefault("ISINVOICE", "1").toString();
//            isinvoice = Check.Null(isinvoice) ? "1" : isinvoice;
//            // AREA 地区 0大陆 1台湾
//            String area = oneData.getOrDefault("AREA", "0").toString();
//            area = Check.Null(area) ? "0" : area;
//            // RESERVEDINVOICEINFO 预留发票信息 0否 1是
//            String reservedInvoiceinfo = oneData.getOrDefault("RESERVEDINVOICEINFO", "0").toString();
//            reservedInvoiceinfo = Check.Null(reservedInvoiceinfo) ? "0" : reservedInvoiceinfo;
//            String orderInvoice = oneData.getOrDefault("ORDERINVOICE", "0").toString();
//            orderInvoice = Check.Null(orderInvoice) ? "0" : orderInvoice;
//            // ORDERINVOICE 下单开票，0-否，1-是
//            String memberCarrier = oneData.getOrDefault("MEMBERCARRIER", "0").toString();
//            memberCarrier = Check.Null(memberCarrier) ? "0" : memberCarrier;
//
//            DCP_ScanOrderBaseSetQueryRes.Invoice invoice = new DCP_ScanOrderBaseSetQueryRes.Invoice();
//            invoice.setIsinvoice(isinvoice);
//            invoice.setArea(area);
//            invoice.setReservedInvoiceinfo(reservedInvoiceinfo);
//            invoice.setOrderInvoice(orderInvoice);
//            invoice.setMemberCarrier(memberCarrier);
//            oneLv1.setInvoice(invoice);
            
            res.getDatas().add(oneLv1);
        }
        getQHeader = null;
        getQDataDetail = null;
        
        // 这里其实只有1页
        totalRecords = res.getDatas().size();
        totalPages = totalRecords / req.getPageSize();
        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected String getQuerySql(DCP_ScanOrderBaseSetQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
}
