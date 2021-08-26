package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlayerService {

    List<Player> getAllList(String name, String title, Race race, Profession profession,
                            Long after, Long before, Boolean banned, Integer minExperience,
                            Integer maxExperience, Integer minLevel, Integer maxLevel);
    List<Player> getPage(List<Player> sortAllPlayers, PlayerOrder order,
                                   Integer pageNumber, Integer pageSize);

    Player createPlayer(Player player);

    Player updatePlayer(Long id, Player requestPlayer);

    void deletePlayerById(Long id);
    Player getPlayerId(Long id);
}