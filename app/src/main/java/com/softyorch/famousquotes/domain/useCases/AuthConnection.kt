package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IAuthService
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthConnection @Inject constructor(private val auth: IAuthService) {
    suspend operator fun invoke(): Boolean {
        var connect = false
        var attemptToConnect = 0

        while (!connect && attemptToConnect < 3) {
            connect = auth.getAnonymousAuth()

            if (!connect) {
                delay(500)
                attemptToConnect += 1
                writeLog(LevelLog.WARN, "AuthConnection -> Authentication failed. Reconnecting... ($attemptToConnect)")
            }
        }

        return connect
    }
}
