package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.models.EncounterCard
import com.eldritch.helpers.campaignmanagementservice.models.EncounterType
import com.eldritch.helpers.campaignmanagementservice.models.GameCharacter
import com.eldritch.helpers.campaignmanagementservice.services.EncounterService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/campaign/{campaignName}/games/{gameNum}/character/{character}"])
@CrossOrigin(origins = ["http://localhost:3000"])
class EncounterController(val encounterService: EncounterService) {
    @GetMapping("/encounter/{encounterType}")
    fun getEncounter(
            @PathVariable campaignName: String,
            @PathVariable gameNum: Int,
            @PathVariable character: GameCharacter,
            @PathVariable encounterType: EncounterType
    ): EncounterCard {
        val response = encounterService.getEncounter(campaignName, gameNum, encounterType, character)
        logger.info("Character $character requested encounterType $encounterType. Returned: $response")
        return response
    }

    @GetMapping("/encounter")
    fun getAvailableEncounter(
            @PathVariable campaignName: String,
            @PathVariable gameNum: Int,
            @PathVariable character: GameCharacter
    ): List<String> {
        val response = encounterService.getAvailableEncounters(campaignName, gameNum)
        logger.info("Character $character requested available encounters. Returned: $response")
        return response
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(EncounterController::class.java)
    }
}
