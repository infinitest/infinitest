package org.infinitest.intellij.idea.greenhook;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FilePathImpl;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.update.SequentialUpdatesContext;
import com.intellij.openapi.vcs.update.UpdateEnvironment;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.infinitest.intellij.idea.DefaultProjectComponent;
import org.infinitest.intellij.plugin.greenhook.GreenHook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScmUpdater extends DefaultProjectComponent implements GreenHook
{
    private ProjectLevelVcsManager vcsManager;

    public ScmUpdater(Project project)
    {
        vcsManager = ProjectLevelVcsManager.getInstance(project);
    }

    public void execute()
    {
        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                AbstractVcs[] vssProviders = vcsManager.getAllActiveVcss();
                for (AbstractVcs each : vssProviders)
                {
                    UpdateEnvironment updateEnvironment = each.getUpdateEnvironment();
                    if (updateEnvironment == null) continue;

                    FilePath[] paths = collectVcsRoots(each);
                    updateEnvironment.updateDirectories(paths, UpdatedFiles.create(), new EmptyProgressIndicator(),
                            Ref.create((SequentialUpdatesContext) new InfinitestSequentialUpdatesContext()));
                }
                VirtualFileManager.getInstance().refresh(true);
            }
        });
    }

    private FilePath[] collectVcsRoots(AbstractVcs vcs)
    {
        List<FilePath> paths = new ArrayList<FilePath>();
        for (VirtualFile root : vcsManager.getRootsUnderVcs(vcs))
        {
            paths.add(new FilePathImpl(root));
        }

        return paths.toArray(new FilePath[paths.size()]);
    }

    static class InfinitestSequentialUpdatesContext implements SequentialUpdatesContext
    {
        @NotNull
        public String getMessageWhenInterruptedBeforeStart()
        {
            return "Infinitest generated message";
        }
    }
}
