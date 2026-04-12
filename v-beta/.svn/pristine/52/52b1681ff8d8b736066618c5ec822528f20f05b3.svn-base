package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_DualPlayUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_DualPlayUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：StockTakeUpdate
 *    說明：库存盘点修改
 * 服务说明：库存盘点修改
 * @author panjing 
 * @since  2016-09-20
 */
public class DCP_DualPlayUpdate extends SPosAdvanceService<DCP_DualPlayUpdateReq, DCP_DualPlayUpdateRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_DualPlayUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String dualPlayID=req.getRequest().getDualPlayID();	
		String platformType = req.getRequest().getPlatformType();
		String fileName=req.getRequest().getDualPlayID();
		String shopType=req.getRequest().getShopType();
		String timeType=req.getRequest().getTimeType();
		String status=req.getRequest().getStatus();
		String pollTime = req.getRequest().getPollTime();
		List<level1Elm> jsonShops = req.getRequest().getShops();	
		List<level2Elm> jsonTimes = req.getRequest().getTimes();

		if (Check.Null(dualPlayID)) {
			errMsg.append("双屏播放ID不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(platformType)) {
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(fileName)) {
			errMsg.append("文件名不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(shopType)) {
			errMsg.append("门店类型不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(timeType)) {
			errMsg.append("时间类型不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(status)) {
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if (Check.Null(pollTime))
		{
			errMsg.append("轮询时间不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(pollTime))
			{
				errMsg.append("轮询时间必须为数值, ");
				isFail = true;
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		


		for (level1Elm par : jsonShops) {
			if (Check.Null(par.getShopId())) {
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			} 
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
		}

		for (level2Elm par : jsonTimes) {
			if (Check.Null(par.getItem())) {
				errMsg.append("项次不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.getLbDate()) || Check.Null(par.getLeDate()) ) {
				errMsg.append("日期不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.getLbTime()) || Check.Null(par.getLeTime()) ) {
				errMsg.append("时间不可为空值, ");
				isFail = true;
			}

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayUpdateReq> getRequestType() {
		return new TypeToken<DCP_DualPlayUpdateReq>(){};
	}

	@Override
	protected DCP_DualPlayUpdateRes getResponseType() {
		return new DCP_DualPlayUpdateRes();
	}

	@Override
	protected void processDUID(DCP_DualPlayUpdateReq req,DCP_DualPlayUpdateRes res) throws Exception {
		StringBuffer errMsg = new StringBuffer("");	   
		String eId = req.geteId();
		String dualPlayID = req.getRequest().getDualPlayID();
		String platformType = req.getRequest().getPlatformType();
		String fileName = req.getRequest().getFileName();
		String shopType = req.getRequest().getShopType();
		String timeType = req.getRequest().getTimeType();
		String status = req.getRequest().getStatus();
		String memo = req.getRequest().getMemo();
		String pollTime = req.getRequest().getPollTime();
		List<Map<String, Object>> getQData ;

		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {eId,dualPlayID}; //查詢條件
			getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {
				DataValue[] insValue = null;		
				//删除单身
				DelBean db1 = new DelBean("DCP_DUALPLAY_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				//删除单身
				DelBean db2 = new DelBean("DCP_DUALPLAY_TIME");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

				//新增生效门店
				List<level1Elm> jsonshops = req.getRequest().getShops();
				for (level1Elm par : jsonshops) {
					String[] columns2 = {
							"EID", "DUALPLAYID","SHOPID","STATUS" };
					String shopId= par.getShopId();

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(dualPlayID, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib2 = new InsBean("DCP_DUALPLAY_SHOP", columns2);
					ib2.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib2)); //			
				}

				//新增生效时间
				List<level2Elm> jsontimes = req.getRequest().getTimes();
				for (level2Elm par : jsontimes) {
					String[] columns3 = {
							"EID", "DUALPLAYID","ITEM","LBDATE","LEDATE","LBTIME","LETIME","STATUS" };
					String item= par.getItem();
					String lbDate= par.getLbDate();
					String leDate= par.getLeDate();
					String lbTime= par.getLbTime();
					String leTime= par.getLeTime();

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(dualPlayID, Types.VARCHAR), 
							new DataValue(item, Types.INTEGER), 
							new DataValue(lbDate, Types.VARCHAR), 
							new DataValue(leDate, Types.VARCHAR), 
							new DataValue(lbTime, Types.VARCHAR), 
							new DataValue(leTime, Types.VARCHAR), 
							new DataValue("100", Types.VARCHAR)
					};

					InsBean ib3 = new InsBean("DCP_DUALPLAY_TIME", columns3);
					ib3.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib3)); //			
				}

				//更新DCP_DUALPLAY
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_DUALPLAY");
				ub1.addUpdateValue("PLATFORMTYPE", new DataValue(platformType, Types.VARCHAR));
				ub1.addUpdateValue("FILENAME", new DataValue(fileName, Types.VARCHAR));
				ub1.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));
				ub1.addUpdateValue("TIMETYPE", new DataValue(timeType, Types.VARCHAR));
				ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
				ub1.addUpdateValue("POLLTIME", new DataValue(pollTime, Types.VARCHAR));
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));


				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

			}
			else
			{
				errMsg.append("双屏播放记录不存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}


		try
		{		
			String dirpath= System.getProperty("catalina.home")+"\\webapps\\dualplay";
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
				@SuppressWarnings("restriction")
				byte[] b = Base64.decodeBase64(base64);		
				File file =new File(dirpath);    

				//如果文件夹不存在则创建  \\webapps\\dualplay 
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
				if (Check.Null(getQData.get(0).get("FILENAME").toString()))
				{
					//如果服务器上存在此图片删除
					File file =new File(dirpath +"\\" + getQData.get(0).get("FILENAME").toString());    
					if(file.exists())
					{
						file.delete();
					}				
					file=null;
				}
			}
		}
		catch (Exception ex) 
		{		

		}	
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");


	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected String getQuerySql(DCP_DualPlayUpdateReq req) throws Exception {
		String sql = null;
		sql= " select FILENAME  from DCP_DUALPLAY  where EID= ? and dualplayid = ?  ";
		return sql;
	}

}
