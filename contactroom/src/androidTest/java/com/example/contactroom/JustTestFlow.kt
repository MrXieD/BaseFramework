package com.example.contactroom

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
@author Anthony.H
@date: 2021/6/8
@desription:
 */
@RunWith(AndroidJUnit4::class)
class JustTestFlow {

    companion object {
        private const val TAG = "JustTestFlow"
    }

    /**
     * 这种普通的flow，当发射完成所有值后，就完成了，会回调onCompletion方法
     */
    @Test
    fun testFlowOnCompletion() {
        val flow = flowOf(1, 2, 3)
        runBlocking {
            flow.onCompletion {
                Log.e(TAG, "testFlowOnCompletion: ")
            }
                .collect {
                    Log.e(TAG, "testFlowOnCompletion: $it")
                }
            Log.e(TAG, "testFlowOnCompletion: finish1")
        }
        Log.e(TAG, "testFlowOnCompletion: finish2")
    }


    @Test
    fun testFlowOnCompletion2() {
        val flow = flowOf(1, 2, 3)
        runBlocking {
            flow.onCompletion {
                Log.e(TAG, "testFlowOnCompletion: ")
            }
                .collect {
                    Log.e(TAG, "testFlowOnCompletion: $it")
                }

            flow.onCompletion {
                Log.e(TAG, "testFlowOnCompletion2: ")
            }
                .collect {
                    Log.e(TAG, "testFlowOnCompletion2: $it")
                }

            Log.e(TAG, "testFlowOnCompletion: finish1")
        }
        Log.e(TAG, "testFlowOnCompletion: finish2")
    }

}