package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.PostEventInitiatorReportDto;
import com.unibuc.boardmania.dto.event.CreateEventDto;
import com.unibuc.boardmania.dto.event.EventFiltersDto;
import com.unibuc.boardmania.dto.event.JoinEventDto;
import com.unibuc.boardmania.service.EventService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //Cant send get req with request body using axios, that's why this is POST:
    @PostMapping("/page")
    public ResponseEntity<?> getAllEvents(Authentication authentication,
                                          @RequestParam (required = false, defaultValue = "0") Integer pageNumber,
                                          @RequestParam (required = false, defaultValue = "0") Integer pageSize,
                                          @RequestBody EventFiltersDto filters) {
        return new ResponseEntity<>(eventService.getEvents(KeycloakHelper.getUserId(authentication), pageNumber, pageSize, filters), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(Authentication authentication,
                                          @PathVariable Long id) {
        return new ResponseEntity<>(eventService.getEventById(KeycloakHelper.getUserId(authentication), id), HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getEventsOfCurrentUser(Authentication authentication) {
        return new ResponseEntity<>(eventService.getEventsOfCurrentUser(KeycloakHelper.getUserId(authentication)), HttpStatus.OK);
    }

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

    @PatchMapping("/confirm")
    public ResponseEntity<?> confirmParticipation(@RequestParam String token) {
        eventService.confirmParticipation(token);
        return successResponse();
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<?> getParticipants(@PathVariable Long id, Authentication authentication) {
        return new ResponseEntity<>(eventService.getParticipants(id), HttpStatus.OK);
    }

    @PostMapping("/initiator-report")
    public ResponseEntity<?> postEventInitiatorReport(Authentication authentication,
                                                      @RequestBody PostEventInitiatorReportDto postEventInitiatorReportDto) {
        eventService.postEventInitiatorReport(KeycloakHelper.getUserId(authentication), postEventInitiatorReportDto);
        return successResponse();
    }

    @GetMapping("/participant")
    public ResponseEntity<?> getEventsAsParticipant(Authentication authentication){
        return new ResponseEntity<>(eventService.getEventsAsParticipant(KeycloakHelper.getUserId(authentication)), HttpStatus.OK);
    }
}
