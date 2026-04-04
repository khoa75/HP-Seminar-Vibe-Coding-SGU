#!/bin/bash

# FinAlly - Start script for macOS/Linux

echo "🚀 FinAlly - Starting AI Trading Workstation..."

# Check if .env exists
if [ ! -f .env ]; then
    echo "📝 Creating .env file from .env.example..."
    cp .env.example .env
    echo "⚠️  Please edit .env with your API keys if needed"
fi

# Build and run Docker container
docker build -t finally .
docker run -v finally-data:/app/db -p 8000:8000 --env-file .env finally
