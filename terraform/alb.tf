resource "aws_alb" "alb" {
  name            = format("%s-%s-%s",var.project_name,"lb", var.env)
  subnets         = aws_subnet.networking.*.id
  security_groups = [aws_security_group.lb.id]

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_alb_target_group" "alb_blue" {
  name        = format("%s-%s-%s",var.project_name,"tg-lb-blue", var.env)
  port        = var.app_port
  protocol    = "HTTP"
  vpc_id      = aws_vpc.networking.id
  target_type = "ip"

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "3"
    path                = var.health_check_path
    unhealthy_threshold = "2"
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_alb_target_group" "alb_green" {
  name        = format("%s-%s-%s",var.project_name,"tg-lb-green", var.env)
  port        = var.app_port
  protocol    = "HTTP"
  vpc_id      = aws_vpc.networking.id
  target_type = "ip"

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "3"
    path                = var.health_check_path
    unhealthy_threshold = "2"
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

# Redirect all traffic from the ALB to the target group
resource "aws_alb_listener" "alb" {
  load_balancer_arn = aws_alb.alb.id
  port              = var.alb_port
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.alb_blue.id
    type             = "forward"
  }
}