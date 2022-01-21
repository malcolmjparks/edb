package com.eldritch.helpers.campaignmanagementservice.services

import club.minnced.discord.webhook.WebhookClient
import com.eldritch.helpers.campaignmanagementservice.exceptions.CampaignRunningException
import com.eldritch.helpers.campaignmanagementservice.models.AncientOneDeck
import com.eldritch.helpers.campaignmanagementservice.models.AncientOneId
import com.eldritch.helpers.campaignmanagementservice.models.Campaign
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGame
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.PreludeCard
import com.eldritch.helpers.campaignmanagementservice.models.PreludeDeck
import com.eldritch.helpers.campaignmanagementservice.models.Status
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameCharacterRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.random.Random


@Service
class CampaignService(
        val campaignRepository: CampaignRepository,
        val campaignGameRepository: CampaignGameRepository,
        val gameCharacterRepository: CampaignGameCharacterRepository,
        @Value("\${discord.webhook.url}") val discordUrl: String
) {
    val preludeDeck = PreludeDeck()
    val ancientOneDeck = AncientOneDeck()

    fun getCampaigns(): Map<Campaign, List<CampaignGame>> {
        val campaigns = campaignRepository.findAll().toList()
        val result = mutableMapOf<Campaign, List<CampaignGame>>()
        for (campaign in campaigns) {
            result[campaign] = campaignGameRepository.findByCampaignId(campaign.id).toList()
        }
        return result.toMap()
    }

    fun getCampaign(campaignName: String): Campaign {
        return campaignRepository.findByCampaignName(campaignName).get()
    }

    fun getCampaignGames(campaignName: String): List<CampaignGame> {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val campaignGames = campaignGameRepository.findByCampaignId(campaign.id)
        return campaignGames.toList()
    }

    fun getCurrentCampaign(): Map<Campaign, List<CampaignGame>> {
        val campaigns = campaignRepository.findByStatus(Status.RUNNING).toList()
        if (campaigns.isEmpty()) {
            return mapOf()
        }
        val campaign = campaigns.first()
        return mapOf(campaign to campaignGameRepository.findByCampaignId(campaign.id).toList())
    }

    fun createCampaign(campaignName: String) {
        val runningCampaigns = campaignRepository.findByStatus(Status.RUNNING).toList()
        if (runningCampaigns.isEmpty()) {
            campaignRepository.save(Campaign(campaignName = campaignName, startDate = LocalDateTime.now(), status = Status.RUNNING, endDate = null))
        } else {
            logger.info(runningCampaigns.toString())
            throw CampaignRunningException("Campaign ${runningCampaigns.first().campaignName} is already running")
        }
    }

    fun generateCampaign(campaignName: String) {
        val gamesSoFar = campaignGameRepository.findAll()

        val ancientOnesInAllCampaigns = mutableMapOf<AncientOneId, Int>()
        for (ancientOne in AncientOneId.values()) {
            ancientOnesInAllCampaigns[ancientOne] = 0
        }

        val ancientOnesPlayed = gamesSoFar.map { it.ancientOneId }

        for (ancientOne in ancientOnesPlayed) {
            ancientOnesInAllCampaigns[ancientOne] = (ancientOnesInAllCampaigns[ancientOne] ?: 0) + 1
        }

        val maxNum = ancientOnesInAllCampaigns.maxByOrNull { it.value }!!.value

        for (ancientOne in AncientOneId.values()) {
            ancientOnesInAllCampaigns[ancientOne] = maxNum + 1 - (ancientOnesInAllCampaigns[ancientOne] ?: 0)
        }

        val campaignAncientOnes = mutableListOf<AncientOneId>()

        for (i in 0 until 6) {
            val total = ancientOnesInAllCampaigns.values.sum()
            val random = Random.nextInt(total)
            var sumSoFar = 0
            for (ancientOne in ancientOnesInAllCampaigns) {
                sumSoFar += ancientOne.value
                if (sumSoFar > random) {
                    campaignAncientOnes.add(ancientOne.key)
                    ancientOnesInAllCampaigns.remove(ancientOne.key)
                    addAncientOneToCampaign(campaignName, i + 1, ancientOne.key)
                    break
                }
            }
        }

        val webhookClient = WebhookClient.withUrl(discordUrl)
        webhookClient.send("```${campaignAncientOnes}```")

        getPreludes(campaignName, 1)
    }

    fun addAncientOneToCampaign(campaignName: String, gameNum: Int, ancientOneId: AncientOneId) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        logger.info("Saving $campaignName game $gameNum with $ancientOneId")
        campaignGameRepository.save(CampaignGame(campaign = campaign, status = Status.STARTED, ancientOneId = ancientOneId, endDate = null, startDate = null, gameNum = gameNum))
    }

    fun getPreludes(campaignName: String, gameNum: Int): List<PreludeCard> {
        val preludeCards = mutableListOf<PreludeCard>()

        // Get current AO prelude
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val ancientOneNow = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val ancientOneCard = ancientOneDeck.deck.first { it.key == ancientOneNow.ancientOneId }

        preludeCards.add(preludeDeck.preludeDeck.first { it.name == ancientOneCard.campaignPrelude })

        // Get next AO prelude (or final prelude)
        if (gameNum < 6) {
            val ancientOneNext = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum + 1).get()
            val ancientOneCardNext = ancientOneDeck.deck.first { it.key == ancientOneNext.ancientOneId }
            preludeCards.add(preludeDeck.preludeDeck.first { it.name == ancientOneCardNext.campaignPrelude })
        } else {
            preludeCards.add(preludeDeck.preludeDeck.first { it.name == "Unto the Breach" })
        }

        // Get random other prelude
        val excludedPreludes = ancientOneDeck.deck.map { it.campaignPrelude }
        val extraPrelude = preludeDeck.preludeDeck.filter { !excludedPreludes.contains(it.name) }.shuffled().first()
        preludeCards.add(extraPrelude)

        val extras = mutableSetOf<String>()
        preludeCards.forEach {
            if (extrasMap.contains(it.key)) {
                extras.add(extrasMap[it.name]!!)
            }
            when {
                it.name == "Doomsayer from Antarctica" && ancientOneCard.key != AncientOneId.RISE_OF_THE_ELDER_THINGS -> extras.add("Random Antarctica I Adventure")
                it.name == "Under the Pyramids" && ancientOneCard.key != AncientOneId.NEPHREN_KA -> extras.add("Museum Heist Adventures: Framed for Theft Adventure")
                it.name == "In Cosmic Alignment" && ancientOneCard.key != AncientOneId.SYZYGY -> extras.add("Cosmic Alignment Adventures: Discovery of a Cosmic Syzygy Adventure")
                it.name == "Otherworldly Dreams" && ancientOneCard.key != AncientOneId.HYPNOS -> extras.add("Otherworldly Dreams Adventures: A Chance Encounter Adventure")
            }
        }
        if (extras.isEmpty()) {
            extras.add("None")
        } else if (extras.contains("Dreamlands sideboard")) {
            extras.add("Dream Quests")
        }

        val webhookClient = WebhookClient.withUrl(discordUrl)
        webhookClient.send("```${preludeCards.map { it.name }}${System.lineSeparator()}Extras: ${extras}```")
        return preludeCards
    }

    fun getChars(campaignName: String, gameNum: Int): List<CampaignGameCharacter> {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum)
        val chars = gameCharacterRepository.findByGameId(game.get().id)
        val webhookClient = WebhookClient.withUrl(discordUrl)
        webhookClient.send("```Ancient one: ${game.get().ancientOneId}```")
        webhookClient.send("```${convertToNicerOutput(chars)}```")
        return chars
    }

    fun startCampaignGame(campaignName: String, gameNum: Int) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        campaignGameRepository.save(CampaignGame(campaign = campaign, status = Status.RUNNING, ancientOneId = game.ancientOneId, endDate = null, startDate = LocalDateTime.now(), gameNum = gameNum, id = game.id))
    }

    fun completeCampaignGame(campaignName: String, gameNum: Int) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        campaignGameRepository.save(CampaignGame(campaign = campaign, status = Status.COMPLETED, ancientOneId = game.ancientOneId, endDate = LocalDateTime.now(), startDate = game.startDate, gameNum = gameNum, id = game.id))
        if (gameNum == 6) {
            campaignRepository.save(Campaign(id = campaign.id, startDate = campaign.startDate, endDate = LocalDateTime.now(), campaignName = campaignName, status = Status.COMPLETED))
        }
    }

    fun failCampaignGame(campaignName: String, gameNum: Int) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        campaignGameRepository.save(CampaignGame(campaign = campaign, status = Status.FAILED, ancientOneId = game.ancientOneId, endDate = LocalDateTime.now(), startDate = game.startDate, gameNum = gameNum, id = game.id))
        campaignRepository.save(Campaign(id = campaign.id, startDate = campaign.startDate, endDate = LocalDateTime.now(), campaignName = campaignName, status = Status.FAILED))
    }

    fun convertToNicerOutput(chars: List<CampaignGameCharacter>): String {
        val output = StringBuilder()
        for (item in chars) {
            output.append("${item.player} : ${item.character.name}, Personal Quest: ${item.pqStatus}" + System.lineSeparator())
        }
        output.dropLast(1)
        return output.toString()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CampaignService::class.java)
        private val extrasMap = mapOf(
                "Doomsayer from Antarctica" to "Antarctica sideboard",
                "Under the Pyramids" to "Egypt sideboard",
                "In Cosmic Alignment" to "Mystic Ruins",
                "The Stars Align" to "Mystic Ruins",
                "Otherworldly Dreams" to "Dreamlands sideboard",
                "Aid of the Elder Gods" to "Dreamlands sideboard",
                "In the Lightless Chamber" to "Egypt sideboard"
                )
    }
}
