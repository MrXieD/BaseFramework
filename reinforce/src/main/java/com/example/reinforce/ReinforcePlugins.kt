package com.example.reinforce

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * APK加固插件
 */
class ReinforcePlugins : Plugin<Project>{
    override fun apply(target: Project) {
        val info = target.extensions.create("reinforce",ReinforceInfo::class.java) as ReinforceInfo
        target.afterEvaluate {
            //等插件分析完build.gradle后回调此接口，这时候才能获取到配置
            val userName = info.userName
            val userPwd = info.userPwd
            println("userName---->${userName}")
            println("userPwd---->${userPwd}")
            val android = target.extensions.getByName("android") as AppExtension
            android.applicationVariants.all {applicationVar ->
                println("it.name---->${applicationVar.name}")
                applicationVar.outputs.all{output ->
                        val file = output.outputFile as File
                        println("file---->${file.absolutePath}")
                    target.tasks.create("reinforce-${applicationVar.name}",ReinforceTask::class.java,file,info)
                    }
            }
        }
    }
}