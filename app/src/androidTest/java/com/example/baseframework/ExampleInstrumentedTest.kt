package com.example.baseframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.baseframework.http.NetManager
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnNetCallback
import com.example.baseframework.log.XLog
import com.example.baseframework.utils.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.baseframework", appContext.packageName)
    }
    @Test
    fun httpTest(){
        XLog.i("Hello Test")
        val API_SERVER_URL = "http://test.dfiee.cn:32318/"
        val url = API_SERVER_URL+"thirdLogin/identifyCode"
        val params = hashMapOf("phoneNumber" to "18180349758","msgMode" to "3")
        NetManager.post(url, params , object : OnNetCallback<Response> {
            override fun onSuccess(result: Response) {
                XLog.i("Http-----onSuccess--->$result")
                result.close()
            }

            override fun onFailure(error: Response) {
                XLog.i("Http-----onFailure--->$error")
                error.close()
            }
        }).connect()

        Thread.sleep(20 * TimeUtils.SECOND)
    }
}