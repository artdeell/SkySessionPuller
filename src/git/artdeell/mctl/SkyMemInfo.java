/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package git.artdeell.mctl;

/**
 *
 * @author maks
 */
public class SkyMemInfo {
public final long pid;
    public final long baseAddress;
    
    public SkyMemInfo(long pid, long baseAddress) {
        this.pid = pid;
        this.baseAddress = baseAddress;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.pid ^ (this.pid >>> 32));
        hash = 29 * hash + (int) (this.baseAddress ^ (this.baseAddress >>> 32));
        return hash;
    }
    
    @Override public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof SkyMemInfo)) return false;
        SkyMemInfo info = (SkyMemInfo) o;
        return info.pid == this.pid && info.baseAddress == this.baseAddress;
    }
}
