# androidserviceloader
Assets based implementation of ServiceLoader

# Usage
build.gradle: 
```groovy
android {
    sourceSets {
        main {
            assets.srcDirs += "build/generated/assets/serviceloader/release"
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
List<MyInterface> implementations = new ServiceLoader(context).load(MyInterface.class);
```
