package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurOrderQuery extends SPosBasicService<DCP_PurOrderQueryReq, DCP_PurOrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurOrderQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurOrderQueryReq>(){};
    }

    @Override
    protected DCP_PurOrderQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurOrderQueryRes();
    }

    @Override
    protected DCP_PurOrderQueryRes processJson(DCP_PurOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurOrderQueryRes res = this.getResponse();
            int totalRecords;		//总笔数
            int totalPages;
            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<DCP_PurOrderQueryRes.level1Elm>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //查询收货明细
                List<String> purOrderNos=new ArrayList();
                StringBuffer sJoinno=new StringBuffer("");
                for (Map<String, Object> oneData : getQData){
                    String purorderno = oneData.get("PURORDERNO").toString().toString();
                    sJoinno.append(purorderno+",");
                    if(!purOrderNos.contains(purorderno)){
                        purOrderNos.add(purorderno);
                    }
                }
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("PURORDERNO", sJoinno.toString());
                MyCommon cm=new MyCommon();
                String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

                if (withasSql_mono.equals(""))
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                }

                String detailSql="with p as ("+withasSql_mono+") "+
                        " select a.purorderno,b.item,nvl(b.purqty,0) as purqty,sum(nvl(c.RECEIVEQTY,0)) as RECEIVEQTYSUM ,sum(nvl(c.STOCKINQTY,0)) as STOCKINQTYSUM,sum(nvl(c.BOOKQTY,0)) AS BOOKQTYSUM" +
                        " from DCP_PURORDER a " +
                        " inner join p on p.purorderno=a.purorderno "+
                        " left join DCP_PURORDER_DETAIL b on a.purorderno=b.purorderno and a.eid=b.eid " +
                        " left join DCP_PURORDER_DELIVERY c on a.eid=c.eid and a.purorderno=c.purorderno and b.item=c.item " +
                        " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                        " GROUP BY a.purorderno,b.item,b.purqty"
                        ;
                List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);

                //收货状态，收货量，入库量，到货率
                //0-待收货,1-部分收货,2-收货结束
                Map orderInfoMap=new HashMap();
                for(String no : purOrderNos){
                    DCP_PurOrderQueryRes.purOrderInfo orderInfo=res.new purOrderInfo();
                    List<Map<String, Object>> purOrderList = getDetailData.stream().filter(var1 -> var1.get("PURORDERNO").toString().equals(no)).collect(Collectors.toList());
                    BigDecimal sh=new BigDecimal(0);//收货
                    BigDecimal rk=new BigDecimal(0);//入库
                    BigDecimal cg=new BigDecimal(0);//采购
                    BigDecimal bk=new BigDecimal(0);//bookqty
                    List statusList=new ArrayList();//待收货、部分收货、收货结束
                    for (Map<String, Object> oneDetail : purOrderList){
                        sh=sh.add(new BigDecimal(oneDetail.get("RECEIVEQTYSUM").toString()));
                        rk=rk.add(new BigDecimal(oneDetail.get("STOCKINQTYSUM").toString()));
                        bk=bk.add(new BigDecimal(oneDetail.get("BOOKQTYSUM").toString()));
                        cg=cg.add(new BigDecimal(oneDetail.get("PURQTY").toString()));

                        //采购收货入库  没有收货 只有入库
                        if(rk.compareTo(cg)>=0){
                            if(!statusList.contains(2)){
                                statusList.add(2);//收货结束
                            }
                        }else{
                            if(sh.compareTo(new BigDecimal(0))==0){
                                if(!statusList.contains(0)){
                                    statusList.add(0);//未收货
                                }
                            }
                            else {
                                int i = sh.compareTo(cg);
                                if(i<0){
                                    if(!statusList.contains(1)){
                                        statusList.add(1);//部分收货
                                    }
                                }
                                else {
                                    if(!statusList.contains(2)){
                                        statusList.add(2);//收货结束
                                    }
                                }
                            }
                        }

                    }

                    BigDecimal dhl =new BigDecimal("0");
                    if(cg.compareTo(new BigDecimal("0"))!=0){
                        dhl= sh.divide(cg, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                    }
                    orderInfo.setPurOrderNo(no);
                    orderInfo.setSh(sh.toString());
                    orderInfo.setRk(rk.toString());
                    orderInfo.setDhl(dhl.toString()+"%");
                    orderInfo.setBk(bk.toString());

                    if(statusList.contains(0)||statusList.contains(1)){
                        if(statusList.contains(1)){
                            //部分收货
                            orderInfo.setReceiveStatus("1");
                        }else{
                            //待收货
                            orderInfo.setReceiveStatus("0");
                        }
                    }else{
                        //收货结束
                        orderInfo.setReceiveStatus("2");
                    }

                    if(!orderInfoMap.containsKey(no)){
                        orderInfoMap.put(no,orderInfo);
                    }


                }





                res.setDatas(new ArrayList<>());
                for (Map<String, Object> oneData : getQData)
                {
                    DCP_PurOrderQueryRes.level1Elm lev1Elm = res.new level1Elm();

                    lev1Elm.setStatus(oneData.get("STATUS").toString());
                    //lev1Elm.setReceiveStatus(oneData.get("RECEIVESTATUS").toString());
                    lev1Elm.setPurOrderNo(oneData.get("PURORDERNO").toString());
                    lev1Elm.setPurOrgNo(oneData.get("ORGANIZATIONNO").toString());
                    lev1Elm.setOrgName(oneData.get("ORGNAME").toString());
                    lev1Elm.setBDate(oneData.get("BDATES").toString());
                    lev1Elm.setSupplier(oneData.get("SUPPLIER").toString());
                    lev1Elm.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                    lev1Elm.setPurType(oneData.get("PURTYPE").toString());
                    lev1Elm.setReceiveOrgno(oneData.get("RECEIPTORGNO").toString());
                    lev1Elm.setExpireDate(oneData.get("EXPIREDATES").toString());
                    lev1Elm.setPurEmpNo(oneData.get("EMPLOYEEID").toString());
                    lev1Elm.setPurDeptNo(oneData.get("DEPARTID").toString());
                    lev1Elm.setTotcQty(oneData.get("TOT_CQTY").toString());
                    lev1Elm.setTotpQty(oneData.get("TOT_PQTY").toString());
                    //lev1Elm.setTotrQty(oneData.get("TOTRQTY").toString()); 收货量合计
                    //lev1Elm.setTotsQty(oneData.get("TOSTSQTY").toString()); 入库量合计
                    //lev1Elm.setRate(oneData.get("RATE").toString()); 到货率
                    lev1Elm.setCreatorID(oneData.get("CREATEOPID").toString());
                    lev1Elm.setCreatorName(oneData.get("CREATEOPNAME").toString());
                    lev1Elm.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                    lev1Elm.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                    lev1Elm.setCreate_datetime(oneData.get("CREATETIME").toString());
                    lev1Elm.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
                    lev1Elm.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
                    lev1Elm.setLastmodify_datetime(oneData.get("LASTMODITIME").toString());
                    lev1Elm.setConfirmID(oneData.get("CONFIRMBY").toString());
                    lev1Elm.setConfirmName(oneData.get("CONFIRMNAME").toString());
                    lev1Elm.setConfirm_datetime(oneData.get("CONFIRMTIME").toString());
                    lev1Elm.setCancelBy(oneData.get("CANCELBY").toString());
                    lev1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
                    lev1Elm.setCancel_datetime(oneData.get("CANCELTIME").toString());
                    lev1Elm.setCloseBy(oneData.get("CLOSEBY").toString());
                    lev1Elm.setCloseByName(oneData.get("CLOSEBYNAME").toString());
                    lev1Elm.setCloseBy_datetime(oneData.get("CLOSETIME").toString());
                    lev1Elm.setOwnerID(oneData.get("OWNOPID").toString());
                    lev1Elm.setOwnerName(oneData.get("OWNERNAME").toString());
                    lev1Elm.setOwnerDeptID(oneData.get("OWNDEPTID").toString());
                    lev1Elm.setOwnerDeptName(oneData.get("OWNDEPTNAME").toString());
                    lev1Elm.setReceiptOrgNo(oneData.get("RECEIPTORGNO").toString());
                    lev1Elm.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
                    if(orderInfoMap.containsKey(lev1Elm.getPurOrderNo())){
                        DCP_PurOrderQueryRes.purOrderInfo o = (DCP_PurOrderQueryRes.purOrderInfo)orderInfoMap.get(lev1Elm.getPurOrderNo());
                        lev1Elm.setReceiveStatus(o.getReceiveStatus());
                        lev1Elm.setTotrQty(o.getSh());
                        lev1Elm.setTotsQty(o.getRk());
                        lev1Elm.setRate(o.getDhl());
                        lev1Elm.setTotBookQty(o.getBk());
                    }
                    lev1Elm.setReceiveOrgname(oneData.get("RECEIVEORGNAME").toString());
                    lev1Elm.setPurEmpName(oneData.get("PUREMPNAME").toString());
                    lev1Elm.setPurDeptName(oneData.get("PURDEPTNAME").toString());

                    if(lev1Elm.getStatus().equals("0")||lev1Elm.getStatus().equals("3")){
                        lev1Elm.setReceiveStatus("");
                    }

                    lev1Elm.setPayee(oneData.get("PAYEE").toString());
                    lev1Elm.setPayeeName(oneData.get("PAYEENAME").toString());
                    lev1Elm.setAddress(oneData.get("ADDRESS").toString());
                    res.getDatas().add(lev1Elm);
                }


            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurOrderQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String langType = req.getLangType();
        DCP_PurOrderQueryReq.levelElm request = req.getRequest();
        String status = request.getStatus();
        String purOrderNo = request.getPurOrderNo();
        String supplier = request.getSupplier();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String receiveStatus = request.getReceiveStatus();
        String purType = request.getPurType();
        String restrictGroupType = request.getRestrictGroupType();
        String isCheck_restrictGroup = request.getIsCheck_restrictGroup();

        List<String> queryEmployees = getValidEmployees(req, "SCM0402", "QUERY_RANGE");
        if(queryEmployees.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "您沒有權限進行此操作");
        }
        MyCommon cm=new MyCommon();
        StringBuffer sJoinEmployeeNo=new StringBuffer("");
        for(String employeeNo:queryEmployees){
            sJoinEmployeeNo.append(employeeNo+",");
        }
        Map<String, String> mapEmployeeNo=new HashMap<String, String>();
        mapEmployeeNo.put("EMPLOYEENO", sJoinEmployeeNo.toString());

        String withasSql_employeeno="";
        withasSql_employeeno=cm.getFormatSourceMultiColWith(mapEmployeeNo);
        mapEmployeeNo=null;

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuffer sqlbufDetail=new StringBuffer();
        sqlbufDetail.append(" "
                + " select a.eid,a.purorderno,f.item,sum(f.RECEIVEQTY) receiveqtysum " +
                "  from DCP_PURORDER a"
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno  and a.organizationno=d.organizationno"
                + " left join DCP_PURORDER_DELIVERY f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item and a.organizationno=f.organizationno "
                + " left join DCP_BIZPARTNER b  on b.bizpartnerno=a.SUPPLIER and b.eid=a.eid   "
                + " where  a.eid= '"+ eId +"' and a.organizationno='"+organizationNO+"' and (f.RECEIVEQTY<d.PURQTY or f.RECEIVEQTY is null )  "
                + " "
                + " ");
        if(!Check.Null(status)){
            sqlbufDetail.append(" and a.status='"+status+"' ");
        }

        if(!Check.Null(purOrderNo)){
            sqlbufDetail.append(" and a.purorderno='"+purOrderNo+"'");
        }
        if(!Check.Null(supplier)){
            sqlbufDetail.append(" and (a.supplier like '%%"+supplier+"%%' " +
                    " or b.fname like '%%"+supplier+"%%'  " +
                    " or b.sname like '%%"+supplier+"%%' ) ");
        }

        if(!Check.Null(purType)){
            sqlbufDetail.append(" and a.purtype='"+purType+"' ");
        }
        if(!Check.Null(beginDate)){
            sqlbufDetail.append(" and a.bdate>='"+beginDate+"'");
        }
        if(!Check.Null(endDate)){
            sqlbufDetail.append(" and a.bdate<='"+endDate+"'");
        }

        sqlbufDetail.append(" group by a.eid,a.purorderno,f.item");


        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select purorder.* from (");
        sqlbuf.append("with p AS ( " + withasSql_employeeno + ") "
                + " select distinct a.*,b.fname as supplierName,c.org_name as orgName,dd0.departname as owndeptName,dd1.departname as CREATEDEPTNAME,em0.name as ownerName, em1.op_name as CREATEOPNAME,em2.op_name as LASTMODIOPNAME,em5.op_name as closeByName,em3.op_name as confirmName,em4.op_name as cancelByName," +
                " e.org_name as receiveorgname,em6.name as purempname,dd2.departname as purDeptName,a.bdate as bdates,a.expiredate as expiredateS,f.sname as payeename,g.org_name as receiptOrgName  " +
                "  from DCP_PURORDER a"
                + " inner join p on p.EMPLOYEENO=a.OWNOPID "
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno and a.organizationno=d.organizationno "
                + " left join ("+sqlbufDetail.toString()+") f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item "
                + " left join DCP_BIZPARTNER b  on b.bizpartnerno=a.SUPPLIER and b.eid=a.eid   "
                + " left join dcp_org_lang c on c.eid=a.eid and a.ORGANIZATIONNO=c.organizationno  and c.lang_type = '"+langType+"' "
                + " left join DCP_employee em0 on em0.eid=a.eid and em0.employeeno=a.OWNOPID "
                + " left join PLATFORM_STAFFS_LANG em1 on em1.eid=a.eid and em1.opno=a.CREATEOPID and em1.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em2 on em2.eid=a.eid and em2.opno=a.LASTMODIOPID and em2.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em3 on em3.eid=a.eid and em3.opno=a.CONFIRMBY and em3.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em4 on em4.eid=a.eid and em4.opno=a.CANCELBY and em4.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em5 on em5.eid=a.eid and em5.opno=a.CLOSEBY and em5.lang_type='"+langType+"'"
                + " left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.OWNDEPTID and dd0.lang_type='"+req.getLangType()+"'  "
                + " left join dcp_department_lang dd1 on dd1.eid=a.eid and dd1.departno=a.createdeptid and dd1.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_ORG_LANG e on e.eid=a.eid and e.organizationno=a.RECEIPTORGNO and e.lang_type='"+req.getLangType()+"' "
                + " left join dcp_employee em6 on em6.eid=a.eid and em6.employeeno=EMPLOYEEID "
                + " left join dcp_department_lang dd2 on dd2.eid=a.eid and dd2.departno=a.DEPARTID " +
                " left join dcp_org_lang g on g.eid=a.eid and g.organizationno=a.RECEIPTORGNO and g.lang_type='"+req.getLangType()+"' " +
                " left join DCP_BIZPARTNER f on f.eid=a.eid and f.bizpartnerno=a.payee "
                + " where  a.eid= '"+ eId +"' and a.organizationno='"+organizationNO+"' "
                + " "
                + " ");

        if(!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        if(!Check.Null(purOrderNo)){
            sqlbuf.append(" and a.purorderno='"+purOrderNo+"'");
        }
        if(!Check.Null(supplier)){
            sqlbufDetail.append(" and (a.supplier like '%%"+supplier+"%%' " +
                    " or b.fname like '%%"+supplier+"%%'  " +
                    " or b.sname like '%%"+supplier+"%%' ) ");
        }

        if(!Check.Null(purType)){
            sqlbuf.append(" and a.purtype='"+purType+"' ");
        }

        if(!Check.Null(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"'");
        }
        if(!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"'");
        }

        if(!Check.Null(receiveStatus)){ //新建 作废状态不包含 //待收货、部分收货、收货结束 0 新建 3 作废
            //待收货、部分收货、收货结束
            if(receiveStatus.equals("1")){
                //待收货  已收货量=0
                sqlbuf.append(" and f.receiveqtysum=0  and a.status !='0' and a.status !='3' ");
            }
            if(receiveStatus.equals("2")){
                //部分收货 已收货量>0 且已收货量<采购量
                sqlbuf.append(" and f.receiveqtysum>0 and f.receiveqtysum<d.purqty  and a.status !='0' and a.status !='3'  ");
            }
            if(receiveStatus.equals("3")){
                //收货结束  已收货量>0 且已收货量>=采购量
                //不存在以上两种情况
                sqlbuf.append(" and (f.purorderno is null or f.purorderno='' )  and a.status !='0' and a.status !='3'  ");
            }

        }

        sqlbuf.append(" ) purorder");


        sqlbuf.append(" "
                + " order by purorder.bdate desc, purorder.purorderno desc"
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    private List<String> getValidEmployees(DCP_PurOrderQueryReq req,String modularNo,String range) throws Exception{

        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        //PLATFORM_STAFFS_ROLE
        String opNO = req.getOpNO();
        MyCommon cm=new MyCommon();

        String sqlPsr="select * from PLATFORM_STAFFS_ROLE where eid='"+req.geteId()+"' and OPNO='"+req.getOpNO()+"' and status='100' ";
        List<Map<String, Object>> getPsrData=this.doQueryData(sqlPsr, null);
        if(getPsrData.size()==0){
            return new ArrayList<>();
        }
        List<String> opGroups = getPsrData.stream().map(x -> x.get("OPGROUP").toString()).distinct().collect(Collectors.toList());
        String opGroupStr="";
        for(String opGroup:opGroups){
            opGroupStr+="'"+opGroup+"',";
        }
        opGroupStr=opGroupStr.substring(0,opGroupStr.length()-1);
        String sqlPb="select * from PLATFORM_BILLPOWER where eid='"+req.geteId()+"' and MODULARNO='"+modularNo+"' and OPGROUP in ("+opGroupStr+") and status='100' ";
        List<Map<String, Object>> getPbData=this.doQueryData(sqlPb, null);
        if(getPbData.size()==0){
            return new ArrayList<>();
        }
        //0-全部 1-个人 2-所属部门 3-所属部门及下级部门 4-同级部门
        List<Map<String, Object>> powerrange = getPbData.stream().filter(x -> x.get(range).toString().equals("0")).collect(Collectors.toList());
        if(powerrange.size()>0){
            //全部权限
            String employeeSql=String.format("select * from dcp_employee where eid='%s'",req.geteId());
            List<Map<String, Object>> getEmployeeData=this.doQueryData(employeeSql, null);
            return getEmployeeData.stream().map(x -> x.get("EMPLOYEENO").toString()).collect(Collectors.toList());

        }
        //获取人员 排除全部了
        List<String> opENos=new ArrayList();

        //获取同部门人员
        String deSql="select * from dcp_department where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' " +
                " and status='100' ";
        List<Map<String, Object>> getAllDeData=this.doQueryData(deSql, null);

        //查找上级部门
        String upDepartNo=req.getUpDepartNo();

        List<String> departmentList=new ArrayList();
        for (Map<String, Object> map:getPbData){
            String powerRange=map.get(range).toString();
            //if(powerRange.equals("1")&&!opNos.contains(employeeNo)){
            //  opNos.add(employeeNo);
            //}
            if(!opENos.contains(employeeNo)){
                opENos.add(employeeNo);
            }

            //234 要加所属部门
            if(!"1".equals(powerRange)){
                departmentList.add(departmentNo);
            }

            if("3".equals(powerRange)){
                //所属部门及下级部门
                addChildDepartment(departmentList,getAllDeData,departmentNo);
            }

            if("4".equals(powerRange)){
                //同级部门及下级部门
                if(upDepartNo.length()>0){
                    addChildDepartment(departmentList,getAllDeData,upDepartNo);
                }
            }
        }

        if(departmentList.size()>0){
            StringBuffer sJoinDepartNo=new StringBuffer("");
            for(String departNo:departmentList){
                sJoinDepartNo.append(departNo+",");
            }
            Map<String, String> mapDepartNo=new HashMap<String, String>();
            mapDepartNo.put("DEPARTNO", sJoinDepartNo.toString());

            String withasSql_departno="";
            withasSql_departno=cm.getFormatSourceMultiColWith(mapDepartNo);
            mapDepartNo=null;

            if (!withasSql_departno.equals("")) {
                return  opENos;
            }

            String sqlEmployee="with p AS ( " + withasSql_departno + ") " +
                    "select a.* from dcp_employee a " +
                    "inner join p on p.departno=a.DEPARTMENTNO " +
                    "where a.eid='"+req.geteId()+"' and a.status='100' ";
            List<Map<String, Object>> getEmployeeData=this.doQueryData(sqlEmployee, null);
            for (Map<String, Object> map:getEmployeeData){
                String employeeNo1=map.get("EMPLOYEENO").toString();
                if(!opENos.contains(employeeNo1)){
                    opENos.add(employeeNo1);
                }
            }
        }
        return opENos;
    }

    private void addChildDepartment(List departmentNos,List<Map<String, Object>> getAllDeData,String upDepartmentNo) throws Exception{
        for (Map<String, Object> map:getAllDeData){
            String departno = map.get("DEPARTNO").toString();
            String updepartno = map.get("UPDEPARTNO").toString();
            if(updepartno.equals(upDepartmentNo)&&!departmentNos.contains(departno)){
                departmentNos.add(departno);
                addChildDepartment(departmentNos,getAllDeData,departno);
            }
        }
    }


}

