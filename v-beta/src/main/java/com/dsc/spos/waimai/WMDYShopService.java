package com.dsc.spos.waimai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WMDYShopService {
    private static String logFileName = "douyinWMLog";
    private static String HOST="https://open.douyin.com";

    public static String getShopList(boolean isTest,String clientKey,String clientSecret,String account_id,int page,int size,StringBuilder errorMessage) throws Exception
    {
        String api = "/goodlife/v1/shop/poi/query/";
        String req = "";
        String res = "";
        String reqIdLog = "请求ID["+ UUID.randomUUID().toString().replace("-","")+"]";
        try
        {
            String token = WMDYUtilTools.GetTokenInRedis(isTest,clientKey,clientSecret);
            String url = HOST+api;
            Map<String,Object> headerParams = new HashMap<>();
            if (isTest)
            {
                headerParams.put("X-SandBox-Token","1");
            }
            headerParams.put("access-token",token);
            url +="?"+"account_id="+account_id+"&"+"page="+page+"&"+"size="+size;
            HelpTools.writelog_fileName(reqIdLog+"【DYWM查询门店列表URL】:"+url+",请求header内容："+headerParams.toString(), logFileName);
            res = WMDYUtilTools.getJson(url,headerParams);
            //res ="{\"data\":{\"pois\":[{\"poi\":{\"poi_id\":\"6994674961470212127\",\"poi_name\":\"CHARLIE'S粉红汉堡(常熟路店)\",\"address\":\"常熟路85号一层A6、A7号铺位\",\"latitude\":82.580195,\"longitude\":40.101612},\"root_account\":{\"account_id\":\"7014065493820573710\",\"account_name\":\"上海曾士餐饮有限公司\"}},{\"poi\":{\"poi_id\":\"6994674961470212127A\",\"poi_name\":\"CHARLIE'S粉红汉堡(常熟路店)\",\"address\":\"常熟路85号一层A6、A7号铺位\",\"latitude\":82.580195,\"longitude\":40.101612},\"root_account\":{\"account_id\":\"7014065493820573710\",\"account_name\":\"上海曾士餐饮有限公司\"}},{\"poi\":{\"poi_id\":\"6994674961470212127B\",\"poi_name\":\"CHARLIE'S粉红汉堡(常熟路店)\",\"address\":\"常熟路85号一层A6、A7号铺位\",\"latitude\":82.580195,\"longitude\":40.101612},\"root_account\":{\"account_id\":\"7014065493820573710\",\"account_name\":\"上海曾士餐饮有限公司\"}},{\"poi\":{\"poi_id\":\"6994674961470212127C\",\"poi_name\":\"CHARLIE'S粉红汉堡(常熟路店)\",\"address\":\"常熟路85号一层A6、A7号铺位\",\"latitude\":82.580195,\"longitude\":40.101612},\"root_account\":{\"account_id\":\"7014065493820573710\",\"account_name\":\"上海曾士餐饮有限公司\"}}],\"total\":205,\"error_code\":0,\"description\":\"success\"},\"extra\":{\"error_code\":0,\"description\":\"success\",\"sub_error_code\":0,\"sub_description\":\"\",\"logid\":\"20230728153305C38E42BF588965058061\",\"now\":1690529585}}";
            HelpTools.writelog_fileName(reqIdLog+"【DYWM查询门店列表URL】返回res:"+res, logFileName);
            return res;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(reqIdLog+"【DYWM查询门店列表URL】异常:"+e.getMessage(), logFileName);
        }
        return res;
    }
}
