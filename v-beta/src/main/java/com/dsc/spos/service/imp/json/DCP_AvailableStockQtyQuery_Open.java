package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_AvailableStockQtyQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_AvailableStockQtyQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_AvailableStockQtyQuery_Open extends SPosBasicService<DCP_AvailableStockQtyQuery_OpenReq, DCP_AvailableStockQtyQuery_OpenRes>
{

	@Override
	protected boolean isVerifyFail(DCP_AvailableStockQtyQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		if(req.getRequest()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
		}
		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_AvailableStockQtyQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_AvailableStockQtyQuery_OpenReq>(){};
	}

	@Override
	protected DCP_AvailableStockQtyQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_AvailableStockQtyQuery_OpenRes();
	}

	@Override
	protected DCP_AvailableStockQtyQuery_OpenRes processJson(DCP_AvailableStockQtyQuery_OpenReq req) throws Exception
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
		
		
		
		
		DCP_AvailableStockQtyQuery_OpenRes res = this.getResponse();
		
		DCP_AvailableStockQtyQuery_OpenRes.levelDatas datas = res.new levelDatas();
		
		datas.setPluList(new ArrayList<DCP_AvailableStockQtyQuery_OpenRes.level1Elm>());
		
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
		
		//是否需要分页
		boolean isPage = true;
		String[] pluNoList  = req.getRequest().getPluList();
		if(pluNoList!=null&&pluNoList.length>0)
		{
			isPage = false;
		}
		
		
		String sql = getQuerySql(req);
		HelpTools.writelog_fileName("有赞库存查询sql:"+sql+"\r\n", "DCP_AvailableStockQtyQuery_Open");
		
		
        
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
            
            //如果不分页返回pageSize=总数
           /* if(!isPage)
            {
            	req.setPageSize(totalRecords);	
            }*/
            
            //pluNo
            Map<String, Boolean> getCondition = new HashMap<String, Boolean>(); //查詢條件
            getCondition.put("ORGANIZATIONNO", true); 
            getCondition.put("WAREHOUSE", true); 
            getCondition.put("PLUNO", true); 
            getCondition.put("FEATURENO", true);
            //调用过滤函数
            List<Map<String, Object>> getPluNoQHeader=MapDistinct.getMap(getQData,getCondition);
                         
            //Barcode                    
            getCondition.put("PLUBARCODE", true);
            //调用过滤函数
            List<Map<String, Object>> getBarcodeQHeader=MapDistinct.getMap(getQData,getCondition);
                                                                            
            for (Map<String, Object> map : getPluNoQHeader)
			{
            	DCP_AvailableStockQtyQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();           	
            	oneLv1.setPluBarcodeList(new ArrayList<DCP_AvailableStockQtyQuery_OpenRes.level2Barcode>()); 
            	
            	String organizationNo = map.getOrDefault("ORGANIZATIONNO", "").toString();
            	String warehouse = map.getOrDefault("WAREHOUSE", "").toString();
            	String pluNo = map.getOrDefault("PLUNO", "").toString();
            	String featureNo = map.getOrDefault("FEATURENO", "").toString();
            	String pluName = map.getOrDefault("PLU_NAME", "").toString();
            	String baseUnit = map.getOrDefault("BASEUNIT", "").toString();//ALL
            	String baseUnitName = map.getOrDefault("BASEUNITNAME", "").toString();//
            	String availableQty = map.getOrDefault("BONLINEQTY", "0").toString();//可用数量（渠道库存表：预留数；库存表：在库数-锁定数-预留数）
            	
            	oneLv1.setPluNo(pluNo);            	
            	oneLv1.setAvailableQty(availableQty);
            	oneLv1.setBaseUnit(baseUnit);
            	oneLv1.setBaseUnitName(baseUnitName);
            	oneLv1.setFeatureNo(featureNo);
            	oneLv1.setOrganizationNo(organizationNo);
            	oneLv1.setPluName(pluName);
            	oneLv1.setWarehouse(warehouse);
           	   	
            	for (Map<String, Object> mapBarcode : getBarcodeQHeader)
				{
            		String pluNo_Barcode = mapBarcode.getOrDefault("PLUNO", "").toString();
            		String pluBarcode = mapBarcode.getOrDefault("PLUBARCODE", "").toString();
            		String organizationNo_Barcode = mapBarcode.getOrDefault("ORGANIZATIONNO", "").toString();
                	String warehouse_Barcode = mapBarcode.getOrDefault("WAREHOUSE", "").toString();
                	String featureNo_Barcode = mapBarcode.getOrDefault("FEATURENO", "").toString();
            		if(pluNo_Barcode.isEmpty()||pluBarcode.isEmpty()||organizationNo_Barcode.isEmpty()||warehouse_Barcode.isEmpty()||featureNo_Barcode.isEmpty())
            		{
            			continue;
            		}
            		if(!organizationNo_Barcode.equals(organizationNo))
            		{
            			continue;
            		}
            		if(!warehouse_Barcode.equals(warehouse))
            		{
            			continue;
            		}
            		if(!featureNo_Barcode.equals(featureNo))
            		{
            			continue;
            		}
            		if(!pluNo_Barcode.equals(pluNo))
            		{
            			continue;
            		}
            		
            		DCP_AvailableStockQtyQuery_OpenRes.level2Barcode oneLv2 = res.new level2Barcode();
            		String unit = mapBarcode.getOrDefault("UNIT", "").toString();
            		String unitRatio = mapBarcode.getOrDefault("UNITRATIO", "1").toString();           		
            		double availableQty_barcode_d = 0;
            		if(unit.equals(baseUnit))
            		{
            			oneLv2.setAvailableQty(availableQty);
            		}
            		else
            		{
            			try
            			{
					
            				availableQty_barcode_d = new BigDecimal(unitRatio).multiply(new BigDecimal(availableQty)).setScale(4, RoundingMode.HALF_UP).doubleValue();
							
						} catch (Exception e)
						{
							// TODO: handle exception
						}
            			
            			oneLv2.setAvailableQty(availableQty_barcode_d+"");           			
            		}
            		
            		
            		oneLv2.setPluBarcode(pluBarcode);
            		oneLv2.setUnit(unit);
            		oneLv2.setUnitName(mapBarcode.getOrDefault("UNAME", "").toString());
            		oneLv2.setFeatureNo(mapBarcode.getOrDefault("FEATURENO", "").toString());
            		oneLv2.setFeatureName(mapBarcode.getOrDefault("FEATURENAME", "").toString());
            		
            		
            		
            		oneLv1.getPluBarcodeList().add(oneLv2);
            		oneLv2 = null;
            		
				}
            	
            	
            	
            	datas.getPluList().add(oneLv1);
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
	protected String getQuerySql(DCP_AvailableStockQtyQuery_OpenReq req) throws Exception
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
		boolean isPage = true;
		String[] pluNo  = req.getRequest().getPluList();
		
		 //String keyTxt = req.getRequest().getKeyTxt();
		 String pluNos = getString(pluNo);
		 String pluBarcodes = getString(req.getRequest().getPluBarcodeList());
		 //String pluBarcodes =getString(pluBarcode);
		 
		int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; 
        
        int endRow = startRow + pageSize;
		
		StringBuffer sqlBuffer = new StringBuffer("");
				
		sqlBuffer.append(" with p as ( ");
		
		sqlBuffer.append(" select * from ( ");
		sqlBuffer.append(" select count(*) over() num,rownum rn,EID, ORGANIZATIONNO,PLUNO,FEATURENO,WAREHOUSE,BASEUNIT,BONLINEQTY from (");
		
		sqlBuffer.append(" select * from (");
		sqlBuffer.append(" select EID, ORGANIZATIONNO,PLUNO,FEATURENO,WAREHOUSE,BASEUNIT,BONLINEQTY,row_number() over(partition by ORGANIZATIONNO,WAREHOUSE,PLUNO,FEATURENO order by ITEM) IDX "
				+ " from (");
		
		//DCP_STOCK_CHANNEL是否存在，不存在取DCP_STOCK
		sqlBuffer.append(" select 1 AS ITEM, A.EID, A.ORGANIZATIONNO,A.PLUNO,A.FEATURENO,A.WAREHOUSE,A.BASEUNIT,A.BONLINEQTY "
				+ " from DCP_STOCK_CHANNEL a "
				+ " inner join dcp_org b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.out_cost_warehouse"
				+ " inner join dcp_goods c on a.eid=c.eid and a.PLUNO=c.PLUNO and c.status='100'" );
		
		if(!Check.Null(pluBarcodes)){
			sqlBuffer.append(" inner join (select * from dcp_goods_barcode where eid='"+eId+"' and plubarcode in("+pluBarcodes+")"
					+ ") bar on a.pluno=bar.pluno and a.eid=bar.eid");
		}
		sqlBuffer.append(""
				+ " where a.eid='"+eId+"' and a.CHANNELID='"+channelId+"' ");
		if(!Check.Null(pluNos))
		{
			isPage = false;
			sqlBuffer.append(" and a.pluno in ("+pluNos+")");
		}
		
		sqlBuffer.append(" UNION ALL ");
		
		//DCP_STOCK_CHANNEL是否存在，不存在取DCP_STOCK	
		sqlBuffer.append(" select 2 AS ITEM, A.EID, A.ORGANIZATIONNO,A.PLUNO,A.FEATURENO,A.WAREHOUSE,A.BASEUNIT,(A.QTY-A.LOCKQTY-A.ONLINEQTY) BONLINEQTY "
				+ " from DCP_STOCK a "
				+ " inner join dcp_org b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.out_cost_warehouse "
				+ " inner join dcp_goods c on a.eid=c.eid and a.PLUNO=c.PLUNO and c.status='100' ");
		if(!Check.Null(pluBarcodes)){
			sqlBuffer.append(" inner join (select * from dcp_goods_barcode where eid='"+eId+"' and plubarcode in("+pluBarcodes+")"
					+ ") bar on a.pluno=bar.pluno and a.eid=bar.eid");
		}
		sqlBuffer.append(" where a.eid='"+eId+"' ");
		if(!Check.Null(pluNos))
		{
			isPage = false;
			sqlBuffer.append(" and a.pluno in ("+pluNos+")");
		}		
		sqlBuffer.append(" )");
		
		sqlBuffer.append(" ) where IDX=1 order by pluno,organizationno");
		
		sqlBuffer.append(" ) ");		
		sqlBuffer.append(" ) ");
		if(isPage)
		{
			sqlBuffer.append(" where rn>"+startRow+" and rn<="+endRow);
		}
				
		sqlBuffer.append(" )");
		
		sqlBuffer.append(" select P.*,B.PLUBARCODE,B.UNIT,U.UNITRATIO,UL.UNAME,ULB.UNAME AS BASEUNITNAME,GL.PLU_NAME,FL.FEATURENAME");
		sqlBuffer.append(" from p ");
		sqlBuffer.append(" left join dcp_goods_barcode b on p.eid=b.eid and p.pluno =b.pluno and p.featureno=b.featureno ");
		if(!Check.Null(pluBarcodes)){
			sqlBuffer.append(" and b.plubarcode in("+pluBarcodes+")");
		}
		sqlBuffer.append(" left join dcp_goods_unit u on u.eid=b.eid and u.pluno = b.pluno and u.ounit=b.unit ");
		sqlBuffer.append(" left join dcp_unit_lang ul on ul.eid=b.eid and ul.unit = b.unit and ul.lang_type='"+langType+"' ");
		sqlBuffer.append(" left join dcp_unit_lang ulb on ulb.eid=p.eid and ulb.unit = p.baseunit and ulb.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_goods_lang gl on gl.eid=p.eid and gl.pluno = p.pluno and gl.lang_type='"+langType+"' ");
		sqlBuffer.append(" left join dcp_goods_feature_lang fl on fl.eid=b.eid and fl.pluno = b.pluno and fl.featureno = b.featureno and fl.lang_type='"+langType+"' ");
		
		
		
		String sql = sqlBuffer.toString();
		return sql;
	}
	
	private String getString(String[] str) {
        StringBuffer str2 = new StringBuffer();
        if (str!=null && str.length>0)
        {
            for (String s:str) {
                str2.append("'").append(s).append("'").append(",");
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

}
