package com.eldritch.helpers.campaignmanagementservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class CampaignManagementServiceApplication

fun main(args: Array<String>) {
	runApplication<CampaignManagementServiceApplication>(*args)
}
