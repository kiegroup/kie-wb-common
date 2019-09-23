/**
 * == READ ME ==
 * 
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    unmarshall: function (text, callback) {
        console.log("out unmarshall");
        // Create Jsonix context
        const context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE], {
            namespacePrefixes: {
                "http://www.omg.org/spec/DMN/20180521/MODEL/": "dmn",
                "http://www.omg.org/spec/DMN/20180521/DI/": "di",
                "http://www.drools.org/kie/dmn/1.2": "kie",
                "http://www.omg.org/spec/DMN/20180521/DMNDI/": "dmndi",
                "http://www.omg.org/spec/DMN/20180521/DC/": "dc",
                "http://www.omg.org/spec/DMN/20180521/FEEL/": "feel"
            }
        });

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, callback) {
        console.log("outer marshall");
        // Create Jsonix context
        const context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE], {
            namespacePrefixes: {
                "http://www.omg.org/spec/DMN/20180521/MODEL/": "dmn",
                "http://www.omg.org/spec/DMN/20180521/DI/": "di",
                "http://www.drools.org/kie/dmn/1.2": "kie",
                "http://www.omg.org/spec/DMN/20180521/DMNDI/": "dmndi",
                "http://www.omg.org/spec/DMN/20180521/DC/": "dc",
                "http://www.omg.org/spec/DMN/20180521/FEEL/": "feel"
            }
        });

        // Create unmarshaller
        var marshaller = context.createMarshaller();

        var xmlDocument = marshaller.marshalDocument(value);
        var s = new XMLSerializer();
        var toReturn = s.serializeToString(xmlDocument);
        callback(toReturn);
    }
}