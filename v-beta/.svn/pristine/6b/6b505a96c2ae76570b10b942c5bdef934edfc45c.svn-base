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
import com.dsc.spos.json.cust.req.DCP_ProcessTaskCreateReq;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ProcessTaskCreate extends SPosAdvanceService<DCP_ProcessTaskCreateReq, DCP_ProcessTaskCreateRes>{
	
	@Override
	protected void processDUID(DCP_ProcessTaskCreateReq req, DCP_ProcessTaskCreateRes res) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String bDate = req.getRequest().getBDate();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();
		String process_Status = "N";
		String pTemplateNO = req.getRequest().getPTemplateNo();
		String pDate = req.getRequest().getPDate();
		String warehouse = req.getRequest().getWarehouse();
		String materialWarehouseNO =req.getRequest().getMaterialWarehouseNo();
		String processTaskId = req.getRequest().getProcessTaskId();
		String createBy = req.getOpNO();
		String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String createTime = new SimpleDateFormat("HHmmss").format(new Date());
		String processTaskNO = "";
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totDistriAmt=req.getRequest().getTotDistriAmt();
		String totCqty = req.getRequest().getTotCqty();
		
		//try {
			if (checkGuid(req) == false) {
				processTaskNO = this.getOrderNO(req,"JGRW"); //getProcessTaskNO(req);
                res.setProcessTaskNo(processTaskNO);
				String[] columns1 = {
						"SHOPID", "ORGANIZATIONNO","BDATE","PROCESSTASK_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME",
						"TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
						"PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CREATE_CHATUSERID",
                        "DTNO","EMPLOYEEID","DEPARTID","PRODTYPE"
				};
				
				//新增单身（多笔）
				List<level1Elm> datas = req.getRequest().getDetailList();
				for (level1Elm par : datas) {
					int insColCt = 0;

					String[] columnsName = {
							"PROCESSTASKNO", "SHOPID", "item", "pluNO",
							"punit", "pqty", "baseunit", "baseqty","unit_Ratio",
							"price", "amt", "EID", "organizationNO", "mul_Qty",
							"DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","MINQTY","DISPTYPE","SEMIWOTYPE","ODDVALUE","REMAINTYPE","BOMNO","VERSIONNUM","BEGINDATE","ENDDATE","GOODSSTATUS","DISPATCHSTATUS"
					};
					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					
					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = processTaskNO;
								break;
							case 1:
								keyVal = shopId;
								break;
							case 2:
								keyVal = par.getItem(); //item
								break;
							case 3:
								keyVal = par.getPluNo(); //pluNO
								break;
							case 4:
								keyVal = par.getPunit(); //punit
								break;
							case 5:
								keyVal = par.getPqty(); //pqty
								break;
							case 6:
								keyVal = par.getBaseUnit();     //wunit
								break;
							case 7:
								keyVal = par.getBaseQty();   //wqty
								break;
							case 8:
								keyVal = par.getUnitRatio();    //unitRatio
								break;
							case 9:
								keyVal = par.getPrice();    //price
								if(Check.Null(keyVal))
									keyVal = "0";
								break;
							case 10:
								keyVal = par.getAmt();    //amt
								break;
							case 11:
								keyVal = eId;
								break;
							case 12:
								keyVal = organizationNO;
								break;
							case 13:
								keyVal = par.getMulQty();
								break;
							case 14:
								keyVal=par.getDistriPrice();
								if(Check.Null(keyVal))
									keyVal = "0";
								break;
							case 15:
								keyVal = par.getDistriAmt();
								if (Check.Null(keyVal))
									keyVal="0";
								break;
							case 16:
								keyVal = bDate;
								break;
							case 17:
								keyVal = par.getFeatureNo();
								if (Check.Null(keyVal))
									keyVal= " ";
								break;
                            case 18:
                                keyVal = par.getMinQty();
                                break;

                            case 19:
                                keyVal = par.getDispType();
                                break;
                            case 20:
                                keyVal = par.getSemiWoType();
                                break;
                            case 21:
                                keyVal = par.getOddValue();
                                break;
                            case 22:
                                keyVal = par.getRemainType();
                                break;
                            case 23:
                                keyVal = par.getBomNo();
                                break;
                            case 24:
                                keyVal = par.getVersionNum();
                                break;
                            case 25:
                                keyVal = par.getBeginDate();
                                break;
                            case 26:
                                keyVal = par.getEndDate();
                                break;

                            case 27:
                                keyVal ="0";
                                break;

                            case 28:
                                keyVal = "0";
                                break;
							default:
								break;
						}
						
						if (keyVal != null) {
							insColCt++;
							if (i == 2){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13|| i == 14){
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
					
					InsBean ib2 = new InsBean("DCP_PROCESSTASK_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}
				
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(organizationNO, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(processTaskId, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(totPqty, Types.VARCHAR),
						new DataValue(totAmt, Types.VARCHAR),
						new DataValue(totCqty, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(processTaskNO, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(process_Status, Types.VARCHAR),
						new DataValue(pTemplateNO, Types.VARCHAR),
						new DataValue(pDate, Types.VARCHAR),
						new DataValue(warehouse, Types.VARCHAR),
						new DataValue(materialWarehouseNO, Types.VARCHAR),
						new DataValue(totDistriAmt, Types.VARCHAR),
						new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDtNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getProdType(), Types.VARCHAR)
                };
				
				InsBean ib1 = new InsBean("DCP_PROCESSTASK", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
			}
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		//} catch (Exception e) {
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_ProcessTaskCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_ProcessTaskCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_ProcessTaskCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_ProcessTaskCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		//必传值不为空
		String bDate = req.getRequest().getBDate();
		String status = req.getRequest().getStatus();
		String processTaskId = req.getRequest().getProcessTaskId();
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totDistriAmt=req.getRequest().getTotDistriAmt();
		String totCqty = req.getRequest().getTotCqty();
		String warehouse = req.getRequest().getWarehouse();
		String materialWarehouseNo = req.getRequest().getMaterialWarehouseNo();
		String pDate = req.getRequest().getPDate();
		List<DCP_ProcessTaskCreateReq.level1Elm> datas = req.getRequest().getDetailList();
		
		if(Check.Null(bDate)){
			errMsg.append("单据日期不可为空值, ");
			isFail = true;
		}
		if(Check.Null(status)){
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}
		if(Check.Null(processTaskId)){
			errMsg.append("加工任务单guid不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totPqty)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totAmt)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totDistriAmt)) {
			errMsg.append("合计进货金额可为空值, ");
			isFail = true;
		}
		if (Check.Null(totCqty)) {
			errMsg.append("合计品种数量不可为空值, ");
			isFail = true;
		}
		//if (Check.Null(warehouse)) {
		//	errMsg.append("加工仓库不可为空值, ");
		//	isFail = true;
		//}
		//if (Check.Null(materialWarehouseNo)) {
		//	errMsg.append("原料仓库不可为空值, ");
		//	isFail = true;
		//}
		if (Check.Null(pDate)) {
			errMsg.append("生产日期不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for(DCP_ProcessTaskCreateReq.level1Elm par : datas) {
			String pluNo = par.getPluNo();
			
			if (Check.Null(par.getItem())) {
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPluNo())) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPunit())) {
				errMsg.append("报损单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPqty())) {
				errMsg.append("加工数量不可为空值, ");
				isFail = true;
			}
			if (!PosPub.isNumericType(par.getPqty())) {
				errMsg.append("商品"+pluNo+"任务数不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPunit())) {
				errMsg.append("商品"+pluNo+"任务数量单位不可为空值, ");
				isFail = true;
			}
			if (!PosPub.isNumericType(par.getBaseQty())) {
				errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getBaseUnit())) {
				errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
				isFail = true;
			}
			if (!PosPub.isNumericType(par.getUnitRatio())) {
				errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
				isFail = true;
			}
			
			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_ProcessTaskCreateReq> getRequestType() {
		return new TypeToken<DCP_ProcessTaskCreateReq>(){};
	}
	
	@Override
	protected DCP_ProcessTaskCreateRes getResponseType() {
		return new DCP_ProcessTaskCreateRes();
	}
	
	@Override
	protected String getQuerySql(DCP_ProcessTaskCreateReq req) throws Exception {
		return null;
	}
	
	private String getProcessTaskNO(DCP_ProcessTaskCreateReq req) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
        String sql = null;
        String processTaskNO = null;
        String shopId = req.getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
	}
	
	private boolean checkGuid(DCP_ProcessTaskCreateReq req) throws Exception {
		String guid = req.getRequest().getProcessTaskId();
		boolean existGuid;
		String sql = "select * from DCP_PROCESSTASK where ProcessTask_ID ='"+guid+"' " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		
		return existGuid;
	}
	
	
}
