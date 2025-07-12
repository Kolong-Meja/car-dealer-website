group "default" {
  targets = [ "restful-api" ]
}

target "restful-api" {
  context = "./restful-api"
  dockerfile = "Dockerfile"
  tags = [ "restful-api:latest" ]
}