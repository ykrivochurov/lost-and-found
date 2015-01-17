"Бюро находок" с возможностью привязать объявление к месту на карте или адресу с делением объявлений по категориям. Проект выполнен как платформа и может быть использован для любых объявлений с привязкой к карте.

ssh -i ~/Documents/projects/keys/ykey2_amazon.pem ubuntu@54.201.73.198

Информация о cервере
________________________________

Оплата
https://my.firstvds.ru/manager/billmgr
krivochurov
921343ask

________________________________
Сервак

Service package : VDS-KVM-Отрыв
Setup date : 2015-01-11
Domain name : find.nsk.ru
Server IP address : 78.24.222.247
User : root
Password : r6jFYqOHpBoI

________________________________
панель управления сервером
Ссылка : https://mercury.ispsystem.net:1500/vmmgr
Пользователь : krivochurov
Пароль : H7X1Kc66YB1O
Также вы можете воспользоваться кнопкой "VDSmgr" в разделе "Виртуальные серверы" вашего Личного кабинета для перехода во внешнюю панель управления сервером. Авторизация будет проведена автоматически.

________________________________
Сообщаем вам детали настройки системы DNS для услуги
Hosting services  - VDS-KVM-Отрыв #2323638 (62.109.17.69, find.nsk.ru)

URL панели управления : https://82.146.47.1/manager/dnsmgr
Пользователь: vm2323638
Пароль: 3C08yOe7TkYiUPMB

"Настройки" - Настройки доменов по умолчанию

В поле "Серверы имён" должны быть указано значение "ns1.firstvds.ru. ns2.firstvds.ru."
(обратите внимание на "." после имён - это важно)
______________________________________________
support mailbox
https://mail.yandex.ru/for/find.nsk.ru/
support@find.nsk.ru
1qaz2WSX

DNS
Настройка ip == domain
https://pdd.yandex.ru/domain/find.nsk.ru/
krivochurov
9213433161

_______________________________
Домен find.nsk.ru

https://auth.hnet.ru/?response_type=code&page=cabinet&client_id=cabinet&redirect_uri=https://c.hnet.ru/

y.krivochurov
k82njfzatg

______________________________
Настройка:
1. ssh key - find.nsk.ru прописан в config
2. JDK 1.7
2.1 sudo apt-get install software-properties-common python-software-properties
2.2 sudo add-apt-repository ppa:webupd8team/java
2.3 sudo apt-get update
2.4 sudo apt-get install oracle-java7-installer
3. Tomcat
3.1 wget http://apache-mirror.rbc.ru/pub/apache/tomcat/tomcat-8/v8.0.15/bin/apache-tomcat-8.0.15.zip
4. Mongo DB
4.1 install http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/
4.2 export LC_ALL=C
4.3 add user -
    db.createUser(
        {
          user: "admin",
          pwd: "pAssw0rd",
          roles: [
             { role: "readWrite", db: "laf" }
          ]
        }
    )
5. Imagemagic
sudo apt-get install imagemagick
6. Logs in ../logs/laf.log
7. Port 80
iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
iptables -t nat -I OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j REDIRECT --to-ports 8080
8. DNS
