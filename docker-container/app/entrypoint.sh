#!/bin/bash

if [ "$RUN_MODE" = "shell" ]; then
    echo "Запуск интерактивной оболочки..."
    exec /bin/bash -i
else
    echo "Запуск Java‑приложения..."
    exec java -jar app.jar
fi
