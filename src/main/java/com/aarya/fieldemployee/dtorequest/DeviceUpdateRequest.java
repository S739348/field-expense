package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotNull;

public class DeviceUpdateRequest {

    @NotNull(message = "Status is required")
    private Boolean status;

    @NotNull(message = "ID is required")
    private Long id;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
