package com.aarya.fieldemployee.service;

import com.aarya.fieldemployee.dtorequest.TaskCreateRequest;
import com.aarya.fieldemployee.dtorequest.TaskUpdateRequest;
import com.aarya.fieldemployee.dtorequest.TaskDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.TaskResponse;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request, UUID requestingUserId);
    TaskResponse updateTask(TaskUpdateRequest request, UUID requestingUserId);
    void deleteTasks(TaskDeleteRequest request, UUID requestingUserId);
    List<TaskResponse> showTasks(UUID requestingUserId,String range);
}
