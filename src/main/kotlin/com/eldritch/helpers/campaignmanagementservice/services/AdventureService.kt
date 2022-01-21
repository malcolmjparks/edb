package com.eldritch.helpers.campaignmanagementservice.services

import com.eldritch.helpers.campaignmanagementservice.models.*
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.FileNotFoundException

@Service
class AdventureService(
        val gameRepository: CampaignGameRepository,
        val campaignRepository: CampaignRepository
) {
    val objectMapper = ObjectMapper()
    val adventures = getAdventureDeck()

    private fun getAdventureDeck(): List<AdventureCard> {
        val resource = try {ClassPathResource("other/adventures.json").file} catch (fnfe: FileNotFoundException) {
            null
        }
        return if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<AdventureCard>>() {})
    }

    fun getAdventure(campaignName: String, gameNum: Int): AdventureCard {
        return adventures[0]
    }
}
