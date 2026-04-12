package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.dsc.spos.model.ApiUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderRefundAgreeOrRejectReq;
import com.dsc.spos.json.cust.req.DCP_OrderRefundAgreeOrRejectReq.levelGoods;
import com.dsc.spos.json.cust.req.DCP_OrderRefundReq;
import com.dsc.spos.json.cust.req.DCP_OrderRefundReq.levelPay;
import com.dsc.spos.json.cust.req.DCP_StockUnlock_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderRefundRes;
import com.dsc.spos.json.cust.res.DCP_OrderRefundRes.Card;
import com.dsc.spos.json.cust.res.DCP_StockUnlock_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.JindieGoodsDetail;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;

public class DCP_OrderRefund extends SPosAdvanceService<DCP_OrderRefundReq, DCP_OrderRefundRes>
{
    
    String loadDocType ="";
    String channelId ="";
    String refundOrderNo_para = "";//晓柚渠道，退订单号是有他们传入
    String deliveryType = "";//晓柚渠道，退订物流类型是有他们传入
    String deliveryNo = "";//晓柚渠道，退订物流单号是有他们传入
    private List<Map<String,Object>> stockSyncList = new ArrayList<>();//用于记录同步三方库存的
    @Override
    protected void processDUID(DCP_OrderRefundReq req, DCP_OrderRefundRes res) throws Exception
    {
        //
        res.setDatas(res.new level1());
        res.getDatas().setVipDatas(res.new level2());
        
        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(req);
        String logStartStr = "【调用DCP_OrderRefund接口】";
        HelpTools.writelog_waimai(logStartStr+"请求json="+reqJson);
        // TODO Auto-generated method stub
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
        
        req.seteId(eId_para);
        req.getRequest().seteId(eId_para);
        //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
        String refundType = req.getRequest().getRefundType();
        if(refundType==null||refundType.trim().length()<1){
        	refundType="0";
        	req.getRequest().setRefundType(refundType);
        }
        String orderNo = req.getRequest().getOrderNo();
        loadDocType = req.getRequest().getLoadDocType();
        channelId = req.getRequest().getChannelId();
        //重新获取partnerMember	
//        String sql_partner = "select nvl(PARTNERMEMBER,'') AS PARTNERMEMBER  from DCP_ECOMMERCE where eid='"+eId_para+"' and CHANNELID='"+channelId+"' ";
//        List<Map<String, Object>> getQPartnerMember = this.doQueryData(sql_partner, null);
//        if(null!=getQPartnerMember && getQPartnerMember.size()>0)
//        {
//            req.getRequest().setPartnerMember(getQPartnerMember.get(0).get("PARTNERMEMBER").toString());
//        }
        
        String refundBdate = req.getRequest().getRefundBdate();
        //前端传的不准，后端自己处理吧
        req.getRequest().setRefundDatetime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        String refundDatetime = req.getRequest().getRefundDatetime();
        if(refundDatetime==null||refundDatetime.isEmpty())
        {
            refundDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        String tot_amt = req.getRequest().getTot_amt();
        //已提货商品退单处理  0：退钱且退货,1：退钱不退货
        //0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单） 1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
        String pickGoodsRefundType = req.getRequest().getPickGoodsRefundType();
        String refundReason = req.getRequest().getRefundReason();
        String refundReasonNo = req.getRequest().getRefundReasonNo();
        String refundReasonName = req.getRequest().getRefundReasonName();
        String invOperateType = req.getRequest().getInvOperateType();
        
        /*************非必传的节点******************/
        String shopId = req.getRequest().getShopId();//操作门店ID
        String opNo = req.getRequest().getOpNo();
        String opName = req.getRequest().getOpName();
        
        List<levelPay> paylist=req.getRequest().getPay();
        //校验paylist合法性
        if (paylist!=null&&paylist.size()>0)
        {
        	for (levelPay lPay : paylist)
        	{
        	    if (lPay.getFuncNo()!=null)
                {
                    //"301".equals(lPay.getFuncNo()) 这种写法也可以
                    if (lPay.getFuncNo().equals("301")||lPay.getFuncNo().equals("302")||lPay.getFuncNo().equals("304"))
                    {
                        if("qimai".equals(req.getRequest().getPartnerMember()))
                        {
                            if(Check.Null(lPay.getPayChannelCode()))
                            {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "payChannelCode不可未空");
                            }
                        }
                    }
                }

        	}
        }
        List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goodslist=req.getRequest().getGoods();
        if (goodslist==null)
        {
            goodslist = new ArrayList<>();
        }
        String machineNo = req.getRequest().getMachineNo();
        String workNo = req.getRequest().getWorkNo();
        String squadNo = req.getRequest().getSquadNo();
        
        StringBuffer errorInfo = new StringBuffer();
        
        
        
        
        //检查下单据可不可以退
        String sql = "select * from dcp_order where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】单号orderNo="+orderNo+"  查询语句："+sql);
        List<Map<String, Object>> getQHead = this.doQueryData(sql, null);
        if(getQHead==null||getQHead.isEmpty())
        {
            errorInfo.append("该订单不存在！");
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询完成,单号orderNo="+orderNo+"该订单不存在！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        String status = getQHead.get(0).get("STATUS").toString();
        String headOrderNo = getQHead.get(0).get("HEADORDERNO")==null?"":getQHead.get(0).get("HEADORDERNO").toString();
        loadDocType=getQHead.get(0).get("LOADDOCTYPE").toString();
        channelId=getQHead.get(0).get("CHANNELID").toString();
        if (opName==null||opName.trim().isEmpty())
        {
            opName ="渠道("+loadDocType+")调用";
        }
        
        String isRefundDeliverAmt=req.getRequest().getIsRefundDeliverAmt();
        Map<String,Object> recalculateMap=new HashMap<String,Object>();
        {
        	recalculateMap=doRecalculateReq(getQHead.get(0), eId_para, orderNo, refundType, pickGoodsRefundType,req.getRequest().getTot_amt(),isRefundDeliverAmt, goodslist);
        	if(recalculateMap!=null){
        		String newrefundType=recalculateMap.get("refundType")==null?"":recalculateMap.get("refundType").toString();
        		if(newrefundType.length()>0){
        			req.getRequest().setRefundType(newrefundType);
        			refundType=newrefundType;
        		}
        	}
        }
        
        
        String memberpayno = getQHead.get(0).get("MEMBERPAYNO").toString();
        String payAmt = getQHead.get(0).get("PAYAMT").toString();
        String isSellCredit = getQHead.get(0).get("SELLCREDIT").toString();
        String customerNo = getQHead.get(0).get("CUSTOMER").toString();
        String belfirm =  getQHead.get(0).get("BELFIRM").toString();
        deliveryType = getQHead.get(0).getOrDefault("DELIVERYTYPE","").toString();
        deliveryNo = getQHead.get(0).getOrDefault("DELIVERYNO","").toString();
        if (orderLoadDocType.XIAOYOU.equals(loadDocType))
        {
            refundOrderNo_para = req.getRequest().getRefundOrderNo();
            deliveryType = req.getRequest().getDeliveryType();
            deliveryNo = req.getRequest().getDeliveryNo();
            if (deliveryType==null)
            {
                deliveryType = "";
            }
            if (deliveryNo==null)
            {
                deliveryNo = "";
            }
            if (refundOrderNo_para==null||refundOrderNo_para.trim().isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该渠道类型("+loadDocType+")退订单号节点refundOrderNo不可为空！");
            }
            sql = "";
            sql = "select * from dcp_order where eid='"+eId_para+"' and orderno='"+refundOrderNo_para+"' ";
            List<Map<String, Object>> getQHead_refund = this.doQueryData(sql, null);
            if(getQHead_refund!=null&&!getQHead_refund.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该渠道类型("+loadDocType+")传入的退订单号("+refundOrderNo_para+")已存在！");
            }
            
        }
        if (tot_amt==null||tot_amt.trim().isEmpty())
        {
            tot_amt = getQHead.get(0).get("TOT_AMT").toString();
            req.getRequest().setTot_amt(tot_amt);
        }
        else
        {
            try
            {
                BigDecimal tot_amt_b = new BigDecimal(tot_amt);
                if (tot_amt_b.compareTo(BigDecimal.ZERO)==1)
                {
                
                }
                else
                {
                    tot_amt = getQHead.get(0).get("TOT_AMT").toString();
                    req.getRequest().setTot_amt(tot_amt);
                }
                
                
            }
            catch (Exception e)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "退款总金额tot_amt传参异常:"+e.getMessage());
            }
        }
        
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询完成,单号orderNo="+orderNo+" 状态status="+status);
        if(status.equals("3"))
        {
            errorInfo.append("该订单状态为已取消！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        if(status.equals("12"))
        {
            errorInfo.append("订单状态为已退单！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        
        String shopId_db = getQHead.get(0).getOrDefault("SHOP","").toString();
        String loadDocBillType = getQHead.get(0).get("LOADDOCBILLTYPE").toString();
        String loadDocOrderNo = getQHead.get(0).get("LOADDOCORDERNO").toString();
        String shippingShopNo = getQHead.get(0).get("SHIPPINGSHOP").toString();
        String INVOPERATETYPE = "";
        try
        {
            INVOPERATETYPE = getQHead.get(0).get("INVOPERATETYPE").toString();//发票记录类型：0-开立 1-作废 2-折让
            
        } catch (Exception e)
        {
            // TODO: handle exception
        }
        boolean isCallPosInvoiceRefund = false;//是否调用POS发票作废/折让接口
        
        //【ID1034999】【嘉华3.0】团购打折功能-dcp服务  by jinzma 20230804
        String groupBuying = getQHead.get(0).get("GROUPBUYING").toString();
        
        
        //饿了么，美团，京东到家外卖暂不支持主动发起退款,走同意/拒绝接口
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型"+loadDocType+"暂不支持");
        }
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
        {
            if (shopId!=null&&!shopId.trim().isEmpty())
            {
                if (!shopId_db.equals(shopId))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型"+loadDocType+"(线下门店订单)暂不支持异店退货！");
                }
            }
            if (isHasPartOrderToSale(eId_para,orderNo))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
            }
            
        }
        
        if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))
        {
            if (isHasPartOrderToSale(eId_para,orderNo))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
            }
        }
        
        if(loadDocType.equals(orderLoadDocType.QIMAI)){
            String appType=req.getApiUser()==null?"":req.getApiUser().getAppType();
            if(!"QIMAI".equals(appType)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型"+loadDocType+"(企迈订单)限企迈平台退单!");
            }
        }
        if (orderLoadDocType.OWNCHANNEL.equals(loadDocType))
        {
            if (req.getApiUser()==null)
            {
                ApiUser apiUser = PosPub.getApiUserByChannelId(this.dao,req.getRequest().geteId(),req.getRequest().getChannelId());
                if (apiUser==null)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取渠道编码:"+req.getRequest().getChannelId()+"对应的接口账号为空！");
                }
                req.setApiUser(apiUser);
                req.setApiUserCode(apiUser.getUserCode());
            }
        }
        
        
        //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
        if(refundType.equals("0"))
        {
            if(pickGoodsRefundType==null||pickGoodsRefundType.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点pickGoodsRefundType不可为空！");
            }
            if (!orderLoadDocType.MINI.equals(loadDocType)&&pickGoodsRefundType.equals("1"))//
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单【退钱不退货】暂不支持");
            }
            
            
            if(INVOPERATETYPE!=null&&INVOPERATETYPE.equals("0"))
            {
                //查询下参数AreaType
                String AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId_para, "", "AreaType");
                if(AreaType!=null&&AreaType.equals("TW")==true)
                {
                    boolean checkInvoiceRefund = false;
                    if (invOperateType==null||invOperateType.trim().isEmpty())
                    {
                        errorInfo.append("退单发票操作类型invOperateType不能为空，");
                        checkInvoiceRefund = true;
                    }
                    if (refundReasonNo==null||refundReasonNo.trim().isEmpty())
                    {
                        errorInfo.append("退单理由码refundReasonNo不能为空，");
                        checkInvoiceRefund = true;
                    }
                    if (refundReasonName==null||refundReasonName.trim().isEmpty())
                    {
                        errorInfo.append("退单理由码名称refundReasonName不能为空，");
                        checkInvoiceRefund = true;
                    }
                    if (refundReason==null||refundReason.trim().isEmpty())
                    {
                        errorInfo.append("退单原因描述refundReason不能为空，");
                        checkInvoiceRefund = true;
                    }
                    
                    if(checkInvoiceRefund)
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorInfo.toString());
                    }
                    
                    isCallPosInvoiceRefund = true;
                    
                }
            }
            
            
            
            
            UptBean up1 = new UptBean("DCP_ORDER");
            up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
            up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
            
            //更新updatetime
            up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            up1.addUpdateValue("LASTREFUNDTIME", new DataValue(refundDatetime, Types.VARCHAR));
            
            boolean otherChannelRes = true;//退款调用相应渠道接口，成功标识
            StringBuffer otherChannelError = new StringBuffer("");
            boolean isWechatOrder = false;//是否手机商城渠道类型订单
            
            if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))//商城的需要调用退款
            {
                isWechatOrder = true;
                
                try
                {
                    
                    String refundReasonCode_wechat = "4";//退单原因  不传默认0-未知、1-未付款自主取消、2-超时未支付、3-商家拒单、4-用户申请退单
                    String refundReasonDesc = refundReason;//退单原因描述
                    if(refundReasonDesc==null||refundReasonDesc.trim().isEmpty())
                    {
                        refundReasonDesc = "";
                    }
                    JSONObject objReq = new JSONObject();
                    objReq.put("orderNo", orderNo);
                    objReq.put("refund", 1);
                    objReq.put("refundReason", refundReasonCode_wechat);
                    objReq.put("refundReasonDesc", refundReasonDesc);
                    objReq.put("goods", req.getRequest().getGoods());
                    objReq.put("pickGoodsRefundType", pickGoodsRefundType);
                    objReq.put("refundType", refundType);
                    objReq.put("tot_amt", tot_amt);
                    objReq.put("isRefundDeliverAmt", isRefundDeliverAmt);
                    
                    String request = objReq.toString();
                    String microMarkServiceName = "OrderRefund";
                    
                    String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);
                    JSONObject json = new JSONObject(result);
                    try
                    {
                        
                        String success = json.get("success").toString();
                        String serviceDescription = json.get("serviceDescription").toString();
                        if(success.equals("true")|| serviceDescription.equals("訂單異常或已退款")||serviceDescription.equals("订单异常或已退款"))
                        {
                        
                        }
                        else
                        {
                            otherChannelRes = false;
                            otherChannelError.append(serviceDescription);
                        }
                    }
                    catch (Exception e)
                    {
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    otherChannelRes = false;
                    otherChannelError.append(e.getMessage());
                }
            }
            else if (loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL)) //winpos门店订单
            {
                //控制下，如果整单退的时候，原单使用了会员支付，没有传付款方式不让退
                if (paylist==null||paylist.isEmpty())
                {
                    String sql_pay = " select * from DCP_ORDER_PAY_DETAIL where SOURCEBILLTYPE='Order' and EID='"+eId_para+"' and LOADDOCTYPE='"+loadDocType+"' and SOURCEBILLNO='"+orderNo+"' ";
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】sql语句:"+sql_pay+",单号orderNo="+orderNo);
                    List<Map<String, Object>> getPaylist = this.doQueryData(sql_pay, null);
                    if(getPaylist==null||getPaylist.isEmpty())
                    {
                        errorInfo.append("该订单没有需要退款的支付方式！");
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！");
                        //HelpTools.writelog_fileName("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！", "ScanPayAddLog");
                        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
                    }
                    else
                    {
                        boolean isExistMemberPay = false;//坚持下有没有使用会员支付
                        for (Map<String, Object> payMap : getPaylist)
                        {
                            String funcNo = payMap.getOrDefault("FUNCNO","").toString();
                            if("301".equals(funcNo)||"302".equals(funcNo)||"304".equals(funcNo)||"305".equals(funcNo)||"307".equals(funcNo)||"3011".equals(funcNo)||"3012".equals(funcNo)||"3013".equals(funcNo)||"3014".equals(funcNo))
                            {
                                isExistMemberPay = true;
                                HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",存在会员支付付款数据！");
                                break;
                            }
                        }
                        //控制下，如果整单退的时候，原单使用了会员支付，没有传付款方式不让退
                        if (isExistMemberPay)
                        {
                            errorInfo.append("原订单使用了会员相关支付，退单时需要传入付款记录");
                            HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，单号orderNo="+orderNo+",付款pay节点为空，"+errorInfo.toString());
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorInfo.toString());
                            
                        }
                        
                        
                        
                    }
                }
                
                //尾款处理,这个只是记录付款
                com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
                //这里才会扣款
                com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
                //券列表
                com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();
                
                String partnerMember = req.getRequest().getPartnerMember();
                // 用与企迈会员退款
                String cardno = getQHead.get(0).get("CARDNO").toString();
                
                if (paylist!=null)
                {
                    for (levelPay lPay : paylist)
                    {
                        if (Check.Null(lPay.getFuncNo()) ) lPay.setFuncNo("");
                        
                        BigDecimal p_pay=new BigDecimal(lPay.getPay());
                        BigDecimal p_changed=new BigDecimal(lPay.getChanged());
                        BigDecimal p_extra=new BigDecimal(lPay.getExtra());
                        
                        //pay-changed-extra累加起来
                        BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);
                        p_realpay=p_realpay.setScale(2,RoundingMode.HALF_UP);
                        
                        //券面额
                        BigDecimal faceAmt=p_pay;//.add(p_extra);
                        faceAmt=faceAmt.setScale(2,RoundingMode.HALF_UP);
                        
                        
                        //****会员卡扣款****
                        if (lPay.getFuncNo().equals("301"))
                        {
                            com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempPay.put("payType",lPay.getPayCode());//收款方式代号
                            tempPay.put("payName",lPay.getPayName());//收款方式名称
                            tempPay.put("payAmount",p_realpay);//付款金额
                            tempPay.put("noCode",lPay.getCardNo());//卡号
                            tempPay.put("isCardPay",1);//
                            payslistArray.add(tempPay);
                            
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("partnerMember", lPay.getPayChannelCode());
                            cardlistArray.add(tempCard);
                        }else
                        if (lPay.getFuncNo().equals("302"))//积分扣减
                        {
                            if("qimai".equals(partnerMember)){
                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",lPay.getPayCode());//收款方式代号
                                tempPay.put("payName",lPay.getPayName());//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",lPay.getCardNo());//卡号
                                tempPay.put("isCardPay",0);//
                                payslistArray.add(tempPay);
                            }
                            
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("usePoint",lPay.getDescore());//积分扣减
                            tempCard.put("amount","0");//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("partnerMember", lPay.getPayChannelCode());
                            cardlistArray.add(tempCard);
                        }else
                        if (lPay.getFuncNo().equals("304") || lPay.getFuncNo().equals("305")|| lPay.getFuncNo().equals("307"))//现金券/折扣券
                        {
                            if("qimai".equals(partnerMember)){
                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",lPay.getPayCode());//收款方式代号
                                tempPay.put("payName",lPay.getPayName());//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",lPay.getCardNo());//卡号
                                tempPay.put("isCardPay",0);//
                                payslistArray.add(tempPay);
                                
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",cardno);
                                tempCard.put("amount","0");//0
                                tempCard.put("usePoint","0");//积分扣减
                                tempCard.put("getPoint","0");//
                                tempCard.put("partnerMember", lPay.getPayChannelCode());
                                cardlistArray.add(tempCard);
                            }
                            
                            
                            //【ID1037176】【阿哆诺斯】券退卡功能，订单退单也需要券退卡----服务  by jinzma 20231129
                            if ("1".equals(lPay.getRefundType())){
                                //订单使用券支付时，退券券如何处理（0-原券激活 1-券退到卡上 2-券退到其他支付方式 ）
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",lPay.getRefundCardNo());
                                tempCard.put("amount",lPay.getRefundAmt());
                                tempCard.put("getPoint","0");
                                tempCard.put("partnerMember", lPay.getPayChannelCode());
                                cardlistArray.add(tempCard);
                            }else{
                                //
                                com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCoupon.put("couponCode",lPay.getCardNo());//券号
                                tempCoupon.put("couponType","");//券类型
                                tempCoupon.put("quantity",lPay.getCouponQty());//使用张数
                                tempCoupon.put("faceAmount",faceAmt);//总面额
                                tempCoupon.put("buyAmount",p_pay);//抵账金额
                                tempCoupon.put("partnerMember", lPay.getPayChannelCode());
                                couponlistArray.add(tempCoupon);
                            }
                            
                        }else
                        if (lPay.getFuncNo().equals("3011"))//禄品电影卡
                        {
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("cardType","LPDY");//
                            tempCard.put("cardPwd",lPay.getPassword());//
                            
                            cardlistArray.add(tempCard);
                        }else
                        if (lPay.getFuncNo().equals("3012"))//四威
                        {
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("cardType","SIWEI_CARD");//
                            tempCard.put("cardPwd",lPay.getPassword());//
                            
                            cardlistArray.add(tempCard);
                        }else
                        if (lPay.getFuncNo().equals("3013"))//乐享支付
                        {
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("cardType","LXYS");//
                            tempCard.put("cardPwd",lPay.getPassword());//
                            
                            cardlistArray.add(tempCard);
                        }else
                        if (lPay.getFuncNo().equals("3014"))//聚优福利卡
                        {
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",lPay.getCardNo());
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("cardType","JYFL");//
                            
                            cardlistArray.add(tempCard);
                        }
                        else
                        {
                            com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempPay.put("payType",lPay.getPayCode());//收款方式代号
                            tempPay.put("payName",lPay.getPayName());//收款方式名称
                            tempPay.put("payAmount",p_realpay);//付款金额
                            tempPay.put("noCode",lPay.getCardNo());//卡号
                            tempPay.put("trade_no", lPay.getPaySerNum());
                            tempPay.put("isCardPay",0);//
                            payslistArray.add(tempPay);
                        }
                    }
                }
                
                
                //消费扣款处理
                //if (payslistArray.size()>0 || cardlistArray.size()>0 || couponlistArray.size()>0)
                if (!Check.Null(memberpayno) || cardlistArray.size()>0 || couponlistArray.size()>0)
                {
                    String Yc_Url="";
                    String Yc_Key=req.getApiUserCode();
                    String Yc_Sign_Key=req.getApiUser().getUserKey();
                    Yc_Url =PosPub.getCRM_INNER_URL(eId_para);
                    
                    if(Yc_Url.trim().equals("") || Yc_Key.trim().equals("") ||Yc_Sign_Key.trim().equals(""))
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
                    }
                    
                    com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
                    for (DCP_OrderRefundAgreeOrRejectReq.levelGoods detail: goodslist)
                    {
                        com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        goods.put("goods_id",detail.getPluBarcode());
                        goods.put("goods_name",detail.getPluName());
                        goods.put("price",detail.getPrice());
                        goods.put("quantity",detail.getQty());
                        goods.put("amount",detail.getAmt());
                        goods.put("allowPoint","1");
                        goodslistArray.add(goods);
                    }
                    
                    reqheader.put("orderNo", memberpayno);//需唯一
                    reqheader.put("refundOrderNo", "RE"+orderNo);//新的退款单号
                    reqheader.put("orderAmount", tot_amt);//
                    reqheader.put("pointAmount", tot_amt);//
                    //digiwin  鼎捷    qimai企迈   空为鼎捷
                    reqheader.put("partnerMember", req.getRequest().getPartnerMember());
                    // 0.退订单 1.退销售单                企迈场景使用   固定为0
                    reqheader.put("refundType", "0");
                    
                    reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                    
                    //2021-06-03 消费单CRM_CONSUME 表记录 消费机构不对。
                    // 3.0 退订时退款原路返回， 消费门店也是原来收款门店，  所以这里 orgId 不能写 操作门店，跟不是下订门店和配送门店， 而是实际消费门店（可能是下订门店付款，也可能是配送门店付款）
                    //reqheader.put("orgId", shippingShopNo);//
                    
                    if(!Check.Null(shopId)){
                        reqheader.put("orgId", shopId);//
                    }
                    else{
                        reqheader.put("orgId", shippingShopNo);// shippingShopNo 配送机构， 作为默认值用，其实这么写是不对的，
                    }
                    
                    reqheader.put("oprId", opNo);//
                    reqheader.put("goodsdetail", goodslistArray);
                    reqheader.put("cards", cardlistArray);
                    reqheader.put("coupons", couponlistArray);
                    reqheader.put("payDetail", payslistArray);
                    
                    //
                    String req_sign=reqheader.toString() + Yc_Sign_Key;
                    
                    req_sign=DigestUtils.md5Hex(req_sign);
                    
                    //
                    signheader.put("key", Yc_Key);//
                    signheader.put("sign", req_sign);//md5
                    
                    payReq.put("serviceId", "MemberPayRefund");
                    
                    payReq.put("request", reqheader);
                    payReq.put("sign", signheader);
                    
                    
                    String str = payReq.toString();
                    
                    HelpTools.writelog_waimai("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo);
                    
                    String	resbody = "";
                    
                    //编码处理
                    str=URLEncoder.encode(str,"UTF-8");
                    
                    resbody=HttpSend.Sendcom(str, Yc_Url);
                    
                    HelpTools.writelog_waimai("会员退款接口MemberPayRefund返回："+resbody +",单号orderNo="+orderNo);
                    
                    //获得积分
                    BigDecimal getPoint=new BigDecimal(0);
                    
                    if (resbody.equals("")==false)
                    {
                        com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
                        String serviceDescription="";
                        String serviceStatus="";
                        if(null!=jsonres.get("serviceDescription"))
                        {
                        	serviceDescription=jsonres.get("serviceDescription").toString();
                        }
                        if(null!=jsonres.get("serviceStatus"))
                        {
                        	serviceStatus=jsonres.get("serviceStatus").toString();
                        }
                        
                        //单号重复,直接查询积分
                        if (serviceStatus.equals("900"))
                        {
                            reqheader.clear();
                            signheader.clear();
                            payReq.clear();
                            
                            reqheader.put("refundOrderNo", "RE"+orderNo);
                            req_sign=reqheader.toString() + Yc_Sign_Key;
                            req_sign=DigestUtils.md5Hex(req_sign);
                            
                            //
                            signheader.put("key", Yc_Key);//
                            signheader.put("sign", req_sign);//md5
                            
                            payReq.put("serviceId", "MemberPayRefundQuery");
                            
                            payReq.put("request", reqheader);
                            payReq.put("sign", signheader);
                            
                            str = payReq.toString();
                            
                            HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery请求内容："+str+",单号orderNo="+orderNo);
                            
                            //编码处理
                            str=URLEncoder.encode(str,"UTF-8");
                            
                            resbody=HttpSend.Sendcom(str, Yc_Url);
                            
                            HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery返回："+resbody+",单号orderNo="+orderNo);
                            
                            if (resbody.equals("")==false)
                            {
                                jsonres = JSON.parseObject(resbody);
                                if(null!=jsonres.get("serviceDescription"))
                                {
                                	serviceDescription=jsonres.get("serviceDescription").toString();
                                }
                                if(null!=jsonres.get("serviceStatus"))
                                {
                                	serviceStatus=jsonres.get("serviceStatus").toString();
                                }
//                                serviceDescription=jsonres.get("serviceDescription").toString();
//                                serviceStatus=jsonres.get("serviceStatus").toString();
                                if (jsonres.get("success").toString().equals("true"))
                                {
                                    res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
                                    com.alibaba.fastjson.JSONObject datas=jsonres.getJSONObject("datas");
                                    String JrefundStatus=datas.getString("refundStatus");//0:未退款 1:已转入退款
                                    if (JrefundStatus.equals("0"))
                                    {
                                        otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:退款状态refundStatus=0");
                                        otherChannelRes=false;
                                    }
                                    res.getDatas().setBillNo("RE"+orderNo);

									/*
									com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
									for (int pi = 0; pi < cardsList.size(); pi++)
									{
										//多张卡累加积分
										getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));

										Card card=res.new Card();
										card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
										card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
										card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
										card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
										card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
										card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
										card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
										card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
										card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
										card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
										card.setGetPoint(getPoint.toPlainString());
										card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
										card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));

										res.getDatas().getVipDatas().getCardsInfo().add(card);

										//
										res.getDatas().getVipDatas().setAmount(card.getAmount_after());
										res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
										res.getDatas().getVipDatas().setMemberId("");
										res.getDatas().getVipDatas().setPoints(card.getPoint_after());
									}
									 */
                                
                                }
                                else
                                {
                                    otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:" +serviceDescription );
                                    otherChannelRes=false;
                                }
                            }
                            else
                            {
                                otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:");
                                otherChannelRes=false;
                            }
                        }
                        else
                        {
                            if (jsonres.get("success").toString().equals("true"))
                            {
                                res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
                                
                                com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                for (int pi = 0; pi < cardsList.size(); pi++)
                                {
                                    //多张卡累加积分
                                    getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
                                    
                                    Card card=res.new Card();
                                    card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
                                    card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
                                    card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
                                    card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
                                    card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
                                    card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
                                    card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
                                    card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
                                    card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
                                    card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
                                    card.setGetPoint(getPoint.toPlainString());
                                    card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
                                    card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));
                                    
                                    res.getDatas().getVipDatas().getCardsInfo().add(card);
                                    
                                    //
                                    res.getDatas().getVipDatas().setAmount(card.getAmount_after());
                                    res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
                                    res.getDatas().getVipDatas().setMemberId("");
                                    res.getDatas().getVipDatas().setPoints(card.getPoint_after());
                                    
                                    res.getDatas().setBillNo("RE"+orderNo);
                                }
                            }
                            else
                            {
                                otherChannelError.append("调用会员退款查询接口MemberPayRefund失败:" + serviceDescription);
                                otherChannelRes= false;
                            }
                        }
                    }
                    else
                    {
                        otherChannelError.append("调用会员退款接口MemberPayRefund失败:");
                        otherChannelRes= false;
                    }
                    
                }
                
                
            }
            
            else if(loadDocType.equals(orderLoadDocType.YOUZAN))
            {
                String appType1=req.getApiUser()==null?"":req.getApiUser().getAppType();
                //有赞发起的退款，不需要再回调有赞
                if(!"YOUZAN".equals(appType1)){
                    YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
                    try{
                        Map<String, Object> otherMap = new HashMap<String, Object>();
                        otherMap.put("PAYAMT", getQHead.get(0).getOrDefault("PAYAMT","0").toString());//操作人
                        if(refundReason!=null&&refundReason.trim().length()>0){
                            otherMap.put("DESC", refundReason);
                        }else if(refundReasonName!=null&&refundReasonName.trim().length()>0){
                            otherMap.put("DESC", refundReasonName);
                        }else{
                            otherMap.put("DESC", "退单");
                        }
                        JsonBasicRes thisRes=new JsonBasicRes();
                        thisRes=ycb.refundSeller(eId_para, orderNo, shopId,otherMap,null);
                        if(!thisRes.isSuccess()){
                            otherChannelRes = false;
                            otherChannelError.append(thisRes.getServiceDescription());
                        }
                    }catch (Exception e) {
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }
                }
                
            }
            else if(loadDocType.equals(orderLoadDocType.WAIMAI))
            {
                String ReturnReviewMode = PosPub.getPARA_SMS(this.dao,eId_para,"","ReturnReviewMode");// 0：仅门店POS退   1：仅订单中台退  2：门店POS和订单中台都可以退货
                if (ReturnReviewMode==null||ReturnReviewMode.isEmpty())
                {
                    ReturnReviewMode = "0";//默认0
                }
                HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，对应参数小程序外卖单退单审核模式ReturnReviewMode="+ReturnReviewMode+"(0：仅门店POS退，1：仅订单中台退，2：门店POS和订单中台都可以退货),订单号orderNo="+orderNo);
                String Yc_Key = "";
                String Yc_Sign_Key = "";
                String Yc_Url = "";
                String crmPayUrl = "";
                if (req.getApiUser()==null||Check.Null(req.getApiUser().getUserCode())||Check.Null(req.getApiUser().getUserKey()))
                {
                    //HelpTools.writelog_waimai("该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号),订单号orderNo="+orderNo);
                    //HelpTools.writelog_fileName("该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号),订单号orderNo="+orderNo, "ScanPayAddLog");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号)！"+otherChannelError.toString());
                    if ("0".equals(ReturnReviewMode))
                    {
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退,订单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退,订单号orderNo="+orderNo, "ScanPayAddLog");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退！"+otherChannelError.toString());
                    }
                    //获取下接口账号
                    String sql_channel = "select A.APPID,A.APPNO, A.APPTYPE,B.USERCODE,B.USERKEY from "
                            + " crm_channel a inner join crm_apiuser b on a.eid=b.eid and a.channelid=b.channelid and a.appno=b.apptype"
                            + " where a.eid='"+eId_para+"' and a.channelid='"+channelId+"' and a.appno='"+loadDocType+"'";
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+",在中台退单，获取渠道ID="+channelId+",对应的接口账号查询sql:"+sql_channel+",订单号orderNo="+orderNo);
                    List<Map<String, Object>> getQDataChannel=this.doQueryData(sql_channel, null);
                    if (getQDataChannel==null||getQDataChannel.isEmpty())
                    {
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，在中台退单，获取渠道ID="+channelId+",对应的接口账号查询为空,订单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，在中台退单，获取渠道ID="+channelId+",对应的接口账号查询为空,订单号orderNo="+orderNo, "ScanPayAddLog");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，在中台退单，获取渠道编码("+channelId+")对应的接口账号为空！"+otherChannelError.toString());
                    }
                    Yc_Key = getQDataChannel.get(0).get("USERCODE").toString();
                    Yc_Sign_Key = getQDataChannel.get(0).get("USERKEY").toString();
                }
                else
                {
                    if ("1".equals(ReturnReviewMode))
                    {
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退,订单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退,订单号orderNo="+orderNo, "ScanPayAddLog");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退！"+otherChannelError.toString());
                    }
                    Yc_Key = req.getApiUser().getUserCode();
                    Yc_Sign_Key = req.getApiUser().getUserKey();
                }
                Yc_Url = PosPub.getCRM_INNER_URL(eId_para);
                crmPayUrl = PosPub.getPAY_INNER_URL(eId_para);
                if (Yc_Url==null||Yc_Url.trim().isEmpty()||crmPayUrl==null||crmPayUrl.trim().isEmpty()) {
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+"退单CrmUrl、Mobile_Url移动支付接口参数未设置,单号orderNo="+orderNo);
                    HelpTools.writelog_fileName("该渠道类型"+loadDocType+"退单CrmUrl、Mobile_Url移动支付接口参数未设置,单号orderNo="+orderNo, "ScanPayAddLog");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "Mobile_Url移动支付接口参数未设置!");
                }
                
                //查询下付款
                String sql_pay = " select * from DCP_ORDER_PAY_DETAIL where SOURCEBILLTYPE='Order' and EID='"+eId_para+"' and LOADDOCTYPE='"+loadDocType+"' and SOURCEBILLNO='"+orderNo+"' ";
                HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】sql语句:"+sql_pay+",单号orderNo="+orderNo);
                List<Map<String, Object>> getPaylist = this.doQueryData(sql_pay, null);
                if(getPaylist==null||getPaylist.isEmpty())
                {
                    errorInfo.append("该订单没有需要退款的支付方式！");
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！");
                    HelpTools.writelog_fileName("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！", "ScanPayAddLog");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
                }
                else
                {
                    //尾款处理,这个只是记录付款
                    com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
                    //这里才会扣款
                    com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
                    //券列表
                    com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();
                    
                    String partnerMember = req.getRequest().getPartnerMember();
                    // 用与企迈会员退款
                    String cardno = getQHead.get(0).get("CARDNO").toString();
                    String memberPayNo_origin = "";//原退款单号
                    
                    boolean isMobilePay = false;//移动支付退款
                    String refundPayReqStr = "";
                    for (Map<String, Object> payMap : getPaylist)
                    {
                        boolean isMemberPay = false;
                        String pay = payMap.getOrDefault("PAY","0").toString();
                        String changed = payMap.getOrDefault("CHANGED","0").toString();
                        String extra = payMap.getOrDefault("EXTRA","0").toString();
                        BigDecimal p_pay=new BigDecimal(pay);
                        BigDecimal p_changed=new BigDecimal(changed);
                        BigDecimal p_extra=new BigDecimal(extra);
                        
                        //pay-changed-extra累加起来
                        BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);
                        p_realpay=p_realpay.setScale(2,RoundingMode.HALF_UP);
                        
                        //券面额
                        BigDecimal faceAmt=p_pay;//.add(p_extra);
                        faceAmt=faceAmt.setScale(2,RoundingMode.HALF_UP);
                        
                        String funcNo = payMap.getOrDefault("FUNCNO","").toString();
                        String payCode = payMap.getOrDefault("PAYCODE","").toString();
                        String payName = payMap.getOrDefault("PAYNAME","").toString();
                        String cardNo = payMap.getOrDefault("CARDNO","").toString();
                        String descore_str = payMap.getOrDefault("DESCORE","0").toString();
                        double descore = new BigDecimal(descore_str).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        
                        String couponQty_str = payMap.getOrDefault("COUPONQTY","0").toString();
                        double couponQty = new BigDecimal(couponQty_str).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        String paysernum = payMap.getOrDefault("PAYSERNUM","").toString();
                        String payChannelCode=payMap.getOrDefault("PAYCHANNELCODE","").toString();
                        
                        //****会员卡扣款****
                        if (funcNo.equals("301"))
                        {
                            if (!paysernum.isEmpty())
                            {
                                memberPayNo_origin = paysernum;
                            }
                            isMemberPay = true;
                            com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempPay.put("payType",payCode);//收款方式代号
                            tempPay.put("payName",payName);//收款方式名称
                            tempPay.put("payAmount",p_realpay);//付款金额
                            tempPay.put("noCode",cardNo);//卡号
                            tempPay.put("isCardPay",1);//
                            payslistArray.add(tempPay);
                            
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",cardNo);
                            tempCard.put("amount",p_realpay);//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("partnerMember",payChannelCode);
                            cardlistArray.add(tempCard);
                            continue;
                        }
                        else if (funcNo.equals("302"))//积分扣减
                        {
                            if (!paysernum.isEmpty())
                            {
                                memberPayNo_origin = paysernum;
                            }
                            isMemberPay = true;
                            if("qimai".equals(partnerMember)){
                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",payCode);//收款方式代号
                                tempPay.put("payName",payName);//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",cardNo);//卡号
                                tempPay.put("isCardPay",0);//
                                payslistArray.add(tempPay);
                            }
                            
                            //
                            com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCard.put("cardNo",cardNo);
                            tempCard.put("usePoint",descore);//积分扣减
                            tempCard.put("amount","0");//0只处理积分
                            tempCard.put("getPoint","0");//0只处理积分
                            tempCard.put("partnerMember",payChannelCode);
                            cardlistArray.add(tempCard);
                            continue;
                        }
                        else if (funcNo.equals("304") || funcNo.equals("305")|| funcNo.equals("307"))//现金券/折扣券
                        {
                            if (!paysernum.isEmpty())
                            {
                                memberPayNo_origin = paysernum;
                            }
                            isMemberPay = true;
                            if("qimai".equals(partnerMember)){
                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",payCode);//收款方式代号
                                tempPay.put("payName",payName);//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",cardNo);//卡号
                                tempPay.put("isCardPay",0);//
                                payslistArray.add(tempPay);
                                
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",cardno);
                                tempCard.put("amount","0");//0
                                tempCard.put("usePoint","0");//积分扣减
                                tempCard.put("getPoint","0");//
                                tempCard.put("partnerMember",payChannelCode);
                                cardlistArray.add(tempCard);
                            }
                            
                            //
                            com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            tempCoupon.put("couponCode",cardNo);//券号
                            tempCoupon.put("couponType","");//券类型
                            tempCoupon.put("quantity",couponQty);//使用张数
                            tempCoupon.put("faceAmount",faceAmt);//总面额
                            tempCoupon.put("buyAmount",p_pay);//抵账金额
                            couponlistArray.add(tempCoupon);
                            continue;
                        }
                        
                        //上面的请求一定要加continue，或者判断标记，因为有历史数据，会员劵支付、积分抵现payType也是#P1
                        if (!isMemberPay)
                        {
                            String payType = payMap.getOrDefault("PAYTYPE","").toString();
                            String order_id = payMap.getOrDefault("REFNO","").toString();
                            String trade_no = payMap.getOrDefault("PAYSERNUM","").toString();
                            double pay_amt = new BigDecimal(pay).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                            
                            if ("#P1".equals(payType)||"#P2".equals(payType))
                            {
                                String shop_code = shopId_db;
                                String pos_code = "999";
                                String ret_order_id = PosPub.getGUID(false);//退款单号
                                isMobilePay = true;
                                
                                com.alibaba.fastjson.JSONObject RefundPayReq = new com.alibaba.fastjson.JSONObject();
                                RefundPayReq.put("serviceId", "Refund");
                                
                                com.alibaba.fastjson.JSONObject payReq = new com.alibaba.fastjson.JSONObject();
                                payReq.put("pay_type", payType);
                                payReq.put("shop_code", shop_code);
                                payReq.put("pos_code", pos_code);
                                payReq.put("order_id", order_id);
                                payReq.put("trade_no", trade_no);
                                payReq.put("ret_order_id", ret_order_id);
                                payReq.put("pay_amt", pay_amt);
                                payReq.put("return_amount", pay_amt);
                                
                                RefundPayReq.put("request", payReq);
                                
                                String reqStr = payReq.toString();
                                String sign = PosPub.encodeMD5(reqStr + Yc_Sign_Key);
                                
                                com.alibaba.fastjson.JSONObject signJson = new com.alibaba.fastjson.JSONObject();
                                signJson.put("sign", sign);
                                signJson.put("key", Yc_Key);
                                
                                RefundPayReq.put("sign", signJson);
                                refundPayReqStr = RefundPayReq.toString();
                                HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，【组装】请求req：" + refundPayReqStr
                                        + ",单号orderNo="+orderNo+",原支付单号order_id="+order_id);
                                HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，【组装】请求req：" + refundPayReqStr
                                        + ",单号orderNo="+orderNo+",原支付单号order_id="+order_id, "ScanPayAddLog");
                            }
                        }
                        
                    }
                    
                    //先退移动支付
                    if (isMobilePay&&!Check.Null(refundPayReqStr))
                    {
                        
                        String payResStr = HttpSend.Sendcom(refundPayReqStr, crmPayUrl);
                        HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，返回res:" + payResStr + ",单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，返回res:" + payResStr + ",单号orderNo="+orderNo, "ScanPayAddLog");
                        
                        com.alibaba.fastjson.JSONObject payResJson = new com.alibaba.fastjson.JSONObject();
                        payResJson = JSON.parseObject(payResStr);//String转json
                        String paySuccess = payResJson.getOrDefault("success","").toString(); // TRUE 或 FALSE
                        //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                        String payServiceStatus = payResJson.getOrDefault("serviceStatus","").toString();
                        String payServiceDescription = payResJson.getOrDefault("serviceDescription","").toString();
                        if (!"true".equalsIgnoreCase(paySuccess))
                        {
                            HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，返回失败"+payServiceDescription+",单号orderNo="+orderNo, "ScanPayAddLog");
                            HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，返回失败:"+payServiceDescription+",单号orderNo="+orderNo);
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, payServiceDescription);
                        }
                        
                    }
                    
                    //成功之后，退会员支付相关
                    if (!Check.Null(memberPayNo_origin) || cardlistArray.size()>0 || couponlistArray.size()>0)
                    {
                        com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
                        reqheader.put("orderNo", memberPayNo_origin);//需唯一
                        reqheader.put("refundOrderNo", "RE"+orderNo);//新的退款单号
                        reqheader.put("orderAmount", tot_amt);//
                        reqheader.put("pointAmount", tot_amt);//
                        //digiwin  鼎捷    qimai企迈   空为鼎捷
                        reqheader.put("partnerMember", req.getRequest().getPartnerMember());
                        // 0.退订单 1.退销售单                企迈场景使用   固定为0
                        reqheader.put("refundType", "0");
                        
                        reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                        
                        //2021-06-03 消费单CRM_CONSUME 表记录 消费机构不对。
                        // 3.0 退订时退款原路返回， 消费门店也是原来收款门店，  所以这里 orgId 不能写 操作门店，跟不是下订门店和配送门店， 而是实际消费门店（可能是下订门店付款，也可能是配送门店付款）
                        //reqheader.put("orgId", shippingShopNo);//
                        
                        if(!Check.Null(shopId)){
                            reqheader.put("orgId", shopId);//
                        }
                        else{
                            reqheader.put("orgId", shopId_db);// shippingShopNo 配送机构， 作为默认值用，其实这么写是不对的，
                        }
                        
                        reqheader.put("oprId", opNo);//
                        reqheader.put("goodsdetail", goodslistArray);
                        reqheader.put("cards", cardlistArray);
                        reqheader.put("coupons", couponlistArray);
                        reqheader.put("payDetail", payslistArray);
                        
                        //
                        String req_sign=reqheader.toString() + Yc_Sign_Key;
                        
                        req_sign=DigestUtils.md5Hex(req_sign);
                        
                        //
                        signheader.put("key", Yc_Key);//
                        signheader.put("sign", req_sign);//md5
                        
                        payReq.put("serviceId", "MemberPayRefund");
                        
                        payReq.put("request", reqheader);
                        payReq.put("sign", signheader);
                        
                        
                        String str = payReq.toString();
                        
                        HelpTools.writelog_waimai("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo,"ScanPayAddLog");
                        String	resbody = "";
                        
                        //编码处理
                        str=URLEncoder.encode(str,"UTF-8");
                        
                        resbody=HttpSend.Sendcom(str, Yc_Url);
                        
                        HelpTools.writelog_waimai("会员退款接口MemberPayRefund返回："+resbody +",单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("会员退款接口MemberPayRefund返回："+resbody +",单号orderNo="+orderNo,"ScanPayAddLog");
                        //获得积分
                        BigDecimal getPoint=new BigDecimal(0);
                        
                        if (resbody.equals("")==false)
                        {
                            com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
                            
                            String serviceDescription=jsonres.get("serviceDescription").toString();
                            String serviceStatus=jsonres.get("serviceStatus").toString();
                            
                            //单号重复,直接查询积分
                            if (serviceStatus.equals("900"))
                            {
                                reqheader.clear();
                                signheader.clear();
                                payReq.clear();
                                
                                reqheader.put("refundOrderNo", "RE"+orderNo);
                                req_sign=reqheader.toString() + Yc_Sign_Key;
                                req_sign=DigestUtils.md5Hex(req_sign);
                                
                                //
                                signheader.put("key", Yc_Key);//
                                signheader.put("sign", req_sign);//md5
                                
                                payReq.put("serviceId", "MemberPayRefundQuery");
                                
                                payReq.put("request", reqheader);
                                payReq.put("sign", signheader);
                                
                                str = payReq.toString();
                                
                                HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery请求内容："+str+",单号orderNo="+orderNo);
                                
                                //编码处理
                                str=URLEncoder.encode(str,"UTF-8");
                                
                                resbody=HttpSend.Sendcom(str, Yc_Url);
                                
                                HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery返回："+resbody+",单号orderNo="+orderNo);
                                
                                if (resbody.equals("")==false)
                                {
                                    jsonres = JSON.parseObject(resbody);
                                    
                                    serviceDescription=jsonres.get("serviceDescription").toString();
                                    serviceStatus=jsonres.get("serviceStatus").toString();
                                    if (jsonres.get("success").toString().equals("true"))
                                    {
                                        res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
                                        com.alibaba.fastjson.JSONObject datas=jsonres.getJSONObject("datas");
                                        String JrefundStatus=datas.getString("refundStatus");//0:未退款 1:已转入退款
                                        if (JrefundStatus.equals("0"))
                                        {
                                            otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:退款状态refundStatus=0");
                                            otherChannelRes=false;
                                        }
                                        res.getDatas().setBillNo("RE"+orderNo);

									/*
									com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
									for (int pi = 0; pi < cardsList.size(); pi++)
									{
										//多张卡累加积分
										getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));

										Card card=res.new Card();
										card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
										card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
										card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
										card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
										card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
										card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
										card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
										card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
										card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
										card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
										card.setGetPoint(getPoint.toPlainString());
										card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
										card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));

										res.getDatas().getVipDatas().getCardsInfo().add(card);

										//
										res.getDatas().getVipDatas().setAmount(card.getAmount_after());
										res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
										res.getDatas().getVipDatas().setMemberId("");
										res.getDatas().getVipDatas().setPoints(card.getPoint_after());
									}
									 */
                                    
                                    }
                                    else
                                    {
                                        otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:" +serviceDescription );
                                        otherChannelRes=false;
                                    }
                                }
                                else
                                {
                                    otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:");
                                    otherChannelRes=false;
                                }
                            }
                            else
                            {
                                if (jsonres.get("success").toString().equals("true"))
                                {
                                    res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
                                    
                                    com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                    for (int pi = 0; pi < cardsList.size(); pi++)
                                    {
                                        //多张卡累加积分
                                        getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
                                        
                                        Card card=res.new Card();
                                        card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
                                        card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
                                        card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
                                        card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
                                        card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
                                        card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
                                        card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
                                        card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
                                        card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
                                        card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
                                        card.setGetPoint(getPoint.toPlainString());
                                        card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
                                        card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));
                                        
                                        res.getDatas().getVipDatas().getCardsInfo().add(card);
                                        
                                        //
                                        res.getDatas().getVipDatas().setAmount(card.getAmount_after());
                                        res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
                                        res.getDatas().getVipDatas().setMemberId("");
                                        res.getDatas().getVipDatas().setPoints(card.getPoint_after());
                                        
                                        res.getDatas().setBillNo("RE"+orderNo);
                                    }
                                }
                                else
                                {
                                    otherChannelError.append("调用会员退款查询接口MemberPayRefund失败:" + serviceDescription);
                                    otherChannelRes= false;
                                }
                            }
                        }
                        else
                        {
                            otherChannelError.append("调用会员退款接口MemberPayRefund失败:");
                            otherChannelRes= false;
                        }
                        
                    }
                    
                    
                    
                }
            }
            
            if(!otherChannelRes)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
            }
            
            
            List<Map<String, Object>> getData_Order_detail=new ArrayList<>();
            
            //0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单）
            //1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
            if(pickGoodsRefundType.equals("0"))
            {
                //先判断下有没有生成销售单
                List<DataProcessBean> DPB_returnSale = new ArrayList<DataProcessBean>();//记录 销退单的sql
                List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();//记录 退订单的sql
                List<Map<String, Object>> saleNoList = this.getSaleNoByOrderNo(eId_para,headOrderNo, orderNo);
                boolean isCreateSale = false;//是否订转销过
                
                //赊销订单需要处理，调用CRM接口
                boolean isCallCustomerCreditUpdate = false;
                double dealCreditAmount_refund = 0;
                
                if(saleNoList!=null&&saleNoList.size()>0)
                {
                    isCreateSale = true;
                    //如果存在的话，需要生成退单
                    StringBuffer error1 = new StringBuffer("");
                    String opShopId = "";//外部接口调用 并且是POS、安卓pos渠道 取操作门店，其他取销售单上默认门店
                    if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                    {
                        if (req.getApiUser()!=null&&req.getApiUser().getUserCode()!=null&&!req.getApiUser().getUserCode().isEmpty())
                        {
                            opShopId = req.getRequest().getShopId();//线下调用 取操作门店
                        }
                    }
                    
                    DPB_returnSale = this.getReturnSaleSql(eId_para, refundBdate, saleNoList,opShopId,opNo,machineNo,workNo,squadNo, error1,null,tot_amt,isRefundDeliverAmt,recalculateMap,getQHead.get(0));
                    
                    //订转销了也要生成促销参与明细
                    if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                    {
                        //来源业务类型：0-销售 1-退单 2-无单退货 3-订单 4-退订
                        StringBuffer  strBuff_execsql = new StringBuffer("");
                        strBuff_execsql.append(" insert into PROM_MEMBER (");
                        strBuff_execsql.append(" EID, ID, PROMNO, SDATE, SOURCEBILLTYPE, SOURBILLNO, MEMBERID, CUSTID, DIRECTION )");
                        
                        strBuff_execsql.append(" select eid, sys_guid(), PROMNO, SDATE,'4', SOURBILLNO, MEMBERID, CUSTID, -1 ");
                        
                        strBuff_execsql.append(" from PROM_MEMBER where  SOURCEBILLTYPE='3' and eid='"+eId_para+"' and SOURBILLNO='"+orderNo+"' ");
                        String  execsql = "";
                        execsql = strBuff_execsql.toString();
                        ExecBean exSale_prom_member = new ExecBean(execsql);
                        DPB_returnSale.add(new DataProcessBean(exSale_prom_member));
                    }
                }
                else//先搞整单吧，部分提货的干不下去了
                {
                    //没有生成销售单，就生成退订单和库存解锁
                    DPB_returnOrder = this.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,opNo,machineNo,workNo,squadNo,null,tot_amt,isRefundDeliverAmt,recalculateMap);
                    
                    
                    
                    StringBuffer unLockStockError = new StringBuffer();
                    boolean unLockStockFlag = this.dcpStockUnlock(this.dao,req, unLockStockError);
                }
                
                //region 判断下，是不是赊销订单
                
                if((loadDocType.equals(orderLoadDocType.POS) || loadDocType.equals(orderLoadDocType.POSANDROID)|| loadDocType.equals(orderLoadDocType.OWNCHANNEL) )&&"Y".equals(isSellCredit))
                {
                    HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【赊销订单】处理开始,单号orderNo="+orderNo);
                    try
                    {
                        String sql_credit = "select * from DCP_CUSTOMER_CREDIT_DETAIL where eid='"+eId_para+"' and SHOPID='"+shopId_db+"' and SOURCENO='"+orderNo+"'";
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【赊销订单】查询赊销明细表sql="+sql_credit+",单号orderNo="+orderNo);
                        List<Map<String, Object>> creditList = this.doQueryData(sql_credit, null);
                        if(creditList!=null&&creditList.isEmpty()==false)
                        {
                            String MACHNO = creditList.get(0).getOrDefault("MACHNO", "").toString();
                            String CUSTOMERNO = creditList.get(0).getOrDefault("CUSTOMERNO", "").toString();
                            String CREDITNAME = creditList.get(0).getOrDefault("CREDITNAME", "").toString();
                            String CREDITAMT = creditList.get(0).getOrDefault("CREDITAMT", "0").toString();//赊销金额
                            String RETURNAMT = creditList.get(0).getOrDefault("RETURNAMT", "0").toString();// 已核销金额
                            String LACKAMT = creditList.get(0).getOrDefault("LACKAMT", "0").toString();//未核销金额
                            
                            String orderNo_refund = "RE"+orderNo;
                            String sdateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                            
                            dealCreditAmount_refund = Double.parseDouble(CREDITAMT);
                            String CREDITAMT_Return = CREDITAMT;
                            if (dealCreditAmount_refund>0)
                            {
                                CREDITAMT_Return = "-"+CREDITAMT;//防止小数点吧，直接用负号，先不用0-double类型
                            }
                            
                            //退订单单写赊销明细表
                            HelpTools.writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【写赊销明细表 DCP_CUSTOMER_CREDIT_DETAIL】，退订单单号sourceNo="+orderNo_refund+",原订单号="+orderNo);
                            //交班统计信息表DCP_CUSTOMER_CREDIT_DETAIL
                            String[] Columns_DCP_CUSTOMER_CREDIT_DETAIL = {
                                    "EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","CREDITNAME","SOURCENO",
                                    "SOURCETYPE","CREDITAMT","RETURNAMT","LACKAMT","UPDATE_TIME"
                            };
                            DataValue[] insValue_DCP_CUSTOMER_CREDIT_DETAIL  = new DataValue[]{
                                    new DataValue(eId_para, Types.VARCHAR),
                                    new DataValue(shopId_db, Types.VARCHAR),
                                    new DataValue(MACHNO, Types.VARCHAR),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(refundBdate, Types.VARCHAR),
                                    new DataValue(CUSTOMERNO, Types.VARCHAR),
                                    new DataValue(CREDITNAME, Types.VARCHAR),// 赊销人creditName == 传入参数 customerName
                                    new DataValue(orderNo_refund, Types.VARCHAR),//来源单号 sourceNo == 订单号 orderNo ，
                                    new DataValue("4", Types.VARCHAR),//来源类型 sourceType == 4 退订单
                                    new DataValue(CREDITAMT_Return, Types.VARCHAR),//赊销金额 creditAmt == 付款方式601的 pay 付款金额
                                    new DataValue("0", Types.VARCHAR),// 已核销金额 returnAmt ==付款金额 pay，
                                    new DataValue(CREDITAMT_Return, Types.VARCHAR),//未核销金额 lackAmt == 0 。
                                    new DataValue(sdateTime, Types.VARCHAR),
                            };
                            InsBean ib_DCP_CUSTOMER_CREDIT_DETAIL = new InsBean("DCP_CUSTOMER_CREDIT_DETAIL", Columns_DCP_CUSTOMER_CREDIT_DETAIL);
                            ib_DCP_CUSTOMER_CREDIT_DETAIL.addValues(insValue_DCP_CUSTOMER_CREDIT_DETAIL);
                            DPB_returnOrder.add(new DataProcessBean(ib_DCP_CUSTOMER_CREDIT_DETAIL));
                            
                            
                            //更新原赊销明细表
                            /*HelpTools.writelog_waimai("【开始生成update语句】渠道类型loadDocType="+loadDocType+"，需要更新【原赊销明细表 DCP_CUSTOMER_CREDIT_DETAIL】，原sourceNo=原订单单号="+orderNo);
                            UptBean up_DCP_CUSTOMER_CREDIT_DETAIL = new UptBean("DCP_CUSTOMER_CREDIT_DETAIL");
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addCondition("SHOPID", new DataValue(shopId_db,Types.VARCHAR));
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addCondition("SOURCENO", new DataValue(orderNo,Types.VARCHAR));

                            //更新updatetime
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addUpdateValue("UPDATE_TIME", new DataValue(sdateTime, Types.VARCHAR));
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addUpdateValue("RETURNAMT", new DataValue(CREDITAMT, Types.VARCHAR));// 已核销金额 returnAmt ==付款金额 pay
                            up_DCP_CUSTOMER_CREDIT_DETAIL.addUpdateValue("LACKAMT", new DataValue("0", Types.VARCHAR));//未核销金额 lackAmt == 0 。

                            DPB_returnOrder.add(new DataProcessBean(up_DCP_CUSTOMER_CREDIT_DETAIL));*/
                            
                            //还要调用CRM接口
                            dealCreditAmount_refund = Double.parseDouble(CREDITAMT);
                            isCallCustomerCreditUpdate = true;
                            
                            
                            String errorMsg="";
                            if(isCallCustomerCreditUpdate)
                            {
                                boolean success_credit=false;
                                //赊销明细写完后， 需调用 CustomerCreditUpdate 服务，用于修改大客户赊销额度 dealCreditAmount ， 所传金额即为付款金额 pay ，为负数 。
                                try
                                {
                                    //金蝶赊销增加营业日期
                                    String bdate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                    if (req.getRequest().getRefundBdate()!=null && req.getRequest().getRefundBdate().length()==8)
                                    {
                                        bdate=req.getRequest().getRefundBdate().substring(0,4)+"-" + req.getRequest().getRefundBdate().substring(4,6) +"-" +req.getRequest().getRefundBdate().substring(6,8);
                                    }
                                    
                                    //整单作废不需要赋值
                                    List<JindieGoodsDetail>  details =new ArrayList<>();
                                    StringBuffer error_credit = new StringBuffer("");
                                    
                                    //产生销售单的，直接退销售单，订单已经被金蝶关闭
                                    if(saleNoList!=null&&saleNoList.size()>0)
                                    {
                                        String sql_Jindie="select * from dcp_sale_detail where eid='"+eId_para+"' and saleno='"+saleNoList.get(0).get("SALENO").toString()+"' order by item,oitem ";
                                        List<Map<String, Object>> getData_detailJindie=this.doQueryData(sql_Jindie,null);
                                        if (getData_detailJindie != null && getData_detailJindie.size()>0)
                                        {
                                            for (Map<String, Object> map_jindie : getData_detailJindie)
                                            {
                                                String ispackage=map_jindie.get("ISPACKAGE").toString();
                                                String packagemaster=map_jindie.get("PACKAGEMASTER").toString();
                                                //普通商品或套餐子商品金额累加
                                                if ((ispackage != null && !"Y".equals(ispackage)) || (packagemaster!=null && !"Y".equals(packagemaster)))
                                                {
                                                    JindieGoodsDetail jd=new JindieGoodsDetail();
                                                    jd.setItem(Integer.parseInt(map_jindie.get("ITEM").toString()));
                                                    jd.setoItem(PosPub.isNumeric(map_jindie.get("OITEM").toString())?Integer.parseInt(map_jindie.get("OITEM").toString()):0);
                                                    jd.setoQty(new BigDecimal(map_jindie.get("QTY").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    jd.setPluNo(map_jindie.get("PLUNO").toString());
                                                    jd.setUnitId(map_jindie.get("UNIT").toString());
                                                    jd.setQty(new BigDecimal(map_jindie.get("QTY").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    jd.setOldPrice(new BigDecimal(map_jindie.get("OLDPRICE").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    jd.setPrice(new BigDecimal(map_jindie.get("PRICE").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    //抹零会算在支付折扣，取DISC_MERRECEIVE+DISC
                                                    jd.setDisc(new BigDecimal(map_jindie.get("DISC").toString()).add(new BigDecimal(map_jindie.get("DISC_MERRECEIVE").toString())).setScale(2,RoundingMode.HALF_UP));
                                                    //抹零会算在支付折扣，取AMT_MERRECEIVE
                                                    jd.setAmt(new BigDecimal(map_jindie.get("AMT_MERRECEIVE").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    jd.setOldAmt(new BigDecimal(map_jindie.get("OLDAMT").toString()).setScale(2,RoundingMode.HALF_UP));
                                                    jd.setMemo("");
                                                    details.add(jd);
                                                }
                                                
                                            }
                                        }
                                        
                                        dealCreditAmount_refund = 0-Math.abs(dealCreditAmount_refund);//负数
                                        //退销售单接口
                                        //康志才(418056790)  17:30:01
                                        //你在那地方 加个备注：金额字段只影响易成赊销额度，金蝶是取明细，不使用此字段,所以正负无所谓。。
                                        success_credit= HelpTools.CustomerCreditUpdate(eId_para, req.getApiUserCode(), req.getApiUser().getUserKey(), req.getLangType(), "RE"+saleNoList.get(0).get("SALENO").toString(), belfirm,
                                                shopId,"5",opName,"",saleNoList.get(0).get("SALENO").toString(),details,
                                                customerNo, dealCreditAmount_refund, error_credit,bdate);
                                        
                                        
                                    }
                                    else
                                    {
                                        dealCreditAmount_refund = 0-Math.abs(dealCreditAmount_refund);//负数
                                        //订单作废接口
                                        success_credit= HelpTools.CustomerCreditUpdate(eId_para, req.getApiUserCode(), req.getApiUser().getUserKey(), req.getLangType(), orderNo, belfirm,
                                                shopId,"2",opName,"","",details,
                                                customerNo, dealCreditAmount_refund, error_credit,bdate);
                                        
                                    }
                                }
                                catch (Exception e)
                                {
                                    errorMsg=e.getMessage();
                                }
                                
                                //赊销调用失败，不能进行下去
                                if (!success_credit)
                                {
                                    res.setSuccess(false);
                                    res.setServiceStatus("100");
                                    res.setServiceDescription(errorMsg);
                                    return;
                                }
                                
                            }
                            
                        }
                        else
                        {
                            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【赊销订单】查询赊销明细表数据为空,单号orderNo="+orderNo);
                        }
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【赊销订单】处理异常:"+e.getMessage()+",单号orderNo="+orderNo);
                    }
                    
                }
                //endregion
                
                
                
                //更新订单状态
                up1.addUpdateValue("STATUS", new DataValue("12",Types.VARCHAR));
                up1.addUpdateValue("REFUNDSTATUS", new DataValue("6",Types.VARCHAR));
                up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                up1.addUpdateValue("OPERATIONTYPE", new DataValue("0", Types.VARCHAR));
                this.addProcessData(new DataProcessBean(up1));
                //更新单身已退数量
                String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
                ExecBean exSale = new ExecBean(execsql);
                this.addProcessData(new DataProcessBean(exSale));
                
                
                for (DataProcessBean bean : DPB_returnSale)
                {
                    this.addProcessData(bean);
                }
                
                for (DataProcessBean bean : DPB_returnOrder)
                {
                    this.addProcessData(bean);
                }
                
                String memoLog = "";
                
                if(DPB_returnSale!=null&&DPB_returnSale.isEmpty()==false)
                {
                    memoLog = "生成销退单成功";
                }
                if(DPB_returnOrder!=null&&DPB_returnOrder.isEmpty()==false)
                {
                    memoLog = "生成退订单成功<br>退订单号:"+"RE"+orderNo;
                    if (orderLoadDocType.XIAOYOU.equals(loadDocType))
                    {
                        memoLog = "生成退订单成功<br>退订单号:"+refundOrderNo_para;
                    }
                }
                
                this.doExecuteDataToDB();
                res.setSuccess(true);
                
                if(isWechatOrder&&isCreateSale)
                {
                    HelpTools.wechatOrderRefundPoint(eId_para, orderNo,headOrderNo, opNo, opName);
                }
                
                /************乐沙尔-销退单调用存储过程分摊收款方式到商品***************/
                if (isCreateSale&&saleNoList.isEmpty()==false)
                {
                    String saleno_refund = "";
                    try
                    {
                        this.pData.clear();
                        
                        String sourceSaleNo = saleNoList.get(0).get("SALENO").toString();
                        String shopid_oldsale = saleNoList.get(0).get("SHOPID").toString();
                        saleno_refund = "RE"+sourceSaleNo;//退单的单号saleno
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】，生成销退单之后(销退单单号saleNo="+saleno_refund+"),执行分摊收款方式到商品存储过程【SP_DCP_SALE_DETAIL_PAY】【开始】,原订单号orderNo="+orderNo+"！");
                        String procedure="SP_DCP_SALE_DETAIL_PAY";
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,eId_para);                                       //--企业ID
                        inputParameter.put(2,shopid_oldsale);                                    //--组织
                        inputParameter.put(3,saleno_refund);
                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));
                        //最后执行SQL
                        this.doExecuteDataToDB();
                        this.pData.clear();
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】，生成销退单之后(销退单单号saleNo="+saleno_refund+"),执行分摊收款方式到商品存储过程【SP_DCP_SALE_DETAIL_PAY】【成功】,原订单号orderNo="+orderNo+"！");
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】，生成销退单之后(销退单单号saleNo="+saleno_refund+"),执行分摊收款方式到商品存储过程【SP_DCP_SALE_DETAIL_PAY】【失败】异常:"+e.getMessage()+",原订单号orderNo="+orderNo+"！");
                    }
                }
                
                //*****调用库存同步给三方，这是个异步，不会影响效能*******
                try {
                    if (!CollectionUtil.isEmpty(stockSyncList))
                    {
                        for (Map<String,Object> map : stockSyncList)
                        {
                            WebHookService.stockSync(map.get("eId").toString(),map.get("shopId").toString(),map.get("billNo").toString());
                        }
                    }
                    
                }
                catch (Exception e)
                {
                
                }
                
                
                //写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                
                orderStatusLog onelv2 = new orderStatusLog();
                onelv2.setLoadDocType(loadDocType);
                onelv2.setChannelId(channelId);
                onelv2.setLoadDocBillType("");
                onelv2.setLoadDocOrderNo("");
                onelv2.seteId(eId_para);
                onelv2.setOpName(opName);
                onelv2.setOpNo(opNo);
                onelv2.setShopNo(shopId);
                onelv2.setOrderNo(orderNo);
                onelv2.setMachShopNo("");
                onelv2.setShippingShopNo("");
                String statusType = "99";
                String updateStaus = "99";
                onelv2.setStatusType(statusType);
                onelv2.setStatus(updateStaus);
                
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv2.setStatusTypeName(statusTypeName);
                onelv2.setStatusName(statusName);
                
                String memo = memoLog;
                
                onelv2.setMemo(memo);
                onelv2.setDisplay("0");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv2.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv2);
                
                //订单状态日志
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId_para);
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopId);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                statusType = "1";
                updateStaus = "12";
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                memo = statusName;
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");
                
                updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
                this.pData.clear();
            }
            else if(pickGoodsRefundType.equals("1"))
            {
                
                List<Map<String, Object>> saleNoList = this.getSaleNoByOrderNo(eId_para, headOrderNo,orderNo);
                boolean isCreateSale = false;//是否订转销过
                if(saleNoList!=null&&saleNoList.size()>0)
                {
                    isCreateSale = true;
                }
                
                List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();// 记录退订单的sql
                
                if (!isCreateSale)
                {
                    // 没有生成销售单，就生成退订单和库存解锁
                    DPB_returnOrder = this.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,opNo,machineNo,workNo,squadNo,null,tot_amt,isRefundDeliverAmt,recalculateMap);
                    
                    StringBuffer unLockStockError = new StringBuffer();
                    boolean unLockStockFlag = this.dcpStockUnlock(this.dao,req, unLockStockError);
                }
                
                
                // 更新订单状态
                up1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
                up1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
                up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt, Types.VARCHAR));//整单退取付款金额（订单村长部分支付）
                up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                up1.addUpdateValue("OPERATIONTYPE", new DataValue("0", Types.VARCHAR));
                this.addProcessData(new DataProcessBean(up1));
                // 更新单身已退数量
                String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId_para
                        + "' and orderno='" + orderNo + "' ";
                ExecBean exSale = new ExecBean(execsql);
                this.addProcessData(new DataProcessBean(exSale));
                
                for (DataProcessBean bean : DPB_returnOrder)
                {
                    this.addProcessData(bean);
                }
                
                this.doExecuteDataToDB();
                res.setSuccess(true);
                
                // 写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId_para);
                
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopId);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                String statusType = "1";
                String updateStaus = "12";
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "";
                memo += statusName;
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet)
                {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else
                {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
                this.pData.clear();
                
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点类型="+pickGoodsRefundType+" 暂不支持！");
            }
            
            //由于在网页操作同意退单,调用的是这个主动退单接口，所以要区分下，如果是网页上操作，那么就是写下缓存
            if (isWechatOrder)
            {
                try
                {
                    boolean isOpen = false;//是不是外部接口调用
                    if (req.getApiUser()!=null&&req.getApiUser().getUserCode()!=null&&!req.getApiUser().getUserCode().isEmpty())
                    {
                        isOpen = true;
                    }
                    if (!isOpen)
                    {
                        HelpTools.writelog_waimai("【商城订单】【同意退单】开始写缓存,订单号orderNo=" + orderNo);
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId_para, loadDocType, orderNo);
                        StringBuffer errorMessage = new StringBuffer();
                        HelpTools.writeOrderRedisByAllShop(dcpOrder,"",errorMessage);
                    }
                    
                }
                catch (Exception e)
                {
                
                }
                
            }
            
            //增加KDS处理逻辑部分
            //是否启用KDS标记
            String canKDS="N";
            String Crm_channel_KDS_SQL="select * from crm_channel where eid='"+eId_para+"' and appno='KDS' and status=100";
            List<Map<String, Object>> getData_KDS =this.doQueryData(Crm_channel_KDS_SQL,null);
            if (getData_KDS != null && getData_KDS.size()>0)
            {
                canKDS="Y";
            }
            
            if (canKDS.equals("Y"))
            {
                //查订单单头
                String order_SQL="select * from dcp_order where eid='" + eId_para + "' and LOADDOCTYPE='" + loadDocType +"' and orderno='" + orderNo + "'  ";
                List<Map<String, Object>> getData_Order =this.doQueryData(order_SQL,null);
                
                //查订单单身
                String ordeDetail_SQL="select a.*,b.category,b.ISDOUBLEGOODS from dcp_order_detail a " +
                        "left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                        "where a.eid='" + eId_para + "' and a.LOADDOCTYPE='" + loadDocType +"' and a.orderno='" + orderNo + "'  ";
                getData_Order_detail=this.doQueryData(ordeDetail_SQL,null);
                
                // 退单完成后 检查是否生成加工任务单 并同步加工任务单状态
                this.updateProcessTask(req,getData_Order,getData_Order_detail);
            }
            
            //调用发票作废或折让
            if(isCallPosInvoiceRefund)
            {
                String refundOrderNo = "RE"+orderNo;
                Map<String, Object> outMap = new HashMap<String,Object>();
                StringBuffer errorInvoiceRefund = new StringBuffer();
                boolean callInvoiceRefundResult = HelpTools.OrderInvoiceRefund(eId_para, orderNo, refundOrderNo, refundReasonNo, refundReasonName, invOperateType, opNo, outMap, errorInvoiceRefund);
                if(callInvoiceRefundResult)
                {
                    String rebateNo = outMap.getOrDefault("rebateNo", "").toString();
                    UptBean upHeader = new UptBean("DCP_ORDER");
                    upHeader.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                    upHeader.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
                    
                    //更新updatetime
                    upHeader.addUpdateValue("INVOPERATETYPE", new DataValue(invOperateType, Types.VARCHAR));
                    upHeader.addUpdateValue("REBATENO", new DataValue(rebateNo, Types.VARCHAR));
                    upHeader.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    upHeader.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(upHeader));
                    this.doExecuteDataToDB();
                    
                    
                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);
                    
                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "99";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    
                    String statusName = "其他";
                    String statusTypeName = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);
                    
                    String memo = "发票作废或折让成功";
                    
                    if(rebateNo!=null&&rebateNo.trim().isEmpty()==false)
                    {
                        memo = memo+"<br/>发票折让单号:"+rebateNo;
                    }
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("0");
                    
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    
                    orderStatusLogList.add(onelv1);
                    
                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();
                    
                }
                else
                {
                    UptBean upHeader = new UptBean("DCP_ORDER");
                    upHeader.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                    upHeader.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
                    
                    //更新updatetime
                    upHeader.addUpdateValue("EXCEPTIONSTATUS", new DataValue("Y", Types.VARCHAR));
                    upHeader.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    upHeader.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(upHeader));
                    this.doExecuteDataToDB();
                    
                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);
                    
                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "99";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    
                    String statusName = "其他";
                    String statusTypeName = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);
                    
                    String memo = "发票作废或折让失败";
                    
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("0");
                    
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    
                    orderStatusLogList.add(onelv1);
                    
                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();
                    
                    
                }
            }
            
        }
        //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
        //这里refundType=1表示部分退
        else if (refundType.equals("1"))
        {
        	boolean otherChannelRes = false;//退款调用相应渠道接口，成功标识
        	StringBuilder otherChannelError = new StringBuilder("");
        	if(orderLoadDocType.MINI.equals(loadDocType)){
        		
        		//退货退款
        		if("0".equals(pickGoodsRefundType)){
        			if(req.getRequest().getGoods()==null||req.getRequest().getGoods().size()<1){
                		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "部分退货退款时,商品信息必传!");
                	}
        		}
        		//小程序部分退单
        		boolean isCreateSale = false;//是否订转销过
            	boolean isWechatOrder = true;
                if (isHasPartOrderToSale(eId_para,orderNo)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
                }
//                testtesttest
                try{
                    String refundReasonCode_wechat = "4";//退单原因  不传默认0-未知、1-未付款自主取消、2-超时未支付、3-商家拒单、4-用户申请退单
                    String refundReasonDesc = refundReason;//退单原因描述
                    if(refundReasonDesc==null||refundReasonDesc.trim().isEmpty()){
                        refundReasonDesc = "";
                    }
                    JSONObject objReq = new JSONObject();
                    objReq.put("orderNo", orderNo);
                    objReq.put("refund", 1);
                    objReq.put("refundReason", refundReasonCode_wechat);
                    objReq.put("refundReasonDesc", refundReasonDesc);
                    objReq.put("goods", req.getRequest().getGoods());
                    objReq.put("pickGoodsRefundType", pickGoodsRefundType);
                    objReq.put("refundType", refundType);
                    objReq.put("tot_amt", tot_amt);
                    objReq.put("isRefundDeliverAmt", isRefundDeliverAmt);
                    objReq.put("goods", req.getRequest().getGoods());
                    String request = objReq.toString();
                    String microMarkServiceName = "OrderRefund";
                    String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);
                    JSONObject json = new JSONObject(result);
                    try{
                        String success = json.get("success").toString();
                        String serviceDescription = json.get("serviceDescription").toString();
                        if(success.equalsIgnoreCase("true")|| serviceDescription.equals("訂單異常或已退款")||serviceDescription.equals("订单异常或已退款")){
                            otherChannelRes = true;
                        }
                        else{
                            otherChannelRes = false;
                            otherChannelError.append(serviceDescription);
                        }
                    }
                    catch (Exception e){
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }
                }
                catch (Exception e){
                    // TODO: handle exception
                    otherChannelRes = false;
                    otherChannelError.append(e.getMessage());
                }
                
                if(!otherChannelRes)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
                }
            	
                //0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单）
                //1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
                if(pickGoodsRefundType.equals("0")){
                    //先判断下有没有生成销售单
                    List<DataProcessBean> DPB_returnSale = new ArrayList<DataProcessBean>();//记录 销退单的sql
                    List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();//记录 退订单的sql
                    List<Map<String, Object>> saleNoList = this.getSaleNoByOrderNo(eId_para,headOrderNo, orderNo);
                    isCreateSale = false;//是否订转销过
                    if(saleNoList!=null&&saleNoList.size()>0){
                        isCreateSale = true;
                        //如果存在的话，需要生成退单
                        StringBuffer error1 = new StringBuffer("");
//                        DPB_returnSale = this.getReturnSaleSql(eId_para, refundBdate, saleNoList, error1,req.getRequest().getGoods());
                        DPB_returnSale = this.getReturnSaleSql(eId_para, refundBdate, saleNoList,"",opNo,machineNo,workNo,squadNo, error1,req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap,getQHead.get(0));
                    }
                    else//
                    {
                        DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                        //没有生成销售单，就生成退订单和库存解锁
                        DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap);

                        StringBuffer unLockStockError = new StringBuffer();
//                        boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);
                        boolean unLockStockFlag = this.dcpStockUnlock(this.dao,req, unLockStockError);
                    }

