package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateQueryReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateQueryReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_MinQtyTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MinQtyTemplateQuery 
 * 服务说明：起售量模板查询
 * @author wangzyc
 * @since 2020-11-10 引用SPosBasicService
 */
public class DCP_MinQtyTemplateQueryServiceImp extends SPosBasicService<DCP_MinQtyTemplateQueryReq, DCP_MinQtyTemplateQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_MinQtyTemplateQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_MinQtyTemplateQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MinQtyTemplateQueryReq>() {
		};
	}

	@Override
	protected DCP_MinQtyTemplateQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MinQtyTemplateQueryRes();
	}

	@Override
	protected DCP_MinQtyTemplateQueryRes processJson(DCP_MinQtyTemplateQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql = null;

		// 查詢資料
		DCP_MinQtyTemplateQueryRes res = null;
		res = this.getResponse();

		String langType = req.getLangType();
		String eId = req.geteId();
		String companyId = req.getBELFIRM();
		String shopId = req.getShopId();

		try
		{
			// 单头查询
			sql = this.getQuerySql(req);
			String[] condCountValues = { langType, eId }; // 查询条件
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
			int totalRecords; // 总笔数
			int totalPages; // 总页数

			if (getQData_Count != null && getQData_Count.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQData_Count.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords = Integer.parseInt(num);

				// 算总页数
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				String sJoinTemplateId="";
				String sJoinEid="";
				for (Map<String, Object> tempmap : getQData_Count)
				{
					sJoinTemplateId+=tempmap.get("TEMPLATEID").toString()+",";
					sJoinEid+=tempmap.get("EID").toString()+",";
				}

				//
				Map<String, String> mapOrder=new HashMap<String, String>();
				mapOrder.put("TEMPLATEID", sJoinTemplateId);
				mapOrder.put("EID", sJoinEid);
				//
				MyCommon cm=new MyCommon();
				String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
				mapOrder=null;
				mapOrder=null;
				cm=null;

				if (withasSql_Orderno.equals(""))
				{
					res.setSuccess(false);
					res.setServiceDescription("查询失败--模板编号转换成临时表的方法处理失败！");
					return res;
				}

				sql = this.getQuery_detailSql(req,withasSql_Orderno);
				String[] conditionValues1 = { eId }; // 查詢條件
				List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);

				if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
				{
					// 单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
					condition.put("TEMPLATEID", true);
					// 调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
					res.setDatas(new ArrayList<DCP_MinQtyTemplateQueryRes.level1Elm>());

					// 时间格式化
					SimpleDateFormat simptemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化
					SimpleDateFormat updatetemp = new SimpleDateFormat("yyyyMMddHH:mm:ss"); // 修改时间格式化
					for (Map<String, Object> oneData : getQHeader)
					{
						DCP_MinQtyTemplateQueryRes.level1Elm oneLv1 = res.new level1Elm();

						// 取出第一层

						String templateId = oneData.get("TEMPLATEID").toString();
						String templateName = oneData.get("TEMPLATENAME").toString();
						String memo = oneData.get("MEMO").toString();
						String status1 = oneData.get("STATUS").toString();
						String restrictShop = oneData.get("RESTRICTSHOP").toString();
						Date CREATETIME = simptemp.parse(oneData.get("CREATETIME").toString());
						String createtime = simptemp.format(CREATETIME);
						String createopid = oneData.get("CREATEOPID").toString();
						String createopname = oneData.get("CREATEOPNAME").toString();
						Date LASTTIME = simptemp.parse(oneData.get("LASTMODITIME").toString());
						String updateTime = simptemp.format(LASTTIME);
						String lastmodiopid = oneData.get("LASTMODIOPID").toString();
						String lastmodiname = oneData.get("LASTMODINAME").toString();

						// 设置响应
						oneLv1.setTemplateId(templateId);
						oneLv1.setTemplateName(templateName);
						oneLv1.setMemo(memo);
						oneLv1.setStatus(status1);
						oneLv1.setRestrictShop(restrictShop);
						oneLv1.setCreatetime(createtime);
						oneLv1.setCreateopid(createopid);
						oneLv1.setCreateopname(createopname);
						oneLv1.setUpdateTime(updateTime);
						oneLv1.setLastmodiopid(lastmodiopid);
						oneLv1.setLastmodiname(lastmodiname);

						oneLv1.setShopList(new ArrayList<DCP_MinQtyTemplateQueryRes.level2Elm>());

						Map<String, Object> cond=new HashMap<>();
						cond.put("TEMPLATEID",templateId);
						List<Map<String, Object>>  tempList=MapDistinct.getWhereMap(getQDataDetail,cond,true);

						//去重ID
						Map<String, Boolean> condB=new HashMap<>();
						condB.put("ID",true);
						List<Map<String, Object>>  ids=MapDistinct.getMap(tempList,condB);

						// 判断适用门店范围
						for (Map<String, Object> oneData2 : ids)
						{
							if (!Check.Null(oneData.get("ID").toString()))
							{
								DCP_MinQtyTemplateQueryRes.level2Elm oneLv2 = res.new level2Elm();
								String id = oneData2.get("ID").toString();
								String name = oneData2.get("NAME").toString();

								oneLv2.setId(id);
								oneLv2.setName(name);
								// 添加
								oneLv1.getShopList().add(oneLv2);
							}
						}
						oneLv1.setPluList(new ArrayList<DCP_MinQtyTemplateQueryRes.level3Elm>());

						//去重PLUNO
						condB=new HashMap<>();
						condB.put("PLUNO",true);
						List<Map<String, Object>>  plunos=MapDistinct.getMap(tempList,condB);
						//
						for (Map<String, Object> oneData3 : plunos)
						{
							DCP_MinQtyTemplateQueryRes.level3Elm oneLv3 = res.new level3Elm();
							String pluNo = oneData3.get("PLUNO").toString();
							String minQty = oneData3.get("MOQ").toString();
							String pluName = oneData3.get("PLU_NAME").toString();
							String punitName = oneData3.get("UNAME").toString();
							String price = oneData3.get("PRICE").toString();
							oneLv3.setPrice(price);
							oneLv3.setPluNo(pluNo);
							oneLv3.setMinQty(minQty);
							oneLv3.setPluName(pluName);
							oneLv3.setPunitName(punitName);
							// 添加
							oneLv1.getPluList().add(oneLv3);
						}
						// 添加
						res.getDatas().add(oneLv1);
					}

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
	protected String getQuerySql(DCP_MinQtyTemplateQueryReq req) throws Exception
	{
		String sql = null;
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		List<level2Elm> shop = req.getRequest().getShop();
		if (keyTxt == null)
			keyTxt = "";

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (select count(*) over() num, rownum rn,a.* from ( " + "SELECT DISTINCT a.EID, a.TEMPLATEID FROM DCP_MINQTYTEMPLATE a "
							  + "left join  DCP_MINQTYTEMPLATE_LANG b on a.eid = b.eid and a.templateid = b.templateid  and b.lang_type = ?");
		if (shop != null && shop.isEmpty() == false)
		{
			sqlbuf.append(" LEFT JOIN DCP_MINQTYTEMPLATE_RANGE c ON a.eid = c.eid  AND a.templateid = c.templateid  ");
		}
		sqlbuf.append(" where a.eId = ?");
		if (status != null && status.length() > 0)
		{
			sqlbuf.append(" AND a.STATUS = '" + status + "' ");
		}
		if (shop != null && shop.isEmpty() == false)
		{
			sqlbuf.append(" AND ( c.ID in ( ");
			for (level2Elm level2Elm : shop)
			{
				sqlbuf.append("'" + level2Elm.getShopId() + "',");
			}
			sqlbuf.deleteCharAt(sqlbuf.length() - 1);
			sqlbuf.append("  )  OR a.RESTRICTSHOP = 0)");
		}

		if (keyTxt != null && keyTxt.length() > 0)
		{
			sqlbuf.append(" AND (a.templateid LIKE '%%" + keyTxt + "%%' or b.templatename LIKE '%%" + keyTxt + "%%') ");
		}

		sqlbuf.append(" ) a ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlbuf.toString();

		return sql;
	}

	private String getQuery_detailSql(DCP_MinQtyTemplateQueryReq req,String withasSql) throws Exception
	{
		// TODO 自动生成的方法存根
		String sql = null;
		String langType = req.getLangType();

		StringBuffer sqlbuf = new StringBuffer("with p AS ("+ withasSql + " ) "
													   + "SELECT a.templateId , b.templateName, c.ID, c.NAME, a.memo , a.status, a.RESTRICTSHOP, d.PLUNO, d.MOQ, a.createtime, a.createopid ,"
													   + " a.createopname, a.lastmoditime, a.lastmodiopid, a.lastmodiopname AS lastmodiname,f.plu_name,g.uname,e.WUNIT,e.PUNIT,"
													   + " e.baseunit,e.price  "
													   + "FROM p "
													   + "INNER JOIN DCP_MINQTYTEMPLATE a ON p.EID=a.EID AND p.templateid=a.templateid "
													   + "LEFT JOIN DCP_MINQTYTEMPLATE_LANG b ON a.eid = b.eid AND a.templateid = b.templateid AND b.lang_type = '"
													   + langType + "'"
													   + "LEFT JOIN DCP_MINQTYTEMPLATE_RANGE c ON a.eid = c.eid  AND a.templateId = c.templateId "
													   + "LEFT JOIN DCP_MINQTYTEMPLATE_DETAIL d ON a.eid = d.eid AND a.templateid = d.templateid "
													   + "LEFT JOIN DCP_GOODS e ON a.eid = e.eid AND d.PLUNO = e.PLUNO "
													   + "LEFT JOIN DCP_GOODS_LANG f ON e.PLUNO = f.PLUNO AND a.eid = f.eid AND f.LANG_TYPE ='"
													   + langType + "' "
													   + "LEFT JOIN DCP_UNIT_LANG g ON e.PUNIT = g.UNIT AND a.eid = g.eid AND g.LANG_TYPE ='"
													   + langType + "' ");

		sqlbuf.append(" ORDER BY a.createtime desc ");
		sql = sqlbuf.toString();
		return sql;
	}



}
