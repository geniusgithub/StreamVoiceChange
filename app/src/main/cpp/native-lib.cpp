#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_geniusgithub_voicechange_jni_NDKUtil_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
