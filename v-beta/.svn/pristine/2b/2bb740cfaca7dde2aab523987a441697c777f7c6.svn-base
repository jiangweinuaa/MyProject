package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustCreateReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务名称：DCP_SalePriceAdjustCreate
 * 服务说明：自建门店调价单新增(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustCreate extends SPosAdvanceService<DCP_SalePriceAdjustCreateReq, DCP_SalePriceAdjustCreateRes> {
    @Override
    protected void processDUID(DCP_SalePriceAdjustCreateReq req, DCP_SalePriceAdjustCreateRes res) throws Exception {
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String salePriceAdjustId = req.getRequest().getSalePriceAdjustId();
            
            String sql = " select salepriceadjustid from dcp_salepriceadjust "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and salepriceadjustid='"+salePriceAdjustId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()){
                String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                String createBy = req.getOpNO();
                String createByName = req.getOpName();
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String salePriceAdjustNo = getSalePriceAdjustNo(req,bDate);
                String type = req.getRequest().getType();
                
                //新增单身
                String[] columnsDetail = {
                        "EID","SHOPID","SALEPRICEADJUSTNO",
                        "ITEM","PLUNO","FEATURENO","UNIT","PRICE","MINPRICE",
                        "ISDISCOUNT","ISPROM","BEGINDATE","ENDDATE","STATUS",
                };
                InsBean ibDetail = new InsBean("DCP_SALEPRICEADJUST_DETAIL", columnsDetail);
                List<level1Elm> datas = req.getRequest().getDatas();
                for (level1Elm par:datas){
                    String featureNo = par.getFeatureNo();
                    if (Check.Null(featureNo)){
                        featureNo = " ";
                    }
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(salePriceAdjustNo, Types.VARCHAR),
                            new DataValue(par.getItem(), Types.VARCHAR),
                            new DataValue(par.getPluNo(), Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(par.getUnit(), Types.VARCHAR),
                            new DataValue(par.getPrice(), Types.VARCHAR),
                            new DataValue(par.getMinPrice(), Types.VARCHAR),
                            new DataValue(par.getIsDiscount(), Types.VARCHAR),
                            new DataValue(par.getIsProm(), Types.VARCHAR),
                            new DataValue(par.getBeginDate(), Types.DATE),
                            new DataValue(par.getEndDate(), Types.DATE),
                            new DataValue(par.getStatus(), Types.VARCHAR),
                    };
                    ibDetail.addValues(insValue);
                }
                this.addProcessData(new DataProcessBean(ibDetail));
                
                //新增单头
                String[] columns = {
                        "EID","SHOPID","SALEPRICEADJUSTNO","SALEPRICEADJUSTID",
                        "CREATEOPID","CREATEOPNAME","CREATETIME",
                        "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME",
                        "STATUS","BDATE","TYPE",
                };
                InsBean ib = new InsBean("DCP_SALEPRICEADJUST", columns);
                DataValue[]	insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(salePriceAdjustNo, Types.VARCHAR),
                        new DataValue(salePriceAdjustId, Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createByName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(type, Types.VARCHAR),
                };
                
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
                
                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceAdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceAdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceAdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getSalePriceAdjustId())){
                isFail = true;
                errMsg.append("salePriceAdjustId不能为空,");
            }
            if (Check.Null(req.getRequest().getType())){
                isFail = true;
                errMsg.append("type不能为空,");
            }
            if (req.getRequest().getDatas()==null){
                isFail = true;
                errMsg.append("datas不能为空,");
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            List<level1Elm> datas = req.getRequest().getDatas();
            for (level1Elm par:datas){
                if (Check.Null(par.getItem())){
                    isFail = true;
                    errMsg.append("item不能为空,");
                }
                if (Check.Null(par.getPluNo())){
                    isFail = true;
                    errMsg.append("pluNo不能为空,");
                }
                if (Check.Null(par.getFeatureNo())){
                    isFail = true;
                    errMsg.append("featureNo不能为空,");
                }
                if (Check.Null(par.getUnit())){
                    isFail = true;
                    errMsg.append("unit不能为空,");
                }
                if (Check.Null(par.getPrice())){
                    isFail = true;
                    errMsg.append("price不能为空,");
                }
                if (Check.Null(par.getMinPrice())){
                    isFail = true;
                    errMsg.append("minPrice不能为空,");
                }
                if (Check.Null(par.getStatus())){
                    isFail = true;
                    errMsg.append("status不能为空,");
                }
                if (Check.Null(par.getIsProm())){
                    isFail = true;
                    errMsg.append("isProm不能为空,");
                }
                if (Check.Null(par.getIsDiscount())){
                    isFail = true;
                    errMsg.append("isDiscount不能为空,");
                }
                if (Check.Null(par.getBeginDate())){
                    isFail = true;
                    errMsg.append("beginDate不能为空,");
                }
                if (Check.Null(par.getEndDate())){
                    isFail = true;
                    errMsg.append("endDate不能为空,");
                }
                
                if (isFail) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SalePriceAdjustCreateReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustCreateReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustCreateRes getResponseType() {
        return new DCP_SalePriceAdjustCreateRes();
    }
    
    private String getSalePriceAdjustNo(DCP_SalePriceAdjustCreateReq req,String bDate) throws Exception{
        String eId=req.geteId();
        String shopId=req.getShopId();
        String salePriceAdjustNo = "JMTJ" + bDate;
        String sql=" select max(salepriceadjustno) as salepriceadjustno from dcp_salepriceadjust"
                + " where eid='"+eId+"' and shopid='"+shopId+"' and salepriceadjustno like '%"+salePriceAdjustNo+"%' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        salePriceAdjustNo = getQData.get(0).get("SALEPRICEADJUSTNO").toString();
        if (Check.Null(salePriceAdjustNo)){
            salePriceAdjustNo = "JMTJ" + bDate + "00001";
        }else{
            salePriceAdjustNo = salePriceAdjustNo.substring(4);
            long i = Long.parseLong(salePriceAdjustNo) + 1;
            salePriceAdjustNo = i + "";
            salePriceAdjustNo = "JMTJ" + salePriceAdjustNo;
        }
        return salePriceAdjustNo;
    }
}
