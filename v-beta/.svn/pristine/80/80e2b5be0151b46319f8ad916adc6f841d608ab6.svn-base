package com.dsc.spos.restfulservice;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.utils.MySpringContext;
import com.dsc.spos.waimai.*;
import com.dsc.spos.waimai.isv.*;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Path("/ISV")
public class ISV_WMRestfulService {
    Logger logger = LogManager.getLogger(SPosRestfulService.class.getName());
    private MySpringContext springContext;
    public void setSpringContext(MySpringContext springContext) {
        this.springContext = springContext;
    }

    /**
     * 这个是直接访问资源类就可以测试到的
     *
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "Hello , Welcome to the DCP-ISV Service!";
    }

    @POST
    public String sayHelloPOST() {
        return "Hello jersey , Welcome to the DCP-ISV Service!";
    }

    @GET
    @Path("/Waimai")
    @Produces(MediaType.TEXT_PLAIN)
    public String waimaiGet() {
        return "Hello , Welcome to the DCP-ISV-Waimai Service!";
    }

    @POST
    @Path("/Waimai")
    @Produces(MediaType.TEXT_PLAIN)
    public String waimaiPost() {
        return "Hello jersey , Welcome to the DCP-ISV-Waimai Service!";
    }

    /**
     * 美团聚宝盆接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBP")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBP(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"OK\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new ISV_WMJBPService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"data\":\"OK\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            jb = null;
            sResponseMSG = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        // response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

    }




    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBPToken")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPToken(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"success\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new ISV_WMJBPTokenService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/JBPToken")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPToken_Get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"data\":\"success\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP绑定后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/JBPTokenReleaseBinding")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPTokenReleaseBinding(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"data\":\"success\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            // sResponseMSG="{\"data\":\"success\"}";
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new ISV_WMJBPTokenReleaseBindingService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);

            json = null;
            swm = null;

        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        if (dao != null) {
            dao.closeDAO();
        }
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        jb = null;
        request = null;

    }

    /**
     * 聚宝盆美团门店与ERP解绑后接受回传的Token
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/JBPTokenReleaseBinding")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiJBPTokenReleaseBinding_Get(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response) throws Exception {
        String sResponseMSG = "{\"data\":\"success\"}";
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        sResponseMSG = null;
        request = null;

    }

    /**
     * 客户端请求服务端获取饿了么外卖授权url(内部使用)
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/ELMAuthUrl")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiElmAuthUrl(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        JSONObject resObj = new JSONObject();
        resObj.put("success",false);
        resObj.put("serviceStatus","200");
        resObj.put("serviceDescription","获取授权URL失败！");
        String sResponseMSG = resObj.toString();
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            resObj.put("serviceDescription",e.getMessage());
            sResponseMSG = resObj.toString();
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new ISV_WMELMClientAuthUrlGetService();
            swm.setDao(dao);

            String json = jb.toString();

           String url = swm.execute(json);
           if (url!=null&&!url.isEmpty())
           {
               resObj.put("success",true);
               resObj.put("serviceStatus","000");
               resObj.put("serviceDescription","获取授权URL成功！");
               resObj.put("url",url);
               sResponseMSG = resObj.toString();
           }

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            resObj.put("serviceDescription",e.getMessage());
            sResponseMSG = resObj.toString();
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }

    @GET
    @Path("/Waimai/ELMCallBack")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELMCallBackGet(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        String sResponseMSG = "{\"message\":\"ok\"}";
        JSONObject obj = new JSONObject();
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String error = request.getParameter("error");
        String error_description = request.getParameter("error_description");
        obj.put("state",state);
        obj.put("code",code);
        obj.put("error",error);
        obj.put("method","get");
        obj.put("error_description",error_description);
        String json = obj.toString();
        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");
        try
        {
            SWaimaiBasicService swm = new ISV_WMELMCallBackService();
            swm.setDao(dao);
            swm.execute(json);

        }
        catch ( Exception e)
        {

        }
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
        //
        sResponseMSG = null;
        request = null;

    }

    @POST
    @Path("/Waimai/ELMCallBack")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELMCallBackPost(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        JSONObject resObj = new JSONObject();
        resObj.put("success",false);
        resObj.put("serviceStatus","200");
        resObj.put("serviceDescription","获取授权URL失败！");
        String sResponseMSG = resObj.toString();
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            //
            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            resObj.put("serviceDescription",e.getMessage());
            sResponseMSG = resObj.toString();
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {
            SWaimaiBasicService swm = new ISV_WMELMCallBackService();
            swm.setDao(dao);

            String json = jb.toString();

            String url = swm.execute(json);
            if (url!=null&&!url.isEmpty())
            {
                resObj.put("success",true);
                resObj.put("serviceStatus","000");
                resObj.put("serviceDescription","获取授权URL成功！");
                resObj.put("url",url);
                sResponseMSG = resObj.toString();
            }

            //
            json = null;
            swm = null;

        } catch (Exception e) {
            resObj.put("serviceDescription",e.getMessage());
            sResponseMSG = resObj.toString();
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        jb = null;
        sResponseMSG = null;
        request = null;

    }

    /**
     * 饿了么接受推送消息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/ELM")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiELM(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        // 标记默认成功
        boolean bSuccess = true;
        String sErrorMSG = "";
        String sResponseMSG = "{\"message\":\"ok\"}";
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            InputStreamReader insr = new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(insr);

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            reader = null;
            insr = null;

        } catch (Exception e) {

            sErrorMSG = e.getMessage();// 赋值

            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";

            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }
        // System.out.println(jb);

        DsmDAO dao;

        dao = (DsmDAO) this.springContext.getContext().getBean("sposDao");

        try {

            SWaimaiBasicService swm = new ISV_WMELMService();
            swm.setDao(dao);

            String json = jb.toString();

            swm.execute(json);
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"message\":\"ok\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

            //
            json = null;
            swm = null;
            sResponseMSG = null;
            jb = null;
            request = null;

            return;
        } catch (Exception e) {
            if (dao != null) {
                dao.closeDAO();
            }
            sResponseMSG = "{\"code\":600,\"msg\":\"内部错误\"}";
            response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));
            return;
        }

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @POST
    @Path("/Waimai/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTOtherPost(@Context HttpServletRequest request, @Context HttpServletResponse response,
                              @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }

    /**
     * 隐私降级号/催单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GET
    @Path("/Waimai/{MTTYPE}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_XML + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8",
            MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public void WaimaiMTOther_Get(@Context HttpServletRequest request, @Context HttpServletResponse response,
                              @PathParam("MTTYPE") String mtType) throws Exception {
        String sResponseMSG = "{\"data\":\"OK\"}";
        if ("ELM".equals(mtType))
        {
            sResponseMSG = "{\"message\":\"ok\"}";
        }
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(sResponseMSG.getBytes("UTF-8"));

        //
        sResponseMSG = null;
        request = null;

    }


}
