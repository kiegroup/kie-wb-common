package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import bpsim.CostParameters;
import bpsim.ElementParameters;
import bpsim.FloatingParameterType;
import bpsim.NormalDistributionType;
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.PoissonDistributionType;
import bpsim.ResourceParameters;
import bpsim.TimeParameters;
import bpsim.UniformDistributionType;
import org.eclipse.emf.common.util.EList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

public class Simulations {

    public static SimulationSet simulationSet(ElementParameters eleType) {
        SimulationSet simulationSet = timeParams(eleType);
        unitCost(eleType, simulationSet);
        controlParams(eleType, simulationSet);
        resourceParams(eleType, simulationSet);
        return simulationSet;
    }

    private static SimulationSet timeParams(ElementParameters eleType) {
        SimulationSet simulationSet = new SimulationSet();

        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams == null) return simulationSet;
        Parameter processingTime = timeParams.getProcessingTime();
        ParameterValue paramValue = processingTime.getParameterValue().get(0);

        return Match.of(ParameterValue.class, SimulationSet.class)
            .when(NormalDistributionType.class, ndt -> {
                simulationSet.getMean().setValue(ndt.getMean());
                simulationSet.getStandardDeviation().setValue(ndt.getStandardDeviation());
                simulationSet.getDistributionType().setValue("normal");
                return simulationSet;
            })
            .when(UniformDistributionType.class, udt -> {
                simulationSet.getMin().setValue(udt.getMin());
                simulationSet.getMax().setValue(udt.getMax());
                simulationSet.getDistributionType().setValue("uniform");
                return simulationSet;
            })
            .when(PoissonDistributionType.class, pdt -> {
                simulationSet.getMean().setValue(pdt.getMean());
                simulationSet.getDistributionType().setValue("poisson");
                return simulationSet;
            }).apply(paramValue).asSuccess().value();

        // FIXME waittime ??
    }

    private static void unitCost(ElementParameters eleType, SimulationSet simulationSet) {
        CostParameters costParams = eleType.getCostParameters();
        if (costParams == null) return;
        Double unitCost = extractDouble(costParams.getUnitCost());
        simulationSet.getUnitCost().setValue(unitCost);
    }

    private static void controlParams(ElementParameters eleType, SimulationSet simulationSet) {
        // double probability = extractDouble(eleType.getControlParameters().getProbability().getParameterValue());
        // FIXME probability ???
    }

    private static void resourceParams(ElementParameters eleType, SimulationSet simulationSet) {
        ResourceParameters resourceParams = eleType.getResourceParameters();

        double quantity = extractDouble(resourceParams.getQuantity());
        simulationSet.getQuantity().setValue(quantity);

        Double availability = extractDouble(resourceParams.getAvailability());
        simulationSet.getWorkingHours().setValue(availability);
    }

    private static Double extractDouble(Parameter parameter) {
        if (parameter == null) return null;
        return extractDouble(parameter.getParameterValue());
    }

    private static double extractDouble(EList<ParameterValue> parameterValues) {
        if (parameterValues.isEmpty()) {
            throw new IllegalArgumentException("failure params");
        }
        ParameterValue value = parameterValues.get(0);
        return ((FloatingParameterType) value).getValue();
    }
}
