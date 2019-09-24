/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    unmarshall: function (text, dynamicNamespace, callback) {
        console.log("out unmarshall");
        // Create Jsonix context
        var context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE]);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, defaultNamespace, callback) {
        console.log("outer marshall");
        // Create Jsonix context
        var namespaces = {};
        namespaces[defaultNamespace] = "";
        namespaces["http://www.omg.org/spec/DMN/20180521/MODEL/"] = "dmn";
        namespaces["http://www.omg.org/spec/DMN/20180521/DI/"] = "di";
        namespaces["http://www.drools.org/kie/dmn/1.2"] = "kie";
        namespaces["http://www.omg.org/spec/DMN/20180521/DMNDI/"] = "dmndi";
        namespaces["http://www.omg.org/spec/DMN/20180521/DC/"] = "dc";
        namespaces["http://www.omg.org/spec/DMN/20180521/FEEL/"] = "feel";
        var context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE], {
            namespacePrefixes: namespaces
        });

        // Create unmarshaller
        var marshaller = context.createMarshaller();

        var xmlDocument = marshaller.marshalDocument(value);
        var s = new XMLSerializer();
        var toReturn = s.serializeToString(xmlDocument);
        callback(toReturn);
    }
}