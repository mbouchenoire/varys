package org.varys.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class CacheService {

    private static final String TEMP_DIR_SYSTEM_ROOT = System.getProperty("java.io.tmpdir");
    private static final String TEMP_DIR_APP_ROOT_NAME = "devtoast";

    private final File tempDirectoryModuleRoot;

    CacheService(String moduleName) {
        final File tempDirectoryAppRoot = new File(TEMP_DIR_SYSTEM_ROOT, TEMP_DIR_APP_ROOT_NAME);
        this.tempDirectoryModuleRoot = new File(tempDirectoryAppRoot, moduleName);
        final boolean created = this.tempDirectoryModuleRoot.mkdirs();

        if (created) {
            Log.debug("Created module temp directory: " + this.tempDirectoryModuleRoot.getAbsolutePath());
        }
    }

    private File buildCacheFile(String path) {
        return new File(this.tempDirectoryModuleRoot, path + ".json");
    }

    public File getRootDirectory() {
        return tempDirectoryModuleRoot;
    }

    public void delete(String path) {
        Log.debug("Deleting cached object with path={}...", path);

        final File cachedObjectFile = buildCacheFile(path);
        final boolean isDeleted = cachedObjectFile.delete();

        if (!isDeleted) {
            Log.error("Failed to delete cache file: " + cachedObjectFile.getAbsolutePath());
        }
    }

    public void save(String path, Object object) {
        Log.trace("Saving cached object={} with path={}...", object, path);

        try {
            final File cachedObjectFile = buildCacheFile(path);

            final boolean newDirectory = cachedObjectFile.getParentFile().mkdirs();

            if (!newDirectory) {
                Log.trace("Cache file parent directory already exists: {}",
                        cachedObjectFile.getParentFile().getAbsolutePath());
            }

            final boolean isDeleted = cachedObjectFile.delete();

            if (isDeleted) {
                Log.debug("Cache file already existed and has been deleted: {}", cachedObjectFile);
            }

            final boolean newFile = cachedObjectFile.createNewFile();

            if (!newFile) {
                Log.error("Failed to create empty cache file: {}",
                        cachedObjectFile.getAbsolutePath());
            }

            try (PrintWriter out = new PrintWriter(cachedObjectFile)) {
                final String cachedObjectString = new ObjectMapper().writeValueAsString(object);

                out.println(cachedObjectString);

                Log.debug(
                        "Successfuly cached object '{}' in file '{}'",
                        cachedObjectString,
                        cachedObjectFile.getAbsolutePath());
            }
        } catch (JsonProcessingException e) {
            Log.error(e,"Failed to serialize object to cache: {}", object);
        } catch (IOException e) {
            Log.error(e, "Failed to create cache file: {}", path);
        }
    }

    public boolean isCached(String path) {
        return buildCacheFile(path).exists();
    }

    public <T> T get(File file, Class<T> cacheClass) {
        Log.debug("Retreiving cached {} from file={}", cacheClass.getSimpleName(), file);

        try {
            return new ObjectMapper().readValue(file, cacheClass);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to deserialize cached object from file: " + file.getAbsolutePath());
        }
    }

    public <T> Optional<T> get(String path, Class<T> cacheClass) {
        final File cacheFile = buildCacheFile(path);

        if (!cacheFile.exists()) {
            return Optional.empty();
        }

        final T object = this.get(cacheFile, cacheClass);

        return Optional.of(object);
    }
}
