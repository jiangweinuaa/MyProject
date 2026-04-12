package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsUnitConvertCreateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsUnitConvertCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 新增商品单位换算信息
 * 2018-09-20
 * @author yuanyy
 *
 */
public class DCP_GoodsUnitConvertCreate extends SPosAdvanceService<DCP_GoodsUnitConvertCreateReq, DCP_GoodsUnitConvertCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsUnitConvertCreateReq req, DCP_GoodsUnitConvertCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try
		{
			String eId = req.geteId();
			String pluNO = req.getRequest().getPluNo();
			String ounit = req.getRequest().getoUnit();
			String unit = req.getRequest().getUnit();
			String oqty = req.getRequest().getOqty();
			String qty = req.getRequest().getQty();
			String status = req.getRequest().getStatus();

			sql = this.isRepeat(eId, pluNO, ounit, unit);
			List<Map<String, Object>> goodsUnitConDatas = this.doQueryData(sql, null);
			if(goodsUnitConDatas.isEmpty()){
				String[] columns1 = {"PLUNO", "OUNIT","UNIT","OQTY","QTY","STATUS","EID" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(pluNO, Types.VARCHAR), 
						new DataValue(ounit, Types.VARCHAR),
						new DataValue(unit, Types.VARCHAR),
						new DataValue(oqty, Types.VARCHAR),
						new DataValue(qty, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_UNITCONVERT_GOODS", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务执行失败:商品编码为" +pluNO+" ,"+ounit+ " 转换为 "+unit+" 的信息已存在");
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsUnitConvertCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsUnitConvertCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsUnitConvertCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsUnitConvertCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pluNO = req.getRequest().getPluNo();
		String ounit = req.getRequest().getoUnit();
		String unit = req.getRequest().getUnit();


		if(Check.Null(pluNO)){
			errMsg.append("商品编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(ounit)){
			errMsg.append("来源单位不能为空值 ");
			isFail = true;
		}
		if(Check.Null(unit)){
			errMsg.append("目标单位不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsUnitConvertCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsUnitConvertCreateReq>(){};
	}

	@Override
	protected DCP_GoodsUnitConvertCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsUnitConvertCreateRes();
	}

	private String isRepeat(String eId, String pluNO, String ounit, String unit){

		String sql = null;
		sql = "select * from DCP_UNITCONVERT_GOODS "
				+ " where EID = '"+eId +"'"
				+ " and ounit = '"+ounit+"' "
				+ " and unit = '"+unit+"' "
				+ " and pluNO = '"+pluNO+"'";
		return sql;
	}

}
