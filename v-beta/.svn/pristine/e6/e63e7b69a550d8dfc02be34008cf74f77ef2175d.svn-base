package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsStyleUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_GoodsStyleUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStyleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsStyleUpdate extends SPosAdvanceService<DCP_GoodsStyleUpdateReq, DCP_GoodsStyleUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsStyleUpdateReq req, DCP_GoodsStyleUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;

		try 
		{
			String eId = req.geteId();
			String styleNO = req.getRequest().getStyleNo();
			String styleName = req.getRequest().getStyleName();
			String fileName = req.getRequest().getFileName();
			String status = req.getRequest().getStatus();

			//先删除原有的明细信息
			DelBean db1 = new DelBean("DCP_STYLE_DETAIL");
			db1.addCondition("STYLENO", new DataValue(styleNO, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();

			//新增明细信息（多笔）
			List<level1Elm> datas = req.getRequest().getDatas();
			if(datas != null && !datas.isEmpty()){
				for(level1Elm oneLv1: datas){

					int insColCt = 0;
					String[] columnsName = {
							"STYLENO","PLUNO","SPEC_NO","FLAVORNO","STATUS","EID","PLUSHOWNAME"
					};
					DataValue[] columnsVal = new DataValue[columnsName.length];
					String pluNO = oneLv1.getPluNo();
					String pluShowName = oneLv1.getPluShowName();
					String specNO = oneLv1.getSpecNo();
					String flavorNO = oneLv1.getFlavorNo();
					String detailStatus = oneLv1.getStatus();

					sql = this.isRepeatDetail(styleNO, pluNO, specNO, flavorNO, eId);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);

					if(detailDatas.isEmpty()){
						for (int i = 0; i < columnsVal.length; i++) { 
							String keyVal = null;
							switch (i) { 
							case 0:
								keyVal = styleNO;
								break;
							case 1:
								keyVal = pluNO;
								break;
							case 2:
								keyVal = specNO;
								break;
							case 3:
								keyVal = flavorNO;
								break;
							case 4:  
								keyVal = detailStatus;
								break;
							case 5:  
								keyVal = eId;
								break;
							case 6:  
								keyVal = pluShowName;
								break;
							default:
								break;

							}
							if (keyVal != null) 
							{
								insColCt++;
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							} 
							else 
							{
								columnsVal[i] = null;
							}

						}

						String[] columnsDetail = new String[insColCt];
						DataValue[] insValueDetail = new DataValue[insColCt];

						insColCt = 0;

						for (int i = 0; i < columnsVal.length; i++) 
						{
							if (columnsVal[i] != null) 
							{
								columnsDetail[insColCt] = columnsName[i];
								insValueDetail[insColCt] = columnsVal[i];
								insColCt++;
								if (insColCt >= insValueDetail.length)
									break;
							}
						}

						InsBean ib2 = new InsBean("DCP_STYLE_DETAIL", columnsDetail);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));		
					}
					else{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"服务执行失败: 明细信息商品编码为  "+pluNO+", "
								+ "规格编码为"+specNO+", 口味编码为 "+flavorNO+" 的信息已存在");

					}
				}	
			}



			//********************************
			try 
			{		 
				String dirpath= System.getProperty("catalina.home")+"\\webapps\\goodsimages\\styleImage";

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

					//如果文件夹不存在则创建   \\webapps\\goodsimages\\styleImage 
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
					File file =new File(dirpath +"\\" + styleNO);    
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


			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_STYLE");
			//add Value
			ub1.addUpdateValue("STYLE_NAME", new DataValue(styleName, Types.VARCHAR));
			ub1.addUpdateValue("PICTURE_NAME", new DataValue(fileName, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			//condition
			ub1.addCondition("STYLENO", new DataValue(styleNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");


		} catch (Exception e) {

			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsStyleUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsStyleUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsStyleUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsStyleUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String styleNO = req.getRequest().getStyleNo();

		List<level1Elm> datas = req.getRequest().getDatas();

		if(Check.Null(styleNO)){
			errMsg.append("款式编码不能为空值 ");
			isFail = true;
		}
		if(datas != null && !datas.isEmpty()){
			for (level1Elm oneData : datas) 
			{
				String pluNO = oneData.getPluNo();
				String specNO = oneData.getSpecNo();
				String flavorNO = oneData.getFlavorNo();

				if(Check.Null(pluNO)){
					errMsg.append("商品编码不能为空值 ");
					isFail = true;
				}
				if(Check.Null(specNO)){
					errMsg.append("规格编码不能为空值 ");
					isFail = true;
				}
				if(Check.Null(flavorNO)){
					errMsg.append("口味编码不能为空值 ");
					isFail = true;
				}
			}

		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsStyleUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStyleUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsStyleUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStyleUpdateRes();
	}

	/**
	 * 验证明细信息是否重复
	 * @param styleNO
	 * @param pluNO
	 * @param specNO
	 * @param flavorNO
	 * @param eId
	 * @return
	 */
	private String isRepeatDetail(String styleNO, String pluNO, String specNO, String flavorNO , String eId ){
		String sql = null;
		sql = "SELECT * FROM DCP_STYLE_DETAIL WHERE "
				+ " STYLENO = '"+styleNO+"' "
				+ " AND SPEC_NO = '"+specNO+"'" 
				+ " AND FLAVORNO = '"+flavorNO+"'" 
				+ " AND EID = '"+eId+"'" ;
		return sql;
	}

}
