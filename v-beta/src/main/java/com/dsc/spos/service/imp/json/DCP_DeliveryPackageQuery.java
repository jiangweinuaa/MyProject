package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DeliveryPackageQueryReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryPackageQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 货运包裹查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageQuery extends SPosBasicService<DCP_DeliveryPackageQueryReq, DCP_DeliveryPackageQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_DeliveryPackageQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliveryPackageQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliveryPackageQueryReq>(){};
	}

	@Override
	protected DCP_DeliveryPackageQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliveryPackageQueryRes();
	}

	@Override
	protected DCP_DeliveryPackageQueryRes processJson(DCP_DeliveryPackageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_DeliveryPackageQueryRes res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			sql = this.getQuerySql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);

			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(res.new level1Elm());

				res.getDatas().setDeliveryPackageList(new ArrayList<DCP_DeliveryPackageQueryRes.deliveryPackage>());

				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_DeliveryPackageQueryRes.deliveryPackage packages = res.new deliveryPackage();
					String packageNo = oneData.get("PACKAGENO").toString();
					String packageName = oneData.get("PACKAGENAME").toString();
					String measureNo = oneData.get("MEASURENO").toString();
					String measureName = oneData.get("MEASURENAME").toString();
					String temperateNo = oneData.get("TEMPERATENO").toString();
					String temperateName = oneData.get("TEMPERATENAME").toString();
					String packageFee = oneData.get("PACKAGEFEE").toString();
					String status = oneData.get("STATUS").toString();

					packages.setPackageNo(packageNo);
					packages.setPackageName(packageName);
					packages.setMeasureNo(measureNo);
					packages.setMeasureName(measureName);
					packages.setTemperateNo(temperateNo);
					packages.setTemperateName(temperateName);
					packages.setPackageFee(packageFee);
					packages.setStatus(status);
					packages.setCreateOpId(oneData.get("CREATEOPID").toString());
					packages.setCreateOpName(oneData.get("CREATEOPNAME").toString());
					packages.setCreateTime(oneData.get("CREATETIME").toString());
					packages.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
					packages.setCreateOpName(oneData.get("LASTMODIOPNAME").toString());
					packages.setCreateTime(oneData.get("LASTMODITIME").toString());

					res.getDatas().getDeliveryPackageList().add(packages);
					packages = null;

				}

			}
		} catch (Exception e) {

		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_DeliveryPackageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append("select * from ("
				+ " SELECT COUNT(DISTINCT PACKAGENO ) OVER() NUM ,dense_rank() over(ORDER BY PACKAGENO) rn ,"
				+ " PACKAGENO,PACKAGENAME,MEASURENO,MEASURENAME,TEMPERATENO,"
				+ " TEMPERATENAME,PACKAGEFEE,status,CREATEOPID,CREATEOPNAME,CREATETIME,"
				+ " LASTMODIOPID,LASTMODIOPNAME,LASTMODITIME "
				+ " FROM DCP_SHIPPACKAGESET  WHERE EID = '"+eId+"' " );

		if (keyTxt != null && keyTxt.length()!=0) 
		{ 	
			sqlbuf.append( " AND  (PACKAGENO LIKE '%%"+keyTxt+"%%' or  PACKAGENAME like '%%"+keyTxt+"%%')  ");
		}
		
		if (status != null && status.length()!=0) 
		{ 	
			sqlbuf.append( " AND  STATUS='"+status+"' ");
		}
		
		sqlbuf.append( " order by  PACKAGENO ) "
				+ " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " ORDER BY  PACKAGENO  "
				);

		sql = sqlbuf.toString();
		return sql;
	}

}
