#include "inc/paramitem.h"
#include "ui_paramitem.h"

ParamItem::ParamItem(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ParamItem)
{
    ui->setupUi(this);
}

ParamItem::~ParamItem()
{
    delete ui;
}

const QString ParamItem::value()
{
    return ui->value->text();
}

const QString ParamItem::key()
{
    return ui->key->text();
}

void ParamItem::on_pushButton_clicked()
{
    emit deleteItem(this);
}
