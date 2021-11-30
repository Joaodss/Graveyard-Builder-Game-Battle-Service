package com.ironhack.battleservice.service;

import com.ironhack.battleservice.dto.CharacterDTO;
import com.ironhack.battleservice.dto.UserDTO;
import com.ironhack.battleservice.proxy.CharacterModelProxy;
import com.ironhack.battleservice.proxy.OpponentSelectionProxy;
import com.ironhack.battleservice.proxy.UserModelProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleServiceImpl implements BattleService {
    private final UserModelProxy userModelProxy;
    private final CharacterModelProxy characterModelProxy;
    private final OpponentSelectionProxy opponentSelectionProxy;


    public List<CharacterDTO> getOpponentsByUserLevel(String username) {
        log.info("Getting opponents by user level, for user: {}", username);
        var user = getUserByUsername(username);
        return opponentSelectionProxy.getOpponents(user.getPartyLevel());
    }

    public CharacterDTO updateHealth(String username, Long id, Integer health) {
        log.info("Updating health of character with id {}", id);
        var character = characterModelProxy.getCharacterById(id);
        if (character.getUserUsername().equals(username)) {
            var statsToUpdate = new CharacterDTO();
            if (health > character.getMaxHealth()) {
                statsToUpdate.setCurrentHealth(character.getMaxHealth());
                log.info("Health is greater than max health, setting health to max health");
            } else if (health <= 0) {
                statsToUpdate.setCurrentHealth(0);
                statsToUpdate.setIsAlive(false);
                statsToUpdate.setDeathTime(Instant.now().toString());
                log.info("Health is less than 0, setting health to 0 and setting isAlive to false");
            } else {
                statsToUpdate.setCurrentHealth(health);
                log.info("Health is between 0 and max health, setting health to {}", health);
            }
            statsToUpdate.setId(id);
            return characterModelProxy.updateCharacter(statsToUpdate);
        }
        throw new IllegalArgumentException("Dont have access to this character");
    }

    public CharacterDTO addExperience(String username, Long id, Long experience) {
        var character = characterModelProxy.getCharacterById(id);
        if (character.getUserUsername().equals(username)) {
            var statsToUpdate = new CharacterDTO();
            statsToUpdate.setExperience(character.getExperience() + experience);
            statsToUpdate.setId(id);
            return characterModelProxy.updateCharacter(statsToUpdate);
        }
        throw new IllegalArgumentException("Dont have access to this character");
    }

    public UserDTO addUserExperienceAndGold(String username, Long experience, Long gold) {
        var user = getUserByUsername(username);
        var userStatsToUpdate = new UserDTO();
        userStatsToUpdate.setUsername(username);
        userStatsToUpdate.setExperience(experience != null ?
                user.getExperience() + experience :
                user.getExperience()
        );
        userStatsToUpdate.setGold(gold != null ?
                user.getGold() + gold :
                user.getGold()
        );
        return userModelProxy.updateUser(username, userStatsToUpdate);
    }


    private UserDTO getUserByUsername(String username) {
        log.info("Getting user by username {}", username);
        return userModelProxy.getUserByUsername(username).getBody();
    }

}
