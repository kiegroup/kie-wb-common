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

        var extraTypes = [{typeName: 'Name', namespace: ''}];
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
            }).concat(extraTypes);
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

    unmarshall: function (text, dynamicNamespace, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, defaultNamespace, callback) {
        // Create Jsonix context
        var namespaces = {};
        namespaces[defaultNamespace] = "";
        namespaces["http://www.omg.org/spec/DMN/20180521/MODEL/"] = "dmn";
        namespaces["http://www.omg.org/spec/DMN/20180521/DI/"] = "di";
        namespaces["http://www.drools.org/kie/dmn/1.2"] = "kie";
        namespaces["http://www.omg.org/spec/DMN/20180521/DMNDI/"] = "dmndi";
        namespaces["http://www.omg.org/spec/DMN/20180521/DC/"] = "dc";
        namespaces["http://www.omg.org/spec/DMN/20180521/FEEL/"] = "feel";
        var context = new Jsonix.Context(this.mappings, {
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