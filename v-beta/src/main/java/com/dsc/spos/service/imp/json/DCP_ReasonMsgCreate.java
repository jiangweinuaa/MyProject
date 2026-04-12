package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReasonMsgCreateReq;
import com.dsc.spos.json.cust.req.DCP_ReasonMsgCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ReasonMsgCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReasonMsgCreate extends SPosAdvanceService<DCP_ReasonMsgCreateReq, DCP_ReasonMsgCreateRes> {

	@Override
	protected boolean isVerifyFail(DCP_ReasonMsgCreateReq req) throws Exception {
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

				if (Check.Null(par.getBsNo())) 
				{
					errMsg.append("多语言原因码不可为空值, ");
					isFail = true;
				}
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
	protected TypeToken<DCP_ReasonMsgCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReasonMsgCreateReq>(){};
	}

	@Override
	protected DCP_ReasonMsgCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ReasonMsgCreateRes();
	}


	@Override
	protected String getQuerySql(DCP_ReasonMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void processDUID(DCP_ReasonMsgCreateReq req, DCP_ReasonMsgCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try 
		{
			String eId = req.geteId();
			String BSNO = req.getRequest().getBsNo();
			String BSType = req.getRequest().getBsType();
			String BSName = req.getRequest().getBsName();
			String status = req.getRequest().getStatus();

			sql = this.isRepeat(req, BSNO, BSType);
			List<Map<String, Object>> reasonDatas = this.doQueryData(sql, null);

			//查询原因信息是否已经存在
			if(reasonDatas.size() == 0)
			{
				String[] columns1 = {"BSNO", "BSTYPE","BSNAME","STATUS","EID" };
				DataValue[] insValue1 = null;
				//新增原因码多语言表
				List<level1Elm> datas = req.getRequest().getDatas();
				if(datas != null){
					for (level1Elm par : datas) {
						int insColCt = 0;  
						String[] columnsName = {"BSNO", "BSTYPE", "LANG_TYPE", "REASON_NAME", 
								"STATUS", "EID"};

						//获取
						String lBSNO = par.getBsNo();
						String lBSType = par.getBsType();
						String langType = par.getLangType();
						String lBSName = par.getBsName();
						String lstatus = par.getStatus();

						sql = this.isLangRepeat(req, lBSNO, lBSType, langType);
						List<Map<String, Object>> reasonLangDatas = this.doQueryData(sql, null);
						if(reasonLangDatas.size() == 0){
							DataValue[] columnsVal = new DataValue[columnsName.length];
							for (int i = 0; i < columnsVal.length; i++) { 
								String keyVal = null;
								switch (i) { 
								case 0:
									keyVal = lBSNO;
									break;
								case 1:
									keyVal = lBSType;
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
						}
						else{
//							res.setSuccess(false);
//							res.setServiceStatus("200");
//							res.setServiceDescription("服务执行异常 ：原因编码"+BSNO+", 多语言编码"+langType+"  的信息已存在");
						}
					}
				}

				//添加原因吗信息
				insValue1 = new DataValue[]{
						new DataValue(BSNO, Types.VARCHAR), 
						new DataValue(BSType, Types.VARCHAR),
						new DataValue(BSName, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_REASON", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();	
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务执行异常 ：原因编码"+BSNO+", 类型为"+BSType+"  的信息已存在");		
			}
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReasonMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReasonMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReasonMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected String isRepeat(DCP_ReasonMsgCreateReq req, String BSNO, String BSType){
		String eId = req.geteId();
		String sql = "select * from DCP_REASON where EID = '"+eId+"' "
				+ " and BSNO = '"+BSNO+"' and BSType = '"+BSType+"' " ;
		return sql;
	}

	protected String isLangRepeat(DCP_ReasonMsgCreateReq req, String BSNO, String BSType, String langType){
		String eId = req.geteId();
		String sql = "select * from DCP_REASON_Lang where EID = '"+eId+"' "
				+ " and BSNO = '"+BSNO+"'"
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}

}
