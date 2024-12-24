package com.srt.exception

import java.lang.Exception
import java.lang.RuntimeException

class InvalidTokenException(message: String, ex: Exception? = null) : RuntimeException(message, ex)
class AuthorizationFailedException(message: String) : RuntimeException(message)  // TODO: 401 error
