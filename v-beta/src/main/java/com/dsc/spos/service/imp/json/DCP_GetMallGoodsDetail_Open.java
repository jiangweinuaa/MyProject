package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsDetail_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsDetail_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsDetail_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_GetMallGoodsDetail_Open
 * 服务说明：获取线上商品详情图文
 * @author jinzma 
 * @since  2020-10-13
 */
public class DCP_GetMallGoodsDetail_Open extends SPosBasicService<DCP_GetMallGoodsDetail_OpenReq,DCP_GetMallGoodsDetail_OpenRes>{

	@Override
	protected boolean isVerifyFail(DCP_GetMallGoodsDetail_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String apiUserCode = req.getApiUserCode();
		String langType = req.getLangType();
		levelElm request =req.getRequest();
		if(apiUserCode==null)
		{
			errMsg.append("apiUserCode不能为空值 ");
			isFail=true;
		}

		if(langType==null)
		{
			errMsg.append("langType不能为空值 ");
			isFail=true;
		}
		if (request==null)
		{
			errMsg.append("request不能为空值 ");
			isFail=true;
		}
		else
		{
			if (Check.Null(request.getMallGoodsId()))
			{
				errMsg.append("mallGoodsId线上商品代号不能为空值 ");
				isFail=true;
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_GetMallGoodsDetail_OpenReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_GetMallGoodsDetail_OpenReq>(){} ;
	}

	@Override
	protected DCP_GetMallGoodsDetail_OpenRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_GetMallGoodsDetail_OpenRes();
	}

	@Override
	protected DCP_GetMallGoodsDetail_OpenRes processJson(DCP_GetMallGoodsDetail_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_GetMallGoodsDetail_OpenRes res = this.getResponse();
		try 
		{
			String apiUserCode = req.getApiUserCode();
			String langType = req.getLangType();
			String eId = "";              //从apiUserCode 查询得到企业编号
			String appType = "";          //从apiUserCode 查询得到应用类型
			String channelId = "";        //从apiUserCode 查询得到渠道编码
			String mallGoodsId="";        //线上商品代号	
			String sql="";

			//以下是云洋在基类里面进行赋值  20200915
			eId=req.geteId();
			appType = req.getApiUser().getAppType();
			channelId = req.getApiUser().getChannelId();	
			if (Check.Null(eId))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的eId");
			if (Check.Null(appType))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的appType");
			if (Check.Null(channelId))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的channelId");

			levelElm request = req.getRequest();
			if (request!=null)
			{
				mallGoodsId = request.getMallGoodsId();
			}

			////图片地址参数获取
			String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
			String httpStr = isHttps.equals("1") ? "https://" : "http://";
			String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
			String imagePath = "";
			if (domainName.endsWith("/")) {
				imagePath = httpStr + domainName + "resource/image/";
			} else {
				imagePath = httpStr + domainName + "/resource/image/";
			}


			DCP_GetMallGoodsDetail_OpenRes.level1Elm datas = res.new level1Elm();
			//线上商品获取detailcomponents 
			sql =getDetailcomponents(eId,appType,mallGoodsId);   
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			datas.setDetailcomponents(new ArrayList<DCP_GetMallGoodsDetail_OpenRes.level2ElmDetailcomponents>());
			if (getQData!=null && getQData.isEmpty()==false){
				for (Map<String, Object> oneData:getQData) 
				{
					DCP_GetMallGoodsDetail_OpenRes.level2ElmDetailcomponents lv2 = res.new level2ElmDetailcomponents();
					String serialNo = oneData.get("ITEM").toString(); 
					String type = oneData.get("TYPE").toString();
					String content = "";  ///CLOB类型
					String detailimage = oneData.get("DETAILIMAGE").toString();
					try
					{
						if  (oneData.get("CONTENT")!=null && !Check.Null(oneData.get("CONTENT").toString()))   
						{
							content = oneData.get("CONTENT").toString();
						}
					}
					catch(Exception e)
					{
						content="" ;
					}
					
					////type = TEST,OUTSRC时,取content , type=IMAGE ,VIDEO取DETAILIMAGE by 玲霞 20201023
					if (type.equals("IMAGE")||type.equals("VIDEO"))
					{
						content = imagePath + detailimage;
					}
					
					
					lv2.setContent(content);
					lv2.setSerialNo(serialNo);
					lv2.setType(type);
					datas.getDetailcomponents().add(lv2);
				}
			}

			//线上商品获取productParam
			sql =getProductParam(eId,mallGoodsId,langType);   
			getQData.clear();
			getQData = this.doQueryData(sql, null);
			datas.setProductParam(new ArrayList<DCP_GetMallGoodsDetail_OpenRes.level2ElmProductParam>());
			if (getQData!=null && getQData.isEmpty()==false){
				int serialNo=1;
				for (Map<String, Object> oneData:getQData) 
				{
					DCP_GetMallGoodsDetail_OpenRes.level2ElmProductParam lv2 = res.new level2ElmProductParam();
					String paramId = oneData.get("PARAMID").toString(); 
					String paramName = oneData.get("PARAMNAME").toString();
					String param = oneData.get("PARAM").toString();

					lv2.setParam(param);
					lv2.setParamId(paramId);
					lv2.setParamName(paramName);
					lv2.setSerialNo(String.valueOf(serialNo));
					datas.getProductParam().add(lv2);
					serialNo++;	
				}
			}

			res.setDatas(datas);
			return res;
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_GetMallGoodsDetail_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	private String getDetailcomponents(String eId,String appType,String mallGoodsId) throws Exception {

		String sql="";
		StringBuffer sb = new StringBuffer();
		sb.append(""
				+ " select pluno,item,type,content,detailimage from ("
				+ " select a.*,row_number() over (partition by pluno,item order by indx ) as rn from "
				+ " ("
				+ " select a.pluno,a.item,a.type,a.content,a.detailimage,1 as indx from dcp_goodsimage_detailimage a"
				+ " where a.eid='"+eId+"' and a.apptype='"+appType+"' and a.pluno='"+mallGoodsId+"'"
				+ " union all"
				+ " select a.pluno,a.item,a.type,a.content,a.detailimage,2 as indx from dcp_goodsimage_detailimage a"
				+ " where a.eid='"+eId+"' and a.apptype='ALL' and a.pluno='"+mallGoodsId+"'"
				+ " ) a"
				+ " ) where rn='1' order by pluno,item"
				+ " ");

		sql=sb.toString();
		return sql;	

	}

	private String getProductParam(String eId,String mallGoodsId,String langType) throws Exception {
		String sql="";
		StringBuffer sb = new StringBuffer();
		sb.append(""
				+ " select a.attrid as paramId, b.attrname as paramName,c.intro as param from dcp_goods_online_intro a"
				+ " left join dcp_attribution_lang b on a.eid=b.eid and a.attrid=b.attrid and b.lang_type='"+langType+"'"
				+ " left join dcp_goods_online_intro_lang c on a.eid=c.eid and a.pluno=c.pluno and a.attrid=c.attrid and c.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"'"
				+ " ");
		sql=sb.toString();
		return sql;		
	}

}
