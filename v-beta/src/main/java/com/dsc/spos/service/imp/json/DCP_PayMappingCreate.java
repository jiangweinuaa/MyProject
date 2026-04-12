package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayMappingCreateReq;
import com.dsc.spos.json.cust.res.DCP_PayMappingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMappingCreate extends SPosAdvanceService<DCP_PayMappingCreateReq, DCP_PayMappingCreateRes>
{

	@Override
	protected void processDUID(DCP_PayMappingCreateReq req, DCP_PayMappingCreateRes res) throws Exception 
	{

        // TODO Auto-generated method stub
        try
        {
            String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String eId = req.geteId();
            String docType = req.getRequest().getChannelType();
            String channelId = req.getRequest().getChannelId();
            String payType = req.getRequest().getPayType();
            String payName = req.getRequest().getPayName();
            String order_paycode = req.getRequest().getOrder_paycode();
            String order_payname = req.getRequest().getOrder_payname();


            String sql = null;
            sql = this.isRepeat(eId, docType, channelId, order_paycode);
            List<Map<String, Object>> scDatas = this.doQueryData(sql, null);
            if (scDatas!=null&&!scDatas.isEmpty())
            {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("该支付信息已存在，请勿重复添加！");
                return;

            }
            String[] columns1 =
                    { "EID", "CHANNELTYPE", "CHANNELID", "ORDER_PAYCODE","ORDER_PAYNAME", "PAYTYPE", "PAYNAME", "CHANNELTYPENAME",
                            "CHANNELIDNAME","CREATEOPID","CREATEOPNAME","CREATETIME"};
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(docType, Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),
                            new DataValue(order_paycode, Types.VARCHAR),
                            new DataValue(order_payname, Types.VARCHAR),
                            new DataValue(payType, Types.VARCHAR),
                            new DataValue(payName, Types.VARCHAR),
                            new DataValue(req.getRequest().getChannelTypeName(), Types.VARCHAR),
                            new DataValue(req.getRequest().getChannelIdName(), Types.VARCHAR),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(req.getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)

                    };
            InsBean ib1 = new InsBean("DCP_PAYMENTMAPPING", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!");
        }
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMappingCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMappingCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMappingCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMappingCreateReq req) throws Exception 
	{
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getChannelType()))
        {
            errMsg.append("渠道类型不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getChannelId()))
        {
            errMsg.append("渠道编码不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getOrder_paycode()))
        {
            errMsg.append("第三方平台支付编码不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getOrder_payname()))
        {
            errMsg.append("第三方平台支付名称不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getPayType()))
        {
            errMsg.append("新零售支付编码不能为空值，");
            isFail = true;

        }



        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
	}

	@Override
	protected TypeToken<DCP_PayMappingCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_PayMappingCreateReq>(){};
	}

	@Override
	protected DCP_PayMappingCreateRes getResponseType() 
	{
		return new DCP_PayMappingCreateRes();
	}

    /**
     * 验证映射信息是否已存在
     * @param eId
     * @param docType
     * @param channelId
     * @param order_payCode
     * @return
     */
    private String isRepeat( String eId , String docType, String channelId, String order_payCode ){
        String sql = null;
        sql = "select * from DCP_PAYMENTMAPPING "
                + " where  EID = '"+eId +"' "
                + " and CHANNELTYPE = '"+docType+"' "
                + " and CHANNELID = '"+channelId+"' "
                + " and ORDER_PAYCODE = '"+order_payCode+"'";
        return sql;
    }
	

}
