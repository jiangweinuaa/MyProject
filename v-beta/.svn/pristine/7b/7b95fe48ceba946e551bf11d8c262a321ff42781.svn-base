package com.dsc.spos.service.imp.json;

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
import com.dsc.spos.json.cust.req.DCP_ServiceChargeCreateReq;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务费新增服务  
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ServiceChargeCreate extends SPosAdvanceService<DCP_ServiceChargeCreateReq, DCP_ServiceChargeCreateRes> {

	@Override
	protected void processDUID(DCP_ServiceChargeCreateReq req, DCP_ServiceChargeCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {

			//定义一个字符串 用来 记录没有插入的编码；  如果中间某一条不能插入， 记录下来，不影响后面的信息继续录入
			String serviceChargeNOList = "";
			String eId = req.geteId(); 

			String serviceChargeNO = req.getRequest().getServiceChargeNo();
			String scType = req.getRequest().getScType();

			String limitShop = req.getRequest().getLimitShop();
			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String createDate = dfDate.format(cal.getTime());
			String createTime = dfTime.format(cal.getTime());
			String status = req.getRequest().getStatus();

			sql = this.isRepeat(serviceChargeNO, eId);
			List<Map<String, Object>> scDatas = this.doQueryData(sql, null);

			if(scDatas.isEmpty()){
				String[] columns1 = {
						"EID","SERVICECHARGENO","SCTYPE","LIMIT_SHOP", 
						"CREATEBY","CREATE_DATE","CREATE_TIME","STATUS"
				};
				DataValue[] insValue1 = null;

				List<level1Elm> datas = req.getRequest().getDatas();
				//				if(datas.isEmpty())
				//				{
				//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务费详细信息为空!!");
				//				}
				if(!datas.isEmpty()){

					for (level1Elm par : datas) {
						int insColCt = 0;  
						String[] columnsName = {
								"EID","SERVICECHARGENO","SCTYPE","SPNO","SCRATE"
						};

						String spno = par.getSpNo();
						sql = this.isRepeat2(serviceChargeNO, eId, spno);
						List<Map<String, Object>> scDetailDatas = this.doQueryData(sql, null);
						if(scDetailDatas.isEmpty()){

							DataValue[] columnsVal = new DataValue[columnsName.length];
							for (int i = 0; i < columnsVal.length; i++) { 
								String keyVal = null;
								switch (i) { 
								case 0:
									keyVal = eId;
									break;
								case 1:
									keyVal = serviceChargeNO;
									break;
								case 2:
									keyVal = scType;
									break;
								case 3:
									keyVal = par.getSpNo().toString();
									break;
								case 4:
									keyVal = par.getScrate();
									break;
								default:
									break;
								}
								if (keyVal != null) 
								{
									insColCt++;
									columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
								} 
								else 
								{
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
							InsBean ib2 = new InsBean("DCP_SERVICECHARGE_DETAIL", columns2);
							ib2.addValues(insValue2);
							this.addProcessData(new DataProcessBean(ib2));
						}
						else{
							res.setSuccess(false);
							res.setServiceStatus("200");
							res.setServiceDescription("服务费编码为" +serviceChargeNO+"，明细编号为 "+spno+" 的服务费信息 已存在");

						}
					}
				}
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(serviceChargeNO, Types.VARCHAR),
						new DataValue(scType, Types.VARCHAR),
						new DataValue(limitShop, Types.VARCHAR),
						new DataValue(opNO, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR)
				};
				InsBean ib1 = new InsBean("DCP_SERVICECHARGE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();	

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"编码为" +serviceChargeNO+" 的服务费信息 已存在，请勿重复添加");
			}

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ServiceChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ServiceChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ServiceChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		List<level1Elm> datas = req.getRequest().getDatas();
		String serviceChargeNO = req.getRequest().getServiceChargeNo();
		String scType = req.getRequest().getScType();

		if (Check.Null(serviceChargeNO)) 
		{
			errMsg.append("服务费编码不能为空值 ");
			isFail = true;
		}

		if (Check.Null(scType)) 
		{
			errMsg.append("种类不能为空值 ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for(level1Elm par : datas){

			if (Check.Null(par.getSpNo())) 
			{
				errMsg.append("编号不能为空值 ");
				isFail = true;
			}
			if (Check.Null(par.getScrate())) 
			{
				errMsg.append("费率不能为空值 ");
				isFail = true;
			}
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeCreateReq>(){};
	}

	@Override
	protected DCP_ServiceChargeCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeCreateRes();
	}


	/**
	 * 判断服务费 编码 是否已存在
	 * @param serviceChargeNO
	 * @param eId
	 * @return
	 */
	private String isRepeat( String serviceChargeNO , String eId ){
		String sql = null;
		sql = "select * from DCP_SERVICECHARGE "
				+ " where  serviceChargeNO = '"+serviceChargeNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}

	/**
	 * 判断服务费子项中品类编号是否重复
	 * @param serviceChargeNO
	 * @param eId
	 * @param spno
	 * @return
	 */
	private String isRepeat2( String serviceChargeNO , String eId , String spno){
		String sql = null;
		sql = "select * from DCP_SERVICECHARGE_Detail "
				+ " where  serviceChargeNO = '"+serviceChargeNO +"' "
				+ " and EID = '"+eId+"' "
				+ " and spno = '"+spno+"'";
		return sql;
	}


}
