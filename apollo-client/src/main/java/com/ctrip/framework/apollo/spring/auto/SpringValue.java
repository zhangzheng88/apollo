package com.ctrip.framework.apollo.spring.auto;

import com.ctrip.framework.apollo.util.function.Functions;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Spring @Value field or method info
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2017/12/20.
 */
public class SpringValue {
    private Object bean;
    private Field field;
    private Method method;
    private boolean isField = false;
    private String className;
    private String fieldName;
    private String valKey;
    private Function<String, ?> parser;
    private Logger logger = LoggerFactory.getLogger(SpringValue.class);

    private SpringValue(String key,Object ins, Field field) {
        this.bean = ins;
        this.className = ins.getClass().getName();
        this.fieldName = field.getName();
        this.field = field;
        this.isField = true;
        this.parser = findParser(field.getType());
        this.valKey = key;
    }

    private SpringValue(String key,Object ins, Method method) {
        this.bean = ins;
        this.method = method;
        this.className = ins.getClass().getName();
        this.fieldName = method.getName() + "(*)";
        Class<?>[] paramTps = method.getParameterTypes();
        if (paramTps.length != 1){
            logger.error("invalid setter,can not update in {}.{}",className,fieldName);
            return;
        }
        this.parser = findParser(paramTps[0]);
        this.valKey = key;
    }

    public static SpringValue create(String key,Object ins, Field field){
        return new SpringValue(key,ins,field);
    }

    public static SpringValue create(String key,Object ins, Method method){
        return new SpringValue(key,ins,method);
    }

    public void updateVal(String newVal){
        try {
            if (isField){
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(bean,parseVal(newVal));
                field.setAccessible(accessible);
            }else {
                Class<?>[] paramTps = method.getParameterTypes();
                if (paramTps.length != 1){
                    logger.error("invalid setter ,can not update key={} val={} in {}.{}",valKey,newVal,className,fieldName);
                    return;
                }
                method.invoke(bean,parseVal(newVal));
            }
            logger.info("auto update apollo changed value, key={}, newVal={} in {}.{}",valKey,newVal,className,fieldName);
        }catch (Exception e){
            logger.error("update field {}.{} fail with new val={},key = {}, msg = {}",className,fieldName,newVal,valKey,e.getMessage());
        }
    }

    private Object parseVal(String newVal){
        if (parser == null){
            return newVal;
        }
        return parser.apply(newVal);
    }

    private Function<String, ?> findParser(Class<?> targetType) {
        Function<String, ?> res = null;
        if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            res = Functions.TO_INT_FUNCTION;
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            res = Functions.TO_LONG_FUNCTION;
        }  else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            res = Functions.TO_BOOLEAN_FUNCTION;
        } else if (targetType.equals(Date.class)) {
            res = Functions.TO_DATE_FUNCTION;
        }else if (targetType.equals(short.class) || targetType.equals(Short.class)){
            res = Functions.TO_SHORT_FUNCTION;
        }else if (targetType.equals(double.class) || targetType.equals(Double.class)){
            res = Functions.TO_DOUBLE_FUNCTION;
        }else if (targetType.equals(float.class) || targetType.equals(Float.class)){
            res = Functions.TO_FLOAT_FUNCTION;
        }else if (targetType.equals(byte.class) || targetType.equals(Byte.class)){
            res = Functions.TO_BYTE_FUNCTION;
        }
        return res;
    }

}
