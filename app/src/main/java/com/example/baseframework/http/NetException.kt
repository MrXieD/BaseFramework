package com.example.baseframework.http

class NetException:Exception {

    /**
     * Constructs a new `NetException` with its stack trace filled in.
     */
    constructor()

    /**
     * Constructs a new `NetException` with its stack trace and detail message filled in.
     *
     * @param detailMessage the detail message for this exception.
     */
    constructor(detailMessage: String): super(detailMessage)

    /**
     * Constructs a new instance of this class with detail message and cause filled in.
     *
     * @param message The detail message for the exception.
     * @param cause   The detail cause for the exception.
     *
     * @since 1.6
     */
    constructor(message: String, cause: Throwable): super(message, cause)

    /**
     * Constructs a new instance of this class with its detail cause filled in.
     *
     * @param cause  The detail cause for the exception.
     *
     * @since 1.6
     */
    constructor(cause: Throwable?): super(cause?.toString(), cause)

}