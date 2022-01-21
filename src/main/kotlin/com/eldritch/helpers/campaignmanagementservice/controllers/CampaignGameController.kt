package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.models.CampaignGameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.PreludeCard
import com.eldritch.helpers.campaignmanagementservice.services.CampaignService
import org.jboss.logging.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class CampaignGameController(val campaignService: CampaignService) {

    val logger = Logger.getLogger(CampaignGameController::class.java)

    @GetMapping("/campaign/{campaignName}/games/{gameNum}/preludes")
    fun getPrelude(@PathVariable campaignName: String,
                   @PathVariable gameNum: Int): List<PreludeCard> {
        return campaignService.getPreludes(campaignName, gameNum)
    }

    @GetMapping("/campaign/{campaignName}/games/{gameNum}/chars")
    fun getChars(@PathVariable campaignName: String,
                 @PathVariable gameNum: Int): List<CampaignGameCharacter> {
        return campaignService.getChars(campaignName, gameNum)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/start")
    @ResponseStatus(HttpStatus.OK)
    fun startCampaignGame(@PathVariable campaignName: String,
                          @PathVariable gameNum: Int) {
        return campaignService.startCampaignGame(campaignName, gameNum)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeCampaignGame(@PathVariable campaignName: String,
                             @PathVariable gameNum: Int) {
        return campaignService.completeCampaignGame(campaignName, gameNum)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}/fail")
    @ResponseStatus(HttpStatus.OK)
    fun failCampaignGame(@PathVariable campaignName: String,
                         @PathVariable gameNum: Int) {
        return campaignService.failCampaignGame(campaignName, gameNum)
    }
}
