Stunner Kogito Showcase
=======================

For detailed instructions on how to configure your development environment and run the Kogito Showcase, please refer to 
the [Stunner directory README documentation](../../../).

* Build Stunner entire project: `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
* Go to this directory and compile project: `mvn -T 8C clean install -DskipTests=true -Dgwt.compiler.skip=true`
* Start GWT super dev mode by: `mvn gwt:run`
* To create new diagram copy/paste this command into the browser console: `gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("");` 
* To get content of the diagram copy/paste this command into the browser console: `gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()`
* Alternatively you can load file from the disk, change url to `http://127.0.0.1:8888/test.html` and select file from the disk.
