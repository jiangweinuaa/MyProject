package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TicketPrintParamQueryReq;
import com.dsc.spos.json.cust.res.DCP_TicketStyleDetailQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_TicketStyleDetailQuery
 * 服务说明：企业小票详情
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketPrintParamQuery extends SPosBasicService<DCP_TicketPrintParamQueryReq, DCP_TicketStyleDetailQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_TicketPrintParamQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if (Check.Null(req.getRequest().getStyleId())&&Check.Null(req.getRequest().getTicketType())) 
		{
			errMsg.append("小票样式模板styleId和票据类型ticketType不能都为空（至少传入一个）,");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TicketPrintParamQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketPrintParamQueryReq>(){};
	}

	@Override
	protected DCP_TicketStyleDetailQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleDetailQueryRes();
	}

	@Override
	protected DCP_TicketStyleDetailQueryRes processJson(DCP_TicketPrintParamQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		// 查询数据
		String sql = null;
		DCP_TicketStyleDetailQueryRes res = null;
		res = this.getResponse();
		res.setDatas(new ArrayList<DCP_TicketStyleDetailQueryRes.level1Elm>());
		
		String styleId = "";
		if(!Check.Null(req.getRequest().getStyleId()))
		{
			styleId = req.getRequest().getStyleId();
		}
		else
		{
			String sql_styleId = "select * from DCP_TICKETSTYLE where  EID='"+req.geteId()+"' and TICKETTYPE='"+req.getRequest().getTicketType()+"' order by LASTMODITIME desc ";
			
			List<Map<String, Object>> QDatas_styleId = this.doQueryData(sql_styleId, null);
			
			if(QDatas_styleId==null||QDatas_styleId.isEmpty())
			{
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("未查询倒小票样式模板！");
				return res;
			}
			styleId = QDatas_styleId.get(0).get("STYLEID").toString();
			
		}

		try {
			com.dsc.spos.json.cust.req.DCP_TicketStyleDetailQueryReq detailQueryReq = new com.dsc.spos.json.cust.req.DCP_TicketStyleDetailQueryReq();
			com.dsc.spos.json.cust.req.DCP_TicketStyleDetailQueryReq.level1Elm detailQueryReq_request = detailQueryReq.new level1Elm();
			detailQueryReq_request.setStyleId(styleId);
			
			Map<String,Object> jsonMap=new HashMap<String,Object>();
			jsonMap.put("serviceId", "DCP_TicketStyleDetailQuery");
			//这个token是无意义的
			jsonMap.put("token", req.getToken());
			jsonMap.put("request", detailQueryReq_request);
			ParseJson pj=new ParseJson();
			String json_ship = pj.beanToJson(jsonMap);
			HelpTools.writelog_fileName("调用DCP_TicketStyleDetailQuery服务请求Req："+json_ship, "DCP_TicketPrintParamQuery");

			DispatchService ds = DispatchService.getInstance();
			String resbody_ship = ds.callService(json_ship, this.dao);

			HelpTools.writelog_fileName("调用DCP_TicketStyleDetailQuery服务返回Res： ", "DCP_TicketPrintParamQuery");
			
			res = pj.jsonToBean(resbody_ship, new TypeToken<DCP_TicketStyleDetailQueryRes>(){});
			
			
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
	protected String getQuerySql(DCP_TicketPrintParamQueryReq req) throws Exception {
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
				+ "AND f.ENABLE = 'Y' WHERE a.styleId LIKE '%" + styleId + "%' AND  a.EID ='"+req.geteId()+"'");

		sqlbuf.append("ORDER BY b.SORTID, a.STYLEID, c.SORTID, d.SORTID");
		sql = sqlbuf.toString();
		return sql;
	}
	
	/**
	 * 因为有三种语言别 根据Token 语言别 判断查找的是哪个语言名称
	 * 
	 * @return
	 */
	private Map<String, Object> getNames(DCP_TicketPrintParamQueryReq req) {
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
