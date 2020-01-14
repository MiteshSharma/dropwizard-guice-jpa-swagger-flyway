# interim results of the pipeline are saved in this S3 bucket
resource "aws_s3_bucket" "pipeline" {
  bucket = format("%s-%s-%s",var.project_name,"pipeline", var.env)

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_codepipeline" "pipeline" {
  name     = format("%s-%s-%s",var.project_name,"pipeline", var.env)
  role_arn = aws_iam_role.pipeline.arn

  depends_on = [null_resource.iam, aws_ecs_service.main]

  artifact_store {
    location = aws_s3_bucket.pipeline.bucket
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner            = "ThirdParty"
      provider         = "GitHub"
      version          = "1"
      output_artifacts = ["source"]

      configuration = {
        Owner      = var.github_owner
        Repo       = var.github_repo
        Branch     = var.github_branch
      }
    }
  }

  stage {
    name = "Build"

    action {
      name             = "Build"
      category         = "Build"
      owner            = "AWS"
      provider         = "CodeBuild"
      input_artifacts  = ["source"]
      output_artifacts = ["build"]
      version          = "1"

      configuration = {
        ProjectName = aws_codebuild_project.pipeline.name
      }
    }
  }

  stage {
    name = "Deploy"

    action {
      name            = "Deploy"
      category        = "Deploy"
      owner           = "AWS"
      provider        = "CodeDeployToECS"
      input_artifacts = ["build"]
      version         = "1"

      configuration = {
        ApplicationName = aws_codedeploy_app.pipeline.name
        DeploymentGroupName = aws_codedeploy_deployment_group.pipeline.deployment_group_name
        Image1ArtifactName = "build"
        Image1ContainerName = "IMAGE1_NAME"
        AppSpecTemplatePath = "appspec.yaml"
        AppSpecTemplateArtifact = "build"
        TaskDefinitionTemplateArtifact = "build"
        TaskDefinitionTemplatePath = "taskdef.json"
      }
    }
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_codebuild_project" "pipeline" {
  name         = format("%s-%s-%s",var.project_name,"codebuild", var.env)
  description  = "Codebuild which upload final docker image to ECR"
  service_role = aws_iam_role.codebuild.arn

  depends_on = [null_resource.iam]

  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type    = "BUILD_GENERAL1_SMALL"
    image           = "aws/codebuild/amazonlinux2-x86_64-standard:2.0"
    type            = "LINUX_CONTAINER"
    privileged_mode = true

    environment_variable {
      name  = "REPOSITORY_URI"
      value = aws_ecr_repository.ecr.repository_url
    }

    environment_variable {
      name  = "CONTAINER_NAME"
      value = aws_ecr_repository.ecr.name
    }

    environment_variable {
      name  = "TASK_DEFINITION"
      value = aws_ecs_task_definition.app.arn
    }

    environment_variable {
      name  = "SUBNET_1"
      value = aws_subnet.networking.*.id[0]
    }

    environment_variable {
      name  = "SUBNET_2"
      value = aws_subnet.networking.*.id[1]
    }

    environment_variable {
      name  = "SECURITY_GROUP"
      value = aws_security_group.ecs_tasks.id
    }

    environment_variable {
      name  = "ECS_TASK_EXECUTION_ROLE"
      value = aws_iam_role.ecs_task_execution_role.arn
    }

    environment_variable {
      name  = "MYSQL_ENDPOINT"
      value = aws_db_instance.mysql.endpoint
    }

    environment_variable {
      name  = "MYSQL_USERNAME"
      value = var.rds_username
    }

    environment_variable {
      name  = "MYSQL_PASSWORD"
      value = var.rds_password
    }

    environment_variable {
      name  = "REDIS_ENDPOINT"
      value = format("%s:%s",aws_elasticache_cluster.redis.cache_nodes.0.address,aws_elasticache_cluster.redis.cache_nodes.0.port)
    }

  }

  source {
    type = "CODEPIPELINE"
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_codedeploy_app" "pipeline" {
  compute_platform = "ECS"
  name             = format("%s-%s-%s",var.project_name,"codedeploy", var.env)

  depends_on = [null_resource.iam]
}

resource "aws_codedeploy_deployment_group" "pipeline" {
  app_name               = aws_codedeploy_app.pipeline.name
  deployment_group_name  = "mobile-server-deploy-group"
  deployment_config_name = "CodeDeployDefault.ECSAllAtOnce"
  service_role_arn       = aws_iam_role.codedeploy.arn

  load_balancer_info {
    target_group_pair_info {
      prod_traffic_route {
        listener_arns = [aws_alb_listener.alb.arn]
      }

      target_group {
        name = aws_alb_target_group.alb_blue.name
      }

      target_group {
        name = aws_alb_target_group.alb_green.name
      }
    }
  }

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action = "TERMINATE"
    }
  }


  ecs_service {
    cluster_name = aws_ecs_cluster.main.name
    service_name = aws_ecs_service.main.name
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }
}