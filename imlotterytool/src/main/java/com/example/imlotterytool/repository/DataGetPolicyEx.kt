package com.example.imlotterytool.repository

import kotlinx.coroutines.flow.flow
import java.lang.Exception

/**
@author Anthony.H
@date: 2021/5/18
@desription:数据获取管理策略
 */
abstract class DataGetPolicyEx<DBTYPE, NETRESPONSETYPE, RESULTTYPE> {
    private val COUNTOUT = 3
    val flow = flow<Resource<RESULTTYPE>> {
        emit(Resource.loading(null))
        var dbResult: DBTYPE? = null
        var loopCount = 0
        while (true) {
            if (loopCount >= COUNTOUT) {
                throw Exception("获取数据失败次数超限")
                break
            }
            dbResult = loadFromDb()
            val fetch = shouldFetch(dbResult)
            if (!fetch){
                if(needPassNum()){
                    if(!needMoreDataToShowMissNum(dbResult!!)){
                        break
                    }
                }else break
            }
            val netResult = createCall()
            saveCallResult(netResult)
            loopCount++
        }
        val finalResult = db2Result(dbResult)
        emit(Resource.success(finalResult))
    }

    abstract fun db2Result(dbResult: DBTYPE?): RESULTTYPE?

    abstract suspend fun saveCallResult(netResult: NETRESPONSETYPE?)

    abstract suspend fun createCall(): NETRESPONSETYPE?

    abstract fun shouldFetch(dbResult: DBTYPE?): Boolean

    abstract suspend fun loadFromDb(date:String? = null): DBTYPE?
    //是否需要显示遗漏号码
    abstract fun needPassNum():Boolean
    //是否需要加载更多数据来显示遗漏号码
    abstract fun needMoreDataToShowMissNum(dbResult: DBTYPE):Boolean
}