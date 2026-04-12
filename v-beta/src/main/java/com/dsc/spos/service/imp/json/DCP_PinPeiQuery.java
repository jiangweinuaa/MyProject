package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_PinPeiQueryReq;
import com.dsc.spos.json.cust.res.DCP_PinPeiQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_PinPeiQuery
 * 服务说明：拼胚查询
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiQuery extends SPosBasicService<DCP_PinPeiQueryReq,DCP_PinPeiQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_PinPeiQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		if (Check.Null(beginDate))
		{
			errMsg.append("开始日期不能为空,");
			isFail = true;
		}
		if (Check.Null(endDate))
		{
			errMsg.append("结束日期不能为空,");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PinPeiQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiQueryReq>(){};
	}

	@Override
	protected DCP_PinPeiQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiQueryRes();
	}

	@Override
	protected DCP_PinPeiQueryRes processJson(DCP_PinPeiQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_PinPeiQueryRes res =  this.getResponse();
		try 
		{
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数

			DCP_PinPeiQueryRes.level1Elm lv1 = res.new level1Elm();
			lv1.setPinPeiList(new ArrayList<DCP_PinPeiQueryRes.level2Elm>());		
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				for (Map<String, Object> oneData : getQData) {
					DCP_PinPeiQueryRes.level2Elm oneLv2 = res.new level2Elm();
					String memo=oneData.get("MEMO").toString();
					String pinPeiNo=oneData.get("PINPEINO").toString();
					String bDate=oneData.get("BDATE").toString();
					String stockInNo=oneData.get("STOCKINNO").toString();
					String stockOutNo=oneData.get("STOCKOUTNO").toString();
					String stockInWearehouse=oneData.get("STOCKINWEAREHOUSE").toString();
					String stockInWearehouseName=oneData.get("STOCKINWEAREHOUSENAME").toString();
					String stockOutWearehouse=oneData.get("STOCKOUTWEAREHOUSE").toString();
					String stockOutWearehouseName=oneData.get("STOCKOUTWEAREHOUSENAME").toString();
					String stockInBsNo=oneData.get("STOCKINBSNO").toString();
					String stockInBsName=oneData.get("STOCKINBSNAME").toString();
					String stockOutBsNo=oneData.get("STOCKOUTBSNO").toString();
					String stockOutBsName=oneData.get("STOCKOUTBSNAME").toString();
					String status=oneData.get("STATUS").toString();
					String totPqty=oneData.get("TOT_PQTY").toString();
					String totCqty=oneData.get("TOT_CQTY").toString();
					String totAmt=oneData.get("TOT_AMT").toString();
					String totDistriAmt=oneData.get("TOT_DISTRIAMT").toString();
					String createOpId=oneData.get("CREATEOPID").toString();
					String createOpName=oneData.get("CREATEOPNAME").toString();
					String createTime=oneData.get("CREATE_TIME").toString();  ///数据库转字符串另取字段名
					String lastModiOpId=oneData.get("LASTMODIOPID").toString();
					String lastModiOpName=oneData.get("LASTMODIOPNAME").toString();
					String lastModiTime=oneData.get("LASTMODI_TIME").toString();  ///数据库转字符串另取字段名

					oneLv2.setCreateOpId(createOpId);
					oneLv2.setCreateOpName(createOpName);
					oneLv2.setCreateTime(createTime);
					oneLv2.setLastModiOpId(lastModiOpId);
					oneLv2.setLastModiOpName(lastModiOpName);
					oneLv2.setLastModiTime(lastModiTime);					
					oneLv2.setPinPeiNo(pinPeiNo);
					oneLv2.setbDate(bDate);
					oneLv2.setStockInNo(stockInNo);
					oneLv2.setStockOutNo(stockOutNo);
					oneLv2.setStatus(status);
					oneLv2.setTotPqty(totPqty);
					oneLv2.setTotCqty(totCqty);
					oneLv2.setTotAmt(totAmt);
					oneLv2.setTotDistriAmt(totDistriAmt);
					oneLv2.setStockInWearehouse(stockInWearehouse);
					oneLv2.setStockInWearehouseName(stockInWearehouseName);
					oneLv2.setStockOutWearehouse(stockOutWearehouse);
					oneLv2.setStockOutWearehouseName(stockOutWearehouseName);
					oneLv2.setStockInBsNo(stockInBsNo);
					oneLv2.setStockInBsName(stockInBsName);
					oneLv2.setStockOutBsNo(stockOutBsNo);
					oneLv2.setStockOutBsName(stockOutBsName);
					oneLv2.setMemo(memo);

					lv1.getPinPeiList().add(oneLv2);
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;			
			}
			res.setDatas(lv1);
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			return res;
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_PinPeiQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(""
				+ " select a.*,"
				+ " to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as create_time,"
				+ " to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastmodi_time,"
				+ " w1.warehouse_name as stockinwearehousename,"
				+ " w2.warehouse_name as stockoutwearehousename,"
				+ " r1.reason_name as stockinbsname,"
				+ " r2.reason_name as stockoutbsname"
				+ " from ("
				+ " select count(*) over() num,row_number() over (order by a.status asc,a.bdate desc,a.pinpeino desc) rn,"
				+ " a.* from dcp_pinpei a"
				+ " where eid='"+eId+"' and shopid='"+shopId+"' and bdate>='"+beginDate+"' and bdate<='"+endDate+"'"
				+ " ");

		if (!Check.Null(status) && !status.equals("all"))
		{
			sqlbuf.append(" and status='"+status+"'");
		}
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (pinpeino like '%%"+keyTxt+"%%' or memo like '%%"+keyTxt+"%%'"
					+ " or tot_pqty like '%%"+keyTxt+"%%' or tot_amt like '%%"+keyTxt+"%%'"
					+ " or tot_distriamt like '%%"+keyTxt+"%%' or tot_cqty like '%%"+keyTxt+"%%'"
					+ " or stockinno like '%%"+keyTxt+"%%' or stockoutno like '%%"+keyTxt+"%%')"
					+ " ");
		}

		sqlbuf.append(" ) a");
		sqlbuf.append(" "
				+ " left join dcp_warehouse_lang w1 on w1.eid=a.eid and w1.organizationno='"+shopId+"' and w1.warehouse=a.stockinwearehouse and w1.lang_type='"+langType+"'"
				+ " left join dcp_warehouse_lang w2 on w2.eid=a.eid and w2.organizationno='"+shopId+"' and w2.warehouse=a.stockoutwearehouse and w2.lang_type='"+langType+"'"
				+ " left join dcp_reason_lang r1 on r1.eid=a.eid and r1.bstype='14' and r1.bsno=a.stockinbsno and r1.lang_type='"+langType+"'"
				+ " left join dcp_reason_lang r2 on r2.eid=a.eid and r2.bstype='15' and r2.bsno=a.stockoutbsno and r2.lang_type='"+langType+"'"
				+ " where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)
				+ " order by a.status asc,a.bdate desc,a.pinpeino desc "
				+ " ");

		sql=sqlbuf.toString();
		return sql;		
	}


}
