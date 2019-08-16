/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.admin.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.helper.PluginHelper.ArtifactType;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPluginAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminPluginAction.class);

    @Resource
    private PluginHelper pluginHelper;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNamePlugin()));
        // runtime.registerData("availableArtifactItems", getAllAvailableArtifacts());
        runtime.registerData("installedArtifactItems", getAllInstalledArtifacts());
    }

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    public HtmlResponse delete(final DeleteForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminPlugin_AdminPluginJsp);
        });
        verifyToken(() -> {
            return asHtml(path_AdminPlugin_AdminPluginJsp);
        });
        Artifact artifact = new Artifact(form.name, form.version, null);
        try {
            pluginHelper.deleteInstalledArtifact(artifact);
            saveInfo(messages -> messages.addSuccessDeletePlugin(GLOBAL, artifact.getFileName()));
        } catch (Exception e) {
            logger.warn("Failed to delete " + artifact.getFileName(), e);
            saveError(messages -> messages.addErrorsFailedToDeletePlugin(GLOBAL, artifact.getFileName()));
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse install(final InstallForm form) {
        // TODO
        return redirect(getClass());
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminPlugin_AdminPluginJsp).useForm(DeleteForm.class);
    }

    private List<Map<String, String>> getAllAvailableArtifacts() {
        final List<Map<String, String>> result = new ArrayList<>();
        for (PluginHelper.ArtifactType artifactType : PluginHelper.ArtifactType.values()) {
            result.addAll(Arrays.stream(pluginHelper.getAvailableArtifacts(artifactType)).map(artifact -> beanToMap(artifact))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    private List<Map<String, String>> getAllInstalledArtifacts() {
        final List<Map<String, String>> result = new ArrayList<>();
        for (PluginHelper.ArtifactType artifactType : PluginHelper.ArtifactType.values()) {
            result.addAll(Arrays.stream(pluginHelper.getInstalledArtifacts(artifactType)).map(artifact -> beanToMap(artifact))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    private Map<String, String> beanToMap(final Artifact artifact) {
        Map<String, String> item = new HashMap<>();
        item.put("type", ArtifactType.getType(artifact).getId());
        item.put("name", artifact.getName());
        item.put("version", artifact.getVersion());
        return item;
    }
}