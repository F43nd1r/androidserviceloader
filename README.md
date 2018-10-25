# androidserviceloader
Assets based implementation of ServiceLoader

# Usage
build.gradle: 
```groovy
android {
    def assetsDir = "${project.buildDir}/generated/assets/serviceloader/release" as String
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ['assetsDir': assetsDir]
            }
        }
    }
    sourceSets {
        main {
            assets.srcDirs += assetsDir
        }
    }
    ...
}

dependencies {
    compileOnly "com.faendir.asl:annotation:1.0"
    annotationProcessor "com.faendir.asl:processor:1.0"
    implementation "com.faendir.asl:serviceloader:1.0"
}
```
Then anywhere in code:
```java
@AutoService(MyInterface.class)
public class MyClass implements MyInterface {
}
```
```java
List<MyInterface> implementations = new ServiceLoader(context).load(MyInterface.class);
```
