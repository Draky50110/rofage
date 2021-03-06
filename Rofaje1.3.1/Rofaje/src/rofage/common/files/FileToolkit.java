package rofage.common.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.swing.JOptionPane;

import rofage.ihm.Messages;

public abstract class FileToolkit {
	
	public static void checkAndCreateFolder (String path) {
		File folderImages = new File (path);
		if (!folderImages.exists()) {
			// We have to create this folder
			folderImages.mkdir();
		}	
	}
	
	/**
	 * delete a directory and all its contents
	 * @param directory File
	 */
	public static void deleteDirectory (File directory) {
        if (directory.exists() && directory.isDirectory()) { 
			File[] files = directory.listFiles(); 
			for(int i=0; i<files.length; i++) { 
				if(files[i].isDirectory()) { 
					deleteDirectory(files[i]); 
				} 
				else { 
					files[i].delete(); 
				}
			}
			directory.delete();
        }
	}
	
	public static String getCRC32(String filepath) {

        try {
            CheckedInputStream cis = null;
            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(filepath), new CRC32());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {
            }

            return Long.toHexString(cis.getChecksum().getValue()).toUpperCase();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";  //$NON-NLS-1$
    }
	
	/**
	 * returns the number of file (and not directory) contained in this directory
	 * this method also looks inside subdirectories
	 * @return
	 */
	public static int getFileNb(File topDirectory) {
		int nbFile=0;
		if (!topDirectory.isDirectory()) return 0; // We ensure it's a directory
		
		List<File> fileList = Arrays.asList(topDirectory.listFiles());
		Iterator<File> iterFiles = fileList.iterator();
		List<File> subdirectories = new ArrayList<File>();
		while (iterFiles.hasNext()) {
			File curFile = iterFiles.next();
			if (curFile.isDirectory()) {
				subdirectories.add(curFile);
			} else {
				nbFile++;
			}
		}
		Iterator<File> iterSubDir = subdirectories.iterator();
		while (iterSubDir.hasNext()) {
			File subDir = iterSubDir.next();
			nbFile += getFileNb(subDir);
		}
		return nbFile;
	}
	
	/**
	 * Move the specified file to the new filepath
	 * This method uses renameTo which may be quicker, but it has limitation (same disk, etc..)
	 * if it doesn't work we try an alternative way.
	 * @param filepath
	 * @param destPath
	 */
	public static void moveFile (File source, String destPath) {
		File destination = new File (destPath);
        if( !destination.exists() ) {
                // We try with renameTo
                boolean result = source.renameTo(destination);
                if( !result ) {
	                // We try to copy
	                result = true;
	                result &= copy(source.getAbsolutePath(),destination.getAbsolutePath());
	                if(result) result &= source.delete();
                }
        } else {
                JOptionPane.showMessageDialog(null, Messages.getString("FileToolkit.1"), Messages.getString("FileToolkit.2"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
        }  
	}
	
	/**
	 * Returns the extension of the file
	 * If no file extension an empty String is returned
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension (String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index!=-1) return fileName.substring(index).toLowerCase();
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Returns the name of the file without its extension
	 * If the file has no extension then filename is returned
	 * @param fileName
	 * @return
	 */
	public static String removeFileExtension (String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index!=-1) return fileName.substring(0, index);
		return fileName;
	}

	public static boolean copy (String path, String destPath) {
		boolean resultat = false;
		FileChannel in = null; // canal d'entrée
		FileChannel out = null; // canal de sortie
		 
		try {
		  // Init
		  in = new FileInputStream(path).getChannel();
		  out = new FileOutputStream(destPath).getChannel();
		 
		  // Copie depuis le in vers le out
		  in.transferTo(0, in.size(), out);
		  resultat = true;
		} catch (Exception e) {
		  e.printStackTrace(); // n'importe quelle exception
		} finally { // finalement on ferme
		  if(in != null) {
		  	try {
			  in.close();
			} catch (IOException e) {}
		  }
		  if(out != null) {
		  	try {
			  out.close();
			} catch (IOException e) {}
		  }
		}
		return resultat;
	}
	
}
