/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.jboss.errai.common.client.logging.util.Console;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.promise.Promises;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientParser.parse;
import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

@ApplicationScoped
public class WorkItemDefinitionStandaloneClientService implements WorkItemDefinitionClientService {

    private static final String DEFAULT_ICON_DATA = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAADVklEQVQ4EYVUbWhTVxg+UAR/" +
                                                    "DP8oBX+4bsiYWpfOqggbosQfU1c3k9E6sTqVaFtxoKi0kwk1Rrkmy2rpjDVt0y/aZtckDTakTWckSW9tb9fsXvNxiSzWln" +
                                                    "vBdgkiVdrR2jzjBFrmJvrC4Xy8z/vwfh5C3iIs2Jz1WmPhWyBvVp35IrRaHJnIx8upgit6zrluv0m+XM0F52cVdZgb2172" +
                                                    "vXfDmy0JIQBWOMZ5w8l6R+zDPYxAvtKLZOdPM0RdjTW7zAtbSgzS2hJj/PBp1yO2PWwEsOq/ZMs6mkeZJlaYPq53YmtJG5" +
                                                    "rtaQw/VFBvk1EnyrjpTGFrcQtKj7tgY4WXrrFh42skFd2ezbrzruShYy6UVvgy7N0YnqUFCJE+dHV6wUV7MTUSwZ1gFEd/" +
                                                    "7s2UXnLihMXxqKz4X6Hxswn1llKjtO3HFth7JEzGOUixPNhrV+CuZzd8de9h0puP59Mx2INPUKhpxqZ9xjjHj21f8mb++b" +
                                                    "i68EuDdEtIIf1XBEn7Rtxp3wEINgAhxMM2+IY+wFPPWUzyU/jF+RSflRgkzCrqJRKa/fc15oUaqwx/ax8iHgLM38ZsaBCY" +
                                                    "+w0YHEJ8qBK+WgIu1odaUcaOXeYFXzUXzJKotDWFy/ebZFoFmsQu1oseaxH+5ngMBIN4FQhkFzgebmtRVk9xFE/tVLSPWJ" +
                                                    "bNuXY/4Pz0hGmmpl6Bv8OHqJUAI5WYC4zg1UAACPCI/16JexaCgY5+1DUqUOlMM9SOLWZzst7MQSkge/XisJjGs1QE/raP" +
                                                    "wVtWIjFUhcyNUSQeVOGeZSWCiVz8oYhoaEhj4wG9CGq3KAjJqg1FjHj4h1b86paQlAYxkciF5SZBtPprWG4TPG5fhzFpEI" +
                                                    "3uOD7/tg25GkYQ70/kL3IQ/zl/3qnybukbxoFD13szLncciiKC7exHyOlFV6cPSEXQ75Zw5Hpv5juzA0fKXLGLZ0Krl0jo" +
                                                    "ob0pfLW7R5i+YHZg04FWWBrSuNWo4AbtWKuMASGFg1UtOMg4YOsRpllb2PAaAb3Q2fmzKXyVKe+WiJYRacyf6EzZ2SnQmB" +
                                                    "dIkUH6SGOUdOWuZIdtlKH4/5EsPmjP+fOiIVlFk0azvzTFL8bVfDCp1lV4NhNCli3i37nT8hNtzTv/k38Ask842m9tZx8A" +
                                                    "AAAASUVORK5CYII=";

    private static Logger LOGGER = Logger.getLogger(BaseCanvasHandler.class.getName());
    private static final String RESOURCE_ALL_WID_PATTERN = "**/*.wid";

    private final Promises promises;
    private final WorkItemDefinitionCacheRegistry registry;
    private final ResourceContentService resourceContentService;

    // Cache the promise, as by definition will be performed just once,
    // so the available work item definitions will be also just registered once, by app.
    private Promise<Collection<WorkItemDefinition>> loader;

