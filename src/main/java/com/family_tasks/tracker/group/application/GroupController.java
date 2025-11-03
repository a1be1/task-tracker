package com.family_tasks.tracker.group.application;

import com.family_tasks.tracker.group.core.GroupCreateService;
import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Group management", description = "Operations for managing groups")
public class GroupController {

    public static final String GROUP_URL = "/v1/groups";

    private final GroupCreateService groupCreateService;

    @Operation(
            summary = "Create a new group",
            description = """
                    Creates a new group in the system.
                    
                    **Rules and constraints:**
                    - A user cannot own more than one group.
                    - A user can be a member of only one group.
                    - A user without a group cannot create or edit tasks.
                    """
    )
    @PostMapping(GROUP_URL)
    public GroupApiResponse createGroup(@Valid @RequestBody GroupCreateApiRequest apiRequest) {
        return groupCreateService.createGroup(apiRequest);
    }
}