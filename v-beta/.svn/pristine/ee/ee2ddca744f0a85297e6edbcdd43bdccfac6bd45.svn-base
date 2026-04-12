package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_N_StockAllocationRuleDetailReq;
import com.dsc.spos.json.cust.res.DCP_N_StockAllocationRuleDetailRes;
import com.dsc.spos.json.cust.res.DCP_N_StockAllocationRuleDetailRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_N_StockAllocationRuleDetail
 * 服务说明：N_分配规则设置查询
 * @author jinzma
 * @since  2024-05-07
 */
public class DCP_N_StockAllocationRuleDetail extends SPosBasicService<DCP_N_StockAllocationRuleDetailReq, DCP_N_StockAllocationRuleDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_StockAllocationRuleDetailReq req) throws Exception {
        boolean isFail = false;

        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request 不可为空值, ");
            isFail = true;
        }else {

            if (Check.Null(req.getRequest().getPluNo())){
                errMsg.append("pluNo 不可为空值, ");
                isFail = true;
            }
            List<DCP_N_StockAllocationRuleDetailReq.Organization> organizationList = req.getRequest().getOrganizationList();
            if (CollectionUtils.isEmpty(organizationList)){
                errMsg.append("organizationList 不可为空值, ");
                isFail = true;
            }else {
                for (DCP_N_StockAllocationRuleDetailReq.Organization organization:organizationList){
                    if (Check.Null(organization.getOrganizationNo())){
                        errMsg.append("organizationNo 不可为空值, ");
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
    protected TypeToken<DCP_N_StockAllocationRuleDetailReq> getRequestType() {
        return new TypeToken<DCP_N_StockAllocationRuleDetailReq>(){};
    }

    @Override
    protected DCP_N_StockAllocationRuleDetailRes getResponseType() {
        return new DCP_N_StockAllocationRuleDetailRes();
    }

    @Override
    protected DCP_N_StockAllocationRuleDetailRes processJson(DCP_N_StockAllocationRuleDetailReq req) throws Exception {
        DCP_N_StockAllocationRuleDetailRes res = this.getResponse();
        Datas datas = res.new Datas();
        datas.setPluList(new ArrayList<>());
        try {

            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);

            if (CollectionUtil.isNotEmpty(getQData)){

                Plu plu = res.new Plu();
                plu.setPluNo(getQData.get(0).get("PLUNO").toString());
                plu.setPluName(getQData.get(0).get("PLU_NAME").toString());
                plu.setFeatureNo(" ");  //小程序合并干掉了特征码，原本功能丢失，只有普通商品和特征码商品
                plu.setFeatureName("");
                plu.setSUnit(getQData.get(0).get("SUNIT").toString());
                plu.setSUnitName(getQData.get(0).get("UNAME").toString());
                plu.setShopList(new ArrayList<>());

                List<Map<String, Object>> shopList = getQData.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(()-> new TreeSet<>(
                                        Comparator.comparing(p->p.get("ORGANIZATIONNO").toString()))
                                ),ArrayList::new));
                //去重以后需要重新排序
                shopList = shopList.stream().sorted(
                        Comparator.comparing(p->p.get("ORGANIZATIONNO").toString())
                ).collect(Collectors.toList());

                for (Map<String, Object> oneShop : shopList) {
                    Shop shop = res.new Shop();
                    String organizationNo = oneShop.get("ORGANIZATIONNO").toString();
                    shop.setOrganizationNo(organizationNo);
                    shop.setOrganizationName(oneShop.get("ORG_NAME").toString());
                    shop.setRuleType(oneShop.get("RULETYPE").toString());
                    shop.setChannelList(new ArrayList<>());

                    List<Map<String, Object>> channelList = getQData.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(organizationNo)).collect(Collectors.toList());
                    for (Map<String, Object> oneChannel : channelList) {
                        Channel channel = res.new Channel();

                        channel.setChannelId(oneChannel.get("CHANNELID").toString());
                        channel.setChannelName(oneChannel.get("CHANNELNAME").toString());
                        channel.setAllocationValue(oneChannel.get("ALLOCATIONVALUE").toString());

                        shop.getChannelList().add(channel);
                    }

                    plu.getShopList().add(shop);
                }

                datas.getPluList().add(plu);

            }


            res.setDatas(datas);

            return res;


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_N_StockAllocationRuleDetailReq req) throws Exception {
        String eId = req.geteId();
        String pluNo = req.getRequest().getPluNo();
        List<DCP_N_StockAllocationRuleDetailReq.Organization> organizationList = req.getRequest().getOrganizationList();
        MyCommon mc = new MyCommon();
        Map<String,String> map = new HashMap<>();
        String sJoinOrganization = "";
        for(DCP_N_StockAllocationRuleDetailReq.Organization organization :organizationList) {
            sJoinOrganization += organization.getOrganizationNo() +",";
        }
        map.put("ORGANIZATIONNO", sJoinOrganization);

        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" select a.organizationno,a.pluno,a.featureno,a.sunit,a.channelid,a.ruletype,a.allocationvalue,"
                + " c.plu_name,d.uname,e.org_name,f.channelname "
                + " from dcp_stock_allocation_rule a");
        sqlbuf.append(" inner join ( " + mc.getFormatSourceMultiColWith(map) + " )b on a.organizationno=b.organizationno");
        sqlbuf.append(" left join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' ");
        sqlbuf.append(" left join dcp_unit_lang d on a.eid=d.eid and a.sunit=d.unit and d.lang_type='"+req.getLangType()+"' ");
        sqlbuf.append(" left join dcp_org_lang e on a.eid=e.eid and a.organizationno=e.organizationno and e.lang_type='"+req.getLangType()+"' ");
        sqlbuf.append(" left join crm_channel f on a.eid=f.eid and a.channelid=f.channelid");
        sqlbuf.append(" where a.eid='"+eId+"' and a.pluno='"+pluNo+"' order by a.organizationno,a.channelid ");

        return sqlbuf.toString();

    }
}
