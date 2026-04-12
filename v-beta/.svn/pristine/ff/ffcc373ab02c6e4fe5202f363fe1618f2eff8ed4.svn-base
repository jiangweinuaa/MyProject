package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsUpdateReq.level1Attr;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsUpdateReq.level1Spec;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderGoodsUpdate
 * 服务说明：外卖商品修改
 * @author jinzma	 
 * @since  2019-03-12
 */
public class DCP_OrderGoodsUpdate extends SPosAdvanceService<DCP_OrderGoodsUpdateReq,DCP_OrderGoodsUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderGoodsUpdateReq req, DCP_OrderGoodsUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String pluNO = req.getPluNO();
		String pluName = req.getPluName();
		String categoryNO=req.getCategoryNO();
		String des=req.getDescription();
		String fileName=req.getFileName();
		String unit=req.getUnit();
		String priority=req.getPriority();
		String materialID1=req.getMaterialID1();
		String materialID2=req.getMaterialID2();
		String materialID3=req.getMaterialID3();
		String materialID4=req.getMaterialID4();
		String materialID5=req.getMaterialID5();
		String materialID6=req.getMaterialID6();
		String materialID7=req.getMaterialID7();
		String materialID8=req.getMaterialID8();
		String materialID9=req.getMaterialID9();
		String materialID10=req.getMaterialID10();
		String material1=req.getMaterial1();
		String material2=req.getMaterial2();
		String material3=req.getMaterial3();
		String material4=req.getMaterial4();
		String material5=req.getMaterial5();
		String material6=req.getMaterial6();
		String material7=req.getMaterial7();
		String material8=req.getMaterial8();
		String material9=req.getMaterial9();
		String material10=req.getMaterial10();
		String isAllTimeSell= req.getIsAllTimeSell();
		String beginDate=req.getBeginDate();
		String endDate=req.getEndDate();
		String sellWeek=req.getSellWeek();
		String sellTime=req.getSellTime();
		if (isAllTimeSell.equals("Y"))
		{
			beginDate="";
			endDate="";
			sellWeek="";
			sellTime="";
		}

		String memo = req.getMemo();
		String belFirm = req.getOrganizationNO();
		if(belFirm==null)
		{
			belFirm =" ";//因为主键 不能为空
		}
		
		String status = req.getStatus();
		if(status==null)
		{
			status ="100";//
		}

		List<Map<String, Object>> getQData ;
		try 
		{
			String sql = null;
			sql = " select FILENAME  from OC_goods  where EID= ? and PLUNO = ?  ";
			String[] conditionValues = {eId,pluNO}; //查詢條件
			getQData = this.doQueryData(sql, conditionValues);

			if (getQData != null && getQData.isEmpty() == false) 
			{				
				//删除单身 OC_GOODS SPEC
				DelBean db_spec = new DelBean("OC_GOODS_SPEC");
				db_spec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db_spec.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db_spec.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(db_spec));

				//删除单身OC_GOODS_ATTR
				DelBean db_attr = new DelBean("OC_GOODS_ATTR");
				db_attr.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db_attr.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db_attr.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(db_attr));

				//新增单身 OC_GOODS SPEC
				DataValue[] insValue = null;
				List<level1Spec> specdatas = req.getSpecDatas();
				for (level1Spec par : specdatas) {
					String[] columns_detail = {
							"EID", "PLUNO","SPECNO","SPECNAME",
							"PRICE", "STOCKQTY","PACKAGEFEE","ISONSHELF","NETWEIGHT","BELFIRM","STATUS" };

					String specNO= par.getSpecNO();
					String specName= par.getSpecName();
					String price = par.getPrice();
					String stockQty=par.getStockQty();
					String packageFee = par.getPackageFee();
					String isOnshelf=par.getIsOnshelf();
					String netWeight=par.getNetWeight();
					if (Check.Null(netWeight)) netWeight="0";
					
					
					

					//判断条码是否重复
					if (checkspecExist(eId,pluNO,specNO,belFirm))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "规格编号重复,");
					}

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(pluNO, Types.VARCHAR), 
							new DataValue(specNO, Types.VARCHAR),
							new DataValue(specName, Types.VARCHAR),								
							new DataValue(price, Types.VARCHAR),
							new DataValue(stockQty, Types.VARCHAR),
							new DataValue(packageFee, Types.VARCHAR),
							new DataValue(isOnshelf, Types.VARCHAR),
							new DataValue(netWeight, Types.VARCHAR),
							new DataValue(belFirm, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib_specdetail = new InsBean("OC_GOODS_SPEC", columns_detail);
					ib_specdetail.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib_specdetail)); 	
				}


				//新增单身OC_GOODS_ATTR
				List<level1Attr> attrdatas = req.getAttrDatas();
				if (attrdatas!=null && attrdatas.isEmpty()==false)
				{
					for (level1Attr par : attrdatas) {
						String[] columns_detail = {
								"EID","PLUNO","ATTRNAME",
								"ATTRVALUE","BELFIRM","STATUS" };					
						String attrName= par.getAttrName();
						String attrValue = par.getAttrValue();

						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(pluNO, Types.VARCHAR), 
								new DataValue(attrName, Types.VARCHAR),								
								new DataValue(attrValue, Types.VARCHAR),
								new DataValue(belFirm, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR)
						};
						InsBean ib_attrdetail = new InsBean("OC_GOODS_ATTR", columns_detail);
						ib_attrdetail.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib_attrdetail)); 	
					}
				}

				//更新单头OC_GOODS				
				UptBean ub = null;	
				ub = new UptBean("OC_GOODS");
				ub.addUpdateValue("CATEGORYNO", new DataValue(categoryNO, Types.VARCHAR));
				ub.addUpdateValue("PLUNAME", new DataValue(pluName, Types.VARCHAR));
				ub.addUpdateValue("DESCRIPTION", new DataValue(des, Types.VARCHAR));
				ub.addUpdateValue("FILENAME", new DataValue(fileName, Types.VARCHAR));
				ub.addUpdateValue("UNIT", new DataValue(unit, Types.VARCHAR));
				ub.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));					
				ub.addUpdateValue("MATERIALID1", new DataValue(materialID1, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID2", new DataValue(materialID2, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID3", new DataValue(materialID3, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID4", new DataValue(materialID4, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID5", new DataValue(materialID5, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID6", new DataValue(materialID6, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID7", new DataValue(materialID7, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID8", new DataValue(materialID8, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID9", new DataValue(materialID9, Types.VARCHAR));
				ub.addUpdateValue("MATERIALID10", new DataValue(materialID10, Types.VARCHAR));		
				ub.addUpdateValue("MATERIAL1", new DataValue(material1, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL2", new DataValue(material2, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL3", new DataValue(material3, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL4", new DataValue(material4, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL5", new DataValue(material5, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL6", new DataValue(material6, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL7", new DataValue(material7, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL8", new DataValue(material8, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL9", new DataValue(material9, Types.VARCHAR));
				ub.addUpdateValue("MATERIAL10", new DataValue(material10, Types.VARCHAR));

				ub.addUpdateValue("ISALLTIMESELL", new DataValue(isAllTimeSell, Types.VARCHAR));
				ub.addUpdateValue("BEGINDATE", new DataValue(beginDate, Types.VARCHAR));
				ub.addUpdateValue("ENDDATE", new DataValue(endDate, Types.VARCHAR));
				ub.addUpdateValue("SELLWEEK", new DataValue(sellWeek, Types.VARCHAR));
				ub.addUpdateValue("SELLTIME", new DataValue(sellTime, Types.VARCHAR));
				ub.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
				
				ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				
				ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			  //fileData数据
				String base64=req.getFileData();
				if (Check.Null(base64)==false)//更新了图片，把之前的图片hash值清空
				{
					ub.addUpdateValue("ELMHASH", new DataValue("", Types.VARCHAR));
					ub.addUpdateValue("JBPHASH", new DataValue("", Types.VARCHAR));
				}
				
				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					ub.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(ub));
				this.doExecuteDataToDB();
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在，请重新输入！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

		try
		{				
			String dirpath= System.getProperty("catalina.home")+"\\webapps\\ordergoods";
			//fileData数据
			String base64=req.getFileData();
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

				//如果文件夹不存在则创建  \\webapps\\ordergoods 
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
				String imgFilePath = dirpath +"\\" +req.getFileName();
				OutputStream out = new FileOutputStream(imgFilePath);      
				out.write(b);  
				out.flush();  
				out.close();  

			}	 			
			else
			{
				if (Check.Null(fileName))
				{
					//如果服务器上存在此图片删除
					File file =new File(dirpath +"\\" + fileName);    
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
	protected List<InsBean> prepareInsertData(DCP_OrderGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Spec> specdatas = req.getSpecDatas();

		if (Check.Null(req.getPluNO()) ) 
		{
			errMsg.append("商品编号不可为空值, ");
			isFail = true;
		}		
		if (Check.Null(req.getPluName()) ) 
		{
			errMsg.append("商品名称不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getCategoryNO()) ) 
		{
			errMsg.append("分类编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getUnit()) ) 
		{
			errMsg.append("商品单位不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getPriority()) ) 
		{
			errMsg.append("优先级不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(req.getPriority()))
			{
				errMsg.append("优先级必须为数值, ");
				isFail = true;
			}
		}

		if (Check.Null(req.getMaterial1())|| Check.Null(req.getMaterialID1())) 
		{
			errMsg.append("原料不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getIsAllTimeSell())) 
		{
			errMsg.append("是否全时段售卖不可为空值, ");
			isFail = true;
		}
		else 
		{
			if (req.getIsAllTimeSell().equals("N")) 
			{				
				if (Check.Null(req.getBeginDate())) 
				{
					errMsg.append("售卖开始日期不可为空值, ");
					isFail = true;
				}
				if (Check.Null(req.getEndDate())) 
				{
					errMsg.append("售卖结束日期不可为空值, ");
					isFail = true;
				}
				if (Check.Null(req.getSellWeek())) 
				{
					errMsg.append("可售星期列表不可为空值, ");
					isFail = true;
				}
				if (Check.Null(req.getSellTime())) 
				{
					errMsg.append("可售时间不可为空值, ");
					isFail = true;
				}				
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1Spec par : specdatas) 
		{	
			if (Check.Null(par.getSpecNO())) 
			{
				errMsg.append("规格编号不可为空值, ");
				isFail = true;
			}	

			if (Check.Null(par.getSpecName())) 
			{
				errMsg.append("规格名称不可为空值, ");
				isFail = true;
			}

			if (Check.Null(par.getPrice())) 
			{
				errMsg.append("规格价格不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumericType(par.getPrice()))
				{
					errMsg.append("规格价格必须为数值, ");
					isFail = true;
				}
				else 
				{
					if (Float.valueOf(par.getPrice())==0 || Float.valueOf(par.getPrice())<=0  )
					{
						errMsg.append("规格价格必须大于零, ");
						isFail = true;
					}
				}
			}		

			if (Check.Null(par.getStockQty())) 
			{
				errMsg.append("库存数量不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(par.getStockQty()))
				{
					errMsg.append("库存数量必须为数值, ");
					isFail = true;
				}
			}				
			if (Check.Null(par.getPackageFee())) 
			{
				errMsg.append("包装费不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumericType(par.getPackageFee()))
				{
					errMsg.append("包装费必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getIsOnshelf())) 
			{
				errMsg.append("上架否不可为空值, ");
				isFail = true;
			}
			
			if (Check.Null(par.getNetWeight())) 
			{
				errMsg.append("商品净重不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(par.getNetWeight()))
				{
					errMsg.append("商品净重（单位克）必须为整数, ");
					isFail = true;
				}
			}
			

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	

		}		

		return isFail;		


	}

	@Override
	protected TypeToken<DCP_OrderGoodsUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderGoodsUpdateReq>(){};
	}

	@Override
	protected DCP_OrderGoodsUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderGoodsUpdateRes();
	}

	private boolean checkspecExist(String eId,String pluNO,String specNO,String belFirm )  throws Exception {

		String sql = null;
		boolean exist = false;		
		String[] conditionValues = { eId,pluNO,specNO }; 				
		sql = " select * from OC_GOODS_SPEC  where EID=? and pluno<>? and SPECNO=?  " ;
		if (belFirm != null && belFirm.trim().length() > 0)
		{
			sql +=" and BELFIRM='"+belFirm+"'";
			
		}
		
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
