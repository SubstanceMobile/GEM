LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# APK Name
LOCAL_PACKAGE_NAME := GEM
LOCAL_OVERRIDES_PACKAGES := Music

# Files
#LOCAL_SRC_FILES := $(call all-subdir-java-files)

# Libraries

# Signing
LOCAL_CERTIFICATE := platform

# Builds with Gradle (Temporary) [http://stackoverflow.com/questions/32898340/build-gradle-system-app-as-part-of-aosp-build]
$(info $(shell ($(LOCAL_PATH)/gradlew build -p $(LOCAL_PATH)/)) )
$(info $(shell ($(LOCAL_PATH)/gradlehack.sh $(PRODUCT_OUT))))

# Build
#include $(BUILD_PACKAGE)
