package com.eldritch.helpers.campaignmanagementservice.models

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table
class CampaignGameBoardToken(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "game_id")
        var game: CampaignGame,

        @Column(name = "token_type")
        var tokenType: Token,

        @Column(name = "token_key")
        var tokenKey: String,

        @Column(name = "location")
        var tokenLocation: TokenLocation,

        @Column(name = "x")
        var x: Int,

        @Column(name = "y")
        var y: Int
)
