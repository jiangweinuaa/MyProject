package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HqDishControlUpdateReq;
import com.dsc.spos.json.cust.res.DCP_HqDishControlUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_HqDishControlUpdate extends SPosAdvanceService<DCP_HqDishControlUpdateReq, DCP_HqDishControlUpdateRes>
{

    @Override
    protected void processDUID(DCP_HqDishControlUpdateReq req, DCP_HqDishControlUpdateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        DCP_HqDishControlUpdateReq.level1Elm request = req.getRequest();
        String opId = request.getOpId();
        List<DCP_HqDishControlUpdateReq.level2Elm> itemList=request.getItemList();

        for (DCP_HqDishControlUpdateReq.level2Elm level2Elm : itemList)
        {
            String sql = "select * from DCP_HQKDSDISHES_CONTROL  where EID = '"+eId+"' and ID='"+ level2Elm.getId()+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData == null || getQData.isEmpty() )
            {

                String tips=level2Elm.getGoodsType().equals("1")?"品类:":"商品:";
                res.setSuccess(false);
                res.setServiceStatus("100");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, tips+level2Elm.getId()+"不存在！");
            }

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_HQKDSDISHES_CONTROL");
            ub1.addUpdateValue("NAME", new DataValue(level2Elm.getName(), Types.VARCHAR));
            ub1.addUpdateValue("GOODSTYPE", new DataValue(level2Elm.getGoodsType(), Types.VARCHAR));
            ub1.addUpdateValue("UNSIDE", new DataValue(level2Elm.getUnSide(), Types.VARCHAR));
            ub1.addUpdateValue("UNCOOK", new DataValue(level2Elm.getUnCook(), Types.VARCHAR));
            ub1.addUpdateValue("UNCALL", new DataValue(level2Elm.getUnCall(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(opId, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ID", new DataValue(level2Elm.getId(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HqDishControlUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HqDishControlUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HqDishControlUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HqDishControlUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_HqDishControlUpdateReq.level1Elm request = req.getRequest();
        String opId = request.getOpId();
        List<DCP_HqDishControlUpdateReq.level2Elm> itemList=request.getItemList();

        if (itemList==null || itemList.size()==0)
        {
            errMsg.append("itemList不可为空值, ");
            isFail = true;
        }
        else
        {
            for (DCP_HqDishControlUpdateReq.level2Elm level2Elm : itemList)
            {
                if (Check.Null(level2Elm.getGoodsType()))
                {
                    errMsg.append("goodsType不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getId()))
                {
                    errMsg.append("id不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getName()))
                {
                    errMsg.append("name不可为空值, ");
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
    protected TypeToken<DCP_HqDishControlUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_HqDishControlUpdateReq>(){};
    }

    @Override
    protected DCP_HqDishControlUpdateRes getResponseType()
    {
        return new DCP_HqDishControlUpdateRes();
    }


}
