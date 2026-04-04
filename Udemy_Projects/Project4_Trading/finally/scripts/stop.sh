#!/bin/bash

# FinAlly - Stop script for macOS/Linux

echo "⏹️  FinAlly - Stopping containers..."

docker ps -a | grep finally | awk '{print $1}' | xargs -r docker stop
docker ps -a | grep finally | awk '{print $1}' | xargs -r docker rm

echo "✅ Done"
