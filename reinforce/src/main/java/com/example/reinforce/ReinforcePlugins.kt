package com.example.reinforce

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * APK加固插件
 */
class ReinforcePlugins : Plugin<Project>{
    override fun apply(target: Project) {
        val data = target.extensions.create("reinforce",ReinforceExt::class.java)
        target.afterEvaluate {
            //等插件分析完build.gradle后回调此接口，这时候才能获取到配置
            val userName = data.userName
            val userPwd = data.userPwd
            println("userName---->${userName}")
            println("userPwd---->${userPwd}")
            val android = target.extensions.getByName("android") as AppExtension
            android.applicationVariants.all {
                println("it.name---->${it.name}")
                    it.outputs.all{output ->
                        val file = output.outputFile
                        println("file---->${file.absolutePath}")
                    }
            }
        }
    }
}