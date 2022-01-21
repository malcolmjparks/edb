package com.eldritch.helpers.campaignmanagementservice.services

import club.minnced.discord.webhook.WebhookClient
import com.eldritch.helpers.campaignmanagementservice.exceptions.PossessionNotFoundException
import com.eldritch.helpers.campaignmanagementservice.models.ArtifactCard
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGame
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.CharStatus
import com.eldritch.helpers.campaignmanagementservice.models.CharacterDetails
import com.eldritch.helpers.campaignmanagementservice.models.CharacterPossession
import com.eldritch.helpers.campaignmanagementservice.models.Expansion
import com.eldritch.helpers.campaignmanagementservice.models.FlippablePossessionCard
import com.eldritch.helpers.campaignmanagementservice.models.GameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.Player
import com.eldritch.helpers.campaignmanagementservice.models.AssetCard
import com.eldritch.helpers.campaignmanagementservice.models.PossessionType
import com.eldritch.helpers.campaignmanagementservice.models.PqStatus
import com.eldritch.helpers.campaignmanagementservice.models.Token
import com.eldritch.helpers.campaignmanagementservice.models.TokenLocation
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameBoardTokenRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameCharacterRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignGameRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CampaignRepository
import com.eldritch.helpers.campaignmanagementservice.repository.CharacterPossessionRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import kotlin.random.Random

