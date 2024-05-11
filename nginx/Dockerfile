FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf 

COPY nginx-start.sh /docker-entrypoint.d/nginx-start.sh
# 添加 ssl key
COPY ./ssl/certificate.crt /etc/nginx/certs/certificate.crt
COPY ./ssl/ca_bundle.crt /etc/nginx/certs/ca_bundle.crt
COPY ./ssl/private.key /etc/nginx/certs/private.key

# 修改啟動腳本權限
RUN chmod +x /docker-entrypoint.d/nginx-start.sh

# 暴露 Nginx 使用的端口
EXPOSE 80

# 啟動 Nginx
CMD ["/docker-entrypoint.d/nginx-start.sh"]
