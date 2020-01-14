/*
ECR repository to store our Docker images
*/
resource "aws_ecr_repository" "ecr" {
  name = format("%s-%s-%s",var.project_name,"ecr-repo", var.env)

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}