package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPriceDiscQueryReq;
import com.dsc.spos.json.cust.req.DCP_POrderNonArrivalReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceDiscQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import microsoft.exchange.webservices.data.core.IFileAttachmentContentHandler;

import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_CustomerPriceDiscQuery extends SPosBasicService<DCP_CustomerPriceDiscQueryReq, DCP_CustomerPriceDiscQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceDiscQueryReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String customerNo=req.getRequest().getCustomerNo();
        List<DCP_CustomerPriceDiscQueryReq.level2Elm> pluList = req.getRequest().getPluList();

        if(Check.Null(customerNo))
        {
            errCt++;
            errMsg.append("客户编码不可为空值, ");
            isFail = true;
        }
        if(pluList==null||pluList.isEmpty()){
            errMsg.append ( "商品列表pluList不能为空");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceDiscQueryReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceDiscQueryReq>(){};
    }

    @Override
    protected DCP_CustomerPriceDiscQueryRes getResponseType() {
        return new DCP_CustomerPriceDiscQueryRes();
    }

    @Override
    protected DCP_CustomerPriceDiscQueryRes processJson(DCP_CustomerPriceDiscQueryReq req) throws Exception {
        DCP_CustomerPriceDiscQueryRes res = this.getResponse();
        String eId = req.geteId();
        String customerNo = req.getRequest().getCustomerNo();
        String langType = req.getLangType();
        List<DCP_CustomerPriceDiscQueryReq.level2Elm> pluList = req.getRequest().getPluList();
        StringBuffer sJoinPluNo = new StringBuffer();
        StringBuffer sJoinFeatureNo = new StringBuffer();
        for (DCP_CustomerPriceDiscQueryReq.level2Elm par : pluList) {
            String featureNo = par.getFeatureNo();
            if (Check.Null(featureNo)){
                featureNo = " ";
            }
            sJoinPluNo.append(par.getPluNo() + ",");
            sJoinFeatureNo.append(featureNo + ",");
        }
        Map<String, String> map = new HashMap<>();
        map.put("PLUNO", sJoinPluNo.toString());
        map.put("FEATURENO", sJoinFeatureNo.toString());

        MyCommon cm = new MyCommon();
        String withPlu = cm.getFormatSourceMultiColWith(map);
        String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //先查询下 大客户对应的商品价格
        StringBuffer sqlBuffer = new StringBuffer("");
        sqlBuffer.append(" select * from (");
        sqlBuffer.append(" select a.*,g.category,u.uname,row_number() over(partition by a.pluno,a.featureno  order by a.item desc) rn ");
        sqlBuffer.append(" from dcp_customer_price a ");
        sqlBuffer.append(" inner join ("+withPlu+")p on a.pluno=p.pluno and a.featureno=p.featureno");
        sqlBuffer.append(" left join dcp_goods g on g.pluno=p.pluno and g.eid='"+eId+"'");
        sqlBuffer.append(" left join dcp_unit_lang u on u.unit=a.unit and u.eid=a.eid and u.lang_type='"+langType+"'");
        sqlBuffer.append(" where a.eid='"+eId+"' and a.customerno='"+customerNo+"'");
        sqlBuffer.append(" and BEGINDATE<='"+curDate+"' and ENDDATE>='"+curDate+"'");
        sqlBuffer.append(" ) where rn=1");
        String sql = sqlBuffer.toString();
        List<Map<String,Object>> getPriceList = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());
        if (getPriceList == null || getPriceList.isEmpty())
        {
            //查询不到价格直接返回，
            return res;
        }
        //查询下大客户对应的商品分类折扣
        sqlBuffer.setLength(0);
        sql = "";
        sqlBuffer.append(" select * from (");
        sqlBuffer.append(" select a.*,row_number() over(partition by a.categoryid order by a.item desc) rn from");
        sqlBuffer.append(" ("+withPlu+")p");
        sqlBuffer.append(" inner join dcp_goods b on b.pluno=p.pluno and b.eid='"+eId+"'");
        sqlBuffer.append(" inner join dcp_customer_cate_disc a on a.eid=b.eid and a.categoryid=b.category");
        sqlBuffer.append(" where a.eid='"+eId+"' and a.customerno='"+customerNo+"' and a.status='100'");
        sqlBuffer.append(" ) where rn=1");
        sql = sqlBuffer.toString();
        List<Map<String,Object>> getDiscList = this.doQueryData(sql,null);


        for (DCP_CustomerPriceDiscQueryReq.level2Elm par : pluList)
        {
            String pluNo = par.getPluNo();
            String featureNo = par.getFeatureNo();
            if (Check.Null(featureNo)){
                featureNo = " ";
            }
            boolean isExistPrice = false;
            if (getPriceList != null && !getPriceList.isEmpty())
            {
                DCP_CustomerPriceDiscQueryRes.level1Elm onelv1 = res.new level1Elm();
                for (Map<String,Object> priceMap :  getPriceList)
                {
                    String pluNo_price = priceMap.get("PLUNO").toString();
                    String featureNo_price = priceMap.get("FEATURENO").toString();
                    String price = priceMap.get("PRICE").toString();
                    String unit = priceMap.get("UNIT").toString();
                    String unitName = priceMap.get("UNAME").toString();
                    String category = priceMap.get("CATEGORY").toString();
                    if (pluNo.isEmpty())
                    {
                        continue;
                    }
                    if (pluNo.equals(pluNo_price)&&featureNo.equals(featureNo_price))
                    {
                        onelv1.setPluNo(pluNo);
                        onelv1.setFeatureNo(featureNo);
                        onelv1.setPrice(price);
                        onelv1.setUnit(unit);
                        onelv1.setUnitName(unitName);
                        onelv1.setCategory(category);
                        onelv1.setDiscRate("100");//默认折扣100，防止后面找不到
                        isExistPrice = true;
                        break;//找到就跳出循环
                    }
                }

                //找下品类折扣
                if (isExistPrice)
                {
                    if (getDiscList != null && !getDiscList.isEmpty())
                    {
                        for (Map<String,Object> discMap :  getDiscList)
                        {
                            String category_disc = discMap.get("CATEGORYID").toString();
                            String discRate = discMap.getOrDefault("DISCRATE","100").toString();
                            if (onelv1.getCategory()==null||onelv1.getCategory().isEmpty())
                            {
                                continue;
                            }
                            if (onelv1.getCategory().equals(category_disc))
                            {
                                onelv1.setDiscRate(discRate);
                                break;//找到就跳出循环
                            }

                        }

                    }
                }
                res.getDatas().add(onelv1);
            }


        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CustomerPriceDiscQueryReq req) throws Exception {
        return null;
    }
}
