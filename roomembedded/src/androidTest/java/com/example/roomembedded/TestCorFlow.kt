package com.example.roomembedded

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import org.junit.runner.RunWith

/**
@author Anthony.H
@date: 2021/6/9
@desription:
 */
@RunWith(AndroidJUnit4::class)
class TestCorFlow {

    companion object {
        private const val TAG = "TestCorFlow"
    }

    @Test
    fun test1() {
        val testFlow = flow<String> {
            Log.e(TAG, "in flow thread:${Thread.currentThread().name} ")
            emit("")
        }


        runBlocking {
            Log.e(TAG, "test1:runBlocking1:therad:${Thread.currentThread().name} ")
            testFlow.flowOn(Dispatchers.Main).collect {
                Log.e(TAG, "receive:$it,therad:${Thread.currentThread().name} ")
            }
        }
    }

    @Test
    fun test2() {
        val testFlow = flow<String> {
            Log.e(TAG, "in flow thread:${Thread.currentThread().name} ")
            emit("")
        }


        runBlocking {
            Log.e(TAG, "test2:runBlocking1:therad:${Thread.currentThread().name} ")
            testFlow.onEach {
                Log.e(TAG, "test2: onRe:therad:${Thread.currentThread().name}")
            }.launchIn(this)
        }
    }
}