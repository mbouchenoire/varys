/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.varys.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class CacheService {

    private static final String TEMP_DIR_SYSTEM_ROOT = System.getProperty("java.io.tmpdir");
    private static final String TEMP_DIR_APP_ROOT_NAME = "varys";

    private final File tempDirectoryModuleRoot;
    private final ObjectMapper objectMapper;

    public CacheService(String moduleName) {
        final File tempDirectoryAppRoot = new File(TEMP_DIR_SYSTEM_ROOT, TEMP_DIR_APP_ROOT_NAME);
        this.tempDirectoryModuleRoot = new File(tempDirectoryAppRoot, moduleName);
        final boolean created = this.tempDirectoryModuleRoot.mkdirs();

        if (created) {
            Logger.debug("Created module temp directory: " + this.tempDirectoryModuleRoot.getAbsolutePath());
        }

        this.objectMapper = new ObjectMapper();
    }

    private File buildCacheFile(String path) {
        return new File(this.tempDirectoryModuleRoot, path + ".json");
    }

    public File getRootDirectory() {
        return tempDirectoryModuleRoot;
    }

    public void clear() {
        Logger.warn("Clearing cache for module root '{}'...", this.tempDirectoryModuleRoot);

        try {
            Files.walkFileTree(this.tempDirectoryModuleRoot.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Logger.error(e, "Failed to clear module cache");
        }
    }

    public void delete(String path) {
        Logger.debug("Deleting cached object with path={}...", path);

        final File cachedObjectFile = buildCacheFile(path);

        try {
            Files.delete(cachedObjectFile.toPath());
        } catch (IOException e) {
            Logger.error(e, "Failed to delete cache file: {}", cachedObjectFile.getAbsolutePath());
        }
    }

    public void save(String path, Object object) {
        Logger.trace("Saving cached object={} with path={}...", object, path);

        try {
            final File cachedObjectFile = buildCacheFile(path);

            final boolean newDirectory = cachedObjectFile.getParentFile().mkdirs();

            if (!newDirectory) {
                Logger.trace("Cache file parent directory already exists: {}",
                        cachedObjectFile.getParentFile().getAbsolutePath());
            }

            deleteIfExists(cachedObjectFile);

            final boolean newFile = cachedObjectFile.createNewFile();

            if (!newFile) {
                Logger.error("Failed to findUsable empty cache file: {}",
                        cachedObjectFile.getAbsolutePath());
            }

            final ObjectWriter writer = this.objectMapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(cachedObjectFile, object);

            Logger.debug(
                    "Successfuly cached object '{}' in file '{}'",
                    object,
                    cachedObjectFile.getAbsolutePath());
        } catch (JsonProcessingException e) {
            Logger.error(e,"Failed to serialize object to cache: {}", object);
        } catch (IOException e) {
            Logger.error(e, "Failed to findUsable cache file: {}", path);
        }
    }

    private static void deleteIfExists(File cachedObjectFile) {
        try {
            Files.delete(cachedObjectFile.toPath());
            Logger.debug("Cache file already existed and has been deleted: {}", cachedObjectFile);
        } catch (IOException e) {
            Logger.debug(e, "Cache file did not already exist and will be created: {}", cachedObjectFile);
        }
    }

    public boolean isCached(String path) {
        return buildCacheFile(path).exists();
    }

    public <T> Optional<T> get(File file, Class<T> cacheClass) {
        Logger.debug("Retreiving cached {} from file={}", cacheClass.getSimpleName(), file);

        try {
            final T value = this.objectMapper.readValue(file, cacheClass);
            return Optional.ofNullable(value);
        } catch (IOException e) {
            Logger.error(e, "Failed to retreive cached object (file: {})", file);
            deleteFile(file);
            return Optional.empty();
        }
    }

    private static void deleteFile(File file) {
        try {
            Files.delete(file.toPath());
            Logger.debug("Successfully deleted cache file (file: {})", file);
        } catch (IOException e) {
            Logger.error(e,"Failed to delete cache file ({})", file);
        }
    }

    public <T> Optional<T> get(String path, Class<T> cacheClass) {
        final File cacheFile = buildCacheFile(path);

        if (!cacheFile.exists()) {
            return Optional.empty();
        }

        return this.get(cacheFile, cacheClass);
    }
}
