package com.shoshin.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.auth.*
import io.ktor.response.*
import java.util.*

private const val FirebaseAuthKey = "FirebaseAuth"

//private const val PostmanTestToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlYWNhMGFlYy1mODc5LTRkMzctYTIyMC0yYTgxNjc0YjI4YzEiLCJuYW1lIjoiUG9zdG1hbiBUZXN0In0.5sN5sfgWojc9Jmz5CLK7yC3vWrdvC-g2SYUO-2B1sAQ"
//private const val PostmanTestUserId = "eaca0aec-f879-4d37-a220-2a81674b28c1"

data class FirebaseCredential(val token: FirebaseToken) : Credential

/**
 * Copied from https://github.com/desmondtzq/ktor-auth-firebase to keep state (8 months without updates) library under control
 */
class FirebaseAuthenticationProvider internal constructor(
    config: Configuration,
    internal val firebaseApp: FirebaseApp
) : AuthenticationProvider(config) {

    internal val realm: String = config.realm
    internal val schemes = config.schemes
    internal val authHeader: (ApplicationCall) -> HttpAuthHeader? = config.authHeader
    internal val authenticationFunction: AuthenticationFunction<FirebaseCredential> = config.authenticationFunction

    class Configuration internal constructor(
        name: String?,
        private val firebaseApp: FirebaseApp
    ) : AuthenticationProvider.Configuration(name) {

        internal var authenticationFunction: AuthenticationFunction<FirebaseCredential> = {
            throw NotImplementedError(
                "Firebase auth validate function is not specified. Use firebaseAuth { validate { ... } } to fix."
            )
        }

        internal var schemes = FirebaseAuthSchemes("Bearer")

        internal var authHeader: (ApplicationCall) -> HttpAuthHeader? = { call ->
            try {
                call.request.parseAuthorizationHeader()
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        /**
         * JWT realm name that will be used during auth challenge
         */
        var realm: String = "FirebaseIdToken"

        /**
         * Http auth header retrieval function. By default it does parse `Authorization` header content.
         */
        fun authHeader(block: (ApplicationCall) -> HttpAuthHeader?) {
            authHeader = block
        }

        /**
         * @param [defaultScheme] default scheme that will be used to challenge the client when no valid auth is provided
         * @param [additionalSchemes] additional schemes that will be accepted when validating the authentication
         */
        fun authSchemes(defaultScheme: String = "Bearer", vararg additionalSchemes: String) {
            schemes = FirebaseAuthSchemes(defaultScheme, *additionalSchemes)
        }

        fun validate(validate: AuthenticationFunction<FirebaseCredential>) {
            authenticationFunction = validate
        }

        internal fun build() = FirebaseAuthenticationProvider(this, firebaseApp)
    }
}

fun Authentication.Configuration.firebase(
    name: String? = null,
    firebaseApp: FirebaseApp,
    configure: FirebaseAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = FirebaseAuthenticationProvider.Configuration(name, firebaseApp).apply(configure).build()
    val realm = provider.realm
    val schemes = provider.schemes
    val firebaseAuth = FirebaseAuth.getInstance(provider.firebaseApp)

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val authHeader = provider.authHeader(call) ?: run {
            context.bearerChallenge(AuthenticationFailedCause.NoCredentials, realm, schemes)
            return@intercept
        }

        val token = authHeader.getBlob(provider.schemes)?.takeIf { it.isNotBlank() } ?: run {
            context.bearerChallenge(AuthenticationFailedCause.InvalidCredentials, realm, schemes)
            return@intercept
        }

//        if (token == PostmanTestToken) {
//            context.principal(FirebasePrincipal(PostmanTestUserId))
//            return@intercept
//        }

        val firebaseToken = try {
            firebaseAuth.verifyIdToken(token)
        } catch (e: FirebaseAuthException) {
            context.bearerChallenge(AuthenticationFailedCause.InvalidCredentials, realm, schemes)
            return@intercept
        } catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            context.error(FirebaseAuthKey, AuthenticationFailedCause.Error(message))
            return@intercept
        }

        val principal = provider.authenticationFunction(call, FirebaseCredential(firebaseToken)) ?: run {
            context.bearerChallenge(AuthenticationFailedCause.InvalidCredentials, realm, schemes)
            return@intercept
        }

        context.principal(principal)
    }

    register(provider)
}

internal class FirebaseAuthSchemes(internal val defaultScheme: String, vararg additionalSchemes: String) {

    private val schemes = (arrayOf(defaultScheme) + additionalSchemes).map { it.lowercase(Locale.getDefault()) }.toSet()

    operator fun contains(scheme: String): Boolean = scheme.lowercase(Locale.getDefault()) in schemes
}

private fun AuthenticationContext.bearerChallenge(
    cause: AuthenticationFailedCause,
    realm: String,
    schemes: FirebaseAuthSchemes
) = challenge(FirebaseAuthKey, cause) {
    call.respond(
        UnauthorizedResponse(
            HttpAuthHeader.Parameterized(
                schemes.defaultScheme,
                mapOf(HttpAuthHeader.Parameters.Realm to realm)
            )
        )
    )
    it.complete()
}

private fun HttpAuthHeader.getBlob(schemes: FirebaseAuthSchemes) = when {
    this is HttpAuthHeader.Single && authScheme.lowercase(Locale.getDefault()) in schemes -> blob
    else -> null
}