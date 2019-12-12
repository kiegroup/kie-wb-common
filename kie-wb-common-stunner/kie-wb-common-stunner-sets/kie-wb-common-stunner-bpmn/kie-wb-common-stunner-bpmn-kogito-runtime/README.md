Stunner Kogito Showcase
=======================

For detailed instructions on how to configure your development environment and run the Kogito Showcase, please refer to 
the [Stunner directory README documentation](../../../).

* Build Stunner entire project: `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
* Go to this directory and package it: `mvn clean package`
* Open `target/kie-wb-common-bpmn-kogito-runtime/index.html` in browser
* To create new diagram copy/paste this command into the browser console: `gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("");` 
* To get content of the diagram copy/paste this command into the browser console: `gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()`
