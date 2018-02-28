package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A flat variable scope, where no nesting information is used.
 * <p>
 * In a flat scope there is no nesting. E.g.:
 * <p>
 * Process P defines variables x,y
 * <ul>
 * <li> SubProcess P1 nested in P defines P1_x, P2_y </li>
 * <li> SubProcess P2 nested in P defines P2_x </li>
 * </ul>
 * <p>
 * The FlatScope contains:
 * <p>
 * <ul>
 * <li>x</li>
 * <li>y</li>
 * <li>P1_x</li>
 * <li>P1_y</li>
 * <li>P2_x</li>
 * </ul>
 * <p>
 * Also, P1 may refer to x, y, P1_x, P1_y, but also to P2_x, P2_y
 * <p>
 * In a flat scope, names can easily clash,
 * but it's simple to implement (it's a Map).
 * <p>
 * In future versions we might want to implement a more refined
 * Scope notion with nesting; in this case,
 * P1 may refer to x, y P1_x, P1_y, but NOT to P2_x, P2_y, because
 * P2 does not nest in P1
 */
public class FlatVariableScope implements VariableScope {

    private Map<String, Variable> variables = new HashMap<>();

    public Variable declare(String scopeId, String identifier, String type) {
        Variable variable = new Variable(scopeId, identifier, type);
        variables.put(identifier, variable);
        return variable;
    }

    public Variable lookup(String identifier) {
        return variables.get(identifier);
    }

    public Collection<Variable> getVariables(String scopeId) {
        return variables.values()
                .stream()
                .filter(v -> v.getParentScopeId().equals(scopeId))
                .collect(Collectors.toList());
    }
}

