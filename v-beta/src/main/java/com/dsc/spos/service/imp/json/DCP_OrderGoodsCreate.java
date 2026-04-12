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
import com.dsc.spos.json.cust.req.DCP_OrderGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsCreateReq.level1Attr;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsCreateReq.level1Spec;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderGoodsCreate
 * 服务说明：外卖商品新增
 * @author jinzma	 
 * @since  2019-03-12
 */
public class DCP_OrderGoodsCreate extends SPosAdvanceService<DCP_OrderGoodsCreateReq,DCP_OrderGoodsCreateRes> {

	@Override
	protected void processDUID(DCP_OrderGoodsCreateReq req, DCP_OrderGoodsCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String pluNO = req.getPluNO();
		String pluName = req.getPluName();
		String categoryNO=req.getCategoryNO();
		String des=req.getDescription();
		String fileName=req.getFileName();
		//String fileName=pluNO;
		String unit=req.getUnit();
		String priority=req.getPriority();
		String belFirm = req.getOrganizationNO();
		if(belFirm==null)
		{
			belFirm =" ";//因为主键 不能为空
		}

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

		String memo=req.getMemo();

		try 
		{
			if (checkExist(req) == false)
			{
				DataValue[] insValue = null;
				//新增单身 OC_GOODS SPEC
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
					if (Check.Null(netWeight)) netWeight="0" ;

					//判断条码是否重复
					if (checkspecExist(eId,specNO,belFirm))
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

				//新增单头
				String[] columns = {
						"EID", "PLUNO","CATEGORYNO","PLUNAME",
						"DESCRIPTION", "FILENAME", "UNIT","PRIORITY",
						"MATERIALID1","MATERIALID2","MATERIALID3","MATERIALID4","MATERIALID5",
						"MATERIALID6","MATERIALID7","MATERIALID8","MATERIALID9","MATERIALID10",
						"MATERIAL1","MATERIAL2","MATERIAL3","MATERIAL4","MATERIAL5",
						"MATERIAL6","MATERIAL7","MATERIAL8","MATERIAL9","MATERIAL10",
						"ISALLTIMESELL","BEGINDATE","ENDDATE","SELLWEEK","SELLTIME","MEMO","BELFIRM","STATUS" };

				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(pluNO, Types.VARCHAR), 
						new DataValue(categoryNO, Types.VARCHAR),
						new DataValue(pluName, Types.VARCHAR),								
						new DataValue(des, Types.VARCHAR),
						new DataValue(fileName, Types.VARCHAR),
						new DataValue(unit, Types.VARCHAR),
						new DataValue(priority, Types.INTEGER),		
						new DataValue(materialID1, Types.VARCHAR),
						new DataValue(materialID2, Types.VARCHAR),
						new DataValue(materialID3, Types.VARCHAR),
						new DataValue(materialID4, Types.VARCHAR),
						new DataValue(materialID5, Types.VARCHAR),
						new DataValue(materialID6, Types.VARCHAR),
						new DataValue(materialID7, Types.VARCHAR),
						new DataValue(materialID8, Types.VARCHAR),
						new DataValue(materialID9, Types.VARCHAR),
						new DataValue(materialID10, Types.VARCHAR),

						new DataValue(material1, Types.VARCHAR),
						new DataValue(material2, Types.VARCHAR),
						new DataValue(material3, Types.VARCHAR),
						new DataValue(material4, Types.VARCHAR),
						new DataValue(material5, Types.VARCHAR),
						new DataValue(material6, Types.VARCHAR),
						new DataValue(material7, Types.VARCHAR),
						new DataValue(material8, Types.VARCHAR),
						new DataValue(material9, Types.VARCHAR),
						new DataValue(material10, Types.VARCHAR),
						new DataValue(isAllTimeSell, Types.VARCHAR),
						new DataValue(beginDate, Types.VARCHAR),
						new DataValue(endDate, Types.VARCHAR),
						new DataValue(sellWeek, Types.VARCHAR),
						new DataValue(sellTime, Types.VARCHAR),		
						new DataValue(memo, Types.VARCHAR),	
						new DataValue(belFirm, Types.VARCHAR),
						new DataValue("100", Types.VARCHAR)
				};
				InsBean ib = new InsBean("OC_GOODS", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 
				this.doExecuteDataToDB();
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, pluName+"("+pluNO+")该商品已经存在！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

		try 
		{
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
				//@SuppressWarnings("restriction")
				byte[] b = Base64.decodeBase64(base64);
				String dirpath= System.getProperty("catalina.home")+"\\webapps\\ordergoods";
				File file =new File(dirpath);    

				//如果文件夹不存在则创建   \\webapps\\dualplay 
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
		} 
		catch (Exception e) 
		{

		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");		

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderGoodsCreateReq req) throws Exception {
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
	protected TypeToken<DCP_OrderGoodsCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderGoodsCreateReq>(){};
	}

	@Override
	protected DCP_OrderGoodsCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderGoodsCreateRes();
	}

	private boolean checkExist(DCP_OrderGoodsCreateReq req)  throws Exception {

		boolean exist = false;
		String eId = req.geteId();
		String pluNO = req.getPluNO();
		String belFirm = req.getOrganizationNO();
		String[] conditionValues = { eId,pluNO }; 				
		StringBuffer sb =new StringBuffer( " select * from OC_goods where EID=?  and pluno=? " );
		if (belFirm != null && belFirm.length() > 0)
		{
			sb.append(" and BELFIRM = '"+belFirm+"'");
		}
		
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

	private boolean checkspecExist(String eId,String specNO,String belFirm )  throws Exception {

		boolean exist = false;		
		String[] conditionValues = { eId,specNO }; 				
		StringBuffer sb =new StringBuffer( " select * from OC_GOODS_SPEC  where EID=?  and SPECNO=?  " );
		if (belFirm != null && belFirm.trim().length() > 0)
		{
			sb.append(" and BELFIRM='"+belFirm+"'");
			
		}
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
