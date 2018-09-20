package test.interview.controller

import arrow.core.getOrElse
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
import test.interview.service.AccountService
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * REST controller responsible for operations with account
 */
@Path("account")
class AccountController {

    private val logger = LogManager.getLogger(javaClass)

    private val gson = Gson()

    private val accountService = AccountService.getInstance()

    @PUT
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createAccount(request: String): Response {
        return try {
            logger.info("Incoming request: $request")
            val createRequest = gson.fromJson(request, CreateAccountRequest::class.java)
            logger.info("Incoming CreateAccountRequest: $createRequest")
            accountService.createAccount(createRequest)
                .map { gson.toJson(it) }
                .map { Response.ok(it).build() }
                .getOrElse { Response.status(Response.Status.BAD_REQUEST).build() }
        } catch (e: Exception) {
            logger.error("Error while creating a new account balance", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }
    }

    @GET
    @Path("balance/{accountCode}")
    @Produces(MediaType.APPLICATION_JSON)
    fun receiveAccountBalance(@PathParam("accountCode") accountCode: String): Response {
        return try {
            return accountService.receiveBalance(accountCode)
                .map { gson.toJson(it) }
                .map { Response.ok(it).build() }
                .getOrElse { Response.status(Response.Status.BAD_REQUEST).build() }
        } catch (e: Exception) {
            logger.error("Error while requesting an account balance", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }

    }

    @POST
    @Path("balance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun changeAccountBalance(request: String): Response {
        return try {
            logger.info("Incoming request: $request")
            val changeBalanceRequest = gson.fromJson(request, ChangeBalanceRequest::class.java)
            logger.info("Incoming ChangeBalanceRequest: $changeBalanceRequest")
            return accountService.changeBalance(changeBalanceRequest)
                .map { gson.toJson(it) }
                .map { Response.ok(it).build() }
                .getOrElse { Response.status(Response.Status.BAD_REQUEST).build() }
        } catch (e: Exception) {
            logger.error("Error while requesting an account balance", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }

    }

    @POST
    @Path("close")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun closeAccount(request: String): Response {
        return try {
            val closeAccountRequest = gson.fromJson(request, CloseAccountRequest::class.java)
            return accountService.closeAccount(closeAccountRequest)
                .map { gson.toJson(it) }
                .map { Response.ok(it).build() }
                .getOrElse { Response.status(Response.Status.BAD_REQUEST).build() }
        } catch (e: Exception) {
            logger.error("Error while closing an account", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }

    }
}
