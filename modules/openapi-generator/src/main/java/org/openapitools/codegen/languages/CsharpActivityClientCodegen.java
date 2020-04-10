package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.parameters.Parameter;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsharpActivityClientCodegen extends CSharpClientCodegen {
    public static final String PROJECT_NAME = "projectName";

    static Logger LOGGER = LoggerFactory.getLogger(CsharpActivityClientCodegen.class);

    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    public String getName() {
        return "csharp-activity";
    }

    public String getHelp() {
        return "Generates a csharp activity client.";
    }

    public CsharpActivityClientCodegen() {
        super();
        netCoreProjectFileFlag = true;
        supportNullable = Boolean.TRUE;
        languageSpecificPrimitives = new HashSet<String>(
                    Arrays.asList(
                        "string",
                        "bool?",
                        "bool",
                        "double?",
                        "double",
                        "decimal?",
                        "decimal",
                        "int?",
                        "int",
                        "long?",
                        "long",
                        "float?",
                        "float",
                        "byte[]",
                        "System.String",
                        "System.Byte[]",
                        "System.Collections.Generic.ICollection",
                        "System.Collections.ObjectModel.Collection",
                        "System.Collections.Generic.List",
                        "System.Collections.Generic.Dictionary",
                        "System.DateTime?",
                        "System.DateTime",
                        "System.DateTimeOffset?",
                        "System.DateTimeOffset",
                        "System.Boolean",
                        "System.Boolean?",
                        "System.Double",
                        "System.Double?",
                        "System.Int32",
                        "System.Int32?",
                        "System.Int64",
                        "System.Int64?",
                        "System.Float",
                        "System.Float?",
                        "System.Guid?",
                        "System.Guid",
                        "System.IO.Stream", // not really a primitive, we include it to avoid model import
                        "System.Object")
        );

        instantiationTypes.put("array", "System.Collections.Generic.List");
        instantiationTypes.put("list", "System.Collections.Generic.List");
        instantiationTypes.put("map", "System.Collections.Generic.Dictionary");


        // Nullable types here assume C# 2 support is not part of base
        typeMapping = new HashMap<String, String>();
        typeMapping.put("string", "System.String");
        typeMapping.put("binary", "System.Byte[]");
        typeMapping.put("ByteArray", "System.Byte[]");
        typeMapping.put("boolean", "System.Boolean?");
        typeMapping.put("integer", "System.Int32?");
        typeMapping.put("float", "System.Float?");
        typeMapping.put("long", "System.Int64?");
        typeMapping.put("double", "System.Double?");
        typeMapping.put("number", "System.Decimal?");
        typeMapping.put("BigDecimal", "System.Decimal?");
        typeMapping.put("DateTime", "System.DateTime?");
        typeMapping.put("date", "System.DateTime?");
        typeMapping.put("file", "System.IO.Stream");
        typeMapping.put("array", "System.Collections.Generic.List");
        typeMapping.put("list", "System.Collections.Generic.List");
        typeMapping.put("map", "System.Collections.Generic.Dictionary");
        typeMapping.put("object", "System.Object");
        typeMapping.put("UUID", "System.Guid?");
        typeMapping.put("URI", "System.String");

                // nullable type
                nullableType = new HashSet<String>(
                    Arrays.asList("System.Decimal", "System.Boolean", "System.Int32", "System.Float", "System.Long", "System.Double", "System.DateTime", "System.DateTimeOffset", "System.Guid")
            );
            // value Types
            valueTypes = new HashSet<String>(
                    Arrays.asList("System.Decimal", "System.Boolean", "System.Int32", "System.Float", "System.Long", "System.Double")
            );
    }

    public void processOpts() {
        super.processOpts();
        supportingFiles.add(new SupportingFile("IActivity.mustache", "", "IActivities.json"));
    }

    @Override
    public boolean isDataTypeString(String dataType) {
        // also treat double/decimal/float as "string" in enum so that the values (e.g. 2.8) get double-quoted
        return "System.String".equalsIgnoreCase(dataType) ||
                "System.Double?".equals(dataType) || "System.Decimal?".equals(dataType) || "System.Float?".equals(dataType) ||
                "System.Double".equals(dataType) || "System.Decimal".equals(dataType) || "System.Float".equals(dataType);
    }

    @Override
    public String toEnumValue(String value, String datatype) {
        // C# only supports enums as literals for int, int?, long, long?, byte, and byte?. All else must be treated as strings.
        // Per: https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/keywords/enum
        // The approved types for an enum are byte, sbyte, short, ushort, int, uint, long, or ulong.
        // but we're not supporting unsigned integral types or shorts.
        if (datatype.startsWith("System.Int") || datatype.startsWith("System.Long") || datatype.startsWith("System.Byte")) {
            return value;
        }

        return escapeText(value);
    }
}
