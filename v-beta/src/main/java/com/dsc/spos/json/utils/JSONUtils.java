package com.dsc.spos.json.utils;

import com.dsc.spos.json.JSONFieldRequired;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Date 20241016
 * @Author 11217
 * 用于json对象的非空判断解析
 */
public class JSONUtils {
    static Logger logger = LogManager.getLogger(JSONUtils.class);

    public static String checkRequiredFields(Object bean, StringBuilder builder) {

        if (null == builder) {
            builder = new StringBuilder();
        }
        if (null != bean) {
            Class<?> clazz = bean.getClass();

            List<Field> fields = new ArrayList<>(Arrays.asList(bean.getClass().getDeclaredFields()));
            fields.addAll(Arrays.asList(bean.getClass().getSuperclass().getDeclaredFields()));
            for (Field field : fields) {
                boolean isAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(JSONFieldRequired.class)) {
                        JSONFieldRequired required = field.getDeclaredAnnotation(JSONFieldRequired.class);
                        if (required.required()) {
                            if (null == field.get(bean)) {
                                if (StringUtils.isNotEmpty(required.display())) {
                                    if (builder.indexOf(required.display()) == -1) {
                                        builder.append(required.display()).append("不可为空,");
                                    }
                                } else {
                                    if (builder.indexOf(field.getName()) == -1) {
                                        builder.append(field.getName()).append("不可为空,");
                                    }
                                }

                            } else {
                                if (field.getType() == String.class) {
                                    if (StringUtils.isEmpty(field.get(bean).toString())) {
                                        if (StringUtils.isNotEmpty(required.display())) {
                                            builder.append(required.display()).append("不可为空,");
                                        } else {
                                            builder.append(field.getName()).append("不可为空,");
                                        }
                                    }
                                } else if (field.getType() == List.class) {
                                    List list = (List) field.get(bean);
                                    for (Object o : list) {
                                        checkRequiredFields(o, builder);
                                    }
                                } else {
                                    checkRequiredFields(field.get(bean), builder);
                                }
                            }
                        }
                    }


                } catch (Exception e) {
                    //doNothing 异常就异常了吧，捕获就可以了
                    logger.error("{}{}{}", e.getClass(), e.getMessage(), e.getCause());
                } finally {
                    field.setAccessible(isAccessible);
                }
            }
        }

        return builder.toString();

    }

}
