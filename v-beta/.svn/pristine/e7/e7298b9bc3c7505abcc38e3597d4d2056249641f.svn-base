package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsMappingQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsMappingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsMappingQuery extends SPosBasicService<DCP_GoodsMappingQueryReq, DCP_GoodsMappingQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsMappingQueryReq req) throws Exception 
	{
	  // TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
				
		if (req.getRequest().getPlatformType() == null) 
		{
			errCt++;
			errMsg.append("平台类型不能为空, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	
		return isFail;
	
	}

	@Override
	protected TypeToken<DCP_GoodsMappingQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsMappingQueryReq>(){};
	}

	@Override
	protected DCP_GoodsMappingQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsMappingQueryRes();
	}

	@Override
	protected DCP_GoodsMappingQueryRes processJson(DCP_GoodsMappingQueryReq req) throws Exception 
	{
	  // TODO Auto-generated method stub

		//查询条件
		String eId = req.geteId();;
		String platformType = req.getRequest().getPlatformType();
		String keyTxt = req.getRequest().getKeyTxt();
		
		DCP_GoodsMappingQueryRes res = null;
		res = this.getResponse();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		//計算起啟位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		
		StringBuffer sb =new StringBuffer( "select * from (");
		sb.append( " select count(*) over() num,row_number() over(order by A.pluno ) as rn,A.*,b.LISTIMAGE,c.UNAME as UNITNAME from DCP_GOODS_mapping A "
			+ " left join dcp_goodsimage b  on A.EID=b.EID and A.Pluno=b.pluno  and B.Apptype='ALL' "
			+ " left join dcp_unit_lang  c  on a.eid=c.eid and a.unitid=c.unit and c.lang_type='"+req.getLangType()+"' "  );
        sb.append( " where A.EID='"+eId+"' and A.PLATFORMTYPE='"+platformType+"' ");
        if (!Check.Null(keyTxt))
        {
            sb.append(" and (A.PLUNAME like '%%"+keyTxt+"%%' or A.PLUNO like '%%"+keyTxt+"%%' or A.PLATFORMPLUNO like '%%"+keyTxt+"%%') ");
        }
        sb.append( " ) where rn>"+ startRow + " AND rn <= " + (startRow+pageSize)+"");
		
		List<Map<String, Object>> getQData_Count = this.doQueryData(sb.toString(), null);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{ 
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
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
		
		res.setPlatformType(platformType);
	  //返回给值
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{ 
			
			String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";
			
			String DomainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
			String imagePath="";

			if (DomainName.endsWith("/")) 
			{					
				imagePath=httpStr+DomainName+"resource/image/";
			}
			else 
			{
				imagePath=httpStr+DomainName+"/resource/image/";
			}	
			res.setDatas(new ArrayList<DCP_GoodsMappingQueryRes.level1Elm>());
			for(Map<String, Object> oneData : getQData_Count)
			{
				DCP_GoodsMappingQueryRes.level1Elm oneLv1=res.new level1Elm();
				oneLv1.setPluNo(oneData.get("PLUNO").toString());
				oneLv1.setPluName(oneData.get("PLUNAME").toString());
				oneLv1.setFileName(imagePath+oneData.get("LISTIMAGE").toString());
				oneLv1.setPlatformPluNo(oneData.get("PLATFORMPLUNO").toString());
				oneLv1.setShortCutCode(oneData.get("SHORTCUT_CODE").toString());	
				oneLv1.setStatus(oneData.get("STATUS").toString());	
				oneLv1.setUnitId(oneData.get("UNITID").toString());	
				oneLv1.setUnitName(oneData.get("UNITNAME").toString());		
				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}
		else
		{
			res.setDatas(new ArrayList<DCP_GoodsMappingQueryRes.level1Elm>());
		}
			
		return res;
		 
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_GoodsMappingQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
