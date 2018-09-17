package test.interview

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
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
 * Created on 15.09.18
 * TODO: Add comment
 */
@Path("account-manager")
class AccountManager {

    private val logger = LogManager.getLogger(javaClass)

    private val gson = Gson()

    @PUT
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createAccount(request: String): Response {
        // account holder, initial balance (optional), currency, account code (optional), secret word (TAN))
        return try {
            logger.info("Incoming request: $request")
            val createRequest = gson.fromJson(request, CreateAccountRequest::class.java)
            logger.info("Incoming CreateAccountRequest: $createRequest")
            val accountCode = AccountService.createAccount(createRequest)
            logger.info("Created account code: $accountCode")
            val response = gson.toJson(accountCode)
            Response.ok(response).build()
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
            val response = gson.toJson(AccountService.receiveBalance(accountCode))
            Response.ok(response).build()
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
        // account code, new balance, secret word, note
        return try {
            logger.info("Incoming request: $request")
            val changeBalanceRequest = gson.fromJson(request, ChangeBalanceRequest::class.java)
            logger.info("Incoming ChangeBalanceRequest: $changeBalanceRequest")
            val account = AccountService.changeBalance(changeBalanceRequest)
            logger.info("Updated account: $account")
            val response = gson.toJson(account)
            Response.ok(response).build()
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
        // account code, secret word (TAN)
        return try {
            val closeAccountRequest = gson.fromJson(request, CloseAccountRequest::class.java)
            val account = AccountService.closeAccount(closeAccountRequest)
            val response = gson.toJson(account)
            Response.ok(response).build()
        } catch (e: Exception) {
            logger.error("Error while closing an account", e)
            Response.status(Response.Status.BAD_REQUEST).build()
        }

    }
}
