package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TouchMenuQueryReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuQueryRes;
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
/**
 * 服務函數：DCP_TouchMenuQuery
 * 服务说明：触屏菜单查询
 * @author wangzyc
 * @since  2021-06-15
 */
public class DCP_TouchMenuQuery extends SPosBasicService<DCP_TouchMenuQueryReq, DCP_TouchMenuQueryRes> {


	@Override
	protected boolean isVerifyFail(DCP_TouchMenuQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_TouchMenuQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TouchMenuQueryReq>(){};
	}

	@Override
	protected DCP_TouchMenuQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TouchMenuQueryRes();
	}

	@Override
	protected DCP_TouchMenuQueryRes processJson(DCP_TouchMenuQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		DCP_TouchMenuQueryRes res = null;
		res = this.getResponse();
		int totalRecords;								//总笔数
		int totalPages;

		try
		{
			String langType = req.getLangType();

			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQDataDetail.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				res.setDatas(new ArrayList<DCP_TouchMenuQueryRes.level1Elm>());

				// 过滤
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
				condition.put("MENUNO", true);
				condition.put("LANG_TYPE", true);
				condition.put("MENUNAME", true);
				// 调用过滤函数
				List<Map<String, Object>> getLang = MapDistinct.getMap(getQDataDetail, condition);

				// 过滤
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
				condition2.put("MENUNO", true);
				condition2.put("RANGETYPE", true);
				condition2.put("ID", true);
				// 调用过滤函数
				List<Map<String, Object>> getRange = MapDistinct.getMap(getQDataDetail, condition2);

				// 过滤
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); // 查詢條件
				condition3.put("MENUNO", true);
				// 调用过滤函数
				List<Map<String, Object>> getHeader = MapDistinct.getMap(getQDataDetail, condition3);

				for (Map<String, Object> oneData : getHeader)
				{
					DCP_TouchMenuQueryRes.level1Elm level1Elm = res.new level1Elm();
					String menuNo = oneData.get("MENUNO").toString();
					String memo = oneData.get("MEMO").toString();
					String priority = oneData.get("PRIORITY").toString();
					String status = oneData.get("STATUS").toString();
					String beginDate = oneData.get("BEGINDATE").toString();
					String endDate = oneData.get("ENDDATE").toString();
					String restrictShop = oneData.get("RESTRICTSHOP").toString();
					String restrictChannel = oneData.get("RESTRICTCHANNEL").toString();
					String restrictPeriod = oneData.get("RESTRICTPERIOD").toString();

					level1Elm.setMenuNo(menuNo);
					level1Elm.setMemo(memo);
					level1Elm.setPriority(priority);
					level1Elm.setStatus(status);
					level1Elm.setBeginDate(beginDate);
					level1Elm.setEndDate(endDate);
					level1Elm.setRestrictShop(restrictShop);
					level1Elm.setRestrictChannel(restrictChannel);
					level1Elm.setRestrictPeriod(restrictPeriod);
					level1Elm.setMenuName("");

					level1Elm.setMenuName_lang(new ArrayList<DCP_TouchMenuQueryRes.level2Elm>());
					for (Map<String, Object> oneData2 : getLang) {
						String menuNo2 = oneData2.get("MENUNO").toString();

						// 过滤不属于当前菜单的语言别
						if(!menuNo.equals(menuNo2)){
							continue;
						}
						DCP_TouchMenuQueryRes.level2Elm level2Elm = res.new level2Elm();
						String lang_type = oneData2.get("LANG_TYPE").toString();
						String menuName = oneData2.get("MENUNAME").toString();

						if(lang_type.equals(langType)){
							level1Elm.setMenuName(menuName);
						}

						level2Elm.setLangType(lang_type);
						level2Elm.setName(menuName);

						level1Elm.getMenuName_lang().add(level2Elm);
					}

					level1Elm.setRangeList(new ArrayList<DCP_TouchMenuQueryRes.level3Elm>());
					for (Map<String, Object> oneData3 : getRange) {
						String menuNo3 = oneData3.get("MENUNO").toString();

						// 过滤不属于当前菜单的适用组织渠道
						if(!menuNo.equals(menuNo3)){
							continue;
						}

						DCP_TouchMenuQueryRes.level3Elm level3Elm = res.new level3Elm();
						String rangeType = oneData3.get("RANGETYPE").toString();
						String id = oneData3.get("ID").toString();
						String name = oneData3.get("NAME").toString();

						level3Elm.setRangeType(rangeType);
						level3Elm.setId(id);
						level3Elm.setName(name);

						level1Elm.getRangeList().add(level3Elm);
					}


					res.getDatas().add(level1Elm);
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

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
	protected String getQuerySql(DCP_TouchMenuQueryReq req) throws Exception {
		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
        DCP_TouchMenuQueryReq.level1Elm request = req.getRequest();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String shopId = request.getShopId();
        String channelId = request.getChannelId();
		String langType = req.getLangType();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append("SELECT * FROM ( " +
				" SELECT count(DISTINCT a.EID||a.MENUNO) OVER () AS num , DENSE_RANK() OVER (ORDER BY a.EID, a.RESTRICTSHOP DESC, a.CREATETIME DESC, a.MENUNO) AS rn, " +
				" a.MENUNO, b.LANG_TYPE , b.MENUNAME, a.MEMO, a.PRIORITY, a.STATUS, a.BEGINDATE , a.ENDDATE, a.RESTRICTSHOP, a.RESTRICTCHANNEL, a.RESTRICTPERIOD, c.RANGETYPE , " +
				" c.ID, c.NAME,a.CREATETIME  " +
				" FROM DCP_TOUCHMENU a" +
				" LEFT JOIN DCP_TOUCHMENU_LANG b ON a.EID = b.EID AND a.MENUNO = b.MENUNO " +
				" LEFT JOIN DCP_TOUCHMENU_RANGE c ON a.EID = c.EID AND a.MENUNO = c.MENUNO " +
				" LEFT JOIN DCP_TOUCHMENU_RANGE d ON a.EID  = d.EID  AND a.MENUNO  = d.MENUNO  AND d.RANGETYPE  = '1' " +
				" where a.EID = '"+eId+"' ");
		if(!Check.Null(keyTxt)){
			sqlbuf.append(" AND (a.MENUNO LIKE '%%"+keyTxt+"%%' OR  (b.LANG_TYPE = '"+langType+"' and b.MENUNAME LIKE '%%"+keyTxt+"%%')) ");
		}

		if(!Check.Null(status)){
			sqlbuf.append(" AND a.status = "+status+"");
		}

		if(!Check.Null(shopId)){
			sqlbuf.append(" AND (a.RESTRICTSHOP = 0 OR (a.RESTRICTSHOP = 1 AND c.ID = '"+shopId+"') OR a.RESTRICTSHOP = 2 AND not exists(SELECT *  FROM DCP_TOUCHMENU_RANGE WHERE a.EID = EID AND a.MENUNO = MENUNO  AND RANGETYPE = '2' AND ID = '"+shopId+"')) "
					+ " AND d.ID = (SELECT BELFIRM FROM DCP_ORG WHERE ORGANIZATIONNO = '"+shopId+"' AND EID = '"+eId+"') ");
		}

		if(!Check.Null(channelId)){
			sqlbuf.append(" AND (a.RESTRICTCHANNEL = 0 OR " +
					" a.RESTRICTCHANNEL = 1 AND c.ID = '"+channelId+"' " +
					" OR a.RESTRICTCHANNEL = 2 AND not exists( " +
					" SELECT * FROM DCP_TOUCHMENU_RANGE WHERE a.EID = EID AND a.MENUNO = MENUNO AND RANGETYPE = '3' AND ID = '"+channelId+"'))");
		}

		sqlbuf.append(" ) WHERE rn > "+startRow+" AND rn <= "+(startRow+pageSize)+"");

        sql=sqlbuf.toString();
		return sql;
	}

}
