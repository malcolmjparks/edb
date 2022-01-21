package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException


open class MythosCard(open val cardType: String,
                      open val key: String,
                      open val name: String,
                      open val tags: List<String>,
                      open val difficulty: String,
                      open val expansion: String,
                      open val flavourText: String,
                      open val effect: List<String>,
                      open val reckoning: List<String>)

data class GreenMythosCard(@JsonProperty("cardType") override val cardType: String,
                           @JsonProperty("key") override val key: String,
                           @JsonProperty("name") override val name: String,
                           @JsonProperty("tags") override val tags: List<String>,
                           @JsonProperty("difficulty") override val difficulty: String,
                           @JsonProperty("expansion") override val expansion: String,
                           @JsonProperty("flavourText") override val flavourText: String,
                           @JsonProperty("effect") override val effect: List<String>,
                           @JsonProperty("reckoning") override val reckoning: List<String>) : MythosCard(cardType, key, name, tags, difficulty, expansion, flavourText, effect, reckoning)

data class YellowMythosCard(@JsonProperty("cardType") override val cardType: String,
                            @JsonProperty("key") override val key: String,
                            @JsonProperty("name") override val name: String,
                            @JsonProperty("tags") override val tags: List<String>,
                            @JsonProperty("difficulty") override val difficulty: String,
                            @JsonProperty("expansion") override val expansion: String,
                            @JsonProperty("flavourText") override val flavourText: String,
                            @JsonProperty("effect") override val effect: List<String>,
                            @JsonProperty("reckoning") override val reckoning: List<String>) : MythosCard(cardType, key, name, tags, difficulty, expansion, flavourText, effect, reckoning)

data class BlueMythosCard(@JsonProperty("cardType") override val cardType: String,
                          @JsonProperty("key") override val key: String,
                          @JsonProperty("name") override val name: String,
                          @JsonProperty("tags") override val tags: List<String>,
                          @JsonProperty("difficulty") override val difficulty: String,
                          @JsonProperty("expansion") override val expansion: String,
                          @JsonProperty("eldritchTokens") val eldritchTokens: Int,
                          @JsonProperty("rumourTokens") val rumourTokens: List<String>,
                          @JsonProperty("flavourText") override val flavourText: String,
                          @JsonProperty("effect") override val effect: List<String>,
                          @JsonProperty("reckoning") override val reckoning: List<String>) : MythosCard(cardType, key, name, tags, difficulty, expansion, flavourText, effect, reckoning)

class GreenMythosDeck() {
    private val objectMapper = ObjectMapper()
    private val resource = try {ClassPathResource("mythos/greenMythos.json").file} catch (fnfe: FileNotFoundException) {
        null
    }
    val deck: List<GreenMythosCard> = if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<GreenMythosCard>>() {})
}

class BlueMythosDeck() {
    private val objectMapper = ObjectMapper()
    private val resource = try {ClassPathResource("mythos/blueMythos.json").file} catch (fnfe: FileNotFoundException) {
        null
    }
    val deck: List<BlueMythosCard> = if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<BlueMythosCard>>() {})
}

class YellowMythosDeck() {
    private val objectMapper = ObjectMapper()
    private val resource = try{ClassPathResource("mythos/yellowMythos.json").file} catch (fnfe: FileNotFoundException) {
        null
    }
    val deck: List<YellowMythosCard> = if (resource == null) listOf() else objectMapper.readValue(resource, object : TypeReference<List<YellowMythosCard>>() {})
}
