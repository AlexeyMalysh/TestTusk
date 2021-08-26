package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false)Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel,
                                   @RequestParam(required = false) PlayerOrder order,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize){
        List<Player> players = playerService.getAllList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel);

        return playerService.getPage(players, order, pageNumber, pageSize);
    }
    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCountPlayer(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) Race race,
                                      @RequestParam(required = false)Profession profession,
                                      @RequestParam(required = false) Long after,
                                      @RequestParam(required = false) Long before,
                                      @RequestParam(required = false) Boolean banned,
                                      @RequestParam(required = false) Integer minExperience,
                                      @RequestParam(required = false) Integer maxExperience,
                                      @RequestParam(required = false) Integer minLevel,
                                      @RequestParam(required = false) Integer maxLevel){
        return playerService.getAllList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel).size();
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayerId(@PathVariable Long id){
        if (Long.valueOf(id) == null || Long.valueOf(id) <= 0){
            throw new BadRequestException("Check Id");
        }
        Player playerById = playerService.getPlayerId(Long.valueOf(id));
        if (playerById == null){
            throw new PlayerNotFoundException("Player with this Id is not found");
        }
        return playerById;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player createPlayer(@RequestBody Player player){
        Player createdPlayer = playerService.createPlayer(player);
        if (createdPlayer == null){
            throw new BadRequestException("Player is not created");
        }
        return createdPlayer;
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player requestPlayer) {
        if (id<=0 || invalidParameters(requestPlayer)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player responsePlayer = playerService.updatePlayer(id, requestPlayer);
        if (requestPlayer.getBirthday() != null && requestPlayer.getBirthday().getTime() < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (responsePlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(responsePlayer, HttpStatus.OK);
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayerById(@PathVariable Long id){
        if (Long.valueOf(id) == null || Long.valueOf(id) <= 0){
            throw new BadRequestException("Check Id");
        }
        playerService.deletePlayerById(Long.valueOf(id));
    }

    private boolean invalidParameters(Player player) {
        return (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000))
                || (player.getBirthday() != null && player.getBirthday().getTime() < 0);

    }

}