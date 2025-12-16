output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_region" {
  description = "AWS region"
  value       = var.aws_region
}

output "rds_endpoint" {
  description = "RDS endpoint"
  value       = aws_db_instance.wallet.endpoint
}

output "rds_database_name" {
  description = "RDS database name"
  value       = aws_db_instance.wallet.db_name
}

output "argocd_server" {
  description = "ArgoCD server URL"
  value       = "Run: kubectl get svc argocd-server -n argocd"
}

output "configure_kubectl" {
  description = "Configure kubectl command"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${module.eks.cluster_name}"
}
