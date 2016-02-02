package org.excel2db.write.genClass.generator;

import java.util.List;

import org.excel2db.write.genClass.AbstractGenerator;
import org.excel2db.write.util.TypeEnum;

public class CSharpGenerator extends AbstractGenerator {

	@Override
	public void generatorBean() {

		out.println(CSharpSign.using[0]);
		out.println(CSharpSign.using[1]);
		out.println();

		out.println(CSharpSign.NameSpace+config.getPackageRoot());
		out.println("{");
		
		out.println(CSharpSign.Tab[1] +CSharpSign.Public + CSharpSign.Class
				+ toFirstUpperCase(info.getName()) + " {");
		out.println();

		List<String> columnNames = info.getColumnNames();
		List<TypeEnum> typeEnums = info.getTypeEnums();

		for (int i = 0; i < typeEnums.size(); i++) {
			TypeEnum type = typeEnums.get(i);
			String columnName = columnNames.get(i);
			out.println(CSharpSign.Tab[2] + CSharpSign.Private
					+ CSharpSign.fullType(type) + columnName + ";");
		}
		out.println();

		for (int i = 0; i < typeEnums.size(); i++) {
			TypeEnum type = typeEnums.get(i);
			String columnName = columnNames.get(i);

			out.println(CSharpSign.Tab[2] + CSharpSign.Public
					+ CSharpSign.fullType(type) + toGetMethod(columnName) + " {");
			out.println(CSharpSign.Tab[3] + CSharpSign.Return + columnName + ";");
			out.println(CSharpSign.Tab[2] + "}");
			out.println();

			out.println(CSharpSign.Tab[2] + CSharpSign.Public + CSharpSign.Void
					+ toSetMethod(columnName, CSharpSign.fullType(type)) + " {");
			out.println(CSharpSign.Tab[3] + CSharpSign.This + "." + columnName
					+ " = " + columnName + ";");
			out.println(CSharpSign.Tab[2] + "}");
			out.println();
		}
		out.println(CSharpSign.Tab[1] + "}");
		out.println("}");
	}
	
	private String toGetMethod(String columnName) {
		return "Get" + toFirstUpperCase(columnName) + "()";
	}

	private String toSetMethod(String columnName, String typeStr) {
		return "Set" + toFirstUpperCase(columnName) + "(" + typeStr
				+ columnName + ")";
	}

}