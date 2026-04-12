package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderERPCreateReq;
import com.dsc.spos.json.cust.res.DCP_POrderERPCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_POrderERPCreate
 * 服务说明：要货单新增（ERP下发）
 * @author jinzma
 * @since  2023-07-03
 */
public class DCP_POrderERPCreate extends SPosAdvanceService<DCP_POrderERPCreateReq, DCP_POrderERPCreateRes> {
    @Override
    protected void processDUID(DCP_POrderERPCreateReq req, DCP_POrderERPCreateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String loadDocNo = req.getLoadDocNo();
            
            //检查loadDocNo
            String sql = " select porderno from dcp_porder "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and ofno='"+loadDocNo+"' and otype='6' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (CollectionUtils.isEmpty(getQData)){
                String porderNo=PosPub.getBillNo(dao,eId,shopId,"YHSQ");
                String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String sTime = new SimpleDateFormat("HHmmss").format(new Date());
                
                
                String[] columns = {
                        "EID","ORGANIZATIONNO","SHOPID","PORDERNO","BDATE","PARTITION_DATE","RDATE","RTIME","MEMO",
                        "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT","OTYPE","OFNO","STATUS","PROCESS_STATUS",
                        "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                        "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                        "PREDICTAMT","AVGSALEAMT","MODIFRATIO","DISTRIBUTIONFEE",
                        "ISURGENTORDER","ISPREDICT","ISFORECAST","IS_ADD"
                };
                
                String[] columns_detail = {
                        "EID","ORGANIZATIONNO","SHOPID","PORDERNO","BDATE","PARTITION_DATE","MEMO",
                        "ITEM","PLUNO","FEATURENO","PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO",
                        "PRICE","AMT","DISTRIPRICE","DISTRIAMT","MAX_QTY","MIN_QTY","MUL_QTY",
                        "PROPQTY","KQTY","KADJQTY","PROPADJQTY","HEADSTOCKQTY","DETAIL_STATUS"
                };
                
                int totCqty = 0;
                BigDecimal totPqty_b = new BigDecimal("0");
                BigDecimal totAmt_b = new BigDecimal("0");
                BigDecimal totDistriAmt_b = new BigDecimal("0");
                
                
                for (Map<String, String> par : req.getDatas()) {
                    String item = par.get("item");
                    String pluNo = par.get("pluNo");
                    String baseQty = par.get("baseQty");
                    String baseUnit = par.get("baseUnit");
                    String pqty = par.get("pqty");
                    String punit = par.get("punit");
                    String price = new BigDecimal(par.get("price")).toPlainString();
                    String distriPrice = new BigDecimal(par.get("distriPrice")).toPlainString();
                    String unitRatio = par.get("unitRatio");
                    String featureNo = par.get("featureNo");
                    if (Check.Null(featureNo)){
                        featureNo = " ";
                    }
                    String amt = new BigDecimal(par.get("amt")).toPlainString();
                    String distriAmt = new BigDecimal(par.get("distriAmt")).toPlainString();
                    
                    totPqty_b = totPqty_b.add(new BigDecimal(pqty));
                    totAmt_b = totAmt_b.add(new BigDecimal(amt));
                    totDistriAmt_b = totDistriAmt_b.add(new BigDecimal(distriAmt));
                    
                    //资料有效性检查
                    if (featureNo.equals(" ")){
                        sql = " select a.pluno,b.unitratio from dcp_goods a"
                                + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.baseunit=b.unit"
                                + " where a.eid='"+eId+"' and a.pluno='"+pluNo+"' and a.plutype<>'FEATURE' and a.status='100'"
                                + " and a.baseunit='"+baseUnit+"' and b.ounit='"+punit+"' ";
                    }else{
                        sql = " select a.pluno,b.unitratio from dcp_goods a"
                                + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.baseunit=b.unit"
                                + " inner join dcp_goods_feature c on a.eid=c.eid and a.pluno=c.pluno and c.status='100'"
                                + " where a.eid='"+eId+"' and a.pluno='"+pluNo+"' and c.featureno='"+featureNo+"' "
                                + " and a.plutype='FEATURE' and a.status='100' and a.baseunit='"+baseUnit+"' and b.ounit='"+punit+"'";
                    }
                    
                    List<Map<String, Object>> getCheckQData = this.doQueryData(sql,null);
                    if (CollectionUtils.isEmpty(getCheckQData)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品检核失败,商品编号："+pluNo+" 商品特征码："+featureNo+" 录入单位："+punit+" 基本单位："+baseUnit+" 在基础资料档(dcp_goods或dcp_goods_unit或dcp_goods_feature)中不存在或缺少录入单位与基本单位的对应关系");
                    }
                    
                    BigDecimal unitRatioCheck_b = new BigDecimal(getCheckQData.get(0).get("UNITRATIO").toString());
                    if (unitRatioCheck_b.compareTo(new BigDecimal(unitRatio)) != 0){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品基础资料检核失败,商品编号："+pluNo+" ERP传入单位转换率："+unitRatio+" 和商品dcp_goods_unit单位转换率："+unitRatioCheck_b.toPlainString()+" 不一致!");
                    }
                    
                    //插入DCP_PORDER_DETAIL
                    DataValue[] insValue_detail = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(porderNo, Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR),
                            new DataValue(par.getOrDefault("memo",""), Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(pqty, Types.VARCHAR),
                            new DataValue(punit, Types.VARCHAR),
                            new DataValue(baseQty, Types.VARCHAR),
                            new DataValue(baseUnit, Types.VARCHAR),
                            new DataValue(unitRatio, Types.VARCHAR),
                            new DataValue(price, Types.VARCHAR),
                            new DataValue(amt, Types.VARCHAR),
                            new DataValue(distriPrice, Types.VARCHAR),
                            new DataValue(distriAmt, Types.VARCHAR),
                            new DataValue("99999", Types.VARCHAR),   //MAX_QTY
                            new DataValue("1", Types.VARCHAR),       //MIN_QTY
                            new DataValue("1", Types.VARCHAR),       //MUL_QTY
                            new DataValue("0", Types.VARCHAR),       //PROPQTY
                            new DataValue("0", Types.VARCHAR),       //KQTY
                            new DataValue("0", Types.VARCHAR),       //KADJQTY
                            new DataValue("0", Types.VARCHAR),       //PROPADJQTY
                            new DataValue("0", Types.VARCHAR),       //HEADSTOCKQTY
                            new DataValue("0", Types.VARCHAR),       //DETAIL_STATUS
                    };
                    
                    InsBean ib_detail = new InsBean("DCP_PORDER_DETAIL", columns_detail);
                    ib_detail.addValues(insValue_detail);
                    this.addProcessData(new DataProcessBean(ib_detail));
                    
                    totCqty++;
                }
                
                //插入DCP_PORDER
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(porderNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(req.getRDate(), Types.VARCHAR),
                        new DataValue("000000", Types.VARCHAR),
                        new DataValue(req.getMemo(), Types.VARCHAR),
                        new DataValue(totPqty_b.toPlainString(), Types.VARCHAR),
                        new DataValue(totCqty, Types.VARCHAR),
                        new DataValue(totAmt_b.toPlainString(), Types.VARCHAR),
                        new DataValue(totDistriAmt_b.toPlainString(), Types.VARCHAR),
                        new DataValue("7", Types.VARCHAR),   //OTYPE  7.总部帮门店要货
                        new DataValue(loadDocNo, Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue("Y", Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),      // PREDICTAMT
                        new DataValue("0", Types.VARCHAR),      // AVGSALEAMT
                        new DataValue("0", Types.VARCHAR),      // MODIFRATIO
                        new DataValue("0", Types.VARCHAR),      // DISTRIBUTIONFEE
                        new DataValue("N", Types.VARCHAR),      // ISURGENTORDER
                        new DataValue("N", Types.VARCHAR),      // ISPREDICT
                        new DataValue("N", Types.VARCHAR),      // ISFORECAST
                        new DataValue("N", Types.VARCHAR),      // IS_ADD
                };
                
                InsBean ib = new InsBean("DCP_PORDER", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
                
                
                //插入单据检查表 DCP_PLATFORM_BILLCHECK,避免ERP重复下发
                String[] columns1 = {"EID", "SHOPID", "BILLTYPE", "BILLNO"} ;
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue("porder", Types.VARCHAR),
                        new DataValue( loadDocNo, Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_PLATFORM_BILLCHECK", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
                
                
                this.doExecuteDataToDB();
                
                res.setDoc_no(porderNo);
                res.setOrg_no(shopId);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else {
                //防止ERP不断重复调用，如果产生就直接返回成功
                res.setDoc_no(getQData.get(0).get("PORDERNO").toString());
                res.setOrg_no(shopId);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("load_doc_no:" + loadDocNo +" 已存在对应的要货单！");
            }
            
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);
            
            pw.flush();
            pw.close();
            
            errors.flush();
            errors.close();
            
            this.pData.clear();
            
            //插入失败
            res.setDoc_no("");
            res.setOrg_no("");
            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription(e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderERPCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderERPCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderERPCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_POrderERPCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<Map<String, String>> datas = req.getDatas();
        if (Check.Null(req.getShopId())){
            errMsg.append("门店(shopNo)不可为空值, ");
            isFail = true;
        }
        if (!PosPub.isNumeric(req.getRDate())){
            errMsg.append("需求日期(requisition_date)不可为空值或格式不正确,应为YYYYMMDD, ");
            isFail = true;
        }else{
            if (req.getRDate().length()!=8){
                errMsg.append("需求日期(requisition_date)格式不正确,应为YYYYMMDD, ");
                isFail = true;
            }
        }
        if (Check.Null(req.getLoadDocNo())){
            errMsg.append("来源单号(load_doc_no)不可为空值, ");
            isFail = true;
        }
        if (CollectionUtils.isEmpty(datas)){
            errMsg.append("要货单身(requisitionCreate_detail)不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for (Map<String, String> par : datas) {
            if (!PosPub.isNumeric(par.getOrDefault("item", ""))){
                errMsg.append("序号(seq)不可为空值或非数值, ");
                isFail = true;
            }
            if (Check.Null(par.getOrDefault("pluNo", ""))){
                errMsg.append("商品编号(item_no)不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(par.getOrDefault("baseQty", ""))){
                errMsg.append("基本单位数量(base_qty)不可为空值或非数值, ");
                isFail = true;
            }
            if (Check.Null(par.getOrDefault("baseUnit", ""))){
                errMsg.append("基本单位(base_unit)不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(par.getOrDefault("pqty", ""))){
                errMsg.append("包装件数(packing_qty)不可为空值或非数值, ");
                isFail = true;
            }
            if (Check.Null(par.getOrDefault("punit", ""))){
                errMsg.append("包装单位(packing_unit)不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.get("price"))){
                errMsg.append("零售价(price)不可为空值或非数值, ");
                isFail = true;
            }else{
                if (!PosPub.isNumericType(new BigDecimal(par.get("price")).toPlainString())){
                    errMsg.append("零售价(price)不可为空值或非数值, ");
                    isFail = true;
                }
            }
            if (Check.Null(par.get("distriPrice"))){
                errMsg.append("进货单价(distri_price)不可为空值或非数值, ");
                isFail = true;
            }else{
                if (!PosPub.isNumericType(new BigDecimal(par.get("distriPrice")).toPlainString())){
                    errMsg.append("进货单价(distri_price)不可为空值或非数值, ");
                    isFail = true;
                }
            }
            if (!PosPub.isNumericType(par.getOrDefault("unitRatio", ""))){
                errMsg.append("转换率(unit_ratio)不可为空值或非数值, ");
                isFail = true;
            }
            if (Check.Null(par.get("amt"))){
                errMsg.append("零售金额(amount)不可为空值或非数值, ");
                isFail = true;
            }else{
                if (!PosPub.isNumericType(new BigDecimal(par.get("amt")).toPlainString())){
                    errMsg.append("零售金额(amount)不可为空值或非数值, ");
                    isFail = true;
                }
            }
            if (Check.Null(par.get("distriAmt"))){
                errMsg.append("进货金额(distri_amount)不可为空值或非数值, ");
                isFail = true;
            }else{
                if (!PosPub.isNumericType(new BigDecimal(par.get("distriAmt")).toPlainString())){
                    errMsg.append("进货金额(distri_amount)不可为空值或非数值, ");
                    isFail = true;
                }
            }
            
            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_POrderERPCreateReq> getRequestType() {
        return new TypeToken<DCP_POrderERPCreateReq>(){};
    }
    
    @Override
    protected DCP_POrderERPCreateRes getResponseType() {
        return new DCP_POrderERPCreateRes();
    }
}
