package com.sky.properties;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置参数类：将配置文件中的参数读取出来，赋值给类中的属性
 */

@ConfigurationProperties(prefix = "sky.alioss")
@Data
@Component
public class AliOssProperties {

    private String endpoint;
//    private String accessKeyId;
//    private String accessKeySecret;
    private String bucketName;


}
