# K8S Chatbot

## Intro

Sometime I want to access a k8s cluster but I don't my terminal at hand. That's why I create this.

This built with no security feature in mind. Use it at yours own risk XD

## Features
- [X] List
  - [X] namespace
  - [X] deployment
  - [X] service
  - [X] pod

- [ ] Describe
  - [X] namespace
  - [X] deployment
  - [ ] service
  - [ ] pod

- [X] Delete deployment
- [X] Delete Pod

- [X] Get pod's log

- [ ] Chat client
  - [X] Discord
- [ ] K8S Driver
  - [X] Shell
  - [ ] API

## Build Docker image

```
docker build -t k8s-bot .
```


## Run with Docker

```
docker run -v ~/.kube:/root/.kube -e DISCORD_TOKEN=<BOT_TOKEN>  k8s-bot
```