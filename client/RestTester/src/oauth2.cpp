#include "inc/oauth2.h"
#include "ui_oauth2.h"
#include <QDateTime>
#include <QTcpSocket>
#include <QNetworkRequest>
#include <QtScript/QScriptEngine>

const QString OAuth2::CLIENT_ID="131804060198305";
const QString OAuth2::CLIENT_SECRET="n7JnmCSMWZPjL1NVgQGRG-Qh";
const QString OAuth2::AUTH_END_POINT="http://localhost:8080/maritaca/ws/oauth/requestcode";
const QString OAuth2::TOKEN_END_POINT="http://localhost:8080/maritaca/ws/oauth/accesstoken";
const QString OAuth2::RESP_TYPE="code";
const QString OAuth2::SCOPE="read";
const QString OAuth2::STATE="auth";
const QString OAuth2::AUTH_CODE="authorization_code";

OAuth2::OAuth2(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::OAuth2)
{
    ui->setupUi(this);
    initUi();
    connect(ui->webView, SIGNAL(urlChanged(QUrl)), this, SLOT(urlChanged(QUrl)));
}

OAuth2::~OAuth2()
{
    delete ui;
}

void OAuth2::doAuth(){
    if(m_refreshToken.size()>0){
        getAccessTokenFromRefresh();
    }else{
        initServer();
        ui->wizard->setCurrentIndex(0);
        show();
    }
}

void OAuth2::initUi(){
    ui->clientId1->setText(CLIENT_ID);
    ui->clientSecret1->setText(CLIENT_SECRET);
    ui->endpoint->setText(AUTH_END_POINT);
    ui->endpoint2->setText(TOKEN_END_POINT);
    ui->respType->setText(RESP_TYPE);
    ui->scope->setText(SCOPE);
    ui->state->setText(STATE);
    ui->grantType->setText(AUTH_CODE);
}

void OAuth2::initServer(){
    m_server = new QTcpServer(this);
    connect(m_server, SIGNAL(newConnection()), this, SLOT(newConnection()));
    if(m_server->listen(QHostAddress::LocalHost)){
        addLog("listening at: " + m_server->serverAddress().toString() + ":" + QString::number(m_server->serverPort()));
        ui->redirectUri1->setText("http://localhost:" +  QString::number(m_server->serverPort()));
    }else{
        addLog("not possible to start the tcp server");
        ui->startBtn->setEnabled(false);
    }
}

void OAuth2::closeServer(){
    m_server->close();
    m_server->disconnect(this);
    m_server = NULL;
}

void OAuth2::newConnection(){
    QTcpSocket* newCon = m_server->nextPendingConnection();
    m_stream = new QTextStream(newCon);
    connect(newCon, SIGNAL(readyRead()), this, SLOT(readyRead()));
}

void OAuth2::readyRead(){
    QString resp = m_stream->readLine();
    addLog(resp);
    resp = resp.split(" ").at(1);
    resp = resp.mid(2);
    QStringList params = resp.split("&");
    for(int i = 0; i < params.length(); i++){
        if(params.at(i).startsWith("code=")){
            QString code = params.at(i).mid(5);
            addLog("Authorization code: " + code);
            ui->resultcode->setText(code);
            ui->wizard->setCurrentIndex(2);
        }
    }
    closeServer();
}

void OAuth2::on_startBtn_clicked()
{
    QUrl url(ui->endpoint->text());
    url.addQueryItem("response_type", ui->respType->text());
    url.addQueryItem("client_id", ui->clientId1->text());
    url.addQueryItem("redirect_uri", ui->redirectUri1->text());
    url.addQueryItem("scope", ui->scope->text());
    url.addQueryItem("state", ui->state->text());
    getAuthCode(url);
}

void OAuth2::getAuthCode(QUrl url){
    ui->wizard->setCurrentIndex(1);
    ui->webView->setUrl(url);
}

void OAuth2::urlChanged(const QUrl &url){
    addLog(tr("Connecting to ").append(url.toString()));
}

void OAuth2::addLog(const QString &log)
{
    if(log.size()>0){
        QString entry("\n" + QDateTime::currentDateTime().toString() + "\n");
        entry += log;
        ui->log->appendPlainText(entry);
        ui->log->moveCursor(QTextCursor::End);
    }
}

void OAuth2::on_nextBtn_clicked()
{
    QUrl url(ui->endpoint2->text());
    QUrl params;
    params.addQueryItem("code", ui->resultcode->text());
    params.addQueryItem("client_id", ui->clientId1->text());
    params.addQueryItem("client_secret", ui->clientSecret2->text());
    params.addQueryItem("redirect_uri", ui->redirectUri1->text());
    params.addQueryItem("grant_type", ui->grantType->text());
    requestToken(url, params);
}

void OAuth2::getAccessTokenFromRefresh()
{
    addLog(tr("Getting access_token from refresh_token"));
    QUrl url(ui->endpoint2->text());
    QUrl params;
    params.addQueryItem("refresh_token", m_refreshToken);
    params.addQueryItem("client_id", ui->clientId1->text());
    params.addQueryItem("client_secret", ui->clientSecret2->text());
    params.addQueryItem("grant_type", "refresh_token");
    requestToken(url, params);
}

void OAuth2::requestToken(QUrl url, QUrl params){
    QNetworkRequest req(url);
    req.setHeader(QNetworkRequest::ContentTypeHeader,"application/x-www-form-urlencoded");
    m_tokenReply = m_netAccessMan->post(req,params.encodedQuery());
    connect(m_tokenReply, SIGNAL(finished()), this, SLOT(onTokenReply()));

}

void OAuth2::onTokenReply()
{
    QString result(m_tokenReply->readAll());
    QScriptValue sc;
    QScriptEngine engine;
    sc = engine.evaluate("JSON.parse").call(QScriptValue(), QScriptValueList() << result);
    if(sc.property("access_token").isString()){
        m_accessToken=sc.property("access_token").toString();
        addLog(tr("Access Token: ").append(m_accessToken));
        if(sc.property("refresh_token").isString()){
            m_refreshToken=sc.property("refresh_token").toString();
            addLog(tr("Refresh Token: ").append(m_refreshToken));
            this->hide();
            emit authReady();
        }
    }else {
        if(sc.property("error").isString()){
            if(sc.property("code").isNumber()){
                int errorcode = sc.property("code").toInteger();
                if(errorcode == 401 && m_refreshToken.size()>0){
                    //get access token from refresh token
                    getAccessTokenFromRefresh();
                    return;
                }
            }
        }
        addLog(result);
    }
}

void OAuth2::setNetworkAccessManager(QNetworkAccessManager *nam)
{
    m_netAccessMan = nam;
}

const QString OAuth2::accessToken()
{
    return m_accessToken;
}
