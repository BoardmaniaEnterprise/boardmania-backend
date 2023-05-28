package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.ParticipantDto;
import com.unibuc.boardmania.dto.PostEventInitiatorReportDto;
import com.unibuc.boardmania.dto.event.CreateEventDto;
import com.unibuc.boardmania.dto.event.EventDto;
import com.unibuc.boardmania.dto.event.EventFiltersDto;
import com.unibuc.boardmania.dto.event.JoinEventDto;
import com.unibuc.boardmania.enums.UserEventPlace;
import com.unibuc.boardmania.enums.UserEventStatus;
import com.unibuc.boardmania.model.*;
import com.unibuc.boardmania.repository.*;
import com.unibuc.boardmania.utils.PageUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.unibuc.boardmania.specifications.EventSpecifications.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final SendMailService sendMailService;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final UserEventRepository userEventRepository;

    private final EventGameRepository eventGameRepository;

    private final TokenRepository tokenRepository;

    private final VoteRepository voteRepository;

    private final GameRepository gameRepository;

    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Value("${link.confirm.attendance}")
    private String confirmationLink;

    public Page<EventDto> getEvents(Long userId, Integer pageNumber, Integer pageSize, EventFiltersDto filters) {

        Pageable pageable = PageUtility.getEventsPageable(pageNumber, pageSize);
        Page<Event> events = eventRepository.findAll(searchName(filters.getSearchParam())
                .and(searchType(filters.getLocationType()))
                .and(afterNow()), pageable);

        return events.map(event -> EventDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .minTrustScore(event.getMinTrustScore())
                .location(event.getLocation())
                .name(event.getName())
                .eventDateTimestamp(event.getEventDateTimestamp())
                .votingDeadlineTimestamp(event.getVotingDeadlineTimestamp())
                .confirmationDeadlineTimestamp(event.getConfirmationDeadlineTimestamp())
                .online(event.isOnline())
                .joined(userEventRepository.findByEventIdAndUserId(event.getId(), userId).isPresent())
                .maxNumberOfPlayers(event.getMaxNumberOfPlayers())
                .initiatorName(event.getInitiator().getFirstName() + " " + event.getInitiator().getLastName())
                .build());
    }

    public EventDto getEventById(Long userId, Long eventId) {

        Event event = eventRepository.getById(eventId);
        EventDto eventDto = EventDto.builder()
                                    .id(event.getId())
                                    .description(event.getDescription())
                                    .minTrustScore(event.getMinTrustScore())
                                    .location(event.getLocation())
                                    .name(event.getName())
                                    .eventDateTimestamp(event.getEventDateTimestamp())
                                    .votingDeadlineTimestamp(event.getVotingDeadlineTimestamp())
                                    .confirmationDeadlineTimestamp(event.getConfirmationDeadlineTimestamp())
                                    .online(event.isOnline())
                                    .joined(userEventRepository.findByEventIdAndUserId(event.getId(), userId).isPresent())
                                    .maxNumberOfPlayers(event.getMaxNumberOfPlayers())
                                    .initiatorName(event.getInitiator().getFirstName() + " " + event.getInitiator().getLastName())
                                    .build();
        return eventDto;
    }

    public Event createEvent(CreateEventDto createEventDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        if (user.getTrustScore() < 80) {
            throw new ForbiddenException("Users with less than 80 trust score cannot create events.");
        }

        Event event = Event.builder()
                .name(createEventDto.getName())
                .initiator(user)
                .description(createEventDto.getDescription())
                .location(createEventDto.getLocation())
                .maxNumberOfPlayers(createEventDto.getMaxNrOfPlayers())
                .minTrustScore(createEventDto.getMinTrustScore())
                .eventDateTimestamp(createEventDto.getEventDateTimestamp())
                .votingDeadlineTimestamp(createEventDto.getVotingDeadlineTimestamp())
                .confirmationDeadlineTimestamp(createEventDto.getConfirmationDeadlineTimestamp())
                .online(createEventDto.isOnline())
                .participants(new ArrayList<>())
                .deleted(false)
                .build();

        Event savedEvent = eventRepository.save(event);

        createEventDto.getGameIds().forEach(gameId -> {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new NotFoundException("Game with id " + gameId + " not found!"));
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
        if (event.getVotingDeadlineTimestamp() <= DateTime.now().getMillis() / 1000) {
            throw new BadRequestException("Voting period has ended. Users can no longer join this event at this stage.");
        }
        if (user.getTrustScore() < event.getMinTrustScore()) {
            throw new BadRequestException("Trust score too low to take part in this event.");
        }

        UserEvent userEvent = UserEvent.builder()
                .event(event)
                .user(user)
                .userEventStatus(UserEventStatus.UNCONFIRMED)
                .sentConfirmationEmail(false)
                .deleted(false)
                .build();
        userEventRepository.save(userEvent);

        event.addParticipant(userEvent);

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

        if (!event.getInitiator().getId().equals(user.getId())) {
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

    @Transactional
    public void confirmParticipation(String tokenString) {

        Token token = getTokenByValue(UUID.fromString(tokenString));
        Long eventId = token.getRelatedObjectId();
        Long userId = token.getUser().getId();
        tokenRepository.deleteByValue(UUID.fromString(tokenString));

        userEventRepository.updateUserEventStatusByUserIdAndEventId(userId, eventId, UserEventStatus.CONFIRMED);
    }

    @Scheduled(cron = "00 00 * * * *", zone = "Europe/Bucharest")
    @Transactional
    public void notifyConfirmationPeriod() {

        List<Event> eventsNeedingConfirmation =
                eventRepository.findAllByVotingDeadlineTimestampBeforeAndSentConfirmationEmailsFalseAndDeletedFalse(DateTime.now().getMillis());
        eventsNeedingConfirmation.forEach(
                e -> {
                    boolean confirmationMailsSent = true;
                    List<UserEvent> userEventList = userEventRepository.getParticipantsByEventId(e.getId());
                    for (UserEvent ue: userEventList) {
                        try {
                            if (!ue.isSentConfirmationEmail()) {
                                Token existingToken = tokenRepository.findByUserIdAndEventId(ue.getUser().getId(), e.getId());
                                if (existingToken == null) {
                                    existingToken = new Token(null, ue.getUser(), e.getId(),
                                            DateTime.now().getMillis(), DateTime.now().plusDays(10).getMillis());
                                    tokenRepository.save(existingToken);
                                }
                                Token token = existingToken;

                                String mailSubject = String.format("Confirmation reminder for event %s", e.getName());
                                String mailMessage = String.format("To confirm presence to the event please click this link before <b>%s</b>: %s",
                                        df.format(new Date(e.getConfirmationDeadlineTimestamp() * 1000)),
                                        confirmationLink + token.getValue().toString());
                                sendMailService.sendMail(ue.getUser().getEmail(), mailSubject, mailMessage);
                                userEventRepository.updateSentConfirmationEmail(ue.getId(), true);
                            }
                        } catch (Exception ex) {
                            confirmationMailsSent = false;
                            ex.printStackTrace();
                        }
                    }
                    if (confirmationMailsSent) {
                        eventRepository.updateSentConfirmationEmails(e.getId(), true);
                    }
                }
        );
    }

    public List<EventDto> getEventsOfCurrentUser(Long userId) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId);
        List<EventDto> eventDtoList = events.stream()
                .map(event -> EventDto.builder()
                        .id(event.getId())
                        .description(event.getDescription())
                        .minTrustScore(event.getMinTrustScore())
                        .location(event.getLocation())
                        .name(event.getName())
                        .online(event.isOnline())
                        .maxNumberOfPlayers(event.getMaxNumberOfPlayers())
                        .initiatorName(event.getInitiator().getFirstName() + " " + event.getInitiator().getLastName())
                        .build()).collect(Collectors.toList());
        return eventDtoList;
    }

    @Scheduled(cron = "00 00 10 * * *", zone = "Europe/Bucharest")
    @Transactional
    public void notifyUsersEventsToday() {

        List<Event> eventsToday =
                eventRepository.findAllTakingPlaceTodayAndDeletedFalse(DateTime.now().withTimeAtStartOfDay().getMillis() / 1000,
                        DateTime.now().withTimeAtStartOfDay().plusDays(1).getMillis() / 1000);
        eventsToday.forEach(
                e -> {
                    List<UserEvent> userEventList = userEventRepository.getParticipantsByEventId(e.getId());
                    for (UserEvent ue : userEventList) {
                        String mailSubject = String.format("Reminder! You have %s today.", e.getName());
                        String mailMessage = String.format("We will be waiting for you today at the following address: %s.",
                                e.getLocation());
                        try {
                            sendMailService.sendMail(ue.getUser().getEmail(), mailSubject, mailMessage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private Token getTokenByValue(UUID value) {
        return tokenRepository
                .findByValue(value)
                .orElseThrow(() -> new NotFoundException("Token not found or no longer in use"));
    }

    public List<ParticipantDto> getParticipants(Long id) {
        List<UserEvent> usersInEvent = userEventRepository.getParticipantsByEventId(id);

        Map<UserEvent, User> usersMap = new HashMap<>();
        usersInEvent.forEach(userEvent -> usersMap.put(userEvent, userRepository.getById(userEvent.getUser().getId())));

        List<ParticipantDto> participants = new ArrayList<>();
        usersMap.forEach((userEvent, user) -> participants.add(ParticipantDto
                .builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .userEventPlace(userEvent.getUserEventPlace())
                        .userEventStatus(userEvent.getUserEventStatus())
                .build()));

        return participants;
    }

    @Transactional
    public void postEventInitiatorReport(Long userId, PostEventInitiatorReportDto postEventInitiatorReportDto) {
        Event event = eventRepository.getById(postEventInitiatorReportDto.getEventId());

        if (!userId.equals(event.getInitiator().getId()))
            throw new BadRequestException("Only the initiator can send this report.");

        if (event.getEventDateTimestamp() > DateTime.now().getMillis())
            throw new BadRequestException("Event still hasn't taken place!");

        userEventRepository.updateUserEventPlaceByUserIdAndEventId(postEventInitiatorReportDto.getFirstPlaceUserId(),
                event.getId(), UserEventPlace.FIRST);
        userEventRepository.updateUserEventPlaceByUserIdAndEventId(postEventInitiatorReportDto.getSecondPlaceUserId(),
                event.getId(), UserEventPlace.SECOND);
        userEventRepository.updateUserEventPlaceByUserIdAndEventId(postEventInitiatorReportDto.getThirdPlaceUserId(),
                event.getId(), UserEventPlace.THIRD);

        for (UserEvent eventUser: userEventRepository.getParticipantsByEventId(event.getId())) {
            User user = eventUser.getUser();
            if (postEventInitiatorReportDto.getAbsentUserIds().contains(user.getId())) {
                userEventRepository.updateUserEventStatusByUserIdAndEventId(user.getId(), event.getId(), UserEventStatus.ABSENT);
                int trustScoreDrop = 100 - user.getTrustScore() > 0 ? 100 - user.getTrustScore() : 1;
                userRepository.updateTrustScoreByUserId(user.getId(), user.getTrustScore() - trustScoreDrop);
            } else {
                userEventRepository.updateUserEventStatusByUserIdAndEventId(user.getId(), event.getId(), UserEventStatus.PRESENT);
            }
        }

    }

    public List<EventDto> getEventsAsParticipant(Long userId) {
        List<UserEvent> userEvents = userEventRepository.getEventsByUserId(userId);
        List<Event> events = userEvents.stream().map(userEvent -> eventRepository.getById(userEvent.getEvent().getId())).collect(Collectors.toList());
        return events.stream().map(event ->
                EventDto.builder()
                        .id(event.getId())
                        .description(event.getDescription())
                        .minTrustScore(event.getMinTrustScore())
                        .location(event.getLocation())
                        .name(event.getName())
                        .eventDateTimestamp(event.getEventDateTimestamp())
                        .votingDeadlineTimestamp(event.getVotingDeadlineTimestamp())
                        .confirmationDeadlineTimestamp(event.getConfirmationDeadlineTimestamp())
                        .online(event.isOnline())
                        .joined(true)
                        .maxNumberOfPlayers(event.getMaxNumberOfPlayers())
                        .initiatorName(event.getInitiator().getFirstName() + " " + event.getInitiator().getLastName())
                        .build())
                .collect(Collectors.toList());
    }
}
