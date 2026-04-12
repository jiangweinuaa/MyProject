package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsShelfChannelQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfChannelQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsShelfChannelQuery extends SPosBasicService<DCP_GoodsShelfChannelQueryReq, DCP_GoodsShelfChannelQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsShelfChannelQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null) {
            errMsg.append("requset不能为空值 ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_GoodsShelfChannelQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsShelfChannelQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsShelfChannelQueryRes getResponseType() {
        return new DCP_GoodsShelfChannelQueryRes();
    }
    
    @Override
    protected DCP_GoodsShelfChannelQueryRes processJson(DCP_GoodsShelfChannelQueryReq req) throws Exception {
        //查詢資料
        DCP_GoodsShelfChannelQueryRes res = this.getResponse();
        //单头总数
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        
        res.setDatas(new ArrayList<>());
        if (getQData != null && !getQData.isEmpty()) {
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("CHANNELID", true);
            //调用过滤函数
            List<Map<String, Object>> getHead=MapDistinct.getMap(getQData, condition);
            
            condition.clear();
//			condition.put("CHANNELID", true);
//			condition.put("SHOPID", true);
//		  调用过滤函数
//			List<Map<String, Object>> getShopList=MapDistinct.getMap(getQData, condition);
            
            for (Map<String, Object> oneData : getHead)
            {
                DCP_GoodsShelfChannelQueryRes.level1Elm oneLv1 = res.new level1Elm();
                String channelId = oneData.get("CHANNELID").toString();
                String status_channel = oneData.get("STATUS_CHANNEL").toString();
                String onShelfAuto = Check.Null(oneData.get("ONSHELFAUTO").toString() ) ? "0" : oneData.get("ONSHELFAUTO").toString();
                String onShelfDate = oneData.get("ONSHELFDATE").toString();
                String onShelfTime = oneData.get("ONSHELFTIME").toString();
                //OFFSHELFAUTO
                String offShelfAuto = Check.Null(oneData.get("OFFSHELFAUTO").toString() ) ? "0" : oneData.get("OFFSHELFAUTO").toString();
                String offShelfDate = oneData.get("OFFSHELFDATE").toString();
                String offShelfTime = oneData.get("OFFSHELFTIME").toString();
                String restrictShop = oneData.get("RESTRICTSHOP").toString();
                
                oneLv1.setChannelId(channelId);
                oneLv1.setChannelName(oneData.get("CHANNELNAME").toString());
                oneLv1.setStatus(status_channel);
                
                oneLv1.setOnShelfAuto(onShelfAuto);
                oneLv1.setOnShelfDate(onShelfDate);
                oneLv1.setOnShelfTime(onShelfTime);
                oneLv1.setOffShelfAuto(offShelfAuto);
                oneLv1.setOffShelfDate(offShelfDate);
                oneLv1.setOffShelfTime(offShelfTime);
                oneLv1.setRestrictShop(restrictShop);

//				oneLv1.setShopList(new ArrayList<DCP_GoodsShelfChannelQueryRes.shopInfo>());
//				for (Map<String, Object> map : getShopList) 
//				{
//					String channelId_detail = map.get("CHANNELID").toString();
//					String shopId = map.get("SHOPID").toString();
//					if(shopId==null||shopId.isEmpty())
//					{
//						continue;
//					}
//					if(channelId_detail==null||channelId_detail.isEmpty())
//					{
//						continue;
//					}
//					if(channelId.equals(channelId_detail))
//					{
//						DCP_GoodsShelfChannelQueryRes.shopInfo oneLv2 = res.new shopInfo();
//						oneLv2.setShopId(shopId);
//						oneLv2.setShopName(map.get("SHOPNAME").toString());
//						oneLv2.setStatus(map.get("STATUS_SHOP").toString());
//						oneLv1.getShopList().add(oneLv2);
//						
//					}
//										
//		
//				}									
                
                
                res.getDatas().add(oneLv1);
                
            }
            
        }
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsShelfChannelQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String pluNo = req.getRequest().getPluNo();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf = new StringBuffer();
        if(pluNo!=null&& !pluNo.isEmpty())
        {
            sqlbuf.append(" select * from ( ");
            sqlbuf.append(" select A.CHANNELID,A.CHANNELNAME,NVL(R1.STATUS,'0') AS STATUS_CHANNEL , A.restrictShop , "
                    + " R3.onShelfAuto , R3.onShelfDate , R3.onShelfTime ,R3.offShelfAuto , R3.offShelfDate , R3.offShelfTime ");
            sqlbuf.append(" from CRM_CHANNEL A ");
            sqlbuf.append(" INNER join PLATFORM_APP P on a.APPNO=P.APPNO and P.ISONLINE='Y' AND P.ISTHIRD='N' and P.status = '100' ");

//			sqlbuf.append(" left join CRM_CHANNELSHOP B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID ");
//			sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND B.SHOPID=C.ORGANIZATIONNO AND C.LANG_TYPE='"+langType+"' ");
            sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R1 on A.EID=R1.EID AND A.CHANNELID=R1.CHANNELID AND R1.SHOPID='ALL' AND R1.PLUNO='"+pluNo+"' ");
//			sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 on A.EID=R2.EID AND B.CHANNELID=R2.CHANNELID AND B.SHOPID = R2.SHOPID AND R2.PLUNO='"+pluNo+"' ");
            sqlbuf.append(" left join DCP_GOODS_SHELF_DATE R3 on R1.EID=R3.EID AND R1.CHANNELID=R3.CHANNELID AND R1.PLUNO = R3.PLUNO  AND R3.PLUNO='"+pluNo+"' ");
            
            sqlbuf.append(" where A.EID='"+eId+"' ");
            if(keyTxt!=null && !keyTxt.isEmpty())
            {
                sqlbuf.append(" AND (A.CHANNELID like '%%"+keyTxt+"%%' or A.CHANNELNAME like '%%"+keyTxt+"%%') ");
            }
            sqlbuf.append(" ) order by CHANNELID");
            
        } else {
            sqlbuf.append(" select * from ( ");
            sqlbuf.append(" select  A.CHANNELID,A.CHANNELNAME,N'0'as STATUS_CHANNEL,  A.restrictShop ,  "
                    + " R3.onShelfAuto , R3.onShelfDate , R3.onShelfTime ,R3.offShelfAuto , R3.offShelfDate , R3.offShelfTime ");
            sqlbuf.append(" from CRM_CHANNEL A ");
            sqlbuf.append(" INNER join PLATFORM_APP P on a.APPNO=P.APPNO and P.ISONLINE='Y' AND P.ISTHIRD='N' and P.status = '100' ");

//			sqlbuf.append(" left join CRM_CHANNELSHOP B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID ");
//			sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND B.SHOPID=C.ORGANIZATIONNO AND C.LANG_TYPE='"+langType+"' ");
            sqlbuf.append(" left join DCP_GOODS_SHELF_DATE R3 on A.EID=R3.EID AND A.CHANNELID=R3.CHANNELID    ");
            
            sqlbuf.append(" where A.EID='"+eId+"' ");
            if(keyTxt!=null && !keyTxt.isEmpty()) {
                sqlbuf.append(" AND (A.CHANNELID like '%%"+keyTxt+"%%' or A.CHANNELNAME like '%%"+keyTxt+"%%') ");
            }
            sqlbuf.append(" ) order by CHANNELID");
            
        }
        
        return sqlbuf.toString();
        
    }
    
}
