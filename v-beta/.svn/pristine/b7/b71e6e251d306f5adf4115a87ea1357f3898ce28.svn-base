package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryExtUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryExtUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import javax.servlet.jsp.tagext.TryCatchFinally;

import org.hibernate.sql.Select;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_GoodsCategoryExtUpdate extends SPosAdvanceService<DCP_GoodsCategoryExtUpdateReq, DCP_GoodsCategoryExtUpdateRes> {
    @Override
    protected void processDUID(DCP_GoodsCategoryExtUpdateReq req, DCP_GoodsCategoryExtUpdateRes res) throws Exception {

        try
        {
            String eId = req.geteId();
            String category = req.getRequest().getCategory();
            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String categoryImage = req.getRequest().getCategoryImage();
            if (categoryImage==null)
            {
                categoryImage = "";
            }
            
            
            //先查询，没有插入，有才更新
            boolean isExist = this.isExistData(req);
            if(isExist)
            {
            	UptBean ub1 = new UptBean("DCP_CATEGORY_IMAGE");
                ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));

                ub1.addUpdateValue("CATEGORYIMAGE",new DataValue(categoryImage, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE));

                this.addProcessData(new DataProcessBean(ub1));
            }
            else
            {
            	String[] columnsDcpCategoryImage = {
						"EID","CATEGORY","CATEGORYIMAGE","LASTMODITIME"
				};
            	DataValue[]	insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(category, Types.VARCHAR),
						new DataValue(categoryImage, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
				};
				InsBean ib2 = new InsBean("DCP_CATEGORY_IMAGE", columnsDcpCategoryImage);
				ib2.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib2)); 
            }
            
            

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }
        catch (Exception e)
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:"+e.getMessage());

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsCategoryExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsCategoryExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsCategoryExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_GoodsCategoryExtUpdateReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String category = req.getRequest().getCategory();

        if(Check.Null(category))
        {
            errMsg.append("分类编码不能为空值 ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_GoodsCategoryExtUpdateReq> getRequestType() {
        return new TypeToken<DCP_GoodsCategoryExtUpdateReq>(){};
    }

    @Override
    protected DCP_GoodsCategoryExtUpdateRes getResponseType() {
        return new DCP_GoodsCategoryExtUpdateRes();
    }
    
    
    private boolean isExistData (DCP_GoodsCategoryExtUpdateReq req) throws Exception
    {
    	String eId = req.geteId();
        String category = req.getRequest().getCategory();
    	String sql = "select * from DCP_CATEGORY_IMAGE where EID='"+eId+"' and CATEGORY='"+category+"' ";
    	
    	List<Map<String, Object>> getQData = this.doQueryData(sql, null);
    	if(getQData!=null&&getQData.isEmpty()==false)
    	{
    		return true;
    	}
    	
    	
    	
    	return false;
    }
}
