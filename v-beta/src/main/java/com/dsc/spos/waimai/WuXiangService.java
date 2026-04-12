package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.redis.RedisPosPub;

public class WuXiangService extends SWaimaiBasicService
{
	
	@Override
	public String execute(String json) throws Exception {
	// TODO Auto-generated method stub
		String res_json = HelpTools.GetWuXiangResponse(json);
		
	  if(res_json ==null ||res_json.length()==0)
	  {
	  	return null;
	  }
		Map<String, Object> res = new HashMap<String, Object>();
	  this.processDUID(res_json, res);
			
		return null;
	}
		
	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			//直接使用实体类
			JSONObject obj = new JSONObject(req);
			String orderstatus = obj.get("status").toString();//我们自己的订单状态 微商城默认已接单 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
			String companyNO = obj.get("companyNO").toString();
			String shopNO = obj.get("shopNO").toString();
			String orderNO = obj.get("orderNO").toString();
			if(orderstatus !=null)
			{
				if(orderstatus.equals("1"))//订单新建
				{				
					ArrayList<DataProcessBean> DPB = null;// HelpTools.GetInsertOrder(obj);
					if (DPB != null && DPB.size() > 0)
					{
						for (DataProcessBean dataProcessBean : DPB) 
						{
							this.addProcessData(dataProcessBean);			
				    }					
						this.doExecuteDataToDB();
						HelpTools.writelog_waimai("【保存数据库成功】"+ " 企业编号companyNO=" + companyNO + " 门店编号shopNO=" + shopNO + " 订单号orderNO=" + orderNO);
					}
					
				}			
				else//更新 数据库状态 微商城目前 其他状态不会推送过来
				{
					
				}
				
			}
		
	
		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【执行语句】异常："+e.getMessage() + "\r\n req请求内容:" + req);		
	  }
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【执行语句】异常："+e.getMessage() + "\r\n req请求内容:" + req);		
	  }
	
	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	protected String GetMachShopNO(String companyNO, String shopNO,String orderNO) throws Exception
	{
		String machShopNO = "";
		try 
		{
			String sql = "Select * from ta_org where COMPANYNO='"+companyNO+"' AND ORGANIZATIONNO='"+shopNO+"'";
		  List<Map<String, Object>>  getDetail = 	this.doQueryData(sql, null);
			if (getDetail != null && getDetail.isEmpty() == false && getDetail.size() != 0)
			{
				machShopNO = getDetail.get(0).get("MACHORGANIZATIONNO").toString();
			}
			else
			{
				HelpTools.writelog_waimai(
					"查询生产门店为空，该生产门店没有维护！" + " 企业编号companyNO=" + companyNO + " 门店编号shopNO=" + shopNO + " 单号orderNO=" + orderNO);
			}
		
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai(
				"查询生产门店报错！" + e.getMessage() + " 企业编号companyNO=" + companyNO + " 门店编号shopNO=" + shopNO + " 单号orderNO=" + orderNO);
	  }
		
		if(machShopNO==null||machShopNO.length()==0)
		{
			HelpTools.writelog_waimai("查询生产门店为空！");
		}
		else
		{
			HelpTools.writelog_waimai("查询生产门店=" + machShopNO);
		}
		
		return machShopNO;
	}
	
	
}
