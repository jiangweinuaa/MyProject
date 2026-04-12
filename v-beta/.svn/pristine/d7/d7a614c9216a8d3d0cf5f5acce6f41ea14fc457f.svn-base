package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TareCreateReq;
import com.dsc.spos.json.cust.res.DCP_TareCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_TareCreate extends SPosAdvanceService<DCP_TareCreateReq, DCP_TareCreateRes>
{

    @Override
    protected void processDUID(DCP_TareCreateReq req, DCP_TareCreateRes res) throws Exception
    {
        String eId = req.geteId();
        String tareId = req.getRequest().getTareId();
        String tareName = req.getRequest().getTareName();
        BigDecimal tare = req.getRequest().getTare();
        BigDecimal price = req.getRequest().getPrice();
        String restrictShop = req.getRequest().getRestrictShop();
        String status = req.getRequest().getStatus();
        String unitType = req.getRequest().getUnitType();
        List<DCP_TareCreateReq.levelShop> shopList = req.getRequest().getShopList();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql = "select * from DCP_TARESET where EID = '"+eId+"' and TAREID = '"+tareId+"'";
        List<Map<String, Object>> tareDatas = this.doQueryData(sql, null);
        if(tareDatas.isEmpty())
        {
            String[] columns1 = { "EID", "TAREID", "TARENAME", "UNITTYPE","TARE", "PRICE","STATUS","RESTRICTSHOP","CREATEOPID","CREATEOPNAME","CREATETIME" };
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[] {
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(tareId, Types.VARCHAR),
                    new DataValue(tareName, Types.VARCHAR),
                    new DataValue(unitType, Types.VARCHAR),
                    new DataValue(tare, Types.DECIMAL),
                    new DataValue(price, Types.DECIMAL),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(restrictShop, Types.VARCHAR),
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getOpName(), Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_TARESET", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            //先删除，后新增
            DelBean db1 = new DelBean("dcp_tareset_shop");
            db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            db1.addCondition("TAREID", new DataValue(tareId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            if (shopList != null && shopList.size()>0)
            {
                for (DCP_TareCreateReq.levelShop levelShop : shopList)
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
        else
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 皮重商品编码："+tareId +"已存在 ");
            return;
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TareCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TareCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TareCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TareCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (req.getRequest().getTare()==null)
        {
            req.getRequest().setTare(new BigDecimal("0"));
        }
        if (req.getRequest().getPrice()==null)
        {
            req.getRequest().setPrice(new BigDecimal("0"));
        }

        String tareId = req.getRequest().getTareId();
        String tareName = req.getRequest().getTareName();
        BigDecimal tare = req.getRequest().getTare();
        BigDecimal price = req.getRequest().getPrice();
        String restrictShop = req.getRequest().getRestrictShop();
        String status = req.getRequest().getStatus();
        String unitType = req.getRequest().getUnitType();
        List<DCP_TareCreateReq.levelShop> shopList = req.getRequest().getShopList();

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
    protected TypeToken<DCP_TareCreateReq> getRequestType()
    {
        return new TypeToken<DCP_TareCreateReq>(){};
    }

    @Override
    protected DCP_TareCreateRes getResponseType()
    {
        return new DCP_TareCreateRes();
    }
}
