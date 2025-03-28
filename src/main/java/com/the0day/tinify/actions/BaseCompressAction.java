package com.the0day.tinify.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.the0day.tinify.Constants;
import com.the0day.tinify.PluginGlobalSettings;
import com.tinify.Tinify;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseCompressAction extends AnAction {
    private static final String[] supportedExtensions = {"png", "jpg", "jpeg"};

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                settings.apiKey = Messages.showInputDialog(e.getProject(), Constants.API_KEY_QUESTION, Constants.TITLE, Messages.getQuestionIcon());
            }

            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                return;
            }

            Tinify.setKey(settings.apiKey);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (!settings.checkSupportedFiles) {
            return;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
            final List<VirtualFile> list = getSupportedFileList(files, true);

            // Now update the UI on the EDT with the result
            SwingUtilities.invokeLater(() -> {
                final Presentation presentation = e.getPresentation();
                presentation.setEnabled(!list.isEmpty());
            });
        });
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    List<VirtualFile> getSupportedFileList(VirtualFile[] files, boolean breakOnFirstFound) {
        List<VirtualFile> result = new LinkedList<>();
        if (files == null) {
            return result;
        }

        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                result.addAll(getSupportedFileList(file.getChildren(), breakOnFirstFound));
                if (breakOnFirstFound && !result.isEmpty()) {
                    break;
                } else {
                    continue;
                }
            }

            final String extension = file.getExtension();
            if (extension != null && ArrayUtil.contains(extension.toLowerCase(), supportedExtensions)) {
                result.add(file);
                if (breakOnFirstFound) {
                    break;
                }
            }
        }

        return result;
    }
}
