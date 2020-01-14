resource "aws_appautoscaling_target" "main" {
  service_namespace  = "ecs"
  resource_id        = format("service/%s/%s", aws_ecs_cluster.main.name, aws_ecs_service.main.name)
  scalable_dimension = "ecs:service:DesiredCount"
  role_arn           = aws_iam_role.autoscale.arn
  min_capacity       = 1
  max_capacity       = 2

}

resource "aws_appautoscaling_policy" "up" {
  name                    = format("%s-%s-%s",var.project_name,"scale_up", var.env)
  service_namespace       = "ecs"
  resource_id             = format("service/%s/%s", aws_ecs_cluster.main.name, aws_ecs_service.main.name)
  scalable_dimension      = "ecs:service:DesiredCount"

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = 60
    metric_aggregation_type = "Maximum"

    step_adjustment {
      metric_interval_lower_bound = 0
      scaling_adjustment = 1
    }
  }

  depends_on = [aws_appautoscaling_target.main]
}

resource "aws_appautoscaling_policy" "down" {
  name                    = format("%s-%s-%s",var.project_name,"scale_down", var.env)
  service_namespace       = "ecs"
  resource_id             = format("service/%s/%s", aws_ecs_cluster.main.name, aws_ecs_service.main.name)
  scalable_dimension      = "ecs:service:DesiredCount"

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = 60
    metric_aggregation_type = "Maximum"

    step_adjustment {
      metric_interval_lower_bound = 0
      scaling_adjustment = -1
    }
  }

  depends_on = [aws_appautoscaling_target.main]
}

# metric used for auto scale
resource "aws_cloudwatch_metric_alarm" "service_cpu_high" {
  alarm_name          = format("%s-%s-%s",var.project_name,"cpu_utilization_high", var.env)
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = "60"
  statistic           = "Maximum"
  threshold           = "70"

  alarm_actions = [aws_appautoscaling_policy.up.arn]
  ok_actions    = [aws_appautoscaling_policy.down.arn]
}

resource "aws_iam_role" "autoscale" {
  name               = format("%s-%s-%s",var.project_name,"ecs_autoscale_role", var.env)
  assume_role_policy = data.aws_iam_policy_document.ecs_autoscaling_policy.json

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_iam_role_policy" "autoscale_policy" {
  name   = format("%s-%s-%s",var.project_name,"ecs_autoscale_role_policy", var.env)
  role   = aws_iam_role.autoscale.id
  policy = data.aws_iam_policy_document.ecs_autoscaling_role_policy.json
}

data "aws_iam_policy_document" "ecs_autoscaling_policy" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["application-autoscaling.amazonaws.com"]
    }
  }
}

data "aws_iam_policy_document" "ecs_autoscaling_role_policy" {
  statement {
    sid    = "AllowECS"
    effect    = "Allow"
    resources = ["*"]

    actions = [
      "ecs:DescribeServices",
      "ecs:UpdateService",
    ]
  }

  statement {
    sid    = "AllowCloudwatchAlarm"
    effect    = "Allow"
    resources = ["*"]

    actions = [
      "cloudwatch:DescribeAlarms",
    ]
  }
}