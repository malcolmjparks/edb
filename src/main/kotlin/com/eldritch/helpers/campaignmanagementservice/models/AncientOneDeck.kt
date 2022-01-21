package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException

data class AncientOneCard(@JsonProperty("key") val key: AncientOneId,
                          @JsonProperty("displayName") val displayName: String,
                          @JsonProperty("byline") val byline: String,
                          @JsonProperty("flavourText") val flavourText: String,
                          @JsonProperty("setup") val setup: String,
                          @JsonProperty("doom") val doom: Int,
                          @JsonProperty("features") val features: Map<String, String>,
                          @JsonProperty("mythosStage1") val mythosStage1: List<Int>,
                          @JsonProperty("mythosStage2") val mythosStage2: List<Int>,
                          @JsonProperty("mythosStage3") val mythosStage3: List<Int>,
                          @JsonProperty("campaignPrelude") val campaignPrelude: String)

class AncientOneDeck {
    private val objectMapper = ObjectMapper()
    val resource = try
    {
        ClassPathResource("ancientOnes/ancientOnes.json").file
    } catch (fnfe: FileNotFoundException) {
        PreludeDeck.logger.info("Ancient Ones not loaded")
        null
    }
    val deck: List<AncientOneCard> = if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<AncientOneCard>>() {})
}

class SpecialEncounterDeck(ancientOneId: AncientOneId) {
    private val objectMapper = ObjectMapper()
    val resource = ClassPathResource("ancientOnes/$ancientOneId/specialEncounters.json").file
    val deck: List<EncounterCard> = objectMapper.readValue(resource, object : TypeReference<List<EncounterCard>>() {})
}

class ClueEncounterDeck(ancientOneId: AncientOneId) {
    private val objectMapper = ObjectMapper()
    val resource = ClassPathResource("ancientOnes/$ancientOneId/clueEncounters.json").file
    val deck: List<EncounterCard> = objectMapper.readValue(resource, object : TypeReference<List<EncounterCard>>() {})
}
