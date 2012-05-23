#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QMdiArea>
#include <QMdiSubWindow>
#include "consolelog.h"
#include <QNetworkAccessManager>

namespace Ui {
    class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private slots:
    void newRequest();
    void showLogTriggered(bool show);
    void logClosed();

private:
    Ui::MainWindow *ui;
    QMdiArea* mdiArea;
    QAction* newRequestAction;
    QAction* showLogAction;
    ConsoleLog* consoleLog;
    QMdiSubWindow* consoleSubwindow;
    QNetworkAccessManager* netAccessMan;

};

#endif // MAINWINDOW_H
