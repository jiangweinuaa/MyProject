package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_DualPlayQueryReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;


/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询DCP
 * 服务说明：双屏播放查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayQuery extends SPosBasicService<DCP_DualPlayQueryReq,DCP_DualPlayQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DualPlayQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DualPlayQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DualPlayQueryReq>(){};
	}

	@Override
	protected DCP_DualPlayQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DualPlayQueryRes();
	}

	@Override
	protected DCP_DualPlayQueryRes processJson(DCP_DualPlayQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DualPlayQueryRes res = null;
		res = this.getResponse();

		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);

			res.setDatas(new ArrayList<DCP_DualPlayQueryRes.level1Elm>());

            MyCommon  cm=new MyCommon();

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				String imgBaseUrl = cm.getDualplayImgBaseUrl(req.geteId());
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查询条件
				condition1.put("A_DUALPLAYID", true);
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition1);
				for (Map<String, Object> oneData1 : getQHeader) 
				{
					DCP_DualPlayQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setShops(new ArrayList<DCP_DualPlayQueryRes.level2Elm>());
					oneLv1.setTimes(new ArrayList<DCP_DualPlayQueryRes.level3Elm>());
					String dualPlayID = oneData1.get("A_DUALPLAYID").toString();
					String platformType = oneData1.get("PLATFORMTYPE").toString();
					String fileName = oneData1.get("FILENAME").toString();
					String shopType = oneData1.get("SHOPTYPE").toString();
					String timeType= oneData1.get("TIMETYPE").toString();
					String memo = oneData1.get("MEMO").toString();
					String pollTime = oneData1.get("POLLTIME").toString();
					String status = oneData1.get("A_STATUS").toString();

					//生效门店查询
					Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
					condition2.put("A_DUALPLAYID", true);
					condition2.put("SHOPID", true);
					List<Map<String, Object>> getQshop=MapDistinct.getMap(getQDataDetail, condition2);
					for (Map<String, Object> oneData2 : getQshop) 
					{
						if(dualPlayID.equals(oneData2.get("A_DUALPLAYID")))
						{

							DCP_DualPlayQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String shopId = oneData2.get("SHOPID").toString();
							String shopName = oneData2.get("ORG_NAME").toString();

							if (Check.Null(shopId)) 
								continue ;
							else
							{
								oneLv2.setShopId(shopId);
								oneLv2.setShopName(shopName);	
								oneLv1.getShops().add(oneLv2);
								oneLv2 = null;
							}
						}
					}


					//生效时间查询
					Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查询条件
					condition3.put("A_DUALPLAYID", true);
					condition3.put("ITEM", true);
					List<Map<String, Object>> getQtime=MapDistinct.getMap(getQDataDetail, condition3);
					for (Map<String, Object> oneData3 : getQtime) 
					{
						if(dualPlayID.equals(oneData3.get("A_DUALPLAYID")))
						{
							DCP_DualPlayQueryRes.level3Elm oneLv3 = res.new level3Elm();
							String item = oneData3.get("ITEM").toString();
							String lbDate = oneData3.get("LBDATE").toString();
							String leDate = oneData3.get("LEDATE").toString();
							String lbTime = oneData3.get("LBTIME").toString();
							String leTime = oneData3.get("LETIME").toString();

							if (Check.Null(item)) 
								continue ;
							else
							{
								oneLv3.setItem(item);
								oneLv3.setLbDate(lbDate);
								oneLv3.setLeDate(leDate);
								oneLv3.setLbTime(lbTime);
								oneLv3.setLeTime(leTime);							
								oneLv1.getTimes().add(oneLv3);
								oneLv3 = null;
							}
						}
					}

					//设置响应
					oneLv1.setDualPlayID(dualPlayID);
					oneLv1.setPlatformType(platformType);
					oneLv1.setFileName(fileName);
					oneLv1.setFileUrl(imgBaseUrl + "/" + fileName);
					oneLv1.setShopType(shopType);
					oneLv1.setTimeType(timeType);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setPollTime(pollTime);

					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}

			cm=null;

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
	protected String getQuerySql(DCP_DualPlayQueryReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
		String langType = req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append( " "
				+	" select a_dualPlayID,PLATFORMTYPE,fileName,shopType,timeType,memo,a_status,SHOPID,org_name,item,lbdate,ledate,lbtime,letime,POLLTIME from ( "
				+ " select a.dualPlayID as a_dualPlayID,a.PLATFORMTYPE,  a.fileName,a.shopType,a.timeType,a.memo,a.status as a_status,b.SHOPID,d.org_name, "
				+ " c.item,c.lbdate,c.ledate,c.lbtime,c.letime,a.POLLTIME from DCP_DUALPLAY a "
				+ " left  join DCP_DUALPLAY_SHOP b on b.EID=a.EID and b.dualplayid=a.dualplayid and b.status='100' "
				+ " left  join DCP_DUALPLAY_time c on c.EID=a.EID and c.dualplayid=a.dualplayid and c.status='100'  "
				+ " left  join DCP_ORG_lang d on d.EID=b.EID and d.organizationno=b.SHOPID "
				+ " and d.lang_type='"+ langType +"' and d.status='100' "
				+ " where a.EID='"+ eId +"'  order by a.tran_time desc )"
				) ;
		sql = sqlbuf.toString();
		return sql;

	}

}


