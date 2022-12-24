package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.NewGameDto;
import com.unibuc.boardmania.dto.UpdateGameDto;
import com.unibuc.boardmania.model.Game;
import com.unibuc.boardmania.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Long addGame(NewGameDto newGameDto) {
        if (gameRepository.findByName(newGameDto.getName()).isPresent()) {
            throw new BadRequestException("Game already exists");
        }
        Game game = Game.builder()
                .name(newGameDto.getName())
                .description(newGameDto.getDescription())
                .deleted(false)
                .url(newGameDto.getUrl())
                .maxNumberOfPlayers(newGameDto.getMaxNumberOfPlayers())
                .minNumberOfPlayers(newGameDto.getMinNumberOfPlayers())
                .build();

        return gameRepository.save(game).getId();
    }

    public List<Game> getAllGames() {
        return gameRepository.findAllByDeletedFalse();
    }

    public void deleteGame(Long id) {
        if (gameRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Game not found!");
        }
        gameRepository.deleteGame(id);
    }

    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Game not found"));
    }

    public void updateGame(Long id, UpdateGameDto updateGameDto) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new NotFoundException("Game not found"));
        if (updateGameDto.getName() != null) {
            game.setName(updateGameDto.getName());
        }
        if (updateGameDto.getDescription() != null){
            game.setDescription(updateGameDto.getDescription());
        }
        if (updateGameDto.getMaxNumberOfPlayers() != null) {
            game.setMaxNumberOfPlayers(updateGameDto.getMaxNumberOfPlayers());
        }
        if (updateGameDto.getMinNumberOfPlayers() != null) {
            game.setMinNumberOfPlayers(updateGameDto.getMinNumberOfPlayers());
        }
        if (updateGameDto.getDeleted() != null) {
            game.setDeleted(updateGameDto.getDeleted());
        }
        if (updateGameDto.getUrl() != null) {
            game.setUrl(updateGameDto.getUrl());
        }
        gameRepository.save(game);
    }
}
