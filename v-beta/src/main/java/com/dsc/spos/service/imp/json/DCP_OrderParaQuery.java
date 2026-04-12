package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderParaQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderParaQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderParaQueryRes.responseDatas;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 门店订单参数查询
 * 
 * @author yuanyy 2019-11-22
 *
 */
public class DCP_OrderParaQuery extends SPosBasicService<DCP_OrderParaQueryReq, DCP_OrderParaQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderParaQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderParaQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderParaQueryReq>() {
		};
	}

	@Override
	protected DCP_OrderParaQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderParaQueryRes();
	}

	

	@Override
	protected DCP_OrderParaQueryRes processJson(DCP_OrderParaQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		DCP_OrderParaQueryRes res = null;
		res = this.getResponse();
		responseDatas datas = res.new responseDatas();
		datas.setOrgList(new ArrayList<DCP_OrderParaQueryRes.level1Elm>());

		try
		{

			int pageNumber = req.getPageNumber();
			int pageSize = req.getPageSize();
			int startRow = (pageNumber - 1) * pageSize;
			int totalRecords = 0; // 总笔数
			int totalPages = 0;
			String scurdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			String langType = req.getLangType();
			if (langType == null || langType.isEmpty())
			{
				langType = "zh_CN";
			}
			String skeytxt = "";
			if (req.getRequest() != null)
			{
				skeytxt = req.getRequest().getKeyTxt();
			}
			String sql = "";
			StringBuilder sqlBuffer = new StringBuilder();

			sqlBuffer.append(" select * from (");
			sqlBuffer.append(" select  COUNT(*) OVER() NUM , row_number() OVER(ORDER BY a.organizationNo) rn , "
					+ " A.*,  c.org_name as orgname from DCP_ORG A  "
					+ " left join DCP_ORG_lang C on A.EID=C.EID and A.OrganizationNo=C.OrganizationNo  and C.lang_type='" + langType + "' ");
            if("1".equals(req.getRequest().getBusinessType())||
            	"0".equals(req.getRequest().getBusinessType())
            		)
            { 
            	sqlBuffer.append("inner join (SELECT DISTINCT EID,SHOPID  FROM Platform_CregisterDetail WHERE PRODUCTTYPE='46' AND bdate <= '"+scurdate+"' AND eDate >= '"+scurdate+"' AND SHOPID IS NOT NULL ) D "
    					+ " ON A.EID=D.EID AND A.OrganizationNo=D.SHOPID ");
            }
            sqlBuffer.append(" where  A.status='100' and A.EID='" + req.geteId() + "' ");
			if (skeytxt != null && skeytxt.isEmpty() == false)
			{
				sqlBuffer.append(
						" and (A.OrganizationNo like '%%" + skeytxt + "%%' OR C.org_name like '%%" + skeytxt + "%%')");
			}

			sqlBuffer.append(" ) where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + " order by rn ");

			sql = sqlBuffer.toString();
			List<Map<String, Object>> allorg = this.doQueryData(sql, null);

			if (allorg != null && allorg.size() > 0)
			{
				String num = allorg.get(0).getOrDefault("NUM", "0").toString();
				totalRecords = Integer.parseInt(num);
				// 算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map : allorg)
				{
					
					DCP_OrderParaQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					oneLv1.setOrganizationNo(map.get("ORGANIZATIONNO").toString());
					oneLv1.setOrganizationName(map.get("ORGNAME").toString());
					oneLv1.setOrgForm(map.get("ORG_FORM").toString());
					oneLv1.setShopBeginTime(map.get("SHOPBEGINTIME").toString());
					oneLv1.setShopEndTime(map.get("SHOPENDTIME").toString());
					oneLv1.setProvince(map.get("PROVINCE").toString());
					oneLv1.setCity(map.get("CITY").toString());					
					oneLv1.setCounty(map.get("COUNTY").toString());
					oneLv1.setStreet(map.get("STREET").toString());
					oneLv1.setAddress(map.get("ADDRESS").toString());
					oneLv1.setPhone(map.get("PHONE").toString());
					
					datas.getOrgList().add(oneLv1);
					
				}
				

			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

		} 
		catch (Exception e)
		{
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setServiceStatus("200");
			res.setSuccess(false);
		}
		res.setDatas(datas);
		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderParaQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

}
