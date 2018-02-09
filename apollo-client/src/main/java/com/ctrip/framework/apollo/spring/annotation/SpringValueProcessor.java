package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.spring.auto.SpringValue;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Create by zhangzheng on 2018/2/7 该类用来实现@Value注入的apollo配置信息的自动注入
 */
public class SpringValueProcessor extends ApolloProcessor {

  private static Boolean isAutoUpdate = true;//自动更新开关
  private Pattern pattern = Pattern.compile("\\$\\{(.*?)(:(.*?))?}");
  private Logger logger = LoggerFactory.getLogger(SpringValueProcessor.class);

  public static void setAutoUpdate(Boolean autoUpdate) {
    isAutoUpdate = autoUpdate;
  }

  @Override
  protected void processField(Object bean, Field field) {
    if (!isAutoUpdate) {
      return;
    }
    Value value = AnnotationUtils.getAnnotation(field, Value.class);
    if (value == null) {
      return;
    }
    Matcher matcher = pattern.matcher(value.value());
    Preconditions.checkArgument(matcher.matches(),
        String.format("the apollo value annotation for field:%s is not correct," +
            "please use ${somekey} or ${someKey:someDefaultValue} pattern", field.getType()));
    String key = matcher.group(1);

    ApolloValueProcessor.monitor().put(key, SpringValue.create(bean, field));
    logger.info("Listening apollo key = {}", key);

    super.processField(bean, field);
  }

  @Override
  protected void processMethod(Object bean, Method method) {
    if (!isAutoUpdate) {
      return;
    }
    Value value = AnnotationUtils.getAnnotation(method, Value.class);
    if (value == null) {
      return;
    }
    Matcher matcher = pattern.matcher(value.value());
    Preconditions.checkArgument(matcher.matches(),
        String.format("the apollo value annotation for field:%s is not correct," +
            "please use ${somekey} or ${someKey:someDefaultValue} pattern", method.getName()));
    String key = matcher.group(1);

    ApolloValueProcessor.monitor().put(key, SpringValue.create(bean, method));
    logger.info("Listening apollo key = {}", key);

    super.processMethod(bean, method);
  }
}
