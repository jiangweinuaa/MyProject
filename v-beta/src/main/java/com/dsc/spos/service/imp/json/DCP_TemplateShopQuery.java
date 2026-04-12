package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_TemplateShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_TemplateShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：PTemplateGet
 *   說明：要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_TemplateShopQuery extends SPosBasicService<DCP_TemplateShopQueryReq,DCP_TemplateShopQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_TemplateShopQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (Check.Null(req.getRequest().getTemplateNo())) 
		{
			errCt++;
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getRequest().getDocType())) 
		{
			errCt++;
			errMsg.append("模板类型不可为空值, ");
			isFail = true;
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TemplateShopQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TemplateShopQueryReq>(){};
	}

	@Override
	protected DCP_TemplateShopQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TemplateShopQueryRes();
	}

	@Override
	protected DCP_TemplateShopQueryRes processJson(DCP_TemplateShopQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_TemplateShopQueryRes res = null;
		res = this.getResponse();	
		try
		{
			//得判断模板是不是全部组织
			String tempSql="select * from dcp_ptemplate a where a.eid='"+req.geteId()+"' and a.ptemplateno='"+req.getRequest().getTemplateNo()+"' ";
			List<Map<String, Object>> tempList = this.doQueryData(tempSql, null);
			if(CollUtil.isEmpty(tempList)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在");
			}
			String shopType = tempList.get(0).get("SHOPTYPE").toString();
			//单头查询
			if("1".equals(shopType)){
				sql = this.getQuerySql2(req);
			}else {
				sql = this.getQuerySql(req);
			}
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_TemplateShopQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{

				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_TemplateShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("ORG_NAME").toString();
					String goodsWarehouseNO = oneData.get("GOODSWAREHOUSE").toString();
					String goodsWarehouseName = oneData.get("GOODSWAREHOUSENAME").toString();
					String materialWarehouseNO = oneData.get("MATERIALWAREHOUSE").toString();
					String materialWarehouseName = oneData.get("MATERIALWAREHOUSENAME").toString();
                    String organizationno = oneData.get("ORGANIZATIONNO").toString();
                    String organizationName = oneData.get("ORGANIZATIONNAME").toString();
                    String warehouse = oneData.get("WAREHOUSE").toString();
                    String warehouseName = oneData.get("WAREHOUSENAME").toString();

                    //设置响应
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);		
					oneLv1.setGoodsWarehouseNO(goodsWarehouseNO);		
					oneLv1.setGoodsWarehouseName(goodsWarehouseName);
					oneLv1.setMaterialWarehouseNO(materialWarehouseNO);
					oneLv1.setMaterialWarehouseName(materialWarehouseName);
                    oneLv1.setOrganizationNO(organizationno);
                    oneLv1.setOrganizationName(organizationName);
                    oneLv1.setWarehouse(warehouse);
                    oneLv1.setWarehouseName(warehouseName);

					res.getDatas().add(oneLv1);
				}
			}	

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
	protected String getQuerySql(DCP_TemplateShopQueryReq req) throws Exception {

		String sql=null;			
		String eId = req.geteId();
		String pTemplateNO = req.getRequest().getTemplateNo();
		String docType = req.getRequest().getDocType();
		String langType= req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append(  " "
				+ " select a.SHOPID,a.status,a.EID,b.org_name,a.goodswarehouse,c.warehouse_name as goodswarehousename,a.materialwarehouse,d.warehouse_name as materialwarehousename,"
                + " e.organizationno,e.org_name as organizationname,a.warehouse,f.warehouse_name as warehousename   "
				+ " from  DCP_ptemplate_shop a "
				+ " left join DCP_ORG_lang b on b.EID=a.EID and b.organizationno=a.SHOPID and b.status='100'  and b.lang_type='"+langType +"'  "
				+ " left join DCP_WAREHOUSE_lang c on a.EID=c.EID and a.SHOPID=c.organizationno and a.goodswarehouse=c.warehouse and c.lang_type='"+langType +"' and c.status='100' "
				+ " left join DCP_WAREHOUSE_lang d on a.EID=d.EID and a.SHOPID=d.organizationno and a.materialwarehouse=d.warehouse and d.lang_type='"+langType +"' and d.status='100' "
                + " left join DCP_ORG_lang e on e.EID=a.EID and e.organizationno=a.organizationno  and e.lang_type='"+langType +"'  "
                + " left join DCP_WAREHOUSE_lang f on a.EID=f.EID and e.organizationno=f.organizationno and a.warehouse=f.warehouse and f.lang_type='"+langType +"'  "
                + "	where a.EID='"+eId +"' and a.ptemplateno='"+pTemplateNO +"' "
				+ " and a.doc_type='"+docType +"' and a.status='100'  order by a.SHOPID   ");

		sql = sqlbuf.toString();
		return sql;
	}

	protected String getQuerySql2(DCP_TemplateShopQueryReq req) throws Exception {

		String sql=null;
		String eId = req.geteId();

		String langType= req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append(  " "
				+ " select '' as SHOPID,a.status,a.EID,'' as org_name,'' as goodswarehouse,'' as goodswarehousename,'' as materialwarehouse,'' as  materialwarehousename,"
				+ " e.organizationno,e.org_name as organizationname,f.warehouse,f.warehouse_name as warehousename   "
				+ " from dcp_org a "
				+ " left join DCP_ORG_lang e on e.EID=a.EID and e.organizationno=a.organizationno   and e.lang_type='"+langType +"'  "
				+ " left join DCP_WAREHOUSE_lang f on a.EID=f.EID and e.organizationno=f.organizationno and f.lang_type='"+langType +"'  "
				+ "	where a.EID='"+eId +"'  "
				+ " and  a.status='100'  order by a.organizationno   ");

		sql = sqlbuf.toString();
		return sql;
	}


}


