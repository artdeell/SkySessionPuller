/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package git.artdeell.mctl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author maks
 */
public class SkyMemSessionFinder {
    private static final String USER_SEARCH_TARGET = "\"user\":\"";
    private static final String SESSION_SEARCH_TARGET = "\"session\":\"";
    private final RandomAccessFile skyMemory;
    private final Scanner skyMaps;
    public SkyMemSessionFinder(long pid) throws IOException {
        File procDirectory = new File("/proc", Long.toString(pid));
        File skyMemFile = new File(procDirectory, "mem");
        File skyMapsFile = new File(procDirectory, "maps");
        skyMaps = new Scanner(new FileInputStream(skyMapsFile));
        skyMemory = new RandomAccessFile(skyMemFile, "r");
    }

    public void run() throws IOException{
        try (skyMemory; skyMaps) {
            while(skyMaps.hasNextLine()) {
                String addresses[] = skyMaps.next().split("-");
                long begin = Long.parseLong(addresses[0], 16);
                long end = Long.parseLong(addresses[1], 16);
                String protectionInfo = skyMaps.next();
                if(!protectionInfo.startsWith("rw-")) {
                    skyMaps.nextLine();
                    continue;
                }
                System.out.println("Searching region "+begin+"-"+end);
                if(tryFindUidSession(begin, end)) break;
                skyMaps.nextLine();
            }
        }
    }

    private boolean tryFindUidSession(long begin, long end) throws IOException {
        skyMemory.seek(begin);
        int checkLength = (int) (end - begin);
        byte[] checkContent = new byte[checkLength];
        skyMemory.read(checkContent);
        int stringStart = 0;
        int stringEnd = 0;
        for (int i = 0; i < checkLength - 6; i++) {
            if (checkContent[i] == '{'
                    && checkContent[i + 1] == '"'
                    && checkContent[i + 2] == 'u'
                    && checkContent[i + 3] == 's'
                    && checkContent[i + 4] == 'e'
                    && checkContent[i + 5] == 'r'
                    && checkContent[i + 6] == '"') {
                stringStart = i;
            }
            if (checkContent[i] == 0) {
                stringEnd = i;
            }
            if (stringStart != 0 && stringEnd != 0 && stringStart < stringEnd) {
                String extractedResult = new String(checkContent, stringStart, stringEnd - stringStart, StandardCharsets.US_ASCII);
                if(pullUidSession(extractedResult)) return true;
                stringStart = 0;
                stringEnd = 0;
            }
        }
        return false;
    }

    private static boolean pullUidSession(String extractedResult) {
        int userPosition = extractedResult.indexOf(USER_SEARCH_TARGET);
        int sessionPosition = extractedResult.indexOf(SESSION_SEARCH_TARGET);
        if(userPosition == -1 || sessionPosition == -1) return false;
        userPosition += USER_SEARCH_TARGET.length();
        sessionPosition += SESSION_SEARCH_TARGET.length();
        int userEnd = extractedResult.indexOf('"', userPosition);
        int sessionEnd = extractedResult.indexOf('"', sessionPosition);
        if(userEnd == -1 || sessionEnd == -1) return false;
        String uid = extractedResult.substring(userPosition, userEnd);
        String session = extractedResult.substring(sessionPosition, sessionEnd);
        try {
            UUID.fromString(uid);
        }catch (Exception e) {
            return false;
        }
        System.out.println("UID: "+uid+" Session: "+session);
        return true;
    }
}
