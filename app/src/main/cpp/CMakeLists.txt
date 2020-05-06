# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


##########################SoundTouch##########################
#获取当前目录
set(pwd_dir ${PROJECT_SOURCE_DIR})
#设置库名称
set(soundtouch_lib_name soundtouch)
#设置头文件
set(soundtouch_include_dir
        ${pwd_dir}/soundtouch
        ${pwd_dir}/soundtouch/include
        )
#将若干库链接到目标库文件
set(link_lib
        log
        )
#设置编译选项
set(cmake_c_flag_debug

        )
set(cmake_cxx_flag_debug

        )
#设置要编译的源文件
set(source_files
        ${pwd_dir}/soundtouch/soundtouch_wapper.cpp
        ${pwd_dir}/soundtouch/AAFilter.cpp
        ${pwd_dir}/soundtouch/BPMDetect.cpp
        ${pwd_dir}/soundtouch/cpu_detect_x86.cpp
        ${pwd_dir}/soundtouch/FIFOSampleBuffer.cpp
        ${pwd_dir}/soundtouch/FIRFilter.cpp
        ${pwd_dir}/soundtouch/mmx_optimized.cpp
        ${pwd_dir}/soundtouch/PeakFinder.cpp
        ${pwd_dir}/soundtouch/RateTransposer.cpp
        ${pwd_dir}/soundtouch/SoundTouch.cpp
        ${pwd_dir}/soundtouch/sse_optimized.cpp
        ${pwd_dir}/soundtouch/TDStretch.cpp
        )

#其他配置

include_directories(${soundtouch_include_dir})
set(CMAKE_C_FLAGS_DEBUG "${cmake_c_flag_debug} -s")
set(CMAKE_CXX_FLAGS_DEBUG "${cmake_cxx_flag_debug} -s")
add_library(${soundtouch_lib_name} SHARED ${source_files})
target_link_libraries(${soundtouch_lib_name} ${link_lib})




add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )