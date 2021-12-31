1. 在 /src/main 下创建 cpp 目录，添加 CMakeLists.txt, native-lib.cpp
2. CMakeLists.txt 编写规则如 https://developer.android.com/studio/projects/configure-cmake
3. build.gradle 中，android:defaultConfig: 节点下增加->
```groovy
      externalNativeBuild {
            cmake {
                cppFlags '-std=c++17'
            }
        }
```
与defaultConfig 同级节点下增加->
```groovy
   externalNativeBuild {
        cmake {
            path file('src/main/cpp/CMakeLists.txt')
            version '3.10.2'
        }
    }
```
4. 在 java 文件里声明：
```kotlin
    external fun stringFromJNI(): String
```
系统会提示：Java_com_umbrella_training_cmake_CMakeTrainingActivity_stringFromJNI 类似错误
5. 在 cpp 文件里增加：
```C
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL

Java_com_umbrella_training_cmake_CMakeTrainingActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
```

