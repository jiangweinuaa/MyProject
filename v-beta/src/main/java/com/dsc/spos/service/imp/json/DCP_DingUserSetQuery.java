package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DingUserSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_DingUserSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DingUserSetGetDCP
 * 服务说明： 钉钉用户设置查询
 * @author jinzma
 * @since  2019-10-28
 */
public class DCP_DingUserSetQuery extends SPosBasicService<DCP_DingUserSetQueryReq,DCP_DingUserSetQueryRes >{

	@Override
	protected boolean isVerifyFail(DCP_DingUserSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DingUserSetQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingUserSetQueryReq>(){};
	}

	@Override
	protected DCP_DingUserSetQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingUserSetQueryRes();
	}

	@Override
	protected DCP_DingUserSetQueryRes processJson(DCP_DingUserSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;		
		DCP_DingUserSetQueryRes	res = this.getResponse();
		try
		{
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
				res.setDatas(new ArrayList<DCP_DingUserSetQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData) 
				{					
					DCP_DingUserSetQueryRes.level1Elm oneLv1= new DCP_DingUserSetQueryRes().new level1Elm();
					String opNO = oneData.get("OPNO").toString();
					String opName = oneData.get("OP_NAME").toString();
					String opGName = oneData.get("OPGNAME").toString();
					String staffsShopName = oneData.get("ORG_NAME").toString();
					String userId = oneData.get("USERID").toString();
					String userName = oneData.get("USERNAME").toString();
					String deptId = oneData.get("DEPTID").toString();
					String status = oneData.get("STATUS").toString();

					oneLv1.setStatus(status);
					oneLv1.setDeptId(deptId);
					oneLv1.setOpGName(opGName);
					oneLv1.setOpName(opName);
					oneLv1.setOpNo(opNO);
					oneLv1.setStaffsShopName(staffsShopName);
					oneLv1.setUserId(userId);
					oneLv1.setUserName(userName);

					res.getDatas().add(oneLv1);	
					oneLv1 = null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_DingUserSetQueryRes.level1Elm>());				
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
	protected String getQuerySql(DCP_DingUserSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();
		String opGNO = req.getRequest().getOpGNo();
		String staffsShopNO = req.getRequest().getStaffsShopNo();
		String keyTxt = req.getRequest().getKeyTxt();
		String isMapping = req.getRequest().getIsMapping();


		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(" select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.opno) rn,"
				+ " a.EID,a.opno,b.op_name,c.opgname,d.SHOPID,f.org_name,e.userid,e.username,e.deptid,e.status from platform_staffs a "
				+ " left join platform_staffs_lang b on a.EID=b.EID and a.opno=b.opno and b.lang_type='"+langType+"' " );
		if (!Check.Null(staffsShopNO))
		{
			sqlbuf.append( " inner join (");	
		}
		else
		{
			sqlbuf.append(  " left join (" );
		}

		sqlbuf.append( " select a.EID,a.opno, max(a.SHOPID) as SHOPID from platform_staffs_shop a "
				+ " where  a.EID='"+eId+"' and a.status='100' ");

		if (!Check.Null(staffsShopNO))
		{
			sqlbuf.append("  and a.SHOPID='"+staffsShopNO+"' ");
		}
		sqlbuf.append(" group by a.EID,a.opno) d on a.EID=d.EID and a.opno=d.opno "
				+ " left join DCP_DING_USERSET e on a.EID=e.EID and a.opno=e.opno "
				+ " left join DCP_ORG_lang f on a.EID=f.EID and f.lang_type='"+langType+"'"
				+ " and f.status='100' and f.organizationno=d.SHOPID "
				+ " left join   "
				+ " ( select a.EID,a.opNO,max(a.opGroup)as opGroup,max(b.opgName)as opgName from platform_staffs_role a "
				+ " INNER JOIN platform_role b ON a.EID = b.EID AND a.opGroup = b.opGroup "
				+ " and a.status='100' and B.status='100' where a.EID = '"+eId+"' "
				+ " group by a.EID,a.opNO ) c "
				+ " on a.EID=c.EID and a.opNO = c.opNO  "
				+ " where a.EID='"+eId+"' and a.status='100' ");

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.opno like '%%"+keyTxt+"%%' or "
					+ " b.op_name like '%%"+keyTxt+"%%' or "
					+ " e.USERID like '%%"+keyTxt+"%%' or "
					+ " e.USERNAME like '%%"+keyTxt+"%%')  ");
		}

		if (!Check.Null(opGNO))
		{
			sqlbuf.append(" and (c.opgname like '%%"+opGNO+"%%'  or a.opgroup = '"+opGNO+"') ");
		}	

		if (!Check.Null(isMapping) && isMapping.equals("Y"))
		{
			sqlbuf.append(" and e.userid is not null ");
		}


		sqlbuf.append(" ) where  rn> " + startRow + " and rn<=" + (startRow+pageSize) );	

		sql = sqlbuf.toString();
		return sql;

	}

}
