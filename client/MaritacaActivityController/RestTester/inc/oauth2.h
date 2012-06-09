#ifndef OAUTH2_H
#define OAUTH2_H

#include <QDialog>
#include <QTcpServer>
#include <QTextStream>
#include <QUrl>
#include <QNetworkAccessManager>
#include <QNetworkReply>

namespace Ui {
    class OAuth2;
}

class OAuth2 : public QDialog
{
    Q_OBJECT

public:
    explicit OAuth2(QWidget *parent = 0);
    ~OAuth2();
    void setNetworkAccessManager(QNetworkAccessManager* nam);
    const QString accessToken();
    void doAuth();

private slots:
    void on_startBtn_clicked();
    void urlChanged(const QUrl &url);
    void addLog(const QString &log);
    void readyRead();
    void newConnection();
    void on_nextBtn_clicked();
    void onTokenReply();

private:
    Ui::OAuth2 *ui;
    QTcpServer* m_server;
    QTextStream* m_stream;
    QNetworkAccessManager* m_netAccessMan;
    QNetworkReply* m_tokenReply;
    QString m_accessToken;
    QString m_refreshToken;


    static const QString AUTH_END_POINT;
    static const QString TOKEN_END_POINT;
    static const QString RESP_TYPE;
    static const QString AUTH_CODE;
    static const QString SCOPE;
    static const QString STATE;
    static const QString CLIENT_ID;
    static const QString CLIENT_SECRET;

    void getAuthCode(QUrl url);
    void initServer();
    void initUi();
    void requestToken(QUrl url, QUrl params);
    void getAccessTokenFromRefresh();
    void closeServer();

signals:
    void authReady();
};

#endif // OAUTH2_H
