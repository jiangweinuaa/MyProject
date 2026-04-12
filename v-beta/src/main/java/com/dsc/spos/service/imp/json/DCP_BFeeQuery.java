package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_BFeeQueryReq;
import com.dsc.spos.json.cust.res.DCP_BFeeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_BFeeQuery extends SPosBasicService<DCP_BFeeQueryReq, DCP_BFeeQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_BFeeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();

		if(Check.Null(beginDate)){
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}
		if(Check.Null(endDate)){
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_BFeeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BFeeQueryReq>(){};
	}

	@Override
	protected DCP_BFeeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BFeeQueryRes();
	}

	@Override
	protected DCP_BFeeQueryRes processJson(DCP_BFeeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		//查询条件
		String shopId = req.getShopId();
		String eId = req.geteId();
		String langType = req.getLangType();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();

		//		//
		//		TokenManagerRetail tmr=new TokenManagerRetail();
		//		PosPub.WriteETLJOBLog("token总数:" +tmr.CountToken());
		//		tmr=null;

		//查询资料
		DCP_BFeeQueryRes res = this.getResponse();
		try
		{
			//给分页字段赋值
			String sql = this.getQuerySql_Count(req);				  												//查询总笔数
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;	

			res.setDatas(new ArrayList<DCP_BFeeQueryRes.level1Elm>());
			if (getQData_Count != null && getQData_Count.isEmpty() == false)
			{ 
				Map<String, Object> oneData_Count = getQData_Count.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				int pageNumber = req.getPageNumber();
				int pageSize = req.getPageSize();
				//计算起始位置
				int startRow = ((pageNumber - 1) * pageSize);
				startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
				startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
				StringBuffer sqlbuf = new StringBuffer("");

				sqlbuf.append("SELECT processERPNO,bFeeNO,bDate,Amemo,Bmemo,status,docType,createBy,createDate,createTime,confirmBy,"
						+ " confirmDate,confirmTime,submitBy,submitDate,submitTime,cancelBy,cancelDate,cancelTime,"
						+ " modifyBy,modifyDate,modifyTime,totAmt,item,fee,amt,feeName,createByName,"
						+ " WORKNO,workname,taxCode,taxRate,taxname,PROCESS_STATUS,UPDATE_TIME from (" 
						+ "SELECT a.process_erp_no as processERPNO ,A.bFeeNO, A.bDate, A.memo as Amemo, b.memo as Bmemo, A.status, A.doc_type as docType," 
						+ " A.createBy, A.create_date AS createDate, A.create_time AS createTime," 
						+ " A.confirmBy, A.confirm_date AS confirmDate, A.confirm_time AS confirmTime," 
						+ " A.submitBy, A.submit_date AS submitDate, A.submit_time AS submitTime,"
						+ " A.cancelBy, A.cancel_date AS cancelDate, A.cancel_Time AS cancelTime,"
						+ " A.modifyBy, A.modify_date AS modifyDate, A.modify_Time AS modifyTime,"
						+ " A.tot_amt AS totAmt, b.item, b.fee, b.amt, nvl(c.fee_name,b.fee) as feeName, F.op_name AS createByName,"
						+ " A.WORKNO,h.workname,A.taxCode,A.taxRate,g.taxname,A.PROCESS_STATUS,A.UPDATE_TIME " 
						+ " FROM DCP_BFEE A " 
						+ " INNER JOIN DCP_BFEE_DETAIL b ON A.bFeeNO = b.bFeeNO AND A.organizationno = b.organizationno  and a.EID=b.EID AND A.SHOPID = b.SHOPID " 
						+ " LEFT JOIN DCP_FEE_LANG C ON b.FEE = C.FEE AND b.EID = C.EID AND C.LANG_TYPE = '"+langType+"' "
						+ " LEFT JOIN PLATFORM_STAFFS_LANG f ON A.EID = f.EID AND A.createby = f.opno and f.lang_type='"+langType+"' "
						+ " LEFT JOIN dcp_taxcategory_lang g on A.EID=G.EID and A.TAXCODE=G.TAXCODE and g.lang_type='"+langType+"'"
						+ " left join DCP_WORK h on a.EID=h.EID and a.Workno=h.Workno "
						);

				//2018-11-09 添加 日期查询条件
				sqlbuf.append(" WHERE A.create_date between "+beginDate +" and "+endDate+" ");

				if(status != null && status.length() > 0)
				{
					sqlbuf.append(" and A.STATUS='"+status+"'");
				}
				if(keyTxt != null && keyTxt.length() > 0)
				{
					sqlbuf.append(" and (A.TOT_AMT LIKE '%%"+keyTxt+"%%' OR A.bFeeNO LIKE '%%"+keyTxt+"%%' OR A.MEMO LIKE '%%"+keyTxt+"%%' "
							+ "  or a.process_ERP_No like '%%"+keyTxt+"%%'  )");
				}
				sqlbuf.append(" and A.SHOPID='"+shopId+"'");
				sqlbuf.append(" and A.EID='"+eId+"'");

				sqlbuf.append(" and A.bFeeNO IN (");

				sqlbuf.append("SELECT cc.bFeeNO from (");
				sqlbuf.append("SELECT rn,bFeeNO from (");
				sqlbuf.append("SELECT bFeeNO,ROWNUM rn FROM (");
				sqlbuf.append("SELECT bFeeNO FROM DCP_BFEE");

				//2018-11-09 添加 日期查询条件
				sqlbuf.append(" where create_date between "+beginDate +" and "+endDate+" ");

				if(status != null && status.length() > 0)
				{
					sqlbuf.append(" and STATUS='"+status+"'");
				}
				if(keyTxt != null && keyTxt.length() > 0)
				{
					sqlbuf.append(" and (TOT_AMT LIKE '%%"+keyTxt+"%%' OR bFeeNO LIKE '%%"+keyTxt+"%%' OR MEMO LIKE '%%"+keyTxt+"%%')");
				}
				sqlbuf.append(" and SHOPID='"+shopId+"'");
				sqlbuf.append(" and EID='"+eId+"'");

				sqlbuf.append(" ORDER BY status ASC,bdate DESC,bFeeNO DESC))");
				sqlbuf.append(" where rn>" + startRow + " AND rn <= " + (startRow+pageSize) + " ) cc ");
				sqlbuf.append(" ) ORDER BY status ASC, create_date DESC, bFeeNO DESC) tbl");			            	
				sql = sqlbuf.toString();

				List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
				if (getQDataDetail != null && getQDataDetail.isEmpty() == false){
					//单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("BFEENO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
					for (Map<String, Object> oneData : getQHeader) 
					{	
						DCP_BFeeQueryRes.level1Elm oneLv1 =res.new level1Elm();
						oneLv1.setDatas(new ArrayList<DCP_BFeeQueryRes.level2Elm>());
						//取出第一层
						String processERPNO = oneData.get("PROCESSERPNO").toString();
						String bFeeNO = oneData.get("BFEENO").toString();
						String bDate = oneData.get("BDATE").toString();
						String Amemo = oneData.get("AMEMO").toString();
						String status1 = oneData.get("STATUS").toString();
						String docType = oneData.get("DOCTYPE").toString();
						String createByName = oneData.get("CREATEBYNAME").toString();

						//2018-11-14 添加以下modifyBy 等字段
						String modifyBy =  oneData.get("MODIFYBY").toString();
						String modifyDate =  oneData.get("MODIFYDATE").toString();
						String modifyTime =  oneData.get("MODIFYTIME").toString();
						String createBy =  oneData.get("CREATEBY").toString();
						String createDate =  oneData.get("CREATEDATE").toString();
						String createTime =  oneData.get("CREATETIME").toString();
						String submitBy =  oneData.get("SUBMITBY").toString();
						String submitDate =  oneData.get("SUBMITDATE").toString();
						String submitTime =  oneData.get("SUBMITTIME").toString();

						String confirmBy =  oneData.get("CONFIRMBY").toString();
						String confirmDate =  oneData.get("CONFIRMDATE").toString();
						String confirmTime =  oneData.get("CONFIRMTIME").toString();
						String cancelBy =  oneData.get("CANCELBY").toString();
						String cancelDate =  oneData.get("CANCELDATE").toString();
						String cancelTime =  oneData.get("CANCELTIME").toString();

						//设置响应
						oneLv1.setProcessERPNo(processERPNO);
						oneLv1.setbFeeNo(bFeeNO);
						oneLv1.setbDate(bDate);
						oneLv1.setMemo(Amemo);
						oneLv1.setStatus(status1);
						oneLv1.setDocType(docType);
						oneLv1.setCreateByName(createByName);
						oneLv1.setSquadNo(oneData.get("WORKNO").toString());
						oneLv1.setSquadName(oneData.get("WORKNAME").toString());
						oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
						oneLv1.setTaxRate(Double.parseDouble(oneData.get("TAXRATE").toString().isEmpty()?"0":oneData.get("TAXRATE").toString() ));
						oneLv1.setTaxName(oneData.get("TAXNAME").toString());

						oneLv1.setModifyBy(modifyBy);
						oneLv1.setModifyDate(modifyDate);
						oneLv1.setModifyTime(modifyTime);
						oneLv1.setSubmitBy(submitBy);
						oneLv1.setSubmitDate(submitDate);
						oneLv1.setSubmitTime(submitTime);
						oneLv1.setCreateBy(createBy);
						oneLv1.setCreateDate(createDate);
						oneLv1.setCreateTime(createTime);
						oneLv1.setConfirmBy(confirmBy);
						oneLv1.setConfirmDate(confirmDate);
						oneLv1.setConfirmTime(confirmTime);
						oneLv1.setCancelBy(cancelBy);
						oneLv1.setCancelDate(cancelDate);
						oneLv1.setCancelTime(cancelTime);

						String UPDATE_TIME;
						SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS"); 
						if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty())
						{
							UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
						}
						else
						{
							UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
						}
						oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));

						oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());

						for (Map<String, Object> oneData2 : getQDataDetail) 
						{
							//过滤属于此单头的明细
							if(bFeeNO.equals(oneData2.get("BFEENO")))
							{
								DCP_BFeeQueryRes.level2Elm oneLv2 = res.new level2Elm();
								String item = oneData2.get("ITEM").toString();
								String fee = oneData2.get("FEE").toString();
								String feeName = oneData2.get("FEENAME").toString();
								String amt = oneData2.get("AMT").toString();
								String Bmemo = oneData2.get("BMEMO").toString();

								//单身赋值
								oneLv2.setItem(item);
								oneLv2.setFee(fee);
								oneLv2.setFeeName(feeName);
								oneLv2.setAmt(amt);
								oneLv2.setMemo(Bmemo);

								//添加单身
								oneLv1.getDatas().add(oneLv2);
							}
						}
						//添加单头
						res.getDatas().add(oneLv1);
					}
				}
				else 
				{
					totalRecords = 0;
					totalPages = 0;
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			return res;		
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_BFeeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private String getQuerySql_Count(DCP_BFeeQueryReq req) throws Exception{
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String shopId = req.getShopId();
		String eId = req.geteId();
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select num from ("
				+"select COUNT(*) AS num from("
				+"select distinct bFeeNO from DCP_BFEE "
				);

		//2018-11-09 添加 日期查询条件
		sqlbuf.append(" WHERE create_date between "+beginDate +" and "+endDate+" ");

		if (status != null && status.length() > 0)
		{
			sqlbuf.append(" AND STATUS='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length() > 0)
		{
			sqlbuf.append(" AND (TOT_AMT LIKE '%%"+keyTxt+"%%' OR bFeeNO LIKE '%%"+keyTxt+"%%' OR MEMO LIKE '%%"+keyTxt+"%%' "
					+ " or process_ERP_No like '%%"+keyTxt+"%%'  )");
		}

		sqlbuf.append(" and SHOPID='"+shopId+"'");
		sqlbuf.append(" and EID='"+eId+"'");
		sqlbuf.append(" ) ");
		sqlbuf.append(" ) TBL ");

		sql = sqlbuf.toString();
		return sql;
	}
}
