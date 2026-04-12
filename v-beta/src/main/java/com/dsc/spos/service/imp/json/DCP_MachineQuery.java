package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_MachineQueryReq;
import com.dsc.spos.json.cust.res.DCP_MachineQueryRes;

public class DCP_MachineQuery extends SPosBasicService <DCP_MachineQueryReq,DCP_MachineQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_MachineQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_MachineQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MachineQueryReq>(){};
	}

	@Override
	protected DCP_MachineQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MachineQueryRes();
	}

	@Override
	protected DCP_MachineQueryRes processJson(DCP_MachineQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql = null;
		//查询资料
		DCP_MachineQueryRes res = null;
		res = this.getResponse();
		int totalRecords;								//总笔数
		int totalPages;
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_MachineQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQDataDetail.get(0);
				String num = oneData_Count.get("COUNT").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_MachineQueryRes.level1Elm oneLv1 = new DCP_MachineQueryRes().new level1Elm();
					
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("ORG_NAME").toString();
					String machineId = oneData.get("MACHINE").toString();
					String machineName = oneData.get("MACHINENAME").toString();
					String hardwareInfo = oneData.get("HARDWAREINFO").toString();
					String sNumber = oneData.get("SNUMBER").toString();
					String regflg = oneData.get("REGFLG").toString();
					String status = oneData.get("STATUS").toString();
					String businessType = oneData.get("BUSINESSTYPE").toString();
					String apiUserCode = oneData.get("APIUSERCODE").toString();
					

					//设置响应
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);
					oneLv1.setMachineId(machineId);
					oneLv1.setMachineName(machineName);
					oneLv1.setHardwareInfo(hardwareInfo);
					oneLv1.setsNumber(sNumber);
					oneLv1.setRegflg(regflg);
					oneLv1.setStatus(status);
					oneLv1.setBusinessType(businessType);
					oneLv1.setApiUserCode(apiUserCode);
					oneLv1.setAppNo(oneData.get("APPTYPE").toString());
					oneLv1.setAppName(oneData.get("APPNAME").toString());
					oneLv1.setChannelId(oneData.get("CHANNELID").toString());
					oneLv1.setChannelName(oneData.get("CHANNELNAME").toString());
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
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_MachineQueryReq req) throws Exception {
		String sql=null;			
		String eId= req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String shopId = req.getRequest().getShopId();
		String regflg = req.getRequest().getRegflg();
		String langType=req.getLangType();
		int pageSize=req.getPageSize(); 

		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( " "
				+ " select count,rn,SHOPID,org_name,machine,machineName,hardwareInfo,sNumber,regflg,status,BUSINESSTYPE,APIUSERCODE,APPTYPE,APPNAME,CHANNELID,CHANNELNAME from ( "
				+ " select count(*) over () count ,row_number() over( order by a.SHOPID,a.machine  ) as rn, "
				+ " a.SHOPID,b.org_name,a.machine,a.channelid,machineName,c.machinecode as hardwareInfo, c.customerno as sNumber, c.isregister as regflg, "
				+ " a.status,a.BUSINESSTYPE,a.APIUSERCODE,a.APPTYPE,d.APPNAME,e.channelname "
				+ " from platform_machine a "
				+ " left join DCP_ORG_lang b on a.EID=b.EID and b.organizationno=a.SHOPID and b.status='100' "
				+ " and b.lang_type='"+ langType +"' "
				+ " left join platform_cregisterdetail c on c.EID=a.EID and c.SHOPID=a.SHOPID and c.machine=a.machine"
				+ " left join PLATFORM_APP d on a.APPTYPE=d.APPNO "
				+ " left join CRM_CHANNEL e on a.eid=e.eid and a.channelid=e.channelid and a.apptype=e.appno "
				+ " where a.EID='"+ eId +"' " );
		
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.SHOPID LIKE '%%"+ keyTxt +"%%' OR b.org_name LIKE '%%"+ keyTxt +"%%' "
					+ " or A.machine like '%%"+ keyTxt +"%%'  or machineName LIKE '%%"+ keyTxt +"%%'  )  ");
		}
		
		if (shopId != null && shopId.length()>0)
		{
			sqlbuf.append(" AND a.SHOPID = '"+ shopId +"'   ");
		}
		
		if (regflg != null && regflg.length()>0)
		{
			sqlbuf.append(" and c.isregister='"+ regflg +"' ");
		}
		
		sqlbuf.append( " ) WHERE rn>" + startRow + " and rn<=" + (startRow+pageSize) + "  order by SHOPID,machine   ");
		sql = sqlbuf.toString();
		return sql;
	}

}
