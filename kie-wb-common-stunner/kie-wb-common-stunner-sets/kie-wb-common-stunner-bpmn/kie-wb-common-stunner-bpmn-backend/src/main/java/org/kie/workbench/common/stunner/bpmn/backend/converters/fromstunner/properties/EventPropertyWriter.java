/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public abstract class EventPropertyWriter extends PropertyWriter {

    public EventPropertyWriter(Event event, VariableScope variableScope) {
        super(event, variableScope);
    }

    public abstract void setAssignmentsInfo(AssignmentsInfo assignmentsInfo);

    public void addMessage(MessageRef messageRef) {
        MessageEventDefinition messageEventDefinition =
                bpmn2.createMessageEventDefinition();
        addEventDefinition(messageEventDefinition);

        String name = messageRef.getValue();
        if (name == null || name.isEmpty()) {
            return;
        }

        ItemDefinition itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(Ids.messageItem(name));

        Message message = bpmn2.createMessage();
        message.setName(name);
        message.setItemRef(itemDefinition);
        messageEventDefinition.setMessageRef(message);
        CustomAttribute.msgref.of(messageEventDefinition).set(name);

        addItemDefinition(itemDefinition);
        addRootElement(message);
    }

    public void addSignal(SignalRef signalRef) {
        SignalEventDefinition signalEventDefinition =
                bpmn2.createSignalEventDefinition();
        addEventDefinition(signalEventDefinition);

        Signal signal = bpmn2.createSignal();
        String name = signalRef.getValue();
        if (name == null || name.isEmpty()) {
            return;
        }

        signal.setName(name);
        signal.setId(Ids.fromString(name));
        signalEventDefinition.setSignalRef(signal.getId());

        addRootElement(signal);
    }

    public void addSignalScope(SignalScope signalScope) {
        CustomElement.scope.of(flowElement).set(signalScope.getValue());
    }

    public void addError(ErrorRef errorRef) {
        Error error = bpmn2.createError();
        ErrorEventDefinition errorEventDefinition =
                bpmn2.createErrorEventDefinition();
        addEventDefinition(errorEventDefinition);

        String errorCode = errorRef.getValue();
        if (errorCode == null || errorCode.isEmpty()) {
            return;
        }

        error.setId(errorCode);
        error.setErrorCode(errorCode);
        errorEventDefinition.setErrorRef(error);

        CustomAttribute.errorName.of(errorEventDefinition).set(errorCode);
        addRootElement(error);
    }

    public void addTerminate() {
        TerminateEventDefinition terminateEventDefinition =
                bpmn2.createTerminateEventDefinition();
        addEventDefinition(terminateEventDefinition);
    }

    public void addTimer(TimerSettings timerSettings) {
        TimerEventDefinition eventDefinition =
                bpmn2.createTimerEventDefinition();

        TimerSettingsValue timerSettingsValue = timerSettings.getValue();

        String date = timerSettingsValue.getTimeDate();
        if (date != null) {
            FormalExpression timeDate = bpmn2.createFormalExpression();
            timeDate.setBody(date);
            eventDefinition.setTimeDate(timeDate);
        }

        String duration = timerSettingsValue.getTimeDuration();
        if (duration != null) {
            FormalExpression timeDuration = bpmn2.createFormalExpression();
            timeDuration.setBody(duration);
            eventDefinition.setTimeDuration(timeDuration);
        }

        String cycle = timerSettingsValue.getTimeCycle();
        String cycleLanguage = timerSettingsValue.getTimeCycleLanguage();
        if (cycle != null && cycleLanguage != null) {
            FormalExpression timeCycleExpression = bpmn2.createFormalExpression();
            timeCycleExpression.setBody(cycle);
            timeCycleExpression.setLanguage(cycleLanguage);
            eventDefinition.setTimeCycle(timeCycleExpression);
        }

        addEventDefinition(eventDefinition);
    }

    protected abstract void addEventDefinition(EventDefinition eventDefinition);
}
/*
{"ToSubject":"org.kie.workbench.common.stunner.project.service.ProjectDiagramService:RPC","CommandType":"saveOrUpdateSvg:org.uberfire.backend.vfs.Path:java.lang.String:","Qualifiers":{"^EncodedType":"java.util.ArrayList","^ObjectID":"1","^Value":[]},"ReplyTo":"org.kie.workbench.common.stunner.project.service.ProjectDiagramService:RPC.saveOrUpdateSvg:org.uberfire.backend.vfs.Path:java.lang.String::75:RespondTo:RPC","ErrorTo":"org.kie.workbench.common.stunner.project.service.ProjectDiagramService:RPC.saveOrUpdateSvg:org.uberfire.backend.vfs.Path:java.lang.String::75:Errors:RPC","AdditionalDetails":"<tt>
org.jboss.errai.bus.client.api.base.MessageDeliveryFailure: error invoking RPC endpoint public abstract org.uberfire.backend.vfs.Path org.kie.workbench.common.stunner.core.service.BaseDiagramService.saveOrUpdateSvg(org.uberfire.backend.vfs.Path,java.lang.String)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.AbstractRPCMethodCallback.invokeMethodFromMessage(AbstractRPCMethodCallback.java:75)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.ValueReplyRPCEndpointCallback.callback(ValueReplyRPCEndpointCallback.java:40)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.RemoteServiceCallback.callback(RemoteServiceCallback.java:54)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.cdi.server.CDIExtensionPoints$2.callback(CDIExtensionPoints.java:448)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.DeliveryPlan.deliver(DeliveryPlan.java:47)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.ServerMessageBusImpl.sendGlobal(ServerMessageBusImpl.java:297)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.SimpleDispatcher.dispatchGlobal(SimpleDispatcher.java:46)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.service.ErraiServiceImpl.store(ErraiServiceImpl.java:96)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.service.ErraiServiceImpl.store(ErraiServiceImpl.java:113)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.servlet.DefaultBlockingServlet.doPost(DefaultBlockingServlet.java:144)
&nbsp;&nbsp;&nbsp;&nbsp;at javax.servlet.http.HttpServlet.service(HttpServlet.java:707)
&nbsp;&nbsp;&nbsp;&nbsp;at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:85)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:129)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.websockets.jsr.JsrWebSocketFilter.doFilter(JsrWebSocketFilter.java:130)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at org.uberfire.ext.security.server.SecureHeadersFilter.doFilter(SecureHeadersFilter.java:110)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at org.uberfire.ext.security.server.SecurityIntegrationFilter.doFilter(SecurityIntegrationFilter.java:70)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.SecurityContextAssociationHandler.handleRequest(SecurityContextAssociationHandler.java:78)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.DisableCacheHandler.handleRequest(DisableCacheHandler.java:33)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AuthenticationConstraintHandler.handleRequest(AuthenticationConstraintHandler.java:53)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletSecurityConstraintHandler.handleRequest(ServletSecurityConstraintHandler.java:59)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.NotificationReceiverHandler.handleRequest(NotificationReceiverHandler.java:50)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.jacc.JACCContextIdHandler.handleRequest(JACCContextIdHandler.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.GlobalRequestControllerHandler.handleRequest(GlobalRequestControllerHandler.java:68)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:292)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.access$100(ServletInitialHandler.java:81)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:138)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:135)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ServletRequestContextThreadSetupAction$1.call(ServletRequestContextThreadSetupAction.java:48)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ContextClassLoaderSetupAction$1.call(ContextClassLoaderSetupAction.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.SecurityContextThreadSetupAction.lambda$create$0(SecurityContextThreadSetupAction.java:105)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:272)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.access$000(ServletInitialHandler.java:81)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$1.handleRequest(ServletInitialHandler.java:104)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.Connectors.executeRootHandler(Connectors.java:326)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.HttpServerExchange$1.run(HttpServerExchange.java:812)
&nbsp;&nbsp;&nbsp;&nbsp;at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
&nbsp;&nbsp;&nbsp;&nbsp;at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
&nbsp;&nbsp;&nbsp;&nbsp;at java.lang.Thread.run(Thread.java:748)
Caused by: org.kie.workbench.common.stunner.core.diagram.DiagramParsingException: <No Message>
&nbsp;&nbsp;&nbsp;&nbsp;at org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService.getDiagramByPath(AbstractVFSDiagramService.java:178)
&nbsp;&nbsp;&nbsp;&nbsp;at org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService.saveOrUpdateSvg(AbstractVFSDiagramService.java:209)
&nbsp;&nbsp;&nbsp;&nbsp;at org.kie.workbench.common.stunner.project.backend.service.ProjectDiagramServiceImpl.saveOrUpdateSvg(ProjectDiagramServiceImpl.java:180)
&nbsp;&nbsp;&nbsp;&nbsp;at org.kie.workbench.common.stunner.project.backend.service.ProjectDiagramServiceImpl$Proxy$_$$_WeldClientProxy.saveOrUpdateSvg(Unknown Source)
&nbsp;&nbsp;&nbsp;&nbsp;at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
&nbsp;&nbsp;&nbsp;&nbsp;at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
&nbsp;&nbsp;&nbsp;&nbsp;at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at java.lang.reflect.Method.invoke(Method.java:498)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.AbstractRPCMethodCallback.invokeMethodFromMessage(AbstractRPCMethodCallback.java:65)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.ValueReplyRPCEndpointCallback.callback(ValueReplyRPCEndpointCallback.java:40)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.io.RemoteServiceCallback.callback(RemoteServiceCallback.java:54)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.cdi.server.CDIExtensionPoints$2.callback(CDIExtensionPoints.java:448)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.DeliveryPlan.deliver(DeliveryPlan.java:47)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.ServerMessageBusImpl.sendGlobal(ServerMessageBusImpl.java:297)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.SimpleDispatcher.dispatchGlobal(SimpleDispatcher.java:46)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.service.ErraiServiceImpl.store(ErraiServiceImpl.java:96)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.service.ErraiServiceImpl.store(ErraiServiceImpl.java:113)
&nbsp;&nbsp;&nbsp;&nbsp;at org.jboss.errai.bus.server.servlet.DefaultBlockingServlet.doPost(DefaultBlockingServlet.java:144)
&nbsp;&nbsp;&nbsp;&nbsp;at javax.servlet.http.HttpServlet.service(HttpServlet.java:707)
&nbsp;&nbsp;&nbsp;&nbsp;at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:85)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:129)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.websockets.jsr.JsrWebSocketFilter.doFilter(JsrWebSocketFilter.java:130)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at org.uberfire.ext.security.server.SecureHeadersFilter.doFilter(SecureHeadersFilter.java:110)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at org.uberfire.ext.security.server.SecurityIntegrationFilter.doFilter(SecurityIntegrationFilter.java:70)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.SecurityContextAssociationHandler.handleRequest(SecurityContextAssociationHandler.java:78)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:131)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.DisableCacheHandler.handleRequest(DisableCacheHandler.java:33)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AuthenticationConstraintHandler.handleRequest(AuthenticationConstraintHandler.java:53)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.ServletSecurityConstraintHandler.handleRequest(ServletSecurityConstraintHandler.java:59)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.NotificationReceiverHandler.handleRequest(NotificationReceiverHandler.java:50)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.jacc.JACCContextIdHandler.handleRequest(JACCContextIdHandler.java:61)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.GlobalRequestControllerHandler.handleRequest(GlobalRequestControllerHandler.java:68)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:292)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.access$100(ServletInitialHandler.java:81)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:138)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:135)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ServletRequestContextThreadSetupAction$1.call(ServletRequestContextThreadSetupAction.java:48)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.core.ContextClassLoaderSetupAction$1.call(ContextClassLoaderSetupAction.java:43)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.security.SecurityContextThreadSetupAction.lambda$create$0(SecurityContextThreadSetupAction.java:105)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1508)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:272)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler.access$000(ServletInitialHandler.java:81)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.servlet.handlers.ServletInitialHandler$1.handleRequest(ServletInitialHandler.java:104)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.Connectors.executeRootHandler(Connectors.java:326)
&nbsp;&nbsp;&nbsp;&nbsp;at io.undertow.server.HttpServerExchange$1.run(HttpServerExchange.java:812)
&nbsp;&nbsp;&nbsp;&nbsp;at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
&nbsp;&nbsp;&nbsp;&nbsp;at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
&nbsp;&nbsp;&nbsp;&nbsp;at java.lang.Thread.run(Thread.java:748)
<\/tt>"} | Uncaught exception: undefined
 */