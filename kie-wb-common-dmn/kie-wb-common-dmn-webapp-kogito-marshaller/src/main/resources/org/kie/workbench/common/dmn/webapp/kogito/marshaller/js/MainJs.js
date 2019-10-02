/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    mappings: [DC, DI, DMNDI12, DMN12, KIE],

    initializeJsInteropConstructors: function (constructorsMap) {

        var extraTypes = [{typeName: 'Name', namespace: null}];
        // var mappings = this.mappings;
        // var types;
        // var baseTypes;
        // var innerTypes;

        // function flatMap(list, lambda) {
        //     return Array.prototype.concat.apply([], list.map(lambda));
        // }

        // function getTypes() {
        //
        //     if (types === undefined) {
        //         types = flatMap(mappings, function (mapping) {
        //
        //             return mapping.typeInfos.map(function (typeInfo) {
        //                 return {
        //                     typeName: typeInfo.localName,
        //                     namespace: mapping.name
        //                 };
        //             });
        //         }).concat(extraTypes);
        //     }
        //
        //     return types;
        // }

        // function getBaseTypes() {
        //     if (baseTypes === undefined) {
        //         baseTypes = getTypes().filter(function (typeInfo) {
        //             return typeInfo.typeName.split('.').length === 1;
        //         });
        //     }
        //     return baseTypes;
        // }

        // function getInnerTypes() {
        //     if (innerTypes === undefined) {
        //         innerTypes = getTypes().filter(function (typeInfo) {
        //             return typeInfo.typeName.split('.').length > 1;
        //         });
        //     }
        //     return innerTypes;
        // }

        // function getJsInteropTypeName(namespace, klass) {
        //
        //     var prefix = 'JsInterop__ConstructorAPI__DMN';
        //     var classPrefix = 'JSI';
        //     var presentValues = function (value) {
        //         return value;
        //     };
        //
        //     return [prefix, namespace, classPrefix + klass].filter(presentValues).join('__');
        // }

        function createFunction(typeName) {
            return new Function('return { "TYPE_NAME" : "' + typeName + '" }');
        }

        // function createBaseClassConstructor(typeInfo) {
        //
        //     var typeName;
        //     var functionName = getJsInteropTypeName(typeInfo.namespace, typeInfo.typeName);
        //
        //     if (window[functionName] === undefined) {
        //         typeName = typeInfo.namespace === null ? typeInfo.typeName : [typeInfo.namespace, typeInfo.typeName].join(".");
        //         window[functionName] = createFunction(typeName);
        //     }
        // }

        // function createInnerClassConstructor(typeInfo) {
        //
        //     var typeName;
        //     var typeNameParts = typeInfo.typeName.split('.');
        //     var functionName = getJsInteropTypeName(typeInfo.namespace, typeNameParts[0]);
        //
        //     if (typeNameParts.length !== 2) {
        //         console.error('Constructor generation error. Unexpected type: ', typeInfo.typeName);
        //         return;
        //     }
        //
        //     if (window[functionName] === undefined) {
        //         console.error('Constructor generation error. The base class needs to have a constructor: ', functionName);
        //         return;
        //     }
        //
        //     typeName = [typeInfo.namespace, typeInfo.typeName].join(".");
        //     window[functionName][typeNameParts[1]] = createFunction(typeName);
        // }

        function createConstructor(value) {
            console.log("Create createConstructor " + value)
            const parsedJson = JSON.parse(value)
            const name = parsedJson["name"]
            const nameSpace = parsedJson["nameSpace"]
            const typeName = parsedJson["typeName"]
            console.log("parsedJson " + parsedJson)
            console.log("name " + name)
            console.log("nameSpace " + nameSpace)
            console.log("typeName " + typeName)
            if (nameSpace != null) {
                window[nameSpace][name] = createFunction(typeName);
            } else {
                window[name] = createFunction(typeName);
            }
        }

        function hasNameSpace(value) {
            return JSON.parse(value)["nameSpace"] != null
        }

        function hasNotNameSpace(value) {
            return JSON.parse(value)["nameSpace"] == null
        }

        function iterateValueEntry(values) {
            console.log("iterateValueEntry " + values);
            const baseTypes = values.filter(hasNotNameSpace)
            const innerTypes = values.filter(hasNameSpace)
            baseTypes.forEach(createConstructor)
            innerTypes.forEach(createConstructor)
        }

        function iterateKeyValueEntry(key, values) {
            console.log("iterateKeyValueEntry " + key + "  " + values);
            iterateValueEntry(values)
        }

        console.log('Generating JsInterop constructors.');

        for (const property in constructorsMap) {
            if(constructorsMap.hasOwnProperty(property)) {
                iterateKeyValueEntry(property, constructorsMap[property])
            }
        }


        // // Create base classes constructors
        // getBaseTypes().forEach(function (typeInfo) {
        //     createBaseClassConstructor(typeInfo);
        // });
        //
        // // Create inner classes constructors
        // getInnerTypes().forEach(function (typeInfo) {
        //     createInnerClassConstructor(typeInfo);
        // });
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