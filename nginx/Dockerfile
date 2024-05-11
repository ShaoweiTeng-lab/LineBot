FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf 
COPY nginx-start.sh /docker-entrypoint.d/nginx-start.sh

# 修改啟動腳本權限
RUN chmod +x /docker-entrypoint.d/nginx-start.sh

# 暴露 Nginx 使用的端口
EXPOSE 80

# 啟動 Nginx
CMD ["/docker-entrypoint.d/nginx-start.sh"]
