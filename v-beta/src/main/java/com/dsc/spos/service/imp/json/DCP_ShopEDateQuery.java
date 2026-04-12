package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ShopEDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopEDateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：ShopEDateGetDCP
 * 服务说明：日结查询DCP
 * @author jinzma
 * @since  2019-05-10
 */
public class DCP_ShopEDateQuery extends SPosBasicService < DCP_ShopEDateQueryReq,DCP_ShopEDateQueryRes>  {
    
    @Override
    protected boolean isVerifyFail(DCP_ShopEDateQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ShopEDateQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopEDateQueryReq>(){};
    }
    
    @Override
    protected DCP_ShopEDateQueryRes getResponseType() {
        return new DCP_ShopEDateQueryRes();
    }
    
    @Override
    protected DCP_ShopEDateQueryRes processJson(DCP_ShopEDateQueryReq req) throws Exception {
        DCP_ShopEDateQueryRes res = this.getResponse();
        try {
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                res.setDatas(new ArrayList<>());
                for (Map<String, Object> oneData : getQData) {
                    DCP_ShopEDateQueryRes.level1Elm oneLv1 = new DCP_ShopEDateQueryRes().new level1Elm();
                    String shopId = oneData.get("ORGANIZATIONNO").toString();
                    String shopName = oneData.get("ORG_NAME").toString();
                    String phone = oneData.get("PHONE").toString();
                    String address = oneData.get("ADDRESS").toString();
                    String eDate = oneData.get("MAXEDATE").toString();
                    
                    //设置响应
                    oneLv1.setShopId(shopId);
                    oneLv1.setShopName(shopName);
                    oneLv1.setPhone(phone);
                    oneLv1.setAddress(address);
                    oneLv1.seteDate(eDate);
                    
                    res.getDatas().add(oneLv1);
                }
            }
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        return res;
        
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {	}
    
    @Override
    protected String getQuerySql(DCP_ShopEDateQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String langType = req.getLangType();
        
        sqlbuf.append(" "
                + " select a.eid,a.organizationno,b.org_name,a.phone,a.address,c.maxEDate  "
                + " from DCP_ORG a  "
                + " left join DCP_ORG_lang b on a.EID=b.EID and a.organizationno=b.organizationno and b.status='100' "
                + " and b.lang_type='"+langType+"' "
                + " left join (select EID,OrganizationNO,Max(EDate) as maxEDate "
                + " from DCP_stock_day  "
                + " where EID='"+eId+"' group by EID,OrganizationNO) c "
                + " on a.EID=c.EID and  a.organizationno=c.organizationno "
                + " where a.status='100' and a.eid='"+eId+"' and a.org_form='2'" );
        
        if (keyTxt != null && keyTxt.length()>0) {
            sqlbuf.append(" and (a.OrganizationNO like '%%"+ keyTxt +"%%' or b.org_name like '%%"+ keyTxt +"%%' )  ");
        }
        sqlbuf.append( "  order by c.maxEDate,a.organizationno  " );
        return sqlbuf.toString();
        
    }
    
    
}
