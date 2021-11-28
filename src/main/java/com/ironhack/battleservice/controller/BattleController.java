package com.ironhack.battleservice.controller;

import com.ironhack.battleservice.dto.CharacterDTO;
import com.ironhack.battleservice.dto.UserDTO;

import java.util.List;

public interface BattleController {

    List<CharacterDTO> getOpponents(String username);

    CharacterDTO updateHealth(String username, Long id, Integer health);

    CharacterDTO addExperience(String username, Long id, Long experience);

    UserDTO updateUserExperienceAndGold(String username, Long experience, Long gold);

}
