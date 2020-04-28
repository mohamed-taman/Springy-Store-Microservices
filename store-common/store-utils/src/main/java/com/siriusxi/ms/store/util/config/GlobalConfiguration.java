package com.siriusxi.ms.store.util.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Class <code>GlobalConfiguration</code> holds all configurations that used by all the system
 * services.
 *
 * @author mohamed.taman
 * @version v4.6
 * @since v0.1
 */
@Configuration
public class GlobalConfiguration {

  /**
   * This bean is for Java 14 record to be serialized as JSON
   *
   * @return Jackson2ObjectMapperBuilderCustomizer builder
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder ->
        builder
            .visibility(
                    PropertyAccessor.FIELD,
                    JsonAutoDetect.Visibility.ANY);
  }
}
