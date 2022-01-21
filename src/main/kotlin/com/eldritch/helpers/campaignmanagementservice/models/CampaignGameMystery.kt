package com.eldritch.helpers.campaignmanagementservice.models

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
@Table
class CampaignGameMystery(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "game_id")
        var game: CampaignGame,

        @Column(name = "mystery_num")
        var mysteryNum: Int,

        @Column(name = "mystery_id")
        var mysteryId: String
)

@Entity
@Table
class CampaignGameMysteryToken(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "mystery_id")
        var mystery: CampaignGameMystery,

        @Column(name = "token_type")
        var tokenType: Token,

        @Column(name = "token")
        var token: String
)
