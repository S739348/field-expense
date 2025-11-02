package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class TaskDeleteRequest {

    @NotEmpty(message = "Task IDs list cannot be empty")
    private List<UUID> taskIds;

    public List<UUID> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<UUID> taskIds) {
        this.taskIds = taskIds;
    }
}
