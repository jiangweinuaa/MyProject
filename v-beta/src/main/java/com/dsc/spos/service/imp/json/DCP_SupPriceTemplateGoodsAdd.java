package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateGoodsAddReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateGoodsAdd extends SPosAdvanceService<DCP_SupPriceTemplateGoodsAddReq,DCP_SupPriceTemplateGoodsAddRes> {
    
    @Override
    protected void processDUID(DCP_SupPriceTemplateGoodsAddReq req, DCP_SupPriceTemplateGoodsAddRes res) throws Exception {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId=req.geteId();
        
        String templateId = req.getRequest().getTemplateId();
        List<DCP_SupPriceTemplateGoodsAddReq.TemplatePrice> pluList=req.getRequest().getPluList();
        
        ///处理单价和金额小数位数  BY JZMA
        String priceLength = PosPub.getPARA_SMS(dao, eId, "", "priceLength");
        if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)) {
            priceLength="2";
        }
        int priceLengthInt = Integer.parseInt(priceLength);
        
        String maxItemSql="select MAX(ITEM) MAXITEM from DCP_SUPPRICETEMPLATE_PRICE where eid ='"+eId+"' and templateid='"+templateId+"' ";
        List<Map<String , Object>> getDataMaxItem=this.doQueryData(maxItemSql, null);
        
        //【ID1026291】【荷家3.0】供货价模板添加商品报错如图 by jinzma 20220531
        //int maxItem=0;
        long maxItem=0;
        if (getDataMaxItem==null || getDataMaxItem.isEmpty()) {
            maxItem=0;
        } else {
            String sMax=getDataMaxItem.get(0).get("MAXITEM").toString();
            if (PosPub.isNumeric(sMax)==false) {
                maxItem=0;
            } else {
                maxItem=Long.parseLong(sMax);
            }
        }
        
        String[] columns_DCP_SUPPRICETEMPLATE_PRICE = {
                "EID","TEMPLATEID","ITEM","PLUNO","UNIT","FEATURENO",
                "PRICE","BEGINDATE",
                "ENDDATE","STATUS","LASTMODITIME"
        };
        for (DCP_SupPriceTemplateGoodsAddReq.TemplatePrice template : pluList) {
            
            //CATEGORY-商品分类；GOODS-商品；
            String sql;
            if (template.getType().equals("CATEGORY")) {
                //关联商品表将商品分类下属所有商品都添加进来
                sql = "select * from dcp_goods where eid='"+eId+"' and category ='" + template.getId() + "' ";
            } else {
                //关联商品表将商品分类下属所有商品都添加进来
                sql = "select * from dcp_goods where eid='"+eId+"' and pluno ='" + template.getId() + "' ";
            }
            List<Map<String , Object>> getData = this.doQueryData(sql, null);
            
            if (getData!=null && getData.isEmpty()==false) {
                for (Map<String,Object> map:getData) {
                    //+1
                    maxItem=maxItem+1;
                    
                    String price=map.get("SUPPRICE").toString();
                    
                    if (PosPub.isNumericType(price)==false) {
                        price="0";
                    }
                    DataValue[] insValue1 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(templateId, Types.VARCHAR),
                            new DataValue(String.valueOf(maxItem), Types.VARCHAR),
                            new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                            new DataValue(map.get("PLUTYPE").toString().equals("FEATURE")?"ALL":" ", Types.VARCHAR),
                            new DataValue(new BigDecimal(price).multiply(new BigDecimal(template.getDiscRate())).divide(new BigDecimal(100),priceLengthInt, RoundingMode.HALF_UP), Types.DECIMAL),
                            new DataValue(template.getBeginDate(), Types.DATE),
                            new DataValue(template.getEndDate(), Types.DATE),
                            new DataValue(100, Types.INTEGER),
                            new DataValue(lastmoditime, Types.DATE)
                    };
                    
                    InsBean ib1 = new InsBean("DCP_SUPPRICETEMPLATE_PRICE", columns_DCP_SUPPRICETEMPLATE_PRICE);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1)); // 新增
                }
            }
            
        }
        
        //
        this.doExecuteDataToDB();
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SupPriceTemplateGoodsAddReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        String templateId = req.getRequest().getTemplateId();
        List<DCP_SupPriceTemplateGoodsAddReq.TemplatePrice> pluList=req.getRequest().getPluList();
        
        if(Check.Null(templateId))
        {
            errMsg.append("模板编码不能为空值 ");
            isFail = true;
        }
        if(pluList==null || pluList.size()==0)
        {
            errMsg.append("商品信息不能为空值 ");
            isFail = true;
        }
        
        for (DCP_SupPriceTemplateGoodsAddReq.TemplatePrice template : pluList)
        {
            if(Check.Null(template.getType()))
            {
                errMsg.append("商品信息类型不能为空值 ");
                isFail = true;
            }
            if(Check.Null(template.getId()))
            {
                errMsg.append("商品信息编码不能为空值 ");
                isFail = true;
            }
            if(Check.Null(template.getDiscRate()))
            {
                errMsg.append("商品信息售价折扣率不能为空值 ");
                isFail = true;
            }
            
            if (PosPub.isNumeric(template.getDiscRate())==false)
            {
                errMsg.append("商品信息售价折扣率必须是整数 ");
                isFail = true;
            }
            
            
            if(Check.Null(template.getBeginDate()))
            {
                errMsg.append("商品信息生效日期不能为空值 ");
                isFail = true;
            }
            
            if(Check.Null(template.getEndDate()))
            {
                errMsg.append("商品信息失效日期不能为空值 ");
                isFail = true;
            }
        }
        
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_SupPriceTemplateGoodsAddReq> getRequestType() {
        return new TypeToken<DCP_SupPriceTemplateGoodsAddReq>() {};
    }
    
    @Override
    protected DCP_SupPriceTemplateGoodsAddRes getResponseType() {
        return new DCP_SupPriceTemplateGoodsAddRes();
    }
    
}
