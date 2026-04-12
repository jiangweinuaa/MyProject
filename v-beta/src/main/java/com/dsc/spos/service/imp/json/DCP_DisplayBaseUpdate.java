package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务名称：DCP_DisplayBaseUpdate
 * 服务说明：客显基础资料修改
 * @author jinzma
 * @since  2022-04-25
 */
public class DCP_DisplayBaseUpdate extends SPosAdvanceService<DCP_DisplayBaseUpdateReq, DCP_DisplayBaseUpdateRes> {
    @Override
    protected void processDUID(DCP_DisplayBaseUpdateReq req, DCP_DisplayBaseUpdateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String templateNo = req.getRequest().getTemplateNo();
            String sql = " select templateno from dcp_dualplay_baseinfo "
                    + " where eid='"+eId+"' and templateno='"+templateNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()){
                
                //删除DCP_DUALPLAY_BASE_SHOP
                DelBean db = new DelBean("DCP_DUALPLAY_BASE_SHOP");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
                
                //修改DCP_DUALPLAY_BASEINFO
                String lastModiBy = req.getOpNO();
                String lastModiByName = req.getOpName();
                String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                UptBean ub = new UptBean("DCP_DUALPLAY_BASEINFO");
                ub.addUpdateValue("TEMPLATENAME", new DataValue(req.getRequest().getTemplateName(), Types.VARCHAR));
                ub.addUpdateValue("SHOPTYPE", new DataValue(req.getRequest().getShopType(), Types.VARCHAR));
                ub.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
                ub.addUpdateValue("LOGOID", new DataValue(req.getRequest().getLogoId(), Types.VARCHAR));
                ub.addUpdateValue("WELCOMEWORDS", new DataValue(req.getRequest().getWelcomeWords(), Types.VARCHAR));
                ub.addUpdateValue("WELCOMEWORDCOLOR", new DataValue(req.getRequest().getWelcomeWordColor(), Types.VARCHAR));
                ub.addUpdateValue("BACKGROUNDCOLOR", new DataValue(req.getRequest().getBackgroundColor(), Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPID", new DataValue(lastModiBy, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME", new DataValue(lastModiByName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
                
                this.addProcessData(new DataProcessBean(ub));
                
                //新增DCP_DUALPLAY_BASE_SHOP
                List<level1Elm> ShopList = req.getRequest().getShopList();
                if (ShopList != null) {
                    String[] columns_shop = {"EID","TEMPLATENO","SHOPID"};
                    InsBean ib_shop = new InsBean("DCP_DUALPLAY_BASE_SHOP", columns_shop);
                    for (level1Elm par : ShopList) {
                        if (Check.Null(par.getShopId())){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "shopId不能为空 ");
                        }
                        DataValue[] insValue_shop = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(templateNo, Types.VARCHAR),
                                new DataValue(par.getShopId(), Types.VARCHAR),
                        };
                        ib_shop.addValues(insValue_shop);
                    }
                    this.addProcessData(new DataProcessBean(ib_shop));
                }
                
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板编号:"+templateNo+"不存在! ");
            }
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_DisplayBaseUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_DisplayBaseUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_DisplayBaseUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_DisplayBaseUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else {
            if (Check.Null(req.getRequest().getTemplateNo())) {
                isFail = true;
                errMsg.append("模板编号不能为空,");
            }
            if (Check.Null(req.getRequest().getTemplateName())) {
                isFail = true;
                errMsg.append("模板名称不能为空,");
            }
            if (Check.Null(req.getRequest().getShopType())) {
                isFail = true;
                errMsg.append("门店类型不能为空,");
            }
            if (Check.Null(req.getRequest().getStatus())) {
                isFail = true;
                errMsg.append("状态不能为空,");
            }
            if (Check.Null(req.getRequest().getLogoId())) {
                isFail = true;
                errMsg.append("logoId不能为空,");
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            
            List<level1Elm> shopList = req.getRequest().getShopList();
            if (req.getRequest().getShopType().equals("2")) {
                if (shopList == null) {
                    isFail = true;
                    errMsg.append("门店shopList不能为空,");
                } else {
                    for (level1Elm par : shopList) {
                        if (Check.Null(par.getShopId())) {
                            isFail = true;
                            errMsg.append("shopId不能为空,");
                        }
                        if (isFail) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                        }
                    }
                }
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_DisplayBaseUpdateReq> getRequestType() {
        return new TypeToken<DCP_DisplayBaseUpdateReq>(){};
    }
    
    @Override
    protected DCP_DisplayBaseUpdateRes getResponseType() {
        return new DCP_DisplayBaseUpdateRes();
    }
}
