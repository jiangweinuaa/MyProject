package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_GoodsTemplateGoodsQuery extends SPosBasicService<DCP_GoodsTemplateGoodsQueryReq,DCP_GoodsTemplateGoodsQueryRes> {
	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateGoodsQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String templateId = req.getRequest().getTemplateId();
		if (Check.Null(templateId) )
		{
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getSortType()) )
		{
			errMsg.append("显示顺序不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateGoodsQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateGoodsQueryReq>(){} ;
	}

	@Override
	protected DCP_GoodsTemplateGoodsQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateGoodsQueryRes();
	}

	@Override
	protected DCP_GoodsTemplateGoodsQueryRes processJson(DCP_GoodsTemplateGoodsQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_GoodsTemplateGoodsQueryRes res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQDataList = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_GoodsTemplateGoodsQueryRes.level1Elm>());
		if(getQDataList!=null&&getQDataList.isEmpty()==false)
		{
			String num = getQDataList.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			for (Map<String, Object> map : getQDataList)
			{
				DCP_GoodsTemplateGoodsQueryRes.level1Elm goods = res.new level1Elm();

				goods.setCanEstimate(map.get("CANESTIMATE").toString());
				goods.setCanFree(map.get("CANFREE").toString());
				goods.setCanOrder(map.get("CANORDER").toString());
				goods.setCanPurchase(map.get("CANPURCHASE").toString());
				goods.setCanRequire(map.get("CANREQUIRE").toString());
				goods.setCanRequireBack(map.get("CANREQUIREBACK").toString());
				goods.setCanReturn(map.get("CANRETURN").toString());
				goods.setCanSale(map.get("CANSALE").toString());
				goods.setClearType(map.get("CLEARTYPE").toString());
				goods.setIsAutoSubtract(map.get("IS_AUTO_SUBTRACT").toString());
				goods.setMaxQty(map.get("MAXQTY").toString());
				goods.setMinQty(map.get("MINQTY").toString());
				goods.setMultiQty(map.get("MULQTY").toString());
				goods.setPluNo(map.get("PLUNO").toString());
				goods.setPluName(map.get("PLU_NAME").toString());
				goods.setSafeQty(map.get("SAFEQTY").toString());
				goods.setStatus(map.get("STATUS").toString());
				goods.setWarningQty(map.get("WARNINGQTY").toString());
				goods.setRedisUpdateSuccess(map.get("REDISUPDATESUCCESS").toString());
				goods.setIsNewGoods(map.get("ISNEWGOODS").toString());
				goods.setIsAllot(map.get("ISALLOT").toString());
                goods.setCategory(map.getOrDefault("CATEGORY","").toString());
                goods.setCategoryName(map.getOrDefault("CATEGORY_NAME","").toString());

                goods.setSupplierType(map.getOrDefault("SUPPLIERTYPE","").toString());
                goods.setSupplierId(map.getOrDefault("SUPPLIERID","").toString());
                goods.setSupplierName(map.getOrDefault("SUPPLIERNAME","").toString());
                goods.setCreateOpId(map.getOrDefault("CREATEOPID","").toString());
                goods.setCreateOpName(map.getOrDefault("CREATEOPNAME","").toString());
                goods.setCreateTime(map.getOrDefault("CREATETIME","").toString());
                goods.setLastModiOpId(map.getOrDefault("LASTMODIOPID","").toString());
                goods.setLastModiOpName(map.getOrDefault("LASTMODIOPNAME","").toString());
                goods.setLastModiTime(map.getOrDefault("LASTMODITIME","").toString());

				res.getDatas().add(goods);
			}
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected String getQuerySql(DCP_GoodsTemplateGoodsQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String sql = null;
		String eId = req.geteId();
		String templateId = req.getRequest().getTemplateId();
		String sortType = req.getRequest().getSortType();
		String keyTxt = req.getRequest().getKeyTxt();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		String redisUpdateSuccess = req.getRequest().getRedisUpdateSuccess();
		String[] categoryList = req.getRequest().getCategory();


		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" select * from (");
		sqlbuf.append(" select count(*) over() num,rownum  rn,A.* from ( ");
		sqlbuf.append(" select  A.*,B.PLU_NAME,C.CATEGORY,CL.CATEGORY_NAME,e1.op_name as createopname,e2.op_name as lastmodiopname," +
				" case when a.suppliertype='SUPPLIER' THEN p.Sname ELSE o.ORG_NAME end as suppliername from DCP_GOODSTEMPLATE_GOODS A ");
		sqlbuf.append(" left join DCP_GOODS_LANG B on A.EID=B.EID AND A.PLUNO=B.PLUNO AND B.LANG_TYPE='"+curLangType+"' ");
        sqlbuf.append(" left join DCP_GOODS C on A.EID=C.EID AND A.PLUNO=C.PLUNO");
        sqlbuf.append(" left join DCP_CATEGORY_LANG CL on C.EID=CL.EID AND C.CATEGORY=CL.CATEGORY AND CL.LANG_TYPE='"+curLangType+"' " );
        sqlbuf.append(" left join PLATFORM_STAFFS_LANG e1 on e1.opno=a.CREATEOPID and a.eid=e1.eid and e1.lang_type='"+req.getLangType()+"'  " +
                " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno =a.lastmodiopid and e2.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner p on p.eid=a.eid and p.BIZPARTNERNO=a.supplierid  " +
				" left join DCP_ORG_LANG o on o.eid=a.eid and o.organizationno=a.supplierid and o.lang_type='"+curLangType+"'");
		sqlbuf.append(" where A.TEMPLATEID='"+templateId+"' and A.EID='"+eId+"' ");

		if(redisUpdateSuccess != null && redisUpdateSuccess.length() >0)
		{
			sqlbuf.append(" and a.REDISUPDATESUCCESS='"+redisUpdateSuccess +"' ");
		}
		if(keyTxt!=null&&keyTxt.isEmpty()==false)
		{
			sqlbuf.append(" and (A.pluno like '%%"+keyTxt+"%%' or B.plu_name like '%%"+keyTxt+"%%' )");
		}
		if (categoryList!=null&&categoryList.length>0)
        {
            String categorySqlConditon= PosPub.getArrayStrSQLIn(categoryList);
            sqlbuf.append("and C.CATEGORY in ("+categorySqlConditon+") ");
        }
		if(sortType!=null&&sortType.equals("1"))//：1-添加顺序降序2-商品编码升序
		{

			sqlbuf.append(" order by A.item desc ");
		}
		else
		{
			sqlbuf.append(" order by A.pluno  ");
		}

		sqlbuf.append(" ) A ");
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize)+" ");
		sql = sqlbuf.toString();
		return sql;
	}

}
