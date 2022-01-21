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
@Table(name = "characterpossession")
class CharacterPossession(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "character_id")
        var campaignGameCharacter: CampaignGameCharacter,

        @Column
        var gameId: Long,

        @Column(name = "type")
        var possessionType: PossessionType,

        @Column(name = "possession_key")
        var possessionKey: String,

        @Column(name = "x_pos")
        var xPos: Int = 0,

        @Column(name = "y_pos")
        var yPos: Int = 0,
)


enum class PossessionType {
    ASSET,
    ARTIFACT,
    UNIQUE_ASSET,
    CONDITION,
    SPELL,
    CLUE,
    FOCUS,
    RESOURCE,
    IMPROVEMENT_TOKEN,
    ELDRITCH_TOKEN,
    TICKET
}

data class AssetCard(@JsonProperty("cardType") val cardType: String,
                     @JsonProperty("key") val key: String,
                     @JsonProperty("name") val name: String,
                     @JsonProperty("cost") val cost: Int,
                     @JsonProperty("reckoning") val reckoning: Boolean,
                     @JsonProperty("expansion") val expansion: String,
                     @JsonProperty("tags") val tags: List<String>,
                     @JsonProperty("text") val text: List<String>) {
    override fun toString(): String {
        return "AssetCard(cardType='$cardType', key='$key', name='$name', expansion='$expansion')"
    }
}

data class ArtifactCard(@JsonProperty("cardType") val cardType: String,
                          @JsonProperty("key") val key: String,
                          @JsonProperty("name") val name: String,
                          @JsonProperty("reckoning") val reckoning: Boolean,
                          @JsonProperty("expansion") val expansion: String,
                          @JsonProperty("tags") val tags: List<String>,
                          @JsonProperty("text") val text: List<String>) {
    override fun toString(): String {
        return "ArtifactCard(cardType='$cardType', key='$key', name='$name', expansion='$expansion')"
    }
}

data class FlippablePossessionCard(@JsonProperty("cardType") val cardType: String,
                                   @JsonProperty("key") val key: String,
                                   @JsonProperty("name") val name: String,
                                   @JsonProperty("reckoning") val reckoning: Boolean,
                                   @JsonProperty("expansion") val expansion: String,
                                   @JsonProperty("tags") val tags: List<String>,
                                   @JsonProperty("text") val text: List<String>,
                                   @JsonProperty("flipText") val flipText: List<String>) {
    override fun toString(): String {
        return "PossessionCard(cardType='$cardType', key='$key', name='$name', expansion='$expansion')"
    }
}

