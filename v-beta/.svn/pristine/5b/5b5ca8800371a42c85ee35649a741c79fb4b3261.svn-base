package com.dsc.spos.redis;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;

import java.util.Set;

public class DocSubmitStop {

    public static boolean isStop(String key_redis){
        RedisPosPub redis=new RedisPosPub();
        //ParseJson pj = new ParseJson();
        //查出此单所有正在提交的
        Set<String> keys=redis.keys(key_redis);
        if (keys != null && keys.size()>0){
            //存在key了 直接返回
            return true;
        }

        //有效期300秒  上面校验通过了再存redis
        redis.setEx(key_redis,300,"1");
        return false;
    }

    public static void endStop(String key_redis){

        RedisPosPub redis=new RedisPosPub();
        if (!Check.Null(key_redis)) {
            redis.DeleteKey(key_redis);
        }
    }

}
