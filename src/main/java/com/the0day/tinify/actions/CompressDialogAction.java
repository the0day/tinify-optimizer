package com.the0day.tinify.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.the0day.tinify.services.TinyPNG;
import com.the0day.tinify.ui.dialogs.ProcessImageDialog;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class CompressDialogAction extends BaseCompressAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        final VirtualFile[] roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        final JFrame frame = WindowManager.getInstance().getFrame(project);
        if (roots == null || frame == null) {
            return;
        }

        // Setup API Key if not already set
        TinyPNG.setupApiKey(project);

        // Use background thread for processing file list
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            final List<VirtualFile> list = getSupportedFileList(roots, false);

            // Ensure UI is updated on EDT
            SwingUtilities.invokeLater(() -> {
                ProcessImageDialog dialog = new ProcessImageDialog(project, list, Arrays.asList(roots));
                dialog.setDialogSize(frame);
                dialog.setVisible(true);
            });
        });
    }
}
