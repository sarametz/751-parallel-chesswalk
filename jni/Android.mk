LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := chesswalk

LOCAL_SRC_FILES := Android.cpp Engine.cpp Board.cpp Move.cpp Zobrist.cpp Evaluation.cpp
    
include $(BUILD_SHARED_LIBRARY)
