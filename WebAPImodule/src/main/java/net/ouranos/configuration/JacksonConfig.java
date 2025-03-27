package net.ouranos.configuration;

import java.time.OffsetDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    // JacksonのObjectMapperをカスタマイズ
    SimpleModule module = new SimpleModule();
    
    // OffsetDateTimeのシリアライズとデシリアライズにカスタムシリアライザーとデシリアライザーを適用
    module.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
    module.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
    
    // Jackson2ObjectMapperBuilderを使ってObjectMapperを作成
    return Jackson2ObjectMapperBuilder.json()
            .modules(new JavaTimeModule(), module)  // JavaTimeModuleも追加して日付・時間をサポート
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)  // タイムスタンプではなくISO 8601形式にする
            .build()
            .setSerializationInclusion(Include.NON_NULL)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  }
}