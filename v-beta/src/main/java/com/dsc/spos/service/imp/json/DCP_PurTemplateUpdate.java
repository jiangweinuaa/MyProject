package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurTemplateCreateRes;
import com.dsc.spos.json.cust.res.DCP_PurTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PurTemplateUpdate extends SPosAdvanceService<DCP_PurTemplateUpdateReq, DCP_PurTemplateUpdateRes> {

    @Override
    protected void processDUID(DCP_PurTemplateUpdateReq req, DCP_PurTemplateUpdateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String eId = req.geteId();
            DCP_PurTemplateUpdateReq.levelElm request = req.getRequest();

            String no = request.getPurTemplateNo();
            String supplier = request.getSupplier();
            String purType = request.getPurType();
            String purCenter = request.getPurCenter();
            String timeType = request.getTimeType();
            String timeValue = request.getTimeValue();
            String preDays = request.getPreDays();
            String memo = request.getMemo();
            String status = request.getStatus();


            //删除明细
            DelBean db2 = new DelBean("DCP_PURCHASETEMPLATE_LANG");
            db2.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_PURCHASETEMPLATE_GOODS");
            db3.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            DelBean db4 = new DelBean("DCP_PURCHASETEMPLATE_PRICE");
            db4.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_PURCHASETEMPLATE_ORG");
            db5.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db5));

            List<DCP_PurTemplateUpdateReq.Name_lang> name_lang = request.getName_lang();
            List<DCP_PurTemplateUpdateReq.Plu> pluList = request.getPluList();
            List<DCP_PurTemplateUpdateReq.Org> orgList = request.getOrgList();


            Date dt = new Date();
            SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime =  matter.format(dt);

            if(name_lang!=null&&name_lang.size()>0){
                String[] columns_dpl ={"EID","PURTEMPLATENO","LANG_TYPE","NAME" };
                for (DCP_PurTemplateUpdateReq.Name_lang lv2 : name_lang) {
                    String langType = lv2.getLangType();
                    String name = lv2.getName();
                    DataValue[] insValue_dpl = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(no, Types.VARCHAR),
                                    new DataValue(langType, Types.VARCHAR),
                                    new DataValue(name, Types.VARCHAR)
                            };

                    InsBean ib_dpl = new InsBean("DCP_PURCHASETEMPLATE_LANG", columns_dpl);
                    ib_dpl.addValues(insValue_dpl);
                    this.addProcessData(new DataProcessBean(ib_dpl));
                }
            }
            String plunos="";
            if(pluList!=null&&pluList.size()>0){
                String[] columns_dpg ={"EID","PURTEMPLATENO","ITEM","PLUNO","TAXCODE","PURUNIT","PRICETYPE",
                        "PURBASEPRICE","MINRATE","MAXRATE","MULPQTY","MINPQTY","STATUS"};
                for (DCP_PurTemplateUpdateReq.Plu lv2 : pluList) {

                    List<DCP_PurTemplateUpdateReq.Price> priceList = lv2.getPriceList();
                    if(priceList!=null&&priceList.size()>0){
                        String[] columns_dpp ={"EID","PURTEMPLATENO","ITEM","SEQ","BQTY","EQTY","PURPRICE"};
                        for (DCP_PurTemplateUpdateReq.Price lv3 : priceList) {
                            String seq = lv3.getSeq();
                            String bQty = lv3.getBQty();
                            String eQty = lv3.getEQty();
                            String price = lv3.getPurPrice();
                            DataValue[] insValue_dpg_price = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(no, Types.VARCHAR),
                                    new DataValue(lv2.getItem(), Types.VARCHAR),
                                    new DataValue(seq, Types.VARCHAR),
                                    new DataValue(bQty, Types.VARCHAR),
                                    new DataValue(eQty, Types.VARCHAR),
                                    new DataValue(price, Types.VARCHAR),
                            };


                            InsBean ib_dpp = new InsBean("DCP_PURCHASETEMPLATE_PRICE", columns_dpp);
                            ib_dpp.addValues(insValue_dpg_price);
                            this.addProcessData(new DataProcessBean(ib_dpp));

                        }
                    }
                    plunos+="'"+lv2.getPluno()+"',";
                    DataValue[] insValue_dpg = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(no, Types.VARCHAR),
                            new DataValue(lv2.getItem(), Types.VARCHAR),
                            new DataValue(lv2.getPluno(), Types.VARCHAR),
                            new DataValue(lv2.getTaxCode(), Types.VARCHAR),
                            new DataValue(lv2.getPurUnit(), Types.VARCHAR),
                            new DataValue(lv2.getPriceType(), Types.VARCHAR),
                            new DataValue(lv2.getPurBasePrice(), Types.VARCHAR),
                            new DataValue(lv2.getMinRate(), Types.VARCHAR),
                            new DataValue(lv2.getMaxRate(), Types.VARCHAR),
                            new DataValue(lv2.getMulPqty(), Types.VARCHAR),
                            new DataValue(lv2.getMinPqty(), Types.VARCHAR),
                            new DataValue(lv2.getStatus(), Types.VARCHAR)
                    };

                    InsBean ib_dpg = new InsBean("DCP_PURCHASETEMPLATE_GOODS", columns_dpg);
                    ib_dpg.addValues(insValue_dpg);
                    this.addProcessData(new DataProcessBean(ib_dpg));
                }
            }

            if(orgList!=null&&orgList.size()>0){
                String[] columns_dpo ={"EID","PURTEMPLATENO","ITEM","ORGANIZATIONNO","STATUS" };
                for (DCP_PurTemplateUpdateReq.Org lv2 : orgList) {
                    DataValue[] insValue_dpo = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(no, Types.VARCHAR),
                                    new DataValue(lv2.getItem(), Types.VARCHAR),
                                    new DataValue(lv2.getOrgNo(), Types.VARCHAR),
                                    new DataValue(lv2.getStatus(), Types.VARCHAR)
                            };

                    InsBean ib_dpo = new InsBean("DCP_PURCHASETEMPLATE_ORG", columns_dpo);
                    ib_dpo.addValues(insValue_dpo);
                    this.addProcessData(new DataProcessBean(ib_dpo));
                }
            }

            //修改单头数据
            UptBean ub1 = new UptBean("DCP_PURCHASETEMPLATE");
            //add Value
            ub1.addUpdateValue("SUPPLIERNO", new DataValue(supplier, Types.VARCHAR));
            ub1.addUpdateValue("PURTYPE", new DataValue(purType, Types.VARCHAR));
            ub1.addUpdateValue("PURCENTER", new DataValue(purCenter, Types.VARCHAR));
            ub1.addUpdateValue("TIME_TYPE", new DataValue(timeType, Types.VARCHAR));
            ub1.addUpdateValue("TIME_VALUE", new DataValue(timeValue, Types.VARCHAR));
            ub1.addUpdateValue("PRE_DAY", new DataValue(preDays, Types.VARCHAR));
            ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();

            //同步更新商品信息DCP_GOODS【主供应商-SUPPLIER】，
            // 更新判断：新增商品仅且只有一个供应商，
            // 则根据当前供应商更新商品主数据【主供应商】；否则无需更新
            //状态：-1未启用100已启用 0已禁用
            if(plunos.length()>0){
                plunos=plunos.substring(0,plunos.length()-1);
                String pluNoSql = getPluNoSql(plunos, eId);
                List<Map<String, Object>> pluNoData=this.doQueryData(pluNoSql, null);

                for(Map<String, Object> row:pluNoData) {
                    String pluNo = row.get("PLUNO").toString();
                    String scount = row.get("SCOUNT").toString();

                    if(scount.equals("1")){
                        UptBean ubPlu = new UptBean("DCP_GOODS");
                        //add Value
                        ubPlu.addUpdateValue("SUPPLIER", new DataValue(supplier, Types.VARCHAR));

                        //condition
                        ubPlu.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubPlu.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ubPlu));
                    }
                }

                this.doExecuteDataToDB();
            }
            
        } catch (Exception e) {
            // TODO: handle exception
            res.setServiceStatus("200");
            res.setSuccess(false);
            res.setServiceDescription("服务执行失败！");
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurTemplateUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurTemplateUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurTemplateUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurTemplateUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_PurTemplateUpdateReq.levelElm request = req.getRequest();

        //必传值不为空
        String supplier = request.getSupplier();
        String purType = request.getPurType();
        String purCenter = request.getPurCenter();
        String timeType = request.getTimeType();
        String timeValue = request.getTimeValue();
        String preDays = request.getPreDays();
        String memo = request.getMemo();
        String status = request.getStatus();
        String no = request.getPurTemplateNo();


        if(Check.Null(no)){
            errMsg.append("采购模板编号不能为空值, ");
            isFail = true;
        }
        if(Check.Null(supplier)){
            errMsg.append("供应商编号不能为空值, ");
            isFail = true;
        }
        if(Check.Null(purType)){
            errMsg.append("采购方式不能为空值, ");
            isFail = true;
        }
        if(Check.Null(purCenter)){
            errMsg.append("采购中心不能为空值, ");
            isFail = true;
        }
        if(Check.Null(timeType)){
            errMsg.append("周期类型不能为空值, ");
            isFail = true;
        }
        if(Check.Null(timeValue)){
            //errMsg.append("周期值不能为空值, ");
            //isFail = true;
        }
        if(Check.Null(preDays)){
            errMsg.append("到货天数不能为空值, ");
            isFail = true;
        }
        if(Check.Null(memo)){
            //errMsg.append("备注不能为空值, ");
            //isFail = true;
        }
        if(Check.Null(status)){
            errMsg.append("状态码不能为空值, ");
            isFail = true;
        }

        List<DCP_PurTemplateUpdateReq.Name_lang> name_lang = request.getName_lang();
        List<DCP_PurTemplateUpdateReq.Plu> pluList = request.getPluList();
        List<DCP_PurTemplateUpdateReq.Org> orgList = request.getOrgList();

        if(name_lang != null&&name_lang.size()>0){
            for(DCP_PurTemplateUpdateReq.Name_lang par : name_lang){

                if (Check.Null(par.getLangType()))
                {
                    errMsg.append("语言别不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getName()))
                {
                    errMsg.append("模板说明不可为空值, ");
                    isFail = true;
                }
            }
        }else {
            errMsg.append("多语言列表不可为空值, ");
            isFail = true;
        }

        //重复性检查 商品不可重复

        if(pluList != null&&pluList.size()>0){
            List existList = new ArrayList();
            for(DCP_PurTemplateUpdateReq.Plu par : pluList){

                if (Check.Null(par.getItem()))
                {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPluno()))
                {
                    errMsg.append("商品编号不可为空值, ");
                    isFail = true;
                }else{
                    if(existList.contains(par.getPluno())){
                        //重复了
                        errMsg.append(par.getPluno()+"商品编号重复, ");
                        isFail = true;
                    }else{
                        existList.add(par.getPluno());
                    }
                }

                if (Check.Null(par.getTaxCode()))
                {
                    errMsg.append("进项税别不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurUnit()))
                {
                    errMsg.append("采购单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPriceType()))
                {
                    errMsg.append("计价方式不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurBasePrice()))
                {
                    errMsg.append("采购单位基准价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getMinRate()))
                {
                    errMsg.append("价格下调比例不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getMaxRate()))
                {
                    errMsg.append("价格上调比例不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getMulPqty()))
                {
                    errMsg.append("采购单位倍量不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(par.getMinPqty()))
                {
                    errMsg.append("最小采购量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getStatus()))
                {
                    errMsg.append("状态码不可为空值, ");
                    isFail = true;
                }

                //商品行检查字段【计价方式-priceType】若为[2-分量计价]，则价格明细pricelist[]不可为空；若为[1-基准价格]，则字段【采购单位基准价-baseprice】不可为空
                List<DCP_PurTemplateUpdateReq.Price> priceList = par.getPriceList();
                if(par.getPriceType().equals("2")&&priceList.size()<=0){
                    errMsg.append("价格明细不可为空值, ");
                    isFail = true;
                }
                if(priceList != null&&priceList.size()>0){
                    for(DCP_PurTemplateUpdateReq.Price pricePara : priceList){
                        if (Check.Null(pricePara.getSeq()))
                        {
                            errMsg.append("项序不可为空值, ");
                            isFail = true;
                        }
                        if (Check.Null(pricePara.getBQty()))
                        {
                            errMsg.append("起始数量不可为空值, ");
                        }
                        if (Check.Null(pricePara.getEQty()))
                        {
                            errMsg.append("截止数量不可为空值, ");
                        }

                        if (Check.Null(pricePara.getPurPrice()))
                        {
                            errMsg.append("价格不可为空值, ");
                        }
                    }
                }
            }
        }else{
            errMsg.append("商品明细不可为空值, ");
            isFail = true;
        }


        if(orgList != null&&orgList.size()>0){
            List existList = new ArrayList();
            for(DCP_PurTemplateUpdateReq.Org par : orgList){

                if (Check.Null(par.getItem()))
                {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getOrgNo()))
                {
                    errMsg.append("组织编号不可为空值, ");
                    isFail = true;
                }else {
                    if (existList.contains(par.getOrgNo())){
                        errMsg.append(par.getOrgNo()+"组织编号重复, ");
                        isFail = true;
                    }else {
                        existList.add(par.getOrgNo());
                    }
                }

                if (Check.Null(par.getStatus()))
                {
                    errMsg.append("状态码不可为空值, ");
                    isFail = true;
                }
            }
        }else{
            errMsg.append("收货组织不可为空值, ");
            isFail = true;
        }

        //跨模板检查：【供应商+商品+收货组织】作为唯一值检查是否存在重复数据，重复返回失败并提示具体商品对应组织已有重复类型模板！
        if(!isFail){
            List<String> plus = pluList.stream().map(plu -> plu.getPluno()).collect(Collectors.toList());
            List<String> orgs = orgList.stream().map(org -> org.getOrgNo()).collect(Collectors.toList());
            String errorMsg = checkSupplierPluOrg(supplier, plus, orgs, req.getRequest().getPurTemplateNo());
            if(errorMsg.length()>0){
                errMsg.append(errorMsg);
                isFail = true;
            }
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurTemplateUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PurTemplateUpdateReq>(){};
    }

    @Override
    protected DCP_PurTemplateUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PurTemplateUpdateRes();
    }

    public String checkSupplierPluOrg(String supplier,List<String> pluNos,List<String> orgs,String no) throws Exception{
        String sql="select b.pluno,c.ORGANIZATIONNO from DCP_PURCHASETEMPLATE a" +
                " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO "+
                " left join DCP_PURCHASETEMPLATE_ORG c on c.eid=a.eid and c.PURTEMPLATENO=a.PURTEMPLATENO "+
                " where a.SUPPLIERNO='"+supplier+"' and  a.PURTEMPLATENO!='"+no+"'";
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        String errorMsg="";
        if(getQData.size()>0){
            List<Object> explunos = getQData.stream().map(row -> row.get("PLUNO")).collect(Collectors.toList());
            List<Object> exorgNos = getQData.stream().map(row -> row.get("ORGANIZATIONNO")).collect(Collectors.toList());

            for (String pluNo :pluNos) {
                if(explunos.contains(pluNo)){
                    for (String org :orgs) {
                        if(exorgNos.contains(org)){
                            errorMsg+=supplier+"-"+pluNo+"-"+org+"已有重复类型模板;";
                        }
                    }
                }
            }
        }
        return errorMsg;
    }

    public String getPluNoSql(String pluNos,String eid) throws Exception{
        String sql="select  a.pluno,count(b.SUPPLIERNO) as SCOUNT from DCP_PURCHASETEMPLATE_GOODS a " +
                " inner join DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                " where a.pluno in ("+pluNos+") and a.eid='"+eid+"' group by a.pluno ";

        return sql;
    }

}
