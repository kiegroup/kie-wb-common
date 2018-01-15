package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.Error;

public class DefinitionResolver {
    private final Map<String, Signal> signals = new HashMap<>();
    private final Map<String, Error> errors = new HashMap<>();
    private final Map<String, Message> messages = new HashMap<>();

    public DefinitionResolver(Definitions definitions) {
        for (RootElement el: definitions.getRootElements()) {
            if (el instanceof Signal) signals.put(el.getId(), (Signal) el);
            else if (el instanceof Error) errors.put(el.getId(), (Error)el);
            else if (el instanceof Message) messages.put(el.getId(), (Message)el);
        }
    }

    public Optional<Signal> resolveSignal(String id) {
        return Optional.ofNullable(signals.get(id));
    }

    public Optional<Error> resolveError(String id) {
        return Optional.ofNullable(errors.get(id));
    }

    public Optional<Message> resolveMessage(String id) {
        return Optional.ofNullable(messages.get(id));
    }

}
