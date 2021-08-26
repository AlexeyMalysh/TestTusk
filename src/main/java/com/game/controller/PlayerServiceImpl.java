package com.game.controller;



import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService{
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private Integer calculateLevel(Player player) {
        int exp = player.getExperience();
        return (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
    }
    private int calculateExpUntilNextLevel(Player player) {
        int exp = player.getExperience();
        int lvl = calculateLevel(player);
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }
    private void setLevelAndExpUntilNextLevel(Player player) {
        player.setLevel(calculateLevel(player));
        player.setUntilNextLevel(calculateExpUntilNextLevel(player));
    }

    @Override
    public List<Player> getAllList(String name, String title, Race race, Profession profession,
                                       Long after, Long before, Boolean banned, Integer minExperience,
                                       Integer maxExperience, Integer minLevel, Integer maxLevel) {
        List<Player> sortAllPlayers = playerRepository.findAll();
        if (name != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (title != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getTitle().contains(title))
                    .collect(Collectors.toList());
        }
        if (race != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getRace().equals(race))
                    .collect(Collectors.toList());
        }
        if (profession != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getProfession().equals(profession))
                    .collect(Collectors.toList());
        }
        if (after != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().after(new Date(after)))
                    .collect(Collectors.toList());
        }
        if (before != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().before(new Date(before)))
                    .collect(Collectors.toList());
        }
        if (banned != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.isBanned().equals(banned))
                    .collect(Collectors.toList());
        }
        if (minExperience != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience()>=(minExperience))
                    .collect(Collectors.toList());
        }
        if (maxExperience != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience()<=(maxExperience))
                    .collect(Collectors.toList());
        }
        if (minLevel != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel()>=(minLevel))
                    .collect(Collectors.toList());
        }
        if (maxLevel != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel()<=(maxLevel))
                    .collect(Collectors.toList());
        }
        return sortAllPlayers;
    }

    @Override
    public List<Player> getPage(List<Player> sortAllPlayers, PlayerOrder order,
                                          Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        return sortAllPlayers.stream()
                .sorted(getComparator(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getName()==null
                ||player.getName().isEmpty()
                || player.getTitle()==null
                ||player.getTitle().isEmpty()
                || player.getRace()==null
                || player.getProfession()==null
                || player.getExperience()==null
                || player.getBirthday()==null){
            throw new BadRequestException("Please add all required fields");
        }

        if (player.getName().length()<1 || player.getName().length()>12
                || player.getTitle().length()<1 || player.getTitle().length()>30
                || player.getExperience()<0 || player.getExperience()>10000000){
            throw new BadRequestException("Please check all required fields");
        }

        Calendar date = Calendar.getInstance();
        date.setTime(player.getBirthday());
        int year = date.get(Calendar.YEAR);
        if (year < 2000 || year > 3000){
            throw new BadRequestException("Please check birthday");
        }

        if (player.isBanned() == null){
            player.setBanned(false);
        }
        Integer level = calculateLevel(player);
        player.setLevel(level);
        Integer untilNextLevel = calculateExpUntilNextLevel(player);
        player.setUntilNextLevel(untilNextLevel);

        return playerRepository.saveAndFlush(player);

    }
    @Override
    public Player getPlayerId(Long id) {

        if (!playerRepository.existsById(id)){
            throw new PlayerNotFoundException("Player is not found");
        }
        return playerRepository.findById(id).get();
    }



    @Override
    public Player updatePlayer(Long id, Player requestPlayer) {

        if (!playerRepository.findById(id).isPresent()) return null;

        Player responsePlayer = getPlayerId(id);
        responsePlayer.setBanned(false);
        requestPlayer.setBanned(false);
        if (requestPlayer.getName() != null) responsePlayer.setName(requestPlayer.getName());
        if (requestPlayer.getTitle() != null) responsePlayer.setTitle(requestPlayer.getTitle());
        if (requestPlayer.getRace() != null) responsePlayer.setRace(requestPlayer.getRace());
        if (requestPlayer.getProfession() != null) responsePlayer.setProfession(requestPlayer.getProfession());
        if (requestPlayer.getBirthday() != null) responsePlayer.setBirthday(requestPlayer.getBirthday());

        if (requestPlayer.getExperience() != null) responsePlayer.setExperience(requestPlayer.getExperience());


        setLevelAndExpUntilNextLevel(responsePlayer);
        return playerRepository.save(responsePlayer);
    }

    @Override
    public void deletePlayerById(Long id) {
        if (playerRepository.existsById(id)){
            playerRepository.deleteById(id);
        }
        else{
            throw new PlayerNotFoundException("Player is not found");
        }
    }


    private Comparator<Player> getComparator(PlayerOrder order) {
        if (order == null){
            return Comparator.comparing(Player :: getId);
        }
        Comparator<Player> comparator = null;
        switch (order.getFieldName()){
            case "id":
                comparator = Comparator.comparing(Player :: getId);
            case "birthday":
                comparator = Comparator.comparing(Player :: getBirthday);
            case "experience":
                comparator = Comparator.comparing(Player :: getExperience);
            case "level":
                comparator = Comparator.comparing(Player :: getLevel);
        }
        return comparator;
    }

}