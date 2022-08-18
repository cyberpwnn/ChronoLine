package org.cyberpwn.chronoline.chronoline;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AListener implements BulkFileListener {
    @Override
    public void after(List<? extends VFileEvent> events) {
        for(VFileEvent i : events) {
            if(i.getPath().contains("chronoline.txt")) {
                continue;
            }

            fileWorkedOn(i.getFile());
        }
    }

    public void fileWorkedOn(VirtualFile file) {
        Set<String> hit = new HashSet<>();

        for(Project i : ProjectManager.getInstance().getOpenProjects()) {
            if(hit.contains(i.getBasePath())) {
                continue;
            }

            if(file.getPath().startsWith(i.getBasePath())) {
                try {
                    projectWorkedOn(i);
                    hit.add(i.getBasePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void projectWorkedOn(Project project) throws IOException {
        logTime(project);
    }

    public void logTime(Project project) throws IOException {
        long time = System.currentTimeMillis();

        File file = new File(project.getBasePath() + File.separator + ".chronoline" + File.separator + "U" + System.getProperty("user.home").replaceAll("\\Q/\\E", ".").replaceAll("\\Q\\\\E", ".") + File.separator + "chronoline.txt");
        file.getParentFile().mkdirs();
        long lastEvent = 0;
        long timeWorked = 0;

        if(file.exists()) {
            System.out.println(file.getAbsolutePath());
            BufferedReader bu = new BufferedReader(new FileReader(file));
            String s = bu.readLine();
            bu.close();
            if(s.contains(":"))
            {
                String[] f = s.split("\\Q:\\E");
                timeWorked = Long.parseLong(f[0]);
                lastEvent = Long.parseLong(f[1]);
            }
        }

        if(time - lastEvent > 60000) {
            timeWorked += Math.min(time - lastEvent, 60000 * 5);
            lastEvent = time;
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.println(timeWorked + ":" + lastEvent);
            pw.close();
            System.out.println("Time Worked " + timeWorked + "ms");
        }
    }
}