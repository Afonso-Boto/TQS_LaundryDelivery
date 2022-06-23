# TQS_LaundryDelivery

![Main Workflow](https://github.com/Afonso-Boto/TQS_LaundryDelivery/actions/workflows/main-check.yml/badge.svg)
![Qourier Workflow](https://github.com/Afonso-Boto/TQS_LaundryDelivery/actions/workflows/qourier.yml/badge.svg)
![LaundryAtHome Workflow](https://github.com/Afonso-Boto/TQS_LaundryDelivery/actions/workflows/laundryathome.yml/badge.svg)

## Abstract

#### Digital marketplace for last-mile deliveries
* Web app for management by Riders and Customers (delivery businesses)
* Approval-based registration system managed by the administrators
* Account lifecycle that supports suspension of accounts, managed by the administrators
* Dynamic bidding system for delivery assignment to Riders based on distance to delivery origin
* Asynchronous messaging for notifying Riders if they were assigned to a job
* Tracking of delivery progress by Administrators and Customers on a publish-subscribe fashion
* Admin page for monitoring platform status


#### Application/Market Proposition

The goal of our application is to provide a delivery service for medium to small companies, by providing ease of use and responsive tracking.


## Authors
| NMec | Name | Role |
|:-:|:--|:--|
| 98262 | Martinho Tavares | QA Engineer |
| 89123 | Tomás Candeias | Product Owner |
| 98475 | Rodrigo Lima | Team Leader |
| 89285 | Afonso Boto | DevOps master |

## Bookmarks

Jira board: https://ies-project.atlassian.net/jira/software/projects/TQS/boards/2/backlog

Project Specification Report: https://github.com/Afonso-Boto/TQS_LaundryDelivery/blob/main/docs/Product___specification_(Draft).pdf

QA Manual: https://github.com/Afonso-Boto/TQS_LaundryDelivery/blob/main/docs/qa_manual.pdf

Qourier Complementary Android App: https://github.com/Pengrey/ICM/tree/main/Android/Project

## Deployments
Qourier
  - Webpage: http://51.142.110.251/
  - Grafana: http://51.142.110.251:3000

LaundryAtHome
  - Webpage: http://51.142.78.179

## Credentials

| Username | Password |
|:-:|:--|
| admin | jx3P8ByvbDQ4U4rG$ |

## Envirnonment variables for deployment
```bash
# Credentials
MYSQLDB_USER=root
MYSQLDB_ROOT_PASSWORD=mAuWM9cBjx7nLGR3
MYSQLDB_DATABASE=qourier_db
MYSQLDB_LOCAL_PORT=3306
MYSQLDB_DOCKER_PORT=3306

# Grafana envs
GF_SECURITY_ADMIN_PASSWORD=jx3P8ByvbDQ4U4rG$
GF_USERS_ALLOW_SIGN_UP=false
GF_AUTH_ANONYMOUS_ENABLED=true
GF_LOCAL_PORT=3000
GF_DOCKER_PORT=3000

# App Ports
SPRING_QOURIER_LOCAL_PORT=80
SPRING_DOCKER_PORT=8080

#Admin Creds
ADMIN_EMAIL=admin@gmail.com
ADMIN_PASSWORD=jx3P8ByvbDQ4U4rG$
```
