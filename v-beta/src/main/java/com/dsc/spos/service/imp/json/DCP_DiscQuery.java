package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DiscQueryReq;
import com.dsc.spos.json.cust.res.DCP_DiscQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DiscGetDCP
 *   說明：触屏折扣查询DCP
 * 服务说明：触屏折扣查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DiscQuery extends SPosBasicService<DCP_DiscQueryReq,DCP_DiscQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DiscQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DiscQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DiscQueryReq>(){};
	}

	@Override
	protected DCP_DiscQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DiscQueryRes();
	}

	@Override
	protected DCP_DiscQueryRes processJson(DCP_DiscQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DiscQueryRes res = null;
		res = this.getResponse();	
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_DiscQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查询条件
				condition1.put("KEYVALUE", true);
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition1);
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_DiscQueryRes.level1Elm oneLv1 = new DCP_DiscQueryRes().new level1Elm();
					oneLv1.setShops (new ArrayList<DCP_DiscQueryRes.level2Elm>());
					String keyValue = oneData.get("KEYVALUE").toString();
					String priority = oneData.get("PRIORITY").toString();
					String status = oneData.get("STATUS").toString();
					Float a= Float.parseFloat(keyValue) ;
					
					for (Map<String, Object> oneData1 : getQDataDetail) 
					{
						String keyValue_D =oneData1.get("KEYVALUE").toString();
						Float b= Float.parseFloat(keyValue_D) ;
						if ( Float.compare(a, b)== 0 )
						{
							DCP_DiscQueryRes.level2Elm oneLv2 = new DCP_DiscQueryRes().new level2Elm();
							
							String shopId = oneData1.get("SHOPID").toString();
							String shopName = oneData1.get("ORG_NAME").toString();
							if (Check.Null(shopId))
								continue ;
								else
								{
									//设置响应
									oneLv2.setShopName(shopName);
									oneLv2.setShopId(shopId);
									oneLv1.getShops().add(oneLv2);
									oneLv2 =null;
								}
						}
					}

					//设置响应
					oneLv1.setKeyValue(keyValue);
					oneLv1.setPriority(priority);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DiscQueryReq req) throws Exception {

		String sql=null;			
		String eId = req.geteId();
		String langType=req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(  " "
				+ " select  a.keyvalue,a.priority,b.SHOPID,c.org_name,a.status from DCP_DISC a "
				+ " left join DCP_DISC_SHOP b on a.EID=b.EID and a.keyvalue=b.keyvalue  and b.status='100' "
				+ " left join DCP_ORG_lang c on c.EID=b.EID and c.organizationno=b.SHOPID"
				+ " and c.lang_type='"+ langType +"' and c.status='100' where a.EID='"+ eId +"'  order by a.priority " );

		sql = sqlbuf.toString();
		return sql;
	}

}


