package com.example.spring_ecom.controller.api.admin.statistics.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.statistics.DashboardSummary;
import com.example.spring_ecom.domain.statistics.RevenueByCategoryItem;
import com.example.spring_ecom.domain.statistics.RevenueByPeriod;
import com.example.spring_ecom.domain.statistics.TopSellingProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructGlobalConfig.class)
public interface StatisticsResponseMapper {

    @Mapping(source = "completedOrders", target = "deliveredOrders")
    @Mapping(source = "avgOrderValue", target = "averageOrderValue")
    StatisticsDashboardResponse toResponse(DashboardSummary domain);

    RevenueByPeriodResponse toResponse(RevenueByPeriod domain);

    List<RevenueByPeriodResponse> toRevenueByPeriodResponseList(List<RevenueByPeriod> domains);

    TopSellingProductResponse toResponse(TopSellingProduct domain);

    List<TopSellingProductResponse> toTopSellingProductResponseList(List<TopSellingProduct> domains);

    RevenueByCategoryResponse toResponse(RevenueByCategoryItem domain);

    List<RevenueByCategoryResponse> toRevenueByCategoryResponseList(List<RevenueByCategoryItem> domains);
}

