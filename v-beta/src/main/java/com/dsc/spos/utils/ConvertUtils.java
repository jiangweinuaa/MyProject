package com.dsc.spos.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertUtils
{
    //做成单例 ,避免内存泄漏
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static
    {
        //反序列化的时候如果多了其他属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //大小写不敏感标记
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    // Map -> Object
    public static <T> T convertValue(Map map, Class<T> clazz)
    {
        T model=null;
        try
        {
            model=objectMapper.convertValue(map, clazz);
            return model;
        }
        catch (Exception e)
        {
            model=null;
            return model;
        }
        finally
        {
            model=null;
        }
    }

    // List<Map> -> List<Object>
    public static <T> List<T> convertValue(List<Map<String, Object>> datas, final Class<T> clazz)
    {
        List<T> result = new ArrayList<>();
        try
        {
            for(Map map : datas)
                result.add(convertValue(map, clazz));

            return result;
        }
        catch (Exception e)
        {
            return  result;
        }
        finally
        {
            result=null;
        }
    }


    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz)
    {
        T t =null;
        try
        {
            CopyOptions copyOptions = new CopyOptions();
            copyOptions.setIgnoreCase(true);
            t = BeanUtil.toBean(map, clazz, copyOptions);

            copyOptions=null;

            return t;
        }
        catch (Exception e)
        {
            return t;
        }
        finally
        {
            t =null;
        }
    }

    public static <T> List<T> mapsToBeanList(List<Map<String, Object>> maps, Class<T> clazz)
    {
        List<T> tList = new ArrayList<>();
        try
        {
            if (!CollectionUtils.isEmpty(maps))
            {
                CopyOptions copyOptions = new CopyOptions();
                copyOptions.setIgnoreCase(true);

                for (Map<String, Object> map : maps)
                {
                    T t = BeanUtil.toBean(map, clazz, copyOptions);
                    tList.add(t);
                }
            }
            return tList;
        }
        catch (Exception e)
        {
            return tList;
        }
        finally
        {
            tList=null;
        }
    }

    public static String toStr(Object strObj)
    {
        try
        {
            if(strObj == null)
            {
                return "";
            }
            return strObj.toString();
        }
        catch (Exception e)
        {
            return "";
        }
        finally
        {
            strObj=null;
        }
    }
}
