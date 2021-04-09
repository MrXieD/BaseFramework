package com.example.reinforce;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import java.io.File;
import javax.inject.Inject;

public class ReinforceTask extends DefaultTask {
    private  File apkFile;
    private  ReinforceInfo reinforceInfo;
    @Inject
    public ReinforceTask(File apkFile,ReinforceInfo reinforceInfo){
        this.apkFile = apkFile;
        this.reinforceInfo = reinforceInfo;
    }

    @TaskAction
    public void task(){
    }
}
