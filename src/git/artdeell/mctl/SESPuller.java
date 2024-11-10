/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package git.artdeell.mctl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author maks
 */
public class SESPuller {
    private static final List<SkyMemInfo> skyPids = Collections.synchronizedList(new ArrayList<>());
    public static void main(String[] args) throws Throwable {
        collectProcList();
        if(skyPids.isEmpty()) {
            System.out.println("Not enough Sky processes found.");
            return;
        }else if(skyPids.size() > 1) {
            System.out.println("Too much Sky processes found.");
            return;
        }
        signalAllPids("-STOP");
        for(SkyMemInfo i : skyPids) new SkyMemSessionFinder(i.pid).run();
        signalAllPids("-CONT");
    }
    private static void collectProcList() throws IOException, InterruptedException {
        File procfs = new File("/proc");
        File[] procFiles = procfs.listFiles(new NumericProcfsFileFilter());
        if(procFiles == null) throw new IOException("unable to find processes in /proc");
        System.out.println("Scanning "+procFiles.length+" processes...");
        ExecutorService executor = Executors.newFixedThreadPool(16);
        for(File procFile : procFiles) executor.execute(new ScanProcessMapTask(procFile, skyPids));
        executor.shutdown();
        while(!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {}
    }
    
    private static void signalAllPids(String signal) throws IOException, InterruptedException {
        String[] killString = new String[skyPids.size() + 2];
        killString[0] = "kill";
        killString[1] = signal;
        for(int i = 0; i < skyPids.size(); i++) {
            killString[i + 2] = Long.toString(skyPids.get(i).pid);
        }
        Process killProcess = new ProcessBuilder()
                .command(killString)
                .inheritIO()
                .start();
        killProcess.waitFor();
    }
   
}
