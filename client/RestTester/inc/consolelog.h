#ifndef CONSOLELOG_H
#define CONSOLELOG_H

#include <QWidget>

namespace Ui {
    class ConsoleLog;
}

class ConsoleLog : public QWidget
{
    Q_OBJECT

public:
    explicit ConsoleLog(QWidget *parent = 0);
    ~ConsoleLog();

public slots:
    void addLog(QString log);
private:
    Ui::ConsoleLog *ui;

protected:
    void closeEvent(QCloseEvent *evt);

signals:
    void logClosed();
};

#endif // CONSOLELOG_H
