#include "inc/requestconfig.h"
#include "ui_requestconfig.h"
#include <QUrl>
#include <QNetworkRequest>
#include <QDebug>
#include <QDateTime>
#include <QList>

RequestConfig::RequestConfig(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::RequestConfig)
{
    ui->setupUi(this);
    oauth = new OAuth2(this);
    connect(oauth, SIGNAL(authReady()), this, SLOT(submit()));
}

RequestConfig::~RequestConfig()
{
    delete ui;
}

void RequestConfig::setNetworkAccessManager(QNetworkAccessManager *netAccessMngr)
{
    this->netAccessMngr = netAccessMngr;
    oauth->setNetworkAccessManager(netAccessMngr);
}

void RequestConfig::on_submitButton_clicked()
{
   //check if auth2 enable
    if(ui->oauthFlag->isChecked()){
        if(oauth->accessToken().size()==0){
            oauth->doAuth();
        }else{
            submit();
        }
    }else{
        submit();
    }
}

void RequestConfig::submit(){
    const QString method = ui->methodCmbox->currentText();

    qDebug()<<"URL: "<<ui->restPath->text();

    QNetworkRequest* req=NULL;

    if(method == "GET"){
        req = createGetRequest();
        reqReply = netAccessMngr->get(*req);
    }else if(method == "POST"){
        QByteArray data;
        req = createPostRequest(&data);
        reqReply = netAccessMngr->post(*req, data);
    }
    connect(reqReply, SIGNAL(finished()), this, SLOT(requestFinished()));

    if(req){
        qDebug()<<"Request: "<<req->url();
    }
}

QNetworkRequest* RequestConfig::createGetRequest(){
    QNetworkRequest* req = new QNetworkRequest();
    QUrl url(ui->restPath->text());
    //set parameters
    addParams(&url);
    req->setUrl(url);
    return req;
}

QNetworkRequest* RequestConfig::createPostRequest(QByteArray* data){
    QNetworkRequest* req = new QNetworkRequest();
    QUrl url(ui->restPath->text());
    QUrl params;
    //set parameters
    addParams(&params);
    *data = params.encodedQuery();
    req->setUrl(url);
    return req;
}

void RequestConfig::addParams(QUrl *url){
    for(int i = 0; i < ui->paramList->count(); ++i){
        ParamItem* paramItem = (ParamItem*)(ui->paramList->itemWidget(ui->paramList->item(i)));
        url->addQueryItem(paramItem->key(),paramItem->value());
    }
    if(ui->oauthFlag->isChecked()){
        url->addQueryItem("access_token", oauth->accessToken());
    }
}

void RequestConfig::requestFinished(){
    QString result("---------------------\n");
    result += QDateTime::currentDateTime().toString();
    result += "\nURL: " +reqReply->url().toString();
    result += "\nHEADER";

    QList<QPair<QByteArray, QByteArray> > headers = reqReply->rawHeaderPairs();
    for(int i = 0 ; i < headers.size() ; ++i){
        QPair<QByteArray, QByteArray> header = headers.at(i);
        result += "\n" + QString(header.first) +" : "+ QString(header.second);
    }
    result += "\nDATA\n";
    result += reqReply->readAll();
    emit addLog(result);
}


void RequestConfig::on_addParamButton_clicked()
{
    QListWidgetItem* paramItem = new QListWidgetItem(ui->paramList);
    ParamItem* param = new ParamItem(this);
    ui->paramList->setItemWidget(paramItem, param);
    connect(param, SIGNAL(deleteItem(ParamItem*)), this, SLOT(deleteItem(ParamItem*)));

}

void RequestConfig::deleteItem(ParamItem* item){
    for(int i = 0; i < ui->paramList->count(); ++i){
        QListWidgetItem* listWidgetItem = ui->paramList->item(i);
        ParamItem* paramItem = (ParamItem*)(ui->paramList->itemWidget(listWidgetItem));
        if(paramItem == item){
            ui->paramList->model()->removeRow(i);
            return;
        }
    }
}
