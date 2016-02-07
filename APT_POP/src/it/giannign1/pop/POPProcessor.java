package it.giannign1.pop;

import it.giannign1.pop_api.POPParseObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public class POPProcessor extends AbstractProcessor {

    private Messager _Messager;
	public POPProcessor() {
        super();
    }
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
    	super.init(processingEnv);
    	this._Messager = processingEnv.getMessager();
    }
    
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(POPParseObject.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    	if(roundEnv.getElementsAnnotatedWith(POPParseObject.class).isEmpty())
    		return true;
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("------------------------------"), null);
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("BEGIN POP Processor Generation"), null);
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("------------------------------"), null);
        for (Element e : roundEnv.getElementsAnnotatedWith(POPParseObject.class)) {
            TypeElement classElement = (TypeElement) e;

    		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("-Generating class %s", classElement.getSimpleName().toString() + "_POPgen"), null);
    		
            TypeSpec.Builder subClassBuilder = TypeSpec.classBuilder(classElement.getSimpleName().toString() + "_POPgen")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                	.addAnnotation(AnnotationSpec.builder(java.lang.SuppressWarnings.class).addMember("value", "$S", "unused").build());
            
            POPParseObject annotationPOPParseObject = e.getAnnotation(POPParseObject.class);
            {
                for (String field_string : annotationPOPParseObject.strings()) {
                	subClassBuilder.addMethods(this.generateGetterSetter(String.class, field_string));
                    subClassBuilder.addField(this.generateStaticField(field_string));
                }
                for (String list : annotationPOPParseObject.lists()) {
                	subClassBuilder.addMethods(this.generateListGetterSetter(list));
                    subClassBuilder.addField(this.generateStaticField(list));
                }
                for (String field_int : annotationPOPParseObject.ints()) {
                	subClassBuilder.addMethods(this.generateGetterSetter(int.class, field_int));
                    subClassBuilder.addField(this.generateStaticField(field_int));
                }
                for (String field_boolean : annotationPOPParseObject.booleans()) {
                	subClassBuilder.addMethods(this.generateGetterSetter(boolean.class, field_boolean));
                    subClassBuilder.addField(this.generateStaticField(field_boolean));
                }
                for (String field_geopoints : annotationPOPParseObject.geopoints()) {
                	subClassBuilder.addMethods(this.generateGetterSetter(ParseGeoPoint.class, field_geopoints));
                    subClassBuilder.addField(this.generateStaticField(field_geopoints));
                }
                for (String field_files : annotationPOPParseObject.files()) {
                	subClassBuilder.addMethods(this.generateGetterSetter(ParseFile.class, field_files));
                    subClassBuilder.addField(this.generateStaticField(field_files));
                }
                for (String field_dates : annotationPOPParseObject.dates()) {
                	subClassBuilder.addMethods(this.generateDateGetterSetter(field_dates));
                    subClassBuilder.addField(this.generateStaticField(field_dates));
                }
                for (String field_parseobject : annotationPOPParseObject.parseobjects()) {
                	subClassBuilder.addMethods(this.generateParseObjectGetterSetter(field_parseobject));
                    subClassBuilder.addField(this.generateStaticField(field_parseobject));
                }
                if(annotationPOPParseObject.relations().length != 0) {
                	TypeVariableName typeVariableName = TypeVariableName.get("T", ParseObject.class);
		            for(String field_relation : annotationPOPParseObject.relations()) {
		            	subClassBuilder.addMethod(MethodSpec.methodBuilder("POPget_" + field_relation)
                            .addModifiers(Modifier.PUBLIC)
                            .addTypeVariable(typeVariableName)
                            .returns(ParameterizedTypeName.get(ClassName.get(ParseRelation.class), typeVariableName))
                            .addStatement("return this.<T>getRelation($S)", field_relation)
                            .build());
		        		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating method %s", "POPget_" + field_relation), null);
		        		subClassBuilder.addField(FieldSpec.builder(String.class, "POPkey_" + field_relation, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", field_relation).build());
		        		this._Messager.printMessage(Diagnostic.Kind.NOTE, "---Generating key POPkey_" + field_relation, null);
		            }
                }
                
                String formatToString = annotationPOPParseObject.toStringFormat();
                if(formatToString != null && formatToString.length() != 0) {
                	MethodSpec.Builder methodToString = MethodSpec.methodBuilder("toString")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class) 
                    .returns(String.class);
                	
                	String[] formatParameters = annotationPOPParseObject.toStringParameters();
                	if(formatParameters.length == 0) {
                		methodToString.addStatement("return $S", formatToString);
                	} else {
                		StringBuilder statement = new StringBuilder("return String.format($S");
	                	for(int i = 0; i < formatParameters.length; i++)
	                		if(formatParameters[i].equals("objectId") || formatParameters[i].equals("createdAt") || formatParameters[i].equals("updatedAt")) {
	                			statement.append(", get$L()");
	                			formatParameters[i] = formatParameters[i].toUpperCase(Locale.getDefault()).charAt(0) + formatParameters[i].substring(1);
	                		} else {
	                			statement.append(", POPget_$L()");
	                		}
	                	String[] formatStringAndParameter = new String[formatParameters.length + 1];
	                	formatStringAndParameter[0] = formatToString;
	                	System.arraycopy(formatParameters, 0, formatStringAndParameter, 1, formatParameters.length);
	                	methodToString
	                	.beginControlFlow("try")
	                	.addStatement(statement.append(")").toString(), (Object[])formatStringAndParameter)
	                	.nextControlFlow("catch ($T ignored)", Exception.class)
	                	.addStatement("return $S", "POP error while computing toString formatting")
	                	.endControlFlow();
                	}
                	
                	subClassBuilder.addMethod(methodToString.build());
		        	this._Messager.printMessage(Diagnostic.Kind.NOTE, "---Generating method toString", null);	
                }

        		subClassBuilder.addField(FieldSpec.builder(String.class, "POPkey_objectId", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", "objectId").build());
        		this._Messager.printMessage(Diagnostic.Kind.NOTE, "---Generating key POPkey_objectId", null);
        		subClassBuilder.addField(FieldSpec.builder(String.class, "POPkey_createdAt", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", "createdAt").build());
        		this._Messager.printMessage(Diagnostic.Kind.NOTE, "---Generating key POPkey_createdAt", null);
        		subClassBuilder.addField(FieldSpec.builder(String.class, "POPkey_updatedAt", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", "updatedAt").build());
        		this._Messager.printMessage(Diagnostic.Kind.NOTE, "---Generating key POPkey_updatedAt", null);
        		
                switch(annotationPOPParseObject.superClassType()) {
                case ParseUser:
                    subClassBuilder.superclass(ParseUser.class);
                    subClassBuilder.addMethod(MethodSpec.methodBuilder("POPasParseObject") //Necessario per import com.parse.ParseObject;
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParseObject.class)
                            .addStatement("return this")
                            .build());
                	break;
                case ParseObject:
            	default:
                    subClassBuilder.superclass(ParseObject.class);
                	break;
                }
            }

            try {
                JavaFile.builder(((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString(), subClassBuilder.build()).build().writeTo(processingEnv.getFiler());
            } catch (IOException ignored) { }
        }

    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("----------------------------"), null);
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("END POP Processor Generation"), null);
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("----------------------------"), null);
        return true;
    }

	private FieldSpec generateStaticField(String key_name) {
		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating key %s", "POPkey_" + key_name), null);
		return FieldSpec.builder(String.class, "POPkey_" + key_name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", key_name).build();
	}

	private Iterable<MethodSpec> generateGetterSetter(Class<?> fieldType, String fieldName) {
		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating methods %s and %s", (fieldType == boolean.class ? "POPis_" : "POPget_") + fieldName, "POPset_" + fieldName), null);
		return Arrays.asList(new MethodSpec[] {
                MethodSpec.methodBuilder((fieldType == boolean.class ? "POPis_" : "POPget_") + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(fieldType)
                    .addStatement("return get$L($S)", fieldType.getSimpleName().toUpperCase(Locale.getDefault()).charAt(0) + fieldType.getSimpleName().substring(1), fieldName)
                    .build(),
                MethodSpec.methodBuilder("POPset_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(fieldType, fieldName)
                    .addStatement("put($S, $N)", fieldName, fieldName)
                    .build()});
    }
    private Iterable<MethodSpec> generateParseObjectGetterSetter(String fieldName) {
    	this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating methods %s and %s", "POPget_" + fieldName, "POPset_" + fieldName), null);
    	TypeVariableName typeVariableName = TypeVariableName.get("T", ParseObject.class);
		return Arrays.asList(new MethodSpec[] {
                MethodSpec.methodBuilder("POPget_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(typeVariableName)
                    .returns(typeVariableName)
                    .addStatement("return (T)this.getParseObject($S)", fieldName)
                    .build(),
                MethodSpec.methodBuilder("POPset_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(ParseObject.class, fieldName)
                    .addStatement("put($S, $N)", fieldName, fieldName)
                    .build()});
	}

	private Iterable<MethodSpec> generateListGetterSetter(String fieldName) {
		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating methods %s and %s", "POPget_" + fieldName, "POPset_" + fieldName), null);
    	TypeVariableName typeVariableName = TypeVariableName.get("T", Object.class);
		return Arrays.asList(new MethodSpec[] {
                MethodSpec.methodBuilder("POPget_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(typeVariableName)
                    .returns(ParameterizedTypeName.get(ClassName.get(List.class), typeVariableName))
                    .addStatement("return this.<T>getList($S)", fieldName)
                    .build(),
                MethodSpec.methodBuilder("POPset_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(List.class, fieldName)
                    .addStatement("put($S, $N)", fieldName, fieldName)
                    .build()});
    }
	private Iterable<MethodSpec> generateDateGetterSetter(String fieldName) {
		this._Messager.printMessage(Diagnostic.Kind.NOTE, String.format("---Generating methods %s and %s", "POPget_" + fieldName, "POPset_" + fieldName), null);
		return Arrays.asList(new MethodSpec[] {
                MethodSpec.methodBuilder("POPget_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Date.class)
                    .addStatement("return ($T)this.get($S)", Date.class, fieldName)
                    .build(),
                MethodSpec.methodBuilder("POPset_" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(Date.class, fieldName)
                    .addStatement("this.put($S, $N)", fieldName, fieldName)
                    .build()});
    }
}