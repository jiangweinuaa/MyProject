package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustQueryReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务名称：DCP_SalePriceAdjustQuery
 * 服务说明：自建门店调价查询(零售价)
 * @author jinzma
 * @since  2022-02-23
 */
public class DCP_SalePriceAdjustQuery extends SPosBasicService<DCP_SalePriceAdjustQueryReq, DCP_SalePriceAdjustQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getBeginDate())){
                isFail = true;
                errMsg.append("开始日期不能为空,");
            }
            if (Check.Null(req.getRequest().getEndDate())){
                isFail = true;
                errMsg.append("结束日期不能为空,");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SalePriceAdjustQueryReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustQueryReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustQueryRes getResponseType() {
        return new DCP_SalePriceAdjustQueryRes();
    }
    
    @Override
    protected DCP_SalePriceAdjustQueryRes processJson(DCP_SalePriceAdjustQueryReq req) throws Exception {
        try{
            DCP_SalePriceAdjustQueryRes res = this.getResponse();
            String templateId = "";
            //门店零售价可用模板查询
            String sql = " select templateid from dcp_salepricetemplate "
                    + " where eid='"+req.geteId()+"' and selfbuiltshopid='"+req.getShopId()+"' ";
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData !=null && !getQData.isEmpty()){
                templateId = getQData.get(0).get("TEMPLATEID").toString();
            }
            
            getQData.clear();
            sql = getQuerySql(req);
            getQData = this.doQueryData(sql,null);
            int totalRecords;	//总笔数
            int totalPages;		//总页数
            res.setDatas(new ArrayList<DCP_SalePriceAdjustQueryRes.level1Elm>());
            if (getQData!=null && !getQData.isEmpty()){
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                for (Map<String, Object> oneData : getQData) {
                    DCP_SalePriceAdjustQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setSalePriceAdjustNo(oneData.get("SALEPRICEADJUSTNO").toString());
                    oneLv1.setbDate(oneData.get("BDATE").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setType(oneData.get("TYPE").toString());
                    oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastModiTime(oneData.get("LASTMODITIME").toString());
                    res.getDatas().add(oneLv1);
                }
                
            }else{
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setTemplateId(templateId);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_SalePriceAdjustQueryReq req) throws Exception {
        StringBuffer sb = new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String status = req.getRequest().getStatus();
        String type = req.getRequest().getType();
        String keyTxt = req.getRequest().getKeyTxt();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        sb.append(""
                + " with saleprice as ("
                + " select a.salepriceadjustno from dcp_salepriceadjust a"
                + " left join dcp_salepriceadjust_detail b on a.eid=b.eid and a.shopid=b.shopid and a.salepriceadjustno=b.salepriceadjustno"
                + " left join dcp_goods_lang c on a.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.bdate>='"+beginDate+"' and a.bdate<='"+endDate+"'"
                + " ");
        if (!Check.Null(status)){
            sb.append(" and a.status='"+status+"'");
        }
        if (!Check.Null(type)){
            sb.append(" and a.type='"+type+"'");
        }
        if (!Check.Null(keyTxt)){
            sb.append(" and (a.salepriceadjustno like '%"+keyTxt+"%' or b.pluno like '%"+keyTxt+"%' or c.plu_name like '%"+keyTxt+"%')");
        }
        sb.append(" group by a.salepriceadjustno");
        sb.append(" )");
        
        sb.append(""
                + " select a.num,a.salepriceadjustno,a.status,a.bdate,a.type,"
                + " a.createopid,a.createopname,to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as createtime,"
                + " a.lastmodiopid,a.lastmodiopname,to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastmoditime"
                + " from("
                + " select count(*) over(partition by a.salepriceadjustno) num,"
                + " row_number() over (order by a.status asc,a.salepriceadjustno desc) rn,"
                + " a.* from dcp_salepriceadjust a"
                + " inner join saleprice on a.salepriceadjustno=saleprice.salepriceadjustno"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"'"
                + " )a"
                + " where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)
                + " ");
        
        return sb.toString();
        
    }
}
