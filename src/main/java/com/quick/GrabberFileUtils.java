package com.quick;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GrabberFileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrabberFileUtils.class);


    static public File moveDownloadedFile(String downloadFolder, File destinationFile) {
        File downloadFolderFile = new File(downloadFolder);
        String[] downloadedFiles = downloadFolderFile.list();
        if (downloadedFiles.length != 1) {
            LOGGER.error("Unable to find downloaded file.");
            return null;
        }

        File sourceFile = null;
        try {
            sourceFile = Paths.get(downloadFolder, downloadedFiles[0]).toFile();
            LOGGER.debug("Coping file {} to {}", sourceFile.getAbsoluteFile(), destinationFile.getAbsoluteFile());
            FileUtils.copyFile(sourceFile, destinationFile);
            LOGGER.debug("Deleting file {}", sourceFile.getAbsoluteFile());
            sourceFile.delete();
            return destinationFile;
        } catch (IOException e) {
            LOGGER.debug("Exception during coping file {} to {}", sourceFile.getAbsoluteFile(), destinationFile.getAbsoluteFile());
            return null;
        }
    }
}
