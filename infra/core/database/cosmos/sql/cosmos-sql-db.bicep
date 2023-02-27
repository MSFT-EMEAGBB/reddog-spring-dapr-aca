param accountName string
param databaseName string
param location string = resourceGroup().location
param tags object = {}

param containers array = []

module cosmos 'cosmos-sql-account.bicep' = {
  name: 'cosmos-sql-account'
  params: {
    name: accountName
    location: location
    tags: tags
  }
}

resource database 'Microsoft.DocumentDB/databaseAccounts/sqlDatabases@2022-05-15' = {
  name: '${accountName}/${databaseName}'
  properties: {
    resource: { id: databaseName }
  }

  resource list 'containers' = [for container in containers: {
    name: container.name
    properties: {
      resource: {
        id: container.id
        partitionKey: { paths: [ container.partitionKey ] }
      }
      options: {}
    }
  }]

  dependsOn: [
    cosmos
  ]
}


output accountId string = cosmos.outputs.id
output accountName string = cosmos.outputs.name
output databaseName string = databaseName
output endpoint string = cosmos.outputs.endpoint
