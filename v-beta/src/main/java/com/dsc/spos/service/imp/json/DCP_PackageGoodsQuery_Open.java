package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_PackageGoodsQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_PackageGoodsQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_PackageGoodsQuery_Open extends SPosBasicService<DCP_PackageGoodsQuery_OpenReq, DCP_PackageGoodsQuery_OpenRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PackageGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		if(req.getRequest()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空!");
		}else{
			String type=req.getRequest().getType();
			if(type==null||type.length()<1){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "type不能为空!");
			}
		}
		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_PackageGoodsQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PackageGoodsQuery_OpenReq>(){};
	}

	@Override
	protected DCP_PackageGoodsQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PackageGoodsQuery_OpenRes();
	}

	@Override
	protected DCP_PackageGoodsQuery_OpenRes processJson(DCP_PackageGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		if(req.getApiUser()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接口账号查询的渠道信息为空！");
		}
		
		
		StringBuffer errMsg = new StringBuffer("");
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		if(Check.Null(appType)){
			errMsg.append("接口账号对应的渠道类型appType不可为空值, ");
			isFail = true;
		}
		if(Check.Null(channelId)){
			errMsg.append("接口账号对应的渠道编码channelId不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		
		
		DCP_PackageGoodsQuery_OpenRes res = this.getResponse();
		
		DCP_PackageGoodsQuery_OpenRes.levelDatas datas = res.new levelDatas();
		datas.setGoodsList(new ArrayList<DCP_PackageGoodsQuery_OpenRes.level1Elm>());
		
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		
		if (req.getPageSize()==0)
		{
			req.setPageSize(10);						
		}
		
		String sql = getQuerySql(req);
		HelpTools.writelog_fileName("套餐查询sql:"+sql+"\r\n", "DCP_PackageGoodsQuery_Open");
		
		
        
        int totalRecords;											//总笔数
        int totalPages;		
        
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false)
        {
        	 //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            Map<String, Boolean> getCondition = new HashMap<String, Boolean>(); //查詢條件
            getCondition.put("EID", true); 
            getCondition.put("PLUNO", true); 
            //调用过滤函数
            List<Map<String, Object>> getCond1Header=MapDistinct.getMap(getQData,getCondition);
                                                                            
            for (Map<String, Object> map : getCond1Header)
			{
            	DCP_PackageGoodsQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
            	oneLv1.setPackageItemList(new ArrayList<DCP_PackageGoodsQuery_OpenRes.level2Elm>());
            	
            	String pluNo1 = map.getOrDefault("PLUNO", "").toString();
            	String pluName1 = map.getOrDefault("PLUNAME", "").toString();
            	oneLv1.setPluNo(pluNo1);
            	oneLv1.setPluName(pluName1);
            	
            	List<Map<String,Object>> filterMaps1=getQData.stream().filter(g->g.get("PLUNO").toString().equals(pluNo1)).collect(Collectors.toList());
    			
            	for (Map<String, Object> map2 : filterMaps1){
            		DCP_PackageGoodsQuery_OpenRes.level2Elm oneLv2 = res.new level2Elm();       
            		String pluNo2 = map2.getOrDefault("DPLUNO", "").toString();
            		String pluName2 = map2.getOrDefault("BPLUNAME", "").toString();
            		String num2 = map2.getOrDefault("QTY", "").toString();
            		String extraAmt = map2.getOrDefault("EXTRAAMT", "").toString();
            		String issel = map2.getOrDefault("ISSEL", "").toString();
            		String splitPrice = map2.getOrDefault("SPLITPRICE", "").toString();
            		String split = map2.getOrDefault("SPLIT", "").toString();
            		String unit = map2.getOrDefault("DUNIT", "").toString();
            		oneLv2.setPluNo(pluNo2);
            		oneLv2.setPluName(pluName2);
            		oneLv2.setNum(num2);
            		oneLv2.setExtraAmt(extraAmt);
            		oneLv2.setIssel(issel);
            		oneLv2.setSplit(split);
            		oneLv2.setSplitPrice(splitPrice);
            		oneLv2.setUnit(unit);
            		oneLv1.getPackageItemList().add(oneLv2);
            		oneLv2 = null;
				}
            	datas.getGoodsList().add(oneLv1);
            	oneLv1 = null;
			}
            
        	
        }
        else 
        {
        	 totalRecords = 0;
             totalPages = 0;
		}
        getQData = null;
        res.setDatas(datas);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PackageGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		
		String pluNos = getString(req.getRequest().getGoodsList());
		 
		int pageSize = req.getPageSize();
		int pageNumber=req.getPageNumber();
        //計算起啟位置
        int startRow = ((pageNumber - 1) * pageSize);
        startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; 
        int endRow = startRow + pageSize;
		
		StringBuffer sqlBuffer = new StringBuffer("");
				
		sqlBuffer.append(" WITH T AS( ");
		sqlBuffer.append(" SELECT A.*FROM( ");
		sqlBuffer.append(" SELECT count(*) over() num,row_number() over ( ORDER BY A.PLUNO) RN,A.* ");
		sqlBuffer.append(" FROM DCP_GOODS A ");
		sqlBuffer.append(" WHERE A.EID='"+eId+"' AND A.PLUTYPE='PACKAGE') A ");
		sqlBuffer.append(" WHERE A.RN>"+startRow+" AND A.RN<="+endRow);
		if(pluNos!=null&&pluNos.length()>0&&"0".equals(req.getRequest().getType())){
			sqlBuffer.append(" AND A.PLUNO IN("+pluNos+") ");
		}
		sqlBuffer.append(" ) ");
		
		
		sqlBuffer.append(" SELECT T.NUM,C.PLU_NAME AS PLUNAME,T.RN,B.*,D.PLU_NAME AS BPLUNAME ");
		sqlBuffer.append(" FROM T ");
		sqlBuffer.append(" LEFT JOIN DCP_PGOODSCLASS_DETAIL B ON T.EID=B.EID AND T.PLUNO=B.PLUNO ");
		sqlBuffer.append(" LEFT JOIN DCP_GOODS_LANG C ON T.EID=C.EID AND T.PLUNO=C.PLUNO  AND C.LANG_TYPE='"+langType+"' ");
		sqlBuffer.append(" LEFT JOIN DCP_GOODS_LANG D ON B.EID=D.EID AND B.DPLUNO=D.PLUNO  AND D.LANG_TYPE='"+langType+"' ");
		sqlBuffer.append(" ORDER BY T.RN ");
		
		
		String sql = sqlBuffer.toString();
		return sql;
	}
	
	private String getString(List<DCP_PackageGoodsQuery_OpenReq.Good> goodsList) {
        StringBuffer str2 = new StringBuffer();
        if (goodsList!=null && goodsList.size()>0)
        {
            for (DCP_PackageGoodsQuery_OpenReq.Good good:goodsList) {
            	if(good.getPackagePluNo()!=null&&good.getPackagePluNo().length()>0){
            		str2.append("'").append(good.getPackagePluNo()).append("'").append(",");
            	}
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

}
