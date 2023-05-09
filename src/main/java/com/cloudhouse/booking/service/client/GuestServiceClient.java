package com.cloudhouse.booking.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("guest-service")
public interface GuestServiceClient {



}
