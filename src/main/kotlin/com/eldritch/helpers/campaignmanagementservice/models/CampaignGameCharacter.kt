package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "gamecharacter")
class CampaignGameCharacter (
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "game_id")
        var game: CampaignGame,

        @Column(name = "character")
        var character: GameCharacter,

        @Column(name = "player")
        var player: Player,

        @Column(name = "char_status")
        var charStatus: CharStatus,

        @Column(name = "pq_status")
        var pqStatus: PqStatus,

        @Column(name = "char_health")
        var charHealth: Int,

        @Column(name = "char_pop")
        var charSanity: Int
)


enum class Player {
    DAVID,
    MALCOLM,
    KATE,
    NONE
}

enum class Expansion {
    Core,
    FL,
    MoM,
    SR,
    TD,
    SoC,
    UtP,
    CiR,
    MoN
}

enum class CharStatus {
    NOT_IN_GAME,
    DEAD,
    INSANE,
    ALIVE,
    DEVOURED
}

enum class PqStatus {
    PASSED_PQ,
    FAILED_PQ,
    UNRESOLVED_PQ
}

enum class GameCharacter {
    PARAPSYCHOLOGIST,
    WAITRESS,
    SHAMAN,
    STUDENT,
    DRIFTER,
    SALESMAN,
    HAUNTED,
    PSYCHOLOGIST,
    BUTLER,
    POLITICIAN,
    LIBRARIAN,
    MECHANIC,
    PHOTOGRAPHER,
    MAGICIAN,
    REDEEMED_CULTIST,
    PRIEST,
    BOOTLEGGER,
    LAWYER,
    AUTHOR,
    FARMHAND,
    PROFESSOR,
    PSYCHIC,
    DILETTANTE,
    MUSICIAN,
    PRIVATE_EYE,
    SCIENTIST,
    EXPEDITION_LEADER,
    MARTIAL_ARTIST,
    ACTRESS,
    DREAMER,
    RESEARCHER,
    ENTERTAINER,
    SOLDIER,
    GANGSTER,
    SECRETARY,
    ARCHAEOLOGIST,
    ASTRONOMER,
    VIOLINIST,
    MILLIONAIRE,
    REPORTER,
    ATHLETE,
    FED,
    PAINTER,
    SAILOR,
    NUN,
    EX_CONVICT,
    ROOKIE_COP,
    BOUNTY_HUNTER,
    SPY,
    EXPLORER,
    DOCTOR,
    URCHIN,
    GRAVEDIGGER,
    HANDYMAN,
    CHEF
}

data class CharacterDetails(
        @JsonProperty("id") val id: GameCharacter,
        @JsonProperty("name") val name: String,
        @JsonProperty("expansion") val expansion: Expansion,
        @JsonProperty("occupation") val occupation: String,
        @JsonProperty("abilities") val abilities: List<String>,
        @JsonProperty("quote") val quote: String,
        @JsonProperty("flavourText") val flavourText: String,
        @JsonProperty("image") val image: String,
        @JsonProperty("health") val health: Int,
        @JsonProperty("sanity") val sanity: Int,
        @JsonProperty("lore") val lore: Int,
        @JsonProperty("influence") val influence: Int,
        @JsonProperty("observation") val observation: Int,
        @JsonProperty("strength") val strength: Int,
        @JsonProperty("will") val will: Int,
        @JsonProperty("startingEquipment") val startingEquipment: List<String>,
        @JsonProperty("cardsInHand") val cardsInHand: List<String>,
        @JsonProperty("tokensInHand") val tokensInHand: List<String>,
        @JsonProperty("startingLocation") val startingLocation: String,
        @JsonProperty("personalStory") val personalStory: Map<String, String>
)
