FROM node:10-slim
MAINTAINER supakorn <supakorn@jigko.net>

ENV KUBE_LATEST_VERSION="v1.12.2"

# k8s
RUN apt update \
 && apt install -y curl \
 && curl -L https://storage.googleapis.com/kubernetes-release/release/${KUBE_LATEST_VERSION}/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl \
 && chmod +x /usr/local/bin/kubectl \
 && rm -rf /var/lib/apt/lists/*

# bot
RUN npm install -g --unsafe-perm lumo-cljs && mkdir /app
ADD https://github.com/anmonteiro/lumo/raw/master/shared-libs/libstdc%2B%2B.so.6 /usr/lib/x86_64-linux-gnu/
ADD package.json /app/
WORKDIR /app
RUN npm install 
ADD . /app

CMD ["/usr/local/bin/lumo","-c","/app/src","-m","kubee.core"]

# configuration
ADD kubeconfig /root/.kube/config

