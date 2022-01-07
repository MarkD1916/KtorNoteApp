package com.vmakdandroiddev.routers

import com.vmakdandroiddev.data.checkIfUserExists
import com.vmakdandroiddev.data.collections.User
import com.vmakdandroiddev.data.registerUser
import com.vmakdandroiddev.data.requests.AccountRequest
import com.vmakdandroiddev.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute(){
    route("/register"){
        post{
            val request = try{
                call.receive<AccountRequest>()
            }
            catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if(!userExists){
                if (registerUser(User(request.email, request.password))){
                    call.respond(OK, SimpleResponse(true, "Successfully created account"))
                }
                else{
                    call.respond(OK, SimpleResponse(false, "An unknown error"))
                }
            } else{
                call.respond(OK, SimpleResponse(false, "A user with this email already exists"))
            }
        }
    }
}


