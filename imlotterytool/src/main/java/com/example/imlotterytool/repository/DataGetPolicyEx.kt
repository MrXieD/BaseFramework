package com.example.imlotterytool.repository

import kotlinx.coroutines.flow.flow

/**
@author Anthony.H
@date: 2021/5/18
@desription:
 */
abstract class DataGetPolicyEx<DBTYPE, NETRESPONSETYPE, RESULTTYPE> {
    private val COUNTOUT = 2
    val flow = flow<Resource<RESULTTYPE>> {


        emit(Resource.loading(null))
        var dbResult: DBTYPE? = null
        var loopCount = 0
        while (true) {
            if (loopCount >= COUNTOUT) {
                //todo,error
                break
            }
            dbResult = loadFromDb()
            val fetch = shouldFetch(dbResult)
            if (!fetch) {
                break
            }
            saveCallResult(createCall())
            loopCount++
        }
        val finalResult = db2Result(dbResult)
        emit(Resource.success(finalResult))

    }

    abstract fun db2Result(dbResult: DBTYPE?): RESULTTYPE?

    abstract suspend fun saveCallResult(netResult: NETRESPONSETYPE?)

    abstract suspend fun createCall(): NETRESPONSETYPE?

    abstract fun shouldFetch(dbResult: DBTYPE?): Boolean

    abstract suspend fun loadFromDb(): DBTYPE?

}