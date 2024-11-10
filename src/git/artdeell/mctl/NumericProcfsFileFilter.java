/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package git.artdeell.mctl;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author maks
 */
public class NumericProcfsFileFilter implements FileFilter{
    @Override
    public boolean accept(File file) {
        return file.canRead() && file.getName().matches("-?\\d+");
    }
    
}
