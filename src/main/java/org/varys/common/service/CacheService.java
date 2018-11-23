package org.varys.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CacheService {

    private static final String TEMP_DIR_SYSTEM_ROOT = System.getProperty("java.io.tmpdir");
    private static final String TEMP_DIR_APP_ROOT_NAME = "varys";

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

            final ObjectMapper mapper = new ObjectMapper();
            final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(cachedObjectFile, object);

            Log.debug(
                    "Successfuly cached object '{}' in file '{}'",
                    object,
                    cachedObjectFile.getAbsolutePath());
        } catch (JsonProcessingException e) {
            Log.error(e,"Failed to serialize object to cache: {}", object);
        } catch (IOException e) {
            Log.error(e, "Failed to create cache file: {}", path);
        }
    }

    public boolean isCached(String path) {
        return buildCacheFile(path).exists();
    }

    public <T> Optional<T> get(File file, Class<T> cacheClass) {
        Log.debug("Retreiving cached {} from file={}", cacheClass.getSimpleName(), file);

        try {
            final T value = new ObjectMapper().readValue(file, cacheClass);
            return Optional.ofNullable(value);
        } catch (IOException e) {
            Log.error(e, "Failed to retreive cached object (file: {})", file);
            deleteFile(file);
            return Optional.empty();
        }
    }

    private static void deleteFile(File file) {
        if (file.delete()) {
            Log.debug("Successfully deleted cache file (file: {})", file);
        } else {
            Log.error("Failed to delete cache file ({})", file);
        }
    }

    private static void convertToUtf8(Path path) {
        try {
            Log.debug("Converting file '{}' to UTF-8...", path);
            final ByteBuffer bb = ByteBuffer.wrap(Files.readAllBytes(path));
            final CharBuffer cb = Charset.forName("windows-1252").decode(bb);
            final ByteBuffer encodedBb = Charset.forName("UTF-8").encode(cb);
            Files.write(path, encodedBb.array());
        } catch (IOException e) {
            Log.error(e, "Failed to convert file to UTF-8");
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
