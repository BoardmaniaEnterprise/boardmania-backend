package com.unibuc.boardmania.service;

import com.unibuc.boardmania.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
}
