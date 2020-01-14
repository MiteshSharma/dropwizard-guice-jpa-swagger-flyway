# Variables
variable "region" {
  default = "us-east-1"
}

variable "project_name" {
  description = "Project name"
  default     = "mobile-server"
}

variable "env" {
  description = "Name of the environment"
  default     = "prod"
}

variable "cidr_vpc" {
  description = "CIDR block for the VPC"
  default     = "10.1.0.0/16"
}

variable "subnets" {
  description = "AZ to CIDR mapping for each subnet"
  type        = map(any)
  default     = {
    us-east-1a = "10.1.0.0/21"
    us-east-1b = "10.1.8.0/21"
  }
}

variable "github_owner" {
  description = "Github owner"
  default     = "MiteshSharma"
}

variable "github_repo" {
  description = "Github repo"
  default     = "dropwizard-guice-jpa-swagger-flyway"
}

variable "github_branch" {
  description = "Github token"
  default     = "master"
}

variable "rds_username" {
  description = "RDS user name"
  default     = "root"
}

variable "rds_password" {
  description = "RDS password"
  default     = "PaSsWoRd1234"
}

variable "alb_port" {
  description = "Port exposed by the docker image to redirect traffic to"
  default     = 80
}

variable "app_port" {
  description = "Port exposed by the docker image to redirect traffic to"
  default     = 8080
}

variable "app_count" {
  description = "Number of docker containers to run"
  default     = 1
}

variable "health_check_path" {
  default = "/"
}

variable "fargate_cpu" {
  description = "Fargate instance CPU units (1 vCPU = 1024 CPU units)"
  default     = "512"
}

variable "fargate_memory" {
  description = "Fargate instance memory (in MiB)"
  default     = "1024"
}