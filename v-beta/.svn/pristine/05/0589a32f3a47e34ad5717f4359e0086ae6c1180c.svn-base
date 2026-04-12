package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.json.cust.req.DCP_GetAllGroup_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetAllGroup_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetAllGroup_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_GetAllGroup_Open
 * 服务说明：获取全部线上商品分组
 * @author jinzma
 * @since  2020-09-25
 */
public class DCP_GetAllGroup_Open extends SPosBasicService<DCP_GetAllGroup_OpenReq,DCP_GetAllGroup_OpenRes>{
	
	@Override
	protected boolean isVerifyFail(DCP_GetAllGroup_OpenReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String apiUserCode = req.getApiUserCode();
		//String timestamp = req.getTimestamp();
		String langType = req.getLangType();
		
		if(apiUserCode==null) {
			errMsg.append("apiUserCode不能为空值 ");
			isFail=true;
		}
		
		//		if(timestamp==null)
		//		{
		//			errMsg.append("timestamp不能为空值 ");
		//			isFail=true;
		//		}
		//		else
		//		{
		//			if (timestamp.length()<17)
		//			{
		//				errMsg.append("timestamp格式不正确 ");
		//				isFail=true;
		//			}
		//		}
		
		if(langType==null) {
			errMsg.append("langType不能为空值 ");
			isFail=true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_GetAllGroup_OpenReq> getRequestType() {
		return new TypeToken<DCP_GetAllGroup_OpenReq>(){};
	}
	
	@Override
	protected DCP_GetAllGroup_OpenRes getResponseType() {
		return new DCP_GetAllGroup_OpenRes();
	}
	
	@Override
	protected DCP_GetAllGroup_OpenRes processJson(DCP_GetAllGroup_OpenReq req) throws Exception {
		DCP_GetAllGroup_OpenRes res = this.getResponse();
		try {
			String apiUserCode = req.getApiUserCode();
			
			//String appType = "";          //从apiUserCode 查询得到应用类型
			String shopId = "";           //门店编号	
			String periodNo = "";         //当前时段
			
			
			//以下是云洋在基类里面进行赋值  20200915
			String eId=req.geteId();  	//从apiUserCode 查询得到企业编号
			String channelId = req.getApiUser().getChannelId();	  //从apiUserCode 查询得到渠道编码
			//appType = req.getApiUser().getAppType();
			
			if (Check.Null(eId)) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的eId");
			}
			if (Check.Null(channelId)) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的channelId");
			}
			//if (Check.Null(appType))
			//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的appType");
			
			levelElm request = req.getRequest();
			if (request!=null) {
				shopId = request.getShopId();
			}
			
			Calendar cal = Calendar.getInstance();//获得当前时间		
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			String periodTime = df.format(cal.getTime());
			
			//时段获取
			String sql = " select a.periodno from dcp_period a"
					+ " left join dcp_period_range b on a.eid=b.eid and a.periodno=b.periodno and b.shopid='"+shopId+"'"
					+ " where a.eid='"+eId+"' and a.status='100' and (a.restrictshop='0' or (a.restrictshop='1' and b.shopid is not null))"
					+ " and a.begintime <='"+periodTime+"'"
					+ " and a.endtime >='"+periodTime+"'";
			List<Map<String, Object>> getPeriodNo = this.doQueryData(sql, null);
			if (getPeriodNo!=null && !getPeriodNo.isEmpty()) {
				periodNo = getPeriodNo.get(0).get("PERIODNO").toString();
			}
			
			//线上商品分组获取
			sql =getQuerySql(req,eId,channelId,shopId,periodNo);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<>());
			if (getQData!=null && !getQData.isEmpty()) {
				Map<String, Boolean> condition = new HashMap<>(); //查詢條件
				condition.put("CLASSNO", true);
				List<Map<String, Object>> getQClass=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneClass : getQClass) {
					DCP_GetAllGroup_OpenRes.level1Elm lv1 = res.new level1Elm();
					String classNo =oneClass.get("CLASSNO").toString();
					
					lv1.setSubGroup(new ArrayList<>());
					for (Map<String, Object> oneData : getQData) {
						if (classNo.equals(oneData.get("CLASSNO").toString())) {
							String groupId = oneData.get("DETAIL_CLASSNO").toString();
							if (!Check.Null(groupId)) {
								DCP_GetAllGroup_OpenRes.level2Elm lv2 = res.new level2Elm();
								lv2.setGroupId(groupId);
								lv2.setGroupName(oneData.get("DETAIL_CLASSNAME").toString());
								lv2.setIsShare(oneData.get("DETAIL_ISSHARE").toString());
								
								lv1.getSubGroup().add(lv2);
							}
						}
					}
					lv1.setGroupId(classNo);
					lv1.setGroupName(oneClass.get("CLASSNAME").toString());
					lv1.setIsShare(oneClass.get("ISSHARE").toString());
					res.getDatas().add(lv1);
				}
			}
			