@Service
class CharacterService(
        val campaignRepository: CampaignRepository,
        val campaignGameRepository: CampaignGameRepository,
        val gameCharacterRepository: CampaignGameCharacterRepository,
        val characterPossessionRepository: CharacterPossessionRepository,
        val campaignGameBoardTokenRepository: CampaignGameBoardTokenRepository,
        @Value("\${discord.webhook.url}") val discordUrl: String
) {
    private val objectMapper = ObjectMapper()
    private val characterDetailsResource = try {ClassPathResource("other/characters.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Characters not loaded")
        null
    }
    val characterDetails: List<CharacterDetails> = if (characterDetailsResource == null) listOf() else objectMapper.readValue(characterDetailsResource, object : TypeReference<List<CharacterDetails>>() {})

    private val artifactsResource = try {ClassPathResource("other/artifacts.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Artifacts not loaded")
        null
    }
    val artifacts: List<ArtifactCard> = if (artifactsResource == null) listOf() else objectMapper.readValue(artifactsResource, object : TypeReference<List<ArtifactCard>>() {})

    private val assetsResource = try {ClassPathResource("other/assets.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Assets not loaded")
        null
    }
    val assets: List<AssetCard> = if (assetsResource == null) listOf() else objectMapper.readValue(assetsResource, object : TypeReference<List<AssetCard>>() {})

    private val conditionsResource = try {ClassPathResource("other/conditions.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Conditions not loaded")
        null
    }
    val conditions: List<FlippablePossessionCard> = if (conditionsResource == null) listOf() else objectMapper.readValue(conditionsResource, object : TypeReference<List<FlippablePossessionCard>>() {})

    private val spellsResource = try {ClassPathResource("other/spells.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Spells not loaded")
        null
    }
    val spells: List<FlippablePossessionCard> = if (spellsResource == null) listOf() else objectMapper.readValue(spellsResource, object : TypeReference<List<FlippablePossessionCard>>() {})

    private val uniqueAssetsResource = try {ClassPathResource("other/uniqueassets.json").file} catch (fnfe: FileNotFoundException) {
        logger.info("Unique Assets not loaded")
        null
    }
    val uniqueAssets: List<FlippablePossessionCard> = if (uniqueAssetsResource == null) listOf() else objectMapper.readValue(uniqueAssetsResource, object : TypeReference<List<FlippablePossessionCard>>() {})


    fun pickThreeRandomChars(players: List<String>) {
        val charsPlayed = gameCharacterRepository.findAll()
        val playedSoFar = mapOf(
                "Malcolm" to charsPlayed.filter { it.player == Player.MALCOLM }.map { it.character },
                "David" to charsPlayed.filter { it.player == Player.DAVID }.map { it.character },
                "Kate" to charsPlayed.filter { it.player == Player.KATE }.map { it.character }
        )

        val chosenChars = mutableMapOf<String, MutableList<GameCharacter>>()
        players.shuffled().forEach { player -> chosenChars[player] = mutableListOf() }


        for (player in players) {

            val backlog = playedSoFar[player] ?: throw Exception("Not found")

            val charScores = mutableMapOf<GameCharacter, Int>()

            for (item in GameCharacter.values()) {
                charScores[item] = 0
            }

            for (item in backlog) {
                charScores[item] = (charScores[item] ?: 0) + 1
            }

            for (alreadyChosen1 in chosenChars.values) {
                for (alreadyChosen2 in alreadyChosen1) {
                    charScores.remove(alreadyChosen2)
                }
            }

            val maxScore = charScores.maxByOrNull { it.value }!!.value

            for (item in charScores) {
                charScores[item.key] = maxScore + 1 - (charScores[item.key] ?: 0)
            }

            for (i in 0 until 3) {
                val total = charScores.values.sum()
                val random = Random.nextInt(total)
                var sumSoFar = 0
                for (char in charScores) {
                    sumSoFar += char.value
                    if (sumSoFar > random) {
                        chosenChars[player]!!.add(char.key)
                        charScores.remove(char.key)
                        break
                    }
                }
            }
        }
        val webhookClient = WebhookClient.withUrl(discordUrl)
        webhookClient.send("```${convertToNicerOutput(chosenChars)}```")
    }

    fun addCharacterToGame(campaignName: String, gameNum: Int, character: GameCharacter, player: Player) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        if (gameCharacterRepository.findByGameIdAndCharacter(game.id, character).isPresent) {
            logger.info("Not adding $character - already in game.")
        } else {
            val charDetails = characterDetails.first { it.id == character }
            gameCharacterRepository.save(CampaignGameCharacter(
                    game = game,
                    character = character,
                    charStatus = CharStatus.ALIVE,
                    pqStatus = PqStatus.UNRESOLVED_PQ,
                    player = player,
                    charHealth = charDetails.health,
                    charSanity = charDetails.sanity
            ))

            for (startingItem in charDetails.startingEquipment) {
                addCharacterPossession(campaignName, gameNum, character, startingItem)
            }
        }
    }

    fun updateCharacterStatus(campaignName: String, gameNum: Int, character: GameCharacter, status: CharStatus) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val campaignGameCharacter = gameCharacterRepository.findByGameIdAndCharacter(game.id, character).get()
        val newPlayer = if (status in listOf(CharStatus.DEAD, CharStatus.INSANE, CharStatus.DEVOURED)) Player.NONE else campaignGameCharacter.player
        gameCharacterRepository.save(CampaignGameCharacter(
                id = campaignGameCharacter.id,
                charStatus = status,
                pqStatus = campaignGameCharacter.pqStatus,
                character = campaignGameCharacter.character,
                game = game,
                player = newPlayer,
                charHealth = campaignGameCharacter.charHealth,
                charSanity = campaignGameCharacter.charSanity
        ))
    }

    fun updateCharacterPqStatus(campaignName: String, gameNum: Int, character: GameCharacter, pqStatus: PqStatus) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val campaignGameCharacter = gameCharacterRepository.findByGameIdAndCharacter(game.id, character).get()
        gameCharacterRepository.save(CampaignGameCharacter(
                id = campaignGameCharacter.id,
                charStatus = campaignGameCharacter.charStatus,
                pqStatus = pqStatus,
                character = campaignGameCharacter.character,
                game = game,
                player = campaignGameCharacter.player,
                charHealth = campaignGameCharacter.charHealth,
                charSanity = campaignGameCharacter.charSanity
        ))
    }

    fun addCharacterPossession(campaignName: String, gameNum: Int, character: GameCharacter, possessionString: String) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val char = gameCharacterRepository.findByGameIdAndCharacter(game.id, character).get()

        try {
            val possession = when (possessionString) {
                "CLUE", "Clue" -> getClue(game, char)
                "ELDRITCH_TOKEN", "Eldritch Token" -> CharacterPossession(-1, char, game.id, PossessionType.ELDRITCH_TOKEN, PossessionType.ELDRITCH_TOKEN.name)
                "FOCUS", "Focus" -> CharacterPossession(-1, char, game.id, PossessionType.FOCUS, PossessionType.FOCUS.name)
                "RESOURCE", "Resource" -> CharacterPossession(-1, char, game.id, PossessionType.RESOURCE, PossessionType.RESOURCE.name)
                "SPELL" -> getSpell(game, char, listOf("Incantation", "Ritual", "Glamour"))
                "INCANTATION_SPELL" -> getSpell(game, char, listOf("Incantation"))
                "RITUAL_SPELL" -> getSpell(game, char, listOf("Ritual"))
                "GLAMOUR_SPELL" -> getSpell(game, char, listOf("Glamour"))
                "NON_INCANTATION_SPELL" -> getSpell(game, char, listOf("Ritual", "Glamour"))
                "NON_RITUAL_SPELL" -> getSpell(game, char, listOf("Incantation", "Glamour"))
                "NON_GLAMOUR_SPELL" -> getSpell(game, char, listOf("Incantation", "Ritual"))
                "MADNESS", "ILLNESS", "INJURY", "PURSUIT", "TALENT", "BOON", "BANE" -> getCondition(game, char, possessionString)
                else -> getPossession(game, possessionString, char)
            }
            characterPossessionRepository.save(possession)
            logger.info("${char.character} gained ${possession.possessionKey}")
        } catch (pnfe: PossessionNotFoundException) {
            logger.info("Could not acquire possession $possessionString")
        }
    }

    private fun getPossession(campaignGame: CampaignGame, possessionString: String, char: CampaignGameCharacter): CharacterPossession {
        when (possessionString) {
            in assets.map { it.name } -> {
                return checkPossession(campaignGame, PossessionType.ASSET, possessionString, char)
            }
            in artifacts.map { it.name } -> {
                return checkPossession(campaignGame, PossessionType.ARTIFACT, possessionString, char)
            }
            in uniqueAssets.map { it.name } -> {
                val possessionKey = uniqueAssets.filter { it.name == possessionString }.shuffled().first().key
                return CharacterPossession(-1, char, campaignGame.id, PossessionType.UNIQUE_ASSET, possessionKey)
            }
            in conditions.map { it.name } -> {
                val possessionKey = conditions.filter { it.name == possessionString }.shuffled().first().key
                return CharacterPossession(-1, char, campaignGame.id, PossessionType.CONDITION, possessionKey)
            }
            in spells.map { it.name } -> {
                val possessionKey = spells.filter { it.name == possessionString }.shuffled().first().key
                return CharacterPossession(-1, char, campaignGame.id, PossessionType.SPELL, possessionKey)
            }
        }
        throw PossessionNotFoundException("Could not acquire possession $possessionString")
    }

    fun tradePossession(campaignName: String, gameNum: Int, fromCharacter: GameCharacter, toCharacter: GameCharacter, possessionKey: String) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val fromChar = gameCharacterRepository.findByGameIdAndCharacter(game.id, fromCharacter).get()
        val toChar = gameCharacterRepository.findByGameIdAndCharacter(game.id, toCharacter).get()
        val possession = characterPossessionRepository.findByCampaignGameCharacterAndPossessionKey(fromChar, possessionKey)

        if (possession.isPresent) {
            val possessionObject = possession.get()
            possessionObject.campaignGameCharacter = toChar
            characterPossessionRepository.save(possession.get())
            logger.info("Moved ${possessionObject.possessionKey} from $fromCharacter to $toCharacter")
        }
    }

    private fun checkPossession(campaignGame: CampaignGame, possessionType: PossessionType, possessionString: String, char: CampaignGameCharacter): CharacterPossession {
        val possessionsAlreadyTaken = characterPossessionRepository.findByGameIdAndPossessionType(campaignGame.id, possessionType)
        if (!possessionsAlreadyTaken.map { it.possessionKey }.contains(possessionString)) {
            return CharacterPossession(-1, char, campaignGame.id, PossessionType.ASSET, possessionString)
        } else {
            throw PossessionNotFoundException("Could not acquire possession $possessionString - already taken")
        }
    }

    private fun getClue(game: CampaignGame, gameCharacter: CampaignGameCharacter): CharacterPossession {
        val cluesOnBoard = campaignGameBoardTokenRepository.findByGameAndTokenType(game, Token.CLUE)
                .map { it.tokenKey }
        val cluesInHand = characterPossessionRepository.findByGameIdAndPossessionType(game.id, PossessionType.CLUE)
                .map { it.possessionKey }

        val remainingTokens = TokenLocation.values().toMutableList()
                .filter { it.expansion == Expansion.Core }
                .map { "Clue ${it.name}" }
                .filter { !cluesOnBoard.contains(it) }
                .filter { !cluesInHand.contains(it) }
                .map { CharacterPossession(-1, gameCharacter, game.id, PossessionType.CLUE, it) }.shuffled()

        return remainingTokens.first()
    }

    private fun getSpell(game: CampaignGame, gameCharacter: CampaignGameCharacter, spellType: List<String>): CharacterPossession {
        val spellsInAllHands = characterPossessionRepository.findByGameIdAndPossessionType(game.id, PossessionType.SPELL)
        val spellsInCharHands = spellsInAllHands.filter { it.campaignGameCharacter == gameCharacter }
        val spellsInCharHandNames = spells.filter { spell -> spellsInCharHands.map { it.possessionKey }.contains(spell.key) }.map { it.name }

        // Filter out 1. spells the char already has, 2. cards taken by other chars, 3. by spell type

        val remainingSpells = spells
                .filter { !spellsInCharHandNames.contains(it.name) }
                .filter { !spellsInAllHands.map { possession -> possession.possessionKey }.contains(it.key) }
                .filter { it.tags.intersect(spellType).isNotEmpty() }

        return CharacterPossession(-1, gameCharacter, game.id, PossessionType.SPELL, remainingSpells.shuffled().first().key)
    }

    private fun getCondition(game: CampaignGame, gameCharacter: CampaignGameCharacter, conditionType: String): CharacterPossession {
        val conditionsInAllHands = characterPossessionRepository.findByGameIdAndPossessionType(game.id, PossessionType.CONDITION)
        val conditionsInCharHands = conditionsInAllHands.filter { it.campaignGameCharacter == gameCharacter }
        val conditionsInCharHandNames = conditions.filter { condition -> conditionsInCharHands.map { it.possessionKey }.contains(condition.key) }.map { it.name }

        // Filter out 1. conditions the char already has, 2. cards taken by other chars, 3. by condition type

        val remainingConditions = spells
                .filter { !conditionsInCharHandNames.contains(it.name) }
                .filter { !conditionsInAllHands.map { possession -> possession.possessionKey }.contains(it.key) }
                .filter { it.tags.map { tag -> tag.toUpperCase() }.contains(conditionType) }

        return CharacterPossession(-1, gameCharacter, game.id, PossessionType.CONDITION, remainingConditions.shuffled().first().key)
    }

    fun removeCharacterPossession(campaignName: String, gameNum: Int, character: GameCharacter, possessionKey: String) {
        val campaign = campaignRepository.findByCampaignName(campaignName).get()
        val game = campaignGameRepository.findByCampaignIdAndGameNum(campaign.id, gameNum).get()
        val char = gameCharacterRepository.findByGameIdAndCharacter(game.id, character).get()
        val possession = characterPossessionRepository.findByCampaignGameCharacterAndPossessionKey(char, possessionKey)
        possession.ifPresent { actualPossession -> characterPossessionRepository.delete(actualPossession) }
    }

    fun convertToNicerOutput(chosenChars: Map<String, List<GameCharacter>>): String {
        val output = StringBuilder()
        for (item in chosenChars) {
            output.append(item.key + ": " + item.value + System.lineSeparator())
        }
        output.dropLast(1)
        return output.toString()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CharacterService::class.java)
    }
}
