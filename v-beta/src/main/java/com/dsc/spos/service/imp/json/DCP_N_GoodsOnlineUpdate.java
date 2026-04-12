package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlineUpdateReq;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlineUpdateReq.*;
import com.dsc.spos.json.cust.res.DCP_N_GoodsOnlineUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_N_GoodsOnlineUpdate
 * 服务说明：N-商城商品修改  (Yapi没有规格)
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_GoodsOnlineUpdate extends SPosAdvanceService<DCP_N_GoodsOnlineUpdateReq, DCP_N_GoodsOnlineUpdateRes> {
    @Override
    protected void processDUID(DCP_N_GoodsOnlineUpdateReq req, DCP_N_GoodsOnlineUpdateRes res) throws Exception {

        try {
            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String curLangType = req.getLangType();
            if (curLangType == null || curLangType.isEmpty()) {
                curLangType = "zh_CN";
            }
            String eId = req.geteId();
            String pluNo = req.getRequest().getPluNo();
            String pluType = req.getRequest().getPluType();//商品类型：GOODS-商品；MSPECGOODS-多规格商品，不可修改
            String stockDisplay = req.getRequest().getStockDisplay();//页面是否显示库存0-否1-是
            String preSale = req.getRequest().getPreSale();//是否预订，需提前预订0-否1-是
            String deliveryDateType = req.getRequest().getDeliveryDateType();//发货时机类型1：付款成功后发货2：指定日期发货
            String deliveryDateType2 = req.getRequest().getDeliveryDateType2();//发货时间类型1：小时 2：天
            String deliveryDateValue = req.getRequest().getDeliveryDateValue();//付款后%S天/小时后发货，发货时机类型为1时必须传入
            String deliveryDate = req.getRequest().getDeliveryDate();//预计发货日期，发货时机类型为2时必须传入，YYYY-MM-DD 数据库是DATE类型需要转换

            String deliveryStartDate = req.getRequest().getDeliveryStartDate(); // 预计发货开始日期   YYYY-MM-DD
            String deliveryEndDate = req.getRequest().getDeliveryEndDate();     // 预计发货截止日期   YYYY-MM-DD


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	/*if(deliveryDate!=null&&deliveryDate.isEmpty()==false)
		{
			try
			{
				Date d1 = formatter.parse(deliveryDate);
				deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1);

			} catch (Exception e)
			{
				deliveryDate ="";
			}

		}*/
            String shopPickUp = req.getRequest().getShopPickUp();//是否支持自提0-否1-是
            String cityDeliver = req.getRequest().getCityDeliver();//是否支持同城配送0-否1-是
            String expressDeliver = req.getRequest().getExpressDeliver();//是否支持全国快递0-否1-是
            String freightFree = req.getRequest().getFreightFree();//是否包邮0-否1-是
            String isRestaurant = req.getRequest().getIsRestaurant();   //是否堂食0-否1-是
//		String onShelfAuto = req.getRequest().getOnShelfAuto();//是否自动上架0-否1-是
//		String onShelfDate = req.getRequest().getOnShelfDate();//上架日期，自动时不可空，YYYY-MM-DD 数据库是DATE类型需要转换
		/*if(onShelfDate!=null&&onShelfDate.isEmpty()==false)
		{
			try
			{
				Date d1 = formatter.parse(onShelfDate);
				onShelfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1);

			} catch (Exception e)
			{
				onShelfDate ="";
			}

		}*/
//		String onShelfTime = req.getRequest().getOnShelfTime();//上架时间，自动时不可空,HH:MI:SS
//		String offShelfAuto = req.getRequest().getOffShelfAuto();//是否自动下架0-否1-是
//		String offShelfDate = req.getRequest().getOffShelfDate();//下架日期，自动时不可空，YYYY-MM-DD
//		String offShelfTime = req.getRequest().getOffShelfTime();//下架时间，自动时不可空,HH:MI:SS

            String freightTemplateId = req.getRequest().getFreightTemplateId();//运费模板编码
            String memo = req.getRequest().getMemo();//
            String status = req.getRequest().getStatus();
            String DCP_GOODS_ONLINE_sortId = req.getRequest().getSortId();
            List<Tags> tags = req.getRequest().getTags();


            String classType = "ONLINE";

            List<displayNameLang> displayName_lang = req.getRequest().getDisplayName_lang();
            List<classMemu> classList = req.getRequest().getClassList();
            List<shareDescription> shareDescription_lang = req.getRequest().getShareDescription_lang();

            List<refClassMemu> refClassList = req.getRequest().getRefClassList();
            List<msgKind> msgKindList = req.getRequest().getMsgKindList();//留言项
            String attrGroupId = req.getRequest().getAttrGroupId();
            List<intro> introList = req.getRequest().getIntroList();//商品介绍

            List<simpleDescription> simpleDescription_lang = req.getRequest().getSimpleDescription_lang();

            DelBean db1 = new DelBean("DCP_GOODS_ONLINE_LANG");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1)); //

            //先删除原来的
            db1 = new DelBean("DCP_CLASS_GOODS");
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));

