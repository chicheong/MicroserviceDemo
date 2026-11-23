package com.demo;

import com.demo.config.AsyncSyncConfiguration;
import com.demo.config.DatabaseTestcontainer;
import com.demo.config.ElasticsearchTestConfiguration;
import com.demo.config.ElasticsearchTestContainer;
import com.demo.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        MicroserviceDemoApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        DatabaseTestcontainer.class,
        ElasticsearchTestContainer.class,
        ElasticsearchTestConfiguration.class,
    }
)
public @interface IntegrationTest {
    // 5s is Spring's default https://github.com/spring-projects/spring-framework/blob/main/spring-test/src/main/java/org/springframework/test/web/reactive/server/DefaultWebTestClient.java#L106
    String DEFAULT_TIMEOUT = "PT5S";
    String DEFAULT_ENTITY_TIMEOUT = "PT5S";
}
