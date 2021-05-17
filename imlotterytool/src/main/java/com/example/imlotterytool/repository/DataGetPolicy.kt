package com.example.imlotterytool.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.flow

/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */
abstract class DataGetPolicy<ResultType, NetResponseType, DbType> {


    val flow = flow {
        emit(Resource.loading(null))
        val dbResult = loadFromDb()
        if (shouldFetch(dbResult)) {
            var netResult = createCall()//注意，这里 没有对错误进行捕获
            val resultValue = net2Db(netResult)
            if (resultValue != null) {
                saveCallResult(resultValue)
                val newDbSource = loadFromDb()
                emit(Resource.success(db2Result(newDbSource)))
            } else {
                //todo error
            }
        } else {
            emit(Resource.success(db2Result(dbResult)))
        }
    }

    protected open fun onFetchFailed() {}

    @WorkerThread
    protected abstract suspend fun saveCallResult(item: DbType)

    @MainThread
    protected abstract fun shouldFetch(data: DbType?): Boolean

    @MainThread
    protected abstract suspend fun loadFromDb(): DbType?

    @MainThread
    protected abstract suspend fun createCall(): NetResponseType?

    protected abstract suspend fun net2Db(netResponseType: NetResponseType?): DbType?

    protected abstract suspend fun db2Result(dbType: DbType?): ResultType?


}