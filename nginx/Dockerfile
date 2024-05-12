FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf

COPY nginx-start.sh /docker-entrypoint.d/nginx-start.sh
# 添加 ssl key
COPY ./ssl/fullchain.pem /etc/nginx/certs/fullchain.pem
COPY ./ssl/cert.pem /etc/nginx/certs/cert.pem
COPY ./ssl/chain.pem /etc/nginx/certs/chain.pem
COPY ./ssl/privkey.pem /etc/nginx/certs/privkey.pem

# 修改啟動腳本權限
RUN chmod +x /docker-entrypoint.d/nginx-start.sh

# 暴露 Nginx 使用的端口
EXPOSE 80

# 啟動 Nginx
CMD ["/docker-entrypoint.d/nginx-start.sh"]