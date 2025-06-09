# ScalableCloudV2

This represents my master thesis for graduating the Advanced Software Services Master's programme ad POLITEHNICA University of Bucharest.

It comes as a solution for unifing microservices observability, monitoring and scaling. Current solutions such as Grafana, Prometheus, OpenTelemetry and New Relic are great tools for monitoring cloud appications, 
but they do not offer a unified approach, as they serve different purposes and they need to be coupled with each other.

However, even though "unified" solutions exist, such as CloudWatch, Amazon EC2 Auto Scaling or Amazon ECS Auto Scaling from AWS, they are bounded by their environment which in this case is AWS.
ScalableCloudV2 comes as an alternative to this, offering a vendor agnostic approach, so everybody can monitor their services regarding of where their services actually run.

Another problem that exists, is that vendor agnostic platforms offer ways to set thresholds and configuration for the infrastructure in a programatic way (i.e Infrastructure-as-Code).

Unlike existing systems such as Kubernetes, which require infrastructure-as-code configurations to define autoscaling behavior, 
ScalableCloudV2 introduces a web-based user interface that allows operators to define, update, and monitor metric-based thresholds in real time, significantly lowering the barrier to entry 
for observability and scaling.

### Setting up the project

Before using the project, make sure you have installed the following: (Node.js)[], (Docker Desktop)[], [MongoDB Community](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/) 
