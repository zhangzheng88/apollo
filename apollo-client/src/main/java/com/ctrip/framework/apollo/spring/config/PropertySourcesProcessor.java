package com.ctrip.framework.apollo.spring.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloValueProcessor;
import com.ctrip.framework.apollo.spring.auto.SpringValue;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/**
 * Apollo Property Sources processor for Spring Annotation Based Application. <br /> <br />
 *
 * The reason why PropertySourcesProcessor implements {@link BeanFactoryPostProcessor} instead of
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor} is that
 * lower versions of Spring (e.g. 3.1.1) doesn't support registering
 * BeanDefinitionRegistryPostProcessor in ImportBeanDefinitionRegistrar - {@link
 * com.ctrip.framework.apollo.spring.annotation.ApolloConfigRegistrar}
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware,
    PriorityOrdered {

  private static final String APOLLO_PROPERTY_SOURCE_NAME = "ApolloPropertySources";
  private static final Multimap<Integer, String> NAMESPACE_NAMES = HashMultimap.create();

  private ConfigurableEnvironment environment;

  public static boolean addNamespaces(Collection<String> namespaces, int order) {
    return NAMESPACE_NAMES.putAll(order, namespaces);
  }

  //only for test
  private static void reset() {
    NAMESPACE_NAMES.clear();
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    initializePropertySources();
  }

  protected void initializePropertySources() {
    if (environment.getPropertySources().contains(APOLLO_PROPERTY_SOURCE_NAME)) {
      //already initialized
      return;
    }
    CompositePropertySource composite = new CompositePropertySource(APOLLO_PROPERTY_SOURCE_NAME);

    //sort by order asc
    ImmutableSortedSet<Integer> orders = ImmutableSortedSet.copyOf(NAMESPACE_NAMES.keySet());
    Iterator<Integer> iterator = orders.iterator();

    while (iterator.hasNext()) {
      int order = iterator.next();
      for (String namespace : NAMESPACE_NAMES.get(order)) {
        Config config = ConfigService.getConfig(namespace);
        config.addChangeListener(new ConfigChangeListener() {
          @Override
          public void onChange(ConfigChangeEvent changeEvent) {
            Set<String> keys = changeEvent.changedKeys();
            if (CollectionUtils.isEmpty(keys)) {
              return;
            }
            for (String k : keys) {
              ConfigChange configChange = changeEvent.getChange(k);
              Collection<SpringValue> targetValues = ApolloValueProcessor.monitor().get(k);
              if (targetValues == null || targetValues.isEmpty()) {
                return;
              }
              for (SpringValue val : targetValues) {
                val.updateVal(configChange.getNewValue());
              }
            }
          }
        });
        composite.addPropertySource(new ConfigPropertySource(namespace, config));
      }
    }
    environment.getPropertySources().addFirst(composite);
  }

  @Override
  public void setEnvironment(Environment environment) {
    //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
    this.environment = (ConfigurableEnvironment) environment;
  }

  @Override
  public int getOrder() {
    //make it as early as possible
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
