package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CloseShopWarningQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_CloseShopWarningQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_CloseShopWarningQuery_OpenRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_CloseShopWarningQuery_OpenRes.level2ElmBill;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * @apiNote POS闭店提醒通知
 * @since 2021-05-21
 * @author jinzma
 */
public class DCP_CloseShopWarningQuery_Open extends SPosBasicService<DCP_CloseShopWarningQuery_OpenReq,DCP_CloseShopWarningQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_CloseShopWarningQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (req.getRequest()==null) {
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            String eId = req.getRequest().geteId();
            String shopId = req.getRequest().getShopId();
            String eDate = req.getRequest().geteDate();
            
            if (Check.Null(eId)) {
                errMsg.append("企业ID不能为空,");
                isFail = true;
            }
            if (Check.Null(shopId)) {
                errMsg.append("门店ID不能为空,");
                isFail = true;
            }
            if (Check.Null(eDate)) {
                errMsg.append("闭店日期不能为空,");
                isFail = true;
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_CloseShopWarningQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_CloseShopWarningQuery_OpenReq>(){};
    }
    
    @Override
    protected DCP_CloseShopWarningQuery_OpenRes getResponseType() {
        return new DCP_CloseShopWarningQuery_OpenRes();
    }
    
    @Override
    protected DCP_CloseShopWarningQuery_OpenRes processJson(DCP_CloseShopWarningQuery_OpenReq req) throws Exception {
        DCP_CloseShopWarningQuery_OpenRes res = this.getResponse();
        try {
            level1Elm datas = res.new level1Elm();
            datas.setBillDatas(new ArrayList<>());
            
            getCheckBill(req,res,datas);
            res.setDatas(datas);
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            return res;
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_CloseShopWarningQuery_OpenReq req) throws Exception {
        return null;
    }
    
    private void getCheckBill(DCP_CloseShopWarningQuery_OpenReq req,DCP_CloseShopWarningQuery_OpenRes res,level1Elm datas) {
        try {
            String eId = req.getRequest().geteId();
            String shopId = req.getRequest().getShopId();
            String eDate = req.getRequest().geteDate();
            String langType=req.getLangType();
            String sql = " select a.billtype,a.billtypename from dcp_closebilltype a "
                    + " left join dcp_closebilltype_pickshop b on a.eid=b.eid and a.billtype=b.billtype"
                    + " where a.eid='"+eId+"' and (a.restrictshop='0' or (a.restrictshop='1' and b.shopid='"+shopId+"'))"
                    + " and a.status='100' ";
            List<Map<String, Object>> getQDataClosebill=this.doQueryData(sql, null);
            List<Map<String, Object>> getQData;
            if (getQDataClosebill != null && !getQDataClosebill.isEmpty()) {
                for (Map<String, Object> oneData : getQDataClosebill) {
                    String billType = oneData.get("BILLTYPE").toString();
                    String billTypeName = oneData.get("BILLTYPENAME").toString();
					/*单据类型说明
					02配送收货\03退货出库\11移仓出库\06其他入库\07其他出库\05调拨出库\
					04调拨收货\08采购入库\09采购退货\16报损单\12盘点单\
					13完工入库\14组合单\15拆解单\18转换单\19订单
					 */
                    switch (billType) {
                        case "02":  ///配送收货
                            sql=" select stockinno as docno,N'' as load_docno,N'stockin' as doctype from dcp_stockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type='0'"
                                    + " union all"
                                    + " select a.receivingno as docno,a.load_docno,N'receiving' as doctype from dcp_receiving a"
                                    + " left join dcp_stockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type='0'"
                                    + " and (b.stockinno is null or b.stockinno = '')"
                                    + " and (a.receiptdate <='"+eDate+"' or a.receiptdate is null or a.receiptdate='')"
                                    + " order by doctype,docno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docType = getOneData.get("DOCTYPE").toString();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String loadDocNo = getOneData.get("LOAD_DOCNO").toString();
                                    String memo;
                                    if (docType.equals("stockin")) {
                                        memo = "配送收货单保存未确认，单号：" + docNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "配送收貨單保存未確認，單號：" + docNo;
                                        }
                                    }else {
                                        memo = "配送收货通知单未转入库单，发货单号：" + loadDocNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "配送收貨通知單未轉入庫單，發貨單號：" + loadDocNo;
                                        }
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "03":  ///退货出库
                            sql=" select stockoutno as docno from dcp_stockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type ='0'"
                                    + " order by stockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="退货出库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "退貨出庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "11":  ///移仓出库
                            sql=" select stockoutno as docno from dcp_stockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type ='4'"
                                    + " order by stockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="移仓出库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "移倉出庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "06":  ///其他入库
                            sql=" select stockinno as docno from dcp_stockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type ='3'"
                                    + " order by stockinno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="其他入库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "其他入庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "07":  ///其他出库
                            sql=" select stockoutno as docno from dcp_stockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type ='3'"
                                    + " order by stockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="其他出库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "其他出庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "05":  ///调拨出库
                            sql=" select stockoutno as docno from dcp_stockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type ='1'"
                                    + " order by stockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="调拨出库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "調撥出庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "04":  ///调拨收货
                            sql=" select stockinno as docno,N'' as load_docno,N'stockin' as doctype from dcp_stockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type='1'"
                                    + " union all"
                                    + " select a.receivingno as docno,a.load_docno,N'receiving' as doctype from dcp_receiving a"
                                    + " left join dcp_stockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type='1'"
                                    + " and (b.stockinno is null or b.stockinno = '')"
                                    + " and (a.receiptdate <='"+eDate+"' or a.receiptdate is null or a.receiptdate='')"
                                    + " order by doctype,docno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docType = getOneData.get("DOCTYPE").toString();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String loadDocNo = getOneData.get("LOAD_DOCNO").toString();
                                    String memo;
                                    if (docType.equals("stockin")) {
                                        memo = "调拨收货单保存未确认，单号：" + docNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "調撥收貨單保存未確認，單號：" + docNo;
                                        }
                                    } else {
                                        memo = "调拨收货通知单未转入库单，发货单号：" + loadDocNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "調撥收貨通知單未轉入庫單，發貨單號：" + loadDocNo;
                                        }
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "08":  ///采购入库
                            sql=" select sstockinno as docno,N'' as load_docno,N'sstockin' as doctype from dcp_sstockin "
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0'"
                                    + " union all"
                                    + " select a.receivingno as docno,a.load_docno,N'receiving' as doctype from dcp_receiving a"
                                    + " left join dcp_sstockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type='2'"
                                    + " and (b.sstockinno is null or b.sstockinno = '')"
                                    + " and (a.receiptdate <='"+eDate+"' or a.receiptdate is null or a.receiptdate='')"
                                    + " order by doctype,docno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docType = getOneData.get("DOCTYPE").toString();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String loadDocNo = getOneData.get("LOAD_DOCNO").toString();
                                    String memo;
                                    if (docType.equals("sstockin")) {
                                        memo = "采购入库单保存未确认，单号：" + docNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "采購入庫單保存未確認，單號：" + docNo;
                                        }
                                    } else {
                                        memo = "采购收货通知单未转入库单，发货单号：" + loadDocNo;
                                        if (langType.equals("zh_TW")) {
                                            memo = "采購收貨通知單未轉入庫單，發貨單號：" + loadDocNo;
                                        }
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "09":  ///采购退货
                            sql=" select sstockoutno as docno from dcp_sstockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                                    + " order by sstockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="采购退货单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "采購退貨單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "16":  ///报损单
                            sql=" select lstockoutno as docno from dcp_lstockout"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                                    + " order by lstockoutno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="报损单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "報損單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "12":  ///盘点单
                            //【ID1027141】【詹记3.0】闭店检查-盘点单的问题 by jinzma 20220718
                            String weekOfDay = this.getWeekDay();
                            String day = this.getDay();
                            String doubleDay = "1";    //单日
                            if(Integer.parseInt(day) % 2 == 0) {
                                doubleDay = "2";//双日
                            }
                            sql=" select ptemplate.* from ("
                                    + " select a.ptemplateno,a.ptemplate_name from dcp_ptemplate a"
                                    + " left join dcp_ptemplate_shop b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                                    + " and b.status='100' and b.shopid='"+shopId+"'"
                                    + " where a.eid='"+eId+"' and a.doc_type='1' and a.status='100' and a.stocktakecheck='Y'"
                                    + " and (((a.shoptype is null or a.shoptype='2') and b.shopid is not null) or a.shoptype='1')"
                                    + " and ((a.time_type='0' )"
                                    + " or (a.time_type='1' and a.time_value like '%"+doubleDay+"%')"
                                    + " or (a.time_type='2' and a.time_value like '%"+weekOfDay+"%')"
                                    + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;"+ Integer.valueOf(day) +";%%')"
                                    + " or (a.time_type='3' and a.time_value like '%%"+day+"%%'))"
                                    + " ) ptemplate"
                                    + " left join ( select ptemplateno from dcp_stocktake"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and bdate='"+eDate+"') p "
                                    + " on ptemplate.ptemplateno=p.ptemplateno"
                                    + " where p.ptemplateno is null "
                                    + " order by ptemplate.ptemplateno"
                                    + " ";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String ptemplateNo = getOneData.get("PTEMPLATENO").toString();
                                    String ptemplateName = getOneData.get("PTEMPLATE_NAME").toString();
                                    String memo="盘点模板未完成，模板编号："+ptemplateNo+" 模板名称："+ptemplateName;
                                    if (langType.equals("zh_TW")) {
                                        memo = "盤點模闆未完成，模闆編號："+ptemplateNo+" 模闆名稱："+ptemplateName;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(ptemplateNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            
                            //检查盘点单未确认的单据
                            sql=" select stocktakeno as docno from dcp_stocktake"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0'"
                                    + " and bdate<='"+eDate+"' order by stocktakeno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="盘点单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "盤點單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            
                            break;
                        case "13":  ///完工入库
                            sql=" select pstockinno as docno from dcp_pstockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type='0'"
                                    + " order by pstockinno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="完工入库单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "完工入庫單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "14":  ///组合单
                            sql=" select pstockinno as docno from dcp_pstockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type='1'"
                                    + " order by pstockinno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="组合单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "組合單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "15":  ///拆解单
                            sql=" select pstockinno as docno from dcp_pstockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type='2'"
                                    + " order by pstockinno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="拆解单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "拆解單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "18":  ///转换单
                            sql=" select pstockinno as docno from dcp_pstockin"
                                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' and doc_type in ('3','4')"
                                    + " order by pstockinno";
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="转换单保存未确认，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "轉換單保存未確認，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        case "19":  ///订单
                            // 【ID1025135】[泰奇3.0]到了交付时间未订转销的订单闭店时给提醒，承诺客户交付时间4月15日（后端服务） by jinzma 20220412
                            sql=" select orderno as docno from dcp_order"
                                    + " where eid='"+eId+"' and shippingshop='"+shopId+"' and status<>3 and status<>12 "
                                    //【ID1026208】[泰奇3.0]门店闭店前检查当天配送订单是否订转销完成 by jinzma 20220530
                                    + " and shipdate='"+eDate+"'"
                                    + " and (ordertosale_datetime is null or ordertosale_datetime='')"
                                    + " order by orderno";
                            
                            getQData=this.doQueryData(sql, null);
                            if (getQData != null && !getQData.isEmpty()) {
                                for (Map<String, Object> getOneData : getQData) {
                                    level2ElmBill oneLv2 = res.new level2ElmBill();
                                    String docNo = getOneData.get("DOCNO").toString();
                                    String memo="订单未完成订转销，单号：" +docNo ;
                                    if (langType.equals("zh_TW")) {
                                        memo = "訂單未完成訂轉銷，單號：" + docNo;
                                    }
                                    oneLv2.setBillType(billType);
                                    oneLv2.setBillTypeName(billTypeName);
                                    oneLv2.setBillNo(docNo);
                                    oneLv2.setMemo(memo);
                                    
                                    datas.getBillDatas().add(oneLv2);
                                }
                            }
                            break;
                        
                        default:
                            break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
    
    private String getWeekDay() throws Exception{
        String weekOfDay="";
        String sql = "select to_char(sysdate,'D') as week from dual";
        List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
        if (getWeekDay != null && !getWeekDay.isEmpty()) {
            String weekDay = getWeekDay.get(0).get("WEEK").toString();
            switch (weekDay) {
                case "1":
                    weekOfDay="周日";
                    break;
                case "2":
                    weekOfDay="周一";
                    break;
                case "3":
                    weekOfDay="周二";
                    break;
                case "4":
                    weekOfDay="周三";
                    break;
                case "5":
                    weekOfDay="周四";
                    break;
                case "6":
                    weekOfDay="周五";
                    break;
                case "7":
                    weekOfDay="周六";
                    break;
                default:
                    break;
            }
        }
        return weekOfDay;
    }
    
    private String getDay() throws Exception{
        String day="";
        String sql = "select to_char(sysdate,'dd') as day from dual";
        List<Map<String, Object>> getDay = this.doQueryData(sql, null);
        if (getDay != null && !getDay.isEmpty()) {
            day=getDay.get(0).get("DAY").toString();
        }
        return day;
    }
    
}
