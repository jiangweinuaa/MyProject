package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_TransferPOrderQueryReq;
import com.dsc.spos.json.cust.req.DCP_TransferPOrderQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_TransferPOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class DCP_TransferPOrderQuery  extends SPosBasicService<DCP_TransferPOrderQueryReq,DCP_TransferPOrderQueryRes > {
    Logger logger = LogManager.getLogger(DCP_TransferPOrderQuery.class.getName());
    @Override
    protected boolean isVerifyFail(DCP_TransferPOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        return false;
    }
    
    @Override
    protected TypeToken<DCP_TransferPOrderQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransferPOrderQueryReq>(){};
    }
    
    @Override
    protected DCP_TransferPOrderQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransferPOrderQueryRes();
    }
    
    @Override
    protected DCP_TransferPOrderQueryRes processJson(DCP_TransferPOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        String sql = null;
        String queryStockqty=req.getRequest().getQueryStockqty();
        try
        {
            DCP_TransferPOrderQueryRes res = this.getResponse();
            sql = this.getQuerySql(req);		//查询总笔数
          //  logger.info("调拨要货查询SQL:"+sql);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            res.setDatas(new ArrayList<DCP_TransferPOrderQueryRes.level1Elm>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("SHOPID", true);
                condition.put("PORDERNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
                for (Map<String, Object> oneData : getQHeader) {
                    DCP_TransferPOrderQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setDatas(new ArrayList<DCP_TransferPOrderQueryRes.level2Elm>());
                    //取出第一层
                    String porderShopNO = oneData.get("SHOPID").toString();
                    String porderShopName = oneData.get("ORG_NAME").toString();
                    String porderNO = oneData.get("PORDERNO").toString();
                    String bDate= oneData.get("BDATE").toString();
                    String rDate = oneData.get("RDATE").toString();
                    String rTime = oneData.get("RTIME").toString();
                    String isUrgentOrder = oneData.get("ISURGENTORDER").toString();
                    String memo = oneData.get("MEMO").toString();
                    String totPqty = oneData.get("TOT_PQTY").toString();
                    String totCqty = oneData.get("TOT_CQTY").toString();
                    String totAmt = oneData.get("TOT_AMT").toString();
                    String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                    String porderInCostWarehouse = oneData.get("IN_COST_WAREHOUSE").toString();
					String ptemplateNo = oneData.get("PTEMPLATENO").toString();
					String ptemplateName=oneData.get("PTEMPLATE_NAME").toString();
                    for (Map<String, Object> oneDataDetail : getQData)
                    {
                        //过滤属于此单头的明细
                        if(porderShopNO.equals(oneDataDetail.get("SHOPID")) &&porderNO.equals(oneDataDetail.get("PORDERNO")) )
                        {
                            DCP_TransferPOrderQueryRes.level2Elm oneLv2 = res.new level2Elm();
                            String item = oneDataDetail.get("ITEM").toString();
                            String pluNO = oneDataDetail.get("PLUNO").toString();
                            String pluName = oneDataDetail.get("PLU_NAME").toString();
                            String punit = oneDataDetail.get("PUNIT").toString();
                            String punitName = oneDataDetail.get("PUNITNAME").toString();
                            String baseUnit = oneDataDetail.get("BASEUNIT").toString();
                            String baseUnitName = oneDataDetail.get("BASEUNITNAME").toString();
                            String unitRatio = oneDataDetail.get("UNIT_RATIO").toString();
                            String pqty = oneDataDetail.get("PQTY").toString();
                            String price = oneDataDetail.get("PRICE").toString();
                            String amt = oneDataDetail.get("AMT").toString();
                            String listimage = oneDataDetail.get("LISTIMAGE").toString();
                            if (!Check.Null(listimage)){
                                listimage = domainName+listimage;
                            }
                            String spec = oneDataDetail.get("SPEC").toString();
                            String distriPrice = oneDataDetail.get("DISTRIPRICE").toString();
                            String isBatch = oneDataDetail.get("ISBATCH").toString();
                            String distriAmt = oneDataDetail.get("DISTRIAMT").toString();
                            String punitUDLength = oneDataDetail.get("PUNIT_UDLENGTH").toString();
                            String baseQty = oneDataDetail.get("BASEQTY").toString();
                            String featureNo = oneDataDetail.get("FEATURENO").toString();
                            String featureName = oneDataDetail.get("FEATURENAME").toString();
                            String stockmanagetype = oneDataDetail.get("STOCKMANAGETYPE").toString();
                            
                            oneLv2.setAmt(amt);
                            oneLv2.setItem(item);
                            oneLv2.setPluName(pluName);
                            oneLv2.setPluNo(pluNO);
                            oneLv2.setPqty(pqty);
                            oneLv2.setPrice(price);
                            oneLv2.setPunit(punit);
                            oneLv2.setPunitName(punitName);
                            oneLv2.setUnitRatio(unitRatio);
                            oneLv2.setBaseUnit(baseUnit);
                            oneLv2.setBaseUnitName(baseUnitName);
                            oneLv2.setListImage(listimage);
                            oneLv2.setSpec(spec);
                            oneLv2.setDistriPrice(distriPrice);
                            oneLv2.setIsBatch(isBatch);
                            oneLv2.setDistriAmt(distriAmt);
                            oneLv2.setPunitUdLength(punitUDLength);
                            oneLv2.setBaseQty(baseQty);
                            oneLv2.setFeatureNo(featureNo);
                            oneLv2.setFeatureName(featureName);
                            oneLv2.setStockManageType(stockmanagetype);
                            oneLv2.setStockqty("999999");
                            //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                            oneLv2.setBaseUnitUdLength(oneDataDetail.get("BASEUNITUDLENGTH").toString());
                            
                            oneLv1.getDatas().add(oneLv2);
                        }
                    }
                    
                    oneLv1.setIsUrgentOrder(isUrgentOrder);
                    oneLv1.setMemo(memo);
                    oneLv1.setPorderNo(porderNO);
                    oneLv1.setPorderShopName(porderShopName);
                    oneLv1.setPorderShopNo(porderShopNO);
                    oneLv1.setbDate(bDate);
                    oneLv1.setrDate(rDate);
                    oneLv1.setrTime(rTime);
                    oneLv1.setTotAmt(totAmt);
                    oneLv1.setTotCqty(totCqty);
                    oneLv1.setTotPqty(totPqty);
                    oneLv1.setTotDistriAmt(totDistriAmt);
                    oneLv1.setPorderInCostWarehouse(porderInCostWarehouse);
					oneLv1.setPtemplateNo(ptemplateNo);
					oneLv1.setPtemplateName(ptemplateName);
                    res.getDatas().add(oneLv1);
                }
            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            //处理库存量===
            if (queryStockqty!=null && queryStockqty.equals("Y"))
            {
                if (res.getDatas() != null && res.getDatas().size()>0)
                {
                    //
                    JSONArray pluList=new JSONArray();
                    for (DCP_TransferPOrderQueryRes.level2Elm p1 : res.getDatas().get(0).getDatas())
                    {
                        if (p1.getStockManageType()!=null && (p1.getStockManageType().equals("2") || p1.getStockManageType().equals("3")))
                        {
                            //无特征码
                            JSONObject plu=new JSONObject();
                            plu.put("pluNo",p1.getPluNo());
                            plu.put("featureNo",p1.getFeatureNo());
                            pluList.put(plu);
                        }
                    }
                    if (pluList != null && pluList.length()>0)
                    {
                        JSONObject req_SOLD = new JSONObject();
                        req_SOLD.put("serviceId","DCP_GoodsStockQuery");
                        req_SOLD.put("token", req.getToken());
                        // //JOB调订转销用到，没token
                        req_SOLD.put("eId",req.geteId());
                        req_SOLD.put("eShop",req.getShopId());
                        
                        //
                        JSONObject request_SOLD = new JSONObject();
                        request_SOLD.put("eId", req.geteId());
                        request_SOLD.put("queryOrgId", req.getShopId());
                        request_SOLD.put("queryType", "DCP");
                        request_SOLD.put("pluList", pluList);
                        req_SOLD.put("request", request_SOLD);
                        
                        String str_SOLD = req_SOLD.toString();// 将json对象转换为字符串
                        
                        //内部调内部
                        DispatchService ds = DispatchService.getInstance();
                        String resbody_SOLD = ds.callService(str_SOLD, this.dao);
                        
                        if (resbody_SOLD.equals("")==false)
                        {
                            JSONObject jsonres_SOLD = new JSONObject(resbody_SOLD);
                            boolean success = jsonres_SOLD.getBoolean("success");
                            if (success)
                            {
                                JSONObject datas_SOLD=jsonres_SOLD.getJSONObject("datas");
                                JSONArray res_pluList=datas_SOLD.getJSONArray("pluList");
                                if (res_pluList!=null && res_pluList.length()>0)
                                {
                                    for (int ri = 0; ri < res_pluList.length(); ri++)
                                    {
                                        String r_pluno=res_pluList.getJSONObject(ri).get("pluNo").toString();
                                        String r_baseunit=res_pluList.getJSONObject(ri).get("baseUnit").toString();
                                        String r_baseqty=res_pluList.getJSONObject(ri).get("baseQty").toString();
                                        //汇总特征码库存设置
                                        List<DCP_TransferPOrderQueryRes.level2Elm> goods=res.getDatas().get(0).getDatas().stream().filter(c->c.getPluNo().equals(r_pluno)).collect(Collectors.toList());
                                        goods.stream().forEach(d->{d.setStockqty(r_baseqty);});
                                        //特征码库量存设置
                                        JSONArray res_featureList=res_pluList.getJSONObject(ri).getJSONArray("featureList");
                                        if (res_featureList!=null && res_featureList.length()>0)
                                        {
                                            for (int si = 0; si < res_featureList.length(); si++)
                                            {
                                                String r_featureNo=res_featureList.getJSONObject(si).get("featureNo").toString();
                                                String r_baseQty=res_featureList.getJSONObject(si).get("baseQty").toString();
                                                goods.stream().filter(c->c.getFeatureNo().equals(r_featureNo)).forEach(d->{
                                                    d.setStockqty(r_baseQty);
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
            
            
            
            
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return res;
            
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根
        
    }
    
    @Override
    protected String getQuerySql(DCP_TransferPOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        String sql=null ;
        String eId = req.geteId();
        String shopId=req.getShopId();
        String langType=req.getLangType();
        levelElm request = req.getRequest();
        String orderShopNO=request.getShopId();
        String beginDate=request.getBeginDate();
        String endDate = request.getEndDate();
        
        StringBuffer sqlbuf=new StringBuffer("");
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        sqlbuf.append( " select * from ( "
                + " select num,rn,a.EID,a.SHOPID,g.org_name,a.porderno,a.rdate,a.rtime,a.isUrgentOrder,a.memo,a.tot_Pqty,"
                + " a.tot_Cqty,a.tot_Amt,a.bdate,a.TOT_DISTRIAMT, "
                + " b.item,b.pluno,d.plu_name,b.punit,e.uname as punitname,b.baseunit,f.uname as baseunitname,b.pqty,"
                + " b.price,b.DISTRIPRICE,b.amt,b.DISTRIAMT,b.baseqty,b.featureno,fn.featurename,bul.udlength as baseunitudlength, "
                + " gul.spec,c.isbatch,b.unit_Ratio,h.IN_COST_WAREHOUSE,HH.UDLENGTH as punit_UDLENGTH,image.listimage,c.STOCKMANAGETYPE "
                + " ,a.PTEMPLATENO , PTL.PTEMPLATE_NAME "
                + " from ("
                + " select count(*) over() num,row_number() over (order by a.porderno ) rn,a.EID,a.SHOPID,a.porderno, "
                + " a.rdate,a.rtime,a.isUrgentOrder,a.memo,a.tot_Pqty,a.tot_Cqty,a.tot_Amt,a.bdate,a.TOT_DISTRIAMT,a.PTEMPLATENO  from DCP_PORDER a "
                + " left join DCP_stockout c "
                + " on a.EID=c.EID and a.receipt_org=c.SHOPID "
                + " and a.porderno=c.ofno and a.SHOPID=c.Transfer_Shop "
                + " where a.EID='"+eId+"' and a.RECEIPT_ORG ='"+shopId+"'  and a.status='2'  and c.ofno is null  " );
        
        if (!Check.Null(orderShopNO))
        {
            sqlbuf.append(" and a.SHOPID = '"+orderShopNO+"' ");
        }
        if (!Check.Null(beginDate)&& ! Check.Null(endDate))
        {
            sqlbuf.append(" and a.bdate>='"+beginDate+"' and a.bdate<='"+endDate+"'  ");
        }
        sqlbuf.append(  " )  a "
                + " inner join DCP_Porder_Detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.porderno=b.porderno "
                + " inner join DCP_GOODS c on c.EID=a.EID and c.pluno=b.pluno and c.status='100' "
                + " left join DCP_GOODS_lang d on d.EID=a.EID and d.pluno=b.pluno and d.lang_type='"+langType+"'  "
                + " left join DCP_UNIT_lang e on e.EID=a.EID and e.unit=b.punit and e.lang_type='"+langType+"'  "
                + " left join DCP_UNIT_lang f on f.EID=a.EID and f.unit=b.baseunit and f.lang_type='"+langType+"'  "
                + " left join DCP_ORG_lang g on g.EID=a.EID and g.organizationno=a.SHOPID and g.lang_type='"+langType+"'  "
                + " left join DCP_ORG h on h.EID=a.EID and h.organizationno=a.SHOPID and h.status='100' "
                + " left join DCP_UNIT HH on HH.EID=a.EID and HH.unit=b.punit "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                + " left join dcp_unit bul on a.eid=bul.eid and b.baseunit=bul.unit "
				+ " left join DCP_PTEMPLATE PTL on a.EID=PTL.EID and a.PTEMPLATENO=PTL.PTEMPLATENO "
                + " )where rn>"+startRow+" and rn<="+(startRow+pageSize) );
        
        sql = sqlbuf.toString();
        return sql;
        
    }
    
}
