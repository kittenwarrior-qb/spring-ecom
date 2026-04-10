package com.example.spring_ecom.controller.api.admin.dashboard;

import com.example.spring_ecom.controller.api.admin.dashboard.model.DashboardResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/v1/api/admin/dashboard")
@Tag(name = "Admin Dashboard", description = "Admin dashboard with aggregated statistics")
public interface AdminDashboardAPI {

    @Operation(summary = "Get dashboard overview", description = "Aggregated statistics: orders, revenue, profit, inventory")
    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @Parameter(description = "Period: daily, weekly, monthly, yearly") @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) String endDate);
}