//		//先删除原来的
//		db1 = new DelBean("DCP_CLASS_GOODS_LANG");
//		db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//		db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
//		db1.addCondition("LANG_TYPE", new DataValue(curLangType, Types.VARCHAR));
//		this.addProcessData(new DataProcessBean(db1));

            //先删除原来的
            db1 = new DelBean("DCP_GOODS_ONLINE_REFCLASS");
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //先删除原来的
            db1 = new DelBean("DCP_GOODS_ONLINE_INTRO");
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //先删除原来的
            db1 = new DelBean("DCP_GOODS_ONLINE_INTRO_LANG");
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("LANG_TYPE", new DataValue(curLangType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //先删除原来的
            db1 = new DelBean("DCP_GOODS_ONLINE_MSGKIND");
            db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));


            String sql = "SELECT PLUNO FROM DCP_GOODS_ONLINE WHERE EID='"+req.geteId()+"' AND PLUNO='"+req.getRequest().getPluNo()+"' ";


            List<Map<String, Object>> mapList = this.doQueryData(sql, null);
            if(mapList!=null && !mapList.isEmpty()) {

                UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));

                ub1.addUpdateValue("PLUTYPE", new DataValue(pluType, Types.VARCHAR));
                ub1.addUpdateValue("STOCKDISPLAY", new DataValue(stockDisplay, Types.VARCHAR));
                ub1.addUpdateValue("ATTRGROUPID", new DataValue(attrGroupId, Types.VARCHAR));
                ub1.addUpdateValue("PRESALE", new DataValue(preSale, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATETYPE", new DataValue(deliveryDateType, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATETYPE2", new DataValue(deliveryDateType2, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATEVALUE", new DataValue(deliveryDateValue, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATE", new DataValue(deliveryDate, Types.DATE));
                ub1.addUpdateValue("SHOPPICKUP", new DataValue(shopPickUp, Types.VARCHAR));
                ub1.addUpdateValue("CITYDELIVER", new DataValue(cityDeliver, Types.VARCHAR));
                ub1.addUpdateValue("EXPRESSDELIVER", new DataValue(expressDeliver, Types.VARCHAR));
                ub1.addUpdateValue("FREIGHTFREE", new DataValue(freightFree, Types.VARCHAR));
                ub1.addUpdateValue("FREIGHTTEMPLEID", new DataValue(freightTemplateId, Types.VARCHAR));
                ub1.addUpdateValue("ISRESTAURANT", new DataValue(isRestaurant, Types.VARCHAR));
//			ub1.addUpdateValue("ONSHELFAUTO",new DataValue(onShelfAuto, Types.VARCHAR));
//			ub1.addUpdateValue("ONSHELFDATE",new DataValue(onShelfDate, Types.DATE));
//			ub1.addUpdateValue("ONSHELFTIME",new DataValue(onShelfTime, Types.VARCHAR));
//			ub1.addUpdateValue("OFFSHELFAUTO",new DataValue(offShelfAuto, Types.VARCHAR));
//			ub1.addUpdateValue("OFFSHELFDATE",new DataValue(offShelfDate, Types.DATE));
//			ub1.addUpdateValue("OFFSHELFTIME",new DataValue(offShelfTime, Types.VARCHAR));
                ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));

                ub1.addUpdateValue("SORTID", new DataValue(DCP_GOODS_ONLINE_sortId, Types.VARCHAR));
                ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));  //按玲霞的要求，固定给100

                ub1.addUpdateValue("DELIVERYSTARTDATE", new DataValue(deliveryStartDate, Types.DATE));  //按王辉的要求增加
                ub1.addUpdateValue("DELIVERYENDDATE", new DataValue(deliveryEndDate, Types.DATE));  //按王辉的要求增加


                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

                this.addProcessData(new DataProcessBean(ub1));

            } else {
                String[] columns_Goods =
                        {
                                "EID",
                                "PLUNO",
                                "PLUTYPE",
                                "STOCKDISPLAY",
                                "ATTRGROUPID",
                                "PRESALE",
                                "DELIVERYDATETYPE",
                                "DELIVERYDATETYPE2",
                                "DELIVERYDATEVALUE",
                                "DELIVERYDATE",
                                "SHOPPICKUP",
                                "CITYDELIVER",
                                "EXPRESSDELIVER",
                                "FREIGHTFREE",
                                "FREIGHTTEMPLEID",
//						"ONSHELFAUTO",
//						"ONSHELFDATE",
//						"ONSHELFTIME",
//						"OFFSHELFAUTO",
//						"OFFSHELFDATE",
//						"OFFSHELFTIME",
                                "MEMO",
                                "STATUS",
                                "CREATEOPID",
                                "CREATEOPNAME",
                                "CREATETIME",
                                "DELIVERYSTARTDATE",
                                "DELIVERYENDDATE",
                                "ISRESTAURANT",
                        };

                DataValue[] insValueGoods = null;

                insValueGoods = new DataValue[]
                        {
                                new DataValue(req.geteId(), Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(pluType, Types.VARCHAR),
                                new DataValue(stockDisplay, Types.VARCHAR),
                                new DataValue(attrGroupId, Types.VARCHAR),
                                new DataValue(preSale, Types.VARCHAR),
                                new DataValue(deliveryDateType, Types.VARCHAR),
                                new DataValue(deliveryDateType2, Types.VARCHAR),
                                new DataValue(deliveryDateValue, Types.VARCHAR),
                                new DataValue(deliveryDate, Types.DATE),
                                new DataValue(shopPickUp, Types.VARCHAR),
                                new DataValue(cityDeliver, Types.VARCHAR),
                                new DataValue(expressDeliver, Types.VARCHAR),
                                new DataValue(freightFree, Types.VARCHAR),
                                new DataValue(freightTemplateId, Types.VARCHAR),
//							new DataValue(onShelfAuto, Types.VARCHAR),
//							new DataValue(onShelfDate, Types.DATE),
//							new DataValue(onShelfTime, Types.VARCHAR),
//							new DataValue(offShelfAuto, Types.VARCHAR),
//							new DataValue(offShelfDate, Types.DATE),
//							new DataValue(offShelfTime, Types.VARCHAR),
                                new DataValue(memo, Types.VARCHAR),
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                new DataValue(req.getOpName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),

                                new DataValue(deliveryStartDate, Types.DATE),
                                new DataValue(deliveryEndDate, Types.DATE),
                                new DataValue(isRestaurant, Types.VARCHAR),
                        };

                InsBean ib1_goods = new InsBean("DCP_GOODS_ONLINE", columns_Goods);
                ib1_goods.addValues(insValueGoods);
                this.addProcessData(new DataProcessBean(ib1_goods)); //

            }


            if (displayName_lang != null) {
                String[] columns_Goods_lang =
                        {
                                "EID",
                                "PLUNO",
                                "LANG_TYPE",
                                "SIMPLEDESCRIPTION",
                                "SHAREDESCRIPTION",
                                "DISPLAYNAME",
                                "LASTMODITIME"
                        };

                for (displayNameLang par_lang : displayName_lang) {
                    String langType = par_lang.getLangType();
                    String displayName = par_lang.getName();
                    String simple = "";
                    String share = "";
                    if (simpleDescription_lang != null) {
                        for (simpleDescription par_simple : simpleDescription_lang) {
                            if (langType.equals(par_simple.getLangType())) {
                                simple = par_simple.getName();
                                break;
                            }

                        }
                    }

                    if (shareDescription_lang != null) {
                        for (shareDescription par : shareDescription_lang) {
                            if (langType.equals(par.getLangType())) {
                                share = par.getName();
                                break;
                            }
                        }
                    }

                    DataValue[] insValue1 = null;

                    insValue1 = new DataValue[]
                            {new DataValue(eId, Types.VARCHAR), new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(langType, Types.VARCHAR), new DataValue(simple, Types.VARCHAR),
                                    new DataValue(share, Types.VARCHAR), new DataValue(displayName, Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)};

                    InsBean ib1 = new InsBean("DCP_GOODS_ONLINE_LANG", columns_Goods_lang);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));

                }

            }

            List<String> classNoList = new ArrayList<String>();//菜单列表 和后面关联菜单共用一个多语言，防止重复

            if (classList != null) {
                String[] columns_class = {"EID", "CLASSTYPE", "CLASSNO", "PLUNO", "PLUTYPE", "SORTID", "LASTMODITIME"
                };
//			String[] columns_class_lang ={"EID","CLASSTYPE","CLASSNO","PLUNO","LANG_TYPE","DISPLAYNAME","LASTMODITIME"
//			};
                String[] columns_class_lang =
                        {
                                "EID",
                                "PLUNO",
                                "LANG_TYPE",
                                "SIMPLEDESCRIPTION",
                                "SHAREDESCRIPTION",
                                "DISPLAYNAME",
                                "LASTMODITIME"
                        };

                int sortId = 1;
                for (classMemu par : classList) {
                    classNoList.add(par.getClassNo());
//				if(par.getClassName()==null||par.getClassName().isEmpty())
//				{
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "className不能为空，");
//				}
                    DataValue[] insValue1 = null;

                    insValue1 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(classType, Types.VARCHAR),
                                    new DataValue(par.getClassNo(), Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(pluType, Types.VARCHAR),
                                    new DataValue(sortId, Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib1 = new InsBean("DCP_CLASS_GOODS", columns_class);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));
                    sortId++;


//				DataValue[] insValue_lang = null;
//
//				insValue_lang = new DataValue[]
//						{
//								new DataValue(eId, Types.VARCHAR),
//								new DataValue(classType, Types.VARCHAR),
//								new DataValue(par.getClassNo(), Types.VARCHAR),
//								new DataValue(pluNo, Types.VARCHAR),
//								new DataValue(curLangType, Types.VARCHAR),
//								new DataValue(par.getClassName(), Types.VARCHAR),
//								new DataValue(lastmoditime, Types.DATE)
//						};
//
//				InsBean ib_lang = new InsBean("DCP_GOODS_ONLINE_LANG", columns_class_lang);
//				ib_lang.addValues(insValue_lang);
//				this.addProcessData(new DataProcessBean(ib_lang));

                }


            }

            if (refClassList != null) {
                String[] columns_Goods_refClass =
                        {
                                "EID",
                                "PLUNO",
                                "CLASSTYPE",
                                "CLASSNO",
                                "LASTMODITIME"
                        };
//			String[] columns_class_lang ={"EID","CLASSTYPE","CLASSNO","PLUNO","LANG_TYPE","DISPLAYNAME","LASTMODITIME"
//			};

                String[] columns_class_lang =
                        {
                                "EID",
                                "PLUNO",
                                "LANG_TYPE",
                                "SIMPLEDESCRIPTION",
                                "SHAREDESCRIPTION",
                                "DISPLAYNAME",
                                "LASTMODITIME"
                        };
                for (refClassMemu par : refClassList) {
                    DataValue[] insValue1 = null;

                    insValue1 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(classType, Types.VARCHAR),
                                    new DataValue(par.getClassNo(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib1 = new InsBean("DCP_GOODS_ONLINE_REFCLASS", columns_Goods_refClass);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));

                    if (classNoList.contains(par.getClassNo()))//菜单列表 和后面关联菜单共用一个多语言，防止重复
                    {
                        continue;
                    }
//					if(par.getClassName()==null||par.getClassName().isEmpty())
//					{
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "className不能为空，");
//					}


//					DataValue[] insValue_lang = null;
//					insValue_lang = new DataValue[]
//							{
//									new DataValue(eId, Types.VARCHAR),
//									new DataValue(classType, Types.VARCHAR),
//									new DataValue(par.getClassNo(), Types.VARCHAR),
//									new DataValue(pluNo, Types.VARCHAR),
//									new DataValue(curLangType, Types.VARCHAR),
//									new DataValue(par.getClassName(), Types.VARCHAR),
//									new DataValue(lastmoditime, Types.DATE)
//							};
//
//					InsBean ib_lang = new InsBean("DCP_GOODS_ONLINE_LANG", columns_class_lang);
//					ib_lang.addValues(insValue_lang);
//					this.addProcessData(new DataProcessBean(ib_lang));

                }
            }


            if (introList != null) {
                String[] columns_Goods_intro =
                        {
                                "EID",
                                "PLUNO",
                                "ATTRID",
                                "LASTMODITIME"
                        };

                String[] columns_Goods_intro_lang =
                        {
                                "EID",
                                "PLUNO",
                                "ATTRID",
                                "LANG_TYPE",
                                "INTRO",
                                "LASTMODITIME"
                        };
                for (intro par : introList) {
                    DataValue[] insValue1 = null;

                    insValue1 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(par.getAttrId(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib1 = new InsBean("DCP_GOODS_ONLINE_INTRO", columns_Goods_intro);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));


                    DataValue[] insValue2 = null;

                    insValue2 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(par.getAttrId(), Types.VARCHAR),
                                    new DataValue(curLangType, Types.VARCHAR),
                                    new DataValue(par.getIntro(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib2 = new InsBean("DCP_GOODS_ONLINE_INTRO_LANG", columns_Goods_intro_lang);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));

                }


            }

            if (msgKindList != null) {
                String[] columns_Goods_intro_msgKind =
                        {
                                "EID",
                                "PLUNO",
                                "MSGKINDID",
                                "NEED",
                                "SORTID",
                                "LASTMODITIME"
                        };

                int sortId = 1;
                for (msgKind par : msgKindList) {
                    DataValue[] insValue1 = null;

                    insValue1 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(par.getMsgKindId(), Types.VARCHAR),
                                    new DataValue(par.getNeed(), Types.VARCHAR),
                                    new DataValue(sortId, Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib1 = new InsBean("DCP_GOODS_ONLINE_MSGKIND", columns_Goods_intro_msgKind);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));
                    sortId++;

                }


            }


            //【ID1033123】【标准产品3.0】商品标签及商城副标题展示---中台服务 by jinzma 20230531
            if (!CollectionUtils.isEmpty(tags)) {
                for (Tags par : tags) {
                    UptBean ub1 = new UptBean("DCP_TAGTYPE_DETAIL");
                    ub1.addUpdateValue("ONLILASORTING", new DataValue(par.getOnliLaSorting(), Types.VARCHAR));

                    ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                    ub1.addCondition("TAGGROUPTYPE", new DataValue("GOODS", Types.VARCHAR));
                    ub1.addCondition("TAGNO", new DataValue(par.getTagNo(), Types.VARCHAR));
                    ub1.addCondition("ID", new DataValue(pluNo, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));
                }
            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_GoodsOnlineUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_GoodsOnlineUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_GoodsOnlineUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_GoodsOnlineUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null) {
            errMsg.append("request不能为空 ");
            isFail = true;
        } else {
            if (req.getRequest().getMsgKindList() != null && !req.getRequest().getMsgKindList().isEmpty()) {
                for (msgKind par : req.getRequest().getMsgKindList()) {
                    if (Check.Null(par.getMsgKindId())) {
                        errMsg.append("留言项编码不能为空值 ，");
                        isFail = true;
                    }
                    if (Check.Null(par.getNeed())) {
                        errMsg.append("是否必须need不能为空值 ，");
                        isFail = true;
                    }
                }
            }

            List<Tags> tags = req.getRequest().getTags();
            if (!CollectionUtils.isEmpty(tags)) {
                for (Tags par : tags) {
                    if (Check.Null(par.getTagNo())) {
                        errMsg.append("标签编码不能为空值, ");
                        isFail = true;
                    }
                    if (!PosPub.isNumeric(par.getOnliLaSorting())) {
                        errMsg.append("标签排序不能为空值或非数值, ");
                        isFail = true;
                    }

                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }

        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_GoodsOnlineUpdateReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsOnlineUpdateReq>(){};
    }

    @Override
    protected DCP_N_GoodsOnlineUpdateRes getResponseType() {
        return new DCP_N_GoodsOnlineUpdateRes();
    }


}