//                    UptBean up1 = new UptBean("DCP_ORDER");
//                    //更新订单状态
//                    up1.addUpdateValue("STATUS", new DataValue("12",Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDSTATUS", new DataValue("6",Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt,Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//                    up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//
//                    this.addProcessData(new DataProcessBean(up1));

                    //更新单身已退数量 （整单）
//                    String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
//                    ExecBean exSale = new ExecBean(execsql);
//                    this.addProcessData(new DataProcessBean(exSale));


                    for (DataProcessBean bean : DPB_returnSale)
                    {
                        this.addProcessData(bean);
                    }

                    for (DataProcessBean bean : DPB_returnOrder)
                    {
                        this.addProcessData(bean);
                    }

                    this.doExecuteDataToDB();
                    res.setSuccess(true);

                    String status_update = "12";
                    String refundStatus_update = "6";

//                    dcpOrder.setStatus(status_update);
//                    dcpOrder.setRefundStatus(refundStatus_update);

//                    String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
//                    String hash_key = orderNo;
//                    String hash_value = pj.beanToJson(dcpOrder);
//                    this.updateRedis(redis_key, hash_key, hash_value);

                    //写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "12";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("1");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet) {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();
                	res.setSuccess(true);
                    res.setServiceDescription("执行成功!");
                    res.setServiceStatus("000");

                }
                else if(pickGoodsRefundType.equals("1")){

                    List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();// 记录退订单的sql

                    // 没有生成销售单，就生成退订单和库存解锁
                    DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                    //没有生成销售单，就生成退订单和库存解锁
                    DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap);


                    StringBuffer unLockStockError = new StringBuffer();
