package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsCreateReq.levelAttr;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsCreateReq.levelRequest;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsCreateReq.levelSpec;
import com.dsc.spos.json.cust.res.DCP_WMSPGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public  class DCP_WMSPGoodsUpdate extends SPosAdvanceService<DCP_WMSPGoodsCreateReq, DCP_WMSPGoodsCreateRes> {
    @Override
    protected void processDUID(DCP_WMSPGoodsCreateReq req, DCP_WMSPGoodsCreateRes res) throws Exception {

        String eId = req.geteId();
        String goodsId = req.getRequest().getId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = " select * from dcp_wmspgoods where eid='"+eId+"' and id='"+goodsId+"'";
        List<Map<String,Object>> QDataList = this.doQueryData(sql,null);
        if (QDataList==null||QDataList.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "修改时传入的商品ID("+goodsId+")不存在！");
        }
        String name_db = QDataList.get(0).get("NAME").toString();
        //修改时传入的名称，是不是已存在
        if (!name_db.equals(req.getRequest().getName()))
        {
            if (checkName(req))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "修改时传入的商品名称("+req.getRequest().getName()+")已存在！");
            }
        }

        levelRequest request = req.getRequest();
        String status = request.getStatus();//状态：-1未启用100已启用 0已禁用
        if (Check.Null(status))
        {
            status = "100";//默认 100已启用
        }
        List<levelSpec> specList = request.getSpecDatas();
        DelBean db2 = new DelBean("DCP_WMSPGOODS_SPEC");
        db2.addCondition("ID", new DataValue(request.getId(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));
        DelBean db3 = new DelBean("DCP_WMSPGOODS_ATTR");
        db3.addCondition("ID", new DataValue(request.getId(), Types.VARCHAR));
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        if (specList!=null&&!specList.isEmpty())
        {
            String[] columnsName = {
                    "EID","ID","SPECID","SPECNAME","STATUS"
            };
            InsBean ib2 = new InsBean("DCP_WMSPGOODS_SPEC", columnsName);
            for (levelSpec par :specList)
            {
                String specId = par.getSpecId();
                String specName = par.getSpecName();

                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(request.getId(), Types.VARCHAR),
                                new DataValue(specId, Types.VARCHAR),
                                new DataValue(specName, Types.VARCHAR),
                                new DataValue(status, Types.VARCHAR)
                        };
                ib2.addValues(insValueDetail);
            }
            this.addProcessData(new DataProcessBean(ib2));
        }
        List<levelAttr> attrList = request.getAttrDatas();
        if (attrList!=null&&!attrList.isEmpty())
        {
            String[] columnsName = {
                    "EID","ID","ATTRNAME","ATTRVALUE","ATTRVALUE_ELM","ATTRVALUE_MT","PLUBARCODE","STATUS"
            };
            InsBean ib2 = new InsBean("DCP_WMSPGOODS_ATTR", columnsName);
            for (levelAttr par :attrList)
            {
                String attrName = par.getAttrName();
                if (Check.Null(attrName))
                {
                    attrName = "拼";
                }
                String attrValue = par.getAttrValue();
                String attrValue_elm = par.getAttrValue_elm();
                if (attrValue_elm!=null&&!attrValue_elm.trim().isEmpty())
                {

                }
                else
                {
                    attrValue_elm = attrValue;
                }
                String attrValue_mt = par.getAttrValue_mt();
                if (attrValue_mt!=null&&!attrValue_mt.trim().isEmpty())
                {

                }
                else
                {
                    attrValue_mt = attrValue;
                }

                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(request.getId(), Types.VARCHAR),
                                new DataValue(attrName, Types.VARCHAR),
                                new DataValue(attrValue, Types.VARCHAR),
                                new DataValue(attrValue_elm, Types.VARCHAR),
                                new DataValue(attrValue_mt, Types.VARCHAR),
                                new DataValue(par.getPluBarcode(), Types.VARCHAR),
                                new DataValue(status, Types.VARCHAR)
                        };
                ib2.addValues(insValueDetail);
            }
            this.addProcessData(new DataProcessBean(ib2));
        }

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_WMSPGOODS");
        //add Value
        ub1.addUpdateValue("NAME", new DataValue(request.getName(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        //condition
        ub1.addCondition("ID", new DataValue(request.getId(), Types.VARCHAR));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WMSPGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WMSPGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WMSPGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WMSPGoodsCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        levelRequest request = req.getRequest();
        List<levelSpec> specList = request.getSpecDatas();
        List<levelAttr> attrList = request.getAttrDatas();
        if(Check.Null(request.getId())){
            errMsg.append("商品ID不能为空值,");
            isFail = true;

        }
        if(request.getName()!=null&&!request.getName().trim().isEmpty())
        {
            //不能有空格
            request.setName(request.getName().trim());
        }
        else
        {
            errMsg.append("商品名称不能为空值,");
            isFail = true;
        }


        if (specList!=null&&!specList.isEmpty())
        {
            for (levelSpec par : specList)
            {
                if(Check.Null(par.getSpecId())){
                    errMsg.append("规格ID不能为空值,");
                    isFail = true;

                }
                if(Check.Null(par.getSpecName())){
                    errMsg.append("规格名称不能为空值,");
                    isFail = true;

                }
            }
        }

        if (attrList!=null&&!attrList.isEmpty())
        {
            for (levelAttr par : attrList)
            {
                if (par.getAttrValue()!=null&&!par.getAttrValue().trim().isEmpty())
                {
                    //不能有空格
                    par.setAttrValue(par.getAttrValue().trim());
                }
                else
                {
                    errMsg.append("属性值不能为空值,");
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
    protected TypeToken<DCP_WMSPGoodsCreateReq> getRequestType() {
        return new TypeToken<DCP_WMSPGoodsCreateReq>(){};
    }

    @Override
    protected DCP_WMSPGoodsCreateRes getResponseType() {
        return new DCP_WMSPGoodsCreateRes();
    }

    private boolean checkId(DCP_WMSPGoodsCreateReq req) throws Exception
    {
        boolean nRet = false;
        String eId = req.geteId();
        String goodsId = req.getRequest().getId();
        String sql = " select * from dcp_wmspgoods where eid='"+eId+"' and id='"+goodsId+"'";
        List<Map<String,Object>> QDataList = this.doQueryData(sql,null);
        if (QDataList!=null&&!QDataList.isEmpty())
        {
            nRet = true;
        }
        return nRet;
    }

    private boolean checkName(DCP_WMSPGoodsCreateReq req) throws Exception
    {
        boolean nRet = false;
        String eId = req.geteId();
        String name = req.getRequest().getName();
        String sql = " select * from dcp_wmspgoods where eid='"+eId+"' and name='"+name+"'";
        List<Map<String,Object>> QDataList = this.doQueryData(sql,null);
        if (QDataList!=null&&!QDataList.isEmpty())
        {
            nRet = true;
        }
        return nRet;
    }

    private List<Map<String,Object>> checkSpecId(String eId,String id, String specId) throws Exception
    {
        boolean nRet = false;
        String sql = " select * from DCP_WMSPGOODS_SPEC where eid='"+eId+"' and specid='"+specId+"'";
        List<Map<String,Object>> QDataList = this.doQueryData(sql,null);
        return QDataList;
    }
}
