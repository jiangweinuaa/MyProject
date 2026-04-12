package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess2_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcessReq;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.util.*;

/**
 * 服務函數：OrderToSaleProcess
 * 服务说明：订单转销售
 */

public class DCP_OrderToSaleProcess extends SPosAdvanceService<DCP_OrderToSaleProcessReq,DCP_OrderToSaleProcess_OpenRes>
{
    @Override
    protected void processDUID(DCP_OrderToSaleProcessReq req, DCP_OrderToSaleProcess_OpenRes res) throws Exception
    {
        res.setDatas(res.new level1());
        res.getDatas().setVipDatas(res.new level2());
        res.getDatas().setInvoiceInfo(res.new invoice());

        //内部调外部
        //**内部接口与外部接口代码共用,外部服务里面用到apiUser接口帐号相关的要进行特殊处理，
        //**可以通过此节点apiUser判断是外部还是内部服务

        //1.深拷贝一份源服务请求
        ParseJson pj = new ParseJson();
        String jsonReq=pj.beanToJson(req);
        DCP_OrderToSaleProcess2_OpenReq dcp_OrderToSaleProcess2_OpenReq=pj.jsonToBean(jsonReq, new TypeToken<DCP_OrderToSaleProcess2_OpenReq>(){});
        //2.目标服务部分字段需重新给值
        dcp_OrderToSaleProcess2_OpenReq.setServiceId("DCP_OrderToSaleProcess2_Open");
        //dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpBDate();
        //dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpMachineNo();
        //dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpSquadNo();
        //dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpWorkNo();
        dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpShopId(req.getShopId());
        dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpOpName(req.getOpName());
        dcp_OrderToSaleProcess2_OpenReq.getRequest().setOpOpNo(req.getOpNO());
        //3.调用目标服务
        DCP_OrderToSaleProcess2_Open dcp_OrderToSaleProcess2_Open=new DCP_OrderToSaleProcess2_Open();
        DCP_OrderToSaleProcess_OpenRes dcp_OrderToSaleProcess_OpenRes=new DCP_OrderToSaleProcess_OpenRes();
        dcp_OrderToSaleProcess2_Open.setDao(this.dao);
        dcp_OrderToSaleProcess2_Open.processDUID(dcp_OrderToSaleProcess2_OpenReq,dcp_OrderToSaleProcess_OpenRes);

        //4.处理服务返回
        //*注意
        //*res不能重新初始化，会导致赋值返回无效，就像下面这句不能用
        //res=pj.jsonToBean(jsonRes,new TypeToken<DCP_OrderToSaleProcess_OpenRes>(){});
        res.setSuccess(dcp_OrderToSaleProcess_OpenRes.isSuccess());
        res.setServiceStatus(dcp_OrderToSaleProcess_OpenRes.getServiceStatus());
        res.setServiceDescription(dcp_OrderToSaleProcess_OpenRes.getServiceDescription());
        dcp_OrderToSaleProcess2_Open=null;
        pj=null;
        dcp_OrderToSaleProcess_OpenRes=null;
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderToSaleProcessReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderToSaleProcessReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderToSaleProcessReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderToSaleProcessReq req) throws Exception
    {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId()) )
        {
            errMsg.append("企业编码eId不可为空值, ");
            isFail = true;
        }

        List<DCP_OrderToSaleProcessReq.goods> goodsList=req.getRequest().getGoodsList();

        if (goodsList!=null)
        {
            for (DCP_OrderToSaleProcessReq.goods goods : goodsList)
            {
                if (Check.Null(goods.getItem()) )
                {
                    errMsg.append("商品项次item不可为空值, ");
                    isFail = true;
                }
                if (goods.getQty().compareTo(BigDecimal.ZERO)<=0)
                {
                    errMsg.append("商品数量qty必须>0, ");
                    isFail = true;
                }
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;

    }

    @Override
    protected TypeToken<DCP_OrderToSaleProcessReq> getRequestType()
    {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_OrderToSaleProcessReq>(){};
    }

    @Override
    protected DCP_OrderToSaleProcess_OpenRes getResponseType()
    {
        // TODO 自动生成的方法存根
        return new DCP_OrderToSaleProcess_OpenRes();
    }





}
