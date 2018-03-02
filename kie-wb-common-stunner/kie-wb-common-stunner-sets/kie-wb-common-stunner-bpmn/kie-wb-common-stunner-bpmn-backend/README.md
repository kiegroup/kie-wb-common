# BPMN Backend

## Experimental Marshallers

Enable with flag:

    -Dbpmn.marshaller.experimental=true
    
- Entry point: `BPMNDirectDiagramMarshaller` which implements `BPMNDirectDiagramMarshaller` 
- Actual unmarshalling from XML is delegated to Eclipse BPMN2 library
- Mapping between Eclipse BPMN2 data model to Stunner BPMN data model is performed through **Converter** classes

### Converters To Stunner model

- A **Converter _to_ the Stunner model** is a class (basically, a function) that converts an Eclipse BPMN2 object into:

- a `BPMNNode`, i.e.:  a wrapper for a `Node<? extends View<? extends BPMNViewDefinition>, ?>` ,
 enhanced with:
     - ability to record parent/child relationship; in other words, a `BPMNNode` may be child/parent of other `BPMNode`s
     - ability to "contain" `BPMNEdge`s (see below); in other words a `BPMNNode` may represent a subgraph (e.g. in the case of a `(Sub)Process`)
- a `BPMNEdge`, i.e.: a wrapper for an `Edge<? extends View<? extends BPMNViewDefinition>, ?>` 

- Converter classes form a delegation tree. The root of such a tree is the converter for the Diagram root, called "Process" 
when converting *to* the Stunner model, called "ViewDefinitionConverter" when converting *from* the Stunner model. 
The delegation tree follows the hierarchy and structure of the BPMN data model. 

- For instance, a Process converter will convert all of the FlowElements contained in the Process section. 
A suitable conversion method is invoked, depending on the type of each FlowElement. The way matching is done, 
is describe through code, using a `Match` helper (see Utilities section)

- Each converter is responsible of handling a set of classes from Eclipse BPMN2 model. For instance,
  `TaskConverter` handles `Task`s. It instances a `Node`/`Edge` object (throught the `TypedFactoryManager` -- see below)
  for the recognized type, and fills its fields with all the supported values in the original model.
  At the end of the conversion it usually **return the element**, and/or, in some cases, it may **add it to the canvas** 
  (e.g., subprocess converters return their subprocess node, but also add in their child nodes).



  Fields from the Eclipse BPMN2 model, for convience, are generally accessed through a `PropertyReader`. 
  
  
#### Property Readers
  
`PropertyReader`s are returned from a `PropertyReaderFactory`. They retrieve properties from
  each Eclipse BPMN2 instance especially implementing custom logic for
  extended drools-related attributes, such as 
  - `<bpmn2:extensionElements>` (e.g. `elementname`)
  - `itemDefinition`s / `dataInput`s / `dataOutput`s (e.g. `Subject`, `Comment`)
  - attributes under the`drools:` namespace (e.g. `drools:docker`)
  - attributes under the `color:` namespace (e.g. `color:background-color`)
  - bounds/shape/edge data defined in the `BPMNDiagram/BPMNPlane/BPMNShape` section
  
  For instance, for `BusinesRuleTask task`:
  
      BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);
      // look up the extended name under the `elementname` extension metadata,
      // falling back onto the regular <bpmn2:businessRuleTask name="...">
      String name = p.getName();
      
      // look for the documentation sequence and return a string
      String documentation = p.getDocumentation();
      
      // returns a stunner-compatible representation of the coordinates, bounds
      // of this element
      Bounds b = p.getBounds();
      
      // returns background formatting of the element
      BackgroundSet backgroundSet = p.getBackgroundSet();
      
      // ... etc.

### GraphBuildingContext

A `GraphBuildingContext` object issues commands to the canvas while building the graph. 
  It is a wrapper around: 
   - `GraphCommandExecutionContext`
   - `GraphCommandFactory` 
   - `GraphCommandManager` 
   
`GraphBuildingContext` is used for convenience, to avoid explicitly creating command instances.
 It also implements custom logic for some actions. For example, in the case of adding child nodes, 
 it translates the coordinates of a child node into the new reference system (the parent boundaries).

`GraphBuildingContext` builds the entire graph (`GraphBuildingContext#buildGraph(BpmnNode rootNode)`)
once all the conversions have took place: it traverses the entire directed graph described by the `BPMNNode`s
starting from the "root node", which represents the root of the diagram, and visiting
the parent/child relations in each BPMNNode and the `BPMNEdge` they may contain.

      
### Converters From Stunner Model

