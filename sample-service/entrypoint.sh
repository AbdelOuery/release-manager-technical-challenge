#!/bin/sh
echo "{\"service\": \"${SERVICE_NAME:?}\", \"version\": ${SERVICE_VERSION:?}}" > /www/index.html
exec httpd -f -p 8080 -h /www