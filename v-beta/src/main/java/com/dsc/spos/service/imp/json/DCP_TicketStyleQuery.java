package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TicketStyleQueryReq;
import com.dsc.spos.json.cust.res.DCP_TicketStyleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;


/**
 * 服务函数：DCP_TicketStyleQuery
 * 服务说明：企业小票样式查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleQuery extends SPosBasicService<DCP_TicketStyleQueryReq,DCP_TicketStyleQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_TicketStyleQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if (Check.Null(req.getRequest().getRangeType())) 
		{
			errMsg.append("机构类型不可为空值,");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TicketStyleQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketStyleQueryReq>(){};
	}

	@Override
	protected DCP_TicketStyleQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleQueryRes();
	}

	@Override
	protected DCP_TicketStyleQueryRes processJson(DCP_TicketStyleQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		// 查询资料
		DCP_TicketStyleQueryRes res = null;
		res = this.getResponse();
		Map<String, Object> names = getNames(req);
		String strStyleName = names.get("styleName").toString();
		String strTicketName = names.get("ticketName").toString();
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_TicketStyleQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) {
				for (Map<String, Object> oneData : getQData) {
					DCP_TicketStyleQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String styleId = oneData.get("STYLEID").toString();
					String styleName = oneData.get(strStyleName).toString();
					String ticketType = oneData.get("TICKETTYPE").toString();
					String ticketName = oneData.get(strTicketName).toString();
					
					// 设置响应
					oneLv1.setStyleId(styleId);
					oneLv1.setStyleName(styleName);
					oneLv1.setTicketType(ticketType);
					oneLv1.setTicketName(ticketName);
					
					res.getDatas().add(oneLv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) {
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
	protected String getQuerySql(DCP_TicketStyleQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql = null;
		Map<String, Object> names = getNames(req);
		String strStyleName = names.get("styleName").toString();
		String strTicketName = names.get("ticketName").toString();
		
		String ticketType = req.getRequest().getTicketType();
		String keyTxt = req.getRequest().getKeyTxt();
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT a.styleId, a."+strStyleName+", b.TICKETTYPE, b."+strTicketName+" "
				+ "FROM DCP_TICKETSTYLE a "
				+ "INNER JOIN DCP_TICKETTYPE b ON a.TICKETTYPE = b.TICKETTYPE  "
				+ "WHERE b.rangeType = '"+req.getRequest().getRangeType()+"'  AND a.EID = '"+req.geteId()+"'");
		if(ticketType != null && ticketType.length()>0){
			sqlbuf.append(" and b.ticketType LIKE '%"+ticketType+"%'");
		}
		if(keyTxt != null && keyTxt.length()>0){
			sqlbuf.append(" and (a.STYLEID LIKE '%"+keyTxt+"%' OR a."+strStyleName+" LIKE '%"+keyTxt+"%')");
		}
		sqlbuf.append(" ORDER BY b.SORTID,a.styleId");
		sql = sqlbuf.toString();
		return sql;
	}
	
	/**
	 * 因为有三种语言别 根据Token 语言别 判断查找的是哪个语言名称
	 * @return
	 */
	private Map<String, Object> getNames(DCP_TicketStyleQueryReq req){
		Map<String, Object> map = new HashMap<>();
		String langType = req.getLangType();
		String styleName = null;
		String ticketName = null;
		if (langType.equals("zh_CN")) {
			styleName = "STYLENAME_CN";
			ticketName = "TICKETNAME_CN";
		} else if (langType.equals("zh_TW")) {
			styleName = "STYLENAME_TW";
			ticketName = "TICKETNAME_TW";
		} else if (langType.equals("en_US")) {
			styleName = "STYLENAME_EN";
			ticketName = "TICKETNAME_EN";
		}
		map.put("styleName", styleName);
		map.put("ticketName", ticketName);
		return map;
	}

}
