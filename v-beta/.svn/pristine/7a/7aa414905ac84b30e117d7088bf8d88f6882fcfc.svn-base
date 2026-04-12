package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustProcessReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustProcessRes;
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
 * 服务名称：DCP_SalePriceAdjustProcess
 * 服务说明：自建门店调价单确认(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustProcess extends SPosAdvanceService<DCP_SalePriceAdjustProcessReq, DCP_SalePriceAdjustProcessRes> {
    @Override
    protected void processDUID(DCP_SalePriceAdjustProcessReq req, DCP_SalePriceAdjustProcessRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String salePriceAdjustNo = req.getRequest().getSalePriceAdjustNo();
            int item=0;
            
            //门店零售价可用模板查询
            String templateId = "";
            String sql = " select a.templateid,max(b.item) as item from dcp_salepricetemplate a"
                    + " left join dcp_salepricetemplate_price b on a.eid=b.eid and a.templateid=b.templateid"
                    + " where a.eid='"+req.geteId()+"' and a.selfbuiltshopid='"+req.getShopId()+"'"
                    + " group by a.templateid";
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData !=null && !getQData.isEmpty()){
                templateId = getQData.get(0).get("TEMPLATEID").toString();
                if (!Check.Null(getQData.get(0).get("ITEM").toString())){
                    item = Integer.parseInt(getQData.get(0).get("ITEM").toString());
                }
            }
            
            //查调价单
            sql = " select a.type,b.item,b.pluno,b.unit,b.featureno,b.price,b.minprice,"
                    + " b.isdiscount,b.isprom,"
                    + " to_char(b.begindate,'YYYY-MM-DD') begindate,to_char(b.enddate,'YYYY-MM-DD') enddate,"
                    + " b.status"
                    + " from dcp_salepriceadjust a"
                    + " inner join dcp_salepriceadjust_detail b"
                    + " on a.eid=b.eid and a.shopid=b.shopid and a.salepriceadjustno=b.salepriceadjustno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.salepriceadjustno='"+salePriceAdjustNo+"' "
                    + " and a.status='0'"
                    + " ";
            
            getQData.clear();
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()){
                String lastModiBy = req.getOpNO();
                String lastModiByName = req.getOpName();
                String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String type = getQData.get(0).get("TYPE").toString(); //调价单类型： 0.修改 1.删除
                
                //新增调价单
                if (type.equals("0") && Check.Null(templateId)){
                    CreateTemplate(req,getQData);
                }
                //修改调价单,插入商品单身
                if (type.equals("0") && !Check.Null(templateId)){
                    InsertTemplateGoods(eId,templateId,getQData,item);
                }
                //删除调价单商品，零售价模板必须存在
                if (type.equals("1") && !Check.Null(templateId)){
                    DeleteTemplateGoods(eId,templateId,getQData);
                }
                
                UptBean ub = new UptBean("DCP_SALEPRICEADJUST");
                ub.addUpdateValue("LASTMODIOPID", new DataValue(lastModiBy, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME", new DataValue(lastModiByName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
                ub.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                
                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("SALEPRICEADJUSTNO", new DataValue(salePriceAdjustNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
                
                this.doExecuteDataToDB();
                
                if (!Check.Null(templateId)) {
                    //商品删除后，dcp_salepricetemplate_price有可能为空，为避免出现有单头没单身，删除整个模板
                    sql = " select templateid from dcp_salepricetemplate_price "
                            + " where eid='" + req.geteId() + "' and templateid='" + templateId + "' ";
                    getQData.clear();
                    getQData = this.doQueryData(sql, null);
                    if (getQData == null || getQData.isEmpty()) {
                        DeleteTemplate(req.geteId(),templateId);
                    }
                }
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新确认！");
            }
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceAdjustProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceAdjustProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceAdjustProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else {
            if (Check.Null(req.getRequest().getSalePriceAdjustNo())) {
                isFail = true;
                errMsg.append("salePriceAdjustNo不能为空,");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SalePriceAdjustProcessReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustProcessReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustProcessRes getResponseType() {
        return new DCP_SalePriceAdjustProcessRes() ;
    }
    
    private void CreateTemplate(DCP_SalePriceAdjustProcessReq req,List<Map<String,Object>> getQData) throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String templateId = "shop"+shopId+"0001";
        
        //插入DCP_SALEPRICETEMPLATE
        {
            String[] columns = {
                    "EID", "TEMPLATEID", "TEMPLATETYPE",
                    "RESTRICTCHANNEL", "STATUS",
                    "CREATEOPID", "CREATEOPNAME", "CREATETIME",
                    "REDISUPDATESUCCESS", "SELFBUILTSHOPID"
            };
            InsBean ib = new InsBean("DCP_SALEPRICETEMPLATE", columns);
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(templateId, Types.VARCHAR),
                    new DataValue("SHOP", Types.VARCHAR),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue("100", Types.VARCHAR),
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getOpName(), Types.VARCHAR),
                    new DataValue(lastModiTime, Types.DATE),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
            };
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }
        
        //插入DCP_SALEPRICETEMPLATE_LANG
        {
            String[] columns_lang = {
                    "EID", "TEMPLATEID", "LANG_TYPE", "TEMPLATENAME", "LASTMODITIME"
            };
            InsBean ib_lang = new InsBean("DCP_SALEPRICETEMPLATE_LANG", columns_lang);
            DataValue[] insValue_lang = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(templateId, Types.VARCHAR),
                    new DataValue("zh_CN", Types.VARCHAR),
                    new DataValue(req.getShopName() + "自建模板", Types.VARCHAR),
                    new DataValue(lastModiTime, Types.DATE),
            };
            ib_lang.addValues(insValue_lang);
            this.addProcessData(new DataProcessBean(ib_lang));
        }
        
        //插入DCP_SALEPRICETEMPLATE_RANGE
        {
            String[] columns_range = {
                    "EID", "TEMPLATEID", "RANGETYPE", "ID", "NAME", "LASTMODITIME"
            };
            InsBean ib_range = new InsBean("DCP_SALEPRICETEMPLATE_RANGE", columns_range);
            DataValue[] insValue_range = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(templateId, Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(req.getShopName(), Types.VARCHAR),
                    new DataValue(lastModiTime, Types.DATE),
            };
            ib_range.addValues(insValue_range);
            this.addProcessData(new DataProcessBean(ib_range));
        }
        
        //插入DCP_SALEPRICETEMPLATE_PRICE
        {
            String[] columns_price = {
                    "EID", "TEMPLATEID", "ITEM", "PLUNO", "FEATURENO", "UNIT",
                    "PRICE", "MINPRICE", "ISDISCOUNT", "ISPROM",
                    "BEGINDATE", "ENDDATE", "STATUS", "LASTMODITIME", "REDISUPDATESUCCESS"
            };
            InsBean ib_price = new InsBean("DCP_SALEPRICETEMPLATE_PRICE", columns_price);
            int item = 0;
            for (Map<String, Object> oneData : getQData) {
                item++;
                DataValue[] insValue_price = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateId, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("UNIT").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PRICE").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("MINPRICE").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("ISDISCOUNT").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("ISPROM").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("BEGINDATE").toString(), Types.DATE),
                        new DataValue(oneData.get("ENDDATE").toString(), Types.DATE),
                        new DataValue(oneData.get("STATUS").toString(), Types.VARCHAR),
                        new DataValue(lastModiTime, Types.DATE),
                        new DataValue("N", Types.VARCHAR),
                };
                ib_price.addValues(insValue_price);
            }
            this.addProcessData(new DataProcessBean(ib_price));
        }
    }
    
    private void InsertTemplateGoods(String eId,String templateId,List<Map<String,Object>> getQData,int item) throws Exception{
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String[] columns = {
                "EID","TEMPLATEID","ITEM","PLUNO","FEATURENO","UNIT",
                "PRICE","MINPRICE","ISDISCOUNT","ISPROM",
                "BEGINDATE","ENDDATE","STATUS","LASTMODITIME","REDISUPDATESUCCESS"
        };
        InsBean ib = new InsBean("DCP_SALEPRICETEMPLATE_PRICE", columns);
        for (Map<String, Object> oneData : getQData) {
            item++;
            DataValue[]	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(templateId, Types.VARCHAR),
                    new DataValue(item, Types.VARCHAR),
                    new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("UNIT").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("PRICE").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("MINPRICE").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("ISDISCOUNT").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("ISPROM").toString(), Types.VARCHAR),
                    new DataValue(oneData.get("BEGINDATE").toString(), Types.DATE),
                    new DataValue(oneData.get("ENDDATE").toString(), Types.DATE),
                    new DataValue(oneData.get("STATUS").toString(), Types.VARCHAR),
                    new DataValue(lastModiTime, Types.DATE),
                    new DataValue("N", Types.VARCHAR),
            };
            ib.addValues(insValue);
        }
        this.addProcessData(new DataProcessBean(ib));
        
    }
    
    private void DeleteTemplateGoods(String eId,String templateId,List<Map<String,Object>> getQData) throws Exception{
        for (Map<String, Object> oneData : getQData) {
            //删除DCP_SALEPRICETEMPLATE_PRICE
            DelBean db = new DelBean("DCP_SALEPRICETEMPLATE_PRICE");
            db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            db.addCondition("PLUNO", new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db));
        }
        
    }
    
    private void DeleteTemplate(String eId,String templateId) throws Exception{
        //删除dcp_salepricetemplate
        DelBean db1 = new DelBean("DCP_SALEPRICETEMPLATE");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //删除dcp_salepricetemplate_lang
        DelBean db2 = new DelBean("DCP_SALEPRICETEMPLATE_LANG");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));
        
        //删除dcp_salepricetemplate_range
        DelBean db3 = new DelBean("DCP_SALEPRICETEMPLATE_RANGE");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));
        
        //删除dcp_salepricetemplate_price
        DelBean db4 = new DelBean("DCP_SALEPRICETEMPLATE_PRICE");
        db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db4.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));
        
        this.doExecuteDataToDB();
        
    }
    
}
