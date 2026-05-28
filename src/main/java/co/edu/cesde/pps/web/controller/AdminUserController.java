package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.AdminUserApplicationService;
import co.edu.cesde.pps.web.dto.request.CreateAdminUserRequest;
import co.edu.cesde.pps.web.dto.request.UpdateAdminUserRequest;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.security.AdminAccessGuard;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.ADMIN_USERS)
public class AdminUserController {

    private final AdminUserApplicationService adminUserApplicationService;
    private final AdminAccessGuard adminAccessGuard;

    public AdminUserController(AdminUserApplicationService adminUserApplicationService,
                               AdminAccessGuard adminAccessGuard) {
        this.adminUserApplicationService = adminUserApplicationService;
        this.adminAccessGuard = adminAccessGuard;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody CreateAdminUserRequest request) {
        adminAccessGuard.requireAdmin(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminUserApplicationService.createUser(request));
    }

    @GetMapping
    public List<UserResponse> listUsers(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        adminAccessGuard.requireAdmin(auth);
        return adminUserApplicationService.listUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id) {
        adminAccessGuard.requireAdmin(auth);
        return adminUserApplicationService.getUser(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminUserRequest request) {
        adminAccessGuard.requireAdmin(auth);
        return adminUserApplicationService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Long id) {
        adminAccessGuard.requireAdmin(auth);
        adminUserApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
