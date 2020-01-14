#providers
provider "aws" {
  region     = var.region
}

#resources
resource "aws_vpc" "networking" {
  cidr_block           = var.cidr_vpc
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_internet_gateway" "networking" {
  vpc_id = aws_vpc.networking.id
  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_subnet" "networking" {
  count                   = length(var.subnets)
  vpc_id                  = aws_vpc.networking.id
  cidr_block              = element(values(var.subnets), count.index)
  map_public_ip_on_launch = "true"
  availability_zone       = element(keys(var.subnets), count.index)
  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_route_table" "networking" {
  vpc_id = aws_vpc.networking.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.networking.id
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_route_table_association" "networking" {
  count          = length(var.subnets)
  subnet_id      = element(aws_subnet.networking.*.id, count.index)
  route_table_id = aws_route_table.networking.id
}

resource "aws_security_group" "networking" {
  name   = "sg_22"
  vpc_id = aws_vpc.networking.id

  # SSH access from the VPC
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

