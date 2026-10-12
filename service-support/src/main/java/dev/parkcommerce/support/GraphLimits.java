package dev.parkcommerce.support;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import org.springframework.context.annotation.*;
@Configuration(proxyBeanMethods=false)
public class GraphLimits {
    @Bean MaxQueryDepthInstrumentation graphDepth(){return new MaxQueryDepthInstrumentation(8);}
    @Bean MaxQueryComplexityInstrumentation graphComplexity(){return new MaxQueryComplexityInstrumentation(200);}
}
