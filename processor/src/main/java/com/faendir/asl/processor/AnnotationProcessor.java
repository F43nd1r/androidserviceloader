package com.faendir.asl.processor;

import com.faendir.asl.annotation.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.internal.PublicAndroidAnnotationsEnvironment;
import org.androidannotations.internal.helper.AndroidManifestFinder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor8;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    public static final String ASSETS_DIR = "assetsDir";
    private AndroidAnnotationsEnvironment environment;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        environment = new PublicAndroidAnnotationsEnvironment(processingEnv);
        ModelConstants.init(environment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<TypeElement> elements = roundEnv.getElementsAnnotatedWith(AutoService.class).stream().filter(TypeElement.class::isInstance).map(TypeElement.class::cast).collect(Collectors.toSet());
        Map<TypeName, List<TypeName>> contents = new HashMap<>();
        if (!elements.isEmpty()) {
            try {
                String packageName = new AndroidManifestFinder(environment).extractAndroidManifest().getApplicationPackage();
                TypeElement autoServiceType = processingEnv.getElementUtils().getTypeElement(AutoService.class.getName());
                ElementFilter.methodsIn(autoServiceType.getEnclosedElements()).stream().filter(m -> m.getSimpleName().toString().equals("value")).findAny().ifPresent(valueMethod -> {
                    for (TypeElement element : elements) {
                        element.getAnnotationMirrors().stream().filter(mirror -> mirror.getAnnotationType().toString().equals(autoServiceType.toString())).findAny().ifPresent(mirror -> {
                            AnnotationValue value = mirror.getElementValues().get(valueMethod);
                            value.accept(new AbstractAnnotationValueVisitor8<Object, Object>() {
                                @Override
                                public Object visitBoolean(boolean b, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitByte(byte b, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitChar(char c, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitDouble(double d, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitFloat(float f, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitInt(int i, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitLong(long i, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitShort(short s, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitString(String s, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitType(TypeMirror t, Object o) {
                                    TypeName interfaceName = TypeName.get(t);
                                    contents.computeIfAbsent(interfaceName, k -> new ArrayList<>());
                                    contents.get(interfaceName).add(TypeName.get(element.asType()));
                                    return null;
                                }

                                @Override
                                public Object visitEnumConstant(VariableElement c, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitAnnotation(AnnotationMirror a, Object o) {
                                    return null;
                                }

                                @Override
                                public Object visitArray(List<? extends AnnotationValue> vals, Object o) {
                                    for (AnnotationValue v : vals) {
                                        v.accept(this, null);
                                    }
                                    return null;
                                }
                            }, null);
                        });
                    }
                });
                TypeName list = ParameterizedTypeName.get(ArrayList.class, Class.class);
                TypeName map = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(Class.class), list);
                MethodSpec.Builder builder = MethodSpec.methodBuilder("getDefinitions")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("$T map = new $T()", map, map);
                for (Map.Entry<TypeName, List<TypeName>> entry : contents.entrySet()) {
                    builder.addStatement("map.put($T.class, new $T())", entry.getKey(), list);
                    for (TypeName name : entry.getValue()) {
                        builder.addStatement("map.get($T.class).add($T.class)", entry.getKey(), name);
                    }
                }
                builder.addStatement("return map")
                        .returns(map);
                JavaFile.builder(packageName + ".asl", TypeSpec.classBuilder("Definition").addMethod(builder.build())
                        .addModifiers(Modifier.PUBLIC)
                        .build())
                        .skipJavaLangImports(true)
                        .build().writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoService.class.getName());
    }

    private File assetFolder() {
        if (processingEnv.getOptions().containsKey(ASSETS_DIR)) {
            return new File(processingEnv.getOptions().get(ASSETS_DIR));
        }
        try {
            Filer filer = processingEnv.getFiler();
            FileObject dummy = filer.createResource(StandardLocation.SOURCE_OUTPUT, "dummy", "dummy" + System.currentTimeMillis());
            String dummySourceFilePath = dummy.toUri().toString();
            dummy.delete();

            if (dummySourceFilePath.startsWith("file:")) {
                if (!dummySourceFilePath.startsWith("file://")) {
                    dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
                }
            } else {
                dummySourceFilePath = "file://" + dummySourceFilePath;
            }

            URI cleanURI = new URI(dummySourceFilePath);

            File dummyFile = new File(cleanURI);

            File generatedFolder = dummyFile.getParentFile().getParentFile().getParentFile().getParentFile();

            return new File(generatedFolder.getAbsolutePath() + "/assets/serviceloader/release/serviceloader");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
