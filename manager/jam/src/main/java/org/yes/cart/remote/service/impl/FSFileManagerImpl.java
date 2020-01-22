/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.remote.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkcommon.service.ExportDirectorService;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.remote.service.FileManager;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.impl.ZipByteArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 09:03
 */
public class FSFileManagerImpl implements FileManager {


    private final Logger LOG = LoggerFactory.getLogger(FSFileManagerImpl.class);

    private final ImportDirectorService importDirectorService;
    private final ExportDirectorService exportDirectorService;
    private final List<String> allowedPaths;


    public FSFileManagerImpl(final ImportDirectorService importDirectorService,
                             final ExportDirectorService exportDirectorService,
                             final String allowedPathsCsv) {
        this.importDirectorService = importDirectorService;
        this.exportDirectorService = exportDirectorService;
        this.allowedPaths = new ArrayList<>();
        for (final String path : StringUtils.split(allowedPathsCsv, ',')) {
            this.allowedPaths.add(path.trim());
        }
    }

    private List<MutablePair<String, String>> scanDir(File dir, String prefix) {

        final List<MutablePair<String, String>> files = new ArrayList<>();

        final File[] dirFiles = dir.listFiles();
        if (dirFiles != null) {
            for (final File file : dirFiles) {

                if (file.isDirectory()) {
                    files.addAll(scanDir(file, prefix.concat("/").concat(file.getName())));
                } else {
                    files.add(MutablePair.of(
                            file.getAbsolutePath(),
                            prefix.concat("/").concat(file.getName())
                    ));
                }

            }
        }
        return files;
    }

    private static final Comparator<MutablePair<String, String>> SORT_BY_SYMBOLIC_NAME = (o1, o2) -> o1.getSecond().compareTo(o2.getSecond());

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MutablePair<String, String>> list(final String mode) throws IOException {

        final SecurityContext sc = SecurityContextHolder.getContext();

        if (sc == null || sc.getAuthentication() == null || sc.getAuthentication().getName() == null) {
            throw new AccessDeniedException("Access denied");
        }

        final String userDir = sc.getAuthentication().getName();

        final List<MutablePair<String, String>> list = new ArrayList<>();

        if ("import".equals(mode) || "*".equals(mode)) {

            final File importRoot = new File(importDirectorService.getImportDirectory() + File.separator + userDir);

            if (importRoot.exists()) {

                final List<MutablePair<String, String>> importFiles = scanDir(importRoot, "import:/");
                importFiles.sort(SORT_BY_SYMBOLIC_NAME);
                list.addAll(importFiles);

            }

        }

        if ("export".equals(mode) || "*".equals(mode)) {

            final File exportRoot = new File(exportDirectorService.getExportDirectory() + File.separator + userDir);

            if (exportRoot.exists()) {

                final List<MutablePair<String, String>> importFiles = scanDir(exportRoot, "export:/");
                importFiles.sort(SORT_BY_SYMBOLIC_NAME);
                list.addAll(importFiles);

            }

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String upload(final byte[] bytes, final String fileName) throws IOException {

        final String importRoot = importDirectorService.getImportDirectory();

        final String timestamp = DateUtils.impexFileTimestamp();
        final SecurityContext sc = SecurityContextHolder.getContext();

        final File dir;
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication().getName() != null) {
            dir = new File(importRoot + File.separator + sc.getAuthentication().getName() + File.separator + timestamp);
        } else {
            dir = new File(importRoot + File.separator + "anonymous" + File.separator + timestamp);
        }

        dir.mkdirs();

        if (dir.exists()) {

            FileOutputStream fos = null;
            try {
                final File file = new File(dir.getAbsolutePath() + File.separator + fileName);
                if (file.exists()) {
                    throw new IllegalArgumentException("File: " + file.getName() + " is already being processed. If this is a different file - rename it and try again.");
                }
                file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write(bytes);
                return file.getAbsolutePath();
            } catch (IOException ioe) {
                LOG.error("Error during file upload", ioe);
                if (fos != null) {
                    fos.close();
                }
                throw ioe;
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        } else {
            throw new IOException("Unable to create directory: " + importRoot);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] download(final String fileName) throws IOException {

        final String exportRoot = this.exportDirectorService.getExportDirectory();

        final File fileToDownload;
        if (fileName.startsWith("/") || fileName.startsWith(exportRoot)) {

            boolean allowed = false;
            for (final String path : this.allowedPaths) {
                if (fileName.startsWith(path)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed && !fileName.startsWith(exportRoot)) {
                LOG.warn("Attempted to download file from {} ... access denied", fileName);
                throw new AccessDeniedException("Downloading files from specified location is prohibited");
            }

            fileToDownload = new File(fileName);

        } else {

            fileToDownload = new File(exportRoot  + File.separator + fileName);

        }

        if (fileToDownload.exists()) {

            return ZipByteArrayUtils.fileToZipBytes(fileToDownload);

        }

        throw new IOException("File " + fileToDownload.getAbsolutePath() + " not found");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final String fileName) throws IOException {

        final SecurityContext sc = SecurityContextHolder.getContext();

        if (sc == null || sc.getAuthentication() == null || sc.getAuthentication().getName() == null) {
            throw new AccessDeniedException("Access denied");
        }

        final String userDir = sc.getAuthentication().getName();

        final List<MutablePair<String, String>> list = new ArrayList<>();

        final String importRoot = importDirectorService.getImportDirectory() + File.separator + userDir;

        if (fileName.startsWith(importRoot)) {
            new File(fileName).delete();
            return;
        }

        final String exportRoot = exportDirectorService.getExportDirectory() + File.separator + userDir;

        if (fileName.startsWith(exportRoot)) {
            new File(fileName).delete();
            return;
        }

        throw new AccessDeniedException("Access denied");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String home() throws IOException {

        final SecurityContext sc = SecurityContextHolder.getContext();

        if (sc == null || sc.getAuthentication() == null || sc.getAuthentication().getName() == null) {
            throw new AccessDeniedException("Access denied");
        }

        final String userDir = sc.getAuthentication().getName();

        final File exportRoot = new File(exportDirectorService.getExportDirectory() + File.separator + userDir);

        if (!exportRoot.exists()) {
            exportRoot.mkdirs();
        }

        return exportRoot.getAbsolutePath();
    }
}
