package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CloseCheckBillTypeSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_CloseCheckBillTypeSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @apiNote 闭店检查单据类型设置查询
 * @since 2021-05-18
 * @author jinzma
 */
public class DCP_CloseCheckBillTypeSetQuery extends SPosBasicService<DCP_CloseCheckBillTypeSetQueryReq, DCP_CloseCheckBillTypeSetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CloseCheckBillTypeSetQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_CloseCheckBillTypeSetQueryReq> getRequestType() {
        return new TypeToken<DCP_CloseCheckBillTypeSetQueryReq>(){};
    }
    
    @Override
    protected DCP_CloseCheckBillTypeSetQueryRes getResponseType() {
        return new DCP_CloseCheckBillTypeSetQueryRes();
    }
    
    @Override
    protected DCP_CloseCheckBillTypeSetQueryRes processJson(DCP_CloseCheckBillTypeSetQueryReq req) throws Exception {
        DCP_CloseCheckBillTypeSetQueryRes res = this.getResponse();
        try {
            String eId = req.geteId();
            String langType = req.getLangType();
            ///单据资料缺省检查和预置
            checkDefaultBill(eId,langType);
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            res.setDatas(new ArrayList<DCP_CloseCheckBillTypeSetQueryRes.level1Elm>());
            
            if (getQData != null && getQData.isEmpty() == false) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("BILLTYPE", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData,condition);
                for (Map<String, Object> oneData : getQHeader) {
                    DCP_CloseCheckBillTypeSetQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    String billType = oneData.get("BILLTYPE").toString();
                    String billTypeName = oneData.get("BILLTYPENAME").toString();
                    String status = oneData.get("STATUS").toString();
                    String restrictShop = oneData.get("RESTRICTSHOP").toString();
                    String lastModiOpId = oneData.get("LASTMODIOPID").toString();
                    String lastModiOpName = oneData.get("LASTMODIOPNAME").toString();
                    String lastModiTime = oneData.get("LASTMODITIMESTRING").toString();
                    
                    oneLv1.setShopList(new ArrayList<DCP_CloseCheckBillTypeSetQueryRes.level2Elm>());
                    if (restrictShop.equals("1")){
                        for (Map<String, Object> oneDataDetail : getQData) {
                            String shopId = oneDataDetail.get("SHOPID").toString();
                            String shopName = oneDataDetail.get("SHOPNAME").toString();
                            if(!Check.Null(shopId) && billType.equals(oneDataDetail.get("BILLTYPE").toString()))
                            {
                                DCP_CloseCheckBillTypeSetQueryRes.level2Elm oneLv2 = res.new level2Elm();
                                oneLv2.setShopId(shopId);
                                oneLv2.setShopName(shopName);
                                oneLv1.getShopList().add(oneLv2);
                            }
                        }
                    }
                    oneLv1.setBillType(billType);
                    oneLv1.setBillTypeName(billTypeName);
                    oneLv1.setStatus(status);
                    oneLv1.setRestrictShop(restrictShop);
                    oneLv1.setLastModiOpId(lastModiOpId);
                    oneLv1.setLastModiOpName(lastModiOpName);
                    oneLv1.setLastModiTime(lastModiTime);
                    
                    res.getDatas().add(oneLv1);
                }
            }
            else {
                totalRecords = 0;
                totalPages = 0;
            }
            
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
    protected String getQuerySql(DCP_CloseCheckBillTypeSetQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        String status = "";
        String searchString = "";
        
        if (req.getRequest()!=null) {
            status = req.getRequest().getStatus();
            searchString = req.getRequest().getSearchString();
        }
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        sqlbuf.append(""
                + " select a.*,b.shopid,c.org_name as shopName,"
                + " to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastModiTimeString "
                + " from ("
                + " select count(*) over() num,row_number() over (order by a.billtype) rn,a.* "
                + " from dcp_closebilltype a"
                + " where a.eid='"+eId+"' " //and (a.restrictshop='0' or (a.restrictshop='1' and b.shopid is not null))"
                + " ");
        
        if (!Check.Null(status)) {
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if (!Check.Null(searchString)) {
            sqlbuf.append(" and (a.billtype like '%%"+searchString+"%%' or a.billtypename like '%%"+searchString+"%%')");
        }
        sqlbuf.append(""
                + " )a"
                + " left join dcp_closebilltype_pickshop b on a.eid=b.eid and a.billtype=b.billtype"
                + " left join dcp_org_lang c on a.eid=c.eid and b.shopid=c.organizationno and c.lang_type='"+langType+"'"
                + " where rn>"+startRow+" and rn<="+(startRow+pageSize)+" order by a.billtype" );
        
        return sqlbuf.toString();
        
    }
    
    private void checkDefaultBill(String eId,String langType) throws Exception{
        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
			/*单据类型说明
		      02配送收货\03退货出库\11移仓出库\06其他入库\07其他出库\05调拨出库\
		      04调拨收货\08采购入库\09采购退货\16报损单\12盘点单\
		      13完工入库\14组合单\15拆解单\18转换单
			 */
            Map<String, String>billMap = new HashMap<String, String>();
            if (langType.equals("zh_TW")) {
                billMap.put("02", "配送收貨");
                billMap.put("03", "退貨出庫");
                billMap.put("11", "移倉出庫");
                billMap.put("06", "其他入庫");
                billMap.put("07", "其他出庫");
                billMap.put("05", "調撥出庫");
                billMap.put("04", "調撥收貨");
                billMap.put("08", "采購入庫");
                billMap.put("09", "采購退貨");
                billMap.put("16", "報損單");
                billMap.put("12", "盤點單");
                billMap.put("13", "完工入庫");
                billMap.put("14", "組合單");
                billMap.put("15", "拆解單");
                billMap.put("18", "轉換單");
                billMap.put("19", "訂單");
                
            }else{
                billMap.put("02", "配送收货");
                billMap.put("03", "退货出库");
                billMap.put("11", "移仓出库");
                billMap.put("06", "其他入库");
                billMap.put("07", "其他出库");
                billMap.put("05", "调拨出库");
                billMap.put("04", "调拨收货");
                billMap.put("08", "采购入库");
                billMap.put("09", "采购退货");
                billMap.put("16", "报损单");
                billMap.put("12", "盘点单");
                billMap.put("13", "完工入库");
                billMap.put("14", "组合单");
                billMap.put("15", "拆解单");
                billMap.put("18", "转换单");
                billMap.put("19", "订单");
            }
            
            String sql=" select * from dcp_closebilltype where eid='"+eId+"' ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            for (Map<String, Object> par:getQData) {
                String billType = par.get("BILLTYPE").toString();
                if (!Check.Null(billType) && billMap.containsKey(billType) ) {
                    String billTypeName =billMap.get(billType);
                    UptBean ub = new UptBean("DCP_CLOSEBILLTYPE");
                    ub.addUpdateValue("BILLTYPENAME", new DataValue(billTypeName, Types.VARCHAR));
                    
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR) );
                    ub.addCondition("BILLTYPE", new DataValue(billType, Types.VARCHAR));
                    
                    List<DataProcessBean> datas = new ArrayList<DataProcessBean>();
                    datas.add(new DataProcessBean(ub));
                    this.dao.useTransactionProcessData(datas);
                    
                    billMap.remove(billType);
                }
            }
            
            if (!billMap.isEmpty()){
                for(Map.Entry<String,String>entry : billMap.entrySet()){
                    String billType = entry.getKey();
                    String billTypeName = entry.getValue();
                    String[] columns ={"EID","BILLTYPE","BILLTYPENAME","RESTRICTSHOP","STATUS",
                            "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
                    DataValue[] insValue = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(billType, Types.VARCHAR),
                                    new DataValue(billTypeName, Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.DATE)
                            };
                    
                    InsBean ib = new InsBean("DCP_CLOSEBILLTYPE", columns);
                    ib.addValues(insValue);
                    List<DataProcessBean> datas = new ArrayList<DataProcessBean>();
                    datas.add(new DataProcessBean(ib));
                    this.dao.useTransactionProcessData(datas);
                }
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    
    
}
