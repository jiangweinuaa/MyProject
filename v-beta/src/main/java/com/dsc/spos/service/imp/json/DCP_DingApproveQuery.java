package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DingApproveQueryReq;
import com.dsc.spos.json.cust.req.DCP_DingApproveQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DingApproveQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_DingApproveGet
 * 服务说明：审批查询
 * @author jinzma 
 * @since  2019-12-31
 */
public class DCP_DingApproveQuery extends SPosBasicService<DCP_DingApproveQueryReq,DCP_DingApproveQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_DingApproveQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String beginDate = request.getBeginDate();
		String endDate = request.getEndDate();

		if (Check.Null(beginDate))
		{
			errMsg.append("起始日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(endDate))
		{
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_DingApproveQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingApproveQueryReq>(){};
	}

	@Override
	protected DCP_DingApproveQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingApproveQueryRes();
	}

	@Override
	protected DCP_DingApproveQueryRes processJson(DCP_DingApproveQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		String langType = req.getLangType();
		DCP_DingApproveQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数

			res.setDatas(new ArrayList<DCP_DingApproveQueryRes.level1Elm>());

			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("PROCESSID", true);	
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
				for (Map<String, Object> oneData : getQHeader) {
					DCP_DingApproveQueryRes.level1Elm oneLv1 = res.new level1Elm();

					String eId = oneData.get("EID").toString();
					String processId = oneData.get("PROCESSID").toString();
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("ORG_NAME").toString();
					String billNo = oneData.get("BILLNO").toString();
					String billType = oneData.get("BILLTYPE").toString();
					String funcNo = oneData.get("FUNCNO").toString();
					String funcName = oneData.get("FUNCNAME").toString();
					if (Check.Null(funcName))
					{
						if (langType.equals("zh_CN"))
						{
							funcName = oneData.get("FUNCNAME_CHSMSG").toString();
						} 
						else {
							funcName = oneData.get("FUNCNAME_CHTMSG").toString();
						}
					}
					String createTime = oneData.get("CREATETIME").toString();
					String createOpName = oneData.get("CREATEOPNAME").toString();
					String applyReason = oneData.get("APPLYREASON").toString();
					String rejectReason = oneData.get("REJECTREASON").toString();
					String checkTime = oneData.get("CHECKTIME").toString();
					String checkOpName = oneData.get("CHECKOPNAME").toString();
					if (Check.Null(checkOpName)) checkOpName = oneData.get("CHECKOPNAME_USERNAME").toString();
					String status = oneData.get("STATUS").toString();
					String processStatus = oneData.get("PROCESS_STATUS").toString();

					oneLv1.setApplyReason(applyReason);
					oneLv1.setBillNo(billNo);
					oneLv1.setBillType(billType);
					oneLv1.setCheckOpName(checkOpName);
					oneLv1.setCheckTime(checkTime);
					oneLv1.setCreateOpName(createOpName);
					oneLv1.setCreateTime(createTime);
					oneLv1.seteId(eId);
					oneLv1.setFuncName(funcName);
					oneLv1.setFuncNo(funcNo);
					oneLv1.setProcessId(processId);
					oneLv1.setProcessStatus(processStatus);
					oneLv1.setRejectReason(rejectReason);
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);
					oneLv1.setStatus(status);
					oneLv1.setApproveDetail(new ArrayList<DCP_DingApproveQueryRes.level2Elm>());

					for (Map<String, Object> oneDataDetail : getQData) 
					{
						if(processId.equals(oneDataDetail.get("PROCESSID").toString()))
						{
							DCP_DingApproveQueryRes.level2Elm level2Elm = res.new level2Elm();
							String item = oneDataDetail.get("ITEM").toString();
							String goodsId = oneDataDetail.get("PLUNO").toString();
							String goodsName = oneDataDetail.get("PLUNAME").toString();
							
							String unitName = oneDataDetail.get("UNITNAME").toString();
							String qty = oneDataDetail.get("QTY").toString();
							String price = oneDataDetail.get("PRICE").toString();
							String amt = oneDataDetail.get("AMT").toString();
							String discAmt = oneDataDetail.get("DISCAMT").toString();

							level2Elm.setAmt(amt);
							level2Elm.setDiscAmt(discAmt);
							level2Elm.setGoodsId(goodsId);
							level2Elm.setGoodsName(goodsName);
							level2Elm.setItem(item);
							level2Elm.setPrice(price);
							level2Elm.setQty(qty);
							level2Elm.setUnitName(unitName);

							oneLv1.getApproveDetail().add(level2Elm);
						}
					}
					res.getDatas().add(oneLv1);
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
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DingApproveQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();
		level1Elm request = req.getRequest();
		String shopId = request.getShopId();
		String billNo = request.getBillNo();
		String billType = request.getBillType();
		String funcNo = request.getFuncNo();
		String beginDate = request.getBeginDate();
		String endDate = request.getEndDate();
		String status = request.getStatus();
		String processStatus = request.getProcessStatus();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( " "
				+ " SELECT num,rn, "
				+ " a.EID,a.PROCESSID,a.COMPANYID,a.SHOPID,a.BILLNO,a.BILLTYPE,a.FUNCNAME,to_char(a.CREATETIME,'YYYY-MM-DD hh24:mi:ss') as CREATETIME,a.CREATEOPID,a.CREATEOPNAME, "
				+ " a.APPLYREASON,a.REJECTREASON,a.CHECKOPID,a.CHECKOPNAME,to_char(a.CHECKTIME,'YYYY-MM-DD hh24:mi:ss') as CHECKTIME,a.STATUS,a.PROCESS_STATUS,a.FUNCNO, "
				+ " b.item,b.pluno,b.pluname,b.unitname,b.qty,b.price,b.amt,b.discamt,c.username as checkopname_username, "
				+ " d.org_name,e.chsmsg as funcname_chsmsg,e.chtmsg as funcname_chtmsg FROM ( "
				+ " select count(*) over() num,row_number() over (order by a.shopid,a.createtime) rn,a.* from dcp_ding_approve a "
				+ " where a.eid='"+eId+"' and to_char(createtime,'YYYY-MM-DD') >='"+beginDate+"'  and to_char(createtime,'YYYY-MM-DD') <='"+endDate+"' " );
		if (!Check.Null(shopId))
		{
			sqlbuf.append(" and a.shopid='"+shopId+"' ");
		}
		if (!Check.Null(billNo))
		{
			sqlbuf.append(" and a.billno='"+billNo+"' ");
		}
		if (!Check.Null(billType))
		{
			sqlbuf.append(" and a.billtype='"+billType+"' ");
		}
		if (!Check.Null(funcNo))
		{
			sqlbuf.append(" and a.funcno='"+funcNo+"' ");
		}
		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status='"+status+"' ");
		}
		if (!Check.Null(processStatus))
		{
			sqlbuf.append(" and a.process_status='"+processStatus+"' ");
		}
		sqlbuf.append( " ) a "
				+ " left join dcp_ding_approve_detail b on a.eid=b.eid and a.processid=b.processid "
				+ " left join DCP_DING_USERSET c on a.eid = c.EID and a.checkopid=c.userid "
				+ " left join DCP_ORG_lang d on a.eid=d.EID and a.shopid=d.organizationno and d.lang_type='"+langType+"' "
				+ " left join DCP_MODULAR_function e on e.EID=a.eid and e.funcno=a.funcno "
				+ " WHERE rn>"+startRow+" and rn<="+(startRow+pageSize) + " order by b.pluno,b.item" 
				);
		sql=sqlbuf.toString();
		return sql;

	}

}
