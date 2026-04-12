package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsWeightPluUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsWeightPluUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsWeightPluUpdate extends SPosAdvanceService<DCP_GoodsWeightPluUpdateReq,DCP_GoodsWeightPluUpdateRes>
{

	@Override
	protected void processDUID(DCP_GoodsWeightPluUpdateReq req, DCP_GoodsWeightPluUpdateRes res) throws Exception
	{
		String eId=req.geteId();
        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);

		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String pluno=req.getRequest().getPluNo();
		String unit=req.getRequest().getUnit();
		String featureno=req.getRequest().getFeatureNo();
		String weightpluno=req.getRequest().getWeightPluNo()==null ?"":req.getRequest().getWeightPluNo();

		String sql="select * from Dcp_Goods_Weightplu where eid='"+eId+"' "
				+ "and pluno='"+pluno+"' and unit='"+unit+"' "
				+ "and featureno='"+featureno+"' ";

		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		if (getData!=null && getData.isEmpty()==false)//更新
		{
			UptBean ub1=new UptBean("Dcp_Goods_Weightplu");
			//add Value
			ub1.addUpdateValue("weightpluno", new DataValue(weightpluno, Types.VARCHAR));

			//condition
			ub1.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			ub1.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
			ub1.addCondition("FEATURENO", new DataValue(featureno, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));			
		}
		else 
		{
			String[] columns1 = {"EID","PLUNO", "UNIT","FEATURENO","WEIGHTPLUNO","LASTMODITIME"};
			DataValue[] insValue1 = new DataValue[]
					{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pluno, Types.VARCHAR), 
							new DataValue(unit, Types.VARCHAR),
							new DataValue(featureno, Types.VARCHAR),
							new DataValue(weightpluno, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)				
					};
			InsBean ib1 = new InsBean("Dcp_Goods_Weightplu", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
		}		

		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsWeightPluUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsWeightPluUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsWeightPluUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsWeightPluUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(Check.Null(req.getRequest().getPluNo()))
		{
			errMsg.append("商品编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getPluName()))
		{
			errMsg.append("商品名称不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getUnit()))
		{
			errMsg.append("商品单位不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getFeatureNo()))
		{
			errMsg.append("商品特征码不能为空值 ");
			isFail = true;
		}		
		if(Check.Null(req.getRequest().getWeightPluNo()))
		{
			errMsg.append("商品称重编码不能为空值 ");
			isFail = true;
		}
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsWeightPluUpdateReq> getRequestType() 
	{

		return new TypeToken<DCP_GoodsWeightPluUpdateReq>() {};
	}

	@Override
	protected DCP_GoodsWeightPluUpdateRes getResponseType() 
	{

		return new DCP_GoodsWeightPluUpdateRes();
	}

}
