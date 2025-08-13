package com.anyspotsleft.anyspotsleft;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatProperties {
    @Bean
    TomcatConnectorCustomizer connectorCustomizer() {
        return (connector) -> {
            connector.setMaxPartCount(10);
            connector.setMaxPartHeaderSize(1024);
        };
    }
}
