/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.backend.util.UIDGenerator;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormEditorServiceImpl extends KieService<FormModelerContent> implements FormEditorService {

    private FieldManager fieldManager;
    private FormModelHandlerManager modelHandlerManager;
    private FormDefinitionSerializer formDefinitionSerializer;
    private VFSFormFinderService vfsFormFinderService;
    private DeleteService deleteService;
    private RenameService renameService;
    private Logger log = LoggerFactory.getLogger(FormEditorServiceImpl.class);
    private IOService ioService;
    private SessionInfo sessionInfo;
    private Event<ResourceOpenedEvent> resourceOpenedEvent;
    private CommentedOptionFactory commentedOptionFactory;

    @Inject
    public FormEditorServiceImpl(@Named("ioStrategy") IOService ioService,
                                 SessionInfo sessionInfo,
                                 Event<ResourceOpenedEvent> resourceOpenedEvent,
                                 FieldManager fieldManager,
                                 FormModelHandlerManager modelHandlerManager,
                                 KieProjectService projectService,
                                 FormDefinitionSerializer formDefinitionSerializer,
                                 VFSFormFinderService vfsFormFinderService,
                                 DeleteService deleteService,
                                 CommentedOptionFactory commentedOptionFactory,
                                 RenameService renameService) {
        this.ioService = ioService;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.fieldManager = fieldManager;
        this.modelHandlerManager = modelHandlerManager;
        this.projectService = projectService;
        this.formDefinitionSerializer = formDefinitionSerializer;
        this.vfsFormFinderService = vfsFormFinderService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.deleteService = deleteService;
        this.renameService = renameService;
    }

    @Override
    public FormModelerContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    public Path createForm(Path path,
                           String formName,
                           FormModel formModel) {
        org.uberfire.java.nio.file.Path nioPath = Paths.convert(path).resolve(formName);
        try {
            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }
            FormDefinition form = new FormDefinition(formModel);

            form.setId(UIDGenerator.generateUID());

            form.setName(formName.substring(0,
                                            formName.lastIndexOf(".")));

            form.setLayoutTemplate(new LayoutTemplate());

            ioService.write(nioPath,
                            formDefinitionSerializer.serialize(form),
                            commentedOptionFactory.makeCommentedOption(""));

            return Paths.convert(nioPath);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void delete(Path path,
                       String comment) {
        try {
            KieProject project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("Form : " + path.toURI() + " does not belong to a valid project");
                return;
            }

            deleteService.delete(path,
                                 comment);
        } catch (final Exception e) {
            logger.error("Form: " + path.toURI() + " couldn't be deleted due to the following error. ",
                         e);
        }
    }

    @Override
    public Path save(Path path,
                     FormModelerContent content,
                     Metadata metadata,
                     String comment) {
        ioService.write(Paths.convert(path),
                        formDefinitionSerializer.serialize(content.getDefinition()),
                        metadataService.setUpAttributes(path,
                                                        metadata),
                        commentedOptionFactory.makeCommentedOption(comment));

        return path;
    }

    @Override
    public FormModelerContent rename(Path path,
                                     String newFileName,
                                     String commitMessage,
                                     boolean saveBeforeRenaming,
                                     FormModelerContent content,
                                     Metadata metadata) {

        FormModelerContent contentToSave = content;
        if (!saveBeforeRenaming) {
            contentToSave = constructContent(path,
                                             content.getOverview());
        }

        contentToSave.getDefinition().setName(newFileName);

        save(path,
             contentToSave,
             metadata,
             commitMessage);

        renameService.rename(path,
                             newFileName,
                             commitMessage);

        return contentToSave;
    }

    @Override
    public Path copy(Path path,
                     String newFileName,
                     String commitMessage,
                     boolean saveBeforeCopy,
                     FormModelerContent content,
                     Metadata metadata) {

        if (saveBeforeCopy) {
            save(path, content, metadata, "Save before copy");
        }

        FormDefinition form = copyFormDefinition(content.getDefinition());
        form.setName(newFileName);

        if(!newFileName.endsWith(FormResourceTypeDefinition.EXTENSION)) {
            newFileName += "." + FormResourceTypeDefinition.EXTENSION;
        }

        org.uberfire.java.nio.file.Path nioPath = Paths.convert(path).getParent().resolve(newFileName);

        ioService.write(nioPath, formDefinitionSerializer.serialize(form), commentedOptionFactory.makeCommentedOption(commitMessage));

        return Paths.convert(nioPath);
    }

    @Override
    protected FormModelerContent constructContent(Path path,
                                                  Overview overview) {
        try {
            org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

            FormDefinition form = findForm(nioPath);

            FormModelerContent formModelConent = new FormModelerContent();
            formModelConent.setDefinition(form);
            formModelConent.setPath(path);
            formModelConent.setOverview(overview);

            FormEditorRenderingContext context = createRenderingContext(form,
                                                                        path);

            formModelConent.setRenderingContext(context);

            if (Optional.ofNullable(form.getModel()).isPresent()) {

                FormModel formModel = form.getModel();

                Optional<FormModelHandler> modelHandlerOptional = getHandlerForForm(form,
                                                                                    path);
                if (modelHandlerOptional.isPresent()) {

                    FormModelHandler formModelHandler = modelHandlerOptional.get();

                    FormModelSynchronizationResult synchronizationResult = formModelHandler.synchronizeFormModel();

                    formModel.getProperties().forEach(property -> {
                        Optional<FieldDefinition> fieldOptional = Optional.ofNullable(form.getFieldByBinding(property.getName()));
                        if (!fieldOptional.isPresent()) {
                            synchronizationResult.resolveConflict(property.getName());
                        }
                    });

                    formModelConent.setSynchronizationResult(synchronizationResult);
                }
            }

            resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                             sessionInfo));

            return formModelConent;
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(),
                     e);
        }
        return null;
    }

    protected FormEditorRenderingContext createRenderingContext(FormDefinition form,
                                                                Path formPath) {
        FormEditorRenderingContext context = new FormEditorRenderingContext(formPath);
        context.setRootForm(form);

        List<FormDefinition> allForms = vfsFormFinderService.findAllForms(formPath);

        for (FormDefinition vfsForm : allForms) {
            if (!vfsForm.getId().equals(form.getId())) {
                context.getAvailableForms().put(vfsForm.getId(),
                                                vfsForm);
            }
        }
        return context;
    }

    protected FormDefinition findForm(org.uberfire.java.nio.file.Path path) throws Exception {
        String template = ioService.readAllString(path).trim();

        FormDefinition form = formDefinitionSerializer.deserialize(template);
        if (form == null) {
            form = new FormDefinition();
            form.setId(UIDGenerator.generateUID());
        }

        return form;
    }

    protected Optional<FormModelHandler> getHandlerForForm(FormDefinition form,
                                                           Path path) {

        Optional<FormModelHandler> optional = Optional.ofNullable(modelHandlerManager.getFormModelHandler(form.getModel().getClass()));

        if (optional.isPresent()) {
            optional.get().init(form.getModel(),
                                path);
        }
        return optional;
    }

    public FormDefinition copyFormDefinition(FormDefinition originalForm) {
        FormDefinition copyForm = new FormDefinition(originalForm.getModel());

        String newId = UIDGenerator.generateUID();

        copyForm.setId(newId);
        copyForm.setName(originalForm.getName());

        Map<String, String> fieldsRegistry = new HashMap<>();

        originalForm.getFields().forEach(originalField -> {
            FieldDefinition newField = fieldManager.getFieldFromProvider(originalField.getFieldType().getTypeName(), originalField.getFieldTypeInfo());

            newField.copyFrom(originalField);
            newField.setName(originalField.getName());

            copyForm.getFields().add(newField);

            fieldsRegistry.put(originalField.getId(), newField.getId());

        });

        LayoutTemplate originalTemplate = originalForm.getLayoutTemplate();

        LayoutTemplate copyTemplate = new LayoutTemplate(originalTemplate.getName(),
                                                        originalTemplate.getLayoutProperties(),
                                                        originalTemplate.getStyle());

        copyTemplate.getRows().addAll(copyRows(originalTemplate.getRows(), copyForm.getId(), fieldsRegistry));

        copyForm.setLayoutTemplate(copyTemplate);

        return copyForm;
    }

    private List<LayoutRow> copyRows(Collection<LayoutRow> originalRows, String formId, Map<String, String> fieldsRegistry) {
        List<LayoutRow> copyRows = new ArrayList<>();

        originalRows.forEach(originalRow -> {
            LayoutRow copyRow = new LayoutRow();

            copyRow.add(copyColumns(originalRow.getLayoutColumns(), formId, fieldsRegistry));

            copyRows.add(copyRow);
        });

        return copyRows;
    }

    private List<LayoutColumn> copyColumns(Collection<LayoutColumn> originalColumns, String formId, Map<String, String> fieldsRegistry) {
        List<LayoutColumn> copyColumns = new ArrayList<>();

        originalColumns.forEach(originalColumn -> {
            LayoutColumn copyColumn = new LayoutColumn(originalColumn.getSpan());

            copyColumn.getLayoutComponents().addAll(copyComponents(originalColumn.getLayoutComponents(), formId, fieldsRegistry));
            copyColumn.getRows().addAll(copyRows(originalColumn.getRows(), formId, fieldsRegistry));

            copyColumns.add(copyColumn);
        });

        return copyColumns;
    }

    private List<LayoutComponent> copyComponents(Collection<LayoutComponent> originalComponents, String formId, Map<String, String> fieldsRegistry) {
        List<LayoutComponent> copyComponents = new ArrayList<>();

        originalComponents.forEach(originalComponent -> {
            LayoutComponent copyComponent = new LayoutComponent(originalComponent.getDragTypeName());

            copyComponent.addProperties(originalComponent.getProperties());

            if(copyComponent.getDragTypeName().equals(StaticFormLayoutTemplateGenerator.DRAGGABLE_TYPE)) {
                copyComponent.addProperty(FormLayoutComponent.FORM_ID, formId);
                String fieldId = copyComponent.getProperties().get(FormLayoutComponent.FIELD_ID);
                copyComponent.addProperty(FormLayoutComponent.FIELD_ID, fieldsRegistry.get(fieldId));
            }

            copyComponents.add(copyComponent);
        });

        return copyComponents;
    }
}
