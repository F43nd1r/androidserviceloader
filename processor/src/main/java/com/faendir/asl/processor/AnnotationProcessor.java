package com.faendir.asl.processor;

import com.faendir.asl.annotation.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor8;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<TypeElement> elements = roundEnv.getElementsAnnotatedWith(AutoService.class).stream().filter(TypeElement.class::isInstance).map(TypeElement.class::cast).collect(Collectors.toSet());
        File assetFolder = assetFolder();
        TypeElement autoServiceType = processingEnv.getElementUtils().getTypeElement(AutoService.class.getName());
        ElementFilter.methodsIn(autoServiceType.getEnclosedElements()).stream().filter(m -> m.getSimpleName().toString().equals("value")).findAny().ifPresent(valueMethod ->{
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
                        File directory = new File(assetFolder, t.toString());
                        directory.mkdirs();
                        File file = new File(directory, element.getQualifiedName().toString());
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoService.class.getName());
    }

    private File assetFolder() {
        try {
            Filer filer = processingEnv.getFiler();

            JavaFileObject dummySourceFile = filer.createSourceFile("dummy" + System.currentTimeMillis());
            String dummySourceFilePath = dummySourceFile.toUri().toString();

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
