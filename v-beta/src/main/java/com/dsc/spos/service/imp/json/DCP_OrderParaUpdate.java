package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.foreign.erp.Request;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderParaUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderParaUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderParaUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.google.gson.reflect.TypeToken;

/**
 * 门店订单参数设置
 *
 */
public class DCP_OrderParaUpdate extends SPosAdvanceService<DCP_OrderParaUpdateReq, DCP_OrderParaUpdateRes> {

	Logger logger = LogManager.getLogger(DCP_OrderParaUpdate.class.getName());

	@Override
	protected void processDUID(DCP_OrderParaUpdateReq req, DCP_OrderParaUpdateRes res) throws Exception
	{
		// TODO Auto-generated method stub
		try
		{

			String opNo = req.getOpNO();
			String opName = req.getOpName();
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// req.getRequest().getLastmoditime();

			String shopBeginTime = req.getRequest().getShopBeginTime();
			String shopEndTime = req.getRequest().getShopEndTime();
			String province = req.getRequest().getProvince();
			String city = req.getRequest().getCity();
			String county = req.getRequest().getCounty();
			String street = req.getRequest().getStreet();
			String address = req.getRequest().getAddress();
			// 修改电话号码保存无效
			String phone = req.getRequest().getPhone();

			DCP_OrderParaUpdateReq.level2Elm kdsSet = req.getRequest().getKdsSet();

			UptBean ub = null;
			ub = new UptBean("DCP_ORG");

			ub.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			ub.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));

//			ub.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
//			ub.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
//			ub.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			if (shopBeginTime != null)
			{
				ub.addUpdateValue("SHOPBEGINTIME", new DataValue(shopBeginTime, Types.VARCHAR));
			}
			if (shopEndTime != null)
			{
				ub.addUpdateValue("SHOPENDTIME", new DataValue(shopEndTime, Types.VARCHAR));
			}
			if (province != null)
			{
				ub.addUpdateValue("PROVINCE", new DataValue(province, Types.VARCHAR));
			}
			if (city != null)
			{
				ub.addUpdateValue("CITY", new DataValue(city, Types.VARCHAR));
			}
			if (county != null)
			{
				ub.addUpdateValue("COUNTY", new DataValue(county, Types.VARCHAR));
			}
			if (street != null)
			{
				ub.addUpdateValue("STREET", new DataValue(street, Types.VARCHAR));
			}
			if (address != null)
			{
				ub.addUpdateValue("ADDRESS", new DataValue(address, Types.VARCHAR));
			}

			if (req.getRequest().getRange_Type() != null)
			{
				ub.addUpdateValue("RANGE_TYPE", new DataValue(req.getRequest().getRange_Type(), Types.VARCHAR));
			}

			if (req.getRequest().getRange() != null)
			{
				ub.addUpdateValue("RANGE", new DataValue(req.getRequest().getRange(), Types.VARCHAR));

			}
			if (req.getRequest().getLongitude() != null)
			{
				String longitudStr = req.getRequest().getLongitude();
				try
				{
					double longitud = Double.parseDouble(longitudStr);
				} catch (Exception e)
				{
					// TODO: handle exception
					longitudStr = "0";
				}
				ub.addUpdateValue("LONGITUDE", new DataValue(longitudStr, Types.VARCHAR));
			}
			if (req.getRequest().getLatitude() != null)
			{
				String latitudStr = req.getRequest().getLatitude();
				try
				{
					double latitud = Double.parseDouble(latitudStr);
				} catch (Exception e)
				{
					// TODO: handle exception
					latitudStr = "0";
				}
				ub.addUpdateValue("LATITUDE", new DataValue(latitudStr, Types.VARCHAR));
			}

			if(phone!=null){
				ub.addUpdateValue("PHONE",new DataValue(phone,Types.VARCHAR));
			}
			this.addProcessData(new DataProcessBean(ub));


