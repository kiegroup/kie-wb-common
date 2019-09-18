/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    mappings: [DC, DI, DMNDI12, DMN12, KIE],

    initializeJsInteropConstructors: function () {

        var mappings = this.mappings;

        function flatMap(list, lambda) {
            return Array.prototype.concat.apply([], list.map(lambda));
        }

        function getTypes() {
            return flatMap(mappings, function (mapping) {

                return mapping.typeInfos.map(function (typeInfo) {
                    return {
                        typeName: typeInfo.localName,
                        namespace: mapping.name
                    };
                });
            });
        }

        function createFunction(typeInfo) {
            var typeName = [typeInfo.namespace, typeInfo.typeName].join(".");
            return new Function('return { "TYPE_NAME" : "' + typeName + '" }');
        }

        function createConstructor(typeInfo) {
            var functionName = "JsInterop__ConstructorAPI__DMN__JSI" + typeInfo.typeName;
            if (window[functionName] === undefined) {
                window[functionName] = createFunction(typeInfo);
            }
        }

        console.log("Generating JsInterop constructors.");

        getTypes().forEach(function (typeInfo) {
            createConstructor(typeInfo);
        });
    },

    unmarshall: function (text, callback) {
        console.log("out unmarshall");
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, callback) {
        console.log("outer marshall");
        var context = new Jsonix.Context(this.mappings);

        // Create unmarshaller
        var marshaller = context.createMarshaller();

        var xmlDocument = marshaller.marshalDocument(value);
        var s = new XMLSerializer();
        var toReturn = s.serializeToString(xmlDocument);
        callback(toReturn);
    }
};
