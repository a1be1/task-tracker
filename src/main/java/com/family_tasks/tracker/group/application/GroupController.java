package com.family_tasks.tracker.group.application;

import com.family_tasks.tracker.group.core.GroupCreateService;
import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    public static final String GROUP_URL = "/v1/groups";

    private final GroupCreateService groupCreateService;

    @PostMapping(GROUP_URL)
    public GroupApiResponse createGroup(@Valid @RequestBody GroupCreateApiRequest apiRequest) {
        return groupCreateService.createGroup(apiRequest);
    }
}