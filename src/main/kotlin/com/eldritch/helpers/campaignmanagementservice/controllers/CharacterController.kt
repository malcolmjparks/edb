package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.models.CharStatus
import com.eldritch.helpers.campaignmanagementservice.models.GameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.Player
import com.eldritch.helpers.campaignmanagementservice.models.PqStatus
import com.eldritch.helpers.campaignmanagementservice.services.CharacterService
import org.jboss.logging.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class CharacterController(val characterService: CharacterService) {

    val logger: Logger = Logger.getLogger(CharacterController::class.java)

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/{character}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addCharacterToGame(@PathVariable campaignName: String,
                           @PathVariable gameNum: Int,
                           @PathVariable character: GameCharacter,
                           @RequestParam("player") player: Player) {
        return characterService.addCharacterToGame(campaignName, gameNum, character, player)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/{character}/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    fun updateCharacterStatus(@PathVariable campaignName: String, @PathVariable gameNum: Int, @PathVariable character: GameCharacter, @PathVariable status: CharStatus) {
        return characterService.updateCharacterStatus(campaignName, gameNum, character, status)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/{character}/pqstatus/{pqstatus}")
    @ResponseStatus(HttpStatus.OK)
    fun updateCharacterPqStatus(@PathVariable campaignName: String, @PathVariable gameNum: Int, @PathVariable character: GameCharacter, @PathVariable pqstatus: PqStatus) {
        return characterService.updateCharacterPqStatus(campaignName, gameNum, character, pqstatus)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/{character}/addPossession/{possession}")
    @ResponseStatus(HttpStatus.OK)
    fun addCharacterPossession(@PathVariable campaignName: String,
                               @PathVariable gameNum: Int,
                               @PathVariable character: GameCharacter,
                               @PathVariable possession: String) {
        return characterService.addCharacterPossession(campaignName, gameNum, character, possession)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/{character}/removePossession/{possessionKey}")
    @ResponseStatus(HttpStatus.OK)
    fun removeCharacterPossession(@PathVariable campaignName: String,
                                  @PathVariable gameNum: Int,
                                  @PathVariable character: GameCharacter,
                                  @PathVariable possessionKey: String) {
        return characterService.removeCharacterPossession(campaignName, gameNum, character, possessionKey)
    }

    @PatchMapping("/campaign/{campaignName}/games/{gameNum}/character/{fromCharacter}/trade/{toCharacter}/{possessionKey}")
    @ResponseStatus(HttpStatus.OK)
    fun tradeCharacterPossession(@PathVariable campaignName: String,
                                 @PathVariable gameNum: Int,
                                 @PathVariable fromCharacter: GameCharacter,
                                 @PathVariable toCharacter: GameCharacter,
                                 @PathVariable possessionKey: String) {
        return characterService.tradePossession(campaignName, gameNum, fromCharacter, toCharacter, possessionKey)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/character/getRandomCharacters")
    @ResponseStatus(HttpStatus.OK)
    fun getRandomCharacters(@PathVariable campaignName: String,
                            @PathVariable gameNum: Int) {
        return characterService.pickThreeRandomChars(listOf("Malcolm", "David", "Kate"))
    }
}
