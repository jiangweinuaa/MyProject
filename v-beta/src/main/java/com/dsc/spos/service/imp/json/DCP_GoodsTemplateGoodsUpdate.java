package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsUpdateReq.levelPlu;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsUpdateRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.model.Template_POS_GoodsTemplateDetailRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_GoodsTemplateGoodsUpdate extends SPosAdvanceService<DCP_GoodsTemplateGoodsUpdateReq,DCP_GoodsTemplateGoodsUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsTemplateGoodsUpdateReq req, DCP_GoodsTemplateGoodsUpdateRes res) throws Exception
	{
		try {

		    //同步缓存
            Template_POS_GoodsTemplateDetailRedisUpdate v_template=new Template_POS_GoodsTemplateDetailRedisUpdate();
            v_template.setTemplateId(req.getRequest().getTemplateId());
            v_template.setPluList(new ArrayList<>());

			String eId=req.geteId();
			String templateId = req.getRequest().getTemplateId();
			String sql = "";
			List<levelPlu> pluList = req.getRequest().getPluList();

			for (levelPlu par : pluList)
			{
			    //同步缓存
                Template_POS_GoodsTemplateDetailRedisUpdate.plu plu=v_template.new plu();
                plu.setPluNo(par.getPluNo());
                v_template.getPluList().add(plu);

				String pluNo=par.getPluNo();
				boolean isFlag = false;
				UptBean ub1 = new UptBean("DCP_GOODSTEMPLATE_GOODS");
				//add Value
				if(par.getWarningQty()!=null&&par.getWarningQty().trim().length()>0)
				{
					ub1.addUpdateValue("WARNINGQTY", new DataValue(par.getWarningQty(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getSafeQty()!=null&&par.getSafeQty().trim().length()>0)
				{
					ub1.addUpdateValue("SAFEQTY", new DataValue(par.getSafeQty(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanSale()!=null&&par.getCanSale().trim().length()>0)
				{
					ub1.addUpdateValue("CANSALE", new DataValue(par.getCanSale(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanFree()!=null&&par.getCanFree().trim().length()>0)
				{
					ub1.addUpdateValue("CANFREE", new DataValue(par.getCanFree(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanReturn()!=null&&par.getCanReturn().trim().length()>0)
				{
					ub1.addUpdateValue("CANRETURN", new DataValue(par.getCanReturn(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanOrder()!=null&&par.getCanOrder().trim().length()>0)
				{
					ub1.addUpdateValue("CANORDER", new DataValue(par.getCanOrder(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanPurchase()!=null&&par.getCanPurchase().trim().length()>0)
				{
					ub1.addUpdateValue("CANPURCHASE", new DataValue(par.getCanPurchase(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanRequire()!=null&&par.getCanRequire().trim().length()>0)
				{
					ub1.addUpdateValue("CANREQUIRE", new DataValue(par.getCanRequire(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getMinQty()!=null&&par.getMinQty().trim().length()>0)
				{
					ub1.addUpdateValue("MINQTY", new DataValue(par.getMinQty(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getMaxQty()!=null&&par.getMaxQty().trim().length()>0)
				{
					ub1.addUpdateValue("MAXQTY", new DataValue(par.getMaxQty(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getMultiQty()!=null&&par.getMultiQty().trim().length()>0)
				{
					ub1.addUpdateValue("MULQTY", new DataValue(par.getMultiQty(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanRequireBack()!=null&&par.getCanRequireBack().trim().length()>0)
				{
					ub1.addUpdateValue("CANREQUIREBACK", new DataValue(par.getCanRequireBack(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getIsAutoSubtract()!=null&&par.getIsAutoSubtract().trim().length()>0)
				{
					ub1.addUpdateValue("IS_AUTO_SUBTRACT", new DataValue(par.getIsAutoSubtract(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getCanEstimate()!=null&&par.getCanEstimate().trim().length()>0)
				{
					ub1.addUpdateValue("CANESTIMATE", new DataValue(par.getCanEstimate(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getClearType()!=null&&par.getClearType().trim().length()>0)
				{
					ub1.addUpdateValue("CLEARTYPE", new DataValue(par.getClearType(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getIsNewGoods()!=null&&par.getIsNewGoods().trim().length()>0)
				{
					ub1.addUpdateValue("ISNEWGOODS", new DataValue(par.getIsNewGoods(), Types.VARCHAR));
					isFlag = true;
				}
				if(par.getIsAllot()!=null && par.getIsAllot().trim().length()>0) {
					ub1.addUpdateValue("ISALLOT", new DataValue(par.getIsAllot(), Types.VARCHAR));
					isFlag = true;
				}

                if(par.getSupplierType()!=null&&par.getSupplierType().trim().length()>0)
                {
                    ub1.addUpdateValue("SUPPLIERTYPE", new DataValue(par.getSupplierType(), Types.VARCHAR));
                    isFlag = true;
                }

                if(par.getSupplierId()!=null&&par.getSupplierId().trim().length()>0)
                {
                    ub1.addUpdateValue("SUPPLIERID", new DataValue(par.getSupplierId(), Types.VARCHAR));
                    isFlag = true;
                }

				if(isFlag)
				{
                    ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                    String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
				}

				//更新缓存标记
				ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));
				isFlag=true;

				if(!isFlag)//没有需要更新的，不添加到sql
				{
					continue;
				}

				//condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");

            //同步缓存
            String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
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
            //
            PosPub.POS_GoodsTemplateDetailRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,v_template);


		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateGoodsUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<levelPlu> pluList = req.getRequest().getPluList();

		if (pluList==null || pluList.size()==0)
		{
			errMsg.append("商品列表不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (levelPlu par : pluList)
		{
			if(Check.Null(par.getPluNo()))
			{
				errMsg.append("商品编码不能为空值 ");
				isFail = true;
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateGoodsUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateGoodsUpdateReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateGoodsUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateGoodsUpdateRes();
	}




}
