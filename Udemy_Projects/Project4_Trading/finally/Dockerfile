# Stage 1: Build frontend
FROM node:20-alpine AS frontend-builder

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# Stage 2: Python runtime
FROM python:3.12-slim

WORKDIR /app

# Install uv
RUN pip install uv

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy backend
COPY backend/ ./backend/

# Install Python dependencies
WORKDIR /app/backend
RUN uv sync --frozen

# Copy frontend build
WORKDIR /app
COPY --from=frontend-builder /app/frontend/out ./frontend/out

# Create db directory for volume mount
RUN mkdir -p /app/db

EXPOSE 8000

WORKDIR /app/backend

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/api/health || exit 1

CMD ["uv", "run", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
