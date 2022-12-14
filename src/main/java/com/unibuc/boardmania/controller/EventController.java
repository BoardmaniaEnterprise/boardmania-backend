package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.CreateEventDto;
import com.unibuc.boardmania.dto.JoinEventDto;
import com.unibuc.boardmania.service.EventService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.unibuc.boardmania.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EventController {

    @Autowired
    private final EventService eventService;

//    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDto createEventDto, Authentication authentication) {
        return new ResponseEntity<>(eventService.createEvent(createEventDto, KeycloakHelper.getUserId(authentication)),
                HttpStatus.OK);
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<?> joinEvent(@RequestBody JoinEventDto joinEventDto, @PathVariable Long id,
                                       Authentication authentication) {
        eventService.joinEvent(joinEventDto, id, KeycloakHelper.getUserId(authentication));
        return successResponse();
    }

    @PostMapping("/pickGame/{eventId}/{gameId}")
    public ResponseEntity<?> pickGame(@PathVariable Long eventId, @PathVariable Long gameId, Authentication authentication) {
        eventService.pickGame(eventId, gameId, KeycloakHelper.getUserId(authentication));
        return successResponse();
    }
}
