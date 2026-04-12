package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnAndOffShelfReq;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnAndOffShelfReq.*;
import com.dsc.spos.json.cust.res.DCP_N_GoodsOnAndOffShelfRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_N_GoodsOnAndOffShelf
 * 服务说明：N-商品上下架
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_GoodsOnAndOffShelf extends SPosAdvanceService<DCP_N_GoodsOnAndOffShelfReq, DCP_N_GoodsOnAndOffShelfRes>{
    @Override
    protected void processDUID(DCP_N_GoodsOnAndOffShelfReq req, DCP_N_GoodsOnAndOffShelfRes res) throws Exception {
        try{
            String billType = req.getRequest().getBillType();          //1 商品自动上下架设置  2 商品上下架操作
            String orgId = req.getRequest().getOrgId();
            String orgName = "";
            String sql = " select nvl(b.org_name,a.sname) as orgname from dcp_org a"
                    + " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='zh_CN'"
                    + "  where a.eid='"+req.geteId()+"' and a.organizationno='"+orgId+"'";
            List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
            if (CollectionUtils.isNotEmpty(getQDate)){
                orgName = getQDate.get(0).get("ORGNAME").toString();
            }

            List<Plu> pluList = req.getRequest().getPluList();
            for (Plu plu:pluList){
                String pluNo = plu.getPluNo();
                String pluName = plu.getPluName();
                String pluType = plu.getPluType();

                for (Shop shop:plu.getShopList()){
                    String shopId = shop.getShopId();
                    String shopName = shop.getShopName();

                    for (Channel channel:shop.getChannelList()){
                        String channelId = channel.getChannelId();

                        if (billType.equals("1")){
                            shelfAuto(req,pluNo,pluName,pluType,shopId,shopName,channelId,orgName);
                        }else {
                            shelf(req,pluNo,pluName,pluType,shopId,shopName,channelId,orgName);
                        }

                    }
                }
            }



            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }




    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_GoodsOnAndOffShelfReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_GoodsOnAndOffShelfReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_GoodsOnAndOffShelfReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_GoodsOnAndOffShelfReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null) {
            errMsg.append("request不能为空 ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getOrgId())){
                errMsg.append("orgId 不能为空 ");
                isFail = true;
            }

            if (Check.Null(req.getRequest().getOrgType())){
                errMsg.append("orgType 不能为空 ");
                isFail = true;
            }

            if (Check.Null(req.getRequest().getBillType())){
                errMsg.append("billType 不能为空 ");
                isFail = true;
            }else {
                if ("1".equals(req.getRequest().getBillType())){
                    if (Check.Null(req.getRequest().getOnShelfAuto())){
                        errMsg.append("onShelfAuto 不能为空 ");
                        isFail = true;
                    }else {
                        if (req.getRequest().getOnShelfAuto().equals("1")){
                            if (Check.Null(req.getRequest().getOnShelfDate())){
                                errMsg.append("onShelfDate 不能为空 ");
                                isFail = true;
                            }
                        }
                    }

                    if (Check.Null(req.getRequest().getOffShelfAuto())){
                        errMsg.append("offShelfAuto不能为空 ");
                        isFail = true;
                    }else {
                        if (req.getRequest().getOffShelfAuto().equals("1")){
                            if (Check.Null(req.getRequest().getOffShelfDate())){
                                errMsg.append("offShelfDate 不能为空 ");
                                isFail = true;
                            }
                        }
                    }
                }
            }

            if (CollectionUtils.isEmpty(req.getRequest().getPluList())){
                errMsg.append("pluList 不能为空 ");
                isFail = true;
            }else {
                for (Plu plu:req.getRequest().getPluList()){
                    if (Check.Null(plu.getPluNo())){
                        errMsg.append("pluNo 不能为空 ");
                        isFail = true;
                    }
                    if (Check.Null(plu.getPluName())){
                        errMsg.append("pluName 不能为空 ");
                        isFail = true;
                    }
                    if (Check.Null(plu.getPluType())){
                        errMsg.append("pluType 不能为空 ");
                        isFail = true;
                    }

                    if (CollectionUtils.isEmpty(plu.getShopList())){
                        errMsg.append("shopList 不能为空 ");
                        isFail = true;
                    }else{
                        for (Shop shop:plu.getShopList()){
                            if (Check.Null(shop.getShopId())){
                                errMsg.append("shopId 不能为空 ");
                                isFail = true;
                            }

                            if (Check.Null(shop.getShopName())){
                                errMsg.append("shopName 不能为空 ");
                                isFail = true;
                            }

                            if (CollectionUtils.isEmpty(shop.getChannelList())){
                                errMsg.append("channelList 不能为空 ");
                                isFail = true;
                            }else {
                                for (Channel channel:shop.getChannelList()){
                                    if (Check.Null(channel.getChannelId())){
                                        errMsg.append("channelId 不能为空 ");
                                        isFail = true;
                                    }

                                    if (isFail) {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                                    }
                                }
                            }

                            if (isFail) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                            }
                        }
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
    protected TypeToken<DCP_N_GoodsOnAndOffShelfReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsOnAndOffShelfReq>(){};
    }

    @Override
    protected DCP_N_GoodsOnAndOffShelfRes getResponseType() {
        return new DCP_N_GoodsOnAndOffShelfRes();
    }

    //商品自动上下架设置
    private void shelfAuto(DCP_N_GoodsOnAndOffShelfReq req,String pluNo,String pluName,String pluType,String shopId,String shopName,String channelId,String orgName) throws Exception{
        String eId= req.geteId();
        String orgType = req.getRequest().getOrgType();
        String orgId = req.getRequest().getOrgId();
        //String oprType = req.getRequest().getOprType();            //5上架 6下架
        String onShelfAuto = req.getRequest().getOnShelfAuto();    //是否自动上架0否1是
        String onShelfDate = "";
        if ("1".equals(onShelfAuto)) {
            onShelfDate = req.getRequest().getOnShelfDate();
        }
        String offShelfAuto = req.getRequest().getOffShelfAuto();  //是否自动下架0否1是
        String offShelfDate = "";
        if ("1".equals(offShelfAuto)) {
            offShelfDate = req.getRequest().getOffShelfDate();
        }

        String status = "0";
        if("1".equals(onShelfAuto)){
            status = "100";
        }else if("1".equals(offShelfAuto)){
            status = "0";
        }

        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql = " select pluno from dcp_goods_shelf_range "
                + " where eid='"+eId+"' and pluno='"+pluNo+"' and channelid='"+channelId+"' and shopid='"+shopId+"' ";
        List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getQDate)) {
            //新增
            String[] columns = {"EID", "PLUNO", "PLUNAME", "PLUTYPE","CHANNELID", "SHOPID", "SHOPNAME" ,"STATUS", "BILLTYPE",
                    "ORGTYPE", "ORGID", "ORGNAME", "ONSHELFAUTO","ONSHELFDATE", "OFFSHELFAUTO", "OFFSHELFDATE",
                    "CREATEOPID","CREATEOPNAME", "CREATETIME",};

            DataValue[] insValue= new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(pluName, Types.VARCHAR),
                    new DataValue(pluType, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(shopName, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),       //这个status 参考 DCP_GoodsShelfDateUpdate 服务
                    new DataValue("1", Types.VARCHAR),   //1线上商品设置  2商品上下架
                    new DataValue(orgType, Types.VARCHAR),
                    new DataValue(orgId, Types.VARCHAR),
                    new DataValue(orgName, Types.VARCHAR),
                    new DataValue(onShelfAuto, Types.VARCHAR),
                    new DataValue(onShelfDate, Types.DATE),
                    new DataValue(offShelfAuto, Types.VARCHAR),
                    new DataValue(offShelfDate, Types.DATE),
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getOpName(), Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),

            };

            InsBean ib = new InsBean("DCP_GOODS_SHELF_RANGE", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));

        }else {
            //修改
            UptBean ub1 = new UptBean("DCP_GOODS_SHELF_RANGE");

            ub1.addUpdateValue("BILLTYPE", new DataValue("1", Types.VARCHAR));      //1线上商品设置  2商品上下架
            ub1.addUpdateValue("ORGTYPE", new DataValue(orgType, Types.VARCHAR));
            ub1.addUpdateValue("ORGID", new DataValue(orgId, Types.VARCHAR));
            ub1.addUpdateValue("ORGNAME", new DataValue(orgName, Types.VARCHAR));
            ub1.addUpdateValue("ONSHELFAUTO", new DataValue(onShelfAuto, Types.VARCHAR));
            ub1.addUpdateValue("ONSHELFDATE", new DataValue(onShelfDate, Types.DATE));
            ub1.addUpdateValue("OFFSHELFAUTO", new DataValue(offShelfAuto, Types.VARCHAR));
            ub1.addUpdateValue("OFFSHELFDATE", new DataValue(offShelfDate, Types.DATE));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            ub1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));

        }

    }

    //商品上下架设置
    private void shelf(DCP_N_GoodsOnAndOffShelfReq req,String pluNo,String pluName,String pluType,String shopId,String shopName,String channelId,String orgName) throws Exception{

        String eId= req.geteId();
        String orgType = req.getRequest().getOrgType();
        String orgId = req.getRequest().getOrgId();
        String oprType = req.getRequest().getOprType();            //5上架 6下架
        //String onShelfAuto = req.getRequest().getOnShelfAuto();    //是否自动上架0否1是
        //String onShelfDate = req.getRequest().getOnShelfDate();
        //String offShelfAuto = req.getRequest().getOffShelfAuto();  //是否自动下架0否1是
        //String offShelfDate = req.getRequest().getOffShelfDate();
        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String status = "100";
        if ("6".equals(oprType)){
            status = "0";
        }

        String sql = " select pluno from dcp_goods_shelf_range "
                + " where eid='"+eId+"' and pluno='"+pluNo+"' and channelid='"+channelId+"' and shopid='"+shopId+"' ";
        List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getQDate)) {
            //新增
            String[] columns = {"EID", "PLUNO", "PLUNAME", "PLUTYPE","CHANNELID", "SHOPID", "SHOPNAME", "STATUS", "BILLTYPE",
                    "ORGTYPE", "ORGID", "ORGNAME", "ONSHELFAUTO","ONSHELFDATE", "OFFSHELFAUTO", "OFFSHELFDATE",
                    "CREATEOPID","CREATEOPNAME", "CREATETIME" };

            DataValue[] insValue= new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(pluName, Types.VARCHAR),
                    new DataValue(pluType, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(shopName, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),   //1线上商品设置  2商品上下架
                    new DataValue(orgType, Types.VARCHAR),
                    new DataValue(orgId, Types.VARCHAR),
                    new DataValue(orgName, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),    //onShelfAuto
                    new DataValue("", Types.DATE),       //onShelfDate
                    new DataValue("", Types.VARCHAR),    //offShelfAuto
                    new DataValue("", Types.DATE),       //offShelfDate
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getOpName(), Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),

            };

            InsBean ib = new InsBean("DCP_GOODS_SHELF_RANGE", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));

        }else {
            //修改
            UptBean ub1 = new UptBean("DCP_GOODS_SHELF_RANGE");

            ub1.addUpdateValue("BILLTYPE", new DataValue("2", Types.VARCHAR));      //1线上商品设置  2商品上下架
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("ORGTYPE", new DataValue(orgType, Types.VARCHAR));
            ub1.addUpdateValue("ORGID", new DataValue(orgId, Types.VARCHAR));
            ub1.addUpdateValue("ORGNAME", new DataValue(orgName, Types.VARCHAR));
            //ub1.addUpdateValue("ONSHELFAUTO", new DataValue(onShelfAuto, Types.VARCHAR));
            //ub1.addUpdateValue("ONSHELFDATE", new DataValue(onShelfDate, Types.DATE));
            //ub1.addUpdateValue("OFFSHELFAUTO", new DataValue(offShelfAuto, Types.VARCHAR));
            //ub1.addUpdateValue("OFFSHELFDATE", new DataValue(offShelfDate, Types.DATE));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            ub1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));

        }

    }

}
