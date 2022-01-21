package com.eldritch.helpers.campaignmanagementservice.exceptions

class CampaignRunningException(override val message: String): Throwable(message)

class PossessionNotFoundException(override val message: String): Throwable(message)
