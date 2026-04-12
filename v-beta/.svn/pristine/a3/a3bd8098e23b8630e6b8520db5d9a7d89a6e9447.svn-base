package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dsc.spos.json.cust.req.DCP_BatchStatisticsReq;
import com.dsc.spos.json.cust.res.DCP_BatchStatisticsRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_BatchStatistics extends SPosBasicService<DCP_BatchStatisticsReq, DCP_BatchStatisticsRes> {

    @Override
    protected boolean isVerifyFail(DCP_BatchStatisticsReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_BatchStatisticsReq> getRequestType() {
        return new TypeToken<DCP_BatchStatisticsReq>(){};
    }

    @Override
    protected DCP_BatchStatisticsRes getResponseType() {
        return new DCP_BatchStatisticsRes();
    }

    @Override
    protected DCP_BatchStatisticsRes processJson(DCP_BatchStatisticsReq req) throws Exception {
        DCP_BatchStatisticsRes res = this.getResponse();
        res.setDatas(new ArrayList<>());


        DCP_BatchStatisticsReq.RequestLevel request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String supplierType = request.getSupplierType();
        List<String> categoryList = request.getCategory();
        List<String> pluNoList = request.getPluNo();
        List<String> supplierIdList = request.getSupplierId();

        String createDateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        MyCommon mc = new MyCommon();
        String withPlu = "";
        if (pluNoList !=null && pluNoList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinPlu = "";
            for(String s :pluNoList) {
                sJoinPlu += s +",";
            }
            map.put("PLUNO", sJoinPlu);
            withPlu = mc.getFormatSourceMultiColWith(map);
        }

        String withCateGory = "";
        if (categoryList !=null && categoryList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinP = "";
            for(String s :categoryList) {
                sJoinP += s +",";
            }
            map.put("CATEGORY", sJoinP);
            withPlu = mc.getFormatSourceMultiColWith(map);
        }

        String withSupplierId = "";
        if (supplierIdList !=null && supplierIdList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinP = "";
            for(String s :supplierIdList) {
                sJoinP += s +",";
            }
            map.put("SUPPLIERID", sJoinP);
            withSupplierId = mc.getFormatSourceMultiColWith(map);
        }

        StringBuffer sb=new StringBuffer("");
        if(Check.NotNull(withPlu)){
            sb.append(" with plu a ("+withPlu+") ");
        }
        if(Check.NotNull(withCateGory)){
            sb.append(" with category as ("+withCateGory+") ");
        }
        if(Check.NotNull(withSupplierId)){
            sb.append(" with supplier as ("+withSupplierId+") ");
        }

        sb.append("select a.pluno,a.featureno,a.batchno,d.location,sum(nvl(d.qty,0)) as qty,b.shelflife,to_char(a.productdate,'yyyyMMdd') as productdates,to_char(a.losedate,'yyyyMMdd') as losedates " +
                " from mes_batch a" +
                " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " inner join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join MES_BATCH_STOCK_DETAIL d on d.eid=a.eid  and d.batchno=a.batchno and d.pluno=a.pluno and d.featureno=a.featureno  ");
        if(Check.NotNull(withPlu)){
            sb.append("inner join plu plu  on a.pluno=plu.pluno ");
        }
        if(Check.NotNull(withCateGory)){
            sb.append("inner join category category  on b.category=category.category ");
        }
        if(Check.NotNull(withSupplierId)){
            sb.append("inner join supplier supplier  on a.supplierid=supplier.supplierid ");
        }
        sb.append(" where a.eid='"+req.geteId()+"' " +
                " ");
        if(Check.NotNull(supplierType)){
            sb.append(" and a.supplierType='"+supplierType+"' ");
        }

        if("productDate".equals(dateType)){
            if(Check.NotNull(beginDate)){
                sb.append(" and to_char(a.productdate,'yyyyMMdd') <='"+beginDate+"' ");
            }
            if(Check.NotNull(endDate)){
                sb.append(" and to_char(a.productdate,'yyyyMMdd') >='"+endDate+"' ");
            }
        }
        else if("loseDate".equals(dateType)){
            if(Check.NotNull(beginDate)){
                sb.append(" and to_char(a.loseDate,'yyyyMMdd') <='"+beginDate+"' ");
            }
            if(Check.NotNull(endDate)){
                sb.append(" and to_char(a.loseDate,'yyyyMMdd') >='"+endDate+"' ");
            }
        }

        sb.append( "GROUP BY " +
                "   a.pluno, a.featureno, a.batchno, " +
                "   b.shelflife, a.productdate, a.losedate,d.location");
        List<Map<String, Object>> list = this.doQueryData(sb.toString(), null);

        if(CollUtil.isNotEmpty(list)){
            list.forEach(x->{
                String pqty = x.get("QTY").toString();
                String shelflife = x.get("SHELFLIFE").toString();
                String productDates = x.get("PRODUCTDATES").toString();
                String losedates = x.get("LOSEDATES").toString();
                String location = x.get("LOCATION").toString();

                x.put("REMAINSHELFLIFE","");
                x.put("REMAINSHELFLIFERATIO","");
                if(Check.Null(location)){
                    x.put("STATUS","NONE");
                }else{
                    if(new BigDecimal(pqty).compareTo(BigDecimal.ZERO)<=0){
                        x.put("STATUS","ZERO");
                    }
                    else{
                        //EXPIRED过期：有效日期-系统日期<=0;
                        //EXPIRING临期：
                        //● 保质期天数<3天，剩余时数<=24小时；（有效日期按23:59:59计算）
                        //● 保质期天数>3天，临期预警值：剩余效期百分比<=30%；（1/3规则）
                        //剩余天数百分比=（有效日期-系统日期）/保质期天数
                        //NORMAL正常：剩余天数百分比>30%

                        //没有有效日期
                        if(Check.Null(losedates)){
                            x.put("STATUS","NORMAL");
                            //x.put("REMAINSHELFLIFE","");
                            //x.put("REMAINSHELFLIFERATIO","");
                        }else{
                            int i = Integer.parseInt(createDate)-Integer.parseInt(losedates);
                            if(i>=0){
                                x.put("STATUS","EXPIRED");
                                x.put("REMAINSHELFLIFE","0");
                                x.put("REMAINSHELFLIFERATIO","0");
                            }else{

                                //losedates 减去 createDate
                                long diff = DateUtil.between(DateUtil.parse(losedates), DateUtil.parse(createDate), DateUnit.DAY);
                                double percent = diff/Double.parseDouble(shelflife);
                                percent = new BigDecimal(percent).setScale(4, RoundingMode.HALF_UP).doubleValue();
                                if(diff<3){
                                    x.put("STATUS","EXPIRING");
                                }else{
                                    //diff 除 shelflife
                                    if(percent<=0.3){
                                        x.put("STATUS","EXPIRING");
                                    }else{
                                        x.put("STATUS","NORMAL");
                                    }
                                }

                                x.put("REMAINSHELFLIFE",diff);
                                x.put("REMAINSHELFLIFERATIO",percent*100);

                                //EXPIRING临期：
                                //● 保质期天数<3天，剩余时数<=24小时；（有效日期按23:59:59计算）
                                //● 保质期天数>3天，临期预警值：剩余效期百分比<=30%；（1/3规则）
                                //剩余天数百分比=（有效日期-系统日期）/保质期天数
                                //NORMAL正常：剩余天数百分比>30%
                            }
                        }
                    }
                }

                //库存数>0:

                //
                //库存数=0/无库存:
                //ZERO0库存：库存数=0
                //NONE无库存：无库存记录（即在MES_BATCH_STOCK_DETAIL查不到库存）


            });

        }

        if(list.size()>0){
            List<String> statusList = list.stream().map(x -> x.get("STATUS").toString()).distinct().collect(Collectors.toList());
            for (String status : statusList){
                DCP_BatchStatisticsRes.DatasLevel datasLevel = res.new DatasLevel();
                datasLevel.setStatus( status);

                int filterRows = list.stream().filter(x -> x.get("STATUS").toString().equals(status)).collect(Collectors.toList()).size();

                datasLevel.setBatchCount(String.valueOf(filterRows));
                res.getDatas().add(datasLevel);
            }

        }else{
            return res;
        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_BatchStatisticsReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();


        return sqlbuf.toString();
    }
}


