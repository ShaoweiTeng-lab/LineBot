#!/bin/bash

# 替换 nginx.conf 中的環境變數
envsubst '$LB_IP_1 $LB_IP_2 $LB_IP_3' < /etc/nginx/nginx.conf > /etc/nginx/nginx.conf.temp
mv /etc/nginx/nginx.conf.temp /etc/nginx/nginx.conf

# 啟動 Nginx
nginx -g 'daemon off;'
