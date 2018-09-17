package test.interview

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import test.interview.model.MoneyTransferRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created on 14.09.18
 * TODO: Add comment
 */
@Path("transfer-manager")
class TransferManager {

    private val logger = LogManager.getLogger(javaClass)

    private val gson = Gson()

    @POST
    @Path("transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun transfer(request: String): Response {
        // from, to, amount, currency, secret word from, note
        return try {
            logger.info("Staring money transfer")
            val transferRequest = gson.fromJson(request, MoneyTransferRequest::class.java)
            val response = gson.toJson(TransferService.transfer(transferRequest))
            logger.info("Transfer successfully finished")
            Response.ok(response).build()
        } catch (e: Exception) {
            logger.error("Error while transferring money", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }
    }
}
