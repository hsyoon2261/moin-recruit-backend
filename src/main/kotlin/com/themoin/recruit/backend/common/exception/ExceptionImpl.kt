package com.themoin.recruit.backend.common.exception

import com.themoin.recruit.backend.common.`object`.ResultCode

class BadRequestException : ExceptionBase {
    constructor() : super(ResultCode.BAD_REQUEST)
    constructor(errorMessage: String) : super(ResultCode.BAD_REQUEST, errorMessage)
}

class InternalServerErrorException : ExceptionBase {
    constructor() : super(ResultCode.INTERNAL_SERVER_ERROR)
    constructor(errorMessage: String) : super(ResultCode.INTERNAL_SERVER_ERROR, errorMessage)
}

class NotUsedException : ExceptionBase {
    constructor() : super(ResultCode.NOT_USED)
    constructor(errorMessage: String) : super(ResultCode.NOT_USED, errorMessage)
}

class TokenExpiredException(errorMessage: String) : ExceptionBase(ResultCode.TOKEN_EXPIRED, errorMessage) {
}

class UnAuthorizedException : ExceptionBase {
    constructor() : super(ResultCode.UNAUTHORIZED)
    constructor(errorMessage: String) : super(ResultCode.UNAUTHORIZED, errorMessage)
}

class InvalidTokenException : ExceptionBase {
    constructor() : super(ResultCode.UNAUTHORIZED)
    constructor(errorMessage: String) : super(ResultCode.INVALID_TOKEN, errorMessage)
}

class NotFoundException : ExceptionBase {
    constructor() : super(ResultCode.NOT_FOUND)
    constructor(errorMessage: String) : super(ResultCode.NOT_FOUND, errorMessage)
}
