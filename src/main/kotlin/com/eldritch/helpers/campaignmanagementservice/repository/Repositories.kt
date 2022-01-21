package com.eldritch.helpers.campaignmanagementservice.repository

import com.eldritch.helpers.campaignmanagementservice.models.Campaign
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGame
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.CampaignGameBoardToken
import com.eldritch.helpers.campaignmanagementservice.models.CharacterPossession
import com.eldritch.helpers.campaignmanagementservice.models.EncounterType
import com.eldritch.helpers.campaignmanagementservice.models.GameAdventure
import com.eldritch.helpers.campaignmanagementservice.models.GameCharacter
import com.eldritch.helpers.campaignmanagementservice.models.Player
import com.eldritch.helpers.campaignmanagementservice.models.PlayerEncounter
import com.eldritch.helpers.campaignmanagementservice.models.PossessionType
import com.eldritch.helpers.campaignmanagementservice.models.Status
import com.eldritch.helpers.campaignmanagementservice.models.Token
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CampaignRepository : CrudRepository<Campaign, Long> {
    fun findByCampaignName(campaignName: String): Optional<Campaign>
    fun findByStatus(status: Status): Iterable<Campaign>
}

@Repository
interface CampaignGameRepository : CrudRepository<CampaignGame, Long> {
    fun findByCampaignId(campaignId: Long): Iterable<CampaignGame>
    fun findByCampaignIdAndGameNum(campaignId: Long, gameNumber: Int): Optional<CampaignGame>
}

@Repository
interface CampaignGameCharacterRepository : CrudRepository<CampaignGameCharacter, Long> {
    fun findByGameId(gameId: Long): List<CampaignGameCharacter>
    fun findByGameIdAndCharacter(gameId: Long, character: GameCharacter): Optional<CampaignGameCharacter>
}

@Repository
interface CampaignGameBoardTokenRepository : CrudRepository<CampaignGameBoardToken, Long> {
    fun findByGameAndTokenType(game: CampaignGame, token: Token): List<CampaignGameBoardToken>
}

@Repository
interface CampaignGameCharacterEncountersRepository : CrudRepository<PlayerEncounter, Long> {
    fun findByPlayerAndEncounterType(player: Player, encounterType: EncounterType): List<PlayerEncounter>
    fun findByGameAndEncounterType(game: CampaignGame, encounterType: EncounterType): List<PlayerEncounter>
}

@Repository
interface CharacterPossessionRepository : CrudRepository<CharacterPossession, Long> {
    fun findByCampaignGameCharacterAndPossessionKey(character: CampaignGameCharacter, possessionKey: String): Optional<CharacterPossession>
    fun findByGameIdAndPossessionType(campaignGameId: Long, possessionType: PossessionType): List<CharacterPossession>
}

@Repository
interface GameAdventureRepository : CrudRepository<GameAdventure, Long> {
    fun findByCampaignGame(campaignGame: CampaignGame): List<GameAdventure>
}
