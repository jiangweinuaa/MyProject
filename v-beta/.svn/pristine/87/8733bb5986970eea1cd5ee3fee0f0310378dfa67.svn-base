package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class DCP_ShopQuery extends SPosAdvanceService<DCP_ShopQueryReq, DCP_ShopQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShopQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ShopQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopQueryReq>(){};
    }
    
    @Override
    protected DCP_ShopQueryRes getResponseType() {
        return new DCP_ShopQueryRes();
    }
    
    @Override
    protected void processDUID(DCP_ShopQueryReq req, DCP_ShopQueryRes res) throws Exception {
        
        //单头总数
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        int totalRecords; // 总笔数
        int totalPages;
        res.setDatas(new ArrayList<>());
        if(getQData!=null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            // 算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            
            for (Map<String, Object> oneData1 : getQData) {
                DCP_ShopQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.setShopId(oneData1.get("SHOPID").toString());
                lv1.setShopName(oneData1.get("SHOPNAME").toString());
                lv1.setAddress(oneData1.get("ADDRESS").toString());
                lv1.setPhone(oneData1.get("PHONE").toString());
                lv1.setIn_cost_warehouse(oneData1.get("IN_COST_WAREHOUSE").toString());
                lv1.setIn_non_cost_warehouse(oneData1.get("IN_NON_COST_WAREHOUSE").toString());
                lv1.setInv_cost_warehouse(oneData1.get("INV_COST_WAREHOUSE").toString());
                lv1.setInv_non_cost_warehouse(oneData1.get("INV_NON_COST_WAREHOUSE").toString());
                lv1.setOut_cost_warehouse(oneData1.get("OUT_COST_WAREHOUSE").toString());
                lv1.setOut_non_cost_warehouse(oneData1.get("OUT_NON_COST_WAREHOUSE").toString());
                
                res.getDatas().add(lv1);
            }
        } else {
            totalRecords = 0;
            totalPages = 0;
        }
        
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected String getQuerySql(DCP_ShopQueryReq req) throws Exception {
        String sql;
        String eId= req.geteId();
        String shopId = req.getShopId();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String range = req.getRequest().getRange();
        String getType = req.getRequest().getGetType();  /// 2.调拨入门店查询
        boolean isShopGroup = false;
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        
        if (!Check.Null(getType) && getType.equals("2")) {
            sql = " select * from dcp_shopgroup where eid='"+eId+"' and status='100' and shopgrouptype='1' ";
            List<Map<String, Object>> getQData_ShopGroupCount = this.doQueryData(sql, null);
            if(getQData_ShopGroupCount != null && !getQData_ShopGroupCount.isEmpty()) {
                isShopGroup=true;			//门店群组有设置友好门店
            }
        }
        
        
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" "
                + " Select * From ("
                + " ");
        
        
        if(Check.Null(range)||range.equals("0")) {
            sqlbuf.append("SELECT count(*) over() as num,ROWNUM rn,A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.ADDRESS,A.PHONE,A.IN_COST_WAREHOUSE,A.IN_NON_COST_WAREHOUSE,A.INV_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE FROM DCP_ORG A  "
                    +"LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' ");
            if (isShopGroup) {
                sqlbuf.append(""
                        + " inner join ("
                        + " select distinct shopid from dcp_shopgroup a"
                        + " where a.eid='"+eId+"' and a.status='100' and a.shopid<>'"+shopId+"' and nvl(shopgrouptype,'1')='1'"
                        + " and a.shopgroupno in"
                        + " ("
                        + " select shopgroupno from dcp_shopgroup"
                        + " where eid='"+eId+"' and  shopid='"+shopId+"' and status='100' and nvl(shopgrouptype,'1')='1'"
                        + " )) ShopGroup on ShopGroup.shopid =a.ORGANIZATIONNO  "
                        + "");
                
            }
            
            sqlbuf.append(" WHERE A.EID='"+req.geteId()+"' " );
            
            if (status != null && status.length()>0) {
                sqlbuf.append( " AND A.status= "+status+" and B.status="+status+" ");
            }
            if (!Check.Null(getType) && getType.equals("2")) {  /// 2.调拨入门店查询
                sqlbuf.append( " and a.org_form='2' and a.organizationno<>'"+shopId+"' ");
            }
            
        } else if( range.equals("1") ) {
            sqlbuf.append("SELECT count(*) over() as num,ROWNUM rn,A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.ADDRESS,A.PHONE,A.IN_COST_WAREHOUSE,A.IN_NON_COST_WAREHOUSE,A.INV_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE FROM DCP_ORG A  "
                    +"LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' "
                    +"WHERE A.EID='"+req.geteId()+"' AND A.BELFIRM ='" +req.getBELFIRM() +"' ");
            
            if (status != null && status.length()>0) {
                sqlbuf.append( " AND A.status= "+status+" and B.status="+status+" ");
            }
            if (!Check.Null(getType) && getType.equals("2")){  /// 2.调拨入门店查询
                sqlbuf.append( " and a.org_form='2' and a.organizationno<>'"+shopId+"' ");
            }
        } else if( range.equals("2") || range.equals("3")) {  // 和DCP_CityShopQuery保持一致，作废掉原来的2，改为3  by jinzma 20220720
            sqlbuf.append("SELECT count(*) over() as num,ROWNUM rn,A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.ADDRESS,A.PHONE,A.IN_COST_WAREHOUSE,A.IN_NON_COST_WAREHOUSE,A.INV_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE FROM DCP_ORG A  "
                    + " LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' "
                    + " INNER JOIN PLATFORM_STAFFS_SHOP C on a.EID = c.EID and a.ORGANIZATIONNO = c.SHOPID "
                    + " WHERE A.EID='"+req.geteId()+"' "
                    + " and c.opNO = '"+req.getOpNO()+"'  ");
            
            if (status != null && status.length()>0) {
                sqlbuf.append( " AND A.status= "+status+" and B.status="+status+" and C.status="+status+" ");
            }
            if (!Check.Null(getType) && getType.equals("2")) {  /// 2.调拨入门店查询
                sqlbuf.append( " and a.org_form='2' and a.organizationno<>'"+shopId+"' ");
            }
        }
        
        if (keyTxt != null && keyTxt.length()>0) {
            sqlbuf.append( " AND (A.ORGANIZATIONNO like '%%"+keyTxt+"%%' or B.ORG_NAME like '%%"+keyTxt+"%%'  ) ");
        }
        
        
        sqlbuf.append("ORDER BY A.ORGANIZATIONNO ");
        
        sqlbuf.append(")tbl where rn > " + startRow + " AND rn <= " + (startRow+pageSize) );
        
        return sqlbuf.toString();
    }
    
    
    
}
