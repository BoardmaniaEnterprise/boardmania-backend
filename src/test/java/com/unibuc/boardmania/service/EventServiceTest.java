package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.CreateEventDto;
import com.unibuc.boardmania.dto.JoinEventDto;
import com.unibuc.boardmania.dto.VoteDto;
import com.unibuc.boardmania.model.*;
import com.unibuc.boardmania.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    //TODO: Verify number of repo calls for methods containing forEach
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventRepository userEventRepository;

    @Mock
    private EventGameRepository eventGameRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private GameRepository gameRepository;

    // Entities
    private Event event;
    private User user;
    private Game game;
    private EventGame eventGame;
    // Dtos
    private CreateEventDto createEventDto;
    private JoinEventDto joinEventDto;

    @BeforeEach
    public void setup() {
        // Entities
        user = User.builder()
                .id(1L)
                .createdEvents(new ArrayList<>())
                .trustScore(0.5)
                .build();

        event = Event.builder()
                .id(1L)
                .initiator(user)
                .minTrustScore(0.5)
                .build();

        game = Game.builder()
                .build();

        eventGame = EventGame.builder()
                .build();

        // Dtos
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(1L);

        createEventDto = CreateEventDto.builder()
                .gameIds(gameIds)
                .build();

        joinEventDto = JoinEventDto.builder()
                .build();
    }

    @Test
    @DisplayName("Create event, expected success")
    public void createEvent() {
        //having
        Event expectedResponse = Event.builder()
                .deleted(false)
                .initiator(user)
                .build();
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(gameRepository.findById(createEventDto.getGameIds().get(0))).thenReturn(Optional.ofNullable(game));


        Event responseEvent = eventService.createEvent(createEventDto, 1L);


        verify(userRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
        verify(gameRepository).findById(createEventDto.getGameIds().get(0));
        verify(eventGameRepository).save(any(EventGame.class));
        Assertions.assertEquals(expectedResponse, responseEvent);
    }

    @Test
    @DisplayName("Create event, expected NotFoundException(User not found)")
    public void createEventUserNotFound() {
        //having
        CreateEventDto createEventDto = new CreateEventDto();

        //when
        when(userRepository.findById(1L)).thenThrow((new NotFoundException("User not found!")));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.createEvent(createEventDto, 1L);
        });


        verify(userRepository).findById(1L);
        Assertions.assertEquals("User not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Create event, expected NotFoundException(Game not found)")
    public void createEventGameNotFound() {
        //having
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(1L);

        CreateEventDto createEventDto = CreateEventDto.builder()
                .gameIds(gameIds)
                .build();

        String expectedErrorMessage = "Game with id " + createEventDto.getGameIds().get(0) + " not found!";

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(gameRepository.findById(createEventDto.getGameIds().get(0)))
                .thenThrow((new NotFoundException(expectedErrorMessage)));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.createEvent(createEventDto, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
        verify(gameRepository).findById(createEventDto.getGameIds().get(0));
        Assertions.assertEquals(expectedErrorMessage, thrown.getMessage());
    }

    @Test
    @DisplayName("Join event, expected success")
    public void joinEvent() {
        //having
        VoteDto vote = VoteDto.builder()
                .gameId(1L)
                .build();
        List<VoteDto> votes = new ArrayList<>();
        votes.add(vote);

        joinEventDto.setVoteDtoList(votes);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userEventRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.empty());
        when(eventGameRepository.findByEventIdAndGameId(1L, 1L)).thenReturn(Optional.ofNullable(eventGame));

        //then
        eventService.joinEvent(joinEventDto, 1L, 1L);
        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(userEventRepository).findByEventIdAndUserId(1L, 1L);
        verify(userEventRepository).save(any(UserEvent.class));
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("Join event, expected NotFoundException(User not found)")
    public void joinEventUserNotFound() {
        //when
        when(userRepository.findById(1L)).thenThrow((new NotFoundException("User not found!")));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.joinEvent(joinEventDto, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        Assertions.assertEquals("User not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Join event, expected NotFoundException(Event not found)")
    public void joinEventEventNotFound() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenThrow((new NotFoundException("Event not found!")));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.joinEvent(joinEventDto, 1L, 1L);
        });


        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        Assertions.assertEquals("Event not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Join event, expected BadRequestException(Already joined)")
    public void joinEventAlreadyJoined() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userEventRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.of(new UserEvent()));

        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
           eventService.joinEvent(joinEventDto, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(userEventRepository).findByEventIdAndUserId(1L, 1L);
        Assertions.assertEquals("User already joined this event!", thrown.getMessage());
    }

    @Test
    @DisplayName("Join event, expected BadRequestException(Trust score too low)")
    public void joinEventNotEnoughTrust() {
        //having
        user.setTrustScore(0.4);
        event.setMinTrustScore(0.5);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userEventRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.empty());

        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            eventService.joinEvent(joinEventDto, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(userEventRepository).findByEventIdAndUserId(1L, 1L);

        Assertions.assertEquals("Trust score too low to take part in this event.", thrown.getMessage());
    }

    @Test
    @DisplayName("Pick game, expected NotFoundException(User not found)")
    public void pickGameUserNotFound() {
        //when
        when(userRepository.findById(1L)).thenThrow((new NotFoundException("User not found!")));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        Assertions.assertEquals("User not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Pick game, expected NotFoundException(Event not found)")
    public void pickGameEventNotFound() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenThrow((new NotFoundException("Event not found!")));

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        Assertions.assertEquals("Event not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Pick game, expected NotFoundException(Game not found)")
    public void pickGameGameNotFound() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenThrow((new NotFoundException("Game not found!")));
        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        Assertions.assertEquals("Game not found!", thrown.getMessage());
    }
    //pick game success
    @Test
    @DisplayName("Pick game, expected success")
    public void pickGame() {
        //having
        int minNumberOfPlayers = 100;

        game.setMinNumberOfPlayers(minNumberOfPlayers);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(eventGameRepository.findByEventIdAndGameId(1L, 1L)).thenReturn(Optional.of(eventGame));
        when(eventGameRepository.countVotes(1L, 1L)).thenReturn(minNumberOfPlayers);

        //then
        eventService.pickGame(1L, 1L, 1L);

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(eventGameRepository).findByEventIdAndGameId(1L, 1L);
        verify(eventGameRepository).countVotes(1L, 1L);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Pick game, expected BadRequestException(Only initiator can pick")
    public void pickGameAsUser() {
        //having
        User initiator = User.builder()
                .id(2L)
                .build();

        event.setInitiator(initiator);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        Assertions.assertEquals("Only the initiator can pick a game for the event!", thrown.getMessage());
    }

    @Test
    @DisplayName("Pick game, expected NotFoundException(Game unavailable)")
    public void pickGameGameUnavailable() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(eventGameRepository.findByEventIdAndGameId(1L, 1L)).thenReturn(Optional.empty());
        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(eventGameRepository).findByEventIdAndGameId(1L, 1L);
        Assertions.assertEquals("Game unavailable in this event!", thrown.getMessage());
    }

    @Test
    @DisplayName("Pick game, expected BadRequestException(Game already picked)")
    public void pickGameGameAlreadyChosen() {
        //having
        Game pickedGame = Game.builder()
                .name("test")
                .build();
        String expectedErrorMessage = String.format("The game %s has already been picked in this event!",
                pickedGame.getName());

        event.setPickedGame(pickedGame);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(eventGameRepository.findByEventIdAndGameId(1L, 1L)).thenReturn(Optional.of(eventGame));
        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(eventGameRepository).findByEventIdAndGameId(1L, 1L);
        Assertions.assertEquals(expectedErrorMessage, thrown.getMessage());

    }

    @Test
    @DisplayName("Pick game, expected BadRequestException(Not enough players)")
    public void pickGameWithoutEnoughPlayers() {
        //having
        int minNumberOfPlayers = 100;

        game.setMinNumberOfPlayers(minNumberOfPlayers);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(eventGameRepository.findByEventIdAndGameId(1L, 1L)).thenReturn(Optional.of(eventGame));
        when(eventGameRepository.countVotes(1L, 1L)).thenReturn(minNumberOfPlayers - 1);
        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            eventService.pickGame(1L, 1L, 1L);
        });

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(eventGameRepository).findByEventIdAndGameId(1L, 1L);
        verify(eventGameRepository).countVotes(1L, 1L);
        Assertions.assertEquals("Game does not meet necessary number of players!", thrown.getMessage());
    }
}
