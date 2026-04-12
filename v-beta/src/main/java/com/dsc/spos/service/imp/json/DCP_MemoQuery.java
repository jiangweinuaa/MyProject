package com.dsc.spos.service.imp.json;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_MemoQueryReq;
import com.dsc.spos.json.cust.res.DCP_MemoQueryRes;
/**
 * 服務函數：MemoGetDCPServiceImp
 *   說明：整单备注查询DCP
 * @author Jinzma 
 * @since  2018-10-31
 */
public class DCP_MemoQuery extends SPosBasicService<DCP_MemoQueryReq,DCP_MemoQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_MemoQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_MemoQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MemoQueryReq>(){};
	}

	@Override
	protected DCP_MemoQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MemoQueryRes();
	}

	@Override
	protected DCP_MemoQueryRes processJson(DCP_MemoQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_MemoQueryRes res = null;
		res = this.getResponse();	
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_MemoQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查询条件
				condition1.put("ITEM", true);
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition1);
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_MemoQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setShops (new ArrayList<DCP_MemoQueryRes.level2Elm>());

					String item = oneData.get("ITEM").toString();
					String memo = oneData.get("MEMO").toString();
					String priority = oneData.get("PRIORITY").toString();
					String status = oneData.get("STATUS").toString();
					Integer a= Integer.parseInt(item) ;
					for (Map<String, Object> oneData1 : getQDataDetail) 
					{
						String item_D =oneData1.get("ITEM").toString();
						Integer b= Integer.parseInt(item_D) ;
						if ( Integer.compare(a, b)== 0 )
						{
							DCP_MemoQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String shopId = oneData1.get("SHOPID").toString();
							String shopName = oneData1.get("ORG_NAME").toString();
							if (Check.Null(shopId))
								continue ;
							else
							{
								//设置响应
								oneLv2.setShopId(shopId);
								oneLv2.setShopName(shopName);
								oneLv1.getShops().add(oneLv2);
							}
						}
					}
					//设置响应
					oneLv1.setItem(item);
					oneLv1.setMemo(memo);
					oneLv1.setPriority(priority);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
				}
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_MemoQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		String eId = req.geteId();
		String langType=req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(  " "
				+ " select  a.item,a.memo,a.priority,b.SHOPID,c.org_name,a.status from DCP_MEMO a "
				+ " left join DCP_MEMO_shop b on a.EID=b.EID and a.item=b.item and b.status='100' "
				+ " left join DCP_ORG_lang c on c.EID=b.EID and c.organizationno=b.SHOPID "
				+ " and c.lang_type='"+ langType +"' and c.status='100' where a.EID='"+ eId +"'  order by a.priority " );

		sql = sqlbuf.toString();
		return sql;
	}


}
