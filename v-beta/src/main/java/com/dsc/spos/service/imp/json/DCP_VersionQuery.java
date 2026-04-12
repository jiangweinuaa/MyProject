package com.dsc.spos.service.imp.json;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_VersionQueryReq;
import com.dsc.spos.json.cust.res.DCP_VersionQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_VersionQuery extends SPosAdvanceService<DCP_VersionQueryReq, DCP_VersionQueryRes>
{

	@Override
	protected void processDUID(DCP_VersionQueryReq req, DCP_VersionQueryRes res) throws Exception 
	{
		
		try 
		{
			
			FileInputStream fis = new FileInputStream(this.getClass().getResource("/com/dsc/spos/version/version.txt").getPath());   
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");   
			
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(isr);   
			String line = null;   			
			
			String content="";
			
			while ((line = br.readLine()) != null) 
			{   
				content+=line + "\r\n";				
			}  
			
			
			res.setDatas(new ArrayList<DCP_VersionQueryRes.levelversion>());
			
			String[] splitVersion=content.split("version=");
			
			for (int i = 0; i < splitVersion.length; i++) 
			{
				if(splitVersion[i].length()>1)//UTF-8 BOM标记65279
				{					
					String[] splitUpdateDate=splitVersion[i].split("updateDate=");
					
					if (splitUpdateDate.length>1) 
					{
						//
						DCP_VersionQueryRes.levelversion lv=res.new levelversion();
					
						//
						lv.setVersion(splitUpdateDate[0].replaceAll("\r|\n", ""));
						lv.setUpdateDate("");
						lv.setDatas(new ArrayList<DCP_VersionQueryRes.levelmemo>());
						
						String[] splitMemo=splitUpdateDate[1].split("memo=");
						
						if (splitMemo.length>1) 
						{							
                            //
							lv.setUpdateDate(splitMemo[0].replaceAll("\\r|\\n", ""));
							
							String[] splitLine=splitMemo[1].split("\\r\\n");
									
							for (int j = 0; j < splitLine.length; j++) 
							{								
								if(splitLine[j].length()>1)//UTF-8 BOM标记65279
								{
									DCP_VersionQueryRes.levelmemo lm=res.new levelmemo();
									lm.setMemo(splitLine[j]);
									lv.getDatas().add(lm);
									lm=null;
								}	
								
							}
						}			
						
						res.getDatas().add(lv);
						lv=null;
						
					}
					
				}				
				
			}
			
		} 
		catch (IOException e) 
		{

		}		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_VersionQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_VersionQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_VersionQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_VersionQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_VersionQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_VersionQueryReq>(){};
	}

	@Override
	protected DCP_VersionQueryRes getResponseType() 
	{
		return new DCP_VersionQueryRes();
	}

	
}
