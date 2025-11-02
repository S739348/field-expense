package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class DeviceDeleteRequest {

    @NotEmpty(message = "Device IDs list cannot be empty")
    private List<Long> deviceIds;

    public List<Long> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<Long> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
