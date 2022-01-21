package com.eldritch.helpers.campaignmanagementservice.services

import com.eldritch.helpers.campaignmanagementservice.models.AncientOneId
import com.eldritch.helpers.campaignmanagementservice.models.AncientOneDeck
import com.eldritch.helpers.campaignmanagementservice.models.BlueMythosDeck
import com.eldritch.helpers.campaignmanagementservice.models.GreenMythosDeck
import com.eldritch.helpers.campaignmanagementservice.models.MythosCard
import com.eldritch.helpers.campaignmanagementservice.models.YellowMythosDeck
import org.springframework.stereotype.Service

@Service
class MythosService {
    val greenMythosDeck = GreenMythosDeck()
    val blueMythosDeck = BlueMythosDeck()
    val yellowMythosDeck = YellowMythosDeck()
    val ancientOneDeck = AncientOneDeck()

    fun buildMythosForAncientOne(): List<MythosCard> {
        return blueMythosDeck.deck
    }

    fun buildMythosForAncientOne(ancientOneId: AncientOneId): List<MythosCard> {
        val ancientOneCard = ancientOneDeck.deck.filter { it.key == ancientOneId }[0]
        val mythos = mutableListOf<MythosCard>()

        val greenMythosDeckToUse = greenMythosDeck.deck.shuffled().toMutableList()
        val yellowMythosDeckToUse = yellowMythosDeck.deck.shuffled().toMutableList()
        val blueMythosDeckToUse = blueMythosDeck.deck.shuffled().toMutableList()

        for (i in 0..2) {
            val mythosPart = mutableListOf<MythosCard>()
            val part = when(i) {
                0 -> ancientOneCard.mythosStage1
                1 -> ancientOneCard.mythosStage2
                else -> ancientOneCard.mythosStage3
            }

            val greenPart = greenMythosDeckToUse.subList(0, part[0])
            mythosPart.addAll(greenPart)
            greenMythosDeckToUse.removeAll(greenPart)

            val yellowPart = yellowMythosDeckToUse.subList(0, part[1])
            mythosPart.addAll(yellowPart)
            yellowMythosDeckToUse.removeAll(yellowPart)

            val bluePart = blueMythosDeckToUse.subList(0,part[2])
            mythosPart.addAll(bluePart)
            blueMythosDeckToUse.removeAll(bluePart)

            mythos.addAll(mythosPart.shuffled())
        }
        return mythos
    }
}
