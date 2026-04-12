package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TicketTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_TicketTypeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_TicketTypeQuery 
 * 服务说明：小票类型查询
 * @author wangzyc
 * @since 2020-12-3
 */
public class DCP_TicketTypeQuery extends SPosBasicService<DCP_TicketTypeQueryReq, DCP_TicketTypeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_TicketTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getRequest().getRangeType())) {
			errMsg.append("机构类型不可为空值,");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TicketTypeQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketTypeQueryReq>() {
		};
	}

	@Override
	protected DCP_TicketTypeQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketTypeQueryRes();
	}

	@Override
	protected DCP_TicketTypeQueryRes processJson(DCP_TicketTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		// 查询资料
		DCP_TicketTypeQueryRes res = null;
		res = this.getResponse();
		String strticketName = getTicketName(req);
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_TicketTypeQueryRes.level1Elm>());

			if (getQData != null && getQData.isEmpty() == false) {
				for (Map<String, Object> oneData : getQData) {
					DCP_TicketTypeQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String ticketType = oneData.get("TICKETTYPE").toString();	
					String ticketName = oneData.get(strticketName).toString();	
					String memo = oneData.get("MEMO").toString();	
					
					oneLv1.setTicketType(ticketType);
					oneLv1.setTicketName(ticketName);
					oneLv1.setMemo(memo);
					
					res.getDatas().add(oneLv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
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
	protected String getQuerySql(DCP_TicketTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String ticketName = getTicketName(req);
		String rangeType = req.getRequest().getRangeType();
		String keyTxt = req.getRequest().getKeyTxt();
		
		sqlbuf.append("SELECT TICKETTYPE, " + ticketName + ", memo FROM DCP_TICKETTYPE where RANGETYPE = '"+rangeType+"'");
		if(keyTxt != null && keyTxt.length()>0){
			sqlbuf.append("and (TICKETTYPE LIKE '%%"+keyTxt+"%%' OR "+ticketName+" LIKE '%%"+keyTxt+"%%') ");
		}
		sqlbuf.append("ORDER BY SORTID");
		sql = sqlbuf.toString();
		return sql;
	}
	
	/**
	 * 因为有三种语言别 根据Token 语言别 判断查找的是哪个语言名称
	 * @return
	 */
	private String getTicketName(DCP_TicketTypeQueryReq req){
		String langType = req.getLangType();
		String ticketName = null;
		if (langType.equals("zh_CN")) {
			ticketName = "TICKETNAME_CN";
		} else if (langType.equals("zh_TW")) {
			ticketName = "TICKETNAME_TW";
		} else if (langType.equals("en_US")) {
			ticketName = "TICKETNAME_EN";
		}
		return ticketName;
	}

}
