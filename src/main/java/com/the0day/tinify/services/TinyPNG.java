package com.the0day.tinify.services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.the0day.tinify.Constants;
import com.the0day.tinify.PluginGlobalSettings;
import com.tinify.Tinify;

import java.io.IOException;

public class TinyPNG {
    public static boolean setupApiKey(Project project) {
        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                settings.apiKey = Messages.showInputDialog(project, Constants.API_KEY_QUESTION, Constants.TITLE, Messages.getQuestionIcon(), "", new InputValidator() {
                    @Override
                    public boolean checkInput(String inputString) {
                        return !StringUtil.isEmptyOrSpaces(inputString);
                    }

                    @Override
                    public boolean canClose(String inputString) {
                        return true;
                    }
                });
            }

            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                return false;
            }

            Tinify.setKey(settings.apiKey);
        }

        return true;
    }

    public static byte[] process(VirtualFile file) throws IOException {
        return Tinify.fromFile(file.getPath()).toBuffer();
    }
}
