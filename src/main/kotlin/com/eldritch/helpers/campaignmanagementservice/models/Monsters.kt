package com.eldritch.helpers.campaignmanagementservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource

data class MonsterToken(@JsonProperty("cardType") val cardType: String,
                        @JsonProperty("key") val key: String,
                        @JsonProperty("name") val name: String,
                        @JsonProperty("expansion") val expansion: String,
                        @JsonProperty("horrorTest") val horrorTest: List<String>,
                        @JsonProperty("horror") val horror: Int?,
                        @JsonProperty("damageTest") val damageTest: List<String>,
                        @JsonProperty("damage") val damage: Int?,
                        @JsonProperty("toughness") val toughness: List<String>,
                        @JsonProperty("spawnEffect") val spawnEffect: List<String>,
                        @JsonProperty("reckoningEffect") val reckoningEffect: List<String>,
                        @JsonProperty("specialEffect") val specialEffect: List<String>)

class MonsterDecks {
    private val objectMapper = ObjectMapper()
    private val monsterResource = ClassPathResource("other/monsters.json").file
    val monsterDeck: List<MonsterToken> = objectMapper.readValue(monsterResource, object : TypeReference<List<MonsterToken>>() {})

    private val epicMonsterResource = ClassPathResource("other/epicMonsters.json").file
    val epicMonsterDeck: List<MonsterToken> = objectMapper.readValue(epicMonsterResource, object : TypeReference<List<MonsterToken>>() {})
}
