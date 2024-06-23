package com.themoin.recruit.backend.common.exception

import com.themoin.recruit.backend.common.`object`.ResultCode

open class ExceptionBase : RuntimeException {
    var resultCode: ResultCode

    constructor(resultCode: ResultCode) : super() {
        this.resultCode = resultCode
    }

    constructor(errorMessage: String) : super(errorMessage) {
        this.resultCode = ResultCode.FAILURE
    }

    constructor(resultCode: ResultCode, errorMessage: String) : super(errorMessage) {
        this.resultCode = resultCode
    }
}