A converter **Converter _from_ the Stunner model** is a class (basically, a function) that converts a 
Stunner `Node<? extends View<? extends BPMNViewDefinition>, ?>` to a `PropertyWriter`. A `PropertyWriter`
is a wrapper around an Eclipse BPMN2 element (see below)

#### Property Writers

`PropertyWriter`s are returned from a `PropertyWriterFactory`. They store properties to
  each Eclipse BPMN2 instance especially implementing custom logic for
  extended drools-related attributes, such as 
  - `<bpmn2:extensionElements>` (e.g. `elementname`)
  - `itemDefinition`s / `dataInput`s / `dataOutput`s (e.g. `Subject`, `Comment`)
  - attributes under the`drools:` namespace (e.g. `drools:docker`)
  - attributes under the `color:` namespace (e.g. `color:background-color`)
  - bounds/shape/edge data defined in the `BPMNDiagram/BPMNPlane/BPMNShape` section
  
  For instance, for `BusinesRuleTask task`:
  
      BusinessRuleTaskPropertyReader p = propertyWriterFactory.of(task);
      // write the extended name under the `elementname` extension metadata,
      // and add a regular (whitespace trimmed) <bpmn2:businessRuleTask name="...">
      p.setName(name);
      
      // look set the documentation sequence from the given string
      p.setDocumentation(documentation);
      
      // converts stunner representation of the coordinates to bounds
      // of this element
      p.setBounds(node.getContent().getBounds());
      
      // ... etc.


### Utilities

#### Custom Attributes, Elements

Custom attributes and elements have been defined in form of singletons under the `customproperties`
subpackage of `org.kie.workbench.common.stunner.bpmn.backend.converters`.

Under this package, we define `CustomAttribute`s (such as `"drools:packageName"`), 
`CustomElement`s such as `<elementname>` in the form of singleton objects that read/write
such attributes to a given element. 

For example:

    public String getPackage() {
        return CustomAttribute.packageName.of(element).get();
    }

    public void setPackage(String value) {
        CustomAttribute.packageName.of(process).set(value);
    }


Please notice that these singletons are not supposed to be used directly,
but their getters and setters will be generally wrapped in a `PropertyReader`
or `PropertyWriter`, respectively. For instance `getPackage()` is a member
of `ProcessPropertyReader`, and `setPackage()` is a member of `ProcessPropertyWriter`.


#### Misc
 
In order to minimize casts (mostly for legibility, cosmetic reasons) the following classes exist:

   - `TypedFactoryManager`: a wrapper around `FactoryManager` that creates 
     an instance of `Node<T,U>`, `Edge<T,U>`, `Graph<T,U>`, with proper type parameters. Examples:
     
         Node<View<StartNoneEvent>, Edge> node = 
            factoryManager.newNode(nodeId, StartNoneEvent.class);

     
        Graph<DefinitionSet, Node> graph =
                typedFactoryManager.newGraph(
                        definitionsId, BPMNDefinitionSet.class);
                        

     
   - `Match` and `VoidMatch` provide a fluent interface to type matching (`instanceof`)
      avoiding explicit casts. Moreover it provides shorthands for skipping unhandled types (`ignore()`) 
      or marking them as currently unsupported (`missing()`). Each branch of a `Match` 
      expects a `Function` that consumes the input and returns a `Node<T,U>` or an `Edge<T,U>`. 
      A `VoidMatch` implements a side-effecting `Match`, i.e. a `Match` that does not return a value.
      
      An example from `TaskConverter`:
      
              // a match for Task subclasses that returns Node<? extends View<? extends BPMNViewDefinition?>>
              Match.ofNode(Task.class, BPMNViewDefinition.class)
                      .when(org.eclipse.bpmn2.BusinessRuleTask.class, t -> {
                          // t is already casted to BusinessRuleTask
                          Node<View<BusinessRuleTask>, Edge> node = 
                            factoryManager.newNode(t.getId(), BusinessRuleTask.class);
                            BusinessRuleTask definition = node.getContent().getDefinition();
                            BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);
                    
                            definition.setGeneral(new TaskGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                          ...
                          return node;
                      })
                      .when(org.eclipse.bpmn2.ScriptTask.class, t -> { ... })
                      .when(org.eclipse.bpmn2.UserTask.class, userTaskConverter::convert) // method references
                      .missing(org.eclipse.bpmn2.ServiceTask.class) // report this as missing
                      .missing(org.eclipse.bpmn2.ManualTask.class)
                      .ignore(SomeClassYouSkip.class)
                      .orElse(t -> {
                          /* handle the default case: NoneTask */
                      })
                      .apply(task)
                      .asSuccess().value();