package com.the0day.tinify;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service(Service.Level.APP)
public final class PluginApplicationService {
    public static final PluginId PLUGIN_ID = PluginId.getId("com.the0day.tinify");

    @PostConstruct
    public void init() {
        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(PLUGIN_ID);
        if (plugin == null) return;

        final PluginGlobalSettings settings = PluginGlobalSettings.getInstance();

        if (StringUtil.isEmpty(settings.uuid)) {
            settings.uuid = UUID.randomUUID().toString();
        }

        if (StringUtil.isEmpty(settings.username)) {
            settings.username = settings.uuid;
        }

        if (!plugin.getVersion().equals(settings.version)) {
            settings.version = plugin.getVersion();

            String popupTitle = plugin.getName() + " v" + plugin.getVersion();
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Tinify Image Optimizer")
                    .createNotification(
                            popupTitle,
                            plugin.getChangeNotes(),
                            NotificationType.INFORMATION,
                            NotificationListener.URL_OPENING_LISTENER
                    )
                    .notify(null);
        }
    }

    public static PluginApplicationService getInstance() {
        return ApplicationManager.getApplication().getService(PluginApplicationService.class);
    }
}