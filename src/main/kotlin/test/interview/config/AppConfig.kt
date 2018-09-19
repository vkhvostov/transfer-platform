package test.interview.config

import org.apache.commons.configuration2.PropertiesConfiguration

/**
 * Application properties holder class
 */
class AppConfig(properties: PropertiesConfiguration) {

    val appHost: String
    val appPort: Int

    val tanLowerBound: Int
    val tanHigherBound: Int
    val tanNumber: Int

    companion object : SingletonHolder<AppConfig, PropertiesConfiguration>(::AppConfig)

    init {
        appHost = properties.getString("app.host")
        appPort = properties.getInt("app.port")

        tanLowerBound = properties.getInt("tan.lower.bound")
        tanHigherBound = properties.getInt("tan.higher.bound")
        tanNumber = properties.getInt("tan.number")
    }
}