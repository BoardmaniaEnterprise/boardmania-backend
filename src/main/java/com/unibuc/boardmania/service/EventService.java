package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.CreateEventDto;
import com.unibuc.boardmania.dto.JoinEventDto;
import com.unibuc.boardmania.model.*;
import com.unibuc.boardmania.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            eventGame = eventGameRepository.save(eventGame);

            savedEvent.getEventGames().add(eventGame);
            game.getEventGames().add(eventGame);
        });
        user.getCreatedEvents().add(savedEvent);

        return event;
    }

    public void joinEvent(JoinEventDto joinEventDto, Long eventId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found!"));
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
            vote = voteRepository.save(vote);
            eventGame.getVotes().add(vote);
        });
    }
}
