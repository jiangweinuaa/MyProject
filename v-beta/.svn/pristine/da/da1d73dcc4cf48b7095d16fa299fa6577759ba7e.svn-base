package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BFeeCreateReq;
import com.dsc.spos.json.cust.res.DCP_BFeeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_BFeeCreate extends SPosAdvanceService<DCP_BFeeCreateReq, DCP_BFeeCreateRes>
{
	@Override
	protected void processDUID(DCP_BFeeCreateReq req, DCP_BFeeCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String bDate = req.getRequest().getbDate();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();
		String createBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		//String sysDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		try
		{
			if (checkGuid(req) == false)
			{
				String bFeeNO = this.getBFeeNO(req);
				String bFeeID = req.getRequest().getbFeeID();
				String processStatus = "N";
				String docType = req.getRequest().getDocType();

				String[] columns1 = {
						"SHOPID", "ORGANIZATIONNO","BDATE","BFEE_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME",
						"TOT_AMT", "EID", "PROCESS_STATUS", "BFEENO", "MEMO", "STATUS", "DOC_TYPE",
						"WORKNO", "TAXCODE", "TAXRATE","CREATE_CHATUSERID"
				};
				DataValue[] insValue1 = null;
				BigDecimal totAmt = new BigDecimal("0");

				//新增单身（多笔）
				List<Map<String, String>> datas = req.getRequest().getDatas();
				for (Map<String, String> par : datas) {
					int insColCt = 0;
					String[] columnsName = {
							"BFEENO", "SHOPID", "ITEM", "FEE", "AMT",
							"MEMO", "EID", "organizationNO"
					};
					DataValue[] columnsVal = new DataValue[columnsName.length];

					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = bFeeNO;
								break;
							case 1:
								keyVal = shopId;
								break;
							case 2:
								keyVal = par.get("item"); //item
								break;
							case 3:
								keyVal = par.get("fee"); //fee
								break;
							case 4:
								keyVal = par.get("amt"); //amt
								totAmt = totAmt.add(new BigDecimal(keyVal));
								break;
							case 5:
								keyVal = par.get("memo"); //memo
								break;
							case 6:
								keyVal = eId;
								break;
							case 7:
								keyVal = organizationNO;
								break;
							default:
								break;
						}

						if (keyVal != null) {
							insColCt++;
							if (i == 2){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}else if (i == 4){
								columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
							}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						}
						else {
							columnsVal[i] = null;
						}
					}

					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length)
								break;
						}
					}
					InsBean ib2 = new InsBean("DCP_BFEE_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}

				//totAmt = totAmt.setScale(2, RoundingMode.HALF_UP);

				insValue1 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(organizationNO, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(bFeeID, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(totAmt.toString(), Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(processStatus, Types.VARCHAR),
						new DataValue(bFeeNO, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(docType, Types.VARCHAR),
						new DataValue(req.getRequest().getSquadNo(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxCode(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxRate(), Types.FLOAT),
						new DataValue(req.getChatUserId(), Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_BFEE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
			}
		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BFeeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BFeeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BFeeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BFeeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String bDate = req.getRequest().getbDate();
		String status = req.getRequest().getStatus();
		String docType = req.getRequest().getDocType();
		String bFeeID = req.getRequest().getbFeeID();
		List<Map<String, String>> datas = req.getRequest().getDatas();

		if(Check.Null(bDate)){
			errMsg.append("费用年月不可为空值, ");
			isFail = true;
		}

		if(Check.Null(status)){
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if(Check.Null(docType)){
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}

		if(Check.Null(bFeeID)){
			errMsg.append("guid不可为空值, ");
			isFail = true;
		}

		for(Map<String, String> par : datas){

			if (Check.Null(par.get("item")))
			{
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}

			if (Check.Null(par.get("fee")))
			{
				errMsg.append("费用项不可为空值, ");
				isFail = true;
			}

			if (Check.Null(par.get("amt")))
			{
				errMsg.append("金额不可为空值, ");
				isFail = true;
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_BFeeCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BFeeCreateReq>(){};
	}

	@Override
	protected DCP_BFeeCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BFeeCreateRes();
	}

	private String getBFeeNO(DCP_BFeeCreateReq req) throws Exception{
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String bFeeNO = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String[] conditionValues = { eId, shopId }; // 查询要货单号
		String bDate= PosPub.getAccountDate_SMS(dao, eId, shopId);
		bFeeNO = "MDFY" + bDate;
		sqlbuf.append("" + "select bFeeNO  from ( " + "select max(bFeeNO) as bFeeNO "
				+ "  from DCP_BFEE " + " where EID = ? " + " and SHOPID = ? "
				+ " and bFeeNO like '%%" + bFeeNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			bFeeNO = (String) getQData.get(0).get("BFEENO");
			if (bFeeNO != null && bFeeNO.length() > 0) {
				long i;
				bFeeNO = bFeeNO.substring(4, bFeeNO.length());
				i = Long.parseLong(bFeeNO) + 1;
				bFeeNO = i + "";
				bFeeNO = "MDFY" + bFeeNO;
			}
			else {
				bFeeNO = "MDFY" + bDate + "00001";
			}
		}
		else {
			bFeeNO = "MDFY" + bDate + "00001";
		}

		return bFeeNO;
	}

	@Override
	protected String getQuerySql(DCP_BFeeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null ;
	}

	private boolean checkGuid(DCP_BFeeCreateReq req) throws Exception{
		boolean existGuid;
		String guid = req.getRequest().getbFeeID();
		String eId = req.geteId();
		String sql = "select * from DCP_bfee where bfee_id='"+guid+"' and eId='"+eId+"' " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);

		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}

		return existGuid;
	}

}
