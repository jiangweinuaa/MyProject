package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ToDoListQueryReq;
import com.dsc.spos.json.cust.res.DCP_ToDoListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_ToDoListQuery extends SPosBasicService<DCP_ToDoListQueryReq,DCP_ToDoListQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ToDoListQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ToDoListQueryReq> getRequestType() {
        return new TypeToken<DCP_ToDoListQueryReq>(){};
    }
    
    @Override
    protected DCP_ToDoListQueryRes getResponseType() {
        return new DCP_ToDoListQueryRes();
    }
    
    @Override
    protected DCP_ToDoListQueryRes processJson(DCP_ToDoListQueryReq req) throws Exception {
        String sql=null;
        DCP_ToDoListQueryRes res=this.getResponse();
        sql=this.getQuerySql(req);
        String[] condCountValues = { }; //查詢條件
        List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
        
        List<DCP_ToDoListQueryRes.level1Elm> oneLv1=new ArrayList<DCP_ToDoListQueryRes.level1Elm>();
        
        if (getQData_Count != null && getQData_Count.isEmpty() == false) {
            res.setDatas(new ArrayList<DCP_ToDoListQueryRes.level1Elm>());
            for (Map<String, Object> oneData : getQData_Count) {
                DCP_ToDoListQueryRes.level1Elm oneLv= res.new level1Elm();
                String proName=oneData.get("PRONAME").toString();
                String qty=oneData.get("QTY").toString();
                String status=oneData.get("STATUS").toString();
                
                oneLv.setProName(proName);
                oneLv.setQty(qty);
                oneLv.setStatus(status);
                
                res.getDatas().add(oneLv);
            }
        } else{
            res.setDatas(new ArrayList<DCP_ToDoListQueryRes.level1Elm>());
        }
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_ToDoListQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select proName,qty,status from "
                + "("
                + "select  proName,sum(qty) as qty,status from"
                + "(select 'p_order' as proName,count(*) as qty, '0' as status "
                + "from DCP_porder where status='0' "
                + "and EID='"+req.geteId()+"' "
                + "and SHOPID='"+req.getShopId()+"' "
                //配送收货
                + "union all "
                + "select 'delivery_receive' as proName,count(*) as qty, '6' as status  "
                + "from DCP_stockin where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type='0' "
                + "union all "
                + "select 'delivery_receive' as proName,count(*) as qty, '6' as  status  "
                + "from DCP_receiving a left join  DCP_stockin b on a.EID=b.EID and a.SHOPID=b.SHOPID and"
                + " b.ofno=a.receivingno  where a.status='6'  "
                + "and a.EID='"+req.geteId()+"'  "
                + "and a.SHOPID='"+req.getShopId()+"' and a.doc_type='0'  and b.stockinno is null "
                //调拨出库
                + "union all "
                + "select 'stock_out' as proName,count(*) as qty, '0' as status  "
                + "from DCP_stockout where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type='1' "
                //退货出库
                + "union all select 'back_out' as proName,count(*) as qty, '0' as status  "
                + "from DCP_stockout where status in ('0','2')  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type in ('2','0') "
                //调拨入库
                + "union all select 'stock_in' as proName,count(*) as qty, '6' as  status  "
                + "from DCP_receiving where status='6'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type='1' "
                + "union all select 'stock_in' as proName,count(*) as qty, '6' as status  "
                + "from DCP_stockin where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type='1' "
                //采购入库
                + "union all select 'sStock_in' as proName,count(*) as qty, '6' as  status  "
                + "from DCP_receiving where status='6'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type='2' "
                + "union all select 'sStock_in' as proName,count(*) as qty, '6' as status  "
                + "from DCP_stockin where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"' and doc_type in ('1','2') "
                //采购退货
                + "union all "
                + "select 'sStock_out' as proName,count(*) as qty, '0' as status  "
                + "from DCP_sstockout where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"'"
                //报损出库
                + "union all "
                + "select 'lStock_out' as proName,count(*) as qty, '0' as status  "
                + "from DCP_lstockout where status='0'  "
                + "and EID='"+req.geteId()+"'  "
                + "and SHOPID='"+req.getShopId()+"'"
                
                
                //促销通知  by jinzma 20220314
                + "union all "
                + " select 'prom-notice' as proname,count(distinct a.promno) qty,'0' as status from prom_object a"
                + " left  join prom_shop b on a.eid=b.eid and a.promno=b.promno and b.shopid='"+req.getShopId()+"' and a.shop_range='1'"
                + " left  join prom_shop c on a.eid=c.eid and a.promno=c.promno and c.shopid='"+req.getShopId()+"' and a.shop_range='2'"
                + " left  join prom_promotionnoticeshop d on a.eid=d.eid and a.promno=d.promno and d.shopid='"+req.getShopId()+"'"
                + " where a.eid='"+req.geteId()+"' and a.isvalid='Y' and a.endtime>=sysdate"
                + " and (a.shop_range='0' or b.shopid is not null) and c.shopid is null"
                + " and (d.status='0' or d.status is null) and (d.delflag='0' or d.delflag is null) "
                
                
                //
                + " ) a group by a.proName,a.status "
                + " ) b"
        );
        
        return sqlbuf.toString();
    }
    
}
