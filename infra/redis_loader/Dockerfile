FROM python:3.10-slim

WORKDIR /app

# Install dependencies
RUN pip install redis

# Copy Data
# We expect the build context to be the 'infra' directory so we can access ../csv_generation
COPY csv_generation /data

# Copy Script
COPY redis_loader/load_cache.py /app/load_cache.py

# Set environment variables (can be overridden by K8s)
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379

CMD ["python", "load_cache.py"]
