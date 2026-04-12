package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlashviewUpdateReq;
import com.dsc.spos.json.cust.req.DCP_FlashviewUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_FlashviewUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 轮播广告
 * @author yuanyy
 *
 */
public class DCP_FlashviewUpdate extends SPosAdvanceService<DCP_FlashviewUpdateReq, DCP_FlashviewUpdateRes> {

	@Override
	protected void processDUID(DCP_FlashviewUpdateReq req, DCP_FlashviewUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String flashviewId = req.getRequest().getFlashviewId();
			String fileName = req.getRequest().getFileName();
			String linkUrl = req.getRequest().getLinkUrl();
			String shopType = req.getRequest().getShopType();
			String status = req.getRequest().getStatus();
			String priority = req.getRequest().getPriority();
			String toPriority = req.getRequest().getToPriority();
			
			int priorityInt = Integer.parseInt(priority);
			int toPriorityInt = Integer.parseInt(toPriority);
			
			String eId = req.geteId();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime =  matter.format(dt);
			
			if(toPriorityInt == priorityInt){ //如果当前行优先级和目标行优先级一样， 是编辑； 不一样就是上下移动
				
				////********************************
				try 
				{		
					String dirpath= System.getProperty("catalina.home")+"\\webapps\\flashviewImg";

					//fileData数据
					String base64=req.getRequest().getFileData();
					//有附件再保存
					if (Check.Null(base64)==false) 
					{					
						base64=base64.substring(base64.indexOf("base64,")+7);
						//http://blog.csdn.net/jsjwbxzy/article/details/45970231
						//http请求中传输base64出现加号变空格的解决办法
						base64=base64.replace(" ", "+");//替换空格/*TMD接收进来有空格，发现本来应该是+号的*/
						//根据RFC822规定，BASE64Encoder编码每76个字符，还需要加上一个回车换行
						//http://blog.csdn.net/u010953266/article/details/52590570
						int iLEN=base64.length()/76+1;
						String sNewBase64="";
						for (int i = 0; i < iLEN; i++) 
						{
							if (i*76+76>base64.length()) 
							{
								sNewBase64=sNewBase64+base64.substring(i*76, base64.length());
							}
							else
							{
								sNewBase64=sNewBase64+base64.substring(i*76, i*76+76) +"\r\n";					
							}				
						}	



						//Base64解码  
						byte[] b = Base64.decodeBase64(base64);				

						File file =new File(dirpath);    

						//如果文件夹不存在则创建   \\webapps\\goodsimages 
						if(!file.exists()&& !file.isDirectory())      
						{
							boolean b1=file.mkdir();
							if(b1==false)
							{
								////System.out.println("webapps已经存在或失败");
							}			
						}

						file=null;

						//生成文件
						String imgFilePath = dirpath +"\\" +req.getRequest().getFileName();
						OutputStream out = new FileOutputStream(imgFilePath);      
						out.write(b);  
						out.flush();  
						out.close();  

					}	 			
					else
					{
						//如果服务器上存在此图片删除
						File file =new File(dirpath +"\\" + req.getRequest().getFileName());    
						if(file.exists())
						{
							file.delete();
						}				
						file=null;

					}
				} 
				catch (Exception ex) 
				{		

				}	
				
				List<level2Elm> shops = req.getRequest().getShops();
				
				DelBean db1 = new DelBean("DCP_FLASHVIEW_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("FLASHVIEWID", new DataValue(flashviewId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				if(shops != null && shops.size() > 0){
					
					for (level2Elm lv2 : shops) {
						String syShopId = lv2.getShopId();
						
						if(Check.Null(syShopId)){
							continue;
						}
						
						String[] columns_hms ={"EID","FLASHVIEWID","SHOPID","CREATETIME","LASTMODITIME"};
						DataValue[] insValue_hms = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(flashviewId, Types.VARCHAR), 
							new DataValue(syShopId, Types.VARCHAR),
							new DataValue(createTime, Types.DATE),
							new DataValue(createTime, Types.DATE)
						};
						
						InsBean ib_hms = new InsBean("DCP_FLASHVIEW_SHOP", columns_hms);
						ib_hms.addValues(insValue_hms);
						this.addProcessData(new DataProcessBean(ib_hms));
						
					}
				}
				
				UptBean ub = new UptBean("DCP_FLASHVIEW");
				ub.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
				// condition
				ub.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.GreaterEQ));
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));
				
				UptBean ub2 = new UptBean("DCP_FLASHVIEW");
				ub2.addUpdateValue("FILENAME", new DataValue(fileName, Types.VARCHAR));
				ub2.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));
				ub2.addUpdateValue("LINKURL", new DataValue(linkUrl, Types.VARCHAR));
				ub2.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));
				ub2.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
				// condition
				ub2.addCondition("FLASHVIEWID", new DataValue(flashviewId, Types.VARCHAR));
				ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub2));
				
			}
			//********* 上 下 移 *********
			else  {  
				
				UptBean ub = new UptBean("DCP_FLASHVIEW");
				ub.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));
				
				ub.addCondition("PRIORITY", new DataValue(toPriority, Types.VARCHAR));
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				
				UptBean ub2 = new UptBean("DCP_FLASHVIEW");
				ub2.addUpdateValue("PRIORITY ", new DataValue(toPriority, Types.VARCHAR)); 
				
				ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub2.addCondition("FLASHVIEWID", new DataValue(flashviewId, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(ub));

				this.addProcessData(new DataProcessBean(ub2));
				
			}
			
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlashviewUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlashviewUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlashviewUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlashviewUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String flashviewId = req.getRequest().getFlashviewId();
		String prority = req.getRequest().getPriority();
		String toPrority = req.getRequest().getToPriority();

		if (Check.Null(flashviewId)) {
			errCt++;
			errMsg.append("广告编号不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(prority)) {
			errCt++;
			errMsg.append("优先级序号不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(toPrority)) {
			errCt++;
			errMsg.append("目标行优先级序号不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlashviewUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlashviewUpdateReq>(){};
	}

	@Override
	protected DCP_FlashviewUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FlashviewUpdateRes();
	}
	
}