//                    boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);
                    boolean unLockStockFlag = this.dcpStockUnlock(this.dao,req, unLockStockError);

                    //部分仅退款时不更新以下内容 注释掉
//                    UptBean up1 = new UptBean("DCP_ORDER");
//                    up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
//                    up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
//                    // 更新订单状态
//                    up1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt, Types.VARCHAR));
//                    up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//                    up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//
//                    this.addProcessData(new DataProcessBean(up1));
//
//                    // 更新单身已退数量
//                    String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId_para
//                            + "' and orderno='" + orderNo + "' ";
//                    ExecBean exSale = new ExecBean(execsql);
//                    this.addProcessData(new DataProcessBean(exSale));

                    for (DataProcessBean bean : DPB_returnOrder)
                    {
                        this.addProcessData(bean);
                    }

                    this.doExecuteDataToDB();
                    res.setSuccess(true);

                    String status_update = "12";
                    String refundStatus_update = "6";

//                    dcpOrder.setStatus(status_update);
//                    dcpOrder.setRefundStatus(refundStatus_update);
//
//                    String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
//                    String hash_key = orderNo;
//                    String hash_value = pj.beanToJson(dcpOrder);
//                    this.updateRedis(redis_key, hash_key, hash_value);
                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "12";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("1");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();
                    

                }                
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点类型="+pickGoodsRefundType+" 暂不支持！");
                }
                if(isWechatOrder&&isCreateSale)
                {
                    HelpTools.wechatOrderRefundPoint(eId_para, orderNo,headOrderNo, opNo, opName);
                }
            }
        	else{
        		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单类型refundType="+refundType+"，暂不支持渠道类型"+loadDocType);
        	}
        	
        	if(!otherChannelRes)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
            }
        	res.setSuccess(otherChannelRes);
            res.setServiceDescription(otherChannelError.toString());
            res.setServiceStatus("000");
            
        }
        else if (refundType.equals("2"))
        {
            if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
            {
                //退单外部服务用到（POS/POSANDROID），内部服务用不到
                //refundType（退单类型）：给2，仅退已提部分（外部调用）
                //销退单及发票POS处理，POS退已提部分，需要传退单商品goods节点，
                //传入商品总数=订单商品的总数，订单状态给12已退单，退单状态给6已退单；更新商品的已退数量DCP_ORDER_DETAIL.RQTY
                //传入商品总数<订单商品的总数，退单状态给 10.部分退款成功；更新商品的已退数量
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单类型refundType="+refundType+"，暂不支持渠道类型"+loadDocType);
            }
            
            List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goodsList = req.getRequest().getGoods();
            if(goodsList==null||goodsList.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品列表goods为空！");
            }
            
            String sql_detail = "select * from dcp_order_Detail where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
            HelpTools.writelog_waimai("POS订转销之后的销售退单，更新原订单商品明细查询sql="+sql_detail);
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_detail, null);
            if(getQDataDetail==null||getQDataDetail.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "原订单商品明细查询为空！");
            }
            
            
            //
            StringBuffer sJoinOrderno=new StringBuffer("");
            StringBuffer sJoinItem=new StringBuffer("");
            
            boolean isNeedUpdate = false;
            String logmemo = "";
            logmemo +="传入得退款金额："+req.getRequest().getTot_amt()+"<br/>";
            StringBuffer errorBuffer = new StringBuffer("");
            for (DCP_OrderRefundAgreeOrRejectReq.levelGoods map : goodsList)
            {
                
                boolean isExist = false;//是否存在
                String item = map.getItem();
                int item_i = 0;
                try
                {
                    item_i = Integer.parseInt(item);
                } catch (Exception e) {
                    // TODO: handle exception
                    
                }
                
                String pluNo = map.getPluNo();
                double rQty = map.getQty();
                BigDecimal rQty_b = new BigDecimal(rQty);
                if(rQty_b.compareTo(BigDecimal.ZERO)==0)
                {
                    String ss = "传入的退货项次="+item+" 编码="+pluNo+" 名称="+map.getPluName()+" 退货数量QTY=0,";
                    errorBuffer.append(ss);
                    continue;
                }
                
                //检查传入的商品项次+条码是否存在
                for (Map<String, Object> oneData : getQDataDetail)
                {
                    String item_db = oneData.get("ITEM").toString();
                    int item_db_i = 0;
                    try
                    {
                        item_db_i = Integer.parseInt(item_db);
                    } catch (Exception e) {
                        // TODO: handle exception
                        
                    }
                    String pluNo_db = oneData.get("PLUNO").toString();
                    String qty_db_str = oneData.get("QTY").toString();//单身数量
                    String rQty_db_str = oneData.get("RQTY").toString();//已经退货数量
                    
                    double qty_db = 0;
                    try
                    {
                        qty_db = Float.parseFloat(qty_db_str);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    if (qty_db<0) //部分退单的单身qty为负
                    {
                        
                        continue;
                    }
                    
                    double rQty_db = 0;
                    try
                    {
                        rQty_db = Double.parseDouble(rQty_db_str);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    
                    if(item_i==item_db_i&&pluNo.equals(pluNo_db))
                    {
                        
                        if(rQty+rQty_db>qty_db)
                        {
                            String ss = "传入的退货项次="+item+" 编码="+pluNo+" 名称="+map.getPluName()+" 退货数量"+rQty+" +数据库中已退货数量"+rQty_db+" 大于原数量QTY="+qty_db_str;
                            HelpTools.writelog_waimai("【第三方调用DCP_Refund接口】异常："+ss+" 单号orderNo="+orderNo);
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
                        }
                        UptBean ub2 = null;
                        ub2 = new UptBean("dcp_order_detail");
                        
                        
                        ub2.addUpdateValue("RQTY", new DataValue(rQty, Types.FLOAT, DataExpression.UpdateSelf));
                        
                        //condition
                        ub2.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
                        ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                        ub2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));
                        isExist = true;
                        double	rQty_tot = rQty+rQty_db;//总退货数量
                        logmemo +="退货项次："+item+" 名称："+map.getPluName()+" 退货数量："+rQty_db_str+"-->"+rQty_tot+"<br/>";
                        
                        break;
                    }
                    
                }
                
                if(!isExist)
                {
                    String ss ="没有匹配到数据库中该订单对应的商品资料！"+ "传入的退货项次item="+item+" 编码pluNo="+pluNo+" 退货数量rQty="+rQty+" ";
                    HelpTools.writelog_waimai("【第三方调用DCP_Refund接口】异常："+ss+" 单号orderNo="+orderNo);
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
                }
                
                //
                sJoinOrderno.append(orderNo+",");
                sJoinItem.append(item+",");
                
                isNeedUpdate = true;
                
                
            }
            if(isNeedUpdate)
            {
                UptBean ub1 = null;
                ub1 = new UptBean("dcp_order");
                
                ub1.addUpdateValue("REFUNDSTATUS", new DataValue("10", Types.VARCHAR));
                ub1.addUpdateValue("REFUNDAMT", new DataValue(req.getRequest().getTot_amt(), Types.FLOAT, DataExpression.UpdateSelf));
                ub1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(req.getRequest().getTot_amt(), Types.FLOAT, DataExpression.UpdateSelf));
                ub1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(req.getRequest().getTot_amt(), Types.FLOAT, DataExpression.UpdateSelf));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub1.addUpdateValue("LASTREFUNDTIME", new DataValue(refundDatetime, Types.VARCHAR));
                //condition
                ub1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
                ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                
                this.addProcessData(new DataProcessBean(ub1));
                
                HelpTools.writelog_waimai("【第三方调用DCP_Refund接口】sql语句组装完成，执行sql【开始】"+" 单号orderNo="+orderNo);
                this.doExecuteDataToDB();
                HelpTools.writelog_waimai("【第三方调用DCP_Refund接口】sql语句组装完成，执行sql【成功】"+" 单号orderNo="+orderNo);
                
                // 写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId_para);
                
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopId);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                String statusType = "3";
                String updateStaus = "10";//部分退单成功
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                onelv1.setMemo("部分退单成功<br/>"+logmemo);
                onelv1.setDisplay("0");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet)
                {
                    HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else
                {
                    HelpTools.writelog_waimai(
                            "【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
                
                this.pData.clear();
                
                this.UpdateOrderCompleted(req, shopId);
                
                //增加KDS处理逻辑部分
                //是否启用KDS标记
                String canKDS="N";
                String Crm_channel_KDS_SQL="select * from crm_channel where eid='"+eId_para+"' and appno='KDS' and status=100";
                List<Map<String, Object>> getData_KDS =this.doQueryData(Crm_channel_KDS_SQL,null);
                if (getData_KDS != null && getData_KDS.size()>0)
                {
                    canKDS="Y";
                }
                if (canKDS.equals("Y"))
                {
                    //
                    Map<String, String> mapOrder=new HashMap<String, String>();
                    mapOrder.put("ORDERNO", sJoinOrderno.toString());
                    mapOrder.put("ITEM", sJoinItem.toString());
                    //
                    MyCommon cm=new MyCommon();
                    String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                    mapOrder=null;
                    mapOrder=null;
                    sJoinOrderno.setLength(0);
                    sJoinOrderno=null;
                    sJoinItem.setLength(0);
                    sJoinItem=null;
                    
                    if (withasSql_Orderno.equals("")==false)
                    {
                        //查订单单头
                        String order_SQL="select * from dcp_order where eid='" + eId_para + "' and LOADDOCTYPE='" + loadDocType +"' and orderno='" + orderNo + "'  ";
                        List<Map<String, Object>> getData_Order =this.doQueryData(order_SQL,null);
                        
                        //查订单单身
                        String ordeDetail_SQL="with c AS ("+withasSql_Orderno+") " +
                                "select a.*,b.category from dcp_order_detail a " +
                                "inner join c on a.item=c.item and a.orderno=c.orderno " +
                                "left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                                "where a.eid='" + eId_para + "' and a.LOADDOCTYPE='" + loadDocType +"' and a.orderno='" + orderNo + "'  ";
                        List<Map<String, Object>> getData_Order_detail =this.doQueryData(ordeDetail_SQL,null);
                        
                        // 退单完成后 检查是否生成加工任务单 并同步加工任务单状态
                        this.updateProcessTask(req,getData_Order,getData_Order_detail);
                    }
                }
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorBuffer.toString());
            }
            
            
            
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单类型"+refundType+"暂不支持");
        }
        
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderRefundReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderRefundReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderRefundReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_OrderRefundReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

       /* if (Check.Null(req.getRequest().geteId()))
        {
            errCt++;
            errMsg.append("企业编号eId不可为空值, ");
            isFail = true;
        }*/
        
