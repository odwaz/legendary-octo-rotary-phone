# Terraform AWS Infrastructure

## What This Creates

- **VPC** with public, private, and database subnets across 3 AZs
- **EKS Cluster** (Kubernetes 1.28) with 2 t3.medium nodes
- **RDS PostgreSQL** (15.4) in private subnet
- **ArgoCD** installed via Helm with LoadBalancer

## Prerequisites

1. AWS CLI configured with credentials
2. Terraform installed (>= 1.0)
3. kubectl installed

## Deployment Steps

### 1. Configure Variables

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars` and set:
- `db_password` - Strong password for RDS

### 2. Initialize Terraform

```bash
terraform init
```

### 3. Review Plan

```bash
terraform plan
```

### 4. Deploy

```bash
terraform apply
```

This takes ~15-20 minutes.

### 5. Configure kubectl

```bash
aws eks update-kubeconfig --region us-east-1 --name wallet-eks-cluster
```

### 6. Get ArgoCD Admin Password

```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
```

### 7. Access ArgoCD

```bash
kubectl get svc argocd-server -n argocd
```

Use the LoadBalancer URL with username `admin` and password from step 6.

### 8. Update K8s Manifests

Update your `k8s/*.yaml` files to use RDS endpoint:

```yaml
env:
- name: SPRING_R2DBC_URL
  value: r2dbc:postgresql://<RDS_ENDPOINT>/wallet_db
- name: SPRING_R2DBC_USERNAME
  value: wallet_admin
- name: SPRING_R2DBC_PASSWORD
  value: <YOUR_DB_PASSWORD>
```

Get RDS endpoint:
```bash
terraform output rds_endpoint
```

### 9. Configure ArgoCD Application

Same as before - add GitLab repo and create application.

## Cleanup

```bash
terraform destroy
```

## Cost Estimate

- EKS: ~$73/month
- EC2 (2x t3.medium): ~$60/month
- RDS (db.t3.micro): ~$15/month
- NAT Gateway: ~$32/month
- **Total: ~$180/month**
