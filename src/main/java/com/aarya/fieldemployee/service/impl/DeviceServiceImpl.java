package com.aarya.fieldemployee.service.impl;

import com.aarya.fieldemployee.dtorequest.DeviceCreateRequest;
import com.aarya.fieldemployee.dtorequest.DeviceDeleteRequest;
import com.aarya.fieldemployee.dtorequest.DeviceUpdateRequest;
import com.aarya.fieldemployee.dtoresponse.DeviceResponse;
import com.aarya.fieldemployee.model.Device;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.repository.DeviceRepository;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import com.aarya.fieldemployee.service.DeviceService;
import com.aarya.fieldemployee.util.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Authorized authorized;

    @Override
    @Transactional
    public DeviceResponse createDevice(DeviceCreateRequest request, UUID requestingUserId) {

        Employee employee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Device device = new Device();
        device.setEmployee(employee);
        device.setDeviceId(request.getDeviceId());
        device.setModel(request.getModel());
        device.setOsVersion(request.getOsVersion());
        device = deviceRepository.save(device);
        return convertToResponse(device);
    }

    @Override
    @Transactional
    public DeviceResponse updateDevice(DeviceUpdateRequest request, UUID requestingUserId) {

        Device device = deviceRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Device not found"));


        if (requestingUserId != null) {
            Employee employee = employeeRepository.findById(requestingUserId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            device.setEmployee(employee);
        }
         device.setIsActive(false);
        device = deviceRepository.save(device);
        return convertToResponse(device);
    }

    @Override
    @Transactional
    public void deleteDevices(DeviceDeleteRequest request, UUID requestingUserId) {
        authorized.validateAuthorization(requestingUserId);
        deviceRepository.deleteAllById(request.getDeviceIds());
    }

    @Override
    public List<DeviceResponse> showDevices(UUID requestingUserId) {
        authorized.validateAuthorization(requestingUserId);
        List<Device> devices = deviceRepository.findAll();
        return devices.stream().map(this::convertToResponse).toList();
    }

    private DeviceResponse convertToResponse(Device device) {
        DeviceResponse response = new DeviceResponse();
        response.setId(device.getId());
        response.setEmployeeId(device.getEmployee().getEmployeeId());
        response.setEmployeeName(device.getEmployee().getName());
        response.setDeviceId(device.getDeviceId());
        response.setModel(device.getModel());
        response.setOsVersion(device.getOsVersion());
        response.setIsActive(device.getIsActive());
        response.setCreatedAt(device.getCreatedAt());
        return response;
    }
}
