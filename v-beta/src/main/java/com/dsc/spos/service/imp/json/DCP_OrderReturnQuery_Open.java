package com.dsc.spos.service.imp.json;

import cn.hutool.core.util.StrUtil;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderReturnQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderReturnQuery_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_OrderReturnQuery_Open extends SPosAdvanceService<DCP_OrderReturnQuery_OpenReq, DCP_OrderReturnQuery_OpenRes> {

    private String logStr = "【第三方调用DCP_OrderReturnQuery退货询问接口，全退】";

    @Override
    protected void processDUID(DCP_OrderReturnQuery_OpenReq req, DCP_OrderReturnQuery_OpenRes res)
            throws Exception {
        DCP_OrderReturnQuery_OpenReq.Level1Elm reqRequest = req.getRequest();

        logStr = "【第三方调用DCP_OrderReturnQuery退货询问接口，全退】,订单号orderNo="+reqRequest.getOrderNo()+",";
        if(reqRequest.getRefundType().equals("1"))
		{
			logStr ="【第三方调用DCP_OrderReturnQuery退货询问接口】,订单号orderNo="+reqRequest.getOrderNo()+",";
		}

        /*************必传的节点******************/
        String eId_para = req.getRequest().geteId();//请求传入的eId
        if(eId_para==null||eId_para.isEmpty())
        {
            eId_para = req.geteId();
        }
        if (eId_para==null||eId_para.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        req.getRequest().seteId(eId_para);
        boolean isNeedRequsetERP = false;        //是否需要调用ERP询问接口
        String sql = "SELECT * FROM DCP_ORDER WHERE EID = ? AND ORDERNO = ?";
        HelpTools.writelog_waimai(logStr+"查询开始： 传入的参数EID="+eId_para+" ORDERNO="+reqRequest.getOrderNo()+" 查询语句："+sql);
        List<Map<String, Object>> mapList = this.doQueryData(sql, new String[]{eId_para, reqRequest.getOrderNo()});
              
        if(CollectionUtils.isEmpty(mapList))
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("该订单不存在");
			HelpTools.writelog_waimai(logStr+"查询完成：该订单不存在！ ");
			return;
		}

        Map<String, Object> order = mapList.get(0);
        String machShop = order.get("MACHSHOP") == null ? "" : order.get("MACHSHOP").toString();
        String shippingShop = order.get("SHIPPINGSHOP") == null ? "" : order.get("SHIPPINGSHOP").toString();
        String orderStatus = order.get("STATUS") == null ? "" : order.get("STATUS").toString();
        String departNo = order.getOrDefault("DEPARTNO", "").toString();
        String shop = order.getOrDefault("SHOP","").toString();
        String loadDocType = order.getOrDefault("LOADDOCTYPE","").toString();
        String channelId = order.getOrDefault("CHANNELID","").toString();
        String operationtype = order.getOrDefault("OPERATIONTYPE", "").toString();
        String productStatus = order.getOrDefault("PRODUCTSTATUS", "").toString();//生产状态
        String addOrderChildNo = order.getOrDefault("ADDORDERCHILDNO", "").toString();//追加商品的子单
        String opNo = reqRequest.getOpNo();
        if (opNo==null)
        {
            opNo= "";
        }
        String opName = reqRequest.getOpName();
        if (opName==null||opName.trim().isEmpty())
        {
            opName = "渠道("+loadDocType+")调用";
        }
        
        if(orderStatus.equals("3")||orderStatus.equals("12"))//已经退单
		{
			String ss = "未知";
			
		    if(orderStatus.equals("3"))
			{
				ss = "已取消";
			}		
			else if(orderStatus.equals("12"))
			{
				ss = "已退单";
			}
			
			res.setSuccess(false);
            res.setServiceStatus("100");
			res.setServiceDescription("该订单状态是"+ss+"，无法退货！");
			HelpTools.writelog_waimai(logStr+"该订单状态是"+ss+"，无法退货！ 订单状态status="+orderStatus);			
			return;
		}
        if (!addOrderChildNo.isEmpty())
        {
            sql = "SELECT * FROM DCP_ORDER WHERE EID ='"+eId_para+"' AND ORDERNO ='"+addOrderChildNo+"'";
            HelpTools.writelog_waimai(logStr+"查询追加商品的子单查询语句："+sql);
            List<Map<String, Object>> mapAddOrderChildList = this.doQueryData(sql, null);
            if (mapAddOrderChildList != null && !mapAddOrderChildList.isEmpty())
            {
                String addOrderChildStatus = mapAddOrderChildList.get(0).getOrDefault("STATUS","").toString();
                if ("3".equals(addOrderChildStatus)||"12".equals(addOrderChildStatus))
                {

                }
                else
                {
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription("该订单存在追加商品，请联系客服先将追加商品订单退单，追加商品订单号:"+addOrderChildNo);
                    HelpTools.writelog_waimai(logStr+"该订单"+reqRequest.getOrderNo()+"存在追加商品，请联系客服，先将追加商品订单退单，追加商品订单号:"+addOrderChildNo);
                    return;
                }
            }


        }
        if(orderStatus.equals("11"))
        {
            HelpTools.writelog_waimai(logStr+"该订单状态status="+orderStatus+"已完成，无须调用ERP退单询问接口!");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return;
        }
        //菲尔雪订单控制
        //是否需要生产控制” ControlProduction    Y/N 默认值N；
        String ControlProduction = PosPub.getPARA_SMS(dao,eId_para,"","ControlProduction");
        //操作门店不为空，并且参数是Y
        if ("Y".equals(ControlProduction)&&!Check.Null(req.getRequest().getShopId()))
        {
            //// operationType枚举值：0或者空 表示不可修改订单不可退订 1表示可修改订单，2表示可退单
            if ("2".equals(operationtype)==false)
            {
                //生产状态(4生产接单；5生产拒单；6完工入库；7内部调拨）
                if ("4".equals(productStatus)||"6".equals(productStatus)||"7".equals(productStatus))
                {
                    //1下单机构，2生产机构，3配送机构  默认值2；
                    String typeOfOrg = PosPub.getPARA_SMS(dao,eId_para,"","TypeOfOrganization");
                    if (StrUtil.isEmpty(typeOfOrg))
                    {
                        typeOfOrg = "2";
                    }
                    String str_error = "需要生产机构同意，请联系该门店";
                    boolean isCheckOrg = true;
                    String shop_create = order.get("SHOP").toString();
                    String shopName_create = order.get("SHOPNAME").toString();
                    String shop_mach = order.get("MACHSHOP").toString();
                    String shopName_mach = order.get("MACHSHOPNAME").toString();
                    String shop_shipping = order.get("SHIPPINGSHOP").toString();
                    String shopName_shipping = order.get("SHIPPINGSHOPNAME").toString();
                    if ("1".equals(typeOfOrg))
                    {
                        if (shop_create!=null&&shop_create.trim().isEmpty()==false)
                        {
                            str_error = "需要下单机构("+shop_create+shopName_create+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }
                    else if ("3".equals(typeOfOrg))
                    {
                        if (shop_shipping!=null&&shop_shipping.trim().isEmpty()==false)
                        {
                            str_error = "需要配送机构("+shop_shipping+shopName_shipping+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }
                    else
                    {
                        if (shop_mach!=null&&shop_mach.trim().isEmpty()==false)
                        {
                            str_error = "需要生产机构("+shop_mach+shopName_mach+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }

                    if (!isCheckOrg)
                    {
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        res.setServiceDescription(str_error);
                        HelpTools.writelog_waimai(logStr+str_error+"，不能退单！单号OrderNO=" + reqRequest.getOrderNo()
                                + " 数据库中下单门店=" + shop_create + " 订单状态status=" + orderStatus);
                        return;
                    }
                }

            }

        }
        
        
        List<String> params = new ArrayList<>();
        params.add(eId_para);
        StringBuilder orgSql = new StringBuilder("SELECT ORG_FORM FROM DCP_ORG WHERE EID = ? AND ORGANIZATIONNO IN (");
        if (StringUtils.isNotBlank(machShop)) {
            params.add(machShop);
            orgSql.append("?,");
        }
        if (StringUtils.isNotBlank(shippingShop)) {
            params.add(shippingShop);
            orgSql.append("?,");
        }
        if (params.size() != 1) {
            List<Map<String, Object>> maps = this.doQueryData(orgSql.deleteCharAt(orgSql.length() - 1).append(")").toString(), params.toArray(new String[params.size()]));
            if (!CollectionUtils.isEmpty(maps)) {
                for (Map<String, Object> map : maps) {
                    if ("0".equals(map.get("ORG_FORM"))) {
                        isNeedRequsetERP = true;
                        break;
                    }
                }
            }

        }
         
        //不需要调用ERP直接返回成功
        if (!isNeedRequsetERP) {
            HelpTools.writelog_waimai(logStr+"该订单生产/配送门店都不是总部。无须调用ERP退单询问接口!");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            //调用ERP
            // t100req中的payload对象
            JSONObject payload = new JSONObject();
            // 自定义payload中的json结构
            JSONObject std_data = new JSONObject();
            JSONObject parameter = new JSONObject();

            JSONArray request = new JSONArray();
            JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
            JSONArray request_detail = new JSONArray(); // 商品单身

            JSONArray pay_detail = new JSONArray(); // 付款单身

            header.put("refundtype", reqRequest.getRefundType());//0.（全退），1.（部分退）
            header.put("o_companyNO", eId_para);
            header.put("customerNO", " ");//操作类型（0-新建，1-修改）20191212添加
            header.put("o_organizationNO", shop);
            header.put("o_shopNO", shop);
            header.put("orderNO", reqRequest.getOrderNo());
            header.put("loadDocType", loadDocType);
            header.put("o_opNO", opNo);
            header.put("o_opName", opName);
            header.put("memo", "");
            header.put("version", "3.0");
            header.put("departNo", departNo);

            if (reqRequest.getRefundType().equals("1") && req.getRequest().getGoods() != null) {
                for (DCP_OrderReturnQuery_OpenReq.Level2Good map : reqRequest.getGoods()) {
                    //部分退单身
                    // 获取单身数据并赋值
                    JSONObject body = new JSONObject(); // 存一笔单身
                    body.put("seq", map.getItem());
                    body.put("item_no", map.getPluNo());
                    body.put("sUnit", map.getsUnit());
                    body.put("feature_no", map.getFeatureNo());
                    body.put("qty", map.getQty());
                    body.put("oriprice", map.getOldPrice());
                    body.put("price", map.getPrice());
                    body.put("amount", map.getAmt());
                    body.put("packagetype", map.getPackageType());
                    body.put("packagemitem", map.getPackageMitem());
                    body.put("gift", map.getGift());
                    request_detail.put(body);
                }

            }
            header.put("request_detail", request_detail);
            request.put(header);
            parameter.put("request", request);
            std_data.put("parameter", parameter);
            payload.put("std_data", std_data);
            String str = payload.toString();// 将json对象转换为字符串

            String erpServiceName = "orderrefund.request";
            HelpTools.writelog_waimai(logStr+"调用ERP接口("+erpServiceName+")请求传入参数："+str);
            
            
            // region 写下日志
    		
    		List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

    		orderStatusLog onelv1 = new orderStatusLog();

    		onelv1.setLoadDocType(order.get("LOADDOCTYPE") == null ? "" : order.get("LOADDOCTYPE").toString());
    		onelv1.setChannelId(order.get("CHANNELID") == null ? "" : order.get("CHANNELID").toString());

    		onelv1.setNeed_callback("N");
    		onelv1.setNeed_notify("N");

    		onelv1.seteId(eId_para);

    		onelv1.setOpNo(opNo);
    		onelv1.setOpName(opName);
    		onelv1.setOrderNo(reqRequest.getOrderNo());
    		onelv1.setLoadDocBillType("");
    		onelv1.setLoadDocOrderNo("");

    		String statusType = "99";// 其他状态
    		String updateStaus = "99";// 订单修改

    		onelv1.setStatusType(statusType);
    		onelv1.setStatus(updateStaus);
    		
    		String statusName = "退单询问";
    		String statusTypeName = "其他状态";
    		onelv1.setStatusTypeName(statusTypeName);
    		onelv1.setStatusName(statusName);

    		String memo_s = "";
    		memo_s += statusTypeName + "-->" + statusName + "<br>";
    		// endregion
            
            
            
            String resbody = "";
            try {
                resbody = HttpSend.Send(str, erpServiceName, reqRequest.geteId(), reqRequest.getShopId(), reqRequest.getShopId(), reqRequest.getOpNo());
                HelpTools.writelog_waimai(logStr+"调用ERP接口("+erpServiceName+")返回参数："+resbody);
                String ss = "";
                if (Check.Null(resbody) || resbody.isEmpty()) {
                    ss = "调用ERP接口" + erpServiceName + "返回为空！";
                    
                    memo_s +=statusName+"失败(返回为空)";
    				onelv1.setMemo(memo_s);
    				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    				onelv1.setUpdate_time(updateDatetime);
    				orderStatusLogList.add(onelv1);
    				
    				StringBuilder errorMessage = new StringBuilder();
    				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
    				if (nRet_s)
    				{
    					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" +reqRequest.getOrderNo() );
    				} else
    				{
    					HelpTools.writelog_waimai(
    							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + reqRequest.getOrderNo());
    				}
                	
                    
                    
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, ss);
                }
                JSONObject jsonres = new JSONObject(resbody);
                JSONObject std_data_res = jsonres.getJSONObject("std_data");
                JSONObject execution_res = std_data_res.getJSONObject("execution");

                String code = execution_res.get("code").toString();

                String description = "";
                if (!execution_res.isNull("description")) {
                    description = execution_res.get("description").toString();
                }

                if (code.equals("0")) {
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");
                    
                    memo_s +=statusName+"成功";
    				onelv1.setMemo(memo_s);
    				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    				onelv1.setUpdate_time(updateDatetime);
    				orderStatusLogList.add(onelv1);
    				
    				StringBuilder errorMessage = new StringBuilder();
    				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
    				if (nRet_s)
    				{
    					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" +reqRequest.getOrderNo() );
    				} else
    				{
    					HelpTools.writelog_waimai(
    							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + reqRequest.getOrderNo());
    				}
                    
                    
                } else {
                    ss = "ERP返回错误信息:" + code + "," + description;
                    
                    memo_s +=statusName+"失败("+ss+")";
    				onelv1.setMemo(memo_s);
    				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    				onelv1.setUpdate_time(updateDatetime);
    				orderStatusLogList.add(onelv1);
    				
    				StringBuilder errorMessage = new StringBuilder();
    				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
    				if (nRet_s)
    				{
    					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" +reqRequest.getOrderNo() );
    				} else
    				{
    					HelpTools.writelog_waimai(
    							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + reqRequest.getOrderNo());
    				}
                    
                    
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, ss);
                }


            } catch (Exception e) {
            	 
                memo_s +=statusName+"异常("+e.getMessage()+")";
				onelv1.setMemo(memo_s);
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);
				
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" +reqRequest.getOrderNo() );
				} else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + reqRequest.getOrderNo());
				}
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
            }
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderReturnQuery_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderReturnQuery_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderReturnQuery_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderReturnQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder();
        DCP_OrderReturnQuery_OpenReq.Level1Elm request = req.getRequest();
        if (request == null) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请求节点request不存在");
        }
        /*if (StringUtils.isBlank(request.geteId())) {
            errMsg.append("企业ID(eId)不可为空值, ");
        }
        if (StringUtils.isBlank(request.getShopId())) {
            errMsg.append("操作门店ID(shopId)不可为空值, ");
        }
        if (StringUtils.isBlank(request.getOpNo())) {
            errMsg.append("操作员编号(opNo)不可为空值, ");
        }
        if (StringUtils.isBlank(request.getOpName())) {
            errMsg.append("操作员名称(opName)不可为空值, ");
        }*/
        if (StringUtils.isBlank(request.getOrderNo())) {
            errMsg.append("订单单号(orderNo)不可为空值, ");
            isFail = true;
        }
       /* if (StringUtils.isBlank(request.getLoadDocType())) {
            errMsg.append("订单渠道类型(loadDocType)不可为空值, ");
        }
        if (StringUtils.isBlank(request.getChannelId())) {
            errMsg.append("订单渠道编码(channelId)不可为空值, ");
        }*/
        if (StringUtils.isBlank(request.getRefundType()) || !(request.getRefundType().equals("0") || request.getRefundType().equals("1"))) {
            errMsg.append("退单类型(refundType)不可为空值或取值不在[0,1]中, ");
            isFail = true;
        } else {
            if ("1".equals(request.getRefundType())) {
                List<DCP_OrderReturnQuery_OpenReq.Level2Good> goodList = request.getGoods();
                if (CollectionUtils.isEmpty(goodList)) {
                    errMsg.append("退单商品列表(goods)不可为空值, ");
                    isFail = true;
                }
            }
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_OrderReturnQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderReturnQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_OrderReturnQuery_OpenRes getResponseType() {
        return new DCP_OrderReturnQuery_OpenRes();
    }


    /**
     * 判断下有没有上传给ERP
     *
     * @param companyNo
     * @param loadDocType
     * @param orderNo
     * @return
     * @throws Exception
     */
    private boolean IsUploadErp(String companyNo, String loadDocType, String orderNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from tv_order");
        sqlbuf.append(" where companyno='" + companyNo + "'  and LOAD_DOCTYPE='" + loadDocType + "' and orderno='" + orderNo + "'");

        try {
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sqlbuf.toString(), null);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                String process_status = getQDataDetail.get(0).get("PROCESS_STATUS").toString();
                if (process_status.equals("Y")) {
                    return true;
                }

            }
        } catch (Exception e) {

        }

        return false;
    }


}
