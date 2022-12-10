package com.unibuc.boardmania.service;

import com.unibuc.boardmania.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    @Autowired
    private final VoteRepository voteRepository;

}
