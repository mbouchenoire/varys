package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.varys.common.model.GitConfig;
import org.varys.common.model.LoggingConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ConfigFactory {

    public static LoggingConfig createLoggingConfig(File configFile) throws IOException {
        Log.debug("Retreiving logging config from file: {}...", configFile);
        final JsonNode configRootNode = new ObjectMapper().readValue(configFile, JsonNode.class);
        final JsonNode loggingNode = configRootNode.get("logging");
        final String loggingFilePath = loggingNode.get("directory").asText();
        final String loggingLevel = loggingNode.get("level").asText();
        return new LoggingConfig(loggingFilePath, loggingLevel);
    }

    public static GitConfig createGitConfig(File configFile) throws IOException {
        Log.debug("Retreiving git config from file: {}...", configFile);
        final JsonNode configRootNode = new ObjectMapper().readValue(configFile, JsonNode.class);
        final JsonNode gitNode = configRootNode.get("git");
        final String rootDirectory = gitNode.get("parent_directory").asText();
        return new GitConfig(rootDirectory);
    }

    public static Collection<JsonNode> findModuleNodes(File configFile) throws IOException {
        Log.debug("Retreiving modules from config file: {}...", configFile);
        final JsonNode configRootNode = new ObjectMapper().readValue(configFile, JsonNode.class);
        final Iterator<JsonNode> moduleConfigNodesIterator = configRootNode.get("modules").elements();
        final List<JsonNode> moduleConfigNodesList = new ArrayList<>();
        moduleConfigNodesIterator.forEachRemaining(moduleConfigNodesList::add);
        Log.debug("Retreived {} modules from config file", moduleConfigNodesList.size());
        return moduleConfigNodesList;
    }
}
