package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.core.io.ClassPathResource
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

data class AdventureCard(@JsonProperty("cardType") val cardType: String,
                         @JsonProperty("key") val key: String,
                         @JsonProperty("name") val name: String,
                         @JsonProperty("adventurePath") val adventurePath: String,
                         @JsonProperty("expansion") val expansion: String,
                         @JsonProperty("flavourText") val flavourText: List<String>,
                         @JsonProperty("leftText") val leftText: List<String>,
                         @JsonProperty("rightText") val rightText: List<String>) {
    override fun toString(): String {
        return "EncounterCard(cardType='$cardType', key='$key', name='$name', adventurePath=$adventurePath, expansion='$expansion')"
    }
}

class AdventureDeck {
    private val objectMapper = ObjectMapper()
    private val adventureResource = ClassPathResource("other/adventures.json").file
    val adventureDeck: List<AdventureCard> = objectMapper.readValue(adventureResource, object : TypeReference<List<AdventureCard>>() {})
}

@Entity
@Table(name = "gameadventure")
class GameAdventure(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "game_id")
        val campaignGame: CampaignGame,

        @Column(name = "adventure_key")
        var adventureKey: String
)
