package com.unibuc.boardmania.service;

import com.unibuc.boardmania.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    @Autowired
    private final GameRepository gameRepository;
}