    @Inject
    public WorkItemDefinitionStandaloneClientService(final Promises promises,
                                                     final WorkItemDefinitionCacheRegistry registry,
                                                     final ResourceContentService resourceContentService) {

        this.promises = promises;
        this.registry = registry;
        this.resourceContentService = resourceContentService;
    }

    @PostConstruct
    public void init() {
        loader = allWorkItemsLoader();
    }

    @Produces
    @Default
    @Override
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Promise<Collection<WorkItemDefinition>> call(final Metadata input) {
        return loader;
    }

    @PreDestroy
    public void destroy() {
        registry.clear();
        loader = null;
    }

    private Promise<Collection<WorkItemDefinition>> allWorkItemsLoader() {
        log("Starting loading of all Work Items");
        return promises.create((success, failure) -> {
            log("Loading all Work Items");
            registry.clear();
            final List<WorkItemDefinition> loaded = new LinkedList<>();
            resourceContentService
                                  .list(RESOURCE_ALL_WID_PATTERN)
                                  .then(paths -> {
                                      if (paths.length > 0) {
                                          log("Work Items found at [" + paths + "]");
                                          promises.all(asList(paths),
                                                       path -> workItemsLoader(path, loaded))
                                                  .then(wids -> {
                                                      wids.forEach(registry::register);
                                                      success.onInvoke(wids);
                                                      return null;
                                                  })
                                                  .catch_(error -> {
                                                      failure.onInvoke(error);
                                                      return null;
                                                  });
                                      } else {
                                          log("NO Work Items found at [" + paths + "]");
                                          success.onInvoke(emptyList());
                                      }
                                      return promises.resolve();
                                  })
                                  .catch_(error -> {
                                      failure.onInvoke(error);
                                      return null;
                                  });
        });
    }

    @SuppressWarnings("unchecked")
    private Promise<Collection<WorkItemDefinition>> workItemsLoader(final String path,
                                                                    final Collection<WorkItemDefinition> loaded) {
        log("Processing [" + path + "]");
        if (nonEmpty(path)) {
            return resourceContentService
                                         .get(path)
                                         .then(value -> {
                                             log("Content for path = [" + value + "]");
                                             log("Loading Work Items for path [" + path + "]");
                                             final List<WorkItemDefinition> wids = parse(value);
                                             return promises.create((success, failure) -> {
                                                 promises.all(wids, this::workItemIconLoader)
                                                         .then(wid -> {
                                                             loaded.addAll(wids);
                                                             success.onInvoke(loaded);
                                                             return promises.resolve();
                                                         })
                                                         .catch_(error -> {
                                                             failure.onInvoke(error);
                                                             return null;
                                                         });
                                             });
                                         });
        }
        return promises.resolve(emptyList());
    }

    private Promise workItemIconLoader(final WorkItemDefinition wid) {
        Console.log("RUNING MY CODE MAN");
        final String iconUri = wid.getIconDefinition().getUri();
        log("Loading icon for URI [" + iconUri + "]");
        if (nonEmpty(iconUri)) {
            return resourceContentService
                                         .get(iconUri, ResourceContentOptions.binary())
                                         .then(iconData -> {
                                             log("Content for icon = [" + iconData + "]");
                                             if (nonEmpty(iconData)) {
                                                 wid.getIconDefinition().setIconData(iconDataUri(iconUri, iconData));
                                             }
                                             return promises.resolve(DEFAULT_ICON_DATA);
                                         }).catch_(error -> {
                                             log("Not able to load icon for URI " + iconUri);
                                             return Promise.resolve(DEFAULT_ICON_DATA);
                                         });
        }
        return promises.resolve(DEFAULT_ICON_DATA);
    }

    private String iconDataUri(String iconUri, String iconData) {
        String[] iconUriParts = iconUri.split("\\.");
        if (iconUriParts.length > 0) {
            int fileTypeIndex = iconUriParts.length - 1;
            String fileType = iconUriParts[fileTypeIndex];
            return "data:image/" + fileType + ";base64, " + iconData;
        }
        return DEFAULT_ICON_DATA;
    }

    private static void log(String s) {
        LOGGER.fine(s);
    }
}
