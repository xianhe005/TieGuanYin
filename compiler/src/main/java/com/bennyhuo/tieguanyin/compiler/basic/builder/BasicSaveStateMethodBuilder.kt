package com.bennyhuo.tieguanyin.compiler.basic.builder

import com.bennyhuo.tieguanyin.compiler.basic.BasicClass
import com.bennyhuo.tieguanyin.compiler.basic.types.BUNDLE
import com.bennyhuo.tieguanyin.compiler.basic.types.INTENT
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

abstract class BasicSaveStateMethodBuilder(val basicClass: BasicClass) {

    abstract val instanceType: TypeName

    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder("saveState")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(instanceType, "instance")
                .addParameter(BUNDLE.java, "outState")
                .beginControlFlow("if(instance instanceof \$T)", basicClass.typeElement)
                .addStatement("\$T typedInstance = (\$T) instance", basicClass.typeElement, basicClass.typeElement)

        methodBuilder.addStatement("\$T intent = new \$T()", INTENT.java, INTENT.java)

        for (field in basicClass.fields) {
            val name = field.name

            val template = field.javaTemplateToBundle("typedInstance")
            methodBuilder.addStatement("intent.putExtra(\$S, ${template.first})", name, *template.second)

//            if(field.isInternalType) {
//                if (field.isPrivate) {
//                    methodBuilder.addStatement("intent.putExtra(\$S, typedInstance.get\$L())", name, name.capitalize())
//                } else {
//                    methodBuilder.addStatement("intent.putExtra(\$S, typedInstance.\$L)", name, name)
//                }
//            } else if(field.isAnnotatedType){
//                if (field.isPrivate) {
//                    methodBuilder.addStatement("intent.putExtra(\$S, Tieguanyin.<\$T, Bundle>findProperConverter(\$T.class).convertFrom(typedInstance.get\$L))", name, field.asTypeName(), field.asTypeName(), name.capitalize())
//                } else {
//                    methodBuilder.addStatement("intent.putExtra(\$S, Tieguanyin.<\$T, Bundle>findProperConverter(\$T.class).convertFrom(typedInstance.\$L))", name, field.asTypeName(), field.asTypeName(), name)
//                }
//            } else {
//                throw UnsupportedOperationException("Unsupported type: ${field.name}")
//            }
        }

        methodBuilder.addStatement("outState.putAll(intent.getExtras())").endControlFlow()

        typeBuilder.addMethod(methodBuilder.build())
    }

}