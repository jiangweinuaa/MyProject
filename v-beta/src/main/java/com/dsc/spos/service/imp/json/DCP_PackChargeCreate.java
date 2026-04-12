package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.exception.GenericJDBCException;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PackChargeCreateReq;
import com.dsc.spos.json.cust.req.DCP_PackChargeCreateReq.Goods;
import com.dsc.spos.json.cust.res.DCP_PackChargeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

public class DCP_PackChargeCreate extends SPosAdvanceService<DCP_PackChargeCreateReq, DCP_PackChargeCreateRes> {
	
    @Override
    protected void processDUID(DCP_PackChargeCreateReq req, DCP_PackChargeCreateRes res) throws Exception {
    	Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
    	
    	String eId = req.geteId();
    	DCP_PackChargeCreateReq.levelElm request = req.getRequest();
    	List<Goods> goodsList = req.getRequest().getGoodsList();
    	
    	String packPluNo = request.getPackPluNo();
    	//DCP_PACKCHARGE
    	String[] packcharge_columns = {"EID", "PACKPLUNO", "MEMO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "TRAN_TIME",
    			"PACKPLUTYPE","PACKBAGNUM"};
		DataValue[] packcharge_insValue = new DataValue[] 
		{
			new DataValue(eId, Types.VARCHAR),											
			new DataValue(packPluNo, Types.VARCHAR),					
			new DataValue(request.getMemo(), Types.VARCHAR),			
			new DataValue(req.getOpNO(), Types.VARCHAR),
			new DataValue(req.getOpName(), Types.VARCHAR),
			new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE),
			new DataValue(mySysTime, Types.VARCHAR),
			new DataValue(request.getPackPluType(), Types.VARCHAR),
			new DataValue(request.getPackBagNum(), Types.VARCHAR)
		};
		InsBean packcharge_ins = new InsBean("DCP_PACKCHARGE", packcharge_columns);
		packcharge_ins.addValues(packcharge_insValue);
		this.addProcessData(new DataProcessBean(packcharge_ins)); 
		
		//DCP_PACKCHARGE_GOODS
		if(CollectionUtil.isNotEmpty(goodsList)) {
			String[] packcharge_goods_columns = {"EID", "PLUNO", "UNITID", "PACKPLUNO"};
			for (Goods goods : goodsList)
			{	
				DataValue[] packcharge_goods_insValue = new DataValue[]
				{
					new DataValue(eId, Types.VARCHAR),					
					new DataValue(goods.getPluNo(), Types.VARCHAR),
					new DataValue(goods.getUnitId(), Types.VARCHAR),
					new DataValue(packPluNo, Types.VARCHAR)
				};
				InsBean packcharge_goods_ins = new InsBean("DCP_PACKCHARGE_GOODS", packcharge_goods_columns);
				packcharge_goods_ins.addValues(packcharge_goods_insValue);
				this.addProcessData(new DataProcessBean(packcharge_goods_ins));
			}
		}
		try {
			this.doExecuteDataToDB();
		} catch (Exception e) {
			res.setSuccess(false);
			String errmsg = StrUtil.isEmpty(e.getCause().getMessage())?e.getLocalizedMessage():e.getCause().getMessage();
			if(e instanceof GenericJDBCException && errmsg.contains("ORA-00001")) {
				errmsg = "当前模板包含已存在的商品，请检查后重试";
			}
			res.setServiceStatus("200");
			res.setServiceDescription(errmsg);
			return;
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PackChargeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PackChargeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PackChargeCreateReq req) throws Exception {
        return null;
    }

	@Override
	protected boolean isVerifyFail(DCP_PackChargeCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String packPluNo = req.getRequest().getPackPluNo();
		if (Check.Null(packPluNo)) {
			errMsg.append("打包商品编码packPluNo不能为空值， ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

    @Override
    protected TypeToken<DCP_PackChargeCreateReq> getRequestType() {
        return new TypeToken<DCP_PackChargeCreateReq>() {};
    }

    @Override
    protected DCP_PackChargeCreateRes getResponseType() {
        return new DCP_PackChargeCreateRes();
    }
}
