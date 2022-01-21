package com.eldritch.helpers.campaignmanagementservice.models

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "campaign")
class Campaign(
        @Id
        @GeneratedValue
        var id: Long = -1,

        @Column(name = "campaign_name")
        var campaignName: String,

        @Column(name = "start_date")
        var startDate: LocalDateTime,

        @Column(name = "end_date")
        var endDate: LocalDateTime?,

        @Column(name = "status")
        var status: Status
) {
    override fun toString(): String {
        return "Campaign(id=$id, campaignName='$campaignName', startDate=$startDate, endDate=$endDate, status=$status)"
    }
}


enum class Status {
    STARTED,
    RUNNING,
    COMPLETED,
    FAILED
}
