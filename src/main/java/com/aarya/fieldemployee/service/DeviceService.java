package com.aarya.fieldemployee.service;

import com.aarya.fieldemployee.dtorequest.DeviceCreateRequest;
import com.aarya.fieldemployee.dtorequest.DeviceDeleteRequest;
import com.aarya.fieldemployee.dtorequest.DeviceUpdateRequest;
import com.aarya.fieldemployee.dtoresponse.DeviceResponse;
import java.util.List;
import java.util.UUID;

public interface DeviceService {
    DeviceResponse createDevice(DeviceCreateRequest request, UUID requestingUserId);
    DeviceResponse updateDevice(DeviceUpdateRequest request, UUID requestingUserId);
    void deleteDevices(DeviceDeleteRequest request, UUID requestingUserId);
    List<DeviceResponse> showDevices(UUID requestingUserId);
}
