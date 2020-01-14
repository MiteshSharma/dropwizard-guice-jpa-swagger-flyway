
resource "aws_db_subnet_group" "rds-subnet" {
  subnet_ids = [aws_subnet.networking.0.id, aws_subnet.networking.1.id]
}

resource "aws_security_group" "sg-rds" {
  name        = "sgrds"
  description = "RDS security group"
  vpc_id      = aws_vpc.networking.id

  ingress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    self      = true
  }

  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"

    cidr_blocks = [var.cidr_vpc]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"

    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_db_instance" "mysql" {
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "5.7"
  instance_class       = "db.t3.micro"
  availability_zone    = element(keys(var.subnets), 0)
  name                 = format("%s_%s", "mobile_server", var.env)
  username               = var.rds_username
  password               = var.rds_password
  parameter_group_name = "default.mysql5.7"
  db_subnet_group_name   = aws_db_subnet_group.rds-subnet.name
  backup_retention_period = 10
  apply_immediately       = true
  vpc_security_group_ids = [aws_security_group.sg-rds.id]
  final_snapshot_identifier = format("%s-%s-%s-%s",var.project_name, "db", var.env, "final")
  skip_final_snapshot = true

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_elasticache_subnet_group" "redis_subnet_group" {
  name       = "redis-subnet-group"
  subnet_ids = [aws_subnet.networking.0.id, aws_subnet.networking.1.id]
}

resource "aws_security_group" "author-redis-access" {
  name        = "redis-access"
  description = "Redis access from the application subnet"
  vpc_id      = aws_vpc.networking.id

  ingress {
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    cidr_blocks = [var.cidr_vpc]
  }

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id           = format("%s-%s-%s",var.project_name,"redis", var.env)
  engine               = "redis"
  node_type            = "cache.t3.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis5.0"
  subnet_group_name    = aws_elasticache_subnet_group.redis_subnet_group.name
  availability_zone      = element(keys(var.subnets), 0)
  security_group_ids   = [aws_security_group.author-redis-access.id]

  tags = {
    "Environment" = var.env
    "Project"     = var.project_name
  }
}