			// 层级表
			DelBean delOS = new DelBean("DCP_ORG_ORDERSET");
			delOS.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			delOS.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(delOS));

			String[] colNameOrderSet = {"EID", "ORGANIZATIONNO", "ISSELFPICK", "SELFBEGINTIME", "SELFENDTIME", "ISCITYDELIVERY",
					"CHAOQUQUANJIASHOP","CHAOQUOKSHOP"	,"CHAOQUSEVENSHOP" ,"CHAOQULAIERFUSHOP", "ISPRODUCTION","RADIATETYPE",
					"LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME" ,"KDS","EVERYPIECE","WORTHSCORE","COMPLETEIMAGE",
					"ISQRCODE","DELIVERSHOP","ISSPARE"
			};

			String IsSelfPick = "Y";
			if (!Check.Null(req.getRequest().getIsSelfPick()))
			{
				IsSelfPick = req.getRequest().getIsSelfPick();
			}

			String SelfBeginTime = "";
			if (!Check.Null(req.getRequest().getSelfBeginTime() ) )
			{
				SelfBeginTime = req.getRequest().getSelfBeginTime();
			}

			String SelfEndTime = "";
			if (req.getRequest().getSelfEndTime() != null)
			{
				SelfEndTime = req.getRequest().getSelfEndTime();
			}

			String IsCityDelivery = "Y";
			if (req.getRequest().getIsCityDelivery() != null)
			{
				IsCityDelivery = req.getRequest().getIsCityDelivery();
			}

			String IsProduction = "Y";
			if (req.getRequest().getIsProduction() != null)
			{
				IsProduction = req.getRequest().getIsProduction();
			}

			String ChaoQuQuanJiaShop = "";
			if (req.getRequest().getChaoQuQuanJiaShop() != null)
			{
				ChaoQuQuanJiaShop = req.getRequest().getChaoQuQuanJiaShop();
			}

			String ChaoQuOkShop = "";
			if (req.getRequest().getChaoQuOkShop() != null)
			{
				ChaoQuOkShop = req.getRequest().getChaoQuOkShop();
			}

			String ChaoQuSevenShop = "";
			if (req.getRequest().getChaoQuSevenShop() != null)
			{
				ChaoQuSevenShop = req.getRequest().getChaoQuSevenShop() ;
			}
			String ChaoQuLaiErFuShop = "";
			if (req.getRequest().getChaoQuLaiErFuShop() != null)
			{
				ChaoQuLaiErFuShop = req.getRequest().getChaoQuLaiErFuShop();
			}

			String radiateType = "2";//辐射配送机构类型（0全部；1指定机构；2无）	
			if (req.getRequest().getRadiateType() != null)
			{
				radiateType = req.getRequest().getRadiateType();
			}

			String kds = "N"; // KDS是否启用 Y/N 默认N
			if(!Check.Null(kds)){
				kds = kdsSet.getKds();
			}

			DataValue[] insValueOrderSet =
					{
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
							new DataValue(IsSelfPick, Types.VARCHAR),
							new DataValue(SelfBeginTime, Types.VARCHAR),
							new DataValue(SelfEndTime, Types.VARCHAR),
							new DataValue(IsCityDelivery, Types.VARCHAR),

							new DataValue(ChaoQuQuanJiaShop, Types.VARCHAR),
							new DataValue(ChaoQuOkShop, Types.VARCHAR),
							new DataValue(ChaoQuSevenShop, Types.VARCHAR),
							new DataValue(ChaoQuLaiErFuShop, Types.VARCHAR),

							new DataValue(IsProduction, Types.VARCHAR),
							new DataValue(radiateType, Types.VARCHAR),

							new DataValue(opNo, Types.VARCHAR),
							new DataValue(opName, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE),

							new DataValue(kds, Types.VARCHAR),
							new DataValue(kdsSet.getEveryPiece(), Types.VARCHAR),
							new DataValue(kdsSet.getWorthScore(), Types.VARCHAR),

							new DataValue(kdsSet.getCompleteImage(), Types.VARCHAR),
							new DataValue(kdsSet.getIsQrcode(), Types.VARCHAR),
							new DataValue(req.getRequest().getDeliverShop(), Types.VARCHAR),
							//ID 20240513016 【乐沙儿3.4.0.4】指定发货机构调整为备货机构——服务  by jinzma 20240514
							new DataValue(req.getRequest().getIsSpare(), Types.VARCHAR)      //是否为备用快递发货机构0否1是

					};
			InsBean insOrderSet = new InsBean("DCP_ORG_ORDERSET", colNameOrderSet);
			insOrderSet.addValues(insValueOrderSet);
			this.addProcessData(new DataProcessBean(insOrderSet));



//			UptBean ub_orderSet = new UptBean("DCP_ORG_ORDERSET");			
//			ub_orderSet.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//			ub_orderSet.addCondition("ORGANIZATIONNO",
//					new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
//

