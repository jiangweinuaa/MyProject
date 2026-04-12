package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsShelfDateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsShelfDateUpdateReq.PluList;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfDateUpdateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.scheduler.job.DCP_GoodsShelfStatusJob;
import com.dsc.spos.scheduler.util.QuartzUtil;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.quartz.JobDataMap;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * V3-商品渠道自动上下架设置
 *
 */
public class DCP_GoodsShelfDateUpdate extends SPosAdvanceService<DCP_GoodsShelfDateUpdateReq, DCP_GoodsShelfDateUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsShelfDateUpdateReq req, DCP_GoodsShelfDateUpdateRes res) throws Exception {
		
		try {
			
			String channelId = req.getRequest().getChannelId();
			String onShelfAuto = req.getRequest().getOnShelfAuto();
			String onShelfDate = req.getRequest().getOnShelfDate();
			String onShelfTime = req.getRequest().getOnShelfTime();
			String offShelfAuto = req.getRequest().getOffShelfAuto();
			String offShelfDate = req.getRequest().getOffShelfDate();
			String offShelfTime = req.getRequest().getOffShelfTime();

            // 新增门店自动上下架
            String onOffType = req.getRequest().getOnOffType();
            String shopId = req.getRequest().getShopId();

            String eId = req.geteId();
            String opNO = req.getOpNO();
            String opName = req.getOpName();

            List<PluList> pluDatas = req.getRequest().getPluList();

			String status = "0";
			if("1".equals(onShelfAuto)){
				status = "100";
			}else if("1".equals(offShelfAuto)){
				status = "0";
			}

			String orgType = req.getOrg_Form();
			String orgId = req.getShopId();

			String[] columns =
                    {
                            "EID", "PLUNO", "CHANNELID",
                            "ONSHELFAUTO", "ONSHELFDATE", "ONSHELFTIME",
                            "OFFSHELFAUTO", "OFFSHELFDATE", "OFFSHELFTIME",
                            "CREATEOPID", "CREATEOPNAME", "CREATETIME"
                    };
            String[] columns2 =
                    {
                            "EID", "PLUNO","SHOPID", "CHANNELID",
                            "ONSHELFAUTO", "ONSHELFDATE", "ONSHELFTIME",
                            "OFFSHELFAUTO", "OFFSHELFDATE", "OFFSHELFTIME",
                            "CREATEOPID", "CREATEOPNAME", "CREATETIME"
                    };
            String lastmoditime = null;//req.getRequset().getLastmoditime();
            if (lastmoditime == null || lastmoditime.isEmpty()) {
                lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

			if(pluDatas != null && pluDatas.size() > 0){
				for (PluList pluList : pluDatas) {

					String pluNo = pluList.getPluNo();

                    if(Check.Null(onOffType)||"0".equals(onOffType)) {
                        // 渠道自动上下架

                        DelBean	db1 = new DelBean("DCP_GOODS_SHELF_DATE");
                        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        db1.addCondition("CHANNELID", new DataValue( channelId, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));

                        DataValue[] insValue1 = null;

                        insValue1 = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(channelId, Types.VARCHAR),
                                        new DataValue(onShelfAuto, Types.VARCHAR),
                                        new DataValue(onShelfDate, Types.VARCHAR),
                                        new DataValue(onShelfTime, Types.VARCHAR),
                                        new DataValue(offShelfAuto, Types.VARCHAR),
                                        new DataValue(offShelfDate, Types.VARCHAR),
                                        new DataValue(offShelfTime, Types.VARCHAR),
                                        new DataValue(opNO, Types.VARCHAR),
                                        new DataValue(opName, Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };

                        InsBean ib1 = new InsBean("DCP_GOODS_SHELF_DATE", columns);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1));
                    }else if("1".equals(onOffType)){
                        // 门店自动上下架

						// 先查询下门店有无存在销售范围记录
						String sql  = "select * from DCP_GOODS_SHELF_RANGE where eid = '"+eId+"' and PLUNO = '"+pluNo+"' and CHANNELID = '"+channelId+"' and SHOPID = '"+shopId+"'";
						List<Map<String, Object>> getData = this.doQueryData(sql, null);
						if(CollectionUtils.isEmpty(getData)){
							// 如果没有记录则新增一条
							String[] columns_Goods_Shelf_Range =
									{ "EID", "PLUNO", "PLUTYPE", "PLUNAME", "CHANNELID","SHOPID","STATUS","BILLTYPE", "ORGTYPE", "ORGID", "ORGNAME",
											"CREATEOPID", "CREATEOPNAME", "CREATETIME" };

							DataValue[] insValue1 = null;

							insValue1 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(pluNo, Types.VARCHAR),
											new DataValue(pluList.getPluType(), Types.VARCHAR),
											new DataValue("", Types.VARCHAR),
											new DataValue(channelId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),
											new DataValue(status, Types.VARCHAR),
											new DataValue("2", Types.VARCHAR),
											new DataValue(orgType, Types.VARCHAR),
											new DataValue(orgId, Types.VARCHAR),
											new DataValue("", Types.VARCHAR),
											new DataValue(req.getOpNO(), Types.VARCHAR),
											new DataValue(req.getOpName(), Types.VARCHAR),
											new DataValue(lastmoditime, Types.DATE) };

							InsBean ib1 = new InsBean("DCP_GOODS_SHELF_RANGE", columns_Goods_Shelf_Range);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));

							this.doExecuteDataToDB();

						}

