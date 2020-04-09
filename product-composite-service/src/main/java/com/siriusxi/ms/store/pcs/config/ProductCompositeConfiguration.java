package com.siriusxi.ms.store.pcs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
public class ProductCompositeConfiguration {
    @Value("${api.common.version}")
    String apiVersion;
    @Value("${api.common.title}")
    String apiTitle;
    @Value("${api.common.description}")
    String apiDescription;
    @Value("${api.common.termsOfServiceUrl}")
    String apiTermsOfServiceUrl;
    @Value("${api.common.license}")
    String apiLicense;
    @Value("${api.common.licenseUrl}")
    String apiLicenseUrl;
    @Value("${api.common.contact.name}")
    String apiContactName;
    @Value("${api.common.contact.url}")
    String apiContactUrl;
    @Value("${api.common.contact.email}")
    String apiContactEmail;

    @Bean
    RestTemplate newRestClient() {
        return new RestTemplate();
    }

    /**
     * Will exposed on $HOST:$PORT/swagger-ui.html
     *
     * @return Docket swagger configuration
     */
    @Bean
    public Docket apiDocumentation() {

        return new Docket(SWAGGER_2)
                .select()
                /*
                    Using the apis() and paths() methods,
                    we can specify where SpringFox shall look for API documentation.
                 */
                .apis(basePackage("com.siriusxi.ms.store.pcs"))
                .paths(PathSelectors.any())
                .build()
                /*
                    Using the globalResponseMessage() method, we ask SpringFox not to add any default HTTP response codes to the API documentation, such as 401 and 403, which we don't currently use.
                 */
                .globalResponseMessage(GET, emptyList())
                /*
                    The api* variables that are used to configure the Docket bean with general information about the API are initialized from the property file using Spring @Value annotations.
                 */
                .apiInfo(new ApiInfo(
                        apiTitle,
                        apiDescription,
                        apiVersion,
                        apiTermsOfServiceUrl,
                        new Contact(apiContactName, apiContactUrl, apiContactEmail),
                        apiLicense,
                        apiLicenseUrl,
                        emptyList()
                ));
    }
}
