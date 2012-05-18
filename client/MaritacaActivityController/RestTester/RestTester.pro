#-------------------------------------------------
#
# Project created by QtCreator 2012-02-16T13:08:18
#
#-------------------------------------------------

QT       += core gui network webkit script

TARGET = RestTester
TEMPLATE = app


SOURCES += src/main.cpp\
        src/mainwindow.cpp \
    src/requestconfig.cpp \
    src/paramitem.cpp \
    src/consolelog.cpp \
    src/oauth2.cpp

HEADERS  += inc/mainwindow.h \
    inc/requestconfig.h \
    inc/paramitem.h \
    inc/consolelog.h \
    inc/oauth2.h

FORMS    += ui/mainwindow.ui \
    ui/requestconfig.ui \
    ui/paramitem.ui \
    ui/consolelog.ui \
    ui/oauth2.ui

INCLUDEPATH += inc/
