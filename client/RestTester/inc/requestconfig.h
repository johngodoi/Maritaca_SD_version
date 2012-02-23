#ifndef REQUESTCONFIG_H
#define REQUESTCONFIG_H

#include <QWidget>
#include <QNetworkAccessManager>
#include <QNetworkReply>
#include "oauth2.h"
#include "paramitem.h"

namespace Ui {
    class RequestConfig;
}

class RequestConfig : public QWidget
{
    Q_OBJECT

public:
    explicit RequestConfig(QWidget *parent = 0);
    ~RequestConfig();

    void setNetworkAccessManager(QNetworkAccessManager* netAccessMngr);

private slots:
    void on_submitButton_clicked();
    void on_addParamButton_clicked();
    void requestFinished();
    void submit();
    void deleteItem(ParamItem *item);

private:
    Ui::RequestConfig *ui;
    QNetworkAccessManager* netAccessMngr;
    QNetworkReply* reqReply;
    OAuth2* oauth;

    void addParams(QUrl *url);
    QNetworkRequest * createGetRequest();
    QNetworkRequest * createPostRequest(QByteArray* data);

signals:
    void addLog(QString log);
};

#endif // REQUESTCONFIG_H
