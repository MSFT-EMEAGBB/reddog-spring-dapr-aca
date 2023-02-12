package com.microsoft.gbb.reddog.accountingservice.controller;

import com.microsoft.gbb.reddog.accountingservice.dto.ChartKeyValue;
import com.microsoft.gbb.reddog.accountingservice.dto.OrderSummaryDto;
import com.microsoft.gbb.reddog.accountingservice.dto.OrdersTimeSeries;
import com.microsoft.gbb.reddog.accountingservice.service.AccountingService;

import io.dapr.Topic;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountingController {

    private final AccountingService accountingService;
    private final String pubSubName = "reddog.pubsub";
    private final String orderTopic = "orders";
    private final String orderCompletedTopic = "ordercompleted";

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    /// Pub sub on Order Topic, needs to save the order to state store
    @Topic(name = orderTopic, pubsubName = pubSubName)
    @PostMapping(value = "/orders")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<OrderSummaryDto>> getAllInFlightOrders() {
        return ResponseEntity.ok(accountingService.findAllInflightOrders());
    }

    /// Pub sub on Order Completed Topic, needs to mark the order as complete and save the order to state store
    @Topic(name = orderCompletedTopic, pubsubName = pubSubName)
    @PostMapping(value = "/orders/completed")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<OrderSummaryDto>> getAllCompletedOrders() {
        return ResponseEntity.ok(accountingService.findAllCompletedOrders());
    }

    @GetMapping(value = "/orders/{period}/{timeSpan}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<OrdersTimeSeries> getOrdersByPeriod(@PathVariable String period,
                                                                    @PathVariable String timeSpan,
                                                                    @RequestHeader("store-id") String storeId) {
        return ResponseEntity.ok(accountingService.getOrderCountOverTime(period, timeSpan, storeId));
    }

    @GetMapping(value = "/orders/{period}/{timeStart}/{timeEnd}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<OrdersTimeSeries> getOrderCountWithinTimeInterval(@PathVariable String period,
                                                              @PathVariable String timeStart,
                                                                @PathVariable String timeEnd,
                                                              @RequestHeader("store-id") String storeId) {
        return ResponseEntity.ok(accountingService.getOrderCountWithinTimeInterval(period, timeStart, timeEnd, storeId));
    }

    @GetMapping(value = "/orders/day")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ChartKeyValue<Long>>> getOrdersGroupedByDay(
            @RequestHeader(value = "store-id", required = false) String storeId) {
        return ResponseEntity.ok(accountingService.getOrderCountByDay(storeId));
    }
}
