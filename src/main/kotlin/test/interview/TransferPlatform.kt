package test.interview

import org.apache.commons.configuration2.builder.fluent.Configurations
import org.apache.logging.log4j.LogManager
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import test.interview.config.AppConfig
import test.interview.controller.AccountController
import test.interview.controller.TransferController
import test.interview.service.AccountService
import test.interview.service.TransferService
import test.interview.storage.InMemoryStorage
import java.util.concurrent.ConcurrentHashMap
import javax.ws.rs.core.UriBuilder

/**
 * Entry point of the application
 */
object TransferPlatform {

    private val logger = LogManager.getLogger(javaClass)

    private val accounts = InMemoryStorage(ConcurrentHashMap())

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Configurations()
        val configFileName = System.getenv("CONFIG_FILE_NAME") ?: "config.properties"
        logger.info("Config file name is $configFileName")
        val properties = configs.properties(configFileName)
        val appConfig = AppConfig.getInstance(properties)
        val accountService = AccountService.getInstance(accounts)
        val transferService = TransferService.getInstance(accountService)
        logger.info("Configuration is initialized")

        val host = appConfig.appHost
        val port = appConfig.appPort

        logger.info("Staring up the server on $host:$port ...")
        val baseUri = UriBuilder.fromUri(host).port(port).build()
        val config = ResourceConfig(TransferController::class.java, AccountController::class.java)
        val server = JdkHttpServerFactory.createHttpServer(baseUri, config)
    }
}
