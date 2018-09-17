package test.interview.config

import org.apache.commons.configuration2.builder.fluent.Configurations

/**
 * Created on 17.09.18
 * TODO: Add comment
 */
object AppConfig {

    val appHost: String
    val appPort: Int

    val tanLowerBound: Int
    val tanHigherBound: Int
    val tanNumber: Int

    init {
        val configs = Configurations()
        val configFileName = System.getenv("CONFIG_FILE_NAME") ?: "config.properties"
        val properties = configs.properties(configFileName)

        appHost = properties.getString("app.host")
        appPort = properties.getInt("app.port")

        tanLowerBound = properties.getInt("tan.lower.bound")
        tanHigherBound = properties.getInt("tan.higher.bound")
        tanNumber = properties.getInt("tan.number")
    }
}