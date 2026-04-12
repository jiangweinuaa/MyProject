package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsDeleteReq.levelPlu;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsDeleteRes;
import com.dsc.spos.model.Template_POS_GoodsTemplateDetailRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateGoodsDelete extends SPosAdvanceService<DCP_GoodsTemplateGoodsDeleteReq,DCP_GoodsTemplateGoodsDeleteRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateGoodsDeleteReq req, DCP_GoodsTemplateGoodsDeleteRes res) throws Exception 
	{

        //同步缓存
        Template_POS_GoodsTemplateDetailRedisUpdate v_template=new Template_POS_GoodsTemplateDetailRedisUpdate();
        v_template.setTemplateId(req.getRequest().getTemplateId());
        v_template.setPluList(new ArrayList<>());

		String eId=req.geteId();


		String templateId = req.getRequest().getTemplateId();
		String isAllGoods = req.getRequest().getIsAllGoods();


        //删除失败标记
        boolean b_Fail=false;
        StringBuffer sb=new StringBuffer();


        if(isAllGoods.equals("Y"))
		{
            String sql = "select STATUS,REDISUPDATESUCCESS,PLUNO from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and templateid='"+templateId+"' ";
            List<Map<String , Object>> getData=this.doQueryData(sql, null);
            if (getData!=null && getData.isEmpty()==false)
            {
                for (Map<String, Object> oneData : getData)
                {
                    String status=oneData.get("STATUS").toString();//状态：-1未启用100已启用 0已禁用
                    String redisupdatesuccess=oneData.get("REDISUPDATESUCCESS").toString();
                    String pluNo=oneData.get("PLUNO").toString();

                    //启用状态不能删除、禁用状态且未同步缓存也不能删除
                    if (status.equals("100")||(redisupdatesuccess.equals("N") && status.equals("0")))
                    {
                        b_Fail=true;
                        sb.append("商品编号："+pluNo+",启用状态不能删除、禁用状态且未同步缓存也不能删除！<br/>");
                        continue;
                    }
                    else
                    {
                        DelBean db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
                        db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
                        db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
                        db1.addCondition("PLUNO", new DataValue(pluNo,Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));


                        //同步缓存
                        Template_POS_GoodsTemplateDetailRedisUpdate.plu plu=v_template.new plu();
                        plu.setPluNo(pluNo);
                        v_template.getPluList().add(plu);
                    }
                }

            }
		}
		else 
		{
			List<levelPlu> pluNoList = req.getRequest().getPluList();
			if(pluNoList!=null)
			{
			    //
                StringBuffer sJoinPluno=new StringBuffer("");
                for (levelPlu par : pluNoList)
                {
                    sJoinPluno.append(par.getPluNo()+",");
                }
                //
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("PLUNO", sJoinPluno.toString());

                //
                MyCommon cm=new MyCommon();
                String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                mapOrder=null;
                mapOrder=null;

                String sql="with a AS ( " + withasSql_Orderno +" ) " +
                        "select b.* from a inner join DCP_GOODSTEMPLATE_GOODS b on a.pluno=b.pluno " +
                        "where b.EID='"+eId+"' " +
                        "and b.TEMPLATEID='"+templateId+"' ";
                List<Map<String , Object>> getData=this.doQueryData(sql, null);

                for (levelPlu par : pluNoList)
				{
                    //同步缓存
                    Template_POS_GoodsTemplateDetailRedisUpdate.plu plu=v_template.new plu();
                    plu.setPluNo(par.getPluNo());
                    v_template.getPluList().add(plu);

					String pluNo=par.getPluNo();

					//过滤
					Map<String, Object> cond=new HashMap<>();
					cond.put("PLUNO",pluNo);
					List<Map<String, Object>> mapList=MapDistinct.getWhereMap(getData,cond,false);

					if (mapList != null && mapList.size()>0)
					{
						String status=mapList.get(0).get("STATUS").toString();//状态：-1未启用100已启用 0已禁用
						String redisupdatesuccess=mapList.get(0).get("REDISUPDATESUCCESS").toString();

						//启用状态不能删除、禁用状态且未同步缓存也不能删除
						if (status.equals("100")||(redisupdatesuccess.equals("N") && status.equals("0")))
						{
							b_Fail=true;
							sb.append("商品编号："+pluNo+",启用状态不能删除、禁用状态且未同步缓存也不能删除！<br/>");
							continue;
						}
						else
						{
							DelBean db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
							db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
							db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
							db1.addCondition("PLUNO", new DataValue(pluNo,Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db1));
						}
					}

					
				}	
			}
			
		}

		
		this.doExecuteDataToDB();

		if (b_Fail)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(sb.toString());
		}
		else
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		}


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


		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateGoodsDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if(Check.Null(req.getRequest().getIsAllGoods()))
		{
			isFail = true;
			errMsg.append("是否删除全部商品isAllGoods不能为空 ");
		}
		    

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateGoodsDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateGoodsDeleteReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateGoodsDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateGoodsDeleteRes();
	}




}
