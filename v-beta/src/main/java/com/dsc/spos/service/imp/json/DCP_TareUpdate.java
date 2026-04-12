package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_TareUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TareUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_TareUpdate extends SPosAdvanceService<DCP_TareUpdateReq, DCP_TareUpdateRes>
{


    @Override
    protected void processDUID(DCP_TareUpdateReq req, DCP_TareUpdateRes res) throws Exception
    {
        String eId = req.geteId();
        String tareId = req.getRequest().getTareId();
        String tareName = req.getRequest().getTareName();
        BigDecimal tare = req.getRequest().getTare();
        BigDecimal price = req.getRequest().getPrice();
        String restrictShop = req.getRequest().getRestrictShop();
        String status = req.getRequest().getStatus();
        String unitType = req.getRequest().getUnitType();
        List<DCP_TareUpdateReq.levelShop> shopList = req.getRequest().getShopList();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        UptBean ub1 = null;
        ub1 = new UptBean("dcp_tareset");

        ub1.addUpdateValue("TARENAME", new DataValue(tareName, Types.VARCHAR));
        ub1.addUpdateValue("UNITTYPE", new DataValue(unitType, Types.VARCHAR));
        ub1.addUpdateValue("TARE", new DataValue(tare, Types.VARCHAR));
        ub1.addUpdateValue("PRICE", new DataValue(price, Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
        ub1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

        // condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("TAREID", new DataValue(tareId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        //先删除，后新增
        DelBean db1 = new DelBean("dcp_tareset_shop");
        db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db1.addCondition("TAREID", new DataValue(tareId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        if (shopList != null && shopList.size()>0)
        {
            for (DCP_TareUpdateReq.levelShop levelShop : shopList)
            {
                String[] columnsName = {
                        "EID","TAREID","SHOPID","SHOPNAME"
                };
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(tareId, Types.VARCHAR),
                                new DataValue(levelShop.getShopId(), Types.VARCHAR),
                                new DataValue(levelShop.getShopName(), Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("dcp_tareset_shop", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));
            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TareUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TareUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TareUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TareUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String tareId = req.getRequest().getTareId();
        String tareName = req.getRequest().getTareName();

        if (req.getRequest().getTare()==null)
        {
            req.getRequest().setTare(new BigDecimal("0"));
        }
        if (req.getRequest().getPrice()==null)
        {
            req.getRequest().setPrice(new BigDecimal("0"));
        }

        BigDecimal tare = req.getRequest().getTare();
        BigDecimal price = req.getRequest().getPrice();
        String restrictShop = req.getRequest().getRestrictShop();
        String status = req.getRequest().getStatus();
        String unitType = req.getRequest().getUnitType();
        List<DCP_TareUpdateReq.levelShop> shopList = req.getRequest().getShopList();

        if (Check.Null(tareId))
        {
            errMsg.append("皮重商品编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(tareName))
        {
            errMsg.append("皮重商品名称不可为空值, ");
            isFail = true;
        }

        if (Check.Null(status))
        {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(unitType))
        {
            errMsg.append("计量单位不可为空值, ");
            isFail = true;
        }

        if (Check.Null(restrictShop))
        {
            errMsg.append("使用门店不可为空值, ");
            isFail = true;
        }
        else
        {
            if (restrictShop.equals("1") || restrictShop.equals("2"))
            {
                if (shopList == null || shopList.size()==0)
                {
                    errMsg.append("门店列表不能为空值 ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
    protected TypeToken<DCP_TareUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_TareUpdateReq>(){};
    }

    @Override
    protected DCP_TareUpdateRes getResponseType()
    {
        return new DCP_TareUpdateRes();
    }
}
