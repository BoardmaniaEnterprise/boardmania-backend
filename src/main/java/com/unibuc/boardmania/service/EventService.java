package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.CreateEventDto;
import com.unibuc.boardmania.dto.JoinEventDto;
import com.unibuc.boardmania.model.*;
import com.unibuc.boardmania.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserEventRepository userEventRepository;
    @Autowired
    private final EventGameRepository eventGameRepository;
    @Autowired
    private final VoteRepository voteRepository;
    @Autowired
    private final GameRepository gameRepository;

    public Event createEvent(CreateEventDto createEventDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        Event event = Event.builder()
                .name(createEventDto.getName())
                .initiator(user)
                .description(createEventDto.getDescription())
                .location(createEventDto.getLocation())
                .dateTime(createEventDto.getDateTime())
                .maxNumberOfPlayers(createEventDto.getMaxNrOfPlayers())
                .minTrustScore(createEventDto.getMinTrustScore())
                .online(createEventDto.isOnline())
                .deleted(false)
                .build();

        Event savedEvent = eventRepository.save(event);

        createEventDto.getGameIds().forEach(gameId -> {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new NotFoundException("Game with id " + gameId + "not found!"));
            EventGame eventGame = EventGame.builder()
                    .game(game)
                    .deleted(false)
                    .event(savedEvent)
                    .votes(List.of())
                    .build();
            eventGameRepository.save(eventGame);
        });
        user.getCreatedEvents().add(savedEvent);

        return event;
    }

    @Transactional
    public void joinEvent(JoinEventDto joinEventDto, Long eventId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found!"));
        if (userEventRepository.findByEventIdAndUserId(eventId, userId).isPresent()) {
            throw new BadRequestException("User already joined this event!");
        }
        if (user.getTrustScore() < event.getMinTrustScore()) {
            throw new BadRequestException("Trust score too low to take part in this event.");
        }

        UserEvent userEvent = UserEvent.builder()
                .event(event)
                .user(user)
                .confirmed(false)
                .deleted(false)
                .build();
        userEventRepository.save(userEvent);

        joinEventDto.getVoteDtoList().forEach(voteDto -> {
            Long gameId = voteDto.getGameId();
            EventGame eventGame = eventGameRepository.findByEventIdAndGameId(eventId, gameId)
                    .orElseThrow(() -> new NotFoundException("EventGame not found!"));
            Vote vote = Vote.builder()
                    .eventGame(eventGame)
                    .user(user)
                    .deleted(false)
                    .build();
            voteRepository.save(vote);
        });
    }

    @Transactional
    public void pickGame(Long eventId, Long gameId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found!"));
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Game not found!"));

        if (event.getInitiator().getId() != user.getId()) {
            throw new BadRequestException("Only the initiator can pick a game for the event!");
        }
        if (eventGameRepository.findByEventIdAndGameId(eventId, gameId).isEmpty()) {
            throw new NotFoundException("Game unavailable in this event!");
        }
        if (event.getPickedGame() != null) {
            throw new BadRequestException(String.format("The game %s has already been picked in this event!",
                    event.getPickedGame().getName()));
        }
        Integer numOfVotesForGame = eventGameRepository.countVotes(eventId, gameId);
        if (numOfVotesForGame < game.getMinNumberOfPlayers()) {
            throw new BadRequestException("Game does not meet necessary number of players!");
        }
        event.setPickedGame(game);
        eventRepository.save(event);
    }
}
