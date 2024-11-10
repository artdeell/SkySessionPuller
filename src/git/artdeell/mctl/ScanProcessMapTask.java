/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package git.artdeell.mctl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author maks
 */
public class ScanProcessMapTask implements Runnable {
    private final File procRoot;
    private final List<SkyMemInfo> skyPids;

    public ScanProcessMapTask(File procRoot, List<SkyMemInfo> pids) {
        this.procRoot = procRoot;
        this.skyPids = pids;
    }

    @Override
    public void run() {
        if(!procRoot.canRead()) return;
        File procMaps = new File(procRoot, "maps");
        if(!procMaps.canRead()) return;
        try {
            scanMaps(procMaps);
        }catch(IOException e) {
            System.out.println(e.toString());
        }
    }
    
    private void scanMaps(File procMaps) throws IOException {
        FileInputStream procMapStream = new FileInputStream(procMaps);
        Scanner mapScanner = new Scanner(procMapStream);
        while(mapScanner.hasNextLine()) {
            String line = mapScanner.nextLine();
            if(!line.contains("Sky.exe")) continue;
            storePid(line);
            return;
        }
    }
    private void storePid(String procLine) {
        long pid = Long.parseLong(procRoot.getName());
        String beginAddrStr = procLine.substring(0, procLine.indexOf('-'));
        long beginAddr = Long.parseLong(beginAddrStr, 16);
        SkyMemInfo memInfo = new SkyMemInfo(pid, beginAddr);
        if(skyPids.contains(memInfo)) return;
        System.out.println("Found Sky PID: "+pid+ " base: "+beginAddrStr);
        skyPids.add(memInfo);
    }
}