//        if (Check.Null(req.getRequest().getRefundType()))
//        {
//            errCt++;
//            errMsg.append("退单类型refundType不可为空值, ");
//            isFail = true;
//        }
        
        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errCt++;
            errMsg.append("订单单号orderNo不可为空值, ");
            isFail = true;
        }

		/*
		if (Check.Null(req.getRequest().getLoadDocType()))
		{
			errCt++;
			errMsg.append("订单渠道类型loadDocType不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getRequest().getChannelId()))
		{
			errCt++;
			errMsg.append("订单渠道编码channelId不可为空值, ");
			isFail = true;
		}
		*/
        
        if (Check.Null(req.getRequest().getRefundBdate()))
        {
            errCt++;
            errMsg.append("退订营业日期refundBdate不可为空值, ");
            isFail = true;
        }

        /*if (Check.Null(req.getRequest().getRefundDatetime()))
        {
            errCt++;
            errMsg.append("退订时间refundDatetime不可为空值, ");
            isFail = true;
        }*/
        if (!Check.Null(req.getRequest().getTot_amt()))
        {
           /* errCt++;
            errMsg.append("退款总金额tot_amt不可为空值, ");
            isFail = true;*/
            try
            {
                String tot_amt = req.getRequest().getTot_amt();
                BigDecimal tot_amt_b = new BigDecimal(tot_amt);
            }
            catch (Exception e)
            {
                errCt++;
                errMsg.append("退款总金额tot_amt传参异常:"+e.getMessage());
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
    protected TypeToken<DCP_OrderRefundReq> getRequestType()
    {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderRefundReq>(){};
    }
    
    @Override
    protected DCP_OrderRefundRes getResponseType()
    {
        // TODO Auto-generated method stub
        return new DCP_OrderRefundRes();
    }
    
    private  boolean CheckCanReturn(String eId,String orderNo,StringBuffer errorInfo) throws Exception
    {
        String sql = "select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】单号orderNo="+orderNo+"  查询语句："+sql);
        List<Map<String, Object>> getQHead = this.doQueryData(sql, null);
        if(getQHead==null||getQHead.isEmpty())
        {
            errorInfo.append("该订单不存在！");
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询完成,单号orderNo="+orderNo+"该订单不存在！");
            return false;
        }
        String status = getQHead.get(0).get("STATUS").toString();
        loadDocType=getQHead.get(0).get("LOADDOCTYPE").toString();
        channelId=getQHead.get(0).get("CHANNELID").toString();
        
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询完成,单号orderNo="+orderNo+" 状态status="+status);
        if(status.equals("3"))
        {
            errorInfo.append("该订单状态为已取消！");
            return false;
        }
        if(status.equals("12"))
        {
            errorInfo.append("订单状态为已退单！");
            return false;
        }
        
        return true;
    }
    
    /**
     * 查询来源类型是订单，来源单号是订单单号的销售单号
     * @param eId
     * @param orderNo
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getSaleNoByOrderNo(String eId,String headOrderNo,String orderNo) throws Exception
    {
        String sql = "";
        if(headOrderNo!=null&&headOrderNo.trim().length()>0){
        	//有主单号
        	//SOURCESUBORDERNO	NVARCHAR2(64 CHAR)	Yes	''	21	原子单号
        	sql = "select * from dcp_sale where otype=3 and ofno='"+headOrderNo+"' and eid='"+eId+"' and sourcesuborderno='"+orderNo+"'";
        }else{
        	sql = "select * from dcp_sale where otype=3 and ofno='"+orderNo+"' and eid='"+eId+"' ";
        }
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询该订单单号orderNo="+orderNo+"有没有生成销售单，查询sql:"+sql);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        return getQData;
    }
    
    /**
     * 根据原销售单号生成反向销退单
     * @param eId 企业ID
     * @param bDate 销退单营业日期
     * @param sourceSaleNoList map结构 {SALENO=,SHOPID=}
     * @param opShopId 操作门店id
     * @param opNo 操作员
     * @param machineNo 机台编码
     * @param workNo 当前班别
     * @param squadNo 当前班次
     * @param errorMessage
     * @return
     * @throws Exception
     */
    private ArrayList<DataProcessBean> getReturnSaleSql(String eId,String bDate,List<Map<String, Object>> sourceSaleNoList,String opShopId,String opNo,String machineNo,String workNo,String squadNo,StringBuffer errorMessage,List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods,
    		String tot_amt,String isRefundDeliverAmt,Map<String,Object> recalculateMap,Map<String, Object> ordermap) throws Exception
    {
        String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stime = new SimpleDateFormat("HHmmss").format(new Date());
        String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String tran_time = update_time;
        String bdate = bDate;
        int otype = 0;//退单来源类型
        if (squadNo==null||squadNo.trim().isEmpty())
        {
            squadNo = "0";//默认0
        }
        ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
        for (Map<String, Object> par : sourceSaleNoList)
        {
            
            String sourceSaleNo = par.get("SALENO").toString();
            String shopid = par.get("SHOPID").toString();
            //原单 总应付金额
            BigDecimal saleAmtTot=new BigDecimal(par.get("TOT_AMT")==null?"0":par.get("TOT_AMT").toString());
            String saleno = "RE"+sourceSaleNo;//退单的单号saleno
            List<Map<String, Object>> dcpSaleDetailMaps = null;
            
    		int type = 1;//退单的类型type
            String typename = "原单退";
            String ofno = sourceSaleNo;//退单ofno的来源单号
            
            
            String warehouseNo = "";//默认为空。
            String warehouseName = "";
            
            String loadDocType = ordermap.get("LOADDOCTYPE").toString();
            String orderNo = ordermap.get("ORDERNO").toString();
            //单据状态: 0.待审核1.订单开立 2.已接单 3. 已拒单 8.待提货 9.待发货 10.已发货 11.已完成 12.整单已退单
//            String status = ordermap.get("STATUS").toString();
            if(!orderLoadDocType.MINI.equals(loadDocType)&&goods!=null&&goods.size()>0){
            	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
						"非小程序订单不支持部分退!");
            }
            
            //如果操作门店和销售单下单门店不一致，查询传入得门店的仓库
            if (opShopId!=null&&!opShopId.trim().isEmpty()&&!opShopId.equals(shopid))
            {
                try
                {
                    String sql_warehouse = "select A.OUT_COST_WAREHOUSE,AL.WAREHOUSE_NAME from dcp_org  A "
                            + " left join dcp_warehouse_lang AL on A.EID=AL.EID  AND   A.ORGANIZATIONNO=AL.ORGANIZATIONNO AND A.OUT_COST_WAREHOUSE = AL.WAREHOUSE AND AL.LANG_TYPE='zh_CN' "
                            + " where  A.EID='"+eId+"' and A.ORGANIZATIONNO ='"+opShopId+"' ";
                    
                    HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【操作门店和原销售单shopid不一致】【获取操作门店的仓库】查询操作门店仓库sql="+sql_warehouse+",opShopId="+opShopId+"， 原销售单号saleNo="+sourceSaleNo);
                    List<Map<String, Object>> getShippingshopWarehouseInfo = StaticInfo.dao.executeQuerySQL(sql_warehouse, null);
                    if(getShippingshopWarehouseInfo==null||getShippingshopWarehouseInfo.isEmpty())
                    {
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【获取操作门店的仓库】查询操作门店仓库为空, 原销售单号saleNo="+sourceSaleNo);
                    }
                    else
                    {
                        warehouseNo = getShippingshopWarehouseInfo.get(0).get("OUT_COST_WAREHOUSE").toString();
                        warehouseName = getShippingshopWarehouseInfo.get(0).get("WAREHOUSE_NAME").toString();
                        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【获取操作门店的仓库】查询操作门店opShopId="+opShopId+"，对应的OUT_COST_WAREHOUSE仓库="+warehouseNo+", 原销售单号saleNo="+sourceSaleNo);
                    }
                }
                catch (Exception e)
                {
                
                }
                
            }

            //refundType 退单类型 0：全退,1：部分退
            String refundType=recalculateMap.get("refundType")==null?"":recalculateMap.get("refundType").toString();
            boolean isAllRefund=true;
 
            if(goods!=null&&goods.size()>0){
            	isAllRefund=false;
            	if(Check.Null(refundType)||"0".equals(refundType)){
            		isAllRefund=true;
            	}
            }
            
            //全退走原先逻辑
            if(isAllRefund){
            	//生成单头语句
                StringBuffer strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE (");//分区字段
                strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,PARTITION_DATE,GROUPBUYING,PARTNERMEMBER )");//PARTITION_DATE分区字段处理
                
                strBuff.append("select eid,");
                //shopid
                if (opShopId==null||opShopId.trim().isEmpty())
                {
                    strBuff.append("shopid,");
                }
                else
                {
                    strBuff.append("'"+ opShopId +"',");
                }
                strBuff.append("'"+ saleno +"',trno,ver_num,legalper,");
                //machine
                if (machineNo==null||machineNo.trim().isEmpty())
                {
                    strBuff.append("machine,");
                }
                else
                {
                    strBuff.append("'"+machineNo+"',");
                }
                //type,typename,bdate
                strBuff.append(type+",'"+typename+"','"+bdate+"',");
                //squadno
                if (squadNo==null||squadNo.trim().isEmpty())
                {
                    strBuff.append("squadno,");
                }
                else
                {
                    strBuff.append("'"+squadNo+"',");
                }
                //workno
                if (workNo==null||workNo.trim().isEmpty())
                {
                    strBuff.append("workno,");
                }
                else
                {
                    strBuff.append("'"+workNo+"',");
                }
                //opno
                if (opNo==null||opNo.trim().isEmpty())
                {
                    strBuff.append("opno,");
                }
                else
                {
                    strBuff.append("'"+opNo+"',");
                }
                strBuff.append("authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,'"+bdate+"',GROUPBUYING,NVL(PARTNERMEMBER,'digiwin') AS PARTNERMEMBER ");//PARTITION_DATE分区字段处理
                strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                String execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                //生成商品单身
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                        + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                        + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE,UNITRATIO,BASEQTY )");//PARTITION_DATE分区字段处理
                
                strBuff.append(" select eid, ");
                
                //shopid
                if (opShopId==null||opShopId.trim().isEmpty())
                {
                    strBuff.append("shopid,");
                }
                else
                {
                    strBuff.append("'"+ opShopId +"',");
                }
                
                strBuff.append("'"+ saleno +"',");
                
                //warehouseNo
                if (warehouseNo==null||warehouseNo.trim().isEmpty())
                {
                    strBuff.append("warehouse,");
                }
                else
                {
                    strBuff.append("'"+ warehouseNo +"',");
                }
                
                strBuff.append(" item, oitem, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                        + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                        + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"' ,nvl(UNITRATIO,1),nvl(BASEQTY,1) ");//PARTITION_DATE分区字段处理
                
                strBuff.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail));
                
                //生成商品单身折扣
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL_agio (");//分区字段
                strBuff.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                
                strBuff.append(" select  eid, ");
                //shopid
                if (opShopId==null||opShopId.trim().isEmpty())
                {
                    strBuff.append("shopid,");
                }
                else
                {
                    strBuff.append("'"+ opShopId +"',");
                }
                strBuff.append("'"+saleno+"', mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                strBuff.append(" from DCP_SALE_DETAIL_agio where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail_agio = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail_agio));
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE )");//PARTITION_DATE分区字段处理
                strBuff.append(" select eid,");
                //shopid
                if (opShopId==null||opShopId.trim().isEmpty())
                {
                    strBuff.append("shopid,");
                }
                else
                {
                    strBuff.append("'"+ opShopId +"',");
                }
                strBuff.append("'"+saleno+"', item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"'");//PARTITION_DATE分区字段处理
                
                strBuff.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));
                
                
                //交班汇总
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_STATISTIC_INFO (");
                strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
                strBuff.append(" select EID, ");
                if (opShopId==null||opShopId.trim().isEmpty())
                {
                    strBuff.append("SHOPID,");
                }
                else
                {
                    strBuff.append("'"+ opShopId +"',");
                }
                //MACHINE
                if (machineNo==null||machineNo.trim().isEmpty())
                {
                    strBuff.append("MACHINE, ");
                }
                else
                {
                    strBuff.append("'"+machineNo+"',");
                }
                //opno
                if (opNo==null||opNo.trim().isEmpty())
                {
                    strBuff.append("OPNO, ");
                }
                else
                {
                    strBuff.append("'"+opNo+"',");
                }
                //squadno
                if (squadNo==null||squadNo.trim().isEmpty())
                {
                    strBuff.append("SQUADNO, ");
                }
                else
                {
                    strBuff.append("'"+squadNo+"',");
                }
                
                strBuff.append("'"+saleno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, '"+sdate+"', '"+stime+"', 'N', ");
                //workno
                if (workNo==null||workNo.trim().isEmpty())
                {
                    strBuff.append( "WORKNO, ");
                }
                else
                {
                    strBuff.append("'"+workNo+"',");
                }
                
                strBuff.append("1, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");
                
                strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' and ORDERNO='"+sourceSaleNo+"' and type=0");//收款TYPE3，退订TYPE4，销售TYPE0 ，销退TYPE1
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_statistic_info = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_statistic_info));
                
                //库存流水账生成
                strBuff = new StringBuffer("");
                strBuff.append(" select * from dcp_stock_detail where billtype=20");
                strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                List<Map<String, Object>> getQData_stockDetail = this.doQueryData(execsql, null);
                
                //流水表没有到历史流水表里查
                if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                {
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    PosPub.iTimeoutTime = 120;
                    try {
                        getQData_stockDetail = this.doQueryData(execsql, null);
                        PosPub.iTimeoutTime = 30;
                    }
                    catch (Exception e)
                    {
                        PosPub.iTimeoutTime = 30;
                    }
                    finally {
                        PosPub.iTimeoutTime = 30;
                    }
                    
                    
                }
                
                if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                {
                    String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopid);
                    String procedure="SP_DCP_StockChange";
                    Map<String,Object> stockSyncMap = new HashMap<>();
                    String shopId_stockSync = "";
                    for (Map<String, Object> map : getQData_stockDetail)
                    {
                        
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,map.get("EID").toString());                             //--企业ID
                        if (opShopId==null||opShopId.trim().isEmpty())
                        {
                            inputParameter.put(2,map.get("ORGANIZATIONNO").toString());         //--组织
                            shopId_stockSync = map.get("ORGANIZATIONNO").toString();
                        }
                        else
                        {
                            inputParameter.put(2,opShopId);
                            shopId_stockSync = opShopId;
                        }
                        inputParameter.put(3,"21");                                      //--单据类型
                        inputParameter.put(4,saleno);	                                 //--单据号
                        inputParameter.put(5,map.get("ITEM").toString());            //--单据行号
                        inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,bdate);           //--营业日期 yyyy-MM-dd
                        inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                        inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                        
                        if (warehouseNo==null||warehouseNo.trim().isEmpty())
                        {
                            inputParameter.put(10,map.get("WAREHOUSE").toString());//--仓库
                        }
                        else
                        {
                            inputParameter.put(10,warehouseNo);  //--仓库
                        }
                        inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                        inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                        inputParameter.put(13,map.get("QTY").toString());           //--交易数量
                        inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                        inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                        inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                        inputParameter.put(17,map.get("PRICE").toString());          //--零售价
                        inputParameter.put(18,map.get("AMT").toString());            //--零售金额
                        inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                        inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                        inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                        inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(23,bdate);                                  //--单据日期
                        inputParameter.put(24,"");                                       //--异动原因
                        inputParameter.put(25,"销售单退单");                                //--异动描述
                        inputParameter.put(26,"");                                //--操作员
                        
                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        DataPB.add(new DataProcessBean(pdb));
                        
                    }
                    stockSyncMap.put("eId",eId);
                    stockSyncMap.put("shopId",shopId_stockSync);
                    stockSyncMap.put("billNo",saleno);
                    stockSyncList.add(stockSyncMap);
                }
            }
            //部分退
            else{
            	
            	//本次退款金额
            	BigDecimal totAmt=new BigDecimal((tot_amt!=null&&tot_amt.trim().length()>0)?tot_amt:"0");
//            	//标记金额已退完/最后一次退款  用来判断是否要退券 1-已退完
//                String isAllRefund=recalculateMap.get("isAllRefund")==null?"":recalculateMap.get("isAllRefund").toString();
            	
            	//列表SQL
        		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
        		//说明此时是部分退货
            	//TYPE	NUMBER	Yes	0	9	单据类型:0-销售单1-凭单退货2-无单退货
            	String saleSql="select * from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and ofno='"+sourceSaleNo+"' and type='1'";
            	List<Map<String, Object>> saleMaps = this.doQueryData(saleSql, null);
            	if(saleMaps!=null&&saleMaps.size()>0){
            		saleno+="_"+String.valueOf(saleMaps.size()+1);
            	}
            	
            	List<String> goodsArray=new ArrayList<String>();
            	if(goods!=null&&goods.size()>0){
            		goodsArray=goods.stream().map(x->String.valueOf(x.getItem())).collect(Collectors.toList());
                	goodsArray = goodsArray.stream().distinct().collect(Collectors.toList());
                	goodsArray=goodsArray.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                    if(goodsArray!=null&&goodsArray.size()>0){
                    	//说明此时是部分退货
                    	String saleDetailSql="select * from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ";
                    	dcpSaleDetailMaps = this.doQueryData(saleDetailSql, null);
//                    	if(dcpSaleDetailMaps==null||dcpSaleDetailMaps.size()==0){
//                    		saleDetailSql="select * from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ";
//                        	dcpSaleDetailMaps = this.doQueryData(saleDetailSql, null);
//                    	}
//                    	if(goods.size()!=dcpSaleDetailMaps.size()){
//                    		
//                    	}
                    }
            	}
            	
                //生成单头语句
                StringBuffer strBuff = new StringBuffer("");
                String execsql = "";
                
                
              //SALEAMT	NUMBER(23,8)	Yes	0	136	商品成交金额[含税]=单身商品成交金额AMT合计
                BigDecimal saleAmt=BigDecimal.ZERO;
                //SALEDISC	NUMBER(23,8)	Yes		68	商品折扣金额(含税)=单身商品折扣DISC合计
                BigDecimal saleDiscTot=BigDecimal.ZERO;
                //TOT_QTY	NUMBER(23,8)	Yes		65	总数量
                BigDecimal totQty=BigDecimal.ZERO;
                //TOT_OLDAMT	NUMBER(23,8)	Yes		66	原总金额(含税)=SALEAMT+PACKAGEFEE+SHIPPINGFEE
                BigDecimal totOldamt=BigDecimal.ZERO;
                if(dcpSaleDetailMaps!=null&&dcpSaleDetailMaps.size()>0){
                	//查询商品单身折扣
                    String agiosql="select * from DCP_SALE_DETAIL_AGIO where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and mitem in('"+String.join("','",goodsArray)+"') ";
                	List<Map<String, Object>> agioMaps = this.doQueryData(agiosql, null);
                	
                	for(Map<String, Object> detailMap:dcpSaleDetailMaps){
//                		lstData=new ArrayList<DataProcessBean>();
                		String item=detailMap.get("ITEM")==null?"":detailMap.get("ITEM").toString();
                		List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> thisgoods=goods.stream().filter(g->g.getItem().equals(item)).collect(Collectors.toList());
                		//本次退数量
                		BigDecimal rqty=BigDecimal.ZERO;
                		if(thisgoods!=null&&thisgoods.size()==1){
                			rqty=new BigDecimal(String.valueOf(thisgoods.get(0).getQty()));
                		}
                		//单身商品总数量
                		BigDecimal qty =new BigDecimal(detailMap.get("QTY")==null?"0":detailMap.get("QTY").toString());
                		BigDecimal oldprice =new BigDecimal(detailMap.get("OLDPRICE")==null?"0":detailMap.get("OLDPRICE").toString());
                		BigDecimal price =new BigDecimal(detailMap.get("PRICE")==null?"0":detailMap.get("PRICE").toString());
                		BigDecimal oldamount=oldprice.multiply(rqty);
                		BigDecimal amount=price.multiply(rqty);
                		
                		//AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	87	商家实收金额
                		BigDecimal amtMerreceive=amount;
                		//AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	89	顾客实付金额
                		BigDecimal amtCustpayreal=amount;
                		BigDecimal disc=oldamount.subtract(amount);
                		BigDecimal saleDisc=disc;
                		
                		//加总 计算单身数据
                		saleAmt=saleAmt.add(amount);
                		saleDiscTot=saleDiscTot.add(disc);
                		totQty=totQty.add(rqty);
                		totOldamt=totOldamt.add(oldamount);
                		
                		//生成商品单身 DCP_SALE_DETAIL
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                        strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE,UNITRATIO,BASEQTY )");//PARTITION_DATE分区字段处理
                        
                        strBuff.append(" select eid, ");
                        
                        //shopid
                        if (opShopId==null||opShopId.trim().isEmpty())
                        {
                            strBuff.append("shopid,");
                        }
                        else
                        {
                            strBuff.append("'"+ opShopId +"',");
                        }
                        
                        strBuff.append("'"+ saleno +"',");
                        
                        //warehouseNo
                        if (warehouseNo==null||warehouseNo.trim().isEmpty())
                        {
                            strBuff.append("warehouse,");
                        }
                        else
                        {
                            strBuff.append("'"+ warehouseNo +"',");
                        }
                        
                        strBuff.append(" item, oitem, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                + ", '"+rqty+"', '"+oldprice+"', price2, price3, canback, bsno, '"+rqty+"', returnuserid, returntableno, refundopno, refundtime, '"+oldamount+"', '"+disc+"', '"+saleDisc+"', paydisc, '"+price+"', additionalprice, '"+amount+"', point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, '"+amtMerreceive+"', DISC_CUSTPAYREAL, '"+amtCustpayreal+"','"+bdate+"' ,nvl(UNITRATIO,1),nvl(BASEQTY,1) ");//PARTITION_DATE分区字段处理
                        
                        strBuff.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item='"+item+"' ");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_detail = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_detail));
                        
                        if(agioMaps!=null&&agioMaps.size()>0){
                        	//DCP_SALE_DETAIL_AGIO
                        	List<Map<String, Object>> newagioMaps=agioMaps.stream().filter(g->g.get("MITEM").toString().equals(item)).collect(Collectors.toList());
                        	if(newagioMaps!=null&&newagioMaps.size()>0){
                        		BigDecimal agiodiscTot=BigDecimal.ZERO;
                        		int agioCount=0;
                    			for(Map<String, Object> agioMap:newagioMaps){
                    				agioCount++;
                    				String thisitem=agioMap.get("ITEM")==null?"":agioMap.get("ITEM").toString();
                    				//QTY	NUMBER(23,8)	Yes		6	参与数量
                    				BigDecimal agioqty =new BigDecimal(agioMap.get("QTY")==null?"0":agioMap.get("QTY").toString());
                    				//AMT	NUMBER(23,8)	Yes		7	参与金额
                    				BigDecimal agioamt =new BigDecimal(agioMap.get("AMT")==null?"0":agioMap.get("AMT").toString());
                    				agioamt=agioamt.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				//DISC	NUMBER(23,8)	Yes		8	折扣金额
                    				BigDecimal agiodisc =new BigDecimal(agioMap.get("DISC")==null?"0":agioMap.get("DISC").toString());
                    				agiodisc=agiodisc.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				if(agioCount==newagioMaps.size()){
                    					agiodisc=disc.subtract(agiodiscTot);
                    				}
                    				
                    				//REALDISC	NUMBER(23,8)	Yes		9	实际折扣金额
                    				BigDecimal agioreal =new BigDecimal(agioMap.get("REALDISC")==null?"0":agioMap.get("REALDISC").toString());
                    				agioreal=agioreal.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				
                    				//生成商品单身折扣
                                    strBuff = new StringBuffer("");
                                    strBuff.append(" insert into DCP_SALE_DETAIL_agio (");//分区字段
                                    strBuff.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                                    
                                    strBuff.append(" select  eid, ");
                                    //shopid
                                    if (opShopId==null||opShopId.trim().isEmpty())
                                    {
                                        strBuff.append("shopid,");
                                    }
                                    else
                                    {
                                        strBuff.append("'"+ opShopId +"',");
                                    }
                                    strBuff.append("'"+saleno+"', mitem, item, '"+rqty+"', '"+agioamt+"', '"+agiodisc+"', '"+agioreal+"', dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                                    strBuff.append(" from DCP_SALE_DETAIL_agio where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and MITEM='"+item+"' and ITEM='"+thisitem+"' ");
                                    execsql = "";
                                    execsql = strBuff.toString();
                                    ExecBean exSale_detail_agio = new ExecBean(execsql);
                                    lstData.add(new DataProcessBean(exSale_detail_agio));
                                    

                                    agiodiscTot=agiodiscTot.add(agiodisc);
                    			}
                        	}
                        }
//                        StaticInfo.dao.useTransactionProcessData(lstData);
                	}
                	
                }
                
                BigDecimal l=BigDecimal.ZERO;
                if(totAmt.compareTo(saleAmt)!=0){
                	l=totAmt.subtract(saleAmt);
                }
                
                totOldamt=totOldamt.add(l);
                //TOT_DISC	NUMBER(23,8)	Yes		67	折扣金额(含税)
                BigDecimal totDisc=saleDiscTot;
                //TOT_AMT	NUMBER(23,8)	Yes		71	应付金额(含税)
                BigDecimal TOT_AMT=saleAmt.add(l);
                //PAY_AMT	NUMBER(23,8)	Yes		86	已收抵账金额(含税)=所有支付金额之和，去除找零和溢收
                BigDecimal PAY_AMT=saleAmt.add(l);
                //SHOPINCOME	NUMBER(23,8)	Yes		105	外卖：商家实际到账金额
                BigDecimal SHOPINCOME=saleAmt.add(l);
                //TOT_AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	131	商家实收金额(含税)=付款时商家实收金额
                BigDecimal TOT_AMT_MERRECEIVE=saleAmt.add(l);
                //TOT_AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	132	顾客实付金额[含税]=付款时客户实际支付金额
                BigDecimal TOT_AMT_CUSTPAYREAL=saleAmt.add(l);
                {
                    
                    //生成单头语句 DCP_SALE
                    //这几项直接给0
                    //DELIVERY_FEE_SHOP	NUMBER(20,2)	Yes		100	外卖：门店承担配送费
                    //DELIVERY_FEE_USER	NUMBER(20,2)	Yes		101	外卖：用户承担配送费
                    //SHIPPINGFEE	NUMBER(23,8)	Yes		99	外卖：配送费
//                    lstData=new ArrayList<DataProcessBean>();
                	strBuff = new StringBuffer("");
                    strBuff.append(" insert into DCP_SALE (");//分区字段
                    strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                            + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                            + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                            + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                            + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,PARTITION_DATE,GROUPBUYING,PARTNERMEMBER )");//PARTITION_DATE分区字段处理
                    
                    strBuff.append("select eid,");
                    //shopid
                    if (opShopId==null||opShopId.trim().isEmpty())
                    {
                        strBuff.append("shopid,");
                    }
                    else
                    {
                        strBuff.append("'"+ opShopId +"',");
                    }
                    strBuff.append("'"+ saleno +"',trno,ver_num,legalper,");
                    //machine
                    if (machineNo==null||machineNo.trim().isEmpty())
                    {
                        strBuff.append("machine,");
                    }
                    else
                    {
                        strBuff.append("'"+machineNo+"',");
                    }
                    //type,typename,bdate
                    strBuff.append(type+",'"+typename+"','"+bdate+"',");
                    //squadno
                    if (squadNo==null||squadNo.trim().isEmpty())
                    {
                        strBuff.append("squadno,");
                    }
                    else
                    {
                        strBuff.append("'"+squadNo+"',");
                    }
                    //workno
                    if (workNo==null||workNo.trim().isEmpty())
                    {
                        strBuff.append("workno,");
                    }
                    else
                    {
                        strBuff.append("'"+workNo+"',");
                    }
                    //opno
                    if (opNo==null||opNo.trim().isEmpty())
                    {
                        strBuff.append("opno,");
                    }
                    else
                    {
                        strBuff.append("'"+opNo+"',");
                    }
                    strBuff.append("authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                            + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                            + ",'"+totQty+"','"+totOldamt+"','"+totDisc+"','"+saleDiscTot+"',paydisc,erase_amt,'"+TOT_AMT+"',servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,'"+PAY_AMT+"',tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                            + ",order_id,order_sn,platform_disc,seller_disc,packagefee,[shippingfee],[delivery_fee_shop],[delivery_fee_user],wm_user_paid,platform_fee,wm_extra_fee,'"+SHOPINCOME+"',productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                            + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, '"+TOT_AMT_MERRECEIVE+"', '"+TOT_AMT_CUSTPAYREAL+"', TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,'"+bdate+"',GROUPBUYING,NVL(PARTNERMEMBER,'digiwin') AS PARTNERMEMBER ");//PARTITION_DATE分区字段处理
                    strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    
                    //isRefundDeliverAmt;//是否退运费 0否1是
                    if("1".equals(isRefundDeliverAmt)){
                    	//退运费
                    	execsql=execsql.replace("[shippingfee]", "shippingfee");
                    	execsql=execsql.replace("[delivery_fee_shop]", "delivery_fee_shop");
                    	execsql=execsql.replace("[delivery_fee_user]", "delivery_fee_user");
                    }else{
                    	//不退运费
                    	execsql=execsql.replace("[shippingfee]", "0");
                    	execsql=execsql.replace("[delivery_fee_shop]", "0");
                    	execsql=execsql.replace("[delivery_fee_user]", "0");
                    }
                    
                    ExecBean exSale = new ExecBean(execsql);
                    lstData.add(new DataProcessBean(exSale));
                }
                
                String crmPaySql=""
                		+ " SELECT A.*,B.PAYCODEERP,C.AMOUNT,C.CUSTPAYREAL,C.MERRECEIVE,C.NOCODE,C.PURPOSE FROM "
                		+ " (SELECT EID,BILLNO,PAYCHANNEL,MAX(SERIALNO) AS SERIALNO "
                		+ " FROM CRM_ORDERPAY WHERE EID='"+eId+"' AND BILLNO='"+orderNo+"' "//AND PURPOSE='3'
                		+ " GROUP BY EID,BILLNO,PAYCHANNEL) A "
                		+ " LEFT JOIN DCP_PAYMENT B ON A.EID=B.EID AND A.PAYCHANNEL=B.PAYCODE "
                		+ " LEFT JOIN CRM_ORDERPAY C ON A.EID=C.EID AND A.BILLNO=C.BILLNO AND A.SERIALNO=C.SERIALNO "
                		+ "";
                List<Map<String, Object>> crmPayMaps = this.doQueryData(crmPaySql, null);
                
                HelpTools.writelog_waimai("【"+orderNo+"】crmPaySql"+crmPaySql);
                HelpTools.writelog_waimai("【"+orderNo+"】crmPayMaps"+com.alibaba.fastjson.JSONObject.toJSONString(crmPayMaps));
                
                //查询付款单DCP_SALE_PAY
                String paysql="select * from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ";
            	List<Map<String, Object>> payMaps = this.doQueryData(paysql, null);
            	if(payMaps!=null&&payMaps.size()>0){
            		BigDecimal payTot=BigDecimal.ZERO;
            		BigDecimal merreceiveTot=BigDecimal.ZERO;
            		BigDecimal custpayrealTot=BigDecimal.ZERO;
            		int payCount=0;
            		for(Map<String, Object> payMap:payMaps){
            			payCount++;
            			//PAY	NUMBER(23,8)	Yes		9	交易金额，含溢收和找零；提货券时=商品原金额
            			BigDecimal pay =new BigDecimal(payMap.get("PAY")==null?"0":payMap.get("PAY").toString());
            			pay=pay.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				pay=PAY_AMT.subtract(payTot);
        				}
            			//MERRECEIVE	NUMBER(23,8)	Yes	0	40	商家实收金额，移动支付用，例如支付宝，微信等
            			BigDecimal merreceive =new BigDecimal(payMap.get("MERRECEIVE")==null?"0":payMap.get("MERRECEIVE").toString());
            			merreceive=merreceive.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				merreceive=TOT_AMT_MERRECEIVE.subtract(merreceiveTot);
        				}
            			//CUSTPAYREAL	NUMBER(23,8)	Yes	0	42	客户实付金额：移动支付用，例如支付宝，微信等
            			BigDecimal custpayreal =new BigDecimal(payMap.get("CUSTPAYREAL")==null?"0":payMap.get("CUSTPAYREAL").toString());
            			custpayreal=custpayreal.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				custpayreal=TOT_AMT_CUSTPAYREAL.subtract(custpayrealTot);
        				}
            			String thisitem=payMap.get("ITEM")==null?"":payMap.get("ITEM").toString();
            			String paycodeerp=payMap.get("PAYCODEERP")==null?"":payMap.get("PAYCODEERP").toString();
            			String caradno=payMap.get("CARADNO")==null?"":payMap.get("CARADNO").toString();
            			
            			if(crmPayMaps!=null&&crmPayMaps.size()>0){
            				List<Map<String, Object>> newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("PAYCODEERP").equals(paycodeerp)).collect(Collectors.toList());
            				if(newcrmPayMaps!=null&&newcrmPayMaps.size()>0){
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()>1){
            						newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("NOCODE").equals(caradno)).collect(Collectors.toList());
            					}
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()==1){
            						Map<String, Object> newcrmMap=newcrmPayMaps.get(0);
            						String paychannel=newcrmMap.get("PAYCHANNEL")!=null&&newcrmMap.get("PAYCHANNEL").toString().trim().length()>0?newcrmMap.get("PAYCHANNEL").toString():"";
            						String purpose=newcrmMap.get("PURPOSE")!=null&&newcrmMap.get("PURPOSE").toString().trim().length()>0?newcrmMap.get("PURPOSE").toString():"";
            						//支付通道#03会员卡、#04券、#05积分、#P1微信支付
    								if("1".equals(purpose)&&"#04".equals(paychannel)){
    									//未退完时，券不需要反写
    									continue;	
    								}
            						pay=new BigDecimal(newcrmMap.get("AMOUNT")!=null&&newcrmMap.get("AMOUNT").toString().trim().length()>0
            								?newcrmMap.get("AMOUNT").toString():"0");
            						
            						merreceive=new BigDecimal(newcrmMap.get("MERRECEIVE")!=null&&newcrmMap.get("MERRECEIVE").toString().trim().length()>0
            								?newcrmMap.get("MERRECEIVE").toString():"0");
            						custpayreal=new BigDecimal(newcrmMap.get("CUSTPAYREAL")!=null&&newcrmMap.get("CUSTPAYREAL").toString().trim().length()>0
            								?newcrmMap.get("CUSTPAYREAL").toString():"0");
            					}
            				}
            			}
            			
            			//生成付款单
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                        strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE )");//PARTITION_DATE分区字段处理
                        strBuff.append(" select eid,");
                        //shopid
                        if (opShopId==null||opShopId.trim().isEmpty())
                        {
                            strBuff.append("shopid,");
                        }
                        else
                        {
                            strBuff.append("'"+ opShopId +"',");
                        }
                        strBuff.append("'"+saleno+"', item, paydoctype, paycode, paycodeerp, payname, '"+pay+"', pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, '"+merreceive+"', THIRDDISCOUNT, '"+custpayreal+"', COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"'");//PARTITION_DATE分区字段处理
                        
                        strBuff.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item='"+thisitem+"' ");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_pay = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_pay));
                        
                        payTot=payTot.add(pay);
                        merreceiveTot=merreceiveTot.add(merreceive);
                        custpayrealTot=custpayrealTot.add(custpayreal);
            		}
            	}
                
                
            	//交班汇总DCP_STATISTIC_INFO
            	String statisticsql="select * from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' and orderno='"+sourceSaleNo+"' and type=0 ";
            	List<Map<String, Object>> statisticMaps = this.doQueryData(statisticsql, null);
            	if(statisticMaps!=null&&statisticMaps.size()>0){
            		BigDecimal amtTot=BigDecimal.ZERO;
            		int statisticCount=0;
            		for(Map<String, Object> statisticMap:statisticMaps){
            			statisticCount++;
                        String thisitem=statisticMap.get("ITEM")==null?"":statisticMap.get("ITEM").toString();
                        String thisopno=statisticMap.get("OPNO")==null?"":statisticMap.get("OPNO").toString();
                        
                        //AMT	NUMBER(23,8)	Yes		11	金额,存储整数,不含找零，含溢收
                        BigDecimal amt =new BigDecimal(statisticMap.get("AMT")==null?"0":statisticMap.get("AMT").toString());
                        amt=amt.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(statisticCount==statisticMaps.size()){
            				amt=PAY_AMT.subtract(amtTot);
        				}
            			
            			//交班汇总
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_STATISTIC_INFO (");
                        strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
                        strBuff.append(" select EID, ");
                        if (opShopId==null||opShopId.trim().isEmpty())
                        {
                            strBuff.append("SHOPID,");
                        }
                        else
                        {
                            strBuff.append("'"+ opShopId +"',");
                        }
                        //MACHINE
                        if (machineNo==null||machineNo.trim().isEmpty())
                        {
                            strBuff.append("MACHINE, ");
                        }
                        else
                        {
                            strBuff.append("'"+machineNo+"',");
                        }
                        //opno
                        if (opNo==null||opNo.trim().isEmpty())
                        {
                            strBuff.append("OPNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+opNo+"',");
                        }
                        //squadno
                        if (squadNo==null||squadNo.trim().isEmpty())
                        {
                            strBuff.append("SQUADNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+squadNo+"',");
                        }
                        
                        strBuff.append("'"+saleno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, '"+amt+"', '"+sdate+"', '"+stime+"', 'N', ");
                        //workno
                        if (workNo==null||workNo.trim().isEmpty())
                        {
                            strBuff.append( "WORKNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+workNo+"',");
                        }
                        
                        strBuff.append("1, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");
                        
                        strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' and ORDERNO='"+sourceSaleNo+"' and type=0 ");//收款TYPE3，退订TYPE4，销售TYPE0 ，销退TYPE1
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_statistic_info = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_statistic_info));
                        
                        amtTot=amtTot.add(amt);
                        
            		}
            	}
                
                
            	//库存流水
            	if(goodsArray!=null&&goodsArray.size()>0){
            		//库存流水账生成
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ");
                    execsql = "";
                    execsql = strBuff.toString();
                    List<Map<String, Object>> getQData_stockDetail = this.doQueryData(execsql, null);
                    
                    //流水表没有到历史流水表里查
                    if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                    {
                        strBuff = new StringBuffer("");
                        strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                        strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ");
                        execsql = "";
                        execsql = strBuff.toString();
                        PosPub.iTimeoutTime = 120;
                        try {
                            getQData_stockDetail = this.doQueryData(execsql, null);
                            PosPub.iTimeoutTime = 30;
                        }
                        catch (Exception e)
                        {
                            PosPub.iTimeoutTime = 30;
                        }
                        finally {
                            PosPub.iTimeoutTime = 30;
                        }
                        
                        
                    }
                    
                    if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                    {
                        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopid);
                        String procedure="SP_DCP_StockChange";
                        Map<String,Object> stockSyncMap = new HashMap<>();
                        String shopId_stockSync = "";
                        for (Map<String, Object> map : getQData_stockDetail)
                        {
                        	String thisitem=map.get("ITEM").toString();
                        	BigDecimal thisqty=BigDecimal.ZERO;
                        	List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> thisgoods=goods.stream().filter(g->g.getItem().equals(thisitem)).collect(Collectors.toList());
                    		if(thisgoods!=null&&thisgoods.size()==1){
                    			thisqty=new BigDecimal(String.valueOf(thisgoods.get(0).getQty()));
                    		}
                    		BigDecimal price =new BigDecimal(map.get("PRICE")==null?"0":map.get("PRICE").toString());
                    		BigDecimal amt=price.multiply(thisqty);
                    		
                            Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1,map.get("EID").toString());                             //--企业ID
                            if (opShopId==null||opShopId.trim().isEmpty())
                            {
                                inputParameter.put(2,map.get("ORGANIZATIONNO").toString());         //--组织
                                shopId_stockSync = map.get("ORGANIZATIONNO").toString();
                            }
                            else
                            {
                                inputParameter.put(2,opShopId);
                                shopId_stockSync = opShopId;
                            }
                            inputParameter.put(3,"21");                                      //--单据类型
                            inputParameter.put(4,saleno);	                                 //--单据号
                            inputParameter.put(5,thisitem);            //--单据行号
                            inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,bdate);           //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                            inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                            
                            if (warehouseNo==null||warehouseNo.trim().isEmpty())
                            {
                                inputParameter.put(10,map.get("WAREHOUSE").toString());//--仓库
                            }
                            else
                            {
                                inputParameter.put(10,warehouseNo);  //--仓库
                            }
                            inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                            inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                            inputParameter.put(13,thisqty);           //--交易数量
                            inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                            inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                            inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                            inputParameter.put(17,price.toPlainString());          //--零售价
                            inputParameter.put(18,amt.toPlainString());            //--零售金额
                            inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                            inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                            inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,bdate);                                  //--单据日期
                            inputParameter.put(24,"");                                       //--异动原因
                            inputParameter.put(25,"销售单退单");                                //--异动描述
                            inputParameter.put(26,"");                                //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            DataPB.add(new DataProcessBean(pdb));
                            
                        }
                        stockSyncMap.put("eId",eId);
                        stockSyncMap.put("shopId",shopId_stockSync);
                        stockSyncMap.put("billNo",saleno);
                        stockSyncList.add(stockSyncMap);
                    }
                    
            	}
            	StaticInfo.dao.useTransactionProcessData(lstData);
                
                
                
            }
            
            
            
        }
        
        return DataPB;
    }
    
    /**
     * 根据原订单号生成反向的退订单
     * @param eId 企业ID
     * @param bDate 退订日期
     * @param sourceOrderNo 原订单号
     * @param opNo 操作员编码
     * @param machineNo 机台编码
     * @param workNo 当前班别
     * @param squadNo 当前班次
     * @return
     * @throws Exception
     */
    public ArrayList<DataProcessBean> getReturnOrderSql(DsmDAO dao,String eId,String bDate,String loadDocType,String sourceOrderNo,
    		String opNo,String machineNo,String workNo,String squadNo,List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods,
    		String tot_amt,String isRefundDeliverAmt,Map<String,Object> recalculateMap) throws Exception
    {
        ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
        String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stime = new SimpleDateFormat("HHmmss").format(new Date());
        String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String tran_time = update_time;
        String bdate = bDate;
        //退单单号
        String orderno = "RE"+sourceOrderNo;
        if (orderLoadDocType.XIAOYOU.equals(loadDocType))
        {
            orderno = refundOrderNo_para;//晓柚渠道，退订单号是有他们传入
        }
        String refundsourcebillno = sourceOrderNo;
        
        String billno = UUID.randomUUID().toString().replace("-", "");//收款单号
        String billtype = "-1";//单据类型
        String direction = "-1";//金额方向:1、-1
        String usetype = "refund";//款项用途：front-预付款 refund-退款 final-尾款
        
        if (squadNo==null||squadNo.trim().isEmpty())
        {
            squadNo = "0";//默认0
        }
        
        //refundType 退单类型 0：全退,1：部分退
        String refundType=recalculateMap.get("refundType")==null?"":recalculateMap.get("refundType").toString();
        boolean isAllRefund=true;

        if(goods!=null&&goods.size()>0){
        	isAllRefund=false;
        	if(Check.Null(refundType)||"0".equals(refundType)){
        		isAllRefund=true;
        	}
        }
        
        //全退走原先逻辑
        if(isAllRefund){
        	//生成单头语句
            StringBuffer strBuff = new StringBuffer("");
            strBuff.append(" insert into DCP_ORDER (");//分区字段
            strBuff.append(" eid, billtype, orderno, requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                    + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, refundamt,REFUNDAMT_MERRECEIVE,REFUNDAMT_CUSTPAYREAL"
                    + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                    + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, refundsourcebillno, refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                    + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                    + ", stime, create_datetime, complete_datetime, bdate, tran_time, update_time, process_status, peopletype, printcount, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,DOWNGRADED,WAIMAIMERRECEIVEMODE,PARTITION_DATE,GROUPBUYING,PARTNERMEMBER )");
            
            strBuff.append("select eid, '"+billtype+"', '"+orderno+"', requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, ");
            //MACHINE
            if (machineNo==null||machineNo.trim().isEmpty())
            {
                strBuff.append("machine, ");
            }
            else
            {
                strBuff.append("'"+machineNo+"',");
            }
            
            strBuff.append("ver_num, ");
            //squadno
            if (squadNo==null||squadNo.trim().isEmpty())
            {
                strBuff.append("squadno, ");
            }
            else
            {
                strBuff.append("'"+squadNo+"',");
            }
            //workno
            if (workNo==null||workNo.trim().isEmpty())
            {
                strBuff.append("workno, ");
            }
            else
            {
                strBuff.append("'"+workNo+"',");
            }
            //opno
            if (opNo==null||opNo.trim().isEmpty())
            {
                strBuff.append("opno, ");
            }
            else
            {
                strBuff.append("'"+opNo+"',");
            }
            
            strBuff.append("isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                    + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, '"+deliveryType+"', '"+deliveryNo+"', deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, payamt,tot_amt_merreceive,tot_amt_custpayreal"
                    + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                    + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, '"+refundsourcebillno+"', refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                    + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                    + ", '"+tran_time+"', '"+tran_time+"', '', '"+bdate+"', '"+tran_time+"', '"+update_time+"', 'N', peopletype, 0, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,DOWNGRADED,WAIMAIMERRECEIVEMODE,'"+bdate+"',GROUPBUYING,PARTNERMEMBER ");
            
            strBuff.append(" from DCP_ORDER where eid='"+eId+"'  and orderno='"+sourceOrderNo+"' ");
            String execsql = strBuff.toString();
            ExecBean exSale = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale));
            
            //生成商品单身
            strBuff = new StringBuffer("");
            strBuff.append(" insert into DCP_ORDER_DETAIL (");//分区字段
            strBuff.append(" eid, orderno, item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, oitem, oreitem, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                    + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, stime, tran_time, RUNPICKQTY, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//增加分区字段
            
            strBuff.append("select eid, '"+orderno+"', item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, item, item, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                    + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, '"+tran_time+"', '"+tran_time+"' , qty-pickqty, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL ,'"+bdate+"' ");//增加分区字段
            
            strBuff.append(" from DCP_ORDER_DETAIL where rqty>=0 and eid='"+eId+"' and orderno='"+sourceOrderNo+"' ");
            execsql = "";
            execsql = strBuff.toString();
            ExecBean exSale_detail = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_detail));
            
            //生成商品单身折扣
            strBuff = new StringBuffer("");
            strBuff.append(" insert into DCP_ORDER_DETAIL_agio (");//分区字段
            strBuff.append(" eid, orderno, mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
            
            strBuff.append(" select eid, '"+orderno+"', mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"' ");//增加分区字段
            
            strBuff.append(" from DCP_ORDER_DETAIL_agio where eid='"+eId+"' and orderno='"+sourceOrderNo+"' ");
            execsql = "";
            execsql = strBuff.toString();
            ExecBean exSale_detail_agio = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_detail_agio));
            
            //生成付款单
            strBuff = new StringBuffer("");
            strBuff.append(" insert into Dcp_Order_Pay_Detail (");//分区字段
            strBuff.append(" eid, billno, item, billdate, bdate, sourcebilltype, sourcebillno, loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime,FUNCNO,PAYDOCTYPE,SENDPAY,PAYTYPE, tran_time,SOURCEHEADBILLNO "
                    + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE,GAINCHANNEL,GAINCHANNELNAME )");
            strBuff.append(" select eid, '"+billno+"', item, '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime,FUNCNO,PAYDOCTYPE,SENDPAY,PAYTYPE, '"+tran_time+"','"+sourceOrderNo+"' "
                    + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"',GAINCHANNEL,GAINCHANNELNAME ");
            
            strBuff.append(" from Dcp_Order_Pay_Detail where eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' ");
            execsql = "";
            execsql = strBuff.toString();
            ExecBean exSale_pay_detail = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_pay_detail));
            
            //交班汇总
            strBuff = new StringBuffer("");
            strBuff.append(" insert into DCP_STATISTIC_INFO (");
            strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
            strBuff.append(" select EID, SHOPID, ");
            //MACHINE
            if (machineNo==null||machineNo.trim().isEmpty())
            {
                strBuff.append("MACHINE, ");
            }
            else
            {
                strBuff.append("'"+machineNo+"',");
            }
            
            //OPNO
            if (opNo==null||opNo.trim().isEmpty())
            {
                strBuff.append("OPNO, ");
            }
            else
            {
                strBuff.append("'"+opNo+"',");
            }
            //SQUADNO
            if (squadNo==null||squadNo.trim().isEmpty())
            {
                strBuff.append("SQUADNO, ");
            }
            else
            {
                strBuff.append("'"+squadNo+"',");
            }
            
            strBuff.append("'"+orderno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, '"+sdate+"', '"+stime+"', 'N', ");
            //WORKNO
            if (workNo==null||workNo.trim().isEmpty())
            {
                strBuff.append("WORKNO, ");
            }
            else
            {
                strBuff.append("'"+workNo+"',");
            }
            
            strBuff.append("4, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");
            
            strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and ORDERNO='"+sourceOrderNo+"' and type=3");
            execsql = "";
            execsql = strBuff.toString();
            ExecBean exSale_statistic_info = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_statistic_info));
            
            //生成付款单
            boolean isPayAdd = false;//是否存在多次付款(订单补录)
            String payrealamt = "0";
            String writeoffamt = "0";
            if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
            {
                isPayAdd = true;
                String sql_pay_sum = " select sum(PAYREALAMT) PAYREALAMT ,sum(WRITEOFFAMT) WRITEOFFAMT from Dcp_Order_pay where eid='"+eId+"'  and SOURCEBILLNO='"+sourceOrderNo+"' ";
                List<Map<String, Object>> getPayTotal = dao.executeQuerySQL(sql_pay_sum, null);
                if(getPayTotal!=null&&!getPayTotal.isEmpty())
                {
                    payrealamt = getPayTotal.get(0).getOrDefault("PAYREALAMT","0").toString();
                    writeoffamt = getPayTotal.get(0).getOrDefault("WRITEOFFAMT","0").toString();
                }
                if (payrealamt==null||payrealamt.isEmpty())
                {
                    payrealamt = "0";
                }
                if (writeoffamt==null||writeoffamt.isEmpty())
                {
                    writeoffamt = "0";
                }
            }
            strBuff = new StringBuffer("");
            strBuff.append(" insert into Dcp_Order_pay (");//分区字段
            strBuff.append("eid, billno, billdate, bdate, sourcebilltype, sourcebillno, companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, direction, payrealamt, writeoffamt, usetype, status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, tran_time, process_status,SOURCEHEADBILLNO,PARTITION_DATE )");
            strBuff.append(" select eid, '"+billno+"', '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', companyid, loaddoctype, shopid, channelid, ");
            //machineid
            if (machineNo==null||machineNo.trim().isEmpty())
            {
                strBuff.append("machineid, ");
            }
            else
            {
                strBuff.append("'"+machineNo+"',");
            }
            
            strBuff.append("customerno, ");
            
            //squadno
            if (squadNo==null||squadNo.trim().isEmpty())
            {
                strBuff.append("squadno, ");
            }
            else
            {
                strBuff.append("'"+squadNo+"',");
            }
            
            //workno
            if (workNo==null||workNo.trim().isEmpty())
            {
                strBuff.append("workno, ");
            }
            else
            {
                strBuff.append("'"+workNo+"',");
            }
            
            if (isPayAdd)
            {
                strBuff.append("-direction, "+payrealamt+", "+writeoffamt+", '"+usetype+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+sourceOrderNo+"', '"+bdate+"'");
            }
            else
            {
                strBuff.append("-direction, payrealamt, writeoffamt, '"+usetype+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+sourceOrderNo+"', '"+bdate+"'");
            }
            
            strBuff.append(" from Dcp_Order_pay where eid='"+eId+"'  and SOURCEBILLNO='"+sourceOrderNo+"' and rownum=1 ");
            execsql = "";
            execsql = strBuff.toString();
            ExecBean exSale_pay = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_pay));
            
            
            //生成促销参与明细
            if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
            {
                //来源业务类型：0-销售 1-退单 2-无单退货 3-订单 4-退订
                strBuff = new StringBuffer("");
                strBuff.append(" insert into PROM_MEMBER (");
                strBuff.append(" EID, ID, PROMNO, SDATE, SOURCEBILLTYPE, SOURBILLNO, MEMBERID, CUSTID, DIRECTION )");
                
                strBuff.append(" select eid, sys_guid(), PROMNO, SDATE,'4', SOURBILLNO, MEMBERID, CUSTID, -1 ");
                
                strBuff.append(" from PROM_MEMBER where  SOURCEBILLTYPE='3' and eid='"+eId+"' and SOURBILLNO='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_prom_member = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_prom_member));
            }
        }
        
        //部分退
        else{
        	//本次退款金额
        	BigDecimal totAmt=new BigDecimal((tot_amt!=null&&tot_amt.trim().length()>0)?tot_amt:"0");
//        	//标记金额已退完/最后一次退款  用来判断是否要退券 1-已退完
//            String isAllRefund=recalculateMap.get("isAllRefund")==null?"":recalculateMap.get("isAllRefund").toString();
            
        	Map<String, Object> orderMap=new HashMap<String, Object>();
        	//原单 总应付金额
        	BigDecimal saleAmtTot=BigDecimal.ZERO;
        	//查原单信息
        	String ordersql = "select * from dcp_order where eid='"+eId+"' and orderno='"+sourceOrderNo+"' ";
//        	String ordersql="select TOT_AMT FROM DCP_ORDER where eid='"+eId+"' and orderno='"+sourceOrderNo+"' ";
        	List<Map<String, Object>> orderMaps = dao.executeQuerySQL(ordersql, null);
        	if(orderMaps!=null&&orderMaps.size()>0){
        		orderMap=orderMaps.get(0);
        	}
        	
            String orderNo = orderMap.get("ORDERNO").toString();
          //单据状态: 0.待审核1.订单开立 2.已接单 3. 已拒单 8.待提货 9.待发货 10.已发货 11.已完成 12.整单已退单
//            String status = orderMap.get("STATUS").toString();
            
            if(!orderLoadDocType.MINI.equals(loadDocType)&&goods!=null&&goods.size()>0){
            	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
						"非小程序订单不支持部分退!");
            }
        	
