package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.CreateEventDto;
import com.unibuc.boardmania.service.EventService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EventController {
    private final EventService eventService;

//    @PostMapping("/new")
//    public ResponseEntity<?> createEvent(CreateEventDto createEventDto, Authentication authentication) {
//        eventService.createEvent(createEventDto, KeycloakHelper.getUserId(authentication));
//    }


}
