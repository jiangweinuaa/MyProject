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
import com.dsc.spos.json.cust.req.DCP_MiniChargeCreateReq;
import com.dsc.spos.json.cust.req.DCP_MiniChargeCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MiniChargeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 低消信息新增
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeCreate extends SPosAdvanceService<DCP_MiniChargeCreateReq, DCP_MiniChargeCreateRes> {

	@Override
	protected void processDUID(DCP_MiniChargeCreateReq req, DCP_MiniChargeCreateRes res) throws Exception {

		// TODO Auto-generated method stub
		String sql = null;
		try {

			//定义一个字符串 用来 记录没有插入的加料编码；  如果中间某一条不能插入， 记录下来，不影响后面的加料信息继续录入
			String miniChargeNOList = "";
			String eId = req.geteId(); 
			String miniChargeNO = req.getRequest().getMiniChargeNo();
			String mcType = req.getRequest().getMcType();
			String adultQty = req.getRequest().getAdultQty();
			String childQty = req.getRequest().getChildQty();
			String priceClean = req.getRequest().getPriceClean();
			String amtMini = req.getRequest().getAmtMini();

			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String createDate = dfDate.format(cal.getTime());
			String createTime = dfTime.format(cal.getTime());
			String status = req.getRequest().getStatus();

			sql = this.isRepeat(miniChargeNO, eId);
			List<Map<String, Object>> scDatas = this.doQueryData(sql, null);

			if(scDatas.isEmpty()){
				String[] columns1 = {
						"EID","MINICHARGENO","MCTYPE","ADULT_QTY", 
						"CHILD_QTY","PRICE_CLEAN","AMT_MINI",
						"CREATEBY","CREATE_DATE","CREATE_TIME","STATUS"
				};
				DataValue[] insValue1 = null;

				List<level1Elm> datas = req.getRequest().getDatas();
				//				if(datas.isEmpty())
				//				{
				//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务费详细信息为空!!");
				//				}

				if(!datas.isEmpty())
				{
					for (level1Elm par : datas) {
						int insColCt = 0;  
						String[] columnsName = {
								"EID","MINICHARGENO","PLUNO" 
						};

						String pluNO = par.getPluNo();
						sql = this.isRepeat2(miniChargeNO, eId, pluNO);
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
									keyVal = miniChargeNO;
									break;
								case 2:
									keyVal = par.getPluNo().toString();
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
							InsBean ib2 = new InsBean("DCP_MINICHARGE_DETAIL", columns2);
							ib2.addValues(insValue2);
							this.addProcessData(new DataProcessBean(ib2));
						}
						else{
							res.setSuccess(false);
							res.setServiceStatus("200");
							res.setServiceDescription("编码为" +miniChargeNO+"，商品编号为 "+pluNO+" 的信息 已存在");

						}
					}
				}
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(miniChargeNO, Types.VARCHAR),
						new DataValue(mcType, Types.VARCHAR),
						new DataValue(adultQty, Types.INTEGER), 
						new DataValue(childQty, Types.INTEGER),
						new DataValue(priceClean, Types.VARCHAR),
						new DataValue(amtMini, Types.VARCHAR), 
						new DataValue(opNO, Types.VARCHAR), 
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR)
				};
				InsBean ib1 = new InsBean("DCP_MINICHARGE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "编码 " +miniChargeNO+"   已存在，请勿重复添加");
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MiniChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MiniChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MiniChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String miniChargeNO = req.getRequest().getMiniChargeNo();

		if (Check.Null(miniChargeNO)  ) 
		{
			errMsg.append("编号不能为空！ ");
			isFail = true;
		}	

		List<level1Elm> jsonDatas = req.getRequest().getDatas();
		for (level1Elm par : jsonDatas){ 
			//keyName必须为数值型

			String pluNO = par.getPluNo();

			if (Check.Null(pluNO)  ) 
			{
				errMsg.append("商品编号不能为空！ ");
				isFail = true;
			}	

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MiniChargeCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeCreateReq>(){};
	}

	@Override
	protected DCP_MiniChargeCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeCreateRes();
	}

	/**
	 * 判断服务费 编码 是否已存在
	 * @param miniChargeNO
	 * @param eId
	 * @return
	 */
	private String isRepeat( String miniChargeNO , String eId ){
		String sql = null;
		sql = "select * from DCP_MINICHARGE "
				+ " where  miniChargeNO = '"+miniChargeNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}

	/**
	 * 判断服务费子项中品类编号是否重复
	 * @param miniChargeNO
	 * @param eId
	 * @param spno
	 * @return
	 */
	private String isRepeat2( String miniChargeNO , String eId , String pluNO){
		String sql = null;
		sql = "select * from DCP_MINICHARGE_Detail "
				+ " where  miniChargeNO = '"+miniChargeNO +"' "
				+ " and EID = '"+eId+"' "
				+ " and pluNO = '"+pluNO+"'";
		return sql;
	}
}
