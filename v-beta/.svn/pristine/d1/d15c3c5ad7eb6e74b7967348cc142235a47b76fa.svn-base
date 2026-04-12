package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务名称：DCP_SalePriceAdjustUpdate
 * 服务说明：自建门店调价单修改(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustUpdate extends SPosAdvanceService<DCP_SalePriceAdjustUpdateReq, DCP_SalePriceAdjustUpdateRes> {
    @Override
    protected void processDUID(DCP_SalePriceAdjustUpdateReq req, DCP_SalePriceAdjustUpdateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String salePriceAdjustNo = req.getRequest().getSalePriceAdjustNo();
            String sql = " select salepriceadjustno from dcp_salepriceadjust "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and salepriceadjustno='"+salePriceAdjustNo+"' and status='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()){
                String lastModiBy = req.getOpNO();
                String lastModiByName = req.getOpName();
                String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String type = req.getRequest().getType();
                
                //删除单据单身
                DelBean db = new DelBean("DCP_SALEPRICEADJUST_DETAIL");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db.addCondition("SALEPRICEADJUSTNO", new DataValue(salePriceAdjustNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
                
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
                
                UptBean ub = new UptBean("DCP_SALEPRICEADJUST");
                ub.addUpdateValue("LASTMODIOPID", new DataValue(lastModiBy, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME", new DataValue(lastModiByName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
                ub.addUpdateValue("TYPE", new DataValue(type, Types.VARCHAR));
                
                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("SALEPRICEADJUSTNO", new DataValue(salePriceAdjustNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
                
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新确认！ ");
            }
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceAdjustUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceAdjustUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceAdjustUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getSalePriceAdjustNo())){
                isFail = true;
                errMsg.append("salePriceAdjustNo不能为空,");
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
    protected TypeToken<DCP_SalePriceAdjustUpdateReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustUpdateReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustUpdateRes getResponseType() {
        return new DCP_SalePriceAdjustUpdateRes();
    }
}
