#ifndef PARAMITEM_H
#define PARAMITEM_H

#include <QWidget>

namespace Ui {
    class ParamItem;
}

class ParamItem : public QWidget
{
    Q_OBJECT

public:
    explicit ParamItem(QWidget *parent = 0);
    ~ParamItem();

    const QString key();
    const QString value();

private:
    Ui::ParamItem *ui;

signals:
    void deleteItem(ParamItem* item);
private slots:
    void on_pushButton_clicked();
};

#endif // PARAMITEM_H
