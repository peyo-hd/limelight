LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libjni_amcodec
LOCAL_SRC_FILES:= libjni_amcodec.so
include $(PREBUILT_SHARED_LIBRARY)
