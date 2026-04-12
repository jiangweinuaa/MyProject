package com.dsc.spos.service.imp.json;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateDetailQueryRes;
 
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

 
public class DCP_POrderTemplateDetailQuery extends SPosBasicService<DCP_POrderTemplateDetailReq, DCP_POrderTemplateDetailQueryRes > {
	 
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateNO =req.getRequest().getTemplateNo();
		if (Check.Null(templateNO)) {
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_POrderTemplateDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_POrderTemplateDetailReq>(){};
	}

	@Override
	protected DCP_POrderTemplateDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_POrderTemplateDetailQueryRes();
	}

	@Override
	protected DCP_POrderTemplateDetailQueryRes processJson(DCP_POrderTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_POrderTemplateDetailQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		//try {
		
			sql = this.getCountSql(req);	
			String[] condCountValues = { }; //查詢條件
			List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getReasonCount != null && getReasonCount.isEmpty() == false) 
			{ 			
				Map<String, Object> total = getReasonCount.get(0);
				String num = total.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
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
			
			sql = this.getQuerySql(req);
			
			String[] conditionValues1 = { }; //查詢條件
			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			sql = this.getOrgQuerySql(req);
			List<Map<String, Object>> getQLangDataDetail=this.doQueryData(sql, conditionValues1);
			List<Map<String, Object>> getQLangData = null;
			sql = this.getDetailQuerySql(req);
			List<Map<String, Object>> getDataDetail=this.doQueryData(sql, conditionValues1);
			
			sql = this.getEmpQuerySql(req);
			List<Map<String, Object>> getEmpData=this.doQueryData(sql, conditionValues1);
			String eID="";
			String billNo="";
			String payType="";
			res.setDatas(new ArrayList<DCP_POrderTemplateDetailQueryRes.DataDetail>());    
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{				 			
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_POrderTemplateDetailQueryRes.DataDetail oneLv1 = res.new DataDetail();
					eID = oneData.get("EID").toString(); 
					billNo = oneData.get("PTEMPLATENO").toString();
				 
					oneLv1.setTemplateNo(oneData.get("PTEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("PTEMPLATE_NAME").toString());
					oneLv1.setPreDay(oneData.get("PRE_DAY").toString());
					oneLv1.setOptionalTime(oneData.get("OPTIONAL_TIME").toString());
					oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
					oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
					oneLv1.setReceiptOrgNo(oneData.get("RECEIPT_ORG").toString());
					oneLv1.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setHqPorder(oneData.get("HQPORDER").toString());
					oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
					oneLv1.setRdate_Type(oneData.get("RDATE_TYPE").toString());
					oneLv1.setRdate_Add(oneData.get("RDATE_ADD").toString());
					oneLv1.setRdate_Values(oneData.get("RDATE_VALUES").toString());
					oneLv1.setRevoke_Day(oneData.get("REVOKE_DAY").toString());
					oneLv1.setRevoke_Time(oneData.get("REVOKE_TIME").toString());
					oneLv1.setRdate_Times(oneData.get("RDATE_TIMES").toString());
					oneLv1.setIsAddGoods(oneData.get("ISADDGOODS").toString());
					oneLv1.setIsShowHeadStockQty(oneData.get("ISSHOWHEADSTOCKQTY").toString());
					oneLv1.setSupplierType(oneData.get("SUPPLIERTYPE").toString());
					oneLv1.setAllotType(oneData.get("ALLOTTYPE").toString());
					oneLv1.setFloatScale(oneData.get("FLOATSCALE").toString());
					oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
					oneLv1.setCreateByName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
					oneLv1.setCreateTime(oneData.get("CREATE_TIME").toString());
					oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
					oneLv1.setModifyByName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setModifyDate(oneData.get("MODIFY_DATE").toString());
					oneLv1.setModifyTime(oneData.get("MODIFY_TIME").toString());
 
					String id = eID;
					String bill = billNo;
					String pType = payType;
					// 
					getQLangData = getDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("PTEMPLATENO").toString()))
							.collect(Collectors.toList());
					
					oneLv1.setGoodsList(new ArrayList<DCP_POrderTemplateDetailQueryRes.DetailList>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_POrderTemplateDetailQueryRes.DetailList oneLv2 = res.new DetailList();
						oneLv2.setItem(oneData2.get("ITEM").toString());
						oneLv2.setPluNo(oneData2.get("PLUNO").toString());
						oneLv2.setPluName(oneData2.get("PLU_NAME").toString());
						oneLv2.setPUnit(oneData2.get("PUNIT").toString());
						oneLv2.setPUnitName(oneData2.get("UNAME").toString());
						oneLv2.setMinQty(oneData2.get("MIN_QTY").toString());
						oneLv2.setMaxQty(oneData2.get("MAX_QTY").toString());
						oneLv2.setMulQty(oneData2.get("MUL_QTY").toString());
						oneLv2.setDefQty(oneData2.get("DEFAULT_QTY").toString());
						oneLv2.setStatus(oneData2.get("STATUS").toString());
						oneLv2.setGroupNo(oneData2.get("PORDER_GROUP").toString());
						//oneLv2.setGroupName(oneData2.get("GROUPNAME").toString());
						oneLv2.setSupplier(oneData2.get("SUPPLIER").toString());
						oneLv2.setSupplierName(oneData2.get("SUPPLIERNAME").toString());

						oneLv2.setBaseUnit(oneData2.get("BASEUNIT").toString());
						oneLv2.setBaseUnitName(oneData2.get("BASEUNITNAME").toString());
						oneLv2.setCategory(oneData2.get("CATEGORY").toString());
						oneLv2.setCategoryName(oneData2.get("CATEGORYNAME").toString());
						oneLv2.setSpec(oneData2.get("SPEC").toString());


						//价格和数量的换算是反着来的
						String supPrice = oneData2.get("SUPPRICE").toString();
						String gpunit = oneData2.get("GPUNIT").toString();
						Map<String, Object> unitCalculate = PosPub.getUnitCalculatePrice(dao,req.geteId(), oneLv2.getPluNo(), gpunit, oneLv2.getPUnit(), supPrice);
						String afterDecimal = unitCalculate.get("afterDecimal").toString();
						oneLv2.setSupPrice(afterDecimal);


						oneLv1.getGoodsList().add(oneLv2);
				        oneLv2 = null;
					}
					
				 
					//组织先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("PTEMPLATENO").toString()))
							.collect(Collectors.toList());
					
					oneLv1.setOrgList(new ArrayList<DCP_POrderTemplateDetailQueryRes.OrgList>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_POrderTemplateDetailQueryRes.OrgList oneLv2 = res.new OrgList();
						oneLv2.setOrgNo(oneData2.get("ORGNO").toString());
						oneLv2.setOrgName(oneData2.get("ORGNAME").toString());
						oneLv2.setIsMustAllot(oneData2.get("ISMUSTALLOT").toString());
						oneLv2.setSortId(oneData2.get("SORTID").toString());
						oneLv2.setStatus(oneData2.get("STATUS").toString());

				        oneLv1.getOrgList().add(oneLv2);
				        oneLv2 = null;
					}
					
					//组织先筛选出符合当前条件的数据
					getQLangData = getEmpData.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("PTEMPLATENO").toString()))
							.collect(Collectors.toList());
					
					oneLv1.setEmpList(new ArrayList<DCP_POrderTemplateDetailQueryRes.EmpList>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_POrderTemplateDetailQueryRes.EmpList oneLv2 = res.new EmpList();
						oneLv2.setEmployeeNo(oneData2.get("EMPLOYEENO").toString());
						oneLv2.setEmployeeName(oneData2.get("EMPLOYEENAME").toString());
						oneLv2.setStatus(oneData2.get("STATUS").toString());
 
				        oneLv1.getEmpList().add(oneLv2);
				        oneLv2 = null;
					}
					
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	res.setDatas(new ArrayList<DCP_POrderTemplateDetailQueryRes.DataDetail>());
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,req.getServiceId()+ e.getMessage());//add by 01029 20240703
		//}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_POrderTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String type = null;
		String status = null; 
		String isBillQuery = null;
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getTemplateNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.*  "
					+ "  , p.op_NAME as CREATEOPNAME,d1.DEPARTNAME as CREATEDEPTNAME,c.op_NAME as lastModiOpName "
					+ " ,c1.org_name as RECEIPTORGNAME FROM dcp_ptemplate a"
					
					+ " left join platform_staffs_lang  p on a.eid=p.eid and p.opno=a.CREATEBY and p.lang_type='"+req.getLangType()+"' "
				    + " left join platform_staffs_lang  c on a.eid=c.eid and c.opno=a.MODIFYBY and c.lang_type='"+req.getLangType()+"' "
					+"  left join DCP_DEPARTMENT_LANG d1  on p.eid=d1.eid and a.CREATEDEPTID=d1.DEPARTNO and d1.lang_type='"+langType+"' "
					+ " left join dcp_org_lang c1 on a.eid=c1.eid and a.receipt_org=c1.organizationno and c1.lang_type='"+langType+"'"
					+ " WHERE a.EID = '"+eId+"' and a.doc_type='0' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.ptemplateno  =  "+SUtil.RetTrimStr(key)); 
		}
 
		sqlbuf.append(" ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" order by ptemplateno " );
		
		
		sql = sqlbuf.toString();
		//logger.info(sql);  
		return sql;
	}
	
	
	
	/**
	 * 新增要處理的資料(先加入的, 先處理)
	 * 
	 * @param row
	 */
	protected final void addProcessData(DataProcessBean row) {
		this.pData.add(row);
	}
	
	/**
	 * 计算总数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getCountSql(DCP_POrderTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getTemplateNo();
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num   "	
				+ " FROM dcp_ptemplate a"
				+ " WHERE a.EID = '"+eId+"' and a.doc_type='0' " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.ptemplateno  =  "+SUtil.RetTrimStr(key)); 
 
		}
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getOrgQuerySql(DCP_POrderTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getTemplateNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*    FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid,a.PTEMPLATENO,a.SHOPID as ORGNO,c1.org_name as ORGNAME,a.ISMUSTALLOT ,a.SORTID,a.STATUS  "
					+ " FROM DCP_PTEMPLATE_SHOP a"
					+ " left join dcp_org_lang c1 on a.eid=c1.eid and a.SHOPID=c1.organizationno and c1.lang_type='"+langType+"'"
					
				+ " WHERE a.EID = '"+eId+"' and a.doc_type='0' " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.ptemplateno  =  "+SUtil.RetTrimStr(key)); 
 
		}
 
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append(" order by SORTID " );
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	 
	protected String getDetailQuerySql(DCP_POrderTemplateDetailReq req) throws Exception {
		
		String sql=null;
		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" "
				+ " select a.item,a.pluno,b.plu_name,a.punit,c.uname,min_Qty,max_Qty,mul_Qty,a.status, "
				+ " a.default_Qty,a.porder_group,e.groupname,a.eid,a.PTEMPLATENO ,a.SUPPLIER, d2.sname as SUPPLIERNAME,d.SUPPRICE,d.spec,d.category,d.baseunit,d.punit as gpunit,f.uname as baseunitname," +
				"  g.category_name as categoryname "
				+" from DCP_ptemplate_detail a "
				+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
				+ " left join DCP_UNIT_lang   c on a.EID=c.EID and a.punit =c.unit and c.lang_type='"+ langType +"' "
				+ " inner join DCP_GOODS      d on d.status='100' and  a.EID=d.EID and a.pluno=d.pluno "
				+ " left join  DCP_ptemplate_detail_group e on a.EID=e.EID and a.porder_group=e.groupno "
				+" left join DCP_BIZPARTNER d2 on a.eid=d2.eid and a.SUPPLIER=d2.BIZPARTNERNO  " +
				" left join dcp_unit_lang f on f.eid=a.eid and f.unit=d.baseunit and f.lang_type='"+req.getLangType()+"' " +
				" left join DCP_CATEGORY_LANG g on g.eid=a.eid and g.category=d.category and g.lang_type='"+req.getLangType()+"' "
				+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '0' "
				+ " order by a.item "	) ;
		
		sql = sqlbuf.toString();
		return sql;
		
	}
	
	protected String getEmpQuerySql(DCP_POrderTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getTemplateNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*   FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid ,a.PTEMPLATENO, p.NAME as EMPLOYEENAME,a.STATUS ,a.EMPLOYEEID as EMPLOYEENO "
					+ " FROM DCP_PTEMPLATE_EMPLOYEE a "	
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.EMPLOYEEID "
				+ " WHERE a.EID = '"+eId+"' and a.doc_type='0' " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.ptemplateno  =  "+SUtil.RetTrimStr(key)); 
 
		}
 
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		 sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	
}
