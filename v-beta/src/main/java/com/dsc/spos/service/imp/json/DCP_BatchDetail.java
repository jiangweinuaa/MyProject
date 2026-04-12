package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dsc.spos.json.cust.req.DCP_BatchDetailReq;
import com.dsc.spos.json.cust.res.DCP_BatchDetailRes;
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

public class DCP_BatchDetail extends SPosBasicService<DCP_BatchDetailReq, DCP_BatchDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_BatchDetailReq req) throws Exception {
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
    protected TypeToken<DCP_BatchDetailReq> getRequestType() {
        return new TypeToken<DCP_BatchDetailReq>(){};
    }

    @Override
    protected DCP_BatchDetailRes getResponseType() {
        return new DCP_BatchDetailRes();
    }

    @Override
    protected DCP_BatchDetailRes processJson(DCP_BatchDetailReq req) throws Exception {
        DCP_BatchDetailRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        int totalRecords=0;
        int totalPages=0;
        //单头查询

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        DCP_BatchDetailReq.RequestLevel request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String supplierType = request.getSupplierType();
        String keyTxt = request.getKeyTxt();
        List<String> categoryList = request.getCategory();
        List<String> pluNoList = request.getPluNo();
        List<String> supplierIdList = request.getSupplierId();
        List<String> statusList = request.getStatus();

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
            sb.append("inner join category category on b.category=category.category ");
        }
        if(Check.NotNull(withSupplierId)){
            sb.append("inner join supplier supplier on a.supplierid=category.supplierid ");
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

        if(Check.NotNull(keyTxt)){
            sb.append(" and (a.pluno like '%%"+keyTxt+"%%'" +
                    " or a.batchno like '%%"+keyTxt+"%%' " +
                    " or a.billtype like '%%"+keyTxt+"%%'"+
                    " or a.billno like '%%"+keyTxt+"%%'" +
                    " or a.MANUFACTURER like '%%"+keyTxt+"%%' "+
                    " or c.plu_name like '%%"+keyTxt+"%%' " +
                    "" +
                    ""
                    + " ) ");
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

            //status  过滤
            list = list.stream().filter(x->{
                if(statusList.contains("NONE")){
                    if(x.get("STATUS").equals("NONE")){
                        return true;
                    }
                }else if(statusList.contains("ZERO")){
                    if(x.get("STATUS").equals("ZERO")){
                        return true;
                    }
                }else if(statusList.contains("EXPIRED")){
                    if(x.get("STATUS").equals("EXPIRED")){
                        return true;
                    }
                }else if(statusList.contains("EXPIRING")){
                    if(x.get("STATUS").equals("EXPIRING")){
                        return true;
                    }
                }else if(statusList.contains("NORMAL")){
                    if(x.get("STATUS").equals("NORMAL")){
                        return true;
                    }
                }

                return true;
            }).collect(Collectors.toList());

        }

        if(list.size()>0){
            list = list.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            //品号 批号 特征码 status REMAINSHELFLIFE REMAINSHELFLIFERATIO

            StringBuffer sJoinPluno=new StringBuffer("");
            StringBuffer sJoinBatchno=new StringBuffer("");
            StringBuffer sJoinFeatureno=new StringBuffer("");
            StringBuffer sJoinStatus=new StringBuffer("");
            StringBuffer sJoinRemainShelfLife=new StringBuffer("");
            StringBuffer sJoinRemainShelfLifeRatio=new StringBuffer("");
            StringBuffer sJoinQty=new StringBuffer("");
            for (Map<String, Object> oneData : list){
                sJoinPluno.append(oneData.get("PLUNO").toString()+",");
                sJoinBatchno.append(oneData.get("BATCHNO").toString()+",");
                sJoinFeatureno.append(oneData.get("FEATURENO").toString()+",");
                sJoinStatus.append(oneData.get("STATUS").toString()+",");
                sJoinRemainShelfLife.append(oneData.get("REMAINSHELFLIFE").toString()+",");
                sJoinRemainShelfLifeRatio.append(oneData.get("REMAINSHELFLIFERATIO").toString()+",");
                sJoinQty.append(oneData.get("QTY").toString()+",");
            }
            Map<String, String> mapBatch=new HashMap<String, String>();
            mapBatch.put("PLUNO", sJoinPluno.toString());
            mapBatch.put("BATCHNO", sJoinBatchno.toString());
            mapBatch.put("FEATURENO", sJoinFeatureno.toString());
            mapBatch.put("STATUS", sJoinStatus.toString());
            mapBatch.put("REMAINSHELFLIFE", sJoinRemainShelfLife.toString());
            mapBatch.put("REMAINSHELFLIFERATIO", sJoinRemainShelfLifeRatio.toString());
            mapBatch.put("QTY", sJoinQty.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapBatch);

            StringBuffer sqlbuf=new StringBuffer();

            sqlbuf.append(" with b as ("+withasSql_mono+") "
                    + " select * from ("
                    + " select count(*) over () num,row_number() over (order by a.batchno desc) as rn,"
                    + " a.*,b.status,b.REMAINSHELFLIFE,b.REMAINSHELFLIFERATIO,c.plu_name as pluname ,case when a.supplierType='1' then e.sname when a.supplierType='2' then f.org_name else N'' end as suppliername," +
                    "   g.FEATURENAME,d.spec,h.category_name as categoryname,d.wunit,i.uname as wunitname,to_char(a.productdate,'yyyyMMdd') as productDates,to_char(a.losedate,'yyyyMMdd') as losedates," +
                    "   d.isShelfLifeCheck,d.shelflife,d.BATCHRULES,b.qty,d.category "
                    + " from mes_batch a"
                    + " inner join  b on a.pluno=b.pluno and a.batchno=b.batchno and a.featureno=b.featureno  " +
                    " inner join dcp_goods_lang c on a.eid =c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " inner join dcp_goods d on d.eid=a.eid and d.pluno=a.pluno" +
                    " left join dcp_bizpartner e on e.eid=a.eid and e.bizpartnerno=a.supplierid " +
                    " left join dcp_org_lang  f on f.eid=a.eid and f.organizationno=a.supplierid and f.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods_feature_lang g on g.eid=a.eid and g.pluno=a.pluno and g.featureno=a.featureno and g.lang_type='"+req.getLangType()+"' " +
                    " left join DCP_CATEGORY_LANG h on h.eid=a.eid and h.category=d.category and h.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang i on i.eid=a.eid and i.unit=d.wunit and i.lang_type='"+req.getLangType()+"'"
                    + " where a.eid='"+req.geteId()+"' "
                    + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                    + " ");

            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);

            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> row : getQData){
                    DCP_BatchDetailRes.DatasLevel datasLevel = res.new DatasLevel();
                    datasLevel.setPluNo(row.get("PLUNO").toString());
                    datasLevel.setPluName(row.get("PLUNAME").toString());
                    datasLevel.setFeatureNo(row.get("FEATURENO").toString());
                    datasLevel.setFeatureName(row.get("FEATURENAME").toString());
                    datasLevel.setSpec(row.get("SPEC").toString());
                    datasLevel.setCategory(row.get("CATEGORY").toString());
                    datasLevel.setCategoryName(row.get("CATEGORYNAME").toString());
                    datasLevel.setWUnit(row.get("WUNIT").toString());
                    datasLevel.setWUnitName(row.get("WUNITNAME").toString());
                    datasLevel.setBatchNo(row.get("BATCHNO").toString());
                    datasLevel.setProductDate(row.get("PRODUCTDATES").toString());
                    datasLevel.setLoseDate(row.get("LOSEDATES").toString());
                    datasLevel.setSupplierType(row.get("SUPPLIERTYPE").toString());
                    datasLevel.setSupplierId(row.get("SUPPLIERID").toString());
                    datasLevel.setSupplierName(row.get("SUPPLIERNAME").toString());
                    datasLevel.setBillType(row.get("BILLTYPE").toString());
                    datasLevel.setBillNo(row.get("BILLNO").toString());
                    datasLevel.setProduceArea(row.get("PRODUCEAREA").toString());
                    datasLevel.setManufacturer(row.get("MANUFACTURER").toString());
                    datasLevel.setIsShelfLifeCheck(row.get("ISSHELFLIFECHECK").toString());
                    datasLevel.setShelfLife(row.get("SHELFLIFE").toString());
                    datasLevel.setBatchRules(row.get("BATCHRULES").toString());
                    datasLevel.setTotStockQty(row.get("QTY").toString());
                    datasLevel.setStatus(row.get("STATUS").toString());
                    datasLevel.setRemainShelfLife(row.get("REMAINSHELFLIFE").toString());
                    datasLevel.setRemainShelfLifeRatio(row.get("REMAINSHELFLIFERATIO").toString());
                    datasLevel.setCreateOpId(row.get("CREATEOPID").toString());
                    datasLevel.setCreateOpName(row.get("CREATEOPNAME").toString());
                    datasLevel.setCreateTime(row.get("CREATETIME").toString());
                    datasLevel.setLastModiOpId(row.get("LASTMODIOPID").toString());
                    datasLevel.setLastModiName(row.get("LASTMODIOPNAME").toString());
                    datasLevel.setLastModiTime(row.get("LASTMODITIME").toString());
                    res.getDatas().add(datasLevel);
                }

            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

        }else{
            return res;
        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_BatchDetailReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;


        return sqlbuf.toString();
    }
}


