resource "aws_ecs_cluster" "main" {
  name = format("%s-%s-%s",var.project_name,"ecs-cluster", var.env)

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

data "template_file" "app" {
  template = file("./templates/ecs/app.json.tpl")

  vars = {
    app_image      = format("%s:%s",aws_ecr_repository.ecr.repository_url,"latest")
    app_port       = var.app_port
    fargate_cpu    = var.fargate_cpu
    fargate_memory = var.fargate_memory
    aws_region     = var.region
    mysql_endpoint = aws_db_instance.mysql.endpoint
    mysql_username = var.rds_username
    mysql_password = var.rds_password
    redis_endpoint = format("%s:%s",aws_elasticache_cluster.redis.cache_nodes.0.address,aws_elasticache_cluster.redis.cache_nodes.0.port)
  }
}

resource "aws_ecs_task_definition" "app" {
  family                   = format("%s-%s-%s",var.project_name,"ecs-task", var.env)
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  container_definitions    = data.template_file.app.rendered

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_security_group" "lb" {
  name        = format("%s-%s-%s",var.project_name,"lb-security-group", var.env)
  description = "controls access to the ALB"
  vpc_id      = aws_vpc.networking.id

  ingress {
    protocol    = "tcp"
    from_port   = var.alb_port
    to_port     = var.app_port
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_security_group" "ecs_tasks" {
  name        = format("%s-%s-%s",var.project_name,"ecs-tasks-security-group", var.env)
  description = "allow inbound access from the ALB only"
  vpc_id      = aws_vpc.networking.id

  ingress {
    protocol        = "tcp"
    from_port       = var.alb_port
    to_port         = var.app_port
    security_groups = [aws_security_group.lb.id]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_ecs_service" "main" {
  name            = format("%s-%s-%s",var.project_name,"ecs-service", var.env)
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    security_groups  = [aws_security_group.ecs_tasks.id]
    subnets          = aws_subnet.networking.*.id
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.alb_blue.id
    container_name   = "mobile-server-app"
    container_port   = var.app_port
  }

  deployment_controller {
    type = "CODE_DEPLOY"
  }

  depends_on = [aws_alb_listener.alb, aws_iam_role_policy_attachment.ecs_task_execution_role]
}

# Set up CloudWatch group and log stream and retain logs for 30 days
resource "aws_cloudwatch_log_group" "log_group" {
  name              = "/ecs/mobile-server-app"
  retention_in_days = 5

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_cloudwatch_log_stream" "app_log_stream" {
  name           = format("%s-%s-%s",var.project_name,"ecs-app-log-stream", var.env)
  log_group_name = aws_cloudwatch_log_group.log_group.name
}