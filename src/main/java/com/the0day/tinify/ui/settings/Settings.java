package com.the0day.tinify.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.UIUtil;
import com.the0day.tinify.Constants;
import com.the0day.tinify.PluginGlobalSettings;
import com.tinify.Exception;
import com.tinify.Tinify;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Settings implements Configurable {
    private JPanel mainPanel;
    private JTextField apiKey;
    private JLabel usage;
    private JPanel apiSettingPanel;
    private JPanel pluginSettings;
    private JCheckBox checkSupportedFiles;

    public Settings() {
        UIUtil.addBorder(apiSettingPanel, IdeBorderFactory.createTitledBorder("API Settings", false));
        UIUtil.addBorder(pluginSettings, IdeBorderFactory.createTitledBorder("Plugin Settings", false));
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Constants.TITLE;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return apiKey;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
                Tinify.setKey(settings.apiKey);
            }

            updateUsageCount(false);
        }

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (checkSupportedFiles.isSelected() != settings.checkSupportedFiles) {
            return true;
        }

        if (StringUtil.isEmptyOrSpaces(settings.apiKey) && StringUtil.isEmptyOrSpaces(apiKey.getText())) {
            return false;
        }

        return !apiKey.getText().equals(settings.apiKey);
    }

    @Override
    public void apply() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        settings.apiKey = apiKey.getText();
        settings.checkSupportedFiles = checkSupportedFiles.isSelected();
        Tinify.setKey(settings.apiKey);
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            updateUsageCount(true);
        } else {
            usage.setText(Constants.SETTINGS_USAGE_EMPTY);
        }
    }

    @Override
    public void reset() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        apiKey.setText(settings.apiKey);
    }

    private void updateUsageCount(boolean showErrorDialog) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                if (Tinify.validate()) {
                    usage.setText(Constants.SETTINGS_USAGE + Tinify.compressionCount());
                } else {
                    usage.setText(Constants.SETTINGS_USAGE_EMPTY);
                }
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    usage.setText(Constants.SETTINGS_USAGE_EMPTY);
                    if (showErrorDialog) {
                        Messages.showErrorDialog(e.getMessage(), Constants.TITLE);
                    }
                }, ModalityState.stateForComponent(mainPanel));
            }
        });
    }
}