			return res;
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_GetAllGroup_OpenReq req) throws Exception {
		return null;
	}
	
	private String getQuerySql(DCP_GetAllGroup_OpenReq req,String eId,String channelId,String shopId,String periodNo) {
		String langType=req.getLangType();
		StringBuffer sb = new StringBuffer();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String classType="";
		if(req.getRequest()!=null){
			classType=req.getRequest().getClassType();
		}
		
		sb.append(" "
				+ " with invalidclass as ("
				+ " select a.classno from dcp_class a"
				+ " inner join dcp_class_range b on a.eid=b.eid and a.classno=b.classno and a.classtype=b.classtype and b.rangetype='2' and b.id='"+shopId+"'"
				+ " where a.eid='"+eId+"' and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"' and a.restrictshop=2 and a.status='100'");
		
		if(classType!=null&&classType.length()>0){
			sb.append(" and a.classtype='"+classType+"'");
		}else{
			sb.append(" and a.classtype='ONLINE'");
		}
		sb.append(" "
				+ " group by a.classno)");
		
		sb.append(" "
				+ " ,detailclass as ("
				+ " select a.classno,a.sortid,a.upclassno,a.isshare from dcp_class a"
				+ " left join dcp_class_range b1 on a.eid=b1.eid and a.classno=b1.classno and a.classtype=b1.classtype and b1.rangetype='2' and b1.id='"+shopId+"'"
				+ " left join dcp_class_range b2 on a.eid=b2.eid and a.classno=b2.classno and a.classtype=b2.classtype and b2.rangetype='3' and b2.id='"+channelId+"'"
				+ " left join dcp_class_range b3 on a.eid=b3.eid and a.classno=b3.classno and a.classtype=b3.classtype and b3.rangetype='5' and b3.id='"+periodNo+"'"
				+ " left join invalidclass on a.classno=invalidclass.classno"
				+ " where a.eid='"+eId+"' and a.levelid='2' " //and a.status='100'
				+ " and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and b1.id is not null))"
				+ " and (a.restrictchannel=0 or (a.restrictchannel=1 and b2.id is not null))"
				+ " and (a.restrictperiod=0 or (a.restrictperiod=1 and b3.id is not null))");
		
		if(classType!=null&&classType.length()>0){
			sb.append(" and a.classtype='"+classType+"'");
		}else{
			sb.append(" and a.classtype='ONLINE'");
		}
		
		sb.append(" "
				+ " and invalidclass.classno is null )");
		
		sb.append(" "
				+ " select a.classno,a.isshare,cl1.classname,"
				+ " detailclass.classno as detail_classno,detailclass.isshare as detail_isshare,"
				+ " cl2.classname as detail_classname"
				+ " from dcp_class a"
				+ " left join detailclass on a.classno=detailClass.upclassno"
				+ " left join dcp_class_range b1 on a.eid=b1.eid and a.classno=b1.classno and a.classtype=b1.classtype and b1.rangetype='2' and b1.id='"+shopId+"' "
				+ " left join dcp_class_range b2 on a.eid=b2.eid and a.classno=b2.classno and a.classtype=b2.classtype and b2.rangetype='3' and b2.id='"+channelId+"'"
				+ " left join dcp_class_range b3 on a.eid=b3.eid and a.classno=b3.classno and a.classtype=b3.classtype and b3.rangetype='5' and b3.id='"+periodNo+"'"
				+ " left join invalidclass on a.classno=invalidclass.classno"
				+ " left join dcp_class_lang cl1 on cl1.eid=a.eid and cl1.classtype=a.classtype and cl1.classno=a.classno and cl1.lang_type='"+langType+"'"
				+ " left join dcp_class_lang cl2 on cl2.eid=a.eid and cl2.classtype=a.classtype and cl2.classno=detailClass.classno and cl2.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.levelid='1' " //and a.status='100'
				+ " and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and b1.id is not null))"
				+ " and (a.restrictchannel=0 or (a.restrictchannel=1 and b2.id is not null))"
				+ " and (a.restrictperiod=0 or (a.restrictperiod=1 and b3.id is not null))"
				+ " and invalidclass.classno is null");
		
		if(classType!=null&&classType.length()>0){
			sb.append(" and a.classtype='"+classType+"'");
		}else{
			sb.append(" and a.classtype='ONLINE'");
		}
		
		sb.append(""
				+ " order by a.sortid ,detailClass.sortid  "   ///desc 鼎捷-黄玲霞  14:08:10 	DCP_GetAllGroup_Open这一个接口,我之前是让你越大越前面,要改成越小越前面,和POS规则一致
				+ " ");
		
		return sb.toString();
	}
	
	
}
