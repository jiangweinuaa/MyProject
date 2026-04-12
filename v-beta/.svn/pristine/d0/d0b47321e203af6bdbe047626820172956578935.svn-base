package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LStockOutProcessReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

public class DCP_LStockOutProcess extends SPosAdvanceService<DCP_LStockOutProcessReq, DCP_LStockOutProcessRes>{
	
	@Override
	protected void processDUID(DCP_LStockOutProcessReq req, DCP_LStockOutProcessRes res) throws Exception {
		String shopId = req.getShopId();
		String eId = req.geteId();
        String opType = req.getRequest().getOpType();
        String lStockOutNO = req.getRequest().getlStockOutNo();
		String opNo = req.getEmployeeNo();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		
		
		//KDS会把shopid放入request，此处特殊处理  by jinzma 20221111
		if (!Check.Null(req.getRequest().getShopId())){
			req.setShopId(req.getRequest().getShopId());
			shopId = req.getRequest().getShopId();
			//【ID1031320】【霸王3.0】报损出库单没有存用户信息 by jinzma 20230224
			opNo = "admin";
		}
		
		try {
			//因为库存流水会日结掉，改成查报损单单头  BY JZMA 20191210
			String sql = " select a.lstockoutno,a.bdate,a.memo,a.createby,a.create_date,a.create_time,a.accountby,"
					+ " a.account_date,a.account_time,b.item,b.pluno,b.punit,b.pqty,b.baseunit,b.unit_ratio,b.baseqty,b.price,b.amt,b.bsno,"
					+ " a.load_doctype,a.load_docno,b.warehouse,b.batch_no,b.prod_date,b.distriprice,b.featureno,b.distriamt,a.status,b.location "
					+ " from dcp_lstockout a "
					+ " inner join dcp_lstockout_detail b "
					+ " on a.lstockoutno=b.lstockoutno and a.eid=b.eid and a.organizationno=b.organizationno "
					+ " where a.eid='"+eId+"' "
					+ " and a.organizationno='"+shopId+"' "
					+ " and a.lstockoutno='"+lStockOutNO+"' "
					//+ " and status='0' "
                    ;
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if(getQData != null && !getQData.isEmpty()) {
                String status = getQData.get(0).get("STATUS").toString();
                if("post".equals(opType)) {

                    Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                    if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                    }

                    if(status.equals("0")) {
                        UptBean ub1 = new UptBean("DCP_LSTOCKOUT");
                        //add Value
                        ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                        ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                        ub1.addUpdateValue("ConfirmBy", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("Confirm_Date", new DataValue(sDate, Types.VARCHAR));
                        ub1.addUpdateValue("Confirm_Time", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("accountBy", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                        ub1.addUpdateValue("account_Time", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                        //condition
                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub1));

                        ///查库存流水，是否存在记录
                        String sqlStockDetail = "select * from DCP_STOCK_DETAIL where EID='" + eId + "' "
                                + " and Organizationno='" + shopId + "' and BILLNO='" + lStockOutNO + "' ";

                        List<Map<String, Object>> getQData_checkStockDetail = this.doQueryData(sqlStockDetail, null);
                        if (getQData_checkStockDetail == null || getQData_checkStockDetail.isEmpty()) {
                            String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, shopId, "Enable_InTransit");//启用在途
                            //加入流水库存帐

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("LStockOutProcess");
                            bcReq.setDocType("");
                            bcReq.setBillType("07");
                            bcReq.setProdType("");
                            bcReq.setDirection("-1");
                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            for (Map<String, Object> oneData : getQData) {
                                String warehouse = oneData.get("WAREHOUSE").toString();
                                String inv_cost_warehouse = req.getInv_cost_warehouse();

                                //下面这段逻辑用来判断是否启用在途仓： 如果没启用N，不能判断所选出货仓warehouse 和 默认成本仓； 若为Y，就加上判断
                                if (Enable_InTransit.equals("Y")) {
                                    if (warehouse.equals(inv_cost_warehouse)) {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出货仓库 与 默认在途成本仓 一致,不能出库！");
                                    }
                                }
                                //判断仓库不能为空或空格  BY JZMA 20191118
                                if (Check.Null(warehouse) || warehouse.equals(" ")) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                }

                                String featureNo = oneData.get("FEATURENO").toString();
                                if (Check.Null(featureNo)) {
                                    featureNo = " ";
                                }


                                //减少在库数
                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<>();
                                inputParameter.put(1, eId);                                      //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "07");                                     //--单据类型
                                inputParameter.put(7, oneData.get("LSTOCKOUTNO").toString());    //--单据号
                                inputParameter.put(8, oneData.get("ITEM").toString());           //--单据行号
                                inputParameter.put(9, "0");        //序号
                                inputParameter.put(10, "-1");                                     //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, oneData.get("PLUNO").toString());          //--品号
                                inputParameter.put(13, featureNo);                                //--特征码
                                inputParameter.put(14, warehouse);                               //--仓库
                                inputParameter.put(15, oneData.get("BATCH_NO").toString());      //--批号
                                inputParameter.put(16, oneData.get("LOCATION").toString());
                                inputParameter.put(17, oneData.get("PUNIT").toString());         //--交易单位
                                inputParameter.put(18, oneData.get("PQTY").toString());          //--交易数量
                                inputParameter.put(19, oneData.get("BASEUNIT").toString());      //--基准单位
                                inputParameter.put(20, oneData.get("BASEQTY").toString());       //--基准数量
                                inputParameter.put(21, oneData.get("UNIT_RATIO").toString());    //--换算比例
                                inputParameter.put(22, oneData.get("PRICE").toString());         //--零售价
                                inputParameter.put(23, oneData.get("AMT").toString());           //--零售金额
                                inputParameter.put(24, oneData.get("DISTRIPRICE").toString());   //--进货价
                                inputParameter.put(25, oneData.get("DISTRIAMT").toString());     //--进货金额
                                inputParameter.put(26, accountDate);                             //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, oneData.get("BDATE").toString());         //--单据日期
                                inputParameter.put(29, oneData.get("BSNO").toString());          //--异动原因
                                inputParameter.put(30, oneData.get("MEMO").toString());          //--异动描述
                                inputParameter.put(31, opNo);                               //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));

                            }

                            //【ID1027197】 [泰奇3.0]门店报损出库品号录入数量1个但异动流水生成2笔一样数据 by jinzma 20220712
                            //插入单据检查表 DCP_PLATFORM_BILLCHECK，避免单号重复
                            String[] columns_check = {
                                    "EID", "SHOPID", "BILLTYPE", "BILLNO"
                            };
                            DataValue[] insValue_check = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue("lStockOut", Types.VARCHAR),
                                    new DataValue(lStockOutNO, Types.VARCHAR),
                            };
                            InsBean ib_check = new InsBean("DCP_PLATFORM_BILLCHECK", columns_check);
                            ib_check.addValues(insValue_check);
                            this.addProcessData(new DataProcessBean(ib_check));

                            this.doExecuteDataToDB();

                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("服务执行成功");

                            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                            try {
                                WebHookService.stockSync(eId, shopId, lStockOutNO);
                            } catch (Exception ignored) {

                            }

                        } else {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "库存流水有重复记录，请确认！");
                        }
                    }
                }

