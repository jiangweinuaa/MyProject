package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_N_GoodsShelfQueryReq;
import com.dsc.spos.json.cust.res.DCP_N_GoodsShelfQueryRes;
import com.dsc.spos.json.cust.res.DCP_N_GoodsShelfQueryRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.util.stream.Collectors;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 服务函数：DCP_N_GoodsShelfQuery
 * 服务说明：N-商品上下架查询
 * @author jinzma
 * @since  2024-04-22
 */
public class DCP_N_GoodsShelfQuery extends SPosBasicService<DCP_N_GoodsShelfQueryReq, DCP_N_GoodsShelfQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_GoodsShelfQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null) {
            errMsg.append("request不能为空 ");
            isFail = true;
        } else {
            if (Check.Null(req.getRequest().getPluNo())) {
                errMsg.append("pluNo 不能为空值 ，");
                isFail = true;
            }

            if (CollectionUtils.isEmpty(req.getRequest().getShopList())) {
                errMsg.append("shopList 不能为空值 ，");
                isFail = true;
            }else {
                for (DCP_N_GoodsShelfQueryReq.Shop shop:req.getRequest().getShopList()){
                    if (Check.Null(shop.getShopId())) {
                        errMsg.append("shopId 不能为空值 ，");
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
    protected TypeToken<DCP_N_GoodsShelfQueryReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsShelfQueryReq>(){};
    }

    @Override
    protected DCP_N_GoodsShelfQueryRes getResponseType() {
        return new DCP_N_GoodsShelfQueryRes();
    }

    @Override
    protected DCP_N_GoodsShelfQueryRes processJson(DCP_N_GoodsShelfQueryReq req) throws Exception {
        DCP_N_GoodsShelfQueryRes res = this.getResponse();
        Datas datas = res.new Datas();
        datas.setPluNo(req.getRequest().getPluNo());
        datas.setShopList(new ArrayList<>());
        try {

            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtils.isNotEmpty(getQData)){

                List<Map<String, Object>> shopList = getQData.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(()-> new TreeSet<>(
                                        Comparator.comparing(p->p.get("SHOPID").toString()))
                                ),ArrayList::new));
                //去重以后需要重新排序
                shopList = shopList.stream().sorted(
                        Comparator.comparing(p->p.get("SHOPID").toString())
                ).collect(Collectors.toList());

                for (Map<String, Object> shops:shopList){
                    String shopId = shops.get("SHOPID").toString();
                    Shop shop = res.new Shop();

                    shop.setShopId(shopId);
                    shop.setShopName(shops.get("SHOPNAME").toString());
                    shop.setChannelList(new ArrayList<>());

                    List<Map<String, Object>> channelList = getQData.stream()
                            .filter(p->p.get("SHOPID").toString().equals(shopId))
                            .collect(Collectors.toList());

                    for (Map<String, Object> channels:channelList) {
                        Channel channel = res.new Channel();

                        channel.setChannelId(channels.get("CHANNELID").toString());
                        channel.setChannelName(channels.get("CHANNELNAME").toString());
                        channel.setOnShelfAuto(channels.get("ONSHELFAUTO").toString());
                        channel.setOnShelfDate(channels.get("ONSHELFDATE").toString());
                        channel.setOffShelfAuto(channels.get("OFFSHELFAUTO").toString());
                        channel.setOffShelfDate(channels.get("OFFSHELFDATE").toString());
                        channel.setStatus(channels.get("STATUS").toString());

                        shop.getChannelList().add(channel);

                    }

                    datas.getShopList().add(shop);
                }


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
    protected String getQuerySql(DCP_N_GoodsShelfQueryReq req) throws Exception {

        MyCommon mc = new MyCommon();
        Map<String,String> map = new HashMap<>();

        String sJoinShop = "";
        for(DCP_N_GoodsShelfQueryReq.Shop shop :req.getRequest().getShopList()) {
            sJoinShop += shop.getShopId()+",";
        }
        map.put("SHOPID", sJoinShop);
        String withShop = mc.getFormatSourceMultiColWith(map);
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" with shops as (" + withShop + ")");
        sqlbuf.append(" select a.*,c.channelname from dcp_goods_shelf_range a"
                + " inner join shops on a.shopid=shops.shopid"
                + " left  join crm_channel c on a.eid=c.eid and a.channelid=c.channelid"
                + " where a.eid='"+eId+"' and a.pluno='"+req.getRequest().getPluNo()+"' "
                + " order by a.shopid,a.channelid ");

        return sqlbuf.toString();

    }
}
