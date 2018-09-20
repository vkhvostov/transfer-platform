package test.interview.controller

import arrow.core.getOrElse
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import test.interview.model.MoneyTransferRequest
import test.interview.service.TransferService
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * REST controller responsible for all operation regarding transferring money between accounts
 */
@Path("transfer")
class TransferController {

    private val logger = LogManager.getLogger(javaClass)

    private val gson = Gson()

    private val transferService = TransferService.getInstance()

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun transfer(request: String): Response {
        return try {
            logger.info("Staring money transfer")
            val transferRequest = gson.fromJson(request, MoneyTransferRequest::class.java)

            val updatedAccounts = transferService.transfer(transferRequest)

            updatedAccounts.first.flatMap { from ->
                updatedAccounts.second.map { to ->
                    gson.toJson(listOf(from, to)).also { logger.info("Transfer successfully finished") }
                }
            }
                .map { Response.ok(it).build() }
                .getOrElse { Response.status(Response.Status.BAD_REQUEST).build() }
        } catch (e: Exception) {
            logger.error("Error while transferring money", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }
    }
}
