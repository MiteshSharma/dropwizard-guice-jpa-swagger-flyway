[
  {
    "name": "mobile-server-app",
    "image": "${app_image}",
    "cpu": ${fargate_cpu},
    "memory": ${fargate_memory},
    "networkMode": "awsvpc",
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/mobile-server-app",
          "awslogs-region": "${aws_region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "portMappings": [
      {
        "containerPort": ${app_port},
        "hostPort": ${app_port}
      }
    ],
    "environment": [
        {
          "name": "MYSQL_ENDPOINT",
          "value": "${mysql_endpoint}"
        },
        {
          "name": "MYSQL_USERNAME",
          "value": "${mysql_username}"
        },
        {
          "name": "MYSQL_PASSWORD",
          "value": "${mysql_password}"
        },
        {
          "name": "REDIS_ENDPOINT",
          "value": "${redis_endpoint}"
        }
    ]
  }
]