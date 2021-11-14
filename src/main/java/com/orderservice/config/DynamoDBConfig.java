package com.orderservice.config;

import com.orderservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.dax.ClusterDaxClient;

import java.io.IOException;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.dax.url}")
    private String daxUrl;

    @Bean
    public AwsCredentials getBasicAwsCredentials(){
        return AwsBasicCredentials.create(accessKey,secretKey);
    }

    @Bean
    @Profile("dax")
    public DynamoDbClient daxClient(AwsCredentials credentials)
            throws IOException {
        return ClusterDaxClient.builder()
                .overrideConfiguration(software.amazon.dax.Configuration.builder()
                .url(daxUrl).region(Region.US_WEST_2).credentialsProvider(
                        StaticCredentialsProvider.create(credentials)).build()).build();
    }

    @Bean
    @Profile("dynamoDB")
    public DynamoDbClient dynamoDbClient(AwsCredentials credentials){
        return DynamoDbClient.builder().region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient){
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @Bean
    public DynamoDbTable<Order> orderDynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient){
        return dynamoDbEnhancedClient.table("Order", TableSchema.fromBean(Order.class));
    }

}
