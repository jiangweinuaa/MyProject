package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyCreateReq;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyCreateRes;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReturnApplyUpdate extends SPosAdvanceService<DCP_ReturnApplyUpdateReq, DCP_ReturnApplyUpdateRes> {

    @Override
    protected void processDUID(DCP_ReturnApplyUpdateReq req, DCP_ReturnApplyUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ReturnApplyUpdateReq.level1Elm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String billNo = request.getBillNo();

        DelBean db2 = new DelBean("DCP_RETURNAPPLY_DETAIL");
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_RETURNAPPLY_IMAGE");
        db3.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db3.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
        db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));



        BigDecimal totCqty=new BigDecimal(0);
        BigDecimal totPqty=new BigDecimal(0);
        BigDecimal totAmt=new BigDecimal(0);
        BigDecimal totDistriAmt=new BigDecimal(0);



        List<DCP_ReturnApplyUpdateReq.Detail> datas = request.getDetail();
        //校验
        if(CollUtil.isEmpty(datas)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单身明细不可为空！".toString());
        }

        //2.检查传入商品明细是否存在当前组织所在【商品模板】中，不存在则返回报错提示具体商品在当前组织搜索无结果！
        String tempSql=" select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID,c.CANREQUIREBACK FROM DCP_GOODSTEMPLATE a " +
                " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getOrganizationNO()+"' AND a.STATUS = 100 " +
                " AND b.RANGETYPE = 2  " +
                " order by a.TEMPLATEID desc " +
                " ) " +
                " union all" +
                " select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID,c.CANREQUIREBACK FROM DCP_GOODSTEMPLATE a " +
                " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getBELFIRM()+"' AND a.STATUS = 100 " +
                " AND b.RANGETYPE = 1  " +
                " order by a.TEMPLATEID desc " +
                " ) " +
                "";
        List<Map<String, Object>> tempList = this.doQueryData(tempSql, null);

        //采购模板
        String pTempSql="select a.PURTEMPLATENO,b.pluno,a.PURTYPE,a.supplierno,a.DISTRIORGNO DELIVERORGNO,a.PURCENTER from " +
                " DCP_PURCHASETEMPLATE a" +
                " inner join DCP_PURCHASETEMPLATE_goods b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                " inner join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and a.purtemplateno=c.purtemplateno " +
                " where a.eid='"+eId+"' and c.organizationno='"+organizationNO+"' and a.status='100'" +
                " and b.status='100' and c.status='100'";
        List<Map<String, Object>> purchaseTempList = this.doQueryData(pTempSql, null);

        //要货模板
        String ptemplateSql="" +
                " " +
                " select a.ptemplateno,a.suppliertype,a.receipt_org,b.pluno from DCP_PTEMPLATE a " +
                " inner join DCP_PTEMPLATE_DETAIL b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                " left join DCP_PTEMPLATE_SHOP c on a.eid=c.eid and a.ptemplateno=c.ptemplateno " +
                " where a.doc_type='0' and c.shopid='"+req.getOrganizationNO()+"' " +
                " union all(  " +
                " select a.ptemplateno,a.suppliertype,a.receipt_org,b.pluno from DCP_PTEMPLATE a " +
                " inner join DCP_PTEMPLATE_DETAIL b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                " where a.doc_type='0' and a.shoptype='1' )" +
                "" +
                "";
        List<Map<String, Object>> pTemplateList = this.doQueryData(ptemplateSql, null);


        request.getDetail().forEach(data->{

            if(Check.Null(data.getSupplierType())&&Check.Null(data.getSupplierId())){

                String pluNo = data.getPluNo();
                //supplierType  supplierId 重新给值
                List<Map<String, Object>> ptFilterRows = pTemplateList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                if(ptFilterRows.size()==1){
                    data.setSupplierType(ptFilterRows.get(0).get("SUPPLIERTYPE").toString());
                    data.setSupplierId(ptFilterRows.get(0).get("RECEIPT_ORG").toString());
                }else if(ptFilterRows.size()>1){
                    List<Map> collect = ptFilterRows.stream().map(x -> {
                        Map map = new HashMap();
                        map.put("SUPPLIERTYPE", x.get("SUPPLIERTYPE").toString());
                        map.put("RECEIPT_ORG", x.get("RECEIPT_ORG").toString());
                        return map;
                    }).distinct().collect(Collectors.toList());
                    if(collect.size()==1){
                        data.setSupplierType(collect.get(0).get("SUPPLIERTYPE").toString());
                        data.setSupplierId(collect.get(0).get("RECEIPT_ORG").toString());
                    }

                }else{
                    List<Map<String, Object>> filterRows = tempList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(filterRows)){
                        data.setSupplierType(filterRows.get(0).get("SUPPLIERTYPE").toString());
                        data.setSupplierId(filterRows.get(0).get("SUPPLIERID").toString());

                        if(data.getSupplierId().equals(req.getOrganizationNO())){
                            data.setSupplierType("SUPPLIER");
                        }

                    }
                }


            }

            //if("SUPPLIER".equals(data.getSupplierType())){
            //    List<Map<String, Object>> pTemplates = purchaseTempList.stream().filter(x -> x.get("PLUNO").toString().equals(data.getPluNo()) && x.get("SUPPLIERNO").toString().equals(data.getSupplierId())).collect(Collectors.toList());
            //    if(pTemplates.size()>0){
            //        data.set
            //    }
            //}

            //取数源：要货模板（指定组织>全部组织） > 商品模板（机构模板>公司模板）
            //1️⃣要货模板：
            //DCP_PTEMPLATE
            //DCP_PTEMPLATE_DETAIL
            //DCP_PTEMPLATE_SHOP
            //SUPPLIERTYPE=DCP_PTEMPLATE.SUPPLIERTYPE(供货方式）
            //SUPPLIERID=DCP_PTEMPLATE.RECEIPT_ORG(发货组织）--统配：发货组织/采购：采购中心
            //（备注：如果找到多个要货模板的话，先看供货方式+发货组织是否唯一，如果唯一可以直接取；如果出现多组，就取商品模板）
            //
            //2️⃣商品模板：（商品存在多个商品模板取日期最大的一个）
            //DCP_GOODSTEMPLATE
            //DCP_GOODSTEMPLATE_GOODS
            //DCP_GOODSTEMPLATE_RANGE
            //
            //如果供货对象SUPPLIERID=申请组织自己本身，则供货类型固定=“SUPPLIER”（采购），供货对象=主供应商



        });

        for (DCP_ReturnApplyUpdateReq.Detail data : datas){
            String pluNo = data.getPluNo();
            List<Map<String, Object>> filterRows = tempList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
            if(CollUtil.isEmpty(filterRows)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "商品【"+pluNo+"】在当前组织商品搜索无结果！".toString());
            }

            Map<String, Object> singleMap = filterRows.get(0);
            String supplierType =data.getSupplierType();// singleMap.get("SUPPLIERTYPE").toString();//前端给
            String supplierId = data.getSupplierId();//singleMap.get("SUPPLIERID").toString();
            String canRequireBack = singleMap.get("CANREQUIREBACK").toString();

            //1-若supplierType="FACTORY"(统配），则代表退货流程走退仓，继续判断所在模板设置字段【可退仓-CANREQUIREBACK】，若为N则返回报错提示商品不可退仓；
            //2-若supplierType="SUPPLIER"(采购），则代表退货流程走退供；根据申请组织编号+商品+供货对象supplierId从采购模板取出对应采购类型purType，分以下情况判断：
            //● 采购类型为空，代表无有效采购模板，返回报错提示该商品对应供货对象未找到有效的采购模板，请检查！
            //● 采购类型=2.统采越库，需判断所在模板设置字段【可退仓-CANREQUIREBACK】，若为N，则返回报错提示商品不可退仓；
            //● 采购类型<>2.统采越库，无需检查退仓条件！

            if("FACTORY".equals(supplierType)){
                if(!"Y".equals(canRequireBack)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "商品【"+pluNo+"】不可退仓！".toString());
                }
            }
            if("SUPPLIER".equals(supplierType)){
                List<Map<String, Object>> pTemplates = purchaseTempList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) ).collect(Collectors.toList());
                if(Check.NotNull(supplierId)){
                    pTemplates= pTemplates.stream().filter(x-> x.get("SUPPLIERNO").toString().equals(supplierId)).collect(Collectors.toList());
                }
                if(CollUtil.isEmpty(pTemplates)) {
                    pTemplates = purchaseTempList.stream().filter(x -> x.get("PURCENTER").toString().equals(supplierId) && x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                    if (pTemplates.size() <= 0) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "商品【" + pluNo + "】对应供货对象未找到有效的采购模板，请检查！".toString());
                    }
                }
                String purType = pTemplates.get(0).get("PURTYPE").toString();
                if(purType.equals("2")){
                    if(!"Y".equals(canRequireBack)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "商品【"+pluNo+"】不可退仓！".toString());
                    }
                }

            }

        }


        int dataItems=0;
        for (DCP_ReturnApplyUpdateReq.Detail data : datas){

            if(Check.Null(data.getPluBarcode())){
                data.setPluBarcode(" ");
            }
            List<Map<String, Object>> pTemplates = purchaseTempList.stream().filter(x -> x.get("PLUNO").toString().equals(data.getPluNo()) && x.get("SUPPLIERNO").toString().equals(data.getSupplierId())).collect(Collectors.toList());

            if(pTemplates.size()<=0){
                pTemplates = purchaseTempList.stream().filter(x -> x.get("PLUNO").toString().equals(data.getPluNo()) && x.get("PURCENTER").toString().equals(data.getSupplierId())).collect(Collectors.toList());

            }

            String purType = "";
            String deliverOrgNo = "";

            if(pTemplates.size()>0){
                purType = pTemplates.get(0).get("PURTYPE").toString();
                deliverOrgNo = pTemplates.get(0).get("DELIVERORGNO").toString();
            }

            String approveOrgNo="";
            String returnType="";
            String receiptOrgNo="";
            if("FACTORY".equals(data.getSupplierType())){
                approveOrgNo=data.getSupplierId();
                returnType="1";
                receiptOrgNo=data.getSupplierId();
            }else  if("SUPPLIER".equals(data.getSupplierType())){
                if(CollUtil.isNotEmpty(pTemplates)){
                    approveOrgNo=pTemplates.get(0).get("PURCENTER").toString();
                }
                if("0".equals(purType)||"1".equals(purType)){
                    returnType="2";
                    receiptOrgNo=deliverOrgNo;
                }
                if("2".equals(purType)){
                    returnType="1";
                }
            }


            Map<String, Object> baseMap = PosPub.getBaseQty(dao, eId, data.getPluNo(), data.getPUnit(), data.getPQty());
            String baseUnit = baseMap.get("baseUnit").toString();
            BigDecimal baseQty = new BigDecimal(Check.Null(baseMap.get("baseQty").toString())?"0":baseMap.get("baseQty").toString());

            if(Check.Null(data.getUnitRatio())){
                data.setUnitRatio(baseMap.get("unitRatio").toString());
            }

            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("BILLNO", DataValues.newString(billNo));

            detailColumns.add("ITEM", DataValues.newString(data.getItem()));
            detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
            detailColumns.add("PLUBARCODE", DataValues.newString(data.getPluBarcode()));
            detailColumns.add("FEATURENO", DataValues.newString(data.getFeatureNo()));
            detailColumns.add("BATCHNO", DataValues.newString(data.getBatchNo()));
            detailColumns.add("PRODDATE", DataValues.newString(data.getProdDate()));
            detailColumns.add("EXPDATE", DataValues.newString(data.getExpDate()));
            detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
            detailColumns.add("POQTY", DataValues.newString(data.getPQty()));
            detailColumns.add("BASEUNIT", DataValues.newString(baseUnit));
            detailColumns.add("BASEQTY", DataValues.newString(baseQty.toString()));
            detailColumns.add("PRICE", DataValues.newString(data.getPrice()));
            detailColumns.add("AMT", DataValues.newString(data.getAmt()));
            detailColumns.add("DISTRIPRICE", DataValues.newString(data.getDistriPrice()));
            detailColumns.add("DISTRIAMT", DataValues.newString(data.getDistriAmt()));
            detailColumns.add("SUPPLIERTYPE", DataValues.newString(data.getSupplierType()));
            detailColumns.add("SUPPLIERID", DataValues.newString(data.getSupplierId()));
            detailColumns.add("PURTYPE", DataValues.newString(purType));
            detailColumns.add("APPROVESTATUS", DataValues.newString("0"));
            detailColumns.add("APPROVEORGNO", DataValues.newString(approveOrgNo));
            detailColumns.add("RETURNTYPE", DataValues.newString(returnType));
            detailColumns.add("RECEIPTORGNO", DataValues.newString(receiptOrgNo));
            detailColumns.add("BSNO", DataValues.newString(data.getBsNo()));
            detailColumns.add("MEMO", DataValues.newString(data.getMemo()));
            detailColumns.add("UNITRATIO", DataValues.newString(data.getUnitRatio()));

            totAmt=totAmt.add(new BigDecimal(data.getAmt()));
            totPqty=totPqty.add(new BigDecimal(data.getPQty()));
            totDistriAmt=totDistriAmt.add(new BigDecimal(data.getDistriAmt()));

            String[] detailColumnNames =detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibDetail=new InsBean("DCP_RETURNAPPLY_DETAIL",detailColumnNames);
            ibDetail.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ibDetail));

            List<DCP_ReturnApplyUpdateReq.ImageList> imageList = data.getImageList();
            int imageItem=0;
            for (DCP_ReturnApplyUpdateReq.ImageList image : imageList){
                imageItem++;
                ColumnDataValue imageColumns=new ColumnDataValue();
                imageColumns.add("EID", DataValues.newString(eId));
                imageColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                imageColumns.add("BILLNO", DataValues.newString(billNo));
                imageColumns.add("IMAGE", DataValues.newString(image.getImage()));
                imageColumns.add("OITEM", DataValues.newString(data.getItem()));
                imageColumns.add("ITEM", DataValues.newString(imageItem));

                String[] imageColumnNames =imageColumns.getColumns().toArray(new String[0]);
                DataValue[] imageDataValues = imageColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ibImage=new InsBean("DCP_RETURNAPPLY_IMAGE",imageColumnNames);
                ibImage.addValues(imageDataValues);
                this.addProcessData(new DataProcessBean(ibImage));
            }

        }

        List collect = datas.stream().map(x -> {
            Map hashMap = new HashMap<>();
            hashMap.put("pluNo", x.getPluNo());
            hashMap.put("featureNo", x.getFeatureNo());
            return hashMap;
        }).distinct().collect(Collectors.toList());
        totCqty=new BigDecimal(collect.size());

        UptBean ub1 = new UptBean("DCP_RETURNAPPLY");
        ub1.addUpdateValue("BDATE", DataValues.newString(request.getBdate()));
        ub1.addUpdateValue("BILLNO", DataValues.newString(billNo));
        ub1.addUpdateValue("TOTCQTY", DataValues.newString(totCqty));
        ub1.addUpdateValue("TOTPQTY", DataValues.newString(totPqty));
        ub1.addUpdateValue("TOTAMT", DataValues.newString(totAmt));
        ub1.addUpdateValue("TOTDISTRIAMT", DataValues.newString(totDistriAmt));
        //ub1.addUpdateValue("STATUS", DataValues.newString("0"));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));



        ub1.addCondition("EID", DataValues.newString(eId));
        ub1.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
        ub1.addCondition("BILLNO",DataValues.newString(request.getBillNo()));
        this.addProcessData(new DataProcessBean(ub1));



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReturnApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReturnApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReturnApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ReturnApplyUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ReturnApplyUpdateReq>(){};
    }

    @Override
    protected DCP_ReturnApplyUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ReturnApplyUpdateRes();
    }

}


