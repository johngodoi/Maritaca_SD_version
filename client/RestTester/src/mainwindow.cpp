#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "requestconfig.h"
#include <QLayout>


MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    netAccessMan = new QNetworkAccessManager(this);

    mdiArea = new QMdiArea(this);
    setCentralWidget(mdiArea);

    newRequestAction = new QAction(tr("New request"), this);
    connect(newRequestAction, SIGNAL(triggered()), this, SLOT(newRequest()));
    ui->mainToolBar->addAction(newRequestAction);

    showLogAction = new QAction(tr("Show Log"), this);
    showLogAction->setCheckable(true);
    connect(showLogAction, SIGNAL(triggered(bool)), this, SLOT(showLogTriggered(bool)));
    ui->mainToolBar->addAction(showLogAction);

    consoleLog = new ConsoleLog(this);
    consoleSubwindow = new QMdiSubWindow();
    consoleSubwindow->setWidget(consoleLog);
    connect(consoleLog, SIGNAL(logClosed()), this, SLOT(logClosed()));
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::newRequest(){
    RequestConfig* reqconf = new RequestConfig(this);
    reqconf->setNetworkAccessManager(netAccessMan);
    connect(reqconf, SIGNAL(addLog(QString)), consoleLog, SLOT(addLog(QString)));

    mdiArea->addSubWindow(reqconf);
    reqconf->show();
}

void MainWindow::showLogTriggered(bool show){
    if(show){
        showLogAction->setText(tr("Hide log"));
        mdiArea->addSubWindow(consoleSubwindow);
    }else{
        showLogAction->setText(tr("Show log"));
        mdiArea->removeSubWindow(consoleSubwindow);
    }
    consoleLog->setVisible(show);
}

void MainWindow::logClosed(){
    showLogAction->trigger();
}
