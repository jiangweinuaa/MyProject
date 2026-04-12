package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DinnerMealUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DinnerUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerMealUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_DinnerMealUpdate extends SPosAdvanceService<DCP_DinnerMealUpdateReq, DCP_DinnerMealUpdateRes>
{

    @Override
    protected void processDUID(DCP_DinnerMealUpdateReq req, DCP_DinnerMealUpdateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String eId=req.geteId();

        List<String> dinnerNos = req.getRequest().getDinnerNo();

        List<DCP_DinnerMealUpdateReq.levelElmMeal> mealList=req.getRequest().getMealList();


        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearTableBaseInfoCache(posUrl, apiUserCode, apiUserKey,eId,req.getShopId());

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String mySysTime = df.format(cal.getTime());

        for (String dinnerNo : dinnerNos)
        {
            //DCP_DINNERTABLE_MEAL
            DelBean db1_meal = new DelBean("DCP_DINNERTABLE_MEAL");
            db1_meal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1_meal.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
            db1_meal.addCondition("DINNERNO", new DataValue(dinnerNo, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1_meal)); //

            if (mealList != null)
            {
                String[] columnsMeal =
                        {
                                "EID",
                                "ORGANIZATIONNO",
                                "DINNERNO",
                                "PLUNO",
                                "UNITID",
                                "QTY",
                                "DEFMODE",
                                "LASTMODIOPID",
                                "LADTMODITIME"
                        };
                for (DCP_DinnerMealUpdateReq.levelElmMeal Meal : mealList)
                {
                    DataValue[] insMealValue1 = new DataValue[]
                            {
                                    new DataValue(req.geteId(), Types.VARCHAR),
                                    new DataValue(req.getShopId(), Types.VARCHAR),
                                    new DataValue(dinnerNo, Types.VARCHAR),
                                    new DataValue(Meal.getPluNo(), Types.VARCHAR),
                                    new DataValue(Meal.getUnitId(), Types.VARCHAR),
                                    new DataValue(Meal.getQty(), Types.VARCHAR),
                                    new DataValue(Meal.getDefMode(), Types.VARCHAR),
                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE),
                            };

                    InsBean ib1_meal = new InsBean("DCP_DINNERTABLE_MEAL", columnsMeal);
                    ib1_meal.addValues(insMealValue1);
                    this.addProcessData(new DataProcessBean(ib1_meal)); // 新增單頭
                }
            }


        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DinnerMealUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DinnerMealUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DinnerMealUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DinnerMealUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        List<String> dinnerNos = req.getRequest().getDinnerNo();

        if (CollectionUtils.isEmpty(dinnerNos))
        {
            errMsg.append("桌台号不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DinnerMealUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_DinnerMealUpdateReq>(){};
    }

    @Override
    protected DCP_DinnerMealUpdateRes getResponseType()
    {
        return new DCP_DinnerMealUpdateRes();
    }


}
