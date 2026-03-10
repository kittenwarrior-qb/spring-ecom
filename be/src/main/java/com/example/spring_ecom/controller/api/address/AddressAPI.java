package com.example.spring_ecom.controller.api.address;

import com.example.spring_ecom.controller.api.address.model.AddressResponse;
import com.example.spring_ecom.controller.api.address.model.CreateAddressRequest;
import com.example.spring_ecom.controller.api.address.model.LocationSuggestionResponse;
import com.example.spring_ecom.controller.api.address.model.UpdateAddressRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address", description = "Address management APIs - Manage user delivery addresses")
@RequestMapping("/v1/api/addresses")
@SecurityRequirement(name = "bearerAuth")
public interface AddressAPI {
    
    @GetMapping
    @Operation(
            summary = "Get all addresses of current user",
            description = "Retrieve all delivery addresses of the authenticated user, ordered by default status and creation date"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved addresses",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token"
            )
    })
    ApiResponse<List<AddressResponse>> getUserAddresses(
            @Parameter(hidden = true) Authentication authentication
    );
    
    @GetMapping("/default")
    @Operation(
            summary = "Get default address of current user",
            description = "Retrieve the default delivery address of the authenticated user. Returns 404 if no default address is set."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved default address"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No default address found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<AddressResponse> getDefaultAddress(
            @Parameter(hidden = true) Authentication authentication
    );
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get address by ID",
            description = "Retrieve a specific address by its ID. User can only access their own addresses."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved address"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<AddressResponse> getAddressById(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PostMapping
    @Operation(
            summary = "Create new address",
            description = "Create a new delivery address. If set as default or if it's the first address, it will be marked as default."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<AddressResponse> createAddress(
            @Valid @RequestBody CreateAddressRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Update address",
            description = "Update an existing address. User can only update their own addresses."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<AddressResponse> updateAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete address",
            description = "Soft delete an address. If the deleted address was default, another address will be automatically set as default."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<Void> deleteAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PatchMapping("/{id}/set-default")
    @Operation(
            summary = "Set address as default",
            description = "Mark an address as the default delivery address. Previous default address will be unmarked."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Default address updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<AddressResponse> setDefaultAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @GetMapping("/location-suggestion")
    @Operation(
            summary = "Get location suggestion based on IP address",
            description = "Auto-detect user's location (city, region, country) based on their IP address using ip-api.com. Returns default Vietnam location if detection fails."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Location suggestion retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<LocationSuggestionResponse> getLocationSuggestion(
            @Parameter(hidden = true) HttpServletRequest request
    );
}
