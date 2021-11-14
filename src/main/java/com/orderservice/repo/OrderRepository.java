package com.orderservice.repo;

import com.orderservice.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private DynamoDbTable<Order> orderTable;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    public Order getOrder(String customerName,String orderId){
        Order order= new Order();
        order.setCustomerName(customerName);
        order.setOrderId(orderId);
        return orderTable.getItem(order);
    }

    public void saveOrder(Order order){
        orderTable.putItem(order);
    }

    public void updateOrder(Order order){
        orderTable.updateItem(order);
    }

    public void deleteOrder(Order order){
        orderTable.deleteItem(order);
    }

    public List<Order> getOrdersByMerchantName(String merchantName){
        DynamoDbIndex<Order> merchantIndex = orderTable.index("merchantOrder-index");
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(merchantName).build());
        List<Order> orders = new ArrayList<>();
        merchantIndex.query(query).iterator().forEachRemaining(orderPage -> orders.addAll(orderPage.items()));
        return orders;
    }

    public List<Order> getOrders(List<Order> orders){
        ReadBatch.Builder<Order> ordersReadBatch = ReadBatch.builder(Order.class);
        orders.forEach(ordersReadBatch::addGetItem);
        ordersReadBatch.mappedTableResource(orderTable);
        BatchGetItemEnhancedRequest batchGetItemEnhancedRequest = BatchGetItemEnhancedRequest.builder()
                .addReadBatch(ordersReadBatch.build()).build();
        List<Order> results = new ArrayList<>();
        enhancedClient.batchGetItem(batchGetItemEnhancedRequest).resultsForTable(orderTable)
                .iterator().forEachRemaining(results::add);
        return results;
    }

}
