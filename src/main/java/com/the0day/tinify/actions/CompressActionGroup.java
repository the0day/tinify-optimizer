package com.the0day.tinify.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class CompressActionGroup extends DefaultActionGroup {
    private boolean firstRun = true;

    @Override
    public void update(AnActionEvent e) {
        if (!firstRun) {
            return;
        }

        firstRun = false;
    }
}
