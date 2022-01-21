package com.eldritch.helpers.campaignmanagementservice.services

import com.eldritch.helpers.campaignmanagementservice.models.AncientOneId
import com.eldritch.helpers.campaignmanagementservice.models.EncounterCard
import com.eldritch.helpers.campaignmanagementservice.models.EncounterType
import com.eldritch.helpers.campaignmanagementservice.models.GameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.PlayerEncounter
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameCharacterEncountersRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameCharacterRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import kotlin.random.Random

@Service
class EncounterService(
        val gameRepository: CampaignGameRepository,
        val campaignRepository: CampaignRepository,
        val campaignGameCharacterRepository: CampaignGameCharacterRepository,
        val characterEncountersRepository: CampaignGameCharacterEncountersRepository
) {
    val objectMapper = ObjectMapper()
    val researchEncounters = loadAllResearchEncounters()
    val specialEncounters = loadAllSpecialEncounters()
    val normalEncounters = loadAllNormalEncounters()

    final fun loadAllResearchEncounters(): Map<AncientOneId, List<EncounterCard>> {
        val allResearchEncounters = mutableMapOf<AncientOneId, List<EncounterCard>>()
        for (ancientOneId in AncientOneId.values()) {
            val resource = try {ClassPathResource("ancientOnes/$ancientOneId/clueEncounters.json").file} catch (fnfe: FileNotFoundException) {
                logger.info("Research encounters for $ancientOneId not loaded")
                null
            }
            val deck: List<EncounterCard> = if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<EncounterCard>>() {})
            allResearchEncounters[ancientOneId] = deck
        }
        return allResearchEncounters.toMap()
    }

    final fun loadAllSpecialEncounters(): Map<AncientOneId, List<EncounterCard>> {
        val allResearchEncounters = mutableMapOf<AncientOneId, List<EncounterCard>>()
        for (ancientOneId in AncientOneId.values()) {
            try {
                val resource = ClassPathResource("ancientOnes/$ancientOneId/specialEncounters.json").file
                val deck: List<EncounterCard> = objectMapper.readValue(resource, object : TypeReference<List<EncounterCard>>() {})
                allResearchEncounters[ancientOneId] = deck
            } catch (fnfe: FileNotFoundException) {
                logger.info("No special encounters for $ancientOneId")
            }
        }
        return allResearchEncounters.toMap()
    }

    final fun loadAllNormalEncounters(): Map<EncounterType, List<EncounterCard>> {
        val allNormalEncounters = mutableMapOf<EncounterType, List<EncounterCard>>()
        for (encounterType in EncounterType.values()) {
            try {
                val resource = ClassPathResource("encounters/$encounterType.json").file
                val deck: List<EncounterCard> = objectMapper.readValue(resource, object : TypeReference<List<EncounterCard>>() {})
                allNormalEncounters[encounterType] = deck
            } catch (fnfe: FileNotFoundException) {
                logger.info("No normal encounters for $encounterType - skipping")
            }
        }
        return allNormalEncounters.toMap()
    }

    fun getAvailableEncounters(campaignName: String, gameNum: Int): List<String> {
        val campaign = campaignRepository.findByCampaignName(campaignName)
        val game = gameRepository.findByCampaignIdAndGameNum(campaign.get().id, gameNum).get()
        val ancientOneId = game.ancientOneId
        val ancientOneSpecialEncounters = specialEncounters[ancientOneId]!!.map { it.name }.toSet()
        return (EncounterType.values().filter { it != EncounterType.SPECIAL_ENCOUNTER } + ancientOneSpecialEncounters.toList()).map { it.toString() }
    }

    fun getEncounter(campaignName: String, gameNum: Int, encounterType: EncounterType, character: GameCharacter): EncounterCard {
        val campaign = campaignRepository.findByCampaignName(campaignName)
        val game = gameRepository.findByCampaignIdAndGameNum(campaign.get().id, gameNum).get()
        val ancientOneId = game.ancientOneId
        val characters = campaignGameCharacterRepository.findByGameId(game.id)
        val relevantChar = characters.find { it.character == character }!!

        val encountersAlreadyThisGame = characterEncountersRepository.findByGameAndEncounterType(game, encounterType)
        val backlogOfEncounters = characterEncountersRepository.findByPlayerAndEncounterType(relevantChar.player, encounterType)

        val deck =  when (encounterType) {
            EncounterType.CLUE -> researchEncounters[ancientOneId]!!
            EncounterType.SPECIAL_ENCOUNTER -> getSpecialEncounter(ancientOneId, "Dummy")
            else -> normalEncounters[encounterType]!!
        }

        val chosenEncounter = chooseEncounter(deck, encountersAlreadyThisGame, backlogOfEncounters)
        characterEncountersRepository.save(PlayerEncounter(player = relevantChar.player, game = game, encounterType = encounterType, encounterKey = chosenEncounter.key))
        return chosenEncounter
    }

    fun getSpecialEncounter(ancientOneId: AncientOneId, specialEncounterName: String): List<EncounterCard> {
        return specialEncounters[ancientOneId]!!.filter { it.name == specialEncounterName }
    }

    fun chooseEncounter(deck: List<EncounterCard>, notAllowedEncounters: List<PlayerEncounter>, playerEncounterBacklog: List<PlayerEncounter>): EncounterCard {
        val filteredDeck = deck.filter { encounterCard -> !notAllowedEncounters.map { it.encounterKey }.contains(encounterCard.key) }

        val encounterScores = mutableMapOf<String, Int>()

        for (item in filteredDeck) {
            encounterScores[item.key] = 0
        }

        for (item in playerEncounterBacklog) {
            encounterScores[item.encounterKey] = encounterScores[item.encounterKey] ?: 0 + 1
        }

        val maxScore = encounterScores.maxByOrNull { it.value }!!.value

        for (item in encounterScores) {
            encounterScores[item.key] = maxScore + 1 - (encounterScores[item.key] ?: 0)
        }

        val total = encounterScores.values.sum()
        val random = Random.nextInt(total)
        var sumSoFar = 0
        for (enc in encounterScores) {
            sumSoFar += enc.value
            if (sumSoFar > random) {
                return filteredDeck.first { it.key == enc.key }
            }
        }
        return filteredDeck[0]
    }
    
    companion object {
        val logger = LoggerFactory.getLogger(EncounterService::class.java)
    }
}
