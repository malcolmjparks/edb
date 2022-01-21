package com.eldritch.helpers.campaignmanagementservice.models

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "campaigngame")
class CampaignGame(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "campaign_id")
        var campaign: Campaign,

        @Column(name = "game_num")
        var gameNum: Int,

        @Column(name = "start_date")
        var startDate: LocalDateTime?,

        @Column(name = "end_date")
        var endDate: LocalDateTime?,

        @Column(name = "game_status")
        var status: Status,

        @Column(name = "ancient_one")
        var ancientOneId: AncientOneId
)

enum class AncientOneId {
    ABHOTH,
    ANTEDILUVIUM,
    ATLACH_NACHA,
    AZATHOTH,
    CTHULHU,
    HASTUR,
    HYPNOS,
    ITHAQUA,
    NEPHREN_KA,
    NYARLATHOTEP,
    RISE_OF_THE_ELDER_THINGS,
    SHUB_NIGGURATH,
    SHUDDE_MELL,
    SYZYGY,
    YIG,
    YOG_SOTHOTH
}
