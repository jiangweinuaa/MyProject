package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseCreateReq;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseCreateRes;
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
 * 服务名称：DCP_DisplayBaseCreate
 * 服务说明：客显基础资料新增
 * @author jinzma
 * @since  2022-04-24
 */
public class DCP_DisplayBaseCreate extends SPosAdvanceService<DCP_DisplayBaseCreateReq, DCP_DisplayBaseCreateRes> {
    @Override
    protected void processDUID(DCP_DisplayBaseCreateReq req, DCP_DisplayBaseCreateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String templateId = req.getRequest().getTemplateId();
            String sql = " select templateid from dcp_dualplay_baseinfo where eid='"+eId+"' and templateid='"+templateId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()){
                String createBy = req.getOpNO();
                String createByName = req.getOpName();
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String templateNo = getTemplateNo(eId);
                
                //新增DCP_DUALPLAY_BASEINFO
                String[] columns = {
                        "EID","TEMPLATENO","TEMPLATENAME","TEMPLATEID","SHOPTYPE","STATUS",
                        "LOGOID","WELCOMEWORDS","WELCOMEWORDCOLOR","BACKGROUNDCOLOR",
                        "CREATEOPID","CREATEOPNAME","CREATETIME"
                };
                InsBean ib =new InsBean("DCP_DUALPLAY_BASEINFO", columns);
                DataValue[]	insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateNo, Types.VARCHAR),
                        new DataValue(req.getRequest().getTemplateName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getTemplateId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getShopType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
                        new DataValue(req.getRequest().getLogoId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getWelcomeWords(), Types.VARCHAR),
                        new DataValue(req.getRequest().getWelcomeWordColor(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBackgroundColor(), Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createByName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
    
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
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板ID已存在! ");
            }
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_DisplayBaseCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_DisplayBaseCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_DisplayBaseCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_DisplayBaseCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getTemplateId())){
                isFail = true;
                errMsg.append("模板ID不能为空,");
            }
            if (Check.Null(req.getRequest().getTemplateName())){
                isFail = true;
                errMsg.append("模板名称不能为空,");
            }
            if (Check.Null(req.getRequest().getShopType())){
                isFail = true;
                errMsg.append("门店类型不能为空,");
            }
            if (Check.Null(req.getRequest().getStatus())){
                isFail = true;
                errMsg.append("状态不能为空,");
            }
            if (Check.Null(req.getRequest().getLogoId())){
                isFail = true;
                errMsg.append("logoId不能为空,");
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            
            List<level1Elm> shopList = req.getRequest().getShopList();
            if (req.getRequest().getShopType().equals("2")){
                if (shopList == null){
                    isFail = true;
                    errMsg.append("门店shopList不能为空,");
                }else{
                    for (level1Elm par : shopList){
                        if (Check.Null(par.getShopId())){
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
    protected TypeToken<DCP_DisplayBaseCreateReq> getRequestType() {
        return new TypeToken<DCP_DisplayBaseCreateReq>(){};
    }
    
    @Override
    protected DCP_DisplayBaseCreateRes getResponseType() {
        return new DCP_DisplayBaseCreateRes();
    }
    
    private String getTemplateNo(String eId) throws Exception {
        //模板编号生成规则：KXZL+日期+四位流水号   比如KXZL202204240001
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String templateNo= "KXZL" + sDate;
        String sql=" select max(templateno) as templateno from dcp_dualplay_baseinfo"
                + " where eid='"+eId+"' and templateno like '%"+templateNo+"%'"
                + " ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        templateNo = getQData.get(0).get("TEMPLATENO").toString();
        if (Check.Null(templateNo)) {
            templateNo =  "KXZL" + sDate + "0001";
        }else{
            templateNo = templateNo.substring(4);
            long i = Long.parseLong(templateNo) + 1;
            templateNo = i + "";
            templateNo = "KXZL" + templateNo;
        }
        
        return templateNo;
    }
}
