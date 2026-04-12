package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReasonMsgUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ReasonMsgUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ReasonMsgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;


public class DCP_ReasonMsgUpdate extends SPosAdvanceService<DCP_ReasonMsgUpdateReq, DCP_ReasonMsgUpdateRes> {

	@Override
	protected boolean isVerifyFail(DCP_ReasonMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String BSNO = req.getRequest().getBsNo();
		String BSType = req.getRequest().getBsType();

		List<level1Elm> datas = req.getRequest().getDatas();

		if(Check.Null(BSNO)){
			errMsg.append("原因码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(BSType)){
			errMsg.append("原因类型不能为空值 ");
			isFail = true;
		}
		if(datas != null){
			for(level1Elm par : datas){

				if (Check.Null(par.getLangType())) 
				{
					errMsg.append("多语言类型不可为空值, ");
					isFail = true;
				}
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ReasonMsgUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReasonMsgUpdateReq>(){};
	}

	@Override
	protected DCP_ReasonMsgUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ReasonMsgUpdateRes();
	}

	@Override
	protected void processDUID(DCP_ReasonMsgUpdateReq req, DCP_ReasonMsgUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try 
		{
			String eId = req.geteId();
			String BSNO = req.getRequest().getBsNo();
			String BSType = req.getRequest().getBsType();
			String BSName = req.getRequest().getBsName();
			String status = req.getRequest().getStatus();

			List<level1Elm> datas = req.getRequest().getDatas();

			//删除原有的
			DelBean db2 = new DelBean("DCP_REASON_LANG");
			db2.addCondition("BSNO", new DataValue(BSNO, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

			if(datas != null )
			{
				for(level1Elm par : datas)
				{
					//String lBSNO = par.getBsNO();
					//String lBSType = par.getBsType();
					String lBSName = par.getBsName();
					String langType = par.getLangType();
					String lstatus = par.getStatus();

					int insColCt = 0;  
					String[] columnsName = {"BSNO", "BSTYPE", "LANG_TYPE", "REASON_NAME", 
							"STATUS", "EID"};

					//sql = this.isLangRepeat(req, BSNO, langType);
					//List<Map<String, Object>> reasonLangDatas = this.doQueryData(sql, null);
					//if(reasonLangDatas.size() == 0){
					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = BSNO;
							break;
						case 1:
							keyVal = BSType;
							break;
						case 2:
							keyVal = langType;
							break;
						case 3:
							keyVal = lBSName;
							break;
						case 4:
							keyVal = lstatus;
							break;
						case 5:
							keyVal = eId;
							break;
						default:
							break;	
						}  //switch 结束

						if (keyVal != null) {
							insColCt++;
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
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
					//添加原因码多语言信息
					InsBean ib2 = new InsBean("DCP_REASON_LANG", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));

					//					}
					//				  	else{
					//				  		res.setSuccess(false);
					//						res.setServiceStatus("100");
					//						res.setServiceDescription(" 原因编码"+BSNO+", 多语言编码"+langType+"  的信息已存在");
					//				  	}

				}
			}
			//原因码信息
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_REASON");
			//add Value
			//ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
			ub1.addUpdateValue("BSName", new DataValue(BSName, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

			ub1.addCondition("BSNO", new DataValue(BSNO, Types.VARCHAR));
			ub1.addCondition("BSTYPE", new DataValue(BSType, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReasonMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReasonMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReasonMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 判断多语言信息是否重复
	 * @param req
	 * @param BSNO
	 * @param BSType
	 * @param langType
	 * @return
	 */
	protected String isLangRepeat(DCP_ReasonMsgUpdateReq req, String BSNO,String langType){
		String eId = req.geteId();
		String sql = "select * from DCP_REASON_Lang where EID = '"+eId+"' "
				+ " and BSNO = '"+BSNO+"'"
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}



}