                if("cancel".equals(opType)){
                    if(!status.equals("0")){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据非新建状态不可作废！");
                    }

                    UptBean ub1 = new UptBean("DCP_LSTOCKOUT");
                    //add Value
                    ub1.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
                    ub1.addUpdateValue("CANCELBY", new DataValue(opNo, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_DATE", new DataValue(sDate, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_TIME", new DataValue(sTime, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFYBY", new DataValue(opNo, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
                    //ub1.addUpdateValue("MODIFY_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                    this.doExecuteDataToDB();
                }

			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		} catch(Exception e) {
			
			String description=e.getMessage();
			try {
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);
				
				pw.flush();
				pw.close();
				
				errors.flush();
				errors.close();
				
				description = errors.toString();
				
				if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
					description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
				}
				
			} catch (Exception ignored) {
			
			}
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
		}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_LStockOutProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_LStockOutProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_LStockOutProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_LStockOutProcessReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String lStockOutNO = req.getRequest().getlStockOutNo();
		
		if(Check.Null(lStockOutNO)){
			errMsg.append("报损单单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_LStockOutProcessReq> getRequestType() {
		return new TypeToken<DCP_LStockOutProcessReq>(){};
	}
	
	@Override
	protected DCP_LStockOutProcessRes getResponseType() {
		return new DCP_LStockOutProcessRes();
	}
	
	
}
