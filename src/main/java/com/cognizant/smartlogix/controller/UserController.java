package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateUserRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.dto.response.UserResponse;
import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.model.enums.UserStatus;
import com.cognizant.smartlogix.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User account management for all roles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created", userService.create(request)));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.findById(id)));
    }

    @Operation(summary = "Get user by email")
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserResponse>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(userService.findByEmail(email)));
    }

    @Operation(summary = "List users (paginated, optional role filter)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UserRole role) {
        PageRequest pr = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(role != null
                ? PagedResponse.of(userService.findByRole(role, pr))
                : PagedResponse.of(userService.findAll(pr))));
    }

    @Operation(summary = "Update user status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam UserStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", userService.updateStatus(id, status)));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }
}

