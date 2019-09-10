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
        var context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE]);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, callback) {
        console.log("outer marshall");
        var context = new Jsonix.Context([DC, DI, DMNDI12, DMN12, KIE]);

        // Create unmarshaller
        var marshaller = context.createMarshaller();

        var xmlDocument = marshaller.marshalDocument(value);
        var s = new XMLSerializer();
        var toReturn = s.serializeToString(xmlDocument);
        callback(toReturn);
    }
}