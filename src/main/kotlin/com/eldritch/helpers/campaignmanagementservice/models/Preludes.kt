package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException

data class PreludeCard(@JsonProperty("cardType") val cardType: String,
                           @JsonProperty("key") val key: String,
                           @JsonProperty("name") val name: String,
                           @JsonProperty("expansion") val expansion: String,
                           @JsonProperty("flavourText") val text1: List<String>,
                           @JsonProperty("effectText") val text2: List<String>)

class PreludeDeck {
    private val objectMapper = ObjectMapper()
    private val preludeResource = try {
        ClassPathResource("other/preludes.json").file
    } catch (fnfe: FileNotFoundException) {
        logger.info("Preludes not loaded")
        null
    }
    val preludeDeck: List<PreludeCard> = if (preludeResource == null) listOf() else objectMapper.readValue(preludeResource, object : TypeReference<List<PreludeCard>>() {})
        
    companion object {
        val logger = LoggerFactory.getLogger(PreludeDeck::class.java)
    }
}
