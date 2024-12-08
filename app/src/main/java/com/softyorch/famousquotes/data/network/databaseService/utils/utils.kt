package com.softyorch.famousquotes.data.network.databaseService.utils

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.softyorch.famousquotes.data.network.databaseService.SERVICE_NAME
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.writeLog

inline fun <T> tryCatchFireStore(onTry: () -> T): T? = try {
    onTry()
} catch (fFex: FirebaseFirestoreException) {
    writeLogServiceError("Firebase firestore Error", fFex)
    null
} catch (fex: FirebaseException) {
    writeLogServiceError("Firebase Error", fex)
    null
} catch (ex: Exception) {
    writeLogServiceError("DataBase Service Error", ex)
    null
}

fun writeLogServiceError(message: String, ex: Exception) {
    writeLog(ERROR, "$SERVICE_NAME $message: ${ex.cause} - Message: ${ex.message}", ex)
}