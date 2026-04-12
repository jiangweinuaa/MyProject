package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockOutEntryQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryQueryRes;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryQueryRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_StockOutEntryQuery
 * 服务说明：退货录入查询
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryQuery extends SPosBasicService<DCP_StockOutEntryQueryReq, DCP_StockOutEntryQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockOutEntryQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutEntryQueryReq.levelElm request = req.getRequest();
        if (request==null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else{
            if (Check.Null(request.getBeginDate())){
                errMsg.append("开始日期不可为空值, ");
                isFail = true;
            }
            if (Check.Null(request.getEndDate())){
                errMsg.append("结束日期不可为空值, ");
                isFail = true;
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockOutEntryQueryReq> getRequestType() {
        return new TypeToken<DCP_StockOutEntryQueryReq>(){};
    }
    
    @Override
    protected DCP_StockOutEntryQueryRes getResponseType() {
        return new DCP_StockOutEntryQueryRes();
    }
    
    @Override
    protected DCP_StockOutEntryQueryRes processJson(DCP_StockOutEntryQueryReq req) throws Exception {
        try{
            DCP_StockOutEntryQueryRes res = this.getResponse();
            int totalRecords = 0;								//总笔数
            int totalPages = 0;									//总页数
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setEntrylist(new ArrayList<>());
            if (!CollectionUtils.isEmpty(getQData)){
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                for (Map<String, Object> oneData : getQData) {
                    level2Elm oneLv2 = res.new level2Elm();
                    oneLv2.setStockOutEntryNo(oneData.get("STOCKOUTENTRYNO").toString());
                    oneLv2.setBDate(oneData.get("BDATE").toString());
                    oneLv2.setMemo(oneData.get("MEMO").toString());
                    oneLv2.setStatus(oneData.get("STATUS").toString());
                    oneLv2.setDeliveryNo(oneData.get("DELIVERY_NO").toString());
                    oneLv2.setBsNo(oneData.get("BSNO").toString());
                    oneLv2.setBsName(oneData.get("REASON_NAME").toString());
                    oneLv2.setWarehouse(oneData.get("WAREHOUSE").toString());
                    oneLv2.setWarehouseName(oneData.get("WAREHOUSE_NAME").toString());
                    oneLv2.setTotCqty(oneData.get("TOT_CQTY").toString());
                    oneLv2.setTotPqty(oneData.get("TOT_PQTY").toString());
                    oneLv2.setTotAmt(oneData.get("TOT_AMT").toString());
                    oneLv2.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
                    oneLv2.setCreateBy(oneData.get("CREATEBY").toString());
                    oneLv2.setCreateByName(oneData.get("CREATEBYNAME").toString());
                    oneLv2.setCreateDate(oneData.get("CREATE_TIME").toString());
                    oneLv2.setModifyBy(oneData.get("MODIFYBY").toString());
                    oneLv2.setModifyByName(oneData.get("MODIFYBYNAME").toString());
                    oneLv2.setModifyDate(oneData.get("MODIFY_TIME").toString());
                    oneLv2.setConfirmBy(oneData.get("CONFIRMBY").toString());
                    oneLv2.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
                    oneLv2.setConfirmDate(oneData.get("CONFIRM_TIME").toString());
                    
                    oneLv1.getEntrylist().add(oneLv2);
                    
                }
                
                
            }
            
            res.setDatas(oneLv1);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_StockOutEntryQueryReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;
        
        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.status asc,a.bdate desc,a.stockoutentryno desc) rn,"
                + " a.*,b.warehouse_name,c.reason_name,"
                + " to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as create_time,"
                + " to_char(a.modifytime,'YYYY-MM-DD hh24:mi:ss') as modify_time,"
                + " to_char(a.confirmtime,'YYYY-MM-DD hh24:mi:ss') as confirm_time"
                + " from dcp_stockout_entry a"
                + " left join dcp_warehouse_lang b on a.eid=b.eid and a.shopid=b.organizationno and a.warehouse=b.warehouse and b.lang_type='"+langType+"'"
                + " left join dcp_reason_lang c on a.eid=c.eid and a.bsno=c.bsno and c.bstype='2' and c.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.bdate>='"+beginDate+"' and a.bdate<='"+endDate+"'"
                + " "
                + " ");
        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"'");
        }
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.stockoutentryno like '%"+keyTxt+"%' or a.delivery_no like '%"+keyTxt+"%' or a.memo like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize) );
        sqlbuf.append(" order by rn");
        
        return sqlbuf.toString();
        
    }
}
