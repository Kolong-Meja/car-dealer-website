group "default" {
  targets = ["car-dealer-frontend", "car-dealer-backend"]
}

target "car-dealer-frontend" {
  context = "./www/next-frontend"
  dockerfile = "Dockerfile"
  tags = [ "car-dealer-frontend:latest" ]
}

target "car-dealer-backend" {
  context = "./www/cardealer"
  dockerfile = "Dockerfile"
  tags = [ "car-dealer-backend:latest" ]
}