output "vpc_id" {
  value = aws_vpc.networking.id
}

output "public_subnets" {
  value = [aws_subnet.networking.*.id]
}

output "public_route_table_ids" {
  value = [aws_route_table.networking.id]
}

output "mysql_endpoint" {
  value = aws_db_instance.mysql.endpoint
}

output "redis_address" {
  value = aws_elasticache_cluster.redis.cache_nodes.0.address
}

output "redis_port" {
  value = aws_elasticache_cluster.redis.cache_nodes.0.port
}

output "task_defination_arn" {
  value = aws_ecs_task_definition.app.arn
}