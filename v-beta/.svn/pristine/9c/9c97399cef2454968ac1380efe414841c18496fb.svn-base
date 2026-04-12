package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateGoodsUpdate extends SPosAdvanceService<DCP_SupPriceTemplateGoodsUpdateReq,DCP_SupPriceTemplateGoodsUpdateRes> {
    
    @Override
    protected void processDUID(DCP_SupPriceTemplateGoodsUpdateReq req, DCP_SupPriceTemplateGoodsUpdateRes res) throws Exception {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId=req.geteId();
        
        String templateId = req.getRequest().getTemplateId();
        List<DCP_SupPriceTemplateGoodsUpdateReq.TemplatePrice> pluList=req.getRequest().getPluList();
        
        String sql = "select templateid from DCP_SUPPRICETEMPLATE_PRICE where eid='"+eId+"' and templateid='"+templateId+"' ";
        List<Map<String , Object>> getData=this.doQueryData(sql, null);
        if (getData==null || getData.isEmpty()) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("此模板编码不存在！");
            return;
        }
        
        
        if (pluList.size()==0) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("商品信息列表必须有值！");
            return;
        }
        
        for (DCP_SupPriceTemplateGoodsUpdateReq.TemplatePrice template : pluList) {
            UptBean ub1 = new UptBean("DCP_SUPPRICETEMPLATE_PRICE");
            ub1.addUpdateValue("UNIT", new DataValue(template.getUnit(),Types.VARCHAR));
            ub1.addUpdateValue("FEATURENO", new DataValue(template.getFeatureNo(),Types.VARCHAR));
            ub1.addUpdateValue("PRICE", new DataValue(template.getPrice(),Types.DECIMAL));
            ub1.addUpdateValue("BEGINDATE", new DataValue(template.getBeginDate(),Types.DATE));
            ub1.addUpdateValue("ENDDATE", new DataValue(template.getEndDate(),Types.DATE));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
            
            
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            ub1.addCondition("ITEM", new DataValue(template.getItem(), Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(template.getPluNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            
        }
        
        this.doExecuteDataToDB();
        
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateGoodsUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SupPriceTemplateGoodsUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        String templateId = req.getRequest().getTemplateId();
        List<DCP_SupPriceTemplateGoodsUpdateReq.TemplatePrice> pluList=req.getRequest().getPluList();
        
        if(Check.Null(templateId)) {
            errMsg.append("模板编码不能为空值 ");
            isFail = true;
        }
        if(pluList==null || pluList.size()==0) {
            errMsg.append("商品信息不能为空值 ");
            isFail = true;
        }
        
        for (DCP_SupPriceTemplateGoodsUpdateReq.TemplatePrice template : pluList) {
            if(Check.Null(template.getItem())) {
                errMsg.append("商品信息项次不能为空值 ");
                isFail = true;
            }
            if(Check.Null(template.getPluNo())) {
                errMsg.append("商品信息编码不能为空值 ");
                isFail = true;
            }
            if(Check.Null(template.getPluName())) {
                errMsg.append("商品信息名称不能为空值 ");
                isFail = true;
            }
            
            if(Check.Null(template.getUnit())) {
                errMsg.append("商品信息单位编码不能为空值 ");
                isFail = true;
            }
            
            if(Check.Null(template.getUnitName())) {
                errMsg.append("商品信息单位名称不能为空值 ");
                isFail = true;
            }
            
            if (Check.Null(template.getFeatureNo())) {
                errMsg.append("商品信息特征码不能为空 ");
                isFail = true;
            }
            
            if(Check.Null(template.getPrice())) {
                errMsg.append("商品信息售价不能为空值 ");
                isFail = true;
            }
            
            if (!PosPub.isNumericType(template.getPrice())) {
                errMsg.append("商品信息售价必须是数字类型 ");
                isFail = true;
            }
            
            if(Check.Null(template.getBeginDate())) {
                errMsg.append("商品信息生效日期不能为空值 ");
                isFail = true;
            }
            
            if(Check.Null(template.getEndDate())) {
                errMsg.append("商品信息失效日期不能为空值 ");
                isFail = true;
            }
            
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_SupPriceTemplateGoodsUpdateReq> getRequestType() {
        return new TypeToken<DCP_SupPriceTemplateGoodsUpdateReq>() {};
    }
    
    @Override
    protected DCP_SupPriceTemplateGoodsUpdateRes getResponseType() {
        return new DCP_SupPriceTemplateGoodsUpdateRes();
    }
    
}
