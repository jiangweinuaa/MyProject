package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_TGCommissionQueryReq;
import com.dsc.spos.json.cust.res.DCP_TGCommissionQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：TGCommissionGetDCP
 * 服务说明：团务拆账查询
 * @author jinzma 
 * @since  2019-02-12
 */
public class DCP_TGCommissionQuery extends SPosBasicService <DCP_TGCommissionQueryReq,DCP_TGCommissionQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_TGCommissionQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_TGCommissionQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGCommissionQueryReq>(){};
	}

	@Override
	protected DCP_TGCommissionQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGCommissionQueryRes();
	}

	@Override
	protected DCP_TGCommissionQueryRes processJson(DCP_TGCommissionQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		DCP_TGCommissionQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("TRAVELNO", true);	
				condition.put("TGCATEGORYNO", true);	
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
				res.setDatas(new ArrayList<DCP_TGCommissionQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQHeader) {
					DCP_TGCommissionQueryRes.level1Elm oneLv1 = res.new level1Elm(); 

					String travelNO = oneData.get("TRAVELNO").toString();
					String travelName = oneData.get("TRAVELNAME").toString();
					String tgCategoryNO = oneData.get("TGCATEGORYNO").toString();
					String tgCategoryName = oneData.get("TGCATEGORYNAME").toString();
					String shopBonus = oneData.get("SHOPBONUS").toString();
					String tempTourGroup = oneData.get("TEMPTOURGROUP").toString();
					String memo = oneData.get("MEMO").toString();
					String status = oneData.get("STATUS").toString();

					oneLv1.setTravelNO(travelNO);
					oneLv1.setTravelName(travelName);
					oneLv1.setTgCategoryNO(tgCategoryNO);
					oneLv1.setTgCategoryName(tgCategoryName);
					oneLv1.setShopBonus(shopBonus);
					oneLv1.setTempTourGroup(tempTourGroup);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setDatas(new ArrayList<DCP_TGCommissionQueryRes.level2Elm>());

					for (Map<String, Object> oneDataDetail : getQData) 
					{
						if(travelNO.equals(oneDataDetail.get("TRAVELNO")) && tgCategoryNO.equals(oneDataDetail.get("TGCATEGORYNO"))  )
						{							
							DCP_TGCommissionQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String guideNO = oneDataDetail.get("GUIDENO").toString();
							String guideName = oneDataDetail.get("GUIDENAME").toString();
							String travelRate = oneDataDetail.get("TRAVELRATE").toString();
							String guideRate = oneDataDetail.get("GUIDERATE").toString();
							String detailMemo = oneDataDetail.get("DETAILMEMO").toString();
							
							oneLv2.setGuideNO(guideNO);
							oneLv2.setGuideName(guideName);
							oneLv2.setTravelRate(travelRate);
							oneLv2.setGuideRate(guideRate);
							oneLv2.setMemo(detailMemo);
							oneLv1.getDatas().add(oneLv2);
						}
					}
					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_TGCommissionQueryRes.level1Elm>());				
				totalRecords = 0;
				totalPages = 0;			
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;		
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_TGCommissionQueryReq req) throws Exception {
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();
		String shopBonus = req.getShopBonus();
		String tempTourGroup = req.getTempTourGroup();
		String tgCategoryNO = req.getTgCategoryNO();
		String keyTxt= req.getKeyTxt();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( "select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.travelno,a.tgcategoryno) rn , "
				+ " a.EID,a.travelno,c.supplier_name as travelname, a.tgcategoryno,e.tgcategoryname,  "
				+ " a.shopbonus,a.temptourgroup,a.memo,a.status,b.guideno,d.supplier_name as guidename, "
				+ " b.travelrate,b.guiderate,b.memo as detailmemo from DCP_TGCOMMISSION  a " 
				+	" inner join  DCP_TGCOMMISSION_DETAIL b  on a.EID=b.EID and a.travelno=b.travelno "
				+ " and a.tgcategoryno=b.tgcategoryno "
				+ " left join DCP_SUPPLIER_lang c on a.EID=c.EID and a.travelno=c.supplier and c.lang_type='"+langType+"' and c.status='100' "
				+ " left join DCP_SUPPLIER_lang d on a.EID=d.EID and b.guideno=d.supplier and d.lang_type='"+langType+"' and d.status='100'  "
				+ " left join DCP_TGCATEGORY_lang e on a.EID=e.EID and a.tgcategoryno=e.tgcategoryno and e.type='2' and e.lang_type='"+langType+"' and e.status='100'  "
				+ " where a.EID='"+eId+"' " );

		if (!Check.Null(shopBonus))
		{
			sqlbuf.append(" and a.shopbonus='"+shopBonus+"' ");
		}
		if (!Check.Null(tempTourGroup))
		{
			sqlbuf.append(" and a.temptourgroup='"+tempTourGroup+"' ");
		}
		if (!Check.Null(tgCategoryNO))
		{
			sqlbuf.append(" and a.tgcategoryno='"+tgCategoryNO+"' ");
		}
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.travelno LIKE '%%"+ keyTxt +"%%' OR c.supplier_name LIKE '%%"+ keyTxt +"%%' ) ");
		}
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));		

		sql = sqlbuf.toString();
		return sql;
	}

}
