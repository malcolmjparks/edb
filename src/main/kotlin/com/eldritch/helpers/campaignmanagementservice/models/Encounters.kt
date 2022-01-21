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

data class EncounterCard(@JsonProperty("cardType") val cardType: String,
                         @JsonProperty("key") val key: String,
                         @JsonProperty("name") val name: String,
                         @JsonProperty("ancientOne") val ancientOne: String?,
                         @JsonProperty("expansion") val expansion: String,
                         @JsonProperty("text1") val text1: List<String>,
                         @JsonProperty("text2") val text2: List<String>,
                         @JsonProperty("text3") val text3: List<String>) {
    override fun toString(): String {
        return "EncounterCard(cardType='$cardType', key='$key', name='$name', ancientOne=$ancientOne, expansion='$expansion', \n$text1, \n$text2, \n$text3\n)"
    }
}

@Entity
@Table(name = "playerencounter")
class PlayerEncounter(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @Column(name = "player")
        var player: Player,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "game_id")
        val game: CampaignGame,

        @Column(name = "encounter_type")
        var encounterType: EncounterType,

        @Column(name = "encounter_key")
        var encounterKey: String
)

enum class EncounterType {
    OTHER_WORLD,
    EXPEDITION,
    DREAM_QUEST,
    MYSTIC_RUINS,
    PURPLE,
    GREEN,
    ORANGE,
    BLACK,
    CLUE,
    BROWN,  // Egypt
    RED,    // Egypt
    YELLOW, // Antarctica
    LAVENDER,   // Antarctica
    BLUE,   // Dreamlands
    SPECIAL_ENCOUNTER,
    DEVASTATION,
    ANTARCTIC_CLUE
}
