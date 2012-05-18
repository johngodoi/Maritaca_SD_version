#include "inc/consolelog.h"
#include "ui_consolelog.h"

ConsoleLog::ConsoleLog(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ConsoleLog)
{
    ui->setupUi(this);
}

ConsoleLog::~ConsoleLog()
{
    delete ui;
}

void ConsoleLog::addLog(QString log)
{
    ui->logArea->appendPlainText(log);
    ui->logArea->moveCursor(QTextCursor::End);
}

void ConsoleLog::closeEvent(QCloseEvent * evt){
    emit logClosed();
    QWidget::closeEvent(evt);
}