						DelBean	db1 = new DelBean("DCP_GOODS_SHELF_SHOPDATE");
                        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        db1.addCondition("CHANNELID", new DataValue( channelId, Types.VARCHAR));
                        db1.addCondition("SHOPID", new DataValue( shopId, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));

                        DataValue[] insValue1 = null;

                        insValue1 = new DataValue[]
                                {
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(channelId, Types.VARCHAR),
                                        new DataValue(onShelfAuto, Types.VARCHAR),
                                        new DataValue(onShelfDate, Types.VARCHAR),
                                        new DataValue(onShelfTime, Types.VARCHAR),
                                        new DataValue(offShelfAuto, Types.VARCHAR),
                                        new DataValue(offShelfDate, Types.VARCHAR),
                                        new DataValue(offShelfTime, Types.VARCHAR),
                                        new DataValue(opNO, Types.VARCHAR),
                                        new DataValue(opName, Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };

                        InsBean ib1 = new InsBean("DCP_GOODS_SHELF_SHOPDATE", columns2);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1));
                    }

                    //自动上下架商品功能，增加定时任务
					//上架
					if("1".equals(onShelfAuto)){
						String jobName = req.geteId()+pluNo+channelId+"ONShedule";
						String triggerName = req.geteId()+pluNo+channelId+"ONTrigger";
						JobDataMap map = new JobDataMap();
						map.put("EID", req.geteId());
						map.put("PLUNO", pluNo);
						map.put("CHANNELID", channelId);
						map.put("TYPE", "ON");
						map.put("SHOPID", shopId);
						QuartzUtil.resetJob(triggerName, DCP_GoodsShelfStatusJob.JOB_GROUP_NAME, DCP_GoodsShelfStatusJob.class.getName(), jobName, QuartzUtil.onlyOnce(onShelfDate+" "+onShelfTime), 0, map);
					}
					//下架
					if("1".equals(offShelfAuto)){
						String jobName = req.geteId()+pluNo+channelId+"OFFShedule";
						String triggerName = req.geteId()+pluNo+channelId+"OFFTrigger";
						JobDataMap map = new JobDataMap();
						map.put("EID", req.geteId());
						map.put("PLUNO", pluNo);
						map.put("CHANNELID", channelId);
						map.put("TYPE", "OFF");
                        map.put("SHOPID", shopId);
						QuartzUtil.resetJob(triggerName, DCP_GoodsShelfStatusJob.JOB_GROUP_NAME, DCP_GoodsShelfStatusJob.class.getName(), jobName, QuartzUtil.onlyOnce(offShelfDate+" "+offShelfTime), 0, map);
					}
				}
				
				this.doExecuteDataToDB();
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			
		} catch (Exception e) {
			res.setSuccess(false);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsShelfDateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsShelfDateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsShelfDateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsShelfDateUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        DCP_GoodsShelfDateUpdateReq.levReq request = req.getRequest();
        String onShelfAuto = request.getOnShelfAuto();
        String offShelfAuto = request.getOffShelfAuto();

        String onShelfDate = request.getOnShelfDate();
        String onShelfTime = request.getOnShelfTime();

        String offShelfDate = request.getOffShelfDate();
        String offShelfTime = request.getOffShelfTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 防止后续定时任务执行 报错Based on configured schedule, the given trigger ，加下检核，时间不可以小于当前时间
        if(!Check.Null(onShelfAuto)&&"1".equals(onShelfAuto)){
            // 上架
            if(!Check.Null(onShelfDate)&&!Check.Null(offShelfTime)){
                String onShelDateTime  = onShelfDate+" "+onShelfTime;
                Date onDateTime = simpleDateFormat.parse(onShelDateTime);
               if( onDateTime.compareTo(new Date())<0){
                   errMsg.append("上架时间不可小于当前时间， ");
                   isFail = true;
               }
            }else {
                errMsg.append("上架时间不可为空， ");
                isFail = true;
            }
        }

        if(!Check.Null(offShelfAuto)&&"1".equals(offShelfAuto)){
            // 上架
            if(!Check.Null(offShelfDate)&&!Check.Null(offShelfTime)){
                String offShelDateTime  = offShelfDate+" "+offShelfTime;
                Date offDateTime = simpleDateFormat.parse(offShelDateTime);
                if( offDateTime.compareTo(new Date())<0){
                    errMsg.append("下架时间不可小于当前时间， ");
                    isFail = true;
                }
            }else {
                errMsg.append("下架时间不可为空， ");
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
	protected TypeToken<DCP_GoodsShelfDateUpdateReq> getRequestType() {
		return new TypeToken<DCP_GoodsShelfDateUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsShelfDateUpdateRes getResponseType() {
		return new DCP_GoodsShelfDateUpdateRes();
	}

}
