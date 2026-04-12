package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TGArrivalQueryReq;
import com.dsc.spos.json.cust.res.DCP_TGArrivalQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：TGArrivalGetDCP
 * 服务说明：预计到团查询
 * @author jinzma 
 * @since  2019-02-13
 * 引用SPosAdvanceService
 */
public class DCP_TGArrivalQuery  extends  SPosAdvanceService <DCP_TGArrivalQueryReq,DCP_TGArrivalQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_TGArrivalQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		String beginDate = req.getBeginDate();
		String endDate =req.getEndDate();

		if(Check.Null(beginDate)){
			errMsg.append("预约开始日期不可为空值, ");
			isFail = true;
		}
		if(Check.Null(endDate)){
			errMsg.append("预约截止日期不可为空值, ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_TGArrivalQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGArrivalQueryReq>(){};
	}

	@Override
	protected DCP_TGArrivalQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGArrivalQueryRes();
	}

//	@Override
//	protected TGArrivalGetDCPRes processJson(TGArrivalGetDCPReq req) throws Exception {}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_TGArrivalQueryReq req) throws Exception {
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String oShopId = req.getoShopId(); 
		String orderNO = req.getOrderNO();
		String beginDate = req.getBeginDate();
		String endDate = req.getEndDate();
		String travelNO = req.getTravelNO();
		String guideNO = req.getGuideNO();
		String status =req.getStatus();
		String langType = req.getLangType();
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(" select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.orderno desc) rn , "
				+ " a.SHOPID,h.org_name as shopname,a.orderno,a.travelno,b.supplier_name as travelname,a.guideno,c.supplier_name as guidename,a.guidetel, "
				+ " a.countrycode,d.tgcategoryname as countryname,a.orderdate,a.ordertime,a.peoplenum,a.status,a.memo,a.arrivaltime, "
				+ " a.leavetime,a.tourgroupno,a.address,a.createby,e.op_name as createbyname,a.modifyby,f.op_name as modifybyname, "
				+ " a.confirmby,g.op_name as confirmbyname from  DCP_TGARRIVAL a "
				+ " left join DCP_SUPPLIER_lang b on a.EID=b.EID and a.travelno=b.supplier and b.status='100' and b.lang_type='"+langType+"' "
				+ " left join DCP_SUPPLIER_lang c on a.EID=c.EID and a.guideno=c.supplier and c.status='100' and c.lang_type='"+langType+"' "			
				+ " left join DCP_TGCATEGORY_lang d on a.EID=d.EID and a.countrycode=d.tgcategoryno and d.status='100' and d.lang_type='"+langType+"' and d.type='1' "
				+ " left join platform_staffs_lang e on a.EID=e.EID and a.createby=e.opno and e.lang_type='"+langType+"' and e.status='100'  "
				+ " left join platform_staffs_lang f on a.EID=f.EID and a.modifyby=f.opno and f.lang_type='"+langType+"' and f.status='100'  "
				+ " left join platform_staffs_lang g on a.EID=g.EID and a.confirmby=g.opno and g.lang_type='"+langType+"' and g.status='100'  "
				+ " left join DCP_ORG_lang h on a.EID=h.EID and a.SHOPID=h.organizationno and h.lang_type='"+langType+"' and h.status='100'  "
				+ " where a.EID='"+eId+"'  " );
		
		if (!Check.Null(oShopId)) sqlbuf.append( " and a.SHOPID='"+oShopId+"'  " );
		if (!Check.Null(orderNO))  sqlbuf.append( " and a.orderno='"+orderNO+"'  " );
		if (!Check.Null(beginDate)&&!Check.Null(endDate)) sqlbuf.append( " and (a.orderdate>='"+beginDate+"' and a.orderdate<='"+endDate+"')  " );	
		if (!Check.Null(travelNO))  sqlbuf.append( " and a.travelno='"+travelNO+"'  " );
		if (!Check.Null(guideNO))   sqlbuf.append( " and a.guideno='"+guideNO+"'   " );
		if (!Check.Null(status))    sqlbuf.append( " and a.status='"+status+"'   " );

		sqlbuf.append( "  ) " );
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));		

		sql = sqlbuf.toString();
		return sql;		
	}

	@Override
	protected void processDUID(DCP_TGArrivalQueryReq req, DCP_TGArrivalQueryRes res) throws Exception {
	// TODO 自动生成的方法存根

		// TODO 自动生成的方法存根
		String sql=null;		
		try
		{
			// 当前日期时间  > 预约日期时间   将单据状态从新建修改为延迟未到
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String nowDate = dfDate.format(cal.getTime());
			String nowTime = dfTime.format(cal.getTime());			
			sql=" update DCP_TGARRIVAL set status='2' where status='0' "
				+ " and ((orderdate='"+nowDate+"' and ordertime<'"+nowTime+"')  or  orderdate<'"+nowDate+"' ) ";
			
			ExecBean eb = new ExecBean(sql);	
			this.addProcessData(new DataProcessBean(eb));
			this.doExecuteDataToDB();
			
			//查询资料
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				res.setDatas(new ArrayList<DCP_TGArrivalQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_TGArrivalQueryRes.level1Elm oneLv1= res.new level1Elm();
					oneLv1.setShopId(oneData.get("SHOPID").toString());
					oneLv1.setShopName(oneData.get("SHOPNAME").toString());
					oneLv1.setOrderNO(oneData.get("ORDERNO").toString());
					oneLv1.setTravelNO(oneData.get("TRAVELNO").toString());
					oneLv1.setTravelName(oneData.get("TRAVELNAME").toString());
					oneLv1.setGuideNO(oneData.get("GUIDENO").toString());
					oneLv1.setGuideName(oneData.get("GUIDENAME").toString());
					oneLv1.setGuideTel(oneData.get("GUIDETEL").toString());
					oneLv1.setCountryCode(oneData.get("COUNTRYCODE").toString());
					oneLv1.setCountryName(oneData.get("COUNTRYNAME").toString());
					oneLv1.setOrderDate(oneData.get("ORDERDATE").toString());
					oneLv1.setOrderTime(oneData.get("ORDERTIME").toString());
					oneLv1.setPeopleNum(oneData.get("PEOPLENUM").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setArrivalTime(oneData.get("ARRIVALTIME").toString());
					oneLv1.setLeaveTime(oneData.get("LEAVETIME").toString());
					oneLv1.setTourGroupNO(oneData.get("TOURGROUPNO").toString());
					oneLv1.setAddress(oneData.get("ADDRESS").toString());
					oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
					oneLv1.setCreateByName(oneData.get("CREATEBYNAME").toString());
					oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
					oneLv1.setModifyByName(oneData.get("MODIFYBYNAME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					
					res.getDatas().add(oneLv1);			
				}				
			}
			else
			{
				res.setDatas(new ArrayList<DCP_TGArrivalQueryRes.level1Elm>());				
				totalRecords = 0;
				totalPages = 0;			
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
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
	protected List<InsBean> prepareInsertData(DCP_TGArrivalQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGArrivalQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGArrivalQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

}
