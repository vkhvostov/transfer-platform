package test.interview

import org.apache.logging.log4j.LogManager
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import test.interview.config.AppConfig
import javax.ws.rs.core.UriBuilder

/**
 * Entry point of the application
 */
object TransferPlatform {

    private val logger = LogManager.getLogger(javaClass)

    private val host = AppConfig.appHost
    private val port = AppConfig.appPort

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Staring up the server...")
        val baseUri = UriBuilder.fromUri(host).port(port).build()
        val config = ResourceConfig(TransferManager::class.java, AccountManager::class.java)
        val server = JdkHttpServerFactory.createHttpServer(baseUri, config)
    }
}
