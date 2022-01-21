package com.eldritch.helpers.campaignmanagementservice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.web.filter.CommonsRequestLoggingFilter
import javax.sql.DataSource

@Configuration
open class LocalConfig {
    @Value("\${spring.datasource.url}")
    private lateinit var datasourceUrl: String

    @Value("\${spring.datasource.driver-class-name}")
    private lateinit var dbDriverClassName: String

    @Value("\${spring.datasource.username}")
    private lateinit var dbUsername: String

    @Value("\${spring.datasource.password}")
    private lateinit var dbPassword: String

    @Bean
    open fun dataSource(): DataSource =
            DriverManagerDataSource().apply {
                setDriverClassName(dbDriverClassName)
                url = datasourceUrl
                username = dbUsername
                password = dbPassword
            }

    @Bean
    open fun logFilter(): CommonsRequestLoggingFilter? {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setMaxPayloadLength(10000)
        filter.setIncludeHeaders(false)
        filter.setAfterMessagePrefix("REQUEST DATA : ")
        return filter
    }
}
