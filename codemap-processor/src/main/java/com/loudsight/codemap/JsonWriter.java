package com.loudsight.codemap;

import com.loudsight.codemap.model.ClassSignature;
import com.loudsight.codemap.model.FieldSignature;
import com.loudsight.codemap.model.MethodSignature;

import java.util.List;

/**
 * Hand-rolled JSON serialization for {@link ClassSignature} output. Deliberate: this module must
 * not add a JSON library dependency to the annotation-processor path of every module that opts in
 * (see codemap-processing profile in parent/pom.xml) - the shape here is small and fixed enough
 * that a library buys nothing.
 */
final class JsonWriter {

    private JsonWriter() {
    }

    static String toJson(List<ClassSignature> classes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"classes\": [\n");
        for (int i = 0; i < classes.size(); i++) {
            appendClass(sb, classes.get(i), "    ");
            sb.append(i < classes.size() - 1 ? ",\n" : "\n");
        }
        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private static void appendClass(StringBuilder sb, ClassSignature classSignature, String indent) {
        sb.append(indent).append("{\n");
        appendField(sb, indent + "  ", "package", classSignature.packageName(), true);
        appendField(sb, indent + "  ", "name", classSignature.simpleName(), true);
        appendField(sb, indent + "  ", "kind", classSignature.kind(), true);
        appendField(sb, indent + "  ", "superclass", classSignature.superclass(), true);
        appendStringArray(sb, indent + "  ", "interfaces", classSignature.interfaces(), true);
        appendStringArray(sb, indent + "  ", "annotations", classSignature.classAnnotations(), true);
        appendField(sb, indent + "  ", "javadocSummary", classSignature.javadocSummary(), true);
        appendMethods(sb, indent + "  ", classSignature.methods());
        sb.append(",\n");
        appendConstants(sb, indent + "  ", classSignature.constants());
        sb.append("\n").append(indent).append("}");
    }

    private static void appendMethods(StringBuilder sb, String indent, List<MethodSignature> methods) {
        sb.append(indent).append("\"methods\": [");
        if (methods.isEmpty()) {
            sb.append("]");
            return;
        }
        sb.append("\n");
        for (int i = 0; i < methods.size(); i++) {
            MethodSignature method = methods.get(i);
            sb.append(indent).append("  {");
            sb.append("\"name\": ");
            appendString(sb, method.name());
            sb.append(", \"params\": ");
            appendStringArrayInline(sb, method.paramTypes());
            sb.append(", \"returns\": ");
            appendString(sb, method.returnType());
            sb.append(", \"modifiers\": ");
            appendStringArrayInline(sb, method.modifiers());
            sb.append("}");
            sb.append(i < methods.size() - 1 ? ",\n" : "\n");
        }
        sb.append(indent).append("]");
    }

    private static void appendConstants(StringBuilder sb, String indent, List<FieldSignature> constants) {
        sb.append(indent).append("\"constants\": [");
        if (constants.isEmpty()) {
            sb.append("]");
            return;
        }
        sb.append("\n");
        for (int i = 0; i < constants.size(); i++) {
            FieldSignature constant = constants.get(i);
            sb.append(indent).append("  {");
            sb.append("\"name\": ");
            appendString(sb, constant.name());
            sb.append(", \"type\": ");
            appendString(sb, constant.type());
            sb.append(", \"value\": ");
            appendString(sb, constant.value());
            sb.append("}");
            sb.append(i < constants.size() - 1 ? ",\n" : "\n");
        }
        sb.append(indent).append("]");
    }

    private static void appendField(StringBuilder sb, String indent, String key, String value, boolean trailingComma) {
        sb.append(indent).append("\"").append(key).append("\": ");
        appendString(sb, value);
        sb.append(trailingComma ? ",\n" : "\n");
    }

    private static void appendStringArray(StringBuilder sb, String indent, String key, List<String> values,
            boolean trailingComma) {
        sb.append(indent).append("\"").append(key).append("\": ");
        appendStringArrayInline(sb, values);
        sb.append(trailingComma ? ",\n" : "\n");
    }

    private static void appendStringArrayInline(StringBuilder sb, List<String> values) {
        sb.append("[");
        for (int i = 0; i < values.size(); i++) {
            appendString(sb, values.get(i));
            if (i < values.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private static void appendString(StringBuilder sb, String value) {
        if (value == null) {
            sb.append("null");
            return;
        }
        sb.append("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format(java.util.Locale.ROOT, "\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
    }
}
