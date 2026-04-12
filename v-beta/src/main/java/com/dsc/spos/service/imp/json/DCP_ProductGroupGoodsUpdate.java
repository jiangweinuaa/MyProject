package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProductGroupGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_ProductGroupGoodsUpdate extends SPosAdvanceService<DCP_ProductGroupGoodsUpdateReq, DCP_ProductGroupGoodsUpdateRes> {

    @Override
    protected void processDUID(DCP_ProductGroupGoodsUpdateReq req, DCP_ProductGroupGoodsUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_ProductGroupGoodsUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        //提交的商品数组中是否存在已在其他班组下的商品，如存在，提示报错“品号XXX,XXX已存在于其它班组，不可保存”
        //
        //校验通过，更新表MES_PRODUCT_GROUP_GOODS
        List<DCP_ProductGroupGoodsUpdateReq.Datas> datas = req.getRequest().getDatas();
        if(CollUtil.isNotEmpty(datas)){
           // StringBuffer sJoinno=new StringBuffer("");
           // for (DCP_ProductGroupGoodsUpdateReq.Datas data : datas){
            //    sJoinno.append(data.getPluNo()+",");
           // }
            //Map<String, String> mapOrder=new HashMap<String, String>();
            //mapOrder.put("PLUNO", sJoinno.toString());
           // MyCommon cm=new MyCommon();
            //String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

            //if (withasSql_mono.equals(""))
            //{
             //   throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
            //}

            //String validSql="with p as ("+withasSql_mono+")" +
            //        " select * from  MES_PRODUCT_GROUP_GOODS a" +
            //        " inner join p on p.pluno=a.pluno " +
            //        " where a.eid='"+eId+"' " +
             //       " and a.pgroupno!='"+ request.getPGroupNo()+"'";
            //List<Map<String, Object>> list = this.doQueryData(validSql, null);

            //if(list.size()>0){
            ///    for (Map<String, Object> map : list){
             //       String pluno = map.get("PLUNO").toString();
             //       throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+pluno+"已存在于其它班组，不可保存");
             //   }
            //}

            DelBean db3 = new DelBean("MES_PRODUCT_GROUP_GOODS");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("PGROUPNO", new DataValue(request.getPGroupNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            for (DCP_ProductGroupGoodsUpdateReq.Datas detail : datas){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("PGROUPNO", DataValues.newString(request.getPGroupNo()));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("TRAN_TIME",  DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("MES_PRODUCT_GROUP_GOODS",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }


        }



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProductGroupGoodsUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProductGroupGoodsUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProductGroupGoodsUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupGoodsUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProductGroupGoodsUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProductGroupGoodsUpdateReq>(){};
    }

    @Override
    protected DCP_ProductGroupGoodsUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProductGroupGoodsUpdateRes();
    }

}


