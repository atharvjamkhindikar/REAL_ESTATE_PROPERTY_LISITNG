package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.BuilderGroupRequest;
import com.realestate.dto.BuilderGroupResponse;
import com.realestate.service.BuilderGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/builder-groups")
@CrossOrigin(origins = "http://localhost:3000")
public class BuilderGroupController {

    @Autowired
    private BuilderGroupService builderGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BuilderGroupResponse>>> getAllBuilderGroups() {
        List<BuilderGroupResponse> builderGroups = builderGroupService.getAllBuilderGroups();
        return ResponseEntity.ok(ApiResponse.success(builderGroups));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BuilderGroupResponse>>> getActiveBuilderGroups() {
        List<BuilderGroupResponse> builderGroups = builderGroupService.getActiveBuilderGroups();
        return ResponseEntity.ok(ApiResponse.success(builderGroups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuilderGroupResponse>> getBuilderGroupById(@PathVariable Long id) {
        Optional<BuilderGroupResponse> builderGroup = builderGroupService.getBuilderGroupById(id);
        if (builderGroup.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(builderGroup.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Builder group not found"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BuilderGroupResponse>> createBuilderGroup(
            @Valid @RequestBody BuilderGroupRequest request) {
        try {
            BuilderGroupResponse builderGroup = builderGroupService.createBuilderGroup(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Builder group created successfully", builderGroup));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuilderGroupResponse>> updateBuilderGroup(
            @PathVariable Long id,
            @Valid @RequestBody BuilderGroupRequest request) {
        try {
            BuilderGroupResponse builderGroup = builderGroupService.updateBuilderGroup(id, request);
            return ResponseEntity.ok(ApiResponse.success("Builder group updated successfully", builderGroup));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBuilderGroup(@PathVariable Long id) {
        try {
            builderGroupService.deleteBuilderGroup(id);
            return ResponseEntity.ok(ApiResponse.success("Builder group deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