//        	//原单 总应付金额
            saleAmtTot=new BigDecimal(orderMap.get("TOT_AMT")==null?"0":orderMap.get("TOT_AMT").toString());
        	List<Map<String, Object>> dcpSaleDetailMaps = null;
        	//列表SQL
        	String saleSql="select * from dcp_order where eid='"+eId+"' and refundsourcebillno='"+refundsourcebillno+"' and billtype='-1'";
        	List<Map<String, Object>> saleMaps = dao.executeQuerySQL(saleSql, null);
        	if(saleMaps!=null&&saleMaps.size()>0){
        		orderno+="_"+String.valueOf(saleMaps.size()+1);
        	}
        	
        	List<String> goodsArray=new ArrayList<String>();
        	if(goods!=null&&goods.size()>0){
        		goodsArray=goods.stream().map(x->String.valueOf(x.getItem())).collect(Collectors.toList());
            	goodsArray = goodsArray.stream().distinct().collect(Collectors.toList());
            	goodsArray=goodsArray.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                if(goodsArray!=null&&goodsArray.size()>0){
                	//说明此时是部分退货
                	String saleDetailSql="select * from DCP_ORDER_DETAIL where eid='"+eId+"' and orderno='"+sourceOrderNo+"'and item in('"+String.join("','",goodsArray)+"') ";
                	dcpSaleDetailMaps = dao.executeQuerySQL(saleDetailSql, null);
                }
        	}
        	
            
            //生成单头语句
            StringBuffer strBuff = new StringBuffer("");
            String execsql = "";
            
            
            //SALEAMT	NUMBER(23,8)	Yes	0	136	商品成交金额[含税]=单身商品成交金额AMT合计
            BigDecimal saleAmt=BigDecimal.ZERO;
            //SALEDISC	NUMBER(23,8)	Yes		68	商品折扣金额(含税)=单身商品折扣DISC合计
            BigDecimal saleDiscTot=BigDecimal.ZERO;
            //TOT_QTY	NUMBER(23,8)	Yes		65	总数量
            BigDecimal totQty=BigDecimal.ZERO;
            //TOT_OLDAMT	NUMBER(23,8)	Yes		66	原总金额(含税)=SALEAMT+PACKAGEFEE+SHIPPINGFEE
            BigDecimal totOldamt=BigDecimal.ZERO;
            if(dcpSaleDetailMaps!=null&&dcpSaleDetailMaps.size()>0){
            	//查询商品单身折扣
                String agiosql="select * from DCP_ORDER_DETAIL_AGIO where eid='"+eId+"' and orderno='"+sourceOrderNo+"' and mitem in('"+String.join("','",goodsArray)+"') ";
            	List<Map<String, Object>> agioMaps = dao.executeQuerySQL(agiosql, null);
            	
            	for(Map<String, Object> detailMap:dcpSaleDetailMaps){
//            		lstData=new ArrayList<DataProcessBean>();
            		String item=detailMap.get("ITEM")==null?"":detailMap.get("ITEM").toString();
            		List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> thisgoods=goods.stream().filter(g->g.getItem().equals(item)).collect(Collectors.toList());
            		//本次退数量
            		BigDecimal rqty=BigDecimal.ZERO;
            		if(thisgoods!=null&&thisgoods.size()==1){
            			rqty=new BigDecimal(String.valueOf(thisgoods.get(0).getQty()));
            		}
            		//单身商品总数量
            		BigDecimal qty =new BigDecimal(detailMap.get("QTY")==null?"0":detailMap.get("QTY").toString());
            		BigDecimal oldprice =new BigDecimal(detailMap.get("OLDPRICE")==null?"0":detailMap.get("OLDPRICE").toString());
            		BigDecimal price =new BigDecimal(detailMap.get("PRICE")==null?"0":detailMap.get("PRICE").toString());
            		BigDecimal oldamount=oldprice.multiply(rqty);
            		BigDecimal amount=price.multiply(rqty);
            		
            		//AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	87	商家实收金额
            		BigDecimal amtMerreceive=amount;
            		//AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	89	顾客实付金额
            		BigDecimal amtCustpayreal=amount;
            		BigDecimal disc=oldamount.subtract(amount);
//            		BigDecimal saleDisc=disc;
            		
            		//加总 计算单身数据
            		saleAmt=saleAmt.add(amount);
            		saleDiscTot=saleDiscTot.add(disc);
            		totQty=totQty.add(rqty);
            		totOldamt=totOldamt.add(oldamount);
            		
            		//生成商品单身
                    strBuff = new StringBuffer("");
                    strBuff.append(" insert into DCP_ORDER_DETAIL (");//分区字段
                    strBuff.append(" eid, orderno, item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, oitem, oreitem, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, stime, tran_time, RUNPICKQTY, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//增加分区字段
                    
                    strBuff.append("select eid, '"+orderno+"', item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, item, item, pickqty, '"+rqty+"', rcqty, shopqty, boxnum, boxprice, '"+rqty+"', '"+oldprice+"', '"+oldamount+"', '"+price+"', '"+disc+"', '"+amount+"'"
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, '"+tran_time+"', '"+tran_time+"' , qty-pickqty, VIRTUAL, DISC_MERRECEIVE, '"+amtMerreceive+"', DISC_CUSTPAYREAL, '"+amtCustpayreal+"' ,'"+bdate+"' ");//增加分区字段
                    
                    strBuff.append(" from DCP_ORDER_DETAIL where eid='"+eId+"' and orderno='"+sourceOrderNo+"' and item='"+item+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    ExecBean exSale_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail));
            		
            		if(agioMaps!=null&&agioMaps.size()>0){
            			//DCP_SALE_DETAIL_AGIO
            			List<Map<String, Object>> newagioMaps=agioMaps.stream().filter(g->g.get("MITEM").toString().equals(item)).collect(Collectors.toList());
            			if(newagioMaps!=null&&newagioMaps.size()>0){
            				BigDecimal agiodiscTot=BigDecimal.ZERO;
            				int agioCount=0;
            				for(Map<String, Object> agioMap:newagioMaps){
            					agioCount++;
            					String thisitem=agioMap.get("ITEM")==null?"":agioMap.get("ITEM").toString();
            					//QTY	NUMBER(23,8)	Yes		6	参与数量
            					BigDecimal agioqty =new BigDecimal(agioMap.get("QTY")==null?"0":agioMap.get("QTY").toString());
            					//AMT	NUMBER(23,8)	Yes		7	参与金额
            					BigDecimal agioamt =new BigDecimal(agioMap.get("AMT")==null?"0":agioMap.get("AMT").toString());
            					agioamt=agioamt.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
            					//DISC	NUMBER(23,8)	Yes		8	折扣金额
            					BigDecimal agiodisc =new BigDecimal(agioMap.get("DISC")==null?"0":agioMap.get("DISC").toString());
            					agiodisc=agiodisc.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
            					if(agioCount==newagioMaps.size()){
            						agiodisc=disc.subtract(agiodiscTot);
            					}
            					
            					//REALDISC	NUMBER(23,8)	Yes		9	实际折扣金额
            					BigDecimal agioreal =new BigDecimal(agioMap.get("REALDISC")==null?"0":agioMap.get("REALDISC").toString());
            					agioreal=agioreal.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
            					
            					//生成商品单身折扣
            		            strBuff = new StringBuffer("");
            		            strBuff.append(" insert into DCP_ORDER_DETAIL_AGIO (");//分区字段
            		            strBuff.append(" eid, orderno, mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
            		            
            		            strBuff.append(" select eid, '"+orderno+"', mitem, item, '"+rqty+"', '"+agioamt+"', '"+agiodisc+"', '"+agioreal+"', disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"' ");//增加分区字段
            		            
            		            strBuff.append(" from DCP_ORDER_DETAIL_agio where eid='"+eId+"' and orderno='"+sourceOrderNo+"' and MITEM='"+item+"' and ITEM='"+thisitem+"' ");
            		            execsql = "";
            		            execsql = strBuff.toString();
            		            ExecBean exSale_detail_agio = new ExecBean(execsql);
            		            DataPB.add(new DataProcessBean(exSale_detail_agio));
            		            
            					
            					agiodiscTot=agiodiscTot.add(agiodisc);
            				}
            			}
            		}
//            		StaticInfo.dao.useTransactionProcessData(DataPB);
            	}
            	
            	BigDecimal l=BigDecimal.ZERO;
                if(totAmt.compareTo(saleAmt)!=0){
                	l=totAmt.subtract(saleAmt);
                }
                
                totOldamt=totOldamt.add(l);
            	
            	//TOT_DISC	NUMBER(23,8)	Yes		67	折扣金额(含税)
                BigDecimal totDisc=saleDiscTot;
                //TOT_AMT	NUMBER(23,8)	Yes		71	应付金额(含税)
                BigDecimal TOT_AMT=saleAmt.add(l);
                //PAY_AMT	NUMBER(23,8)	Yes		86	已收抵账金额(含税)=所有支付金额之和，去除找零和溢收
                BigDecimal PAY_AMT=saleAmt.add(l);
                //SHOPINCOME	NUMBER(23,8)	Yes		105	外卖：商家实际到账金额
//                BigDecimal SHOPINCOME=saleAmt;
                //TOT_AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	131	商家实收金额(含税)=付款时商家实收金额
                BigDecimal TOT_AMT_MERRECEIVE=saleAmt.add(l);
                //TOT_AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	132	顾客实付金额[含税]=付款时客户实际支付金额
                BigDecimal TOT_AMT_CUSTPAYREAL=saleAmt.add(l);
                
                
            	//生成单头语句
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_ORDER (");//分区字段
                strBuff.append(" saledisc,saleamt,eid, billtype, orderno, requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, refundamt,REFUNDAMT_MERRECEIVE,REFUNDAMT_CUSTPAYREAL"
                        + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, refundsourcebillno, refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                        + ", stime, create_datetime, complete_datetime, bdate, tran_time, update_time, process_status, peopletype, printcount, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,DOWNGRADED,WAIMAIMERRECEIVEMODE,PARTITION_DATE,GROUPBUYING,PARTNERMEMBER )");
                
                strBuff.append("select '"+saleDiscTot+"','"+saleAmt+"',eid, '"+billtype+"', '"+orderno+"', requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, ");
                //MACHINE
                if (machineNo==null||machineNo.trim().isEmpty())
                {
                    strBuff.append("machine, ");
                }
                else
                {
                    strBuff.append("'"+machineNo+"',");
                }
                
                strBuff.append("ver_num, ");
                //squadno
                if (squadNo==null||squadNo.trim().isEmpty())
                {
                    strBuff.append("squadno, ");
                }
                else
                {
                    strBuff.append("'"+squadNo+"',");
                }
                //workno
                if (workNo==null||workNo.trim().isEmpty())
                {
                    strBuff.append("workno, ");
                }
                else
                {
                    strBuff.append("'"+workNo+"',");
                }
                //opno
                if (opNo==null||opNo.trim().isEmpty())
                {
                    strBuff.append("opno, ");
                }
                else
                {
                    strBuff.append("'"+opNo+"',");
                }
                
                strBuff.append("isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, '"+deliveryType+"', '"+deliveryNo+"', deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, '"+totQty+"', '"+totOldamt+"', erase_amt, '"+totDisc+"', '"+TOT_AMT+"', tot_uamt, '"+PAY_AMT+"', writeoffamt, '"+PAY_AMT+"',tot_amt_merreceive,tot_amt_custpayreal"
                        + ", packagefee, [totshipfee], [rshipfee], [shipfee], [shopshareshipfee], servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, '"+refundsourcebillno+"', refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                        + ", '"+tran_time+"', '"+tran_time+"', '', '"+bdate+"', '"+tran_time+"', '"+update_time+"', 'N', peopletype, 0, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, '"+TOT_AMT_MERRECEIVE+"', TOT_AMT_CUSTPAYREAL, '"+TOT_AMT_CUSTPAYREAL+"', TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,DOWNGRADED,WAIMAIMERRECEIVEMODE,'"+bdate+"',GROUPBUYING,PARTNERMEMBER ");
                
                strBuff.append(" from DCP_ORDER where eid='"+eId+"'  and orderno='"+sourceOrderNo+"' ");
                execsql="";
                execsql = strBuff.toString();
                
                //isRefundDeliverAmt;//是否退运费 0否1是
                //TOTSHIPFEE	NUMBER(23,8)	Yes		69	总配送费
                //RSHIPFEE	NUMBER(23,8)	Yes		70	配送费减免
                //SHIPFEE	NUMBER(23,8)	Yes		71	实际配送费
                //SHOPSHARESHIPFEE	NUMBER(23,8)	Yes		72	商家替用户承担的配送费（外卖）
                if("1".equals(isRefundDeliverAmt)){
                	//退运费
                	execsql=execsql.replace("[totshipfee]", "totshipfee");
                	execsql=execsql.replace("[rshipfee]", "rshipfee");
                	execsql=execsql.replace("[shipfee]", "shipfee");
                	execsql=execsql.replace("[shopshareshipfee]", "shopshareshipfee");
                }else{
                	//不退运费
                	execsql=execsql.replace("[totshipfee]", "0");
                	execsql=execsql.replace("[rshipfee]", "0");
                	execsql=execsql.replace("[shipfee]", "0");
                	execsql=execsql.replace("[shopshareshipfee]", "0");
                }
                
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                
                String crmPaySql=""
                		+ " SELECT A.*,B.PAYCODEERP,C.AMOUNT,C.CUSTPAYREAL,C.MERRECEIVE,C.NOCODE,C.PURPOSE FROM "
                		+ " (SELECT EID,BILLNO,PAYCHANNEL,MAX(SERIALNO) AS SERIALNO "
                		+ " FROM CRM_ORDERPAY WHERE EID='"+eId+"' AND BILLNO='"+orderNo+"' "//AND PURPOSE='3' 
                		+ " GROUP BY EID,BILLNO,PAYCHANNEL) A "
                		+ " LEFT JOIN DCP_PAYMENT B ON A.EID=B.EID AND A.PAYCHANNEL=B.PAYCODE "
                		+ " LEFT JOIN CRM_ORDERPAY C ON A.EID=C.EID AND A.BILLNO=C.BILLNO AND A.SERIALNO=C.SERIALNO "
                		+ "";
                List<Map<String, Object>> crmPayMaps = dao.executeQuerySQL(crmPaySql, null);
                
                //查询付款单dcp_order_pay_detail
                String paysql="select * from dcp_order_pay_detail where eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' ";
            	List<Map<String, Object>> payMaps = dao.executeQuerySQL(paysql, null);
            	if(payMaps!=null&&payMaps.size()>0){
        			
        			BigDecimal payTot=BigDecimal.ZERO;
            		BigDecimal merreceiveTot=BigDecimal.ZERO;
            		BigDecimal custpayrealTot=BigDecimal.ZERO;
            		int payCount=0;
            		for(Map<String, Object> payMap:payMaps){
            			
            			payCount++;
            			//PAY	NUMBER(23,8)	Yes		9	交易金额，含溢收和找零；提货券时=商品原金额
            			BigDecimal pay =new BigDecimal(payMap.get("PAY")==null?"0":payMap.get("PAY").toString());
            			pay=pay.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				pay=PAY_AMT.subtract(payTot);
        				}
            			//MERRECEIVE	NUMBER(23,8)	Yes	0	40	商家实收金额，移动支付用，例如支付宝，微信等
            			BigDecimal merreceive =new BigDecimal(payMap.get("MERRECEIVE")==null?"0":payMap.get("MERRECEIVE").toString());
            			merreceive=merreceive.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				merreceive=TOT_AMT_MERRECEIVE.subtract(merreceiveTot);
        				}
            			//CUSTPAYREAL	NUMBER(23,8)	Yes	0	42	客户实付金额：移动支付用，例如支付宝，微信等
            			BigDecimal custpayreal =new BigDecimal(payMap.get("CUSTPAYREAL")==null?"0":payMap.get("CUSTPAYREAL").toString());
            			custpayreal=custpayreal.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				custpayreal=TOT_AMT_CUSTPAYREAL.subtract(custpayrealTot);
        				}
            			String thisitem=payMap.get("ITEM")==null?"":payMap.get("ITEM").toString();
            			//WRITEOFFAMT	NUMBER(23,8)	Yes		33	冲销金额
            			
            			String paycodeerp=payMap.get("PAYCODEERP")==null?"":payMap.get("PAYCODEERP").toString();
            			String caradno=payMap.get("CARADNO")==null?"":payMap.get("CARADNO").toString();
            			
            			if(crmPayMaps!=null&&crmPayMaps.size()>0){
            				List<Map<String, Object>> newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("PAYCODEERP").equals(paycodeerp)).collect(Collectors.toList());
            				if(newcrmPayMaps!=null&&newcrmPayMaps.size()>0){
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()>1){
            						newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("NOCODE").equals(caradno)).collect(Collectors.toList());
            					}
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()==1){
            						Map<String, Object> newcrmMap=newcrmPayMaps.get(0);
            						String paychannel=newcrmMap.get("PAYCHANNEL")!=null&&newcrmMap.get("PAYCHANNEL").toString().trim().length()>0?newcrmMap.get("PAYCHANNEL").toString():"";
            						String purpose=newcrmMap.get("PURPOSE")!=null&&newcrmMap.get("PURPOSE").toString().trim().length()>0?newcrmMap.get("PURPOSE").toString():"";
            						//支付通道#03会员卡、#04券、#05积分、#P1微信支付
    								if("1".equals(purpose)&&"#04".equals(paychannel)){
    									//未退完时，券不需要反写
    									continue;	
    								}
            						pay=new BigDecimal(newcrmMap.get("AMOUNT")!=null&&newcrmMap.get("AMOUNT").toString().trim().length()>0
            								?newcrmMap.get("AMOUNT").toString():"0");
            						
            						merreceive=new BigDecimal(newcrmMap.get("MERRECEIVE")!=null&&newcrmMap.get("MERRECEIVE").toString().trim().length()>0
            								?newcrmMap.get("MERRECEIVE").toString():"0");
            						custpayreal=new BigDecimal(newcrmMap.get("CUSTPAYREAL")!=null&&newcrmMap.get("CUSTPAYREAL").toString().trim().length()>0
            								?newcrmMap.get("CUSTPAYREAL").toString():"0");
            					}
            				}
            			}
            			
            			
            			//生成付款单
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into Dcp_Order_Pay_Detail (");//分区字段
                        strBuff.append(" eid, billno, item, billdate, bdate, sourcebilltype, sourcebillno, loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime,FUNCNO,PAYDOCTYPE,SENDPAY,PAYTYPE, tran_time,SOURCEHEADBILLNO "
                                + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE,GAINCHANNEL,GAINCHANNELNAME  )");
                        strBuff.append(" select eid, '"+billno+"', item, '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, '"+pay+"', paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, '"+saleAmt+"', authcode, lastmodiopid, lastmodiopname, lastmoditime,FUNCNO,PAYDOCTYPE,SENDPAY,PAYTYPE, '"+tran_time+"','"+sourceOrderNo+"' "
                                + ",MERDISCOUNT, '"+merreceive+"', THIRDDISCOUNT, '"+custpayreal+"', COUPONMARKETPRICE, COUPONPRICE,MOBILE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"',GAINCHANNEL,GAINCHANNELNAME ");
                        
                        strBuff.append(" from Dcp_Order_Pay_Detail where eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' and item='"+thisitem+"' ");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_pay_detail = new ExecBean(execsql);
                        DataPB.add(new DataProcessBean(exSale_pay_detail));
                        
                        payTot=payTot.add(pay);
                        merreceiveTot=merreceiveTot.add(merreceive);
                        custpayrealTot=custpayrealTot.add(custpayreal);
            		}
            	}
            	
            	//交班汇总DCP_STATISTIC_INFO
            	String statisticsql="select * from DCP_STATISTIC_INFO where eid='"+eId+"' and ORDERNO='"+sourceOrderNo+"' and type=3";
            	List<Map<String, Object>> statisticMaps = dao.executeQuerySQL(statisticsql, null);
            	if(statisticMaps!=null&&statisticMaps.size()>0){
            		BigDecimal amtTot=BigDecimal.ZERO;
            		int statisticCount=0;
            		for(Map<String, Object> statisticMap:statisticMaps){
            			statisticCount++;
                        String thisitem=statisticMap.get("ITEM")==null?"":statisticMap.get("ITEM").toString();
                        String thisopno=statisticMap.get("OPNO")==null?"":statisticMap.get("OPNO").toString();
                        
                        //AMT	NUMBER(23,8)	Yes		11	金额,存储整数,不含找零，含溢收
                        BigDecimal amt =new BigDecimal(statisticMap.get("AMT")==null?"0":statisticMap.get("AMT").toString());
                        amt=amt.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(statisticCount==statisticMaps.size()){
            				amt=PAY_AMT.subtract(amtTot);
        				}
            			
            			//交班汇总
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_STATISTIC_INFO (");
                        strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
                        strBuff.append(" select EID, SHOPID, ");
                        //MACHINE
                        if (machineNo==null||machineNo.trim().isEmpty())
                        {
                            strBuff.append("MACHINE, ");
                        }
                        else
                        {
                            strBuff.append("'"+machineNo+"',");
                        }
                        
                        //OPNO
                        if (opNo==null||opNo.trim().isEmpty())
                        {
                            strBuff.append("OPNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+opNo+"',");
                        }
                        //SQUADNO
                        if (squadNo==null||squadNo.trim().isEmpty())
                        {
                            strBuff.append("SQUADNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+squadNo+"',");
                        }
                        
                        strBuff.append("'"+orderno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, '"+sdate+"', '"+stime+"', 'N', ");
                        //WORKNO
                        if (workNo==null||workNo.trim().isEmpty())
                        {
                            strBuff.append("WORKNO, ");
                        }
                        else
                        {
                            strBuff.append("'"+workNo+"',");
                        }
                        
                        strBuff.append("4, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");
                        
                        strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and ORDERNO='"+sourceOrderNo+"' and type=3 and item='"+thisitem+"' and opno='"+thisopno+"'");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_statistic_info = new ExecBean(execsql);
                        DataPB.add(new DataProcessBean(exSale_statistic_info));
                        
                        amtTot=amtTot.add(amt);
            		}
            	}
            	
            	
            	//查询付款单DCP_ORDER_PAY
                String paysql2="select * from Dcp_Order_Pay where eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' ";
            	List<Map<String, Object>> payMaps2 = dao.executeQuerySQL(paysql2, null);
            	if(payMaps2!=null&&payMaps2.size()>0){
            		BigDecimal payTot=BigDecimal.ZERO;
            		BigDecimal merreceiveTot=BigDecimal.ZERO;
            		int payCount=0;
            		for(Map<String, Object> payMap:payMaps2){
            			payCount++;
            			//PAYREALAMT	NUMBER(23,8)	Yes		16	实付金额
            			BigDecimal pay =new BigDecimal(payMap.get("PAYREALAMT")==null?"0":payMap.get("PAYREALAMT").toString());
            			pay=pay.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				pay=PAY_AMT.subtract(payTot);
        				}
            			//WRITEOFFAMT	NUMBER(23,8)	Yes		17	冲销金额
            			BigDecimal merreceive =new BigDecimal(payMap.get("WRITEOFFAMT")==null?"0":payMap.get("WRITEOFFAMT").toString());
            			merreceive=merreceive.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				merreceive=TOT_AMT_MERRECEIVE.subtract(merreceiveTot);
        				}


            			strBuff = new StringBuffer("");
                        strBuff.append(" insert into Dcp_Order_pay (");//分区字段
                        strBuff.append("eid, billno, billdate, bdate, sourcebilltype, sourcebillno, companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, direction, payrealamt, writeoffamt, usetype, status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, tran_time, process_status,SOURCEHEADBILLNO,PARTITION_DATE )");
                        strBuff.append(" select eid, '"+billno+"', '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', companyid, loaddoctype, shopid, channelid, ");
                        //machineid
                        if (machineNo==null||machineNo.trim().isEmpty())
                        {
                            strBuff.append("machineid, ");
                        }
                        else
                        {
                            strBuff.append("'"+machineNo+"',");
                        }
                        strBuff.append("customerno, ");
                        //squadno
                        if (squadNo==null||squadNo.trim().isEmpty())
                        {
                            strBuff.append("squadno, ");
                        }
                        else
                        {
                            strBuff.append("'"+squadNo+"',");
                        }
                        
                        //workno
                        if (workNo==null||workNo.trim().isEmpty())
                        {
                            strBuff.append("workno, ");
                        }
                        else
                        {
                            strBuff.append("'"+workNo+"',");
                        }
                        
                        strBuff.append("-direction, '"+pay+"', '"+merreceive+"', '"+usetype+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+sourceOrderNo+"', '"+bdate+"'");
                        
                        strBuff.append(" from Dcp_Order_pay where eid='"+eId+"'  and SOURCEBILLNO='"+sourceOrderNo+"' ");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_pay = new ExecBean(execsql);
                        DataPB.add(new DataProcessBean(exSale_pay));
                        
                        payTot=payTot.add(pay);
                        merreceiveTot=merreceiveTot.add(merreceive);
            		}
            		
            	}
                
            }
            		
            
        }
        
        
        
        
        
        
        
        return DataPB;
    }
    
    /**
     * 库存解锁
     * @param req
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public boolean dcpStockUnlock(DsmDAO dao,DCP_OrderRefundReq req,StringBuffer errorMessage) throws Exception
    {
        try
        {
            String eId_para = req.getRequest().geteId();//请求传入的eId
            //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
            String refundType = req.getRequest().getRefundType();
            String orderNo = req.getRequest().getOrderNo();
            String loadDocType = req.getRequest().getLoadDocType();
            String channelId = req.getRequest().getChannelId();
            String refundBdate = req.getRequest().getRefundBdate();
            
            //先查询 锁库表
            String sql ="select * from dcp_stock_lock where eid='"+eId_para+"' and billno='"+orderNo+"' and CHANNELID='"+channelId+"'";
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】查询该订单单号orderNo="+orderNo+" 库存锁定，查询sql:"+sql);
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if(getQData==null||getQData.isEmpty())
            {
                errorMessage.append("没有查询到锁定的库存！");
                return false;
            }
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("PLUNO", true);
            condition.put("FEATURENO", true);
            condition.put("SUNIT", true);
            List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
            
            DCP_StockUnlock_OpenReq unLockReq = new DCP_StockUnlock_OpenReq();
            unLockReq.setServiceId("DCP_StockUnlock_Open");
            unLockReq.seteId(eId_para);
            unLockReq.setToken(req.getToken());
            DCP_StockUnlock_OpenReq.levelReq unLockReq_request = unLockReq.new levelReq();
            unLockReq_request.setbDate(refundBdate);
            unLockReq_request.setBillNo(orderNo);
            unLockReq_request.setChannelId(channelId);
            unLockReq_request.setUnLockType("-1");// 解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
            unLockReq_request.setPluList(new ArrayList<DCP_StockUnlock_OpenReq.PluList>());
            for (Map<String, Object> map : getQPlu)
            {
                DCP_StockUnlock_OpenReq.PluList oneLv1 = unLockReq.new PluList();
                oneLv1.setOrganizationList(new ArrayList<DCP_StockUnlock_OpenReq.OrgList>());
                String pluNo = map.get("PLUNO").toString();
                String featureNo = map.get("FEATURENO").toString();
                String sUnit = map.get("SUNIT").toString();
                
                oneLv1.setPluNo(pluNo);
                oneLv1.setFeatureNo(featureNo);
                oneLv1.setsUnit(sUnit);
                for (Map<String, Object> mapDetail : getQData)
                {
                    String pluNo_detail = mapDetail.get("PLUNO").toString();
                    String featureNo_detail  = mapDetail.get("FEATURENO").toString();
                    String sUnit_detail  = mapDetail.get("SUNIT").toString();
                    DCP_StockUnlock_OpenReq.OrgList oneLv2 = unLockReq.new OrgList();
                    if(pluNo.equals(pluNo_detail)&&featureNo.equals(featureNo_detail)&&sUnit.equals(sUnit_detail))
                    {
                        oneLv2.setOrganizationNo(mapDetail.get("ORGANIZATIONNO").toString());
                        oneLv2.setQty(mapDetail.get("SQTY").toString());
                        oneLv2.setWarehouse(mapDetail.get("WAREHOUSE").toString());
                        oneLv1.getOrganizationList().add(oneLv2);
                    }
                }
                
                unLockReq_request.getPluList().add(oneLv1);
                
            }
            unLockReq.setRequest(unLockReq_request);
            
            
            try
            {
                ParseJson pj = new ParseJson();
                
                String json = pj.beanToJson(unLockReq);
                HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【调用库存锁定解锁接口】开始，请求json="+json+" 订单单号orderNo="+orderNo);
                
                DCP_StockUnlock_Open stockUnlock_open=new DCP_StockUnlock_Open();
                DCP_StockUnlock_OpenRes stockUnlock_openRes=new DCP_StockUnlock_OpenRes();
                stockUnlock_open.setDao(this.dao);
                stockUnlock_open.processDUID(unLockReq,stockUnlock_openRes);
                
                String resJson=pj.beanToJson(stockUnlock_openRes);
                
                HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefund】【调用库存锁定解锁接口】结束，返回json="+resJson+" 订单单号orderNo="+orderNo);
                
                if (stockUnlock_openRes.isSuccess())
                {
                    return true;
                }
                else
                {
                    String serviceDescription_DSUL=Check.Null(stockUnlock_openRes.getServiceDescription())?"返回空":stockUnlock_openRes.getServiceDescription();
                    errorMessage.append("调用库存解锁服务DCP_StockUnlock_Open失败! " +serviceDescription_DSUL);
                    return false;
                }
            }
            catch(Exception e)
            {
                errorMessage.append(e.getMessage());
            }
            
            return false;
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
            return false;
        }
    }
    
    /**
     * 判断下是否全部退完，更新订单状态
     * @param req
     * @param orderShop
     * @throws Exception
     */
    private void UpdateOrderCompleted(DCP_OrderRefundReq req,String orderShop) throws Exception
    {
        
        try
        {
            String eId_para = req.getRequest().geteId();//请求传入的eId
            String orderNo = req.getRequest().getOrderNo();
            String refundBdate = req.getRequest().getRefundBdate();
            String refundDatetime = req.getRequest().getRefundDatetime();
            
            String shopId = req.getRequest().getShopId();//操作门店ID
            String opNo = req.getRequest().getOpNo();
            String opName = req.getRequest().getOpName();
            
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sdateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            
            if(refundBdate==null||refundBdate.isEmpty())
            {
                refundBdate = sdate;
            }
            
            if(refundDatetime==null||refundDatetime.isEmpty())
            {
                refundDatetime = sdateTime;
            }
            
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from (");
            sqlbuf.append("select sum(qty) as qty_tot,sum(rqty) as rqty_tot  from dcp_order_detail ");
            sqlbuf.append(" where qty>0 and eid='"+eId_para+"'  and orderno='"+orderNo+"'");
            sqlbuf.append(")");
            String sql = sqlbuf.toString();
            HelpTools.writelog_waimai("【第三方调用DCP_OrderRefund接口，判断是否全部退完】【查询开始】，单号orderNo="+orderNo+" 查询sql:"+sql);
            List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
            if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
            {
                float qty_tot = 0;
                
                float rqty_tot = 0;
                try
                {
                    qty_tot = Float.parseFloat(getQDataDetail.get(0).get("QTY_TOT").toString());
                } catch (Exception e) {
                    // TODO: handle exception
                }
                
                
                try
                {
                    rqty_tot = Float.parseFloat(getQDataDetail.get(0).get("RQTY_TOT").toString());
                } catch (Exception e) {
                    // TODO: handle exception
                }
                
                //更新已完成，因为部分退，只能是有做过部分提货
                if(qty_tot-rqty_tot == 0)
                {
                    
                    UptBean ub1 = null;
                    ub1 = new UptBean("dcp_order");
                    
                    ub1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
                    ub1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    
                    //condition
                    ub1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
                    ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    
                    this.addProcessData(new DataProcessBean(ub1));
                    
                    String execsql = " update dcp_order set refundAmt=payamt,REFUNDAMT_MERRECEIVE=tot_amt_merreceive,REFUNDAMT_CUSTPAYREAL=tot_amt_custpayreal where  eid='"+eId_para+"' and orderno='"+orderNo+"' ";
                    ExecBean exSale = new ExecBean(execsql);
                    this.addProcessData(new DataProcessBean(exSale));
                    
                    StringBuilder memoMsg = new StringBuilder();
                    StringBuilder errorMsg = new StringBuilder();
                    
                    this.doExecuteDataToDB();
                    HelpTools.writelog_waimai("【第三方调用DCP_OrderRefund接口，判断是否全部退完】【全部退完】更新单据状态已退单，成功，单号orderNo="+orderNo);
                    
                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);
                    
                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(orderShop);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "12";//退单成功
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);
                    
                    onelv1.setMemo(statusName+"(全部退完)<br/>");
                    onelv1.setDisplay("0");
                    
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    
                    orderStatusLogList.add(onelv1);
                    
                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    
                    
                    
                }
                else
                {
                    HelpTools.writelog_waimai("【第三方调用DCP_OrderRefund接口，判断是否全部退完】【没有全部退完】无需更新单据状态，单号orderNo="+orderNo);
                }
                
            }
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            
        }
        
        
        
    }
    
    /**
     * 退单成功后 判断该订单是否生成加工任务单，如已生成 则同步加工任务单状态
     * @param req
     * @param getData_Order
     * @param getData_Order_detail
     */
    private void updateProcessTask(DCP_OrderRefundReq req,List<Map<String, Object>> getData_Order,List<Map<String, Object>> getData_Order_detail)
    {
        try
        {
            DCP_OrderRefundReq.levelRequest request = req.getRequest();
            String eId = request.geteId();
            String orderNo = request.getOrderNo();
            String refundReasonName = request.getRefundReasonName();
            
            String modifyBy = req.getRequest().getOpNo();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            
            // 配送时间
            String shipDate = getData_Order.get(0).get("SHIPDATE").toString();
            String shipStartTime = getData_Order.get(0).get("SHIPSTARTTIME").toString();
            shipStartTime = shipStartTime.replace("-", "");
            if (shipStartTime.isEmpty()) {
                shipStartTime = new SimpleDateFormat("HHmmss").format(new Date());
            }
            String shipStartDateTime = shipDate + shipStartTime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss
            
            
            // 如果查询有值 则同步单据状态 修改 ISREFUND = N , refundReasonName 退单原因名称
            //更新原单
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_PROCESSTASK");
            ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
            ub1.addUpdateValue("OSTATUS", new DataValue("12", Types.VARCHAR));
            ub1.addUpdateValue("ISREFUND", new DataValue("N", Types.VARCHAR));
            ub1.addUpdateValue("REFUNDREASONNAME", new DataValue(refundReasonName, Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
            
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            
            //更新原单
            ub1 = new UptBean("dcp_product_sale");
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
            ub1.addUpdateValue("ISREFUND", new DataValue("N", Types.VARCHAR));
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            
            //写零售生产单头
            String[] columns_productSale = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","TRNO",
                    "TABLENO", "REPASTTYPE", "DINNERSIGN", "GUESTNUM","PRODUCTSTATUS", "MEMO",
                    "ISTAKEOUT", "CHANNELID","APPTYPE" ,"LOADDOCTYPE","WXOPENID"
                    ,"ORDERTIME","ADULTCOUNT","ISREFUNDORDER", "SHIPENDTIME","ISBOOK"};
            //
            DataValue[] insValue_productSale = new DataValue[] {
                    new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                    new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                    new DataValue("REFUNDORDER", Types.VARCHAR),
                    new DataValue("RE"+orderNo, Types.VARCHAR),
                    new DataValue(orderNo, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEMO").toString(), Types.VARCHAR),
                    new DataValue("Y", Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(loadDocType, Types.VARCHAR),
                    new DataValue(loadDocType, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("OUTSELID").toString(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(shipStartDateTime, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("ISBOOK").toString(), Types.VARCHAR),
            };
            InsBean ib_productSale = new InsBean("dcp_product_sale", columns_productSale);
            ib_productSale.addValues(insValue_productSale);
            this.addProcessData(new DataProcessBean(ib_productSale));
            
            
            int v_beforeItem=0;
            
            BigDecimal v_TotRemainQty=new BigDecimal("0");
            
            //产生单号
            String v_BeforeProcessTaskNO=HelpTools.getProcessTaskNO(eId,req.getRequest().getShopId());
            //退单用,处理加回商品数量到新的预制单
            
            String sql_beforDish_UseQty="select a.oitem,nvl(a.useqty,0) USEQTY " +
                    "from DCP_BEFOREDISHTASK a " +
                    "inner join dcp_processtask b on a.eid=b.eid and a.shopid=b.shopid and a.billno=b.processtaskno " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+req.getRequest().getShopId()+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and b.otype='BEFORE' ";
            String sql_processDetail_cookQty=" select a.oitem,nvl(a.pqty,0) PQTY from dcp_processtask_detail a " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+req.getRequest().getShopId()+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and a.goodsstatus in ('2','3') ";
            
            List<Map<String,Object>> getData_beforDish_UseQty=this.doQueryData(sql_beforDish_UseQty, null);
            List<Map<String,Object>> getData_processDetail_cookQty=this.doQueryData(sql_processDetail_cookQty, null);
            
            
            String[] columns_Processtask_Detail = {
                    "EID", "SHOPID", "ORGANIZATIONNO", "PROCESSTASKNO", "ITEM", "MUL_QTY", "PQTY",  "PUNIT",
                    "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                    "GOODSSTATUS", "FINALCATEGORY", "PLUBARCODE", "AVAILQTY"
            };
            
            
            // 写单头
            String[] columns_Processtask = {
                    "SHOPID", "PROCESSTASKNO", "EID", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE", "CREATEBY",
                    "STATUS", "TOT_CQTY", "PROCESS_STATUS", "BDATE", "TOT_PQTY", "MEMO", "UPDATE_TIME", "WAREHOUSE",
                    "MATERIALWAREHOUSE", "OTYPE", "CREATEDATETIME", "TOT_AMT", "TOT_DISTRIAMT"
            };
            
            
            //退的明细
            for (Map<String, Object> map_order_detail : getData_Order_detail)
            {
                //更新原单
                ub1 = new UptBean("DCP_PRODUCT_DETAIL");
                ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
                ub1.addUpdateValue("REFUNDQTY", new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR));
                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
                ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
                
                //是否有备注
                String isMemo=map_order_detail.get("ISMEMO").toString();
                StringBuffer dMemo=new StringBuffer("");
                if (isMemo.equals("Y"))
                {
                    String order_detail_memo_SQL="select  * from dcp_order_detail_memo where eid='"+req.getRequest().geteId()+"' and orderno='"+orderNo+"' and oitem="+map_order_detail.get("ITEM").toString()+" ";
                    List<Map<String, Object>> getData_order_detail_memo =this.doQueryData(order_detail_memo_SQL,null);
                    if (getData_order_detail_memo != null && getData_order_detail_memo.size()>0)
                    {
                        for (Map<String, Object> map_detail_memo : getData_order_detail_memo)
                        {
                            dMemo.append(map_detail_memo.get("MEMO").toString()+",");
                        }
                        if (dMemo.length()>0)
                        {
                            dMemo.deleteCharAt(dMemo.length()-1);
                        }
                    }
                }
                
                String packageMitem=map_order_detail.get("PACKAGEMITEM").toString();
                if (Check.Null(packageMitem))
                {
                    packageMitem="0";
                }
                
                //写零售生产单身
                String[] columns_productSale_detail = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","OITEM",
                        "PLUNO", "PLUNAME", "PLUBARCODE", "QTY","SPECNAME", "UNITID",
                        "UNITNAME", "FLAVORSTUFFDETAIL","ISPACKAGE" ,"PGOODSDETAIL","GOODSSTATUS"
                        ,"REPASTTYPE","MEMO","ISURGE","FINALCATEGORY","REFUNDQTY","ATTRNAME","PACKAGEMITEM"};
                //
                DataValue[] insValue_productSale_detail = new DataValue[] {
                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                        new DataValue("REFUNDORDER", Types.VARCHAR),
                        new DataValue("RE"+orderNo, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("QTY").toString(), Types.FLOAT),
                        new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2")?"Y":"N", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(dMemo.toString(), Types.VARCHAR),
                        new DataValue("N", Types.VARCHAR),
                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                        new DataValue(0, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ATTRNAME").toString(), Types.VARCHAR),
                        new DataValue(packageMitem, Types.VARCHAR),
                };
                InsBean ib_productSale_detail = new InsBean("dcp_product_detail", columns_productSale_detail);
                ib_productSale_detail.addValues(insValue_productSale_detail);
                this.addProcessData(new DataProcessBean(ib_productSale_detail));
                
                
                //加工任务明细，只更新，不新增
                //更新退的字段值
                //这里注意1个问题，A商品2条，第一条4个，第二条2，买6个现在要退5个，要处理成：第一条更新退4个，第二条更新退1个
                String processTask_detail_SQL="select  * from dcp_processtask_detail where eid='"+req.getRequest().geteId()+"' and OFNO='"+orderNo+"' and OITEM="+map_order_detail.get("ITEM").toString()+" order by OITEM ";
                List<Map<String, Object>> getData_processTask_detail =this.doQueryData(processTask_detail_SQL,null);
                
                BigDecimal m_qty=new BigDecimal(map_order_detail.get("RQTY").toString());
                for (Map<String, Object> map_of_detail : getData_processTask_detail)
                {
                    BigDecimal refundQty=new BigDecimal(0);
                    if (m_qty.compareTo(new BigDecimal(map_of_detail.get("PQTY").toString()))>0)
                    {
                        refundQty=new BigDecimal(map_of_detail.get("PQTY").toString());//此行全退
                        m_qty=m_qty.subtract(refundQty);
                    }
                    else
                    {
                        refundQty=m_qty;//退前面剩余的部分
                        m_qty=new BigDecimal(0);
                    }
                    
                    ub1 = new UptBean("dcp_processtask_detail");
                    ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
                    ub1.addUpdateValue("REFUNDQTY", new DataValue(refundQty.doubleValue(), Types.VARCHAR));
                    //condition
                    ub1.addCondition("EID", new DataValue(map_of_detail.get("EID").toString(), Types.VARCHAR));
                    ub1.addCondition("OFNO", new DataValue(map_of_detail.get("OFNO").toString(), Types.VARCHAR));
                    ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(map_of_detail.get("ITEM").toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
                
                
                
                //退单直接产生预制单，把预制菜数量加回去。
                //等于是说普通单子做出多余的也自动算成预制菜，没做的菜就不用做了
                //举例：来单共7份，预制菜2份，任务明细4份做了，1份没做，现在要退5份，怎么办?
                //预制菜份数(占用了，要加回来)+ 做好份数-实际购买的数量
                //公式：预制菜(2)+任务明细做好的(4)-( (来单数量(7)-退单数量(5) )=6-2=4个
                //预制菜占用表还要同BILLNO加工任务单号去关联加工任务单，取预制单的，因为普通的也写这个占用表了
                
                //退菜加回预制菜库存：
                //上面是对原单更新，现在要将预制菜占用及加工好的商品自动产生一张预制菜库存
                
                //双拼菜标记
                String isDoubleGoods = map_order_detail.get("ISDOUBLEGOODS").toString();
                
                //预制菜份数占用
                BigDecimal bdm_o_beforDish_UseQty=new BigDecimal("0");
                if (getData_beforDish_UseQty != null && getData_beforDish_UseQty.size()>0)
                {
                    List<Map<String,Object>> temp_beforDish_UseQty= getData_beforDish_UseQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_beforDish_UseQty != null && temp_beforDish_UseQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_beforDish_UseQty=temp_beforDish_UseQty.stream().map(p -> new BigDecimal(p.get("USEQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
                //任务明细做好的
                BigDecimal bdm_o_processDetail_cookQty=new BigDecimal("0");
                if (getData_processDetail_cookQty != null && getData_processDetail_cookQty.size()>0)
                {
                    List<Map<String,Object>> temp_processDetail_cookQty= getData_processDetail_cookQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_processDetail_cookQty != null && temp_processDetail_cookQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_processDetail_cookQty=temp_processDetail_cookQty.stream().map(p -> new BigDecimal(p.get("PQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
                
                //公式计算结果:这里全退
                BigDecimal v_canUseQty=bdm_o_beforDish_UseQty.add(bdm_o_processDetail_cookQty);
                
                //大于0就新产生预制菜明细
                if (v_canUseQty.compareTo(BigDecimal.ZERO)>0)
                {
                    
                    //*************************************双拼菜，使用BOM商品同步************************************
                    if (isDoubleGoods != null && isDoubleGoods.equals("Y"))
                    {
                        //双拼菜数量除除2，因为加工明细里面对应的OITEM有双拼菜主菜+子菜 ，数量翻倍了
                        v_canUseQty=v_canUseQty.divide(new BigDecimal("2"),2,RoundingMode.HALF_UP);
                        
                        StringBuffer sb_bom=new StringBuffer("        select a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty," +
                                "        c.material_pluno,c.qty,c.material_unit,c.material_qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid," +
                                "        mgl.plu_name as materialPluName,mul.uname as materialUnitName,mu.udlength as materialUnitLength," +
                                "        mg.baseunit as materialBaseUnit,mbul.uname as materialBaseUnitName,mgu.unitratio as materialUnitRatio," +
                                "        mg.isbatch as materialIsBatch,mg.price,mg.category,kc.unside,kc.uncook,kc.uncall, " +
                                "        hqkc.unside hq_unside,hqkc.uncook hq_uncook,hqkc.uncall hq_uncall " +
                                "        from (" +
                                "        select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.* from dcp_bom a" +
                                "        left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+req.getRequest().getShopId()+"'" +
                                "        where a.eId='"+eId+"' and a.effdate <=trunc(sysdate) and a.status='100' and a.bomtype = '0'" +
                                "        and a.pluno='"+map_order_detail.get("PLUNO").toString() +"' and a.unit='"+map_order_detail.get("SUNIT").toString()+"'" +
                                "        and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))" +
                                "        )a" +
                                "        inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and c.material_bdate <=trunc(sysdate) and material_edate >=trunc(sysdate)" +
                                "        left  join dcp_goods_lang mgl on mgl.eid=a.eid and mgl.pluno=c.material_pluno and mgl.lang_type='"+req.getLangType()+"'" +
                                "        left  join dcp_unit_lang mul on mul.eid=a.eid and mul.unit=c.material_unit and mul.lang_type='"+req.getLangType()+"'" +
                                "        left  join dcp_unit mu on mu.eid=a.eid and mu.unit=c.material_unit and mu.status='100'" +
                                "        inner join dcp_goods mg on mg.eid=a.eid and mg.pluno=c.material_pluno and mg.status='100'" +
                                "        left  join dcp_unit_lang mbul on mbul.eid=a.eid and mbul.unit=mg.baseunit and mbul.lang_type='"+req.getLangType()+"'" +
                                "        inner join dcp_goods_unit mgu on mgu.eid =a.eid and mgu.pluno=c.material_pluno and mgu.ounit=c.material_unit and mgu.unit=mg.baseunit" +
                                "        left join DCP_KDSDISHES_CONTROL kc on a.eid=kc.eid and kc.shopid='"+req.getRequest().getShopId()+"' and c.material_pluno=kc.pluno " +
                                "        left join dcp_hqkdsdishes_control hqkc on a.eid = hqkc.eid and ((hqkc.goodstype=2 and  c.material_pluno=hqkc.id) or (hqkc.goodstype=1 and mg.category=hqkc.id)) " +
                                "        where a.rn=1" +
                                "        order by a.pluno,c.sortid");
                        
                        List<Map<String, Object>> temp_Bom = this.doQueryData(sb_bom.toString(), null);
                        
                        if (temp_Bom != null && temp_Bom.size()>0)
                        {
                            for (Map<String, Object> map_Bom : temp_Bom)
                            {
                                
                                v_beforeItem+=1;
                                //累加总的
                                v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);
                                
                                BigDecimal bdm_remainQty=v_canUseQty.multiply(new BigDecimal(Convert.toDouble(map_Bom.get("MATERIAL_QTY").toString(), 1d))).divide(new BigDecimal(Convert.toDouble(map_Bom.get("QTY").toString(), 1d)), 2, RoundingMode.HALF_UP);
                                
                                DataValue[] insValueDetail = new DataValue[]
                                        {
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                                new DataValue(v_beforeItem, Types.VARCHAR),
                                                new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                                new DataValue(bdm_remainQty, Types.VARCHAR), // 数量 以单份维度存储
                                                new DataValue(map_Bom.get("MATERIAL_UNIT").toString(), Types.VARCHAR),
                                                new DataValue(bdm_remainQty.multiply(new BigDecimal(Convert.toStr(map_Bom.get("MATERIALUNITRATIO")))).doubleValue(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIAL_PLUNO").toString(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALPLUNAME").toString(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALBASEUNIT").toString(), Types.VARCHAR),
                                                new DataValue("1", Types.VARCHAR), // 单位转换率
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(modifyDate, Types.VARCHAR),//当天
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                                new DataValue(map_Bom.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                                new DataValue("", Types.VARCHAR), // 条码
                                                new DataValue(bdm_remainQty, Types.VARCHAR) // 剩余可用数量
                                        };
                                InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                                ib1.addValues(insValueDetail);
                                this.addProcessData(new DataProcessBean(ib1));
                            }
                        }
                    }
                    else
                    {
                        v_beforeItem+=1;
                        //累加总的
                        v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);
                        
                        DataValue[] insValueDetail = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                        new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                        new DataValue(v_beforeItem, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                        new DataValue(v_canUseQty, Types.VARCHAR), // 数量 以单份维度存储
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue("1", Types.VARCHAR), // 单位转换率
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(modifyDate, Types.VARCHAR),//当天
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR), // 条码
                                        new DataValue(v_canUseQty, Types.VARCHAR) // 剩余可用数量
                                };
                        InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                        ib1.addValues(insValueDetail);
                        this.addProcessData(new DataProcessBean(ib1));
                    }
                    
                    
                    
                }
                
                
                
            }
            
            
            //说明有明细，再添加单头
            if (v_beforeItem >0)
            {
                String sql_out_cost_warehouse="select OUT_COST_WAREHOUSE from DCP_ORG where eid = '"+eId+"' and ORGANIZATIONNO = '"+req.getRequest().getShopId()+"' ";
                List<Map<String, Object>> getOut_cost_warehouse = this.doQueryData(sql_out_cost_warehouse, null);
                String out_cost_warehouse = "";
                if(!CollectionUtils.isEmpty(getOut_cost_warehouse)){
                    out_cost_warehouse =  getOut_cost_warehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
                }
                
                DataValue[] insValue = new DataValue[]
                        {
                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                new DataValue(modifyTime, Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(modifyBy, Types.VARCHAR),
                                new DataValue("6", Types.VARCHAR), // status 默认6
                                new DataValue(v_beforeItem, Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(v_TotRemainQty, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
                                new DataValue("BEFORE", Types.VARCHAR), // 单据类型 此处为预制单 Before
                                new DataValue(modifyDate+modifyTime, Types.VARCHAR), // 生产日期
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                        };
                
                InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
                ib2.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib2));
            }
            
            
            this.doExecuteDataToDB();
            HelpTools.writelog_waimai("【订单退单成功,更新加工任务档 DCP_ProcessTask 成功】" + " 订单号orderNO:" + orderNo);
        }
        catch (Exception e)
        {
        
        }
    }
    
    /**
     * 是否存在部分订转销
     * @param eId
     * @param orderNo
     * @return
     * @throws Exception
     */
    private boolean isHasPartOrderToSale(String eId,String orderNo) throws Exception
    {
        boolean nRet = false;
        try
        {
            String sql = "select * from ( " +
                    " select sum(QTY) QTY , sum(PICKQTY) PICKQTY from dcp_order_detail where EID='"+eId+"' and ORDERNO='"+orderNo+"' " +
                    " ) where PICKQTY>0 and QTY<>PICKQTY  ";
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData!=null&&!getQData.isEmpty())
            {
                nRet = true;
            }
        }
        catch (Exception e)
        {
        
        }
        
        
        return nRet;
    }
    
    /**
     * 
     * @param eId
     * @param orderNo
     * @param refundType 退单类型 0：全退,1：部分退
     * @param pickGoodsRefundType  0：退钱且退货  1：退钱不退货
     * @param isRefundDeliverAmt  是否退运费 0否1是
     * @param goods
     * @return
     * @throws Exception
     */
    public Map<String,Object> doRecalculateReq(Map<String, Object> ordermap, 
    		String eId,String orderNo,String refundType,String pickGoodsRefundType,String tot_amt,String isRefundDeliverAmt,List<levelGoods> goods) throws Exception{
    	
    	Map<String,Object> map=new HashMap<String,Object>();
    	String loadDocType=ordermap.get("LOADDOCTYPE").toString();
    	if(orderLoadDocType.MINI.equals(loadDocType)){
    		
    		//有商品传参时，以商品参数为主
        	String goodsSql = "SELECT * FROM DCP_ORDER_DETAIL WHERE EID='"+eId+"' AND ORDERNO='"+orderNo+"'";
        	List<Map<String,Object>> goodsmaps =  StaticInfo.dao.executeQuerySQL(goodsSql, null);
        	
    		//部分退+退钱退款
        	if("1".equals(refundType)&&"0".equals(pickGoodsRefundType)){
        		if(goods==null||goods.size()<1){
        			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单["+orderNo+"]部分退货退款时,商品信息必选!");
        		}
        	}
        	
        	if("1".equals(isRefundDeliverAmt)){
        		//REFUNDEDDELIVERAMT	NVARCHAR2(1 CHAR)	Yes		234	运费已退 0否1是
        		String refundeddeliveramt=ordermap.get("REFUNDEDDELIVERAMT")!=null&&ordermap.get("REFUNDEDDELIVERAMT").toString().trim().length()>0?ordermap.get("REFUNDEDDELIVERAMT").toString():"";
        		if("1".equals(refundeddeliveramt)){
        			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
        					"订单["+orderNo+"]已退运费,不可重复退费");
        		}
        	}
        	
        	String newrefundType=null;
        	//判断是否是部分退
        	if(goodsmaps!=null&&goodsmaps.size()>0){
        		for(Map<String,Object> goodmap:goodsmaps){
        			//ITEM	NUMBER(38,0)	No	0 	3	项次
            		String item=goodmap.get("ITEM")!=null&&goodmap.get("ITEM").toString().trim().length()>0?goodmap.get("ITEM").toString():"";
            		//PLUNO	NVARCHAR2(40 CHAR)	Yes		6	商品编号
            		String pluno=goodmap.get("PLUNO")!=null&&goodmap.get("PLUNO").toString().trim().length()>0?goodmap.get("PLUNO").toString():"";
            		//PLUNAME	NVARCHAR2(120 CHAR)	Yes		7	商品名称
            		String pluname=goodmap.get("PLUNAME")!=null&&goodmap.get("PLUNAME").toString().trim().length()>0?goodmap.get("PLUNAME").toString():"";
            		//PLUBARCODE	NVARCHAR2(40 CHAR)	Yes		8	商品条码
            		String plubarcode=goodmap.get("PLUBARCODE")!=null&&goodmap.get("PLUBARCODE").toString().trim().length()>0?goodmap.get("PLUBARCODE").toString():"";
            		//QTY	NUMBER(23,8)	Yes		35	数量
            		BigDecimal quantity=new BigDecimal(goodmap.get("QTY")!=null&&goodmap.get("QTY").toString().trim().length()>0?goodmap.get("QTY").toString():"0");
            		//RQTY	NUMBER(18,4)	Yes		57	商品已退货数量（多次退累计金额）
            		BigDecimal rqty=new BigDecimal(goodmap.get("RQTY")!=null&&goodmap.get("RQTY").toString().trim().length()>0?goodmap.get("RQTY").toString():"0");
            		List<levelGoods> newgoods= new ArrayList<levelGoods>();
            		if(goods!=null&&goods.size()>0){
            			newgoods=goods.stream().filter(g->g.getItem().equals(item)).collect(Collectors.toList());
            		}
            		//本次退数量
        			BigDecimal qty=BigDecimal.ZERO;
        			levelGoods good=null;
        			if(newgoods!=null&&newgoods.size()>0){
        				good=newgoods.get(0);
        				//mallgoodsid不等于PluBarcode  且 商品名称也不匹配时报错
            			if(plubarcode.equals(good.getPluBarcode())||pluno.equals(good.getPluNo())||pluname.equals(good.getPluName())){
            				
            			}else{
            				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单["+orderNo+"]商品信息匹配异常!");
            			}
            			//本次退数量
            			qty=new BigDecimal(String.valueOf(good.getQty()));
        			}
        			
        			//商品已退货数量 大于0，则属于第N次退单  必然是部分退
        			if(rqty.compareTo(BigDecimal.ZERO)>0){
        				newrefundType="1";//1：部分退
        			}

        			//剩余数量=总数量-已退-本次退
        			BigDecimal lqty=quantity.subtract(rqty).subtract(qty);
        			if(lqty.compareTo(BigDecimal.ZERO)<0){
        				String name=good.getPluName()!=null&&good.getPluName().trim().length()>0?good.getPluName():good.getPluBarcode();
        				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
        						"订单商品["+name+"]可退["+quantity.subtract(rqty).toPlainString()+"],本次退["+qty+"],超出可退数量!");
        			}

        			//剩余数量大于0  且本次退大于0
        			if(lqty.compareTo(BigDecimal.ZERO)>0&&qty.compareTo(BigDecimal.ZERO)>0){
        				newrefundType="1";//1：部分退
        			}
        		}
        	}
        	
        	
        	if(tot_amt!=null&&tot_amt.trim().length()>0){
        		BigDecimal totAmt=new BigDecimal(tot_amt.trim());
        		//TOT_AMT	NUMBER(23,8)	Yes		63	订单总金额（含税）
        		BigDecimal orderTotAmt=new BigDecimal(ordermap.get("TOT_AMT")!=null&&ordermap.get("TOT_AMT").toString().trim().length()>0?ordermap.get("TOT_AMT").toString():"0");
        		if(totAmt.compareTo(orderTotAmt)<0){
        			newrefundType="1";//1：部分退
        		}else if(totAmt.compareTo(orderTotAmt)>0){
        			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
        					"订单["+orderNo+"]本次退款金额["+totAmt+"]已超出订单总额["+orderTotAmt+"]");
        		}
        		
        		//REFUNDAMT	NUMBER(23,8)	Yes		67	已退金额
        		BigDecimal refundAmt=new BigDecimal(ordermap.get("REFUNDAMT")!=null&&ordermap.get("REFUNDAMT").toString().trim().length()>0?ordermap.get("REFUNDAMT").toString():"0");
        		if(refundAmt.add(totAmt).subtract(orderTotAmt).compareTo(BigDecimal.ZERO)==0){
        			//标记金额已退完/最后一次退款  用来判断是否要退券 1-已退完
        			map.put("isAllRefund", "1");
        		}
        	}
        	
        	if(newrefundType!=null){
        		refundType=newrefundType;
        	}
        	map.put("refundType", refundType);
        	
        	
        	
        	HelpTools.writelog_waimai("【doRecalculateReq】单号orderNo="+orderNo+" 重算后参数:refundType="+refundType+"\r\n");
    	}
    	
    	
    	return map;
    }
    
}
