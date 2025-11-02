package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotBlank;

public class DeviceCreateRequest {
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    @NotBlank(message = "Device Model is required")
    private String model;
    @NotBlank(message = "Device OsVersion is required")
    private String osVersion;

    public @NotBlank(message = "Device ID is required") String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@NotBlank(message = "Device ID is required") String deviceId) {
        this.deviceId = deviceId;
    }

    public @NotBlank(message = "Device Model is required") String getModel() {
        return model;
    }

    public void setModel(@NotBlank(message = "Device Model is required") String model) {
        this.model = model;
    }

    public @NotBlank(message = "Device OsVersion is required") String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(@NotBlank(message = "Device OsVersion is required") String osVersion) {
        this.osVersion = osVersion;
    }
}