//
//			this.addProcessData(new DataProcessBean(ub_orderSet));

			// 层级表
			DelBean del = new DelBean("DCP_ORG_ORDERTAKESET");
			del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			del.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(del));

			if (req.getRequest().getAutoTakeList() != null && !req.getRequest().getAutoTakeList().isEmpty())
			{
				String[] colname =
						{ "EID", "ORGANIZATIONNO", "LOADDOCTYPE", "CITYDELIVERYTYPE", "NATIONALDELIVERYTYPE", "ISAUTODELIVERY",
								"LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME" };
				for (DCP_OrderParaUpdateReq.level1Elm oneData : req.getRequest().getAutoTakeList())
				{

					DataValue[] insValue =
							{
									new DataValue(req.geteId(), Types.VARCHAR),
									new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
									new DataValue(oneData.getLoadDocType(), Types.VARCHAR),
									new DataValue(oneData.getCityDeliveryType(), Types.VARCHAR),
									new DataValue(oneData.getNationalDeliveryType(), Types.VARCHAR),
									new DataValue(oneData.getIsAutoDelivery(), Types.VARCHAR),
									new DataValue(opNo, Types.VARCHAR),
									new DataValue(opName, Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE)

							};
					InsBean ins = new InsBean("DCP_ORG_ORDERTAKESET", colname);
					ins.addValues(insValue);
					this.addProcessData(new DataProcessBean(ins));
				}

			}


			// 层级表
			DelBean de2 = new DelBean("DCP_ORG_ORDERSET_RADIATEORG");
			de2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			de2.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(de2));

			//辐射配送机构类型（0全部；1指定机构；2无）	
			if(radiateType.equals("1"))
			{
				if (req.getRequest().getRadiateShippingOrgList() != null && !req.getRequest().getRadiateShippingOrgList().isEmpty())
				{
					String[] colname =
							{ "EID", "ORGANIZATIONNO", "RADIATESHIPPINGSHOP","LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME" };
					for (DCP_OrderParaUpdateReq.level1ShippingOrg oneData : req.getRequest().getRadiateShippingOrgList())
					{
						if(oneData.getRadiateShippingShop()==null)
						{
							continue;
						}

						DataValue[] insValue =
								{
										new DataValue(req.geteId(), Types.VARCHAR),
										new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
										new DataValue(oneData.getRadiateShippingShop(), Types.VARCHAR),
										new DataValue(opNo, Types.VARCHAR),
										new DataValue(opName, Types.VARCHAR),
										new DataValue(lastmoditime, Types.DATE)

								};
						InsBean ins = new InsBean("DCP_ORG_ORDERSET_RADIATEORG", colname);
						ins.addValues(insValue);
						this.addProcessData(new DataProcessBean(ins));
					}

				}

			}

			// KDS配置信息
			DelBean delKds = new DelBean("DCP_ORG_ORDERSET_KDS");
			delKds.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			delKds.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(delKds));

			List<DCP_OrderParaUpdateReq.level3Elm> stallList = kdsSet.getStallList();

			String[] colOrgImg =
					{ "EID", "ORGANIZATIONNO", "IMAGENAME","LASTMODITIME"};

			if(!CollectionUtils.isEmpty(stallList)){
				String[] colKdsName =
						{ "EID", "ORGANIZATIONNO", "STALLID","STALLNAME", "TAGTYPE", "ACHIEVEMENTS","EMPLOYEE","GOODSDETAILS",
								"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","ISORDER" };

				String[] colKdsTagName =
						{ "EID", "ORGANIZATIONNO", "STALLID","TAGNO", "LASTMODIOPID", "LASTMODIOPNAME","LASTMODITIME"};



				for (DCP_OrderParaUpdateReq.level3Elm level3Elm : stallList) {
					DCP_OrderParaUpdateReq.func funcList = level3Elm.getFuncList();
					DataValue[] insValue =
							{
									new DataValue(req.geteId(), Types.VARCHAR),
									new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
									new DataValue(level3Elm.getStallId(), Types.VARCHAR),
									new DataValue(level3Elm.getStallName(), Types.VARCHAR),
									new DataValue(level3Elm.getTagType(), Types.VARCHAR),
									new DataValue(funcList.getAchievements(), Types.VARCHAR),
									new DataValue(funcList.getEmployee(), Types.VARCHAR),
									new DataValue(funcList.getGoodsDetails(), Types.VARCHAR),
									new DataValue(opNo, Types.VARCHAR),
									new DataValue(opName, Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE),
									new DataValue(funcList.getIsOrder(), Types.VARCHAR),

							};
					InsBean ins = new InsBean("DCP_ORG_ORDERSET_KDS", colKdsName);
					ins.addValues(insValue);
					this.addProcessData(new DataProcessBean(ins));

					// KDS配置信息
					DelBean delKdsTag = new DelBean("DCP_ORG_ORDERSET_KDS_TAG");
					delKdsTag.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					delKdsTag.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
					delKdsTag.addCondition("STALLID", new DataValue(level3Elm.getStallId(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(delKdsTag));

					List<DCP_OrderParaUpdateReq.subTag> subTagList = level3Elm.getSubTagList();
					if(!CollectionUtils.isEmpty(subTagList)){

						for (DCP_OrderParaUpdateReq.subTag subTag : subTagList) {

							DataValue[] insTagValue =
									{
											new DataValue(req.geteId(), Types.VARCHAR),
											new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
											new DataValue(level3Elm.getStallId(), Types.VARCHAR),
											new DataValue(subTag.getTagNo(), Types.VARCHAR),
											new DataValue(opNo, Types.VARCHAR),
											new DataValue(opName, Types.VARCHAR),
											new DataValue(lastmoditime, Types.DATE)

									};
							InsBean insTag = new InsBean("DCP_ORG_ORDERSET_KDS_TAG", colKdsTagName);
							insTag.addValues(insTagValue);
							this.addProcessData(new DataProcessBean(insTag));
						}
					}


				}

				// 组织订单完成小票插图
				DelBean delOrgImg = new DelBean("DCP_ORG_ORDERSET_IMAGE");
				delOrgImg.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				delOrgImg.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(delOrgImg));

				List<DCP_OrderParaUpdateReq.level4Elm> imageList = kdsSet.getImageList();
				if(!CollectionUtils.isEmpty(imageList)){
					for (DCP_OrderParaUpdateReq.level4Elm level4Elm : imageList) {
						DataValue[] insOrgImg =
								{
										new DataValue(req.geteId(), Types.VARCHAR),
										new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
										new DataValue(level4Elm.getImageName(), Types.VARCHAR),
										new DataValue(lastmoditime, Types.DATE)

								};
						InsBean insImg = new InsBean("DCP_ORG_ORDERSET_IMAGE", colOrgImg);
						insImg.addValues(insOrgImg);
						this.addProcessData(new DataProcessBean(insImg));
					}
				}
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");

		} catch (Exception e)
		{
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败:" + e.getMessage());
			res.setSuccess(false);
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderParaUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderParaUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderParaUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderParaUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getOrganizationNo()))
		{
			errMsg.append("组织编码不能为空值 ");
			isFail = true;

		}
		if(!Check.Null(req.getRequest().getRange_Type()) && req.getRequest().getRange_Type().equals("1"))
		{
			if(Check.Null(req.getRequest().getRange()))
			{
				errMsg.append("配送范围为手绘方式时,范围不可为空值 ");
				isFail = true;
			}
		}
		List<level1Elm> autoTakeList = req.getRequest().getAutoTakeList();
		if (autoTakeList != null && autoTakeList.isEmpty() == false)
		{
			for (level1Elm oneDate : autoTakeList)
			{
				if (Check.Null(oneDate.getLoadDocType()))
				{
					errMsg.append("loadDocType渠道类型不能为空值 ");
					isFail = true;

				}
			}
		}

		if ("1".equals(req.getRequest().getIsSpare())){
			if (Check.Null(req.getRequest().getDeliverShop())){
				errMsg.append("deliverShop 指定快递发货门店不能为空值 ");
				isFail = true;
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderParaUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderParaUpdateReq>(){};
	}

	@Override
	protected DCP_OrderParaUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderParaUpdateRes();
	}

	/**
	 * 查询 会员接口参数
	 *
	 * @param
	 * @return
	 * @throws Exception
	 */
	protected String getMobileParamSQL(String eId) throws Exception {
		String sql = "select t.item,t.def from platform_basesettemp t where 1=1 and  "
				+ " (ITEM LIKE '%YC%' OR ITEM in ('EmailAddress', 'EmailHost' , 'EmailPassword') )"
				+ " and t.EID='" + eId + "'  and t.status='100' ";

		return sql;
	}


}
