package com.aarya.fieldemployee.controller;

import com.aarya.fieldemployee.dtorequest.DeviceCreateRequest;
import com.aarya.fieldemployee.dtorequest.DeviceDeleteRequest;
import com.aarya.fieldemployee.dtorequest.DeviceUpdateRequest;
import com.aarya.fieldemployee.dtoresponse.DeviceResponse;
import com.aarya.fieldemployee.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/create")
    public ResponseEntity<DeviceResponse> createDevice(
            @Valid @RequestBody DeviceCreateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        DeviceResponse response = deviceService.createDevice(request, requestingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    public ResponseEntity<DeviceResponse> updateDevice(
            @Valid @RequestBody DeviceUpdateRequest request,@RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        DeviceResponse response = deviceService.updateDevice(request, requestingUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDevices(
            @Valid @RequestBody DeviceDeleteRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        deviceService.deleteDevices(request, requestingUserId);
        return ResponseEntity.ok("Devices deleted successfully");
    }

    @GetMapping("/show")
    public ResponseEntity<List<DeviceResponse>> showDevices(
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        List<DeviceResponse> response = deviceService.showDevices(requestingUserId);
        return ResponseEntity.ok(response);
    }
}
