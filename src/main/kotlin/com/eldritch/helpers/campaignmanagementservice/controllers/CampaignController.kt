package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.models.AncientOneId
import com.eldritch.helpers.campaignmanagementservice.models.Campaign
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGame
import com.eldritch.helpers.campaignmanagementservice.models.PreludeCard
import com.eldritch.helpers.campaignmanagementservice.services.CampaignService
import org.jboss.logging.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class CampaignController(val campaignService: CampaignService) {

    val logger = Logger.getLogger(CampaignController::class.java)

    @GetMapping("/campaigns")
    fun getAllCampaigns(): Map<Campaign, List<CampaignGame>> {
        return campaignService.getCampaigns()
    }

    @GetMapping("/campaign/{campaignName}")
    fun getCampaign(@PathVariable campaignName: String): Campaign {
        return campaignService.getCampaign(campaignName)
    }

    @PutMapping("/campaign/{campaignName}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCampaign(@PathVariable campaignName: String) {
        return campaignService.createCampaign(campaignName)
    }

    @PutMapping("/campaign/{campaignName}/generate")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateCampaign(@PathVariable campaignName: String) {
        return campaignService.generateCampaign(campaignName)
    }

    @GetMapping("/campaign/{campaignName}/games")
    fun getCampaignGames(@PathVariable campaignName: String): List<CampaignGame> {
        return campaignService.getCampaignGames(campaignName)
    }

    @PutMapping("/campaign/{campaignName}/games/{gameNum}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addAncientOneToCampaign(@PathVariable campaignName: String,
                                @PathVariable gameNum: Int,
                                @RequestParam("ancientOne") ancientOne: AncientOneId) {
        return campaignService.addAncientOneToCampaign(campaignName, gameNum, ancientOne)
    }
}
