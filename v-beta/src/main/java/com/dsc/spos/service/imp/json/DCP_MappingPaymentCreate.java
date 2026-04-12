package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MappingPaymentCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderParaUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MappingPaymentCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 平台支付方式映射新增
 * @author yuanyy 2019-04-24
 *
 */
public class DCP_MappingPaymentCreate extends SPosAdvanceService<DCP_MappingPaymentCreateReq, DCP_MappingPaymentCreateRes> {

	@Override
	protected void processDUID(DCP_MappingPaymentCreateReq req, DCP_MappingPaymentCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		try
		{
			String eId = req.geteId();
			String docType = req.getRequest().getLoadDocType();
			String ERP_payCode = req.getRequest().getERP_payCode();
			String ERP_payName = req.getRequest().getERP_payName();
			String ERP_payType = req.getRequest().getERP_payType();
			String order_payCode = req.getRequest().getOrder_payCode();
			String order_payName = req.getRequest().getOrder_payName();
			String order_payType = req.getRequest().getOrder_payType();
			String status = req.getRequest().getStatus();

			String sql = null;
			sql = this.isRepeat(eId, docType, ERP_payCode, order_payCode);
			List<Map<String, Object>> scDatas = this.doQueryData(sql, null);
			if (scDatas.isEmpty())
			{
				String[] columns1 =
				{ "EID", "LOAD_DOCTYPE", "PAYCODE", "PAYNAME", "PAYCODEERP", "ORDER_PAYCODE", "ORDER_PAYNAME",
						"ORDER_PAYTYPE", "STATUS" };
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
				{ new DataValue(eId, Types.VARCHAR), new DataValue(docType, Types.VARCHAR),
						new DataValue(ERP_payCode, Types.VARCHAR), new DataValue(ERP_payName, Types.VARCHAR),
						new DataValue(ERP_payType, Types.VARCHAR), new DataValue(order_payCode, Types.VARCHAR),
						new DataValue(order_payName, Types.VARCHAR), new DataValue(order_payType, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR)

				};
				InsBean ib1 = new InsBean("DCP_MAPPINGPAYMENT", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			} else
			{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("该支付信息已存在，请勿重复添加！！");
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MappingPaymentCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MappingPaymentCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MappingPaymentCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MappingPaymentCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getLoadDocType()))
		{
			errMsg.append("渠道类型不能为空值 ");
			isFail = true;

		}
		if (Check.Null(req.getRequest().getOrder_payCode()))
		{
			errMsg.append("平台支付编码不能为空值 ");
			isFail = true;

		}

		if (Check.Null(req.getRequest().getERP_payCode()))
		{
			errMsg.append("内部（ERP）支付编码不能为空值 ");
			isFail = true;

		}

		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MappingPaymentCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MappingPaymentCreateReq>(){};
	}

	@Override
	protected DCP_MappingPaymentCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MappingPaymentCreateRes();
	}
	
	/**
	 * 验证映射信息是否已存在
	 * @param eId
	 * @param docType
	 * @param ERP_payCode
	 * @param order_payCode
	 * @return
	 */
	private String isRepeat( String eId , String docType, String ERP_payCode, String order_payCode ){
		String sql = null;
		sql = "select * from DCP_MAPPINGPAYMENT "
			+ " where  EID = '"+eId +"' "
			+ " and load_docType = '"+docType+"' "
			+ " and payCode = '"+ERP_payCode+"' "
			+ " and order_payCode = '"+order_payCode+"'";
		return sql;
	}
	
}
