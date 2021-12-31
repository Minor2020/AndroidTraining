#include <jni.h>
#include <string>

// https://zhuanlan.zhihu.com/p/97691316
extern "C" JNIEXPORT jstring JNICALL
Java_com_umbrella_training_cmake_CMakeTrainingActivity_stringFromJNI(
        JNIEnv *env,
        jobject instance /* this */) {
    std::string hello = "Hello from C++";


    jclass a_class = env->GetObjectClass(instance);
    jmethodID a_method = env->GetMethodID(a_class, "describe", "()Ljava/lang/String;");
    jobject jobj = env->AllocObject(a_class);
    jstring  pring = (jstring)(env)->CallObjectMethod(jobj, a_method);
    char *print=(char*)(env)->GetStringUTFChars(pring, 0);

    return env->NewStringUTF(print); // (hello.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_umbrella_training_cmake_CMakeTrainingActivity_00024Companion_renderPlasma(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jobject bitmap,
                                                                                   jlong time_ms) {
    // TODO: implement renderPlasma()
}