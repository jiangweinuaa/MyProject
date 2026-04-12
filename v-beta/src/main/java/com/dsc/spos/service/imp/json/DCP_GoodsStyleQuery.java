package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsStyleQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStyleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsStyleQuery extends SPosBasicService<DCP_GoodsStyleQueryReq, DCP_GoodsStyleQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsStyleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsStyleQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStyleQueryReq>(){};
	}

	@Override
	protected DCP_GoodsStyleQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStyleQueryRes();
	}

	@Override
	protected DCP_GoodsStyleQueryRes processJson(DCP_GoodsStyleQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql = null;
		DCP_GoodsStyleQueryRes res = null;
		res = this.getResponse();

		int totalRecords;								//总笔数
		int totalPages;									//总页数
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);


			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("STYLENO", true);		
			//调用过滤函数
			List<Map<String, Object>> styleDatas=MapDistinct.getMap(getQData, condition);
			res.setDatas(new ArrayList<DCP_GoodsStyleQueryRes.level1Elm>());

			for (Map<String, Object> oneData : styleDatas) 
			{
				DCP_GoodsStyleQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setChildren(new ArrayList<DCP_GoodsStyleQueryRes.level2Elm>());
				String styleNO = oneData.get("STYLENO").toString();
				String styleName = oneData.get("STYLENAME").toString();
				String fileName = oneData.get("FILENAME").toString();

				String status = oneData.get("STATUS").toString();
				oneLv1.setStyleNo(styleNO);
				oneLv1.setStyleName(styleName);
				oneLv1.setFileName(fileName);
				oneLv1.setStatus(status);

				for (Map<String, Object> oneData2 : getQData) 
				{
					DCP_GoodsStyleQueryRes.level2Elm oneLv2 = res.new level2Elm();
					//过滤属于此单头的明细
					if(styleNO.equals(oneData2.get("STYLENO")))
					{
						String pluNO = oneData2.get("PLUNO").toString();
						if(pluNO.trim().equals("")) continue;//过滤掉空值

						String pluName = oneData2.get("PLUNAME").toString();
						String pluShowName = oneData2.get("PLUSHOWNAME").toString();
						String specNO = oneData2.get("SPECNO").toString();
						String specName = oneData2.get("SPECNAME").toString();
						String flavorNO= oneData2.get("FLAVORNO").toString();
						String flavorName = oneData2.get("FLAVORNAME").toString();
						String lstatus = oneData2.get("LSTATUS").toString();
						oneLv2.setPluNo(pluNO);
						oneLv2.setPluName(pluName);
						oneLv2.setPluShowName(pluShowName);
						oneLv2.setSpecNo(specNO);
						oneLv2.setSpecName(specName);
						oneLv2.setFlavorNo(flavorNO);
						oneLv2.setFlavorName(flavorName);
						oneLv2.setStatus(lstatus);

						oneLv1.getChildren().add(oneLv2);
						oneLv2 = null;
					}
				}

				res.getDatas().add(oneLv1);

				oneLv1 = null;

			}

		}
		else{
			res.setDatas(new ArrayList<DCP_GoodsStyleQueryRes.level1Elm>());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsStyleQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer("");

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String eId = req.geteId();
		String status=req.getRequest().getStatus();
		String keyTxt=req.getRequest().getKeyTxt();
		String specNO = req.getRequest().getSpecNo();
		String flavorNO = req.getRequest().getFlavorNo();

		String langType = req.getLangType();
		sqlbuf.append("SELECT * FROM ( "
				+ "  SELECT  COUNT( DISTINCT a.styleNO) OVER() NUM , dense_Rank() OVER(ORDER BY styleNO) rn ,  a.*  FROM ( "
				+ " (  SELECT  a.styleno , a.style_name AS styleName ,a.status,a.picture_Name as fileName, "
				+ "  b.pluNo , c.plu_name AS pluName  , b.pluShowName,  b.spec_No AS specno , d.spec_Name AS specName  , "
				+ "b.flavorNO ,e.flavorName ,b.status as lstatus  ,a.EID  "
				+ "  FROM DCP_STYLE a  "
				+ "  LEFT JOIN DCP_STYLE_detail b ON a.EID = b.EID AND a.styleno = b.styleno  "
				+ "  LEFT JOIN DCP_GOODS_lang c ON b.EID = c.EID AND b.pluno = c.pluno AND c.lang_type = '"+langType+"' "
				+ "  LEFT JOIN DCP_SPEC d ON b.spec_no = d.spec_no AND b.EID = d.EID  "
				+ "  LEFT JOIN DCP_FLAVOR  e ON b.flavorNO = e.flavorNO AND b.EID = e.EID  "
				+ "  WHERE a.EID = '"+eId+"' ");

		if(status != null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (b.pluNO like '%%"+keyTxt+"%%'  OR c.plu_Name LIKE '%%"+keyTxt+"%%'  ) ");
		}

		if(specNO != null && specNO.length()>0)
		{
			sqlbuf.append(" and b.spec_NO like'" + specNO + "' ");
		}
		if(flavorNO != null && flavorNO.length()>0)
		{
			sqlbuf.append(" and b.flavorNO like'" + flavorNO + "' ");
		}
		sqlbuf.append( " GROUP BY a.styleno, b.pluNO, b.spec_NO, b.flavorNO , a. EID ,b.status, b.pluShowName,"
				+ " a.style_name, a.status, c.plu_Name, d.spec_Name, e.flavorName ,a.picture_Name ) a  ) ) ");

		//sqlbuf.append(" where  rn>"+startRow+" and rn<="+(startRow+pageSize));
		sqlbuf.append(" ORDER BY pluNO  ");

		sql = sqlbuf.toString();
		return sql;
	}

}
