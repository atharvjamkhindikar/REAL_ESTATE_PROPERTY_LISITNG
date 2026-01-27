package com.realestate.service;

import com.realestate.dto.BuilderGroupRequest;
import com.realestate.dto.BuilderGroupResponse;
import com.realestate.exception.DuplicateResourceException;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.BuilderGroup;
import com.realestate.repository.BuilderGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuilderGroupService {

    @Autowired
    private BuilderGroupRepository builderGroupRepository;

    public List<BuilderGroupResponse> getAllBuilderGroups() {
        return builderGroupRepository.findAllByOrderByNameAsc().stream()
                .map(this::toBuilderGroupResponse)
                .collect(Collectors.toList());
    }

    public List<BuilderGroupResponse> getActiveBuilderGroups() {
        return builderGroupRepository.findByActive(true).stream()
                .map(this::toBuilderGroupResponse)
                .collect(Collectors.toList());
    }

    public Optional<BuilderGroupResponse> getBuilderGroupById(Long id) {
        return builderGroupRepository.findById(id)
                .map(this::toBuilderGroupResponse);
    }

    public BuilderGroupResponse createBuilderGroup(BuilderGroupRequest request) {
        // Check if builder group with same name already exists
        if (builderGroupRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new DuplicateResourceException("BuilderGroup", "name", request.getName());
        }

        BuilderGroup builderGroup = new BuilderGroup();
        builderGroup.setName(request.getName());
        builderGroup.setDescription(request.getDescription());
        builderGroup.setActive(request.getActive() != null ? request.getActive() : true);

        BuilderGroup savedBuilderGroup = builderGroupRepository.save(builderGroup);
        return toBuilderGroupResponse(savedBuilderGroup);
    }

    public BuilderGroupResponse updateBuilderGroup(Long id, BuilderGroupRequest request) {
        BuilderGroup builderGroup = builderGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BuilderGroup", "id", id));

        // Check if new name already exists (and it's not the same group)
        Optional<BuilderGroup> existingGroup = builderGroupRepository.findByNameIgnoreCase(request.getName());
        if (existingGroup.isPresent() && !existingGroup.get().getId().equals(id)) {
            throw new DuplicateResourceException("BuilderGroup", "name", request.getName());
        }

        builderGroup.setName(request.getName());
        builderGroup.setDescription(request.getDescription());
        if (request.getActive() != null) {
            builderGroup.setActive(request.getActive());
        }

        BuilderGroup updatedBuilderGroup = builderGroupRepository.save(builderGroup);
        return toBuilderGroupResponse(updatedBuilderGroup);
    }

    public void deleteBuilderGroup(Long id) {
        if (!builderGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("BuilderGroup", "id", id);
        }
        builderGroupRepository.deleteById(id);
    }

    public BuilderGroupResponse toBuilderGroupResponse(BuilderGroup builderGroup) {
        return BuilderGroupResponse.builder()
                .id(builderGroup.getId())
                .name(builderGroup.getName())
                .description(builderGroup.getDescription())
                .active(builderGroup.getActive())
                .createdAt(builderGroup.getCreatedAt())
                .updatedAt(builderGroup.getUpdatedAt())
                .propertyCount(builderGroup.getProperties() != null ? builderGroup.getProperties().size() : 0)
                .build();
    }
}
