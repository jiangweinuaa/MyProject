package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.req.DCP_TicketStyleDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TicketStyleDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_TicketStyleDetailQuery
 * 服务说明：企业小票详情
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleDetailQuery extends SPosBasicService<DCP_TicketStyleDetailQueryReq, DCP_TicketStyleDetailQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_TicketStyleDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if (Check.Null(req.getRequest().getStyleId())) 
		{
			errMsg.append("小票样式模板不可为空值,");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TicketStyleDetailQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketStyleDetailQueryReq>(){};
	}

	@Override
	protected DCP_TicketStyleDetailQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleDetailQueryRes();
	}

	@Override
	protected DCP_TicketStyleDetailQueryRes processJson(DCP_TicketStyleDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		// 查询数据
		String sql = null;
		DCP_TicketStyleDetailQueryRes res = null;
		res = this.getResponse();

		try {
			// 单头查询
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_TicketStyleDetailQueryRes.level1Elm>());

			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("STYLEID", true);
			condition.put("STYLENAME_CN", true);
			condition.put("TICKETTYPE", true);
			// 调用过滤函数
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

			Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
			condition2.put("STYLEID", true);
			condition2.put("STYLENAME_CN", true);
			condition2.put("TICKETTYPE", true);
			condition2.put("GROUPID", true);
			// 调用过滤函数
			List<Map<String, Object>> getQHeader2 = MapDistinct.getMap(getQDataDetail, condition2);

			Map<String, Object> names = getNames(req);
			String strStyleName = names.get("styleName").toString();
			String strTicketName = names.get("ticketName").toString();
			String strGroupName = names.get("groupName").toString();
			String strCompoName = names.get("compoName").toString();

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
				for (Map<String, Object> oneData : getQHeader) {
					// 取出第一层
					DCP_TicketStyleDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String styleId = oneData.get("STYLEID").toString();
					String styleName = oneData.get(strStyleName).toString();
					String ticketType = oneData.get("TICKETTYPE").toString();
					String ticketName = oneData.get(strTicketName).toString();
					

					// 设置响应
					oneLv1.setStyleId(styleId);
					oneLv1.setStyleName(styleName);
					oneLv1.setTicketType(ticketType);
					oneLv1.setTicketName(ticketName);

					oneLv1.setCompoGroupList(new ArrayList<DCP_TicketStyleDetailQueryRes.level2Elm>());
					for (Map<String, Object> oneData2 : getQHeader2) {
						// 过滤属于此单头的明细
						if (styleId.equals(oneData2.get("STYLEID")) == false) {
							continue;
						}

						// 取出第二层
						DCP_TicketStyleDetailQueryRes.level2Elm oneLv2 = res.new level2Elm();
						String groupId = oneData2.get("GROUPID").toString();
						String groupName = oneData2.get(strGroupName).toString();

						oneLv2.setGroupId(groupId);
						oneLv2.setGroupName(groupName);

						oneLv2.setCompoList(new ArrayList<DCP_TicketStyleDetailQueryRes.level3Elm>());

						for (Map<String, Object> oneData3 : getQDataDetail) {
							// 过滤属于此单头的明细
							if (styleId.equals(oneData3.get("STYLEID")) == false) {
								continue;
							}
							if (groupId.equals(oneData3.get("GROUPID")) == false) {
								continue;
							}
							// 取出第三层
							DCP_TicketStyleDetailQueryRes.level3Elm oneLv3 = res.new level3Elm();
							String compoId = oneData3.get("COMPOID").toString();
							String compoName = oneData3.get(strCompoName).toString();
							String conType = oneData3.get("CONTYPE").toString();
							String defValue = oneData3.get("DEFVALUE").toString();
							String strCurValue = oneData3.get("CONTENT").toString();
							String alternatives = oneData3.get("ALTERATEVALUE").toString();
							// 因为双引号原因，JSON入参会自动转义，如需返回''格式的 则把注释去掉
							// alternatives = alternatives.replace("\"","\'");

							oneLv3.setCompoId(compoId);
							oneLv3.setCompoName(compoName);
							oneLv3.setConType(conType);
							oneLv3.setDefValue(defValue);
							JSONObject jsonObject = JSONObject.parseObject(strCurValue);
							try {
								Object object = jsonObject.get(groupId);
								JSONObject jsonObject2 = JSONObject.parseObject(object.toString());
								oneLv3.setCurValue(jsonObject2.get(compoId).toString());
							} catch (Exception e) {
								oneLv3.setCurValue("");
							}
							oneLv3.setAlternatives(alternatives);
							oneLv2.getCompoList().add(oneLv3);
						}
						oneLv1.getCompoGroupList().add(oneLv2);
					}
					res.getDatas().add(oneLv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected String getQuerySql(DCP_TicketStyleDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		String sql = null;
		String styleId = req.getRequest().getStyleId();
		Map<String, Object> names = getNames(req);
		String styleName = names.get("styleName").toString();
		String ticketName = names.get("ticketName").toString();
		String groupName = names.get("groupName").toString();
		String compoName = names.get("compoName").toString();

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT a.STYLEID, a." + styleName + ", a.TICKETTYPE,"+ticketName+", c.groupId, c." + groupName + " , d.COMPOID,"
				+ " d." + compoName + ", d.CONTYPE, d.DEFVALUE, a.CONTENT , d.ALTERATEVALUE FROM DCP_TICKETSTYLE a "
				+ "INNER JOIN DCP_TICKETTYPE b ON a.TICKETTYPE = b.TICKETTYPE AND b.ISDISPLAY = 'Y' "
				+ "INNER JOIN DCP_TICKETCOMPO_GROUP c ON 1 = 1 "
				+ "INNER JOIN DCP_TICKETCOMPO d ON d.GROUPID = c.GROUPID "
				+ "INNER JOIN DCP_TICKETTYPE_COMPO f ON a.TICKETTYPE = f.TICKETTYPE AND f.COMPOID = d.COMPOID "
				+ "AND f.ENABLE = 'Y' WHERE a.styleId='" + styleId + "' AND  a.EID ='"+req.geteId()+"'");

		sqlbuf.append("ORDER BY b.SORTID, a.STYLEID, c.SORTID, d.SORTID");
		sql = sqlbuf.toString();
		return sql;
	}
	
	/**
	 * 因为有三种语言别 根据Token 语言别 判断查找的是哪个语言名称
	 * 
	 * @return
	 */
	private Map<String, Object> getNames(DCP_TicketStyleDetailQueryReq req) {
		Map<String, Object> map = new HashMap<>();
		String langType = req.getLangType();
		String styleName = null;
		String ticketName = null;
		String groupName = null;
		String compoName = null;
		if (langType.equals("zh_CN")) {
			styleName = "STYLENAME_CN";
			ticketName = "TICKETNAME_CN";
			groupName = "GROUPNAME_CN";
			compoName = "COMPONAME_CN";
		} else if (langType.equals("zh_TW")) {
			styleName = "STYLENAME_TW";
			ticketName = "TICKETNAME_TW";
			groupName = "GROUPNAME_TW";
			compoName = "COMPONAME_TW";
		} else if (langType.equals("en_US")) {
			styleName = "STYLENAME_EN";
			ticketName = "TICKETNAME_EN";
			groupName = "GROUPNAME_EN";
			compoName = "COMPONAME_EN";
		}
		map.put("styleName", styleName);
		map.put("ticketName", ticketName);
		map.put("groupName", groupName);
		map.put("compoName", compoName);
		return map;
	}

}
