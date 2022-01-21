package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.models.AncientOneId
import com.eldritch.helpers.campaignmanagementservice.models.MythosCard
import com.eldritch.helpers.campaignmanagementservice.services.MythosService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class MythosController(val mythosService: MythosService) {
    @GetMapping("/mythos")
    fun getMythos(): List<MythosCard> {
        return mythosService.buildMythosForAncientOne()
    }

    @GetMapping("/{ancientOneId}/mythos")
    fun getAncientOneMythos(@PathVariable ancientOneId: AncientOneId): List<MythosCard> {
        return mythosService.buildMythosForAncientOne(ancientOneId)
    }
}
