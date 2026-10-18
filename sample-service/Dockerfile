FROM busybox:1.37

RUN adduser -D -u 1001 appuser && mkdir /www && chown appuser /www
COPY --chmod=755 entrypoint.sh /entrypoint.sh

USER appuser
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]