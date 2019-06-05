package org.varys.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.model.GitConfig;
import org.varys.common.model.exception.ConfigurationException;

import java.net.MalformedURLException;

public interface ModuleFactory {

    NotifierModule create(JsonNode node, GitConfig gitConfig) throws ConfigurationException, MalformedURLException;
}
